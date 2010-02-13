/* SubVector.java v 1.0.9   11/6/04 7:15 PM
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

public final class SubVector extends SingleElement {
	public float minimum = 0;
	public float maximum = 10;
	public int subVectorSize;
	public int startFreq;
	public int endFreq;

	private final static String propertiesDescriptions[][] = {
		{"minimum", "Low frequency  [Hz]", ""},
		{"maximum", "High frequency [Hz]", ""},
		{"subVectorSize", "Output vector size", "", "false"},
		{"startFreq", "Start index", "", "false"},
		{"endFreq", "End index", "", "false"},

	};
	
	private BufferedVectorPipe in;
	private VectorPipeDistributor out;
	private int inputBuffer[][], inputVector[], outputVector[], outputVectorSize, inputVectorFrom;

	protected static boolean debug = bioera.Debugger.get("subvector");
public SubVector() {
	super();
	setName("SubVector");
	inputs = new BufferedVectorPipe[1];
	inputs[0] = in = new BufferedVectorPipe(this);
	in.setName("IN");
	inputBuffer = in.getVBuffer();
	
	outputs = new VectorPipeDistributor[1];
	outputs[0] = out = new VectorPipeDistributor(this);
	out.setName("OUT");
}
public String getElementDescription() {
	return "Sub-vector is being written to output";
}
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
public final void process() {
	int n = in.available();
	if (n == 0)
		return;

	for (int i = 0; i < n; i++) {
		inputVector = inputBuffer[i];
		for (int j = 0; j < outputVectorSize; j++){
			outputVector[j] = inputVector[j + inputVectorFrom];
		}
		out.writeVector(outputVector);
		//System.out.print("[outputVectorFrom=" + inputVectorFrom + "-" + outputVectorBuffer.length);
		//for (int j = 0; j < outputVectorBuffer.length; j++){
			//System.out.print("," + outputVectorBuffer[j]);
		//}
		//System.out.println("]");
	}
	
	in.reset();	
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

	//System.out.println("Signal rate in vector filter "+ getClass() +"= " + getSignalParameters().signalRate);	

	PipeDistributor pd = getFirstDistributorConnectedToInput(0);
	if (pd == null || !(pd instanceof VectorPipe)) {
		throw new Exception("Element not connected properly");
	}

	VectorPipe vp = (VectorPipe) pd;
	float vMax = vp.getSignalParameters().getVectorMax();
	float vMin = vp.getSignalParameters().getVectorMin();
	int vSize = vp.getSignalParameters().getVectorLength();
	//System.out.println("size=" + vSize);
	//if (vMin > vMax)
		//throw new Exception("Input vector min>max");	

	if (minimum < vMin)
		minimum = vMin;
	if (maximum > vMax)
		maximum = vMax;
	if (minimum > maximum)
		minimum = maximum;
	
	outputVectorSize = (int) ((maximum - minimum) * vp.getSignalParameters().getVectorResolution());
	outputVector = new int[outputVectorSize];
	inputVectorFrom = (int) ((minimum - vMin) * vp.getSignalParameters().getVectorResolution());

	//System.out.println("outputVectorSize=" + outputVectorSize);
		
	out.setVectorLength(outputVectorSize);
	out.getSignalParameters().setVectorMax(maximum);
	out.getSignalParameters().setVectorMin(minimum);

	subVectorSize = outputVectorSize;
	startFreq = (int) (minimum * vp.getSignalParameters().getVectorResolution());
	endFreq = (int) (maximum * vp.getSignalParameters().getVectorResolution());
	
	//System.out.println("reinited: length=" + vp.getVectorLength() + "  minimum=" + minimum + "  vMin=" + vMin + "  vMax=" + vMax + " resolution=" + vp.getVectorResolution());
//System.out.println("param_sub: " + getSignalParameters());	
	super.reinit();
}
}
