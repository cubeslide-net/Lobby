package net.cubeslide.lobbysystem;

import net.cubeslide.lobbysystem.commands.BuildCMD;
import net.cubeslide.lobbysystem.commands.CheckpointCMD;
import net.cubeslide.lobbysystem.commands.SpawnCMD;
import net.cubeslide.lobbysystem.handler.PlayerHandler;
import net.cubeslide.lobbysystem.handler.CheckpointHandler;
import net.cubeslide.lobbysystem.handler.InventoryHandler;
import net.cubeslide.lobbysystem.handler.JoinQuitHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class LobbySystem extends JavaPlugin {

    private static LobbySystem instance;
    private static File file;


    @Override
    public void onEnable() {
        instance = this;
        file = new File("plugins/LobbySystem/", "checkpoints.yml");
        if(!file.exists()){
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

        getConfig().addDefault("MYSQL.HOSTNAME", "localhost");
        getConfig().addDefault("MYSQL.USERNAME", "root");
        getConfig().addDefault("MYSQL.PASSWORD", "");
        getConfig().addDefault("MYSQL.DATABASE", "SkyWarsFFA");
        getConfig().addDefault("MYSQL.PORT", 3306);
        getConfig().addDefault("prefix", "&3CubeSlide &8» &7");
        getConfig().addDefault("noPermission", "&cYou don't have permissions to do that.");
        getConfig().addDefault("noPlayer", "&cYou need to be a player to do that.");
        getConfig().addDefault("playerNotOnline", "&cThe target player could not be found.");
        getConfig().addDefault("prefixUse", "&cPlease use: &e/");
        getConfig().options().copyDefaults(true);
        saveConfig();

        Bukkit.getConsoleSender().sendMessage(instance.getConfig().getString("prefix").replace("&", "§") + "&aPlugin enabled.");

        getCommand("spawn").setExecutor(new SpawnCMD());
        getCommand("build").setExecutor(new BuildCMD());
        getCommand("checkpoint").setExecutor(new CheckpointCMD());

    }

    public static LobbySystem getInstance() {
        return instance;
    }

    public static String getPrefix() {
        final String data = instance.getConfig().getString("prefix").replace("&", "§");
        if (data != null) {
            return data;
        }
        Bukkit.getConsoleSender().sendMessage("§3CubeSlide §8» §7");
        return "&5&lServer &8| &7";
    }

    public static String getNoPermission() {
        final String data = instance.getConfig().getString("noPermission").replace("&", "§");
        if (data != null) {
            return data;
        }
        Bukkit.getConsoleSender().sendMessage("§3CubeSlide §8» §cError: §7String 'noPermission' not found in config.yml");
        return null;
    }

    public static String getNoPlayer() {
        final String data = instance.getConfig().getString("noPlayer").replace("&", "§");
        if (data != null) {
            return data;
        }
        Bukkit.getConsoleSender().sendMessage("§3CubeSlide §8» §cError: §7String 'noPlayer' not found in config.yml");
        return null;
    }

    public static String getPlayerNotOnline() {
        final String data = instance.getConfig().getString("playerNotOnline").replace("&", "§");
        if (data != null) {
            return data;
        }
        Bukkit.getConsoleSender().sendMessage("§3CubeSlide §8» §cError: §7String 'playerNotOnline' not found in config.yml");
        return null;
    }

    public static String getPrefixUse() {
        final String data = instance.getConfig().getString("prefixUse").replace("&", "§");
        if (data != null) {
            return data;
        }
        Bukkit.getConsoleSender().sendMessage("§3CubeSlide §8» §cError: §7String 'prefixUse' not found in config.yml");
        return null;
    }


    public static File getCheckpointFile() {
        return file;
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

}
