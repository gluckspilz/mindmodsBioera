/* ScalarsToVector.java v 1.0.9   11/6/04 7:15 PM
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

public final class ScalarsToVector extends SingleElement {
	public boolean fieldsDescriptionsFromNames = true;
	private final static String propertiesDescriptions[][] = {
	};	

	private VectorPipeDistributor out;
//	private int vMin, vMax, vSize;
	private int vectorLength = 0;
	private int outputVector[];
	private BufferedScalarPipe connectedInputs[];

	protected static boolean debug = bioera.Debugger.get("scalars.to.vector");
public ScalarsToVector() {
	super();
	setName("MS-V");
	outputs = new VectorPipeDistributor[getOutputsCount()];
	for (int i = 0; i < outputs.length; i++){
		outputs[i] = new VectorPipeDistributor(this);
	}
	out = (VectorPipeDistributor) outputs[0];
	out.setName("OUT");
}
public String getElementDescription() {
	return "Concatenates one or more linear streams into vector. Each stream's rate must be the same";
}
public int getInputsCount() {
	return 6;
}
public int getOutputsCount() {
	return 1;
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
	int n = Integer.MAX_VALUE;
	for (int i = 0; i < vectorLength; i++){
		if (connectedInputs[i].available() == 0) {
			return;
		} else {
			if (n > connectedInputs[i].available())
				n = connectedInputs[i].available();
		}
	}

	for (int i = 0; i < n; i++){
		for (int s = 0; s < vectorLength; s++){
			outputVector[s] = connectedInputs[s].getBuffer()[i];
		}

		out.writeVector(outputVector);
	}

	for (int s = 0; s < vectorLength; s++){
		connectedInputs[s].purge(n);
	}
}
public void reinit()  throws Exception {
	vectorLength = getInputsConnectedCount();
	outputVector = new int[vectorLength];
	out.setVectorLength(vectorLength);

	// Create list of connected inputs
	connectedInputs = new BufferedScalarPipe[vectorLength];
	int c = 0;
	if (fieldsDescriptionsFromNames) {
		String descr[] = new String[vectorLength];
		for (int i = 0; i < getInputsCount(); i++){
			BufferedScalarPipe p = (BufferedScalarPipe) inputs[i];
			if (p != null && p.isConnected()) {
				connectedInputs[c] = p;
				descr[c] = getFirstElementConnectedToInput(i).getName();
				c++;
			}
		}
		out.getSignalParameters().setVectorDescriptions(descr);
	}
	if (vectorLength > 0) {
		predecessorElement = connectedInputs[0].getConnectedDistributors()[0].getElement();
		if (!predecessorElement.isReinited()) {
			// If the first predecessor has yet been initialized
			// Return and this reinitialization will be repeated again
			return;
		}
		//System.out.println("Vector gatherer - predecesssor"+ predecessorElement.getClass() +"= " + predecessorElement.getSignalParameters().signalRate);		
	} else {
		System.out.println("Element not connected " + getName() + " (" + getClass() + ")");
		reinited = true;
		disactivate("Not connected");
		return;
	}

	//System.out.println("Signal rate in vector gatherer "+ getClass() +"= " + getSignalParameters().signalRate);
	out.setVectorLength(vectorLength);
	out.getSignalParameters().setVectorMin(0);
	out.getSignalParameters().setVectorMax(vectorLength);
//	System.out.println("from=" + vFrom + " vSize=" + vSize + " vMin=" + vMin + " vMax=" + vMax);
	super.reinit();
}
}
