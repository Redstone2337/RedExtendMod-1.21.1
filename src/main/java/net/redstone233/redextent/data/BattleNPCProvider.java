package net.redstone233.redextent.data;

import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.redstone233.redextent.core.generator.PixelmonNPCProvider;

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
        // 使用直接文本创建标题和消息
        JsonObject title = PixelmonNPCProvider.createTextTitle("神兽道馆馆主·蓝龙", "#4169E1", true, false, true);
        JsonObject greeting = PixelmonNPCProvider.createTextMessage("欢迎来到神兽道馆！我是馆主蓝龙。准备好面对传说中的宝可梦了吗？");
        JsonObject winMessage = PixelmonNPCProvider.createTextMessage("不可思议！你竟然战胜了我的神兽队伍！这是神兽徽章，你值得拥有！");
        JsonObject loseMessage = PixelmonNPCProvider.createTextMessage("神兽的力量果然不是那么容易挑战的，继续努力吧！");

        List<String> blueDragonTeam = List.of(
                createPokemonSpecWithIVsEVs("Zygarde", 100, "Power Construct", "Leftovers", "Adamant",
                        List.of("Thousand Arrows", "Thousand Waves", "Core Enforcer", "Extreme Speed"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 252, "atk", 252, "def", 4, "spatk", 0, "spdef", 0, "spd", 0)),

                createPokemonSpecWithIVsEVs("Rayquaza", 98, "Air Lock", "Life Orb", "Jolly",
                        List.of("Dragon Ascent", "Extreme Speed", "Dragon Dance", "Earthquake"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 4, "spatk", 0, "spdef", 0, "spd", 252)),

                createPokemonSpecWithIVsEVs("Mewtwo", 96, "Pressure", "Mewtwonite X", "Timid",
                        List.of("Psystrike", "Aura Sphere", "Ice Beam", "Recover"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 4, "spatk", 252, "spdef", 0, "spd", 252)),

                createPokemonSpecWithIVsEVs("Dialga", 95, "Pressure", "Adamant Orb", "Modest",
                        List.of("Roar of Time", "Flash Cannon", "Fire Blast", "Thunder"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 252, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 0)),

                createPokemonSpecWithIVsEVs("Palkia", 94, "Pressure", "Lustrous Orb", "Timid",
                        List.of("Spacial Rend", "Hydro Pump", "Thunderbolt", "Fire Blast"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Giratina", 92, "Levitate", "Griseous Orb", "Modest",
                        List.of("Shadow Force", "Dragon Pulse", "Aura Sphere", "Earth Power"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 252, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 0))
        );

        // 使用完整参数版本的馆主NPC
        addGymLeader(
                "blue_dragon_leader",          // 文件名
                "蓝龙",                        // NPC名称
                title,                         // 标题（直接文本）
                greeting,                      // 问候语（直接文本）
                winMessage,                    // 胜利消息（直接文本）
                loseMessage,                   // 失败消息（直接文本）
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
        JsonObject title = PixelmonNPCProvider.createTextTitle("火焰神兽训练家·炎煌", "#FF4500", true, false, false);
        JsonObject greeting = PixelmonNPCProvider.createTextMessage("我是火焰神兽训练家炎煌！让火焰净化一切吧！");
        JsonObject winMessage = PixelmonNPCProvider.createTextMessage("你的实力让我印象深刻！继续前进吧！");
        JsonObject loseMessage = PixelmonNPCProvider.createTextMessage("火焰的力量果然还是最强的！");

        List<String> fireTeam = List.of(
                createPokemonSpecWithIVsEVs("Groudon", 88, "Drought", "Red Orb", "Adamant",
                        List.of("Precipice Blades", "Fire Punch", "Rock Slide", "Swords Dance"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Ho-Oh", 85, "Pressure", "Life Orb", "Adamant",
                        List.of("Sacred Fire", "Brave Bird", "Earthquake", "Recover"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 248, "atk", 252, "def", 0, "spatk", 0, "spdef", 8, "spd", 0)),

                createPokemonSpecWithIVsEVs("Moltres", 82, "Pressure", "Heavy-Duty Boots", "Timid",
                        List.of("Hurricane", "Fire Blast", "Roost", "U-turn"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Entei", 80, "Pressure", "Choice Band", "Adamant",
                        List.of("Sacred Fire", "Extreme Speed", "Stone Edge", "Iron Head"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Reshiram", 78, "Turboblaze", "Choice Scarf", "Modest",
                        List.of("Blue Flare", "Draco Meteor", "Focus Blast", "Stone Edge"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Blaziken", 75, "Speed Boost", "Blazikenite", "Adamant",
                        List.of("Flare Blitz", "High Jump Kick", "Stone Edge", "Protect"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252))
        );

        addGymLeader(
                "fire_legendary_trainer",      // 文件名
                "炎煌",                        // NPC名称
                title,                         // 标题（直接文本）
                greeting,                      // 问候语（直接文本）
                winMessage,                    // 胜利消息（直接文本）
                loseMessage,                   // 失败消息（直接文本）
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
        JsonObject title = PixelmonNPCProvider.createTextTitle("水系神兽训练家·海澜", "#1E90FF", true, false, false);
        JsonObject greeting = PixelmonNPCProvider.createTextMessage("我是水系神兽训练家海澜！感受大海的力量吧！");
        JsonObject winMessage = PixelmonNPCProvider.createTextMessage("你的实力如海洋般深不可测！继续前进吧！");
        JsonObject loseMessage = PixelmonNPCProvider.createTextMessage("大海的力量是无穷无尽的！");

        List<String> waterTeam = List.of(
                createPokemonSpecWithIVsEVs("Kyogre", 87, "Drizzle", "Blue Orb", "Modest",
                        List.of("Origin Pulse", "Water Spout", "Thunder", "Ice Beam"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 4, "atk", 0, "def", 0, "spatk", 252, "spdef", 0, "spd", 252)),

                createPokemonSpecWithIVsEVs("Lugia", 84, "Pressure", "Leftovers", "Bold",
                        List.of("Aeroblast", "Calm Mind", "Roost", "Ice Beam"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 252, "atk", 0, "def", 252, "spatk", 0, "spdef", 4, "spd", 0)),

                createPokemonSpecWithIVsEVs("Articuno", 81, "Pressure", "Heavy-Duty Boots", "Calm",
                        List.of("Freeze-Dry", "Hurricane", "Roost", "Toxic"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 248, "atk", 0, "def", 0, "spatk", 0, "spdef", 252, "spd", 8)),

                createPokemonSpecWithIVsEVs("Suicune", 79, "Pressure", "Leftovers", "Bold",
                        List.of("Scald", "Ice Beam", "Calm Mind", "Rest"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 252, "atk", 0, "def", 252, "spatk", 0, "spdef", 4, "spd", 0)),

                createPokemonSpecWithIVsEVs("Zekrom", 77, "Teravolt", "Choice Band", "Adamant",
                        List.of("Bolt Strike", "Outrage", "Stone Edge", "Volt Switch"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Swampert", 74, "Swift Swim", "Swampertite", "Adamant",
                        List.of("Waterfall", "Earthquake", "Ice Punch", "Rain Dance"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252))
        );

        addGymLeader(
                "water_legendary_trainer",     // 文件名
                "海澜",                        // NPC名称
                title,                         // 标题（直接文本）
                greeting,                      // 问候语（直接文本）
                winMessage,                    // 胜利消息（直接文本）
                loseMessage,                   // 失败消息（直接文本）
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
        JsonObject title = PixelmonNPCProvider.createTextTitle("电系神兽训练家·雷霆", "#FFD700", true, false, false);
        JsonObject greeting = PixelmonNPCProvider.createTextMessage("我是电系神兽训练家雷霆！准备好被电击了吗？");
        JsonObject winMessage = PixelmonNPCProvider.createTextMessage("你的实力如闪电般耀眼！继续前进吧！");
        JsonObject loseMessage = PixelmonNPCProvider.createTextMessage("电力的力量无人能挡！");

        List<String> electricTeam = List.of(
                createPokemonSpecWithIVsEVs("Raikou", 86, "Pressure", "Assault Vest", "Timid",
                        List.of("Thunderbolt", "Volt Switch", "Shadow Ball", "Extrasensory"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Zapdos", 83, "Pressure", "Heavy-Duty Boots", "Timid",
                        List.of("Thunderbolt", "Hurricane", "Heat Wave", "Roost"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Thundurus", 80, "Prankster", "Life Orb", "Timid",
                        List.of("Thunderbolt", "Focus Blast", "Hidden Power Ice", "Nasty Plot"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Tapu Koko", 78, "Electric Surge", "Choice Specs", "Timid",
                        List.of("Thunderbolt", "Dazzling Gleam", "Volt Switch", "Grass Knot"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Zeraora", 76, "Volt Absorb", "Expert Belt", "Jolly",
                        List.of("Plasma Fists", "Close Combat", "Blaze Kick", "Volt Switch"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Ampharos", 73, "Mold Breaker", "Ampharosite", "Modest",
                        List.of("Thunderbolt", "Dragon Pulse", "Focus Blast", "Volt Switch"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 248, "atk", 0, "def", 0, "spatk", 252, "spdef", 8, "spd", 0))
        );

        addGymLeader(
                "electric_legendary_trainer",  // 文件名
                "雷霆",                        // NPC名称
                title,                         // 标题（直接文本）
                greeting,                      // 问候语（直接文本）
                winMessage,                    // 胜利消息（直接文本）
                loseMessage,                   // 失败消息（直接文本）
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
        JsonObject title = PixelmonNPCProvider.createTextTitle("龙系神兽训练家·龙啸", "#8A2BE2", true, false, false);
        JsonObject greeting = PixelmonNPCProvider.createTextMessage("我是龙系神兽训练家龙啸！感受龙之怒吧！");
        JsonObject winMessage = PixelmonNPCProvider.createTextMessage("你的实力如同传说中的龙骑士！继续前进吧！");
        JsonObject loseMessage = PixelmonNPCProvider.createTextMessage("龙族的力量永远是最强的！");

        List<String> dragonTeam = List.of(
                createPokemonSpecWithIVsEVs("Kyurem", 89, "Pressure", "Leftovers", "Timid",
                        List.of("Ice Beam", "Freeze-Dry", "Earth Power", "Roost"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 56, "atk", 0, "def", 0, "spatk", 200, "spdef", 0, "spd", 252)),

                createPokemonSpecWithIVsEVs("Latias", 85, "Levitate", "Soul Dew", "Timid",
                        List.of("Draco Meteor", "Psyshock", "Recover", "Calm Mind"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Latios", 85, "Levitate", "Soul Dew", "Timid",
                        List.of("Draco Meteor", "Psyshock", "Recover", "Calm Mind"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Hydreigon", 82, "Levitate", "Choice Scarf", "Timid",
                        List.of("Draco Meteor", "Dark Pulse", "Fire Blast", "U-turn"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 0, "def", 0, "spatk", 252, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Salamence", 79, "Aerilate", "Salamencite", "Jolly",
                        List.of("Return", "Dragon Dance", "Roost", "Earthquake"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252)),

                createPokemonSpecWithIVsEVs("Dragapult", 76, "Infiltrator", "Choice Band", "Jolly",
                        List.of("Dragon Darts", "Phantom Force", "U-turn", "Sucker Punch"),
                        Map.of("hp", 31, "atk", 31, "def", 31, "spatk", 31, "spdef", 31, "spd", 31),
                        Map.of("hp", 0, "atk", 252, "def", 0, "spatk", 0, "spdef", 4, "spd", 252))
        );

        addGymLeader(
                "dragon_legendary_trainer",    // 文件名
                "龙啸",                        // NPC名称
                title,                         // 标题（直接文本）
                greeting,                      // 问候语（直接文本）
                winMessage,                    // 胜利消息（直接文本）
                loseMessage,                   // 失败消息（直接文本）
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
}