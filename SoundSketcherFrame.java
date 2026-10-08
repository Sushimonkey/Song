package Song;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
/**
 * This class represents the the main GUI for the Sound Sketcher application. It
 * allows users to create, edit, and play a song by interacting with controls
 * such as play/stop, looping, tempo adjustment, and song duration.
 * 
 * The frame contains: -A set of control buttons for playing and looping the
 * song -A tempo slider for adjusting playback speed -A duration spinner for
 * setting the length of the song -A tabbed pane with multiple TrackPanel
 * components for editing individual tracks
 *
 * This class listens for user interactions through ActionListener and
 * ChangeListener to update the song state in real time.
 * 
 * @author Maeve Wang
 * @version April 2, 2026
 */
public class SoundSketcherFrame extends JFrame implements ActionListener, ChangeListener {
	private static final int tempo = 300;
	private static final int duration = 16;
	private Song song;
	private TrackPanel[] tracks;
	private JToggleButton play;
	private JToggleButton loop;
	private JSlider tempoSlider;
	private JSpinner durationSetter;
	private JMenuItem saving;
	private JMenuItem loading;

	/**
	 * Construct a new SoundSketcherFrame and initializes all GUI components for the
	 * Sound Sketcher Program.
	 */
	public SoundSketcherFrame() {
		super("Song 1");

		// initialize song variable
		song = new Song(tempo, duration);

		// set preferred window size and close operation

		// set layout
		setLayout(new BorderLayout());

		// initialize play and loop buttons
		play = new JToggleButton("Play");
		loop = new JToggleButton("Enable Loop");

		// top row contains play and loop buttons
		JPanel topRow = new JPanel();
		topRow.add(play);
		topRow.add(loop);

		// create a JSlider for setting tempo
		tempoSlider = new JSlider(20, 600);
		tempoSlider.setMajorTickSpacing(100);
		tempoSlider.setMinorTickSpacing(20);
		// show ticks
		tempoSlider.setPaintTicks(true);
		// show labels
		tempoSlider.setPaintLabels(true);

		// create JLabel next to the tempo slider
		JLabel tempoLabel = new JLabel("Set Tempo");

		durationSetter = new JSpinner(new SpinnerNumberModel(16, 4, 1024, 4));
		// create JLabel next to the duration spinner
		JLabel durationLabel = new JLabel("Set Duration");

		// bottom row contains tempo Slider and duration Spinner
		JPanel bottomRow = new JPanel();
		bottomRow.add(tempoLabel);
		bottomRow.add(tempoSlider);
		bottomRow.add(durationLabel);
		bottomRow.add(durationSetter);

		// add Control Panel
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(2, 1));

		// add top row and bottom row to control panel
		controlPanel.add(topRow);
		controlPanel.add(bottomRow);

		// add control panel to the frame
		add(controlPanel, BorderLayout.SOUTH);

		// initialize tracks with an array of 10 TrackPanels
		tracks = new TrackPanel[10];

		// create an JTabbedPane for all the TrackPanels
		JTabbedPane editTracks = new JTabbedPane();
		// loop 10 times initializing each TrackPanel
		for (int i = 0; i < 10; i++) {
			tracks[i] = new TrackPanel(
					// set track number from 0 to 9
					i,
					// set song length to default
					song.getSongLength(),
					// set track to default
					song.getTrack(i),
					// set synth to default
					song.getSynthesizer());
			// add each track as a tab
			editTracks.add("Track " + i, tracks[i]);

		}
		// add JTabbedPane to the center of the frame
		add(editTracks, BorderLayout.CENTER);

		// add action listener to play and loop button
		play.addActionListener(this);
		loop.addActionListener(this);
		// add change listener to tempo slider and duration spinner
		tempoSlider.addChangeListener(this);
		durationSetter.addChangeListener(this);

