package com.trevorschoeny.hivesight.mixin;

import net.minecraft.world.level.block.entity.BeehiveBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Turns one entry of a hive's occupant list back into the public
 * {@link BeehiveBlockEntity.Occupant} record that vanilla's release method
 * takes. The entry's class ({@code BeehiveBlockEntity.BeeData}) is
 * package-private, so it is targeted by name; callers cast the raw
 * {@code Object} from {@link HSBeehiveBlockEntityAccessor#hivesight$stored()}
 * to this interface.
 */
@Mixin(targets = "net.minecraft.world.level.block.entity.BeehiveBlockEntity$BeeData")
public interface HSBeeDataInvoker {

    @Invoker("toOccupant")
    BeehiveBlockEntity.Occupant hivesight$toOccupant();
}
