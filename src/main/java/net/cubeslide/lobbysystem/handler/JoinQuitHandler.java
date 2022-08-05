package net.cubeslide.lobbysystem.handler;

import net.cubeslide.lobbysystem.LobbySystem;
import net.cubeslide.lobbysystem.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class JoinQuitHandler implements Listener {
    public static final String navigator_name = "§8[§9Navigator§8]";
    public static final String playerhider_name = "§8[§9Player§3Hider§8]";

    @EventHandler
    public void on(PlayerJoinEvent event){
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

        for(Player hiders : InventoryHandler.hidden){
            hiders.hidePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent event){
        event.setQuitMessage(null);
    }

    public static void setInventory(Player player){
        final Inventory playerInv = player.getInventory();
        playerInv.clear();
        playerInv.setItem(1, new ItemBuilder(Material.BLAZE_ROD).setDisplayname(playerhider_name).build());
        playerInv.setItem(4, new ItemBuilder(Material.COMPASS).setDisplayname(navigator_name).build());
    }

}
