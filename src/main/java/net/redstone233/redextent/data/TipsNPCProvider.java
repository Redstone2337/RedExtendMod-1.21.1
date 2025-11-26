package net.redstone233.redextent.data;

import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.redstone233.redextent.core.generator.PixelmonNPCProvider;
import net.redstone233.redextent.core.npc.NPCDefinition;

import java.util.List;

public class TipsNPCProvider extends PixelmonNPCProvider {
    public TipsNPCProvider(PackOutput output) {
        super(output, "pixelmon", "tips");
    }

    @Override
    public void registerNPCs() {
        // 创建模组宣传NPC
        createPublicityAmbassador();
    }

    /**
     * 创建模组宣传NPC
     */
    private void createPublicityAmbassador() {
        // 创建多页对话内容
        List<String> messages = List.of(
                "欢迎来到 RedExtent 模组！",
                "这个模组添加了许多新的NPC和功能，",
                "包括道馆挑战、商店系统和各种互动。",
                "探索世界，挑战强大的训练家，",
                "收集稀有物品，享受宝可梦冒险！"
        );

        // 创建聊天交互
        List<JsonObject> chatInteractions = List.of(
                createMultiPageDialogueInteraction("模组介绍", messages)
        );

        // 创建NPC模型
        List<NPCDefinition.PlayerModel> models = List.of(
                createPlayerModel(false, "rem:textures/steve/fire_dragon.png"),
                createPlayerModel(false, "rem:textures/steve/gwenthe_dragon.png"),
                createPlayerModel(false, "rem:textures/steve/magic_dragon_electricity.png")
        );

        // 创建属性
        JsonObject properties = createBasicProperties(20.0f, 1.0f, 1.0f, 2.0f,
                false, false, false, false, true);

        // 创建AI提供者
        JsonObject aiProvider = createStandAndLookAI(4.0f, false);

        // 使用聊天NPC生成方法
        addChattingNPC(
                "publicity_ambassador",           // 文件名
                List.of("Fire Dragon Kael", "Lu Ming Fei", "Magic Dragon An Ye"), // 名称列表
                models,                           // 模型列表
                properties,                       // 属性
                aiProvider,                       // AI提供者
                chatInteractions                  // 交互列表
        );
    }
}