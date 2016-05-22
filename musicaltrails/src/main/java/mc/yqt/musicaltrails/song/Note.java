package mc.yqt.musicaltrails.song;

import org.bukkit.Sound;

public class Note {

	private Track track;
	private Sound instrument;
	private float pitch;
	
	/**
	 * Constructor for raw input direct from parser.
	 * @param instrument
	 * @param key
	 */
	public Note(Track track, int instrument, int key) {
		// keep within bounds of notchian clients
		if(key < 33)
			key = 0;
		else if(key > 58)
			key = 25;
		else
			key -= 33;
		
		this.track = track;
		//this.pitch = 0.5f + (key * 0.06f); for some reason, pitch values must be precise to sound correct
		this.pitch = NotePitch.getPitch(key);
		this.instrument = getSound(instrument);
	}
	
	public Sound instrument() {
		return instrument;
	}
	
	public float pitch() {
		return pitch;
	}
	
	public int volume() {
		return track.volume();
	}
	
	private Sound getSound(int instrument) {
		switch(instrument) {
		case 0:
			return Sound.NOTE_PIANO;
		case 1:
			return Sound.NOTE_BASS;
		case 2:
			return Sound.NOTE_BASS_DRUM;
		case 3:
			return Sound.NOTE_SNARE_DRUM;
		case 4:
			return Sound.NOTE_STICKS;
		case 5:
			return Sound.NOTE_PLING;
		default:
			return Sound.NOTE_PIANO;
		}
	}
	
	public enum NotePitch {
		NOTE_0(0, 0.5F),
		NOTE_1(1, 0.53F),
		NOTE_2(2, 0.56F),
		NOTE_3(3, 0.6F),
		NOTE_4(4, 0.63F),
		NOTE_5(5, 0.67F),
		NOTE_6(6, 0.7F),
		NOTE_7(7, 0.76F),
		NOTE_8(8, 0.8F),
		NOTE_9(9, 0.84F),
		NOTE_10(10, 0.9F),
		NOTE_11(11, 0.94F),
		NOTE_12(12, 1.0F),
		NOTE_13(13, 1.06F),
		NOTE_14(14, 1.12F),
		NOTE_15(15, 1.18F),
		NOTE_16(16, 1.26F),
		NOTE_17(17, 1.34F),
		NOTE_18(18, 1.42F),
		NOTE_19(19, 1.5F),
		NOTE_20(20, 1.6F),
		NOTE_21(21, 1.68F),
		NOTE_22(22, 1.78F),
		NOTE_23(23, 1.88F),
		NOTE_24(24, 2.0F);

	    protected int key;
	    protected float pitch;

	    private NotePitch(int key, float pitch) {
	        this.key = key;
	        this.pitch = pitch;
	    }

	    public static float getPitch(int key) {
	    	for(NotePitch notePitch : values()) 
	    		if(notePitch.key == key) 
	    			return notePitch.pitch;

	        return 0.0F;
	    }
	}
}
