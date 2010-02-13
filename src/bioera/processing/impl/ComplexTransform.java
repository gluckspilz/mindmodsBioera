/* ComplexTransform.java v 1.0.9   11/6/04 7:15 PM
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


public final class ComplexTransform extends SingleElement {
	public ComboProperty function = new ComboProperty(new String[] {
	});

	private final static String propertiesDescriptions[][] = {
		//{"samplesNo", "Sample number", ""},
		{"function", "Transform", ""},		
	};	


	private int type = 0;

	private BufferedComplexPipe in;
	private ComplexPipeDistributor out;
	private int inBuffer[][], inVector[], outVector[], vectorSize;

	protected static boolean debug = bioera.Debugger.get("complex.transform");
public ComplexTransform() {
	super();
}
public static final void addVector(int src[], int dest[], int len) {
	for (int i = 0; i < len; i++){
		dest[i] += src[i];
	}
}
public static final void copyVector(int src[], int dest[], int len) {
	for (int i = 0; i < len; i++){
		dest[i] = src[i];
	}
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
public void initialize() {
	setName("C_Transf");
	inputs = new VectorPipe[1];
	inputs[0] = in = new BufferedComplexPipe(this);
	in.setName("IN");
	inBuffer = in.getVBuffer();
	
	outputs = new VectorPipe[1];
	outputs[0] = out = new ComplexPipeDistributor(this);
	out.setName("OUT");	
}
public final void process() throws Exception {
	int n = in.available();
	if (n == 0)
		return;

	switch (type) {
		default:
			throw new Exception("Unknown function type: " + type);
	}
}
public void reinit()  throws Exception {
	if (function.getSelectedIndex() == -1)
		function.setSelectedIndex(type);
	else
		type = function.getSelectedIndex();

	//System.out.println("type=" + type);
		
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		throw new NotConnectedException();		
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}
	//System.out.println("Signal rate in vector filter "+ getClass() +"= " + getSignalParameters().signalRate);	

//	connectVectorsParametersInOut();
	vectorSize = in.getSignalParameters().getVectorLength();
	outVector = new int[vectorSize];
			
	//System.out.println("reinited: length=" + vp.getVectorLength() + "  minimum=" + minimum + "  vMin=" + vMin + "  vMax=" + vMax + " resolution=" + vp.getVectorResolution());
	
	super.reinit();
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
public void start()  throws Exception {

}
}
