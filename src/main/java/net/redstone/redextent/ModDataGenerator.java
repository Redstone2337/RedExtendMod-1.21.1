package net.redstone.redextent;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.redstone.redextent.data.BattleNPCProvider;
import net.redstone.redextent.data.ModEnglishLanguageProvider;
import net.redstone.redextent.data.PixelmonAbilityProvider;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = RedExtendMod.MOD_ID)
public class ModDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(),new PixelmonAbilityProvider(packOutput));
        generator.addProvider(event.includeClient(), new ModEnglishLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new BattleNPCProvider(packOutput));
    }
}
