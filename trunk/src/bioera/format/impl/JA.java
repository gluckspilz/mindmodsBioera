/* JA.java v 1.0.9   11/6/04 7:15 PM
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

public class JA extends Element {
	public int rate = 256;
	public int range = 1024;
	public int uVrange = 512;
	public int channel = 0;
	private final static String propertiesDescriptions[][] = {
	};
	
	// Current packet counter
	private int packetCounter = -1;
	private int packetsLost = 0;
	private int processedPackets = 0;
	
	private BufferedScalarPipe in;
	private ScalarPipeDistributor out[];
	private int inb[];

	protected static boolean debug = bioera.Debugger.get("format.jm");
public JA() {
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
public String getElementDescription() {
	return "Translates stream from John Atkeson into data channels";
}
public int getInputsCount() {
	return 1;
}
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
public void initialize() {
}
public final void process() throws Exception {
	int n = in.available();
	if (n < 44)
		return;

//for (int i = 0; i < n; i++){
	//System.out.print(" " + (char) inb[i]);
//}
//if (1==1)
//return;
		
	StringBuffer line = new StringBuffer();
	int j;
	while (n > 46) {
		// Find EOL
		j = 0;
		line.setLength(0);
		while (j < n) {
			if (inb[j] != '\n')
				line.append((char) inb[j]);
			else {
				in.purge(j+1);
				n = in.available();
				break;
			}
			j++;
		}

		if (j == n)
			break;
		
		if (line.length() != 43) {
			System.out.println("Bad line: " + line);
			continue;
		}
		
		//System.out.println("line=" + line);
		
		if (line.length() == 0)
			throw new Exception("empty line");

		// Parse value
		String value = line.substring(4 + 5 * channel, 4 + 5 * (channel + 1)).trim();

		//System.out.println("to parse=" + value);
		
		int v = Integer.parseInt(value);
		
		out[0].write(v);
	}
}
public void reinit() throws Exception {
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
}
