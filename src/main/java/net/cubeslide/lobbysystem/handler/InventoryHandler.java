package net.cubeslide.lobbysystem.handler;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import net.cubeslide.lobbysystem.LobbySystem;
import net.cubeslide.lobbysystem.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.CopyOnWriteArrayList;

public class InventoryHandler implements Listener {

    public static final String skywarsffa_name = "§8[§5SkywarsFFA§8]";
    public static final String oneblock_name = "§8[§5MC-OneBlock§8]";
    public static CopyOnWriteArrayList<Player> hidden = new CopyOnWriteArrayList<>();


    @EventHandler
    public void on(PlayerInteractEvent event) {
        final Inventory inventory = Bukkit.createInventory(null, 3 * 9, JoinQuitHandler.navigator_name);
        final Inventory stats_inventory = Bukkit.createInventory(null, 3 * 9, JoinQuitHandler.stats_name);

        final Player player = event.getPlayer();
        final ItemStack currentItem = player.getItemInHand();
        if (currentItem == null) return;
        if ((currentItem.getType() == Material.AIR) || (player.getItemInHand().getItemMeta().getDisplayName() == null))
            return;

        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR)) {
            if ((currentItem.getType() == Material.COMPASS) && (currentItem.getItemMeta().getDisplayName().equals(JoinQuitHandler.navigator_name))) {
                for (int i = 0; i < inventory.getSize(); i++) {
                    inventory.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayname("§8[§5§m---§8]").build());
                }

                inventory.setItem(13, new ItemBuilder(Material.DIAMOND_SWORD).setDisplayname(skywarsffa_name).build());
                inventory.setItem(16, new ItemBuilder(Material.GRASS_BLOCK).setDisplayname(oneblock_name).build());

                player.openInventory(inventory);
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1F, 1F);
            }
            if ((currentItem.getType() == Material.BLAZE_ROD) && (currentItem.getItemMeta().getDisplayName().equals(JoinQuitHandler.playerhider_name))) {
                if (hidden.contains(player)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (20), 10));
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1F, 1F);
                    hidden.remove(player);
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        player.showPlayer(all);
                    }
                    player.sendMessage(LobbySystem.getPrefix() + "You can see all players now.");
                } else {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (20), 10));
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1F, 1F);
                    hidden.add(player);
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        player.hidePlayer(all);
                    }
                    player.sendMessage(LobbySystem.getPrefix() + "All players are hidden now.");
                }
            }
            if ((currentItem.getType() == Material.DIAMOND) && (currentItem.getItemMeta().getDisplayName().equals(JoinQuitHandler.stats_name))) {
                for (int i = 0; i < stats_inventory.getSize(); i++) {
                    stats_inventory.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayname("§8[§5§m---§8]").build());
                }

                stats_inventory.setItem(13, new ItemBuilder(Material.EMERALD).setDisplayname("§d§lHIER STATS VLLT aber bin zu dumm für SQL xd").setLore("§3Kills", "§7Zahl", "§7", "§3Deaths", "§7Zahl", "§7", "§3K/D", "§7Zahl").build());

                player.openInventory(stats_inventory);
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1F, 1F);
            }
        } return;
    }


    @EventHandler
    public void on(InventoryClickEvent event) {
        if (event.getInventory() != null) {
            if (event.getWhoClicked() != null) {
                if (event.getWhoClicked() instanceof Player) {
                    Player player = (Player) event.getWhoClicked();
                    if (player.getOpenInventory() != null && player.getOpenInventory().getTitle() != null) {
                        if (player.getOpenInventory().getTitle().equals(JoinQuitHandler.navigator_name)) {
                            event.setCancelled(true);
                            if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta().getDisplayName() != null) {
                                if (event.getCurrentItem().getType() == Material.DIAMOND_SWORD || event.getCurrentItem().getItemMeta().getDisplayName().equals(skywarsffa_name)) {
                                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                                    final IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
                                    playerManager.getPlayerExecutor(playerManager.getOnlinePlayer(player.getUniqueId())).connect("SkywarsFFA-1"); //send a player to the target server if the player is login on a proxy
                                }
                                if (event.getCurrentItem().getType() == Material.GRASS_BLOCK || event.getCurrentItem().getItemMeta().getDisplayName().equals(oneblock_name)) {
                                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                                    final IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
                                    playerManager.getPlayerExecutor(playerManager.getOnlinePlayer(player.getUniqueId())).connect("MCOneBlock-1"); //send a player to the target server if the player is login on a proxy
                                }
                            }
                        }
                        if (player.getOpenInventory().getTitle().equals(JoinQuitHandler.stats_name)) {
                            event.setCancelled(true);
                            if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta().getDisplayName() != null) {

                            }
                        }
                    }
                }
            }
        }
    }
}





