/* VectorDisplay.java v 1.0.9   11/6/04 7:15 PM
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
public final class VectorDisplay extends Display {
//	public float yRange = 100;
	public FloatCompoundUnitScale amplitudeRange;
	public boolean bars = true;
	public boolean grid = false;
//	public boolean symmetrical = false;
	public String frequencyColor[] = {"gray", "blue", "green", "yellow", "orange", "red", "white"};
	public int frequencyValue[] = {0, 4, 8, 13, 16, 20, 38};

	private final static String propertiesDescriptions[][] = {
//		{"showSeconds", "Time range [s]", ""},
		{"yRange", "Range [%]", ""},
		{"bars", "Show bars", ""},
		{"frequencyColor", "Colors", ""},		
		{"frequencyValue", "Scopes", ""},
	};	
		
	private int displayVector[];
	private BufferedVectorPipe in;
	private int inbuffer[];
	private HorizontalColorPointVectorChart chart;
	private int vSize, chartRange, digiRange;
/**
 * VectorDisplay constructor comment.
 */
public VectorDisplay() {
	super();
	setName("V_Disp");
	inputs = new BufferedVectorPipe[getInputsCount()];	
	inputs[0] = in = new BufferedVectorPipe(this);
	in.setName("IN");
//	in.setBufferSize(1024*4);

	amplitudeRange = new FloatCompoundUnitScale(new FloatCompoundScale(100, new SmallVerticalScale(this)), new SmallBalancedVertScale(this));
}
/**
 * Element constructor comment.
 */
public Chart createChart() {
	return chart = new HorizontalColorPointVectorChart();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Shows vector on graphic chart";
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
 * Element constructor comment.
 */
public final void process() throws Exception {
	int n = in.available();
	if (n == 0)
		return;

	//System.out.println("Processing vectors " + n);

	// Get the last vector in buffer	
	inbuffer = in.getVBuffer()[n-1];
		
	for (int i = 0; i < displayVector.length; i++){

		// value/maxRange * chartH * (maxAmplitudeuV/amplitudeToShowuV)
		
		//displayVector[i] = inbuffer[i] * chart.getChartHeight() * 100 / (amplitudePercent * getSignalParameters().signalRange);
		displayVector[i] = (int)(inbuffer[i] * chartRange / digiRange);
		//if (displayVector[i] > 5)
			//System.out.println("FFT [" + i + "]=" + displayVector[i]);
	}	

	chart.pushVector(displayVector);
	chart.repaint();

	in.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	// Set default unit for range calculation
	if (((SmallVerticalScale)(amplitudeRange.scaledValue.scale)).getSelectedIndex() == -1) {
		((SmallVerticalScale)(amplitudeRange.scaledValue.scale)).setSelectedItemThrow("%");
	}
	
	if (((SmallBalancedVertScale)amplitudeRange.unit).getSelectedIndex() == -1) {
		((SmallBalancedVertScale)amplitudeRange.unit).setSelectedItemThrow("uV");
	}
	
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		setDesignErrorMessage("Not connected");
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	PipeDistributor pd = getFirstDistributorConnectedToInput(0);
	if (pd == null || !(pd instanceof VectorPipe)) {
		throw new Exception("Element not connected properly");
	}
	
	VectorPipe vp = (VectorPipe) pd;
	
	chart.xMaxValue = vp.getSignalParameters().getVectorMax();
	chart.xMinValue = vp.getSignalParameters().getVectorMin();
	chart.setXPixelInc(vp.getSignalParameters().getVectorResolution());
	chart.xUnit = "Hz";
	chart.rescaleX();
		
	vSize = vp.getSignalParameters().getVectorLength();
	
	//chart.yMaxValue = yRange * vp.getSignalParameters().getPhysMax() / 100;	
	//chart.yMinValue = yRange * vp.getSignalParameters().getPhysMin() / 100;
	//	chart.yUnit = vp.getSignalParameters().getPhysicalUnit();

	//double yRange = ((FloatCompoundScale)amplitudeRange.scaledValue).value;
	//double yRangeRatio = ((FloatCompoundScale)amplitudeRange.scaledValue).scale.calculatePreciseRangeRatio(yRange, getSignalParameters());
	//float yMaxScale = (float)(amplitudeRange.unit.calculatePreciseScaleMaxValue(yRangeRatio, getSignalParameters()));
	
	//mult = (int)(chart.getChartHeight() * 100);
	//div = (int) (yRange * getSignalParameters().getDigitalRange());

	amplitudeRange.update(this);
	chart.yMaxValue = amplitudeRange.getScaleMax();
	chart.yMinValue = amplitudeRange.getScaleMin();
	chartRange = chart.getChartHeight();		
	digiRange = (int) amplitudeRange.getChartDigiRange();
	chart.symmetrical = getSignalParameters().isPhysBalanced();
	
	//System.out.println("symm="+chart.symmetrical);
	//System.out.println("chart.yMaxValue="+chart.yMaxValue);
	//System.out.println("chart.yMinValue="+chart.yMinValue);
	//System.out.println("digiRange="+digiRange);
		
	//System.out.println("yRange="+yRange);
	//System.out.println("yRangeRatio="+yRangeRatio);
	//System.out.println("yMaxScale="+yMaxScale);
	
	//if (amplitudeRange.unit.isBalanced()) {
		//chart.yMaxValue = yMaxScale;
		//chart.yMinValue = -yMaxScale;
	//} else {
		//div /= 2;
		//chart.yMaxValue = yMaxScale;
		//chart.yMinValue = 0;
	//}

	chart.yUnit = amplitudeRange.unit.getUnitStr();

	chart.bars = this.bars;		
	chart.grid = this.grid;
	chart.rescaleY();
		
	if (debug)
		System.out.println("VectorDisplay: len=" + vp.getSignalParameters().getVectorLength() + " min=" + vp.getSignalParameters().getVectorMin() + " max=" + vp.getSignalParameters().getVectorMax() + " resolution=" + vp.getSignalParameters().getVectorResolution());			

	displayVector = new int[vSize];

	if (frequencyColor != null && frequencyColor.length > 0 && frequencyValue.length > 0) {
		java.awt.Color cl[] = new java.awt.Color[vSize];
		for (int i = 0; i < frequencyValue.length && i < frequencyColor.length; i++){
			int f = frequencyValue[i];
			if (f >=  vp.getSignalParameters().getVectorMin() && f < vp.getSignalParameters().getVectorMax()) {
				int index = (int) ((f - vp.getSignalParameters().getVectorMin()) * vp.getSignalParameters().getVectorResolution());
				java.lang.reflect.Field field = java.awt.Color.class.getField(frequencyColor[i]);
				cl[index] = (java.awt.Color) field.get(null);
			}
		}
		chart.setColors(cl);
	}

	super.reinit();
}
}
