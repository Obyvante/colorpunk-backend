package com.barden.bravo.cosmetics.trail;

import com.barden.bravo.cosmetics.trail.type.TrailType;
import com.barden.library.BardenJavaLibrary;
import com.barden.library.file.TomlFileLoader;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

/**
 * Trail provider class.
 */
public class TrailProvider {

    private static final BiMap<Integer, Trail> content = HashBiMap.create();

    /**
     * Initializes trail provider.
     */
    public static void initialize() {
        //If it is already initialized, no need to continue.
        if (!content.isEmpty()) {
            BardenJavaLibrary.getLogger().error("trail provider is already initialized!");
            return;
        }

        //Handles configuration.
        TomlFileLoader.getConfig("trails", false).ifPresent(file -> {
            //Loops through trails.
            file.entrySet().forEach(trail_file -> {
                //Gets required fields.
                CommentedConfig trail_config = file.get(trail_file.getKey());
                int trail_id = Integer.parseInt(trail_file.getKey());
                @Nonnull String trail_name = trail_config.get("name");
                @Nonnull String trail_asset_id = trail_config.get("assetId");
                @Nonnull TrailType trail_type = TrailType.valueOf(trail_config.get("type"));

                //Checks duplicate trail.
                if (TrailProvider.find(trail_id).isPresent()) {
                    BardenJavaLibrary.getLogger().error("[TRAIL] -> Duplicate found! (" + trail_id + ")");
                    return;
                }

                //Creates new trail then adds to the content list.
                content.put(trail_id, new Trail(trail_id, trail_type, trail_name, trail_asset_id));
            });
        });

        //Logging.
        BardenJavaLibrary.getLogger().info(content.size() + " trails are initialized successfully!");
    }

    /**
     * Gets trails.
     *
     * @return Trails.
     */
    @Nonnull
    public static Set<Trail> getContent() {
        return content.values();
    }

    /**
     * Finds trail. (SAFE)
     *
     * @param id Trail id.
     * @return Optional trail.
     */
    @Nonnull
    public static Optional<Trail> find(int id) {
        return Optional.ofNullable(content.get(id));
    }

    /**
     * Gets trail. (SAFE)
     *
     * @param id Trail id.
     * @return Trail.
     */
    @Nonnull
    public static Trail get(int id) {
        return find(id).orElseThrow(() -> new NullPointerException("trail cannot be null!"));
    }
}
