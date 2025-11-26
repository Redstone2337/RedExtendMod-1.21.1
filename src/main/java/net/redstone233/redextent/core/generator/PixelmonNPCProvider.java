/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.redstone233.redextent.core.generator;

import com.google.common.hash.HashCode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.redstone233.redextent.core.npc.NPCDefinition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * 用于生成 Pixelmon NPC JSON 文件的数据提供者，这些文件定义了宝可梦模组中
 * 自定义NPC的行为、交互和属性。
 */
public abstract class PixelmonNPCProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private final PackOutput output;
    private final String modId;
    private final String subDirectory;
    private final String customOutputPath;
    private final NPCDefinition.SaveMode saveMode;

    private final Map<String, JsonObject> npcs = new LinkedHashMap<>();

    protected PixelmonNPCProvider(final PackOutput output, final String modId, final String subDirectory) {
        this(output, modId, subDirectory, null, NPCDefinition.SaveMode.UNORDERED_FORMATTED);
    }

    protected PixelmonNPCProvider(final PackOutput output, final String modId, final String subDirectory, final String customOutputPath) {
        this(output, modId, subDirectory, customOutputPath, NPCDefinition.SaveMode.UNORDERED_FORMATTED);
    }

    protected PixelmonNPCProvider(final PackOutput output, final String modId, final String subDirectory, final NPCDefinition.SaveMode saveMode) {
        this(output, modId, subDirectory, null, saveMode);
    }

    protected PixelmonNPCProvider(final PackOutput output, final String modId, final String subDirectory,
                                  final String customOutputPath, final NPCDefinition.SaveMode saveMode) {
        this.output = output;
        this.modId = modId;
        this.subDirectory = subDirectory;
        this.customOutputPath = customOutputPath;
        this.saveMode = saveMode;
    }

    /**
     * 注册应该通过 {@code add} 方法之一生成的NPC定义。
     */
    public abstract void registerNPCs();

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        this.npcs.clear();
        this.registerNPCs();

        if (!this.npcs.isEmpty()) {
            // 使用自定义路径或默认路径：data/modid/pixelmon/npc/preset/
            Path npcPath;
            if (this.customOutputPath != null && !this.customOutputPath.isEmpty()) {
                npcPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK)
                        .resolve(this.modId)
                        .resolve(this.customOutputPath)
                        .resolve(this.subDirectory);
            } else {
                npcPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK)
                        .resolve(this.modId)
                        .resolve("pixelmon")
                        .resolve("npc")
                        .resolve("preset")
                        .resolve(this.subDirectory);
            }

            CompletableFuture<?>[] futures = new CompletableFuture[this.npcs.size()];
            int i = 0;

            for (Map.Entry<String, JsonObject> entry : this.npcs.entrySet()) {
                Path filePath = npcPath.resolve(entry.getKey() + ".json");

                switch (this.saveMode) {
                    case UNORDERED_FORMATTED:
                        futures[i++] = saveFormattedUnordered(cache, entry.getValue(), filePath);
                        break;
                    case COMPACT:
                        futures[i++] = saveCompactJson(cache, entry.getValue(), filePath);
                        break;
                    case ORIGINAL:
                    default:
                        futures[i++] = saveOriginal(cache, entry.getValue(), filePath);
                        break;
                }
            }

            return CompletableFuture.allOf(futures);
        }

        return CompletableFuture.allOf();
    }

    @Override
    public @NotNull String getName() {
        String pathInfo = (customOutputPath != null && !customOutputPath.isEmpty())
                ? customOutputPath + "/" + subDirectory
                : "pixelmon/npc/preset/" + subDirectory;
        return "Pixelmon NPC 定义 (" + saveMode + "): " + pathInfo;
    }

    /**
     * 添加具有给定文件名和JSON内容的NPC定义。
     *
     * @param fileName NPC定义的文件名（不带.json扩展名）。
     * @param npcDefinition 包含NPC定义的JSON对象。
     */
    protected void add(final String fileName, final JsonObject npcDefinition) {
        if (this.npcs.put(fileName, npcDefinition) != null) {
            throw new IllegalStateException("NPC定义 '" + fileName + "' 已存在");
        }
    }

    /**
     * 创建一个新的空NPC定义构建器。
     */
    protected static NPCDefinition.Builder definition() {
        return NPCDefinition.builder();
    }

    // ==================== NPC生成方法（带类型支持） ====================

    /**
     * 生成对战馆主NPC（严格匹配 dragon_1.json 格式）
     */
    protected void addBattleLeaderNPC(String fileName, String npcName, JsonObject title,
                                      List<String> pokemonSpecs, String texturePath,
                                      JsonObject interactions) {

        JsonObject npcDefinition = definition()
                .withType(NPCDefinition.NPCType.GYM_LEADER)
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
     * 生成简化版对战馆主NPC
     */
    protected void addSimpleBattleLeaderNPC(String fileName, String npcName, String titleTranslate,
                                            List<String> pokemonSpecs, String texturePath) {

        JsonObject title = createTranslateTitle(titleTranslate, "#8E44AD", true, false, false);
        JsonObject interactions = createBattleLeaderInteractions();

        addBattleLeaderNPC(fileName, npcName, title, pokemonSpecs, texturePath, interactions);
    }

    /**
     * 生成聊天NPC（严格匹配 aquaboss.json 格式）
     */
    protected void addChattingNPC(String fileName, List<String> npcNames,
                                  List<NPCDefinition.PlayerModel> models,
                                  JsonObject properties, JsonObject aiProvider,
                                  List<JsonObject> chatInteractions) {

        JsonObject npcDefinition = definition()
                .withType(NPCDefinition.NPCType.CHAT)
                .withProperties(properties)
                .withRandomNames(npcNames)
                .withEmptyParty()
                .withRandomPlayerModels(models)
                .withAIProvider(aiProvider)
                .withUniformInteractions(chatInteractions)
                .build()
                .serialize();

        this.add(fileName, npcDefinition);
    }

    /**
     * 生成教程NPC
     */
    protected void addTutorialNPC(String fileName, List<String> npcNames, JsonObject title,
                                  List<NPCDefinition.PlayerModel> models, JsonObject aiProvider,
                                  List<JsonObject> tutorialInteractions) {

        JsonObject npcDefinition = definition()
                .withType(NPCDefinition.NPCType.CHAT)
                .withTitledProperties(20.0f, 1.0f, 1.0f, 2.0f,
                        title.get("translate").getAsString(),
                        title.get("color").getAsString(),
                        title.get("bold").getAsBoolean(),
                        title.get("italic").getAsBoolean(),
                        title.get("underlined").getAsBoolean(),
                        false, false, false, false, true)
                .withRandomNames(npcNames)
                .withEmptyParty()
                .withRandomPlayerModels(models)
                .withAIProvider(aiProvider)
                .withUniformInteractions(tutorialInteractions)
                .build()
                .serialize();

        this.add(fileName, npcDefinition);
    }

    /**
     * 生成商店NPC（严格匹配 general_1.json 格式）
     */
    protected void addShopkeeperNPC(String fileName, List<String> npcNames, JsonObject title,
                                    List<NPCDefinition.PlayerModel> models, JsonObject aiProvider,
                                    List<JsonObject> shopInteractions) {

        JsonObject npcDefinition = definition()
                .withType(NPCDefinition.NPCType.SHOP)
                .withTitledProperties(20.0f, 1.0f, 1.0f, 2.0f,
                        title.get("translate").getAsString(),
                        title.get("color").getAsString(),
                        title.get("bold").getAsBoolean(),
                        title.get("italic").getAsBoolean(),
                        title.get("underlined").getAsBoolean(),
                        false, false, false, false, true)
                .withRandomNames(npcNames)
                .withEmptyParty()
                .withRandomPlayerModels(models)
                .withAIProvider(aiProvider)
                .withUniformInteractions(shopInteractions)
                .build()
                .serialize();

        this.add(fileName, npcDefinition);
    }

    /**
     * 生成技能教学NPC
     */
    protected void addMoveTutorNPC(String fileName, String npcName,
                                   List<NPCDefinition.PlayerModel> models, JsonObject aiProvider,
                                   JsonObject tutorInteractions) {

        JsonObject npcDefinition = definition()
                .withType(NPCDefinition.NPCType.CHAT)
                .withBasicProperties(20.0f, 1.9f, 0.65f, 2.0f, false, false, false, false, true)
                .withSingleName(npcName)
                .withEmptyParty()
                .withRandomPlayerModels(models)
                .withAIProvider(aiProvider)
                .withConstantInteractions(tutorInteractions)
                .build()
                .serialize();

        this.add(fileName, npcDefinition);
    }

    /**
     * 生成通用NPC（支持自定义类型）
     */
    protected void addCustomNPC(String fileName, NPCDefinition.Builder definitionBuilder) {
        JsonObject npcDefinition = definitionBuilder.build().serialize();
        this.add(fileName, npcDefinition);
    }

    // ==================== 高级NPC生成方法 ====================

    /**
     * 生成道馆馆主NPC（完整配置）
     */
    protected void addGymLeaderNPC(String fileName, String npcName, String titleText, String titleColor,
                                   List<String> pokemonSpecs, String texturePath,
                                   double rewardMoney, List<JsonObject> rewardItems, int cooldownDays) {

        JsonObject title = createTextTitle(titleText, titleColor, true, false, false);
        JsonObject interactions = createCustomBattleLeaderInteractions(
                titleText,
                "准备好挑战道馆了吗？",
                "恭喜你战胜了道馆！",
                "还需要更多训练才能战胜我。",
                rewardMoney,
                rewardItems,
                cooldownDays
        );

        addBattleLeaderNPC(fileName, npcName, title, pokemonSpecs, texturePath, interactions);
    }

    /**
     * 生成商店NPC（完整配置）
     */
    protected void addShopkeeperNPC(String fileName, List<String> npcNames, String titleText, String titleColor,
                                    List<NPCDefinition.PlayerModel> models, String profession,
                                    List<JsonObject> shopItems) {

        JsonObject title = createTextTitle(titleText, titleColor, true, false, false);
        JsonObject aiProvider = createStandardNPC(profession);
        List<JsonObject> shopInteractions = List.of(
                createShopInteraction("商店", "欢迎光临！", "商店", "谢谢惠顾！", shopItems)
        );

        addShopkeeperNPC(fileName, npcNames, title, models, aiProvider, shopInteractions);
    }

    /**
     * 生成聊天NPC（完整配置）
     */
    protected void addChatNPC(String fileName, List<String> npcNames,
                              List<NPCDefinition.PlayerModel> models, String profession,
                              List<List<String>> chatPages) {

        JsonObject properties = createBasicProperties(20.0f, 1.0f, 1.0f, 2.0f, false, false, false, false, true);
        JsonObject aiProvider = createStandardNPC(profession);

        List<JsonObject> chatInteractions = chatPages.stream()
                .map(pages -> createMultiPageDialogueInteraction("对话", pages))
                .toList();

        addChattingNPC(fileName, npcNames, models, properties, aiProvider, chatInteractions);
    }

    // ==================== 属性创建方法 ====================

    /**
     * 创建对战馆主属性（严格匹配 dragon_1.json 格式）
     */
    protected static JsonObject createBattleLeaderProperties(JsonObject title) {
        JsonObject properties = new JsonObject();
        JsonObject value = new JsonObject();

        // 严格按照 dragon_1.json 中 properties.value 的顺序
        value.addProperty("health", 20.0f);
        value.addProperty("eyeHeight", 1.9f);

        JsonObject dimensions = new JsonObject();
        dimensions.addProperty("width", 0.65f);
        dimensions.addProperty("height", 2.0f);
        value.add("dimensions", dimensions);

        value.add("title", title);

        value.addProperty("pushable", false);
        value.addProperty("child", false);
        value.addProperty("invulnerable", false);
        value.addProperty("immovable", false);
        value.addProperty("nameplate", true);

        properties.add("value", value);
        properties.addProperty("type", "pixelmon:constant");
        return properties;
    }

    /**
     * 创建基础NPC属性
     */
    public static JsonObject createBasicProperties(float health, float eyeHeight, float width, float height,
                                                   boolean pushable, boolean child, boolean invulnerable,
                                                   boolean immovable, boolean nameplate) {
        JsonObject properties = new JsonObject();
        JsonObject value = new JsonObject();

        value.addProperty("health", health);
        value.addProperty("eyeHeight", eyeHeight);

        JsonObject dimensions = new JsonObject();
        dimensions.addProperty("width", width);
        dimensions.addProperty("height", height);
        value.add("dimensions", dimensions);

        value.addProperty("pushable", pushable);
        value.addProperty("child", child);
        value.addProperty("invulnerable", invulnerable);
        value.addProperty("immovable", immovable);
        value.addProperty("nameplate", nameplate);

        properties.add("value", value);
        properties.addProperty("type", "pixelmon:constant");
        return properties;
    }

    // ==================== AI提供者创建方法 ====================

    /**
     * 创建站立并看向附近的AI（严格匹配 dragon_1.json 格式）
     */
    public static JsonObject createStandAndLookAI(float lookDistance, boolean swim) {
        JsonObject aiProvider = new JsonObject();
        JsonObject value = new JsonObject();

        // 严格按照 dragon_1.json 中 ai_provider.value 的顺序
        value.addProperty("type", "pixelmon:stand_and_look");
        value.addProperty("look_distance", lookDistance);
        value.addProperty("swim", swim);

        aiProvider.add("value", value);
        aiProvider.addProperty("type", "pixelmon:constant");
        return aiProvider;
    }

    /**
     * 创建标准NPC AI
     */
    public static JsonObject createStandardNPC(String profession) {
        JsonObject aiProvider = new JsonObject();
        JsonObject value = new JsonObject();

        value.addProperty("type", "pixelmon:standard_npc");
        value.addProperty("profession", profession);

        aiProvider.add("value", value);
        aiProvider.addProperty("type", "pixelmon:constant");
        return aiProvider;
    }

    /**
     * 创建游荡并看向附近的AI
     */
    public static JsonObject createWanderAndLookAI(float lookDistance, float speed, boolean swim) {
        JsonObject aiProvider = new JsonObject();
        JsonObject value = new JsonObject();

        value.addProperty("type", "pixelmon:wander_and_look");
        value.addProperty("look_distance", lookDistance);
        value.addProperty("speed", speed);
        value.addProperty("swim", swim);

        aiProvider.add("value", value);
        aiProvider.addProperty("type", "pixelmon:constant");
        return aiProvider;
    }

    // ==================== 交互创建方法 ====================

    /**
     * 创建对战馆主交互（严格匹配 dragon_1.json 格式）
     */
    public static JsonObject createBattleLeaderInteractions() {
        return createCustomBattleLeaderInteractions(
                "pixelmon.npc.dialogue.battle.leader.gym.dragon.1.title",
                "pixelmon.npc.dialogue.battle.leader.gym.dragon.1.initiate",
                "pixelmon.npc.dialogue.battle.leader.gym.dragon.1.win",
                "pixelmon.npc.dialogue.battle.leader.gym.dragon.1.lose",
                500.0,
                List.of(createBadgeReward("pixelmon:legend_badge", 1)),
                1
        );
    }

    /**
     * 创建自定义对战馆主交互
     */
    public static JsonObject createCustomBattleLeaderInteractions(String titleKey, String initiateMessage,
                                                                  String winMessage, String loseMessage,
                                                                  double rewardMoney, List<JsonObject> rewardItems,
                                                                  int cooldownDays) {
        JsonObject interactionsWrapper = new JsonObject();
        JsonArray interactionsArray = new JsonArray();

        // 第一个交互：右键点击（可以战斗且不在冷却中）
        JsonObject interaction1 = createRightClickInteraction(
                titleKey,
                initiateMessage,
                true, true, false
        );
        interactionsArray.add(interaction1);

        // 第二个交互：右键点击（在冷却中）
        JsonObject interaction2 = createRightClickCooldownInteraction(titleKey, cooldownDays);
        interactionsArray.add(interaction2);

        // 第三个交互：右键点击（不能战斗）
        JsonObject interaction3 = createRightClickUnableToBattleInteraction(titleKey);
        interactionsArray.add(interaction3);

        // 第四个交互：关闭对话
        JsonObject interaction4 = createCloseDialogueInteraction();
        interactionsArray.add(interaction4);

        // 第五个交互：战斗失败
        JsonObject interaction5 = createLoseBattleInteraction(titleKey, loseMessage);
        interactionsArray.add(interaction5);

        // 第六个交互：战斗胜利
        JsonObject interaction6 = createWinBattleInteraction(titleKey, winMessage, rewardMoney, rewardItems, cooldownDays);
        interactionsArray.add(interaction6);

        interactionsWrapper.add("interactions", interactionsArray);
        return interactionsWrapper;
    }

    private static JsonObject createRightClickInteraction(String title, String message,
                                                          boolean canBattle, boolean notOnCooldown,
                                                          boolean unableToBattle) {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:right_click");

        JsonArray conditions = new JsonArray();

        // 条件1：使用主手
        conditions.add(createMainHandCondition());

        if (canBattle) {
            conditions.add(createCanBattleCondition());
        }

        if (notOnCooldown) {
            conditions.add(createNotOnCooldownCondition());
        }

        if (unableToBattle) {
            conditions.add(createUnableToBattleCondition());
        }

        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject dialogue = new JsonObject();
        dialogue.addProperty("type", "pixelmon:open_dialogue");
        dialogue.addProperty("title", title);
        dialogue.addProperty("message", message);

        resultsArray.add(dialogue);
        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

    private static JsonObject createRightClickCooldownInteraction(String titleKey, int cooldownDays) {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:right_click");

        JsonArray conditions = new JsonArray();
        conditions.add(createMainHandCondition());
        conditions.add(createOnCooldownCondition(cooldownDays));
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject message = new JsonObject();
        message.addProperty("type", "pixelmon:message_player");
        JsonArray messages = new JsonArray();
        JsonObject messageObj = new JsonObject();
        messageObj.addProperty("translate", "pixelmon.npc.dialogue.battle.leader.gym.dragon.1.cooldown");
        messages.add(messageObj);
        message.add("messages", messages);

        resultsArray.add(message);
        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

    private static JsonObject createRightClickUnableToBattleInteraction(String titleKey) {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:right_click");

        JsonArray conditions = new JsonArray();
        conditions.add(createMainHandCondition());
        conditions.add(createUnableToBattleCondition());
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject message = new JsonObject();
        message.addProperty("type", "pixelmon:message_player");
        JsonArray messages = new JsonArray();
        JsonObject messageObj = new JsonObject();
        messageObj.addProperty("translate", "pixelmon.npc.dialogue.battle.leader.gym.dragon.1.unable_to_battle");
        messages.add(messageObj);
        message.add("messages", messages);

        resultsArray.add(message);
        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

    private static JsonObject createCloseDialogueInteraction() {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:close_dialogue");

        JsonObject conditions = new JsonObject();
        conditions.addProperty("type", "pixelmon:constant_boolean");
        conditions.addProperty("value", true);
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject battle = new JsonObject();
        battle.addProperty("type", "pixelmon:player_start_npc_battle");
        resultsArray.add(battle);

        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

    private static JsonObject createLoseBattleInteraction(String titleKey, String loseMessage) {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:lose_battle");

        JsonObject conditions = new JsonObject();
        conditions.addProperty("type", "pixelmon:true");
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject dialogue = new JsonObject();
        dialogue.addProperty("type", "pixelmon:open_dialogue");
        dialogue.addProperty("title", titleKey);
        dialogue.addProperty("message", loseMessage);
        dialogue.addProperty("fire_close_event", false);
        resultsArray.add(dialogue);

        JsonObject context = new JsonObject();
        context.addProperty("type", "pixelmon:set_string_context");
        context.addProperty("key", "pixelmon:leader");
        context.addProperty("value", "leader_gym_dragon_1");
        resultsArray.add(context);

        JsonObject trigger = new JsonObject();
        trigger.addProperty("type", "pixelmon:trigger_interaction_event");
        trigger.addProperty("event", "pixelmon:lose_to_leader");
        resultsArray.add(trigger);

        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

    private static JsonObject createWinBattleInteraction(String titleKey, String winMessage,
                                                         double rewardMoney, List<JsonObject> rewardItems,
                                                         int cooldownDays) {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:win_battle");

        JsonObject conditions = new JsonObject();
        conditions.addProperty("type", "pixelmon:true");
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject dialogue = new JsonObject();
        dialogue.addProperty("type", "pixelmon:open_dialogue");
        dialogue.addProperty("title", titleKey);
        dialogue.addProperty("message", winMessage);
        dialogue.addProperty("fire_close_event", false);
        resultsArray.add(dialogue);

        if (rewardMoney > 0) {
            JsonObject money = new JsonObject();
            money.addProperty("type", "pixelmon:give_money");
            money.addProperty("money", rewardMoney);
            resultsArray.add(money);
        }

        if (!rewardItems.isEmpty()) {
            JsonObject item = new JsonObject();
            item.addProperty("type", "pixelmon:give_item");
            JsonArray items = new JsonArray();
            for (JsonObject rewardItem : rewardItems) {
                items.add(rewardItem);
            }
            item.add("items", items);
            resultsArray.add(item);
        }

        JsonObject cooldown = new JsonObject();
        cooldown.addProperty("type", "pixelmon:set_cooldown");
        JsonObject player = new JsonObject();
        player.addProperty("key", "pixelmon:player");
        player.addProperty("type", "pixelmon:context_player");
        cooldown.add("player", player);
        cooldown.addProperty("key", "pixelmon:leader_gym_dragon_1");
        cooldown.addProperty("cooldown", cooldownDays);
        cooldown.addProperty("unit", "DAYS");
        resultsArray.add(cooldown);

        JsonObject context = new JsonObject();
        context.addProperty("type", "pixelmon:set_string_context");
        context.addProperty("key", "pixelmon:leader");
        context.addProperty("value", "leader_gym_dragon_1");
        resultsArray.add(context);

        JsonObject trigger = new JsonObject();
        trigger.addProperty("type", "pixelmon:trigger_interaction_event");
        trigger.addProperty("event", "pixelmon:defeat_leader");
        resultsArray.add(trigger);

        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

    // ==================== 条件创建辅助方法 ====================

    private static JsonObject createMainHandCondition() {
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "pixelmon:interaction_condition");
        JsonObject conditionValue = new JsonObject();
        conditionValue.addProperty("type", "pixelmon:string_compare");

        JsonObject first = new JsonObject();
        first.addProperty("value", "MAIN_HAND");
        first.addProperty("type", "pixelmon:constant_string");

        JsonObject second = new JsonObject();
        second.addProperty("type", "pixelmon:hand_used");

        conditionValue.add("first", first);
        conditionValue.add("second", second);
        condition.add("condition", conditionValue);
        return condition;
    }

    private static JsonObject createCanBattleCondition() {
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "pixelmon:interaction_condition");
        JsonObject conditionValue = new JsonObject();
        conditionValue.addProperty("type", "pixelmon:can_battle");

        JsonObject player = new JsonObject();
        player.addProperty("key", "pixelmon:player");
        player.addProperty("type", "pixelmon:context_player");
        conditionValue.add("player", player);

        condition.add("condition", conditionValue);
        return condition;
    }

    private static JsonObject createNotOnCooldownCondition() {
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "pixelmon:interaction_condition");
        JsonObject conditionValue = new JsonObject();
        conditionValue.addProperty("type", "pixelmon:logical_not");

        JsonObject notCondition = new JsonObject();
        notCondition.addProperty("type", "pixelmon:interaction_condition");
        notCondition.add("condition", createOnCooldownConditionValue(1));
        conditionValue.add("condition", notCondition);

        condition.add("condition", conditionValue);
        return condition;
    }

    private static JsonObject createOnCooldownCondition(int cooldownDays) {
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "pixelmon:interaction_condition");
        condition.add("condition", createOnCooldownConditionValue(cooldownDays));
        return condition;
    }

    private static JsonObject createOnCooldownConditionValue(int cooldownDays) {
        JsonObject conditionValue = new JsonObject();
        conditionValue.addProperty("type", "pixelmon:on_cooldown");

        JsonObject player = new JsonObject();
        player.addProperty("key", "pixelmon:player");
        player.addProperty("type", "pixelmon:context_player");
        conditionValue.add("player", player);

        conditionValue.addProperty("cooldown_key", "pixelmon:leader_gym_dragon_1");
        conditionValue.addProperty("cooldown", cooldownDays);
        conditionValue.addProperty("unit", "DAYS");
        return conditionValue;
    }

    private static JsonObject createUnableToBattleCondition() {
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "pixelmon:interaction_condition");
        JsonObject conditionValue = new JsonObject();
        conditionValue.addProperty("type", "pixelmon:logical_not");

        JsonObject notCondition = new JsonObject();
        notCondition.addProperty("type", "pixelmon:interaction_condition");
        notCondition.add("condition", createCanBattleConditionValue());
        conditionValue.add("condition", notCondition);

        condition.add("condition", conditionValue);
        return condition;
    }

    private static JsonObject createCanBattleConditionValue() {
        JsonObject conditionValue = new JsonObject();
        conditionValue.addProperty("type", "pixelmon:can_battle");

        JsonObject player = new JsonObject();
        player.addProperty("key", "pixelmon:player");
        player.addProperty("type", "pixelmon:context_player");
        conditionValue.add("player", player);

        return conditionValue;
    }

    // ==================== 其他交互创建方法 ====================

    /**
     * 创建多页对话交互
     */
    public static JsonObject createMultiPageDialogueInteraction(String title, List<String> pages) {
        JsonObject interactionsWrapper = new JsonObject();
        JsonArray interactionsArray = new JsonArray();

        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:right_click");

        JsonObject conditions = new JsonObject();
        conditions.addProperty("type", "pixelmon:true");
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject dialogue = new JsonObject();
        dialogue.addProperty("title", title);
        dialogue.addProperty("type", "pixelmon:open_paged_dialogue");

        JsonArray pagesArray = new JsonArray();
        for (String page : pages) {
            pagesArray.add(page);
        }
        dialogue.add("pages", pagesArray);

        resultsArray.add(dialogue);
        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        interactionsArray.add(interaction);
        interactionsWrapper.add("interactions", interactionsArray);
        return interactionsWrapper;
    }

    /**
     * 创建商店交互
     */
    public static JsonObject createShopInteraction(String greetingTitle, String greetingMessage,
                                                   String goodbyeTitle, String goodbyeMessage,
                                                   List<JsonObject> shopItems) {
        JsonObject interactionsWrapper = new JsonObject();
        JsonArray interactionsArray = new JsonArray();

        // 右键点击交互
        JsonObject rightClickInteraction = new JsonObject();
        rightClickInteraction.addProperty("event", "pixelmon:right_click");

        JsonObject rightClickConditions = new JsonObject();
        rightClickConditions.addProperty("type", "pixelmon:true");
        rightClickInteraction.add("conditions", rightClickConditions);

        JsonObject rightClickResults = new JsonObject();
        JsonArray rightClickResultsArray = new JsonArray();

        JsonObject dialogue = new JsonObject();
        dialogue.addProperty("title", greetingTitle);
        dialogue.addProperty("message", greetingMessage);
        dialogue.addProperty("type", "pixelmon:open_dialogue");
        rightClickResultsArray.add(dialogue);

        rightClickResults.add("value", rightClickResultsArray);
        rightClickResults.addProperty("type", "pixelmon:constant");
        rightClickInteraction.add("results", rightClickResults);

        interactionsArray.add(rightClickInteraction);

        // 关闭对话打开商店交互
        JsonObject closeDialogueInteraction = new JsonObject();
        closeDialogueInteraction.addProperty("event", "pixelmon:close_dialogue");

        JsonObject closeDialogueConditions = new JsonObject();
        closeDialogueConditions.addProperty("type", "pixelmon:true");
        closeDialogueInteraction.add("conditions", closeDialogueConditions);

        JsonObject closeDialogueResults = new JsonObject();
        JsonArray closeDialogueResultsArray = new JsonArray();

        JsonObject shop = new JsonObject();
        shop.addProperty("type", "pixelmon:open_shop");

        JsonArray itemsArray = new JsonArray();
        for (JsonObject item : shopItems) {
            itemsArray.add(item);
        }
        shop.add("items", itemsArray);

        closeDialogueResultsArray.add(shop);
        closeDialogueResults.add("value", closeDialogueResultsArray);
        closeDialogueResults.addProperty("type", "pixelmon:constant");
        closeDialogueInteraction.add("results", closeDialogueResults);

        interactionsArray.add(closeDialogueInteraction);

        // 关闭商店显示告别语交互
        JsonObject closeShopInteraction = new JsonObject();
        closeShopInteraction.addProperty("event", "pixelmon:close_shop");

        JsonObject closeShopConditions = new JsonObject();
        closeShopConditions.addProperty("type", "pixelmon:true");
        closeShopInteraction.add("conditions", closeShopConditions);

        JsonObject closeShopResults = new JsonObject();
        JsonArray closeShopResultsArray = new JsonArray();

        JsonObject goodbyeDialogue = new JsonObject();
        goodbyeDialogue.addProperty("title", goodbyeTitle);
        goodbyeDialogue.addProperty("message", goodbyeMessage);
        goodbyeDialogue.addProperty("fire_close_event", false);
        goodbyeDialogue.addProperty("type", "pixelmon:open_dialogue");
        closeShopResultsArray.add(goodbyeDialogue);

        closeShopResults.add("value", closeShopResultsArray);
        closeShopResults.addProperty("type", "pixelmon:constant");
        closeShopInteraction.add("results", closeShopResults);

        interactionsArray.add(closeShopInteraction);

        interactionsWrapper.add("interactions", interactionsArray);
        return interactionsWrapper;
    }

    /**
     * 创建技能教学交互
     */
    public static JsonObject createMoveTutorInteraction(List<JsonObject> learnableMoves) {
        JsonObject interactionsWrapper = new JsonObject();
        JsonArray interactionsArray = new JsonArray();

        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:right_click");

        JsonObject conditions = new JsonObject();
        conditions.addProperty("type", "pixelmon:true");
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject tutor = new JsonObject();
        tutor.addProperty("type", "pixelmon:tutor_move");

        JsonArray movesArray = new JsonArray();
        for (JsonObject move : learnableMoves) {
            movesArray.add(move);
        }
        tutor.add("learnable_moves", movesArray);

        resultsArray.add(tutor);
        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        interactionsArray.add(interaction);
        interactionsWrapper.add("interactions", interactionsArray);
        return interactionsWrapper;
    }

    // ==================== 辅助创建方法 ====================

    /**
     * 创建可学习技能
     */
    public static JsonObject createLearnableMove(String attack, List<JsonObject> costs, boolean learnable) {
        JsonObject move = new JsonObject();
        move.addProperty("attack", attack);

        JsonArray costsArray = new JsonArray();
        for (JsonObject cost : costs) {
            costsArray.add(cost);
        }
        move.add("costs", costsArray);

        move.addProperty("learnable", learnable);
        return move;
    }

    /**
     * 创建技能学习成本
     */
    public static JsonObject createMoveCost(String itemId, int count) {
        JsonObject cost = new JsonObject();
        cost.addProperty("id", itemId);
        cost.addProperty("count", count);
        return cost;
    }

    /**
     * 创建徽章奖励
     */
    public static JsonObject createBadgeReward(String badgeId, int count) {
        JsonObject badge = new JsonObject();
        badge.addProperty("id", badgeId);
        badge.addProperty("count", count);
        return badge;
    }

    /**
     * 创建物品奖励
     */
    public static JsonObject createItemReward(String itemId, int count) {
        JsonObject item = new JsonObject();
        item.addProperty("id", itemId);
        item.addProperty("count", count);
        return item;
    }

    /**
     * 创建大师球奖励
     */
    public static JsonObject createMasterBallReward(int count) {
        return createItemReward("pixelmon:master_ball", count);
    }

    // ==================== 宝可梦队伍配置方法 ====================

    /**
     * 创建宝可梦队伍配置
     */
    public static String createPokemonSpec(String pokemon, int level, String ability, String heldItem,
                                           String nature, int ivHp, int ivAtk, int ivDef, int ivSpAtk,
                                           int ivSpDef, int ivSpd, int evHp, int evAtk, int evDef,
                                           int evSpAtk, int evSpDef, int evSpd, List<String> moves) {
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
        spec.append(" ivhp:").append(ivHp)
                .append(" ivatk:").append(ivAtk)
                .append(" ivdef:").append(ivDef)
                .append(" ivspatk:").append(ivSpAtk)
                .append(" ivspdef:").append(ivSpDef)
                .append(" ivspd:").append(ivSpd);

        // 添加努力值
        if (evHp > 0) spec.append(" evhp:").append(evHp);
        if (evAtk > 0) spec.append(" evatk:").append(evAtk);
        if (evDef > 0) spec.append(" evdef:").append(evDef);
        if (evSpAtk > 0) spec.append(" evspatk:").append(evSpAtk);
        if (evSpDef > 0) spec.append(" evspdef:").append(evSpDef);
        if (evSpd > 0) spec.append(" evspd:").append(evSpd);

        // 添加技能
        for (int i = 0; i < moves.size() && i < 4; i++) {
            spec.append(" move").append(i + 1).append(":").append(moves.get(i));
        }

        return spec.toString();
    }

    /**
     * 创建简化版宝可梦队伍配置
     */
    public static String createSimplePokemonSpec(String pokemon, int level, String ability,
                                                 String heldItem, String nature, List<String> moves) {
        return createPokemonSpec(pokemon, level, ability, heldItem, nature,
                31, 31, 31, 0, 31, 31, 0, 0, 0, 0, 0, 0, moves);
    }

    // ==================== 玩家模型创建方法 ====================

    /**
     * 创建玩家模型配置
     */
    public static NPCDefinition.PlayerModel createPlayerModel(boolean slim, String textureResource) {
        return NPCDefinition.PlayerModel.of(slim, textureResource);
    }

    /**
     * 创建玩家模型配置（带后备纹理）
     */
    public static NPCDefinition.PlayerModel createPlayerModel(boolean slim, String textureResource, String textureFallback) {
        return new NPCDefinition.PlayerModel(slim, textureResource, textureFallback);
    }

    // ==================== 物品创建方法 ====================

    /**
     * 创建商店物品（基础方法）
     */
    public static JsonObject createShopItem(String itemId, int count, double buyPrice, double sellPrice) {
        JsonObject item = new JsonObject();

        JsonObject itemObj = new JsonObject();
        itemObj.addProperty("id", itemId);
        itemObj.addProperty("count", count);

        item.add("item", itemObj);
        item.addProperty("buyPrice", buyPrice);
        item.addProperty("sellPrice", sellPrice);

        return item;
    }

    /**
     * 创建带有自定义组件的商店物品
     */
    public static JsonObject createShopItemWithComponents(String itemId, int count, JsonObject components, double buyPrice, double sellPrice) {
        JsonObject item = new JsonObject();

        JsonObject itemObj = new JsonObject();
        itemObj.addProperty("id", itemId);
        itemObj.addProperty("count", count);

        if (components != null && !components.isEmpty()) {
            itemObj.add("components", components);
        }

        item.add("item", itemObj);
        item.addProperty("buyPrice", buyPrice);
        item.addProperty("sellPrice", sellPrice);

        return item;
    }

    /**
     * 创建精灵球商店物品（新版本组件系统）
     */
    public static JsonObject createPokeBallShopItem(String ballType, int count, double buyPrice, double sellPrice) {
        JsonObject components = new JsonObject();
        components.addProperty("pixelmon:poke_ball", ballType);

        return createShopItemWithComponents("pixelmon:poke_ball", count, components, buyPrice, sellPrice);
    }

    /**
     * 创建大师球商店物品
     */
    public static JsonObject createMasterBallShopItem(int count, double buyPrice, double sellPrice) {
        return createPokeBallShopItem("master_ball", count, buyPrice, sellPrice);
    }

    /**
     * 创建高级球商店物品
     */
    public static JsonObject createUltraBallShopItem(int count, double buyPrice, double sellPrice) {
        return createPokeBallShopItem("ultra_ball", count, buyPrice, sellPrice);
    }

    /**
     * 创建超级球商店物品
     */
    public static JsonObject createGreatBallShopItem(int count, double buyPrice, double sellPrice) {
        return createPokeBallShopItem("great_ball", count, buyPrice, sellPrice);
    }

    // ==================== 标题创建方法 ====================

    /**
     * 创建使用翻译键的标题
     */
    public static JsonObject createTranslateTitle(String translate, String color, boolean bold, boolean italic, boolean underlined) {
        JsonObject title = new JsonObject();
        title.addProperty("translate", translate);
        title.addProperty("color", color);
        title.addProperty("bold", bold);
        title.addProperty("italic", italic);
        title.addProperty("underlined", underlined);
        return title;
    }

    /**
     * 创建使用直接文本的标题
     */
    public static JsonObject createTextTitle(String text, String color, boolean bold, boolean italic, boolean underlined) {
        JsonObject title = new JsonObject();
        title.addProperty("text", text);
        title.addProperty("color", color);
        title.addProperty("bold", bold);
        title.addProperty("italic", italic);
        title.addProperty("underlined", underlined);
        return title;
    }

    /**
     * 创建简单的文本标题（默认样式）
     */
    public static JsonObject createSimpleTextTitle(String text) {
        return createTextTitle(text, "#FFFFFF", false, false, false);
    }

    /**
     * 创建简单的翻译标题（默认样式）
     */
    public static JsonObject createSimpleTranslateTitle(String translate) {
        return createTranslateTitle(translate, "#FFFFFF", false, false, false);
    }

    /**
     * 精灵球类型常量
     */
    public static class PokeBallTypes {
        public static final String POKE_BALL = "poke_ball";
        public static final String GREAT_BALL = "great_ball";
        public static final String ULTRA_BALL = "ultra_ball";
        public static final String MASTER_BALL = "master_ball";
        public static final String SAFARI_BALL = "safari_ball";
        public static final String LEVEL_BALL = "level_ball";
        public static final String LURE_BALL = "lure_ball";
        public static final String MOON_BALL = "moon_ball";
        public static final String FRIEND_BALL = "friend_ball";
        public static final String LOVE_BALL = "love_ball";
        public static final String HEAVY_BALL = "heavy_ball";
        public static final String FAST_BALL = "fast_ball";
        public static final String SPORT_BALL = "sport_ball";
        public static final String PREMIER_BALL = "premier_ball";
        public static final String REPEAT_BALL = "repeat_ball";
        public static final String TIMER_BALL = "timer_ball";
        public static final String NEST_BALL = "nest_ball";
        public static final String NET_BALL = "net_ball";
        public static final String DIVE_BALL = "dive_ball";
        public static final String LUXURY_BALL = "luxury_ball";
        public static final String HEAL_BALL = "heal_ball";
        public static final String DUSK_BALL = "dusk_ball";
        public static final String QUICK_BALL = "quick_ball";
        public static final String CHERISH_BALL = "cherish_ball";
        public static final String PARK_BALL = "park_ball";
        public static final String DREAM_BALL = "dream_ball";
        public static final String BEAST_BALL = "beast_ball";
    }

    // ==================== JSON保存方法 ====================

    private CompletableFuture<?> saveOriginal(CachedOutput cache, JsonObject json, Path path) {
        return CompletableFuture.runAsync(() -> {
            try {
                // 使用原版DataProvider.saveStable方法
                DataProvider.saveStable(cache, json, path);
            } catch (Exception e) {
                LOGGER.error("Failed to save original JSON to {}", path, e);
                throw new RuntimeException(e);
            }
        });
    }

    private CompletableFuture<?> saveFormattedUnordered(CachedOutput cache, JsonObject json, Path path) {
        return CompletableFuture.runAsync(() -> {
            try {
                // 创建保持格式但不排序的Gson
                Gson unorderedGson = new GsonBuilder()
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .create();

                // 通过字符串转换来保持插入顺序
                String jsonString = unorderedGson.toJson(json);
                byte[] data = jsonString.getBytes(StandardCharsets.UTF_8);

                // 确保目录存在
                Files.createDirectories(path.getParent());

                // 使用CachedOutput写入文件
                cache.writeIfNeeded(path, data, HashCode.fromInt(DataProvider.INDENT_WIDTH.hashCode()));
            } catch (Exception e) {
                LOGGER.error("Failed to save formatted unordered JSON to {}", path, e);
                throw new RuntimeException(e);
            }
        });
    }

    private CompletableFuture<?> saveCompactJson(CachedOutput cache, JsonObject json, Path path) {
        return CompletableFuture.runAsync(() -> {
            try {
                // 创建紧凑格式的Gson
                Gson compactGson = new GsonBuilder()
                        .disableHtmlEscaping()
                        .create();

                String content = compactGson.toJson(json);
                byte[] data = content.getBytes(StandardCharsets.UTF_8);

                // 确保目录存在
                Files.createDirectories(path.getParent());

                // 使用CachedOutput写入文件
                cache.writeIfNeeded(path, data, HashCode.fromInt(DataProvider.INDENT_WIDTH.hashCode()));
            } catch (Exception e) {
                LOGGER.error("Failed to save compact JSON to {}", path, e);
                throw new RuntimeException(e);
            }
        });
    }
}