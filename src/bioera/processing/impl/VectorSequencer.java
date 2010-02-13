/* VectorSequencer.java v 1.0.9   11/6/04 7:15 PM
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
import java.util.*;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.graph.designer.*;
import bioera.fft.*;
import bioera.graph.chart.*;
import bioera.properties.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class VectorSequencer extends SingleElement {
	public int values[][] = {{1,2,3,4}, {5,6,7,8}};

	public ComboProperty function = new ComboProperty(new String[] {
		"SEQUENCE",
//		"SUM",
		});

	public static final int SEQ = 0;
	public static final int SUM = 1;
	
	private int type = 0, sum, index, prev, inb[];
		
	public ComboProperty trigger = new ComboProperty(new String[] {
		"Any sample",
		"TRUE",
		"FALSE",
		"TRUE_THEN_FALSE",
		"FALSE_THEN_TRUE",
		});

	public static final int T_ANY = 0;
	public static final int T_TRUE = 1;
	public static final int T_FALSE = 2;
	public static final int T_TRUE_TO_FALSE = 3;
	public static final int T_FALSE_TO_TRUE = 4;
	
	private int tType = T_ANY;

	private final static String propertiesDescriptions[][] = {
		{"trigger", "Input trigger", ""},
		{"function", "Output value", ""}
	};
	
	private VectorPipeDistributor out;
	private BufferedLogicalPipe in;
/**
 * VectorDisplay constructor comment.
 */
public VectorSequencer() {
	super();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "User can generate interactive actions here";
}
/**
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
public final void initialize() {
	setName("V_Seq");

	initVectorOutputs();
	out = (VectorPipeDistributor) outputs[0];
	out.setName("O");
	inputs[0] = in = new BufferedLogicalPipe(this);
	in.setName("I");
	inb = in.getBuffer();
}
/**
 * Element constructor comment.
 */
public final void process() {
	if (in.isEmpty())
		return;

	int n = in.available();
	switch (tType) {
		case T_ANY:
			for (int i = 0; i < n; i++){
				send();
			}
			break;
		case T_TRUE:
			for (int i = 0; i < n; i++){
				if (inb[i] != 0)
					send();
			}
			break;
		case T_FALSE:
			for (int i = 0; i < n; i++){
				if (inb[i] == 0)
					send();
			}
			break;
		case T_TRUE_TO_FALSE:
			for (int i = 0; i < n; i++){
				if (inb[i] == 0 && prev != 0)
					send();
				prev = inb[i];
			}
			break;
		case T_FALSE_TO_TRUE:		
			for (int i = 0; i < n; i++){
				if (inb[i] != 0 && prev == 0)
					send();
				prev = inb[i];
			}
			break;
		default:
			break;
	}

	in.purgeAll();	
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (trigger.getSelectedIndex() == -1)
		trigger.setSelectedIndex(tType);
	else
		tType = trigger.getSelectedIndex();

	if (function.getSelectedIndex() == -1)
		function.setSelectedIndex(type);
	else
		type = function.getSelectedIndex();

	int len = 0;
	for (int i = 0; i < values.length; i++){
		if (values[i].length != len) {
			if (len != 0)
				throw new Exception("Each vector field's length must be the same");
			len = values[i].length;
		}
	}

	out.setVectorLength(len);
			
	super.reinit();
}
/**
 * Element constructor comment.
 */
public final void send() {
	switch (type) {
		case SEQ:
			out.writeVector(values[index]);
			break;
		default:
			return;
	}

	index = (index + 1) % values.length;
}
/**
 */
public void start() {
	prev = -1;
}
}
