package net.redstone.redextent.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.redstone.redextent.core.generator.PixelmonNPCProvider;
import net.redstone.redextent.core.npc.NpcPresetBuilder;
import net.redstone.redextent.core.npc.NpcPresetBuilder.*;
import net.redstone.redextent.core.codecs.NpcDefinitionCodec;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 提示NPC数据提供者 - 专门生成提示NPC
 */
public class TipNPCProvider extends PixelmonNPCProvider {
    
    public TipNPCProvider(PackOutput output) {
        super(output, "pixelmon", "info");
    }

    @Override
    protected void registerNpcs(BiConsumer<String, NpcDefinitionCodec> consumer) {
        createModPublicityAmbassador(consumer);
    }

    /**
     * 创建模组宣传大使NPC
     */
    private void createModPublicityAmbassador(BiConsumer<String, NpcDefinitionCodec> consumer) {
        TextContent title = TextContent.literal("模组宣传");
        List<TextContent> tipMessages = List.of(
                TextContent.literal("欢迎来到我们的像素精灵世界！"),
                TextContent.literal("探索、战斗、培育，体验前所未有的冒险！"),
                TextContent.literal("记得定期检查我们的更新，获取最新内容和活动信息！")
        );

        NpcTemplate ambassador = NpcPresetBuilder.createChatNpc(
            "publicity_ambassador",
            List.of("Fire Dragon Kael", "Lu Ming Fei", "Magic Dragon An Ye"),
            List.of(
                ResourceLocation.parse("rem:textures/steve/fire_dragon.png"),
                ResourceLocation.parse("rem:textures/steve/gwenthe_dragon.png"),
                ResourceLocation.parse("rem:textures/steve/magic_dragon_electricity.png")
            ),
            title,
            tipMessages,
            true,
            true
        );

        consumer.accept(ambassador.name(), ambassador.build());
    }
}