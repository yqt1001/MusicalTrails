package mc.yqt.musicaltrails;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import mc.yqt.musicaltrails.playback.SongPlayer;
import mc.yqt.musicaltrails.playback.Trail;
import mc.yqt.musicaltrails.song.Note;
import mc.yqt.musicaltrails.song.Song;

public class MusicalTrails extends JavaPlugin implements Listener {

	private LinkedList<Song> songs = new LinkedList<>();
	private LinkedList<SongPlayer> activePlayers = new LinkedList<>();
	
	@Override
	public void onEnable() {
		// attempt to load sound files from data folder
		File pluginFolder;
		if(!(pluginFolder = getPlugin(MusicalTrails.class).getDataFolder()).exists()) {
			// if no folder, make it
			pluginFolder.mkdirs();
		} else {
			// if folder, get files
			File[] files = pluginFolder.listFiles();
			
			// try and parse the .nbs files
			if(files != null)
				for(File file : files)
					if(file.isFile()) {
						String path = file.getAbsolutePath();
						int index = path.lastIndexOf(".");
						
						if(index > 0) {
							String extension = path.substring(index);
							
							if(extension.equals(".nbs"))
								new NBSDecoder(this, file);
						}
					}
		}
		
		// register events
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		stopAll();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("trail")) {
			if(!(sender instanceof Player))
				return false;
			
			new SongSelectionGUI(this, (Player) sender).show();
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("stopplayers")) {
			sender.sendMessage(ChatColor.YELLOW + "Stopped all active song players.");
			stopAll();
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("print")) {
			if(args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please give a song name to print to a text file.");
				return true;
			}
			
			StringBuilder string = new StringBuilder();
			for(int i = 0; i < args.length; i++)
				string.append(args[i]);
			
			Song song = songFromTitle(string.toString());
			if(song == null) {
				sender.sendMessage(ChatColor.RED + "No song found with the name " + string.toString());
				return true;
			}
			
			print(song);
			sender.sendMessage(ChatColor.GREEN + "Printed " + string.toString());
			return true;
		}
		
		return false;
	}
	
	/**
	 * @return a collection of all loaded songs.
	 */
	public Collection<Song> songs() {
		return songs;
	}
	
	/**
	 * Adds the specified song to song list.
	 * @param song
	 */
	public void add(Song song) {
		songs.add(song);
	}
	
	/**
	 * @return a collection of playing or paused song players.
	 */
	public Collection<SongPlayer> songPlayers() {
		return activePlayers;
	}
	
	/**
	 * Adds the specified player to the active list.
	 * @param songPlayer
	 */
	public void add(SongPlayer songPlayer) {
		activePlayers.add(songPlayer);
	}
	
	/**
	 * Removes the specified player from the active list.
	 * @param songPlayer
	 */
	public void remove(SongPlayer songPlayer) {
		activePlayers.remove(songPlayer);
	}
	
	/**
	 * @param s title
	 * @return the song specified from the title.
	 */
	public Song songFromTitle(String s) {
		return songs.stream().filter(song -> song.title().equals(s)).findFirst().orElse(null);
	}
	
	/**
	 * @param player
	 * @return if the player has a currently active trail effect.
	 */
	public boolean playerHasTrailActive(Player player) {
		for(SongPlayer active : activePlayers)
			if(active instanceof Trail)
				if(((Trail) active).player().equals(player))
					return true;
		return false;
	}
	
	private void stopAll() {
		Iterator<SongPlayer> it = activePlayers.iterator();
		while(it.hasNext()) {
			SongPlayer player = it.next();
			player.pause();
			it.remove();
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onGUIInteract(InventoryClickEvent e) {
		if(e.getInventory().getName() != null && e.getInventory().getName().equals("Select a Song!")) {
			// GUI interacted
			e.setCancelled(true);
			
			if(e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null || e.getCurrentItem().getItemMeta().getDisplayName() == null)
				return;
			
			if(playerHasTrailActive((Player) e.getWhoClicked())) {
				e.getWhoClicked().sendMessage(ChatColor.RED + "You already have a trail active!");
				return;
			}
			
			String s = e.getCurrentItem().getItemMeta().getDisplayName().substring(4);
			Song song = songFromTitle(s);
			
			if(song == null)
				return;
			
			// startup musical trail effect
			new Trail(this, song, (Player) e.getWhoClicked()).start();
			e.getWhoClicked().sendMessage(ChatColor.YELLOW + "NOW PLAYING: " + ChatColor.GREEN + ChatColor.BOLD + song.author() + " - " + song.title());
			
			// close inventory in one tick
			new BukkitRunnable() {
				@Override
				public void run() {
					e.getWhoClicked().closeInventory();
				}
			}.runTaskLater(this, 1l);
		}
	}
	
	/**
	 * Using this to easily print for other projects. Will need to be changed for use in other things.
	 * @param song
	 */
	private void print(Song song) {
		// run it asynchronously to save the main thread from writing potentially thousands of lines
		new BukkitRunnable() {
			@Override
			public void run() {
				PrintWriter out;
				try {
					out = new PrintWriter(new File(song.noWhiteSpaceTitle() + ".txt"));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return;
				}
				
				int count = 0;
				for(int i = 0; i < song.length(); i++) {
					List<Note> notes = song.getNotesFromTick(i).stream().collect(Collectors.toList());
					if(notes.size() > 0) {
						StringBuilder string = new StringBuilder();
						string.append("\t\todeMap.put(");
						string.append(count);
						string.append(", new SoundEffect[] {");
						
						for(int k = 0; k < notes.size(); k++) {
							Note n = notes.get(k);
							string.append("new SoundEffect(Sound.");
							string.append(n.instrument());
							string.append(", ");
							string.append((float) n.volume() / 100.0f);
							string.append("f, ");
							string.append(n.pitch());
							string.append("f)");
							if((k + 1) < notes.size())
								string.append(", ");
						}
						string.append("});");
						out.println(string.toString());
						count++;
					}
				}
				
				out.close();
			}
		}.runTaskAsynchronously(this);
	}
}
