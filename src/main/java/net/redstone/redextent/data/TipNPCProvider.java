/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
 
package net.redstone.redextent.data;

import net.minecraft.data.PackOutput;
import net.redstone.redextent.core.generator.PixelmonNPCProvider;
import net.redstone.redextent.core.codecs.NpcDefinitionCodec;
import net.redstone.redextent.core.npc.NpcPresetBuilder;
import net.minecraft.resources.ResourceLocation;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 提示NPC提供者
 */
public class TipNPCProvider extends PixelmonNPCProvider {
    
    public TipNPCProvider(PackOutput output, String modId) {
        super(output, modId, "tips");
    }

    @Override
    protected void registerNpcs(BiConsumer<String, NpcDefinitionCodec> consumer) {
        registerTipNpcs(consumer);
    }

    private void registerTipNpcs(BiConsumer<String, NpcDefinitionCodec> consumer) {
        // 创建对话页面
        List<NpcPresetBuilder.TextContent> dialoguePages = List.of(
            NpcPresetBuilder.TextContent.literal("欢迎来到我们的像素精灵世界！"),
            NpcPresetBuilder.TextContent.literal("探索、战斗、培育，体验前所未有的冒险！"),
            NpcPresetBuilder.TextContent.literal("记得定期检查我们的更新，获取最新内容和活动信息！")
        );

        // 创建标题
        NpcPresetBuilder.TextContent title = NpcPresetBuilder.TextContent.literal("模组宣传");

        // 创建提示NPC
        NpcPresetBuilder.NpcTemplate tipNpc = NpcPresetBuilder.createChatNpc(
            "mod_promotion_npc",
            List.of("向导小智"),
            List.of(new ResourceLocation("textures/entity/npc/guide.png")),
            title,
            dialoguePages,
            false,
            false
        );

        consumer.accept("mod_promotion_npc", tipNpc.build());
    }
}