package Song;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

@SuppressWarnings("serial")
/**
 * This class represents the GUI components in each track panels. It allows
 * users to mute, edit, copy/paste, clear, and change instrument in every track.
 * The Panel contains: -A Mute Button that mutes the track -A Note Editing
 * Button that switches the mode to note editing -A Volume Editing Button that
 * switches the mode to volume editing -A Copy/paste Button that switches the
 * mode to copy/paste -A Clear Button which clears the track -A Instrument Combo
 * Box that allows the user to change the instrument
 * 
 * This class listens for user interaction through ActionListener to update the
 * track state in real time.
 * 
 * @author Maeve Wang
 * @version April 2, 2026
 */
public class TrackPanel extends JPanel implements ActionListener {
	private int trackNumber;
	private SimpleSynthesizer synth;
	private TrackEditor editor;
	private JComboBox<String> selectInstrument;
	private JToggleButton mute;
	private JToggleButton noteEdit;
	private JToggleButton volEdit;
	private JToggleButton copy;
	private JButton clear;

	/**
	 * Constructs a new TrackPanel and initializes all GUI components for editing a
	 * single track in the Sound Sketcher application.
	 * 
	 * This constructor provides functionality for: -editing notes and volume within
	 * the track -copying sections of the track -muting/unmuting the track
	 * -selecting the instrument -Clearing all events from the track
	 * 
	 * This constructor includes a TrackEditor for editing tracks, multiple controls
	 * for switch editing modes and instruments, as well as buttons for muting and
	 * clearing tracks.
	 * 
	 * @param trackNumber an int representing the track number
	 * @param songLength  an int representing the duration of the song
	 * @param events      an ArrayList of Audioevents
	 * @param synth       an SimpleSynthesizer
	 */
	public TrackPanel(int trackNumber, int songLength, ArrayList<AudioEvent> events, SimpleSynthesizer synth) {
		this.trackNumber = trackNumber;
		this.synth = synth;
		// set Layout
		setLayout(new BorderLayout());
		// initialize editor
		editor = new TrackEditor(trackNumber, songLength, events, synth);
		// initialize instrument select to combo box
		selectInstrument = new JComboBox<String>(new Vector<String>(synth.getInstrumentNames()));
		// create label to combo box
		JLabel selectInstrumentLabel = new JLabel("Select Instrument:");

		// initialize toggle buttons
		mute = new JToggleButton("Mute");
		noteEdit = new JToggleButton("Note Editing");
		volEdit = new JToggleButton("Volume Editing");
		copy = new JToggleButton("Copy");
		// initialize clear button
		clear = new JButton("Clear Track");

		// create new button group so only one of the editing mode can be selected at
		// once
		ButtonGroup buttons = new ButtonGroup();
		buttons.add(noteEdit);
		buttons.add(volEdit);
		buttons.add(copy);

		// create mode panel and add all editing modes
		JPanel modePanel = new JPanel();
		modePanel.add(noteEdit);
		modePanel.add(volEdit);
		modePanel.add(copy);

		// create control panel and add all control buttons
		JPanel controlPanel = new JPanel();
		controlPanel.add(mute);
		controlPanel.add(clear);

		// create instrument panel to add selectInstrument combo box and label
		JPanel instrumentPanel = new JPanel();
		instrumentPanel.add(selectInstrumentLabel);
		instrumentPanel.add(selectInstrument);

		// create side panel and add all sectional panels
		JPanel sidePanel = new JPanel(new GridLayout(3, 1, 5, 5));
		sidePanel.add(modePanel);
		sidePanel.add(instrumentPanel);
		sidePanel.add(controlPanel);

		// create main panel and add editor
		JPanel mainPanel = new JPanel(new BorderLayout());
		// place in the center of borderLayout
		mainPanel.add(editor, BorderLayout.CENTER);

		// place sidePanel on top and mainPanel in the center
		add(sidePanel, BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);

		// add action listener for every buttons and combo box
		mute.addActionListener(this);
		noteEdit.addActionListener(this);
		volEdit.addActionListener(this);
		clear.addActionListener(this);
		copy.addActionListener(this);
		selectInstrument.addActionListener(this);

	}

	/**
	 * This method sets the instrument to the value specified in the parameter.
	 * 
	 * @param instrument an int representing the instrument to set to
	 */
	public void setInstrument(int instrument) {
		// call setInstrument method
		synth.setInstrument(trackNumber, instrument);
		// Edit UI component
		selectInstrument.setSelectedIndex(instrument);

	}

	/**
	 * this method sets the song length to the specified value in the parameter.
	 * 
	 * @param duration an int representing the new song length to set to
	 */
	public void setSongLength(int duration) {
		editor.setSongLength(duration);
	}

	/**
	 * Handles the Action Events for muting, setting editing modes, clearing, and
	 * changing instruments.
	 * 
	 * @param e an ActionEvent triggered by clicking on the button mute, noteEdit,
	 *          volEdit, copy, and clear as well as setting Instrument using the
	 *          combo box.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// Mute button
		if (mute.isSelected()) {
			// if mute button is pressed
			// calls setMute method and set param to true
			synth.setMute(trackNumber, true);
			mute.setText("Unmute");
			// if mute button is not pressed
			// calls setMute method and set param to false
		} else {
			synth.setMute(trackNumber, false);
			mute.setText("Mute");
		}

		// Note mode button
		if (noteEdit.isSelected()) {
			editor.setMode(TrackEditor.Mode.NOTE);
		}
		// Volume mode button
		if (volEdit.isSelected()) {
			editor.setMode(TrackEditor.Mode.VOLUME);

		}
		// Copy/paste mode button
		if (copy.isSelected()) {
			editor.setMode(TrackEditor.Mode.COPY);

		}
		// Clear button
		if (clear.isSelected()) {
			editor.clearTrack();
		}
		// Instrument combo box
		// store the new instrument value
		int value = selectInstrument.getSelectedIndex();
		// set instrument to the new value
		synth.setInstrument(trackNumber, value);

	}

}
