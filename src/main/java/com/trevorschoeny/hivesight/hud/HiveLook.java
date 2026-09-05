package com.trevorschoeny.hivesight.hud;

import com.trevorschoeny.hivesight.config.HSConfig;
import com.trevlar.menukit.core.PanelStyle;
import com.trevlar.menukit.hud.MKHudAnchor;
import com.trevlar.menukit.hud.MKHudPanel;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * HUD: while the player holds shift with the crosshair on a hive, a card
 * below the crosshair shows {@code Bees: N/3} and {@code Honey: N/5}.
 *
 * <p>The gesture is a predicate over vanilla's own per-frame block pick
 * ({@code Minecraft.hitResult}), which already applies the player's reach.
 * Nothing is cached: the panel's suppliers re-read the picked hive on every
 * render, and the bee count is real on the client because of
 * {@code HSBeehiveSyncMixin}.
 */
public final class HiveLook {

    private HiveLook() {}

    public static void register() {
        MKHudPanel.builder("hivesight:hive-card")
                .anchor(MKHudAnchor.CENTER, 0, 20)   // just below the crosshair
                .autoSize().padding(4)
                .style(PanelStyle.NONE)
                .hideInScreen()                       // no card while a menu is open
                .showWhen(HiveLook::isActive)
                .text(0, 0,  () -> "Bees: "  + bees()  + "/" + BeehiveBlockEntity.MAX_OCCUPANTS)
                .text(0, 12, () -> "Honey: " + honey() + "/" + BeehiveBlock.MAX_HONEY_LEVELS)
                .build();
    }

    /** Shift held and the crosshair is on a hive within reach. */
    private static boolean isActive() {
        Minecraft mc = Minecraft.getInstance();
        return HSConfig.hud()
                && mc.player != null
                && mc.player.isShiftKeyDown()
                && target() != null;
    }

    /** The hive under the crosshair, or null. Vanilla's pick stops at glass; see design.md. */
    private static BeehiveBlockEntity target() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return null;
        if (!(mc.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) return null;
        return mc.level.getBlockEntity(hit.getBlockPos()) instanceof BeehiveBlockEntity hive ? hive : null;
    }

    private static int bees() {
        BeehiveBlockEntity hive = target();
        return hive == null ? 0 : hive.getOccupantCount();
    }

    private static int honey() {
        BeehiveBlockEntity hive = target();
        return hive == null ? 0 : BeehiveBlockEntity.getHoneyLevel(hive.getBlockState());
    }
}
