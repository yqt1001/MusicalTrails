package mc.yqt.musicaltrails.playback;

import java.util.Collection;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import mc.yqt.musicaltrails.MusicalTrails;
import mc.yqt.musicaltrails.song.Note;
import mc.yqt.musicaltrails.song.Song;

public class Trail extends SongPlayer {

	private Player player;
	
	public Trail(MusicalTrails main, Song song, Player player) {
		super(main, song);
		this.player = player;
	}
	
	public Player player() {
		return player;
	}
	
	@Override
	protected void play() {
		Collection<Note> notes = song.getNotesFromTick(tick);
		Random r = new Random();
		
		if(notes.size() > 0)
			for(Note note : notes) {
				// play note at players location
				player.getWorld().playSound(player.getLocation(), 
						note.instrument(), 
						note.volume() / 200.0f, // half volume
						note.pitch());
				
				// display a music note particle 
				player.getWorld().playEffect(randomizeSlightly(player.getEyeLocation(), r), Effect.NOTE, 0);
			}
	}
	
	@Override
	protected void end() {
		super.end();
		// replay when song ends
		tick = 0;
		start();
	}

	private Location randomizeSlightly(Location l, Random r) {
		return l.clone().add(r.nextGaussian() / 3d, r.nextGaussian() / 3d, r.nextGaussian() / 3d);
	}
}
