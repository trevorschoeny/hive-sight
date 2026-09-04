package com.trevorschoeny.hivesight.honeyfaces;

import com.trevorschoeny.hivesight.HiveSight;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.repository.PackRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Honey Faces: when a hive is full (honey level 5), the honey drips show on
 * all four side faces, not just the front. Pure data: a built-in resource
 * pack that overrides vanilla's two "honey" block models to point their side
 * texture at a side sprite carrying vanilla's own drip pixels.
 *
 * <p>The pack ships default-enabled. The config toggle flips it in the
 * client's pack list and reloads resources, so it behaves like any other
 * pack the player could manage from the resource pack screen.
 */
public final class HoneyFaces {

    private HoneyFaces() {}

    private static final Identifier PACK_ID = Identifier.fromNamespaceAndPath(HiveSight.MOD_ID, "honey_faces");

    public static void register() {
        ResourceLoader.registerBuiltinPack(
                PACK_ID,
                FabricLoader.getInstance().getModContainer(HiveSight.MOD_ID).orElseThrow(),
                Component.literal("Hive Sight: Honey Faces"),
                PackActivationType.DEFAULT_ENABLED);
    }

    /** Enables or disables the pack to match the toggle, then reloads resources. */
    public static void apply(boolean on) {
        Minecraft mc = Minecraft.getInstance();
        PackRepository repo = mc.getResourcePackRepository();
        repo.reload();
        // Fabric names the pack after its identifier; match loosely so a naming
        // tweak upstream can't silently turn the toggle into a no-op.
        String id = repo.getAvailableIds().stream()
                .filter(s -> s.contains(PACK_ID.getPath()))
                .findFirst().orElse(null);
        if (id == null) {
            HiveSight.LOGGER.warn("[honeyfaces] built-in pack not found among {}", repo.getAvailableIds());
            return;
        }
        List<String> selected = new ArrayList<>(repo.getSelectedIds());
        boolean changed = on ? !selected.contains(id) && selected.add(id) : selected.remove(id);
        if (!changed) return;
        repo.setSelected(selected);
        mc.reloadResourcePacks();
    }
}
