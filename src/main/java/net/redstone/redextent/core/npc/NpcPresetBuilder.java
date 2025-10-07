/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
 
package net.redstone.redextent.core.npc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.redstone.redextent.core.codecs.*;
import net.redstone.redextent.core.codecs.InteractionDefinition.Interaction;
import net.redstone.redextent.core.codecs.InteractionDefinition.InteractionResults;
import net.redstone.redextent.core.codecs.InteractionDefinition.InteractionSet;
import com.google.gson.JsonElement;

import java.util.*;

/**
 * NPC 预设构建器 - 提供快捷方法生成常见类型的NPC
 */
public class NpcPresetBuilder {
    
    /**
     * 批量生成NPC
     */
    public static Map<String, NpcDefinitionCodec> batchGenerateNpcs(List<NpcTemplate> templates) {
        Map<String, NpcDefinitionCodec> result = new HashMap<>();
        for (NpcTemplate template : templates) {
            result.put(template.name(), template.build());
        }
        return result;
    }
    
    /**
     * 快捷生成道馆馆主NPC
     */
    public static NpcTemplate createGymLeader(
        String name,
        String displayName,
        ResourceLocation texture,
        List<String> partySpecs,
        TextContent title,
        TextContent winMessage,
        TextContent loseMessage,
        TextContent cooldownMessage,
        double moneyReward,
        List<RewardItem> rewardItems,
        String leaderKey
    ) {
        return new NpcTemplate(name, () -> {
            NpcProperties.Title titleObj = NpcProperties.Title.builder()
                .translate(title.translate())
                .color("#8E44AD")
                .bold(true)
                .italic(false)
                .underlined(false)
                .build();

            return NpcDefinitionCodec.builder()
                .properties(NpcProperties.builder()
                    .health(20.0)
                    .eyeHeight(1.9)
                    .dimensions(0.65, 2.0)
                    .title(titleObj)
                    .pushable(false)
                    .child(false)
                    .invulnerable(false)
                    .immovable(false)
                    .nameplate(true)
                    .build())
                .names(NameDefinition.builder()
                    .values(displayName)
                    .constant()
                    .build())
                .party(PartyDefinition.builder()
                    .specs(partySpecs)
                    .build())
                .models(ModelDefinition.builder()
                    .singleModel(true, texture)
                    .build())
                .goals(GoalDefinition.builder()
                    .lookAtNearby(10, 0.9)
                    .build())
                .interactions(createGymLeaderInteractions(
                    title.translate(), winMessage, loseMessage, cooldownMessage, 
                    moneyReward, rewardItems, leaderKey
                ))
                .build();
        });
    }

    /**
     * 快捷生成聊天对话NPC
     */
    public static NpcTemplate createChatNpc(
        String name,
        List<String> displayNames,
        List<ResourceLocation> textures,
        TextContent title,
        List<TextContent> dialoguePages,
        boolean randomName,
        boolean randomModel
    ) {
        return new NpcTemplate(name, () -> {
            NpcProperties.Title titleObj = NpcProperties.Title.builder()
                .translate(title.translate())
                .color("#F8F8F2")
                .bold(true)
                .italic(false)
                .underlined(false)
                .build();

            NameDefinition.Builder nameBuilder = NameDefinition.builder()
                .values(displayNames);
            
            if (randomName) {
                nameBuilder.random();
            } else {
                nameBuilder.constant();
            }

            ModelDefinition.Builder modelBuilder = ModelDefinition.builder();
            if (randomModel && textures.size() > 1) {
                List<ModelDefinition.ModelValue> modelValues = new ArrayList<>();
                for (ResourceLocation texture : textures) {
                    modelValues.add(ModelDefinition.ModelValue.of(texture));
                }
                modelBuilder.randomModels(modelValues);
            } else {
                modelBuilder.singleModel(textures.get(0));
            }

            return NpcDefinitionCodec.builder()
                .properties(NpcProperties.builder()
                    .health(20.0)
                    .eyeHeight(1.0)
                    .dimensions(1.0, 2.0)
                    .title(titleObj)
                    .pushable(false)
                    .child(false)
                    .invulnerable(false)
                    .immovable(false)
                    .nameplate(true)
                    .build())
                .names(nameBuilder.build())
                .party(PartyDefinition.builder().empty().build())
                .models(modelBuilder.build())
                .goals(GoalDefinition.builder()
                    .lookAtNearby(4.0, 0.9)
                    .build())
                .interactions(createChatNpcInteractions(title.translate(), dialoguePages))
                .build();
        });
    }

