package de.turtleboi.spigot.clicky_particles.interaction;

import de.turtleboi.spigot.clicky_particles.ClickyParticles;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class PlayerUtil {
    public static List<OfflinePlayer> getPlayers() {
        ArrayList<OfflinePlayer> players = new ArrayList<>(ClickyParticles.singleton.getServer().getWhitelistedPlayers());

        for (OfflinePlayer offlinePlayer : ClickyParticles.singleton.getServer().getOfflinePlayers())
            if (!players.contains(offlinePlayer))
                players.add(offlinePlayer);

        return players;
    }

    /**
     * Provides the players head as an {@link ItemStack} if the player is online or a skeleton skull if not. The item
     * will have the name of the player in both cases.
     */
    public static ItemStack getPlayerHeadFormat(OfflinePlayer player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);

            if (player instanceof Player oPlayer) {
                meta.setDisplayName(ChatColor.GOLD + oPlayer.getDisplayName());
            } else {
                meta.setDisplayName(ChatColor.GRAY + Objects.requireNonNullElse(player.getName(), "Unknown player"));
            }
        }
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack getPlayerHead(OfflinePlayer player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null)
            meta.setOwningPlayer(player);
        item.setItemMeta(meta);

        return item;
    }
}
