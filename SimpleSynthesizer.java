package Song;

import java.util.ArrayList;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

/**
 * A simplified midi synthesizer.
 * This contains an instance of Java's midi Synthesizer, but provides 
 * a much simpler interface for it.
 * 
 * The available channels and instruments depend on your machine. The
 * midi system is supposed to support 16 channels, but there may be some
 * variation. If a specified channel is not supported by the system, the
 * methods will have no effect, but should not throw exceptions. This is
 * to avoid crashes on irregular hardware.
 * If your machine does not provide midi support, this class can still
 * be used but will not produce any sound. In that case, It will appear 
 * to have one DEFAULT instrument.
 * 
 * @author CS 1420 course staff
 * @version 2025-10-19
 */
public class SimpleSynthesizer {
	private Synthesizer synth;
	private MidiChannel[] channels;
	private Instrument[] instruments;
	private ArrayList<Integer> validChannels;
	
	/**
	 * Creates a new SimpleSynthesizer that uses the default soundbank.
	 * Every channel is initialized with the first available instrument.
	 * If there is an error setting up the midi system, this synthesizer
	 * will still be valid and can be used, but it won't produce any audio.
	 */
	public SimpleSynthesizer() {
		try {
			synth = MidiSystem.getSynthesizer();
			synth.open();
			channels = synth.getChannels();
			instruments = new Instrument[channels.length];
			synth.loadAllInstruments(synth.getDefaultSoundbank());
			if(synth.getLoadedInstruments().length == 0) {
				System.out.println("There are no midi instruments provided by the midi system. Can't make sound.");
				synth = null;
				channels = null;
				instruments = null;
			}
			else{
				Instrument defaultInstrument = synth.getLoadedInstruments()[0];
				for(int i = 0; i < channels.length; i++) {
					instruments[i] = defaultInstrument;
					channels[i].programChange(defaultInstrument.getPatch().getProgram());
				}
			}
		} catch (MidiUnavailableException e) {
			System.out.println("Couldn't start a midi synthesizer. You may not have support on this machine.");
			synth = null;
			channels = null;
			instruments = null;
		}
		
		validChannels = new ArrayList<Integer>();
		if(channels != null) {
			for(int i = 0; i < channels.length; i++)
				if(channels[i] != null)
					validChannels.add(i);
		}
		else // Provide channels 0 to 15 if the midi system is not available
			for(int i = 0; i < 16; i++)
				validChannels.add(i);
	}
	
	/**
	 * Begins playing the specified pitch on the track.
	 * The note will not end until noteOff is called for the same pitch and track.
	 * The pitch value is clamped between 0 and 127
	 *
	 * This has no effect if the midi system is not available or if the
	 * track number is not valid.
	 *
	 * @param trackNumber - number between 0 and 9
	 * @param pitch - number between 0 and 127 of pitch to turn on
	 */
	public void noteOn(int trackNumber, int pitch) {
		if(channels != null && validChannels.contains(trackNumber))
			channels[trackNumber].noteOn(pitch, 100);
		// velocity is always 100 here
		// Including a velocity parameter is possible, but it is omitted for simplicity.
	}
	
	/**
	 * Stops playing the specified pitch on the track.
	 * If the pitch was not already playing, nothing happens.
	 * The pitch value is clamped between 0 and 127
	 *
	 * This has no effect if the midi system is not available or if the
	 * track number is not valid.
	 *
	 * @param trackNumber - number between 0 and 9
	 * @param pitch - number between 0 and 127 of pitch to turn off
	 */
	public void noteOff(int trackNumber, int pitch) {
		if(channels != null && validChannels.contains(trackNumber))
			channels[trackNumber].noteOff(pitch);
	}
	
	/**
	 * Sets the volume of the specified track.
	 * The value is clamped between 0 and 127.
	 * This has no effect if the midi system is not available or it the
	 * track number index is not valid.
	 *
	 * @param trackNumber - number between 0 and 9 of track to set the volume of
	 * @param volume - value that will be clamped between 0 and 127
	 */
	public void setVolume(int trackNumber, int volume) {
		if(channels == null || !validChannels.contains(trackNumber))
			return;
		if(volume > 127)
			volume = 127;
		else if(volume < 0)
			volume = 0;
		// Note: 7 is the control number for volume (midi 1.0 spec)
		channels[trackNumber].controlChange(7, volume);
	}
	
