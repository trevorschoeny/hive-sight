package com.trevorschoeny.hivesight.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

/**
 * Reaches the private parts of a hive that Eject needs: the occupant list,
 * the remembered flower position, and vanilla's own single-bee release.
 *
 * <p>The list's element type ({@code BeehiveBlockEntity.BeeData}) is
 * package-private in vanilla, so it is exposed as {@code List<Object>} and each
 * element is read through {@link HSBeeDataInvoker}. Mixin matches on the erased
 * descriptor, so the generic argument is free.
 */
@Mixin(BeehiveBlockEntity.class)
public interface HSBeehiveBlockEntityAccessor {

    @Accessor("stored")
    List<Object> hivesight$stored();

    @Accessor("savedFlowerPos")
    BlockPos hivesight$savedFlowerPos();

    /**
     * Vanilla's per-bee release: spawns the bee at the hive's front face, plays
     * the exit sound, fires the game event. Returns false only if the bee
     * entity could not be created (or, under a non-EMERGENCY status, if the
     * exit is blocked or bees are told to stay in). Eject always calls it with
     * {@code EMERGENCY} and does its own exit check first; see design.md.
     */
    @Invoker("releaseOccupant")
    static boolean hivesight$releaseOccupant(Level level, BlockPos pos, BlockState state,
                                             BeehiveBlockEntity.Occupant occupant,
                                             List<Entity> released,
                                             BeehiveBlockEntity.BeeReleaseStatus status,
                                             BlockPos flowerPos) {
        throw new AssertionError("mixin invoker not applied");
    }
}
