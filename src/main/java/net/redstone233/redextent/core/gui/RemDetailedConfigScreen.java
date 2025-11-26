package net.redstone233.redextent.core.gui;

import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.redstone233.redextent.config.ClientConfig;
import net.redstone233.redextent.config.CommonConfig;
import net.redstone233.redextent.core.util.ConfigUtil;
import net.redstone233.redextent.core.gui.widget.ListStringInput;

import java.util.ArrayList;
import java.util.List;

public class RemDetailedConfigScreen extends AbstractSimiScreen {
    private final Screen parent;

    // 通用设置
    private AbstractWidget isClearServerItemCheckbox;
    private AbstractWidget isItemFilterCheckbox;
    private AbstractWidget isDebugModeCheckbox;
    private AbstractWidget isOnPonderCheckbox;
    private AbstractWidget onBrewingRecipeCheckbox;

    // 服务器物品设置
    private EditBox clearTimeEditBox;
    private ListStringInput itemWhitelistInput;

    // 清理客户端设置
    private EditBox displayTextHeadEditBox;
    private EditBox displayTextBodyEditBox;
    private EditBox displayTextTailEditBox;

    // 客户端设置
    private AbstractWidget customAbilityCheckbox;
    private AbstractWidget startAbilityWhitelistCheckbox;
    private ListStringInput customAbilityWhitelistInput;
    private ListStringInput onGhostPixelmonsInput;

    // 禁用模组列表
    private ListStringInput disabledModListInput;

