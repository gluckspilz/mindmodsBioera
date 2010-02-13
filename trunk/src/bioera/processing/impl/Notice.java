/* Notice.java v 1.0.9   11/6/04 7:15 PM
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
public final class Notice extends SingleElement {
	public String message[] = {""};
	public ComboProperty action = new ComboProperty(new String[]{
		"STOP PROCESSING",
		"PAUSE PROCESSING",
		"CONTINUE PROCESSING"
	});

	public static final int P_STOP = 0;
	public static final int P_PAUSE = 1;
	public static final int P_CONTINUE = 2;	
	
	private int type = P_STOP;
	
	private final static String propertiesDescriptions[][] = {
	};	
	
	private int inb[], last = -1;
	private BufferedScalarPipe in;
/**
 * VectorDisplay constructor comment.
 */
public Notice() {
	super();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "The text is shown upon positive input state";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 1;
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 0;
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
	setName("Notice");
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

	n = inb[n-1];

	if (n != last) {
		last = n;
		if (n >= 0 && n < message.length) {
			if (message[n] != null && message[n].length() > 0) {
				// show dialog
				MessageDialog d = new MessageDialog(bioera.Main.app.runtimeFrame.getFrame(), "Notice");
				if (type == P_PAUSE)
					d.setModal(true);
				else if (type == P_STOP) {
					bioera.Main.app.processor.setSignal(bioera.Main.app.processor.SIGNAL_STOP);
					d.setModal(true);
				} else
					d.setModal(false);

				d.show(message[n]);
					
				System.out.println("Dialog: " + message[n]);
			}
		}
	}

	in.purgeAll();	
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (action.getSelectedIndex() != -1)
		type = action.getSelectedIndex();
	else
		action.setSelectedIndex(type);
	
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
	last = -1;
}
}
