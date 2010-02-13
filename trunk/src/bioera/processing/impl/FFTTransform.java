/* FFTTransform.java v 1.0.9   11/6/04 7:15 PM
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

import bioera.processing.*;
import bioera.fft.*;
import bioera.properties.*;

public final class FFTTransform extends SingleElement {
	public double computeTimeSeconds = 4.0; // must be power of 2
	public int recalculateInSecond = 4;
	public int maximumFrequency = 64;
	public boolean fastResponse = true;
	public boolean subtractBias = true;
	public boolean adjustLength = true;
	public String calculationError = "0%";
	public String precision = "";
	public ComboProperty type = new ComboProperty(FFT.items);
	public ComboProperty window = new ComboProperty(Windowing.items);
	public boolean amplitudePrecision = true;
	
	private final static String propertiesDescriptions[][] = {
		{"maximumFrequency", "Maximum frequency", "", "false"},
		{"computeTimeSeconds", "Period (must be power of 2) [s]", ""},
		{"recalculateInSecond", "Rate (calculations per second)", ""},				
		{"subtractBias", "Subtract bias", ""},
		{"fastResponse", "Fast response", ""},
		{"precision", "Frequency resolution", "", "false"},
		{"calculationError", "Calculation error", "", "false"},		
		{"type", "FFT type", ""},
		{"window", "Window", ""},
		{"amplitudePrecision", "High resolution of amplitude", ""},
		{"adjustLength", "Adjust length", ""},
		
	};
	
	private BufferedScalarPipe in;
	private VectorPipeDistributor out;
	private int inputLength = 1024; // must be power of 2
	private int outputLength = 512;
	private int computePeriod = 64;
	private int inb[];
	private int fftinput[];
	private int fftoutput[];
	private FFT fft;
	private int windowing[];
	private int winDivider = 1024;

	private SignalParameters signalParams;

	protected static boolean debug = bioera.Debugger.get("transform.fft");
public FFTTransform() {
	super();
	setName("FFT");
	in = (BufferedScalarPipe) inputs[0];
	inb = in.getBuffer();
	in.setName("IN");
	outputs = new VectorPipeDistributor[1];
	outputs[0] = out = new VectorPipeDistributor(this);
	out.setName("FFT");
}
public String getElementDescription() throws Exception {
	return "Performs FFT transformation";
}
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
public final void process() {
	int n = in.available();
	if (n < Math.max(inputLength, computePeriod))
		return;
		
	if (fastResponse) {
		do {
			// Perform FFT tranformation, use internal Pipe's buffer
			if (windowing != null) {
				for (int i = 0; i < inputLength; i++){
					fftinput[i] = (inb[i] * windowing[i]) / winDivider;
				}
				fft.perform(fftinput, fftoutput);
			} else {
				fft.perform(inb, fftoutput);
			}
			if (subtractBias)
				fftoutput[0] = 0;
			out.writeVector(fftoutput);
			in.purge(computePeriod);			
		} while (in.available() > Math.max(inputLength, computePeriod));
	} else {
		// Process only once and only the most recent data		
		if (windowing != null) {
			for (int i = 0; i < inputLength; i++){
				fftinput[i] = (inb[i + n - inputLength] * windowing[i]) / winDivider;
			}
		} else {
			System.arraycopy(in.getBuffer(), n - inputLength, fftinput, 0, inputLength);
		}
		
		// Perform FFT tranformation, use internal Pipe's buffer
		fft.perform(fftinput, fftoutput);

		if (subtractBias)
			fftoutput[0] = 0;
		out.writeVector(fftoutput);

		in.purgeAll();
	}
}
public final void processDebug() {
	if (in.available() < Math.max(inputLength, computePeriod))
		return;

	long t[] = new long[5]; int n = 0;
	t[n++] = System.currentTimeMillis();
		
	// Process only once and only the most recent data
	System.arraycopy(in.getBuffer(), in.available() - inputLength, fftinput, 0, inputLength);

	t[n++] = System.currentTimeMillis();
	
	// Perform FFT tranformation, use internal Pipe's buffer
	fft.perform(fftinput, fftoutput);

	t[n++] = System.currentTimeMillis();
	
	out.writeVector(fftoutput);

	t[n++] = System.currentTimeMillis();
	
	in.purgeAll();

	t[n++] = System.currentTimeMillis();

	ProcessingTools.showTimeCharacteristic(t);
}
public void reinit() throws Exception {
	if (type.getSelectedIndex() == -1)
		type.setSelectedIndex(FFT.C_INTEGER);
	if (window.getSelectedIndex() == -1)
		window.setSelectedIndex(Windowing.W_HAMMING);
			
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		// Nothing is connected to the input so no initiation is required
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	signalParams = null;

	int precisionIncreaseBits = 0;
	if (amplitudePrecision) {
		int inr = predecessorElement.getSignalParameters().getSignalResolutionBits();
		precisionIncreaseBits = 15 - inr;
		if (precisionIncreaseBits > inr - 1)
			precisionIncreaseBits = inr - 1;
		if (precisionIncreaseBits < 0)
			throw new Exception("Input range should be up to 15 bits, now is " + inr);
		if (precisionIncreaseBits > 0) {
			//System.out.println("precisionIncreaseBits="+precisionIncreaseBits);
			getSignalParameters().setDigitalPositiveRange(predecessorElement.getSignalParameters().getDigitalRange() << precisionIncreaseBits);
			setOutputResolutionBits(predecessorElement.getSignalParameters().getSignalResolutionBits() + precisionIncreaseBits);
		} else {
			getSignalParameters().setDigitalPositiveRange(predecessorElement.getSignalParameters().getDigitalRange());
			setOutputResolutionBits(predecessorElement.getSignalParameters().getSignalResolutionBits());
		}
		if (debug)
			System.out.println("FFT output range is " + getSignalParameters().getDigitalRange());
	}

	int redeRate = (int) predecessorElement.getSignalParameters().getSignalRate();
	if (adjustLength) {
		int nl = (int)(Math.log(redeRate)/Math.log(2));
		if ((1 << nl) != redeRate) { 
			if (debug)
				System.out.println("rate " + redeRate + " is not a power of 2 (2^" + nl + "="+(1<<nl)+", 2^"+(nl+1)+"="+(2<<nl)+")");
			// Take higher number of bits and compare errors
			int e1 = redeRate - (1<<nl);
			int e2 = (1<<(nl+1)) - redeRate;
			double error;
			if (e1 < e2) {
				error = ((double) e1) / redeRate;
				redeRate = 1<<nl;
			} else {
				error = ((double) e2) / redeRate;
				redeRate = 1<<(nl+1);
			}
			calculationError = "" + ((int)(error * 10000)) / 100.0 + "%";
			if (debug)
				System.out.println("FFT: Adjusted computation rate: " + redeRate);
		} else {
			calculationError = "none";
		}
	} else {
		calculationError = "none";		
	}
	
	inputLength = (int) (computeTimeSeconds * redeRate);	
	fft = FFT.getFFT(type.getSelectedIndex(), inputLength, predecessorElement.getSignalParameters().getDigitalRange(), precisionIncreaseBits+1);
	computePeriod = redeRate / recalculateInSecond;
	fftinput = new int[inputLength];
	outputLength = inputLength / 2;
	fftoutput = new int[outputLength];
	maximumFrequency = redeRate / 2;	
	out.setVectorLength(outputLength);
	out.getSignalParameters().setVectorMin(0);
	out.getSignalParameters().setVectorMax(maximumFrequency);
	precision = "" + 1.0 / out.getSignalParameters().getVectorResolution() + "Hz";
	if (window.getSelectedIndex() != Windowing.W_NONE) {
		//winDivider = 1 << (30 - predecessorElement.getSignalParameters().signalResolutionBits);
		winDivider = (1 << 28) / in.getSignalParameters().getDigitalRange();
		if (winDivider == 0)
			throw new Exception("Unexpected error: winDivider=0");
		Windowing w = new Windowing(window.getSelectedIndex(), inputLength, winDivider);
		windowing = w.getPowerWindow();
	} else {
		windowing = null;
	}

	setOutputSignalRate(recalculateInSecond);
	getSignalParameters().setPhysicalUnit("uVpp");
	getSignalParameters().setVectorUnit("Hz");
	if (debug)
		System.out.println("FTBandwithFilter: computationSamplesNumber=" + inputLength + " computeEveryNSamples=" + computePeriod);
	getSignalParameters().setPhysMax(in.getSignalParameters().getPhysRange());
	getSignalParameters().setPhysMin(0);

	if (debug) {
		System.out.println("FFTTransform diagnostic info:");
		System.out.println("Input  length: " + fftinput.length);
		System.out.println("Output length: " + fftoutput.length);
		System.out.println("Max frequency: " + maximumFrequency);
		System.out.println("computeEveryNSamples: " + computePeriod);
		System.out.println("redeRate: " + redeRate);
		System.out.println("recalculateInSecond: " + recalculateInSecond);
	}
	
	super.reinit();
}
public void start() throws Exception {
	for (int i = 0; i < outputLength; i++){
		fftoutput[i] = 0;
	}

	for (int i = 0; i < recalculateInSecond * computeTimeSeconds; i++){
		out.writeVector(fftoutput);
	}		
}
}
