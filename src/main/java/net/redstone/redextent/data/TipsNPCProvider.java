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
        JsonObject tipTitle = PixelmonNPCProvider.createTextTitle("模组宣传", "#F8F8F2", true, false, false);
        List<JsonObject> tipMessages = List.of(
                PixelmonNPCProvider.createTextMessage("欢迎来到我们的像素精灵世界！"),
                PixelmonNPCProvider.createTextMessage("探索、战斗、培育，体验前所未有的冒险！"),
                PixelmonNPCProvider.createTextMessage("记得定期检查我们的更新，获取最新内容和活动信息！")
        );

        addTipNPC(
                "publicity_ambassador",                     // 文件名
                List.of("Fire Dragon Kael", "Lu Ming Fei", "Magic Dragon An Ye"), // NPC名称列表
                tipTitle,                             // 自定义标题
                tipMessages,                          // 自定义消息列表
                List.of(                              // 纹理资源
                        "rem:textures/steve/fire_dragon.png",
                        "rem:textures/steve/gwenthe_dragon.png",
                        "rem:textures/steve/magic_dragon_electricity.png"
                )
        );
    }
}
