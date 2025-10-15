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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
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

    /**
     * 创建此数据提供者的新实例。
     *
     * @param output 数据生成器提供的 {@linkplain PackOutput} 实例。
     * @param modId  当前模组的模组ID。
     * @param subDirectory 在 data/modid/pixelmon/npc/preset/ 中放置文件的子目录。
     */
    protected PixelmonNPCProvider(final PackOutput output, final String modId, final String subDirectory) {
        this(output, modId, subDirectory, null, NPCDefinition.SaveMode.UNORDERED_FORMATTED);
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
        this(output, modId, subDirectory, customOutputPath, NPCDefinition.SaveMode.UNORDERED_FORMATTED);
    }

    /**
     * 创建此数据提供者的新实例，支持自定义保存模式。
     *
     * @param output 数据生成器提供的 {@linkplain PackOutput} 实例。
     * @param modId  当前模组的模组ID。
     * @param subDirectory 放置文件的子目录。
     * @param saveMode JSON保存模式。
     */
    protected PixelmonNPCProvider(final PackOutput output, final String modId, final String subDirectory, final NPCDefinition.SaveMode saveMode) {
        this(output, modId, subDirectory, null, saveMode);
    }

    /**
     * 创建此数据提供者的新实例，支持自定义输出路径和保存模式。
     *
     * @param output 数据生成器提供的 {@linkplain PackOutput} 实例。
     * @param modId  当前模组的模组ID。
     * @param subDirectory 放置文件的子目录。
     * @param customOutputPath 自定义输出路径，如果为null则使用默认路径 data/modid/pixelmon/npc/preset/。
     * @param saveMode JSON保存模式。
     */
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

    // ==================== 自定义商店NPC生成方法 ====================

    /**
     * 生成自定义商店NPC，结构与evostones_1.json完全一致
     *
     * @param fileName 文件名
     * @param npcNames NPC名称列表
     * @param propertyTitle 属性标题配置（显示在名称牌上）
     * @param interactionTitle 交互标题配置（显示在对话框上）
     * @param greeting 问候语配置
     * @param goodbye 告别语配置
     * @param shopItems 商店物品列表
     * @param textureResources 纹理资源列表
     * @param lookDistance 查看距离
     * @param lookProbability 查看概率
     */
    protected void addCustomShopNPC(String fileName, List<String> npcNames,
                                    JsonObject propertyTitle, JsonObject interactionTitle,
                                    JsonObject greeting, JsonObject goodbye,
                                    List<JsonObject> shopItems, List<String> textureResources,
                                    float lookDistance, float lookProbability) {

        // 创建玩家模型列表，使用完整的fallback结构
        List<NPCDefinition.PlayerModel> models = textureResources.stream()
                .map(texture -> createPlayerModelWithFallback(false, texture, texture))
                .toList();

        JsonObject npcDefinition = definition()
                .withUniformInteractions(List.of(createCustomShopInteractions(interactionTitle, greeting, goodbye, shopItems)))
                .withTitleProperties(20.0f, 1.0f, 1.0f, 2.0f, propertyTitle, false, false, false, false, true)
                .withEmptyParty()
                .withRandomNames(npcNames)
                .withRandomPlayerModels(models)
                .withLookAtNearbyGoal(lookDistance, lookProbability, 1)
                .build()
                .serialize();

        this.add(fileName, npcDefinition);
    }

    /**
     * 生成自定义商店NPC（简化版，使用翻译键）
     */
    protected void addCustomShopNPC(String fileName, List<String> npcNames,
                                    String propertyTitleTranslate, String interactionTitleTranslate,
                                    String greetingTranslate, String goodbyeTranslate,
                                    List<JsonObject> shopItems, List<String> textureResources) {

        JsonObject propertyTitle = createTranslateTitle(propertyTitleTranslate, "#2176FF", true, false, false);
        JsonObject interactionTitle = createTranslateTitle(interactionTitleTranslate, "#2176FF", true, false, false);
        JsonObject greeting = createTranslateMessage(greetingTranslate);
        JsonObject goodbye = createTranslateMessage(goodbyeTranslate);

        addCustomShopNPC(fileName, npcNames, propertyTitle, interactionTitle,
                greeting, goodbye, shopItems, textureResources, 5.0f, 0.9f);
    }

    // ==================== 其他快速生成方法 ====================

    /**
     * 快速生成多页提示NPC（类似 ivs_basic_m.json）
     */
    protected void addMultiPageTipNPC(String fileName, List<String> npcNames,
                                      JsonObject propertyTitle, JsonObject interactionTitle,
                                      List<JsonObject> messagePages, List<String> textureResources,
                                      float lookDistance, float lookProbability) {

        List<NPCDefinition.PlayerModel> models = textureResources.stream()
                .map(texture -> createPlayerModelWithFallback(false, texture, texture))
                .toList();

        JsonObject npcDefinition = definition()
                .withUniformInteractions(List.of(createMultiPageTipInteractions(interactionTitle, messagePages)))
                .withTitleProperties(20.0f, 1.0f, 1.0f, 2.0f, propertyTitle, false, false, false, false, true)
                .withEmptyParty()
                .withRandomNames(npcNames)
                .withRandomPlayerModels(models)
                .withLookAtNearbyGoal(lookDistance, lookProbability, 1)
                .build()
                .serialize();

        this.add(fileName, npcDefinition);
    }

    /**
     * 快速生成多页提示NPC（简化版）
     */
    protected void addMultiPageTipNPC(String fileName, List<String> npcNames,
                                      String propertyTitleTranslate, String interactionTitleTranslate,
                                      List<String> messageTranslates, List<String> textureResources) {

        JsonObject propertyTitle = createTranslateTitle(propertyTitleTranslate, "#F8F8F2", true, false, false);
        JsonObject interactionTitle = createTranslateTitle(interactionTitleTranslate, "#F8F8F2", true, false, false);
        List<JsonObject> messagePages = messageTranslates.stream()
                .map(PixelmonNPCProvider::createTranslateMessage)
                .toList();

        addMultiPageTipNPC(fileName, npcNames, propertyTitle, interactionTitle,
                messagePages, textureResources, 4.0f, 0.9f);
    }

    /**
     * 快速生成进化石商店NPC（类似 evostones_1.json）
     */
    protected void addEvolutionStoneShop(String fileName, List<String> npcNames,
                                         JsonObject propertyTitle, JsonObject interactionTitle,
                                         JsonObject greeting, JsonObject goodbye,
                                         List<JsonObject> shopItems, List<String> textureResources,
                                         float lookDistance, float lookProbability) {

        List<NPCDefinition.PlayerModel> models = textureResources.stream()
                .map(texture -> createPlayerModelWithFallback(false, texture, texture))
                .toList();

        JsonObject npcDefinition = definition()
                .withUniformInteractions(List.of(createCustomShopInteractions(interactionTitle, greeting, goodbye, shopItems)))
                .withTitleProperties(20.0f, 1.0f, 1.0f, 2.0f, propertyTitle, false, false, false, false, true)
                .withEmptyParty()
                .withRandomNames(npcNames)
                .withRandomPlayerModels(models)
                .withLookAtNearbyGoal(lookDistance, lookProbability, 1)
                .build()
                .serialize();

        this.add(fileName, npcDefinition);
    }

    /**
     * 快速生成进化石商店NPC（简化版）
     */
    protected void addEvolutionStoneShop(String fileName, List<String> npcNames,
                                         String propertyTitleTranslate, String interactionTitleTranslate,
                                         String greetingTranslate, String goodbyeTranslate,
                                         List<JsonObject> shopItems, List<String> textureResources) {

        JsonObject propertyTitle = createTranslateTitle(propertyTitleTranslate, "#2176FF", true, false, false);
        JsonObject interactionTitle = createTranslateTitle(interactionTitleTranslate, "#2176FF", true, false, false);
        JsonObject greeting = createTranslateMessage(greetingTranslate);
        JsonObject goodbye = createTranslateMessage(goodbyeTranslate);

        addEvolutionStoneShop(fileName, npcNames, propertyTitle, interactionTitle,
                greeting, goodbye, shopItems, textureResources, 5.0f, 0.9f);
    }

    /**
     * 快速生成馆主NPC
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
     * 创建进化石商店物品（简化版）
     */
    public static JsonObject createEvolutionStoneItem(String stoneId, double buyPrice, double sellPrice) {
        return createShopItem(stoneId, 1, buyPrice, sellPrice);
    }

    /**
     * 创建物品奖励（使用Item对象）
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
     */
    public static JsonObject createItemReward(String itemId, int count) {
        JsonObject item = new JsonObject();
        item.addProperty("id", itemId);
        item.addProperty("count", count);
        return item;
    }

    /**
     * 创建宝可梦队伍配置
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

    /**
     * 创建玩家模型配置（带完整的fallback结构，与evostones_1.json一致）
     */
    public static NPCDefinition.PlayerModel createPlayerModelWithFallback(boolean slim, String textureResource, String textureFallback) {
        return new NPCDefinition.PlayerModel(slim, textureResource, textureFallback);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 创建自定义商店交互（与evostones_1.json结构完全一致）
     */
    private JsonObject createCustomShopInteractions(JsonObject title, JsonObject greeting,
                                                    JsonObject goodbye, List<JsonObject> shopItems) {
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
        dialogue.add("title", title);
        dialogue.add("message", greeting);
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
        goodbyeDialogue.add("title", title);
        goodbyeDialogue.add("message", goodbye);
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
     * 创建多页提示交互（类似 ivs_basic_m.json）
     */
    private JsonObject createMultiPageTipInteractions(JsonObject title, List<JsonObject> messagePages) {
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
        dialogue.add("title", title);
        dialogue.addProperty("type", "pixelmon:open_paged_dialogue");

        JsonArray pagesArray = new JsonArray();
        for (JsonObject message : messagePages) {
            pagesArray.add(message);
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
        dialogue.add("title", title);
        dialogue.add("message", message);
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
        dialogue.add("title", title);
        dialogue.add("message", message);
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