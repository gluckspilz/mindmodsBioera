/* VectorLineDisplay.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.fft.*;
import bioera.graph.chart.*;
import bioera.properties.*;
import bioera.properties.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class VectorLineDisplay extends Display {
	public float showSeconds = 50;
	public FloatCompoundUnitScale amplitudeRange;
	
	public String frequencyColor[] = {"green", "red", "yellow"};
	public String vectorValueDesc[] = {};
	public boolean reflectTimeRange = true;
	public boolean grid = false;
	public ComboProperty agenda = new ComboProperty(new String[]{"PREDESSOR", "DEFINED NAMES", "PHYSICAL VALUES", "INDEXES"});

	private final static String propertiesDescriptions[][] = {
		{"showSeconds", "Time range [s]", ""},
		{"uVRange", "Amplitude range [uV]", ""},
		{"frequencyColor", "Colors", ""},
		{"vectorValueDesc", "Descriptions", ""},			
		{"agenda", "Agenda", ""},				
	};	
	
	private BufferedVectorPipe in;
	private int inb[], vbuf[][], displayVector[];
	private HorizontalColorVectorLinearChart chart;
	private int vSize;
	int counter, digiRange, chartRange, incr, chartLevel;
	boolean started = true;

	protected static boolean debug = bioera.Debugger.get("impl.vector.linedisplay");	
/**
 * VectorDisplay constructor comment.
 */
public VectorLineDisplay() {
	super();

	setName("VL_Disp");
	inputs = new BufferedVectorPipe[getInputsCount()];	
	inputs[0] = in = new BufferedVectorPipe(this);
	in.setName("IN");
	vbuf = in.getVBuffer();
	//System.out.println("1");	
	amplitudeRange = new FloatCompoundUnitScale(new FloatCompoundScale(10, new SmallVerticalScale(this)), new SmallBalancedVertScale(this));
	//System.out.println("2");		
}
/**
 * Element constructor comment.
 */
public final void chartRepainted() {
	incr = chart.valueIncrement;
	//System.out.println("repainted p_incr=" + chart.pixelIncrement);
	//System.out.println("repainted v_incr=" + chart.valueIncrement);
	//System.out.println("digiRange="+digiRange);
}
/**
 * Element constructor comment.
 */
