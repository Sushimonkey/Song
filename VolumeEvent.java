package Song;

/**
 * Represents a volume change event in a synthesizer track. This class extends
 * from AudioEvent class. VolumeEvent contains methods: -getValue -execute
 * -toString -compareTo as well as a constructor for VolumeEvent.
 * 
 * @author Maeve Wang
 * @version 03/19/2026
 */
public class VolumeEvent extends AudioEvent {
	private int value;

	/**
	 * A constructor for VolumeEvent that contains an integer variable 'value'
	 * representing the new value for the volume, as well as variables time and
	 * trackNumber extended from AudioEvent class
	 * 
	 * @param time        an integer variable extended from AudioEvent that
	 *                    represents the starting time at which the event occurs
	 * @param trackNumber an integer variable that extended from AudioEvent that
	 *                    represents the track number.
	 * @param value       an integer representing the new value for the volume that
	 *                    must be between 0 and 127 inclusive.
	 */
	public VolumeEvent(int time, int trackNumber, int value) {
		super(time, trackNumber);
		this.value = value;
		// if value is out of range 0-127,
		// throws IllegalArgumentException
		if (value < 0 || value > 127) {
			throw new IllegalArgumentException("Invalid volume");
		}
	}

	/**
	 * A getter method that returns value that represents the new value for volume
	 * 
	 * @return an integer that represents the new value for volume
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Execute volumeEvent on the given synthesizer. When this method is called, it
	 * sets the volume of the track specified by trackNumber to the value provided
	 * in this event.
	 * 
	 * Contains instructions for the event to execute at its specified time, such as
	 * starting a sound. Must be defined by subclasses.
	 * 
	 * @param synth a SimpleSynthesizer instance on which to set the volume
	 */
	@Override
	public void execute(SimpleSynthesizer synth) {
		// calls setVolume method from SimpleSynthesizer class
		synth.setVolume(this.getTrackNumber(), value);

	}

	/**
	 * Returns the String that represents the VolumeEvent.
	 * 
	 * @return the volume details such as time, track number, and value of the new
	 *         volume in String form.
	 */
	public String toString() {
		return "Volume[" + this.getTime() + ", " + this.getTrackNumber() + ", " + value + "]";
	}

	/**
	 * This method overrides the compareTo method in AudioEvent class. It compares
	 * this event to the other event in the parameter. The track number is not
	 * considered when comparing events Note: this class has a natural ordering that
	 * is inconsistent with equals.
	 * 
	 * @return negative integer if this time is before the other time, positive
	 *         integer if this time is after the other time (regardless of the event
	 *         type).
	 * 
	 *         If a NoteEvent and a VolumeEvent occur at the same time, the
	 *         VolumeEvent "comes first." The reason is that the synthesizer should
	 *         set the volume before starting to play the note. If 'this' is a
	 *         NoteEvent and 'other' is a VolumeEvent, then this.compareTo(other)
	 *         returns a positive value.
	 * 
	 *         Two VolumeEvents at the same time are ordered by value. The event
	 *         with the smaller value comes first
	 * 
	 *         returns 0 if both events have the same type, time, and value.
	 */
	@Override
	public int compareTo(AudioEvent other) {
		if (this.getTime() != other.getTime())
			return this.getTime() - other.getTime();
		// VolumeEvent comes first
		if (other instanceof NoteEvent) {
			return -1;
		}
		// both are VolumeEvents
		if (this instanceof VolumeEvent && other instanceof VolumeEvent) {
			// type cast into VolumeEvent
			VolumeEvent otherVol = (VolumeEvent) other;
			VolumeEvent thisVol = (VolumeEvent) this;
			// compare value
			return thisVol.getValue() - otherVol.getValue();
		}
		// return 0 if both object are the same type with same value
		return 0;

	}

}
