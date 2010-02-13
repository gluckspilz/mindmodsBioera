/* VectorToComplex.java v 1.0.9   11/6/04 7:15 PM
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


public class VectorToComplex extends SingleElement {
//	public int inputLength;
	public ComboProperty function = new ComboProperty(new String[] {
		"TO_RE",
		"TO_IM",
		"TO_BOTH",
		});

	private final static String propertiesDescriptions[][] = {
		{"function", "Processing function", ""},
		
	};	

	private final static int TO_RE = 0;
	private final static int TO_IM = 1;
	private final static int TO_BOTH = 2;

	private int type = TO_RE;
	
	private BufferedVectorPipe in;
	private ComplexPipeDistributor out;
	private int inBuff[][], inVector[], vectorSize, complexSize, outVector[];

	protected static boolean debug = bioera.Debugger.get("vector.to.complex");
public VectorToComplex() {
	super();
	setName("V->C");
	inputs = new BufferedVectorPipe[1];
	inputs[0] = in = new BufferedVectorPipe(this);
	in.setName("IN");
	inBuff = in.getVBuffer();
	
	outputs = new ComplexPipeDistributor[1];
	outputs[0] = out = new ComplexPipeDistributor(this);
	out.setName("OUT");
}
public String getElementDescription() {
	return "Vector-to-Complex transforms";
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
		case TO_RE:
			for (int i = 0; i < n; i++){
				inVector = inBuff[i];
				System.arraycopy(inVector, 0, outVector, 0, complexSize);
				out.writeVector(outVector);
			}
			break;
		case TO_IM:
			for (int i = 0; i < n; i++){
				inVector = inBuff[i];
				System.arraycopy(inVector, 0, outVector, complexSize, complexSize);
				out.writeVector(outVector);
			}
			break;
		case TO_BOTH:
			for (int i = 0; i < n; i++){
				inVector = inBuff[i];
				System.arraycopy(inVector, 0, outVector, 0, complexSize);
				System.arraycopy(inVector, 0, outVector, complexSize, complexSize);
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
	
	complexSize = predecessorElement.getSignalParameters().getVectorLength();
	vectorSize = complexSize * 2;
	outVector = new int[vectorSize];
	setOutputVectorLength(vectorSize);
	
	//System.out.println("reinited: length=" + vp.getVectorLength() + "  minimum=" + minimum + "  vMin=" + vMin + "  vMax=" + vMax + " resolution=" + vp.getVectorResolution());
	
	super.reinit();
}
public void start()  throws Exception {
	for (int i = 0; i < vectorSize; i++){
		outVector[i] = 0;
	}
}
}
