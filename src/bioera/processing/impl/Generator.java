/* Generator.java v 1.0.9   11/6/04 7:15 PM
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
import javax.comm.*;
import bioera.processing.*;
import bioera.properties.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class Generator extends Element {
	public final static double PI2 = 2 * Math.PI;
	public final static int factor = 100;
	public boolean remember = true;

	public ComboProperty function = new ComboProperty(new String[] {
		"SINE",
		"TRIANGLE",
		"RECTANGLE",
		"NOISE",
	});

	public static final int SINE = 0;
	public static final int TRIANGLE = 1;
	public static final int RECTANGLE = 2;
	public static final int NOISE = 3;
	
	private int type = SINE;
		
	public double frequency = 16;
	public int amplitude = 20;
	public int digitalRange = 1024;
	public int phaseShift = 0;
	public int points = 256;
	public int precisionBits = 10;

	public boolean smoothedFrequencyChange = true;
	public int freqChangeDynamics = 100;
 	
	private int smoothedFreqCounter = -1;
	private int newFreq = 0;	
		
	private final static String propertiesDescriptions[][] = {
		{"frequency", "Frequency [Hz]", ""},
		{"amplitude", "Amplitude [peak-to-peak]", ""},
		{"phaseShift", "Phase shift [0-360]", ""},
		{"signalRange", "Signal range [uV]", ""},
		{"rate", "Output sample rate [sps]", ""},
	};

	private int index, tab[];
	BufferedScalarPipe inFreq;
	int bufFreq[],  // buffer for freq input
		freq, 		// Current value with precision divider
		lastInFreq; // Last frequency value read from input

	BufferedScalarPipe inAmp;
	int bufAmp[],	// buffer for amplitude input
		amp; 		// Current value of amplitude with precision divider
		
	private ScalarPipeDistributor out0;

	private int bufferOccupiedThresh = -1;
	public int bufferOccupiedThreshPercent = 30;  // Start filling output buffer if it is less then 30% full

	private int bufferDestinationSpace = -1;
	public int bufferOccupiedThreshPercentTo   = 90;  // Fill output buffer up to 99%
/**
 * SerialDeviceSource constructor comment.
 */
