package de.turtleboi.spigot.clicky_particles;

import de.turtleboi.spigot.clicky_particles.core.PlayerService;
import de.turtleboi.spigot.clicky_particles.interaction.CommandClicky;
import de.turtleboi.spigot.clicky_particles.listener.PlayerInteractEntityListener;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.logging.Level;

public class ClickyParticles extends JavaPlugin {
    public static ClickyParticles singleton;

    private PlayerService playerService;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        singleton = this;

        this.saveDefaultConfig();
        try {
            playerService = new PlayerService();
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        this.getServer().getPluginManager().registerEvents(new PlayerInteractEntityListener(), this);

        this.getCommand("clicky").setExecutor(new CommandClicky());
    }

    @Override
    public void onDisable() {
        try {
            playerService.save();
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Could not save particle config!", e);
        }
    }

    public PlayerService getPlayerService() {
        return playerService;
    }
}
