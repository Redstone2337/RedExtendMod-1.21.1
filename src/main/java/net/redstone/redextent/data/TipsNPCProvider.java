package net.redstone.redextent.data;

import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.redstone.redextent.core.generator.PixelmonNPCProvider;

import java.util.List;

public class TipsNPCProvider extends PixelmonNPCProvider {
    public TipsNPCProvider(PackOutput output) {
        super(output, "pixelmon", "tips");
    }

    @Override
    public void registerNPCs() {
        // 使用直接文本创建标题和消息
        JsonObject propertyTitle = PixelmonNPCProvider.createTextTitle("模组宣传", "#F8F8F2", true, false, false);
        JsonObject interactionTitle = PixelmonNPCProvider.createTextTitle("游戏提示", "#F8F8F2", true, false, false);
        List<JsonObject> messages = List.of(
                PixelmonNPCProvider.createTextMessage("欢迎来到我们的像素精灵世界！"),
                PixelmonNPCProvider.createTextMessage("探索、战斗、培育，体验前所未有的冒险！"),
                PixelmonNPCProvider.createTextMessage("记得定期检查我们的更新，获取最新内容和活动信息！"),
                PixelmonNPCProvider.createTextMessage("与朋友组队挑战道馆，获得稀有奖励！"),
                PixelmonNPCProvider.createTextMessage("野外探险时注意稀有宝可梦的出现时机！")
        );

        // 使用完整参数版本的提示NPC
        addMultiPageTipNPC(
                "publicity_ambassador",                     // 文件名
                List.of("Fire Dragon Kael", "Lu Ming Fei", "Magic Dragon An Ye"), // NPC名称列表
                propertyTitle,                              // 属性标题（直接文本）
                interactionTitle,                           // 交互标题（直接文本）
                messages,                                   // 消息列表（直接文本）
                List.of(                                    // 纹理资源
                        "rem:textures/steve/fire_dragon.png",
                        "rem:textures/steve/gwenthe_dragon.png",
                        "rem:textures/steve/magic_dragon_electricity.png"
                ),
                4.0f,
                0.9f
        );

        // 添加特性介绍npc
        JsonObject abilityTitle = PixelmonNPCProvider.createTextTitle("特性介绍", "#FF6B6B", true, false, false);
        JsonObject abilityInteractionTitle = PixelmonNPCProvider.createTextTitle("特性教学", "#FF6B6B", true, false, false);
        List<JsonObject> abilityMessages = List.of(
                PixelmonNPCProvider.createTextMessage("关于自定义特性类型，模组内有5个，每个都有其独特的能力。"),
                PixelmonNPCProvider.createTextMessage("快启动：宝可梦登场后的前3回合内，攻击与速度提升50%，3回合后效果结"),
                PixelmonNPCProvider.createTextMessage("升龙崛起：当宝可梦登场时，若其为龙属性，则特攻、攻击与速度提升，持续3回合。每回合提升效果增强"),
                PixelmonNPCProvider.createTextMessage("斗神神格：大幅提升格斗属性技能的威力。若宝可梦为格斗属性，登场时特攻与攻击造成的伤害提升10%。"),
                PixelmonNPCProvider.createTextMessage("雷神神格：电属性宝可梦登场时攻击提升20%，特攻提升25%，速度提升5%，且受到效果绝佳技能的伤害减少5%。"),
                PixelmonNPCProvider.createTextMessage("沼泽赐福：宝可梦在沼泽上场时，特攻物攻各加5%，速度加15%。如果是名单里的幽灵系宝可梦，特攻物攻直接加10%，速度加15%，然后攻击和特攻再额外加20%，总共能加到30%！")
        );

        addMultiPageTipNPC(
                "ability_tips",
                List.of("特性介绍师·菲尔特"),
                abilityTitle,
                abilityInteractionTitle,
                abilityMessages,
                List.of("rem:textures/steve/ender_dragon.png"),
                5.0f,
                0.9f
        );
    }
}