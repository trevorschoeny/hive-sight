package com.trevorschoeny.hivesight.config;

import com.trevorschoeny.hivesight.honeyfaces.HoneyFaces;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

/** YACL screen: one tab, one toggle per feature. */
public final class HSConfigScreen {

    private HSConfigScreen() {}

    public static Screen create(Screen parent) {
        Option<Boolean> hud = booleanOption(
                "HUD",
                "Hold shift with your crosshair on a bee hive or bee nest to see how many "
                        + "bees are inside and how full of honey it is.",
                true, HSConfig::hud, HSConfig::setHud);
        Option<Boolean> eject = booleanOption(
                "Eject",
                "Shift-right-click a hive to let one bee out, whatever you're holding. "
                        + "Works at night and in the rain. On a dedicated server this is the "
                        + "server's setting.",
                true, HSConfig::eject, HSConfig::setEject);
        Option<Boolean> honeyFaces = booleanOption(
                "Honey Faces",
                "When a hive is full, show the honey drips on all four sides instead of "
                        + "only the front. Changing this reloads resources.",
                true, HSConfig::honeyFaces, v -> {
                    HSConfig.setHoneyFaces(v);
                    HoneyFaces.apply(v);
                });

        ConfigCategory features = ConfigCategory.createBuilder()
                .name(Component.literal("Features"))
                .group(OptionGroup.createBuilder()
                        .name(Component.literal("Hive Sight"))
                        .description(OptionDescription.of(Component.literal(
                                "Bee hives made legible. Each feature has its own switch.")))
                        .option(hud)
                        .option(eject)
                        .option(honeyFaces)
                        .build())
                .build();

        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Hive Sight"))
                .category(features)
                .build()
                .generateScreen(parent);
    }

    private static Option<Boolean> booleanOption(String name, String description, boolean defaultValue,
                                                 Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return Option.<Boolean>createBuilder()
                .name(Component.literal(name))
                .description(OptionDescription.of(Component.literal(description)))
                .binding(defaultValue, getter, setter)
                .controller(BooleanControllerBuilder::create)
                .build();
    }
}
