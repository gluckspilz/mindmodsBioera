/* StatusML.java v 1.0.9   11/6/04 7:15 PM
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
public class StatusML extends Display {
	public int linesNo = 2;
	public boolean scrollDown = true;
	private final static String propertiesDescriptions[][] = {
		{"linesNo", "Lines", ""}
	};	
	
	private int inb[], last;
	private BufferedScalarPipe in;
	private TextMLChart chart;
	private StringBuffer text = new StringBuffer("");

	private String buf[];
/**
 * VectorDisplay constructor comment.
 */
public StatusML() {
	super();
}
/**
 * Element constructor comment.
 */
public Chart createChart() {
	return chart = new TextMLChart();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "A few lines of text can be displayed here";
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

	for (int i = 0; i < n; i++){
		if (inb[i] == '\n' || inb[i] == '\r') {
			if (scrollDown)
				buf[0] = text.toString();
			else
				buf[linesNo - 1] = text.toString();			
			scroll();
			text.setLength(0);
		} else
			text.append((char) inb[i]);
	}

	if (scrollDown)
		buf[0] = text.toString();
	else
		buf[linesNo - 1] = text.toString();

	//for (int i = 0; i < buf.length; i++){
		//System.out.println("b" + i + ": " + buf[i]);
	//}		
	chart.pushText(buf);
	chart.repaint();
	in.purgeAll();	
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	buf = new String[linesNo];
	for (int i = 0; i < linesNo; i++){
		buf[i] = "";
	}

	chart.setLineNo(linesNo);
	
	super.reinit();
}
/**
 * Element constructor comment.
 */
private void scroll() {
	if (scrollDown) {
		for (int i = linesNo - 1; i > 0; i--){
			buf[i] = buf[i-1];
		}
		buf[0] = "";
	} else {
		for (int i = 0; i < linesNo - 1; i++){
			buf[i] = buf[i+1];
		}
		buf[linesNo - 1] = "";
	}
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
}
}
