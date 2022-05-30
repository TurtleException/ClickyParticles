package de.eldritch.spigot.clicky_particles.interaction;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.eldritch.spigot.clicky_particles.ClickyParticles;
import de.eldritch.spigot.clicky_particles.core.Particles;
import de.eldritch.spigot.clicky_particles.core.PlayerConfig;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class ClickyGUI {
    private static final OfflinePlayer MHF_ARROW_LEFT = Bukkit.getOfflinePlayer(UUID.fromString("a68f0b64-8d14-4000-a95f-4b9ba14f8df9"));
    private static final OfflinePlayer MHF_ARROW_RIGHT = Bukkit.getOfflinePlayer(UUID.fromString("50c8510b-5ea0-4d60-be9a-7d542d6cd156"));

    public void generatePlayerGUI(@NotNull Player player, @NotNull Consumer<OfflinePlayer> clickAction) {
        TreeSet<OfflinePlayer> players = new TreeSet<>(Comparator
                .comparing(OfflinePlayer::isOnline)
                .thenComparing(OfflinePlayer::getName)
        );
        players.addAll(PlayerUtil.getPlayers());

        // remove the player that is using the GUI
        players.remove(player);

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (OfflinePlayer selectedPlayer : players) {
            guiItems.add(new GuiItem(PlayerUtil.getPlayerHeadFormat(player), event -> {
                event.setCancelled(true);
                clickAction.accept(selectedPlayer);
            }));
        }

        // default button
        GuiItem defaultItem = new GuiItem(
                renameItem(new ItemStack(Material.ZOMBIE_HEAD), ChatColor.GOLD + "Default"),
                event -> {
                    event.setCancelled(true);
                    clickAction.accept(null);
                });

        // create GUI
        ChestGui gui = buildGUI(contentRows(guiItems.size()), "Players", guiItems, defaultItem);

        // show GUI to player
        gui.show(player);
        gui.update();
    }

    public void generateParticleGUI(@NotNull Player player, @Nullable OfflinePlayer clickedPlayer, @NotNull Consumer<Particle> clickAction) {
        PlayerConfig<Particle> particleConfig = ClickyParticles.singleton.getPlayerService().particleConfig();
        Particle currentParticle = clickedPlayer != null
                ? particleConfig.get(player, clickedPlayer)
                : particleConfig.get(player);

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (Map.Entry<String, Particle> entry : Particles.PARTICLE_NAMES.entrySet()) {
            guiItems.add(getParticleItem(entry.getKey(), entry.getValue(), currentParticle, clickedPlayer, clickAction));
        }

        // default button
        GuiItem defaultItem = getParticleItem("Default", null, currentParticle, clickedPlayer, clickAction);

        String title = clickedPlayer != null ? clickedPlayer.getName() : "default";
        ChestGui gui = buildGUI(contentRows(guiItems.size()), "Particles (" + title + ")", guiItems, defaultItem);

        // show GUI to player
        gui.show(player);
        gui.update();
    }

    private static @NotNull GuiItem getParticleItem(@NotNull String name, @Nullable Particle particle, @Nullable Particle currentParticle, @Nullable OfflinePlayer clickedPlayer, @NotNull Consumer<Particle> clickAction) {
        // glowstone for currently selected particle, gunpowder for every other particle
        Material material = currentParticle != particle ? Material.GUNPOWDER : Material.GLOWSTONE_DUST;
        // golden name for currently selected particle, gray name for every other particle
        ChatColor color = currentParticle != particle ? ChatColor.GOLD : ChatColor.GRAY;

        // default button
        if (particle == null)
            material = Material.REDSTONE;

        return new GuiItem(
                renameItem(new ItemStack(material), color + name),
                event -> {
                    event.setCancelled(true);
                    clickAction.accept(particle);
                });
    }

    private static ItemStack renameItem(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null)
            meta.setDisplayName(name);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Calculates how many rows are needed to display the provided amount of items. This will return a maximum of 5 to
     * prevent gui overflow.
     */
    private static int contentRows(int contentItems) {
        return Math.min((int) Math.ceil(contentItems / 9.0), 5);
    }

    private static ChestGui buildGUI(int contentHeight, @NotNull String title, @NotNull List<GuiItem> items, @Nullable GuiItem defaultItem) {
        // create GUI
        ChestGui gui = new ChestGui(contentHeight + 1, title);

        PaginatedPane contentPane = new PaginatedPane(0, 0, 9, contentHeight);
        StaticPane    controlPane = new StaticPane(0, contentHeight, 9, 1);

        // populate with content items
        contentPane.populateWithGuiItems(items);

        // add default button
        if (defaultItem != null) {
            controlPane.addItem(defaultItem, 4, 0);
        }

        // add pagination support
        controlPane.addItem(new GuiItem(
                renameItem(PlayerUtil.getPlayerHead(MHF_ARROW_LEFT), ChatColor.GRAY + "Previous Page"),
                event -> {
                    event.setCancelled(true);
                    if (contentPane.getPage() > 0)
                        contentPane.setPage(contentPane.getPage() - 1);
                }), 2, 0);
        controlPane.addItem(new GuiItem(
                renameItem(PlayerUtil.getPlayerHead(MHF_ARROW_RIGHT), ChatColor.GRAY + "Next Page"),
                event -> {
                    event.setCancelled(true);
                    if (contentPane.getPage() < contentPane.getPages() - 1)
                        contentPane.setPage(contentPane.getPage() + 1);
                }), 6, 0);


        gui.addPane(contentPane);
        gui.addPane(contentPane);

        return gui;
    }
}
