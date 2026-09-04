package com.trevorschoeny.hivesight;

import com.trevorschoeny.hivesight.config.HSConfig;
import com.trevorschoeny.hivesight.eject.Eject;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common entry point, runs on both physical sides. Eject registers here
 * because its callback has a client half (swing, send the packet) and a
 * server half (release the bee). The bee-count sync is a mixin and needs no
 * registration.
 */
public class HiveSight implements ModInitializer {

    public static final String MOD_ID = "hivesight";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        HSConfig.load();
        Eject.register();
        LOGGER.info("[hivesight] Common init: eject registered, hive occupancy sync active.");
    }
}
