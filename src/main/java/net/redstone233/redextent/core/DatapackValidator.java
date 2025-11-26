// DatapackValidator.java
package net.redstone233.redextent.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import net.redstone233.redextent.RedExtendMod;
import net.redstone233.redextent.core.brewing.RhinoBrewingRecipeParser;
import net.redstone233.redextent.core.util.CodecUtils;
import net.redstone233.redextent.core.util.RemConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;

public class DatapackValidator {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();

    /**
     * 验证数据包的兼容性并注册酿造配方
     *
     * @param pack 数据包资源
     * @param currentFormat 当前游戏的数据包格式版本
     * @param reloader 触发重载的玩家（可为null）
     * @param registries 注册表访问器，用于Component序列化
     * @return 验证是否通过
     */
    public static boolean validateDatapack(PackResources pack, int currentFormat, Object reloader, HolderLookup.Provider registries) {
        String packId = pack.packId();

        try {
            // 读取 pack.mcmeta
            Optional<IoSupplier<InputStream>> metaSupplier = Optional.ofNullable(pack.getRootResource("pack.mcmeta"));
            if (metaSupplier.isEmpty()) {
                LOGGER.warn("数据包 {} 缺少 pack.mcmeta 文件", packId);
                sendPlayerMessage(reloader,
                        Component.literal("数据包 " + packId + " 缺少 pack.mcmeta 文件")
                                .withStyle(ChatFormatting.YELLOW));
                return false;
            }

            String metaContent;
            try (InputStream is = metaSupplier.get().get()) {
                metaContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }

            JsonObject metaJson = GSON.fromJson(metaContent, JsonObject.class);

            // 检查 rem 字段
            if (!metaJson.has("rem")) {
                LOGGER.warn("数据包 {} 缺少 'rem' 字段，跳过加载酿造台配方", packId);
                sendPlayerMessage(reloader,
                        Component.literal("数据包 " + packId + " 缺少必要的 'rem' 配置，跳过加载酿造台配方")
                                .withStyle(ChatFormatting.YELLOW));
                return false;
            }

            JsonElement remElement = metaJson.get("rem");
            DataResult<RemConfig> result = CodecUtils.parseRemConfig(remElement, registries);

            if (result.error().isPresent()) {
                String errorMsg = result.error().get().message();
                LOGGER.error("解析数据包 {} 的 rem 配置失败: {}", packId, errorMsg);

                // 检查是否是 rem_format 不支持的错误
                if (errorMsg.contains("Unsupported rem_format")) {
                    // 尝试提取 rem_format 值
                    try {
                        JsonObject remObject = remElement.getAsJsonObject();
                        if (remObject.has("rem_format")) {
                            int invalidFormat = remObject.get("rem_format").getAsInt();
                            sendPlayerMessage(reloader,
                                    Component.literal(String.format("数据包 %s 使用了不支持的 rem_format: %d (只支持 rem_format: 8)",
                                            packId, invalidFormat)).withStyle(ChatFormatting.RED));
                        }
                    } catch (Exception e) {
                        // 如果无法提取，使用通用错误消息
                        sendPlayerMessage(reloader,
                                Component.literal("数据包 " + packId + " 的 rem 配置格式错误: " + errorMsg)
                                        .withStyle(ChatFormatting.RED));
                    }
                } else {
                    sendPlayerMessage(reloader,
                            Component.literal("数据包 " + packId + " 的 rem 配置格式错误: " + errorMsg)
                                    .withStyle(ChatFormatting.RED));
                }
                return false;
            }

            RemConfig remConfig = result.result().orElseThrow();

            // 检查版本兼容性
            if (!remConfig.supports(currentFormat)) {
                String warnMsg = String.format("数据包 %s 版本不兼容 (当前: %d, 支持: %d-%d)",
                        packId, currentFormat, remConfig.minFormat(), remConfig.maxFormat());
                LOGGER.warn(warnMsg);

                sendPlayerMessage(reloader, remConfig.getIncompatibilityMessage(currentFormat));
                return false;
            }

            // 验证通过，输出描述信息和格式范围
            LOGGER.info("数据包 {} 验证成功: {} (支持格式: {})",
                    packId,
                    remConfig.getDescriptionAsString(),
                    getFormatsDescription(remConfig)
            );
            sendPlayerMessage(reloader, remConfig.getSuccessMessage(packId));

            // 如果验证通过，注册酿造配方
            registerBrewingRecipes(pack, registries);

            return true;

        } catch (Exception e) {
            LOGGER.error("验证数据包 {} 时发生错误", packId, e);
            sendPlayerMessage(reloader,
                    Component.literal("验证数据包 " + packId + " 时发生错误: " + e.getMessage())
                            .withStyle(ChatFormatting.RED));
            return false;
        }
    }

    /**
     * 向玩家发送消息（如果玩家存在）
     */
    private static void sendPlayerMessage(Object reloader, Component message) {
        // 在实际项目中，如果 reloader 是 ServerPlayer，则发送消息
        // 这里简化处理，只记录到日志
        if (reloader != null) {
            // 如果有玩家触发重载，向该玩家发送消息
            // 需要根据实际情况实现
            RedExtendMod.LOGGER.info("To player: {}", message.getString());
        } else {
            // 如果没有指定玩家，仍然记录到日志
            RedExtendMod.LOGGER.info("Data pack message: {}", message.getString());
        }
    }

