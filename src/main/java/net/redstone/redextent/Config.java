package net.redstone.redextent;

import java.util.List;
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

    // 服务器物品设置子节
    public static final ModConfigSpec.IntValue CLEAR_TIME = BUILDER
            .comment("清理的时间(单位:游戏刻)\n清理时间的范围在180t ~ 36000t以内\n超过范围或者范围，越界则不执行，默认5分钟。")
            .defineInRange("ServerItemSettings.clearTime", 6000, 180, 36000);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_WHITELIST = BUILDER
            .comment("物品过滤器列表(若物品过滤器模式属于开启状态的话)")
            .defineListAllowEmpty("ServerItemSettings.itemWhitelist",
                    List.of("minecraft:command_block"),
                    Config::validateItemName);

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

    public static final ModConfigSpec.ConfigValue<List<? extends String>> CUSTOM_ABILITY_WHITELIST = BUILDER
            .comment("设定白名单列表\n检测整个模组中是否有与其字符串匹配的类名\n白名单用于设定哪些加载，哪些不加载。")
            .defineListAllowEmpty("ClientSettings.customAbilityWhitelist",
                    List.of("FastStart"),
                    Config::validateClassName);

    // 新增的幽灵宝可梦配置
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ON_GHOST_POKEMON_NAMES = BUILDER
            .comment("用于检测是否是幽灵属性的宝可梦\n若为幽灵属性，则在指定群系会有加成\n一行一个宝可梦")
            .defineListAllowEmpty("ClientSettings.onGhostPokemonNames",
                    List.of("Gengar"),
                    Config::validateClassName);

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
    static void onLoad(final ModConfigEvent event) {
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

    public static int getClearTime() {
        int time = CLEAR_TIME.get();
        return (time >= 180 && time <= 36000) ? time : 6000;
    }

    public static List<String> getItemWhitelist() {
        return ITEM_WHITELIST.get().stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
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
        return CUSTOM_ABILITY_WHITELIST.get().stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

    // 新增的幽灵宝可梦配置访问方法
    public static List<String> getOnGhostPokemonNames() {
        return ON_GHOST_POKEMON_NAMES.get().stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
    }
}