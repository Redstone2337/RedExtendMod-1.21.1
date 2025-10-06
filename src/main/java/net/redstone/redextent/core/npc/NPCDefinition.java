/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.redstone.redextent.core.npc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 用于以类型安全的方式创建宝可梦NPC定义的构建器类，使用CODEC序列化。
 */
public class NPCDefinition {
    private final Optional<JsonObject> properties;
    private final Optional<JsonObject> names;
    private final Optional<JsonObject> party;
    private final Optional<JsonObject> models;
    private final Optional<JsonObject> goals;
    private final Optional<JsonObject> interactions;

    private NPCDefinition(Optional<JsonObject> properties, Optional<JsonObject> names,
                        Optional<JsonObject> party, Optional<JsonObject> models,
                        Optional<JsonObject> goals, Optional<JsonObject> interactions) {
        this.properties = properties;
        this.names = names;
        this.party = party;
        this.models = models;
        this.goals = goals;
        this.interactions = interactions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public JsonObject serialize() {
        JsonObject root = new JsonObject();

        properties.ifPresent(props -> root.add("properties", props));
        names.ifPresent(n -> root.add("names", n));
        party.ifPresent(p -> root.add("party", p));
        models.ifPresent(m -> root.add("models", m));
        goals.ifPresent(g -> root.add("goals", g));
        interactions.ifPresent(i -> root.add("interactions", i));

        return root;
    }

    // CODEC 定义
    public static final Codec<NPCDefinition> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.optionalField("properties", JsonObject.CODEC).forGetter(NPCDefinition::getProperties),
            Codec.optionalField("names", JsonObject.CODEC).forGetter(NPCDefinition::getNames),
            Codec.optionalField("party", JsonObject.CODEC).forGetter(NPCDefinition::getParty),
            Codec.optionalField("models", JsonObject.CODEC).forGetter(NPCDefinition::getModels),
            Codec.optionalField("goals", JsonObject.CODEC).forGetter(NPCDefinition::getGoals),
            Codec.optionalField("interactions", JsonObject.CODEC).forGetter(NPCDefinition::getInteractions)
        ).apply(instance, NPCDefinition::new)
    );

    public Optional<JsonObject> getProperties() { return properties; }
    public Optional<JsonObject> getNames() { return names; }
    public Optional<JsonObject> getParty() { return party; }
    public Optional<JsonObject> getModels() { return models; }
    public Optional<JsonObject> getGoals() { return goals; }
    public Optional<JsonObject> getInteractions() { return interactions; }

    /**
     * 从JSON对象创建NPCDefinition实例
     */
    public static NPCDefinition fromJson(JsonObject json) {
        DataResult<NPCDefinition> result = CODEC.parse(JsonOps.INSTANCE, json);
        return result.result().orElseThrow(() -> new IllegalArgumentException("Invalid NPC JSON: " + result.error().orElse(null)));
    }

    /**
     * 转换为JSON对象
     */
    public JsonObject toJson() {
        DataResult<JsonObject> result = CODEC.encodeStart(JsonOps.INSTANCE, this);
        return result.result().orElseGet(JsonObject::new);
    }

    public static class Builder {
        private JsonObject properties;
        private JsonObject names;
        private JsonObject party;
        private JsonObject models;
        private JsonObject goals;
        private JsonObject interactions;

        private Builder() {}

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

            value.add("title", title);

            value.addProperty("pushable", pushable);
            value.addProperty("child", child);
            value.addProperty("invulnerable", invulnerable);
            value.addProperty("immovable", immovable);
            value.addProperty("nameplate", nameplate);

            properties.add("value", value);
            properties.addProperty("type", "pixelmon:constant");
            this.properties = properties;
            return this;
        }

        // 交互配置
        public Builder withInteractions(JsonObject interactions) {
            this.interactions = interactions;
            return this;
        }

        public Builder withUniformInteractions(List<JsonObject> interactionValues) {
            JsonObject interactions = new JsonObject();
            JsonArray valuesArray = new JsonArray();
            interactionValues.forEach(valuesArray::add);
            interactions.add("values", valuesArray);
            interactions.addProperty("type", "pixelmon:uniformly_random");
            this.interactions = interactions;
            return this;
        }

        public Builder withConstantInteractions(JsonObject interactionValue) {
            JsonObject interactions = new JsonObject();
            interactions.add("value", interactionValue);
            interactions.addProperty("type", "pixelmon:constant");
            this.interactions = interactions;
            return this;
        }

        // 属性配置
        public Builder withProperties(JsonObject properties) {
            this.properties = properties;
            return this;
        }

        /**
         * @deprecated 使用 {@link #withTitleProperties(float, float, float, float, JsonObject, boolean, boolean, boolean, boolean, boolean)} 替代
         */
        @Deprecated
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
            this.properties = properties;
            return this;
        }

        /**
         * @deprecated 使用 {@link #withTitleProperties(float, float, float, float, JsonObject, boolean, boolean, boolean, boolean, boolean)} 替代
         */
        @Deprecated
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

            return withTitleProperties(health, eyeHeight, width, height, title, pushable, child, invulnerable, immovable, nameplate);
        }

        // 队伍配置
        public Builder withParty(JsonObject party) {
            this.party = party;
            return this;
        }

        public Builder withEmptyParty() {
            JsonObject party = new JsonObject();
            JsonObject value = new JsonObject();
            value.addProperty("type", "pixelmon:empty");
            party.add("value", value);
            party.addProperty("type", "pixelmon:constant");
            this.party = party;
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
            this.party = party;
            return this;
        }

        // 名称配置
        public Builder withNames(JsonObject names) {
            this.names = names;
            return this;
        }

        public Builder withRandomNames(List<String> nameList) {
            JsonObject names = new JsonObject();
            JsonArray valuesArray = new JsonArray();
            nameList.forEach(valuesArray::add);
            names.add("values", valuesArray);
            names.addProperty("type", "pixelmon:uniformly_random");
            this.names = names;
            return this;
        }

        public Builder withSingleName(String name) {
            JsonObject names = new JsonObject();
            JsonArray valuesArray = new JsonArray();
            valuesArray.add(name);
            names.add("values", valuesArray);
            names.addProperty("type", "pixelmon:uniformly_random");
            this.names = names;
            return this;
        }

        // 模型配置
        public Builder withModels(JsonObject models) {
            this.models = models;
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
            this.models = models;
            return this;
        }

        /**
         * @deprecated 使用 {@link #withSinglePlayerModel(boolean, String, String)} 替代
         */
        @Deprecated
        public Builder withSinglePlayerModel(boolean slim, String textureResource) {
            return withSinglePlayerModel(slim, textureResource, textureResource);
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
            this.models = models;
            return this;
        }

        // 行为目标配置
        public Builder withGoals(JsonObject goals) {
            this.goals = goals;
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
            this.goals = goals;
            return this;
        }

        public NPCDefinition build() {
            return new NPCDefinition(
                Optional.ofNullable(properties),
                Optional.ofNullable(names),
                Optional.ofNullable(party),
                Optional.ofNullable(models),
                Optional.ofNullable(goals),
                Optional.ofNullable(interactions)
            );
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

        // CODEC for PlayerModel
        public static final Codec<PlayerModel> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.BOOL.fieldOf("slim").forGetter(m -> m.slim),
                Codec.STRING.fieldOf("textureResource").forGetter(m -> m.textureResource),
                Codec.STRING.fieldOf("textureFallback").forGetter(m -> m.textureFallback)
            ).apply(instance, PlayerModel::new)
        );
    }
}