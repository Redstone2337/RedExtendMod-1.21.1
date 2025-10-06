package net.redstone.redextent.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.redstone.redextent.core.generator.PixelmonNPCProvider;
import net.redstone.redextent.core.npc.NpcPresetBuilder;
import net.redstone.redextent.core.npc.NpcPresetBuilder.*;
import net.redstone.redextent.core.npc.NPCType;

import java.util.List;
import java.util.Map;

/**
 * 提示NPC数据提供者 - 专门生成提示NPC
 */
public class TipNPCProvider extends PixelmonNPCProvider {
    
    public TipNPCProvider(PackOutput output) {
        super(output, "pixelmon", "info");
    }

    @Override
    public void registerNPCs() {
        createModPublicityAmbassador();
    }

    /**
     * 创建模组宣传大使NPC
     */
    private void createModPublicityAmbassador() {
        TextContent title = TextContent.literal("模组宣传");
        List<TextContent> tipMessages = List.of(
                TextContent.literal("欢迎来到我们的像素精灵世界！"),
                TextContent.literal("探索、战斗、培育，体验前所未有的冒险！"),
                TextContent.literal("记得定期检查我们的更新，获取最新内容和活动信息！")
        );

        Map<String, Object> chatProperties = Map.of(
            "dialoguePages", tipMessages,
            "randomName", true,
            "randomModel", true
        );

        NpcTemplate ambassador = NpcPresetBuilder.createNpcByType(
            "publicity_ambassador",
            NPCType.CHATTING,
            List.of("Fire Dragon Kael", "Lu Ming Fei", "Magic Dragon An Ye"),
            List.of(
                ResourceLocation.parse("rem:textures/steve/fire_dragon.png"),
                ResourceLocation.parse("rem:textures/steve/gwenthe_dragon.png"),
                ResourceLocation.parse("rem:textures/steve/magic_dragon_electricity.png")
            ),
            title,
            chatProperties
        );

        addNPC(ambassador);
    }
}