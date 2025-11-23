package net.redstone233.redextent.manager;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ModJarManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String DISABLED_EXTENSION = ".disabled";

    /**
     * 批量禁用模组
     */
    public static int batchDisableMods(List<String> modIds, boolean enableLogging) {
        int successCount = 0;
        for (String modId : modIds) {
            if (disableMod(modId, enableLogging)) {
                successCount++;
            }
        }

        if (enableLogging) {
            LOGGER.info("批量禁用操作完成: {}/{} 个模组成功禁用", successCount, modIds.size());
        }

        return successCount;
    }

    /**
     * 通过modid禁用模组
     */
    public static boolean disableMod(String modId, boolean enableLogging) {
        try {
            Optional<Path> modPath = getModJarPathByModId(modId);
            if (modPath.isEmpty()) {
                if (enableLogging) {
                    LOGGER.warn("未找到模组 {} 的JAR文件", modId);
                }
                return false;
            }

            Path originalPath = modPath.get();
            Path disabledPath = getDisabledPath(originalPath);

            // 检查是否已经禁用
            if (Files.exists(disabledPath)) {
                if (enableLogging) {
                    LOGGER.info("模组 {} 已经被禁用", modId);
                }
                return true;
            }

            // 执行重命名
            Files.move(originalPath, disabledPath, StandardCopyOption.REPLACE_EXISTING);

            if (enableLogging) {
                LOGGER.info("已禁用模组: {}", modId);
            }
            return true;

        } catch (Exception e) {
            LOGGER.error("禁用模组 {} 时发生错误: {}", modId, e.getMessage());
            return false;
        }
    }

    /**
     * 检查模组是否被禁用
     */
    public static boolean isModDisabled(String modId) {
        return findDisabledModFile(modId).isPresent();
    }

    /**
     * 通过modid获取模组JAR文件路径
     */
    private static Optional<Path> getModJarPathByModId(String modId) {
        try {
            // 使用安全的类型转换
            return ModList.get().getModContainerById(modId)
                    .map(container -> (Object) container) // 安全转换
                    .flatMap(container ->
                            Optional.ofNullable(((ModContainer) container).getModInfo()
                                    .getOwningFile().getFile().getFilePath())
                    );
        } catch (Exception e) {
            LOGGER.error("获取模组 {} 文件路径时发生错误: {}", modId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 查找已禁用的模组文件
     */
    private static Optional<Path> findDisabledModFile(String modId) {
        Path modsDir = FMLPaths.MODSDIR.get();
        // 使用 try-with-resources 确保 Stream 被关闭
        try (Stream<Path> filesStream = Files.list(modsDir)) {
            return filesStream
                    .filter(path -> {
                        String fileName = path.getFileName().toString();
                        return fileName.startsWith(modId) &&
                                fileName.endsWith(".jar.disabled");
                    })
                    .findFirst();
        } catch (IOException e) {
            LOGGER.error("查找已禁用模组 {} 文件时发生错误: {}", modId, e.getMessage());
            return Optional.empty();
        }
    }

    private static Path getDisabledPath(Path originalPath) {
        return originalPath.resolveSibling(originalPath.getFileName().toString() + DISABLED_EXTENSION);
    }
}