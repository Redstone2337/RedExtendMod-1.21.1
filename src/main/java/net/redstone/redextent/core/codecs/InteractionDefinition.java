/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
 
package net.redstone.redextent.core.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.util.List;
import java.util.Map;

/**
 * 交互定义 Codec
 */
public record InteractionDefinition(
    List<InteractionSet> values,
    String type
) {
    public static final Codec<InteractionDefinition> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            InteractionSet.CODEC.listOf().fieldOf("values").forGetter(InteractionDefinition::values),
            Codec.STRING.fieldOf("type").forGetter(InteractionDefinition::type)
        ).apply(instance, InteractionDefinition::new)
    );

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<InteractionSet> values;
        private String type = "pixelmon:uniformly_random";

        public Builder interactions(List<InteractionSet> interactions) {
            this.values = interactions;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder constant() {
            this.type = "pixelmon:constant";
            return this;
        }

        public Builder random() {
            this.type = "pixelmon:uniformly_random";
            return this;
        }

        public InteractionDefinition build() {
            return new InteractionDefinition(values, type);
        }
    }

    public record InteractionSet(List<Interaction> interactions) {
        public static final Codec<InteractionSet> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Interaction.CODEC.listOf().fieldOf("interactions").forGetter(InteractionSet::interactions)
            ).apply(instance, InteractionSet::new)
        );

        public static InteractionSet of(Interaction... interactions) {
            return new InteractionSet(List.of(interactions));
        }
    }

    public record Interaction(
        String event,
        JsonElement conditions,
        InteractionResults results
    ) {
        public static final Codec<Interaction> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("event").forGetter(Interaction::event),
                ExtraCodecs.JSON.fieldOf("conditions").forGetter(Interaction::conditions),
                InteractionResults.CODEC.fieldOf("results").forGetter(Interaction::results)
            ).apply(instance, Interaction::new)
        );

        public static Interaction of(String event, JsonElement conditions, InteractionResults results) {
            return new Interaction(event, conditions, results);
        }
    }

    public record InteractionResults(
        String type,
        List<JsonElement> value
    ) {
        public static final Codec<InteractionResults> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("type").forGetter(InteractionResults::type),
                ExtraCodecs.JSON.listOf().fieldOf("value").forGetter(InteractionResults::value)
            ).apply(instance, InteractionResults::new)
        );

        public static InteractionResults constant(List<JsonElement> value) {
            return new InteractionResults("pixelmon:constant", value);
        }
    }

    // 工具方法：将Map转换为JsonElement
    public static JsonElement toJsonElement(Object obj) {
        if (obj instanceof Map) {
            JsonObject jsonObject = new JsonObject();
            ((Map<?, ?>) obj).forEach((key, value) -> {
                if (key instanceof String) {
                    if (value instanceof String) {
                        jsonObject.addProperty((String) key, (String) value);
                    } else if (value instanceof Number) {
                        jsonObject.addProperty((String) key, (Number) value);
                    } else if (value instanceof Boolean) {
                        jsonObject.addProperty((String) key, (Boolean) value);
                    } else if (value instanceof Map) {
                        jsonObject.add((String) key, toJsonElement(value));
                    } else if (value instanceof List) {
                        JsonArray array = new JsonArray();
                        ((List<?>) value).forEach(item -> array.add(toJsonElement(item)));
                        jsonObject.add((String) key, array);
                    }
                }
            });
            return jsonObject;
        } else if (obj instanceof List) {
            JsonArray array = new JsonArray();
            ((List<?>) obj).forEach(item -> array.add(toJsonElement(item)));
            return array;
        }
        return new JsonObject();
    }
}