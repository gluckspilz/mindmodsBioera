/* TestSimulationSource.java v 1.0.9   11/6/04 7:15 PM
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
public class TestSimulationSource extends SourceElement {
	public final static double PI2 = 2 * Math.PI;
	public int rate = 256;

	private long time = System.currentTimeMillis();
	private int index = 0;
	private int counter2 = 0;

	
//	p int frequency = 16;
//	public int phaseShift_0_360 = 0;
	private int amplitude = 100;
//	public int constant = 512;


	private ScalarPipe out0;	
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Test software-simulator";
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
	double freq[] = {9, 17, 36};//, 24, 32, 26};
//	double freq[] = {5, 6.5, 9.55, 13.5, 17.8, 20.5};
	long now = System.currentTimeMillis();
	// 32 times per 125ms
	long n = (now - time) / 125;
	time += n * 125;
	n *= (32 * rate / 256);
	while (n-- > 0) {
		index = (index + 1) % rate;

		int i = (int) (System.currentTimeMillis() / 1000) % (freq.length * 4);
		i /= 4;
		int v =  512 + (int) (510.0 * Math.sin(freq[i] * PI2 * index / rate));	

		
		if (v < 0 || v >= getSignalParameters().getDigitalRange()) {
			System.out.println("sil " + getSignalParameters().getDigitalRange() / 2);
			System.out.println("amp " + amplitude);		
			throw new Exception("Simulation out of bounds error: " + v + " (index=" + index + ")");
		}

		out0.write(v);
	}
}
/**
 * Element constructor comment.
 */
public void process1() throws Exception {
/*	
	double freq[] = {3, 0, 4, 0};//, 24, 32, 26};
//	double freq[] = {5, 6.5, 9.55, 13.5, 17.8, 20.5};
	long now = System.currentTimeMillis();
	// 32 times per 125ms
	long n = (now - time) / 125;
	time += n * 125;
	n *= 32;
	while (n-- > 0) {
		index = (index + 1) % getSignalParameters().signalRate;

		int i = (int) (System.currentTimeMillis() / 1000) % (freq.length * 4);
		i /= 4;
		int v =  512 + (int) (510.0 * Math.sin(freq[i] * PI2 * index / 256) / (freq.length));	

		
		//int v = getSignalParameters().zeroLevel 
			//+ (int) (amplitude * Math.sin(frequency * PI2 * index / getSignalParameters().rate) / 2)
			//+ (int) (amplitude * Math.sin((frequency+4) * PI2 * index / getSignalParameters().rate) / 2)
			//;
		if (v < getSignalParameters().signalSilcenceLevel - getSignalParameters().signalAmplitude || v >= getSignalParameters().signalSilcenceLevel + getSignalParameters().signalAmplitude) {
			System.out.println("sil " + getSignalParameters().signalSilcenceLevel);
			System.out.println("amp " + amplitude);		
			throw new Exception("Simulation out of bounds error: " + v + " (index=" + index + ")");
		}

		out0.write(v);
	}
*/	
}
/**
 * Element constructor comment.
 */
public void process2() throws Exception {
/*	
	double freq[] = {18, 36, 4, 0};//, 24, 32, 26};
	long now = System.currentTimeMillis();
	// 32 times per 125ms
	long n = (now - time) / 125;
	time += n * 125;
	n *= 32;
	while (n-- > 0) {
		index = (index + 1) % getSignalParameters().signalRate;
		if (index == 0)
			counter2 ++;
	
		int v =  512 + (int) (510.0 * Math.sin(freq[(counter2/4) % 2] * PI2 * index / 256));	
		
		if (v < getSignalParameters().signalSilcenceLevel - getSignalParameters().signalAmplitude || v >= getSignalParameters().signalSilcenceLevel + getSignalParameters().signalAmplitude) {
			System.out.println("sil " + getSignalParameters().signalSilcenceLevel);
			System.out.println("amp " + amplitude);		
			throw new Exception("Simulation out of bounds error: " + v + " (index=" + index + ")");
		}

		out0.write(v);
	}
*/	
}
/**
 * Element constructor comment.
 */
public final void process3() throws Exception {
/*	
	int factor = 100;
	long now = System.currentTimeMillis();
	long n = (now - time) / factor;
	if (n == 0)
		return;
	time += n * factor;
	n = n * getSignalParameters().signalRate * factor / 1000;
	while (n-- > 0) {
		index = (index + 1) % getSignalParameters().signalRate;
		if (index == 0)
			counter2 ++;
	
		int v =  512 + (int) (510.0 * Math.sin(PI2 * index / getSignalParameters().signalRate));	
		
		if (v < getSignalParameters().signalSilcenceLevel - getSignalParameters().signalAmplitude || v >= getSignalParameters().signalSilcenceLevel + getSignalParameters().signalAmplitude) {
			System.out.println("sil " + getSignalParameters().signalSilcenceLevel);
			System.out.println("amp " + amplitude);		
			throw new Exception("Simulation out of bounds error: " + v + " (index=" + index + ")");
		}

		out0.write(v);
	}
*/	
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	setOutputSignalRate(rate);
	super.reinit();
}

/**
 * SerialDeviceSource constructor comment.
 */
public TestSimulationSource() {
	super();
	out0 = (ScalarPipe) outputs[0];
	out0.setName("SINE");
}
}
