/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
 
package net.redstone.redextent.core.generator;

import net.minecraft.data.PackOutput;
import net.redstone.redextent.core.npc.NpcDataProvider;
import net.redstone.redextent.core.codecs.NpcDefinitionCodec;
import net.redstone.redextent.core.npc.NpcPresetBuilder;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 像素精灵NPC提供者基类
 */
public abstract class PixelmonNPCProvider extends NpcDataProvider {
    
    protected PixelmonNPCProvider(PackOutput output, String modId, String subPath) {
        super(output, modId, "pixelmon/npc/" + subPath);
    }

    /**
     * 创建商店物品JSON对象
     */
    public static JsonObject createShopItem(String itemId, int count, double buyPrice, double sellPrice) {
        JsonObject item = new JsonObject();
        JsonObject itemData = new JsonObject();
        itemData.addProperty("id", itemId);
        itemData.addProperty("count", count);
        item.add("item", itemData);
        item.addProperty("buyPrice", buyPrice);
        item.addProperty("sellPrice", sellPrice);
        return item;
    }

    /**
     * 创建文本标题JSON对象
     */
    public static JsonObject createTextTitle(String text, String color, boolean bold, boolean italic, boolean underlined) {
        JsonObject title = new JsonObject();
        title.addProperty("translate", text);
        title.addProperty("color", color);
        title.addProperty("bold", bold);
        title.addProperty("italic", italic);
        title.addProperty("underlined", underlined);
        return title;
    }

    /**
     * 创建文本消息JSON对象
     */
    public static JsonObject createTextMessage(String text) {
        JsonObject message = new JsonObject();
        message.addProperty("translate", text);
        return message;
    }

    /**
     * 创建宝可梦规格字符串（带IVs和EVs）
     */
    public String createPokemonSpecWithIVsEVs(String pokemon, int level, String ability, String heldItem, String nature,
                                                List<String> moves, Map<String, Integer> ivs, Map<String, Integer> evs) {
        StringBuilder spec = new StringBuilder();
        
        spec.append(pokemon).append(" lvl:").append(level);
        
        if (ability != null && !ability.isEmpty()) {
            spec.append(" ability:").append(ability);
        }
        
        if (heldItem != null && !heldItem.isEmpty()) {
            spec.append(" helditem:").append(heldItem);
        }
        
        if (nature != null && !nature.isEmpty()) {
            spec.append(" nature:").append(nature);
        }
        
        // 添加IVs
        if (ivs != null) {
            ivs.forEach((stat, value) -> {
                if (value != null && value > 0) {
                    spec.append(" iv").append(stat).append(":").append(value);
                }
            });
        }
        
        // 添加EVs
        if (evs != null) {
            evs.forEach((stat, value) -> {
                if (value != null && value > 0) {
                    spec.append(" ev").append(stat).append(":").append(value);
                }
            });
        }
        
        // 添加技能
        if (moves != null && !moves.isEmpty()) {
            for (int i = 0; i < moves.size(); i++) {
                spec.append(" move").append(i + 1).append(":").append(moves.get(i));
            }
        }
        
        return spec.toString();
    }
}
