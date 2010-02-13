/* VectorInstantTransform.java v 1.0.9   11/6/04 7:15 PM
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


public final class VectorInstantTransform extends SingleElement {
	public int leftLength = 4, rightLength = 0;
	public ComboProperty function = new ComboProperty(new String[] {
		"AVERAGE",
		"MAX",
		"MIN",
		"RMS",
		"ABS",
		"HAMMING WINDOW",
	});

	private final static String propertiesDescriptions[][] = {
		{"leftLength", "Left", ""},
		{"rightLength", "Right", ""},
		{"function", "Transform", ""},		
	};	

	private final static int AVERAGE = 0;
	private final static int MAX = 1;
	private final static int MIN = 2;
	private final static int RMS = 3;
	private final static int ABS = 4;
	private final static int W_HAMM = 5;

	private int type = MAX;

	private BufferedVectorPipe in;
	private VectorPipeDistributor out;
	private int inBuffer[][], inVector[], outVector[], vectorSize, windowing[], winDivider;

	protected static boolean debug = bioera.Debugger.get("vector.transform");
public VectorInstantTransform() {
	super();
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
	setName("VS_Transf");
	inputs = new VectorPipe[1];
	inputs[0] = in = new BufferedVectorPipe(this);
	in.setName("IN");
	inBuffer = in.getVBuffer();
	
	outputs = new VectorPipe[1];
	outputs[0] = out = new VectorPipeDistributor(this);
	out.setName("OUT");
}
public final void process() throws Exception {
	int n = in.available();
	if (n == 0)
		return;

	switch (type) {
		case AVERAGE:
			for (int a = 0; a < n; a++){
				// Vector
				inVector = inBuffer[a];
				for (int s = 0; s < vectorSize; s++){
					int lPref = Math.min(s, leftLength);
					int lPost = Math.min(vectorSize - s - 1, rightLength);
					long scalarSum = 0;
					for (int i = s - lPref; i <= s + lPost; i++){
						scalarSum += inVector[i];
					}
					outVector[s] = (int)(scalarSum / (lPref + lPost + 1));
				}
				out.writeVector(outVector);
			}
			break;
		case MAX:
			for (int a = 0; a < n; a++){
				// Vector
				inVector = inBuffer[a];
				for (int s = 0; s < vectorSize; s++){
					int lPref = Math.min(s, leftLength);
					int lPost = Math.min(vectorSize - s - 1, rightLength);
					int scalarMax = inVector[s];
					for (int i = s - lPref; i <= s + lPost; i++){
						if (scalarMax < inVector[i])
							scalarMax = inVector[i];
					}
					outVector[s] = scalarMax;
				}
				out.writeVector(outVector);
			}
			break;
		case MIN:
			for (int a = 0; a < n; a++){
				// Vector
				inVector = inBuffer[a];
				for (int s = 0; s < vectorSize; s++){
					int lPref = Math.min(s, leftLength);
					int lPost = Math.min(vectorSize - s - 1, rightLength);
					int scalarMin = inVector[s];
					for (int i = s - lPref; i <= s + lPost; i++){
						if (scalarMin > inVector[i])
							scalarMin = inVector[i];
					}
					outVector[s] = scalarMin;
				}
				out.writeVector(outVector);
			}
			break;
		case RMS:
			for (int a = 0; a < n; a++){
				// Vector
				inVector = inBuffer[a];
				for (int s = 0; s < vectorSize; s++){
					int lPref = Math.min(s, leftLength);
					int lPost = Math.min(vectorSize - s - 1, rightLength);
					long scalarSum = 0;
					for (int i = s - lPref; i <= s + lPost; i++){
						scalarSum += (inVector[i] * inVector[i]);
					}
					outVector[s] = FFTTools.sqrt(scalarSum / (lPref + lPost + 1));
				}
				out.writeVector(outVector);
			}
			break;
		case ABS:
			for (int a = 0; a < n; a++){
				// Vector
				inVector = inBuffer[a];
				for (int i = 0; i < vectorSize; i++){
					outVector[i] = Math.abs(inVector[i]);
				}
				out.writeVector(outVector);
			}
			break;
		case W_HAMM:
			for (int f = 0; f < n; f++){
				inVector = inBuffer[f];
				for (int i = 0; i < vectorSize; i++){
					outVector[i] = (inVector[i] * windowing[i] / winDivider);
				}
				out.writeVector(outVector);
			}
			break;
		default:
			throw new Exception("Bad type: " + type);
	}

	in.purgeAll();	
}
public void reinit()  throws Exception {
	verifyDesignState(leftLength >= 0);
	verifyDesignState(rightLength >= 0);

	if (function.getSelectedIndex() == -1)
		function.setSelectedIndex(type);
	else
		type = function.getSelectedIndex();
	
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		throw new NotConnectedException();		
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}
	//System.out.println("Signal rate in vector filter "+ getClass() +"= " + getSignalParameters().signalRate);	

//	connectVectorsParametersInOut();
	
	PipeDistributor pd = getFirstDistributorConnectedToInput(0);
	if (pd == null || !(pd instanceof VectorPipe)) {
		throw new Exception("Element not connected properly");
	}

	VectorPipe vp = (VectorPipe) pd;
	vectorSize = vp.getSignalParameters().getVectorLength();
	outVector = new int[vectorSize];

	if (type == W_HAMM) {
		winDivider = (1 << 28) / getSignalParameters().getDigitalRange();
		windowing = new Windowing(Windowing.W_HAMMING, vectorSize, winDivider).getPowerWindow();
	}

		
	//System.out.println("reinited: length=" + vp.getVectorLength() + "  minimum=" + minimum + "  vMin=" + vMin + "  vMax=" + vMax + " resolution=" + vp.getVectorResolution());
	
	super.reinit();
}
public void start()  throws Exception {
}
}
