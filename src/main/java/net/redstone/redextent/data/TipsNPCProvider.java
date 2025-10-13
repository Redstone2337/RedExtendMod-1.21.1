package net.redstone.redextent.data;

import net.minecraft.data.PackOutput;
import net.redstone.redextent.core.generator.PixelmonNPCProvider;

import java.util.List;

public class TipsNPCProvider extends PixelmonNPCProvider {
    public TipsNPCProvider(PackOutput output) {
        super(output, "pixelmon", "tips");
    }

    @Override
    public void registerNPCs() {
        // 使用新的 addMultiPageTipNPC 方法
        addMultiPageTipNPC(
                "publicity_ambassador",                     // 文件名
                List.of("Fire Dragon Kael", "Lu Ming Fei", "Magic Dragon An Ye"), // NPC名称列表
                "模组宣传",                                 // 属性标题翻译键
                "游戏提示",                                 // 交互标题翻译键
                List.of(                                   // 消息翻译键列表
                        "欢迎来到我们的像素精灵世界！",
                        "探索、战斗、培育，体验前所未有的冒险！",
                        "记得定期检查我们的更新，获取最新内容和活动信息！",
                        "与朋友组队挑战道馆，获得稀有奖励！",
                        "野外探险时注意稀有宝可梦的出现时机！"
                ),
                List.of(                                   // 纹理资源
                        "rem:textures/steve/fire_dragon.png",
                        "rem:textures/steve/gwenthe_dragon.png",
                        "rem:textures/steve/magic_dragon_electricity.png"
                )
        );

        // 添加更多提示NPC
//        addMultiPageTipNPC(
//                "battle_tips_trainer",
//                List.of("战斗导师·小明"),
//                "战斗技巧",
//                "对战教学",
//                List.of(
//                        "属性相克是战斗的关键！记得研究属性关系表。",
//                        "宝可梦的特性在战斗中能起到决定性作用！",
//                        "合理搭配招式和携带物品可以大幅提升战斗力！",
//                        "状态变化和场地效果往往能扭转战局！",
//                        "记得培养宝可梦的个体值和努力值！"
//                ),
//                List.of("rem:textures/steve/battle_tutor.png")
//        );

        // 使用完整参数版本的提示NPC
//        JsonObject propertyTitle = PixelmonNPCProvider.createTextTitle("培育专家", "#98FB98", true, false, false);
//        JsonObject interactionTitle = PixelmonNPCProvider.createTextTitle("培育咨询", "#98FB98", true, false, false);
//        List<JsonObject> breedingTips = List.of(
//                PixelmonNPCProvider.createTextMessage("宝可梦的个体值决定了它的潜力上限！"),
//                PixelmonNPCProvider.createTextMessage("通过培育可以遗传优秀的个体值和招式！"),
//                PixelmonNPCProvider.createTextMessage("不同性格会影响宝可梦的能力成长！"),
//                PixelmonNPCProvider.createTextMessage("闪光宝可梦的出现概率很低，需要耐心！")
//        );
//
//        addMultiPageTipNPC(
//                "breeding_expert",
//                List.of("培育家·小美"),
//                propertyTitle,
//                interactionTitle,
//                breedingTips,
//                List.of("rem:textures/steve/breeder.png"),
//                6.0f,  // 查看距离
//                0.95f  // 查看概率
//        );
    }
}