public Generator() {
	super();
	setName("Generator");

	out0 = (ScalarPipeDistributor) outputs[0];
	out0.setName("OUT");
	
	inFreq = (BufferedScalarPipe)inputs[0];
	inFreq.setName("Freq");
	bufFreq = inFreq.getBuffer();
	
	inAmp = (BufferedScalarPipe)inputs[1];
	inAmp.setName("Vol");
	bufAmp = inAmp.getBuffer();	
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Generator";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 2;
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
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
public final void process() throws Exception {
	if (out0.minAvailableSpace() < bufferOccupiedThresh) {
		//System.out.println("minav=" + out0.minAvailableSpace() + " down thresh=" + bufferOccupiedThresh);
		return;
	}

	if (inFreq.available() > 0) {
		if (smoothedFrequencyChange) {
			newFreq = bufFreq[inFreq.available() - 1] << precisionBits;
			smoothedFreqCounter = freqChangeDynamics;
		} else {
			freq = bufFreq[inFreq.available() - 1] << precisionBits;
		}
		inFreq.purgeAll();
	}

	if (inAmp.available() > 0) {
		amp = bufAmp[inAmp.available() - 1];
		inAmp.purgeAll();
	}
		
	// Ok, buffer is free, now fill it up to required space
	int n = Math.min(bufferDestinationSpace, out0.minAvailableSpace()), v;
	//System.out.println("minav=" + out0.minAvailableSpace() + "  thresh=" + bufferDestinationSpace + "  n=" + n);
	while (n-- > 0) {
		index = (index + freq) % (points << precisionBits);
		v = (tab[index >> precisionBits] * amp) / digitalRange;
		out0.write(v);
		
		if (smoothedFreqCounter != -1) {
			if (smoothedFreqCounter > 0)
				smoothedFreqCounter--;
			else {
				shiftFrequency();
			}
		}
	}	
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	if (function.getSelectedIndex() == -1)
		function.setSelectedIndex(type);
	else
		type = function.getSelectedIndex(); 
	
	freq = (int)(frequency * (1 << precisionBits));	
	amp = amplitude;
		
	tab = new int[points];

	switch (type) {
		case SINE:
			for (int i = 0; i < points; i++){
				tab[i] = (int) (digitalRange * (Math.sin(PI2 * i / points)) / 2);
			}
			break;
		case TRIANGLE:
			for (int i = 0; i < points / 4; i++){
				tab[i] = (i * (digitalRange/2) / (points/4));
				tab[i + points*1/4] = digitalRange/2 - (i * (digitalRange/2) / (points/4));
				tab[i + points*2/4] = - (i * (digitalRange/2) / (points/4));
				tab[i + points*3/4] = -(digitalRange/2) + (i * (digitalRange/2) / (points/4));
			}
			break;
		case RECTANGLE:
			for (int i = 0; i < points / 2; i++){
				tab[i] = digitalRange / 2;
				tab[i + points / 2] = - tab[i];
			}
			break;
		case NOISE:
			long seed = System.currentTimeMillis();
			int noiseBits = 0;
			while ((1 << noiseBits) < digitalRange)
				noiseBits++;
			int noiseHalf = (1 << noiseBits) / 2;
			for (int i = 0; i < points; i++){				
				seed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL;			
				tab[i] = (int)(seed >> (48 - noiseBits)) - noiseHalf;				
			}
			break;
		default: 
			break;
	}

	setOutputSignalRate(points);
	setOutputDigitalRange(digitalRange);
	setOutputPhysicalRange(digitalRange);
	int bits = 0;
	while ((1 << bits) < digitalRange)
		bits++;

	setOutputResolutionBits(bits);
//	setOutputPhysicalRange(signalRange);

//	zeroLevel = digitalRange / 2;

	if (((double)points * 2 * (1 << precisionBits)) > Integer.MAX_VALUE) {
		throw new Exception("Precision bits or number of points too high");
	}
	
	super.reinit();
}
/**
 * Element constructor comment.
 */
private final void shiftFrequency() throws Exception {
	if ((freqChangeDynamics >> precisionBits) > 0) {
		smoothedFreqCounter = freqChangeDynamics >> precisionBits;
		if (newFreq > freq) {
			freq++;
		} else if (newFreq < freq) {
			freq--;
		} else
			smoothedFreqCounter = -1;
	} else {
		int incr = (1 << precisionBits) / freqChangeDynamics;
		smoothedFreqCounter = 0;
		if (newFreq > freq) {
			freq+=incr;
			if (freq >= newFreq) {
				freq = newFreq;
				smoothedFreqCounter = -1;
			}
		} else {
			freq-=incr;
			if (freq <= newFreq) {
				freq = newFreq;
				smoothedFreqCounter = -1;
			}
		} 						
	}
}
/**
 * Element constructor comment.
 */
public void start() throws Exception {
	index = (points * phaseShift / 360) << precisionBits;
	bufferOccupiedThresh = out0.minBufferSize() * bufferOccupiedThreshPercent / 100;
	bufferDestinationSpace = out0.minBufferSize() * bufferOccupiedThreshPercentTo / 100;

	//System.out.println("out0.minBufferSize()=" + out0.minBufferSize());
	//System.out.println("bufferOccupiedThresh=" + bufferOccupiedThresh);
	//System.out.println("bufferDestinationSpace=" + bufferDestinationSpace);	
}
/**
 * Element constructor comment.
 */
public void stop() throws Exception {
	if (remember) {
		frequency = ((double) freq) / (1 << precisionBits);
		amplitude = amp;
	}
}
}
