/* VectorToVector.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.tests;

import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.properties.*;


public class VectorToVector extends SingleElement {
//	public int inputLength;
	public ComboProperty function = new ComboProperty(new String[] {
//		"FFT"
		});

	private final static String propertiesDescriptions[][] = {
		{"function", "Processing function", ""},
		
	};	

	private final static int FFT = 1;

	private int type = FFT;
	
	private BufferedVectorPipe in;
	private VectorPipeDistributor out;
	private int inBuffer[][], inVector[], sum[], vectorSize;

	protected static boolean debug = bioera.Debugger.get("vector.to.vector");
public String getElementDescription() {
	return "Various vector-to-vector transforms";
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
		
	switch (type) {
		case FFT:
			
			break;
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

	//PipeDistributor pd = getFirstDistributorConnectedToInput(0);
	//if (pd == null || !(pd instanceof VectorPipe)) {
		//throw new Exception("Element not connected properly");
	//}
	//VectorPipe vp = (VectorPipe) pd;
	//out.setVectorLength(vp.getVectorLength());
	//out.setPhysicalMax(vp.getPhysicalMax());
	//out.setPhysicalMin(vp.getPhysicalMin());
	vectorSize = in.getSignalParameters().getVectorLength();
	sum = new int[vectorSize];
	
	//System.out.println("reinited: length=" + vp.getVectorLength() + "  minimum=" + minimum + "  vMin=" + vMin + "  vMax=" + vMax + " resolution=" + vp.getVectorResolution());
	
	super.reinit();
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}

public VectorToVector() {
	super();
	setName("V-to-V");
	inputs = new BufferedVectorPipe[1];
	inputs[0] = in = new BufferedVectorPipe(this);
	in.setName("IN");
	inBuffer = in.getVBuffer();
	
	outputs = new VectorPipeDistributor[1];
	outputs[0] = out = new VectorPipeDistributor(this);
	out.setName("OUT");
}
}
