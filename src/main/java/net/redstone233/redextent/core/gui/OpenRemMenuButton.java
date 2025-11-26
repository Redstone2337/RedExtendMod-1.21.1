package net.redstone233.redextent.core.gui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableObject;

import net.redstone233.redextent.RedExtendMod;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.jetbrains.annotations.NotNull;

public class OpenRemMenuButton extends Button {

    public OpenRemMenuButton(int x, int y) {
        super(x, y, 20, 20, CommonComponents.EMPTY, OpenRemMenuButton::click, DEFAULT_NARRATION);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // 使用原版按钮的渲染逻辑
        if (this.visible) {
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

            // 渲染按钮背景
            graphics.blitSprite(Button.SPRITES.get(this.active, this.isHovered()), this.getX(), this.getY(), this.width, this.height);

            // 渲染图标
            this.renderIcon(graphics);
        }
    }

    @Override
    public void renderString(@NotNull GuiGraphics graphics, @NotNull Font font, int color) {
        // 空实现，因为我们已经在 renderWidget 中处理了图标
    }

    private void renderIcon(GuiGraphics graphics) {
        // 使用书与笔作为配置图标，更符合主题
        ItemStack icon = new ItemStack(Items.WRITABLE_BOOK);

        BakedModel bakedmodel = Minecraft.getInstance()
                .getItemRenderer()
                .getModel(icon, Minecraft.getInstance().level, Minecraft.getInstance().player, 0);
        if (bakedmodel == null)
            return;

        // 修复图标位置：在按钮中心渲染图标
        // 物品渲染默认是16x16，但按钮是20x20，所以需要计算居中位置
        int iconX = this.getX() + (this.width - 16) / 2;
        int iconY = this.getY() + (this.height - 16) / 2;

        // 根据按钮状态调整透明度
        float alpha = this.active ? 1.0F : 0.5F;

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 100); // 确保图标在按钮上方
        graphics.renderItem(icon, iconX, iconY);
        graphics.pose().popPose();
    }

    public static void click(Button b) {
        // 直接打开 BaseConfigScreen（模组全部配置）
        ScreenOpener.open(new BaseConfigScreen(Minecraft.getInstance().screen, RedExtendMod.MOD_ID));
    }

    public record SingleMenuRow(String leftTextKey, String rightTextKey) {
        public SingleMenuRow(String centerTextKey) {
            this(centerTextKey, centerTextKey);
        }
    }

    public static class MenuRows {
        public static final MenuRows MAIN_MENU = new MenuRows(Arrays.asList(
                new SingleMenuRow("menu.singleplayer"),
                new SingleMenuRow("menu.multiplayer"),
                new SingleMenuRow("fml.menu.mods", "menu.online"),
                new SingleMenuRow("narrator.button.language", "narrator.button.accessibility")
        ));

        public static final MenuRows INGAME_MENU = new MenuRows(Arrays.asList(
                new SingleMenuRow("menu.returnToGame"),
                new SingleMenuRow("gui.advancements", "gui.stats"),
                new SingleMenuRow("menu.sendFeedback", "menu.reportBugs"),
                new SingleMenuRow("menu.options", "menu.shareToLan"),
                new SingleMenuRow("menu.returnToMenu")
        ));

        protected final List<String> leftTextKeys, rightTextKeys;

        public MenuRows(List<SingleMenuRow> rows) {
            leftTextKeys = rows.stream().map(SingleMenuRow::leftTextKey).collect(Collectors.toList());
            rightTextKeys = rows.stream().map(SingleMenuRow::rightTextKey).collect(Collectors.toList());
        }
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class OpenRemMenuButtonHandler {

        @SubscribeEvent
        public static void onGuiInit(ScreenEvent.Init.Post event) {
            Screen screen = event.getScreen();

            MenuRows menu;
            int rowIdx;
            int offsetX;
            if (screen instanceof TitleScreen) {
                menu = MenuRows.MAIN_MENU;
                rowIdx = 3; // 默认在第3行显示（语言和辅助功能按钮行）
                offsetX = -24; // 在左侧显示
            } else if (screen instanceof PauseScreen) {
                menu = MenuRows.INGAME_MENU;
                rowIdx = 4; // 默认在第4行显示（返回主菜单按钮行）
                offsetX = -24; // 在左侧显示
            } else {
                return;
            }

            if (rowIdx == 0) {
                return;
            }

            boolean onLeft = offsetX < 0;
            String targetMessage = I18n.get((onLeft ? menu.leftTextKeys : menu.rightTextKeys).get(rowIdx - 1));

            int offsetX_ = offsetX;
            MutableObject<GuiEventListener> toAdd = new MutableObject<>(null);

            // 找到目标按钮并在其旁边放置我们的按钮
            event.getListenersList()
                    .stream()
                    .filter(w -> w instanceof AbstractWidget)
                    .map(w -> (AbstractWidget) w)
                    .filter(w -> w.getMessage()
                            .getString()
                            .equals(targetMessage))
                    .findFirst()
                    .ifPresent(w -> {
                        // 修复按钮位置：使用更小的偏移量，让按钮紧贴原版控件
                        // 原版20×20按钮之间的标准间距是2像素
                        int buttonOffset = onLeft ? -24 : 2;
                        toAdd.setValue(new OpenRemMenuButton(
                                w.getX() + buttonOffset,
                                w.getY()
                        ));
                    });

            if (toAdd.getValue() != null) {
                event.addListener(toAdd.getValue());

                // 为按钮添加工具提示
                OpenRemMenuButton button = (OpenRemMenuButton) toAdd.getValue();
                button.setTooltip(net.minecraft.client.gui.components.Tooltip.create(
                        Component.literal("REM Mod 配置")
                ));
            }
        }
    }
}