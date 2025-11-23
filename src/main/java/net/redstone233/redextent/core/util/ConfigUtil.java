package net.redstone233.redextent.core.util;

import net.redstone233.redextent.Config;

import java.util.List;

public class ConfigUtil {

    public static void setClearServerItem(boolean isClearServerItem) {
        Config.IS_CLEAR_SERVER_ITEM.set(isClearServerItem);
    }

    public static void setItemFilter(boolean isItemFilter) {
        Config.IS_ITEM_FILTER.set(isItemFilter);
    }

    public static void setDebugMode(boolean isDebugMode) {
        Config.IS_DEBUG_MODE.set(isDebugMode);
    }

    public static void setOnPonder(boolean isOnPonder) {
        Config.IS_ON_PONDER.set(isOnPonder);
    }

    public static void setOnBrewingRecipe(boolean onBrewingRecipe) {
        Config.ON_BREWING_RECIPE.set(onBrewingRecipe);
    }

    public static void setClearTime(int clearTime) {
        Config.CLEAR_TIME.set(clearTime);
    }

    public static void setItemWhitelist(List<String> itemWhitelist) {
        Config.ITEM_WHITELIST.set(itemWhitelist);
    }

    public static void setOnGhostPokemons(List<String> onGhostPokemons) {
        Config.ON_GHOST_PIXELMONS.set(onGhostPokemons);
    }

    public static void setDisabledModList(List<String> disabledModList) {
        Config.DISABLED_MOD_LIST.set(disabledModList);
    }

    public static void setDisplayTextHead(String displayTextHead) {
        Config.DISPLAY_TEXT_HEAD.set(displayTextHead);
    }

    public static void setDisplayTextBody(String displayTextBody) {
        Config.DISPLAY_TEXT_BODY.set(displayTextBody);
    }

    public static void setCustomAbility(boolean customAbility) {
        Config.CUSTOM_ABILITY.set(customAbility);
    }

    public static void setStartAbilityWhitelist(boolean startAbilityWhitelist) {
        Config.START_ABILITY_WHITELIST.set(startAbilityWhitelist);
    }

    public static void setCustomAbilityWhitelist(List<String> customAbilityWhitelist) {
        Config.CUSTOM_ABILITY_WHITELIST.set(customAbilityWhitelist);
    }
}