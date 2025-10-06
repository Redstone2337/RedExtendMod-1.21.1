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
 * 商店NPC数据提供者 - 专门生成商店NPC
 */
public class ShopNPCProvider extends PixelmonNPCProvider {
    
    public ShopNPCProvider(PackOutput output) {
        super(output, "pixelmon", "shopper");
    }

    @Override
    public void registerNPCs() {
        createPremiumShopKeeper();
    }

    /**
     * 创建高级商店NPC - 售卖稀有物品
     */
    private void createPremiumShopKeeper() {
        TextContent title = TextContent.literal("毛毛龙的商店");
        TextContent greeting = TextContent.literal("欢迎来到毛毛龙商店，这里只有最稀有的商品！");
        TextContent goodbye = TextContent.literal("感谢您的光临，期待再次为您服务！");

        List<ShopItem> premiumItems = List.of(
                ShopItem.of("pixelmon:master_ball", 1, 50000.0, 12500.0),
                ShopItem.of("pixelmon:rare_candy", 10, 50000.0, 12500.0),
                ShopItem.of("pixelmon:park_ball", 1, 30000.0, 15000.0),
                ShopItem.of("pixelmon:ability_capsule", 1, 10000.0, 2500.0)
        );

        Map<String, Object> shopProperties = Map.of(
            "greeting", greeting,
            "goodbye", goodbye,
            "shopItems", premiumItems,
            "randomName", false,
            "randomModel", false
        );

        NpcTemplate shopKeeper = NpcPresetBuilder.createNpcByType(
            "info_dragon",
            NPCType.SHOPKEEPER,
            List.of("毛毛龙·晨曦"),
            List.of(ResourceLocation.parse("rem:textures/steve/info_dragon.png")),
            title,
            shopProperties
        );

        addNPC(shopKeeper);
    }
}