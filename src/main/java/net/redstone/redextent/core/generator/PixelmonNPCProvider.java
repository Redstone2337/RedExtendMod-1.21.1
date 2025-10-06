/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.redstone.redextent.core.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.redstone.redextent.core.npc.NPCDefinition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/** 用于生成 Pixelmon NPC JSON 文件的数据提供者，这些文件定义了宝可梦模组中 自定义NPC的行为、交互和属性。 */
public abstract class PixelmonNPCProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private final PackOutput output;
    private final String modId;
    private final String subDirectory;
    private final String customOutputPath;

    private final Map<String, JsonObject> npcs = new LinkedHashMap<>();

    /**
     * 创建此数据提供者的新实例。
     *
     * @param output 数据生成器提供的 {@linkplain PackOutput} 实例。
     * @param modId 当前模组的模组ID。
     * @param subDirectory 在 data/modid/pixelmon/npc/preset/ 中放置文件的子目录。
     */
    protected PixelmonNPCProvider(final PackOutput output, final String modId, final String subDirectory) {
        this.output = output;
        this.modId = modId;
        this.subDirectory = subDirectory;
        this.customOutputPath = null; // 使用默认路径
    }

    /**
     * 创建此数据提供者的新实例，支持自定义输出路径。
     *
     * @param output 数据生成器提供的 {@linkplain PackOutput} 实例。
     * @param modId 当前模组的模组ID。
     * @param subDirectory 放置文件的子目录。
     * @param customOutputPath 自定义输出路径，如果为null则使用默认路径 data/modid/pixelmon/npc/preset/。
     */
    protected PixelmonNPCProvider(final PackOutput output, final String modId, final String subDirectory, final String customOutputPath) {
        this.output = output;
        this.modId = modId;
        this.subDirectory = subDirectory;
        this.customOutputPath = customOutputPath;
    }

    /** 注册应该通过 {@code add} 方法之一生成的NPC定义。 */
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
                futures[i++] = DataProvider.saveStable(cache, entry.getValue(), filePath);
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
        return "Pixelmon NPC 定义: " + pathInfo;
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

    /** 创建一个新的空NPC定义构建器。 */
    protected static NPCDefinition.Builder definition() {
        return NPCDefinition.builder();
    }

    // ==================== 快速生成方法 ====================

    /**
     * 快速生成馆主NPC（与dragon_1.json布局一致）
     *
     * @param fileName 文件名
     * @param npcName NPC名称
     * @param title 标题配置（JsonObject，支持translate或text）
     * @param greeting 问候语配置（JsonObject，支持translate或text）
     * @param winMessage 胜利消息配置（JsonObject，支持translate或text）
     * @param loseMessage 失败消息配置（JsonObject，支持translate或text）
     * @param pokemonSpecs 宝可梦配置列表
     * @param rewardMoney 奖励金钱
     * @param rewardItems 奖励物品列表
     * @param cooldownDays 冷却天数
     * @param texture 纹理路径
     * @param cooldownKey 冷却键名
     */
    protected void addGymLeader(String fileName, String npcName, JsonObject title,
            JsonObject greeting, JsonObject winMessage, JsonObject loseMessage,
            List<String> pokemonSpecs, double rewardMoney,
            List<JsonObject> rewardItems, int cooldownDays, String texture, String cooldownKey) {

        JsonObject npcDefinition = definition()
                .withConstantInteractions(createGymLeaderInteractionsWithConditions(title, greeting, winMessage,
                loseMessage, rewardMoney, rewardItems, cooldownDays, cooldownKey))
                .withTitleProperties(20.0f, 1.9f, 0.65f, 2.0f, title, false, false, false, false, true)
                .withSpecParty(pokemonSpecs)
                .withSingleName(npcName)
                .withSinglePlayerModel(false, texture)
                .withLookAtNearbyGoal(10.0f, 0.9f, 1)
                .build()
                .serialize();

        // 重新组织JSON结构以匹配dragon_1.json的布局
        reorganizeGymLeaderJson(npcDefinition);

        this.add(fileName, npcDefinition);
    }

    /** 快速生成馆主NPC（简化版，使用翻译键作为消息） */
    protected void addGymLeader(String fileName, String npcName, String titleTranslate,
            String greetingTranslate, String winMessageTranslate, String loseMessageTranslate,
            List<String> pokemonSpecs, double rewardMoney,
            List<JsonObject> rewardItems, int cooldownDays, String texture, String cooldownKey) {

        JsonObject title = createTranslateTitle(titleTranslate, "#8E44AD", true, false, false);
        JsonObject greeting = createTranslateMessage(greetingTranslate);
        JsonObject winMessage = createTranslateMessage(winMessageTranslate);
        JsonObject loseMessage = createTranslateMessage(loseMessageTranslate);

        addGymLeader(fileName, npcName, title, greeting, winMessage, loseMessage,
        pokemonSpecs, rewardMoney, rewardItems, cooldownDays, texture, cooldownKey);
    }

    /**
     * 快速生成商店NPC（与evostones_1.json布局一致）
     *
     * @param fileName 文件名
     * @param npcNames NPC名称列表
     * @param title 标题配置（JsonObject，支持translate或text）
     * @param greeting 问候语配置（JsonObject，支持translate或text）
     * @param goodbye 告别语配置（JsonObject，支持translate或text）
     * @param shopItems 商店物品列表
     * @param textureResources 纹理资源列表
     */
    protected void addShopKeeper(String fileName, List<String> npcNames, JsonObject title,
            JsonObject greeting, JsonObject goodbye, List<JsonObject> shopItems,
            List<String> textureResources) {

        List<NPCDefinition.PlayerModel> models = textureResources.stream()
                .map(texture -> NPCDefinition.PlayerModel.of(false, texture))
                .toList();

        JsonObject npcDefinition = definition()
                .withUniformInteractions(List.of(createShopInteractions(title, greeting, goodbye, shopItems)))
                .withTitleProperties(20.0f, 1.0f, 1.0f, 2.0f, title, false, false, false, false, true)
                .withEmptyParty()
                .withRandomNames(npcNames)
                .withRandomPlayerModels(models)
                .withLookAtNearbyGoal(5.0f, 0.9f, 1)
                .build()
                .serialize();

        // 重新组织JSON结构以匹配evostones_1.json的布局
        reorganizeShopKeeperJson(npcDefinition);

        this.add(fileName, npcDefinition);
    }

    /** 快速生成商店NPC（简化版，使用翻译键作为消息） */
    protected void addShopKeeper(String fileName, List<String> npcNames, String titleTranslate,
            String greetingTranslate, String goodbyeTranslate, List<JsonObject> shopItems,
            List<String> textureResources) {

        JsonObject title = createTranslateTitle(titleTranslate, "#2176FF", true, false, false);
        JsonObject greeting = createTranslateMessage(greetingTranslate);
        JsonObject goodbye = createTranslateMessage(goodbyeTranslate);

        addShopKeeper(fileName, npcNames, title, greeting, goodbye, shopItems, textureResources);
    }

    /**
     * 快速生成提示NPC（与aquaboss.json布局一致）
     *
     * @param fileName 文件名
     * @param npcNames NPC名称列表
     * @param title 标题配置（JsonObject，支持translate或text）
     * @param messages 消息配置列表（JsonObject，支持translate或text）
     * @param textureResources 纹理资源列表
     */
    protected void addTipNPC(String fileName, List<String> npcNames, JsonObject title,
            List<List<JsonObject>> messagesGroups, List<String> textureResources) {

        List<NPCDefinition.PlayerModel> models = textureResources.stream()
                .map(texture -> NPCDefinition.PlayerModel.of(false, texture))
                .toList();

        JsonObject npcDefinition = definition()
                .withUniformInteractions(createTipInteractionsWithRandomGroups(title, messagesGroups))
                .withTitleProperties(20.0f, 1.0f, 1.0f, 2.0f, title, false, false, false, false, true)
                .withEmptyParty()
                .withRandomNames(npcNames)
                .withRandomPlayerModels(models)
                .withRandomStrollGoal(0.4f, 20, true, 1)
                .build()
                .serialize();

        // 重新组织JSON结构以匹配aquaboss.json的布局
        reorganizeTipNPCJson(npcDefinition);

        this.add(fileName, npcDefinition);
    }

    /** 快速生成提示NPC（简化版，使用翻译键作为消息） */
    protected void addTipNPC(String fileName, List<String> npcNames, String titleTranslate,
            List<List<String>> messageGroups, List<String> textureResources) {

        JsonObject title = createTranslateTitle(titleTranslate, "#F8F8F2", true, false, false);
        List<List<JsonObject>> messagesGroups = messageGroups.stream()
                .map(group -> group.stream()
                        .map(PixelmonNPCProvider::createTranslateMessage)
                        .toList())
                .toList();

        addTipNPC(fileName, npcNames, title, messagesGroups, textureResources);
    }

    /**
     * 快速生成宝可梦中心医生NPC（与doctor_john.json布局一致）
     *
     * @param fileName 文件名
     * @param npcName NPC名称
     * @param titleTranslate 标题翻译键
     * @param plateTranslate 名称牌翻译键
     * @param greetingTranslate 问候语翻译键
     * @param goodbyeTranslate 告别语翻译键
     * @param texture 纹理路径
     * @param isSlim 是否使用slim模型
     */
    protected void addPokeCenterDoctor(String fileName, String npcName, String titleTranslate,
            String plateTranslate, String greetingTranslate,
            String goodbyeTranslate, String texture, boolean isSlim) {

        JsonObject npcDefinition = definition()
                .withUniformInteractions(createPokeCenterInteractions(titleTranslate, greetingTranslate, goodbyeTranslate))
                .withTitleProperties(20.0f, 1.62f, 0.9f, 1.9f,
                        createTranslateTitle(plateTranslate, "#751515", true, false, false),
                        false, false, false, false, true)
                .withEmptyParty()
                .withSingleName(npcName)
                .withSinglePlayerModel(isSlim, texture)
                .withLookAtNearbyGoal(5.0f, 0.9f, 1)
                .build()
                .serialize();

        // 重新组织JSON结构以匹配doctor_john.json的布局
        reorganizePokeCenterJson(npcDefinition);

        this.add(fileName, npcDefinition);
    }

