/* TestVectorSimulationSource.java v 1.0.9   11/6/04 7:15 PM
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
import javax.comm.*;
import bioera.processing.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class TestVectorSimulationSource extends SourceElement {
	public final static double PI2 = 2 * Math.PI;

	public int rate = 1;
	public int vectorLength = 2;
	public int vectorMin = 10;
	public int vectorMax = 50;

	
	private long startTime;
	private int index = 0;
	private int counter = 0;
	
	private int v[];
		
	private VectorPipeDistributor out;
/**
 * SerialDeviceSource constructor comment.
 */
public TestVectorSimulationSource() {
	super();
	outputs = new VectorPipeDistributor[1];
	outputs[0] = out = new VectorPipeDistributor(this);
	out.setName("Out");
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Test source-code based simulator";
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 1;
}
/**
 * Element constructor comment.
 */
public void process() throws Exception {
	int diff = (int) (mainProcessingTime - startTime);

	int expectedCount = (rate * diff) / 1000;

	for (int i = 0; i < expectedCount - counter; i++){
		for (int j = 0; j < v.length; j++){
//			v[j] = (int) (250 + 250*Math.sin(2 * Math.PI * j/v.length));
			v[j] = (int) (10 * Math.random());
//			v[j] = 0(int) (512 * Math.random());
//			v[j] = (j > 100 && j < 150 ? ((counter + i) % 20 * 512 / 20) : 0);
//			v[j] = ((counter + i + j) % 300 * 200 / 300);
		}
		//v[(int)(v.length * Math.random())] = (int) (512 * Math.random());
		//v[(int)(v.length * Math.random())] = (int) (512 * Math.random());
		out.writeVector(v);
	}

	counter = expectedCount;
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	v = new int[vectorLength];
	out.setVectorLength(v.length);
	out.getSignalParameters().setVectorMin(vectorMin);
	out.getSignalParameters().setVectorMax(vectorMax);
	setOutputSignalRate(rate);
	
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start() throws Exception {
	startTime = System.currentTimeMillis();	
}
}
