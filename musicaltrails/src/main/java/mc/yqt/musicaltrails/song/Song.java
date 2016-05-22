package mc.yqt.musicaltrails.song;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

public class Song implements Iterable<Track> {

	private HashMap<Integer, Track> songLayers;
	private short length;
	private short tracksNum;
	private String title;
	private String author;
	private String originalAuthor;
	private String description;
	private float tempo;
	private int timeSignature;
	
	/**
	 * Default constructor.
	 */
	public Song() {
		songLayers = new HashMap<>();
	}
	
	/**
	 * @param tick
	 * @return All notes scheduled to be played on this tick.
	 */
	public Collection<Note> getNotesFromTick(int tick) {
		return songLayers.values().stream()
				.map(t -> t.get(tick))
				.filter(n -> n != null)
				.collect(Collectors.toSet());
	}
	
	/**
	 * @return Map of all tracks.
	 */
	public HashMap<Integer, Track> map() {
		return songLayers;
	}
	
	/**
	 * Adds a specified track.
	 * @param layer
	 * @param track
	 */
	public void add(int layer, Track track) {
		songLayers.put(layer, track);
	}
	
	/**
	 * @param layer
	 * @return The track object for the specified layer.
	 */
	public Track get(int track) {
		return songLayers.get(track);
	}
	
	/**
	 * Adds a note to this song on a specific track.
	 * @param track
	 * @param tick
	 * @param instrument
	 * @param key
	 */
	public void add(int track, int tick, int instrument, int key) {
		Track t = songLayers.get(track);
		
		if(t == null) 
			songLayers.put(track, (t = new Track()));
		
		t.add(tick, instrument, key);
	}
	
	public short length() {
		return length;
	}
	
	public void setLength(short length) {
		this.length = length;
	}
	
	public short tracksNum() {
		return tracksNum;
	}
	
	public void setTracksNum(short n) {
		tracksNum = n;
	}
	
	public String title() {
		return title;
	}
	
	public String noWhiteSpaceTitle() {
		return title.replaceAll(" ", "");
	}
	
	public void setTitle(String name) {
		this.title = name;
	}
	
	public String author() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String originalAuthor() {
		return originalAuthor;
	}
	
	public void setOriginalAuthor(String origAuthor) {
		originalAuthor = origAuthor;
	}
	
	public String description() {
		return description;
	}
	
	public void setDescription(String desc) {
		description = desc;
	}
	
	public float tempo() {
		return tempo;
	}
	
	public void setTempo(float tempo) {
		this.tempo = tempo;
	}
	
	public int timeSignature() {
		return timeSignature;
	}
	
	public void setTimeSignature(int timeSig) {
		timeSignature = timeSig;
	}

	@Override
	public Iterator<Track> iterator() {
		return songLayers.values().iterator();
	}
}
