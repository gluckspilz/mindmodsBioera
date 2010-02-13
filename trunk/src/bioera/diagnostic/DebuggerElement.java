/* DebuggerElement.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.diagnostic;

import java.io.*;
import java.util.*;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.graph.chart.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class DebuggerElement extends SingleElement {
	public int printPeriod;
	public int maxPrintedCount = -1;
	public boolean printAscii;
	public int inputBufferSize = 1000;

	private long lastTime = 1;
	
	int inb[];
	int counter;
	private BufferedScalarPipe in0;

	private final static String propertiesDescriptions[][] = {
		{"printPeriod", "Print period [s]", ""},				
	};
	
/**
 * SignalDisplay constructor comment.
 */
public DebuggerElement() {
	super();
	setName("Debugger");
	in0 = (BufferedScalarPipe)inputs[0];
	in0.setName("0");
	inb = in0.getBuffer();
	name = getName();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Simply prints to console all received values";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 1;
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 0;
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
 * SignalDisplay constructor comment.
 */
public String printAdvancedDetails() throws Exception {
	return super.printAdvancedDetails() ;
}
/**
 * Element constructor comment.
 */
public final void process() {
	int n = in0.available();
	if (n == 0)
		return;

	if (maxPrintedCount != -1 && n > maxPrintedCount)
		n = maxPrintedCount;
	//System.out.println("in0=" + in0.getId() + "  " + inb[0]);
	if (mainProcessingTime - lastTime > (printPeriod * 1000)) {
		lastTime = mainProcessingTime;
		if (n == 1) {
			if (printAscii)
				System.out.println(getName() + "(" + n + "): " + (char)inb[0]);
			else
				System.out.println(getName() + "(" + n + "): " + inb[0]);
		} else {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < n; i++){
				if (i > 0)
					sb.append(", ");
				if (printAscii)
					sb.append((char)inb[i]);
				else
					sb.append(inb[i]);
			}
			System.out.println(getName() + "(" + n + "): " + sb);
		}
		
		in0.purgeAll();
	}
}
/**
 * SignalDisplay constructor comment.
 */
public void reinit() throws Exception {
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		disactivate("Not connected");
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	if (inputBufferSize > -1)
		in0.setBufferSize(inputBufferSize);
	else
		in0.setBufferSize(bioera.DesignSettings.defaultScalarBufferLength);

	inb = in0.getBuffer();
			
	System.out.println("---------------- debug --------------------");
	ProcessingTools.printSignalParameters(predecessorElement);
	System.out.println("-------------------------------------------");

	
	//System.out.println("Debug: Predecessor: " + predecessorElement.getName());
	//System.out.println("rate      : " + predecessorElement.getSignalParameters().getSignalRate());
	//System.out.println("digi range: " + predecessorElement.getSignalParameters().getDigitalRange());
	//System.out.println("phys range: " + predecessorElement.getSignalParameters().getPhysRange());
	//System.out.println("bits      : " + predecessorElement.getSignalParameters().getSignalResolutionBits());
	
	super.reinit();
}
}
