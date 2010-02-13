/* WavSoundPlayer.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.sound;

import java.io.*;
//import com.sun.media.sound.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class WavSoundPlayer implements Runnable {
	private Thread thread;
	String executable;
	String folder;
	int valueToPlay = 0;
	String extension;
	boolean ready = false;
	boolean shutdown = false;
	Object o = new Object();
public WavSoundPlayer(String iexecutable, String ifolder, String iextension) {
	executable = iexecutable;
	folder = ifolder;
	extension = iextension;

	thread = new Thread(this);
	thread.setDaemon(true);
	thread.start();
}
public static void main(String s[]) {
	try {
		WavSoundPlayer w = new WavSoundPlayer("c:\\tools\\wav.exe", "c:\\projects\\eeg\\s1\\", ".wav");
		Thread.sleep(2000);
		w.play(1);
		Thread.sleep(2000);
		w.play(2);
		Thread.sleep(2000);
		w.shutdown();
//		Thread.sleep(2000);
	} catch (Exception e) {
		e.printStackTrace();
	}
}
public synchronized void play(int value) {
	valueToPlay = value;
	ready = true;
	notify();
}
public void run() {
	while (true) {
		while (!ready && !shutdown) {
			synchronized (this)	{
				try {
					wait();
				} catch (InterruptedException e) {
		        }
			}
		}

		if (shutdown) {
			break;
		}
		
		ready = false;

		try {
	//		String s = "/tools/wav.exe c:\\projects\\eeg\\samples\\" + value + ".wav";
	//		String s = "c:\\projects\\eeg\\play.bat c:\\projects\\eeg\\samples\\" + valueToPlay + ".wav";
			String s = executable + " " + folder + valueToPlay + extension;
			System.out.println("Playing " + valueToPlay);
			System.out.println("Executing " + s);
			Process p = Runtime.getRuntime().exec(s);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
public synchronized void shutdown() {
	shutdown = true;
	notify();
}
}
