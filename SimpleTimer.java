package Song;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A timer for scheduling AudioEvents.
 * Do not modify this class.
 * 
 * @author CS 1420 course staff
 * @version 2025-10-19
 */
public class SimpleTimer {
	
	private SimpleSynthesizer synth;
	private ArrayList<EventExecutionTask> scheduledTasks;
	private long startTime;
	private double speedFactor;
	private boolean running;
	private boolean doLoop;
	private long songDuration;
	private Timer timer;
	
	/**
	 * Creates a new timer that will schedule events for the given synthesizer
	 * 
	 * @param synth - synthesizer to use
	 */
	public SimpleTimer(SimpleSynthesizer synth) {
		this.synth = synth;
		scheduledTasks = new ArrayList<EventExecutionTask>();
		startTime = 0;
		running = false;
		speedFactor = 1;
		doLoop = false;
		songDuration = 0;
		timer = new Timer();
	}
	
	/**
	 * Schedules all AudioEvents in a song to begin playing.
	 * 
	 * @param events - array of lists of events to schedule
	 * @param tempo - speed to play at
	 * @param songLength - total duration of the song in ticks
	 */
	public void scheduleEvents(ArrayList<AudioEvent>[] events, int tempo, int songLength) {
		setSpeedFactor(tempo);
		startTime = System.currentTimeMillis();
		running = true;
		scheduledTasks.clear();
		int trackNum = 0;
		for(ArrayList<AudioEvent> track : events) {
			// Since we assume that volume starts at 100 and it may have been set to something else previously
			EventExecutionTask task  = new EventExecutionTask(new VolumeEvent(0, trackNum, 100), true, 0);
			timer.schedule(task, 0);
			scheduledTasks.add(task);
			trackNum++;
			for(AudioEvent event : track) {
				long eventTime = ticksToMillis(event.getTime());
				// Schedule the call to execute
				// Add 100ms delay to give time for scheduling all events
				// The first time playing can still have strange latency issues
				task = new EventExecutionTask(event, true, eventTime);
				timer.schedule(task, eventTime + 100);
				scheduledTasks.add(task);
				// NoteEvents also need a call to complete
				if(event instanceof NoteEvent) {
					long completeTime = eventTime + ticksToMillis(((NoteEvent)event).getDuration()) - 2;
					task = new EventExecutionTask(event, false, completeTime);
					timer.schedule(task, completeTime + 100);
					scheduledTasks.add(task);
				}
			}
		}
		songDuration = ticksToMillis(songLength);
		timer.schedule(new EndOfSongSignal(), songDuration + 100);
	}
	
	/**
	 * Stops playing.
	 * This terminates all scheduled events and tells the synthesizer
	 * to turn all notes off.
	 */
	public void stop() {
		startTime = 0;
		running = false;
		timer.cancel();
		timer = new Timer();
		synth.allNotesOff();
	}
	
	/**
	 * Sets whether the song loops.
	 * 
	 * @param doLoop - true to loop the song, false to only play once
	 */
	public void enableLoop(boolean doLoop) {
		this.doLoop = doLoop;
	}
	
	/**
	 * The elapsed time is the time since the sequence last started. 
	 * If the sequence has not started or has ended, this returns zero.
	 * The time is in ticks.
	 * 
	 * @return elapsed time in ticks
	 */
	public double getElapsedTime() {
		if(running)
			return millisToTicks(System.currentTimeMillis() - startTime);
		return 0.0;
	}
	
	////////////////////////////////////////////////////////////////////////
	// Private methods and classes below
	////////////////////////////////////////////////////////////////////////
	
	/**
	 * Sets the relationship between song beats and milliseconds.
	 * 
	 * @param beatsPerMinute - tempo
	 */
	private void setSpeedFactor(double beatsPerMinute) {
		speedFactor = 60000.0 / beatsPerMinute; // milliseconds per beat
	}
	
	/**
	 * Converts a number of ticks into milliseconds depending on the current tempo.
	 * 
	 * @param ticks amount
	 * @return milliseconds amount
	 */
	private long ticksToMillis(int ticks) {
		return (long)(ticks * speedFactor);
	}
	
	/**
	 * Converts a number of milliseconds into ticks depending on the current tempo.
	 * 
	 * @param milliseconds amount
	 * @return ticks amount
	 */
	private double millisToTicks(long millis) {
		return millis / speedFactor;
	}
	
	/**
	 * Reschedules all events to loop the song.
	 */
	private void scheduleLoop() {
		startTime = System.currentTimeMillis();
		running = true;
		for(EventExecutionTask task : scheduledTasks) {
			timer.schedule(task.copy(), task.getTime());
		}
		timer.schedule(new EndOfSongSignal(), songDuration);
	}
	
	/**
	 * A TimerTask that executes or completes an event.
	 */
	private class EventExecutionTask extends TimerTask{
		private AudioEvent event;
		private boolean isStarting;
		private long time;
		
		public EventExecutionTask(AudioEvent event, boolean isStarting, long time) {
			this.event = event;
			this.isStarting = isStarting;
			this.time = time;
		}
		
		/**
		 * Calls the event's execute or complete method.
		 */
		@Override
		public void run() {
			if(isStarting)
				event.execute(synth);
			else
				((NoteEvent)event).complete(synth);
		}
		
		/**
		 * Gets the starting time. This is needed for looping.
		 * @return
		 */
		public long getTime() {
			return time;
		}
		
		/**
		 * Since TimerTasks can only be run once, a copy needs to be made to reuse this.
		 * @return
		 */
		public EventExecutionTask copy() {
			return new EventExecutionTask(event, isStarting, time);
		}
	}
	
	/**
	 * A TimerTask that runs at the end of the sequence.
	 */
	private class EndOfSongSignal extends TimerTask{
		/**
		 * Restarts the sequence.
		 */
		@Override
		public void run() {
			if(doLoop)
				scheduleLoop();
			else
				running = false;
		}
	}
}