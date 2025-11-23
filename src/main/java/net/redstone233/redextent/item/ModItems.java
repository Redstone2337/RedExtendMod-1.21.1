package net.redstone233.redextent.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.redstone233.redextent.RedExtendMod;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RedExtendMod.MOD_ID);

    public static final DeferredItem<Item> GITHUB_ITEM = ITEMS.registerSimpleItem(
            "github_item",
            new Item.Properties().stacksTo(64) // The properties to use.
    );

    public static final DeferredItem<Item> DISCORD_ITEM = ITEMS.registerSimpleItem(
            "discord_item",
            new Item.Properties().stacksTo(64) // The properties to use.
    );


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