public Chart createChart() {
	return chart = new HorizontalColorVectorLinearChart();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Each vector index value is being drawn separately and with different color";
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

	for (int k = 0; k < n; k++){
		inb = vbuf[k];
		for (int i = 0; i < vSize; i++){
			displayVector[i] += inb[i];
		}
		if ((counter++ % incr) == 0) {
			// Take average from the last points that were not shown
			for (int i = 0; i < vSize; i++){
				displayVector[i] = chartLevel + (displayVector[i] / incr) * chartRange / digiRange;
			}	
			chart.pushVector(displayVector);
			for (int i = 0; i < vSize; i++){
				displayVector[i] = 0;
			}
		}
	}
	
	chart.repaint();
	in.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (agenda.getSelectedIndex() == -1)
		agenda.setSelectedIndex(3);

	// Set default unit on scale
	if (((SmallBalancedVertScale)amplitudeRange.unit).getSelectedIndex() == -1) {
		((SmallBalancedVertScale)amplitudeRange.unit).setSelectedItemThrow("uV");
	}

	// Set default unit for range calculation
	if (((SmallVerticalScale)(amplitudeRange.scaledValue.scale)).getSelectedIndex() == -1) {
		((SmallVerticalScale)(amplitudeRange.scaledValue.scale)).setSelectedItemThrow("%");
	}			
			
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
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
	vSize = vp.getSignalParameters().getVectorLength();
	//System.out.println("size=" + vSize + "  in " + getName());


	//if (amplitudeRange.unit.isBalanced()) {
		//amin = (getSignalParameters().getDigitalRange() - digiRange) / 2;
		//amax = amin + digiRange;
	//} else {
		//amin = 0;
		//amax = digiRange;
	//}
	
	//System.out.println("intRange=" + rI + " min=" + amin + " max=" + amax);

	//System.out.println(getName() + ": rangeInteger: " + rangeInteger);
	//System.out.println(getName() + ": levelDifference: " + levelDifference);
	//System.out.println(getName() + ": uVRange: " + getSignalParameters().uVrange);
	
	chart.xMinSpace = 15;
	chart.xMinValue = 0;
	chart.xMaxValue = showSeconds;
	chart.xUnit = "s";
	chart.setXPixelInc(predecessorElement.getSignalParameters().getSignalRate());

	if (debug)
		System.out.println("VectorLinearDisplay: elem="+ predecessorElement.getName() + "  rate=" + predecessorElement.getSignalParameters().getSignalRate());		

	amplitudeRange.update(this);
	chart.yMaxValue = amplitudeRange.getScaleMax();
	chart.yMinValue = amplitudeRange.getScaleMin();
	chartRange = chart.getChartHeight();		
	digiRange = (int) amplitudeRange.getChartDigiRange();
	if (getSignalParameters().isPhysBalanced())
		chartLevel = (int)(chartRange * amplitudeRange.getChartRangeRatio());
	else
		chartLevel = 0;
	

	//System.out.println("chart.yMaxValue="+chart.yMaxValue);
	//System.out.println("chart.yMinValue="+chart.yMinValue);
	//System.out.println("chartLevel="+chartLevel);
	//System.out.println("digiRange="+digiRange);
		
	chart.yUnit = amplitudeRange.unit.getUnitStr();
	chart.setYValueInc(1);
	chart.setVectorSize(vSize);

	chart.grid = grid;
		
	displayVector = new int[vSize];

	if (debug)
		System.out.println("Initializing colors");
	
	if (frequencyColor != null && frequencyColor.length > 0 && frequencyColor.length >= vSize) {
		// Manual colors
		java.awt.Color cl[] = new java.awt.Color[vSize];
		for (int i = 0; i < vSize; i++){
			if (frequencyColor[i] != null && frequencyColor[i].length() > 0) {
				java.lang.reflect.Field field = java.awt.Color.class.getField(frequencyColor[i]);
				cl[i] = (java.awt.Color) field.get(null);
			}
		}
		chart.setColors(cl);
	} else {
		// Automated colors
		java.awt.Color cl[] = new java.awt.Color[vSize];
		int colrs[][] = Vector2DColorDisplay.defaultColors;
		int k;
		for (int i = 0; i < vSize; i++){
			k = i * colrs.length / vSize;
			cl[i] = new java.awt.Color(colrs[k][0],colrs[k][1],colrs[k][2]);
		}
		chart.setColors(cl);		
	}

	if (debug)
		System.out.println("Initializing desc vector");
	
	String descVector[] = new String[vSize];
	int n = agenda.getSelectedIndex();
	switch (n) {
		//PREDESSOR
		case 0:
			String precessorNames[] = vp.getSignalParameters().getVectorFieldDescriptions();
			System.arraycopy(precessorNames, 0, descVector, 0, Math.min(precessorNames.length, vSize));
			if (precessorNames.length < vSize) {
				for (int i = precessorNames.length; i < vSize; i++){
					descVector[i] = "none";
				}
			}
			break;
		//DEFINED NAMES
		case 1:
			System.arraycopy(vectorValueDesc, 0, descVector, 0, Math.min(vectorValueDesc.length, vSize));
			if (vectorValueDesc.length < vSize) {
				for (int i = vectorValueDesc.length; i < vSize; i++){
					descVector[i] = "none";
				}
			}
			break;
		//PHYSICAL VALUES
		case 2:
			for (int i = 0; i < vSize; i++){
				descVector[i] = roundTo(vp.getSignalParameters().getVectorMin() + i * ((double) vp.getSignalParameters().getVectorMax() - vp.getSignalParameters().getVectorMin()) / vSize, vp.getSignalParameters().getVectorPhysicalPrecision());
			}
			break;
		//INDEXES
		case 3:
			for (int i = 0; i < vSize; i++){
				descVector[i] = "" + i;
			}
			break;
	};

	chart.setVectorDesc(descVector);	
		
	//System.out.println("len=" + vp.getVectorLength() + " min=" + vp.getPhysicalMin() + " max=" + vp.getPhysicalMax() + " resolution=" + vp.getVectorResolution());			


	if (reflectTimeRange) {
		if (debug)
			System.out.println("TimeRange feature is initialized");		
		TimeRangeFeature tFeature = (TimeRangeFeature) ProcessingTools.traversPredecessorsForFeature(this, TimeRangeFeature.class);
		if (tFeature != null) {
			chart.xMinValue = tFeature.physicalStart();
			chart.xMaxValue = tFeature.physicalEnd();
		}
	}

	incr = chart.valueIncrement;
	
	double ratio = chart.rescaleX();
	chart.setXPixelInc(getSignalParameters().getSignalRate() / ratio);

	chart.rescaleY();
		
	super.reinit();
}
/**
 * Element constructor comment.
 */
public final String roundTo(double value, double prec) throws Exception {
    if (prec >= 1) {
        return "" + Math.round(value);
    }

    if (prec <= 0)
        return "0";

    value = (((int) value) + ((int) ((value - (int) value) / prec)) * prec);

	String ret = "" + ( ((int)(value * 100)) / 100.0);
        
    while (ret.length() > 0
        && (ret.charAt(ret.length() - 1) == '0' || ret.charAt(ret.length() - 1) == '.'))
        ret = ret.substring(0, ret.length() - 1);
	       
    // Truncate digits
    return ret;
}
/**
 * Element constructor comment.
 */
public final void start() throws Exception {
	chart.reset();
}
}
