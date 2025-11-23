package net.redstone233.redextent.core.gui;

import net.minecraft.client.gui.screens.Screen;
import net.redstone233.redextent.Config;
import net.redstone233.redextent.core.util.ConfigUtil;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;

public class RemDetailedConfigScreen extends AbstractSimiScreen {
    private final Screen parent;
    private EditBox clearTimeField;
    private EditBox displayTextHeadField;
    private EditBox displayTextBodyField;
    private EditBox itemWhitelistField;
    private EditBox customAbilityWhitelistField;
    private EditBox ghostPixelmonsField;

    public RemDetailedConfigScreen(Screen parent) {
        super(Component.literal("REM 详细配置"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        setWindowSize(300, 280);
        super.init();

        int yPos = guiTop + 20;

        // 清理时间设置
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 100, 20,
                Component.literal("清理时间 (ticks):"), font));

        clearTimeField = new EditBox(font, guiLeft + 120, yPos, 60, 20, Component.literal(""));
        clearTimeField.setValue(String.valueOf(Config.getClearTime()));
        clearTimeField.setFilter(s -> s.matches("\\d*"));
        addRenderableWidget(clearTimeField);

        yPos += 25;

        // 显示文本头设置
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 100, 20,
                Component.literal("显示文本头:"), font));

        displayTextHeadField = new EditBox(font, guiLeft + 120, yPos, 140, 20, Component.literal(""));
        displayTextHeadField.setValue(Config.getDisplayTextHead());
        addRenderableWidget(displayTextHeadField);

        yPos += 25;

        // 显示文本体设置
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 100, 20,
                Component.literal("显示文本体:"), font));

        displayTextBodyField = new EditBox(font, guiLeft + 120, yPos, 140, 20, Component.literal(""));
        displayTextBodyField.setValue(Config.getDisplayTextBody());
        addRenderableWidget(displayTextBodyField);

        yPos += 25;

        // 物品白名单设置
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 100, 20,
                Component.literal("物品白名单:"), font));

        itemWhitelistField = new EditBox(font, guiLeft + 120, yPos, 140, 20, Component.literal(""));
        itemWhitelistField.setValue(String.join(", ", Config.getItemWhitelist()));
        addRenderableWidget(itemWhitelistField);

        yPos += 25;

        // 自定义特性白名单设置
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 100, 20,
                Component.literal("特性白名单:"), font));

        customAbilityWhitelistField = new EditBox(font, guiLeft + 120, yPos, 140, 20, Component.literal(""));
        customAbilityWhitelistField.setValue(String.join(", ", Config.getCustomAbilityWhitelist()));
        addRenderableWidget(customAbilityWhitelistField);

        yPos += 25;

        // 幽灵宝可梦设置
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 100, 20,
                Component.literal("幽灵宝可梦:"), font));

        ghostPixelmonsField = new EditBox(font, guiLeft + 120, yPos, 140, 20, Component.literal(""));
        ghostPixelmonsField.setValue(String.join(", ", Config.getOnGhostPixelmons()));
        addRenderableWidget(ghostPixelmonsField);

        yPos += 40;

        // 保存和取消按钮
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {
            saveConfig();
            onClose();
        }).bounds(guiLeft + 50, yPos, 80, 20).build());

        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> {
            onClose();
        }).bounds(guiLeft + 150, yPos, 80, 20).build());
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // 绘制背景
        graphics.fill(guiLeft, guiTop, guiLeft + windowWidth, guiTop + windowHeight, 0xAA000000);
        graphics.drawCenteredString(font, title, width / 2, guiTop - 15, 0xFFFFFF);

        // 绘制说明文本
        graphics.drawString(font, "使用逗号分隔多个项目", guiLeft + 10, guiTop + windowHeight - 30, 0xAAAAAA, false);
    }

    private void saveConfig() {
        try {
            // 保存清理时间
            int clearTime = Integer.parseInt(clearTimeField.getValue());
            if (clearTime >= 180 && clearTime <= 36000) {
                ConfigUtil.setClearTime(clearTime);
            }

            // 保存文本设置
            ConfigUtil.setDisplayTextHead(displayTextHeadField.getValue());
            ConfigUtil.setDisplayTextBody(displayTextBodyField.getValue());

            // 保存列表设置
            ConfigUtil.setItemWhitelist(parseList(itemWhitelistField.getValue()));
            ConfigUtil.setCustomAbilityWhitelist(parseList(customAbilityWhitelistField.getValue()));
            ConfigUtil.setOnGhostPokemons(parseList(ghostPixelmonsField.getValue()));

        } catch (NumberFormatException e) {
            // 处理数字格式错误
        }
    }

    private List<String> parseList(String input) {
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.setScreen(parent);
        }
    }
}