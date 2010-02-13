/* Formatter.java v 1.0.9   11/6/04 7:15 PM
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
public final class Formatter extends SingleElement {
	public ComboProperty function = new ComboProperty(new String[] {
		"TO 2 Unsigned Little-Endian",
		"FROM 2 Unsigned Little-Endian",
		"TO 2 Signed Little-Endian",
		"FROM 2 Signed Little-Endian",
		"TO 1 Signed",
		"FROM 1 Signed",
		});
	
	private final static String propertiesDescriptions[][] = {
		{"function", "Convertion function", ""},
		
	};	

	public static final int TO_2_ULE = 0;
	public static final int FROM_2_ULE = 1;
	public static final int TO_2_SLE = 2;
	public static final int FROM_2_SLE = 3;
	public static final int TO_1_SIGNED = 4;
	public static final int FROM_1_SIGNED = 5;
	
	private int type = TO_2_ULE;
	
	private ScalarPipeDistributor out;
	private BufferedScalarPipe in;
	private int inb[];
	private int counter = 0;

	private static boolean debug = bioera.Debugger.get("impl.formatter");
/**
 * Element constructor comment.
 */
public Formatter() {
	super();
	setName("Formatter");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	inb = in.getBuffer();
	out = (ScalarPipeDistributor) outputs[0];
	out.setName("OUT");
	
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Converts input stream to another format stream";
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

	switch (type) {
		case TO_2_ULE:
			int b;
			for (int i = 0; i < n; i++) {
				b = inb[i] + 32768;
				out.write(b & 0xFF);
				out.write((b >> 8) & 0xFF);
			}
			in.purgeAll();
			break;
		case TO_2_SLE:
			for (int i = 0; i < n; i++) {
				b = (inb[i] + 32768) ^ 32768;
				out.write(b & 0xFF);
				out.write((b >> 8) & 0xFF);
			}
			in.purgeAll();
			break;
		case FROM_2_SLE:
			n = n - (n % 2);
			for (int i = 0; i < n; i+=2){
				b = inb[i] + (inb[i+1] << 8);
				b = (b ^ 32768) - 32768;
				out.write(b);
			}
			in.purge(n); 
			break;
		case FROM_2_ULE: 
			n = n - (n % 2);
			for (int i = 0; i < n; i+=2){
				b = inb[i] + (inb[i+1] << 8);
				out.write(b - 32768);
			}
			in.purge(n); 
			break;
		case TO_1_SIGNED:
			for (int i = 0; i < n; i++) {
				b = (inb[i] + 128) ^ 128;
				out.write(b);
			}
			in.purgeAll();
			break;
		case FROM_1_SIGNED:
			for (int i = 0; i < n; i++){
				b = (inb[i] ^ 128) - 128;
				out.write(b);
			}
			in.purge(n); 
			break;		
	}	
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (function.getSelectedIndex() == -1)
		function.setSelectedIndex(type);
	else
		type = function.getSelectedIndex();
	
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	switch(type) {
		case TO_2_SLE:
		case TO_2_ULE:
		case FROM_2_SLE:
		case FROM_2_ULE:
			getSignalParameters().setDigitalRange(1<<16);
			getSignalParameters().setPhysicalRange(1<<16);
			break;
			//zeroLevel = (1 << 15) - getSignalParameters().getDigitalRange() / 2;
		case TO_1_SIGNED:
		case FROM_1_SIGNED:
			getSignalParameters().setDigitalRange(1<<8);
			getSignalParameters().setPhysicalRange(1<<8);
			break;
		default:
			//zeroLevel = 0;
	}
	
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
	in.purgeAll();
}
}
