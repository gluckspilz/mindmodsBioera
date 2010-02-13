/* SimulationSource.java v 1.0.9   11/6/04 7:15 PM
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


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class SimulationSource extends SourceElement {
	public final static double PI2 = 2 * Math.PI;
	public final static int factor = 100;

	public double frequencies[] = {16};
	public int amplitudes[] = {100};
	public int signalRange = 512;
	public int digitalRange = 1024;
	public int phaseShift = 0;
	public int noiseLevel = 0;
	public int rate = 256;
//	public int bits = 10;
	

	private final static String propertiesDescriptions[][] = {
		{"frequencies", "Frequency array [>0]", ""},
		{"amplitudes", "Amplitude array [uV]", ""},
		{"phaseShift", "Phase shift [0-360]", ""},
		{"signalRange", "Signal range [uV]", ""},
		{"noiseLevel", "Noise level [uV]", ""},
		{"rate", "Output sample rate [Hz]", ""},
	};

	private int index;
	private long time;

	private long seed;
	private int noiseBits;
	private int noiseHalf;
	private ScalarPipe out0;
	private int digitalAmplitude;
/**
 * SerialDeviceSource constructor comment.
 */
public SimulationSource() {
	super();
	setName("Simulator");
	out0 = (ScalarPipe) outputs[0];
	out0.setName("OUT");
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Simulator";
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
	long n = (mainProcessingTime - time) / factor;
	if (n == 0)
		return;
	time += n * factor;
	n = n * rate * factor / 1000;
	int v;
	int mod = (Integer.MAX_VALUE / rate) * rate;
	seed = mainProcessingTime;
	while (n-- > 0) {
		index = (index + 1) % mod;
//		v =  signalRange / 2 * digitalRange / signalRange;
//		v =  digitalRange / 2;
		v =  0;
		for (int i = 0; i < frequencies.length; i++){
			v+= (int) (amplitudes[i] * Math.sin(PI2* frequencies[i] * index / rate) * digitalRange / signalRange);
		}
		if (noiseLevel > 0) {
			// Pseudo random value
			seed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL;			
			v += (int)(seed >> (48 - noiseBits)) - noiseHalf;
		}

		if (v < -digitalAmplitude) {
			v = -digitalAmplitude;
		} else if (v > digitalAmplitude) {
			v = digitalAmplitude;
		}

		out0.write(v);

		////System.out.println("set ");
		//if (v > digitalRange / 2)
			//setStatusChar('U', v % 10, 1);
		//else 
			//setStatusChar('O', v % 10, 0);
		
	}
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	index = rate * phaseShift / 360;
	seed = System.currentTimeMillis();
	int v = noiseLevel * digitalRange / signalRange;
	noiseBits = 0;
	while ((1 << noiseBits) < v)
		noiseBits++;	
	noiseHalf = (1 << noiseBits) / 2;
	digitalAmplitude = digitalRange / 2;
	SignalParameters p = getSignalParameters();
	p.setSignalRate(rate);
	p.setDigitalRange(digitalRange);
	p.setPhysicalRange(signalRange);
	int bits = 0, r = digitalRange - 1;
	while (r > 0) {
		bits++;
		r/=2;
	}
	p.setSignalResolutionBits(bits);
	p.setInfo("Source: Simulator");
	p.setPhysicalUnit("uV");
	if (amplitudes.length < frequencies.length) {
		if (amplitudes.length < 1)
			throw new bioera.graph.designer.DesignException("Amplitude not set");
		int namp[] = new int[frequencies.length];
		System.arraycopy(amplitudes, 0, namp, 0, amplitudes.length);
		for (int i = amplitudes.length; i < namp.length; i++){
			namp[i] = namp[i-1];
		}
		amplitudes = namp;
	}
		
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start() throws Exception {
	time = mainProcessingTime;
}
}
