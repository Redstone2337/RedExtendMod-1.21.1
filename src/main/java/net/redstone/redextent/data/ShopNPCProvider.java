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

        // 使用新的 addCustomShopNPC 方法
        addCustomShopNPC(
                "info_dragon",                       // 文件名
                List.of("毛毛龙·晨曦"),              // NPC名称列表
                "毛毛龙的商店",                      // 属性标题翻译键
                "商店交互",                          // 交互标题翻译键
                "欢迎来到毛毛龙商店，这里只有最稀有的商品！", // 问候语翻译键
                "感谢您的光临，期待再次为您服务！",   // 告别语翻译键
                premiumItems,                        // 商店物品列表
                List.of("rem:textures/steve/info_dragon.png") // 纹理资源
        );

        // 添加更多商店NPC示例
//        List<JsonObject> evolutionStones = List.of(
//                PixelmonNPCProvider.createEvolutionStoneItem("pixelmon:fire_stone", 5000.0, 1250.0),
//                PixelmonNPCProvider.createEvolutionStoneItem("pixelmon:water_stone", 5000.0, 1250.0),
//                PixelmonNPCProvider.createEvolutionStoneItem("pixelmon:thunder_stone", 5000.0, 1250.0),
//                PixelmonNPCProvider.createEvolutionStoneItem("pixelmon:leaf_stone", 5000.0, 1250.0),
//                PixelmonNPCProvider.createEvolutionStoneItem("pixelmon:moon_stone", 8000.0, 2000.0)
//        );

//        addEvolutionStoneShop(
//                "evolution_stone_shop",
//                List.of("石头商人·小刚"),
//                "进化石商店",
//                "进化石交易",
//                "需要进化石吗？我这里有各种进化石！",
//                "进化愉快！记得再来哦！",
//                evolutionStones,
//                List.of("rem:textures/steve/stone_merchant.png")
//        );
    }
}