/** 快速生成宝可梦中心护士NPC（与nurse_joy.json布局一致） */
    protected void addPokeCenterNurse(String fileName, String npcName, String titleTranslate,
            String plateTranslate, String greetingTranslate,
            String goodbyeTranslate, String texture, boolean isSlim) {

        // 护士与医生的结构相同，直接调用医生方法
        addPokeCenterDoctor(fileName, npcName, titleTranslate, plateTranslate,
        greetingTranslate, goodbyeTranslate, texture, isSlim);
    }

    // ==================== 辅助类方法 ====================

    /** 创建使用翻译键的标题 */
    public static JsonObject createTranslateTitle(String translate, String color, boolean bold, boolean italic, boolean underlined) {
        JsonObject title = new JsonObject();
        title.addProperty("translate", translate);
        title.addProperty("color", color);
        title.addProperty("bold", bold);
        title.addProperty("italic", italic);
        title.addProperty("underlined", underlined);
        return title;
    }

    /** 创建使用直接文本的标题 */
    public static JsonObject createTextTitle(String text, String color, boolean bold, boolean italic, boolean underlined) {
        JsonObject title = new JsonObject();
        title.addProperty("text", text);
        title.addProperty("color", color);
        title.addProperty("bold", bold);
        title.addProperty("italic", italic);
        title.addProperty("underlined", underlined);
        return title;
    }

    /** 创建简单的文本标题（默认样式） */
    public static JsonObject createSimpleTextTitle(String text) {
        return createTextTitle(text, "#FFFFFF", false, false, false);
    }

    /** 创建简单的翻译标题（默认样式） */
    public static JsonObject createSimpleTranslateTitle(String translate) {
        return createTranslateTitle(translate, "#FFFFFF", false, false, false);
    }

    /** 创建使用翻译键的消息 */
    public static JsonObject createTranslateMessage(String translate) {
        JsonObject message = new JsonObject();
        message.addProperty("translate", translate);
        return message;
    }

    /** 创建使用直接文本的消息 */
    public static JsonObject createTextMessage(String text) {
        JsonObject message = new JsonObject();
        message.addProperty("text", text);
        return message;
    }

    /** 创建简单的文本消息 */
    public static JsonObject createSimpleTextMessage(String text) {
        return createTextMessage(text);
    }

    /** 创建简单的翻译消息 */
    public static JsonObject createSimpleTranslateMessage(String translate) {
        return createTranslateMessage(translate);
    }

    /**
     * 创建商店物品（使用Item对象）
     *
     * @param item 物品对象
     * @param count 数量
     * @param buyPrice 购买价格
     * @param sellPrice 出售价格
     * @return 商店物品JSON对象
     */
    public static JsonObject createShopItem(Item item, int count, double buyPrice, double sellPrice) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        if (itemId == null) {
            throw new IllegalArgumentException("物品未注册: " + item);
        }
        return createShopItem(itemId.toString(), count, buyPrice, sellPrice);
    }

    /**
     * 创建商店物品（使用物品ID字符串）
     *
     * @param itemId 物品ID
     * @param count 数量
     * @param buyPrice 购买价格
     * @param sellPrice 出售价格
     * @return 商店物品JSON对象
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
     * 创建物品奖励（使用Item对象）
     *
     * @param item 物品对象
     * @param count 数量
     * @return 物品奖励JSON对象
     */
    public static JsonObject createItemReward(Item item, int count) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        if (itemId == null) {
            throw new IllegalArgumentException("物品未注册: " + item);
        }
        return createItemReward(itemId.toString(), count);
    }

    /**
     * 创建物品奖励（使用物品ID字符串）
     *
     * @param itemId 物品ID
     * @param count 数量
     * @return 物品奖励JSON对象
     */
    public static JsonObject createItemReward(String itemId, int count) {
        JsonObject item = new JsonObject();
        item.addProperty("id", itemId);
        item.addProperty("count", count);
        return item;
    }

    /**
     * 创建宝可梦队伍配置
     *
     * @param pokemon 宝可梦名称
     * @param level 等级
     * @param ability 特性
     * @param heldItem 持有物品
     * @param nature 性格
     * @param moves 招式列表
     * @return 宝可梦配置字符串
     */
    public static String createPokemonSpec(String pokemon, int level, String ability, String heldItem,
            String nature, List<String> moves) {
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

        for (int i = 0; i < moves.size() && i < 4; i++) {
            spec.append(" move").append(i + 1).append(":").append(moves.get(i));
        }

        return spec.toString();
    }

    /** 创建宝可梦队伍配置（带个体值） */
    public static String createPokemonSpecWithIVs(String pokemon, int level, String ability, String heldItem,
            String nature, List<String> moves, Map<String, Integer> ivs) {
        String baseSpec = createPokemonSpec(pokemon, level, ability, heldItem, nature, moves);
        StringBuilder spec = new StringBuilder(baseSpec);

        for (Map.Entry<String, Integer> entry : ivs.entrySet()) {
            spec.append(" iv").append(entry.getKey()).append(":").append(entry.getValue());
        }

        return spec.toString();
    }

    /** 创建宝可梦队伍配置（带努力值） */
    public static String createPokemonSpecWithEVs(String pokemon, int level, String ability, String heldItem,
            String nature, List<String> moves, Map<String, Integer> evs) {
        String baseSpec = createPokemonSpec(pokemon, level, ability, heldItem, nature, moves);
        StringBuilder spec = new StringBuilder(baseSpec);

        for (Map.Entry<String, Integer> entry : evs.entrySet()) {
            spec.append(" ev").append(entry.getKey()).append(":").append(entry.getValue());
        }

        return spec.toString();
    }

    /** 创建玩家模型配置 */
    public static NPCDefinition.PlayerModel createPlayerModel(boolean slim, String textureResource) {
        return NPCDefinition.PlayerModel.of(slim, textureResource);
    }

    /** 创建玩家模型配置（带后备纹理） */
    public static NPCDefinition.PlayerModel createPlayerModel(boolean slim, String textureResource, String textureFallback) {
        return new NPCDefinition.PlayerModel(slim, textureResource, textureFallback);
    }

    // ==================== 私有辅助方法 ====================

    /** 创建带条件的道馆馆主交互（与dragon_1.json布局一致） */
    private JsonObject createGymLeaderInteractionsWithConditions(JsonObject title, JsonObject greeting,
            JsonObject winMessage, JsonObject loseMessage,
            double rewardMoney, List<JsonObject> rewardItems,
            int cooldownDays, String cooldownKey) {
        JsonObject interactions = new JsonObject();
        JsonArray interactionsArray = new JsonArray();

        // 右键点击交互（可战斗且不在冷却中）
        interactionsArray.add(createRightClickInteractionWithConditions(greeting, title, cooldownKey, cooldownDays));

        // 右键点击交互（在冷却中）
        interactionsArray.add(createCooldownInteraction(cooldownKey, cooldownDays));

        // 右键点击交互（无法战斗）
        interactionsArray.add(createUnableToBattleInteraction());

        // 关闭对话开始战斗
        interactionsArray.add(createCloseDialogueBattleInteraction());

        // 战斗胜利交互
        interactionsArray.add(createWinBattleInteractionWithRewards(winMessage, title, rewardMoney, rewardItems, cooldownKey, cooldownDays));

        // 战斗失败交互
        interactionsArray.add(createLoseBattleInteractionWithCooldown(loseMessage, title, cooldownKey, cooldownDays));

        interactions.addProperty("type", "pixelmon:constant");
        JsonObject value = new JsonObject();
        value.add("interactions", interactionsArray);
        interactions.add("value", value);

        return interactions;
    }

    private JsonObject createShopInteractions(JsonObject title, JsonObject greeting,
            JsonObject goodbye, List<JsonObject> shopItems) {
        JsonObject interactionsWrapper = new JsonObject();
        JsonArray interactionsArray = new JsonArray();

        // 右键点击打开对话
        interactionsArray.add(createRightClickInteraction(greeting, title));
        // 关闭对话打开商店
        interactionsArray.add(createOpenShopInteraction(shopItems));
        // 关闭商店显示告别语
        interactionsArray.add(createShopGoodbyeInteraction(goodbye, title));

        interactionsWrapper.add("interactions", interactionsArray);
        return interactionsWrapper;
    }

    /** 创建带随机组的提示交互（与aquaboss.json布局一致） */
    private List<JsonObject> createTipInteractionsWithRandomGroups(JsonObject title, List<
                    List<JsonObject>> messagesGroups) {
        JsonArray valuesArray = new JsonArray();

        for (List<JsonObject> messages : messagesGroups) {
            JsonObject interactionGroup = new JsonObject();
            JsonArray interactionsArray = new JsonArray();

            JsonObject interaction = new JsonObject();
            interaction.addProperty("event", "pixelmon:right_click");

            JsonObject conditions = new JsonObject();
            conditions.addProperty("type", "pixelmon:true");
            interaction.add("conditions", conditions);

            JsonObject results = new JsonObject();
            JsonArray resultsArray = new JsonArray();

            JsonObject dialogue = new JsonObject();
            dialogue.add("title", title);

            JsonArray pagesArray = new JsonArray();
            for (JsonObject message : messages) {
                pagesArray.add(message);
            }
            dialogue.add("pages", pagesArray);
            dialogue.addProperty("type", "pixelmon:open_paged_dialogue");

            resultsArray.add(dialogue);
            results.add("value", resultsArray);
            results.addProperty("type", "pixelmon:constant");
            interaction.add("results", results);

            interactionsArray.add(interaction);
            interactionGroup.add("interactions", interactionsArray);
            valuesArray.add(interactionGroup);
        }

        JsonObject interactionsWrapper = new JsonObject();
        interactionsWrapper.add("values", valuesArray);
        interactionsWrapper.addProperty("type", "pixelmon:uniformly_random");

        return List.of(interactionsWrapper);
    }

    private JsonObject createRightClickInteraction(JsonObject message, JsonObject title) {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:right_click");

        JsonObject conditions = new JsonObject();
        conditions.addProperty("type", "pixelmon:true");
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject dialogue = new JsonObject();
        dialogue.add("title", title);
        dialogue.add("message", message);
        dialogue.addProperty("type", "pixelmon:open_dialogue");
        resultsArray.add(dialogue);

        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

    /** 创建带条件的右键点击交互 */
    private JsonObject createRightClickInteractionWithConditions(JsonObject message, JsonObject title, String cooldownKey, int cooldownDays) {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:right_click");

        JsonArray conditions = new JsonArray();

        // 手部使用条件
        JsonObject handCondition = new JsonObject();
        handCondition.addProperty("type", "pixelmon:interaction_condition");
        JsonObject handCompare = new JsonObject();
        handCompare.addProperty("type", "pixelmon:string_compare");

        JsonObject firstHand = new JsonObject();
        firstHand.addProperty("value", "MAIN_HAND");
        firstHand.addProperty("type", "pixelmon:constant_string");
        handCompare.add("first", firstHand);

        JsonObject secondHand = new JsonObject();
        secondHand.addProperty("type", "pixelmon:hand_used");
        handCompare.add("second", secondHand);

        handCondition.add("condition", handCompare);
        conditions.add(handCondition);

        // 可战斗条件
        JsonObject battleCondition = new JsonObject();
        battleCondition.addProperty("type", "pixelmon:interaction_condition");
        JsonObject canBattle = new JsonObject();
        canBattle.addProperty("type", "pixelmon:can_battle");

        JsonObject player = new JsonObject();
        player.addProperty("key", "pixelmon:player");
        player.addProperty("type", "pixelmon:context_player");
        canBattle.add("player", player);

        battleCondition.add("condition", canBattle);
        conditions.add(battleCondition);

        // 不在冷却中条件
        JsonObject cooldownCondition = new JsonObject();
        cooldownCondition.addProperty("type", "pixelmon:interaction_condition");
        JsonObject notCondition = new JsonObject();
        notCondition.addProperty("type", "pixelmon:logical_not");

        JsonObject cooldownSubCondition = new JsonObject();
        cooldownSubCondition.addProperty("type", "pixelmon:interaction_condition");
        JsonObject onCooldown = new JsonObject();
        onCooldown.addProperty("type", "pixelmon:on_cooldown");
        onCooldown.add("player", player);
        onCooldown.addProperty("cooldown_key", "pixelmon:" + cooldownKey);
        onCooldown.addProperty("cooldown", cooldownDays);
        onCooldown.addProperty("unit", "DAYS");
        cooldownSubCondition.add("condition", onCooldown);
        notCondition.add("condition", cooldownSubCondition);

        cooldownCondition.add("condition", notCondition);
        conditions.add(cooldownCondition);

        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject dialogue = new JsonObject();
        dialogue.addProperty("type", "pixelmon:open_dialogue");
        dialogue.add("title", title);
        dialogue.add("message", message);
        resultsArray.add(dialogue);

        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

    /** 创建冷却中交互 */
    private JsonObject createCooldownInteraction(String cooldownKey, int cooldownDays) {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:right_click");

        JsonArray conditions = new JsonArray();

        // 手部使用条件
        JsonObject handCondition = new JsonObject();
        handCondition.addProperty("type", "pixelmon:interaction_condition");
        JsonObject handCompare = new JsonObject();
        handCompare.addProperty("type", "pixelmon:string_compare");

        JsonObject firstHand = new JsonObject();
        firstHand.addProperty("value", "MAIN_HAND");
        firstHand.addProperty("type", "pixelmon:constant_string");
        handCompare.add("first", firstHand);

        JsonObject secondHand = new JsonObject();
        secondHand.addProperty("type", "pixelmon:hand_used");
        handCompare.add("second", secondHand);

        handCondition.add("condition", handCompare);
        conditions.add(handCondition);

        // 在冷却中条件
        JsonObject cooldownCondition = new JsonObject();
        cooldownCondition.addProperty("type", "pixelmon:interaction_condition");
        JsonObject onCooldown = new JsonObject();
        onCooldown.addProperty("type", "pixelmon:on_cooldown");

        JsonObject player = new JsonObject();
        player.addProperty("key", "pixelmon:player");
        player.addProperty("type", "pixelmon:context_player");
        onCooldown.add("player", player);
        onCooldown.addProperty("cooldown_key", "pixelmon:" + cooldownKey);
        onCooldown.addProperty("cooldown", cooldownDays);
        onCooldown.addProperty("unit", "DAYS");

        cooldownCondition.add("condition", onCooldown);
        conditions.add(cooldownCondition);

        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject message = new JsonObject();
        message.addProperty("type", "pixelmon:message_player");
        JsonArray messages = new JsonArray();
        JsonObject msg = new JsonObject();
        msg.addProperty("translate", "pixelmon.npc.dialogue.battle.leader.gym." + cooldownKey + ".cooldown");
        messages.add(msg);
        message.add("messages", messages);
        resultsArray.add(message);

        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

    /** 创建无法战斗交互 */
    private JsonObject createUnableToBattleInteraction() {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:right_click");

        JsonArray conditions = new JsonArray();

        // 手部使用条件
        JsonObject handCondition = new JsonObject();
        handCondition.addProperty("type", "pixelmon:interaction_condition");
        JsonObject handCompare = new JsonObject();
        handCompare.addProperty("type", "pixelmon:string_compare");

        JsonObject firstHand = new JsonObject();
        firstHand.addProperty("value", "MAIN_HAND");
        firstHand.addProperty("type", "pixelmon:constant_string");
        handCompare.add("first", firstHand);

        JsonObject secondHand = new JsonObject();
        secondHand.addProperty("type", "pixelmon:hand_used");
        handCompare.add("second", secondHand);

        handCondition.add("condition", handCompare);
        conditions.add(handCondition);

        // 无法战斗条件
        JsonObject battleCondition = new JsonObject();
        battleCondition.addProperty("type", "pixelmon:interaction_condition");
        JsonObject notCondition = new JsonObject();
        notCondition.addProperty("type", "pixelmon:logical_not");

        JsonObject battleSubCondition = new JsonObject();
        battleSubCondition.addProperty("type", "pixelmon:interaction_condition");
        JsonObject canBattle = new JsonObject();
        canBattle.addProperty("type", "pixelmon:can_battle");

        JsonObject player = new JsonObject();
        player.addProperty("key", "pixelmon:player");
        player.addProperty("type", "pixelmon:context_player");
        canBattle.add("player", player);

        battleSubCondition.add("condition", canBattle);
        notCondition.add("condition", battleSubCondition);

        battleCondition.add("condition", notCondition);
        conditions.add(battleCondition);

        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject message = new JsonObject();
        message.addProperty("type", "pixelmon:message_player");
        JsonArray messages = new JsonArray();
        JsonObject msg = new JsonObject();
        msg.addProperty("translate", "pixelmon.npc.dialogue.battle.leader.gym.unable_to_battle");
        messages.add(msg);
        message.add("messages", messages);
        resultsArray.add(message);

        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

    private JsonObject createCloseDialogueBattleInteraction() {
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

    /** 创建带奖励的胜利战斗交互 */
    private JsonObject createWinBattleInteractionWithRewards(JsonObject message, JsonObject title,
            double rewardMoney, List<JsonObject> rewardItems,
            String cooldownKey, int cooldownDays) {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:win_battle");

        JsonObject conditions = new JsonObject();
        conditions.addProperty("type", "pixelmon:true");
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        // 胜利对话
        JsonObject dialogue = new JsonObject();
        dialogue.addProperty("type", "pixelmon:open_dialogue");
        dialogue.add("title", title);
        dialogue.add("message", message);
        dialogue.addProperty("fire_close_event", false);
        resultsArray.add(dialogue);

        // 金钱奖励
        if (rewardMoney > 0) {
            JsonObject moneyReward = new JsonObject();
            moneyReward.addProperty("money", rewardMoney);
            moneyReward.addProperty("type", "pixelmon:give_money");
            resultsArray.add(moneyReward);
        }

        // 物品奖励
        if (!rewardItems.isEmpty()) {
            JsonObject itemReward = new JsonObject();
            itemReward.addProperty("type", "pixelmon:give_item");

            JsonArray itemsArray = new JsonArray();
            for (JsonObject item : rewardItems) {
                itemsArray.add(item);
            }
            itemReward.add("items", itemsArray);
            resultsArray.add(itemReward);
        }

        // 设置冷却
        JsonObject cooldown = new JsonObject();
        cooldown.addProperty("type", "pixelmon:set_cooldown");

        JsonObject player = new JsonObject();
        player.addProperty("key", "pixelmon:player");
        player.addProperty("type", "pixelmon:context_player");
        cooldown.add("player", player);

        cooldown.addProperty("key", "pixelmon:" + cooldownKey);
        resultsArray.add(cooldown);

        // 设置字符串上下文
        JsonObject stringContext = new JsonObject();
        stringContext.addProperty("type", "pixelmon:set_string_context");
        stringContext.addProperty("key", "pixelmon:leader");
        stringContext.addProperty("value", cooldownKey);
        resultsArray.add(stringContext);

        // 触发击败首领事件
        JsonObject defeatEvent = new JsonObject();
        defeatEvent.addProperty("type", "pixelmon:trigger_interaction_event");
        defeatEvent.addProperty("event", "pixelmon:defeat_leader");
        resultsArray.add(defeatEvent);

        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

    /** 创建带冷却的失败战斗交互 */
    private JsonObject createLoseBattleInteractionWithCooldown(JsonObject message, JsonObject title, String cooldownKey, int cooldownDays) {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:lose_battle");

        JsonObject conditions = new JsonObject();
        conditions.addProperty("type", "pixelmon:true");
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        // 失败对话
        JsonObject dialogue = new JsonObject();
        dialogue.addProperty("type", "pixelmon:open_dialogue");
        dialogue.add("title", title);
        dialogue.add("message", message);
        dialogue.addProperty("fire_close_event", false);
        resultsArray.add(dialogue);

        // 设置字符串上下文
        JsonObject stringContext = new JsonObject();
        stringContext.addProperty("type", "pixelmon:set_string_context");
        stringContext.addProperty("key", "pixelmon:leader");
        stringContext.addProperty("value", cooldownKey);
        resultsArray.add(stringContext);

        // 触发输给首领事件
        JsonObject loseEvent = new JsonObject();
        loseEvent.addProperty("type", "pixelmon:trigger_interaction_event");
        loseEvent.addProperty("event", "pixelmon:lose_to_leader");
        resultsArray.add(loseEvent);

        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

    private JsonObject createOpenShopInteraction(List<JsonObject> shopItems) {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:close_dialogue");

        JsonObject conditions = new JsonObject();
        conditions.addProperty("type", "pixelmon:true");
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject shop = new JsonObject();
        shop.addProperty("type", "pixelmon:open_shop");

        JsonArray itemsArray = new JsonArray();
        for (JsonObject item : shopItems) {
            itemsArray.add(item);
        }
        shop.add("items", itemsArray);

        resultsArray.add(shop);
        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

    private JsonObject createShopGoodbyeInteraction(JsonObject message, JsonObject title) {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:close_shop");

        JsonObject conditions = new JsonObject();
        conditions.addProperty("type", "pixelmon:true");
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject dialogue = new JsonObject();
        dialogue.addProperty("type", "pixelmon:open_dialogue");
        dialogue.add("title", title);
        dialogue.add("message", message);
        dialogue.addProperty("fire_close_event", false);
        resultsArray.add(dialogue);

        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

    /** 重新组织道馆馆主JSON结构以匹配dragon_1.json */
    private void reorganizeGymLeaderJson(JsonObject npcDefinition) {
        // 重新排序字段以匹配dragon_1.json的布局
        JsonObject reordered = new JsonObject();

        // 按照dragon_1.json的顺序添加字段
        if (npcDefinition.has("properties"))
            reordered.add("properties", npcDefinition.get("properties"));
        if (npcDefinition.has("names")) reordered.add("names", npcDefinition.get("names"));
        if (npcDefinition.has("party")) reordered.add("party", npcDefinition.get("party"));
        if (npcDefinition.has("models")) reordered.add("models", npcDefinition.get("models"));
        if (npcDefinition.has("goals")) reordered.add("goals", npcDefinition.get("goals"));
        if (npcDefinition.has("interactions"))
            reordered.add("interactions", npcDefinition.get("interactions"));

        // 清空原对象并添加重新排序的字段
        npcDefinition.entrySet().clear();
        npcDefinition.entrySet().addAll(reordered.entrySet());
    }

    /** 重新组织商店NPC JSON结构以匹配evostones_1.json */
    private void reorganizeShopKeeperJson(JsonObject npcDefinition) {
        // 重新排序字段以匹配evostones_1.json的布局
        JsonObject reordered = new JsonObject();

        // 按照evostones_1.json的顺序添加字段
        if (npcDefinition.has("interactions"))
            reordered.add("interactions", npcDefinition.get("interactions"));
        if (npcDefinition.has("properties"))
            reordered.add("properties", npcDefinition.get("properties"));
        if (npcDefinition.has("party")) reordered.add("party", npcDefinition.get("party"));
        if (npcDefinition.has("names")) reordered.add("names", npcDefinition.get("names"));
        if (npcDefinition.has("models")) reordered.add("models", npcDefinition.get("models"));
        if (npcDefinition.has("goals")) reordered.add("goals", npcDefinition.get("goals"));

        // 清空原对象并添加重新排序的字段
        npcDefinition.entrySet().clear();
        npcDefinition.entrySet().addAll(reordered.entrySet());
    }

    /** 重新组织提示NPC JSON结构以匹配aquaboss.json */
    private void reorganizeTipNPCJson(JsonObject npcDefinition) {
        // 重新排序字段以匹配aquaboss.json的布局
        JsonObject reordered = new JsonObject();

        // 按照aquaboss.json的顺序添加字段
        if (npcDefinition.has("interactions"))
            reordered.add("interactions", npcDefinition.get("interactions"));
        if (npcDefinition.has("properties"))
            reordered.add("properties", npcDefinition.get("properties"));
        if (npcDefinition.has("party")) reordered.add("party", npcDefinition.get("party"));
        if (npcDefinition.has("names")) reordered.add("names", npcDefinition.get("names"));
        if (npcDefinition.has("models")) reordered.add("models", npcDefinition.get("models"));
        if (npcDefinition.has("goals")) reordered.add("goals", npcDefinition.get("goals"));

        // 清空原对象并添加重新排序的字段
        npcDefinition.entrySet().clear();
        npcDefinition.entrySet().addAll(reordered.entrySet());
    }

    /** 创建宝可梦中心交互（医生/护士专用） */
    private List<
                    JsonObject> createPokeCenterInteractions(String titleTranslate, String greetingTranslate, String goodbyeTranslate) {
        JsonObject interactionsWrapper = new JsonObject();
        JsonArray valuesArray = new JsonArray();

        JsonObject interactionGroup = new JsonObject();
        JsonArray interactionsArray = new JsonArray();

        // 右键点击交互
        interactionsArray.add(createPokeCenterRightClickInteraction(titleTranslate, greetingTranslate));

        // 关闭对话开始治疗
        interactionsArray.add(createPokeCenterHealInteraction());

        // 治疗完成显示告别语
        interactionsArray.add(createPokeCenterGoodbyeInteraction(titleTranslate, goodbyeTranslate));

        interactionGroup.add("interactions", interactionsArray);
        valuesArray.add(interactionGroup);

        interactionsWrapper.add("values", valuesArray);
        interactionsWrapper.addProperty("type", "pixelmon:uniformly_random");

        return List.of(interactionsWrapper);
    }

/** 创建宝可梦中心右键点击交互 */
    private JsonObject createPokeCenterRightClickInteraction(String titleTranslate, String greetingTranslate) {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:right_click");

        JsonObject conditions = new JsonObject();
        conditions.addProperty("type", "pixelmon:true");
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject dialogue = new JsonObject();
        dialogue.addProperty("type", "pixelmon:open_dialogue");
        dialogue.addProperty("title", titleTranslate);
        dialogue.addProperty("message", greetingTranslate);
        resultsArray.add(dialogue);

        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

/** 创建宝可梦中心治疗交互 */
    private JsonObject createPokeCenterHealInteraction() {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:close_dialogue");

        JsonObject conditions = new JsonObject();
        conditions.addProperty("type", "pixelmon:true");
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject heal = new JsonObject();
        heal.addProperty("type", "pixelmon:heal_player");

        JsonObject requireHealerBlock = new JsonObject();
        requireHealerBlock.addProperty("value", true);
        requireHealerBlock.addProperty("type", "pixelmon:constant_boolean");
        heal.add("require_healer_block", requireHealerBlock);

        resultsArray.add(heal);

        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

/** 创建宝可梦中心告别交互 */
    private JsonObject createPokeCenterGoodbyeInteraction(String titleTranslate, String goodbyeTranslate) {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:finish_healing");

        JsonObject conditions = new JsonObject();
        conditions.addProperty("type", "pixelmon:true");
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject dialogue = new JsonObject();
        dialogue.addProperty("type", "pixelmon:open_dialogue");
        dialogue.addProperty("title", titleTranslate);
        dialogue.addProperty("message", goodbyeTranslate);
        dialogue.addProperty("fire_close_event", false);
        resultsArray.add(dialogue);

        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

/** 重新组织宝可梦中心NPC JSON结构以匹配doctor_john.json和nurse_joy.json的布局 */
    private void reorganizePokeCenterJson(JsonObject npcDefinition) {
        // 重新排序字段以匹配宝可梦中心NPC的布局
        JsonObject reordered = new JsonObject();

        // 按照doctor_john.json和nurse_joy.json的顺序添加字段
        if (npcDefinition.has("names")) reordered.add("names", npcDefinition.get("names"));
        if (npcDefinition.has("models")) reordered.add("models", npcDefinition.get("models"));
        if (npcDefinition.has("interactions"))
            reordered.add("interactions", npcDefinition.get("interactions"));
        if (npcDefinition.has("properties"))
            reordered.add("properties", npcDefinition.get("properties"));
        if (npcDefinition.has("party")) reordered.add("party", npcDefinition.get("party"));
        if (npcDefinition.has("goals")) reordered.add("goals", npcDefinition.get("goals"));

        // 清空原对象并添加重新排序的字段
        npcDefinition.entrySet().clear();
        npcDefinition.entrySet().addAll(reordered.entrySet());
    }
}