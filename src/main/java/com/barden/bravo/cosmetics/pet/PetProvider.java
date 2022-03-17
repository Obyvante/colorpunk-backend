package com.barden.bravo.cosmetics.pet;

import com.barden.library.BardenJavaLibrary;
import com.barden.library.file.TomlFileLoader;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

/**
 * Pet provider class.
 */
public final class PetProvider {

    private static final BiMap<Integer, Pet> content = HashBiMap.create();

    /**
     * Initializes pet provider.
     */
    public static void initialize() {
        //If it is already initialized, no need to continue.
        if (!content.isEmpty()) {
            BardenJavaLibrary.getLogger().error("pet provider is already initialized!");
            return;
        }

        //Handles configuration.
        TomlFileLoader.getConfig("pets", false).ifPresent(file -> {
            //Loops through pets.
            file.entrySet().forEach(pet_file -> {
                //Gets required fields.
                CommentedConfig pet_config = file.get(pet_file.getKey());
                int pet_id = Integer.parseInt(pet_file.getKey());
                @Nonnull String _name = pet_config.get("name");
                @Nonnull String _icon_id = pet_config.get("iconId");
                @Nonnull String _mesh_id = pet_config.get("meshId");
                @Nonnull String _texture_id = pet_config.get("textureId");

                //Checks duplicate pet.
                if (PetProvider.find(pet_id).isPresent()) {
                    BardenJavaLibrary.getLogger().error("[PET] -> Duplicate found! (" + pet_id + ")");
                    return;
                }

                //Creates new pet then adds to the content list.
                content.put(pet_id, new Pet(pet_id, _name, _icon_id, _mesh_id, _texture_id));
            });
        });

        //Logging.
        BardenJavaLibrary.getLogger().info(content.size() + " pets are initialized successfully!");
    }

    /**
     * Gets pets.
     *
     * @return Pets.
     */
    @Nonnull
    public static Set<Pet> getContent() {
        return content.values();
    }

    /**
     * Finds pet. (SAFE)
     *
     * @param id Pet id.
     * @return Optional pet.
     */
    @Nonnull
    public static Optional<Pet> find(int id) {
        return Optional.ofNullable(content.get(id));
    }

    /**
     * Gets pet. (SAFE)
     *
     * @param id Pet id.
     * @return Pet.
     */
    @Nonnull
    public static Pet get(int id) {
        return find(id).orElseThrow(() -> new NullPointerException("pet cannot be null!"));
    }
}
