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
         * 使用字符串标题的属性配置（宝可梦NPC兼容）
         */
        public Builder withStringTitleProperties(float health, float eyeHeight, float width, float height,
                                                 String title, boolean pushable, boolean child,
                                                 boolean invulnerable, boolean immovable, boolean nameplate) {
            JsonObject properties = new JsonObject();
            JsonObject value = new JsonObject();

            value.addProperty("health", health);
            value.addProperty("eyeHeight", eyeHeight);

            JsonObject dimensions = new JsonObject();
            dimensions.addProperty("width", width);
            dimensions.addProperty("height", height);
            value.add("dimensions", dimensions);

            value.addProperty("title", title); // 使用纯字符串而不是JsonObject

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

                JsonObject texture = new JsonObject();
                JsonObject resource = new JsonObject();
                resource.addProperty("resource", model.textureResource);
                resource.addProperty("fallback", model.textureFallback);
                texture.add("resource", resource);
                texture.addProperty("type", "pixelmon:fallback");

                modelObj.add("texture", texture);
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

            JsonObject texture = new JsonObject();
            JsonObject resource = new JsonObject();
            resource.addProperty("resource", textureResource);
            resource.addProperty("fallback", textureFallback);
            texture.add("resource", resource);
            texture.addProperty("type", "pixelmon:fallback");

            value.add("texture", texture);

            models.add("value", value);
            models.addProperty("type", "pixelmon:constant");
            definition.models = models;
            return this;
        }

        public Builder withSinglePlayerModel(boolean slim, String textureResource) {
            return withSinglePlayerModel(slim, textureResource, textureResource);
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
    }
}