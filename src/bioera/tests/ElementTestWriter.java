/* ElementTestWriter.java v 1.0.9   11/6/04 7:15 PM
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

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;
import bioera.processing.*;
import bioera.processing.impl.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class ElementTestWriter extends Element 
{
	Element dest;
	private boolean debug = true;
public ElementTestWriter(Element e) {
	super();
	setName("TestWriter");	
	dest = e;
	if (e.getInputsCount()  > 0 && e.getInput(0).getType() == bioera.processing.VectorPipe.TYPE)
		initVectorOutputs();
	else
		initScalarOutputs();	
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 0;
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	if (dest == null)
		return 0;
	return dest.getInputsCount();
}
/**
 * Element constructor comment.
 */
public void process() throws java.lang.Exception {
	
}
/**
 * Element constructor comment.
 */
public void reinit() throws java.lang.Exception {
	for (int i = 0; i < dest.getInputsCount(); i++){
		registerReceiver(getOutput(i), dest.getInput(i));
		if (dest.getInput(i).getType() == VectorPipe.TYPE) {
			VectorPipe in = (VectorPipe) dest.getInput(i);
			VectorPipeDistributor out = (VectorPipeDistributor) getOutput(i);
			out.setVectorLength(in.getSignalParameters().getVectorLength());
			//out.setPhysicalMax(in.getPhysicalMax());
			//out.setPhysicalMin(in.getPhysicalMin());
		}
	}

	super.reinit();
}
}
