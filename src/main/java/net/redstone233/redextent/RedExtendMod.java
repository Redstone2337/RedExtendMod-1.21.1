package net.redstone233.redextent;

import com.pixelmonmod.pixelmon.api.pokemon.ability.AbilityRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.redstone233.redextent.ability.*;
import net.redstone233.redextent.config.ClientConfig;
import net.redstone233.redextent.config.CommonConfig;
import net.redstone233.redextent.core.DatapackValidator;
import net.redstone233.redextent.core.brewing.RhinoBrewingRecipeParser;
import net.redstone233.redextent.core.event.BrewingRecipeReloadListener;
import net.redstone233.redextent.core.mod.SuperFurnaceRegistration;
import net.redstone233.redextent.core.packet.PacketHandler;
import net.redstone233.redextent.core.packet.S2CDisabledModListPacket;
import net.redstone233.redextent.core.proxy.ServerProxy;
import net.redstone233.redextent.functions.ConfigRegistrar;
import net.redstone233.redextent.item.ModItems;
import net.redstone233.redextent.manager.ItemClearManager;
import net.redstone233.redextent.manager.ModJarManager;
import net.redstone233.redextent.ponder.SuperBlastFurnaceScene;
import net.redstone233.redextent.ponder.SuperFurnaceScene;
import net.redstone233.redextent.ponder.SuperSmokerScene;
import net.redstone233.redextent.ponder.tags.ModPonderTags;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(RedExtendMod.MOD_ID)
public class RedExtendMod {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "rem";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    private static String MOD_VERSION;

    // 掉落物清理管理器
    private final ItemClearManager itemClearManager;
    private final ServerProxy serverProxy = new ServerProxy();

