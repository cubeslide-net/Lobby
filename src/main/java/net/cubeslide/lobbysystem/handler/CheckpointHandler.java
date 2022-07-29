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
import java.util.concurrent.CopyOnWriteArrayList;

public class CheckpointHandler implements Listener {
    private static final CopyOnWriteArrayList<Player> cooldown = new CopyOnWriteArrayList<>();
    private static final HashMap<Player, Location> currentCheckpoint = new HashMap<>();
    final File file = LobbySystem.getCheckpointFile();

    public static HashMap<Player, Location> getCurrentCheckpoint() {
        return currentCheckpoint;
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ArrayList<Location> checkpoints = (ArrayList<Location>) config.getList("locations");
        if (checkpoints == null) checkpoints = new ArrayList<>();
        Location playerLocation = new Location(player.getWorld(), Math.round(player.getLocation().getX()), Math.round(player.getLocation().getY()), Math.round(player.getLocation().getZ()));

        checkpoints.forEach(loc -> {
            if ((loc.getX() == playerLocation.getX()) && (loc.getY() == playerLocation.getY()) && (loc.getZ() == playerLocation.getZ())) {


                if (currentCheckpoint.get(player) != null) {
                    if ((loc.getX() == currentCheckpoint.get(player).getX()) && (loc.getY() == currentCheckpoint.get(player).getY()) && (loc.getZ() == currentCheckpoint.get(player).getZ()))
                        return;

                }

                if ((-80 == playerLocation.getX()) && (119 == playerLocation.getY()) && (-111 == playerLocation.getZ())) {
                    player.sendMessage(LobbySystem.getPrefix() + "§6§lCongrats! §7You reached the §3last checkpoint§7. Use §3/checkpoint remove§7.");
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                }

                currentCheckpoint.put(player, new Location(player.getWorld(), Math.round(player.getLocation().getX()), Math.round(player.getLocation().getY()), Math.round(player.getLocation().getZ())));
                player.sendMessage(LobbySystem.getPrefix() + "You have reached a checkpoint.");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);


            }
        });

    }


}
