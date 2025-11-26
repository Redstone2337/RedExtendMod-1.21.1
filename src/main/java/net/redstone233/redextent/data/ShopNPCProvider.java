package net.redstone233.redextent.data;

import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.redstone233.redextent.core.generator.PixelmonNPCProvider;
import net.redstone233.redextent.core.npc.NPCDefinition;

import java.util.List;

public class ShopNPCProvider extends PixelmonNPCProvider {
    public ShopNPCProvider(PackOutput output) {
        super(output, "pixelmon", "shop");
    }

    @Override
    public void registerNPCs() {
        // 只创建高级物品商店
        createPremiumShop();
    }

    /**
     * 创建高级物品商店
     */
    private void createPremiumShop() {
        // 创建商店物品 - 使用新的组件系统
        List<JsonObject> premiumItems = List.of(
                createMasterBallShopItem(1, 50000.0, 12500.0),           // 大师球
                createShopItem("pixelmon:rare_candy", 10, 50000.0, 12500.0), // 稀有糖果
                createUltraBallShopItem(4, 15000.0, 3750.0),             // 高级球
                createGreatBallShopItem(8, 8000.0, 2000.0),              // 超级球
                createQuickBallShopItem(4, 12000.0, 3000.0),             // 先机球
                createRepeatBallShopItem(4, 12000.0, 3000.0),            // 重复球
                createTimerBallShopItem(4, 12000.0, 3000.0),             // 计时球
                createDuskBallShopItem(4, 12000.0, 3000.0),              // 黑暗球
                createNetBallShopItem(4, 12000.0, 3000.0),               // 捕网球
                createDiveBallShopItem(4, 12000.0, 3000.0),              // 潜水球
                createNestBallShopItem(4, 10000.0, 2500.0),              // 巢穴球
                createHealBallShopItem(4, 8000.0, 2000.0),               // 治愈球
                createLuxuryBallShopItem(4, 15000.0, 3750.0),            // 豪华球
                createPremierBallShopItem(4, 10000.0, 2500.0),           // Premier Ball
                createShopItem("pixelmon:ability_capsule", 1, 10000.0, 2500.0), // 特性胶囊
                createShopItem("pixelmon:ability_patch", 1, 20000.0, 5000.0) // 特性膏药
        );

        // 创建商店交互
        JsonObject shopInteraction = createShopInteraction(
                "高级物品商店",
                "欢迎来到高级物品商店！这里出售各种稀有道具和精灵球。",
                "高级物品商店",
                "感谢您的惠顾！欢迎下次光临！",
                premiumItems
        );

        // 使用基础方法生成商店NPC
        JsonObject title = createTextTitle(
                "高级物品商店",
                "#FFD700", true, false, false
        );

        List<NPCDefinition.PlayerModel> models = List.of(
                createPlayerModel(false, "rem:textures/steve/info_dragon.png")
        );

        JsonObject npcDefinition = definition()
                .withType(NPCDefinition.NPCType.SHOP) // 设置NPC类型为商店
                .withTitledProperties(20.0f, 1.9f, 0.65f, 2.0f,
                        "高级物品商店", "#FFD700", true, false, false,
                        false, false, false, false, true)
                .withSingleName("毛毛龙·晨曦")
                .withEmptyParty()
                .withRandomPlayerModels(models)
                .withStandAndLookAI(5.0f, false)
                .withConstantInteractions(shopInteraction)
                .build()
                .serialize();

        this.add("premium_shopkeeper", npcDefinition);
    }

    // ==================== 精灵球商店物品创建方法 ====================

    /**
     * 创建先机球商店物品
     */
    public static JsonObject createQuickBallShopItem(int count, double buyPrice, double sellPrice) {
        return createPokeBallShopItem("quick_ball", count, buyPrice, sellPrice);
    }

    /**
     * 创建重复球商店物品
     */
    public static JsonObject createRepeatBallShopItem(int count, double buyPrice, double sellPrice) {
        return createPokeBallShopItem("repeat_ball", count, buyPrice, sellPrice);
    }

    /**
     * 创建计时球商店物品
     */
    public static JsonObject createTimerBallShopItem(int count, double buyPrice, double sellPrice) {
        return createPokeBallShopItem("timer_ball", count, buyPrice, sellPrice);
    }

    /**
     * 创建黑暗球商店物品
     */
    public static JsonObject createDuskBallShopItem(int count, double buyPrice, double sellPrice) {
        return createPokeBallShopItem("dusk_ball", count, buyPrice, sellPrice);
    }

    /**
     * 创建捕网球商店物品
     */
    public static JsonObject createNetBallShopItem(int count, double buyPrice, double sellPrice) {
        return createPokeBallShopItem("net_ball", count, buyPrice, sellPrice);
    }

    /**
     * 创建潜水球商店物品
     */
    public static JsonObject createDiveBallShopItem(int count, double buyPrice, double sellPrice) {
        return createPokeBallShopItem("dive_ball", count, buyPrice, sellPrice);
    }

    /**
     * 创建巢穴球商店物品
     */
    public static JsonObject createNestBallShopItem(int count, double buyPrice, double sellPrice) {
        return createPokeBallShopItem("nest_ball", count, buyPrice, sellPrice);
    }

    /**
     * 创建治愈球商店物品
     */
    public static JsonObject createHealBallShopItem(int count, double buyPrice, double sellPrice) {
        return createPokeBallShopItem("heal_ball", count, buyPrice, sellPrice);
    }

    /**
     * 创建豪华球商店物品
     */
    public static JsonObject createLuxuryBallShopItem(int count, double buyPrice, double sellPrice) {
        return createPokeBallShopItem("luxury_ball", count, buyPrice, sellPrice);
    }

    /**
     * 创建 Premier Ball 商店物品
     */
    public static JsonObject createPremierBallShopItem(int count, double buyPrice, double sellPrice) {
        return createPokeBallShopItem("premier_ball", count, buyPrice, sellPrice);
    }
}