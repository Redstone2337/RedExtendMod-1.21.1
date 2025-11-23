package net.redstone233.redextent.core.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.createmod.catnip.data.Iterate;
import net.redstone233.redextent.Config;
import net.redstone233.redextent.RedExtendMod;
import net.redstone233.redextent.core.util.ConfigUtil;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.gui.element.BoxElement;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.catnip.theme.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.redstone233.redextent.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RemMainMenuScreen extends AbstractSimiScreen {

    // 使用默认的全景背景
    public static final CubeMap PANORAMA_RESOURCES = new CubeMap(ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama"));
    public static final ResourceLocation PANORAMA_OVERLAY_TEXTURES = ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama_overlay.png");
    public static final PanoramaRenderer PANORAMA = new PanoramaRenderer(PANORAMA_RESOURCES);

    // REM 模组相关链接
    private static final String GITHUB_LINK = "https://github.com/Redstone2337/RedExtendMod-1.21.1";
    private static final String DISCORD_LINK = "https://github.com/your-username/red-extent-mod/wiki";
    private static final String ISSUES_LINK = "https://github.com/Redstone2337/RedExtendMod-1.21.1/issues";

    private static final Component GITHUB_TOOLTIP = Component.literal("GitHub").withStyle(s -> s.withColor(0x6CC644).withBold(true));
    private static final Component DISCORD_TOOLTIP = Component.literal("Discord").withStyle(s -> s.withColor(0x4285F4).withBold(true));

    protected final Screen parent;
    protected boolean returnOnClose;

    private final PanoramaRenderer vanillaPanorama;
    private long firstRenderTime;
    private Button itemClearButton;
    private Button brewingRecipeButton;
    private Button debugModeButton;
    private Button customAbilityButton;

    public RemMainMenuScreen(Screen parent) {
        this.parent = parent;
        returnOnClose = true;
        if (parent instanceof TitleScreen)
            vanillaPanorama = Screen.PANORAMA;
        else
            vanillaPanorama = new PanoramaRenderer(TitleScreen.CUBE_MAP);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (firstRenderTime == 0L)
            this.firstRenderTime = Util.getMillis();
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderWindow(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        float f = (float) (Util.getMillis() - this.firstRenderTime) / 1000.0F;
        float alpha = Mth.clamp(f, 0.0F, 1.0F);
        float elapsedPartials = 0;
        if (minecraft != null) {
            elapsedPartials = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
        }

        if (parent instanceof TitleScreen) {
            if (alpha < 1)
                vanillaPanorama.render(graphics, this.width, this.height, 1, elapsedPartials);
            PANORAMA.render(graphics, this.width, this.height, 1, elapsedPartials);

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            graphics.blit(PANORAMA_OVERLAY_TEXTURES, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
        }

        RenderSystem.enableDepthTest();

        PoseStack ms = graphics.pose();

        // 自定义 3D 渲染元素 - 使用铁锭和金锭作为展示
        for (int side : Iterate.positiveAndNegative) {
            ms.pushPose();
            ms.translate((float) width / 2, 60, 200);
            ms.scale(20 * side, 20 * side, 24);
            ms.translate(-1.5f * ((alpha * alpha) / 2f + .5f), .25f, 0);
            TransformStack.of(ms)
                    .rotateXDegrees(45);
            GuiGameElement.of(new ItemStack(Items.IRON_INGOT))
                    .rotateBlock(0, Util.getMillis() / 32f * side, 0)
                    .render(graphics);
            ms.translate(-1, 0, -1);
            GuiGameElement.of(new ItemStack(Items.GOLD_INGOT))
                    .rotateBlock(0, Util.getMillis() / -16f * side + 22.5f, 0)
                    .render(graphics);
            ms.popPose();
        }

        RenderSystem.enableBlend();

        // 标题和版本信息
        ms.pushPose();
        ms.translate((float) width / 2 - 40, 32, -10);

        // 版本信息背景框
        new BoxElement().withBackground(0x88_000000)
                .flatBorder(new Color(0x01_000000))
                .at(-40, 56, 100)
                .withBounds(160, 11)
                .render(graphics);
        ms.popPose();

        // 版本文本
        ms.pushPose();
        ms.translate(0, 0, 200);
        graphics.drawCenteredString(font,
                Component.literal("Red Extent Mod").withStyle(ChatFormatting.BOLD)
                        .append(Component.literal(RedExtendMod.getModVersion()).withStyle(ChatFormatting.BOLD, ChatFormatting.WHITE)),
                width / 2, 89, 0xFF_FF6B6B);
        ms.popPose();

        RenderSystem.disableDepthTest();
    }

    @Override
    protected void init() {
        super.init();
        returnOnClose = true;
        this.addButtons();
    }

    private void addButtons() {
        int yStart = height / 4 + 40;
        int center = width / 2;
        int bHeight = 20;
        int bShortWidth = 98;
        int bLongWidth = 200;

        // 返回按钮
        addRenderableWidget(Button.builder(Component.literal("返回"), $ -> linkTo(parent))
                .bounds(center - 100, yStart + 132, bLongWidth, bHeight)
                .build());

        // 服务器物品清理配置
        addRenderableWidget(Button.builder(
                Component.literal("物品清理: " + (Config.isClearServerItemEnabled() ? "开启" : "关闭")),
                $ -> toggleItemClear()
        ).bounds(center - 100, yStart + 24, bLongWidth, bHeight).build());

        // 物品过滤器配置
        addRenderableWidget(Button.builder(
                Component.literal("物品过滤器: " + (Config.isItemFilterEnabled() ? "开启" : "关闭")),
                $ -> toggleItemFilter()
        ).bounds(center - 100, yStart + 48, bLongWidth, bHeight).build());

        // 酿造配方配置
        brewingRecipeButton = Button.builder(
                Component.literal("酿造配方: " + (Config.isOnBrewingRecipeEnabled() ? "开启" : "关闭")),
                $ -> toggleBrewingRecipe()
        ).bounds(center - 100, yStart + 72, bLongWidth, bHeight).build();
        addRenderableWidget(brewingRecipeButton);

        // 调试模式配置
        debugModeButton = Button.builder(
                Component.literal("调试模式: " + (Config.isDebugModeEnabled() ? "开启" : "关闭")),
                $ -> toggleDebugMode()
        ).bounds(center - 100, yStart + 96, bLongWidth, bHeight).build();
        addRenderableWidget(debugModeButton);

        // 自定义特性配置
        customAbilityButton = Button.builder(
                Component.literal("自定义特性: " + (Config.isCustomAbilityEnabled() ? "开启" : "关闭")),
                $ -> toggleCustomAbility()
        ).bounds(center - 100, yStart + 120, bLongWidth, bHeight).build();
        addRenderableWidget(customAbilityButton);

        // 社区链接按钮
        addRenderableWidget(new RemIconButton(center + 2, yStart + 24, bShortWidth / 2, bHeight,
                ModItems.DISCORD_ITEM.toStack(), 1.0f,
                b -> linkTo(DISCORD_LINK),
                Tooltip.create(DISCORD_TOOLTIP)));

        addRenderableWidget(new RemIconButton(center + 2 + bShortWidth / 2, yStart + 24, bShortWidth / 2, bHeight,
                ModItems.GITHUB_ITEM.toStack(), 1.0f,
                b -> linkTo(GITHUB_LINK),
                Tooltip.create(GITHUB_TOOLTIP)));

        // 报告问题按钮
        addRenderableWidget(Button.builder(Component.literal("报告问题"), $ -> linkTo(ISSUES_LINK))
                .bounds(center + 2, yStart + 48, bShortWidth, bHeight)
                .build());

        // 详细配置按钮
        addRenderableWidget(Button.builder(Component.literal("详细配置"), $ -> openDetailedConfig())
                .bounds(center + 2, yStart + 72, bShortWidth, bHeight)
                .build());

        // 重置配置按钮
        addRenderableWidget(Button.builder(Component.literal("重置配置"), $ -> resetConfig())
                .bounds(center + 2, yStart + 96, bShortWidth, bHeight)
                .build());
    }

    @Override
    protected void renderWindowForeground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWindowForeground(graphics, mouseX, mouseY, partialTicks);

        // 添加配置状态提示
        if (brewingRecipeButton != null && brewingRecipeButton.isMouseOver(mouseX, mouseY)) {
            graphics.renderComponentTooltip(font,
                    FontHelper.cutTextComponent(
                            Component.literal("当前清理时间: " + Config.getClearTime() + " ticks (" + (Config.getClearTime() / 20) + "秒)"),
                            FontHelper.Palette.ALL_GRAY), mouseX, mouseY);
        }
    }

    // 配置切换方法 - 使用 ConfigUtil
    private void toggleItemClear() {
        boolean current = Config.isClearServerItemEnabled();
        ConfigUtil.setClearServerItem(!current);

        // 更新按钮文本
        updateButtonText("物品清理:", !current);
        RedExtendMod.LOGGER.info("物品清理功能: {}", !current ? "启用" : "禁用");
    }

    private void toggleItemFilter() {
        boolean current = Config.isItemFilterEnabled();
        ConfigUtil.setItemFilter(!current);

        updateButtonText("物品过滤器:", !current);
        RedExtendMod.LOGGER.info("物品过滤器: {}", !current ? "启用" : "禁用");
    }

    private void toggleBrewingRecipe() {
        boolean current = Config.isOnBrewingRecipeEnabled();
        ConfigUtil.setOnBrewingRecipe(!current);

        if (brewingRecipeButton != null) {
            brewingRecipeButton.setMessage(Component.literal("酿造配方: " + (!current ? "开启" : "关闭")));
        }
        RedExtendMod.LOGGER.info("酿造配方: {}", !current ? "启用" : "禁用");
    }

    private void toggleDebugMode() {
        boolean current = Config.isDebugModeEnabled();
        ConfigUtil.setDebugMode(!current);

        if (debugModeButton != null) {
            debugModeButton.setMessage(Component.literal("调试模式: " + (!current ? "开启" : "关闭")));
        }
        RedExtendMod.LOGGER.info("调试模式: {}", !current ? "启用" : "禁用");
    }

    private void toggleCustomAbility() {
        boolean current = Config.isCustomAbilityEnabled();
        ConfigUtil.setCustomAbility(!current);

        if (customAbilityButton != null) {
            customAbilityButton.setMessage(Component.literal("自定义特性: " + (!current ? "开启" : "关闭")));
        }
        RedExtendMod.LOGGER.info("自定义特性: {}", !current ? "启用" : "禁用");
    }

    // 辅助方法：更新按钮文本
    private void updateButtonText(String prefix, boolean newState) {
        for (var widget : renderables) {
            if (widget instanceof Button button) {
                String message = button.getMessage().getString();
                if (message.startsWith(prefix)) {
                    button.setMessage(Component.literal(prefix + " " + (newState ? "开启" : "关闭")));
                    break;
                }
            }
        }
    }

    private void openDetailedConfig() {
        // 打开详细的配置屏幕
        linkTo(new RemDetailedConfigScreen(this));
    }

    private void linkTo(Screen screen) {
        returnOnClose = false;
        ScreenOpener.open(screen);
    }

    private void linkTo(String url) {
        returnOnClose = false;
        ScreenOpener.open(new ConfirmLinkScreen((confirmed) -> {
            if (confirmed)
                Util.getPlatform().openUri(url);
            if (this.minecraft != null) {
                this.minecraft.setScreen(this);
            }
        }, url, true));
    }

    private void resetConfig() {
        if (minecraft != null) {
            minecraft.setScreen(new ConfirmLinkScreen((confirmed) -> {
                if (confirmed) {
                    // 执行重置配置的操作
                    resetAllConfigs();
                    // 重新加载屏幕以更新按钮状态
                    minecraft.setScreen(new RemMainMenuScreen(parent));
                } else {
                    this.minecraft.setScreen(this);
                }
            }, "确定要重置所有配置为默认值吗？", false));
        }
    }

    // 重置所有配置为默认值
    private void resetAllConfigs() {
        // 使用 ConfigUtil 重置所有配置
        ConfigUtil.setClearServerItem(false);
        ConfigUtil.setItemFilter(false);
        ConfigUtil.setDebugMode(false);
        ConfigUtil.setOnPonder(true);
        ConfigUtil.setOnBrewingRecipe(true);
        ConfigUtil.setClearTime(6000);
        ConfigUtil.setItemWhitelist(List.of("minecraft:command_block"));
        ConfigUtil.setDisplayTextHead("[扫地姬]");
        ConfigUtil.setDisplayTextBody("本次总共清理了%s种掉落物，距离下次清理还剩%s秒");
        ConfigUtil.setCustomAbility(true);
        ConfigUtil.setStartAbilityWhitelist(false);
        ConfigUtil.setCustomAbilityWhitelist(List.of("FastStart"));
        ConfigUtil.setOnGhostPokemons(List.of("Gengar"));
        ConfigUtil.setDisabledModList(List.of());

        RedExtendMod.LOGGER.info("所有配置已重置为默认值");
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public void onClose() {
        if (returnOnClose) {
            if (minecraft != null) {
                minecraft.setScreen(parent);
            }
        }
    }

    // REM 图标按钮类
    protected static class RemIconButton extends Button {
        protected final ItemStack icon;
        protected final float scale;

        public RemIconButton(int x, int y, int width, int height, ItemStack icon,
                             float scale, OnPress onPress, Tooltip tooltip) {
            super(x, y, width, height, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
            this.icon = icon;
            this.scale = scale;
            setTooltip(tooltip);
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            super.renderWidget(graphics, mouseX, mouseY, partialTicks);
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.translate(getX() + (float) width / 2 - 8 * scale,
                    getY() + (float) height / 2 - 8 * scale, 0);
            poseStack.scale(scale, scale, 1);
            graphics.renderItem(icon, 0, 0);
            poseStack.popPose();
        }
    }
}