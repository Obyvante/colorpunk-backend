package com.barden.bravo.product;

import com.barden.library.BardenJavaLibrary;
import com.barden.library.file.TomlFileLoader;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public final class ProductProvider {

    private static final BiMap<Long, Product> content = HashBiMap.create();

    /**
     * Initializes product provider.
     */
    public static void initialize() {
        //Handles configuration.
        TomlFileLoader.getConfig("products", false).ifPresent(file -> {
            //Loops through products.
            file.entrySet().forEach(_file -> {
                //Gets required fields.
                CommentedConfig _config = file.get(_file.getKey());
                long id = Long.parseLong(_file.getKey());
                String type = _config.get("type");
                String name = _config.get("name");
                boolean item = _config.get("item");
                int cap = _config.get("cap");

                HashMap<String, String> metadata = new HashMap<>();
                CommentedConfig _metadata = _config.get("metadata");
                if (_metadata != null)
                    _metadata.entrySet().forEach(entry -> metadata.put(entry.getKey(), entry.getValue().toString()));

                //Checks duplicate items.
                if (ProductProvider.find(id).isPresent()) {
                    BardenJavaLibrary.getLogger().error("[PRODUCT] -> Duplicate found! (" + id + ")");
                    return;
                }

                //Creates new product then adds to the content list.
                content.put(id, new Product(id, type, name, item, cap, metadata));
            });
        });

        //Logging.
        BardenJavaLibrary.getLogger().info(content.size() + " products are initialized successfully!");
    }

    /**
     * Gets products.
     *
     * @return Products.
     */
    @Nonnull
    public static Set<Product> getContent() {
        return content.values();
    }

    /**
     * Finds product. (SAFE)
     *
     * @param id Product id.
     * @return Optional product.
     */
    @Nonnull
    public static Optional<Product> find(long id) {
        return Optional.ofNullable(content.get(id));
    }

    /**
     * Gets product. (SAFE)
     *
     * @param id Product id.
     * @return Product.
     */
    @Nonnull
    public static Product get(long id) {
        return find(id).orElseThrow(() -> new NullPointerException("product cannot be null!"));
    }
}
