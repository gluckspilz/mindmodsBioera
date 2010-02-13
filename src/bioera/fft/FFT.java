/* FFT.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.fft;

import java.lang.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public abstract class FFT {
	public final static String items[] = {
		"INTEGER",
		"FLOAT",
		"NATIVE",
	};
	
	
	// Those values can be changed
	public int NO_OF_BITS; //	number of bits in sample number	
	public int MAX_AMPLITUDE_BITS; // max number of bits in signal amplitude
	
	public static final int DEFAULT_SAMPLES_NUMBER = 1024;
	public static final int DEFAULT_MAX_AMPLITUDE = 1024;
	
	public static final int C_INTEGER = 0;
	public static final int C_FLOAT = 1;
	public static final int C_NATIVE = 2;
	public static final int C_DOUBLE = 3;
	public static final int C_LONG = 4;

	public int samplesNumber;
	public int maxAmplitude;
	public int oPB;
	public int oPBV;
	public int type;

	protected static boolean debug = bioera.Debugger.get("fft");
protected FFT(int len, int amp, int outputPrecBits) {
	samplesNumber = len;
	maxAmplitude = amp;
	oPB = outputPrecBits;
	oPBV = 1 << oPB;
}
public final static FFT getFFT(int type, int length, int maxAmplitude, int outputPrecisionBits) throws Exception {
	return newInstance(type, length, maxAmplitude, outputPrecisionBits);
}
public abstract int getPrecisionFactor();
public void init() throws Exception {
	int x = 0;
	while ((1 << x) < samplesNumber) {
		x++;
	}
	NO_OF_BITS = x;

	if ((1 << NO_OF_BITS) != samplesNumber)
		throw new RuntimeException("The length of the sample " + samplesNumber + " must be power of 2 (" + NO_OF_BITS + ")");
	
	x = 0;
	while ((1 << x) < maxAmplitude) {
		x++;
	}
	MAX_AMPLITUDE_BITS = x;	
}
protected final static void initializeNative() {
	try {
		System.loadLibrary("bioerafft");
	} catch (Throwable e) {
		//e.printStackTrace();
		if (debug) {
			System.out.println("" + e.getMessage());
			System.out.println("Native implementation (bioerafft) of FFT not found in "+System.getProperty("java.library.path")+ ", using pure version");
		}
	}
}
public final static FFT newInstance() throws Exception {
	return newInstance(C_INTEGER, DEFAULT_SAMPLES_NUMBER, DEFAULT_MAX_AMPLITUDE, 0);
}
public final static FFT newInstance(int itype, int length, int maxAmplitude, int outputPrecisionBits) throws Exception {
	if (itype == C_NATIVE)
		initializeNative();

	FFT ret;
	switch (itype) {
		case C_INTEGER:
			ret = new FFTInteger(length, maxAmplitude, outputPrecisionBits);
			break;
		case C_FLOAT:
			ret = new FFTFloat(length, maxAmplitude, outputPrecisionBits);
			break;
		case C_NATIVE:
			ret = new FFTNative(length, maxAmplitude, outputPrecisionBits);
			break;
		case C_DOUBLE:
			ret = new FFTDouble(length, maxAmplitude, outputPrecisionBits);
			break;
		case C_LONG:
			ret = new FFTInteger(length, maxAmplitude, outputPrecisionBits);
			break;
		default:
			throw new RuntimeException("Unknown type: " + itype);
	}

	ret.type = itype;
	ret.init();

	return ret;
}
public abstract void perform(int input[], int output[]);
public int type() throws Exception {
	return this.type;
}

public void performComplex(int inpRe[], int inpIm[], int outRe[], int outIm[]) {
	throw new RuntimeException("Complex transform not implemented");
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
