package net.redstone233.redextent.core.event;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.redstone233.redextent.core.brewing.RhinoBrewingRecipeParser;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BrewingRecipeReloadListener implements PreparableReloadListener {
    @Override
    public @NotNull CompletableFuture<Void> reload(
            PreparationBarrier preparationBarrier,
            @NotNull ResourceManager resourceManager,
            @NotNull ProfilerFiller profilerFiller,
            @NotNull ProfilerFiller profilerFiller1,
            @NotNull Executor executor,
            @NotNull Executor executor1
    ) {
        return preparationBarrier.wait(null).thenRun(() -> RhinoBrewingRecipeParser.reloadAll(resourceManager));
    }
}