    // 已知的特性类映射
    private static final Set<String> KNOWN_ABILITIES = new HashSet<>();
    static {
        // 注册已知的特性类
        KNOWN_ABILITIES.add("FastStartReforged");
        KNOWN_ABILITIES.add("DragonRise");
        KNOWN_ABILITIES.add("FightingDivinity");
        KNOWN_ABILITIES.add("ElectricDivinity");
        KNOWN_ABILITIES.add("BiomeBlessingSwamp");
        // 未来可以在这里添加更多已知特性
        // KNOWN_ABILITIES.add("AnotherAbility");
    }

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public RedExtendMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::pixelmonAbilitySetup);
        modEventBus.addListener(this::onRegisterPayload);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (RedExtendMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
        ModItems.register(modEventBus);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        setModVersion(modContainer.getModInfo().getVersion().toString());

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        // 注册通用配置和客户端配置
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "rem-common.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC, "rem-client.toml");

        // 初始化清理管理器
        this.itemClearManager = new ItemClearManager();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("开始初始化模组内容...");
        long startTime = System.currentTimeMillis();
        LOGGER.info("HELLO FROM COMMON SETUP");

        // 调试信息
        if (CommonConfig.isDebugModeEnabled()) {
            LOGGER.info("=== REM Mod 调试信息 ===");
            LOGGER.info("服务器清理功能: {}", CommonConfig.isClearServerItemEnabled());
            LOGGER.info("物品过滤模式: {}", CommonConfig.isItemFilterEnabled());
            LOGGER.info("清理时间间隔: {}t ({}秒)", CommonConfig.getClearTime(), CommonConfig.getClearTime() / 20);
            LOGGER.info("白名单物品数量: {}", CommonConfig.getItemWhitelist().size());
            LOGGER.info("自定义特性: {}", ClientConfig.isCustomAbilityEnabled());
            LOGGER.info("自定义特性白名单: {}", ClientConfig.isStartAbilityWhitelistEnabled());

            List<String> abilityWhitelist = ClientConfig.getCustomAbilityWhitelist();
            LOGGER.info("白名单类数量: {}", abilityWhitelist.size());
            if (!abilityWhitelist.isEmpty()) {
                LOGGER.info("白名单类: {}", String.join(", ", abilityWhitelist));
            }
            LOGGER.info("======================");
        }
        LOGGER.info("调试功能初始化完成，耗时: {}ms", System.currentTimeMillis() - startTime);

        SuperFurnaceScene.init();
        LOGGER.info("超级熔炉思索初始化完成，耗时{}ms", System.currentTimeMillis() - startTime);

        SuperBlastFurnaceScene.init();
        LOGGER.info("超级高炉思索初始化完成，耗时{}ms", System.currentTimeMillis() - startTime);

        SuperSmokerScene.init();
        LOGGER.info("超级烟熏炉思索初始化完成，耗时{}ms", System.currentTimeMillis() - startTime);

        ModPonderTags.init();
        LOGGER.info("思索标签初始化完成，耗时{}ms", System.currentTimeMillis() - startTime);

        SuperFurnaceRegistration.init();
        LOGGER.info("超级熔炼系统注册初始化完成，耗时{}ms", System.currentTimeMillis() - startTime);

        RhinoBrewingRecipeParser.registerWithNeoForgeToDataPack();
        LOGGER.info("犀牛酿造配方解析器注册初始化完成，耗时{}ms", System.currentTimeMillis() - startTime);

        serverProxy.syncToAll();
        LOGGER.info("已同步禁用模组列表信息到所有在线玩家，耗时：{}ms", System.currentTimeMillis() - startTime);

        ConfigRegistrar.registerAllConfigs();
        LOGGER.info("已注册所有配置，耗时{}ms", System.currentTimeMillis() - startTime);

        LOGGER.info("模组初始化完成，总耗时{}ms", System.currentTimeMillis() - startTime);

        // 3. 在游戏加载的合适时机处理模组禁用
        event.enqueueWork(() -> {
            // 从配置中获取需要禁用的模组列表
            List<String> modIdsToDisable = CommonConfig.getDisabledModList();
            LOGGER.info("配置中已禁用的模组：{}", modIdsToDisable);
            handleFileBasedModControl(modIdsToDisable, true);
        });
    }


    private void pixelmonAbilitySetup(FMLCommonSetupEvent event) {
        // Some Pixelmon Ability setup code
        // 注册宝可梦特性
        if (ClientConfig.isCustomAbilityEnabled()) {
            List<String> whitelist = ClientConfig.getCustomAbilityWhitelist();

            // 检查白名单设置
            if (ClientConfig.isStartAbilityWhitelistEnabled()) {
                // 启用白名单模式，只注册白名单中的特性
                if (whitelist.isEmpty()) {
                    LOGGER.warn("自定义特性白名单已启用但为空，不会注册任何特性");
                    return;
                }

                int registeredCount = 0;
                for (String abilityName : whitelist) {
                    if (registerAbility(abilityName)) {
                        registeredCount++;
                    }
                }

                LOGGER.info("已注册 {} 个自定义特性（白名单模式）", registeredCount);
            } else {
                // 未启用白名单模式，注册所有已知特性
                int registeredCount = 0;
                for (String abilityName : KNOWN_ABILITIES) {
                    if (registerAbility(abilityName)) {
                        registeredCount++;
                    }
                }

                LOGGER.info("已注册 {} 个自定义特性（全部模式）", registeredCount);
            }
        }
        LOGGER.info("HELLO FROM PIXELMON ABILITY SETUP");
    }


    /**
     * 注册单个特性
     * @param abilityName 特性名称
     * @return 是否成功注册
     */
    private boolean registerAbility(String abilityName) {
        try {
            switch (abilityName) {
                case "FastStartReforged":
                case "FastStart":
                    AbilityRegistry.register(FastStartReforged.class.getName());
                    LOGGER.info("注册自定义特性: Fast Start");
                    return true;
                // 未来可以在这里添加更多特性
                case "DragonRise":
                    AbilityRegistry.register(DragonRise.class.getName());
                    LOGGER.info("注册自定义特性: Dragon Rise");
                    return true;
                case "FightingDivinity":
                    AbilityRegistry.register(FightingDivinity.class.getName());
                    LOGGER.info("注册自定义特性: Fighting Divinity");
                    return true;
                case "ElectricDivinity":
                    AbilityRegistry.register(ElectricDivinity.class.getName());
                    LOGGER.info("注册自定义特性: Electric Divinity");
                    return true;
                case "BiomeBlessingSwamp":
                    AbilityRegistry.register(BiomeBlessingSwamp.class.getName());
                    LOGGER.info("注册自定义特性： Biome Blessing Swamp");
                default:
                    LOGGER.warn("未知的自定义特性: {}", abilityName);
                    return false;
            }
        } catch (Exception e) {
            LOGGER.error("注册自定义特性 {} 时发生错误", abilityName, e);
            return false;
        }
    }

    /**
     * 文件模式：通过重命名文件禁用模组
     */
    private void handleFileBasedModControl(List<String> modIdsToDisable, boolean enableLogging) {
        if (modIdsToDisable.isEmpty()) {
            if (enableLogging) {
                LOGGER.info("没有需要禁用的模组");
            }
            return;
        }

        int successCount = ModJarManager.batchDisableMods(modIdsToDisable, enableLogging);

        if (enableLogging) {
            LOGGER.info("文件模式操作完成: {}/{} 个模组成功处理", successCount, modIdsToDisable.size());
        }
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    private void onRegisterPayload(RegisterPayloadHandlersEvent evt) {
        PacketHandler.init(evt);   // 就是前面写的单文件注册器
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("REM Mod 服务器启动处理");

        // 启动掉落物清理任务（如果启用）
        itemClearManager.startClearTask(event.getServer());

        DatapackValidator.validateAndRegisterAllDatapacks(
                event.getServer().getPackRepository().getAvailablePacks(),
                8,
                event.getServer().registryAccess()
        );
        LOGGER.info("REM Mod 数据包验证成功!");

        // 输出任务状态信息
        itemClearManager.getTaskInfo().ifPresent(LOGGER::info);
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        // 注册你的重载监听器
        if (CommonConfig.isOnBrewingRecipeEnabled()) {
            event.addListener(new BrewingRecipeReloadListener());
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp) {
            List<String> list = CommonConfig.getDisabledModList();
            PacketHandler.sendToPlayer(sp, new S2CDisabledModListPacket(list));
        }
    }


    // 服务器停止事件
    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        // 停止清理任务
        itemClearManager.stopClearTask();
        LOGGER.info("REM Mod 服务器停止处理完成");
    }

    public static String getModVersion() {
        return MOD_VERSION;
    }

    public static void setModVersion(String modVersion) {
        MOD_VERSION = modVersion;
    }

    /**
     * 获取已知的特性列表（用于调试或其他用途）
     * @return 已知特性名称集合
     */
    public static Set<String> getKnownAbilities() {
        return new HashSet<>(KNOWN_ABILITIES);
    }

    public ItemClearManager getItemClearManager() {
        return itemClearManager;
    }
}