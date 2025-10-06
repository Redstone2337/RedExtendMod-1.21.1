/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.redstone.redextent.core.npc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于以类型安全的方式创建宝可梦NPC定义的构建器类。
 */
public class NPCDefinition {
    private JsonObject interactions;
    private JsonObject properties;
    private JsonObject party;
    private JsonObject names;
    private JsonObject models;
    private JsonObject goals;

    private NPCDefinition() {}

    public static Builder builder() {
        return new Builder();
    }

    public JsonObject serialize() {
        JsonObject root = new JsonObject();

        if (interactions != null) root.add("interactions", interactions);
        if (properties != null) root.add("properties", properties);
        if (party != null) root.add("party", party);
        if (names != null) root.add("names", names);
        if (models != null) root.add("models", models);
        if (goals != null) root.add("goals", goals);

        return root;
    }

    public static class Builder {
        private final NPCDefinition definition;

        private Builder() {
            this.definition = new NPCDefinition();
        }

        /**
         * 使用自定义标题对象的属性配置
         */
        public Builder withTitleProperties(float health, float eyeHeight, float width, float height,
                                           JsonObject title, boolean pushable, boolean child,
                                           boolean invulnerable, boolean immovable, boolean nameplate) {
            JsonObject properties = new JsonObject();
            JsonObject value = new JsonObject();

            value.addProperty("health", health);
            value.addProperty("eyeHeight", eyeHeight);

            JsonObject dimensions = new JsonObject();
            dimensions.addProperty("width", width);
            dimensions.addProperty("height", height);
            value.add("dimensions", dimensions);

            value.add("title", title); // 使用传入的标题对象

            value.addProperty("pushable", pushable);
            value.addProperty("child", child);
            value.addProperty("invulnerable", invulnerable);
            value.addProperty("immovable", immovable);
            value.addProperty("nameplate", nameplate);

            properties.add("value", value);
            properties.addProperty("type", "pixelmon:constant");
            definition.properties = properties;
            return this;
        }

        /**
         * 使用文本标题而非翻译键的属性配置
         */
        public Builder withTextTitleProperties(float health, float eyeHeight, float width, float height,
                                              String titleText, String color, boolean bold,
                                              boolean italic, boolean underlined, boolean pushable,
                                              boolean child, boolean invulnerable, boolean immovable,
                                              boolean nameplate) {
            JsonObject properties = new JsonObject();
            JsonObject value = new JsonObject();

            value.addProperty("health", health);
            value.addProperty("eyeHeight", eyeHeight);

            JsonObject dimensions = new JsonObject();
            dimensions.addProperty("width", width);
            dimensions.addProperty("height", height);
            value.add("dimensions", dimensions);

            JsonObject title = new JsonObject();
            title.addProperty("text", titleText);
            title.addProperty("color", color);
            title.addProperty("bold", bold);
            title.addProperty("italic", italic);
            title.addProperty("underlined", underlined);
            value.add("title", title);

            value.addProperty("pushable", pushable);
            value.addProperty("child", child);
            value.addProperty("invulnerable", invulnerable);
            value.addProperty("immovable", immovable);
            value.addProperty("nameplate", nameplate);

            properties.add("value", value);
            properties.addProperty("type", "pixelmon:constant");
            definition.properties = properties;
            return this;
        }

        /**
         * 使用翻译键标题的属性配置
         */
        public Builder withTranslateTitleProperties(float health, float eyeHeight, float width, float height,
                                                   String titleTranslate, String color, boolean bold,
                                                   boolean italic, boolean underlined, boolean pushable,
                                                   boolean child, boolean invulnerable, boolean immovable,
                                                   boolean nameplate) {
            JsonObject properties = new JsonObject();
            JsonObject value = new JsonObject();

            value.addProperty("health", health);
            value.addProperty("eyeHeight", eyeHeight);

            JsonObject dimensions = new JsonObject();
            dimensions.addProperty("width", width);
            dimensions.addProperty("height", height);
            value.add("dimensions", dimensions);

            JsonObject title = new JsonObject();
            title.addProperty("translate", titleTranslate);
            title.addProperty("color", color);
            title.addProperty("bold", bold);
            title.addProperty("italic", italic);
            title.addProperty("underlined", underlined);
            value.add("title", title);

            value.addProperty("pushable", pushable);
            value.addProperty("child", child);
            value.addProperty("invulnerable", invulnerable);
            value.addProperty("immovable", immovable);
            value.addProperty("nameplate", nameplate);

            properties.add("value", value);
            properties.addProperty("type", "pixelmon:constant");
            definition.properties = properties;
            return this;
        }

        // 交互配置
        public Builder withInteractions(JsonObject interactions) {
            definition.interactions = interactions;
            return this;
        }

