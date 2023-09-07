package de.turtleboi.spigot.clicky_particles.core;

import de.turtleboi.spigot.clicky_particles.ClickyParticles;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerService {
    private final PlayerConfig<Particle> particles = new PlayerConfig<>();
    private final PlayerConfig<String>   messages  = new PlayerConfig<>();

    private final YamlConfiguration config = new YamlConfiguration();
    private final File file;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public PlayerService() throws IOException, InvalidConfigurationException {
        this.file = new File(ClickyParticles.singleton.getDataFolder(), "particles.yml");

        this.file.getParentFile().mkdirs();
        this.file.createNewFile();

        this.load();
    }

    /* ----- ----- ----- */

    public PlayerConfig<Particle> particleConfig() {
        return particles;
    }

    public PlayerConfig<String> messageConfig() {
        return messages;
    }

    /* ----- ----- ----- */

    /**
     * Loads all particle and message associations from the YAML file in the plugins' data folder.
     * @throws IOException if the YAML config could not be loaded properly.
     * @throws InvalidConfigurationException if the YAML config could not be loaded properly.
     */
    public void load() throws IOException, InvalidConfigurationException {
        config.load(file);

        particles.set(parseParticle(config.getString("default.particle")));
        messages.set(config.getString("default.message"));

        for (String key : config.getKeys(false)) {
            if (key.equals("default")) continue;

            // try to parse the key to a player (fail silently and ignore this section)
            OfflinePlayer player = parsePlayer(key);
            if (player == null) continue;

            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) {
                ClickyParticles.singleton.getLogger().log(Level.FINE, "\"" + key + "\" does not hold a ConfigurationSection.");
                continue;
            }

            // try to define the default particle
            // note: this also removes the previously set particle if it is not listed in the new config
            particles.set(player, parseParticle(config.getString(key + ".default.particle")));

            // try to define the default message
            messages.set(player, config.getString(key + ".default.message"));

            // iterate through secondary players and define each value
            for (String sectionKey : section.getKeys(false)) {
                if (sectionKey.equals("default")) continue;

                OfflinePlayer clickedPlayer = parsePlayer(sectionKey);
                if (clickedPlayer == null) continue;

                // try to define the particle
                particles.set(player, clickedPlayer, parseParticle(config.getString(key + "." + sectionKey + ".particle")));

                // try to define the message
                messages.set(player, clickedPlayer, config.getString(key + "." + sectionKey + ".message"));
            }
        }
    }

    /**
     * Dumps all particle and message associations into a YAML config and saves it in the plugins' data folder.
     * @throws IOException if the YAML config could not be saved properly.
     */
    public void save() throws IOException {
        HashMap<String, Particle> particleMap = particles.getAsMap("particle");
        HashMap<String, String>    messageMap =  messages.getAsMap("message");

        for (String s : particleMap.keySet())
            config.set(s, particleMap.get(s));

        for (String s : messageMap.keySet())
            config.set(s, messageMap.get(s));

        config.save(file);
    }

    /**
     * Utility method to parse an {@link OfflinePlayer} from the String representation of its {@link UUID}.
     * If the UUID cannot be parsed <code>null</code> will be returned.
     */
    private static @Nullable OfflinePlayer parsePlayer(@NotNull String uuidString) {
        try {
            UUID uuid = UUID.fromString(uuidString);
            return ClickyParticles.singleton.getServer().getOfflinePlayer(uuid);
        } catch (IllegalArgumentException e) {
            // only log this as FINE to prevent spam
            ClickyParticles.singleton.getLogger().log(Level.FINE, "\"" + uuidString + "\" is not a valid UUID.");
            return null;
        }
    }

    /**
     * Utility method to parse a {@link Particle} from its String representation.
     * If the Particle cannot be parsed <code>null</code> will be returned.
     */
    private static @Nullable Particle parseParticle(@Nullable String str) {
        if (str == null) return null;

        try {
            return Particle.valueOf(str);
        } catch (IllegalArgumentException e) {
            // only log this as FINE to prevent spam
            ClickyParticles.singleton.getLogger().log(Level.FINE, "\"" + str + "\" is not a valid particle.");
            return null;
        }
    }
}
