package de.greenman1805.protectionblocks;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.greenman1805.uuids.UUIDs;

public class ProtectionCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		if (sender instanceof Player) {
			p = (Player) sender;
		}

		if (p != null) {

			if (cmd.getName().equalsIgnoreCase("p")) {
				if (args.length == 0) {
					sender.sendMessage("§7- §9/p list §7- §rListet deine Regionen auf.");
					sender.sendMessage("§7- §9/p home <id> §7- §rTeleportiert dich zu deiner Region.");
					sender.sendMessage("§7- §9/p info §7- §rZeigt dir Infos zu deiner Region in der du stehst.");
					sender.sendMessage("§7- §9/p add <spieler> §7- §rDer Spieler kann in deiner Region bauen in der du stehst.");
					sender.sendMessage("§7- §9/p remove <spieler> §7- §rDer Spieler kann nicht mehr in deiner Region bauen in der du stehst.");
					sender.sendMessage("§7- §9/p refund §7- §rLöscht deine Region in der du stehst, du bekommst den Preis erstattet.");

				} else if (args.length == 1) {

					if (args[0].equalsIgnoreCase("help")) {
						sender.sendMessage("§7- §9/p list §7- §rListet deine Regionen auf.");
						sender.sendMessage("§7- §9/p home <id> §7- §rTeleportiert dich zu deiner Region.");
						sender.sendMessage("§7- §9/p info §7- §rZeigt dir Infos zu deiner Region in der du stehst.");
						sender.sendMessage("§7- §9/p add <spieler> §7- §rDer Spieler kann in deiner Region bauen in der du stehst.");
						sender.sendMessage("§7- §9/p remove <spieler> §7- §rDer Spieler kann nicht mehr in deiner Region bauen in der du stehst.");
						sender.sendMessage("§7- §9/p refund §7- §rLöscht deine Region in der du stehst, du bekommst den Preis erstattet.");

					}

					if (args[0].equalsIgnoreCase("admin")) {
						if (p.hasPermission("protectionblocks.admin")) {
							sender.sendMessage("§7- §4/p admin listall §7- §rListet alle Regionen auf.");
							sender.sendMessage("§7- §4/p admin delete §7- §rEntfernt die Region auf der du stehst.");
							sender.sendMessage("§7- §4/p admin list <spieler> §7- §rListet alle Regionen des Spielers auf.");
							sender.sendMessage("§7- §4/p admin tp <spieler> <id> §7- §rTeleportiert dich zu der Region eines Spielers.");
							sender.sendMessage("§7- §4/p admin deleteall <spieler> §7- §rLöscht alle Regionen des Spielers.");

						}
					}

					if (args[0].equalsIgnoreCase("refund")) {
						int price = ProtectionAPI.refundRegion(p);
						if (price > 0) {
							Main.econ.depositPlayer(p, price);
							p.sendMessage(Main.prefix + "§aDeine Region wurde entfernt. Du hast §f" + price + " Shards §aerstattet bekommen.");
						} else {
							p.sendMessage(Main.prefix + "§4Du hast hier keine Region.");
						}

					}

					if (args[0].equalsIgnoreCase("openShop")) {
						ProtectionAPI.openProtectionBlockShop(p);

					}

					if (args[0].equalsIgnoreCase("info")) {
						if (!ProtectionAPI.showRegionInfo(p)) {
							p.sendMessage(Main.prefix + "§4Du hast hier keine Region.");
						}

					}

					if (args[0].equalsIgnoreCase("list")) {
						ProtectionAPI.listPlayerRegions(p);

					}

					if (args[0].equalsIgnoreCase("home")) {
						if (!ProtectionAPI.teleportPlayerToRegion(p, 1)) {
							p.sendMessage(Main.prefix + "§4Du hast noch kein Grundstück.");
						}

					}

				} else if (args.length == 2) {

					if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("home")) {
						try {
							int regionId = Integer.parseInt(args[1]);
							if (!ProtectionAPI.teleportPlayerToRegion(p, regionId)) {
								p.sendMessage(Main.prefix + "§4Diese ID exestiert nicht.");
							}
						} catch (NumberFormatException e) {
							p.sendMessage(Main.prefix + "§4Die ID besteht nur aus einer Zahl.");
						}

					}

					if (args[0].equalsIgnoreCase("add")) {
						String playerToAdd = args[1];
						if (UUIDs.hasEntry(playerToAdd)) {
							UUID uuidToAdd = UUIDs.getUUID(playerToAdd);
							if (ProtectionAPI.addPlayer(p, uuidToAdd)) {
								OfflinePlayer op = Bukkit.getOfflinePlayer(uuidToAdd);
								p.sendMessage(Main.prefix + "§aDer Spieler §f" + op.getName() + "§a wurde zu deiner Region hinzugefügt.");
							} else {
								p.sendMessage(Main.prefix + "§4Du hast hier keine Region.");
							}
						} else {
							p.sendMessage(Main.prefix + "§4Dieser Spieler war noch nie auf dem Survival Server.");
						}

					}

					if (args[0].equalsIgnoreCase("remove")) {
						String playerToAdd = args[1];
						if (UUIDs.hasEntry(playerToAdd)) {
							UUID uuidToAdd = UUIDs.getUUID(playerToAdd);
							if (ProtectionAPI.removePlayer(p, uuidToAdd)) {
								OfflinePlayer op = Bukkit.getOfflinePlayer(uuidToAdd);
								p.sendMessage(Main.prefix + "§aDer Spieler §f" + op.getName() + "§a wurde von deiner Region entfernt.");
							} else {
								p.sendMessage(Main.prefix + "§4Du hast hier keine Region.");
							}
						} else {
							p.sendMessage(Main.prefix + "§4Dieser Spieler war noch nie auf dem Survival Server.");
						}

					}

					if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("delete")) {
						if (p.hasPermission("protectionblocks.admin")) {
							if (ProtectionAPI.deleteRegion(p)) {
								p.sendMessage(Main.prefix + "§aRegion wurde entfernt.");
							} else {
								p.sendMessage(Main.prefix + "§4Region konnte nicht entfernt werden.");
							}
						}

					}

					if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("listall")) {
						if (p.hasPermission("protectionblocks.admin")) {
							ProtectionAPI.listAllRegions(p);
						}
					}

				} else if (args.length == 3) {
					if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("list")) {
						if (p.hasPermission("protectionblocks.admin")) {
							String player = args[2];
							if (UUIDs.hasEntry(player)) {
								UUID uuid = UUIDs.getUUID(player);
								OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
								ProtectionAPI.listPlayerRegions(p, op);
							} else {
								p.sendMessage(Main.prefix + "§4Dieser Spieler war noch nie auf dem Survival Server.");
							}
						}
					}

					if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("deleteall")) {
						if (p.hasPermission("protectionblocks.admin")) {
							String player = args[2];
							if (UUIDs.hasEntry(player)) {
								UUID uuid = UUIDs.getUUID(player);
								OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
								ProtectionAPI.deleteAllPlayerRegions(op);
								p.sendMessage(Main.prefix + "§aRegionen wurden entfernt.");

							} else {
								p.sendMessage(Main.prefix + "§4Dieser Spieler war noch nie auf dem Survival Server.");
							}
						}
					}
				} else if (args.length == 4) {
					if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("tp")) {
						if (p.hasPermission("protectionblocks.admin")) {
							String player = args[2];
							if (UUIDs.hasEntry(player)) {
								UUID uuid = UUIDs.getUUID(player);
								OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
								try {
									int regionId = Integer.parseInt(args[3]);
									if (!ProtectionAPI.teleportToRegion(p, op, regionId)) {
										p.sendMessage(Main.prefix + "§4Diese ID exestiert nicht.");
									}
								} catch (NumberFormatException e) {
									p.sendMessage(Main.prefix + "§4Die ID besteht nur aus einer Zahl.");
								}
							} else {
								p.sendMessage(Main.prefix + "§4Dieser Spieler war noch nie auf dem Survival Server.");
							}
						}

					}
				}
			}

		}
		return false;
	}

}