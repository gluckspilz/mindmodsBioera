/* FFT2Transform.java v 1.0.9   11/6/04 7:15 PM
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

public final class FFT2Transform extends Element {
	public float maximumFrequency = 64;
	public boolean subtractBias = true;
	public ComboProperty type = new ComboProperty(FFT.items);
	public boolean amplitudePrecision = true;
	
	private final static String propertiesDescriptions[][] = {
		{"maximumFrequency", "Maximum frequency", "", "false"},
		{"subtractBias", "Subtract bias", ""},
		{"precision", "Frequency resolution", "", "false"},
		{"calculationError", "Calculation error", "", "false"},		
		{"type", "FFT type", ""},
		{"window", "Window", ""},
		{"amplitudePrecision", "High resolution of amplitude", ""},
		{"adjustLength", "Adjust length", ""},
		
	};
	
	private BufferedVectorPipe inRe, inIm;
	private VectorPipeDistributor outRe, outIm;
	private int inbuffRe[][], inbuffIm[][], inbRe[], inbIm[];
//	private int inRe[], inIm[], outRe[], outIm[];
	private int outReb[], outImb[];
	private FFT fft;
//	private int zeroLevel, 
	private int zeroVector[];
	private int inputSize, outputSize;
	private boolean reConnected, imConnected;

	private SignalParameters signalParams;

	protected static boolean debug = bioera.Debugger.get("transform.fft");
public FFT2Transform() {
	super();
	setName("FFT");
	inputs = new BufferedVectorPipe[2];
	inputs[0] = inRe = new BufferedVectorPipe(this);
	inbuffRe = inRe.getVBuffer();
	inRe.setName("RE");
	inputs[1] = inIm = new BufferedVectorPipe(this);
	inbuffIm = inIm.getVBuffer();
	inIm.setName("IM");
	
	outputs = new VectorPipeDistributor[2];
	outputs[0] = outRe = new VectorPipeDistributor(this);
	outRe.setName("RE");
	outputs[1] = outIm = new VectorPipeDistributor(this);
	outIm.setName("IM");
	
}
public String getElementDescription() throws Exception {
	return "Complex FFT transformation";
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
	return 2;
}
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
public final void process() {	
	if (reConnected) {
		if (inRe.isEmpty())
			return;

		int n = imConnected ? Math.min(inRe.available(), inIm.available()) : inRe.available();

		for (int i = 0; i < n; i++){
			inbIm = imConnected ? inbuffIm[i] : zeroVector;
			fft.performComplex(inbuffRe[i], inbIm, outReb, outImb);
			if (subtractBias) {
				outReb[0] = 0;
				outImb[0] = 0;
			}
			outRe.writeVector(outReb);
			outIm.writeVector(outImb);
		}

		inRe.purgeAll();
		if (imConnected)
			inIm.purgeAll();
	} else if (imConnected) {
		if (inIm.isEmpty())
			return;

		int n = inIm.available();

		for (int i = 0; i < n; i++){
			fft.performComplex(zeroVector, inbIm, outReb, outImb);
			if (subtractBias) {
				outReb[0] = 0;
				outImb[0] = 0;
			}
			outRe.writeVector(outReb);
			outIm.writeVector(outImb);
		}
		
		inIm.purgeAll();		
	}	
}
public final void process1() {
	if (reConnected && inRe.isEmpty() || imConnected && inIm.isEmpty())
		return;

	int n;
	if (reConnected && imConnected)
		n = Math.min(inRe.available(), inIm.available());
	else if (reConnected)
		n = inRe.available();
	else
		n = inIm.available();

	for (int i = 0; i < n; i++){
		inbRe = reConnected ? inbuffRe[i] : zeroVector;
		inbIm = imConnected ? inbuffIm[i] : zeroVector;
		fft.performComplex(inbRe, inbIm, outReb, outImb);
		if (subtractBias) {
			outReb[0] = 0;
			outImb[0] = 0;
		}
		outRe.writeVector(outReb);
		outIm.writeVector(outImb);
	}

	if (reConnected)
		inRe.purgeAll();
	if (imConnected)
		inIm.purgeAll();		
}
public final void process2() {	
	if (reConnected) {
		if (inRe.isEmpty())
			return;

		int n = imConnected ? Math.min(inRe.available(), inIm.available()) : inRe.available();

		for (int i = 0; i < n; i++){
			inbIm = imConnected ? inbuffIm[i] : zeroVector;
			fft.performComplex(inbRe, inbIm, outReb, outImb);
			if (subtractBias) {
				outReb[0] = 0;
				outImb[0] = 0;
			}
			outRe.writeVector(outReb);
			outIm.writeVector(outImb);
		}

		inRe.purgeAll();
		if (imConnected)
			inIm.purgeAll();
	} else if (imConnected) {
		if (inIm.isEmpty())
			return;

		int n = inIm.available();

		for (int i = 0; i < n; i++){
			fft.performComplex(zeroVector, inbIm, outReb, outImb);
			if (subtractBias) {
				outReb[0] = 0;
				outImb[0] = 0;
			}
			outRe.writeVector(outReb);
			outIm.writeVector(outImb);
		}
		
		inIm.purgeAll();		
	}	
}
public void reinit() throws Exception {
	if (type.getSelectedIndex() == -1)
		type.setSelectedIndex(FFT.C_INTEGER);
	
	reConnected = getFirstElementConnectedToInput(0) != null;
	imConnected = getFirstElementConnectedToInput(1) != null;

	if (!reConnected)
		predecessorElement = getFirstElementConnectedToInput(1);

	if (predecessorElement == null)
		throw new RuntimeException("Not connected");
		
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
			setOutputDigitalRange(predecessorElement.getSignalParameters().getDigitalRange() << precisionIncreaseBits);
			setOutputResolutionBits(predecessorElement.getSignalParameters().getSignalResolutionBits() + precisionIncreaseBits);
		} else {
			setOutputDigitalRange(predecessorElement.getSignalParameters().getDigitalRange());
			setOutputResolutionBits(predecessorElement.getSignalParameters().getSignalResolutionBits());
		}
		if (debug)
			System.out.println("FFT output range is " + getSignalParameters().getDigitalRange());
	}

	inputSize = predecessorElement.getSignalParameters().getVectorLength();
	outputSize = inputSize / 2;
	outReb = new int[outputSize];
	outImb = new int[outputSize];
	if (!reConnected || !imConnected) {
		zeroVector = new int[inputSize];
		for (int i = 0; i < inputSize; i++){
			zeroVector[i] = 0;
		}
	}

	fft = FFT.getFFT(type.getSelectedIndex(), inputSize, predecessorElement.getSignalParameters().getDigitalRange(), precisionIncreaseBits);
	
//	zeroLevel = predecessorElement.getSignalParameters().getDigitalRange() / 2;
	getSignalParameters().setPhysicalUnit("uV");
	getSignalParameters().setVectorUnit("Hz");

	maximumFrequency = predecessorElement.getSignalParameters().getVectorMax() / 2;
	outRe.getSignalParameters().setVectorMax(maximumFrequency);
	outIm.getSignalParameters().setVectorMax(maximumFrequency);
	outRe.setVectorLength(outputSize);
	outIm.setVectorLength(outputSize);

	getSignalParameters().setPhysMax(predecessorElement.getSignalParameters().getPhysRange() /2);
	getSignalParameters().setPhysMin(-predecessorElement.getSignalParameters().getPhysRange()/2);
	
	if (debug) {
		System.out.println("FFTTransform diagnostic info:");
		System.out.println("Input  length: " + inputSize);
		System.out.println("Output length: " + outputSize / 2);
		System.out.println("Max frequency: " + maximumFrequency);
	}
	
	super.reinit();
}
}
