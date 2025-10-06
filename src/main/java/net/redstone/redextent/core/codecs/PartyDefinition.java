/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
 
package net.redstone.redextent.core.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

/**
 * 队伍定义 Codec
 */
public record PartyDefinition(
    PartyValue value,
    String type
) {
    public static final Codec<PartyDefinition> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            PartyValue.CODEC.fieldOf("value").forGetter(PartyDefinition::value),
            Codec.STRING.fieldOf("type").forGetter(PartyDefinition::type)
        ).apply(instance, PartyDefinition::new)
    );

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PartyValue value;
        private String type = "pixelmon:constant";

        public Builder value(PartyValue value) {
            this.value = value;
            return this;
        }

        public Builder empty() {
            this.value = new PartyValue(Optional.empty(), "pixelmon:empty");
            return this;
        }

        public Builder specs(List<String> specs) {
            this.value = new PartyValue(Optional.of(specs), "pixelmon:spec");
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public PartyDefinition build() {
            return new PartyDefinition(value, type);
        }
    }

    public record PartyValue(
        Optional<List<String>> specs,
        String type
    ) {
        public static final Codec<PartyValue> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.listOf().optionalFieldOf("specs").forGetter(PartyValue::specs), // 修改这里
                Codec.STRING.fieldOf("type").forGetter(PartyValue::type)
            ).apply(instance, PartyValue::new)
        );
    }
}