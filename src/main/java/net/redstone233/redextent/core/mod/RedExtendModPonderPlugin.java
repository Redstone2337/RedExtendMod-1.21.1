package net.redstone233.redextent.core.mod;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.redstone233.redextent.RedExtendMod;
import net.redstone233.redextent.ponder.tags.ModPonderTags;
import org.jetbrains.annotations.NotNull;

public class RedExtendModPonderPlugin implements PonderPlugin {
    @Override
    public @NotNull String getModId() {
        return RedExtendMod.MOD_ID;
    }

    @Override
    public void registerScenes(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
        SuperFurnaceRegistration.registerAll(helper);
    }

    @Override
    public void registerTags(@NotNull PonderTagRegistrationHelper<ResourceLocation> helper) {
        ModPonderTags.register(helper);
    }
}
