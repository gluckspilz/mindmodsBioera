/* VectorTimeTransform.java v 1.0.9   11/6/04 7:15 PM
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


public final class VectorTimeTransform extends SingleElement {
	public int samplesNo = 4;
	public ComboProperty function = new ComboProperty(new String[] {
		"AVERAGE",
		"MAX",
		"MIN",
	});

	private final static String propertiesDescriptions[][] = {
		{"samplesNo", "Sample number", ""},
		{"function", "Transform", ""},		
	};	

	private final static int AVERAGE = 0;
	private final static int MAX = 1;
	private final static int MIN = 2;

	private int type = AVERAGE;

	private int tCount;				// Function is performed on this number of samples, until samplesNo is reached
	private BufferedVectorPipe in;
	private VectorPipeDistributor out;
	private int inBuffer[][], inVector[], outVector[], vectorSize;
	private long bufferVector[];

	protected static boolean debug = bioera.Debugger.get("vector.transform");
public VectorTimeTransform() {
	super();

	setName("V_Transf");
	inputs = new BufferedVectorPipe[1];
	inputs[0] = in = new BufferedVectorPipe(this);
	in.setName("IN");
	inBuffer = in.getVBuffer();
	
	outputs = new VectorPipeDistributor[1];
	outputs[0] = out = new VectorPipeDistributor(this);
	out.setName("OUT");	
	
}
public static final void addVector(int src[], int dest[], int len) {
	for (int i = 0; i < len; i++){
		dest[i] += src[i];
	}
}
public static final void addVector(int src[], long dest[], int len) {
	for (int i = 0; i < len; i++){
		dest[i] += src[i];
	}
}
public static final void copyVector(int src[], int dest[], int len) {
	for (int i = 0; i < len; i++){
		dest[i] = src[i];
	}
}
public static final void copyVector(int src[], long dest[], int len) {
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
public final void process() throws Exception {
	int n = in.available();
	if (n < tCount)
		return;

	int diff = n - tCount;

	if (tCount == samplesNo)
		diff ++;
		
	switch (type) {
		case AVERAGE:
			for (int di = 0; di < diff; di++){
				if (tCount < samplesNo) {
					tCount++;
					if (di != 0) {
						diff --;
						di = 0;
					}
				}
				copyVector(inBuffer[di], bufferVector, vectorSize);
				for (int k = 1; k < tCount; k++){
					addVector(inBuffer[di + k], bufferVector, vectorSize);
				}
				for (int i = 0; i < vectorSize; i++){
					outVector[i] = (int)(bufferVector[i] / tCount);
				}
				out.writeVector(outVector);					
			}
			break;
		case MAX:
			//System.out.println("inb: " + n + "  " + arrayToString(inBuffer));
			for (int di = 0; di < diff; di++){
				if (tCount < samplesNo) {
					tCount++;
					if (di != 0) {
						diff --;
						di = 0;
					}
				}
				//System.out.println("in: " + arrayToString(inBuffer[di]));
				copyVector(inBuffer[di], outVector, vectorSize);
				for (int k = 1; k < tCount; k++){
					inVector = inBuffer[di + k];
					for (int i = 0; i < vectorSize; i++){
						if (outVector[i] < inVector[i])
							outVector[i] = inVector[i];
					}
				}
				out.writeVector(outVector);					
			}
			break;
		case MIN:
			for (int di = 0; di < diff; di++){
				if (tCount < samplesNo) {
					tCount++;
					if (di != 0) {
						diff --;
						di = 0;
					}
				}
				copyVector(inBuffer[di], outVector, vectorSize);
				for (int k = 1; k < tCount; k++){
					inVector = inBuffer[di + k];
					for (int i = 0; i < vectorSize; i++){
						if (outVector[i] > inVector[i])
							outVector[i] = inVector[i];
					}
				}
				out.writeVector(outVector);					
				
				//for (int k = 0; k < tCount; k++){
					//inVector = inBuffer[k + i];
					//for (int j = 0; j < vectorSize; j++){
						//if (k == 0)
							//outVector[j] = inVector[j];  // assign for first
						//else						
							//outVector[j] = Math.min(inVector[j], outVector[j]);
					//}
				//}
				//out.writeVector(outVector);					
			}
			break;
		default:
			throw new Exception("Bad type: " + type);
	}

	if (n >= samplesNo) {
		in.purge(n - samplesNo + 1);
		//System.out.println("purged " + (n - samplesNo + 1));		
	}
	
	//System.out.println("afterPurge=" + ProcessingTools.arrayToString(inBuffer));
}
public void reinit()  throws Exception {
	verifyDesignState(samplesNo > 0);
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

	PipeDistributor pd = getFirstDistributorConnectedToInput(0);
	if (pd == null || !(pd instanceof VectorPipe)) {
		throw new Exception("Element not connected properly");
	}
	VectorPipe vp = (VectorPipe) pd;
	//System.out.println("before: " + ProcessingTools.arrayToString(vp.getSignalParameters().getVectorFieldDescriptions(), 4));
	//System.out.println("pred:" + predecessorElement.getName()); 
	//System.out.println("after:" + ProcessingTools.arrayToString(getSignalParameters().getVectorFieldDescriptions(), 4));
	vectorSize = vp.getSignalParameters().getVectorLength();
	outVector = new int[vectorSize];
	if (type == AVERAGE)
		bufferVector = new long[vectorSize];

//	setOutputVectorLength(vectorSize);
	//System.out.println("vect size=" + vectorSize + "  in " + getName());
	
	in.setBufferSize(samplesNo + bioera.DesignSettings.defaultVectorBufferLength);
	inBuffer = in.getVBuffer();
			
	super.reinit();
}
public void start()  throws Exception {
	tCount = 0;
}
}
