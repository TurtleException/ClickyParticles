package de.turtleboi.spigot.clicky_particles.listener;

import de.turtleboi.spigot.clicky_particles.ClickyParticles;
import de.turtleboi.spigot.clicky_particles.core.Particles;
import de.turtleboi.spigot.clicky_particles.core.PlayerService;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntityListener implements Listener {
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        // fail silently if the entity is not a player
        if (!(event.getRightClicked() instanceof Player entityPlayer)) return;

        PlayerService pService = ClickyParticles.singleton.getPlayerService();
        Particle      particle = pService.particleConfig().get(event.getPlayer().getUniqueId(), entityPlayer.getUniqueId());

        if (particle == null)
            particle = pService.particleConfig().get(event.getPlayer().getUniqueId());
        if (particle == null)
            particle = pService.particleConfig().get();
        if (particle == null)
            return; // no default particle


        int count = Particles.PARTICLE_COUNT[particle.ordinal()] > 0
                ? Particles.PARTICLE_COUNT[particle.ordinal()]
                : Particles.PARTICLE_COUNT_DEFAULT;

        entityPlayer.getWorld().spawnParticle(particle, entityPlayer.getLocation().add(0, 1, 0), count, 0.5, 0.5, 0.5);
    }
}
