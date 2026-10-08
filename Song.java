package Song;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A Song is made of 10 tracks that contain AudioEvents.
 * 
 * @author CS 1420 course staff and Maeve Wang
 * @version 03/26/2026
 */
public class Song {
	// Each track is represented by an ArrayList of AudioEvents.
	// There are always 10 tracks, so a basic array is used to
	// store the 10 ArrayLists.
	// TODO: Add an instance variable named "tracks" that is a basic array of
	// ArrayLists that specify the generic type AudioEvent.

	@SuppressWarnings("unchecked")
	// this create an array that stores 10 array lists
	private ArrayList<AudioEvent>[] tracks = new ArrayList[10];

	private int tempo;
	private int songLength;
	private SimpleSynthesizer synth;
	private SimpleTimer timer;

	/**
	 * Construct a new Song with the given tempo and length. It starts with 10 empty
	 * tracks. This also initializes the synthesizer and timer.
	 * 
	 * @param tempo      - speed of the song in beats(ticks) per minute
	 * @param songLength - length of the song in ticks
	 */
	@SuppressWarnings("unchecked")
	public Song(int tempo, int songLength) {
		this.tempo = tempo;
		this.songLength = songLength;
		synth = new SimpleSynthesizer();
		timer = new SimpleTimer(synth);
		// Creating tracks requires casting because we can't allocate a new array
		// of a generic class with a specified generic type.
		tracks = (ArrayList<AudioEvent>[]) new ArrayList[10];
		for (int i = 0; i < tracks.length; i++)
			tracks[i] = new ArrayList<AudioEvent>();
	}

	/**
	 * Add one NoteEvent to the specified track. After adding, the track is sorted
	 * to ensure the events are always in sorted order.
	 * 
	 * @param time        - for the NoteEvent
	 * @param trackNumber - for the NoteEvent
	 * @param duration    - for the NoteEvent
	 * @param pitch       - for the NoteEvent
	 */
	public void addNoteEvent(int time, int trackNumber, int duration, int pitch) {
		// TODO - Fill in to create a NoteEvent with the given data and add
		// it to the ArrayList of the track with the given number.
		// Then use Collections.sort to sort the modified ArrayList.
		// See
		// https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Collections.html#sort(java.util.List)
		NoteEvent noteEvent = new NoteEvent(time, trackNumber, duration, pitch);
		// add the note event to the specified track
		tracks[trackNumber].add(noteEvent);
		// sort by using the collection.sort so make sure the noteEvents added are in
		// the right order
		Collections.sort(tracks[trackNumber]);
	}

	/**
	 * Add one VolumeEvent to the specified track. After adding, the track is sorted
	 * to ensure the events are always in sorted order.
	 * 
	 * @param time        - for the VolumeEvent
	 * @param trackNumber - for the VolumeEvent
	 * @param value       - for the VolumeEvent
	 */
	public void addVolumeEvent(int time, int trackNumber, int value) {
		// TODO - Fill in to create a VolumeEvent with the given data and add
		// it to the ArrayList of the track with the given number.
		// Then use Collections.sort to sort the modified ArrayList.
		// See
		// https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Collections.html#sort(java.util.List)

		VolumeEvent volEvent = new VolumeEvent(time, trackNumber, value);
		// add the volume event to the specified track
		tracks[trackNumber].add(volEvent);
		// sort by using the collection.sort so make sure the volumeEvents added are in
		// the right order
		Collections.sort(tracks[trackNumber]);
	}

	/**
	 * Remove all events from the specified track.
	 * 
	 * @param trackNumber - of track to clear
	 * @throws IllegalArgumentException if trackNumber is not between 0 and 9
	 */
	public void clearTrack(int trackNumber) {
		// TODO - Fill in to clear the ArrayList for the given track,
		// throwing an IllegalArgumentException if needed
		if (trackNumber < 0 || trackNumber >= tracks.length)
			throw new IllegalArgumentException();
		// calls the clear method to clear the track
		tracks[trackNumber].clear();
	}

	/**
	 * Remove all events from all tracks.
	 */
	public void clearAll() {
		// loop through all the tracks to clear them one by one
		for (int i = 0; i < tracks.length; i++) {
			tracks[i].clear();
		}
	}

	/**
	 * Get the list of events for a given track.
	 * 
	 * @param trackNumber - of track to get
	 * @return list of events in that track
	 * @throws IllegalArgumentException if trackNumber is not between 0 and 9
	 */
	public ArrayList<AudioEvent> getTrack(int trackNumber) {
		// TODO - Fill in to return the ArrayList for the given track,
		// throwing an IllegalArgumentException if needed
		if (trackNumber < 0 || trackNumber >= tracks.length)
			throw new IllegalArgumentException();
		return tracks[trackNumber];
	}

	//////////////////////////////////////////////////////////////////////
	// The methods below are complete. Do not modify them.
	// They will be used in the next stages of the project.
	//////////////////////////////////////////////////////////////////////

	/**
	 * Get the playing speed of the song.
	 * 
	 * @return tempo
	 */
	public int getTempo() {
		return tempo;
	}

	/**
	 * Set the playing speed of the song.
	 * 
	 * @param newTempo - to set
	 */
	public void setTempo(int newTempo) {
		tempo = newTempo;
	}

	/**
	 * Get the total time that the song will play in ticks.
	 * 
	 * @return length of the song in ticks
	 */
	public int getSongLength() {
		return songLength;
	}

	/**
	 * Set the total time that the song will play in ticks.
	 * 
	 * @param newLength - of the song in ticks
	 */
	public void setSongLength(int newLength) {
		songLength = newLength;
	}

	/**
	 * Get the synthesizer that this song uses to make sound.
	 * 
	 * @return synthesizer
	 */
	public SimpleSynthesizer getSynthesizer() {
		return synth;
	}

	/**
	 * Get the timer that this song uses to schedule events.
	 * 
	 * @return timer
	 */
	public SimpleTimer getTimer() {
		return timer;
	}

	/**
	 * Schedule all events in all tracks to begin playback. This first stops
	 * playback to avoid overlapping any currently playing song.
	 */
	public void play() {
		stop();
		timer.scheduleEvents(tracks, tempo, songLength);
	}

	/**
	 * Tell the timer to stop all scheduled events. This effectively stops playback.
	 */
	public void stop() {
		timer.stop();
	}

	/**
	 * Sets whether the song loops.
	 * 
	 * @param doLoop - true to loop the song, false to only play once
	 */
	public void enableLoop(boolean doLoop) {
		timer.enableLoop(doLoop);
	}

	/**
	 * This method sets the instrument for the Synthesizer for the specified
	 * trackNumber
	 * 
	 * @param trackNumber the track to set the instrument
	 * @param instrument  the instrument to set to
	 */
	public void setInstrument(int trackNumber, int instrument) {
		synth.setInstrument(trackNumber, instrument);

	}

	/**
	 * This method return an integer that represents the instrument from the track.
	 * 
	 * @param trackNumber the number that represents the track to get instrument
	 *                    from
	 * @return integer that represents the instrument
	 */
	public int getInstrument(int trackNumber) {
		return synth.getInstrument(trackNumber);
	}
}
