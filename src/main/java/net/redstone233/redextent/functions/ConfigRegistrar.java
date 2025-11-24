package net.redstone233.redextent.functions;

import net.redstone233.redextent.Config;
import net.redstone233.redextent.core.util.ConfigUtil;
import net.redstone233.redextent.manager.ListConfigManager;

/**
 * 配置注册器 - 在游戏启动时注册所有列表配置
 */
public class ConfigRegistrar {

    public static void registerAllConfigs() {
        // 注册物品白名单配置
        ListConfigManager.registerListConfig(
                "item_whitelist",
                Config::getItemWhitelist,
                ConfigUtil::setItemWhitelist
        );

        // 注册自定义特性白名单配置
        ListConfigManager.registerListConfig(
                "custom_ability_whitelist",
                Config::getCustomAbilityWhitelist,
                ConfigUtil::setCustomAbilityWhitelist
        );

        // 注册幽灵宝可梦配置
        ListConfigManager.registerListConfig(
                "ghost_pixelmons",
                Config::getOnGhostPixelmons,
                ConfigUtil::setOnGhostPokemons
        );

        // 注册禁用模组列表配置
        ListConfigManager.registerListConfig(
                "disabled_mods",
                Config::getDisabledModList,
                ConfigUtil::setDisabledModList
        );
    }
}