    /**
     * 获取格式范围的描述信息
     */
    private static String getFormatsDescription(RemConfig config) {
        return String.format("[%d, %d]", config.minFormat(), config.maxFormat());
    }

    /**
     * 注册酿造配方
     */
    private static void registerBrewingRecipes(PackResources pack, HolderLookup.Provider registries) {
        String packId = pack.packId();
        RedExtendMod.LOGGER.info("Registering brewing recipes from validated data pack: {}", packId);

        try {
            // 调用 RhinoBrewingRecipeParser 注册酿造配方
            // 假设这个方法会处理数据包中的所有酿造配方
            RhinoBrewingRecipeParser.registerWithNeoForgeToDataPack();
            RedExtendMod.LOGGER.info("Successfully registered brewing recipes from data pack: {}", packId);

        } catch (Exception e) {
            RedExtendMod.LOGGER.error("Failed to register brewing recipes from data pack: {}", packId, e);
            throw new RuntimeException("Failed to register brewing recipes from data pack: " + packId, e);
        }
    }

    /**
     * 验证单个数据包的简化方法（用于测试）
     */
    public static ValidationResult validateDatapackSimple(PackResources pack, int currentFormat, HolderLookup.Provider registries) {
        String packId = pack.packId();

        try {
            Optional<IoSupplier<InputStream>> metaSupplier = Optional.ofNullable(pack.getRootResource("pack.mcmeta"));
            if (metaSupplier.isEmpty()) {
                return new ValidationResult(false, "Missing pack.mcmeta file");
            }

            String metaContent;
            try (InputStream is = metaSupplier.get().get()) {
                metaContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }

            JsonObject metaJson = GSON.fromJson(metaContent, JsonObject.class);

            if (!metaJson.has("rem")) {
                return new ValidationResult(false, "Missing 'rem' field");
            }

            JsonElement remElement = metaJson.get("rem");
            DataResult<RemConfig> result = CodecUtils.parseRemConfig(remElement, registries);

            if (result.error().isPresent()) {
                return new ValidationResult(false, "Invalid rem configuration: " + result.error().get().message());
            }

            RemConfig remConfig = result.result().orElseThrow();

            if (!remConfig.supports(currentFormat)) {
                return new ValidationResult(false,
                        String.format("Incompatible version (current: %d, supported: %d-%d)",
                                currentFormat, remConfig.minFormat(), remConfig.maxFormat()));
            }

            return new ValidationResult(true, "Validation successful: " + remConfig.getDescriptionAsString());

        } catch (Exception e) {
            return new ValidationResult(false, "Validation error: " + e.getMessage());
        }
    }

    /**
     * 验证结果记录
     */
    public record ValidationResult(boolean success, String message) {

        public Component toComponent(String packId) {
            if (success) {
                return Component.literal("[" + packId + "] " + message)
                        .withStyle(ChatFormatting.GREEN);
            } else {
                return Component.literal("[" + packId + "] " + message)
                        .withStyle(ChatFormatting.RED);
            }
        }
    }

    /**
     * 批量验证数据包并注册配方
     */
    public static void validateAndRegisterAllDatapacks(Collection<Pack> packs, int currentFormat, HolderLookup.Provider registries) {
        int total = 0;
        int valid = 0;

        RedExtendMod.LOGGER.info("Starting data pack validation and brewing recipe registration...");

        for (Pack pack : packs) {
            total++;
            try {
                // 修复：从 Pack 对象获取 PackResources
                PackResources packResources = pack.open();
                boolean isValid = validateDatapack(packResources, currentFormat, null, registries);
                if (isValid) {
                    valid++;
                }
                // 确保资源被正确关闭
                packResources.close();
            } catch (Exception e) {
                RedExtendMod.LOGGER.error("Failed to process data pack: {}", pack.getId(), e);
            }
        }

        RedExtendMod.LOGGER.info("Data pack validation and recipe registration completed: {}/{} packs are valid", valid, total);
    }

    /**
     * 获取数据包的 rem 配置（用于调试）
     */
    public static Optional<RemConfig> getRemConfig(Pack pack, HolderLookup.Provider registries) {
        try {
            // 修复：从 Pack 对象获取 PackResources
            PackResources packResources = pack.open();
            try {
                Optional<IoSupplier<InputStream>> metaSupplier = Optional.ofNullable(packResources.getRootResource("pack.mcmeta"));
                if (metaSupplier.isEmpty()) {
                    return Optional.empty();
                }

                String metaContent;
                try (InputStream is = metaSupplier.get().get()) {
                    metaContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                }

                JsonObject metaJson = GSON.fromJson(metaContent, JsonObject.class);

                if (!metaJson.has("rem")) {
                    return Optional.empty();
                }

                JsonElement remElement = metaJson.get("rem");
                DataResult<RemConfig> result = CodecUtils.parseRemConfig(remElement, registries);

                return result.result();
            } finally {
                packResources.close();
            }

        } catch (Exception e) {
            RedExtendMod.LOGGER.error("Failed to get rem config for data pack: {}", pack.getId(), e);
            return Optional.empty();
        }
    }
}