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
 * NPC 名称定义 Codec
 */
public record NameDefinition(
    List<String> values,
    String type
) {
    public static final Codec<NameDefinition> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ExtraCodecs.STRING.listOf().fieldOf("values").forGetter(NameDefinition::values),
            Codec.STRING.fieldOf("type").forGetter(NameDefinition::type)
        ).apply(instance, NameDefinition::new)
    );

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> values;
        private String type = "pixelmon:uniformly_random";

        public Builder values(List<String> values) {
            this.values = values;
            return this;
        }

        public Builder values(String... values) {
            this.values = List.of(values);
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

        public NameDefinition build() {
            return new NameDefinition(values, type);
        }
    }
}