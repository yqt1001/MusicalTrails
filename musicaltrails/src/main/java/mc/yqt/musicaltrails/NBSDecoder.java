package mc.yqt.musicaltrails;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import mc.yqt.musicaltrails.song.Song;
import mc.yqt.musicaltrails.song.Track;

/**
 * Big thanks to 
 * https://github.com/xxmicloxx/NoteBlockAPI/blob/master/src/main/java/com/xxmicloxx/NoteBlockAPI/NBSDecoder.java
 * for the basics of decoding these files!
 * 
 */
public class NBSDecoder {

	private File file;
	private DataInputStream in;
	private Song song;
	
	public NBSDecoder(MusicalTrails main, File file) {
		System.out.println("[MusicalTrails] Decoding and parsing " + file.getName());
		this.file = file;
		
		try {
			if(parse()) {
				main.add(song);
				System.out.println("[MusicalTrails] Successfully parsed " + file.getName());
			}
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("[MusicalTrails] Failed to parse " + file.getName());
		}
	}
	
	/**
	 * Attempts to parse the NBS file into a Song object.
	 * @throws IOException
	 */
	public boolean parse() throws IOException {
		in = new DataInputStream(new FileInputStream(file));
		song = new Song();
		
		song.setLength(Short()); // length of song in beats
		song.setTracksNum(Short()); // height of the song in tracks
		
		song.setTitle(String()); // title of song
		song.setAuthor(String()); // author of song
		song.setOriginalAuthor(String()); // original author of song
		song.setDescription(String()); // description of song
		
		song.setTempo(Short() / 100.0f); // tempo of song
		in.readBoolean(); // autosave
		Byte(); // autosave duration
		song.setTimeSignature(Byte()); // time signature x/4
		Int(); // time spent on project
		Int(); // left clicks
		Int(); // right clicks
		Int(); // blocks added
		Int(); // blocks placed
		String(); // file name
		
		// loop through track to add all notes to all tracks
		int tick = -1;
		while(true) {
			// how many jumps until next tick
			int jumpTicks = Short();
			if(jumpTicks == 0)
				break;
			
			tick += jumpTicks;
			
			int track = -1;
			while(true) {
				// how many jumps until next track
				int jumpTracks = Short();
				if(jumpTracks == 0)
					break;
				
				track += jumpTracks;
				
				// add note to the song
				int instrument = Byte();
				int key = Byte();
				song.add(track, tick, instrument, key);
			}
		}
		
		// loop through all tracks to add their names
		for(int i = 0; i < song.tracksNum(); i++) {
			Track track = song.get(i);
			if(track != null) {
				track.setName(String()); // name of track
				track.setVolume(Byte()); // volume of track
			}
		}
		
		return true;
	}
	
	private byte Byte() throws IOException {
		return in.readByte();
	}
	
	private short Short() throws IOException {
		byte byte1 = Byte();
		byte byte2 = Byte();
		return (short) (byte1 + (byte2 << 8));
	}
	
	private int Int() throws IOException {
		short short1 = Short();
		short short2 = Short();
		return short1 + (short2 << 16);
	}
	
	private String String() throws IOException {
		int length = Int();
		StringBuilder string = new StringBuilder(length);
		
		for(; length > 0; length--) {
			char c = (char) Byte();
			if(c == (char) 0x0D)
				c = ' ';
			
			string.append(c);
		}
		
		return string.toString();
	}
}
