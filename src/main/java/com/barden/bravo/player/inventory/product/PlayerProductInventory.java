package com.barden.bravo.player.inventory.product;

import com.barden.bravo.player.Player;
import com.barden.bravo.product.Product;
import com.barden.bravo.product.ProductProvider;
import com.barden.library.metadata.MetadataEntity;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import org.bson.BsonDocument;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Player product inventory class.
 */
public final class PlayerProductInventory extends MetadataEntity {

    private final Player player;
    private final BiMap<Long, PlayerProduct> content = HashBiMap.create();

    /**
     * Creates a player product inventory.
     *
     * @param player Player.
     */
    public PlayerProductInventory(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
    }

    /**
     * Creates a player product inventory from a bson document.
     *
     * @param player   Player.
     * @param document Player product inventory bson document.
     */
    public PlayerProductInventory(@Nonnull Player player, @Nonnull BsonDocument document) {
        //Objects null check.
        Objects.requireNonNull(document, "player product inventory bson document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");

        document.keySet().forEach(product_id_string -> {
            //Declares required fields.
            @Nonnull BsonDocument product_document = document.getDocument(product_id_string);
            long _id = product_document.getInt64("id").getValue();
            int _cap = product_document.getInt32("amount").getValue();

            //Creates new player product then adds to the products list.
            this.content.put(_id, new PlayerProduct(this.player, _id, _cap));
        });
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
     * Gets player products.
     *
     * @return Player products.
     */
    @Nonnull
    public Set<PlayerProduct> getContent() {
        return Sets.newHashSet(this.content.values());
    }

    /**
     * Finds player product. (SAFE)
     *
     * @param id Player product id.
     * @return Optional player product.
     */
    @Nonnull
    public Optional<PlayerProduct> find(long id) {
        return Optional.ofNullable(this.content.get(id));
    }

    /**
     * Gets player product. (UNSAFE)
     *
     * @param id Player product id.
     * @return Player product.
     */
    @Nonnull
    public PlayerProduct get(long id) {
        return this.find(id).orElseThrow(() -> new NullPointerException("player product cannot be null!"));
    }

    /**
     * Creates a player product.
     *
     * @param id     Product id.
     * @param amount Product amount.
     * @return Player product.
     */
    @Nonnull
    public PlayerProduct add(long id, int amount) {
        Product _product = ProductProvider.find(id).orElse(null);
        if (_product == null)
            throw new NullPointerException("product(" + id + ") does not exist!");

        PlayerProduct product = this.find(id).orElse(null);
        if (product != null) {
            product.addAmount(amount);
        } else {
            if (amount > _product.getCap())
                throw new IllegalStateException("Player product amount must be lower than or equals to " + _product.getCap());
            product = new PlayerProduct(this.player, id, amount);
            this.content.put(product.getId(), product);
        }

        return product;
    }

    /**
     * Removes player product.
     *
     * @param id Player product id.
     */
    public void remove(long id) {
        this.content.remove(id);
    }


    /*
    CONVERTERS
     */

    /**
     * Converts player product inventory to a json object.
     *
     * @return Player product inventory json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();
        this.content.forEach((key, value) -> object.add(key.toString(), value.toJsonObject()));
        return object;
    }

    /**
     * Converts player product inventory to a bson document.
     *
     * @return Player product inventory bson document.
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        BsonDocument document = new BsonDocument();
        this.content.forEach((key, value) -> document.put(key.toString(), value.toBsonDocument()));
        return document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player product inventory.
     *
     * @param json Player product json object.
     */
    public void update(@Nonnull JsonObject json) {
        //Objects null check.
        Objects.requireNonNull(json, "player product inventory json object cannot be null!");

        //Declares required fields.
        HashMap<Long, JsonObject> _content = new HashMap<>();
        json.entrySet().forEach(entry -> _content.put(Long.parseLong(entry.getKey()), entry.getValue().getAsJsonObject()));

        //Removing and updating existing ones.
        this.getContent().forEach(_item -> {
            var _id = _item.getId();

            //If item is existed, updates it.
            if (_content.containsKey(_id)) {
                _item.update(_content.get(_id));
                return;
            }

            //Removes item.
            this.remove(_id);
        });

        //Handles new player items.
        json.entrySet().forEach(entry -> {
            //Declares required fields.
            var _id = Long.parseLong(entry.getKey());

            //If item is existed, no need to continue.
            if (this.find(_id).isPresent())
                return;

            var _json = entry.getValue().getAsJsonObject();

            //Adds item to the player's inventory.
            this.content.put(_id, new PlayerProduct(this.player, _id, _json.get("amount").getAsInt()));
        });
    }
}
