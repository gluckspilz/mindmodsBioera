/* Vector3DDisplay.java v 1.0.9   11/6/04 7:15 PM
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

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class Vector3DDisplay extends Display {
	public int depth = 100;
	public int rotate[] = {-65,0,0};	
	public double shift[] = {0, 0, 0};
	public double scale[] = {0.5, 2.0, 0.5, 0.75};
	public boolean redraw = false;
	public boolean invert = false;
	public int uVRange = 256;
	public String amplitudeColors[] = {"green","blue", "red","yellow","white"};

	private final static String propertiesDescriptions[][] = {
//		{"showSeconds", "Time range [s]", ""},
		{"redraw", "Redraw mode", ""},
		{"uVRange", "Range [uV]", ""},
		{"amplitudeColors", "Colors", ""},
		{"depth", "Depth (points)", ""},
		{"rotate", "Rotate (x,y,x) [0-360]", ""},
		{"shift", "Shift position (x,y,z)", ""},
		{"scale", "Scale (x,y,z,A)", ""},
		{"invert", "Inverted", ""},
	};	
		
	private int displayVector[];
	private BufferedVectorPipe in;
	private int inbuffer[];
	private Vector3DChart chart;
	private int vSize;

	public static final int defaultColors[][] = {
		//{204, 196, 212}, {204, 204, 212}, 
		{252, 252, 252}, {252, 252, 244},
		{252, 252, 236}, {252, 252, 228}, {252, 252, 220}, {252, 252, 212},
		{252, 252, 204}, {252, 252, 196}, {252, 252, 188}, {252, 252, 180},
		{252, 252, 172}, {252, 252, 164}, {252, 252, 156}, {252, 252, 148},
		{252, 252, 140}, {252, 252, 132}, {252, 252, 124}, {252, 252, 116},
		{252, 252, 108}, {252, 252, 100}, {252, 252, 92}, {252, 252, 84},
		{252, 252, 76}, {252, 252, 68}, {252, 252, 60}, {252, 252, 52},
		{252, 252, 44}, {252, 252, 36}, {252, 252, 28}, {252, 252, 20},
		{252, 252, 12}, {252, 252, 4}, {252, 244, 4}, {252, 236, 4},
		{252, 228, 4}, {252, 220, 4}, {252, 212, 4}, {252, 204, 4},
		{252, 196, 4}, {252, 188, 4}, {252, 180, 4}, {252, 172, 4},
		{252, 164, 4}, {252, 156, 4}, {252, 148, 4}, {252, 140, 4},
		{252, 132, 4}, {252, 124, 4}, {252, 116, 4}, {252, 108, 4},
		{252, 100, 4}, {252, 92, 4}, {252, 84, 4}, {252, 76, 4},
		{252, 68, 4}, {252, 60, 4}, {252, 52, 4}, {252, 44, 4},
		{252, 36, 4}, {252, 28, 4}, {252, 20, 4}, {252, 12, 4},
		{252, 4, 4}, {244, 4, 12},  {236, 4, 20}, {228, 4, 28},
		{220, 4, 36}, {212, 4, 36}, {212, 4, 44}, {204, 4, 44},
		{196, 4, 60}, {188, 4, 68}, {180, 4, 76}, {164, 4, 92},
		{180, 4, 76}, {164, 4, 92}, {156, 4, 100}, {148, 4, 100},
		{148, 4, 108}, {148, 4, 100}, {148, 4, 108}, {148, 4, 100},
		{140, 4, 116}, {132, 4, 124}, {128, 0, 128}, {124, 4, 132},
		{128, 0, 128}, {116, 4, 140}, {108, 4, 148}, {100, 4, 148},
		{100, 4, 156}, {92, 4, 156}, {84, 4, 172}, {76, 4, 180},
		{68, 4, 188}, {60, 4, 196}, {52, 4, 204}, {44, 4, 212},
		{36, 4, 212}, {36, 4, 220}, {28, 4, 228}, {4, 4, 228},
		{20, 4, 236}, {12, 4, 244}, {4, 4, 252}, {4, 4, 244},
		{4, 4, 236}, {4, 4, 228}, {4, 4, 220}, {4, 4, 212},
		{4, 4, 204}, {4, 4, 196}, {4, 4, 188}, {4, 4, 180},
		{4, 4, 172}, {4, 4, 164}, {4, 4, 156}, {4, 4, 148},
		{4, 4, 140}, {4, 4, 132}, {4, 4, 124}, {4, 4, 116},
		//{4, 4, 108}, {4, 4, 100}, {4, 4, 92}, {4, 4, 84},
		//{4, 4, 76}, {4, 4, 68}, {4, 4, 60}, {4, 4, 52}, {4, 4, 44},
		//{4, 4, 36}, {4, 4, 28}, {4, 4, 20}, {4, 4, 12}, {4, 4, 20},
		//{4, 4, 12}, {4, 4, 4}
		};
	
/**
 * VectorDisplay constructor comment.
 */
