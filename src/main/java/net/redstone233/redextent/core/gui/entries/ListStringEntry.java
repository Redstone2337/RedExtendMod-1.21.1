package net.redstone233.redextent.core.gui.entries;

import java.util.List;

import net.createmod.catnip.config.ui.entries.ValueEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.redstone233.redextent.core.gui.widget.ListStringInput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ListStringEntry extends ValueEntry<List<String>> {

    protected ListStringInput listInput;

    public ListStringEntry(String label, ModConfigSpec.ConfigValue<List<String>> value, ModConfigSpec.ValueSpec spec) {
        super(label, value, spec);

        listInput = new ListStringInput(0, 0, 150, 20)
                .withValues(value.get())
                .titled(Component.literal(label))
                .withHint(Component.literal("使用逗号分隔多个项目"))
                .calling(this::setValue);

        listeners.add(listInput);
        onReset();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean p_230432_9_, float partialTicks) {
        super.render(graphics, index, y, x, width, height, mouseX, mouseY, p_230432_9_, partialTicks);

        // 设置列表输入框的位置和大小
        int inputWidth = Math.min(width - getLabelWidth(width) - resetWidth - 10, 200);
        listInput.setX(x + width - inputWidth - resetWidth - 5);
        listInput.setY(y + 5);
        listInput.setWidth(inputWidth);
        listInput.setHeight(height - 10);

        listInput.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void setEditable(boolean b) {
        super.setEditable(b);
        listInput.setEditable(b);
    }

    @Override
    public void onReset() {
        super.onReset();
        listInput.withValues(getValue());
    }

    @Override
    public void tick() {
        super.tick();
        listInput.tick();
    }
}