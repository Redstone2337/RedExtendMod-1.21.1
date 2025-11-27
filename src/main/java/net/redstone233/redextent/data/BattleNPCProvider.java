package net.redstone233.redextent.data;

import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.redstone233.redextent.core.generator.PixelmonNPCProvider;
import net.redstone233.redextent.core.npc.NPCDefinition;

import java.util.List;
import java.util.Map;

public class BattleNPCProvider extends PixelmonNPCProvider {
    public BattleNPCProvider(PackOutput output) {
        super(output, "pixelmon", "battle");
    }

    @Override
    public void registerNPCs() {
        // ==================== 馆主：蓝龙 ====================
        createBlueDragonGymLeader();

        // ==================== 四位实习道馆训练家 ====================
        createFireLegendaryTrainer();
        createWaterLegendaryTrainer();
        createElectricLegendaryTrainer();
        createDragonLegendaryTrainer();
    }

    /**
     * 创建馆主：蓝龙（神兽道馆馆主）
     */
    private void createBlueDragonGymLeader() {
        // 使用纯字符串创建标题和消息
        String title = "神兽道馆馆主·蓝龙";
        String greeting = "欢迎来到神兽道馆！我是馆主蓝龙。准备好面对传说中的宝可梦了吗？";
        String winMessage = "不可思议！你竟然战胜了我的神兽队伍！这是神兽徽章，你值得拥有！";
        String loseMessage = "神兽的力量果然不是那么容易挑战的，继续努力吧！";

        List<String> blueDragonTeam = List.of(
                createPokemonSpecWithIVsEVs("Zygarde", 100, "Power Construct", "leftovers", "adamant",
                        List.of("Thousand Arrows", "Thousand Waves", "Core Enforcer", "Extreme Speed"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 252, "atk", 252, "def", 4, "spatk", 0, "spdef", 0, "spd", 0)),

                createPokemonSpecWithIVsEVs("Rayquaza", 98, "Air Lock", "life_orb", "jolly",
                        List.of("Dragon Ascent", "Extreme Speed", "Dragon Dance", "Earthquake"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 4, "spatk", 0, "spdef", 0, "spd", 252)),

                createPokemonSpecWithIVsEVs("Mewtwo", 96, "Pressure", "mewtwonite_x", "timid",
                        List.of("Psystrike", "Aura Sphere", "Ice Beam", "Recover"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 4, "spatk", 252, "spdef", 0, "spd", 252)),

                createPokemonSpecWithIVsEVs("Dialga", 95, "Pressure", "adamant_orb", "modest",
                        List.of("Roar of Time", "Flash Cannon", "Fire Blast", "Thunder"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 252, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 0)),

                createPokemonSpecWithIVsEVs("Palkia", 94, "Pressure", "lustrous_orb", "timid",
                        List.of("Spacial Rend", "Hydro Pump", "Thunderbolt", "Fire Blast"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Giratina", 92, "Levitate", "griseous_orb", "modest",
                        List.of("Shadow Force", "Dragon Pulse", "Aura Sphere", "Earth Power"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 252, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 0))
        );

        // 使用纯字符串版本的馆主NPC，指定类型为GYM_LEADER
        addGymLeaderString(
                "blue_dragon_leader",          // 文件名
                "蓝龙",                        // NPC名称
                title,                         // 标题（纯字符串）
                greeting,                      // 问候语（纯字符串）
                winMessage,                    // 胜利消息（纯字符串）
                loseMessage,                   // 失败消息（纯字符串）
                blueDragonTeam,                // 神兽队伍配置
                10000.0,                       // 奖励金钱
                List.of(                       // 奖励物品
                        createMasterBallReward(2),
                        createItemReward("pixelmon:rare_candy", 10)
                ),
                3,                             // 冷却天数
                "rem:textures/npc/battle/blue_dragon.png", // 纹理路径
                NPCDefinition.NPCType.GYM_LEADER // 指定NPC类型
        );
    }

    /**
     * 创建实习训练家：火焰神兽训练家
     */
    private void createFireLegendaryTrainer() {
        String title = "火焰神兽训练家·炎煌";
        String greeting = "我是火焰神兽训练家炎煌！让火焰净化一切吧！";
        String winMessage = "你的实力让我印象深刻！继续前进吧！";
        String loseMessage = "火焰的力量果然还是最强的！";

        List<String> fireTeam = List.of(
                createPokemonSpecWithIVsEVs("Groudon", 88, "Drought", "red_orb", "adamant",
                        List.of("Precipice Blades", "Fire Punch", "Rock Slide", "Swords Dance"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Ho-Oh", 85, "Pressure", "life_orb", "adamant",
                        List.of("Sacred Fire", "Brave Bird", "Earthquake", "Recover"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 248, "atk", 252, "def", 0, "spatk", 0, "spdef", 8, "spd", 0)),

                createPokemonSpecWithIVsEVs("Moltres", 82, "Pressure", "heavy_duty_boots", "timid",
                        List.of("Hurricane", "Fire Blast", "Roost", "U-turn"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Entei", 80, "Pressure", "choice_band", "adamant",
                        List.of("Sacred Fire", "Extreme Speed", "Stone Edge", "Iron Head"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Reshiram", 78, "Turboblaze", "choice_scarf", "modest",
                        List.of("Blue Flare", "Draco Meteor", "Focus Blast", "Stone Edge"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Blaziken", 75, "Speed Boost", "blazikenite", "adamant",
                        List.of("Flare Blitz", "High Jump Kick", "Stone Edge", "Protect"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252))
        );

        addGymLeaderString(
                "fire_legendary_trainer",      // 文件名
                "炎煌",                        // NPC名称
                title,                         // 标题（纯字符串）
                greeting,                      // 问候语（纯字符串）
                winMessage,                    // 胜利消息（纯字符串）
                loseMessage,                   // 失败消息（纯字符串）
                fireTeam,                      // 神兽队伍配置
                5000.0,                        // 奖励金钱
                List.of(                       // 奖励物品
                        createItemReward("pixelmon:fire_stone", 1),
                        createItemReward("pixelmon:rare_candy", 5)
                ),
                2,                             // 冷却天数
                "rem:textures/npc/battle/eric.png", // 纹理路径
                NPCDefinition.NPCType.GYM_LEADER // 指定NPC类型
        );
    }

    /**
     * 创建实习训练家：水系神兽训练家
     */
    private void createWaterLegendaryTrainer() {
        String title = "水系神兽训练家·海澜";
        String greeting = "我是水系神兽训练家海澜！感受大海的力量吧！";
        String winMessage = "你的实力如海洋般深不可测！继续前进吧！";
        String loseMessage = "大海的力量是无穷无尽的！";

        List<String> waterTeam = List.of(
                createPokemonSpecWithIVsEVs("Kyogre", 87, "Drizzle", "blue_orb", "modest",
                        List.of("Origin Pulse", "Water Spout", "Thunder", "Ice Beam"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 4, "atk", 0, "def", 0, "spatk", 252, "spdef", 0, "spd", 252)),

                createPokemonSpecWithIVsEVs("Lugia", 84, "Pressure", "leftovers", "bold",
                        List.of("Aeroblast", "Calm Mind", "Roost", "Ice Beam"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 252, "atk", 0, "def", 252, "spatk", 0, "spdef", 4, "spd", 0)),

                createPokemonSpecWithIVsEVs("Articuno", 81, "Pressure", "heavy_duty_boots", "calm",
                        List.of("Freeze-Dry", "Hurricane", "Roost", "Toxic"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 248, "atk", 0, "def", 0, "spatk", 0, "spdef", 252, "spd", 8)),

                createPokemonSpecWithIVsEVs("Suicune", 79, "Pressure", "leftovers", "bold",
                        List.of("Scald", "Ice Beam", "Calm Mind", "Rest"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 252, "atk", 0, "def", 252, "spatk", 0, "spdef", 4, "spd", 0)),

                createPokemonSpecWithIVsEVs("Zekrom", 77, "Teravolt", "choice_band", "adamant",
                        List.of("Bolt Strike", "Outrage", "Stone Edge", "Volt Switch"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Swampert", 74, "Swift Swim", "swampertite", "adamant",
                        List.of("Waterfall", "Earthquake", "Ice Punch", "Rain Dance"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252))
        );

        addGymLeaderString(
                "water_legendary_trainer",     // 文件名
                "海澜",                        // NPC名称
                title,                         // 标题（纯字符串）
                greeting,                      // 问候语（纯字符串）
                winMessage,                    // 胜利消息（纯字符串）
                loseMessage,                   // 失败消息（纯字符串）
                waterTeam,                     // 神兽队伍配置
                5000.0,                        // 奖励金钱
                List.of(                       // 奖励物品
                        createItemReward("pixelmon:water_stone", 1),
                        createItemReward("pixelmon:rare_candy", 5)
                ),
                2,                             // 冷却天数
                "rem:textures/npc/battle/ice_dragon.png", // 纹理路径
                NPCDefinition.NPCType.GYM_LEADER // 指定NPC类型
        );
    }

    /**
     * 创建实习训练家：电系神兽训练家
     */
    private void createElectricLegendaryTrainer() {
        String title = "电系神兽训练家·雷霆";
        String greeting = "我是电系神兽训练家雷霆！准备好被电击了吗？";
        String winMessage = "你的实力如闪电般耀眼！继续前进吧！";
        String loseMessage = "电力的力量无人能挡！";

        List<String> electricTeam = List.of(
                createPokemonSpecWithIVsEVs("Raikou", 86, "Pressure", "assault_vest", "timid",
                        List.of("Thunderbolt", "Volt Switch", "Shadow Ball", "Extrasensory"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Zapdos", 83, "Pressure", "heavy_duty_boots", "timid",
                        List.of("Thunderbolt", "Hurricane", "Heat Wave", "Roost"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Thundurus", 80, "Prankster", "life_orb", "timid",
                        List.of("Thunderbolt", "Focus Blast", "Hidden Power Ice", "Nasty Plot"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Tapu Koko", 78, "Electric Surge", "choice_specs", "timid",
                        List.of("Thunderbolt", "Dazzling Gleam", "Volt Switch", "Grass Knot"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Zeraora", 76, "Volt Absorb", "expert_belt", "jolly",
                        List.of("Plasma Fists", "Close Combat", "Blaze Kick", "Volt Switch"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Ampharos", 73, "Mold Breaker", "ampharosite", "modest",
                        List.of("Thunderbolt", "Dragon Pulse", "Focus Blast", "Volt Switch"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 248, "atk", 0, "def", 0, "spatk", 252, "spdef", 8, "spd", 0))
        );

        addGymLeaderString(
                "electric_legendary_trainer",  // 文件名
                "雷霆",                        // NPC名称
                title,                         // 标题（纯字符串）
                greeting,                      // 问候语（纯字符串）
                winMessage,                    // 胜利消息（纯字符串）
                loseMessage,                   // 失败消息（纯字符串）
                electricTeam,                  // 神兽队伍配置
                5000.0,                        // 奖励金钱
                List.of(                       // 奖励物品
                        createItemReward("pixelmon:thunder_stone", 1),
                        createItemReward("pixelmon:rare_candy", 5)
                ),
                2,                             // 冷却天数
                "rem:textures/npc/battle/ender_dragon.png", // 纹理路径
                NPCDefinition.NPCType.GYM_LEADER // 指定NPC类型
        );
    }

    /**
     * 创建实习训练家：龙系神兽训练家
     */
    private void createDragonLegendaryTrainer() {
        String title = "龙系神兽训练家·龙啸";
        String greeting = "我是龙系神兽训练家龙啸！感受龙之怒吧！";
        String winMessage = "你的实力如同传说中的龙骑士！继续前进吧！";
        String loseMessage = "龙族的力量永远是最强的！";

        List<String> dragonTeam = List.of(
                createPokemonSpecWithIVsEVs("Kyurem", 89, "Pressure", "leftovers", "timid",
                        List.of("Ice Beam", "Freeze-Dry", "Earth Power", "Roost"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 56, "atk", 0, "def", 0, "spatk", 200, "spdef", 0, "spd", 252)),

                createPokemonSpecWithIVsEVs("Latias", 85, "Levitate", "soul_dew", "timid",
                        List.of("Draco Meteor", "Psyshock", "Recover", "Calm Mind"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Latios", 85, "Levitate", "soul_dew", "timid",
                        List.of("Draco Meteor", "Psyshock", "Recover", "Calm Mind"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Hydreigon", 82, "Levitate", "choice_scarf", "timid",
                        List.of("Draco Meteor", "Dark Pulse", "Fire Blast", "U-turn"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Salamence", 79, "Aerilate", "salamencite", "jolly",
                        List.of("Return", "Dragon Dance", "Roost", "Earthquake"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Dragapult", 76, "Infiltrator", "choice_band", "jolly",
                        List.of("Dragon Darts", "Phantom Force", "U-turn", "Sucker Punch"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252))
        );

        addGymLeaderString(
                "dragon_legendary_trainer",    // 文件名
                "龙啸",                        // NPC名称
                title,                         // 标题（纯字符串）
                greeting,                      // 问候语（纯字符串）
                winMessage,                    // 胜利消息（纯字符串）
                loseMessage,                   // 失败消息（纯字符串）
                dragonTeam,                    // 神兽队伍配置
                5000.0,                        // 奖励金钱
                List.of(                       // 奖励物品
                        createItemReward("pixelmon:dragon_scale", 1),
                        createItemReward("pixelmon:rare_candy", 5),
                        createPokeBallReward(PokeBallTypes.FAST_BALL, 3)
                ),
                2,                             // 冷却天数
                "pixelmon:textures/npc/gym_leaders/dragon_trainer.png", // 纹理路径
                NPCDefinition.NPCType.GYM_LEADER // 指定NPC类型
        );
    }

    // ==================== BattleNPCProvider 所需的辅助方法 ====================

    /**
     * 创建物品奖励（用于战斗奖励）
     */
    public static JsonObject createItemReward(String itemId, int count) {
        JsonObject item = new JsonObject();
        item.addProperty("id", itemId);
        item.addProperty("count", count);
        return item;
    }

    /**
     * 创建大师球奖励（使用新版本的组件系统）
     */
    public static JsonObject createMasterBallReward(int count) {
        JsonObject item = new JsonObject();
        item.addProperty("id", "pixelmon:poke_ball");
        item.addProperty("count", count);

        JsonObject components = new JsonObject();
        components.addProperty("pixelmon:poke_ball", "master_ball");
        item.add("components", components);

        return item;
    }

    /**
     * 创建精灵球奖励（支持所有类型的精灵球）
     */
    public static JsonObject createPokeBallReward(String ballType, int count) {
        JsonObject item = new JsonObject();
        item.addProperty("id", "pixelmon:poke_ball");
        item.addProperty("count", count);

        JsonObject components = new JsonObject();
        components.addProperty("pixelmon:poke_ball", ballType);
        item.add("components", components);

        return item;
    }

    /**
     * 生成简化版对战馆主NPC（使用纯字符串参数）
     * 这是专门为 BattleNPCProvider 设计的简化方法
     */
    protected void addGymLeaderString(String fileName, String npcName, String titleText,
                                      String greeting, String winMessage, String loseMessage,
                                      List<String> pokemonSpecs, double rewardMoney,
                                      List<JsonObject> rewardItems, int cooldownDays,
                                      String texturePath) {

        // 创建标题（使用纯文本而不是翻译键）
        JsonObject title = createTextTitle(titleText, "#8E44AD", true, false, false);

        // 创建自定义交互
        JsonObject interactions = createCustomBattleLeaderInteractions(
                titleText,      // 使用纯文本作为标题
                greeting,       // 问候语
                winMessage,     // 胜利消息
                loseMessage,    // 失败消息
                rewardMoney,    // 奖励金钱
                rewardItems,    // 奖励物品
                cooldownDays    // 冷却天数
        );

        // 调用现有的对战馆主生成方法
        addBattleLeaderNPC(fileName, npcName, title, pokemonSpecs, texturePath, interactions);
    }

    /**
     * 生成简化版对战馆主NPC（带类型参数）
     */
    protected void addGymLeaderString(String fileName, String npcName, String titleText,
                                      String greeting, String winMessage, String loseMessage,
                                      List<String> pokemonSpecs, double rewardMoney,
                                      List<JsonObject> rewardItems, int cooldownDays,
                                      String texturePath, NPCDefinition.NPCType type) {

        // 创建标题（使用纯文本而不是翻译键）
        JsonObject title = createTextTitle(titleText, "#8E44AD", true, false, false);

        // 创建自定义交互
        JsonObject interactions = createCustomBattleLeaderInteractions(
                titleText,      // 使用纯文本作为标题
                greeting,       // 问候语
                winMessage,     // 胜利消息
                loseMessage,    // 失败消息
                rewardMoney,    // 奖励金钱
                rewardItems,    // 奖励物品
                cooldownDays    // 冷却天数
        );

        // 使用自定义NPC生成方法，支持类型参数
        JsonObject npcDefinition = definition()
                .withType(type)
                .withProperties(createBattleLeaderProperties(title))
                .withSingleName(npcName)
                .withSpecParty(pokemonSpecs)
                .withSinglePlayerModel(true, texturePath)
                .withStandAndLookAI(10.0f, false)
                .withConstantInteractions(interactions)
                .build()
                .serialize();

        this.add(fileName, npcDefinition);
    }

    /**
     * 创建带个体值和努力值的宝可梦配置（BattleNPCProvider 专用）
     * 使用父类的createPokemonSpec方法来确保格式正确
     */
    public String createPokemonSpecWithIVsEVs(String pokemon, int level, String ability, String heldItem,
                                              String nature, List<String> moves, Map<String, Integer> ivs,
                                              Map<String, Integer> evs) {
        // 调用父类的createPokemonSpec方法，确保格式与官方一致
        return createPokemonSpec(
                pokemon, level, ability, heldItem, nature,
                ivs.getOrDefault("hp", 31), ivs.getOrDefault("atk", 31), ivs.getOrDefault("def", 31),
                ivs.getOrDefault("spatk", 31), ivs.getOrDefault("spdef", 31), ivs.getOrDefault("spd", 31),
                evs.getOrDefault("hp", 0), evs.getOrDefault("atk", 0), evs.getOrDefault("def", 0),
                evs.getOrDefault("spatk", 0), evs.getOrDefault("spdef", 0), evs.getOrDefault("spd", 0),
                moves
        );
    }
}