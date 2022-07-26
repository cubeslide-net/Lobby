package net.cubeslide.lobbysystem.handler;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import net.cubeslide.lobbysystem.LobbySystem;
import net.cubeslide.lobbysystem.commands.BuildCMD;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class InventoryHandler implements Listener {

    public static final String skywarsffa_name = "§8[§3Skywars§bFFA§8]";
    public static final String oneblock_name = "§8[§3MC-§bOneBlock§8]";
    public static final String buildffa_name = "§8[§3Build§bFFA§8]";
    public static CopyOnWriteArrayList<UUID> hidden = new CopyOnWriteArrayList<>();
    private static HashMap<UUID, Integer> hasPlayerHiderCooldown = new HashMap<>();
    private static final HashMap<UUID, Long> clickcooldown = new HashMap<>();


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        if (clickcooldown.containsKey(uuid)) {
            clickcooldown.remove(uuid);
        }
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        final Inventory inventory = Bukkit.createInventory(null, 3 * 9, PlayerHandler.navigator_name);

        final Player player = event.getPlayer();
        final ItemStack currentItem = player.getItemInHand();
        if ((currentItem.getType() == Material.AIR)) {
            return;
        } else {
            Objects.requireNonNull(player.getItemInHand().getItemMeta()).getDisplayName();
        }

        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR)) {
            if ((currentItem.getType() == Material.COMPASS) && (Objects.requireNonNull(currentItem.getItemMeta()).getDisplayName().equals(PlayerHandler.navigator_name))) {
                for (int i = 0; i < inventory.getSize(); i++) {
                    inventory.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1).setDisplayname("§8[§5§m---§8]").build());
                }


                final ServiceInfoSnapshot skywarsInfo = CloudNetDriver.getInstance()
                        .getCloudServiceProvider().getCloudServiceByName("SkywarsFFA-1");
                int skywars_onlineCount = skywarsInfo.getProperty(BridgeServiceProperty.ONLINE_COUNT)
                        .get();


                final ServiceInfoSnapshot oneblockInfo = CloudNetDriver.getInstance()
                        .getCloudServiceProvider().getCloudServiceByName("MCOneBlock-1");
                int oneblockInfo_onlineCount = oneblockInfo.getProperty(BridgeServiceProperty.ONLINE_COUNT)
                        .get();

                final ServiceInfoSnapshot buildffaInfp = CloudNetDriver.getInstance()
                        .getCloudServiceProvider().getCloudServiceByName("BuildFFA-1");
                int buildFFAInfo_onlineCount = buildffaInfp.getProperty(BridgeServiceProperty.ONLINE_COUNT)
                        .get();


                final Logger logger = LobbySystem.getInstance().getLogger();

                logger.info(skywars_onlineCount + "");
                logger.info(oneblockInfo_onlineCount + "");

                ItemStack skywarsFFAStack;

                if (skywars_onlineCount == 0) {
                    skywarsFFAStack = new ItemBuilder(Material.DIAMOND_SWORD,1).setDisplayname(skywarsffa_name).setLore(Arrays.asList("§7(Online§8: §6" + skywars_onlineCount + " Player§7)")).build();
                } else {
                    skywarsFFAStack = new ItemBuilder(Material.DIAMOND_SWORD, skywars_onlineCount).setDisplayname(skywarsffa_name).setLore(Arrays.asList("§7(Online§8: §6" + skywars_onlineCount + " Players§7)")).build();
                }
                inventory.setItem(10, skywarsFFAStack);

                ItemStack oneblockStack;
                if (buildFFAInfo_onlineCount == 0) {
                    oneblockStack = new ItemBuilder(Material.GRASS_BLOCK, 1).setDisplayname(oneblock_name).setLore(Arrays.asList("§7(Online§8: §6" + oneblockInfo_onlineCount + " Player§7)")).build();
                } else {
                    oneblockStack = new ItemBuilder(Material.GRASS_BLOCK, oneblockInfo_onlineCount).setDisplayname(oneblock_name).setLore(Arrays.asList("§7(Online§8: §6" + oneblockInfo_onlineCount + " Players§7)")).build();
                }
                inventory.setItem(13, oneblockStack);


                ItemStack buildFFAStack;

                if (buildFFAInfo_onlineCount == 0) {
                    buildFFAStack = new ItemBuilder(Material.SANDSTONE,1 ).setDisplayname(buildffa_name).setLore(Arrays.asList("§7(Online§8: §6" + buildFFAInfo_onlineCount + " Player§7)")).build();
                } else {
                    buildFFAStack = new ItemBuilder(Material.SANDSTONE, oneblockInfo_onlineCount).setDisplayname(buildffa_name).setLore(Arrays.asList("§7(Online§8: §6" + buildFFAInfo_onlineCount + " Players§7)")).build();
                }
                inventory.setItem(16, buildFFAStack);

                player.openInventory(inventory);
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1F, 1F);
            }
            if ((currentItem.getType() == Material.BLAZE_ROD) && (Objects.requireNonNull(currentItem.getItemMeta()).getDisplayName().equals(PlayerHandler.playerhider_name))) {
                if (hasPlayerHiderCooldown.containsKey(player.getUniqueId())) {
                    player.sendMessage(LobbySystem.getPrefix() + "§cPlease slow down. You can use the PlayerHider in §4" + hasPlayerHiderCooldown.get(player.getUniqueId()) + " seconds§c.");
                    return;
                }
                if (hidden.contains(player.getUniqueId())) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (20), 10));
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1F, 1F);
                    hidden.remove(player.getUniqueId());
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        player.showPlayer(all);
                    }
                    player.sendMessage(LobbySystem.getPrefix() + "You can see all players now.");
                    hasPlayerHiderCooldown.put(player.getUniqueId(), 5);
                } else {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (20), 10));
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1F, 1F);
                    hidden.add(player.getUniqueId());
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        player.hidePlayer(all);
                    }
                    player.sendMessage(LobbySystem.getPrefix() + "All players are hidden now.");
                    hasPlayerHiderCooldown.put(player.getUniqueId(), 5);
                }
            }
        }
    }


    @EventHandler
    public void on(InventoryClickEvent event) {

        if (event.getCurrentItem() == null) return;

        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (!BuildCMD.getInBuildMode().contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
            if (player.getOpenInventory().getTitle() != null) {
                if (player.getOpenInventory().getTitle().equals(PlayerHandler.navigator_name)) {
                    event.setCancelled(true);

                    if (event.getCurrentItem().getType() == Material.BLACK_STAINED_GLASS_PANE) return;

                    if (event.getCurrentItem() != null) {
                        if (!clickcooldown.containsKey(player.getUniqueId())) {
                            clickcooldown.put(player.getUniqueId(), System.currentTimeMillis());
                        } else {
                            if (System.currentTimeMillis() - clickcooldown.get(player.getUniqueId()) < 2000) {
                                player.sendMessage(LobbySystem.getPrefix() + "§cPlease wait until you try to click again");
                                player.closeInventory();
                                return;
                            } else {
                                clickcooldown.remove(player.getUniqueId());
                            }
                        }
                        if (event.getCurrentItem().getType() == Material.DIAMOND_SWORD || event.getCurrentItem().getItemMeta().getDisplayName().equals(skywarsffa_name)) {
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                            final IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
                            playerManager.getPlayerExecutor(Objects.requireNonNull(playerManager.getOnlinePlayer(player.getUniqueId()))).connect("SkywarsFFA-1"); //send a player to the target server if the player is login on a proxy
                        }
                        if (event.getCurrentItem().getType() == Material.GRASS_BLOCK || event.getCurrentItem().getItemMeta().getDisplayName().equals(oneblock_name)) {
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                            final IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
                            playerManager.getPlayerExecutor(Objects.requireNonNull(playerManager.getOnlinePlayer(player.getUniqueId()))).connect("MCOneBlock-1"); //send a player to the target server if the player is login on a proxy
                        }
                        if (event.getCurrentItem().getType() == Material.SANDSTONE || event.getCurrentItem().getItemMeta().getDisplayName().equals(buildffa_name)) {
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                            final IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
                            playerManager.getPlayerExecutor(Objects.requireNonNull(playerManager.getOnlinePlayer(player.getUniqueId()))).connect("BuildFFA-1"); //send a player to the target server if the player is login on a proxy
                        }
                    }
                }
            }
        }
    }

    public static HashMap<UUID, Integer> getHasPlayerHiderCooldown() {
        return hasPlayerHiderCooldown;
    }
}






