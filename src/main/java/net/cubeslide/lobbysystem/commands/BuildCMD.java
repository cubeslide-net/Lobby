package net.cubeslide.lobbysystem.commands;

import net.cubeslide.lobbysystem.LobbySystem;
import net.cubeslide.lobbysystem.handler.PlayerHandler;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class BuildCMD implements CommandExecutor {

    private static final ArrayList<UUID> inBuildMode = new ArrayList<>();

    public static ArrayList<UUID> getInBuildMode() {
        return inBuildMode;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(LobbySystem.getNoPlayer());
            return true;
        }
        final Player player = (Player) sender;
        if (!player.hasPermission("LobbySystem.build") || !player.hasPermission("LobbySystem.admin")) {
            player.sendMessage(LobbySystem.getNoPermission());
            return true;
        }
        final UUID playerUUID = player.getUniqueId();
        if (inBuildMode.contains(playerUUID)) {
            inBuildMode.remove(playerUUID);
            player.sendMessage(LobbySystem.getPrefix() + "You left the §3BuildMode§7.");
            player.setGameMode(GameMode.SURVIVAL);
            PlayerHandler.setInventory(player);
        } else {
            inBuildMode.add(playerUUID);
            player.sendMessage(LobbySystem.getPrefix() + "You entered the §3BuildMode§7.");
            player.setGameMode(GameMode.CREATIVE);
        }
        return false;
    }
}
