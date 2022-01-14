package com.barden.bravo.player.inventory.trail;

import com.barden.bravo.player.Player;
import com.barden.bravo.player.inventory.PlayerInventory;
import com.barden.library.metadata.MetadataEntity;
import com.google.gson.JsonObject;
import org.bson.BsonDocument;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Player trail inventory class.
 */
public final class PlayerTrailInventory extends MetadataEntity {

    private final Player player;
    private final PlayerInventory inventory;
    private final HashSet<PlayerTrail> content = new HashSet<>();
    private HashSet<PlayerTrail> actives = new HashSet<>();

    /**
     * Creates player trail inventory object.
     *
     * @param player    Player.
     * @param inventory Player inventory.
     */
    public PlayerTrailInventory(@Nonnull Player player, @Nonnull PlayerInventory inventory) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.inventory = Objects.requireNonNull(inventory, "player inventory cannot be null!");
    }

    /**
     * Creates player trail inventory object.
     *
     * @param player       Player.
     * @param inventory    Player inventory.
     * @param bsonDocument Bson document. (MONGO)
     */
    public PlayerTrailInventory(@Nonnull Player player, @Nonnull PlayerInventory inventory, @Nonnull BsonDocument bsonDocument) {
        //Objects null check.
        Objects.requireNonNull(bsonDocument, "player trail inventory bson document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.inventory = Objects.requireNonNull(inventory, "player inventory cannot be null!");

        //Gets trail content.
        @Nonnull BsonDocument trails_content = Objects.requireNonNull(bsonDocument.getDocument("content"), "player trail inventory content bson document cannot be null!");
        //Loops trail bson documents.
        trails_content.keySet().forEach(trail_uid_string -> {
            //Declares required fields.
            @Nonnull BsonDocument trail_document = Objects.requireNonNull(trails_content.getDocument(trail_uid_string), "player trail bson document cannot be null!");
            int trail_id = Objects.requireNonNull(trail_document.getInt32("id"), "trail id cannot be null!").intValue();
            boolean trail_active = trail_document.getBoolean("active").getValue();
            @Nonnull UUID trail_uid = UUID.fromString(trail_uid_string);

            //Creates new player trail then adds to the trails list.
            this.content.add(new PlayerTrail(this.player, this, trail_id, trail_uid, trail_active));
        });

        //Refreshes actives.
        this.refreshActives();
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
     * Gets trails.
     *
     * @return Player trails.
     */
    @Nonnull
    public HashSet<PlayerTrail> getContent() {
        return this.content;
    }

    /**
     * Gets trails.
     *
     * @return Player trails. (SAFE)
     */
    @Nonnull
    public HashSet<PlayerTrail> getContentSafe() {
        return new HashSet<>(this.content);
    }

    /**
     * Finds trail. (SAFE)
     *
     * @param uid Player trail unique id.
     * @return Optional player trail.
     */
    @Nonnull
    public Optional<PlayerTrail> find(@Nonnull UUID uid) {
        return this.content.stream().filter(trail -> trail.getUID().equals(Objects.requireNonNull(uid, "unique id cannot be null!"))).findFirst();
    }

    /**
     * Gets trail. (UNSAFE)
     *
     * @param uid Player trail unique id.
     * @return Player trail.
     */
    @Nonnull
    public PlayerTrail get(@Nonnull UUID uid) {
        return this.find(uid).orElseThrow(() -> new NullPointerException("player trail cannot be null!"));
    }

    /**
     * Creates new trail.
     *
     * @param id Trail id.
     * @return Player trail.
     */
    @Nonnull
    public PlayerTrail create(int id) {
        //Creates new trail.
        PlayerTrail trail = new PlayerTrail(this.player, this, id, UUID.randomUUID(), false);
        //Adds created trail to the trails list.
        this.content.add(trail);
        //Returns created trail.
        return trail;
    }

    /**
     * Adds new trail to the trails list.
     *
     * @param trail Player trail.
     */
    public void add(@Nonnull PlayerTrail trail) {
        //Adds trail to the trails list.
        this.content.add(Objects.requireNonNull(trail, "player trail cannot be null!"));
    }

    /**
     * Removes new trail to from trails list.
     *
     * @param player_trail Player trail.
     */
    public void remove(@Nonnull PlayerTrail player_trail) {
        //Removes trail from the trails list.
        this.content.remove(player_trail);
        //Updates player trail for player trail storage.
        this.updateActive(player_trail);
    }


    /**
     * Gets active player trails.
     *
     * @return Active player trails.
     */
    @Nonnull
    public HashSet<PlayerTrail> getActives() {
        return this.actives;
    }

    /**
     * Gets active player trails.
     *
     * @return Active player trails. (SAFE)
     */
    @Nonnull
    public HashSet<PlayerTrail> getActivesSafe() {
        return new HashSet<>(this.actives);
    }

    /**
     * Updates player trail active status.
     *
     * @param player_trail Player trail.
     */
    public void updateActive(@Nonnull PlayerTrail player_trail) {
        //Objects null check.
        Objects.requireNonNull(player_trail, "player trail cannot be null!");

        //If player trail is removed, no need to continue.
        if (this.find(player_trail.getUID()).isEmpty()) {
            this.actives.remove(player_trail);
            return;
        }

        //Handles player trail path in the storage.
        if (player_trail.isActive())
            this.actives.add(player_trail);
        else
            this.actives.remove(player_trail);
    }

    /**
     * Refreshes actives trails list.
     */
    public void refreshActives() {
        this.actives = this.getContent().stream().filter(PlayerTrail::isActive).collect(Collectors.toCollection(HashSet::new));
    }


    /*
    CONVERTERS
     */

    /**
     * Gets player trail inventory as a json object.
     *
     * @return Player trail inventory as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        //Creates json object.
        JsonObject json_object = new JsonObject();

        //Configures trails.
        JsonObject trails_json_object = new JsonObject();
        //Loops through trails and adds one by one to the created json object.
        this.content.forEach(trail -> trails_json_object.add(trail.getUID().toString(), trail.toJsonObject()));
        //Adds trail(s) json object to the base json object.
        json_object.add("content", trails_json_object);

        //Returns created json object.
        return json_object;
    }

    /**
     * Converts player trail inventory object to bson document.
     *
     * @return Player trail inventory bson document. (MONGO)
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        //Creates empty bson document.
        BsonDocument bson_document = new BsonDocument();

        //Creates content bson document.
        BsonDocument content_document = new BsonDocument();
        //Loops through content and add trails one by one to the content bson document.
        this.content.forEach(trail -> content_document.put(trail.getUID().toString(), trail.toBsonDocument()));
        //Puts content bson document to the base document.
        bson_document.put("content", content_document);

        //Returns created bson document.
        return bson_document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player trail inventory object.
     *
     * @param json_object Json object.
     */
    public void update(@Nonnull JsonObject json_object) {
        //Objects null check.
        Objects.requireNonNull(json_object, "player trail inventory json object cannot be null!");

        //Gets content json object from the declared json object.
        JsonObject content_json_object = json_object.getAsJsonObject("content");

        //Loops through trails.
        this.getContentSafe().forEach(trail -> {
            //Handles existing trails.
            if (content_json_object.keySet().contains(trail.getUID().toString()))
                trail.update(content_json_object.getAsJsonObject(trail.getUID().toString())); //If trail is exist in the content json object.
            else
                trail.delete(); //Deletes trail since it is no longer in the inventory.
        });

        //Handles new trails.
        content_json_object.keySet().stream().filter(trail_uid_string -> this.find(UUID.fromString(trail_uid_string)).isEmpty()).forEach(trail_uid_string -> {
            //Declares trail uid.
            @Nonnull UUID trail_uid = UUID.fromString(trail_uid_string);
            //Creates new player trail object then adds to the content list.
            this.content.add(new PlayerTrail(this.player, this, trail_uid, content_json_object.getAsJsonObject(trail_uid_string)));
        });
    }
}
