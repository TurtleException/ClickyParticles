package de.turtleboi.spigot.clicky_particles.core;

import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Particles {
    public static final ConcurrentHashMap<String, Particle> PARTICLE_NAMES = new ConcurrentHashMap<>(Map.ofEntries(
            Map.entry("Hearts", Particle.HEART),
            Map.entry("Music", Particle.NOTE),
            Map.entry("Angry Villager", Particle.VILLAGER_ANGRY),
            Map.entry("Happy Villager", Particle.VILLAGER_HAPPY),
            Map.entry("Witch", Particle.SPELL_WITCH),
            Map.entry("Glow", Particle.GLOW),
            Map.entry("Portal", Particle.PORTAL),
            Map.entry("Wax", Particle.WAX_ON),
            Map.entry("Nautilus", Particle.NAUTILUS)
    ));


    /*
    I tried to implement the following part as efficient (computing speed) as possible
    to minimize the performance impact of this plugin as much as possible. This is why
    it might be a bit confusing, but I currently don't know a better solution.
     */

    public static final int PARTICLE_COUNT_DEFAULT = 10;

    public static final int[] PARTICLE_COUNT = new int[Particle.values().length];
    static {
        applyMultiplier(Particle.ASH, 10.0);
        applyMultiplier(Particle.COMPOSTER, 2.0);
        applyMultiplier(Particle.CRIMSON_SPORE, 5.0);
        applyMultiplier(Particle.CRIT, 1.5);
        applyMultiplier(Particle.DOLPHIN, 5.0);
        applyMultiplier(Particle.DRIPPING_DRIPSTONE_LAVA, 2.0);
        applyMultiplier(Particle.DRIPPING_DRIPSTONE_WATER, 2.0);
        applyMultiplier(Particle.DRIPPING_HONEY, 1.5);
        applyMultiplier(Particle.DRIP_LAVA, 2.0);
        applyMultiplier(Particle.DRIP_WATER, 2.0);
        applyMultiplier(Particle.ENCHANTMENT_TABLE, 2.5);
        applyMultiplier(Particle.EXPLOSION_NORMAL, 0.1);
        applyMultiplier(Particle.PORTAL, 7.5);
    }

    private static void applyMultiplier(@NotNull Particle particle, double multiplier) {
        // using enum ordinals is safe here (even across versions) because they are newly queried on class initialization
        PARTICLE_COUNT[particle.ordinal()] = (int) (PARTICLE_COUNT_DEFAULT * multiplier) + 1;
    }
}
