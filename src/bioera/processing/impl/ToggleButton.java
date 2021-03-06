/* ToggleButton.java v 1.0.9   11/6/04 7:15 PM
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
public final class ToggleButton extends Display {
	public ComboProperty toggle = new ComboProperty(new String[] {
		"pressed",
		"released",
		});

	private static final int PRESSED = 0;
	private static final int RELEASED = 1;
	
	private boolean prevState = false;
	
	private final static String propertiesDescriptions[][] = {
	};	
	
	private LogicalPipeDistributor out;
	private ButtonChart chart;
/**
 * VectorDisplay constructor comment.
 */
public ToggleButton() {
	super();
}
/**
 * Element constructor comment.
 */
public Chart createChart() {
	return chart = new ButtonChart();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "User can generate interactive actions here";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 0;
}
/**
 * Element constructor comment.
 */
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
/**
 * Element constructor comment.
 */
public final void initialize() {
	setName("Toggle");

	outputs[0] = out = new LogicalPipeDistributor(this);
	
//	System.out.println("Scale=" + ProcessingTools.propertiesToString(scale));
}
/**
 * Element constructor comment.
 */
public final void process() {
	//int n = in.available();
	//if (n == 0)
		//return;	

	//System.out.println("v=" + scale.toScale(b[n-1]) + "  " + b[n-1]);
		
	//chart.pushText("" + scale.toScale(b[n-1]));

//	in.purgeAll();
	
	chart.repaint();	
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (toggle.getSelectedIndex() == -1)
		toggle.setSelectedIndex(PRESSED);

	super.reinit();
}
/**
 */
public void sendActionEvent(int t, Object o) {
	if (t == AEvent.BUTTON_PRESSED && (PRESSED == toggle.getSelectedIndex())) {
		out.write(!prevState);
		prevState = !prevState;
		chart.changeState(prevState);
	} else if (t == AEvent.BUTTON_RELEASED && (RELEASED == toggle.getSelectedIndex())) {
		out.write(!prevState);
		prevState = !prevState;
		chart.changeState(prevState);
	}

}
}
