package mc.yqt.musicaltrails.playback;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import mc.yqt.musicaltrails.MusicalTrails;
import mc.yqt.musicaltrails.song.Note;
import mc.yqt.musicaltrails.song.Song;

public class SongPlayer {

	protected MusicalTrails main;
	protected Song song;
	protected long delay; // in ms
	protected int tick;
	
	private Thread thread;
	
	public SongPlayer(MusicalTrails main, Song song) {
		this.main = main;
		this.song = song;
		this.delay = (long) ((20 / song.tempo()) * 50);
		this.tick = 0;
	}
	
	/**
	 * Starts the song player thread.
	 */
	public void start() {
		main.add(this);
		thread = new Thread(() -> {
			while(true) {
				synchronized(this) {
					// stop thread if song is done playing
					if(tick > song.length()) {
						end();
						break;
					}
					
					// play the song
					play();
					tick++;
					
					// wait for next tick
					try {
						this.wait(delay);
					} catch(InterruptedException e) {
						// if interrupted, stop the thread
						break;
					}
				}
			}
			
		});
		
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}
	
	/**
	 * Stops the thread but does not remove the player from actively played.
	 */
	public void pause() {
		if(thread != null)
			thread.interrupt();
	}
	
	/**
	 * Stops the thread and removes the player from the active list.
	 */
	public void stop() {
		if(thread != null)
			thread.interrupt();
		main.remove(this);
	}
	
	/**
	 * Called when the song ends
	 */
	protected void end() {
		main.remove(this);
	}
	
	/**
	 * Called once every song tick.
	 * Default implementation: Like a radio, all online players get the song at max volume in their current location.
	 */
	protected void play() {
		Collection<Note> notes = song.getNotesFromTick(tick);
		
		if(notes.size() > 0)
			for(Player player : Bukkit.getOnlinePlayers()) 
				for(Note note : notes) 
					player.playSound(player.getLocation(), 
							note.instrument(), 
							note.volume() / 100.0f, 
							note.pitch());
	}
}