    public RemDetailedConfigScreen(Screen parent) {
        super(Component.literal("REM 详细配置"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        setWindowSize(450, 350);
        super.init();

        int yPos = guiTop + 10;

        // 通用设置部分
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 200, 20,
                Component.literal("通用设置"), font));
        yPos += 25;

        // 布尔值配置项
        isClearServerItemCheckbox = createToggleButton(
                guiLeft + 10, yPos, 200, 20,
                Component.literal("开启服务器掉落物清理功能"),
                CommonConfig.isClearServerItemEnabled()
        );
        addRenderableWidget(isClearServerItemCheckbox);
        yPos += 25;

        isItemFilterCheckbox = createToggleButton(
                guiLeft + 10, yPos, 200, 20,
                Component.literal("物品过滤器模式"),
                CommonConfig.isItemFilterEnabled()
        );
        addRenderableWidget(isItemFilterCheckbox);
        yPos += 25;

        isDebugModeCheckbox = createToggleButton(
                guiLeft + 10, yPos, 200, 20,
                Component.literal("调试模式"),
                CommonConfig.isDebugModeEnabled()
        );
        addRenderableWidget(isDebugModeCheckbox);
        yPos += 25;

        isOnPonderCheckbox = createToggleButton(
                guiLeft + 10, yPos, 200, 20,
                Component.literal("启用内置思索"),
                CommonConfig.isOnPonderEnabled()
        );
        addRenderableWidget(isOnPonderCheckbox);
        yPos += 25;

        onBrewingRecipeCheckbox = createToggleButton(
                guiLeft + 10, yPos, 200, 20,
                Component.literal("启用酿造配方"),
                CommonConfig.isOnBrewingRecipeEnabled()
        );
        addRenderableWidget(onBrewingRecipeCheckbox);
        yPos += 35;

        // 服务器物品设置
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 200, 20,
                Component.literal("服务器物品设置"), font));
        yPos += 25;

        // 清理时间设置
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 80, 20,
                Component.literal("清理时间:"), font));
        clearTimeEditBox = new EditBox(font, guiLeft + 100, yPos, 160, 20,
                Component.literal(""));
        clearTimeEditBox.setValue(String.valueOf(CommonConfig.getClearTime()));
        clearTimeEditBox.setFilter(s -> s.matches("\\d*"));
        clearTimeEditBox.setTooltip(Tooltip.create(Component.literal("物品清理时间（游戏刻）\n范围：180 ~ 36000\n默认：6000")));
        addRenderableWidget(clearTimeEditBox);
        yPos += 30;

        // 物品白名单 - 使用 ListStringInput
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 80, 20,
                Component.literal("物品白名单:"), font));
        itemWhitelistInput = new ListStringInput(guiLeft + 100, yPos, 250, 20)
                .withValues(CommonConfig.getItemWhitelist())
                .titled(Component.literal("物品白名单"))
                .withHint(Component.literal("不会被清理的物品列表\n存储格式: [\"item1\",\"item2\",\"item3\"]"))
                .calling(this::onItemWhitelistChanged);
        addRenderableWidget(itemWhitelistInput);
        yPos += 30;

        // 清理客户端设置
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 200, 20,
                Component.literal("清理客户端设置"), font));
        yPos += 25;

        // 显示文本头
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 80, 20,
                Component.literal("显示文本头:"), font));
        displayTextHeadEditBox = new EditBox(font, guiLeft + 100, yPos, 250, 20,
                Component.literal(""));
        displayTextHeadEditBox.setValue(ClientConfig.getDisplayTextHead());
        displayTextHeadEditBox.setTooltip(Tooltip.create(Component.literal("显示在消息开头的文本\n支持颜色代码：&a&l等")));
        addRenderableWidget(displayTextHeadEditBox);
        yPos += 30;

        // 显示文本体
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 80, 20,
                Component.literal("显示文本体:"), font));
        displayTextBodyEditBox = new EditBox(font, guiLeft + 100, yPos, 250, 20,
                Component.literal(""));
        displayTextBodyEditBox.setValue(ClientConfig.getDisplayTextBody());
        displayTextBodyEditBox.setTooltip(Tooltip.create(Component.literal("显示在消息主体的文本\n支持颜色代码：&a&l等\n使用 %s 作为占位符")));
        addRenderableWidget(displayTextBodyEditBox);
        yPos += 35;

        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 200, 20,
                        Component.literal("自定义倒计时提示:"), font));
        displayTextTailEditBox = new EditBox(font, guiLeft + 100, yPos, 250, 20,
                Component.literal(""));
        displayTextTailEditBox.setValue(ClientConfig.getClearDownText());
        displayTextTailEditBox.setTooltip(Tooltip.create(Component.literal("显示自定义倒计时提示的文本\n支持颜色代码：&a&l等\n使用 %s 作为占位符")));
        addRenderableWidget(displayTextTailEditBox);
        yPos += 40;

        // 客户端设置
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 200, 20,
                Component.literal("客户端设置"), font));
        yPos += 25;

        customAbilityCheckbox = createToggleButton(
                guiLeft + 10, yPos, 200, 20,
                Component.literal("是否开启自定义特性"),
                ClientConfig.isCustomAbilityEnabled()
        );
        addRenderableWidget(customAbilityCheckbox);
        yPos += 25;

        startAbilityWhitelistCheckbox = createToggleButton(
                guiLeft + 10, yPos, 200, 20,
                Component.literal("是否开启自定义特性白名单"),
                ClientConfig.isStartAbilityWhitelistEnabled()
        );
        addRenderableWidget(startAbilityWhitelistCheckbox);
        yPos += 30;

        // 自定义特性白名单 - 使用 ListStringInput
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 80, 20,
                Component.literal("特性白名单:"), font));
        customAbilityWhitelistInput = new ListStringInput(guiLeft + 100, yPos, 250, 20)
                .withValues(ClientConfig.getCustomAbilityWhitelist())
                .titled(Component.literal("自定义特性白名单"))
                .withHint(Component.literal("允许的自定义特性列表\n存储格式: [\"ability1\",\"ability2\"]"))
                .calling(this::onCustomAbilityWhitelistChanged);
        addRenderableWidget(customAbilityWhitelistInput);
        yPos += 30;

        // 幽灵宝可梦 - 使用 ListStringInput
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 80, 20,
                Component.literal("幽灵宝可梦:"), font));
        onGhostPixelmonsInput = new ListStringInput(guiLeft + 100, yPos, 250, 20)
                .withValues(ClientConfig.getOnGhostPixelmons())
                .titled(Component.literal("幽灵宝可梦列表"))
                .withHint(Component.literal("被视为幽灵宝可梦的列表\n存储格式: [\"pokemon1\",\"pokemon2\"]"))
                .calling(this::onGhostPixelmonsChanged);
        addRenderableWidget(onGhostPixelmonsInput);
        yPos += 30;

        // 禁用模组列表 - 使用 ListStringInput
        addRenderableWidget(new StringWidget(guiLeft + 10, yPos, 80, 20,
                Component.literal("禁用模组:"), font));
        disabledModListInput = new ListStringInput(guiLeft + 100, yPos, 250, 20)
                .withValues(CommonConfig.getDisabledModList())
                .titled(Component.literal("禁用模组列表"))
                .withHint(Component.literal("禁用的模组ID列表\n存储格式: [\"modid1\",\"modid2\"]"))
                .calling(this::onDisabledModListChanged);
        addRenderableWidget(disabledModListInput);

        // 操作按钮
        int buttonY = guiTop + windowHeight - 30;
        addRenderableWidget(Button.builder(Component.literal("保存"), button -> saveConfig())
                .bounds(guiLeft + 50, buttonY, 80, 20)
                .build());

        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose())
                .bounds(guiLeft + 150, buttonY, 80, 20)
                .build());

        addRenderableWidget(Button.builder(Component.literal("重置"), button -> resetToDefaults())
                .bounds(guiLeft + 250, buttonY, 80, 20)
                .build());
    }

    /**
     * 创建切换按钮来模拟复选框功能
     */
    private AbstractWidget createToggleButton(int x, int y, int width, int height, Component text, boolean initialState) {
        // 使用按钮模拟复选框行为
        return Button.builder(getToggleButtonText(text, initialState), button -> {
            // 切换状态
            boolean newState = !getToggleButtonState(button.getMessage());
            button.setMessage(getToggleButtonText(text, newState));
        }).bounds(x, y, width, height).build();
    }

    /**
     * 获取切换按钮的显示文本
     */
    private Component getToggleButtonText(Component baseText, boolean state) {
        String stateText = state ? "§a✔ " : "§c✘ ";
        return Component.literal(stateText).append(baseText);
    }

    /**
     * 从按钮文本中提取状态
     */
    private boolean getToggleButtonState(Component buttonText) {
        String text = buttonText.getString();
        return text.contains("✔");
    }

    /**
     * 获取切换按钮的当前状态
     */
    private boolean getToggleButtonState(AbstractWidget button) {
        if (button instanceof Button) {
            return getToggleButtonState(button.getMessage());
        }
        return false;
    }

    /**
     * 物品白名单改变时的回调
     */
    private void onItemWhitelistChanged(List<String> newList) {
        // 可以在这里实时保存，或者等到点击保存按钮时统一保存
    }

    /**
     * 自定义特性白名单改变时的回调
     */
    private void onCustomAbilityWhitelistChanged(List<String> newList) {
    }

    /**
     * 幽灵宝可梦列表改变时的回调
     */
    private void onGhostPixelmonsChanged(List<String> newList) {
    }

    /**
     * 禁用模组列表改变时的回调
     */
    private void onDisabledModListChanged(List<String> newList) {
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // 绘制背景
        graphics.fill(guiLeft, guiTop, guiLeft + windowWidth, guiTop + windowHeight, 0xAA000000);
        graphics.drawCenteredString(font, title, width / 2, guiTop - 15, 0xFFFFFF);

        // 绘制说明
        graphics.drawString(font, "列表配置项使用JSON数组格式: [\"item1\",\"item2\",\"item3\"]",
                guiLeft + 10, guiTop + windowHeight - 50, 0xAAAAAA, false);
        graphics.drawString(font, "同时兼容裸逗号格式: [item1,item2,item3]",
                guiLeft + 10, guiTop + windowHeight - 35, 0xAAAAAA, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC键返回
        if (keyCode == 256) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void saveConfig() {
        try {
            // 保存通用设置
            ConfigUtil.setClearServerItem(getToggleButtonState(isClearServerItemCheckbox));
            ConfigUtil.setItemFilter(getToggleButtonState(isItemFilterCheckbox));
            ConfigUtil.setDebugMode(getToggleButtonState(isDebugModeCheckbox));
            ConfigUtil.setOnPonder(getToggleButtonState(isOnPonderCheckbox));
            ConfigUtil.setOnBrewingRecipe(getToggleButtonState(onBrewingRecipeCheckbox));

            // 保存服务器物品设置
            int clearTime = Integer.parseInt(clearTimeEditBox.getValue());
            if (clearTime >= 180 && clearTime <= 36000) {
                ConfigUtil.setClearTime(clearTime);
            }

            // 保存列表设置 - 从 ListStringInput 获取值并转换为JSON数组格式存储
            ConfigUtil.setItemWhitelist(itemWhitelistInput.getValues());
            ConfigUtil.setCustomAbilityWhitelist(customAbilityWhitelistInput.getValues());
            ConfigUtil.setOnGhostPokemons(onGhostPixelmonsInput.getValues());
            ConfigUtil.setDisabledModList(disabledModListInput.getValues());

            // 保存文本设置
            ConfigUtil.setDisplayTextHead(displayTextHeadEditBox.getValue());
            ConfigUtil.setDisplayTextBody(displayTextBodyEditBox.getValue());
            ConfigUtil.setDisplayTextTail(displayTextTailEditBox.getValue());

            // 保存布尔值设置
            ConfigUtil.setCustomAbility(getToggleButtonState(customAbilityCheckbox));
            ConfigUtil.setStartAbilityWhitelist(getToggleButtonState(startAbilityWhitelistCheckbox));

            if (minecraft != null && minecraft.player != null) {
                minecraft.player.displayClientMessage(Component.literal("§a配置已保存！"), true);
            }

        } catch (NumberFormatException e) {
            if (minecraft != null && minecraft.player != null) {
                minecraft.player.displayClientMessage(Component.literal("§c清理时间格式错误！"), true);
            }
        }
    }

    private void resetToDefaults() {
        // 重置切换按钮状态
        setToggleButtonState(isClearServerItemCheckbox, false);
        setToggleButtonState(isItemFilterCheckbox, false);
        setToggleButtonState(isDebugModeCheckbox, false);
        setToggleButtonState(isOnPonderCheckbox, true);
        setToggleButtonState(onBrewingRecipeCheckbox, true);
        setToggleButtonState(customAbilityCheckbox, true);
        setToggleButtonState(startAbilityWhitelistCheckbox, false);

        // 重置文本字段
        clearTimeEditBox.setValue("6000");
        displayTextHeadEditBox.setValue("[扫地姬]");
        displayTextBodyEditBox.setValue("本次总共清理了%s种掉落物，距离下次清理还剩%s秒");
        displayTextTailEditBox.setValue("亲爱的冒险家们，本次清理即将开始，剩余时间%s秒");

        // 重置列表字段 - 使用 ListStringInput 的 setValues 方法
        itemWhitelistInput.setValues(List.of("minecraft:command_block"));
        customAbilityWhitelistInput.setValues(List.of("DragonRise"));
        onGhostPixelmonsInput.setValues(List.of("Gengar"));
        disabledModListInput.setValues(new ArrayList<>());

        if (minecraft != null && minecraft.player != null) {
            minecraft.player.displayClientMessage(Component.literal("§6已重置为默认值"), true);
        }
    }

    /**
     * 设置切换按钮的状态
     */
    private void setToggleButtonState(AbstractWidget button, boolean state) {
        if (button instanceof Button btn) {
            // 从当前消息中提取基础文本（去掉状态前缀）
            Component currentMessage = btn.getMessage();
            String currentText = currentMessage.getString();

            // 提取基础文本（去掉状态前缀）
            String baseText = currentText.substring(currentText.indexOf(" ") + 1);

            // 设置新的状态
            btn.setMessage(getToggleButtonText(Component.literal(baseText), state));
        }
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.setScreen(parent);
        }
    }
}