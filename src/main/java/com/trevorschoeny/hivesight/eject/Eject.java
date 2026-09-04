package com.trevorschoeny.hivesight.eject;

import com.trevorschoeny.hivesight.config.HSConfig;
import com.trevorschoeny.hivesight.mixin.HSBeeDataInvoker;
import com.trevorschoeny.hivesight.mixin.HSBeehiveBlockEntityAccessor;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Eject: shift-right-click a hive to let one bee out, whatever is in hand.
 *
 * <p>Hooked through Fabric's {@link UseBlockCallback}, which fires ahead of
 * vanilla's "sneaking with an item skips the block" rule. That rule is why a
 * hook on the hive block itself could never see a shift-click with shears.
 * The callback runs on both sides: the client returns SUCCESS so the arm
 * swings and the use packet goes out; the server does the release.
 */
public final class Eject {

    private Eject() {}

    public static void register() {
        UseBlockCallback.EVENT.register(Eject::onUse);
    }

    private static InteractionResult onUse(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
        if (!HSConfig.eject()) return InteractionResult.PASS;
        // Vanilla's own "sneak-use" predicate (shift, or crawling).
        if (!player.isSecondaryUseActive() || player.isSpectator()) return InteractionResult.PASS;
        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BeehiveBlock)) return InteractionResult.PASS;

        if (level instanceof ServerLevel server) one(server, pos, state);
        // Consumed: vanilla's shears / bottle harvest never runs for this click.
        return InteractionResult.SUCCESS;
    }

    /** Releases the first bee in the hive, or plays the "cannot" feedback. */
    private static void one(ServerLevel level, BlockPos pos, BlockState state) {
        if (!(level.getBlockEntity(pos) instanceof BeehiveBlockEntity hive)) return;
        HSBeehiveBlockEntityAccessor access = (HSBeehiveBlockEntityAccessor) hive;
        Direction facing = state.getValue(BeehiveBlock.FACING);
        BlockPos front = pos.relative(facing);

        // EMERGENCY status bypasses vanilla's night/rain refusal (wanted) and
        // its blocked-exit refusal (not wanted: that would drop a bee inside a
        // solid block). So the exit check is done here, vanilla's own test.
        boolean exitClear = level.getBlockState(front).getCollisionShape(level, front).isEmpty();
        List<Object> stored = access.hivesight$stored();

        if (exitClear && !stored.isEmpty()) {
            BeehiveBlockEntity.Occupant bee = ((HSBeeDataInvoker) stored.get(0)).hivesight$toOccupant();
            boolean released = HSBeehiveBlockEntityAccessor.hivesight$releaseOccupant(
                    level, pos, state, bee, new ArrayList<>(),
                    BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY,
                    access.hivesight$savedFlowerPos());
            if (released) {
                // Only drop the entry once the bee actually exists in the world.
                stored.remove(0);
                hive.setChanged();
                return;
            }
        }

        // Empty hive (or blocked exit): a dry click and a puff of smoke at the front face.
        level.playSound(null, pos, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0f, 1.0f);
        Vec3 puff = Vec3.atCenterOf(pos).add(facing.getStepX() * 0.55, 0.0, facing.getStepZ() * 0.55);
        level.sendParticles(ParticleTypes.SMOKE, puff.x, puff.y, puff.z, 6, 0.1, 0.1, 0.1, 0.01);
    }
}
