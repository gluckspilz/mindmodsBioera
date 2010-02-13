/* ScalarValue.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.properties.*;
import bioera.storage.*;
import bioera.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class ScalarValue extends SingleElement {
	public int minValue = 1;
	public int maxValue = 10;
	public int initialValue = 5;
	public ComboProperty function = new ComboProperty(new String[] {
		"SUM MODULO",
		"SUM",
		});

	private final static String propertiesDescriptions[][] = {
		{"function", "Function", ""},
		{"minValue", "Minimum value", ""},
		{"maxValue", "Maximum value", ""},
		{"initialValue", "Initial value", ""},
	};	

	private final static int SUM_MODULO = 0;
	private final static int SUM = 1;

	private int type = SUM_MODULO;

	private ScalarPipeDistributor out;
	private BufferedScalarPipe in;
	private int inb[], value, range;

	protected static boolean debug = bioera.Debugger.get("impl.scalar.value");
/**
 * Element constructor comment.
 */
public ScalarValue() {
	super();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Converts input data according to chosen function";
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
public void initialize() {
	setName("S_Value");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	inb = in.getBuffer();
	out = (ScalarPipeDistributor) outputs[0];
	out.setName("OUT");	
}
/**
 * Element constructor comment.
 */
public final void process() {
	if (in.isEmpty())
		return;

	int n = in.available();

	switch (type) {
		case SUM_MODULO:
			for (int i = 0; i < n; i++){
				value = (value + range + inb[i] % range) % range;
				out.write(minValue + value);
			}
			break;
		case SUM:
			for (int i = 0; i < n; i++){
				value += inb[i];
				if (value > maxValue)
					value = maxValue;
				else if (value < minValue)
					value = minValue;
				out.write(value);
			}
			break;
	}

	in.purgeAll();
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

	if (maxValue < minValue)
		maxValue = minValue;

	if (initialValue < minValue)
		initialValue = minValue;
	if (initialValue > maxValue)
		initialValue = maxValue;
	
	switch (type) {
		case SUM_MODULO:
			range = maxValue - minValue + 1;
			// Value is kept from 0 to range
			value = initialValue - minValue;
			break;
		case SUM:
			value = initialValue;
			break;
	}
	
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
}
}
