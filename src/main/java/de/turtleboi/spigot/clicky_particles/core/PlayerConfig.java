package de.turtleboi.spigot.clicky_particles.core;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerConfig<T> {
    private final ConcurrentHashMap<OfflinePlayer, ConcurrentHashMap<OfflinePlayer, T>> playerValues = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<OfflinePlayer, T> defaultValues = new ConcurrentHashMap<>();
    private T defaultValue;

    /* ----- ----- ----- */

    public @Nullable T get(OfflinePlayer player, OfflinePlayer clickedPlayer) {
        ConcurrentHashMap<OfflinePlayer, T> map = playerValues.get(player);
        return map == null ? null : map.get(clickedPlayer);
    }

    public @Nullable T get(OfflinePlayer player) {
        return defaultValues.get(player);
    }

    public @Nullable T get() {
        return defaultValue;
    }

    public void set(@NotNull OfflinePlayer player, @NotNull OfflinePlayer clickedPlayer, @Nullable T value) {
        ConcurrentHashMap<OfflinePlayer, T> map = playerValues.get(player);

        if (map == null) {
            map = new ConcurrentHashMap<>();
            playerValues.put(player, map);
        }

        if (value == null) {
            map.remove(clickedPlayer);
        } else {
            map.put(clickedPlayer, value);
        }
    }

    public void set(@NotNull OfflinePlayer player, @Nullable T value) {
        if (value == null) {
            defaultValues.remove(player);
        } else {
            defaultValues.put(player, value);
        }
    }

    public void set(@Nullable T value) {
        defaultValue = value;
    }

    /* ----- ----- ----- */

    public HashMap<String, T> getAsMap(@NotNull String key) {
        HashMap<String, T> map = new HashMap<>();

        map.put("default." + key, defaultValue);

        for (OfflinePlayer player : defaultValues.keySet()) {
            map.put(player.getUniqueId().toString() + ".default." + key, defaultValues.get(player));
        }

        for (OfflinePlayer player : playerValues.keySet()) {
            ConcurrentHashMap<OfflinePlayer, T> specificPlayerValues = playerValues.get(player);
            for (OfflinePlayer clickedPlayer : specificPlayerValues.keySet()) {
                map.put(player.getUniqueId().toString() + "." + clickedPlayer.getUniqueId().toString() + "." + key, specificPlayerValues.get(clickedPlayer));
            }
        }

        return map;
    }
}
