package net.cubeslide.lobbysystem;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionManagement;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import fr.mrmicky.fastboard.FastBoard;
import net.cubeslide.lobbysystem.commands.BuildCMD;
import net.cubeslide.lobbysystem.commands.CheckpointCMD;
import net.cubeslide.lobbysystem.commands.SpawnCMD;
import net.cubeslide.lobbysystem.handler.CheckpointHandler;
import net.cubeslide.lobbysystem.handler.InventoryHandler;
import net.cubeslide.lobbysystem.handler.PlayerHandler;
import net.cubeslide.lobbysystem.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class LobbySystem extends JavaPlugin {

    private static LobbySystem instance;
    private static File file;
    private final Map<UUID, FastBoard> boards = new HashMap<>();
    ArrayList<String> tmp_list = new ArrayList();

    public static LobbySystem getInstance() {
        return instance;
    }

    public static String getPrefix() {
        return Objects.requireNonNull(instance.getConfig().getString("prefix")).replace("&", "§");
    }

    public static String getNoPermission() {
        return Objects.requireNonNull(instance.getConfig().getString("noPermission")).replace("&", "§");
    }

    public static String getNoPlayer() {
        return Objects.requireNonNull(instance.getConfig().getString("noPlayer")).replace("&", "§");
    }

    public static String getPlayerNotOnline() {
        return Objects.requireNonNull(instance.getConfig().getString("playerNotOnline")).replace("&", "§");
    }

    public static String getPrefixUse() {
        return Objects.requireNonNull(instance.getConfig().getString("prefixUse")).replace("&", "§");
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
        pluginManager.registerEvents(new InventoryHandler(), this);
        pluginManager.registerEvents(new PlayerHandler(), this);
        pluginManager.registerEvents(new CheckpointHandler(), this);

        getConfig().addDefault("prefix", "&3CubeSlide &8» &7");
        getConfig().addDefault("noPermission", "&cYou don't have permissions to do that.");
        getConfig().addDefault("noPlayer", "&cYou need to be a player to do that.");
        getConfig().addDefault("playerNotOnline", "&cThe target player could not be found.");
        getConfig().addDefault("prefixUse", "&cPlease use: &e/");
        getConfig().addDefault("scoreboard.title", "&8» &3Lobby");
        getConfig().addDefault("scoreboard.content", Arrays.asList("&cLine 1", "&aLine 2", "&7Line 3", "&8Line 4"));
        getConfig().options().copyDefaults(true);
        saveConfig();

        Bukkit.getConsoleSender().sendMessage(Objects.requireNonNull(instance.getConfig().getString("prefix")).replace("&", "§") + "&aPlugin enabled.");

        Objects.requireNonNull(getCommand("spawn")).setExecutor(new SpawnCMD());
        Objects.requireNonNull(getCommand("build")).setExecutor(new BuildCMD());
        Objects.requireNonNull(getCommand("checkpoint")).setExecutor(new CheckpointCMD());


        getServer().getScheduler().runTaskTimer(this, () -> {
            for (FastBoard board : this.boards.values()) {
                updateBoard(board);
            }

            Bukkit.getOnlinePlayers().forEach(player -> {

                final HashMap<UUID, Integer> player_cooldown_map = InventoryHandler.getHasPlayerHiderCooldown();
                if (!player_cooldown_map.containsKey(player.getUniqueId())) return;

                if (player_cooldown_map.get(player.getUniqueId()) < 1) {
                    player_cooldown_map.remove(player.getUniqueId());
                } else {
                    player_cooldown_map.put(player.getUniqueId(), player_cooldown_map.get(player.getUniqueId()) - 1);
                }
            });

            Bukkit.getOnlinePlayers().forEach(player -> {
                final HashMap<UUID, Integer> map = PlayerHandler.getPlayerUsedEP();
                if (!map.containsKey(player.getUniqueId())) return;
                if (map.get(player.getUniqueId()) < 1) {
                    if (!player.getInventory().contains(new ItemBuilder(Material.ENDER_PEARL).build())) {
                        player.getInventory().setItem(7, new ItemBuilder(Material.ENDER_PEARL).build());
                        PlayerHandler.getPlayerUsedEP().remove(player.getUniqueId());
                    }
                    return;
                } else {
                    map.put(player.getUniqueId(), map.get(player.getUniqueId()) - 1);
                }
            });
        }, 0, 20);

    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    private void updateBoard(FastBoard board) {
        final Player player = board.getPlayer();
        final CloudNetDriver cloudNetDriver = CloudNetDriver.getInstance();
        final IPlayerManager iPlayerManager = cloudNetDriver.getServicesRegistry().getFirstService(IPlayerManager.class);
        final IPermissionUser iPermissionUser = cloudNetDriver.getPermissionManagement().getUser(player.getUniqueId());
        final IPermissionManagement permissionManagement = cloudNetDriver.getPermissionManagement();


        tmp_list.clear();
        getConfig().getStringList("scoreboard.content").forEach(line -> {
            tmp_list.add(line.replace("%rank%", permissionManagement.getHighestPermissionGroup(iPermissionUser).getName()).replace("%server%", iPlayerManager.getOnlinePlayer(player.getUniqueId()).getConnectedService().getServerName()).replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size())).replace("&", "§"));
        });
        board.updateLines(tmp_list);
        board.updateTitle(getConfig().getString("scoreboard.title").replace("&", "§"));
    }


    public Map<UUID, FastBoard> getBoards() {
        return boards;
    }
}
