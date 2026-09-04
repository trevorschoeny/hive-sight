package com.trevorschoeny.hivesight.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/** ModMenu entry point: opens the Hive Sight config screen from the mods list. */
public final class HSConfigModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return HSConfigScreen::create;
    }
}
