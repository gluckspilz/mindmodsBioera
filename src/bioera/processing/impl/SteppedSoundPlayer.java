/* SteppedSoundPlayer.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.processing.impl;

import java.io.*;
import java.util.*;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.fft.*;
import bioera.*;
import bioera.sound.*;


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
public final class SteppedSoundPlayer extends SingleElement {
	public static String samplesFolder = "media/ding/";
	public static String sampleExecutable = "";
	public static String sampleExtension = ".wav";
	public boolean nameLookup = true;
	
	private final static String propertiesDescriptions[][] = {
		{"sampleExecutable", "External sound player path", ""},				
	};

	private static final int SOUND_BUFFER_SIZE = 1024;
	
	private int b[];
	private BufferedScalarPipe in;
	private ScalarPipeDistributor out;
	private WavSoundPlayer soundPlayer;
/**
 * VectorDisplay constructor comment.
 */
public SteppedSoundPlayer() {
	super();
	setName("Sound");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	b = in.getBuffer();
	out = (ScalarPipeDistributor) outputs[0];
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Sound player, external or internal";
}
/**
 * Element constructor comment.
 */
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
/**
 * Element constructor comment.
 */
protected void play(int n) throws java.lang.Exception {
	//System.out.println("#" + i);

	File file = new File(samplesFolder, "" + n + sampleExtension);
	if (!file.exists()) {
		// search for file name
		File folder = new File(samplesFolder);
		if (folder.exists() && folder.isDirectory()) {
			String list[] = folder.list();
			for (int i = 0; i < list.length; i++){
				String name = list[i];
				if (name.indexOf("_") != -1) {
					try {
						int num = Integer.parseInt(name.substring(0, name.indexOf("_")));
						if (num == n) {
							file = new File(samplesFolder, list[i]);
							break;
						}
					} catch (Exception e) {
					}
				}
			}
		}
	}
	

	if (soundPlayer == null)
		playInternal(file);
	else
		soundPlayer.play(n);
}
/**
 * Element constructor comment.
 */
protected void playInternal(File soundFile) throws java.lang.Exception {
	if (!soundFile.exists()) {
		System.out.println("Sound file not found: " + soundFile);
		return;
	}

	try {
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		AudioFormat	audioFormat = audioInputStream.getFormat();
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
		line.open(audioFormat);
		line.start();
		int	nBytesRead = 0;
		byte[]	abData = new byte[SOUND_BUFFER_SIZE];
		while ((nBytesRead = audioInputStream.read(abData, 0, abData.length)) != -1) {
			line.write(abData, 0, nBytesRead);
		}

		line.drain();
		line.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
}
/**
 * Element constructor comment.
 */
public final void process() throws java.lang.Exception {
	int n = in.available();

	if (n == 0)
		return;

	// Write sound that are being played to output
	out.write(b[n - 1]);

	// Play the last sound in buffer
	play(b[n - 1]);

	in.purgeAll();
		
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (!new File(samplesFolder).exists() || !((new File(samplesFolder)).isDirectory()))
		throw new bioera.graph.designer.DesignException("Folder '" + samplesFolder + "' not found");
	
	if (sampleExecutable != null && sampleExecutable.trim().length() == 0) {
		sampleExecutable = null;
	}

	if (sampleExecutable != null) {
		soundPlayer = new WavSoundPlayer(sampleExecutable, toRootFolder(samplesFolder), sampleExtension);
	}
	
	super.reinit();	
}
}