	/**
	 * Returns the current volume value for a given track.
	 * If the midi system is not available or the track number is invalid, 
	 * this returns 0.
	 * 
	 * @param trackNumber - to get the volume of
	 * @return volume value on that channel
	 */
	public int getVolume(int trackNumber) {
		if(channels == null || !validChannels.contains(trackNumber))
			return 0;
		// Note: 7 is the control number for volume (midi 1.0 spec)
		return channels[trackNumber].getController(7);
	}
	
	/**
	 * Gets a list of available instrument names from the midi system.
	 * If the midi system is not available, this returns a list with
	 * one element: "DEFAULT"
	 * 
	 * @return list of instrument names
	 */
	public ArrayList<String> getInstrumentNames() {
		ArrayList<String> names = new ArrayList<String>();
		if(synth != null) {
			for(Instrument instr : synth.getLoadedInstruments())
				names.add(instr.getName());
		} else {
			// provide one default instrument if the midi system is not available
			names.add("DEFAULT");
		}
		return names;
	}
	
	/**
	 * Sets the current instrument on a given track.
	 * The index will match an index in the list of instrument names
	 * provided by getInstrumentNames. 
	 * This has no effect if the midi system is not available or if the
	 * track number or instrument index is not valid.
	 * 
	 * @param trackNumber - which track to set the instrument of
	 * @param instrumentIndex - index of instrument in the list
	 */
	public void setInstrument(int trackNumber, int instrumentIndex) {
		if(channels == null || !validChannels.contains(trackNumber))
			return;
		Instrument[] instr = synth.getLoadedInstruments();
		if(instrumentIndex > instr.length || instrumentIndex < 0)
			return;
		instruments[trackNumber] = instr[instrumentIndex];
		channels[trackNumber].programChange(instruments[trackNumber].getPatch().getProgram());
	}
	
	/**
	 * Gets the current instrument on a given track.
	 * The index will match an index in the list of instrument names
	 * provided by getInstrumentNames.
	 * If the midi system is not available or if the track number or instrument 
	 * index is not valid, returns 0.
	 * 
	 * @param trackNumber - index of channel
	 * @return instrument number currently used by this channel
	 */
	public int getInstrument(int trackNumber) {
		if(channels == null || !validChannels.contains(trackNumber))
			return 0;
		int currentProgram = channels[trackNumber].getProgram();
		int index = 0;
		for(Instrument instr : synth.getLoadedInstruments()) {
			if(instr.getPatch().getProgram() == currentProgram)
				return index;
			index++;
		}
		return 0;
	}
	
	/**
	 * Mutes or unmutes a given track.
	 * This has no effect if the midi system is not available or if the
	 * track number is not valid.
	 * 
	 * @param trackNumber - to mute
	 * @param mute - true to mute, false to unmute
	 */
	public void setMute(int trackNumber, boolean mute) {
		if(channels == null || !validChannels.contains(trackNumber))
			return;
		channels[trackNumber].setMute(mute);
	}
	
	/**
	 * Sets a pitch bend on a given track. This may not have an effect
	 * on all instruments or hardware implementations, and the range of
	 * pitch changes can vary, though it is typically a maximum of two 
	 * semitones up or down from center. (e.g. amount 8191 raises the pitch
	 * two semitones, amount -8192 lowers two semitones)
	 * 
	 * The amount ranges from -8192 to 8191, with 0 being the center.
	 * The value is clamped to this range.
	 * 
	 * This has no effect if the midi system is not available or if the
	 * track num  is not valid.
	 * 
	 * @param trackNumber - to bend the pitch of
	 * @param amount - to bend the pitch between -8192 and 8191 (0 is center)
	 */
	public void setPitchBend(int trackNumber, int amount) {
		if(channels == null || !validChannels.contains(trackNumber))
			return;
		if(amount > 8191)
			amount = 8191;
		else if(amount < -8192)
			amount = -8192;
		channels[trackNumber].setPitchBend(amount + 8192);
	}
	
	/**
	 * Turns off all notes that are playing on all channels.
	 */
	public void allNotesOff() {
		if(channels == null)
			return;
		for(MidiChannel ch : channels)
			ch.allNotesOff();
	}
}
