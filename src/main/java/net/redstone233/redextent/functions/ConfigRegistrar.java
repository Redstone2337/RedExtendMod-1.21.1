package net.redstone233.redextent.functions;

import net.redstone233.redextent.config.ClientConfig;
import net.redstone233.redextent.config.CommonConfig;
import net.redstone233.redextent.core.util.ConfigUtil;
import net.redstone233.redextent.manager.ListConfigManager;

/**
 * 配置注册器 - 在游戏启动时注册所有列表配置
 */
public class ConfigRegistrar {

    public static void registerAllConfigs() {
        // 注册物品白名单配置 (通用配置)
        ListConfigManager.registerListConfig(
                "item_whitelist",
                CommonConfig::getItemWhitelist,
                ConfigUtil::setItemWhitelist
        );

        // 注册自定义特性白名单配置 (客户端配置)
        ListConfigManager.registerListConfig(
                "custom_ability_whitelist",
                ClientConfig::getCustomAbilityWhitelist,
                ConfigUtil::setCustomAbilityWhitelist
        );

        // 注册幽灵宝可梦配置 (客户端配置)
        ListConfigManager.registerListConfig(
                "ghost_pixelmons",
                ClientConfig::getOnGhostPixelmons,
                ConfigUtil::setOnGhostPokemons
        );

        // 注册禁用模组列表配置 (通用配置)
        ListConfigManager.registerListConfig(
                "disabled_mods",
                CommonConfig::getDisabledModList,
                ConfigUtil::setDisabledModList
        );
    }
}