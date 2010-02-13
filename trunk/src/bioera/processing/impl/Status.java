/* Status.java v 1.0.9   11/6/04 7:15 PM
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
public class Status extends Display {
	public ComboProperty type = new ComboProperty(new String[]{
		"DYNAMIC",
		"STATIC"
	});

	public static final int DYNAMIC = 0;
	public static final int STATIC = 1;

	private int typ = DYNAMIC;
	
	public String message[] = {""};

	private final static String propertiesDescriptions[][] = {
		{"message", "Messages", ""}
	};	
	
	private int inb[], last;
	private BufferedScalarPipe in;
	private TextChart chart;
	private StringBuffer text = new StringBuffer("");
/**
 * VectorDisplay constructor comment.
 */
public Status() {
	super();
}
/**
 * Element constructor comment.
 */
public Chart createChart() {
	return chart = new TextChart();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "The text message is displayed here";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
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
/**
 * Element constructor comment.
 */
public final void initialize() {
	setName("Status");
	inputs[0] = in = new BufferedScalarPipe(this);
	in.setName("IN");
	inb = in.getBuffer();
}
/**
 * Element constructor comment.
 */
public final void process() {
	int n = in.available();
	if (n == 0)
		return;	

	if (typ == DYNAMIC) {
		for (int i = 0; i < n; i++){
			if (inb[i] == '\n' || inb[i] == '\r')
				text.setLength(0);
			else
				text.append((char) inb[i]);
		}
		chart.pushText(text.toString());
	} else {
		n = inb[n-1];

		if (n != last) {
			last = n;
			if (n >= 0 && n < message.length)
				chart.pushText(message[n]);
			else
				chart.pushText("Out of range");
		}
	}

	chart.repaint();
	in.purgeAll();	
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (type.getSelectedIndex() == -1)
		type.setSelectedIndex(typ);
	else
		typ = type.getSelectedIndex();
	
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
	last = -1;
}
}
