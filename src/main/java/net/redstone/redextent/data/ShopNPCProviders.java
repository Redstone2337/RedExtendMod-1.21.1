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
 * 商店NPC提供者
 */
public class ShopNPCProviders extends PixelmonNPCProvider {
    
    public ShopNPCProviders(PackOutput output) {
        super(output, "pixelmon", "shop");
    }

    @Override
    protected void registerNpcs(BiConsumer<String, NpcDefinitionCodec> consumer) {
        registerShopNpcs(consumer);
    }

    private void registerShopNpcs(BiConsumer<String, NpcDefinitionCodec> consumer) {
        // 创建商店物品列表
        List<NpcPresetBuilder.ShopItem> shopItems = List.of(
            NpcPresetBuilder.ShopItem.of("pixelmon:master_ball", 1, 50000.0, 12500.0),
            NpcPresetBuilder.ShopItem.of("pixelmon:rare_candy", 10, 50000.0, 12500.0),
            NpcPresetBuilder.ShopItem.of("pixelmon:park_ball", 1, 30000.0, 15000.0),
            NpcPresetBuilder.ShopItem.of("pixelmon:ability_capsule", 1, 10000.0, 2500.0)
        );

        // 创建文本内容
        NpcPresetBuilder.TextContent title = NpcPresetBuilder.TextContent.literal("毛毛龙的商店");
        NpcPresetBuilder.TextContent greeting = NpcPresetBuilder.TextContent.literal("欢迎来到毛毛龙商店，这里只有最稀有的商品！");
        NpcPresetBuilder.TextContent goodbye = NpcPresetBuilder.TextContent.literal("感谢您的光临，期待再次为您服务！");

        // 修复 ResourceLocation 创建方式
        List<ResourceLocation> textures = List.of(
            ResourceLocation.parse("rem:textures/steve/info_dragon.png")
        );

        // 创建商店NPC
        NpcPresetBuilder.NpcTemplate shopNpc = NpcPresetBuilder.createShopNpc(
            "maomao_dragon_shop",
            List.of("毛毛龙"),
            textures,
            title,
            greeting,
            goodbye,
            shopItems,
            false,
            false
        );

        consumer.accept("maomao_dragon_shop", shopNpc.build());
    }
}
