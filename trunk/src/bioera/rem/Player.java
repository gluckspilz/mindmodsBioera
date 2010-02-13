/* Player.java v 1.0.9   11/6/04 7:15 PM
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

import java.util.*;
import bioera.sound.*;
import bioera.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class Player implements Runnable  {
	public final static String SOUND_FILE_EXTENSION = ".wav";
	public final static int DEFAULT_LOUNDESS = 4;
	
	private REMEngine remEngine;
	
	private Thread playingThread;	
	private boolean nowPlaying = false;
	private boolean shutdown = false;

	public final static String SHORT_SOUND = "beep";
	public final static String END_SOUND = "end";	
	
	private String filename;
	private int loudness = -1;
	private Vector filenameChain = new Vector();
	private int repeat;	

	Process process;
	
	protected static boolean debug = bioera.Debugger.get("rem.player");
/**
 * Player constructor comment.
 */
public Player(REMEngine engine) {
	super();
	remEngine = engine;
}
/**
 * Player constructor comment.
 */
public boolean isPlaying() {
	return nowPlaying;
}
/**
 * Player constructor comment.
 */
public static void main(String args[]) throws Exception {
	try {
		int n = 1003;
		if (args.length > 0)
			n = Integer.parseInt(args[0]);
		bioera.processing.SignalParameters s = new bioera.processing.SignalParameters(null);
		REMEngine remEngine = new REMEngine();
		remEngine.soundPlayerPath = "c:\\projects\\eeg\\sounds\\wav.exe"; 
		remEngine.soundsFolder = "c:\\projects\\eeg\\dzwieki\\";
	//	new Player(remEngine).playShortSound();
		Player p = new Player(remEngine);
		p.playUnitSoundNumber(n);
		//UnitConcatenation conc = new UnitConcatenation();
		//String st[] = conc.concatenateTime(1030);
		//for (int i = 0; i < st.length; i++){
			//p.playSound("" + st[i] + ".wav");
			//System.out.println("" + st[i]);
			//while (p.isPlaying()) {
				//Thread.sleep(100);
			//}
		//}	
		//for (int i = 0; i < UnitConcatenation.numbers.length; i++){
			//p.playSound("" + UnitConcatenation.numbers[i] + ".wav");
			//while (p.isPlaying()) {
				//Thread.sleep(100);
			//}
		//}

		Thread.sleep(1000);
		while (p.isPlaying()) {
			Thread.sleep(100);
		}
		
	} catch (Throwable e) {
		e.printStackTrace();
	}
}
/**
 * Player constructor comment.
 */
public void playEndSound() {
	startPlayThread(END_SOUND, 1);
}
/**
 * Player constructor comment.
 */
public void playFlash(int count) {
	startFlashThread(count);	
}
/**
 * Player constructor comment.
 */
public void playShortSound() {
	startPlayThread(SHORT_SOUND, 0);
}
/**
 * Player constructor comment.
 */
public void playSound(String filename) throws Exception {
	String cmd = "wavp /mnt/hda/sounds/too_low.wav";
	System.out.println("1");
	process = Runtime.getRuntime().exec(cmd);
	System.out.println("2");	
	process.waitFor();
	System.out.println("3");	
//	startPlayThread(filename, 0);
}
/**
 * Player constructor comment.
 */
public void playSoundSeries(int i) {
	startPlayThread(SHORT_SOUND, i - 1);
}
/**
 * Player constructor comment.
 */
public void playUnitSoundNumber(int n) {
	String s[] = new UnitConcatenation().concatenateNumber(n);
	for (int i = 0; i < s.length; i++){
		filenameChain.addElement(s[i]);
	}
	
	if (!isPlaying())
		startPlayThread(null, 0);
}
/**
 * Player constructor comment.
 */
public void playUnitSoundSeconds(int seconds) {
	String s[] = new UnitConcatenation().concatenateTime(seconds);
	for (int i = 0; i < s.length; i++){
		filenameChain.addElement(s[i]);
	}

	if (!isPlaying())	
		startPlayThread(null, 0);
}
/**
 * Player constructor comment.
 */
