/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
 
package net.redstone.redextent.core.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * 文本内容 Codec - 支持纯文本和翻译键
 */
public class TextCodec {
    public static final Codec<TextContent> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.optionalFieldOf("text", "").forGetter(TextContent::text),
            Codec.STRING.optionalFieldOf("translate", "").forGetter(TextContent::translate)
        ).apply(instance, TextContent::new)
    );

    public record TextContent(String text, String translate) {
        public static TextContent literal(String text) {
            return new TextContent(text, "");
        }
        
        public static TextContent translated(String translateKey) {
            return new TextContent("", translateKey);
        }
    }
}