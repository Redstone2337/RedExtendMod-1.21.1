package net.redstone.redextent.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class SuperFurnaceBlockEntityMixin {
    @Unique
    private boolean rem_template_1_21_1$isFast = false;

    @Unique
    private final static BlockPattern SUPER_FURNACE = BlockPatternBuilder.start()
            .aisle("AAA","AAA","AAA")
            .aisle("AAA","AAA","ABA")
            .aisle("AAA","AAA","AAA")
            .where('A', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.STONE)))
            .where('B', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.FURNACE)))
            .build();

    @Unique
    private final static BlockPattern SUPER_BLAST_FURNACE = BlockPatternBuilder.start()
            .aisle("AAA","AAA","AAA")
            .aisle("AAA","AAA","ABA")
            .aisle("AAA","AAA","AAA")
            .where('A', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.STONE)))
            .where('B', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.BLAST_FURNACE)))
            .build();

    @Unique
    private final static BlockPattern SUPER_SMOKER = BlockPatternBuilder.start()
            .aisle("AAA","AAA","AAA")
            .aisle("AAA","AAA","ABA")
            .aisle("AAA","AAA","AAA")
            .where('A', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.STONE)))
            .where('B', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SMOKER)))
            .build();

    @Unique
    private static final Set<BlockPattern> rem_template_1_21_1$PATTERNS = Set.of(SUPER_FURNACE, SUPER_BLAST_FURNACE, SUPER_SMOKER);


    @Inject(method = "serverTick", at = @At("TAIL"))
    private static void onServerTick(Level level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        if (!(state.getBlock() instanceof net.minecraft.world.level.block.FurnaceBlock ||
              state.getBlock() instanceof net.minecraft.world.level.block.BlastFurnaceBlock ||
              state.getBlock() instanceof net.minecraft.world.level.block.SmokerBlock)) return;
        SuperFurnaceBlockEntityMixin mixin = (SuperFurnaceBlockEntityMixin) (Object) blockEntity;
        boolean bl = rem_template_1_21_1$PATTERNS.stream().anyMatch(pattern -> pattern.find(level, pos) != null);
        if (bl && mixin != null) {
            mixin.rem_template_1_21_1$isFast = true;
        } else if (mixin != null) {
            mixin.rem_template_1_21_1$isFast = false;
        }
    }

    @Inject(method = "getTotalCookTime", at = @At("RETURN"), cancellable = true)
    private static void onGetTotalCookTime(Level level, AbstractFurnaceBlockEntity blockEntity, CallbackInfoReturnable<Integer> cir) {
        if (blockEntity == null) return;
        SuperFurnaceBlockEntityMixin mixin = (SuperFurnaceBlockEntityMixin) (Object) blockEntity;
        if (mixin != null && mixin.rem_template_1_21_1$isFast) {
            cir.setReturnValue(cir.getReturnValueI() / 4); // 将总烹饪时间减少为原来的四分之一
        }
    }
}
