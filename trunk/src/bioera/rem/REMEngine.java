/* REMEngine.java v 1.0.9   11/6/04 7:15 PM
 *
 * BioEra - visual designer for biofeedback (http://www.bioera.net)
 *
 * Copyright (c) 2003-2004 Jarek Foltynski (http://www.foltynski.info)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package bioera.rem;


import java.io.*;
import java.util.*;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.device.*;
import bioera.graph.chart.*;
import bioera.graph.designer.*;
import bioera.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class REMEngine extends SingleElement {
	//public final static int NOISE = 5; // Signal changes below this value don't count
	//public final static int TIME_FRAME = 3 * 1000; // 20 seconds
	//public final static int ACTIVE_PERIOD = 10 * 1000; // 5 minutes
	//public final static int PASSIVE_PERIOD = 5 * 1000; // 5 minutes
	//public final static int COUNT_THRESHOLD = 3;


	
	public final static int NOISE = 20; // % Signal changes below this value don't count

	// Counter is incremented only once within that time frame
	public final static int TIME_FRAME = 20 * 1000; // 20 seconds

	// This is the time when rems are checked
	// It means rem has to happen COUNT_THRESHOLD times withing ACTIVE_PERIOD
	// For example if rem occurs 10 times but withing 1 hour this doesn't count
	public final static int ACTIVE_PERIOD = 2 * 60 * 1000; // 5 minutes

	// This is the time after REM signal was send,
	// During that time, there is no other action done
	public final static int PASSIVE_PERIOD = 5 * 60 * 1000; // 5 minutes
	
	// This number of REMs is required within the great period
	public final static int COUNT_THRESHOLD = 3;

	public String soundPlayerPath = "";
	public String soundsFolder = "";
	
	// This array remembers times when rem occurred
	long times[] = new long[COUNT_THRESHOLD];
	int timeIndex = 0;

	// This time is set after led-alarm was played so that is no repetition.
	long passiveTime = System.currentTimeMillis() + PASSIVE_PERIOD;
	
	BufferedScalarPipe in0;
	private VerticalBarChart chart;
	private int processLength;
	private int buffer[];
	private long lastPlayed = 0;

	ModEEGBackComm cmd;
	private boolean deviceInited = false;	

	Player player;
	int averageLevel;

	boolean realityButtonMode = false;
	RealityButtonHandler realityButtonHandler;

	long lastLevelExceededTime;
	
	protected static boolean debug = bioera.Debugger.get("rem.engine");
	static REMEngine debugREMEngine;
/**
 * SignalDisplay constructor comment.
 */
public REMEngine() {
	super();
	processLength = (int)(TIME_FRAME * getSignalParameters().getSignalRate() / 1000);
	in0 = (BufferedScalarPipe)inputs[0];
	in0.setName("0");
	buffer = in0.getBuffer();
	realityButtonHandler = new RealityButtonHandler(this);
	player = new Player(this);
	debugREMEngine = this;
	if (debug)
		System.out.println("REMEngine created at " + new Date());
}
/**
 * SignalDisplay constructor comment.
 */
public void destroy() throws Exception {
	if (realityButtonHandler != null) {
		realityButtonHandler = null;
	}
}
/**
 * SignalDisplay constructor comment.
 */
private void markTime() throws Exception {
	long now = System.currentTimeMillis();

	// Add the value to table if not yet there
	long lastTime = times[(timeIndex - 1 + times.length) % times.length];
	if (now - lastTime > TIME_FRAME) {
		// Ok, this time will be recorded
		times[timeIndex] = now;
		timeIndex = (timeIndex + 1) % times.length;

		if (debug)
			System.out.println("Time " + now + " was marked on " + timeIndex + "  at " + new Date());
		Main.app.putEvent("REM recorded");
		
		// Check, if the table is already full
		if (now - times[timeIndex] < ACTIVE_PERIOD) {
			// Since we are here, it means that REM alarm signal should be played
			if (debug)
				System.out.println("Flashing REM diodes");
			Main.app.putEvent("REM flashed!!!");
			player.playFlash(6);
			passiveTime = now + PASSIVE_PERIOD;
			
			// Reset time table
			for (int i = 0; i < times.length; i++){
				times[i] = 0;
			}			
		}		
	} else {
		if (debug)
			System.out.print("-");
	}
}
/**
 * Element constructor comment.
 */
public final void playSoundWarning(String name) throws Exception {
	long t = System.currentTimeMillis();

	// Play warning not often then eery 5 seconds
	if (t - lastLevelExceededTime > 5000) {
		lastLevelExceededTime = t;
System.out.println("playin1");
		player.playSound("too_low");
System.out.println("playin2");		
	}
}
/**
 * Element constructor comment.
 */
