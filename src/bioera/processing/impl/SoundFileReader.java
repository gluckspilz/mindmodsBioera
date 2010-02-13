/* SoundFileReader.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.*;
import bioera.sound.*;
import bioera.properties.*;
import javax.sound.sampled.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class SoundFileReader extends SingleElement {
	public BrowseFile filePath = new BrowseFile("archives/a.wav");

	private final static String propertiesDescriptions[][] = {
//		{"", "", ""},
	};	

	
	private ScalarPipeDistributor out[];
	private int buffer[];
	private int chNo, counter, fsize, ssize;//, zeroLevel;
	protected File infile;
	private boolean lEndian;
	protected AudioInputStream ain;
	private byte fBuf[];

	protected static boolean debug = bioera.Debugger.get("impl.sound.reader");
/**
 * Element constructor comment.
 */
public SoundFileReader() {
	super();
	setName("SoundReader");
	outputs = out = new ScalarPipeDistributor[2];
	outputs[0] = out[0] = new ScalarPipeDistributor(this);
	out[0].setName("1");
	outputs[1] = out[1] = new ScalarPipeDistributor(this);
	out[1].setName("2");
}
/**
 * Element constructor comment.
 */
public void close() throws Exception {
	if (ain != null) {
		ain.close();
		ain = null;
	}		
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Writes to file in WAV sound format";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 0;
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 2;
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
public void initFile()  throws Exception {
	if (!infile.exists()) {
		throw new Exception("File '" + infile + " not found");
	}
	
	ain = AudioSystem.getAudioInputStream(infile);
	AudioFormat format = ain.getFormat();

	setOutputSignalRate((int) format.getSampleRate());

	lEndian = !format.isBigEndian();
	chNo = format.getChannels();
	if (chNo < 1)
		throw new DesignException("Channel number must be at least 1");
	
	fsize = format.getFrameSize();
	fBuf = new byte[fsize];
	
	ssize = format.getSampleSizeInBits();

	setOutputResolutionBits(ssize);
	setOutputDigitalRange(1 << ssize);
	setOutputPhysicalRange(1 << ssize);

	if (ssize != 8 && ssize != 16) {
		System.out.println("Sample size " + ssize + " not implemented, only 8 or 16 allowed");
	}

	if (debug) {
		System.out.println("Sample rate: " + format.getSampleRate());
		System.out.println("Channels   : " + chNo);
		System.out.println("Encoding   : " + format.getEncoding());
		System.out.println("Frame rate : " + format.getFrameRate());
		System.out.println("Frame size : " + fsize);
		System.out.println("Sample size: " + ssize);
		System.out.println("Big endian : " + !lEndian);
		System.out.println("Format     : " + format);
	}
}
/**
 * Element constructor comment.
 */
public void process() throws Exception {
	if (ain == null)
		return;

	int n = Math.min(ain.available(), out[0].minAvailableSpace() >> 1);
	while (n >= fsize) {
		int i, ind = 0;
		if ((i = ain.read(fBuf)) != fsize)
			throw new Exception("read=" + i + "!= fsize=" + fsize); 
		//System.out.println("received " + n);
		counter++;
		for (int ch = 0; ch < chNo; ch++){
			if (lEndian) {
				i = (((int)fBuf[ind++]) + 128) ^ 128;
				if (ssize == 16) {
					i += (((((int)fBuf[ind++]) + 128)^128) << 8);
					i = (i^32768) - 32768;
				} else {
					i = (i^128) - 128;
				}
			} else {
				i = (((int)fBuf[ind++]) + 128) ^ 128;
				if (ssize == 16) {
					i = (i << 8) + ((((int)fBuf[ind++]) + 128) ^ 128);
					i = (i^32768) - 32768;
				} else {
					i = (i^128) - 128;
				}
			}
			out[ch].write(i);
		}
		
		n-=fsize;
	}

	if (ain.available() == 0)
		close();
	
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	infile = filePath.getAbsoluteFile();
	if (!infile.exists()) {
		throw new DesignException("File '" + infile + "' not found");
	}

	// Open file to read all parameters
	reinit2();
}
/**
 * Element constructor comment.
 */
public void reinit2()  throws Exception {
	// Open file to read all parameters
	initFile();

	// Close, it will be reopen when processing starts
	close();	

	super.reinit();
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2003 1:22:12 PM)
 */
public void start() throws Exception {
	ain = AudioSystem.getAudioInputStream(infile);
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2003 1:22:12 PM)
 */
public void stop() throws Exception {
	close();
}
}
