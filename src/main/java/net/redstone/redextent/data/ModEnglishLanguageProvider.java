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
        add("ability.FastStart", "Fast Start");
        add("ability.DragonRise","Dragon Rise");
        add("ability.DragonRise.description", "When the Pokémon enters the field, if it is Dragon-type, its Special Attack, Attack, and Speed are boosted for 3 turns. The boosts increase each turn.");
        add("ability.FastStart.description","For the first 3 rounds after the Pokémon enters the field, its attack and speed increase by 50%, and the effect ends after 3 rounds.");
        // %s has entered the Quick Start state! Speed and attack have been greatly increased!
        // The Quick Start effect of %s has ended! Speed and attack have returned to normal.
        add("pixelmon.abilities.faststart", "%s has entered the Fast Start state! Speed and attack have been greatly increased!");
        add("pixelmon.abilities.faststartend", "The Fast Start effect of %s has ended! Speed and attack have returned to normal.");
        add("pixelmon.abilities.dragon_rise.activate", "%s's Dragon Rise activated!");
        add("pixelmon.abilities.dragon_rise.end", "%s's Dragon Rise effect ended.");
        add("pixelmon.abilities.dragon_rise.boost", "%s's Dragon Rise: Special Attack +%d%%, Attack +%d%%, Speed +%d%% (Turn %d)");
        add("pixelmon.abilities.dragon_rise.dragon_ascent", "%s's Dragon Ascent is empowered by Dragon Rise!");
    }
}
