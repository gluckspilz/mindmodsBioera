/* Sequencer.java v 1.0.9   11/6/04 7:15 PM
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
public final class Sequencer extends SingleElement {
	public int values[] = {0, 1, 2};

	public ComboProperty function = new ComboProperty(new String[] {
		"SEQUENCE",
		"SUM",
		"RANDOM ALL",
		"RANDOM ANY",
		});

	public static final int SEQ = 0;
	public static final int SUM = 1;
	public static final int RANDOM_ALL = 2;
	public static final int RANDOM_ANY = 3;
	
	private int type = SEQ, sum, index, prev, inb[];
		
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
	
	private ScalarPipeDistributor out;
	private BufferedLogicalPipe in;
	private long seed;
	private boolean availableRandomIndexes[];
	private int availableRandomIndexCount;
/**
 * VectorDisplay constructor comment.
 */
public Sequencer() {
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
	setName("Sequencer");

	outputs[0] = out = new ScalarPipeDistributor(this);
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

	seed = System.currentTimeMillis();

	if (type == RANDOM_ALL) {
		availableRandomIndexes = new boolean[values.length];
		availableRandomIndexCount = 0;
	}
		
	super.reinit();
}
/**
 * Element constructor comment.
 */
public final void send() {
	switch (type) {
		case SEQ:
			out.write(values[index]);
			index = (index + 1) % values.length;
			break;
		case SUM:
			sum += values[index];
			out.write(sum);
			index = (index + 1) % values.length;
			break;
		case RANDOM_ALL:
			if (availableRandomIndexCount == 0) {
				availableRandomIndexCount = values.length;
				for (int i = 0; i < availableRandomIndexCount; i++){
					availableRandomIndexes[i] = true;
				}
			}
			
			seed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL;

			// Get the random number withing available indexes
			index = (int) ((seed * availableRandomIndexCount) >> 48);

			int i = -1;
			while (index > 0) {
				if (availableRandomIndexes[++i])
					index--;
			}

			availableRandomIndexCount--;
			availableRandomIndexes[i] = false;
			out.write(values[i]);
			
			break;
		case RANDOM_ANY:
			seed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL;
			out.write(values[(int) ((seed * values.length) >> 48)]);
			break;
		default:
			return;
	}
}
/**
 */
public void start() {
	prev = -1;
}
}