    /**
     * 快捷生成商店NPC
     */
    public static NpcTemplate createShopNpc(
        String name,
        List<String> displayNames,
        List<ResourceLocation> textures,
        TextContent title,
        TextContent greeting,
        TextContent goodbye,
        List<ShopItem> shopItems,
        boolean randomName,
        boolean randomModel
    ) {
        return new NpcTemplate(name, () -> {
            NpcProperties.Title titleObj = NpcProperties.Title.builder()
                .translate(title.translate())
                .color("#2176FF")
                .bold(true)
                .italic(false)
                .underlined(false)
                .build();

            NameDefinition.Builder nameBuilder = NameDefinition.builder()
                .values(displayNames);
            
            if (randomName) {
                nameBuilder.random();
            } else {
                nameBuilder.constant();
            }

            ModelDefinition.Builder modelBuilder = ModelDefinition.builder();
            if (randomModel && textures.size() > 1) {
                List<ModelDefinition.ModelValue> modelValues = new ArrayList<>();
                for (ResourceLocation texture : textures) {
                    modelValues.add(ModelDefinition.ModelValue.of(texture));
                }
                modelBuilder.randomModels(modelValues);
            } else {
                modelBuilder.singleModel(textures.get(0));
            }

            return NpcDefinitionCodec.builder()
                .properties(NpcProperties.builder()
                    .health(20.0)
                    .eyeHeight(1.0)
                    .dimensions(1.0, 2.0)
                    .title(titleObj)
                    .pushable(false)
                    .child(false)
                    .invulnerable(false)
                    .immovable(false)
                    .nameplate(true)
                    .build())
                .names(nameBuilder.build())
                .party(PartyDefinition.builder().empty().build())
                .models(modelBuilder.build())
                .goals(GoalDefinition.builder()
                    .lookAtNearby(5.0, 0.9)
                    .build())
                .interactions(createShopNpcInteractions(title.translate(), greeting, goodbye, shopItems))
                .build();
        });
    }

    // 私有方法 - 创建道馆馆主交互
    private static InteractionDefinition createGymLeaderInteractions(
        String titleKey,
        TextContent winMessage,
        TextContent loseMessage,
        TextContent cooldownMessage,
        double moneyReward,
        List<RewardItem> rewardItems,
        String leaderKey
    ) {
        List<JsonElement> rewardItemsData = new ArrayList<>();
        for (RewardItem item : rewardItems) {
            Map<String, Object> itemData = Map.of(
                "id", item.itemId(),
                "count", item.count()
            );
            rewardItemsData.add(InteractionDefinition.toJsonElement(itemData));
        }

        List<Interaction> interactions = List.of(
            // 战斗开始交互
            Interaction.of(
                "pixelmon:right_click",
                InteractionDefinition.toJsonElement(Map.of("type", "pixelmon:true")),
                InteractionResults.constant(List.of(
                    InteractionDefinition.toJsonElement(Map.of(
                        "type", "pixelmon:open_dialogue",
                        "title", titleKey,
                        "message", "pixelmon.npc.dialogue.battle.leader.gym.dragon.1.initiate"
                    ))
                ))
            ),
            // 战斗胜利交互
            Interaction.of(
                "pixelmon:win_battle",
                InteractionDefinition.toJsonElement(Map.of("type", "pixelmon:true")),
                InteractionResults.constant(List.of(
                    InteractionDefinition.toJsonElement(Map.of(
                        "type", "pixelmon:open_dialogue",
                        "title", titleKey,
                        "message", winMessage.translate(),
                        "fire_close_event", false
                    )),
                    InteractionDefinition.toJsonElement(Map.of(
                        "type", "pixelmon:give_money",
                        "money", moneyReward
                    )),
                    InteractionDefinition.toJsonElement(Map.of(
                        "type", "pixelmon:give_item",
                        "items", rewardItemsData
                    )),
                    InteractionDefinition.toJsonElement(Map.of(
                        "type", "pixelmon:set_cooldown",
                        "player", Map.of(
                            "key", "pixelmon:player",
                            "type", "pixelmon:context_player"
                        ),
                        "key", "pixelmon:" + leaderKey
                    ))
                ))
            ),
            // 战斗失败交互
            Interaction.of(
                "pixelmon:lose_battle",
                InteractionDefinition.toJsonElement(Map.of("type", "pixelmon:true")),
                InteractionResults.constant(List.of(
                    InteractionDefinition.toJsonElement(Map.of(
                        "type", "pixelmon:open_dialogue",
                        "title", titleKey,
                        "message", loseMessage.translate(),
                        "fire_close_event", false
                    ))
                ))
            )
        );

        return InteractionDefinition.builder()
            .interactions(List.of(InteractionSet.of(interactions.toArray(new Interaction[0]))))
            .constant()
            .build();
    }

