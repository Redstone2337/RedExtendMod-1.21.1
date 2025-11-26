package net.redstone233.redextent.config;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

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

    public static final ModConfigSpec SPEC = BUILDER.build();

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
        // 验证列表格式
        validateListFormat(CUSTOM_ABILITY_WHITELIST.get(), "[\"DragonRise\"]");
        validateListFormat(ON_GHOST_PIXELMONS.get(), "[\"Gengar\"]");
    }

    private static void validateListFormat(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        String trimmed = value.trim();
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            if (value.equals(CUSTOM_ABILITY_WHITELIST.get())) {
                CUSTOM_ABILITY_WHITELIST.set(defaultValue);
            } else if (value.equals(ON_GHOST_PIXELMONS.get())) {
                ON_GHOST_PIXELMONS.set(defaultValue);
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

    // 设置方法
    public static void setCustomAbilityWhitelist(List<String> list) {
        CUSTOM_ABILITY_WHITELIST.set(listToJsonArrayString(list));
    }

    public static void setOnGhostPixelmons(List<String> list) {
        ON_GHOST_PIXELMONS.set(listToJsonArrayString(list));
    }

    public static void setCustomAbilityWhitelistString(String value) {
        CUSTOM_ABILITY_WHITELIST.set(value);
    }

    public static void setOnGhostPixelmonsString(String value) {
        ON_GHOST_PIXELMONS.set(value);
    }

    public static void setDisplayTextHead(String value) {
        DISPLAY_TEXT_HEAD.set(value);
    }

    public static void setDisplayTextBody(String value) {
        DISPLAY_TEXT_BODY.set(value);
    }

    public static void setCustomAbility(boolean value) {
        CUSTOM_ABILITY.set(value);
    }

    public static void setStartAbilityWhitelist(boolean value) {
        START_ABILITY_WHITELIST.set(value);
    }
}