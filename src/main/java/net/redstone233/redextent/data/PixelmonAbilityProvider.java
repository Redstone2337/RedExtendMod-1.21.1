package net.redstone233.redextent.data;

import net.minecraft.data.PackOutput;
import net.redstone233.redextent.core.generator.BaseAbilitiesProvider;

public class PixelmonAbilityProvider extends BaseAbilitiesProvider {

    public PixelmonAbilityProvider(PackOutput output) {
        super(output, "pixelmon");
    }

    @Override
    protected void addAbilities() {
        addAbility("ability.net.redstone233.redextent.FastStartReforged");
        addAbility("ability.net.redstone233.redextent.DragonRise");
        addAbility("ability.net.redstone233.redextent.FightingDivinity");
        addAbility("ability.net.redstone233.redextent.ElectricDivinity");
        addAbility("ability.net.redstone233.redextent.BiomeBlessingSwamp");
    }
}
