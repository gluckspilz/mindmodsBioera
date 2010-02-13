/* ComplexFFTTransform.java v 1.0.9   11/6/04 7:15 PM
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

public final class ComplexFFTTransform extends SingleElement {
	public float maximumFrequency = 64;
	public boolean subtractBias = true;
	public ComboProperty type = new ComboProperty(FFT.items);
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
	
	private BufferedComplexPipe in;
	private ComplexPipeDistributor out;
	private int inbuff[][], inb[];
	private int inRe[], inIm[], outRe[], outIm[];
	private int outVector[];
	private FFT fft;
	private int vectorSize, complexSize;

	protected static boolean debug = bioera.Debugger.get("transform.fft");
public ComplexFFTTransform() {
	super();
	setName("FFT");
	inputs = new BufferedComplexPipe[1];
	inputs[0] = in = new BufferedComplexPipe(this);
	inbuff = in.getVBuffer();
	in.setName("IN");
	outputs = new ComplexPipeDistributor[1];
	outputs[0] = out = new ComplexPipeDistributor(this);
	out.setName("FFT");
}
public String getElementDescription() throws Exception {
	return "Complex FFT transformation";
}
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
public final void process() {
	if (in.isEmpty())
		return;

	int n = in.available();

	for (int i = 0; i < n; i++){
		inb = inbuff[i];
		System.arraycopy(inb, 0, inRe, 0, complexSize);
		System.arraycopy(inb, complexSize, inIm, 0, complexSize);
		fft.performComplex(inRe, inIm, outRe, outIm);
		if (subtractBias) {
			outRe[0] = 0;
			outIm[0] = 0;
		}
		System.arraycopy(outRe, 0, outVector, 0, complexSize);
		System.arraycopy(outIm, 0, outVector, complexSize, complexSize);
		out.writeVector(outVector);
	}

	in.purgeAll();	
}
public void reinit() throws Exception {
	if (type.getSelectedIndex() == -1)
		type.setSelectedIndex(FFT.C_INTEGER);
			
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		// Nothing is connected to the input so no initiation is required
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}
	
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

	vectorSize = getSignalParameters().getVectorLength();
	complexSize = vectorSize / 2;
	outVector = new int[vectorSize];
	inRe = new int[complexSize];
	inIm = new int[complexSize];
	outRe = new int[complexSize];
	outIm = new int[complexSize];

	fft = FFT.getFFT(type.getSelectedIndex(), complexSize, predecessorElement.getSignalParameters().getDigitalRange(), precisionIncreaseBits);
	
	getSignalParameters().setPhysicalUnit("uV");
	getSignalParameters().setVectorUnit("Hz");

	maximumFrequency = predecessorElement.getSignalParameters().getVectorMax() / 2;
	out.getSignalParameters().setVectorMax(maximumFrequency);
	out.setVectorLength(complexSize);
	
	if (debug) {
		System.out.println("FFTTransform diagnostic info:");
		System.out.println("Input  length: " + complexSize);
		System.out.println("Output length: " + complexSize / 2);
		System.out.println("Max frequency: " + maximumFrequency);
	}
	
	super.reinit();
}
public void start() throws Exception {
}
}
