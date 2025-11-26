/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.redstone233.redextent.core.npc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 用于以类型安全的方式创建宝可梦NPC定义的构建器类。
 */
public class NPCDefinition {
    private JsonObject properties;
    private JsonObject names;
    private JsonObject party;
    private JsonObject models;
    private JsonObject aiProvider;
    private JsonObject interactions;
    private NPCType type;

    private NPCDefinition() {}

    public static Builder builder() {
        return new Builder();
    }

    public JsonObject serialize() {
        JsonObject root = new JsonObject();

        // 根据类型确定字段顺序
        switch (type) {
            case GYM_LEADER:
                // 道馆馆主顺序：properties, names, party, models, ai_provider, interactions
                if (properties != null) root.add("properties", properties);
                if (names != null) root.add("names", names);
                if (party != null) root.add("party", party);
                if (models != null) root.add("models", models);
                if (aiProvider != null) root.add("ai_provider", aiProvider);
                if (interactions != null) root.add("interactions", interactions);
                break;

            case SHOP:
                // 商店NPC顺序：interactions, properties, party, names, models, ai_provider
                if (interactions != null) root.add("interactions", interactions);
                if (properties != null) root.add("properties", properties);
                if (party != null) root.add("party", party);
                if (names != null) root.add("names", names);
                if (models != null) root.add("models", models);
                if (aiProvider != null) root.add("ai_provider", aiProvider);
                break;

            case CHAT:
                // 聊天NPC顺序：interactions, properties, party, names, models, ai_provider
                if (interactions != null) root.add("interactions", interactions);
                if (properties != null) root.add("properties", properties);
                if (party != null) root.add("party", party);
                if (names != null) root.add("names", names);
                if (models != null) root.add("models", models);
                if (aiProvider != null) root.add("ai_provider", aiProvider);
                break;

            default:
                // 默认顺序
                if (properties != null) root.add("properties", properties);
                if (names != null) root.add("names", names);
                if (party != null) root.add("party", party);
                if (models != null) root.add("models", models);
                if (aiProvider != null) root.add("ai_provider", aiProvider);
                if (interactions != null) root.add("interactions", interactions);
                break;
        }

        return root;
    }

    /**
     * NPC类型枚举
     */
    public enum NPCType {
        /**
         * 道馆馆主类型（战斗NPC）
         */
        GYM_LEADER,

        /**
         * 商店NPC类型
         */
        SHOP,

        /**
         * 聊天NPC类型
         */
        CHAT,

        /**
         * 默认类型
         */
        DEFAULT
    }

    /**
     * JSON保存模式枚举
     */
    public enum SaveMode {
        /**
         * 使用原版保存方式（可能对键排序）
         */
        ORIGINAL,

        /**
         * 使用自定义保存方式（保持键的插入顺序，保留格式）
         */
        UNORDERED_FORMATTED,

        /**
         * 使用紧凑保存方式（无空格和换行）
         */
        COMPACT
    }

    public static class Builder {
        private final NPCDefinition definition;

        private Builder() {
            this.definition = new NPCDefinition();
        }

        /**
         * 设置NPC类型
         */
        public Builder withType(NPCType type) {
            definition.type = type;
            return this;
        }

