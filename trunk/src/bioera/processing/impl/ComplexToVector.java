/* ComplexToVector.java v 1.0.9   11/6/04 7:15 PM
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


public class ComplexToVector extends SingleElement {
//	public int inputLength;
	public ComboProperty function = new ComboProperty(new String[] {
		"RE",
		"IM",
		"RS",
	});

	private final static String propertiesDescriptions[][] = {
		{"function", "Processing function", ""},
		
	};	

	private final static int COPY_RE = 0;
	private final static int COPY_IM = 1;
	private final static int SQRT = 2;

	private int type = COPY_RE;
	
	private BufferedComplexPipe in;
	private VectorPipeDistributor out;
	private int inBuff[][], inVector[], inputSize, outputSize, outVector[];

	protected static boolean debug = bioera.Debugger.get("complex.to.vector");
public ComplexToVector() {
	super();
	setName("C-to-V");
	inputs = new BufferedComplexPipe[1];
	inputs[0] = in = new BufferedComplexPipe(this);
	in.setName("IN");
	inBuff = in.getVBuffer();
	
	outputs = new VectorPipeDistributor[1];
	outputs[0] = out = new VectorPipeDistributor(this);
	out.setName("OUT");
}
public String getElementDescription() {
	return "Complex-to-Vector transforms";
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
		
	switch (type) {
		case COPY_RE:
			for (int i = 0; i < n; i++){
				inVector = inBuff[i];
				System.arraycopy(inVector, 0, outVector, 0, outputSize);
				out.writeVector(outVector);
			}
			break;
		case COPY_IM:
			for (int i = 0; i < n; i++){
				inVector = inBuff[i];
				System.arraycopy(inVector, outputSize, outVector, 0, outputSize);
				out.writeVector(outVector);
			}
			break;
		case SQRT:
			for (int i = 0; i < n; i++){
				inVector = inBuff[i];
				for (int j = 0; j < outputSize; j++){
					outVector[j] = FFTTools.sqrt(inVector[j] * inVector[j] + inVector[j + outputSize] * inVector[j + outputSize]);
				}
				out.writeVector(outVector);
			}
			break;
	}

	in.purgeAll();
}
public void reinit()  throws Exception {
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}
	
	inputSize = predecessorElement.getSignalParameters().getVectorLength();
	outputSize = inputSize / 2;
	outVector = new int[outputSize];
	setOutputVectorLength(outputSize);
	
	//System.out.println("reinited: length=" + vp.getVectorLength() + "  minimum=" + minimum + "  vMin=" + vMin + "  vMax=" + vMax + " resolution=" + vp.getVectorResolution());
	
	super.reinit();
}
public void start()  throws Exception {
	for (int i = 0; i < outputSize; i++){
		outVector[i] = 0;
	}
}
}
