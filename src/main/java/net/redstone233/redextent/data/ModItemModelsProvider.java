package net.redstone233.redextent.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.redstone233.redextent.RedExtendMod;
import net.redstone233.redextent.item.ModItems;

public class ModItemModelsProvider extends ItemModelProvider {
    public ModItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, RedExtendMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.DISCORD_ITEM.get());
        basicItem(ModItems.GITHUB_ITEM.get());
    }
}