		// creating menu items
		saving = new JMenuItem("Save");
		loading = new JMenuItem("Load");
		// add Action listener
		saving.addActionListener(this);
		loading.addActionListener(this);
		// Setup menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		// add saving and loading to file menu
		fileMenu.add(saving);
		fileMenu.add(loading);
		// add file menu to menuBar
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
		// finalize layout
		pack();
		// set the size of the window
		setSize(800, 800);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	/**
	 * Handles the Change Events for the tempo Slider and duration Spinner.
	 * 
	 * When tempo Slider is adjusted, it calls the method setTempo and sets the
	 * tempo to the new value. when duration Spinner is changed, it calls the the
	 * method setSongLength and sets song length to the new length.
	 * 
	 * @param e the ChangeEvent triggered by changing the value on tempo Slider or
	 *          duration Spinner
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		// Tempo Slider
		int tempo = tempoSlider.getValue();
		song.setTempo(tempo);

		// Length Spinner
		int length = (int) durationSetter.getValue();
		// update Song length
		song.setSongLength(length);
		// update all 10 TrackPanels
		for (int i = 0; i < 10; i++) {
			tracks[i].setSongLength(length);

		}

	}

	/**
	 * Checks whether the new tempo is within the slider's range and adjusts it
	 * accordingly.
	 * 
	 * @param newTempo the new tempo to check whether its within slider range
	 */
	private void setTempoSlider(int newTempo) {
		if (newTempo < tempoSlider.getMinimum())
			tempoSlider.setMinimum(newTempo);
		else if (newTempo > tempoSlider.getMaximum())
			tempoSlider.setMaximum(newTempo);
		tempoSlider.setValue(newTempo);
	}

	/**
	 * Handle the Action Events for the play and loop buttons.
	 * 
	 * When Play button is selected, it calls the method play in Song class and
	 * changes the text on the button to "Stop". When play button is not selected,
	 * it call the method stop in Song class and changes the test on the button back
	 * to "Play".
	 * 
	 * When Loop button is selected, it calls the method enableLoop, set the param
	 * to true, and changes the text on the button to "Disable Loop". When Loop
	 * button is not selected, it calls the method enableLoop, set the param to
	 * false, and changes the text on the button to back "Enable Loop".
	 * 
	 * @param e the ActionEvent triggered by clicking either the play or loop button
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// Play button
		// when selected
		if (play.isSelected()) {
			// calls play method
			song.play();
			// change text to stop
			play.setText("Stop");
		} else {
			// when unselected
			// calls stop method
			song.stop();
			// change text to play
			play.setText("Play");

		}
		// Loop button
		// when selected
		if (loop.isSelected()) {
			// calls enable loop and set to true
			song.enableLoop(true);
			loop.setText("Disable Loop");
		} else {
			// calls enable loop and set to false
			song.enableLoop(false);
			loop.setText("Enable Loop");

		}
		// when saving is clicked
		if (e.getSource() == saving) {
			// create JFileChooser
			JFileChooser chooser = new JFileChooser();
			// use file extension filter
			chooser.setFileFilter(new FileNameExtensionFilter("Song files", "song"));
			// call showSaveDialog method
			int result = chooser.showSaveDialog(null);
			// if the result from file chooser is approved
			if (result == JFileChooser.APPROVE_OPTION) {
				// pass file returned by the file chooser to witeFile method
				File file = chooser.getSelectedFile();
				SongFiles.writeFile(file, song);

			}
		}
		// when loading is clicked
		if (e.getSource() == loading) {
			// create JFileChooser
			JFileChooser chooser = new JFileChooser();
			// use file extension filter
			chooser.setFileFilter(new FileNameExtensionFilter("Song files", "song"));
			// call showOpenDialog
			int result = chooser.showOpenDialog(null);
			// if the result from file chooser is approved
			if (result == JFileChooser.APPROVE_OPTION) {

				// pass the result of its getSelectedFile method to readingFile method
				File file = chooser.getSelectedFile();
				SongFiles.readFile(file, song);

				// update tempo slider
				setTempoSlider(song.getTempo());

				// update each track panel
				for (int i = 0; i < 10; i++) {
					tracks[i].setSongLength(song.getSongLength());
					tracks[i].setInstrument(song.getInstrument(i));
					tracks[i].repaint();

				}

			}

		}
	}
}
