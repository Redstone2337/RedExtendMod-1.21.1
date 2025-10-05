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

    private final Map<String, JsonObject> npcs = new LinkedHashMap<>();

    /**
     * 创建此数据提供者的新实例。
     *
     * @param output 数据生成器提供的 {@linkplain PackOutput} 实例。
     * @param modId  当前模组的模组ID。
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
     * @param modId  当前模组的模组ID。
     * @param subDirectory 放置文件的子目录。
     * @param customOutputPath 自定义输出路径，如果为null则使用默认路径 data/modid/pixelmon/npc/preset/。
     */
    protected PixelmonNPCProvider(final PackOutput output, final String modId, final String subDirectory, final String customOutputPath) {
        this.output = output;
        this.modId = modId;
        this.subDirectory = subDirectory;
        this.customOutputPath = customOutputPath;
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

    /**
     * 创建一个新的空NPC定义构建器。
     */
    protected static NPCDefinition.Builder definition() {
        return NPCDefinition.builder();
    }

    // ==================== 快速生成方法 ====================

    /**
     * 快速生成馆主NPC
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
     */
    protected void addGymLeader(String fileName, String npcName, JsonObject title,
                                JsonObject greeting, JsonObject winMessage, JsonObject loseMessage,
                                List<String> pokemonSpecs, double rewardMoney,
                                List<JsonObject> rewardItems, int cooldownDays, String texture) {

        JsonObject npcDefinition = definition()
                .withConstantInteractions(createGymLeaderInteractions(title, greeting, winMessage,
                        loseMessage, rewardMoney, rewardItems, cooldownDays))
                .withTitleProperties(20.0f, 1.9f, 0.65f, 2.0f, title, false, false, false, false, true)
                .withSpecParty(pokemonSpecs)
                .withSingleName(npcName)
                .withSinglePlayerModel(false, texture)
                .withLookAtNearbyGoal(8.0f, 0.9f, 1)
                .build()
                .serialize();

        this.add(fileName, npcDefinition);
    }

    /**
     * 快速生成馆主NPC（简化版，使用翻译键作为消息）
     */
    protected void addGymLeader(String fileName, String npcName, String titleTranslate,
                                String greetingTranslate, String winMessageTranslate, String loseMessageTranslate,
                                List<String> pokemonSpecs, double rewardMoney,
                                List<JsonObject> rewardItems, int cooldownDays, String texture) {

        JsonObject title = createTranslateTitle(titleTranslate, "#FF6B35", true, false, false);
        JsonObject greeting = createTranslateMessage(greetingTranslate);
        JsonObject winMessage = createTranslateMessage(winMessageTranslate);
        JsonObject loseMessage = createTranslateMessage(loseMessageTranslate);

        addGymLeader(fileName, npcName, title, greeting, winMessage, loseMessage,
                pokemonSpecs, rewardMoney, rewardItems, cooldownDays, texture);
    }

    /**
     * 快速生成商店NPC
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

        this.add(fileName, npcDefinition);
    }

    /**
     * 快速生成商店NPC（简化版，使用翻译键作为消息）
     */
    protected void addShopKeeper(String fileName, List<String> npcNames, String titleTranslate,
                                 String greetingTranslate, String goodbyeTranslate, List<JsonObject> shopItems,
                                 List<String> textureResources) {

        JsonObject title = createTranslateTitle(titleTranslate, "#2176FF", true, false, false);
        JsonObject greeting = createTranslateMessage(greetingTranslate);
        JsonObject goodbye = createTranslateMessage(goodbyeTranslate);

        addShopKeeper(fileName, npcNames, title, greeting, goodbye, shopItems, textureResources);
    }

    /**
     * 快速生成提示NPC
     *
     * @param fileName 文件名
     * @param npcNames NPC名称列表
     * @param title 标题配置（JsonObject，支持translate或text）
     * @param messages 消息配置列表（JsonObject，支持translate或text）
     * @param textureResources 纹理资源列表
     */
    protected void addTipNPC(String fileName, List<String> npcNames, JsonObject title,
                             List<JsonObject> messages, List<String> textureResources) {

        List<NPCDefinition.PlayerModel> models = textureResources.stream()
                .map(texture -> NPCDefinition.PlayerModel.of(false, texture))
                .toList();

        JsonObject npcDefinition = definition()
                .withUniformInteractions(List.of(createTipInteractions(title, messages)))
                .withTitleProperties(20.0f, 1.0f, 1.0f, 2.0f, title, false, false, false, false, true)
                .withEmptyParty()
                .withRandomNames(npcNames)
                .withRandomPlayerModels(models)
                .withLookAtNearbyGoal(4.0f, 0.9f, 1)
                .build()
                .serialize();

        this.add(fileName, npcDefinition);
    }

    /**
     * 快速生成提示NPC（简化版，使用翻译键作为消息）
     */
    protected void addTipNPC(String fileName, List<String> npcNames, String titleTranslate,
                             List<String> messageTranslates, List<String> textureResources) {

        JsonObject title = createTranslateTitle(titleTranslate, "#F8F8F2", true, false, false);
        List<JsonObject> messages = messageTranslates.stream()
                .map(PixelmonNPCProvider::createTranslateMessage)
                .toList();

        addTipNPC(fileName, npcNames, title, messages, textureResources);
    }

    // ==================== 辅助类方法 ====================

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
     * 创建使用翻译键的消息
     */
    public static JsonObject createTranslateMessage(String translate) {
        JsonObject message = new JsonObject();
        message.addProperty("translate", translate);
        return message;
    }

    /**
     * 创建使用直接文本的消息
     */
    public static JsonObject createTextMessage(String text) {
        JsonObject message = new JsonObject();
        message.addProperty("text", text);
        return message;
    }

    /**
     * 创建简单的文本消息
     */
    public static JsonObject createSimpleTextMessage(String text) {
        return createTextMessage(text);
    }

    /**
     * 创建简单的翻译消息
     */
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

    /**
     * 创建宝可梦队伍配置（带个体值）
     */
    public static String createPokemonSpecWithIVs(String pokemon, int level, String ability, String heldItem,
                                                  String nature, List<String> moves, Map<String, Integer> ivs) {
        String baseSpec = createPokemonSpec(pokemon, level, ability, heldItem, nature, moves);
        StringBuilder spec = new StringBuilder(baseSpec);

        for (Map.Entry<String, Integer> entry : ivs.entrySet()) {
            spec.append(" iv").append(entry.getKey()).append(":").append(entry.getValue());
        }

        return spec.toString();
    }

    /**
     * 创建宝可梦队伍配置（带努力值）
     */
    public static String createPokemonSpecWithEVs(String pokemon, int level, String ability, String heldItem,
                                                  String nature, List<String> moves, Map<String, Integer> evs) {
        String baseSpec = createPokemonSpec(pokemon, level, ability, heldItem, nature, moves);
        StringBuilder spec = new StringBuilder(baseSpec);

        for (Map.Entry<String, Integer> entry : evs.entrySet()) {
            spec.append(" ev").append(entry.getKey()).append(":").append(entry.getValue());
        }

        return spec.toString();
    }

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

    // ==================== 私有辅助方法 ====================

    private JsonObject createGymLeaderInteractions(JsonObject title, JsonObject greeting,
                                                   JsonObject winMessage, JsonObject loseMessage,
                                                   double rewardMoney, List<JsonObject> rewardItems,
                                                   int cooldownDays) {
        JsonObject interactions = new JsonObject();
        JsonArray interactionsArray = new JsonArray();

        // 右键点击交互
        interactionsArray.add(createRightClickInteraction(greeting, title));
        // 关闭对话开始战斗
        interactionsArray.add(createCloseDialogueBattleInteraction());
        // 战斗胜利交互
        interactionsArray.add(createWinBattleInteraction(winMessage, title, rewardMoney, rewardItems, cooldownDays));
        // 战斗失败交互
        interactionsArray.add(createLoseBattleInteraction(loseMessage, title, cooldownDays));

        interactions.add("interactions", interactionsArray);
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

    private JsonObject createTipInteractions(JsonObject title, List<JsonObject> messages) {
        JsonObject interactionsWrapper = new JsonObject();
        JsonArray interactionsArray = new JsonArray();

        // 右键点击打开多页对话
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:right_click");

        JsonObject conditions = new JsonObject();
        conditions.addProperty("type", "pixelmon:true");
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        JsonObject dialogue = new JsonObject();
        dialogue.add("title", title); // 使用传入的标题对象

        JsonArray pagesArray = new JsonArray();
        for (JsonObject message : messages) {
            pagesArray.add(message); // 使用传入的消息对象
        }
        dialogue.add("pages", pagesArray);
        dialogue.addProperty("type", "pixelmon:open_paged_dialogue");

        resultsArray.add(dialogue);
        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        interactionsArray.add(interaction);
        interactionsWrapper.add("interactions", interactionsArray);
        return interactionsWrapper;
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
        dialogue.add("title", title); // 使用传入的标题对象
        dialogue.add("message", message); // 使用传入的消息对象
        dialogue.addProperty("type", "pixelmon:open_dialogue");
        resultsArray.add(dialogue);

        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

    private JsonObject createCloseDialogueBattleInteraction() {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:close_dialogue");

        JsonObject conditions = new JsonObject();
        conditions.addProperty("type", "pixelmon:true");
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

    private JsonObject createWinBattleInteraction(JsonObject message, JsonObject title,
                                                  double rewardMoney, List<JsonObject> rewardItems,
                                                  int cooldownDays) {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:win_battle");

        JsonObject conditions = new JsonObject();
        conditions.addProperty("type", "pixelmon:true");
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        // 胜利对话
        JsonObject dialogue = new JsonObject();
        dialogue.add("title", title); // 使用传入的标题对象
        dialogue.add("message", message); // 使用传入的消息对象
        dialogue.addProperty("fire_close_event", false);
        dialogue.addProperty("type", "pixelmon:open_dialogue");
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

        // 触发训练师击败事件
        JsonObject defeatEvent = new JsonObject();
        defeatEvent.addProperty("type", "pixelmon:trigger_interaction_event");
        defeatEvent.addProperty("event", "pixelmon:defeat_trainer");
        resultsArray.add(defeatEvent);

        // 设置冷却
        JsonObject cooldown = new JsonObject();
        cooldown.addProperty("type", "pixelmon:set_cooldown");

        JsonObject player = new JsonObject();
        player.addProperty("key", "pixelmon:player");
        player.addProperty("type", "pixelmon:context_player");
        cooldown.add("player", player);

        cooldown.addProperty("key", "pixelmon:gym_leader");
        cooldown.addProperty("cooldown", cooldownDays);
        cooldown.addProperty("unit", "DAYS");
        resultsArray.add(cooldown);

        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }

    private JsonObject createLoseBattleInteraction(JsonObject message, JsonObject title, int cooldownDays) {
        JsonObject interaction = new JsonObject();
        interaction.addProperty("event", "pixelmon:lose_battle");

        JsonObject conditions = new JsonObject();
        conditions.addProperty("type", "pixelmon:true");
        interaction.add("conditions", conditions);

        JsonObject results = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        // 失败对话
        JsonObject dialogue = new JsonObject();
        dialogue.add("title", title); // 使用传入的标题对象
        dialogue.add("message", message); // 使用传入的消息对象
        dialogue.addProperty("fire_close_event", false);
        dialogue.addProperty("type", "pixelmon:open_dialogue");
        resultsArray.add(dialogue);

        // 设置冷却
        JsonObject cooldown = new JsonObject();
        cooldown.addProperty("type", "pixelmon:set_cooldown");

        JsonObject player = new JsonObject();
        player.addProperty("key", "pixelmon:player");
        player.addProperty("type", "pixelmon:context_player");
        cooldown.add("player", player);

        cooldown.addProperty("key", "pixelmon:gym_leader");
        cooldown.addProperty("cooldown", cooldownDays);
        cooldown.addProperty("unit", "DAYS");
        resultsArray.add(cooldown);

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

        JsonArray itemsArray = new JsonArray();
        for (JsonObject item : shopItems) {
            itemsArray.add(item);
        }
        shop.add("items", itemsArray);
        shop.addProperty("type", "pixelmon:open_shop");

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
        dialogue.add("title", title); // 使用传入的标题对象
        dialogue.add("message", message); // 使用传入的消息对象
        dialogue.addProperty("fire_close_event", false);
        dialogue.addProperty("type", "pixelmon:open_dialogue");
        resultsArray.add(dialogue);

        results.add("value", resultsArray);
        results.addProperty("type", "pixelmon:constant");
        interaction.add("results", results);

        return interaction;
    }
}