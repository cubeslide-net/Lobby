package net.cubeslide.lobbysystem.handler;

import fr.mrmicky.fastboard.FastBoard;
import net.cubeslide.lobbysystem.LobbySystem;
import net.cubeslide.lobbysystem.commands.BuildCMD;
import net.cubeslide.lobbysystem.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PlayerHandler implements Listener {


    public static final String navigator_name = "§8[§dNavigator§8]";
    public static final String playerhider_name = "§8[§3Player§bHider§8]";
    public static final String enderpearl_name = "§8[§2Ender§aPearl§8]";
    private static final HashMap<UUID, Integer> playerUsedEP = new HashMap<>();

    final LobbySystem instance = LobbySystem.getInstance();

    public static void setInventory(Player player) {
        final Inventory playerInv = player.getInventory();
        playerInv.clear();
        playerInv.setItem(1, new ItemBuilder(Material.BLAZE_ROD).setDisplayname(playerhider_name).build());
        playerInv.setItem(4, new ItemBuilder(Material.COMPASS).setDisplayname(navigator_name).build());
        playerInv.setItem(7, new ItemBuilder(Material.ENDER_PEARL).setDisplayname(enderpearl_name).setLore("§7You can use this enderpearl. You will receive a new one in 30 seconds.").build());
    }

    public static HashMap<UUID, Integer> getPlayerUsedEP() {
        return playerUsedEP;
    }



    @EventHandler
    public void on(PlayerJoinEvent event) {
        final LobbySystem instance = LobbySystem.getInstance();
        final Player player = event.getPlayer();
        final String prefix = LobbySystem.getPrefix();
        event.setJoinMessage(null);
        if (instance.getConfig().get("spawn") != null) {
            player.teleport((Location) instance.getConfig().get("spawn"));
        } else {
            player.sendMessage(prefix + "§cSpawn Error: §7Please contact a team member.");
        }
        setInventory(player);

        for (Player hiders : InventoryHandler.hidden) {
            hiders.hidePlayer(event.getPlayer());
        }
        FastBoard board = new FastBoard(player);
        board.updateTitle(LobbySystem.getInstance().getConfig().getString("scoreboard.title").replace("&", "§"));
        instance.getBoards().put(player.getUniqueId(), board);
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        final LobbySystem instance = LobbySystem.getInstance();
        final Player player = event.getPlayer();
        event.setQuitMessage(null);
        FastBoard board = instance.getBoards().remove(player.getUniqueId());
        if (board != null) {
            board.delete();
        }
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player.getItemInHand().getType() == Material.ENDER_PEARL) {
            event.setCancelled(false);
            playerUsedEP.put(player.getUniqueId(), 30);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();
        if (!BuildCMD.getInBuildMode().contains(playerUUID)) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void on(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();
        if (!BuildCMD.getInBuildMode().contains(playerUUID)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();
        if (!BuildCMD.getInBuildMode().contains(playerUUID)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final Player player = (Player) event.getEntity();
        final UUID playerUUID = player.getUniqueId();
        if (!BuildCMD.getInBuildMode().contains(playerUUID)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();
        if (!BuildCMD.getInBuildMode().contains(playerUUID)) {
            if (player.getLocation().getBlockY() < 0) {
                Location loc = CheckpointHandler.getCurrentCheckpoint().get(player);
                if (loc == null) {
                    player.teleport((Location) Objects.requireNonNull(instance.getConfig().get("spawn")));
                } else {
                    player.teleport(CheckpointHandler.getCurrentCheckpoint().get(player));
                    player.sendMessage(LobbySystem.getPrefix() + "You were teleported to your last checkpoint. Use §3/checkpoint remove§7 to respawn at spawn.");
                }
            }
        }
    }

    @EventHandler
    public void on(WeatherChangeEvent event) {
        event.setCancelled(true);
    }
}
