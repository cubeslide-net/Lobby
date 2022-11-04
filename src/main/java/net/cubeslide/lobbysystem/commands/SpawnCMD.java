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
        if (instance.getConfig().get("spawn") != null) {
            player.teleport((Location) Objects.requireNonNull(instance.getConfig().get("spawn")));
        } else {
            player.sendMessage(prefix + "§cSomething went wrong! Please tell the Admin (Spawn not set) ");
        }
        return false;
    }
}
