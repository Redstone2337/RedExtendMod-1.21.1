package net.redstone.redextent.data;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.redstone.redextent.core.generator.PixelmonNPCProvider;

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
        JsonObject title = createTextTitle("神兽道馆馆主·蓝龙", "#4169E1", true, false, true);
        JsonObject greeting = createTextMessage("欢迎来到神兽道馆！我是馆主蓝龙。准备好面对传说中的宝可梦了吗？");
        JsonObject winMessage = createTextMessage("不可思议！你竟然战胜了我的神兽队伍！这是神兽徽章，你值得拥有！");
        JsonObject loseMessage = createTextMessage("神兽的力量果然不是那么容易挑战的，继续努力吧！");

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

        addGymLeader(
                "blue_dragon_leader",          // 文件名
                "蓝龙",                        // NPC名称
                title,                         // 自定义标题
                greeting,                      // 自定义问候语
                winMessage,                    // 自定义胜利消息
                loseMessage,                   // 自定义失败消息
                blueDragonTeam,                // 神兽队伍配置
                10000.0,                       // 奖励金钱
                List.of(                       // 奖励物品
                        createItemReward("pixelmon:master_ball", 2),
                        createItemReward("pixelmon:rare_candy", 10)
                ),
                3,                             // 冷却天数
                "rem:textures/npc/battle/blue_dragon.png" // 纹理路径
        );
    }

    /**
     * 创建实习训练家：火焰神兽训练家
     */
    private void createFireLegendaryTrainer() {
        JsonObject title = createTextTitle("火焰神兽训练家·炎煌", "#FF4500", true, false, false);
        JsonObject greeting = createTextMessage("我是火焰神兽训练家炎煌！让火焰净化一切吧！");
        JsonObject winMessage = createTextMessage("你的实力让我印象深刻！继续前进吧！");
        JsonObject loseMessage = createTextMessage("火焰的力量果然还是最强的！");

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

        addGymLeader(
                "fire_legendary_trainer",      // 文件名
                "炎煌",                        // NPC名称
                title,                         // 自定义标题
                greeting,                      // 自定义问候语
                winMessage,                    // 自定义胜利消息
                loseMessage,                   // 自定义失败消息
                fireTeam,                      // 神兽队伍配置
                5000.0,                        // 奖励金钱
                List.of(                       // 奖励物品
                        createItemReward("pixelmon:fire_stone", 1),
                        createItemReward("pixelmon:rare_candy", 5)
                ),
                2,                             // 冷却天数
                "rem:textures/npc/battle/eric.png" // 纹理路径
        );
    }

    /**
     * 创建实习训练家：水系神兽训练家
     */
    private void createWaterLegendaryTrainer() {
        JsonObject title = createTextTitle("水系神兽训练家·海澜", "#1E90FF", true, false, false);
        JsonObject greeting = createTextMessage("我是水系神兽训练家海澜！感受大海的力量吧！");
        JsonObject winMessage = createTextMessage("你的实力如海洋般深不可测！继续前进吧！");
        JsonObject loseMessage = createTextMessage("大海的力量是无穷无尽的！");

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

        addGymLeader(
                "water_legendary_trainer",     // 文件名
                "海澜",                        // NPC名称
                title,                         // 自定义标题
                greeting,                      // 自定义问候语
                winMessage,                    // 自定义胜利消息
                loseMessage,                   // 自定义失败消息
                waterTeam,                     // 神兽队伍配置
                5000.0,                        // 奖励金钱
                List.of(                       // 奖励物品
                        createItemReward("pixelmon:water_stone", 1),
                        createItemReward("pixelmon:rare_candy", 5)
                ),
                2,                             // 冷却天数
                "rem:textures/npc/battle/ice_dragon.png" // 纹理路径
        );
    }

    /**
     * 创建实习训练家：电系神兽训练家
     */
    private void createElectricLegendaryTrainer() {
        JsonObject title = createTextTitle("电系神兽训练家·雷霆", "#FFD700", true, false, false);
        JsonObject greeting = createTextMessage("我是电系神兽训练家雷霆！准备好被电击了吗？");
        JsonObject winMessage = createTextMessage("你的实力如闪电般耀眼！继续前进吧！");
        JsonObject loseMessage = createTextMessage("电力的力量无人能挡！");

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

        addGymLeader(
                "electric_legendary_trainer",  // 文件名
                "雷霆",                        // NPC名称
                title,                         // 自定义标题
                greeting,                      // 自定义问候语
                winMessage,                    // 自定义胜利消息
                loseMessage,                   // 自定义失败消息
                electricTeam,                  // 神兽队伍配置
                5000.0,                        // 奖励金钱
                List.of(                       // 奖励物品
                        createItemReward("pixelmon:thunder_stone", 1),
                        createItemReward("pixelmon:rare_candy", 5)
                ),
                2,                             // 冷却天数
                "rem:textures/npc/battle/ender_dragon.png" // 纹理路径
        );
    }

    /**
     * 创建实习训练家：龙系神兽训练家
     */
    private void createDragonLegendaryTrainer() {
        JsonObject title = createTextTitle("龙系神兽训练家·龙啸", "#8A2BE2", true, false, false);
        JsonObject greeting = createTextMessage("我是龙系神兽训练家龙啸！感受龙之怒吧！");
        JsonObject winMessage = createTextMessage("你的实力如同传说中的龙骑士！继续前进吧！");
        JsonObject loseMessage = createTextMessage("龙族的力量永远是最强的！");

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

        addGymLeader(
                "dragon_legendary_trainer",    // 文件名
                "龙啸",                        // NPC名称
                title,                         // 自定义标题
                greeting,                      // 自定义问候语
                winMessage,                    // 自定义胜利消息
                loseMessage,                   // 自定义失败消息
                dragonTeam,                    // 神兽队伍配置
                5000.0,                        // 奖励金钱
                List.of(                       // 奖励物品
                        createItemReward("pixelmon:dragon_scale", 1),
                        createItemReward("pixelmon:rare_candy", 5)
                ),
                2,                             // 冷却天数
                "pixelmon:textures/npc/gym_leaders/dragon_trainer.png" // 纹理路径
        );
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

    /**
     * 创建物品奖励（使用物品ID字符串）
     */
    public static JsonObject createItemReward(String itemId, int count) {
        JsonObject item = new JsonObject();
        item.addProperty("id", itemId);
        item.addProperty("count", count);
        return item;
    }

    public static JsonObject createItemReward(Item item, int count) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        if (itemId == null) {
            throw new IllegalArgumentException("物品未注册: " + item);
        }
        return createItemReward(itemId.toString(), count);
    }
}
