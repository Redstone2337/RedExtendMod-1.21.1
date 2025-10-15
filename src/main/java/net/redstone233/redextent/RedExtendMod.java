package net.redstone233.redextent;

import com.pixelmonmod.pixelmon.api.pokemon.ability.AbilityRegistry;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.redstone233.redextent.ability.*;
import net.redstone233.redextent.ability.*;
import net.redstone233.redextent.manager.ItemClearManager;
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

    // 掉落物清理管理器
    private final ItemClearManager itemClearManager;

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

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (RedExtendMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register the Pixelmon Ability

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC, "rem-common.toml");

        // 初始化清理管理器
        this.itemClearManager = new ItemClearManager();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        // 调试信息
        if (Config.isDebugModeEnabled()) {
            LOGGER.info("=== REM Mod 调试信息 ===");
            LOGGER.info("服务器清理功能: {}", Config.isClearServerItemEnabled());
            LOGGER.info("物品过滤模式: {}", Config.isItemFilterEnabled());
            LOGGER.info("清理时间间隔: {}t ({}秒)", Config.getClearTime(), Config.getClearTime() / 20);
            LOGGER.info("白名单物品数量: {}", Config.getItemWhitelist().size());
            LOGGER.info("自定义特性: {}", Config.isCustomAbilityEnabled());
            LOGGER.info("自定义特性白名单: {}", Config.isStartAbilityWhitelistEnabled());

            List<String> abilityWhitelist = Config.getCustomAbilityWhitelist();
            LOGGER.info("白名单类数量: {}", abilityWhitelist.size());
            if (!abilityWhitelist.isEmpty()) {
                LOGGER.info("白名单类: {}", String.join(", ", abilityWhitelist));
            }
            LOGGER.info("======================");
        }

    }

    private void pixelmonAbilitySetup(FMLCommonSetupEvent event) {
        // Some Pixelmon Ability setup code
        // 注册宝可梦特性
        if (Config.isCustomAbilityEnabled()) {
            List<String> whitelist = Config.getCustomAbilityWhitelist();

            // 检查白名单设置
            if (Config.isStartAbilityWhitelistEnabled()) {
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

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("REM Mod 服务器启动处理");

        // 启动掉落物清理任务（如果启用）
        itemClearManager.startClearTask(event.getServer());

        // 输出任务状态信息
        itemClearManager.getTaskInfo().ifPresent(LOGGER::info);
        LOGGER.info("HELLO from server starting");
    }

    // 服务器停止事件
    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        // 停止清理任务
        itemClearManager.stopClearTask();
        LOGGER.info("REM Mod 服务器停止处理完成");
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
