package net.redstone233.redextent;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
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

    // 客户端清理设置子节
    public static final ModConfigSpec.ConfigValue<String> DISPLAY_TEXT_HEAD = BUILDER
            .comment("清理时候显示的文本头\n文本格式：文本头+文本体")
            .define("ClearClientSettings.displayTextHead", "[扫地姬]");

    public static final ModConfigSpec.ConfigValue<String> DISPLAY_TEXT_BODY = BUILDER
            .comment("清理的时候显示的文本体\n文本格式：文本头+文本体")
            .define("ClearClientSettings.displayTextBody", "本次总共清理了%s种掉落物，距离下次清理还剩%s秒");

    // 客户端设置子节
    public static final ModConfigSpec.BooleanValue CUSTOM_ABILITY = BUILDER
            .comment("是否开启自定义特性(如：快启动)")
            .define("ClientSettings.customAbility", true);

    public static final ModConfigSpec.BooleanValue START_ABILITY_WHITELIST = BUILDER
            .comment("是否开启自定义特性白名单")
            .define("ClientSettings.startAbilityWhitelist", false);

    // 修改为 String 类型，存储JSON数组格式的列表
    public static final ModConfigSpec.ConfigValue<String> CUSTOM_ABILITY_WHITELIST = BUILDER
            .comment("设定白名单列表\n检测整个模组中是否有与其字符串匹配的类名\n格式: [\"ability1\",\"ability2\"] 或 [ability1,ability2]")
            .define("ClientSettings.customAbilityWhitelist", "[\"DragonRise\"]");

    // 幽灵宝可梦配置 - 修改为 String 类型
    public static final ModConfigSpec.ConfigValue<String> ON_GHOST_PIXELMONS = BUILDER
            .comment("用于检测是否是幽灵属性的宝可梦\n若为幽灵属性，则在指定群系会有加成\n格式: [\"pokemon1\",\"pokemon2\"] 或 [pokemon1,pokemon2]")
            .define("ClientSettings.onGhostPixelmons", "[\"Gengar\"]");

    // 禁用模组列表 - 修改为 String 类型
    public static final ModConfigSpec.ConfigValue<String> DISABLED_MOD_LIST = BUILDER
            .comment("填写要禁用的 modid，格式: [\"modid1\",\"modid2\"] 或 [modid1,modid2]\n保存后服务端自动重命名并重启(目前不稳定)")
            .define("disabledModList", "[]");

    static final ModConfigSpec SPEC = BUILDER.build();

    // 验证物品名称
    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    // 验证类名（简单验证是否为非空字符串）
    private static boolean validateClassName(final Object obj) {
        return obj instanceof String className && !className.trim().isEmpty();
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
        validateListFormat(CUSTOM_ABILITY_WHITELIST.get(), "[\"DragonRise\"]");
        validateListFormat(ON_GHOST_PIXELMONS.get(), "[\"Gengar\"]");
        validateListFormat(DISABLED_MOD_LIST.get(), "[]");
    }

    private static void validateListFormat(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        // 基本格式验证：应该以[开头，以]结尾
        String trimmed = value.trim();
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            // 如果格式不正确，设置为默认值
            if (value.equals(ITEM_WHITELIST.get())) {
                ITEM_WHITELIST.set(defaultValue);
            } else if (value.equals(CUSTOM_ABILITY_WHITELIST.get())) {
                CUSTOM_ABILITY_WHITELIST.set(defaultValue);
            } else if (value.equals(ON_GHOST_PIXELMONS.get())) {
                ON_GHOST_PIXELMONS.set(defaultValue);
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

        // 确保以[开头和]结尾
        if (!cleaned.startsWith("[") || !cleaned.endsWith("]")) {
            return List.of();
        }

        // 提取内容部分（去掉外层的方括号）
        String content = cleaned.substring(1, cleaned.length() - 1).trim();

        if (content.isEmpty()) {
            return List.of();
        }

        List<String> result = new ArrayList<>();

        // 处理JSON数组格式：["item1","item2","item3"]
        if (content.contains("\"")) {
            // 使用简单的引号分割逻辑
            String[] parts = content.split("\"\\s*,\\s*\"");
            for (String part : parts) {
                // 移除每个元素前后的引号
                String item = part.replace("\"", "").trim();
                if (!item.isEmpty()) {
                    result.add(item);
                }
            }
        } else {
            // 处理裸逗号格式：[item1,item2,item3]
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

        // 使用双引号包围每个元素
        String content = list.stream()
                .map(item -> "\"" + item.trim() + "\"")
                .collect(Collectors.joining(","));
        return "[" + content + "]";
    }

    // 向后兼容的方法 - 使用旧的命名但内部使用新的JSON格式
    public static List<String> bracketStringToList(String str) {
        return jsonArrayStringToList(str);
    }

    public static String listToBracketString(List<String> list) {
        return listToJsonArrayString(list);
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

    public static String getDisplayTextHead() {
        return DISPLAY_TEXT_HEAD.get();
    }

    public static String getDisplayTextBody() {
        return DISPLAY_TEXT_BODY.get();
    }

    public static boolean isCustomAbilityEnabled() {
        return CUSTOM_ABILITY.get();
    }

    public static boolean isStartAbilityWhitelistEnabled() {
        return START_ABILITY_WHITELIST.get();
    }

    public static List<String> getCustomAbilityWhitelist() {
        return jsonArrayStringToList(CUSTOM_ABILITY_WHITELIST.get());
    }

    public static String getCustomAbilityWhitelistString() {
        return CUSTOM_ABILITY_WHITELIST.get();
    }

    // 幽灵宝可梦配置访问方法
    public static List<String> getOnGhostPixelmons() {
        return jsonArrayStringToList(ON_GHOST_PIXELMONS.get());
    }

    public static String getOnGhostPixelmonsString() {
        return ON_GHOST_PIXELMONS.get();
    }

    public static List<String> getDisabledModList() {
        return jsonArrayStringToList(DISABLED_MOD_LIST.get());
    }

    public static String getDisabledModListString() {
        return DISABLED_MOD_LIST.get();
    }

    // 新增：设置方法，供 ConfigUtil 使用
    public static void setItemWhitelist(List<String> list) {
        ITEM_WHITELIST.set(listToJsonArrayString(list));
    }

    public static void setCustomAbilityWhitelist(List<String> list) {
        CUSTOM_ABILITY_WHITELIST.set(listToJsonArrayString(list));
    }

    public static void setOnGhostPixelmons(List<String> list) {
        ON_GHOST_PIXELMONS.set(listToJsonArrayString(list));
    }

    public static void setDisabledModList(List<String> list) {
        DISABLED_MOD_LIST.set(listToJsonArrayString(list));
    }

    public static void setItemWhitelistString(String value) {
        ITEM_WHITELIST.set(value);
    }

    public static void setCustomAbilityWhitelistString(String value) {
        CUSTOM_ABILITY_WHITELIST.set(value);
    }

    public static void setOnGhostPixelmonsString(String value) {
        ON_GHOST_PIXELMONS.set(value);
    }

    public static void setDisabledModListString(String value) {
        DISABLED_MOD_LIST.set(value);
    }
}