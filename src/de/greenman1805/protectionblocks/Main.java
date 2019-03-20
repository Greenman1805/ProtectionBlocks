package de.greenman1805.protectionblocks;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public static Economy econ = null;
	public static WorldGuardPlugin worldGuard;
	public static Main plugin;

	public static List<String> enabledWorlds;
	public static List<Material> MaterialBlacklist = new ArrayList<Material>();
	public static String prefix = "§8[§9Protection§8] §f";
	public static String prefixConsole = "[Protection] "; 
	public static String shoptitle = "§9ProtectionBlock Shop";
	public static int autoRemoveDays;

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		if (!setupEconomy()) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		plugin = this;
		checkFiles();
		getValues();

		PluginManager pluginManager = getServer().getPluginManager();
		worldGuard = (WorldGuardPlugin) pluginManager.getPlugin("WorldGuard");

		BlockListener blockListener = new BlockListener();
		pluginManager.registerEvents(blockListener, this);

		MaterialBlacklist.add(Material.AIR);
		MaterialBlacklist.add(Material.LEGACY_LEAVES);
		MaterialBlacklist.add(Material.LEGACY_LEAVES_2);
		MaterialBlacklist.add(Material.TALL_GRASS);
		MaterialBlacklist.add(Material.CACTUS);
		MaterialBlacklist.add(Material.SUGAR_CANE);
		MaterialBlacklist.add(Material.SUNFLOWER);
		MaterialBlacklist.add(Material.CHORUS_FLOWER);
		MaterialBlacklist.add(Material.LEGACY_LOG_2);
		MaterialBlacklist.add(Material.LEGACY_LOG);
		MaterialBlacklist.add(Material.ROSE_RED);
		MaterialBlacklist.add(Material.LILY_PAD);
		MaterialBlacklist.add(Material.LEGACY_DOUBLE_PLANT);

		registerCommands("p", new ProtectionCommands());

		Bukkit.getScheduler().runTaskLater(this, new Runnable() {

			@Override
			public void run() {
				ProtectionAPI.checkOldRegions();
			}

		}, 20 * 60);
	}

	public void registerCommands(String cmd, CommandExecutor exe) {
		getCommand(cmd).setExecutor(exe);
	}

	private void checkFiles() {
		File file1 = new File("plugins//ProtectionBlocks");
		File file2 = new File("plugins//ProtectionBlocks//config.yml");

		if (!file1.isDirectory()) {
			file1.mkdir();
		}

		if (!file2.exists()) {
			try {
				file2.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file2);
			List<String> list = new ArrayList<String>();
			list.add("Survival");
			cfg.set("enabledWorlds", list);
			cfg.set("protectionAutoRemoveAfterDays", 60);
			cfg.set("Blocks." + Material.IRON_BLOCK + ".size", 21);
			cfg.set("Blocks." + Material.IRON_BLOCK + ".shopInventoryPosition", 1);
			cfg.set("Blocks." + Material.IRON_BLOCK + ".isStartblock", true);
			try {
				cfg.save(file2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void getValues() {
		File file = new File("plugins//ProtectionBlocks//config.yml");
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		enabledWorlds = cfg.getStringList("enabledWorlds");
		autoRemoveDays = cfg.getInt("protectionAutoRemoveAfterDays");
		for (String s : cfg.getConfigurationSection("Blocks").getKeys(false)) {
			new ProtectionBlock(Material.getMaterial(s), cfg.getInt("Blocks." + s + ".size"), cfg.getInt("Blocks." + s + ".price"), cfg.getInt("Blocks." + s + ".shopInventoryPosition"), cfg.getBoolean("Blocks." + s + ".isStartblock"));
		}
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

}
