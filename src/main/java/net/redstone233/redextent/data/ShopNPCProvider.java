package net.redstone233.redextent.data;

import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.redstone233.redextent.core.generator.PixelmonNPCProvider;

import java.util.List;

public class ShopNPCProvider extends PixelmonNPCProvider {
    public ShopNPCProvider(PackOutput output) {
        super(output, "pixelmon", "shop");
    }

    @Override
    public void registerNPCs() {
        // 创建更多商店物品的示例
        List<JsonObject> premiumItems = List.of(
                PixelmonNPCProvider.createShopItem("pixelmon:master_ball", 1, 50000.0, 12500.0),
                PixelmonNPCProvider.createShopItem("pixelmon:rare_candy", 10, 50000.0, 12500.0),
                PixelmonNPCProvider.createShopItem("pixelmon:park_ball", 1, 30000.0, 15000.0),
                PixelmonNPCProvider.createShopItem("pixelmon:ability_capsule", 1, 10000.0, 2500.0)
        );

        // 使用直接文本创建标题和消息
        JsonObject propertyTitle = PixelmonNPCProvider.createTextTitle("毛毛龙的商店", "#FFD700", true, false, true);
        JsonObject interactionTitle = PixelmonNPCProvider.createTextTitle("商店交互", "#FFD700", true, false, true);
        JsonObject greeting = PixelmonNPCProvider.createTextMessage("欢迎来到毛毛龙商店，这里只有最稀有的商品！");
        JsonObject goodbye = PixelmonNPCProvider.createTextMessage("感谢您的光临，期待再次为您服务！");

        // 使用完整参数版本的商店NPC
        addCustomShopNPC(
                "info_dragon",                       // 文件名
                List.of("毛毛龙·晨曦"),              // NPC名称列表
                propertyTitle,                       // 属性标题（直接文本）
                interactionTitle,                    // 交互标题（直接文本）
                greeting,                            // 问候语（直接文本）
                goodbye,                             // 告别语（直接文本）
                premiumItems,                        // 商店物品列表
                List.of("rem:textures/steve/info_dragon.png"), // 纹理资源
                5.0f,                                // 查看距离
                0.9f                                 // 查看概率
        );
    }
}