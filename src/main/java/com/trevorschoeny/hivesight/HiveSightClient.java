package com.trevorschoeny.hivesight;

import com.trevorschoeny.hivesight.config.HSConfig;
import com.trevorschoeny.hivesight.honeyfaces.HoneyFaces;
import com.trevorschoeny.hivesight.hud.HiveLook;

import net.fabricmc.api.ClientModInitializer;

/** Client entry point: the HUD card and the Honey Faces pack. */
public class HiveSightClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HSConfig.load();
        HiveLook.register();
        HoneyFaces.register();
        HiveSight.LOGGER.info("[hivesight] Client init: HUD card and Honey Faces pack registered.");
    }
}
