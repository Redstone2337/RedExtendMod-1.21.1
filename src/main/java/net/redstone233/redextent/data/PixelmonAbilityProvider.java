package net.redstone233.redextent.data;

import net.minecraft.data.PackOutput;
import net.redstone233.redextent.core.generator.BaseAbilitiesProvider;

public class PixelmonAbilityProvider extends BaseAbilitiesProvider {

    public PixelmonAbilityProvider(PackOutput output) {
        super(output, "pixelmon");
    }

    @Override
    protected void addAbilities() {
        addAbility("net.redstone233.redextent.ability.FastStartReforged");
        addAbility("net.redstone233.redextent.ability.DragonRise");
        addAbility("net.redstone233.redextent.ability.FightingDivinity");
        addAbility("net.redstone233.redextent.ability.ElectricDivinity");
        addAbility("net.redstone233.redextent.ability.BiomeBlessingSwamp");
    }
}
