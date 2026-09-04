package com.trevorschoeny.hivesight.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Makes a hive's occupancy visible to clients.
 *
 * <p>Vanilla never sends a hive's bee list to the client (no update tag, no
 * update packet), so a client-side {@code getOccupantCount()} is always 0.
 * This mixin gives the hive the same sync shape other vanilla block entities
 * use: the update tag is the full save data, the update packet is the
 * standard block-entity data packet, and a block update is broadcast whenever
 * a bee enters or leaves. The client's own {@code loadAdditional} then fills
 * its occupant list, so the HUD needs no client-side code at all.
 *
 * <p>Runs regardless of the HUD toggle; broadcasting what the server already
 * knows is harmless and keeps the toggle a pure client concern.
 */
@Mixin(BeehiveBlockEntity.class)
public abstract class HSBeehiveSyncMixin extends BlockEntity {

    private HSBeehiveSyncMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // Chunk load and block-entity data packets both read this. The full save
    // data is at most three bees' worth of NBT; not worth a slimmer tag.
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // A bee entered (addOccupant funnels through storeBee).
    @Inject(method = "storeBee", at = @At("TAIL"))
    private void hivesight$afterStore(BeehiveBlockEntity.Occupant occupant, CallbackInfo ci) {
        hivesight$broadcast(this.level, this.worldPosition);
    }

    // A bee left, through any path: natural exit, harvest, fire, Eject.
    @Inject(method = "releaseOccupant", at = @At("RETURN"))
    private static void hivesight$afterRelease(Level level, BlockPos pos, BlockState state,
                                               BeehiveBlockEntity.Occupant occupant,
                                               List<Entity> released,
                                               BeehiveBlockEntity.BeeReleaseStatus status,
                                               BlockPos flowerPos,
                                               CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) hivesight$broadcast(level, pos);
    }

    // Same state in and out: this is only here to make the chunk holder flush
    // the block-entity data packet to every tracking client.
    @Unique
    private static void hivesight$broadcast(Level level, BlockPos pos) {
        if (level == null || level.isClientSide()) return;
        BlockState state = level.getBlockState(pos);
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }
}
