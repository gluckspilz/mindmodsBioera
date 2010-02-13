/* ScalarCrossTransform.java v 1.0.9   11/6/04 7:15 PM
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
import java.util.*;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.graph.designer.*;
import bioera.properties.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class ScalarCrossTransform extends SingleElement {
	public int inputLength = 1;
	public ComboProperty function = new ComboProperty(new String[] {
		"CORRELATION",
	});

	private final static String propertiesDescriptions[][] = {
		{"inputLength", "Samples number", ""},
		{"function", "Processing function", ""},
		
	};	
		
	private final static int CORRELATION = 0;
	
	private int type = CORRELATION;
	
	int bufA[], bufB[];
	private BufferedScalarPipe inA, inB;
	private ScalarPipeDistributor out;
	private int maxLevel, minLevel, range2, tCount;
/**
 * SignalDisplay constructor comment.
 */
public ScalarCrossTransform() {
	super();
	setName("Cross");
	inA = (BufferedScalarPipe)inputs[0];
	inA.setName("A");
	bufA = inA.getBuffer();
	inB = (BufferedScalarPipe)inputs[1];
	inB.setName("B");
	bufB = inB.getBuffer();
	out = (ScalarPipeDistributor) outputs[0];
	outputs[0].setName("O");	
}
/**
 * Element constructor comment.
 */
public final float corr(int shift) {
	float mean0 = bufA[shift];
	float mean1 = bufB[shift];
	for (int k = 1; k < tCount; k++){
		mean0 += bufA[k + shift];
		mean1 += bufB[k + shift];
	}
	mean0 /= tCount;
	mean1 /= tCount;

	float c = 0, v1 = 0, v2 = 0, d1, d2;
    for (int i = 0; i < tCount; i++)
    {
    	d1 = bufA[i + shift] - mean0;
        d2 = bufB[i + shift] - mean1;
        c += d1 * d2;
        v1 += d1 * d1;
        v2 += d2 * d2;
    }
    
    return (float) (c / Math.sqrt(v1 * v2));	
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Calculates two input signals according to chosen function";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 2;
}
/**
 * Element constructor comment.
 */
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
/**
 * Element constructor comment.
 */
public final void process() {
	int n = Math.min(inA.available(), inB.available());
	if (n < tCount)
		return;

	int diff = n - tCount;

	if (tCount == inputLength)
		diff ++;
	//System.out.println("c=" + tCount);
	//System.out.println("d=" + diff);
	//System.out.println("a=" + inA.available());
	//System.out.println("b=" + inB.available());
	switch (type) {
		case CORRELATION:
			for (int i = 0; i < diff; i++){
				if (tCount < inputLength) {
					tCount++;
					if (i != 0) {
						diff --;
						i = 0;
					}
				}
				out.write((int)((corr(i) * range2)));
			}
			break;
	}

	if (n >= inputLength) {
		inA.purge(n - inputLength + 1);
		inB.purge(n - inputLength + 1);
	}
}
/**
 * SignalDisplay constructor comment.
 */
public final void reinit() throws Exception {
	if (function.getSelectedIndex() == -1)
		function.setSelectedIndex(type);
	else
		type = function.getSelectedIndex();

	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		throw new DesignException("Input A must be connected");
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}
		
	Element predecessorElement1 = getFirstElementConnectedToInput(1);
	if (predecessorElement1 == null) {
		throw new DesignException("Input B must be connected");
	} else if (!predecessorElement1.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	maxLevel = getSignalParameters().getDigitalMax();
	minLevel = getSignalParameters().getDigitalMin();
	range2 = getSignalParameters().getDigitalRange() /2;

	out.getSignalParameters().setPhysMin(-1);
	out.getSignalParameters().setPhysMax(1);
	
	if (!out.isConnected()) {
		throw new DesignException("Output must be connected");
	}

	super.reinit();
}
/**
 * SignalDisplay constructor comment.
 */
public final void start() throws Exception {
	tCount = 0;
}
}
