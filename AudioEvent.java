package Song;

/**
 * An abstract class with two private instance variables: a non-negative int to
 * represent the starting time at which the event occurs and an int for track
 * number.
 * 
 * Track number must be a number between 0 and 9 that determines which
 * instrument the event is for and will be used more in later stages of the
 * project.
 * 
 * This class contains methods: -getTime -getTrackerNumber -execute
 * 
 * This class also implements a version of the Comparable interface for
 * comparing objects of the AudioEvent class.
 * 
 * @author Maeve Wang
 * @version 03/19/2026
 */
public abstract class AudioEvent implements Comparable<AudioEvent> {
	// declare protected variables
	private int time;
	private int trackNumber;

	/**
	 * A constructor for AudioEvent. Initializes the state of the event. Throw an
	 * IllegalArgumentException if time is less than 0, trackNumber is less than 0,
	 * or trackNumber is greater than 9.
	 * 
	 * @param time        must be a non-negative value
	 * @param trackNumber must be a value between 0 and 9
	 */
	public AudioEvent(int time, int trackNumber) {
		this.time = time;
		this.trackNumber = trackNumber;
		// if tracker number or time goes out of range,
		// throws IllegalArgumentException
		if (trackNumber < 0 || trackNumber > 9 || time < 0)
			throw new IllegalArgumentException();
	}

	/**
	 * A getter method that returns the time.
	 * 
	 * @return the time as an integer
	 */
	public int getTime() {
		return time;
	}

	/**
	 * A getter method that returns the track number.
	 * 
	 * @return the track number as an integer
	 */
	public int getTrackNumber() {
		return trackNumber;
	}

	/**
	 * 
	 * An abstract method that overrides the compareTo method. Compares this
	 * AudioEvent with the other AudioEvent for ordering.
	 * 
	 * The compareTo method required by the Comparable interface is not defined in
	 * the AudioEvent class because this abstract class does not have enough
	 * information to do the comparison. Instead, it is defined in each of the
	 * subclasses.
	 * 
	 * Note: this class has a natural ordering that is inconsistent with equals.
	 * 
	 * @param other the AudioEvent to compare with
	 * @return an integer indicating relative order; negative, zero, or positive
	 */
	@Override
	public abstract int compareTo(AudioEvent other);

	/**
	 * An abstract method that execute the audio event at its specified time using
	 * the synthesizer provided in the parameter. Contains instructions for the
	 * event to execute at its specified time, such as starting a sound. Must be
	 * defined by subclasses.
	 * 
	 * @param synth the simpleSynthesizer instance used to perform the event
	 */
	public abstract void execute(SimpleSynthesizer synth);

}
