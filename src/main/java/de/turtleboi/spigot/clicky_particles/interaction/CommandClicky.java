package de.turtleboi.spigot.clicky_particles.interaction;

import de.turtleboi.spigot.clicky_particles.ClickyParticles;
import de.turtleboi.spigot.clicky_particles.core.PlayerConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandClicky implements CommandExecutor {
    private final ClickyGUI clickyGUI = new ClickyGUI();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // fail silently if the sender is not a player
        if (!(sender instanceof Player player)) return true;

        clickyGUI.generatePlayerGUI(player, clickedPlayer ->
            clickyGUI.generateParticleGUI(player, clickedPlayer, particle -> {
                PlayerConfig particleConfig = ClickyParticles.singleton.getPlayerService().particleConfig();

                if (clickedPlayer == null) {
                    particleConfig.set(player.getUniqueId(), particle);
                } else {
                    particleConfig.set(player.getUniqueId(), clickedPlayer.getUniqueId(), particle);
                }

                player.closeInventory();
            })
        );

        return true;
    }
}