        public Builder withUniformInteractions(List<JsonObject> interactionValues) {
            JsonObject interactions = new JsonObject();
            JsonArray valuesArray = new JsonArray();
            interactionValues.forEach(valuesArray::add);
            interactions.add("values", valuesArray);
            interactions.addProperty("type", "pixelmon:uniformly_random");
            definition.interactions = interactions;
            return this;
        }

        public Builder withConstantInteractions(JsonObject interactionValue) {
            JsonObject interactions = new JsonObject();
            interactions.add("value", interactionValue);
            interactions.addProperty("type", "pixelmon:constant");
            definition.interactions = interactions;
            return this;
        }

        // 属性配置
        public Builder withProperties(JsonObject properties) {
            definition.properties = properties;
            return this;
        }

        public Builder withBasicProperties(float health, float eyeHeight, float width, float height,
                                           boolean pushable, boolean child, boolean invulnerable,
                                           boolean immovable, boolean nameplate) {
            JsonObject properties = new JsonObject();
            JsonObject value = new JsonObject();

            value.addProperty("health", health);
            value.addProperty("eyeHeight", eyeHeight);

            JsonObject dimensions = new JsonObject();
            dimensions.addProperty("width", width);
            dimensions.addProperty("height", height);
            value.add("dimensions", dimensions);

            value.addProperty("pushable", pushable);
            value.addProperty("child", child);
            value.addProperty("invulnerable", invulnerable);
            value.addProperty("immovable", immovable);
            value.addProperty("nameplate", nameplate);

            properties.add("value", value);
            properties.addProperty("type", "pixelmon:constant");
            definition.properties = properties;
            return this;
        }

        public Builder withTitledProperties(float health, float eyeHeight, float width, float height,
                                            String titleTranslate, String color, boolean bold,
                                            boolean italic, boolean underlined, boolean pushable,
                                            boolean child, boolean invulnerable, boolean immovable,
                                            boolean nameplate) {
            JsonObject properties = new JsonObject();
            JsonObject value = new JsonObject();

            value.addProperty("health", health);
            value.addProperty("eyeHeight", eyeHeight);

            JsonObject dimensions = new JsonObject();
            dimensions.addProperty("width", width);
            dimensions.addProperty("height", height);
            value.add("dimensions", dimensions);

            JsonObject title = new JsonObject();
            title.addProperty("translate", titleTranslate);
            title.addProperty("color", color);
            title.addProperty("bold", bold);
            title.addProperty("italic", italic);
            title.addProperty("underlined", underlined);
            value.add("title", title);

            value.addProperty("pushable", pushable);
            value.addProperty("child", child);
            value.addProperty("invulnerable", invulnerable);
            value.addProperty("immovable", immovable);
            value.addProperty("nameplate", nameplate);

            properties.add("value", value);
            properties.addProperty("type", "pixelmon:constant");
            definition.properties = properties;
            return this;
        }

        // 队伍配置
        public Builder withParty(JsonObject party) {
            definition.party = party;
            return this;
        }

        public Builder withEmptyParty() {
            JsonObject party = new JsonObject();
            JsonObject value = new JsonObject();
            value.addProperty("type", "pixelmon:empty");
            party.add("value", value);
            party.addProperty("type", "pixelmon:constant");
            definition.party = party;
            return this;
        }

        public Builder withSpecParty(List<String> specs) {
            JsonObject party = new JsonObject();
            JsonObject value = new JsonObject();

            JsonArray specsArray = new JsonArray();
            specs.forEach(specsArray::add);
            value.add("specs", specsArray);
            value.addProperty("type", "pixelmon:spec");

            party.add("value", value);
            party.addProperty("type", "pixelmon:constant");
            definition.party = party;
            return this;
        }

        // 名称配置
        public Builder withNames(JsonObject names) {
            definition.names = names;
            return this;
        }

        public Builder withRandomNames(List<String> nameList) {
            JsonObject names = new JsonObject();
            JsonArray valuesArray = new JsonArray();
            nameList.forEach(valuesArray::add);
            names.add("values", valuesArray);
            names.addProperty("type", "pixelmon:uniformly_random");
            definition.names = names;
            return this;
        }

        public Builder withSingleName(String name) {
            JsonObject names = new JsonObject();
            JsonArray valuesArray = new JsonArray();
            valuesArray.add(name);
            names.add("values", valuesArray);
            names.addProperty("type", "pixelmon:uniformly_random");
            definition.names = names;
            return this;
        }

        // 模型配置
        public Builder withModels(JsonObject models) {
            definition.models = models;
            return this;
        }

