/* VectorToScalar.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.graph.designer.*;
import bioera.fft.*;
import bioera.properties.*;


public final class VectorToScalar extends SingleElement {
	public ComboProperty function = new ComboProperty(new String[] {
		"AVERAGE",
		"MAX",
		"MIN",
		"MIDDLE_POWER",
		"MIDDLE_POWER_2",
		"SUM",
		"RMS",
		"RS",
		"DOMINANT_INDEX",
		"DIGITAL_AVERAGE",
		"DIGITAL_RMS",
		"STREAM",
		});

	private final static String propertiesDescriptions[][] = {
		{"function", "Processing function", ""},
		{"normalRes", "Normalize resolution", ""},		
	};	

	private final static int AVERAGE = 0;
	private final static int MAX = 1;
	private final static int MIN = 2;
	private final static int MIDDLE_POWER = 3;
	private final static int MIDDLE_POWER_2 = 4;
	private final static int SUM = 5;
	private final static int RMS = 6;
	private final static int RS = 7;
	private final static int DOMINANT_INDEX = 8;
	private final static int DIGI_AVER = 9;
	private final static int DIGI_RMS = 10;
	private final static int STREAM = 11;

	private int type = MAX;
		
	private BufferedVectorPipe in;
	private int buffer[][];
	private ScalarPipe out;
	private int vSize, div;

	protected static boolean debug = bioera.Debugger.get("impl.vector.to.scalar");
public VectorToScalar() {
	super();
	setName("V-to-S");
	inputs = new BufferedVectorPipe[1];
	inputs[0] = in = new BufferedVectorPipe(this);
	in.setName("IN");
	buffer = in.getVBuffer();
	out = (ScalarPipe) outputs[0];
	out.setName("OUT");
}
public String getElementDescription() {
	return "An avarage value of the vector is being written to output";
}
public int getInputsCount() {
	return 1;
}
public int getOutputsCount() {
	return 1;
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

	int t[], m;
	long m1;
	switch (type) {
		case RMS:
			for (int i = 0; i < n; i++){
				t = buffer[i];
				m1 = 0;
				for (int j = 0; j < vSize; j++){
					m1 += (t[j]*t[j]);
				}
				out.write(FFTTools.sqrt((m1 << 5) / div));
			}
			break;
		case MAX:
			for (int i = 0; i < n; i++){
				t = buffer[i];
				m = 0;
				for (int j = 0; j < vSize; j++){
					if (t[j] > m)
						m = t[j];
				}
				out.write(m);
			}
			break;
		case AVERAGE:
			for (int i = 0; i < n; i++){
				t = buffer[i];
				m1 = 0;
				for (int j = 0; j < vSize; j++){
					m1 += t[j];
				}
				out.write((int) ((m1 << 5) / div));
			}
			break;
		case RS:
			for (int i = 0; i < n; i++){
				t = buffer[i];
				m1 = 0;
				for (int j = 0; j < vSize; j++){
					m1 += (t[j]*t[j]);
				}
				out.write(FFTTools.sqrt(m1));
			}
			break;
		case SUM:
			for (int i = 0; i < n; i++){
				t = buffer[i];
				m = 0;
				for (int j = 0; j < vSize; j++){
					m += t[j];
				}
				out.write(m);
			}
			break;
		case MIN:
			for (int i = 0; i < n; i++){
				t = buffer[i];
				m = Integer.MAX_VALUE;
				for (int j = 0; j < vSize; j++){
					if (t[j] < m)
						m = t[j];
				}
				out.write(m);
			}
			break;
		case MIDDLE_POWER:
			for (int i = 0; i < n; i++){
				m = FFTTools.calculateMiddlePowerFreq(buffer[i], 0, vSize - 1);
				out.write(m);
			}
			break;
		case MIDDLE_POWER_2:
			for (int i = 0; i < n; i++){
				m = FFTTools.calculateMiddlePowerFreq2(in.getVBuffer()[i], 0, vSize - 1);
				out.write(m);
			}
			break;
		case DOMINANT_INDEX:
			for (int i = 0; i < n; i++){
				t = buffer[i];
				m = 0;
				for (int j = 1; j < vSize; j++){
					if (t[j] > t[m])
						m = j;
				}
				out.write(m);
			}
			break;
		case DIGI_RMS:
			for (int i = 0; i < n; i++){
				t = buffer[i];
				m1 = 0;
				for (int j = 0; j < vSize; j++){
					m1 += (t[j]*t[j]);
				}
				out.write(FFTTools.sqrt(m1 / vSize));
			}
			break;			
		case DIGI_AVER:
			for (int i = 0; i < n; i++){
				t = buffer[i];
				m1 = 0;
				for (int j = 0; j < vSize; j++){
					m1 += t[j];
				}
				out.write((int)(m1 / vSize));
			}
			break;
		case STREAM:
			for (int i = 0; i < n; i++){
				out.write(buffer[i], 0, vSize);
			}
			break;
	}

	in.reset();
}
public void reinit()  throws Exception {
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

	if (debug)
		System.out.println("Signal rate in vector averager "+ getClass() +"= " + getSignalParameters().getSignalRate());		

	PipeDistributor pd = getFirstDistributorConnectedToInput(0);
	if (pd == null || !(pd instanceof VectorPipe)) {
		throw new Exception("Element not connected properly");
	}

	VectorPipe vp = (VectorPipe) pd;
	//vMax = vp.getVectorMax();
	//vMin = vp.getVectorMin();
	vSize = vp.getSignalParameters().getVectorLength();
	double resolution = vp.getSignalParameters().getVectorResolution();
	if (resolution == Double.MAX_VALUE)
		throw new Exception("Max/Min vector ranges not set, so the resolution is infinity");
	div = (int) ((vSize<<5) / resolution);

	//System.out.println("size=" + vSize + " resDiv=" + divSize);

	if (vSize == 0)
		throw new DesignException("Input vector length must be greater then 0 (" + vSize + ")");
	
	setOutputVectorLength(vSize);
		
//	System.out.println("from=" + vFrom + " vSize=" + vSize + " vMin=" + vMin + " vMax=" + vMax);

	type = function.getSelectedIndex();
	getSignalParameters().setVectorUnit(in.getSignalParameters().getPhysicalUnit());

	if (type == STREAM)
		setOutputSignalRate(predecessorElement.getSignalParameters().getSignalRate() * vSize);

	super.reinit();
}
}
