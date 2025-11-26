package net.redstone233.redextent.core.gui;

import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.redstone233.redextent.config.CommonConfig;
import net.redstone233.redextent.core.util.ConfigUtil;

public class RemQuickSettingsScreen extends AbstractSimiScreen {
    private final Screen parent;

    public RemQuickSettingsScreen(Screen parent) {
        super(Component.literal("REM 快速设置"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        setWindowSize(250, 180);
        super.init();

        int yPos = guiTop + 20;
        int fieldWidth = 120;

        // 清理时间快速设置
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 80, 20,
                Component.literal("清理时间:"), font));

        EditBox clearTimeField = new EditBox(font, guiLeft + 100, yPos, fieldWidth, 20, Component.literal(""));
        clearTimeField.setValue(String.valueOf(CommonConfig.getClearTime()));
        clearTimeField.setFilter(s -> s.matches("\\d*"));
        addRenderableWidget(clearTimeField);

        yPos += 30;

        // 快速预设按钮
        addRenderableWidget(Button.builder(Component.literal("1分钟"), $ -> setClearTime(1200))
                .bounds(guiLeft + 20, yPos, 60, 20).build());

        addRenderableWidget(Button.builder(Component.literal("5分钟"), $ -> setClearTime(6000))
                .bounds(guiLeft + 90, yPos, 60, 20).build());

        addRenderableWidget(Button.builder(Component.literal("10分钟"), $ -> setClearTime(12000))
                .bounds(guiLeft + 160, yPos, 60, 20).build());

        yPos += 30;

        // 功能快速切换
        addRenderableWidget(Button.builder(Component.literal("启用所有功能"), $ -> enableAllFeatures())
                .bounds(guiLeft + 20, yPos, 100, 20).build());

        addRenderableWidget(Button.builder(Component.literal("禁用所有功能"), $ -> disableAllFeatures())
                .bounds(guiLeft + 130, yPos, 100, 20).build());

        yPos += 40;

        // 保存和取消按钮
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {
            saveSettings(clearTimeField.getValue());
            onClose();
        }).bounds(guiLeft + 50, yPos, 80, 20).build());

        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> {
            onClose();
        }).bounds(guiLeft + 140, yPos, 80, 20).build());
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // 绘制背景
        graphics.fill(guiLeft, guiTop, guiLeft + windowWidth, guiTop + windowHeight, 0xAA000000);
        graphics.drawCenteredString(font, title, width / 2, guiTop - 15, 0xFFFFFF);
    }

    private void setClearTime(int ticks) {
        ConfigUtil.setClearTime(ticks);
    }

    private void enableAllFeatures() {
        ConfigUtil.setClearServerItem(true);
        ConfigUtil.setOnBrewingRecipe(true);
        ConfigUtil.setCustomAbility(true);
    }

    private void disableAllFeatures() {
        ConfigUtil.setClearServerItem(false);
        ConfigUtil.setOnBrewingRecipe(false);
        ConfigUtil.setCustomAbility(false);
    }

    private void saveSettings(String clearTime) {
        try {
            int time = Integer.parseInt(clearTime);
            if (time >= 180 && time <= 36000) {
                setClearTime(time);
            }
        } catch (NumberFormatException e) {
            // 忽略格式错误
        }
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.setScreen(parent);
        }
    }
}