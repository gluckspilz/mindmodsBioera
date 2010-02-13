/* SteppedSoundFileReader.java v 1.0.9   11/6/04 7:15 PM
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
public final class SteppedSoundFileReader extends SoundFileReader {
	public static String sampleExtension = ".wav";
	
	private final static String propertiesDescriptions[][] = {
		{"filePath", "Folder with samples", ""},				
	};

	private BufferedScalarPipe in;	
/**
 * VectorDisplay constructor comment.
 */
public SteppedSoundFileReader() {
	super();
	setName("StepSound");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Stepped sound reader, for use with many files";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 1;
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
public final void process() throws java.lang.Exception {
	if (in.isEmpty() && ain == null)
		return;
	
	int n = in.available();

	if (n > 0) {
		infile = new File(filePath.path, "" + in.getBuffer()[n - 1] + sampleExtension);
		super.initFile();
		in.purgeAll();		
	}

	super.process();
}
/**
 * Element constructor comment.
 */
public final void reinit() throws java.lang.Exception {
	if (!new File(filePath.path).exists()) {
		throw new Exception("Folder '" + filePath.path + "' not found");
	}

	// Find try to find a file in folder by index
	for (int i = 0; i < 10; i++){
		infile = new File(filePath.path, "" + i + sampleExtension);
		if (infile.exists())
			break;
	}

	if (!infile.exists()) {
		// Then find any file
		String list[] = new File(filePath.path).list();
		if (list != null) {
			for (int i = 0; i < list.length; i++){
				if (list[i].endsWith(sampleExtension)) {
					infile = new File(filePath.path, list[i]);
					break;
				}			
			}
		}
	}

	if (!infile.exists()) {
		throw new Exception("No files found in folder " + filePath.path);
	}	

	super.reinit2();
}
/**
 * Element constructor comment.
 */
public final void start() throws java.lang.Exception {
	// This empty method is here so that the parent start() is not invoked
}
}