public void run() {
	if (debug)
		System.out.println("inside the sound thread");
	
	if (filename != null) {
		runSound();
	} else if (filenameChain != null) {
		runChainSound();
	} else
		runFlash();
	nowPlaying = false;

	if (debug)
		System.out.println("done with sound thread");	
}
/**
 * Player constructor comment.
 */
private void runChainSound() {
	try {
		int i;
		for (i = 0; !shutdown && i < filenameChain.size(); i++){
			java.io.File f = new java.io.File(remEngine.soundsFolder + filenameChain.elementAt(i) + SOUND_FILE_EXTENSION);
			if (!f.exists()) {
				System.out.println("Sound file '" + f + " not found");
				break;
			}
			
			String cmd = remEngine.soundPlayerPath + " " + f.getAbsolutePath();

			if (debug)
				System.out.println("executing command " + cmd);

			Process p = Runtime.getRuntime().exec(cmd);	
			p.waitFor();
		}

		nowPlaying = false;

		synchronized (filenameChain) {
			while (i-- > 0){
				filenameChain.removeElementAt(0);
			}
		}
	} catch (Exception e) {
		System.out.println("Error: " + e);
	}
}
/**
 * Player constructor comment.
 */
private void runFlash() {
	if (debug)
		System.out.println("runFlashThread " + repeat);
	try {
		boolean state = true;
		for (int i = 0; i < repeat*2; i++){
			if (shutdown)
				break;

			if (state) {
				remEngine.cmd.cmdPortDBitSet(7, true);
				remEngine.cmd.cmdPortDBitSet(6, false);
			} else {
				remEngine.cmd.cmdPortDBitSet(7, false);
				remEngine.cmd.cmdPortDBitSet(6, true);
			}

			state = !state;
			Thread.sleep(1000);
		}			
		
	} catch (Exception e) {
		System.out.println("Error: " + e);
	}


	// Turn of the leds
	remEngine.cmd.cmdPortDBitSet(7, false);
	remEngine.cmd.cmdPortDBitSet(6, false);
	
	nowPlaying = false;
}
/**
 * Player constructor comment.
 */
private void runSound() {
	if (debug)
		System.out.println("inside the sound thread procedure");

	try {
		if (!filename.toLowerCase().endsWith(SOUND_FILE_EXTENSION))
			filename += SOUND_FILE_EXTENSION;

		String cmd = remEngine.soundPlayerPath + " " + remEngine.soundsFolder + filename;

		//System.out.println("executing command " + cmd);

		if (repeat > 3) {
			System.out.println("repeat=" + repeat);
			repeat = 3;
		}
		
		while (repeat-- >= 0 && !shutdown){
			
		if (debug)
			System.out.println("executing command " + cmd);
			
			process = Runtime.getRuntime().exec(cmd);

		if (debug)
			System.out.println("waiting for exec process to finish");

			process.wait(1000);
			//p.waitFor();
			//Thread.sleep(1000);

		if (debug)
			System.out.println("exec process finished");
			
		}
	} catch (Exception e) {
		System.out.println("Error: " + e);
	}
}
/**
 * Player constructor comment.
 */
public void shutdown() {
	shutdown = true;
}
/**
 * Player constructor comment.
 */
private void startFlashThread(int n) {
	if (debug)
		System.out.println("startFlashThread");	
	filename = null;
	filenameChain = null;
	repeat = n;
	nowPlaying = true;
	shutdown = false;
	playingThread = new Thread(this);
	playingThread.setDaemon(true);
	playingThread.start();
}
/**
 * Player constructor comment.
 */
private void startPlayThread(String fname, int rep) {
	filename = fname;
	repeat = rep;
	nowPlaying = true;
	shutdown = false;

	if (debug)
		System.out.println("starting sound thread");

	playingThread = new Thread(this);
	playingThread.setDaemon(true);
	playingThread.start();
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
