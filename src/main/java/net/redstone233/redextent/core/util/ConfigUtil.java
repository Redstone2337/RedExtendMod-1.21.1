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
        Config.setItemWhitelist(itemWhitelist);
    }

    public static void setItemWhitelistString(String itemWhitelist) {
        Config.setItemWhitelistString(itemWhitelist);
    }

    public static void setOnGhostPokemons(List<String> onGhostPokemons) {
        Config.setOnGhostPixelmons(onGhostPokemons);
    }

    public static void setOnGhostPokemonsString(String onGhostPokemons) {
        Config.setOnGhostPixelmonsString(onGhostPokemons);
    }

    public static void setDisabledModList(List<String> disabledModList) {
        Config.setDisabledModList(disabledModList);
    }

    public static void setDisabledModListString(String disabledModList) {
        Config.setDisabledModListString(disabledModList);
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
        Config.setCustomAbilityWhitelist(customAbilityWhitelist);
    }

    public static void setCustomAbilityWhitelistString(String customAbilityWhitelist) {
        Config.setCustomAbilityWhitelistString(customAbilityWhitelist);
    }
}