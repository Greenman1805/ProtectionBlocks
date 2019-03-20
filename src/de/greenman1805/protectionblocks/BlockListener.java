package de.greenman1805.protectionblocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (Main.enabledWorlds.contains(p.getWorld().getName())) {
			ItemStack current = p.getInventory().getItemInMainHand();
			if (current != null && current.getType() != Material.AIR && current.getItemMeta().getDisplayName() != null) {
				ProtectionBlock pb = ProtectionBlock.getProtectionBlock(current);
				Location bLocation = e.getBlock().getLocation();

				if (pb != null) {
					int regionCount = ProtectionAPI.getPlayerRegionCount(p);
					int regionLimit = ProtectionAPI.getPlayerRegionLimit(p);
					if (regionCount < regionLimit) {
						if (ProtectionAPI.createRegion(p, bLocation, pb.price, pb.size)) {
							bLocation.getBlock().setType(Material.AIR);
							p.sendMessage(Main.prefix + "§aDeine Region wurde erstellt.");
							p.sendMessage(Main.prefix + "§9/p help §f- §7Für weitere Befehle.");
						} else {
							p.sendMessage(Main.prefix + "§4Deine Region überschneidet sich mit einer anderen Region.");
							e.setCancelled(true);
						}
					} else {
						p.sendMessage(Main.prefix + "§4Du hast bereits " + regionCount + " von " + regionLimit + " Regionen erstellt.");
						e.setCancelled(true);
					}
				} else {
					if (current.getItemMeta().getDisplayName().equalsIgnoreCase("§9[ProtectionBlock]")) {
						p.sendMessage(Main.prefix + "§4Dein ProtectionBlock ist nicht mehr aktuell. Wende dich an einen Admin.");
						e.setCancelled(true);
					}
				}
			}

		} else {
			ItemStack current = p.getInventory().getItemInMainHand();
			if (current != null && current.getItemMeta().getDisplayName() != null) {
				ProtectionBlock pb = ProtectionBlock.getProtectionBlock(current);

				if (pb != null) {
					p.sendMessage(Main.prefix + "§4Du kannst hier keinen ProtectionBlock platzieren.");
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void startblockOnFirstJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		if (!p.hasPlayedBefore()) {
			for (final ProtectionBlock pb : ProtectionBlock.blocks) {
				if (pb.isStartblock) {
					Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {

						@Override
						public void run() {
							p.getInventory().addItem(pb.itemstack);
						}
						
					}, 20 * 3);
				}
			}
		}

	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		ProtectionAPI.checkRegionEnterLeave(e.getPlayer(), e.getTo());
	}

	public boolean isInventoryFull(Player p) {
		return p.getInventory().firstEmpty() == -1;
	}

	@EventHandler
	public void clickedOnItemInShop(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getClickedInventory() != null) {
			if (e.getInventory().getTitle().equalsIgnoreCase(Main.shoptitle)) {
				if (e.isLeftClick()) {
					if (!e.getCurrentItem().getType().name().equalsIgnoreCase("AIR")) {
						if (e.getRawSlot() >= 0 && e.getRawSlot() <= 9) {

							ItemStack item = e.getCurrentItem();
							ProtectionBlock pb = ProtectionBlock.getProtectionBlock(item);
							if (pb != null) {
								if (!isInventoryFull(p)) {
									int account_after = (int) (Main.econ.getBalance(p) - pb.price);
									if (account_after >= 0) {
										Main.econ.withdrawPlayer(p, pb.price);
										p.getInventory().addItem(pb.itemstack);
										p.sendMessage(Main.prefix + "§aDu hast den ProtectionBlock für §f" + pb.price + " Shards §agekauft.");
										p.sendMessage(Main.prefix + "§fBlock platzieren um dein " + pb.size + "x" + pb.size + " Gebiet zu sichern.");
										int regionCount = ProtectionAPI.getPlayerRegionCount(p);
										int regionLimit = ProtectionAPI.getPlayerRegionLimit(p);
										if (regionCount >= regionLimit) {
											p.sendMessage(Main.prefix + "§4Du hast bereits " + regionCount + " von " + regionLimit + " Regionen erstellt.");
										}

									} else {
										p.sendMessage(Main.prefix + "§4Du hast nicht genug Geld!");
									}
								}else {
									p.sendMessage(Main.prefix + "§4Dein Inventar ist voll!");
								}
							}
						}
					}
				}
				e.setCancelled(true);
			}
		}
	}

}
