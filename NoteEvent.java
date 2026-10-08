package Song;

/**
 * Represents a note event in a synthesizer track. This class extends from
 * AudioEvent class.
 * 
 * NoteEvent contains these methods: -getDuration -getPitch -complete -compareTo
 * -execute -toString as well as a constructor for NoteEvent.
 * 
 * @author Maeve Wang
 * @version 03/19/2026
 */
public class NoteEvent extends AudioEvent {
	private int duration;
	private int pitch;

	/**
	 * A constructor for NoteEvent that contains integer variables 'duration' and
	 * 'pitch' that represents the duration of the note as well as the pitch. it
	 * also contains variables that extends from AudioEvent class such as 'time' and
	 * 'trackNumber'.
	 * 
	 * @param time        an integer variable extended from AudioEvent that
	 *                    represents the starting time at which the event occurs
	 * @param trackNumber an integer variable that extended from AudioEvent that
	 *                    represents the track number
	 * @param duration    an integer that represents the duration of the note,
	 *                    cannot be negative
	 * @param pitch       an integer that represents the pitch of the note ranging
	 *                    from 0 to 127 (inclusive)
	 */
	public NoteEvent(int time, int trackNumber, int duration, int pitch) {
		super(time, trackNumber);
		this.duration = duration;
		this.pitch = pitch;
		// if duration is negative or pitch is out of range,
		// throw IllegalArgumentException
		if (duration < 0 || pitch < 0 || pitch > 127) {
			throw new IllegalArgumentException("Invalid duration or pitch");
		}

	}

	/**
	 * Getter method that returns the duration of the note as an integer.
	 * 
	 * @return an integer that represents the duration of the note
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Getter method that returns the pitch of he note as an integer.
	 * 
	 * @return an integer that represents the pitch of the note ranging from 0 to
	 *         127 (inclusive)
	 */
	public int getPitch() {
		return pitch;
	}

	/**
	 * Indicates the note event has finished playing. Call the SimpleSynthesizer's
	 * noteOff method, passing the trackNumber and pitch.
	 * 
	 * @param synth a SimpleSynthesizer instance on which to set the pitch
	 */
	public void complete(SimpleSynthesizer synth) {
		// calls noteOff method from SimpleSynthesizer class
		synth.noteOff(this.getTrackNumber(), pitch);
	}

	/**
	 * This method overrides the compareTo method in AudioEvent class. It compares
	 * this event to the other event in the parameter. The track number is not
	 * considered when comparing events Note: this class has a natural ordering that
	 * is inconsistent with equals.
	 * 
	 * @return negative integer if 'this' time is before the 'other' time, positive
	 *         integer if 'this' time is after the 'other' time (regardless of the
	 *         event type).
	 * 
	 *         If a NoteEvent and a VolumeEvent occur at the same time, the
	 *         VolumeEvent "comes first." The reason is that the synthesizer should
	 *         set the volume before starting to play the note. If 'this' is a
	 *         NoteEvent and 'other' is a VolumeEvent, then this.compareTo(other)
	 *         returns a positive value.
	 * 
	 *         Two NoteEvents at the same time are ordered so that the event with
	 *         shorter duration comes first. If the time, and the duration are the
	 *         same, the event with smaller pitch value comes first.
	 * 
	 *         returns 0 if both events have the same type, time, duration and
	 *         pitch.
	 */
	@Override
	public int compareTo(AudioEvent other) {
		// compare by time
		if (this.getTime() != other.getTime())
			return this.getTime() - other.getTime();
		// NoteEvent comes after VolumeEvent
		if (other instanceof VolumeEvent) {
			return 1;
		}
		// both are NoteEvents
		if (this instanceof NoteEvent && other instanceof NoteEvent) {
			// type cast into NoteEvent
			NoteEvent otherNote = (NoteEvent) other;
			NoteEvent thisNote = (NoteEvent) this;
			// if thisNote and otherNote has different duration,
			// the note with shorter duration comes first
			if (thisNote.getDuration() != otherNote.getDuration()) {
				return thisNote.getDuration() - otherNote.getDuration();
			}
			// if thisNote and otherNote has different pitch,
			// the note with smaller pitch comes first
			if (thisNote.getPitch() != otherNote.getPitch()) {
				return thisNote.getPitch() - otherNote.getPitch();
			}
			// otherwise, return 0
			return 0;
		}
		// if both object are the same type, and has same time, duration, and pitch
		// return 0
		return 0;
	}

	/**
	 * Execute NoteEvent on the given synthesizer. When this method is called, it
	 * sets the pitch to the note on the track specified by trackNumber to the
	 * integer provided by this event.
	 * 
	 * Contains instructions for the event to execute at its specified time, such as
	 * starting a sound. Must be defined by subclasses.
	 * 
	 * @param synth a SimpleSynthesizer instance on which to set the pitch
	 */
	@Override
	public void execute(SimpleSynthesizer synth) {
		// calls noteOn method from SimpleSynthesizer class
		synth.noteOn(this.getTrackNumber(), pitch);

	}

	/**
	 * Returns the String that represents the NoteEvent.
	 * 
	 * @return the Note details such as time, track number, duration and pitch of
	 *         the note in String form.
	 */
	public String toString() {
		return "Note[" + this.getTime() + ", " + this.getTrackNumber() + ", " + duration + ", " + pitch + "]";
	}

}
