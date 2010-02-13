/* JM.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.format.impl;

import bioera.processing.*;
import bioera.fft.*;

public class JM extends Element {
	public boolean jm2 = false;
	public int rate = 128;
	public int range = 256;
	private final static String propertiesDescriptions[][] = {
	};
	
	// Current packet counter
	private int packetCounter = -1;
	private int packetsLost = 0;
	private int processedPackets = 0;
	
	private BufferedScalarPipe in;
	private ScalarPipeDistributor out[];
	private int inb[];
	private int shift = 0;

	protected static boolean debug = bioera.Debugger.get("format.jm");
public String getElementDescription() {
	return "Translates stream from JMeissner into data channels";
}
public int getInputsCount() {
	return 1;
}
public int getOutputsCount() {
	return jm2 ? 2 : 4;
}
/**
 */
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
public void initialize() {
}
public final void process() throws Exception {
	int n = in.available();
	if (n == 0)
		return;

	int i;
	for (i = 0; i < n; i++){
		if (inb[i] != 3) {
			packetsLost++;
			continue;
		}
		if (jm2) {
			if (i + 2 >= n)
				break;
			out[0].write(inb[i++] + shift);
	        out[1].write(inb[i++] + shift);
		} else {
			if (i + 4 >= n)
				break;
			out[0].write(inb[i++] + shift);
	        out[1].write(inb[i++] + shift);			
			out[2].write(inb[i++] + shift);
	        out[3].write(inb[i++] + shift);			
		}
		
		packetCounter++;
	}

	in.purge(i);		
}
public void reinit() throws Exception {
	shift = getSignalParameters().getDigitalRange() / 2 - (range / 2);

	setOutputSignalRate(rate);
	setOutputDigitalRange(range);

	super.reinit();
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
public void start() throws Exception {
	packetCounter = 0;
}

public JM() {
	super();

	setName("JM");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	in.setBufferSize(1 << 12);

	inb = in.getBuffer();

	outputs = out = new ScalarPipeDistributor[getOutputsCount()];
	for (int i = 0; i < out.length; i++){
		out[i] = new ScalarPipeDistributor(this);
		out[i].setName("ch" + i);
	}
	
}
}
