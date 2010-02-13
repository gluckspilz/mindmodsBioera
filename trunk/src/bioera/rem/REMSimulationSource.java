/* REMSimulationSource.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.rem;

import java.io.*;
import java.util.*;
import javax.comm.*;
import bioera.processing.*;
import bioera.rem.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class REMSimulationSource extends SourceElement {
	private long time = System.currentTimeMillis();

	private ScalarPipe out0;
	private int index = 0;
	private int counter2 = 0;

	boolean realityButtonPressed = false;
	boolean remTakingPlace = false;

	private int processingWhat = 0;
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 1;
}
/**
 * Element constructor comment.
 */
public final void process() throws Exception {
	if (realityButtonPressed) {
		if (processingWhat != 0) {
			processingWhat = 0;
			//System.out.println("Now reality button simulation");
		}
		realityButtonProcess();
	} else if (remTakingPlace) {
		if (processingWhat != 1) {
			processingWhat = 1;
			//System.out.println("Now rem simulation");
		}
		remButtonProcess();
	} else {
		if (processingWhat != 2) {
			processingWhat = 2;
			//System.out.println("Now main simulation");
		}
		
		int factor = 100;
		long now = System.currentTimeMillis();
		long n = (now - time) / factor;
		if (n == 0)
			return;
		time += n * factor;
		n = (int) (n * getSignalParameters().getSignalRate() * factor / 1000);
		while (n-- > 0) {
			out0.write(512);
		}
	}

		
	//int factor = 100;
	//long now = System.currentTimeMillis();
	//long n = (now - time) / factor;
	//if (n == 0)
		//return;
	//time += n * factor;
	//n = n * settings.signalRate * factor / 1000;
	//while (n-- > 0) {
		//index = (index + 1) % settings.signalRate;
		//if (index == 0)
			//counter2 ++;
	
		//int v =  512 + ((counter2 % 3) == 0 && index > 0 && index < 100? 250 : 1);
		
		//if (v < settings.signalSilcenceLevel - settings.signalAmplitude || v >= settings.signalSilcenceLevel + settings.signalAmplitude) {
			//System.out.println("sil " + settings.signalSilcenceLevel);
			//throw new Exception("Simulation out of bounds error: " + v + " (index=" + index + ")");
		//}

		//out0.write(v);
	//}
}
/**
 * Element constructor comment.
 */
public final void realityButtonProcess() throws Exception {
	int factor = 100;
	long now = System.currentTimeMillis();
	long n = (now - time) / factor;
	if (n == 0)
		return;
	time += n * factor;
	n = (int)(n * getSignalParameters().getSignalRate() * factor / 1000);
	while (n-- > 0) {
		out0.write(5);
	}
}
/**
 * Element constructor comment.
 */
public final void reinit() throws Exception {
	time = System.currentTimeMillis();

	super.reinit();
}
/**
 * Element constructor comment.
 */
public final void remButtonProcess() throws Exception {
	int factor = 100;
	long now = System.currentTimeMillis();
	long n = (now - time) / factor;
	if (n == 0)
		return;
	time += n * factor;
	n = (int)( n * getSignalParameters().getSignalRate() * factor / 1000);
	while (n-- > 0) {
		out0.write((int)(256 + 512 * (n % 2)));
	}
}

/**
 * SerialDeviceSource constructor comment.
 */
public REMSimulationSource() {
	super();
	out0 = (ScalarPipe) outputs[0];
	out0.setName("SINE");

//	new REMSimulatorFrame(this).show();
}
}