        public Builder withRandomPlayerModels(List<PlayerModel> modelList) {
            JsonObject models = new JsonObject();
            JsonArray valuesArray = new JsonArray();

            for (PlayerModel model : modelList) {
                JsonObject modelObj = new JsonObject();
                modelObj.addProperty("slim", model.slim);
                modelObj.addProperty("type", "pixelmon:player");

                // 支持简单纹理字符串和复杂纹理对象
                if (model.textureFallback != null && !model.textureFallback.equals(model.textureResource)) {
                    JsonObject texture = new JsonObject();
                    JsonObject resource = new JsonObject();
                    resource.addProperty("resource", model.textureResource);
                    resource.addProperty("fallback", model.textureFallback);
                    texture.add("resource", resource);
                    texture.addProperty("type", "pixelmon:fallback");
                    modelObj.add("texture", texture);
                } else {
                    modelObj.addProperty("texture", model.textureResource);
                }

                valuesArray.add(modelObj);
            }

            models.add("values", valuesArray);
            models.addProperty("type", "pixelmon:uniformly_random");
            definition.models = models;
            return this;
        }

        public Builder withSinglePlayerModel(boolean slim, String textureResource, String textureFallback) {
            JsonObject models = new JsonObject();
            JsonObject value = new JsonObject();

            value.addProperty("slim", slim);
            value.addProperty("type", "pixelmon:player");

            // 支持简单纹理字符串和复杂纹理对象
            if (textureFallback != null && !textureFallback.equals(textureResource)) {
                JsonObject texture = new JsonObject();
                JsonObject resource = new JsonObject();
                resource.addProperty("resource", textureResource);
                resource.addProperty("fallback", textureFallback);
                texture.add("resource", resource);
                texture.addProperty("type", "pixelmon:fallback");
                value.add("texture", texture);
            } else {
                value.addProperty("texture", textureResource);
            }

            models.add("value", value);
            models.addProperty("type", "pixelmon:constant");
            definition.models = models;
            return this;
        }

        public Builder withSimplePlayerModel(boolean slim, String textureResource) {
            return withSinglePlayerModel(slim, textureResource, null);
        }

        // 行为目标配置
        public Builder withGoals(JsonObject goals) {
            definition.goals = goals;
            return this;
        }

        public Builder withLookAtNearbyGoal(float lookDistance, float probability, int priority) {
            JsonObject goals = new JsonObject();
            JsonObject value = new JsonObject();

            JsonArray goalsArray = new JsonArray();
            JsonObject goal = new JsonObject();
            JsonObject provider = new JsonObject();

            provider.addProperty("type", "pixelmon:look_at_nearby");
            provider.addProperty("look_distance", lookDistance);
            provider.addProperty("probability", probability);

            goal.add("provider", provider);
            goal.addProperty("priority", priority);
            goalsArray.add(goal);

            value.add("goals", goalsArray);
            goals.add("value", value);
            goals.addProperty("type", "pixelmon:constant");
            definition.goals = goals;
            return this;
        }

        public NPCDefinition build() {
            return definition;
        }
    }

    /**
     * 玩家模型配置类
     */
    public static class PlayerModel {
        public final boolean slim;
        public final String textureResource;
        public final String textureFallback;

        public PlayerModel(boolean slim, String textureResource, String textureFallback) {
            this.slim = slim;
            this.textureResource = textureResource;
            this.textureFallback = textureFallback;
        }

        /**
         * 创建玩家模型实例
         *
         * @param slim 是否为瘦手臂模型
         * @param textureResource 纹理资源路径
         * @return 玩家模型实例
         */
        public static PlayerModel of(boolean slim, String textureResource) {
            return new PlayerModel(slim, textureResource, textureResource);
        }

        /**
         * 创建带有fallback纹理的玩家模型实例
         *
         * @param slim 是否为瘦手臂模型
         * @param textureResource 纹理资源路径
         * @param textureFallback 备用纹理路径
         * @return 玩家模型实例
         */
        public static PlayerModel withFallback(boolean slim, String textureResource, String textureFallback) {
            return new PlayerModel(slim, textureResource, textureFallback);
        }
    }

    /**
     * 对话消息构建工具类
     */
    public static class DialogueBuilder {
        
        /**
         * 创建文本消息对象
         */
        public static JsonObject createTextMessage(String text) {
            JsonObject message = new JsonObject();
            message.addProperty("text", text);
            return message;
        }

        /**
         * 创建翻译消息对象
         */
        public static JsonObject createTranslateMessage(String translate) {
            JsonObject message = new JsonObject();
            message.addProperty("translate", translate);
            return message;
        }

        /**
         * 创建文本标题对象
         */
        public static JsonObject createTextTitle(String text, String color, boolean bold, 
                                                boolean italic, boolean underlined) {
            JsonObject title = new JsonObject();
            title.addProperty("text", text);
            title.addProperty("color", color);
            title.addProperty("bold", bold);
            title.addProperty("italic", italic);
            title.addProperty("underlined", underlined);
            return title;
        }

