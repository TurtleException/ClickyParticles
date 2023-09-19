package de.turtleboi.spigot.clicky_particles.core;

import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerConfig {
    private final ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, Particle>> playerValues = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Particle> defaultValues = new ConcurrentHashMap<>();
    private Particle defaultValue;

    /* ----- ----- ----- */

    public @Nullable Particle get(UUID player, UUID clickedPlayer) {
        ConcurrentHashMap<UUID, Particle> map = playerValues.get(player);
        return map == null ? null : map.get(clickedPlayer);
    }

    public @Nullable Particle get(UUID player) {
        return defaultValues.get(player);
    }

    public @Nullable Particle get() {
        return defaultValue;
    }

    public void set(@NotNull UUID player, @NotNull UUID clickedPlayer, @Nullable Particle value) {
        ConcurrentHashMap<UUID, Particle> map = playerValues.get(player);

        if (map == null) {
            map = new ConcurrentHashMap<>();
            playerValues.put(player, map);
        }

        if (value == null)
            map.remove(clickedPlayer);
        else
            map.put(clickedPlayer, value);
    }

    public void set(@NotNull UUID player, @Nullable Particle value) {
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

        for (UUID player : defaultValues.keySet()) {
            map.put(player + ".default", defaultValues.get(player));
        }

        for (UUID player : playerValues.keySet()) {
            ConcurrentHashMap<UUID, Particle> specificPlayerValues = playerValues.get(player);
            for (UUID clickedPlayer : specificPlayerValues.keySet()) {
                map.put(player + "." + clickedPlayer, specificPlayerValues.get(clickedPlayer));
            }
        }

        return map;
    }
}