public Vector3DDisplay() {
	super();
	setName("V_Disp");
	inputs = new BufferedVectorPipe[getInputsCount()];	
	inputs[0] = in = new BufferedVectorPipe(this);
	in.setName("IN");
//	in.setBufferSize(1024*4);
}
/**
 * Element constructor comment.
 */
public Chart createChart() {
	try {
		return chart = new Vector3DChart();
	} catch (java.lang.NoClassDefFoundError e) {
		throw new RuntimeException("Object '"+getClass().getName()+"' couldn't be loaded because java 3D had not been installed. For more information check the manual.");
	} 
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
	if (redraw)
		chart.pushRedraw(in.getVBuffer(), n);
	else {
		chart.pushShift(in.getVBuffer(), n);
	}

	in.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
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
	//chart.xMaxValue = (int) vp.getPhysicalMax();
	//chart.xMinValue = (int) vp.getPhysicalMin();
	vSize = vp.getSignalParameters().getVectorLength();

	if (rotate.length >= 3) {
		chart.rotateX = rotate[0];
		chart.rotateY = rotate[1];
		chart.rotateZ = rotate[2];
	}

	if (shift.length >= 3) {
		chart.shiftX = (float) shift[0];
		chart.shiftY = (float) shift[1];
		chart.shiftZ = (float) shift[2];
	}

	
	if (debug)
		System.out.println("VectorDisplay: len=" + vp.getSignalParameters().getVectorLength() + " min=" + vp.getSignalParameters().getVectorMin() + " max=" + vp.getSignalParameters().getVectorMax() + " resolution=" + vp.getSignalParameters().getVectorResolution());			

	displayVector = new int[vSize];

	if (amplitudeColors != null && amplitudeColors.length > 0) {
		java.awt.Color cl[] = new java.awt.Color[amplitudeColors.length];
		for (int i = 0; i < cl.length; i++){
			java.lang.reflect.Field field = java.awt.Color.class.getField(amplitudeColors[i]);
			cl[i] = (java.awt.Color) field.get(null);			
		}
		chart.setColors(cl);
	} else {
		byte colors[][] = new byte[defaultColors.length][3];
		for (int i = 0; i < colors.length; i++){
			for (int j = 0; j < 3; j++){
				colors[colors.length - 1 - i][j] = (byte) defaultColors[i][j];			
			}
		}
		chart.setColors(colors);		
	}

	chart.setValueRange((int)(uVRange * getSignalParameters().getDigitalRange() / getSignalParameters().getPhysRange()));
	chart.scaleX = (float)scale[0];
	chart.scaleY = (float)scale[1];
	chart.scaleZ = (float)scale[2];
	chart.scale = (float)scale[3];

	chart.invert = invert;
	
	chart.initialize(vSize, depth, 
//		new java.awt.Color((chart. +128) ^ 128, (colors[0][1]+128) ^ 128, (colors[0][2]+128) ^ 128)
		null//java.awt.Color.green
	);
	
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
	chart.reset();
}
}
