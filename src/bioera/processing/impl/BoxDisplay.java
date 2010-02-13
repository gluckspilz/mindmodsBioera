/* BoxDisplay.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.properties.*;
import bioera.fft.*;
import bioera.graph.chart.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class BoxDisplay extends Display {
	public FloatCompoundScale amplitudeRange;
	//public int range = 1000;
	//public FullVertScale scale;
	public String foregroundColor = "red";
	public boolean showChartFrame = true;

//	public String backgroundColor = "";
	public ComboProperty shape = new ComboProperty(new String[] {"rectangle", "circle", "triangle"});

	private final static String propertiesDescriptions[][] = {
//		{"", "", ""},
	};

	private BufferedScalarPipe in;
	private BoxChart chart;
	private int digiRange, b[], prev;
	private int chartHalf, chartRange;
/**
 * VectorDisplay constructor comment.
 */
public BoxDisplay() {
	super();
	setName("Box");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	b = in.getBuffer();

	amplitudeRange = new FloatCompoundScale(100, new SmallVerticalScale(this));
}
/**
 * Element constructor comment.
 */
public Chart createChart() {
	return chart = new BoxChart();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() throws Exception {
	return "Displays graphic box of size depended on input value";
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
public final void process() {
	int n = in.available();
	if (n == 0)
		return;

	n = b[n-1];
	if (n == prev) {
		in.purgeAll();
		return;
	}

	prev = n;

	chart.pushCenter(chartHalf + n * chartRange / digiRange);
	chart.repaint();

	//System.out.println("n=" + n + "   " + (chartHalf + n * chartRange / digiRange));

	in.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	if (((SmallVerticalScale)(amplitudeRange.scale)).getSelectedIndex() == -1) {
		((SmallVerticalScale)(amplitudeRange.scale)).setSelectedItemThrow("%");
	}

	if (shape.getSelectedIndex() == -1)
		shape.setSelectedIndex(0);

	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		setReinited(true);
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	java.lang.reflect.Field field = java.awt.Color.class.getField(foregroundColor);

	chart.color = (java.awt.Color) field.get(null);
	chart.type = shape.getSelectedIndex();
	chart.showFrame = showChartFrame;

	amplitudeRange.scale.update(this);
	chartRange = Math.min(chart.getChartHeight(), chart.getChartWidth()) / 2;
	digiRange = (int) amplitudeRange.getChartDigiRange();
	chartHalf = (int)(chartRange * amplitudeRange.getChartRangeRatio());

	//System.out.println("chartHalf="+chartHalf);
	//System.out.println("digiRange="+digiRange);
	//System.out.println("getChartHeight()="+chart.getChartHeight());
	//System.out.println("getChartWidth()="+chart.getChartWidth());


	super.reinit();
}
}
