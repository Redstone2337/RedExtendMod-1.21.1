package net.redstone233.redextent.config;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // 根级别配置
    public static final ModConfigSpec.BooleanValue IS_CLEAR_SERVER_ITEM = BUILDER
            .comment("开启服务器掉落物清理功能")
            .define("isClearServerItem", false);

    public static final ModConfigSpec.BooleanValue IS_ITEM_FILTER = BUILDER
            .comment("物品过滤器模式")
            .define("isItemFilter", false);

    public static final ModConfigSpec.BooleanValue IS_DEBUG_MODE = BUILDER
            .comment("调试模式(默认为关闭)")
            .define("isDebugMode", false);

    public static final ModConfigSpec.BooleanValue IS_ON_PONDER = BUILDER
            .comment("启用内置思索(默认开启)")
            .define("isOnPonder", true);

    public static final ModConfigSpec.BooleanValue ON_BREWING_RECIPE = BUILDER
            .comment("启用酿造配方(默认开启)")
            .define("onBrewingRecipe", true);

    // 服务器物品设置子节
    public static final ModConfigSpec.IntValue CLEAR_TIME = BUILDER
            .comment("清理的时间(单位:游戏刻)\n清理时间的范围在180t ~ 36000t以内\n超过范围或者范围，越界则不执行，默认5分钟。")
            .defineInRange("ServerItemSettings.clearTime", 6000, 180, 36000);

    // 修改为 String 类型，存储JSON数组格式的列表
    public static final ModConfigSpec.ConfigValue<String> ITEM_WHITELIST = BUILDER
            .comment("物品过滤器列表(若物品过滤器模式属于开启状态的话)\n格式: [\"item1\",\"item2\",\"item3\"] 或 [item1,item2,item3]")
            .define("ServerItemSettings.itemWhitelist", "[\"minecraft:command_block\"]");

    // 禁用模组列表 - 修改为 String 类型
    public static final ModConfigSpec.ConfigValue<String> DISABLED_MOD_LIST = BUILDER
            .comment("填写要禁用的 modid，格式: [\"modid1\",\"modid2\"] 或 [modid1,modid2]\n保存后服务端自动重命名并重启(目前不稳定)")
            .define("disabledModList", "[]");

    public static final ModConfigSpec SPEC = BUILDER.build();

    // 验证物品名称
    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            validateConfigValues();
        }
    }

    private static void validateConfigValues() {
        // 验证清理时间是否在有效范围内
        int clearTimeValue = CLEAR_TIME.get();
        if (clearTimeValue < 180 || clearTimeValue > 36000) {
            CLEAR_TIME.set(6000); // 默认5分钟
        }

        // 验证列表格式
        validateListFormat(ITEM_WHITELIST.get(), "[\"minecraft:command_block\"]");
        validateListFormat(DISABLED_MOD_LIST.get(), "[]");
    }

    private static void validateListFormat(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        String trimmed = value.trim();
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            if (value.equals(ITEM_WHITELIST.get())) {
                ITEM_WHITELIST.set(defaultValue);
            } else if (value.equals(DISABLED_MOD_LIST.get())) {
                DISABLED_MOD_LIST.set(defaultValue);
            }
        }
    }

    // 辅助方法：将JSON数组格式字符串转换为列表
    public static List<String> jsonArrayStringToList(String str) {
        if (str == null || str.trim().isEmpty() || str.trim().equals("[]")) {
            return List.of();
        }

        String cleaned = str.trim();
        if (!cleaned.startsWith("[") || !cleaned.endsWith("]")) {
            return List.of();
        }

        String content = cleaned.substring(1, cleaned.length() - 1).trim();
        if (content.isEmpty()) {
            return List.of();
        }

        List<String> result = new ArrayList<>();
        if (content.contains("\"")) {
            String[] parts = content.split("\"\\s*,\\s*\"");
            for (String part : parts) {
                String item = part.replace("\"", "").trim();
                if (!item.isEmpty()) {
                    result.add(item);
                }
            }
        } else {
            String[] parts = content.split("\\s*,\\s*");
            for (String part : parts) {
                String item = part.trim();
                if (!item.isEmpty()) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    // 辅助方法：将列表转换为JSON数组格式字符串
    public static String listToJsonArrayString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        String content = list.stream()
                .map(item -> "\"" + item.trim() + "\"")
                .collect(Collectors.joining(","));
        return "[" + content + "]";
    }

    // 配置访问方法
    public static boolean isClearServerItemEnabled() {
        return IS_CLEAR_SERVER_ITEM.get();
    }

    public static boolean isItemFilterEnabled() {
        return IS_ITEM_FILTER.get();
    }

    public static boolean isDebugModeEnabled() {
        return IS_DEBUG_MODE.get();
    }

    public static boolean isOnPonderEnabled() {
        return IS_ON_PONDER.get();
    }

    public static boolean isOnBrewingRecipeEnabled() {
        return ON_BREWING_RECIPE.get();
    }

    public static int getClearTime() {
        int time = CLEAR_TIME.get();
        return (time >= 180 && time <= 36000) ? time : 6000;
    }

    public static List<String> getItemWhitelist() {
        return jsonArrayStringToList(ITEM_WHITELIST.get());
    }

    public static String getItemWhitelistString() {
        return ITEM_WHITELIST.get();
    }

    public static List<String> getDisabledModList() {
        return jsonArrayStringToList(DISABLED_MOD_LIST.get());
    }

    public static String getDisabledModListString() {
        return DISABLED_MOD_LIST.get();
    }

    // 设置方法
    public static void setItemWhitelist(List<String> list) {
        ITEM_WHITELIST.set(listToJsonArrayString(list));
    }

    public static void setDisabledModList(List<String> list) {
        DISABLED_MOD_LIST.set(listToJsonArrayString(list));
    }

    public static void setItemWhitelistString(String value) {
        ITEM_WHITELIST.set(value);
    }

    public static void setDisabledModListString(String value) {
        DISABLED_MOD_LIST.set(value);
    }
}