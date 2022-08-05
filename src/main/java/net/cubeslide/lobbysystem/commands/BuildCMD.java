package net.cubeslide.lobbysystem.commands;

import java.util.ArrayList;
import java.util.UUID;
import net.cubeslide.lobbysystem.LobbySystem;
import net.cubeslide.lobbysystem.handler.JoinQuitHandler;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BuildCMD implements CommandExecutor {

  private static ArrayList<UUID> inBuildMode = new ArrayList<>();

  public static ArrayList<UUID> getInBuildMode() {
    return inBuildMode;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {
    if (sender instanceof Player) {
      final Player player = (Player) sender;
      if (player.hasPermission("LobbySystem.build") || player.hasPermission("LobbySystem.admin")) {
        final UUID playerUUID = player.getUniqueId();
        if (inBuildMode.contains(playerUUID)) {
          inBuildMode.remove(playerUUID);
          player.sendMessage(LobbySystem.getPrefix() + "You left the §3BuildMode§7.");
          player.setGameMode(GameMode.SURVIVAL);
          JoinQuitHandler.setInventory(player);
        } else {
          inBuildMode.add(playerUUID);
          player.sendMessage(LobbySystem.getPrefix() + "You entered the §3BuildMode§7.");
          player.setGameMode(GameMode.CREATIVE);
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
