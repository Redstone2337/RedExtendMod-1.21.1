package net.redstone233.redextent.core.mod;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.redstone233.redextent.RedExtendMod;
import net.redstone233.redextent.ponder.SuperBlastFurnaceScene;
import net.redstone233.redextent.ponder.SuperFurnaceScene;
import net.redstone233.redextent.ponder.SuperSmokerScene;

public class SuperFurnaceRegistration {

    public static void registerAll(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        // 注册超级熔炉场景
        helper.addStoryBoard(
                ResourceLocation.withDefaultNamespace("super_furnace"),
                "super_furnace_structure",
                SuperFurnaceScene::superFurnace
        ).highlightAllTags();

        // 注册超级高炉场景
        helper.addStoryBoard(
                ResourceLocation.withDefaultNamespace("super_blast_furnace"),
                "super_blast_furnace_structure",
                SuperBlastFurnaceScene::superBlastFurnace
        ).highlightAllTags();

        // 注册超级烟熏炉场景
        helper.addStoryBoard(
                ResourceLocation.withDefaultNamespace("super_smoker"),
                "super_smoker_structure",
                SuperSmokerScene::superSmoker
        ).highlightAllTags();
    }

    public static void init() {
        RedExtendMod.LOGGER.info("Registering SuperFurnace Ponder Scenes");
    }
}