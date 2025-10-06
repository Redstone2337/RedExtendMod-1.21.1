/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
 
package net.redstone.redextent.core.npc;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.redstone.redextent.core.codecs.NpcDefinitionCodec;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * NPC 数据提供器 - 支持批量生成和自定义路径
 */
public abstract class NpcDataProvider implements DataProvider {
    private final PackOutput output;
    private final String modId;
    private final String basePath;

    protected NpcDataProvider(PackOutput output, String modId) {
        this(output, modId, "pixelmon/npc/preset");
    }

    protected NpcDataProvider(PackOutput output, String modId, String basePath) {
        this.output = output;
        this.modId = modId;
        this.basePath = basePath;
    }

    protected abstract void registerNpcs(BiConsumer<String, NpcDefinitionCodec> consumer);

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<String, NpcDefinitionCodec> npcs = new HashMap<>();
        registerNpcs(npcs::put);
        
        List<CompletableFuture<?>> futures = new ArrayList<>();
        npcs.forEach((name, definition) -> {
            Path filePath = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(modId)
                .resolve(basePath)
                .resolve(name + ".json");
            
            // 修改 getOrThrow 调用
        JsonElement json = NpcDefinitionCodec.CODEC.encodeStart(JsonOps.INSTANCE, definition)
            .getOrThrow(msg -> { // 移除 boolean 参数
                throw new IllegalArgumentException("Failed to encode NPC definition for " + name + ": " + msg);
            });
            
            futures.add(DataProvider.saveStable(cache, json, filePath));
        });
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    @Override
    public String getName() {
        return "NPC Definitions";
    }
}