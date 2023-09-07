package de.turtleboi.spigot.clicky_particles.core;

import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerConfig {
    private final ConcurrentHashMap<OfflinePlayer, ConcurrentHashMap<OfflinePlayer, Particle>> playerValues = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<OfflinePlayer, Particle> defaultValues = new ConcurrentHashMap<>();
    private Particle defaultValue;

    /* ----- ----- ----- */

    public @Nullable Particle get(OfflinePlayer player, OfflinePlayer clickedPlayer) {
        ConcurrentHashMap<OfflinePlayer, Particle> map = playerValues.get(player);
        return map == null ? null : map.get(clickedPlayer);
    }

    public @Nullable Particle get(OfflinePlayer player) {
        return defaultValues.get(player);
    }

    public @Nullable Particle get() {
        return defaultValue;
    }

    public void set(@NotNull OfflinePlayer player, @NotNull OfflinePlayer clickedPlayer, @Nullable Particle value) {
        ConcurrentHashMap<OfflinePlayer, Particle> map = playerValues.get(player);

        if (map == null) {
            map = new ConcurrentHashMap<>();
            playerValues.put(player, map);
        }

        if (value == null)
            map.remove(clickedPlayer);
        else
            map.put(clickedPlayer, value);
    }

    public void set(@NotNull OfflinePlayer player, @Nullable Particle value) {
        if (value == null) {
            defaultValues.remove(player);
        } else {
            defaultValues.put(player, value);
        }
    }

    public void set(@Nullable Particle value) {
        defaultValue = value;
    }

    /* ----- ----- ----- */

    public HashMap<String, Particle> getAsMap() {
        HashMap<String, Particle> map = new HashMap<>();

        map.put("default", defaultValue);

        for (OfflinePlayer player : defaultValues.keySet()) {
            map.put(player.getUniqueId() + ".default", defaultValues.get(player));
        }

        for (OfflinePlayer player : playerValues.keySet()) {
            ConcurrentHashMap<OfflinePlayer, Particle> specificPlayerValues = playerValues.get(player);
            for (OfflinePlayer clickedPlayer : specificPlayerValues.keySet()) {
                map.put(player.getUniqueId() + "." + clickedPlayer.getUniqueId(), specificPlayerValues.get(clickedPlayer));
            }
        }

        return map;
    }
}
