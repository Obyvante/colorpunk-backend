package com.barden.bravo.cosmetics.pet;

import com.barden.library.BardenJavaLibrary;
import com.barden.library.file.TomlFileLoader;
import com.electronwill.nightconfig.core.CommentedConfig;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Optional;

/**
 * Pet repository class.
 */
public final class PetRepository {

    private static final HashSet<Pet> content = new HashSet<>();

    /**
     * Initializes pet repository object.
     */
    public static void initialize() {
        //If it is already initialized, no need to continue.
        if (!content.isEmpty()) {
            BardenJavaLibrary.getLogger().error("pet repository is already initialized!");
            return;
        }

        //Handles configuration.
        TomlFileLoader.getConfig("pets", false).ifPresent(file -> {
            //Loops through pets.
            file.entrySet().forEach(pet_file -> {
                //Gets required fields.
                CommentedConfig pet_config = file.get(pet_file.getKey());
                int pet_id = Integer.parseInt(pet_file.getKey());
                @Nonnull String pet_name = pet_config.get("name");
                @Nonnull String pet_asset_id = pet_config.get("assetId");

                //Checks duplicate pet.
                if (PetRepository.find(pet_id).isPresent()) {
                    BardenJavaLibrary.getLogger().error("[PET] -> Duplicate found! (" + pet_id + ")");
                    return;
                }

                //Creates new pet then adds to the content list.
                content.add(new Pet(pet_id, pet_name, pet_asset_id));
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
    public static HashSet<Pet> getContent() {
        return content;
    }

    /**
     * Finds pet. (SAFE)
     *
     * @param id Pet id.
     * @return Optional pet.
     */
    @Nonnull
    public static Optional<Pet> find(int id) {
        return content.stream().filter(pet -> pet.getId() == id).findFirst();
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
