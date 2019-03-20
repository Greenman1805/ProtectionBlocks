package de.greenman1805.protectionblocks;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class ProtectionAPI {
	public static HashMap<String, String> isInRegion = new HashMap<String, String>();
	
	
	
	public static ApplicableRegionSet getRegions(Location loc) {
        RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(loc));
        return set;
	}

	public static void checkRegionEnterLeave(Player p, Location l) {
		if (!isInRegion.containsKey(p.getName())) {
			for (ProtectedRegion r : getRegions(l)) {
				String regionName = r.getId();
				String[] splitted = regionName.split(",");
				if (splitted.length == 3) {
					String locationString = splitted[0];
					String[] splittedLocation = locationString.split("_");
					if (splittedLocation.length == 3) {
						OfflinePlayer owner = null;
						for (UUID uuid : r.getOwners().getUniqueIds()) {
							owner = Bukkit.getOfflinePlayer(uuid);
							break;
						}
						if (!owner.getName().equalsIgnoreCase(p.getName())) {
							p.sendMessage(Main.prefix + "§aDu hast die Region von §f" + owner.getName() + " §abetreten.");
						} else {
							p.sendMessage(Main.prefix + "§aDu hast deine Region betreten.");
						}
						ProtectionAPI.isInRegion.put(p.getName(), owner.getName());

					}
				}
			}
		} else {
			int size = 0;
			for (ProtectedRegion r : getRegions(l)) {
				String regionName = r.getId();
				String[] splitted = regionName.split(",");
				if (splitted.length == 3) {
					String locationString = splitted[0];
					String[] splittedLocation = locationString.split("_");
					if (splittedLocation.length == 3) {
						size++;
					}
				}
			}
			if (size == 0) {
				String owner = isInRegion.get(p.getName());
				if (!owner.equalsIgnoreCase(p.getName())) {
					p.sendMessage(Main.prefix + "§cDu hast die Region von §f" + owner + " §cverlassen.");
				} else {
					p.sendMessage(Main.prefix + "§cDu hast deine Region verlassen.");
				}
				ProtectionAPI.isInRegion.remove(p.getName());
			}
		}
	}

	public static int refundRegion(Player p) {
		Location check = p.getLocation();
		for (ProtectedRegion r : getRegions(check)) {
			if (r.getOwners().contains(p.getUniqueId())) {
				String regionName = r.getId();
				String[] splitted = regionName.split(",");
				if (splitted.length == 3) {
					String locationString = splitted[0];
					int price = Integer.parseInt(splitted[1]);
					int sizeFull = Integer.parseInt(splitted[2]);
					String[] splittedLocation = locationString.split("_");
					if (splittedLocation.length == 3) {

						Location l = new Location(check.getWorld(), Double.parseDouble(splittedLocation[0]), Double.parseDouble(splittedLocation[1]), Double.parseDouble(splittedLocation[2]));
						int size = sizeFull / 2;
						int x = l.getBlockX(), y = l.getBlockY(), z = l.getBlockZ();

						Location l1 = new Location(l.getWorld(), x - size, y, z - size);
						Location l2 = new Location(l.getWorld(), x + size, y, z - size);
						Location l3 = new Location(l.getWorld(), x + size, y, z + size);
						Location l4 = new Location(l.getWorld(), x - size, y, z + size);

						removeFence(l1, Direction.EAST, size * 2);
						removeFence(l2, Direction.SOUTH, size * 2);
						removeFence(l3, Direction.WEST, size * 2);
						removeFence(l4, Direction.NORTH, size * 2);

						
				        RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
						RegionManager regions = container.get(BukkitAdapter.adapt(l.getWorld()));

						regions.removeRegion(regionName);

						return price;
					}
				}
			}
		}
		return 0;
	}

	public static boolean deleteRegion(Player p) {
		Location check = p.getLocation();
		for (ProtectedRegion r : getRegions(check)) {
			String regionName = r.getId();
			String[] splitted = regionName.split(",");
			if (splitted.length == 3) {
				String locationString = splitted[0];
				int sizeFull = Integer.parseInt(splitted[2]);
				String[] splittedLocation = locationString.split("_");
				if (splittedLocation.length == 3) {
					Location l = new Location(check.getWorld(), Double.parseDouble(splittedLocation[0]), Double.parseDouble(splittedLocation[1]), Double.parseDouble(splittedLocation[2]));
					int size = sizeFull / 2;
					int x = l.getBlockX(), y = l.getBlockY(), z = l.getBlockZ();

					Location l1 = new Location(l.getWorld(), x - size, y, z - size);
					Location l2 = new Location(l.getWorld(), x + size, y, z - size);
					Location l3 = new Location(l.getWorld(), x + size, y, z + size);
					Location l4 = new Location(l.getWorld(), x - size, y, z + size);

					removeFence(l1, Direction.EAST, size * 2);
					removeFence(l2, Direction.SOUTH, size * 2);
					removeFence(l3, Direction.WEST, size * 2);
					removeFence(l4, Direction.NORTH, size * 2);

			        RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
					RegionManager regions = container.get(BukkitAdapter.adapt(l.getWorld()));

					regions.removeRegion(regionName);
					return true;
				}

			}
		}
		return false;
	}

	public static int getPlayerRegionCount(Player p) {
		int count = 0;
		for (String s : Main.enabledWorlds) {
	        RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(Bukkit.getWorld(s)));
			count += regions.getRegionCountOfPlayer(Main.worldGuard.wrapPlayer(p));

		}

		return count;
	}

	public static void listPlayerRegions(Player p) {
		int id = 1;
		p.sendMessage("§9Liste deiner Regionen §f(" + getPlayerRegionCount(p) + " von " + getPlayerRegionLimit(p) + ")§9");
		for (String s : Main.enabledWorlds) {
			World w = Bukkit.getWorld(s);
			
			
			
	        RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(Bukkit.getWorld(s)));
			for (ProtectedRegion r : regions.getRegions().values()) {
				if (r.isOwner(Main.worldGuard.wrapPlayer(p))) {
					String regionName = r.getId();
					String[] splitted = regionName.split(",");
					if (splitted.length == 3) {
						String locationString = splitted[0];
						int price = Integer.parseInt(splitted[1]);
						int sizeFull = Integer.parseInt(splitted[2]);
						String[] splittedLocation = locationString.split("_");
						if (splittedLocation.length == 3) {
							p.sendMessage("§7- §9ID: §f" + id + " §9Location: §f" + "Welt: " + w.getName() + " x: " + splittedLocation[0] + " y: " + splittedLocation[1] + " z: " + splittedLocation[2] + " §9Größe: §f" + sizeFull + " §9Preis: §f" + price);
							id++;
						}
					}
				}
			}
		}

	}

	public static void deleteAllPlayerRegions(OfflinePlayer p) {
		for (String s : Main.enabledWorlds) {
			World w = Bukkit.getWorld(s);
	        RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(Bukkit.getWorld(s)));
			for (ProtectedRegion r : regions.getRegions().values()) {
				if (r.isOwner(Main.worldGuard.wrapOfflinePlayer(p))) {
					String regionName = r.getId();
					String[] splitted = regionName.split(",");
					if (splitted.length == 3) {
						String locationString = splitted[0];
						int sizeFull = Integer.parseInt(splitted[2]);
						String[] splittedLocation = locationString.split("_");
						if (splittedLocation.length == 3) {
							Location l = new Location(w, Double.parseDouble(splittedLocation[0]), Double.parseDouble(splittedLocation[1]), Double.parseDouble(splittedLocation[2]));
							int size = sizeFull / 2;
							int x = l.getBlockX(), y = l.getBlockY(), z = l.getBlockZ();

							Location l1 = new Location(l.getWorld(), x - size, y, z - size);
							Location l2 = new Location(l.getWorld(), x + size, y, z - size);
							Location l3 = new Location(l.getWorld(), x + size, y, z + size);
							Location l4 = new Location(l.getWorld(), x - size, y, z + size);

							removeFence(l1, Direction.EAST, size * 2);
							removeFence(l2, Direction.SOUTH, size * 2);
							removeFence(l3, Direction.WEST, size * 2);
							removeFence(l4, Direction.NORTH, size * 2);

							regions.removeRegion(regionName);
							System.out.println(Main.prefixConsole + "Region entfernt: " + "Besitzer: " + p.getName() + " Location: " + "Welt: " + w.getName() + " x: " + splittedLocation[0] + " y: " + splittedLocation[1] + " z: " + splittedLocation[2] + " Größe: " + sizeFull);
						}
					}
				}
			}
		}

	}

	public static void listPlayerRegions(Player p, OfflinePlayer op) {
		int id = 1;
		p.sendMessage("§9Liste der Regionen von §f" + op.getName() + "§9:");
		for (String s : Main.enabledWorlds) {
			World w = Bukkit.getWorld(s);
	        RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(Bukkit.getWorld(s)));
			for (ProtectedRegion r : regions.getRegions().values()) {
				if (r.isOwner(Main.worldGuard.wrapOfflinePlayer(op))) {
					String regionName = r.getId();
					String[] splitted = regionName.split(",");
					if (splitted.length == 3) {
						String locationString = splitted[0];
						int sizeFull = Integer.parseInt(splitted[2]);
						String[] splittedLocation = locationString.split("_");
						if (splittedLocation.length == 3) {
							p.sendMessage("§7- §9ID: §f" + id + " §9Location: §f" + "Welt: " + w.getName() + " x: " + splittedLocation[0] + " y: " + splittedLocation[1] + " z: " + splittedLocation[2] + " §9Größe: §f" + sizeFull);
							id++;
						}
					}
				}
			}
		}
	}

	public static void checkOldRegions() {
		System.out.println(Main.prefixConsole + "Checking Offline Players...");
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {

			@Override
			public void run() {
				for (final OfflinePlayer op : Bukkit.getOfflinePlayers()) {
					long lastPlayed = op.getLastPlayed();
					long offlineSince = System.currentTimeMillis() - lastPlayed;
					long days = TimeUnit.MILLISECONDS.toDays(offlineSince);
					if (days > Main.autoRemoveDays) {
						new BukkitRunnable() {
							public void run() {
								deleteAllPlayerRegions(op);
							}
						}.runTask(Main.plugin);
					}
				}
			}
		});

	}

	public static void listAllRegions(Player p) {
		p.sendMessage("§9Liste aller Regionen");
		for (String s : Main.enabledWorlds) {
			World w = Bukkit.getWorld(s);
	        RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(Bukkit.getWorld(s)));
			for (ProtectedRegion r : regions.getRegions().values()) {
				String regionName = r.getId();
				String[] splitted = regionName.split(",");
				if (splitted.length == 3) {
					String locationString = splitted[0];
					int price = Integer.parseInt(splitted[1]);
					int sizeFull = Integer.parseInt(splitted[2]);
					String[] splittedLocation = locationString.split("_");

					if (splittedLocation.length == 3) {
						OfflinePlayer op = null;
						for (UUID uuid : r.getOwners().getUniqueIds()) {
							op = Bukkit.getOfflinePlayer(uuid);
							break;
						}
						p.sendMessage("§7- §9Besitzer: §f" + op.getName() + " §9Location: §f" + "Welt: " + w.getName() + " x: " + splittedLocation[0] + " y: " + splittedLocation[1] + " z: " + splittedLocation[2] + " §9Größe: §f" + sizeFull + " §9Preis: §f" + price);
					}
				}
			}
		}

	}

	public static boolean teleportToRegion(Player p, OfflinePlayer op, int id) {
		int current = 1;
		for (String s : Main.enabledWorlds) {
			World w = Bukkit.getWorld(s);
	        RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(Bukkit.getWorld(s)));
			for (ProtectedRegion r : regions.getRegions().values()) {
				if (r.isOwner(Main.worldGuard.wrapOfflinePlayer(op))) {
					String regionName = r.getId();
					String[] splitted = regionName.split(",");
					if (splitted.length == 3) {
						String locationString = splitted[0];
						String[] splittedLocation = locationString.split("_");
						if (splittedLocation.length == 3) {
							if (id == current) {
								Location l = new Location(w, Double.parseDouble(splittedLocation[0]), Double.parseDouble(splittedLocation[1]), Double.parseDouble(splittedLocation[2]));
								l.setY(255);
								while (l.getBlockY() > 0) {
									if (l.getBlock().getType() != Material.AIR) {
										break;
									}
									l.subtract(0, 1, 0);
								}
								l.add(0, 1, 0);
								p.teleport(l);
								return true;
							}
							current++;
						}
					}
				}
			}
		}

		return false;
	}

	public static boolean teleportPlayerToRegion(Player p, int id) {
		int current = 1;
		for (String s : Main.enabledWorlds) {
			World w = Bukkit.getWorld(s);
	        RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(Bukkit.getWorld(s)));
			for (ProtectedRegion r : regions.getRegions().values()) {
				if (r.isOwner(Main.worldGuard.wrapPlayer(p))) {
					String regionName = r.getId();
					String[] splitted = regionName.split(",");
					if (splitted.length == 3) {
						String locationString = splitted[0];
						String[] splittedLocation = locationString.split("_");
						if (splittedLocation.length == 3) {
							if (id == current) {
								Location l = new Location(w, Double.parseDouble(splittedLocation[0]), Double.parseDouble(splittedLocation[1]), Double.parseDouble(splittedLocation[2]));
								l.setY(255);
								while (l.getBlockY() > 0) {
									if (l.getBlock().getType() != Material.AIR) {
										break;
									}
									l.subtract(0, 1, 0);
								}
								l.add(0, 1, 0);
								p.teleport(l);
								return true;
							}
							current++;
						}
					}
				}
			}
		}

		return false;
	}

	public static int getPlayerRegionLimit(Player p) {
		for (int i = 10; i > 0; i--) {
			String permission = "protectionblocks.limit." + i;
			if (p.hasPermission(permission)) {
				return i;
			}
		}
		return 0;
	}

	public static boolean addPlayer(Player p, UUID toAdd) {
		Location check = p.getLocation();
		for (ProtectedRegion r : getRegions(check)) {
			if (r.getOwners().contains(p.getUniqueId()) || p.hasPermission("protectionblocks.admin")) {
				r.getMembers().addPlayer(toAdd);
				return true;
			}
		}
		return false;
	}

	public static boolean removePlayer(Player p, UUID toAdd) {
		Location check = p.getLocation();
		for (ProtectedRegion r : getRegions(check)) {
			if (r.getOwners().contains(p.getUniqueId()) || p.hasPermission("protectionblocks.admin")) {
				r.getMembers().removePlayer(toAdd);
				return true;
			}
		}
		return false;
	}

	public static boolean showRegionInfo(Player p) {
		Location check = p.getLocation();
		for (ProtectedRegion r : getRegions(check)) {
			if (r.getOwners().contains(p.getUniqueId()) || p.hasPermission("protectionblocks.admin")) {
				String regionName = r.getId();
				String[] splitted = regionName.split(",");
				if (splitted.length == 3) {
					String locationString = splitted[0];
					int price = Integer.parseInt(splitted[1]);
					int sizeFull = Integer.parseInt(splitted[2]);
					String[] splittedLocation = locationString.split("_");
					if (splittedLocation.length == 3) {
						OfflinePlayer owner = null;
						for (UUID uuid : r.getOwners().getUniqueIds()) {
							owner = Bukkit.getOfflinePlayer(uuid);
							break;
						}
						if (p.hasPermission("protectionblocks.admin")) {
							p.sendMessage("§9Infos zu der Region von " + owner.getName() + ":");
						} else {
							p.sendMessage("§9Infos zu deiner Region:");
						}
						p.sendMessage("§7Ort: §f" + " x: " + splittedLocation[0] + " y: " + splittedLocation[1] + " z: " + splittedLocation[2]);
						p.sendMessage("§7Größe: §f" + sizeFull + "x" + sizeFull);
						p.sendMessage("§7Preis: §f" + price + " Shards");
						p.sendMessage("§7Erlaubte Spieler:");
						for (UUID uuid : r.getMembers().getUniqueIds()) {
							OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
							p.sendMessage("§f- " + op.getName());
						}

						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean createRegion(Player p, Location l, int price, int sizeFull) {
		int size = sizeFull / 2;
		int x = l.getBlockX(), y = l.getBlockY(), z = l.getBlockZ();
		BlockVector3 pointA = BlockVector3.at(x - size, 255, z - size);
		BlockVector3 pointB = BlockVector3.at(x + size, 0, z + size);

		String regionName = x + "_" + y + "_" + z + "," + price + "," + sizeFull;
		ProtectedRegion region = new ProtectedCuboidRegion(regionName, pointA, pointB);

		
        RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(l.getWorld()));

        
		if (region.getIntersectingRegions(regions.getRegions().values()).size() > 0) {
			return false;
		}

		region.getOwners().addPlayer(p.getUniqueId());

		regions.addRegion(region);

		Location l1 = new Location(l.getWorld(), x - size, y, z - size);
		Location l2 = new Location(l.getWorld(), x + size, y, z - size);
		Location l3 = new Location(l.getWorld(), x + size, y, z + size);
		Location l4 = new Location(l.getWorld(), x - size, y, z + size);
		placeFence(l1, Direction.EAST, size * 2);
		placeFence(l2, Direction.SOUTH, size * 2);
		placeFence(l3, Direction.WEST, size * 2);
		placeFence(l4, Direction.NORTH, size * 2);

		return true;
	}

	private static void placeFence(Location loc, Direction dir, int length) {
		for (int i = 0; i < length; ++i, loc.add(dir.getVector())) {
			loc.setY(255);
			while (loc.getBlockY() > 0) {
				if (!Main.MaterialBlacklist.contains(loc.getBlock().getType())) {
					loc.add(0, 1, 0);
					loc.getBlock().setType(Material.OAK_FENCE);
					break;
				}
				loc.subtract(0, 1, 0);
			}

		}
	}

	public static void openProtectionBlockShop(Player p) {
		Inventory shop = Bukkit.createInventory(null, 9, Main.shoptitle);
		for (int i = 0; i < shop.getSize(); i++) {
			shop.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
		}
		for (ProtectionBlock pb : ProtectionBlock.blocks) {
			shop.setItem(pb.shopInventoryPosition, pb.itemstack);
		}
		p.openInventory(shop);
	}

	private static void removeFence(Location loc, Direction dir, int length) {
		for (int i = 0; i < length; ++i, loc.add(dir.getVector())) {
			loc.setY(255);
			while (loc.getBlockY() > 0) {
				if (loc.getBlock().getType() == Material.OAK_FENCE) {
					loc.getBlock().setType(Material.AIR);
					break;
				}
				loc.subtract(0, 1, 0);
			}

		}
	}

}
