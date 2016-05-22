package mc.yqt.musicaltrails;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SongSelectionGUI {

	private MusicalTrails main;
	private Player player;
	
	public SongSelectionGUI(MusicalTrails main, Player player) {
		this.main = main;
		this.player = player;
	}
	
	public void show() {
		Inventory i = Bukkit.createInventory(player, 27, "Select a Song!");
		
		main.songs().stream().forEach(s -> {
			LinkedList<String> lore = new LinkedList<>();
			lore.add(ChatColor.GRAY + "Author: " + ChatColor.GREEN + s.author());
			lore.add(ChatColor.GRAY + "Midi Author: " + ChatColor.GREEN + s.originalAuthor());
			
			ItemStack is = new ItemStack(Material.JUKEBOX);
			ItemMeta meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + s.title());
			meta.setLore(lore);
			is.setItemMeta(meta);
			
			i.addItem(is);
		});
		
		player.openInventory(i);
	}
}
