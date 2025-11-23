package net.redstone233.redextent;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.redstone233.redextent.core.gui.RemMainMenuScreen;
import net.redstone233.redextent.core.mod.RedExtendModPonderPlugin;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = RedExtendMod.MOD_ID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = RedExtendMod.MOD_ID, value = Dist.CLIENT)
public class RedExtendModClient {
    public RedExtendModClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

//        container.registerExtensionPoint(IConfigScreenFactory.class,
//                (Supplier<IConfigScreenFactory>) () -> (minecraft, parent) -> new BaseConfigScreen(parent,RedExtendMod.getModVersion())
//        );



    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        if (Config.isOnPonderEnabled()) {
            PonderIndex.addPlugin(new RedExtendModPonderPlugin());
        }
        List<String> softList = Config.getDisabledModList();
        if (softList.isEmpty()) return;

        //1. 跳过 Mod 容器初始化（防止注册方块/物品/渲染）
        for (String modid : softList) {
            ModContainer container = ModList.get().getModContainerById(modid).orElse(null);
            if (container != null) {
                //让模组后续生命周期事件不再派发
                Objects.requireNonNull(container.getEventBus()).start();
                RedExtendMod.LOGGER.warn("[Rem] 软禁用 - 关闭 {} 的 EventBus", modid);
            }
        }
        // Some client setup code
        RedExtendMod.LOGGER.info("HELLO FROM CLIENT SETUP");
        RedExtendMod.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
