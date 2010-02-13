/* RangeMapper.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.storage.*;
import bioera.*;
import bioera.properties.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class RangeMapper extends SingleElement {
	public FloatCompoundScale srcFrom;
	public FloatCompoundScale srcTo;

	public FloatCompoundScale destFrom;
	public FloatCompoundScale destTo;
		
	//public int srcFrom, srcTo;
//	public int destFrom, destTo;
	public boolean invertedOrder = false;

	private final static String propertiesDescriptions[][] = {
		{"cleanExcessiveData", "Clean excessive data in input buffer", ""},
		
	};	

	private int srcRange;
	private int destRange;
	private int sFrom, sTo, dFrom, dTo;
	
	private ScalarPipeDistributor out;
	private BufferedScalarPipe in;
	private int inbuffer[];
	private boolean inverted;
	
	protected static boolean debug = bioera.Debugger.get("impl.Scalar.range.mapper");
/**
 * Element constructor comment.
 */
public RangeMapper() {
	super();
	setName("Mapper");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	inbuffer = in.getBuffer();
	out = (ScalarPipeDistributor) outputs[0];
	out.setName("OUT");

	srcFrom = new FloatCompoundScale(0, new SmallVerticalScale(this));
	srcTo = new FloatCompoundScale(0, new SmallVerticalScale(this));
	destFrom = new FloatCompoundScale(0, new SmallVerticalScale(this));
	destTo = new FloatCompoundScale(0, new SmallVerticalScale(this));
	
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Converts value to another range e.g. in=4 inRange=1-8 outRange=10-13 -> out=11";
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
public final void process() {
	int n = in.available();
	if (n == 0)
		return;
		
	// No rate limitation - read everything
	if (inverted) {
		int k;
		for (int i = 0; i < n; i++){
			k = inbuffer[i];
			if (k > sTo)
				out.write(dFrom);
			else if (k < sFrom)
				out.write(dTo);
			else {
				out.write(dTo - (k - sFrom) * destRange / srcRange);
			}
		}				
	} else {
		int k;
		for (int i = 0; i < n; i++){
			k = inbuffer[i];
			if (k > sTo)
				out.write(dTo);
			else if (k < sFrom)
				out.write(dFrom);
			else {
				out.write((k - sFrom) * destRange / srcRange + dFrom);
			}
		}		
	}

	in.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (((SmallVerticalScale)srcFrom.scale).getSelectedIndex() == -1) {
		((SmallVerticalScale)srcFrom.scale).setSelectedItemThrow("digi");
	}

	if (((SmallVerticalScale)srcTo.scale).getSelectedIndex() == -1) {
		((SmallVerticalScale)srcTo.scale).setSelectedItemThrow("digi");
	}

	if (((SmallVerticalScale)destFrom.scale).getSelectedIndex() == -1) {
		((SmallVerticalScale)destFrom.scale).setSelectedItemThrow("digi");
	}

	if (((SmallVerticalScale)destTo.scale).getSelectedIndex() == -1) {
		((SmallVerticalScale)destTo.scale).setSelectedItemThrow("digi");
	}

	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	srcFrom.scale.update(this);
	double rangeRatio = srcFrom.scale.calculatePreciseRangeRatio(srcFrom.value);
	sFrom = (int)(rangeRatio * getSignalParameters().getDigitalRange()/2);// + getSignalParameters().getDigitalMin());
//System.out.println("rangeRatio="+rangeRatio);
	srcTo.scale.update(this);
	rangeRatio = srcTo.scale.calculatePreciseRangeRatio(srcTo.value);	
	sTo = (int)(rangeRatio * getSignalParameters().getDigitalRange()/2);//+ getSignalParameters().getDigitalMin());
//System.out.println("rangeRatio="+rangeRatio);
	destFrom.scale.update(this);
	rangeRatio = destFrom.scale.calculatePreciseRangeRatio(destFrom.value);	
	dFrom = (int)(rangeRatio * getSignalParameters().getDigitalRange()/2);//+ getSignalParameters().getDigitalMin());
//System.out.println("rangeRatio="+rangeRatio);
	destTo.scale.update(this);
	rangeRatio = destTo.scale.calculatePreciseRangeRatio(destTo.value);	
	dTo = (int)(rangeRatio * getSignalParameters().getDigitalRange()/2);//+ getSignalParameters().getDigitalMin());
//System.out.println("rangeRatio="+rangeRatio);
	inverted = invertedOrder;
	
	if (sTo < sFrom) {
		int a = sTo;
		sTo = sFrom;
		sFrom = a;
		inverted = !inverted;
	}

	if (dTo < dFrom) {
		int a = dTo;
		dTo = dFrom;
		dFrom = a;
		inverted = !inverted;
	}		
	
	//System.out.println("sFrom=" + sFrom + " sTo=" + sTo);
	//System.out.println("dFrom=" + dFrom + " dTo=" + dTo);
	
	//dTo = destTo;
	//dFrom = destFrom;
	
	srcRange = Math.abs(sTo - sFrom) + 1;
	destRange = Math.abs(dTo - dFrom) + 1;
	
	super.reinit();
}
}
