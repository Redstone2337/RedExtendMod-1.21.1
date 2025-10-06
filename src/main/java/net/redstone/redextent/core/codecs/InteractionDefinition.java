/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
 
package net.redstone.redextent.core.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

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
        Object conditions,
        InteractionResults results
    ) {
        public static final Codec<Interaction> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("event").forGetter(Interaction::event),
                ExtraCodecs.JSON.fieldOf("conditions").forGetter(Interaction::conditions),
                InteractionResults.CODEC.fieldOf("results").forGetter(Interaction::results)
            ).apply(instance, Interaction::new)
        );

        public static Interaction of(String event, Object conditions, InteractionResults results) {
            return new Interaction(event, conditions, results);
        }
    }

    public record InteractionResults(
        String type,
        List<Object> value
    ) {
        public static final Codec<InteractionResults> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("type").forGetter(InteractionResults::type),
                ExtraCodecs.JSON.listOf().fieldOf("value").forGetter(InteractionResults::value)
            ).apply(instance, InteractionResults::new)
        );

        public static InteractionResults constant(List<Object> value) {
            return new InteractionResults("pixelmon:constant", value);
        }
    }
}