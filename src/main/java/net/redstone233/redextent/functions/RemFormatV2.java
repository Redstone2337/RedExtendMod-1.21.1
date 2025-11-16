// RemFormatV2.java
package net.redstone233.redextent.functions;

import net.minecraft.network.chat.Component;

public record RemFormatV2(Component description, int minFormat, int maxFormat) {
    public boolean supports(int format) {
        return format >= minFormat && format <= maxFormat;
    }
}