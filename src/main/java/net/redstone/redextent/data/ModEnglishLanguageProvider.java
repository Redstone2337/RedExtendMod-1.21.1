package net.redstone.redextent.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.redstone.redextent.RedExtendMod;

public class ModEnglishLanguageProvider extends LanguageProvider {
    public ModEnglishLanguageProvider(PackOutput output) {
        super(output, RedExtendMod.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("ability.FastStartReforged", "Fast Start");
        add("ability.DragonRise","Dragon Rise");
        add( "ability.FightingDivinity", "Fighting Divinity");
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
    }
}
