package net.redstone233.redextent.data;

import net.minecraft.data.PackOutput;
import net.redstone233.redextent.core.generator.PixelmonNPCProvider;

import java.util.List;

public class TipsNPCProvider extends PixelmonNPCProvider {
    public TipsNPCProvider(PackOutput output) {
        super(output, "pixelmon", "tips");
    }

    @Override
    public void registerNPCs() {
        // 使用纯字符串创建标题和消息
        String propertyTitle = "模组宣传";
        String interactionTitle = "游戏提示";
        List<String> messages = List.of(
                "欢迎来到我们的像素精灵世界！",
                "探索、战斗、培育，体验前所未有的冒险！",
                "记得定期检查我们的更新，获取最新内容和活动信息！",
                "与朋友组队挑战道馆，获得稀有奖励！",
                "野外探险时注意稀有宝可梦的出现时机！"
        );

        // 使用纯字符串版本的提示NPC
        addMultiPageTipNPCString(
                "publicity_ambassador",                     // 文件名
                List.of("Fire Dragon Kael", "Lu Ming Fei", "Magic Dragon An Ye"), // NPC名称列表
                propertyTitle,                              // 属性标题（纯字符串）
                interactionTitle,                           // 交互标题（纯字符串）
                messages,                                   // 消息列表（纯字符串）
                List.of(                                    // 纹理资源
                        "rem:textures/steve/fire_dragon.png",
                        "rem:textures/steve/gwenthe_dragon.png",
                        "rem:textures/steve/magic_dragon_electricity.png"
                ),
                4.0f,
                0.9f
        );

        // 添加特性介绍npc
        String abilityTitle = "特性介绍";
        String abilityInteractionTitle = "特性教学";
        List<String> abilityMessages = List.of(
                "关于自定义特性类型，模组内有5个，每个都有其独特的能力。",
                "快启动：宝可梦登场后的前3回合内，攻击与速度提升50%，3回合后效果结",
                "升龙崛起：当宝可梦登场时，若其为龙属性，则特攻、攻击与速度提升，持续3回合。每回合提升效果增强",
                "斗神神格：大幅提升格斗属性技能的威力。若宝可梦为格斗属性，登场时特攻与攻击造成的伤害提升10%。",
                "雷神神格：电属性宝可梦登场时攻击提升20%，特攻提升25%，速度提升5%，且受到效果绝佳技能的伤害减少5%。",
                "沼泽赐福：宝可梦在沼泽上场时，特攻物攻各加5%，速度加15%。如果是名单里的幽灵系宝可梦，特攻物攻直接加10%，速度加15%，然后攻击和特攻再额外加20%，总共能加到30%！"
        );

        addMultiPageTipNPCString(
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