        /**
         * 使用翻译标题对象的属性配置（新版本格式）
         */
        public Builder withTitleProperties(float health, float eyeHeight, float width, float height,
                                           JsonObject title, boolean pushable, boolean child,
                                           boolean invulnerable, boolean immovable, boolean nameplate) {
            JsonObject properties = new JsonObject();
            JsonObject value = new JsonObject();

            // 严格按照 dragon_1.json 中 properties.value 的顺序
            value.addProperty("health", health);
            value.addProperty("eyeHeight", eyeHeight);

            JsonObject dimensions = new JsonObject();
            dimensions.addProperty("width", width);
            dimensions.addProperty("height", height);
            value.add("dimensions", dimensions);

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
         * 使用基础属性配置（无标题）
         */
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

        /**
         * 使用完整标题属性的属性配置（新版本格式）
         */
        public Builder withTitledProperties(float health, float eyeHeight, float width, float height,
                                            String titleTranslate, String color, boolean bold,
                                            boolean italic, boolean underlined, boolean pushable,
                                            boolean child, boolean invulnerable, boolean immovable,
                                            boolean nameplate) {
            JsonObject title = new JsonObject();
            title.addProperty("translate", titleTranslate);
            title.addProperty("color", color);
            title.addProperty("bold", bold);
            title.addProperty("italic", italic);
            title.addProperty("underlined", underlined);

            return withTitleProperties(health, eyeHeight, width, height, title, pushable, child,
                    invulnerable, immovable, nameplate);
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
            // 严格按照 dragon_1.json 中 interactions 的结构
            interactions.add("value", interactionValue);
            interactions.addProperty("type", "pixelmon:constant");
            definition.interactions = interactions;
            return this;
        }

        // 属性配置（直接设置）
        public Builder withProperties(JsonObject properties) {
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

                if (model.textureResource != null) {
                    // 直接纹理路径，不使用fallback结构以匹配 dragon_1.json
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

            // 直接纹理路径，不使用fallback结构以匹配 dragon_1.json
            value.addProperty("texture", textureResource);

            models.add("value", value);
            models.addProperty("type", "pixelmon:constant");
            definition.models = models;
            return this;
        }

        public Builder withSinglePlayerModel(boolean slim, String textureResource) {
            return withSinglePlayerModel(slim, textureResource, textureResource);
        }

        // AI提供者配置
        public Builder withAIProvider(JsonObject aiProvider) {
            definition.aiProvider = aiProvider;
            return this;
        }

        public Builder withStandAndLookAI(float lookDistance, boolean swim) {
            JsonObject aiProvider = new JsonObject();
            JsonObject value = new JsonObject();

            // 严格按照 dragon_1.json 中 ai_provider.value 的顺序
            value.addProperty("type", "pixelmon:stand_and_look");
            value.addProperty("look_distance", lookDistance);
            value.addProperty("swim", swim);

            aiProvider.add("value", value);
            aiProvider.addProperty("type", "pixelmon:constant");
            definition.aiProvider = aiProvider;
            return this;
        }

        public Builder withStandardNPC(String profession) {
            JsonObject aiProvider = new JsonObject();
            JsonObject value = new JsonObject();

            value.addProperty("type", "pixelmon:standard_npc");
            value.addProperty("profession", profession);

            aiProvider.add("value", value);
            aiProvider.addProperty("type", "pixelmon:constant");
            definition.aiProvider = aiProvider;
            return this;
        }

        public Builder withWanderAndLookAI(float lookDistance, float speed, boolean swim) {
            JsonObject aiProvider = new JsonObject();
            JsonObject value = new JsonObject();

            value.addProperty("type", "pixelmon:wander_and_look");
            value.addProperty("look_distance", lookDistance);
            value.addProperty("speed", speed);
            value.addProperty("swim", swim);

            aiProvider.add("value", value);
            aiProvider.addProperty("type", "pixelmon:constant");
            definition.aiProvider = aiProvider;
            return this;
        }

        public NPCDefinition build() {
            return definition;
        }
    }

    /**
     * 玩家模型配置类
     */
    public record PlayerModel(boolean slim, String textureResource, String textureFallback) {

        /**
         * 创建玩家模型实例
         *
         * @param slim            是否为瘦手臂模型
         * @param textureResource 纹理资源路径
         * @return 玩家模型实例
         */
        public static PlayerModel of(boolean slim, String textureResource) {
            return new PlayerModel(slim, textureResource, textureResource);
        }

        /**
         * 创建玩家模型实例（带后备纹理）
         */
        public static PlayerModel of(boolean slim, String textureResource, String textureFallback) {
            return new PlayerModel(slim, textureResource, textureFallback);
        }
    }
}