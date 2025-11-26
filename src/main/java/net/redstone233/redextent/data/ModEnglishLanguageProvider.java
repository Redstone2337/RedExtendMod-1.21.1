package net.redstone233.redextent.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.redstone233.redextent.RedExtendMod;
import net.redstone233.redextent.item.ModItems;

public class ModEnglishLanguageProvider extends LanguageProvider {
    public ModEnglishLanguageProvider(PackOutput output) {
        super(output, RedExtendMod.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("ability.FastStartReforged", "Fast Start");
        add("ability.DragonRise","Dragon Rise");
        add("ability.FightingDivinity", "Fighting Divinity");
        add("ability.ElectricDivinity", "Electric Divinity");
        add("ability.BiomeBlessingSwamp", "Biome Blessing Swamp");
        add("ability.BiomeBlessingSwamp.description", "When the Pokémon enters a swamp biome, its Special Attack and Attack are increased by 5% and Speed by 15%. If it's a Ghost-type Pokémon listed in the config, its Special Attack and Attack are increased by 10% and Speed by 15%, then further increases Attack and Special Attack by 20% for a total of 30%.");
        add("ability.ElectricDivinity.description", "Electric-type Pokémon gain 20% Attack, 25% Special Attack, and 5% Speed upon entering battle, and reduce damage from super effective moves by 5%.");
        add("ability.FightingDivinity.description", "Greatly boosts the power of Fighting-type moves. If the Pokémon is Fighting-type, its Special Attack and Attack damage are increased by 10% when it enters the field.");
        add("ability.DragonRise.description", "When the Pokémon enters the field, if it is Dragon-type, its Special Attack, Attack, and Speed are boosted for 3 turns. The boosts increase each turn.");
        add("ability.FastStartReforged.description","For the first 3 rounds after the Pokémon enters the field, its attack and speed increase by 50%, and the effect ends after 3 rounds.");
        // %s has entered the Quick Start state! Speed and attack have been greatly increased!
        // The Quick Start effect of %s has ended! Speed and attack have returned to normal.
        add("pixelmon.abilities.fast_start.activate", "%s has entered the Fast Start state! Speed and attack have been greatly increased!");
        add("pixelmon.abilities.fast_start.end", "The Fast Start effect of %s has ended! Speed and attack have returned to normal.");
        add("pixelmon.abilities.dragon_rise.activate", "%s's Dragon Rise activated!");
        add("pixelmon.abilities.dragon_rise.end", "%s's Dragon Rise effect ended.");
        add("pixelmon.abilities.dragon_rise.boost", "%s's Dragon Rise: Special Attack +%d%%, Attack +%d%%, Speed +%d%% (Turn %d)");
        add("pixelmon.abilities.dragon_rise.dragon_ascent", "%s's Dragon Ascent is empowered by Dragon Rise!");
        add("pixelmon.abilities.fighting_divinity.activate", "%s's Fighting Divinity activated!");
        add("pixelmon.abilities.fighting_divinity.boost", "%s's %s is empowered by Fighting Divinity!");
        add("pixelmon.abilities.electric_divinity.activate", "%s's Electric Divinity activated!");
        add("pixelmon.abilities.electric_divinity.reduce_damage", "%s's Electric Divinity reduced the power of %s!");
        add("pixelmon.abilities.biome_blessing_swamp.activate", "%s's Biome Blessing: Swamp activated! Special Attack and Attack increased by 5%, Speed by 15%.");
        add( "pixelmon.abilities.biome_blessing_swamp.ghost_boost", "%s's Biome Blessing: Swamp activated! As a blessed Ghost-type, its Special Attack and Attack increased by 10%, Speed by 15%, and then total Attack and Special Attack increased by 30%.");


        // Super Furnace - 使用正确的键名格式
        add("rem.ponder.super_furnace.header", "Super Furnace Construction Guide");
        add("rem.ponder.super_furnace.text_1", "Super Furnace: A special structure that greatly improves smelting efficiency");
        add("rem.ponder.super_furnace.text_2", "Build Layer 1: 3x3 stone base");
        add("rem.ponder.super_furnace.text_3", "Build Layer 2: Note the center position");
        add("rem.ponder.super_furnace.text_4", "Place a furnace in the center");
        add("rem.ponder.super_furnace.text_5", "Build Layer 3: 3x3 stone top cover");
        add("rem.ponder.super_furnace.text_6", "Done! The Super Furnace significantly boosts smelting speed");

        // Super Blast Furnace - 使用正确的键名格式
        add("rem.ponder.super_blast_furnace.header", "Super Blast Furnace Construction Guide");
        add("rem.ponder.super_blast_furnace.text_1", "Super Blast Furnace: A special structure for highly efficient ore smelting");
        add("rem.ponder.super_blast_furnace.text_2", "Layer 1: 3x3 smooth stone base");
        add("rem.ponder.super_blast_furnace.text_3", "Layer 2: Surround the blast furnace with iron blocks");
        add("rem.ponder.super_blast_furnace.text_4", "Place a blast furnace in the center");
        add("rem.ponder.super_blast_furnace.text_5", "Layer 3: 3x3 block of iron top cover");
        add("rem.ponder.super_blast_furnace.text_6", "Compared to a regular blast furnace, the Super Blast Furnace is far more efficient");

        // Super Smoker - 使用正确的键名格式
        add("rem.ponder.super_smoker.header", "Super Smoker Construction Guide");
        add("rem.ponder.super_smoker.text_1", "Super Smoker: A special structure for quickly smoking food");
        add("rem.ponder.super_smoker.text_2", "Layer 1: Alternate coal blocks and logs in a cross pattern");
        add("rem.ponder.super_smoker.text_3", "Layer 2: Keep the cross pattern, place a smoker in the center");
        add("rem.ponder.super_smoker.text_4", "Place a smoker in the center");
        add("rem.ponder.super_smoker.text_5", "Layer 3: Mirror Layer 1 exactly");
        add("rem.ponder.super_smoker.text_6", "Done! The Super Smoker greatly increases food smoking speed");


        // New category tags
        add("rem.furnace.tag.title", "Super Furnaces");
        add("rem.furnace.tag.description", "Construction guides for high-efficiency smelting setups");

        // 配置文件翻译文本
        add("rem.configuration.isClearServerItem","Clear server item");
        add("rem.configuration.isItemFilter","Item filter mode");
        add("rem.configuration.isDebugMode","Debug mode");
        add("rem.configuration.isOnPonder","Enable built-in pondering");
        add("rem.configuration.clearTime","Clear time (in ticks)");
        add("rem.configuration.itemWhitelist", "Item white lists");
        add("rem.configuration.displayTextHead","Display text head");
        add("rem.configuration.displayTextBody","Display text body");
        add("rem.configuration.customAbility","Custom ability");
        add("rem.configuration.startAbilityWhitelist","Custom ability whitelist");
        add("rem.configuration.customAbilityWhitelist","Custom ability white lists");
        add("rem.configuration.onGhostPixelmons","Ghost-type pixelmons");
        add("rem.configuration.ServerItemSettings","Server item settings");
        add("rem.configuration.ClearClientSettings","Clear client settings");
        add("rem.configuration.ClientSettings","Client settings");
        add("rem.configuration.onBrewingRecipe","Enable brewing recipe");
        add("rem.configuration.disabledModList","Disabled mod list");
        add("rem.configuration.displayCountdownText","Display countdown text");

        add(ModItems.DISCORD_ITEM.get(), "Discord");
        add(ModItems.GITHUB_ITEM.get(), "GitHub");
    }
}
