/* ElementTestReader.java v 1.0.9   11/6/04 7:15 PM
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
public class ElementTestReader extends Element 
{
	Element src;
	private boolean debug = true;
public ElementTestReader(Element e) {
	super();
	setName("TestReader");
	src = e;
	if (e.getOutputsCount()  > 0 && e.getOutput(0).getType() == VectorPipeDistributor.TYPE)
		initVectorInputs();
	else {
		initScalarInputs();
	}
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	if (src == null)
		return 0;
	return src.getOutputsCount();
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 0;
}
public void initScalarInputs() {
	super.initScalarInputs();

	for (int i = 0; i < getInputsCount(); i++){
		Pipe p = getInput(i);
		if (p instanceof BufferedPipe) {
			((BufferedPipe)p).setBufferSize(ElementTest.SCALAR_BUFFER_SIZE);
		}
	}
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
	for (int i = 0; i < src.getOutputsCount(); i++){
		src.registerReceiver(src.getOutput(i), getInput(i));
		if (getInput(i).getType() == VectorPipe.TYPE) {
			VectorPipe in = (VectorPipe) getInput(i);
			VectorPipeDistributor out = (VectorPipeDistributor) src.getOutput(i);
			//in.getVectorParameters().setVectorLength(out.getVectorParameters().getVectorLength());
			//in.getVectorParameters().setPhysicalMax(out.getVectorParameters().getPhysicalMax());
			//in.getVectorParameters().setPhysicalMin(out.getVectorParameters().getPhysicalMin());
		}
		
	}
	super.reinit();
}
}
