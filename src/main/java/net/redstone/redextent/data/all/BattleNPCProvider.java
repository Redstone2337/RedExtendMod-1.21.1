package net.redstone.redextent.data.all;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.redstone.redextent.core.generator.PixelmonNPCProvider;
import net.redstone.redextent.core.npc.NpcPresetBuilder;
import net.redstone.redextent.core.npc.NpcPresetBuilder.*;
import net.redstone.redextent.core.codecs.NpcDefinitionCodec;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class BattleNPCProvider extends PixelmonNPCProvider {
    public BattleNPCProvider(PackOutput output) {
        super(output, "pixelmon", "shopper");
    }

    @Override
    protected void registerNpcs(BiConsumer<String, NpcDefinitionCodec> consumer) {
        // ==================== 商店NPC ====================
        registerShopNpcs(consumer);

        // ==================== 提示NPC ====================
        registerTipNpcs(consumer);

        // ==================== 馆主：蓝龙 ====================
        createBlueDragonGymLeader(consumer);

        // ==================== 四位实习道馆训练家 ====================
        createFireLegendaryTrainer(consumer);
        createWaterLegendaryTrainer(consumer);
        createElectricLegendaryTrainer(consumer);
        createDragonLegendaryTrainer(consumer);
    }

    /**
     * 注册商店NPC
     */
    private void registerShopNpcs(BiConsumer<String, NpcDefinitionCodec> consumer) {
        // 创建商店物品列表
        List<ShopItem> shopItems = List.of(
            ShopItem.of("pixelmon:master_ball", 1, 50000.0, 12500.0),
            ShopItem.of("pixelmon:rare_candy", 10, 50000.0, 12500.0),
            ShopItem.of("pixelmon:park_ball", 1, 30000.0, 15000.0),
            ShopItem.of("pixelmon:ability_capsule", 1, 10000.0, 2500.0)
        );

        // 创建文本内容
        TextContent title = TextContent.literal("毛毛龙的商店");
        TextContent greeting = TextContent.literal("欢迎来到毛毛龙商店，这里只有最稀有的商品！");
        TextContent goodbye = TextContent.literal("感谢您的光临，期待再次为您服务！");

        // 纹理资源
        List<ResourceLocation> textures = List.of(
            ResourceLocation.parse("rem:textures/steve/info_dragon.png")
        );

        // 创建商店NPC
        NpcTemplate shopNpc = NpcPresetBuilder.createShopNpc(
            "info_dragon",
            List.of("毛毛龙·晨曦"),
            textures,
            title,
            greeting,
            goodbye,
            shopItems,
            false,
            false
        );

        consumer.accept("info_dragon", shopNpc.build());
    }

    /**
     * 注册提示NPC
     */
    private void registerTipNpcs(BiConsumer<String, NpcDefinitionCodec> consumer) {
        // 创建对话页面
        List<TextContent> dialoguePages = List.of(
            TextContent.literal("欢迎来到我们的像素精灵世界！"),
            TextContent.literal("探索、战斗、培育，体验前所未有的冒险！"),
            TextContent.literal("记得定期检查我们的更新，获取最新内容和活动信息！")
        );

        // 创建标题
        TextContent title = TextContent.literal("模组宣传");

        // 纹理资源
        List<ResourceLocation> textures = List.of(
            ResourceLocation.parse("rem:textures/steve/fire_dragon.png"),
            ResourceLocation.parse("rem:textures/steve/gwenthe_dragon.png"),
            ResourceLocation.parse("rem:textures/steve/magic_dragon_electricity.png")
        );

        // 创建提示NPC
        NpcTemplate tipNpc = NpcPresetBuilder.createChatNpc(
            "publicity_ambassador",
            List.of("Fire Dragon Kael", "Lu Ming Fei", "Magic Dragon An Ye"),
            textures,
            title,
            dialoguePages,
            false,
            false
        );

        consumer.accept("publicity_ambassador", tipNpc.build());
    }

    /**
     * 创建馆主：蓝龙（神兽道馆馆主）
     */
    private void createBlueDragonGymLeader(BiConsumer<String, NpcDefinitionCodec> consumer) {
        TextContent title = TextContent.literal("神兽道馆馆主·蓝龙");
        TextContent winMessage = TextContent.literal("不可思议！你竟然战胜了我的神兽队伍！这是神兽徽章，你值得拥有！");
        TextContent loseMessage = TextContent.literal("神兽的力量果然不是那么容易挑战的，继续努力吧！");
        TextContent cooldownMessage = TextContent.literal("你已经挑战过我了，明天再来吧！");

        // 蓝龙的队伍 - 全是神兽，等级90-100
        List<String> blueDragonTeam = List.of(
                // 基格尔德（配三个专属技能）
                createPokemonSpecWithIVsEVs("Zygarde", 100, "Power Construct", "Leftovers", "Adamant",
                        List.of("Thousand Arrows", "Thousand Waves", "Core Enforcer", "Extreme Speed"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 252, "atk", 252, "def", 4, "spatk", 0, "spdef", 0, "spd", 0)),

                // 烈空坐
                createPokemonSpecWithIVsEVs("Rayquaza", 98, "Air Lock", "Life Orb", "Jolly",
                        List.of("Dragon Ascent", "Extreme Speed", "Dragon Dance", "Earthquake"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 4, "spatk", 0, "spdef", 0, "spd", 252)),

                // 超梦
                createPokemonSpecWithIVsEVs("Mewtwo", 96, "Pressure", "Mewtwonite X", "Timid",
                        List.of("Psystrike", "Aura Sphere", "Ice Beam", "Recover"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 4, "spatk", 252, "spdef", 0, "spd", 252)),

                // 帝牙卢卡
                createPokemonSpecWithIVsEVs("Dialga", 95, "Pressure", "Adamant Orb", "Modest",
                        List.of("Roar of Time", "Flash Cannon", "Fire Blast", "Thunder"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 252, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 0)),

                // 帕路奇亚
                createPokemonSpecWithIVsEVs("Palkia", 94, "Pressure", "Lustrous Orb", "Timid",
                        List.of("Spacial Rend", "Hydro Pump", "Thunderbolt", "Fire Blast"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                // 骑拉帝纳（起源形态）
                createPokemonSpecWithIVsEVs("Giratina", 92, "Levitate", "Griseous Orb", "Modest",
                        List.of("Shadow Force", "Dragon Pulse", "Aura Sphere", "Earth Power"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 252, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 0))
        );

        List<RewardItem> rewardItems = List.of(
                RewardItem.of("pixelmon:master_ball", 2),
                RewardItem.of("pixelmon:rare_candy", 10)
        );

        NpcTemplate gymLeader = NpcPresetBuilder.createGymLeader(
            "blue_dragon_leader",
            "蓝龙",
            ResourceLocation.parse("rem:textures/npc/battle/blue_dragon.png"),
            blueDragonTeam,
            title,
            winMessage,
            loseMessage,
            cooldownMessage,
            10000.0,
            rewardItems,
            "blue_dragon_leader"
        );

        consumer.accept(gymLeader.name(), gymLeader.build());
    }

    /**
     * 创建实习训练家：火焰神兽训练家
     */
    private void createFireLegendaryTrainer(BiConsumer<String, NpcDefinitionCodec> consumer) {
        TextContent title = TextContent.literal("火焰神兽训练家·炎煌");
        TextContent winMessage = TextContent.literal("你的实力让我印象深刻！继续前进吧！");
        TextContent loseMessage = TextContent.literal("火焰的力量果然还是最强的！");
        TextContent cooldownMessage = TextContent.literal("你今天已经挑战过我了，明天再来吧！");

        // 火焰神兽队伍 - 等级70-90
        List<String> fireTeam = List.of(
                // 固拉多（原始回归）
                createPokemonSpecWithIVsEVs("Groudon", 88, "Drought", "Red Orb", "Adamant",
                        List.of("Precipice Blades", "Fire Punch", "Rock Slide", "Swords Dance"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252)),

                // 凤王
                createPokemonSpecWithIVsEVs("Ho-Oh", 85, "Pressure", "Life Orb", "Adamant",
                        List.of("Sacred Fire", "Brave Bird", "Earthquake", "Recover"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 248, "atk", 252, "def", 0, "spatk", 0, "spdef", 8, "spd", 0)),

                // 火焰鸟
                createPokemonSpecWithIVsEVs("Moltres", 82, "Pressure", "Heavy-Duty Boots", "Timid",
                        List.of("Hurricane", "Fire Blast", "Roost", "U-turn"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                // 炎帝
                createPokemonSpecWithIVsEVs("Entei", 80, "Pressure", "Choice Band", "Adamant",
                        List.of("Sacred Fire", "Extreme Speed", "Stone Edge", "Iron Head"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252)),

                // 莱希拉姆
                createPokemonSpecWithIVsEVs("Reshiram", 78, "Turboblaze", "Choice Scarf", "Modest",
                        List.of("Blue Flare", "Draco Meteor", "Focus Blast", "Stone Edge"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                // 火焰鸡（Mega进化）
                createPokemonSpecWithIVsEVs("Blaziken", 75, "Speed Boost", "Blazikenite", "Adamant",
                        List.of("Flare Blitz", "High Jump Kick", "Stone Edge", "Protect"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252))
        );

        List<RewardItem> rewardItems = List.of(
                RewardItem.of("pixelmon:fire_stone", 1),
                RewardItem.of("pixelmon:rare_candy", 5)
        );

        NpcTemplate trainer = NpcPresetBuilder.createGymLeader(
            "fire_legendary_trainer",
            "炎煌",
            ResourceLocation.parse("rem:textures/npc/battle/eric.png"),
            fireTeam,
            title,
            winMessage,
            loseMessage,
            cooldownMessage,
            5000.0,
            rewardItems,
            "fire_legendary_trainer"
        );

        consumer.accept(trainer.name(), trainer.build());
    }

    /**
     * 创建实习训练家：水系神兽训练家
     */
    private void createWaterLegendaryTrainer(BiConsumer<String, NpcDefinitionCodec> consumer) {
        TextContent title = TextContent.literal("水系神兽训练家·海澜");
        TextContent winMessage = TextContent.literal("你的实力如海洋般深不可测！继续前进吧！");
        TextContent loseMessage = TextContent.literal("大海的力量是无穷无尽的！");
        TextContent cooldownMessage = TextContent.literal("你今天已经挑战过我了，明天再来吧！");

        // 水系神兽队伍 - 等级70-90
        List<String> waterTeam = List.of(
                // 盖欧卡（原始回归）
                createPokemonSpecWithIVsEVs("Kyogre", 87, "Drizzle", "Blue Orb", "Modest",
                        List.of("Origin Pulse", "Water Spout", "Thunder", "Ice Beam"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 4, "atk", 0, "def", 0, "spatk", 252, "spdef", 0, "spd", 252)),

                // 洛奇亚
                createPokemonSpecWithIVsEVs("Lugia", 84, "Pressure", "Leftovers", "Bold",
                        List.of("Aeroblast", "Calm Mind", "Roost", "Ice Beam"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 252, "atk", 0, "def", 252, "spatk", 0, "spdef", 4, "spd", 0)),

                // 急冻鸟
                createPokemonSpecWithIVsEVs("Articuno", 81, "Pressure", "Heavy-Duty Boots", "Calm",
                        List.of("Freeze-Dry", "Hurricane", "Roost", "Toxic"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 248, "atk", 0, "def", 0, "spatk", 0, "spdef", 252, "spd", 8)),

                // 水君
                createPokemonSpecWithIVsEVs("Suicune", 79, "Pressure", "Leftovers", "Bold",
                        List.of("Scald", "Ice Beam", "Calm Mind", "Rest"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 252, "atk", 0, "def", 252, "spatk", 0, "spdef", 4, "spd", 0)),

                // 捷克罗姆
                createPokemonSpecWithIVsEVs("Zekrom", 77, "Teravolt", "Choice Band", "Adamant",
                        List.of("Bolt Strike", "Outrage", "Stone Edge", "Volt Switch"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252)),

                // 巨沼怪（Mega进化）
                createPokemonSpecWithIVsEVs("Swampert", 74, "Swift Swim", "Swampertite", "Adamant",
                        List.of("Waterfall", "Earthquake", "Ice Punch", "Rain Dance"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252))
        );

        List<RewardItem> rewardItems = List.of(
                RewardItem.of("pixelmon:water_stone", 1),
                RewardItem.of("pixelmon:rare_candy", 5)
        );

        NpcTemplate trainer = NpcPresetBuilder.createGymLeader(
            "water_legendary_trainer",
            "海澜",
            ResourceLocation.parse("rem:textures/npc/battle/ice_dragon.png"),
            waterTeam,
            title,
            winMessage,
            loseMessage,
            cooldownMessage,
            5000.0,
            rewardItems,
            "water_legendary_trainer"
        );

        consumer.accept(trainer.name(), trainer.build());
    }

    /**
     * 创建实习训练家：电系神兽训练家
     */
    private void createElectricLegendaryTrainer(BiConsumer<String, NpcDefinitionCodec> consumer) {
        TextContent title = TextContent.literal("电系神兽训练家·雷霆");
        TextContent winMessage = TextContent.literal("你的实力如闪电般耀眼！继续前进吧！");
        TextContent loseMessage = TextContent.literal("电力的力量无人能挡！");
        TextContent cooldownMessage = TextContent.literal("你今天已经挑战过我了，明天再来吧！");

        // 电系神兽队伍 - 等级70-90
        List<String> electricTeam = List.of(
                // 雷公
                createPokemonSpecWithIVsEVs("Raikou", 86, "Pressure", "Assault Vest", "Timid",
                        List.of("Thunderbolt", "Volt Switch", "Shadow Ball", "Extrasensory"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                // 闪电鸟
                createPokemonSpecWithIVsEVs("Zapdos", 83, "Pressure", "Heavy-Duty Boots", "Timid",
                        List.of("Thunderbolt", "Hurricane", "Heat Wave", "Roost"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                // 雷电云
                createPokemonSpecWithIVsEVs("Thundurus", 80, "Prankster", "Life Orb", "Timid",
                        List.of("Thunderbolt", "Focus Blast", "Hidden Power Ice", "Nasty Plot"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                // 卡璞·鸣鸣
                createPokemonSpecWithIVsEVs("Tapu Koko", 78, "Electric Surge", "Choice Specs", "Timid",
                        List.of("Thunderbolt", "Dazzling Gleam", "Volt Switch", "Grass Knot"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                // 捷拉奥拉
                createPokemonSpecWithIVsEVs("Zeraora", 76, "Volt Absorb", "Expert Belt", "Jolly",
                        List.of("Plasma Fists", "Close Combat", "Blaze Kick", "Volt Switch"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252)),

                // 电龙（Mega进化）
                createPokemonSpecWithIVsEVs("Ampharos", 73, "Mold Breaker", "Ampharosite", "Modest",
                        List.of("Thunderbolt", "Dragon Pulse", "Focus Blast", "Volt Switch"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 248, "atk", 0, "def", 0, "spatk", 252, "spdef", 8, "spd", 0))
        );

        List<RewardItem> rewardItems = List.of(
                RewardItem.of("pixelmon:thunder_stone", 1),
                RewardItem.of("pixelmon:rare_candy", 5)
        );

        NpcTemplate trainer = NpcPresetBuilder.createGymLeader(
            "electric_legendary_trainer",
            "雷霆",
            ResourceLocation.parse("rem:textures/npc/battle/ender_dragon.png"),
            electricTeam,
            title,
            winMessage,
            loseMessage,
            cooldownMessage,
            5000.0,
            rewardItems,
            "electric_legendary_trainer"
        );

        consumer.accept(trainer.name(), trainer.build());
    }

    /**
     * 创建实习训练家：龙系神兽训练家
     */
    private void createDragonLegendaryTrainer(BiConsumer<String, NpcDefinitionCodec> consumer) {
        TextContent title = TextContent.literal("龙系神兽训练家·龙啸");
        TextContent winMessage = TextContent.literal("你的实力如同传说中的龙骑士！继续前进吧！");
        TextContent loseMessage = TextContent.literal("龙族的力量永远是最强的！");
        TextContent cooldownMessage = TextContent.literal("你今天已经挑战过我了，明天再来吧！");

        // 龙系神兽队伍 - 等级70-90
        List<String> dragonTeam = List.of(
                // 酋雷姆
                createPokemonSpecWithIVsEVs("Kyurem", 89, "Pressure", "Leftovers", "Timid",
                        List.of("Ice Beam", "Freeze-Dry", "Earth Power", "Roost"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 56, "atk", 0, "def", 0, "spatk", 200, "spdef", 0, "spd", 252)),

                // 拉帝亚斯
                createPokemonSpecWithIVsEVs("Latias", 85, "Levitate", "Soul Dew", "Timid",
                        List.of("Draco Meteor", "Psyshock", "Recover", "Calm Mind"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                // 拉帝欧斯
                createPokemonSpecWithIVsEVs("Latios", 85, "Levitate", "Soul Dew", "Timid",
                        List.of("Draco Meteor", "Psyshock", "Recover", "Calm Mind"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                // 三首恶龙
                createPokemonSpecWithIVsEVs("Hydreigon", 82, "Levitate", "Choice Scarf", "Timid",
                        List.of("Draco Meteor", "Dark Pulse", "Fire Blast", "U-turn"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                // 暴飞龙（Mega进化）
                createPokemonSpecWithIVsEVs("Salamence", 79, "Aerilate", "Salamencite", "Jolly",
                        List.of("Return", "Dragon Dance", "Roost", "Earthquake"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252)),

                // 多龙巴鲁托
                createPokemonSpecWithIVsEVs("Dragapult", 76, "Infiltrator", "Choice Band", "Jolly",
                        List.of("Dragon Darts", "Phantom Force", "U-turn", "Sucker Punch"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252))
        );

        List<RewardItem> rewardItems = List.of(
                RewardItem.of("pixelmon:dragon_scale", 1),
                RewardItem.of("pixelmon:rare_candy", 5)
        );

        NpcTemplate trainer = NpcPresetBuilder.createGymLeader(
            "dragon_legendary_trainer",
            "龙啸",
            ResourceLocation.parse("pixelmon:textures/npc/gym_leaders/dragon_trainer.png"),
            dragonTeam,
            title,
            winMessage,
            loseMessage,
            cooldownMessage,
            5000.0,
            rewardItems,
            "dragon_legendary_trainer"
        );

        consumer.accept(trainer.name(), trainer.build());
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建带个体值和努力值的宝可梦配置
     */
    private String createPokemonSpecWithIVsEVs(String pokemon, int level, String ability, String heldItem,
                                               String nature, List<String> moves, Map<String, Integer> ivs,
                                               Map<String, Integer> evs) {
        StringBuilder spec = new StringBuilder(pokemon);
        spec.append(" lvl:").append(level);

        if (ability != null && !ability.isEmpty()) {
            spec.append(" ability:").append(ability);
        }

        if (heldItem != null && !heldItem.isEmpty()) {
            spec.append(" helditem:").append(heldItem);
        }

        if (nature != null && !nature.isEmpty()) {
            spec.append(" nature:").append(nature);
        }

        // 添加个体值
        for (Map.Entry<String, Integer> entry : ivs.entrySet()) {
            spec.append(" iv").append(entry.getKey()).append(":").append(entry.getValue());
        }

        // 添加努力值
        for (Map.Entry<String, Integer> entry : evs.entrySet()) {
            spec.append(" ev").append(entry.getKey()).append(":").append(entry.getValue());
        }

        // 添加招式
        for (int i = 0; i < moves.size() && i < 4; i++) {
            spec.append(" move").append(i + 1).append(":").append(moves.get(i));
        }

        return spec.toString();
    }
          }
