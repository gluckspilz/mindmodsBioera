/* Oscilloscope.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.graph.chart.*;
import bioera.properties.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class Oscilloscope extends Display {
	public float showSeconds = 10;
	public FloatCompoundUnitScale amplitudeRange;
	public boolean highPrecision = true;
	public boolean joinPoints = true;
	public boolean reflectTimeRange = true;
	public boolean grid = false;


	private final static String propertiesDescriptions[][] = {
		{"highPrecision", "High precision", ""},				
		{"reflectTimeRange", "Reflect time range", ""},
		{"showSeconds", "Time range [s]", ""},
		{"amplitudeRange", "Amplitude range", ""},
		{"joinPoints", "Join points", ""},
	};
	
	int b[], lastMM = Integer.MIN_VALUE, 	min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
	int counter, mean;
	int chartRange;
	private HorizontalScalarChart chart;
	private int digiRange;
	private int chartLevel;//, amax;
	private BufferedScalarPipe in0;

	protected static boolean debug = bioera.Debugger.get("impl.Oscilloscope");
/**
 * SignalDisplay constructor comment.
 */
public Oscilloscope() {
	super();
	setName("Osc");
	in0 = (BufferedScalarPipe)inputs[0];
	in0.setName("0");
	b = in0.getBuffer();

//	amplitudeRange = new FloatCompoundUnitScale(new FloatCompoundScale(100, new SmallVerticalScale(this)), new SmallBalancedVertScale(this));
	amplitudeRange = new FloatCompoundUnitScale(new FloatCompoundScale(100, new SmallVerticalScale(this)), new SmallBalancedVertScale(this));
}
/**
 * Element constructor comment.
 */
public Chart createChart() {
	return chart = new HorizontalScalarChart();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Displays linear stream data on graphic chart";
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
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
/**
 * SignalDisplay constructor comment.
 */
public String printAdvancedDetails() throws Exception {
	return super.printAdvancedDetails() 
	+ ">counter=" + counter + "\n"
	+ ">chartHeight=" + chart.getChartHeight() + "\n"
	+ ">chartWidth=" + chart.getChartWidth() + "\n"
	+ ">chart.valueIncrement=" + chart.valueIncrement + "\n";
}
/**
 * Element constructor comment.
 */
public final void process() {
	int n = in0.available();
	if (n == 0)
		return;

	if (highPrecision) {
		int v;
		//System.out.println("arrived " + n + " data");
		for (int i = 0; i < n; i++) {
			v = b[i];
			if (v < min)
				min = v;
			if (v > max)
				max = v;
			if (lastMM == Integer.MIN_VALUE)
				lastMM = v;
			if ((counter++ % chart.valueIncrement) == 0) {
				chart.pushMinMax2(
					chartLevel + lastMM * chartRange / digiRange, 
					chartLevel + v * chartRange / digiRange, 
					chartLevel + min * chartRange / digiRange, 
					chartLevel + max * chartRange / digiRange);
				min = Integer.MAX_VALUE; 
				max = Integer.MIN_VALUE;
				lastMM = Integer.MIN_VALUE;
			}
		}
	} else {
		for (int i = 0; i < n; i++) {
			mean += b[i];
			if (counter++ % chart.valueIncrement == 0) {
				chart.pushValue(chartLevel + (mean / chart.valueIncrement) * chartRange / digiRange);
				mean = 0;
			}
		}		
	}

	in0.purgeAll();
	chart.repaint();	
}
/**
 * SignalDisplay constructor comment.
 */
public void reinit() throws Exception {
	if (((SmallVerticalScale)(amplitudeRange.scaledValue.scale)).getSelectedIndex() == -1) {
		((SmallVerticalScale)(amplitudeRange.scaledValue.scale)).setSelectedItemThrow("%");
	}

	if (((SmallBalancedVertScale)amplitudeRange.unit).getSelectedIndex() == -1) {
		((SmallBalancedVertScale)amplitudeRange.unit).setSelectedIndex(0);
	}
	
	//System.out.println("1.5");			
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		disactivate("Not connected");
		reinited = true;
		//System.out.println("1.6");			
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		//System.out.println("1.7");			
		return;
	}


	amplitudeRange.update(this);
	chart.yMaxValue = amplitudeRange.getScaleMax();
	chart.yMinValue = amplitudeRange.getScaleMin();
	chartRange = chart.getChartHeight();		
	chartLevel = (int)(chartRange * amplitudeRange.getChartRangeRatio());
	digiRange = (int) amplitudeRange.getChartDigiRange();

	//System.out.println("chart.yMaxValue="+chart.yMaxValue);
	//System.out.println("chart.yMinValue="+chart.yMinValue);
	//System.out.println("chartLevel="+chartLevel);
	//System.out.println("digiRange="+digiRange);
	
	lastMM = getSignalParameters().getDigitalZero();

	chart.xMinSpace = 15;
	chart.xMinValue = 0;
	chart.xMaxValue = showSeconds;
	chart.xUnit = "s";
	chart.setXPixelInc(getSignalParameters().getSignalRate());

	//System.out.println("yRange="+yRange);
	//System.out.println("yRangeRatio="+yRangeRatio);
	//System.out.println("yMaxScale="+yMaxScale);
		
	chart.yUnit = amplitudeRange.unit.getUnitStr();

	//System.out.println("unit=" + chart.yUnit);

	
	chart.grid = this.grid;
	chart.joinPoints = this.joinPoints;
	
	if (reflectTimeRange) {
		TimeRangeFeature tFeature = (TimeRangeFeature) ProcessingTools.traversPredecessorsForFeature(this, TimeRangeFeature.class);
		if (tFeature != null) {
			if (tFeature.physicalStart() > 0)
				chart.xMinValue = (float) tFeature.physicalStart();
			if (tFeature.physicalEnd() > 0)
				chart.xMaxValue =  (float) tFeature.physicalEnd();
		}
	}

	double ratio = chart.rescaleX();
	chart.setXPixelInc(getSignalParameters().getSignalRate() / ratio);
	
	chart.rescaleY();

	super.reinit();
}
/**
 * Element constructor comment.
 */
public final void start() {
	chart.reset();
	lastMM = Integer.MIN_VALUE;
}
}
