package net.cubeslide.lobbysystem.commands;

import net.cubeslide.lobbysystem.LobbySystem;
import net.cubeslide.lobbysystem.handler.CheckpointHandler;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CheckpointCMD implements CommandExecutor {


    final File file = LobbySystem.getCheckpointFile();


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("LobbySystem.admin")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("list")) {
                        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                        ArrayList<Location> checkpoints = (ArrayList<Location>) config.getList("locations");
                        if (checkpoints == null) checkpoints = new ArrayList<>();
                        player.sendMessage(LobbySystem.getPrefix() + "Jump and Run - Checkpoints");
                        checkpoints.forEach(loc -> {
                            player.sendMessage("§cX: §7" + loc.getBlockX() + " §cY: §7" + loc.getBlockY() + " §cZ: §7" + loc.getBlockZ());
                        });
                    } else if (args[0].equalsIgnoreCase("set")) {
                        Location location = new Location(player.getWorld(), Math.round(player.getLocation().getX()), Math.round(player.getLocation().getY()), Math.round(player.getLocation().getZ()));
                        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                        ArrayList<Location> checkpoints;
                        if (config.get("locations") == null) {
                           checkpoints = new ArrayList<>();
                        } else {
                            checkpoints = (ArrayList<Location>) config.getList("locations");
                        }
                        if (!checkpoints.contains(location)) {
                            checkpoints.add(location);
                            config.set("locations", checkpoints);
                            player.sendMessage(LobbySystem.getPrefix() + "Checkpoint set.");
                            try {
                                config.save(file);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            player.sendMessage(LobbySystem.getPrefix() + "The checkpoint was already set.");
                        }
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        CheckpointHandler.getCurrentCheckpoint().put(player, null);
                        player.sendMessage(LobbySystem.getPrefix() + "You removed your checkpoint.");
                        player.teleport((Location) LobbySystem.getInstance().getConfig().get("spawn"));
                    } else {
                        player.sendMessage(LobbySystem.getPrefix() + LobbySystem.getPrefixUse() + "checkpoint list/set/remove");
                    }

                } else {
                    player.sendMessage(LobbySystem.getPrefix() + LobbySystem.getPrefixUse() + "checkpoint list/set/remove");
                }
            } else {
                player.sendMessage(LobbySystem.getPrefix() + LobbySystem.getNoPermission());
            }
        } else {
            sender.sendMessage(LobbySystem.getPrefix() + LobbySystem.getNoPlayer());
        }
        return false;
    }


}
