package net.redstone233.redextent.core.util;


import net.redstone233.redextent.config.ClientConfig;
import net.redstone233.redextent.config.CommonConfig;

import java.util.List;

public class ConfigUtil {

    // 通用配置方法
    public static void setClearServerItem(boolean isClearServerItem) {
        CommonConfig.IS_CLEAR_SERVER_ITEM.set(isClearServerItem);
    }

    public static void setItemFilter(boolean isItemFilter) {
        CommonConfig.IS_ITEM_FILTER.set(isItemFilter);
    }

    public static void setDebugMode(boolean isDebugMode) {
        CommonConfig.IS_DEBUG_MODE.set(isDebugMode);
    }

    public static void setOnPonder(boolean isOnPonder) {
        CommonConfig.IS_ON_PONDER.set(isOnPonder);
    }

    public static void setOnBrewingRecipe(boolean onBrewingRecipe) {
        CommonConfig.ON_BREWING_RECIPE.set(onBrewingRecipe);
    }

    public static void setClearTime(int clearTime) {
        CommonConfig.CLEAR_TIME.set(clearTime);
    }

    public static void setItemWhitelist(List<String> itemWhitelist) {
        CommonConfig.setItemWhitelist(itemWhitelist);
    }

    public static void setItemWhitelistString(String itemWhitelist) {
        CommonConfig.setItemWhitelistString(itemWhitelist);
    }

    public static void setDisabledModList(List<String> disabledModList) {
        CommonConfig.setDisabledModList(disabledModList);
    }

    public static void setDisabledModListString(String disabledModList) {
        CommonConfig.setDisabledModListString(disabledModList);
    }

    // 客户端配置方法
    public static void setDisplayTextHead(String displayTextHead) {
        ClientConfig.setDisplayTextHead(displayTextHead);
    }

    public static void setDisplayTextBody(String displayTextBody) {
        ClientConfig.setDisplayTextBody(displayTextBody);
    }

    public static void setCustomAbility(boolean customAbility) {
        ClientConfig.setCustomAbility(customAbility);
    }

    public static void setStartAbilityWhitelist(boolean startAbilityWhitelist) {
        ClientConfig.setStartAbilityWhitelist(startAbilityWhitelist);
    }

    public static void setCustomAbilityWhitelist(List<String> customAbilityWhitelist) {
        ClientConfig.setCustomAbilityWhitelist(customAbilityWhitelist);
    }

    public static void setCustomAbilityWhitelistString(String customAbilityWhitelist) {
        ClientConfig.setCustomAbilityWhitelistString(customAbilityWhitelist);
    }

    public static void setOnGhostPokemons(List<String> onGhostPokemons) {
        ClientConfig.setOnGhostPixelmons(onGhostPokemons);
    }

    public static void setOnGhostPokemonsString(String onGhostPokemons) {
        ClientConfig.setOnGhostPixelmonsString(onGhostPokemons);
    }
}