        /**
         * 创建翻译标题对象
         */
        public static JsonObject createTranslateTitle(String translate, String color, boolean bold,
                                                     boolean italic, boolean underlined) {
            JsonObject title = new JsonObject();
            title.addProperty("translate", translate);
            title.addProperty("color", color);
            title.addProperty("bold", bold);
            title.addProperty("italic", italic);
            title.addProperty("underlined", underlined);
            return title;
        }

        /**
         * 创建分页对话的页面数组（支持文本和翻译键）
         */
        public static JsonArray createPages(List<Object> pages) {
            JsonArray pagesArray = new JsonArray();
            for (Object page : pages) {
                if (page instanceof String) {
                    // 如果是字符串，假设是翻译键
                    pagesArray.add(((String) page));
                } else if (page instanceof JsonObject) {
                    // 如果是JsonObject，直接添加
                    pagesArray.add((JsonObject) page);
                }
            }
            return pagesArray;
        }

        /**
         * 创建简单对话交互
         */
        public static JsonObject createSimpleDialogueInteraction(JsonObject title, Object message, 
                                                                boolean fireCloseEvent) {
            JsonObject dialogue = new JsonObject();
            dialogue.add("title", title);
            
            if (message instanceof String) {
                dialogue.addProperty("message", (String) message);
            } else if (message instanceof JsonObject) {
                dialogue.add("message", (JsonObject) message);
            }
            
            dialogue.addProperty("type", "pixelmon:open_dialogue");
            if (!fireCloseEvent) {
                dialogue.addProperty("fire_close_event", false);
            }
            return dialogue;
        }

        /**
         * 创建分页对话交互
         */
        public static JsonObject createPagedDialogueInteraction(JsonObject title, JsonArray pages) {
            JsonObject dialogue = new JsonObject();
            dialogue.add("title", title);
            dialogue.add("pages", pages);
            dialogue.addProperty("type", "pixelmon:open_paged_dialogue");
            return dialogue;
        }

        /**
         * 创建商店交互
         */
        public static JsonObject createShopInteraction(List<ShopItem> items) {
            JsonObject shop = new JsonObject();
            JsonArray itemsArray = new JsonArray();
            
            for (ShopItem item : items) {
                JsonObject shopItem = new JsonObject();
                JsonObject itemObj = new JsonObject();
                itemObj.addProperty("id", item.itemId);
                itemObj.addProperty("count", item.count);
                shopItem.add("item", itemObj);
                shopItem.addProperty("buyPrice", item.buyPrice);
                shopItem.addProperty("sellPrice", item.sellPrice);
                itemsArray.add(shopItem);
            }
            
            shop.add("items", itemsArray);
            shop.addProperty("type", "pixelmon:open_shop");
            return shop;
        }
    }

    /**
     * 商店物品配置类
     */
    public static class ShopItem {
        public final String itemId;
        public final int count;
        public final double buyPrice;
        public final double sellPrice;

        public ShopItem(String itemId, int count, double buyPrice, double sellPrice) {
            this.itemId = itemId;
            this.count = count;
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
        }

        public static ShopItem of(String itemId, int count, double buyPrice, double sellPrice) {
            return new ShopItem(itemId, count, buyPrice, sellPrice);
        }
    }

    /**
     * 交互构建工具类
     */
    public static class InteractionBuilder {
        
        /**
         * 创建完整的交互配置
         */
        public static JsonObject createInteraction(String event, JsonObject conditions, JsonObject results) {
            JsonObject interaction = new JsonObject();
            interaction.addProperty("event", event);
            interaction.add("conditions", conditions);
            interaction.add("results", results);
            return interaction;
        }

        /**
         * 创建始终为真的条件
         */
        public static JsonObject createTrueCondition() {
            JsonObject condition = new JsonObject();
            condition.addProperty("type", "pixelmon:true");
            return condition;
        }

        /**
         * 创建常量结果
         */
        public static JsonObject createConstantResults(List<JsonObject> values) {
            JsonObject results = new JsonObject();
            JsonArray valuesArray = new JsonArray();
            values.forEach(valuesArray::add);
            results.add("value", valuesArray);
            results.addProperty("type", "pixelmon:constant");
            return results;
        }

        /**
         * 创建单个交互值的交互配置（用于uniformly_random类型）
         */
        public static JsonObject createInteractionValue(List<JsonObject> interactions) {
            JsonObject interactionValue = new JsonObject();
            JsonArray interactionsArray = new JsonArray();
            interactions.forEach(interactionsArray::add);
            interactionValue.add("interactions", interactionsArray);
            return interactionValue;
        }
    }
}