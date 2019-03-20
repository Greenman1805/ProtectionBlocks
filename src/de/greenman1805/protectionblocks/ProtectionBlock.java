package de.greenman1805.protectionblocks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ProtectionBlock {
	public int size;
	public int price;
	public int shopInventoryPosition;
	public boolean isStartblock;
	public ItemStack itemstack;

	public static List<ProtectionBlock> blocks = new ArrayList<ProtectionBlock>();

	public ProtectionBlock(Material m, int size, int price, int shopInventoryPosition, boolean isStartblock) {
		this.size = size;
		this.price = price;
		this.isStartblock = isStartblock;
		this.shopInventoryPosition = shopInventoryPosition;
		itemstack = new ItemStack(m, 1);
		ArrayList<String> lore_list = new ArrayList<String>();
		lore_list.add("§aGröße: §f"+ size + "x" + size);
		lore_list.add("§aPreis: §f"+ price + " Shards");
		setItemName(itemstack, "§9[ProtectionBlock]", lore_list);
		blocks.add(this);
	}
	
	private static void setItemName(ItemStack item, String name, ArrayList<String> lore_list) {
		ItemMeta meta;
		meta = item.getItemMeta();
		meta.setLore(lore_list);
		meta.setDisplayName(name);
		item.setItemMeta(meta);
	}

	public static ProtectionBlock getProtectionBlock(ItemStack current) {
		for (ProtectionBlock pb : blocks) {
			if (pb.itemstack.isSimilar(current)) {
				return pb;
			}
		}
		return null;
	}

}
