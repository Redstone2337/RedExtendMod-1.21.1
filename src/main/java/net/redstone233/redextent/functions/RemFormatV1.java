// RemFormatV1.java
package net.redstone233.redextent.functions;

import net.minecraft.network.chat.Component;
import net.redstone233.redextent.functions.SupportedFormatsData;

public record RemFormatV1(int remFormat, Component description, SupportedFormatsData supportedFormats) {
    // remFormat 必须为 8，表示 1.21.1 语法版本
}