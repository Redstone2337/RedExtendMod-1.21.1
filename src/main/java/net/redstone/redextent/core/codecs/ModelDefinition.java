/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
 
package net.redstone.redextent.core.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Optional;

/**
 * 模型定义 Codec
 */
public record ModelDefinition(
    List<ModelValue> values,
    String type
) {
    public static final Codec<ModelDefinition> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ModelValue.CODEC.listOf().fieldOf("values").forGetter(ModelDefinition::values),
            Codec.STRING.fieldOf("type").forGetter(ModelDefinition::type)
        ).apply(instance, ModelDefinition::new)
    );

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<ModelValue> values;
        private String type = "pixelmon:constant";

        public Builder values(List<ModelValue> values) {
            this.values = values;
            return this;
        }

        public Builder singleModel(boolean slim, ResourceLocation texture) {
            this.values = List.of(new ModelValue(slim, texture, "pixelmon:player"));
            this.type = "pixelmon:constant";
            return this;
        }

        public Builder singleModel(ResourceLocation texture) {
            return singleModel(false, texture);
        }

        public Builder randomModels(List<ModelValue> modelValues) {
            this.values = modelValues;
            this.type = "pixelmon:uniformly_random";
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public ModelDefinition build() {
            return new ModelDefinition(values, type);
        }
    }

    public record ModelValue(
        boolean slim,
        ResourceLocation texture,
        String type
    ) {
        public static final Codec<ModelValue> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.BOOL.fieldOf("slim").forGetter(ModelValue::slim),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(ModelValue::texture),
                Codec.STRING.fieldOf("type").forGetter(ModelValue::type)
            ).apply(instance, ModelValue::new)
        );

        public static ModelValue of(ResourceLocation texture) {
            return new ModelValue(false, texture, "pixelmon:player");
        }

        public static ModelValue of(String texture) {
            return of(ResourceLocation.parse(texture));
        }
    }
}