/* StreamToVector.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.graph.designer.*;
import bioera.properties.*;

public final class StreamToVector extends SingleElement {
	public int vectorLength = 256;
	public float rate = 4;

	public ComboProperty function = new ComboProperty(new String[] {
		"FRAME",
		"TRIANGULAR",
		"HANNING",
		"HAMMING",
		"BLACKMAN",
	});

	private int type = 0;
	
	private final static String propertiesDescriptions[][] = {
		{"vectorLength", "Size", ""},
		{"rate", "Rate", ""},
		{"function", "Function", ""},		
	};	

	private final static int FRAME = 0;

	private VectorPipeDistributor out;
	private int outputVector[];
	private BufferedScalarPipe in;
	private int inbuffer[], period, windowing[], winDivider;

	protected static boolean debug = bioera.Debugger.get("stream.to.vector");
public StreamToVector() {
	super();
	setName("S-to-V");
	outputs = new VectorPipeDistributor[1];
	outputs[0] = out = new VectorPipeDistributor(this);
	out.setName("OUT");

	in = (BufferedScalarPipe) inputs[0];
	inbuffer = in.getBuffer();
}
public String getElementDescription() {
	return "Transformation from linear to vector";
}
/**
 */
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
public final void process() {
	if (in.isEmpty())
		return;

	int n = in.available() - vectorLength + period;
	
	if (n < period)
		return;

	n /= period;
		
	if (n == 0)
		return;

	int start = 0;
	switch (type) {
		case 0 :
			for (int f = 0; f < n; f++){
				System.arraycopy(inbuffer, start, outputVector, 0, vectorLength);
				out.writeVector(outputVector);
				start += period;
			}
			break;
		case Windowing.W_HAMMING:
		case Windowing.W_HANNING:
		case Windowing.W_BLACKMAN:
		case Windowing.W_TRIANGULAR:
			for (int f = 0; f < n; f++){
				for (int i = 0; i < vectorLength; i++){
					outputVector[i] = inbuffer[start + i] * windowing[i] / winDivider;
				}
				out.writeVector(outputVector);
				start += period;
			}
			break;
	}

	in.purge(start);	
}
public void reinit()  throws Exception {
	verifyDesignState(vectorLength > 0);
	verifyDesignState(rate > 0);

	if (function.getSelectedIndex() == -1)
		function.setSelectedIndex(type);
	else
		type = function.getSelectedIndex();
	
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	period = Math.round(predecessorElement.getSignalParameters().getSignalRate() / rate);
		
	outputVector = new int[vectorLength];

	out.getSignalParameters().setSignalRate(rate);
	out.setVectorLength(vectorLength);
	out.getSignalParameters().setVectorMax(predecessorElement.getSignalParameters().getSignalRate());
	out.getSignalParameters().setVectorMin(0);

	if (type > 0 && type < 5) {
		winDivider = (1 << 28) / getSignalParameters().getDigitalRange();
		windowing = new Windowing(type, vectorLength, winDivider).getPowerWindow();
	}

	//System.out.println("period=" + period);
		
	super.reinit();
}
}
