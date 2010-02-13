/* RealityButtonHandler.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.graph.designer.*;
import bioera.*;
import bioera.rem.*;

/*
	Actions being taken on reality button press:

	1. Single short button press (less then 2 seconds)
		- disactivate alarm being played at that time (if any)
		- adds 5 minutes to current passive time
		- alarm (sound and light) is played at minimum strength
	2. Multiple button press
		- disactivate alarm being played at that time (if any)
		- adds to passive time 5 minutes multiplied by number of hits
		- after the button was released last time (2 seconds without activity), 
		  voice only message (or sound) will indicate the lentgh of current passive time
	3. Long button press (more then 2 seconds)
		- disactivate alarm being played at that time
		- sets passive time to 5 minutes
		- play (sound only) short confirmation
	4. If no activity has been recorded within last 2 seconds, reset buffer with button events
*/


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class RealityButtonHandler {
	public final static int SIGNAL_LEVEL = 10; // Signal changes below this value don't count
	public final static int TIME_BUTTON_CHECK_MILLIS = 100; // Signal fluctuations below this value don't count
	public final static int TIME_BUTTON_RELEASED_MILLIS = 1000; // time delay after button was released

	private REMEngine rem;
		
	private BufferedScalarPipe in;
	private int buffer[];
	private boolean pressed = false;
	
	private int timeButtonCheckTicks;
	private int timeButtonReleasedTicks;

	ArrayList buttonActions = new ArrayList();
	private int timeTicker;

	protected static boolean debug = bioera.Debugger.get("rem.realitybutton");	
/**
 * RealityButton constructor comment.
 */
public RealityButtonHandler(REMEngine d) {
	super();

	rem = d;
	in = d.in0;
	buffer = in.getBuffer();
	int rate = (int) in.getSignalParameters().getSignalRate();
	
	timeButtonCheckTicks = TIME_BUTTON_CHECK_MILLIS * rate / 1000;
	timeButtonReleasedTicks = TIME_BUTTON_RELEASED_MILLIS * rate / 1000;;
}
/**
 * RealityButton constructor comment.
 */
private RealityButtonEvent addActionEvent(int ticks) throws Exception {
	if (debug)
		System.out.println("RealityButtonHandler: action event added");
	RealityButtonEvent ret = new RealityButtonEvent(RealityButtonEvent.ACTION_TAKEN, ticks);
	buttonActions.add(ret);
	return ret;
}
/**
 * RealityButton constructor comment.
 */
private RealityButtonEvent addEvent(int time, boolean pressed) throws Exception {
	RealityButtonEvent ret = new RealityButtonEvent(pressed ? RealityButtonEvent.PRESSED : RealityButtonEvent.RELEASED, time);

	if (buttonActions.size() > 0) {
		RealityButtonEvent e = getEvent(buttonActions.size() - 1);
		if (e.getType() == ret.getType()) {
			// Looks like the last event was the same or action was alreayd taken
			return null;
		}
	}

	if (debug)
		System.out.println("RealityButtonHandler: event added " + pressed);
		
	buttonActions.add(ret);
	return ret;
}
private final int countButtonPressNumber() throws Exception {
	int n = buttonActions.size() - 1, counter = 0;	
	while (n >= 0) {
		RealityButtonEvent e = getEvent(n);
		if (e.isActionTaken())
			break;
		else if (e.isPressed()) {
			counter++;
		}
		n--;
	}
	
	return counter;
}
/*
	1. Single short button press (less then 2 seconds)
	- disactivate alarm being played at that time (if any)
	- adds 5 minutes to current passive time
	- alarm (sound and light) is played at the minimum level
*/
private final void executeAction1() throws Exception {
	//executeAction2(1);

	int remainingSecond = (int)((rem.passiveTime - System.currentTimeMillis()) / 1000);
	rem.player.playUnitSoundSeconds(remainingSecond);
	rem.player.playUnitSoundNumber(rem.averageLevel);
	//while (rem.player.isPlaying()) {
		//Thread.sleep(100);
	//}
	//while (rem.player.isPlaying()) {
		//Thread.sleep(100);
	//}

	Main.app.putEvent("Action1: remain_time=" + remainingSecond + "s, level=" + rem.averageLevel);
}
/*
	2. Multiple button press
	- disactivate alarm being played at that time (if any)
	- adds to passive time 5 minutes multiplied by number of hits
	- after the button was released last time (2 seconds without activity), 
	  voice only message (or sound) will indicate the lentgh of current passive time
*/
private final void executeAction2(int icount) throws Exception {
	if (rem.passiveTime < System.currentTimeMillis())
		rem.passiveTime = System.currentTimeMillis();
	
	rem.passiveTime += icount * REMEngine.PASSIVE_PERIOD;

	// Calculate for how many minutes passive time is set
	int newcount = (int)((rem.passiveTime - System.currentTimeMillis()) / 1000);

//	System.out.println("passive=" + rem.passiveTime + "  diff=" + (rem.passiveTime - System.currentTimeMillis()));

//	System.out.println("Action 2: " + newcount + "  clicks= "+icount+"(" + new Date() + ")");
//	System.out.println("Remained " + newcount + " minutes");
	
	
	rem.player.playUnitSoundSeconds(newcount);
	
	Main.app.putEvent("Action2, count=" + icount + " newtime=" + newcount + "[s]");
}
/*
	3. Long button press (more then 2 seconds)
	- disactivate alarm being played at that time
	- sets passive time to 5 minutes
	- play (sound only) short confirmation
*/
private final void executeAction3() throws Exception {
	rem.passiveTime = System.currentTimeMillis() + REMEngine.PASSIVE_PERIOD;
	
	Main.app.putEvent("Action3");
	
	executeAction1();		
}
/**
 * RealityButton constructor comment.
 */
private RealityButtonEvent getEvent(int index) throws Exception {
	return (RealityButtonEvent) buttonActions.get(index);
}
/**
 * RealityButton constructor comment.
 */
private RealityButtonEvent getLastEvent(int index) throws Exception {
	//System.out.println("LastEvent: " + buttonActions.size());	
	return (RealityButtonEvent) buttonActions.get(buttonActions.size() - 1 - index);
}
/**
 * RealityButton constructor comment.
 */
public final boolean isPressed() throws Exception {
	int n = in.available();

	// Check if the reality button was pressed		
	for (int i = 0; i < n - 2; i++){
		if (buffer[i] < SIGNAL_LEVEL && buffer[i+1] < SIGNAL_LEVEL && buffer[i+2] < SIGNAL_LEVEL) {
			// The button was pressed
			return true;
		}
	}
	
	return false;
}
private final boolean perform() throws Exception {
	// In this moment just perform the reality test
	if (buttonActions.size() == 0) {
		throw new Exception("Something is wrong, no action button has been recorded");
	}

	// Any button action stops current alarm playing
	if (rem.player.isPlaying())
		rem.player.shutdown();

	RealityButtonEvent event = getLastEvent(0);
	//System.out.println("event: " + event.getType());	

	int delay = timeTicker - event.getTicks();	
	if (event.isPressed()) {
		// If button was pressed for long time
		if (delay > timeButtonReleasedTicks) {
			executeAction3();  // See the description for the class
			addActionEvent(timeTicker);
			//return true;
		}

		// Otherwise just exit
		return false;
	}	

	// Button just changed, not enough to determine action so just exit
	if (delay < timeButtonReleasedTicks) {
		// But the action have not yet finished, lets wait
		return false;
	}

	if (event.isActionTaken()) {
		// Ok, action has been already done here, 
		// The case will be closed when button is released
		return false;
	}

	if (event.isReleased()) {
		int n = countButtonPressNumber();
		if (n == 1) {
			executeAction1();
		} else if (n > 1) {
			executeAction2(n);
		}

		return true;
	}

	// We should never get to here, but in case we do, just clean the case
	return true;
}
/**
 * RealityButton constructor comment.
 */
public final void process() throws Exception {
	int n = in.available();

	// Check if the reality button has changed state	
	for (int i = 0; i < n - 2; i++){
		if (pressed) {
			// Search if it was released
			if (buffer[i] > SIGNAL_LEVEL && buffer[i+1] > SIGNAL_LEVEL && buffer[i+2] > SIGNAL_LEVEL) {
				pressed = false;
				addEvent(timeTicker + i, pressed);
			}
		} else {
			// Search when the button was pressed
			if (buffer[i] < SIGNAL_LEVEL && buffer[i+1] < SIGNAL_LEVEL && buffer[i+2] < SIGNAL_LEVEL) {
				pressed = true;
				addEvent(timeTicker + i, pressed);
			}
		}
	}

	timeTicker += n;
	in.purgeAll();

	// Now perform an action on pressed button
	if (perform()) {
		//System.out.println("restting");
		
		// Performance finished, close the case
		reset();
	}	
}
/**
 * RealityButton constructor comment.
 */
public final void reset() throws Exception {
	timeTicker = 0;
	buttonActions.clear();
	rem.realityButtonMode = false;
	pressed = false;

	if (debug)
		System.out.println("RealityButtonHandler: cleaned");
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
