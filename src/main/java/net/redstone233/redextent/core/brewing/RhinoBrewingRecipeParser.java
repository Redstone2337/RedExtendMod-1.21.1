package net.redstone233.redextent.core.brewing;

import com.mojang.logging.LogUtils;
import dev.latvian.mods.rhino.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.redstone233.redextent.RedExtendMod;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RhinoBrewingRecipeParser {

    /* 临时队列，脚本解析阶段往里扔数据 */
    private static final List<RecipeTriple> QUEUE = new ArrayList<>();
    private static final Logger LOGGER = LogUtils.getLogger();

    /* 供模组初始化调用的入口 - 从文件系统加载 */
    public static void reloadAll() {
        QUEUE.clear();

        // 使用NeoForge的方式获取游戏目录
        Path scriptPath = Path.of(FMLLoader.getGamePath().toString() + "/data/" + RedExtendMod.MOD_ID + "/scripts/brewing_recipes");
        File scriptDir = scriptPath.toFile();

        if (!scriptDir.exists() || !scriptDir.isDirectory()) {
            LOGGER.warn("Brewing script directory not found: {}", scriptPath);
            return;
        }

        File[] scriptFiles = scriptDir.listFiles((dir, name) -> name.endsWith(".js"));
        if (scriptFiles == null) {
            LOGGER.warn("No brewing script files found in: {}", scriptPath);
            return;
        }

        for (File scriptFile : scriptFiles) {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(scriptFile))) {
                parseScript(reader);
                LOGGER.info("Successfully loaded brewing script: {}", scriptFile.getName());
            } catch (Exception ex) {
                LOGGER.error("Failed to load brewing script {}", scriptFile.getAbsolutePath());
            }
        }
        LOGGER.info("Brewing scripts reloaded from file system. Queued {} recipes.", QUEUE.size());
    }

    /* 新增：从数据包加载酿造配方脚本 */
    public static void reloadAll(ResourceManager resourceManager) {
        QUEUE.clear();

        // 查找所有命名空间下的酿造配方脚本
        resourceManager.listResources("scripts/brewing_recipes", id -> id.getPath().endsWith(".js"))
                .forEach((resourceLocation, resource) -> {
                    try (InputStreamReader reader = new InputStreamReader(resource.open())) {
                        parseScript(reader);
                        LOGGER.info("Successfully loaded brewing script from datapack: {}", resourceLocation);
                    } catch (Exception ex) {
                        LOGGER.error("Failed to load brewing script from datapack: {}", resourceLocation, ex);
                    }
                });

        LOGGER.info("Brewing scripts reloaded from datapacks. Queued {} recipes.", QUEUE.size());
    }

    /* 新增：将酿造配方解析器注册到NeoForge以支持数据包 */
    public static void registerWithNeoForgeToDataPack() {
        // 这个方法不需要参数，它只是确保RhinoBrewingRecipeParser类被加载
        // 这样@SubscribeEvent注解就能被NeoForge事件总线扫描到
        LOGGER.info("Rhino brewing recipe parser registered for datapack support");
    }

    /* NeoForge 事件处理器 */
    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        for (RecipeTriple tr : QUEUE) {
            builder.addMix(tr.input, tr.ingredient, tr.output);
        }

        LOGGER.info("Registered {} potion recipes to NeoForge.", QUEUE.size());
    }

    /* 解析脚本 */
    private static void parseScript(InputStreamReader reader) throws Exception {
        ContextFactory cf = new ContextFactory();
        Context cx = null;
        try {
            cx = cf.enter();
            Scriptable scope = cx.initStandardObjects();
            ScriptableObject.defineConstProperty(scope, String.valueOf(BrewingRecipeEventClass.class), cx);

            // 在ScriptableObject实例上调用defineFunctionProperties
            if (scope instanceof ScriptableObject scriptableScope) {
                scriptableScope.defineFunctionProperties(cx, new String[]{"brew"}, BrewingRecipeEventClass.class, ScriptableObject.DONTENUM);
            }

            cx.evaluateReader(scope, reader, "brewScript", 1, null);

        } catch (Exception ex) {
            LOGGER.error("Failed to parse brewing script");
            throw ex;
        } finally {
            // 清理Context
            if (cx != null) {
                try {
                    cx = null;
                } catch (Exception e) {
                    LOGGER.error("Error during context cleanup");
                }
            }
        }
    }

    /* 队列里的三元组 - 使用Holder<Potion>和Item */
    private record RecipeTriple(Holder<Potion> input, Item ingredient, Holder<Potion> output) {}

    /* Rhino 宿主类，仅负责把数据压入 QUEUE */
    public static class BrewingRecipeEventClass extends ScriptableObject {
        @Override
        public String getClassName() {
            return "BrewingRecipeEvent";
        }

        /* JS: BrewingRecipeEvent.create(e => {...}) */
        public static void create(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
            Scriptable event = cx.newObject(thisObj);
            Scriptable input = cx.newObject(event);
            Scriptable material = cx.newObject(event);
            Scriptable output = cx.newObject(event);

            ScriptableObject.putProperty(event, "input", input, cx);
            ScriptableObject.putProperty(event, "material", material, cx);
            ScriptableObject.putProperty(event, "output", output, cx);

            Function lambda = (Function) args[0];
            lambda.call(cx, thisObj, event, new Object[]{event});

            /* 收集结果 - 修复未检查转换警告 */
            Object inPotionObj = ScriptableObject.getProperty(input, "potion", cx);
            Object outPotionObj = ScriptableObject.getProperty(output, "potion", cx);
            Object itemObj = ScriptableObject.getProperty(material, "item", cx);

            Holder<Potion> inP = parsePotionFromObject(inPotionObj);
            Holder<Potion> outP = parsePotionFromObject(outPotionObj);
            Item ing = parseItemFromObject(itemObj);

            if (inP != null && ing != null && outP != null) {
                QUEUE.add(new RecipeTriple(inP, ing, outP));
            }
        }

        /* JS: brew({input:{...}, material:..., output:{...}}) */
        public static void brew(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
            Scriptable obj = (Scriptable) args[0];

            Object inputObj = ScriptableObject.getProperty(obj, "input", cx);
            Object materialObj = ScriptableObject.getProperty(obj, "material", cx);
            Object outputObj = ScriptableObject.getProperty(obj, "output", cx);

            Holder<Potion> inP = null;
            Holder<Potion> outP = null;
            Item ing = null;

            if (inputObj instanceof Scriptable inputScriptable) {
                Object potionObj = ScriptableObject.getProperty(inputScriptable, "potion", cx);
                inP = parsePotionFromObject(potionObj);
            }

            if (materialObj instanceof Scriptable materialScriptable) {
                Object itemObj = ScriptableObject.getProperty(materialScriptable, "item", cx);
                ing = parseItemFromObject(itemObj);
            }

            if (outputObj instanceof Scriptable outputScriptable) {
                Object potionObj = ScriptableObject.getProperty(outputScriptable, "potion", cx);
                outP = parsePotionFromObject(potionObj);
            }

            if (inP != null && ing != null && outP != null) {
                QUEUE.add(new RecipeTriple(inP, ing, outP));
            }
        }

        /* ---------- 工具方法 ---------- */
        private static Holder<Potion> parsePotionFromObject(Object potionObj) {
            if (!(potionObj instanceof String potId)) return null;

            ResourceLocation location = ResourceLocation.tryParse(potId);
            if (location == null) return null;

            return BuiltInRegistries.POTION.getHolder(location).orElse(null);
        }

        private static Item parseItemFromObject(Object itemObj) {
            if (!(itemObj instanceof String itemId)) return null;

            ResourceLocation location = ResourceLocation.tryParse(itemId);
            if (location == null) return Items.AIR;

            return BuiltInRegistries.ITEM.get(location);
        }

        /* 链式空实现 */
        public static Scriptable toCreateBrewing(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
            return thisObj;
        }
    }
}