public final void process() throws Exception {
	if (realityButtonMode) {
		realityButtonHandler.process();
		return;
	}
	
	if (realityButtonHandler.isPressed()) {
		realityButtonMode = true;
		realityButtonHandler.process();
		return;
	}

	int n = in0.available();	

	// Take average from at least 1 second
	if (n < 256) {
		return;
	}
		
	int sum = 0, b[] = in0.getBuffer(), counter = 0;
	for (int i = 0; i < n; i++) {
		if (b[i] > RealityButtonHandler.SIGNAL_LEVEL) {
			sum += b[i];
			counter++;
		}
	}
	
	// Measure average amplitude	
	averageLevel = sum / counter;

	if (averageLevel < 100) {
		// Average signal level exceeds margins
		playSoundWarning("too_low");
		return;
	} else if (averageLevel > 950) {
		playSoundWarning("too_high");
		return;
	}
	
	//System.out.println("n="+n + " (" + processLength + ")");
	if (n < processLength) {
		// Process at least 20 seconds of the data, do nothing if not yet ready
		return;
	}
		
	if (passiveTime > 0) {
		// Still during passive time
		if (System.currentTimeMillis() < passiveTime) {
			if (debug)
				System.out.print("p");
			in0.purgeAll();
			return;
		} else {
			passiveTime = 0;
		}
	}
	
	// Now check, if there is anything above noise
	for (int i = 0; i < n; i++){
		if (b[i] > RealityButtonHandler.SIGNAL_LEVEL && Math.abs(b[i] - averageLevel) > NOISE) {
			// Found an activity
			markTime();
			break;
		}
	}				
	
	in0.purgeAll();

	if (debug)
		System.out.print("o");
	
//	chart.repaint();	
}
/**
 * Element constructor comment.
 */
public final void process_TetsSensivity() throws Exception {
	int n = in0.available();	
		
	if (n < 10) {
		return;
	}

	int sum = 0, b[] = in0.getBuffer(), i;
	for (i = 0; i < n; i++) {
		sum += b[i];
	}
	
	// Measure average amplitude	
	int average = sum / n;

	// Now check, if there is anything above noise
	for (i = 0; i < n; i++){
		if (Math.abs(b[i] - average) > NOISE) {
			// Found an activity
			System.out.println("average is " + average + " value is " + b[i]);
			if (b[i] > 1020)
				player.playSound("12.wav");
			else if (b[i] < 20)
				player.playSound("1.wav");
			else 
				player.playSound("7.wav");
			lastPlayed = System.currentTimeMillis();
			break;
		}
	}


	if (i == n) {
		// none sound played
		if (System.currentTimeMillis() - lastPlayed > 2000) {
			if (b[0] > 1020)
				player.playSound("12.wav");
			else if (b[0] < 20)
				player.playSound("1.wav");
			else 
				player.playSound("7.wav");			
			lastPlayed = System.currentTimeMillis();
		}
	}	
	
	in0.purgeAll();
}
/**
 * Element constructor comment.
 */
private void realityButtonPressed() throws Exception {
	
}
/**
 * SignalDisplay constructor comment.
 */
public void reinit() throws Exception {
	Element elements[] = getElementsConnectedToOutput(0);
	if (elements == null) {
		disactivate("Not connected");	// Without output the rem component is not supposed to work
		reinited = true;
		return;
	} else {
		// Check if all connected elements are initialized
		for (int i = 0; i < elements.length; i++){
			if (!elements[i].isReinited()) {
				// Wait until this element is initialized
				if (debug)
					System.out.println("Element '" + elements[i].getName() + "' not yet inited");
				return;
			}
		}
	}

	verifyDesignState(!TOOLS.isNone(soundPlayerPath));
	verifyDesignState(!TOOLS.isNone(soundsFolder));	
	
	cmd = new ModEEGBackComm(new ScalarPipeOutputStream((ScalarPipeDistributor) outputs[0]));
	if (((PipeDistributor) outputs[0]).isConnected()) {
		cmd.cmdPortDBitSet(7, false);
		cmd.cmdPortDBitBrightOnTime(7, 1);
		cmd.cmdPortDBitBrightPeriod(7, 0);
		cmd.cmdPortDBitSet(6, false);
		cmd.cmdPortDBitBrightOnTime(6, 1);
		cmd.cmdPortDBitBrightPeriod(6, 0);
	} else {
		throw new DesignException("output not connected");
	}
	
	super.reinit();
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