    // 私有方法 - 创建聊天NPC交互
    private static InteractionDefinition createChatNpcInteractions(String titleKey, List<TextContent> dialoguePages) {
        List<String> pageKeys = new ArrayList<>();
        for (TextContent page : dialoguePages) {
            pageKeys.add(page.translate());
        }
        
        List<Interaction> interactions = List.of(
            Interaction.of(
                "pixelmon:right_click",
                InteractionDefinition.toJsonElement(Map.of("type", "pixelmon:true")),
                InteractionResults.constant(List.of(
                    InteractionDefinition.toJsonElement(Map.of(
                        "type", "pixelmon:open_paged_dialogue",
                        "title", titleKey,
                        "pages", pageKeys
                    ))
                ))
            )
        );

        return InteractionDefinition.builder()
            .interactions(List.of(InteractionSet.of(interactions.toArray(new Interaction[0]))))
            .random()
            .build();
    }

    // 私有方法 - 创建商店NPC交互
    private static InteractionDefinition createShopNpcInteractions(
        String titleKey,
        TextContent greeting,
        TextContent goodbye,
        List<ShopItem> shopItems
    ) {
        List<JsonElement> shopItemsData = new ArrayList<>();
        for (ShopItem item : shopItems) {
            Map<String, Object> itemData = Map.of(
                "item", Map.of("id", item.itemId(), "count", item.count()),
                "buyPrice", item.buyPrice(),
                "sellPrice", item.sellPrice()
            );
            shopItemsData.add(InteractionDefinition.toJsonElement(itemData));
        }

        List<Interaction> interactions = List.of(
            Interaction.of(
                "pixelmon:right_click",
                InteractionDefinition.toJsonElement(Map.of("type", "pixelmon:true")),
                InteractionResults.constant(List.of(
                    InteractionDefinition.toJsonElement(Map.of(
                        "type", "pixelmon:open_dialogue",
                        "title", titleKey,
                        "message", greeting.translate()
                    ))
                ))
            ),
            Interaction.of(
                "pixelmon:close_dialogue",
                InteractionDefinition.toJsonElement(Map.of("type", "pixelmon:true")),
                InteractionResults.constant(List.of(
                    InteractionDefinition.toJsonElement(Map.of(
                        "type", "pixelmon:open_shop",
                        "items", shopItemsData
                    ))
                ))
            ),
            Interaction.of(
                "pixelmon:close_shop",
                InteractionDefinition.toJsonElement(Map.of("type", "pixelmon:true")),
                InteractionResults.constant(List.of(
                    InteractionDefinition.toJsonElement(Map.of(
                        "type", "pixelmon:open_dialogue",
                        "title", titleKey,
                        "message", goodbye.translate(),
                        "fire_close_event", false
                    ))
                ))
            )
        );

        return InteractionDefinition.builder()
            .interactions(List.of(InteractionSet.of(interactions.toArray(new Interaction[0]))))
            .random()
            .build();
    }

    // 记录类型定义
    public record NpcTemplate(String name, java.util.function.Supplier<NpcDefinitionCodec> builder) {
        public NpcDefinitionCodec build() {
            return builder.get();
        }
    }

    public record TextContent(String text, String translate) {
        public TextContent(String text) {
            this(text, text);
        }
        
        public static TextContent translated(String translateKey) {
            return new TextContent(null, translateKey);
        }
        
        public static TextContent literal(String text) {
            return new TextContent(text, "pixelmon.literal." + text.hashCode());
        }
    }

    public record RewardItem(Item item, String itemId, int count) {
        public RewardItem(Item item, int count) {
            this(item, item.toString(), count);
        }
        
        public RewardItem(String itemId, int count) {
            this(Items.AIR, itemId, count);
        }
        
        public String itemId() {
            return item != Items.AIR ? item.toString() : itemId;
        }
        
        public static RewardItem of(Item item, int count) {
            return new RewardItem(item, count);
        }
        
        public static RewardItem of(String itemId, int count) {
            return new RewardItem(itemId, count);
        }
    }

    public record ShopItem(String itemId, int count, double buyPrice, double sellPrice) {
        public static ShopItem of(Item item, int count, double buyPrice, double sellPrice) {
            return new ShopItem(item.toString(), count, buyPrice, sellPrice);
        }
        
        public static ShopItem of(String itemId, int count, double buyPrice, double sellPrice) {
            return new ShopItem(itemId, count, buyPrice, sellPrice);
        }
    }
}