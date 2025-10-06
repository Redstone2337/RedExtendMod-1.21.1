/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
 
package net.redstone.redextent.core.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

/**
 * 目标定义 Codec
 */
public record GoalDefinition(
    String type,
    GoalValue value
) {
    public static final Codec<GoalDefinition> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("type").forGetter(GoalDefinition::type),
            GoalValue.CODEC.fieldOf("value").forGetter(GoalDefinition::value)
        ).apply(instance, GoalDefinition::new)
    );

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String type = "pixelmon:constant";
        private GoalValue value;

        public Builder lookAtNearby(double lookDistance, double probability) {
            this.value = new GoalValue(List.of(
                new Goal(1, new GoalProvider("pixelmon:look_at_nearby", lookDistance, probability))
            ));
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder value(GoalValue value) {
            this.value = value;
            return this;
        }

        public GoalDefinition build() {
            return new GoalDefinition(type, value);
        }
    }

    public record GoalValue(List<Goal> goals) {
        public static final Codec<GoalValue> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Goal.CODEC.listOf().fieldOf("goals").forGetter(GoalValue::goals)
            ).apply(instance, GoalValue::new)
        );
    }

    public record Goal(
        int priority,
        GoalProvider provider
    ) {
        public static final Codec<Goal> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.INT.fieldOf("priority").forGetter(Goal::priority),
                GoalProvider.CODEC.fieldOf("provider").forGetter(Goal::provider)
            ).apply(instance, Goal::new)
        );
    }

    public record GoalProvider(
        String type,
        double lookDistance,
        double probability
    ) {
        public static final Codec<GoalProvider> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("type").forGetter(GoalProvider::type),
                Codec.DOUBLE.fieldOf("look_distance").forGetter(GoalProvider::lookDistance),
                Codec.DOUBLE.fieldOf("probability").forGetter(GoalProvider::probability)
            ).apply(instance, GoalProvider::new)
        );
    }
}