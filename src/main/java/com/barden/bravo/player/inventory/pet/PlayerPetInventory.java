package com.barden.bravo.player.inventory.pet;

import com.barden.bravo.player.Player;
import com.barden.bravo.player.inventory.PlayerInventory;
import com.barden.library.metadata.MetadataEntity;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Player pet inventory class.
 */
public final class PlayerPetInventory extends MetadataEntity {

    private final Player player;
    private final PlayerInventory inventory;

    private HashSet<PlayerPet> content = new HashSet<>();
    private HashSet<UUID> actives = new HashSet<>();

    /**
     * Creates player pet inventory object.
     *
     * @param player    Player.
     * @param inventory Player inventory.
     */
    public PlayerPetInventory(@Nonnull Player player, @Nonnull PlayerInventory inventory) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.inventory = Objects.requireNonNull(inventory, "player inventory cannot be null!");
    }

    /**
     * Creates player pet inventory object.
     *
     * @param player    Player.
     * @param inventory Player inventory.
     */
    public PlayerPetInventory(@Nonnull Player player, @Nonnull PlayerInventory inventory, @Nonnull Document document) {
        //Objects null check.
        Objects.requireNonNull(document, "document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.inventory = Objects.requireNonNull(inventory, "player inventory cannot be null!");

        //Gets pet content.
        @Nonnull Document pets_content = Objects.requireNonNull(document.get("content", Document.class), "content cannot be null!");
        //Loops pet documents.
        pets_content.keySet().forEach(pet_uid_string -> {
            //Declares required fields.
            @Nonnull Document pet_document = Objects.requireNonNull(pets_content.get(pet_uid_string, Document.class), "pet document cannot be null!");
            int pet_id = Objects.requireNonNull(pet_document.getInteger("id"), "pet id cannot be null!");
            @Nonnull UUID pet_uid = UUID.fromString(pet_uid_string);

            //Creates new player pet then adds to the pets list.
            this.content.add(new PlayerPet(this.player, this, pet_id, pet_uid));
        });

        //Gets active pets from the field.
        this.actives = new HashSet<>(document.getList("actives", UUID.class));
    }

    /**
     * Gets player.
     *
     * @return Player.
     */
    @Nonnull
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Gets player inventory.
     *
     * @return Player inventory.
     */
    @Nonnull
    public PlayerInventory getInventory() {
        return this.inventory;
    }

    /**
     * Gets pets.
     *
     * @return Player pets.
     */
    @Nonnull
    public HashSet<PlayerPet> getContent() {
        return this.content;
    }

    /**
     * Finds pet. (SAFE)
     *
     * @param uid Player pet unique id.
     * @return Optional player pet.
     */
    @Nonnull
    public Optional<PlayerPet> find(@Nonnull UUID uid) {
        return this.content.stream().filter(pet -> pet.getUID().equals(Objects.requireNonNull(uid, "unique id cannot be null!"))).findFirst();
    }

    /**
     * Gets pet. (UNSAFE)
     *
     * @param uid Player pet unique id.
     * @return Player pet.
     */
    @Nonnull
    public PlayerPet get(@Nonnull UUID uid) {
        return this.find(uid).orElseThrow(() -> new NullPointerException("player pet cannot be null!"));
    }


    /**
     * Creates new pet.
     *
     * @param id Pet id.
     * @return Player pet.
     */
    @Nonnull
    public PlayerPet create(int id) {
        //Creates new pet.
        PlayerPet pet = new PlayerPet(this.player, this, id, UUID.randomUUID());
        //Adds created pet to the pets list.
        this.content.add(pet);
        //Returns created pet.
        return pet;
    }

    /**
     * Adds new pet to the pets list.
     *
     * @param pet Player pet.
     */
    public void add(@Nonnull PlayerPet pet) {
        //Adds pet to the pets list.
        this.content.add(Objects.requireNonNull(pet, "player pet cannot be null!"));
    }

    /**
     * Removes new pet to from pets list.
     *
     * @param pet Player pet.
     */
    public void remove(@Nonnull PlayerPet pet) {
        //Removes pet from the pets list.
        this.content.remove(Objects.requireNonNull(pet, "player pet cannot be null!"));
    }

    /**
     * Gets active pets.
     *
     * @return Active pets.
     */
    @Nonnull
    public HashSet<UUID> getActives() {
        return this.actives;
    }

    /**
     * Adds pet to the active pets list.
     *
     * @param pet Player pet.
     */
    public void addActive(@Nonnull PlayerPet pet) {
        this.actives.add(Objects.requireNonNull(pet, "pet cannot be null!").getUID());
    }

    /**
     * Removes pet from the active pets list.
     *
     * @param pet Player pet.
     */
    public void removeActive(@Nonnull PlayerPet pet) {
        this.actives.remove(Objects.requireNonNull(pet, "pet cannot be null!").getUID());
    }
}
