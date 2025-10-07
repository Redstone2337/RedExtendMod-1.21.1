package net.redstone.redextent.data;

import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.redstone.redextent.core.generator.PixelmonNPCProvider;

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

        addShopKeeper(
                "info_dragon",                       // 文件名
                List.of("毛毛龙·晨曦"),              // NPC名称列表
                PixelmonNPCProvider.createTextTitle("毛毛龙的商店", "#FFD700", true, false, true),
                PixelmonNPCProvider.createTextMessage("欢迎来到毛毛龙商店，这里只有最稀有的商品！"),
                PixelmonNPCProvider.createTextMessage("感谢您的光临，期待再次为您服务！"),
                premiumItems,
                List.of("rem:textures/steve/info_dragon.png")
        );
    }
}
