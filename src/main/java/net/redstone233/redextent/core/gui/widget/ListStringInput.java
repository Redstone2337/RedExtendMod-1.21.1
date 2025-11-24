package net.redstone233.redextent.core.gui.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.createmod.catnip.config.ui.ConfigTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ListStringInput extends AbstractSimiWidget {

    protected Consumer<List<String>> onChanged;
    protected List<String> values;
    protected EditBox textField;
    protected Component title;
    protected Component hint;

    public ListStringInput(int xIn, int yIn, int widthIn, int heightIn) {
        super(xIn, yIn, widthIn, heightIn);
        this.values = new ArrayList<>();

        // 创建文本框
        this.textField = new ConfigTextField(Minecraft.getInstance().font, xIn, yIn, widthIn, heightIn);
        this.textField.setResponder(this::onTextChanged);
        this.textField.setVisible(true);
        this.textField.setBordered(true);

        updateDisplay();
    }

    public ListStringInput withValues(List<String> values) {
        this.values = new ArrayList<>(values);
        updateDisplay();
        return this;
    }

    public ListStringInput calling(Consumer<List<String>> onChanged) {
        this.onChanged = onChanged;
        return this;
    }

    public ListStringInput titled(Component title) {
        this.title = title;
        updateTooltip();
        return this;
    }

    public ListStringInput withHint(Component hint) {
        this.hint = hint;
        updateTooltip();
        return this;
    }

    public List<String> getValues() {
        return new ArrayList<>(values);
    }

    public void setValues(List<String> values) {
        this.values = new ArrayList<>(values);
        updateDisplay();
        if (onChanged != null) {
            onChanged.accept(this.values);
        }
    }

    private void onTextChanged(String newText) {
        // 将逗号分隔的字符串解析为列表
        if (newText == null || newText.trim().isEmpty()) {
            this.values.clear();
        } else {
            this.values = Arrays.stream(newText.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        if (onChanged != null) {
            onChanged.accept(this.values);
        }

        updateTooltip();
    }

    private void updateDisplay() {
        // 将列表转换为逗号分隔的字符串显示
        String displayText = String.join(", ", values);
        textField.setValue(displayText);
    }

    private void updateTooltip() {
        toolTip.clear();
        if (title != null) {
            toolTip.add(title.copy().withStyle(style -> style.withColor(0x5391e1)));
        }
        if (hint != null) {
            toolTip.add(hint.copy().withStyle(style -> style.withColor(0x96b7e0)));
        }
        if (!values.isEmpty()) {
            toolTip.add(Component.literal("当前项目: " + String.join(", ", values))
                    .withStyle(style -> style.withColor(0xCCCCCC)));
        } else {
            toolTip.add(Component.literal("暂无项目").withStyle(style -> style.withColor(0x888888)));
        }
        toolTip.add(Component.literal("使用逗号分隔多个项目").withStyle(style -> style.withColor(0x888888).withItalic(true)));
    }

    @Override
    protected void doRender(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // 渲染文本框
        textField.setX(getX());
        textField.setY(getY());
        textField.setWidth(width);
        textField.setHeight(height);
        textField.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        textField.setFocused(true);
        super.onClick(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (textField.isFocused()) {
            return textField.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (textField.isFocused()) {
            return textField.charTyped(codePoint, modifiers);
        }
        return false;
    }

    @Override
    public void tick() {
        // EditBox 不需要 tick 方法，这里保持空实现
    }

    public void setEditable(boolean editable) {
        textField.setEditable(editable);
        active = editable;
    }

    // 添加一些便捷方法，便于从外部访问文本框状态
    public boolean isFocused() {
        return textField.isFocused();
    }

    public void setFocused(boolean focused) {
        textField.setFocused(focused);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return textField.isMouseOver(mouseX, mouseY);
    }
}