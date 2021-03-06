/* InteractiveScalarSource.java v 1.0.9   11/6/04 7:15 PM
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
public final class InteractiveScalarSource extends Display {
	public int pressValues[] = {0, 1, 2};
	public int releaseValues[] = {};

	public ComboProperty function = new ComboProperty(new String[] {
		"SEQUENCE",
		"SUM",
		});

	public static final int SEQ = 0;
	public static final int SUM = 1;
	
	private int type = 0, sum;
		
	public ComboProperty onPress = new ComboProperty(new String[] {
		"Send value",
		"Do nothing",
		});

	public ComboProperty onRelease = new ComboProperty(new String[] {
		"Send value",
		"Do nothing"
		});

	private int pType = 0;
	private int rType = 1;

	private int pressIndex = 0, releaseIndex = 0;
	
	private final static String propertiesDescriptions[][] = {
	};
	
	private ScalarPipeDistributor out;
	private ButtonChart chart;
/**
 * VectorDisplay constructor comment.
 */
public InteractiveScalarSource() {
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
	setName("ISS");

	outputs[0] = out = new ScalarPipeDistributor(this);
	
//	System.out.println("Scale=" + ProcessingTools.propertiesToString(scale));
}
/**
 * Element constructor comment.
 */
public final void process() {
	chart.repaint();	
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (onPress.getSelectedIndex() == -1)
		onPress.setSelectedIndex(pType);
	else
		pType = onPress.getSelectedIndex();

	if (onRelease.getSelectedIndex() == -1)
		onRelease.setSelectedIndex(rType);
	else
		rType = onRelease.getSelectedIndex();

	if (function.getSelectedIndex() == -1)
		function.setSelectedIndex(type);
	else
		type = function.getSelectedIndex();
			
	super.reinit();
}
/**
 */
public void sendActionEvent(int t, Object o) {
	if (type == SEQ) {
		if (t == AEvent.BUTTON_PRESSED) {
			chart.changeState(true);
			if (pType == 0) {
				if (pressValues.length > 0) {
					out.write(pressValues[pressIndex]);
					pressIndex = (pressIndex + 1) % pressValues.length;
				}
			}
		} else {
			chart.changeState(false);
			if (rType == 0) {
				if (releaseValues.length > 0) {
					out.write(releaseValues[releaseIndex]);
					releaseIndex = (releaseIndex + 1) % releaseValues.length;
				}
			}
		}
	} else if (type == SUM) {
		if (t == AEvent.BUTTON_PRESSED) {
			chart.changeState(true);
			if (pType == 0) {
				if (pressValues.length > 0) {
					sum+=pressValues[pressIndex];
					//System.out.println("sum=" + sum);
					out.write(sum);
					pressIndex = (pressIndex + 1) % pressValues.length;
				}
			}
		} else {
			chart.changeState(false);
			if (rType == 0) {
				if (releaseValues.length > 0) {
					sum+=releaseValues[releaseIndex];
					out.write(sum);
					releaseIndex = (releaseIndex + 1) % releaseValues.length;
				}
			}
		}		
	}
}
/**
 */
public void start() {
	pressIndex = 0;
	releaseIndex = 0;
	sum = 0;
}
}
