// RemConfig.java
package net.redstone233.redextent.core.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public record RemConfig(Component description, int minFormat, int maxFormat) {
    public boolean supports(int format) {
        return format >= minFormat && format <= maxFormat;
    }

    public Component getIncompatibilityMessage(int currentFormat) {
        return Component.literal(
                String.format("数据包不兼容! 当前格式版本: %d, 支持范围: [%d, %d]",
                        currentFormat, minFormat, maxFormat)
        ).withStyle(ChatFormatting.RED);
    }

    public Component getSuccessMessage(String packId) {
        return Component.literal("[" + packId + "] ").append(description).withStyle(ChatFormatting.GREEN);
    }

    public Component getMissingRemMessage(String packId) {
        return Component.literal("数据包 " + packId + " 缺少必要的 'rem' 配置，跳过加载酿造台配方")
                .withStyle(ChatFormatting.YELLOW);
    }

    public Component getInvalidRemFormatMessage(String packId, int remFormat) {
        return Component.literal(
                String.format("数据包 %s 使用了不支持的 rem_format: %d (只支持 rem_format: 8)", packId, remFormat)
        ).withStyle(ChatFormatting.RED);
    }

    public String getDescriptionAsString() {
        return description.getString();
    }
}