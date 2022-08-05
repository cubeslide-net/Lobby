package net.cubeslide.lobbysystem;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import net.cubeslide.lobbysystem.commands.BuildCMD;
import net.cubeslide.lobbysystem.commands.CheckpointCMD;
import net.cubeslide.lobbysystem.commands.SpawnCMD;
import net.cubeslide.lobbysystem.handler.CheckpointHandler;
import net.cubeslide.lobbysystem.handler.InventoryHandler;
import net.cubeslide.lobbysystem.handler.JoinQuitHandler;
import net.cubeslide.lobbysystem.handler.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class LobbySystem extends JavaPlugin {

  private static LobbySystem instance;
  private static File file;

  public static LobbySystem getInstance() {
    return instance;
  }

  public static String getPrefix() {
    return Objects.requireNonNull(instance.getConfig().getString("prefix"))
        .replace("&", "§");
  }

  public static String getNoPermission() {
    return Objects.requireNonNull(instance.getConfig().getString("noPermission"))
        .replace("&", "§");
  }

  public static String getNoPlayer() {
    return Objects.requireNonNull(instance.getConfig().getString("noPlayer"))
        .replace("&", "§");
  }

  public static String getPlayerNotOnline() {
    return Objects.requireNonNull(instance.getConfig().getString("playerNotOnline"))
        .replace("&", "§");
  }

  public static String getPrefixUse() {
    return Objects.requireNonNull(instance.getConfig().getString("prefixUse"))
        .replace("&", "§");
  }

  public static File getCheckpointFile() {
    return file;
  }

  @Override
  public void onEnable() {
    instance = this;
    file = new File("plugins/LobbySystem/", "checkpoints.yml");
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    final PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(new JoinQuitHandler(), this);
    pluginManager.registerEvents(new InventoryHandler(), this);
    pluginManager.registerEvents(new PlayerHandler(), this);
    pluginManager.registerEvents(new CheckpointHandler(), this);

    getConfig().addDefault("prefix", "&3CubeSlide &8» &7");
    getConfig().addDefault("noPermission", "&cYou don't have permissions to do that.");
    getConfig().addDefault("noPlayer", "&cYou need to be a player to do that.");
    getConfig().addDefault("playerNotOnline", "&cThe target player could not be found.");
    getConfig().addDefault("prefixUse", "&cPlease use: &e/");
    getConfig().options().copyDefaults(true);
    saveConfig();

    Bukkit.getConsoleSender().sendMessage(
        Objects.requireNonNull(instance.getConfig().getString("prefix")).replace("&", "§") + "&aPlugin enabled.");

    Objects.requireNonNull(getCommand("spawn")).setExecutor(new SpawnCMD());
    Objects.requireNonNull(getCommand("build")).setExecutor(new BuildCMD());
    Objects.requireNonNull(getCommand("checkpoint")).setExecutor(new CheckpointCMD());

  }

  @Override
  public void onDisable() {
    saveConfig();
  }

}
