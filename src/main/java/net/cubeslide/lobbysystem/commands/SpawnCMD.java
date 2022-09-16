package net.cubeslide.lobbysystem.commands;

import net.cubeslide.lobbysystem.LobbySystem;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class SpawnCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final LobbySystem instance = LobbySystem.getInstance();
        final String prefix = LobbySystem.getPrefix();
        if (!(sender instanceof Player)) {
            sender.sendMessage(LobbySystem.getNoPlayer());
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            if (!player.hasPermission("LobbySystem.admin")) {
                player.sendMessage(prefix + LobbySystem.getNoPermission());
                return true;
            }
            if (args[0].equalsIgnoreCase("set")) {
                instance.getConfig().set("spawn", player.getLocation());
                player.sendMessage(prefix + "§7Spawn set.");
            } else if (args[0].equalsIgnoreCase("delete")) {
                if (instance.getConfig().get("spawn") != null) {
                    instance.getConfig().set("spawn", null);
                    instance.saveConfig();
                    player.sendMessage(prefix + "§7Spawn deleted.");
                }
            } else {
                player.sendMessage(prefix + LobbySystem.getPrefixUse() + "spawn (set/delete)");
            }
        } else {
            if (instance.getConfig().get("spawn") != null) {
                player.teleport((Location) Objects.requireNonNull(instance.getConfig().get("spawn")));
            }
        }
        return false;
    }
}
