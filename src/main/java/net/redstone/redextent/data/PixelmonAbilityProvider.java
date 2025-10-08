package net.redstone.redextent.data;

import net.minecraft.data.PackOutput;
import net.redstone.redextent.core.generator.BaseAbilitiesProvider;

public class PixelmonAbilityProvider extends BaseAbilitiesProvider {

    public PixelmonAbilityProvider(PackOutput output) {
        super(output, "pixelmon");
    }

    @Override
    protected void addAbilities() {
        addAbility("net.redstone.redextent.ability.FastStart");
        addAbility("net.redstone.redextent.ability.DragonRise");
    }
}
