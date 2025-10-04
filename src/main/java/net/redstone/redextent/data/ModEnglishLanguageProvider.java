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
        add("ability.FastStart.description","For the first 3 rounds after the Pokémon enters the field, its attack and speed increase by 50%, and the effect ends after 3 rounds.");
        // %s has entered the Quick Start state! Speed and attack have been greatly increased!
        // The Quick Start effect of %s has ended! Speed and attack have returned to normal.
        add("pixelmon.abilities.faststart", "%s has entered the Fast Start state! Speed and attack have been greatly increased!");
        add("pixelmon.abilities.faststartend", "The Fast Start effect of %s has ended! Speed and attack have returned to normal.");
    }
}
