package mc.yqt.musicaltrails.song;

import java.util.HashMap;
import java.util.Iterator;

public class Track implements Iterable<Note> {

	private HashMap<Integer, Note> trackNotes;
	private String name;
	private int volume;
	
	/**
	 * Default constructor.
	 */
	public Track() {
		trackNotes = new HashMap<>();
	}
	
	/**
	 * @return Map with all notes and their timings for this track.
	 */
	public HashMap<Integer, Note> map() {
		return trackNotes;
	}
	
	/**
	 * @param tick
	 * @return A note from the given tick, if any.
	 */
	public Note get(int tick) {
		return trackNotes.get(tick);
	}
	
	/**
	 * Adds and creates a note to the map.
	 * @param tick
	 * @param instrument
	 * @param key
	 */
	public void add(int tick, int instrument, int key) {
		trackNotes.put(tick, new Note(this, instrument, key));
	}
	
	public String name() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int volume() {
		return volume;
	}
	
	public void setVolume(int volume) {
		this.volume = volume;
	}

	@Override
	public Iterator<Note> iterator() {
		return trackNotes.values().iterator();
	}
}
