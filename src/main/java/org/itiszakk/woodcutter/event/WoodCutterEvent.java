package org.itiszakk.woodcutter.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.After;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.itiszakk.woodcutter.WoodCutterConfiguration;
import org.itiszakk.woodcutter.util.ContinuousBlocks;
import org.jetbrains.annotations.Nullable;

public class WoodCutterEvent implements After {

    @Override
    public void afterBlockBreak(World world,
                                PlayerEntity player,
                                BlockPos pos,
                                BlockState state,
                                @Nullable BlockEntity blockEntity) {
        if (!canBreakLogs(player, state)) {
            return;
        }

        int brokenBlocks = breakBlocks(world, pos);
        damageMainItem(player, brokenBlocks);
    }

    private boolean canBreakLogs(PlayerEntity player, BlockState state) {
        return modEnabled() && checkBlockState(state) && checkCreativeMod(player) && checkSneaking(player);
    }

    private boolean modEnabled() {
        return WoodCutterConfiguration.MOD_ENABLED.getValue();
    }

    private boolean checkBlockState(BlockState state) {
        return state.isIn(BlockTags.LOGS);
    }

    private boolean checkCreativeMod(PlayerEntity player) {
        return !player.isInCreativeMode() || WoodCutterConfiguration.ENABLED_IN_CREATIVE.getValue();
    }

    private boolean checkSneaking(PlayerEntity player) {
        return !player.isSneaking() || WoodCutterConfiguration.ENABLED_WHILE_SNEAKING.getValue();
    }

    private int breakBlocks(World world, BlockPos pos) {
        ContinuousBlocks blocks = new ContinuousBlocks(world, pos, Direction.UP, this::checkBlockState);

        int brokenBlocks = 0;

        for (BlockPos p : blocks) {
            if (world.breakBlock(p, true)) {
                brokenBlocks++;
            }
        }

        return brokenBlocks;
    }

    private void damageMainItem(PlayerEntity player, int brokenBlocks) {
        player.getMainHandStack().damage(brokenBlocks, player, EquipmentSlot.MAINHAND);
    }
}