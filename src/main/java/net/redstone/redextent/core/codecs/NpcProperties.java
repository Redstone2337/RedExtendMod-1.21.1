/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
 
package net.redstone.redextent.core.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

/**
 * NPC 属性定义 Codec
 */
public record NpcProperties(
    double health,
    double eyeHeight,
    Dimensions dimensions,
    Title title,
    boolean pushable,
    boolean child,
    boolean invulnerable,
    boolean immovable,
    boolean nameplate
) {
    public static final Codec<NpcProperties> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.DOUBLE.fieldOf("health").forGetter(NpcProperties::health),
            Codec.DOUBLE.fieldOf("eyeHeight").forGetter(NpcProperties::eyeHeight),
            Dimensions.CODEC.fieldOf("dimensions").forGetter(NpcProperties::dimensions),
            Title.CODEC.fieldOf("title").forGetter(NpcProperties::title),
            Codec.BOOL.fieldOf("pushable").forGetter(NpcProperties::pushable),
            Codec.BOOL.fieldOf("child").forGetter(NpcProperties::child),
            Codec.BOOL.fieldOf("invulnerable").forGetter(NpcProperties::invulnerable),
            Codec.BOOL.fieldOf("immovable").forGetter(NpcProperties::immovable),
            Codec.BOOL.fieldOf("nameplate").forGetter(NpcProperties::nameplate)
        ).apply(instance, NpcProperties::new)
    );

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private double health = 20.0;
        private double eyeHeight = 1.9;
        private Dimensions dimensions = new Dimensions(0.65, 2.0);
        private Title title;
        private boolean pushable = false;
        private boolean child = false;
        private boolean invulnerable = false;
        private boolean immovable = false;
        private boolean nameplate = true;

        public Builder health(double health) {
            this.health = health;
            return this;
        }

        public Builder eyeHeight(double eyeHeight) {
            this.eyeHeight = eyeHeight;
            return this;
        }

        public Builder dimensions(double width, double height) {
            this.dimensions = new Dimensions(width, height);
            return this;
        }

        public Builder title(Title title) {
            this.title = title;
            return this;
        }

        public Builder pushable(boolean pushable) {
            this.pushable = pushable;
            return this;
        }

        public Builder child(boolean child) {
            this.child = child;
            return this;
        }

        public Builder invulnerable(boolean invulnerable) {
            this.invulnerable = invulnerable;
            return this;
        }

        public Builder immovable(boolean immovable) {
            this.immovable = immovable;
            return this;
        }

        public Builder nameplate(boolean nameplate) {
            this.nameplate = nameplate;
            return this;
        }

        public NpcProperties build() {
            return new NpcProperties(health, eyeHeight, dimensions, title, pushable, child, invulnerable, immovable, nameplate);
        }
    }

    public record Dimensions(double width, double height) {
        public static final Codec<Dimensions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.DOUBLE.fieldOf("width").forGetter(Dimensions::width),
                Codec.DOUBLE.fieldOf("height").forGetter(Dimensions::height)
            ).apply(instance, Dimensions::new)
        );
    }

    public record Title(
        String translate,
        String color,
        boolean bold,
        boolean italic,
        boolean underlined
    ) {
        public static final Codec<Title> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("translate").forGetter(Title::translate),
                Codec.STRING.fieldOf("color").forGetter(Title::color),
                Codec.BOOL.fieldOf("bold").forGetter(Title::bold),
                Codec.BOOL.fieldOf("italic").forGetter(Title::italic),
                Codec.BOOL.fieldOf("underlined").forGetter(Title::underlined)
            ).apply(instance, Title::new)
        );

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String translate;
            private String color = "#FFFFFF";
            private boolean bold = false;
            private boolean italic = false;
            private boolean underlined = false;

            public Builder translate(String translate) {
                this.translate = translate;
                return this;
            }

            public Builder color(String color) {
                this.color = color;
                return this;
            }

            public Builder bold(boolean bold) {
                this.bold = bold;
                return this;
            }

            public Builder italic(boolean italic) {
                this.italic = italic;
                return this;
            }

            public Builder underlined(boolean underlined) {
                this.underlined = underlined;
                return this;
            }

            public Title build() {
                return new Title(translate, color, bold, italic, underlined);
            }
        }
    }
}