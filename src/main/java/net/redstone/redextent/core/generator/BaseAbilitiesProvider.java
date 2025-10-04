package net.redstone.redextent.core.generator;

import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class BaseAbilitiesProvider implements DataProvider {
    private final Map<String, String> data = new TreeMap<>();
    private final PackOutput output;
    private final String modid;

    public BaseAbilitiesProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
    }

    protected abstract void addAbilities();

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        this.addAbilities();
        return !this.data.isEmpty() ?
                this.saveAll(cache, this.output.getOutputFolder(Target.DATA_PACK)) :
                CompletableFuture.allOf();
    }

    @Override
    public @NotNull String getName() {
        return "Abilities for mod: " + this.modid;
    }

    private CompletableFuture<?> saveAll(CachedOutput cache, Path basePath) {
        CompletableFuture<?>[] futures = this.data.entrySet().stream()
                .map(entry -> this.saveAbility(cache, basePath, entry.getKey(), entry.getValue()))
                .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(futures);
    }

    private CompletableFuture<?> saveAbility(CachedOutput cache, Path basePath, String abilityName, String className) {
        JsonObject json = new JsonObject();
        json.addProperty("className", className);

        Path target = basePath
                .resolve(this.modid)
                .resolve("abilities")
                .resolve(abilityName + ".json");

        return DataProvider.saveStable(cache, json, target);
    }

    public void addAbility(String abilityName, String className) {
        if (this.data.put(abilityName, className) != null) {
            throw new IllegalStateException("Duplicate ability name: " + abilityName);
        }
    }
    public void addAbility(String className) {
        String simpleName = this.extractSimpleClassName(className);
        this.addAbility(simpleName.toLowerCase(), className);
    }

    public void addAbility(Supplier<String> className) {
        this.addAbility(className.get());
    }

    public void addAbility(ResourceLocation abilityId, String className) {
        this.addAbility(abilityId.getPath(), className);
    }

    public void addAbility(Supplier<ResourceLocation> abilityId, Supplier<String> className) {
        this.addAbility(abilityId.get(), className.get());
    }

    private String extractSimpleClassName(String className) {
        int lastDot = className.lastIndexOf('.');
        return lastDot == -1 ? className : className.substring(lastDot + 1);
    }
}