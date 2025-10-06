/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
 
package net.redstone.redextent.core.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.redstone.redextent.core.codecs.*;


import java.util.List;
import java.util.Optional;

/**
 * 完整的 NPC 定义 Codec
 */
public record NpcDefinitionCodec(
    NpcProperties properties,
    NameDefinition names,
    PartyDefinition party,
    ModelDefinition models,
    GoalDefinition goals,
    Optional<InteractionDefinition> interactions
) {
    public static final Codec<NpcDefinitionCodec> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            NpcProperties.CODEC.fieldOf("properties").forGetter(NpcDefinitionCodec::properties),
            NameDefinition.CODEC.fieldOf("names").forGetter(NpcDefinitionCodec::names),
            PartyDefinition.CODEC.fieldOf("party").forGetter(NpcDefinitionCodec::party),
            ModelDefinition.CODEC.fieldOf("models").forGetter(NpcDefinitionCodec::models),
            GoalDefinition.CODEC.fieldOf("goals").forGetter(NpcDefinitionCodec::goals),
            InteractionDefinition.CODEC.optionalFieldOf("interactions").forGetter(NpcDefinitionCodec::interactions)
        ).apply(instance, NpcDefinitionCodec::new)
    );

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private NpcProperties properties;
        private NameDefinition names;
        private PartyDefinition party;
        private ModelDefinition models;
        private GoalDefinition goals;
        private InteractionDefinition interactions;

        public Builder properties(NpcProperties properties) {
            this.properties = properties;
            return this;
        }

        public Builder names(NameDefinition names) {
            this.names = names;
            return this;
        }

        public Builder party(PartyDefinition party) {
            this.party = party;
            return this;
        }

        public Builder models(ModelDefinition models) {
            this.models = models;
            return this;
        }

        public Builder goals(GoalDefinition goals) {
            this.goals = goals;
            return this;
        }

        public Builder interactions(InteractionDefinition interactions) {
            this.interactions = interactions;
            return this;
        }

        public NpcDefinitionCodec build() {
            return new NpcDefinitionCodec(
                properties,
                names,
                party,
                models,
                goals,
                Optional.ofNullable(interactions)
            );
        }
    }
}