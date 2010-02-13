/* ScalarTimeTransform.java v 1.0.9   11/6/04 7:15 PM
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

import java.io.*;
import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.properties.*;
import bioera.storage.*;
import bioera.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class ScalarTimeTransform extends SingleElement {
	public int inputLength = 1;
	public ComboProperty function = new ComboProperty(new String[] {
		"AVERAGE",
		"MAX",
		"MIN",
		"PEAK_TO_PEAK",
		});

	private final static String propertiesDescriptions[][] = {
		{"inputLength", "Samples number", ""},
		{"function", "Processing function", ""},
		
	};	

	private final static int AVERAGE = 0;
	private final static int MAX = 1;
	private final static int MIN = 2;
	private final static int PEAK_TO_PEAK = 3;

	private int type = AVERAGE;

	private int tCount;				// Function is performed on this number of samples, until inputLength is reached
		
	private ScalarPipeDistributor out;
	private BufferedScalarPipe in;
	private int inbuffer[];

	protected static boolean debug = bioera.Debugger.get("impl.scalar.transform");
/**
 * Element constructor comment.
 */
public ScalarTimeTransform() {
	super();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Converts input data according to chosen function";
}
/**
 * Element constructor comment.
 */
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
/**
 * Element constructor comment.
 */
public void initialize() {
	setName("S_Transf");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	inbuffer = in.getBuffer();
	out = (ScalarPipeDistributor) outputs[0];
	out.setName("OUT");	
}
/**
 * Element constructor comment.
 */
public final void process() {
	int n = in.available();
	if (n < tCount)
		return;

	int diff = n - tCount;

	if (tCount == inputLength)
		diff ++;

	switch (type) {
		case AVERAGE:
			long sum;
			for (int i = 0; i < diff; i++){
				if (tCount < inputLength) {
					tCount++;
					if (i != 0) {
						diff --;
						i = 0;
					}
				}
				sum = inbuffer[i];
				for (int k = 1; k < tCount; k++){
					sum += inbuffer[k + i];		
				}
				out.write((int)(sum / tCount));
			}
			break;
		case MAX:
			int max;
			for (int i = 0; i < diff; i++){
				if (tCount < inputLength) {
					tCount++;
					if (i != 0) {
						diff --;
						i = 0;
					}
				}
				max = inbuffer[i];
				for (int k = 1; k < tCount; k++){
					if (inbuffer[k + i] > max)
						max = inbuffer[k + i];
				}
				out.write(max);
			}
			break;
		case MIN:
			int min;
			for (int i = 0; i < diff; i++){
				if (tCount < inputLength) {
					tCount++;
					if (i != 0) {
						diff --;
						i = 0;
					}
				}
				min = inbuffer[i];
				for (int k = 1; k < tCount; k++){
					if (inbuffer[k + i] < min)
						min = inbuffer[k + i];
				}
				out.write(min);
			}
			break;
		case PEAK_TO_PEAK:
			for (int i = 0; i < diff; i++){
				if (tCount < inputLength) {
					tCount++;
					if (i != 0) {
						diff --;
						i = 0;
					}
				}				
				min = max = inbuffer[i];
				for (int k = 1; k < tCount; k++){
					if (inbuffer[k + i] > max)
						max = inbuffer[k + i];
					if (inbuffer[k + i] < min)
						min = inbuffer[k + i];
				}
				out.write(max - min);
			}
			break;
	}

	if (n >= inputLength)
		in.purge(n - inputLength + 1);
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (function.getSelectedIndex() == -1)
		function.setSelectedIndex(type);
	else
		type = function.getSelectedIndex();

	verifyDesignState(inputLength > 0);
		
	//predecessorElement = getFirstElementConnectedToInput(0);
	//if (predecessorElement == null) {
		//reinited = true;
		//return;
	//} else if (!predecessorElement.isReinited()) {
		//// Wait until the preceding element is inited
		//return;
	//}
	
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
	tCount = 0;
}
}
