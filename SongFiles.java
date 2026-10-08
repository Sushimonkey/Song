package Song;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.lang.IllegalStateException;

/**
 * This class represents a utility for reading and writing song data to and from
 * files.
 * 
 * @author Maeve Wang
 * @version 04/21/2026
 */
public class SongFiles {
	
	/**
	 * this method writes the song in the parameter into a file.
	 * 
	 * @param file the file to write the song into
	 * @param song the song that will be written into a file
	 */
	public static void writeFile(File file, Song song) {
		try {
			// create a FileWriter to write text to the given file
			FileWriter writer = new FileWriter(file);
			// write song data (temp and length)
			writer.write(song.getTempo() + "\n");
			writer.write(song.getSongLength() + "\n");

			// loop ten times through every track
			for (int i = 0; i < 10; i++) {
				// get the event from this track
				ArrayList<AudioEvent> track = song.getTrack(i);
				// remove duplicates using filterEvents
				ArrayList<AudioEvent> list = filterEvents(track);
				// sort event by time
				Collections.sort(list, new SongFiles.ChronologicalOrder());

				// track header information
				writer.write("track" + i + "\n"); // track label
				writer.write(i + "/n"); // track number
				writer.write(song.getInstrument(i) + "\n"); // instrument number
				writer.write(list.size()); // number of events in this track

				// for every events in list
				for (AudioEvent event : list) {
					// if its a NoteEvent
					if (event instanceof NoteEvent) {
						// cast event to Note
						NoteEvent note = (NoteEvent) event;
						// write type "note"
						writer.write("note\n");
						writer.write(note.getTime() + "\n"); // time
						writer.write(note.getDuration() + "\n"); // duration
						writer.write(note.getPitch() + "\n"); // pitch
						// if event is a VolumeEvent
					} else if (event instanceof VolumeEvent) {
						// cast event to volumeEvent
						VolumeEvent vol = (VolumeEvent) event;
						// write type "volume"
						writer.write("volume\n");
						writer.write(vol.getTime() + "\n");// time
						writer.write(vol.getValue() + "\n");// value
						writer.write("0\n");// no pitch for volume
					}
				}

			}
			// close the writer to save and release the file
			writer.close();

		} catch (IOException e) {

			// print error message if file writing fails
			e.printStackTrace();
		}

	}

	/**
	 * this method read the file provided in the parameter and creates the song
	 * based on the data from the file.
	 * 
	 * @param file the .song file for scanner to scan through
	 * @param song the song object that will be populated with data from the file
	 */
	public static void readFile(File file, Song song) {
		// using a try block to try and read the song detail in the file
		try {
			// create a new scanner that scans the file
			Scanner scanner = new Scanner(file);

			// read song settings
			int tempo = scanner.nextInt();
			int length = scanner.nextInt();

			// set song tempo and length to scanner input
			song.setTempo(tempo);
			song.setSongLength(length);

			// loop ten times for each track
			for (int i = 0; i < 10; i++) {
				// scans track label
				scanner.next();
				// scans trackNumber, instrument and numEvents
				int trackNumber = scanner.nextInt();
				int instrument = scanner.nextInt();
				int numEvents = scanner.nextInt();

				// set song instrument to scanner input
				song.setInstrument(trackNumber, instrument);

				// loop through each events
				for (int j = 0; j < numEvents; j++) {
					// scans for the type of event
					String type = scanner.next();
					// scans for time
					int time = scanner.nextInt();
					// if its a NoteEvent create a note event according to scanner input
					if (type.equals("note")) {
						int duration = scanner.nextInt();
						int pitch = scanner.nextInt();
						song.addNoteEvent(time, trackNumber, duration, pitch);
					}
					// if its a VolumeEvent create a volume event according to scanner input
					if (type.equals("volume")) {
						int value = scanner.nextInt();
						scanner.nextInt();
						song.addVolumeEvent(time, trackNumber, value);

					}
				}
			}
			// close scanner
			scanner.close();
			// catch file not found exception
		} catch (FileNotFoundException e) {
			// print a message telling user that the compiler cannot find the file
			System.out.println("File not found. Please check file path.");
			// catch input mismatch or illegal state exception if the file is not the
			// correct format
		} catch (InputMismatchException | IllegalStateException e) {
			// Print out a message telling user that the song is not in the right format
			System.out.println("Invalid song file format.");
		}

	}

	/**
	 * This method filters through the ArrayList provided and returns a version of
	 * the arrayList without duplicates.
	 * 
	 * @param events the event to filter through and make sure there is no duplicate
	 * @return a filtered version of parameter ArrayList with no duplicates
	 */
	public static ArrayList<AudioEvent> filterEvents(ArrayList<AudioEvent> events) {
		// create new ArrayList to store result
		ArrayList<AudioEvent> result = new ArrayList<>();
		// create new HashMap
		HashMap<Integer, ArrayList<AudioEvent>> map = new HashMap<>();
		// for every AudioEvent in events
		for (AudioEvent event : events) {
			// get the time of the event
			int time = event.getTime();

			// create an ArrayList that stores list of events at that time from the map
			ArrayList<AudioEvent> listAtTime = map.get(time);
			// if no list exists yet
			if (listAtTime == null) {
				// create an empty ArrayList
				listAtTime = new ArrayList<>();
				// add event to list
				listAtTime.add(event);
				// add list to map
				map.put(time, listAtTime);
				// add event to result
				result.add(event);

				// if list exist, check for duplicates
			} else {

				boolean duplicate = false;
				// for every AudioEvent in listAtTime
				for (AudioEvent exist : listAtTime) {
					// if exist is NoteEvent look for duplicates of NoteEvent
					if (event instanceof NoteEvent && exist instanceof NoteEvent) {
						NoteEvent note1 = (NoteEvent) event;
						NoteEvent note2 = (NoteEvent) exist;

						if (note1.getPitch() == note2.getPitch() && note1.getTime() == note2.getTime())
							duplicate = true;
						break;
					}
					// if exist is VolumeEvent loop for duplicate of VolumeEvent
					if (event instanceof VolumeEvent && exist instanceof VolumeEvent) {
						// time is already equal
						duplicate = true;
						break;
					}
				}
				// there is no duplicates
				if (duplicate == false) {
					// add event to list
					listAtTime.add(event);
					// add event to result
					result.add(event);
				}

			}

		}

		return result;
	}

	/**
	 * A Comparator that orders AudioEvent objects in chronological order based on
	 * their execution time.
	 * 
	 * @author Maeve Wang
	 * @version 04/21/2026
	 */
	public static class ChronologicalOrder implements Comparator<AudioEvent> {
		/**
		 * Compares two AudioEvent objects by their execution time.
		 * 
		 * @param o1 the first AudioEvent to compare
		 * @param o2 the second AudioEvent to compare
		 * @return a negative integer, zero, or a positive integer depending on whether
		 *         o1 occurs before, at the same time, or after o2
		 */
		@Override
		public int compare(AudioEvent o1, AudioEvent o2) {

			return o1.compareTo(o2);
		}

	}
}
