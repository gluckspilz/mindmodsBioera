/* ProgressBarDisplay.java v 1.0.9   11/6/04 7:15 PM
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
public final class ProgressBarDisplay extends Display {
	public FloatCompoundUnitScale amplitudeRange;
	
	private final static String propertiesDescriptions[][] = {
//		{"", "", ""},
	};	
	
	private BufferedScalarPipe in;
	private HorizontalBarChart chart;
	private int b[], prev;
	private int digiRange, chartLevel, chartRange;
/**
 * VectorDisplay constructor comment.
 */
public ProgressBarDisplay() {
	super();
	setName("Progress");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	b = in.getBuffer();

	amplitudeRange = new FloatCompoundUnitScale(new FloatCompoundScale(100, new SmallVerticalScale(this)), new SmallBalancedVertScale(this));
}
/**
 * Element constructor comment.
 */
public Chart createChart() {
	return chart = new HorizontalBarChart();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() throws Exception {
	return "Shows progress of the process";
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

	if (n == prev)
		return;

	prev = n;
			
	chart.push(chartLevel + n * chartRange / digiRange);	
	chart.repaint();
	in.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	if (((SmallVerticalScale)(amplitudeRange.scaledValue.scale)).getSelectedIndex() == -1) {
		((SmallVerticalScale)(amplitudeRange.scaledValue.scale)).setSelectedItemThrow("%");
	}

	if (((SmallBalancedVertScale)amplitudeRange.unit).getSelectedIndex() == -1) {
		((SmallBalancedVertScale)amplitudeRange.unit).setSelectedItemThrow("uV");
	}
	
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		setReinited(true);
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	amplitudeRange.update(this);
	chart.xMaxValue = amplitudeRange.getScaleMax();
	chart.xMinValue = amplitudeRange.getScaleMin();
	chartRange = chart.getChartWidth();		
	chartLevel = (int)(chartRange * amplitudeRange.getChartRangeRatio());
	digiRange = (int) amplitudeRange.getChartDigiRange();

	chart.xUnit = amplitudeRange.unit.getUnitStr();
	
	chart.yUnit = "";
	chart.yMaxValue = Float.MAX_VALUE;
	chart.yMinValue = Float.MIN_VALUE;
		
	super.reinit();

	//System.out.println("computeN=" + computeN + ", averageLastNSamples=" + averageLastNSamples);

}
/**
 * Element constructor comment.
 */
public void start() throws Exception {
	prev = -1;
}
}
