package net.cubeslide.lobbysystem.handler;

import net.cubeslide.lobbysystem.LobbySystem;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class CheckpointHandler implements Listener {

    private static final CopyOnWriteArrayList<Player> cooldown = new CopyOnWriteArrayList<>();
    private static final HashMap<UUID, Location> currentCheckpoint = new HashMap<>();
    final File file = LobbySystem.getCheckpointFile();

    public static HashMap<UUID, Location> getCurrentCheckpoint() {
        return currentCheckpoint;
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ArrayList<Location> checkpoints = (ArrayList<Location>) config.getList("locations");
        if (checkpoints == null) {
            checkpoints = new ArrayList<>();
        }
        Location playerLocation = new Location(player.getWorld(), Math.round(player.getLocation().getX()), Math.round(player.getLocation().getY()), Math.round(player.getLocation().getZ()));
        checkpoints.forEach(loc -> {
            if ((loc.getX() == playerLocation.getX()) && (loc.getY() == playerLocation.getY()) && (loc.getZ() == playerLocation.getZ())) {
                if (currentCheckpoint.get(playerUUID) != null) {
                    if ((loc.getX() == currentCheckpoint.get(playerUUID).getX()) && (loc.getY() == currentCheckpoint.get(playerUUID).getY()) && (loc.getZ() == currentCheckpoint.get(playerUUID).getZ())) {
                        return;
                    }
                }
                if ((-80 == playerLocation.getX()) && (119 == playerLocation.getY()) && (-111 == playerLocation.getZ())) {
                    player.sendMessage(LobbySystem.getPrefix() + "§6§lCongrats! §7You reached the §3last checkpoint§7. Use §3/checkpoint remove§7.");
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                }
                currentCheckpoint.put(playerUUID, new Location(player.getWorld(), Math.round(player.getLocation().getX()), Math.round(player.getLocation().getY()), Math.round(player.getLocation().getZ())));
                player.sendMessage(LobbySystem.getPrefix() + "You have reached a checkpoint.");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            }
        });
    }
}
