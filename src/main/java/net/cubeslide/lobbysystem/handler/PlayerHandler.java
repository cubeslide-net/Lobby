package net.cubeslide.lobbysystem.handler;

import net.cubeslide.lobbysystem.LobbySystem;
import net.cubeslide.lobbysystem.commands.BuildCMD;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerHandler implements Listener {

    final HashMap<UUID, Location> checkpoints = new HashMap<>();

    final LobbySystem instance = LobbySystem.getInstance();

    @EventHandler
    public void on(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player == null) return;
        final UUID playerUUID = player.getUniqueId();
        if (!BuildCMD.getInBuildMode().contains(playerUUID)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        if (player == null) return;
        final UUID playerUUID = player.getUniqueId();
        if (!BuildCMD.getInBuildMode().contains(playerUUID)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        if (player == null) return;
        final UUID playerUUID = player.getUniqueId();
        if (!BuildCMD.getInBuildMode().contains(playerUUID)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player)) return;
        final Player player = (Player) event.getEntity();
        if (player == null) return;
        final UUID playerUUID = player.getUniqueId();
        if (!BuildCMD.getInBuildMode().contains(playerUUID)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerMoveEvent event){
        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();
        if (!BuildCMD.getInBuildMode().contains(playerUUID)) {
            if(player.getLocation().getBlockY() < 0){
                Location loc = CheckpointHandler.getCurrentCheckpoint().get(player);
                if(loc == null) {
                    player.teleport((Location) instance.getConfig().get("spawn"));
                }else{
                    player.teleport(CheckpointHandler.getCurrentCheckpoint().get(player));
                    player.sendMessage(LobbySystem.getPrefix() + "You were teleported to your last checkpoint. Use §3/checkpoint remove§7 to respawn at spawn.");
                }
            }
        }
    }

    @EventHandler
    public void on(WeatherChangeEvent event){
        event.setCancelled(true);
    }

}
