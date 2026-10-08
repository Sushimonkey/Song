package Song;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JPanel;

/**
 * A TrackEditor is the interactive GUI component for drawing a sequence of note
 * events or volume changes in a track.
 * 
 * @author CS 1420 course staff and Maeve Wang
 * @version 4/12/2026
 */
public class TrackEditor extends JPanel implements MouseListener, MouseMotionListener {

	public static enum Mode {
		NOTE, VOLUME, COPY
	};

	private Mode mode;

	private int trackNumber;
	private SimpleSynthesizer synth;
	private ArrayList<AudioEvent> events; // The AudioEvents for this track
	private ArrayList<NoteEvent> notesToCopy; // Stores notes during a copy operation

	private int columns, rows; // The number of columns and rows in the grid
	private boolean drawing; // Set to true during drawing operations
	private int currentRow, currentColumn; // Used by drawing operations
	private int noteDuration; // The duration of a note being drawn

	// For defining an area of the grid to copy
	private int copyFromRow, copyFromColumn; // Top and left of area
	private int copyToRow, copyToColumn; // Bottom and right of area

	// This pitch range matches a piano. You can change these values if desired.
	private static final int lowestPitch = 21;
	private static final int highestPitch = 108;

	/**
	 * Create a new TrackEditor with the default configuration.
	 * 
	 * @param trackNumber assigned to this track in the midi system
	 * @param synthesizer for making sounds
	 * @param sequencer   for sequencing note events
	 */
	public TrackEditor(int trackNumber, int songLength, ArrayList<AudioEvent> events, SimpleSynthesizer synth) {
		columns = songLength;
		rows = highestPitch - lowestPitch + 1;

		this.trackNumber = trackNumber;
		this.synth = synth;
		this.events = events;
		notesToCopy = new ArrayList<NoteEvent>();
		drawing = false;
		mode = Mode.NOTE;

		setBackground(Color.WHITE);

		// TODO Uncomment the next two lines when implementing the MouseListener and
		// MouseMotionListener interfaces.
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/**
	 * Removes all events from the track.
	 */
	public void clearTrack() {
		events.clear();
		repaint();
	}

	/**
	 * Set the song duration in ticks.
	 * 
	 * @param songLength in ticks
	 */
	public void setSongLength(int songLength) {
		columns = songLength;
		if (columns < 1)
			columns = 1;
		repaint();
	}

	/**
	 * Set the editor to the specified mode.
	 * 
	 * @param mode - either Mode.NOTE, Mode.VOLUME, or Mode.COPY
	 */
	public void setMode(Mode mode) {
		this.mode = mode;
		// Set volume back to default in case it was changed in volume mode
		synth.setVolume(trackNumber, 100);
	}

	/**
	 * This method is called by the system when a component needs to be painted.
	 * Which can be at one of three times: --when the component first appears --when
	 * the size of the component changes (including resizing by the user) --when
	 * repaint() is called
	 * 
	 * Partially overrides the paintComponent method of JPanel.
	 * 
	 * @param g -- graphics context onto which we can draw
	 */

	public void paintComponent(Graphics g) {
		// TODO Call superclass JPanel's paintComponent method to fill in panel with
		// background color.
		super.paintComponent(g);
		// create variable for cell height and width
		// making them typed double so when divided, it doesn't round down
		double cellHeight = getHeight() / (double) rows;
		double cellWidth = getWidth() / (double) columns;

		/*
		 * TODO Draw the volume bars These should be a very light color so that other
		 * colors can be easily seen. To make a light color, set red, green, and blue
		 * components to all be between 230 and 255. The volume stays constant between
		 * volume events, so follow the procedure described here.
		 */

		int previousVolume = 100; // the volume begins with value 100 by default
		int previousTime = 0; // the beginning of the song

		// for every audio event in events
		for (AudioEvent event : events) {
			// if the event is volume event
			if (event instanceof VolumeEvent) {
				// setting color for volume bars
				g.setColor(new Color(255, 230, 238));
				// create a preview for the new volumeEvent being created
				int leftEdge = (int) (previousTime * cellWidth);
				int topEdge = (int) (volumeToRow(previousVolume) * cellHeight);
				int rightEdge = (int) (event.getTime() * cellWidth);
				int bottomEdge = getHeight();

				g.fillRect(leftEdge, topEdge, rightEdge - leftEdge, bottomEdge - topEdge);
				// TODO Draw a rectangle showing the volume prior to this event
				// - The left edge is from the column corresponding to previousTime.
				// - The top edge is from the row corresponding to previousVolume (using
				// volumeToRow).
				// - The right edge is the column corresponding to this event's time.
				// (The rectangle does not include the column for this event's time)
				// - The bottom edge is the bottom of the panel.

				// Recall that the fillRect method has width and height parameters. These are
				// the
				// differences between the right and left edges and between the bottom and top
				// edges.

				// Update previousVolume and previousTime with values from this event.
				previousVolume = ((VolumeEvent) event).getValue();
				previousTime = event.getTime();
			}
		}
		// TODO Draw one more rectangle for the last volume event. Use the instructions
		// for the
		// rectangle from above, but this time the right edge is the right edge of the
		// panel.
		g.setColor(new Color(255, 230, 238));
		int leftEdge = (int) (previousTime * cellWidth);
		int topEdge = (int) (volumeToRow(previousVolume) * cellHeight);
		// the new volume event sets the volume for the rest of the columns
		int rightEdge = getWidth();
		int bottomEdge = getHeight();

		g.fillRect(leftEdge, topEdge, rightEdge - leftEdge, bottomEdge - topEdge);

		// TODO Draw the grid using a different, darker color (black is a good choice
		// for this)
		// This involves drawing lines to show each of the rows and columns in the grid.
		// The number of rows and columns are in the instance variables named rows and
		// columns.
		g.setColor(Color.BLACK);

		// a loop that draws the grid line for rows
		for (int i = 0; i <= rows; i++) {
			int y = (int) (i * cellHeight);
			g.drawLine(0, y, getWidth(), y);
		}
		// a loop that draws the grid line for columns
		for (int i = 0; i <= columns; i++) {
			int x = (int) (i * cellWidth);
			g.drawLine(x, 0, x, getHeight());
		}
		// TODO Draw some thicker lines every few rows to make it easier to use.
		// 12 rows represents one octave, so that is a natural choice for spacing.
		// Thicker lines can be made by drawing a rectangle with height 2.
		for (int i = 0; i <= getHeight(); i += 12) {
			int y = (int) (i * cellHeight);
			g.fillRect(0, y, getWidth(), 2);
		}
		// TODO Draw some thicker lines every few columns to make it easier to use.
		// You can choose a spacing you think works well. A good starting point is every
		// four columns.
		// In this case, draw a rectangle with width 2.
		for (int i = 0; i <= getWidth(); i += 4) {
			int x = (int) (i * cellWidth);
			g.fillRect(x, 0, 2, getHeight());
		}

		// Draw preview only if something is currently being drawn by the mouse
		if (drawing) {
			if (mode == Mode.VOLUME) {
				g.setColor(new Color(255, 230, 238));
				int x = (int) (currentColumn * cellWidth);
				int y = (int) (currentRow * cellHeight);
				g.fillRect(x, y, (int) cellWidth, getHeight() - y);
				// TODO Set the color to your volume drawing color and draw a rectangle in
				// the current column from the current row to the bottom of the panel.
				// current column and row are in variables currentRow and currentVolume.

			} else if (mode == Mode.NOTE && noteDuration > 0) {
				g.setColor(Color.PINK);
				int x = (int) (currentColumn * cellWidth);
				int y = (int) (currentRow * cellHeight);
				int width = (int) (noteDuration * cellWidth);
				int height = (int) (cellHeight);
				g.fillRect(x, y, width, height);
				// TODO Set the color to a new color for drawing notes. Choose a color that is
				// easy
				// to distinguish from your volume color. Draw a rectangle beginning in the
				// current row and
				// current column with a height equal to the height of that row (subtract the
				// pixel location
				// of the current row from that of the next row). The noteDuration variable
				// represents
				// the width of this rectangle in number of columns, which needs to be converted
				// to pixels.

			} else if (mode == Mode.COPY) {
				g.setColor(new Color(143, 217, 251, 100));

				// TODO Set the color to a new color for copy selection. To make the background
				// visible
				// behind this rectangle, set the transparency of this color using the fourth
				// argument
				// to the Color constructor, alpha in "new Color(red, green, blue, alpha)".
				// Adjust this
				// transparency value as needed. A good starting point is 100.
				// Draw a rectangle that covers all cells from (copyFromColumn, copyFromRow) to
				// (copyToColumn, copyToRow)

				// Calculate the left x-coordinate of the selection
				int x1 = (Math.min(copyFromColumn, copyToColumn) * getWidth()) / columns;
				// Calculate the right x-coordinate of the selection
				int x2 = ((Math.max(copyFromColumn, copyToColumn) + 1) * getWidth()) / columns;
				// Calculate the top y-coordinate of the selection
				int y1 = (Math.min(copyFromRow, copyToRow) * getHeight()) / rows;
				// Calculate the bottom y-coordinate of the selection
				int y2 = ((Math.max(copyFromRow, copyToRow) + 1) * getHeight()) / rows;

				int width = x2 - x1;
				int height = y2 - y1;

				g.fillRect(x1, y1, width, height);

			}
		}

		// Draw note events
		// TODO Set the color to your note drawing color.
		g.setColor(Color.PINK);
		// this creates new NoteEvents
		// loop through every event in ArrayLst "events"
		for (AudioEvent event : events) {
			// if event is noteEvent
			if (event instanceof NoteEvent) {
				NoteEvent note = (NoteEvent) event;
				// initiate column number to note time
				int col = note.getTime();
				// initiate row number to note pitch
				int row = pitchToRow(note.getPitch());
				// initiate duration to note duration
				int duration = note.getDuration();
				// the top left x-coordinate of the cell
				int x1 = (int) (col * cellWidth);
				// top right x- coordinate of the cell
				int x2 = (int) ((col + duration) * cellWidth);

				// the top left y-coordinate of the cell
				int y1 = (int) (row * cellHeight);
				// the top right y-coordinate of the cell
				int y2 = (int) ((row + 1) * cellHeight);

				int width = x2 - x1;
				int height = y2 - y1;
				g.fillRect(x1, y1, width, height);
				// TODO Draw a rectangle for this note. The row is found by passing this note's
				// pitch
				// to the pitchToRow method. The column is this note's time. The height is the
				// height
				// of one row. The width is from this note's duration, which is a number of
				// columns.

			}
		}

		// Optional: You can draw other indicators if you want, such as middle C at
		// pitch 60.

	} // end of paintComponent

	//////////////////////////////////////////////////////////////////////
	// Private helper methods
	//////////////////////////////////////////////////////////////////////

	/**
	 * Convert a row index in the grid to a pitch number.
	 * 
	 * @param rowNumber - to convert
	 * @return pitch corresponding to that row
	 */
	private int rowToPitch(int rowNumber) {
		return highestPitch - rowNumber;
	}

	/**
	 * Convert a pitch number to a row index in the grid.
	 * 
	 * @param pitch - to convert
	 * @return row index corresponding to that pitch
	 */
	private int pitchToRow(int pitch) {
		return highestPitch - pitch;
	}

	/**
	 * Convert a row index in the grid to a volume value.
	 * 
	 * @param rowNumber - to convert
	 * @return volume value corresponding to that row
	 */
	private int rowToVolume(int rowNumber) {
		return 127 - rowNumber * 127 / rows;
	}

	/**
	 * Convert a volume value to a row index in the grid.
	 * 
	 * @param volume - to convert
	 * @return row index corresponding to that volume
	 */
	private int volumeToRow(int volume) {
		return rows - volume * rows / 127;
	}


	/**
	 * Converts a pixel y value to a row index.
	 * 
	 * @param pixelY - pixel y value
	 * @return index of row containing that pixel
	 */
	private int pixelToRow(int pixelY) {
		return rows * pixelY / getHeight();
	}

	/**
	 * Converts a pixel x value to a column index.
	 * 
	 * @param pixelX - pixel x value
	 * @return index of column containing that pixel
	 */
	private int pixelToCol(int pixelX) {
		return columns * pixelX / getWidth();
	}

	// Required by a serializable class (ignore for now)
	private static final long serialVersionUID = 1L;

	@Override
	public void mouseDragged(MouseEvent e) {
		// temporary variable to keep track of the row and column
		int tempRow = pixelToRow(e.getY());
		int tempCol = pixelToCol(e.getX());
		// when note mode is toggled
		if (mode == Mode.NOTE) {
			// set note duration to the difference in columns
			// between tempCol and current column
			noteDuration = Math.max(1, tempCol - currentColumn);

			// if tempRow and currentRow are different
			if (tempRow != currentRow) {
				// turn of the previous note
				synth.noteOff(trackNumber, rowToPitch(currentRow));
				// and turn on the new note to play sound
				synth.noteOn(trackNumber, tempRow);
				currentRow = tempRow;
			}

		}
		if (mode == Mode.VOLUME) {
			// when changes are made to volumeEvent
			if (tempRow != currentRow) {
				// set new volume
				synth.setVolume(trackNumber, rowToVolume(tempCol));
				// turn note at pitch 60 on and play the volume of that note
				synth.noteOn(trackNumber, 60);
				currentRow = tempRow;
			}
		}
		if (mode == Mode.COPY) {
			// set copyToRow and Column to the temporary row and column
			copyToRow = tempRow;
			copyToColumn = tempCol;

		}
		repaint();

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {

		// when left mouse is clicked
		if (e.getButton() != MouseEvent.BUTTON1) {
			// declare a temporary row and column
			// and initialize it to the current location of the mouse
			int rowClicked = pixelToRow(e.getY());
			int columnClicked = pixelToCol(e.getX());

			if (mode == Mode.NOTE) {
				// loop through every note in events
				for (int i = 0; i < events.size(); i++) {
					// if the event is NoteEvent
					if (events.get(i) instanceof NoteEvent) {

						NoteEvent note = (NoteEvent) events.get(i);
						// and if the note's time and pitch matches the mouse location
						if (note.getTime() == columnClicked && note.getPitch() == rowToPitch(rowClicked)) {
							// remove the Note
							events.remove(i);
							// keep looping because it needs to delete additional copies from the track
							i--;

						}
					}
				}
			}

			if (mode == Mode.VOLUME) {
				// do nothing if mode is volume editing
				return;
			}
			// if mode is on copy
			if (mode == Mode.COPY) {
				// calculate time offset and pitch offset
				int timeOffset = columnClicked - copyFromColumn;
				int pitchOffset = rowClicked - copyFromRow;

				for (NoteEvent note : notesToCopy) {
					// create new note and add time offset to note time
					// and subtract note pitch by pitch offset
					NoteEvent newNote = new NoteEvent(note.getTime() + timeOffset, trackNumber, note.getDuration(),
							note.getPitch() - pitchOffset);
					// add the new note to events
					events.add(newNote);

				}
				// sort events
				Collections.sort(events);
			}
		}
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			// set drawing to true so previous shows
			drawing = true;
			// set current row and column to the location of the mouse when pressed
			currentRow = pixelToRow(e.getY());
			currentColumn = pixelToCol(e.getX());
			// if on note editing mode
			if (mode == Mode.NOTE) {
				// turn note on to play sound
				synth.noteOn(trackNumber, rowToPitch(currentRow));
				// set duration to 1
				noteDuration = 1;
			}
			if (mode == Mode.VOLUME) {
				// set volume to the current row
				synth.setVolume(trackNumber, rowToVolume(currentRow));
				// turn note 60 to hear the volume
				synth.noteOn(trackNumber, 60);
			}
			if (mode == Mode.COPY) {
				// set copyFrom to current row and column
				copyFromRow = currentRow;
				copyFromColumn = currentColumn;

			}
		}
		repaint();

		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {

		if (mode == Mode.NOTE) {
			// turn note off to stop playing sound
			synth.noteOff(trackNumber, rowToPitch(currentRow));
			// if the note duration is longer than 0
			if (noteDuration > 0) {
				// create new note event
				var noteEvent = new NoteEvent(currentColumn, trackNumber, noteDuration, rowToPitch(currentRow));
				// add the new note event to events
				events.add(noteEvent);
				// sort events
				Collections.sort(events);
			}
		}
		if (mode == Mode.VOLUME) {
			// turn note off to stop playing sound
			synth.noteOff(trackNumber, 60);
			// create new volume event

			// add new volume event to events
			for (int i = 0; i < events.size(); i++) {
				AudioEvent ae = events.get(i);
				if (ae instanceof VolumeEvent) {

					VolumeEvent ve = (VolumeEvent) ae;

					if (ve.getTime() == currentColumn)
						events.remove(i);

				}
			}
			VolumeEvent volumeEvent = new VolumeEvent(currentColumn, trackNumber, rowToVolume(currentRow));
			events.add(volumeEvent);
			repaint();
			// sort events
			Collections.sort(events);
		}
		if (mode == Mode.COPY) {
			// clear clip board by initializing notesToCopy to an empty ArrayList
			notesToCopy = new ArrayList<NoteEvent>();
			// find the smaller value in copyFromColumn and copyToColumn
			int startCol = Math.min(copyFromColumn, copyToColumn);
			// find the bigger value in copyFromColumn and copyToColumn
			int endCol = Math.max(copyFromColumn, copyToColumn);
			// find the smaller value in copyFromRow and copyToRow
			// find the bigger value in copyFromRow and copyToRow
			int lowPitch = Math.min(rowToPitch(copyFromRow), rowToPitch(copyToRow));
			int highPitch = Math.max(rowToPitch(copyFromRow), rowToPitch(copyToRow));

			for (AudioEvent event : events) {

				if (event instanceof NoteEvent) {
					// create new note event and initialize it to "event"
					NoteEvent note = (NoteEvent) event;

					// if note time is between start column and end column
					if (startCol <= note.getTime() && note.getTime() <= endCol) {
						// and if pitch is between low and high pitch
						if (lowPitch <= note.getPitch() && note.getPitch() <= highPitch) {
							// add note to notesToCopy
							notesToCopy.add(note);
						}
					}

				}
			}
			// sort notesToCopy
			Collections.sort(notesToCopy);
		}
		// set drawing to false
		drawing = false;
		repaint();

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
