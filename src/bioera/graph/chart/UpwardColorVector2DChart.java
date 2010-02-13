/* UpwardColorVector2DChart.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.graph.chart;

import java.awt.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import bioera.*;
import bioera.graph.designer.*;
import bioera.processing.impl.*;
import bioera.processing.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class UpwardColorVector2DChart extends ScaledAxisChart {
	// Internal values
	protected int y; 	// position of points to draw
	protected int yPos;		// print position in buffer

	protected Color definedColors[];
	protected Color runtimeColors[];
	public String colorUnit = "";
	public int valueRange = 1024;

	public int valueMaxDesc = 1024;
	public int valueMinDesc = 0;

	public int originalVectorLength;
	public int linesNumber;
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public UpwardColorVector2DChart() {
	super();
}
/**
 * 	Print on chart
 */
protected void createInitImage() {
	super.createInitImage();

	if (chartWidth < 20) {
		return;
	}
	
	y = 0;

	if (runtimeColors == null)
		return;
	
	// Draw color scale
	int fontHeight = initGraphics.getFontMetrics().getAscent();
	//int descWidth = initGraphics.getFontMetrics().stringWidth(chartName);

	// This is the range of the color scale
	int hm = 15;
	int cornerLength = (fontHeight + 2);
	int h = compHeight - cornerLength - downMargin - hm * 2;
	int hs = compHeight - downMargin - hm;
	int w = 10;
	
	// Map colors to the color scale
	Color c = initGraphics.getColor();
	for (int i = 0; i < h; i++){
		initGraphics.setColor(runtimeColors[i * valueRange/ h]);
		initGraphics.drawLine(compWidth - w, hs - i, compWidth - 5, hs - i);
	}
	initGraphics.setColor(c);

	// Print values on the color scale
	int n = h / (fontHeight + 5);
	String prev = ""; 
	for (int i = 0; i < n - 1; i++){		
		String s = "" + (i * (valueMaxDesc - valueMinDesc) / (n-1) + valueMinDesc);
		if (prev.equals(s)) {
			continue;
		} else {
			prev = s;
		}
		int sw = initGraphics.getFontMetrics().stringWidth(s);
		initGraphics.drawString(s, compWidth - w - 2 - sw, hs - (i * h / n));
		initGraphics.drawLine(compWidth - w - 2, hs - (i * h / n), compWidth - w, hs - (i * h / n));
	}

	String s = "[" + colorUnit + "]";
	int sw = initGraphics.getFontMetrics().stringWidth(s);
	initGraphics.drawString(s, compWidth - w - 2 - sw, hs - h + fontHeight);
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public void distributeColors() {
	runtimeColors = new Color[valueRange];
	if (definedColors == null || definedColors.length == 0) {
		// Colors not defines, create default
		for (int i = 0; i < valueRange; i++){
			runtimeColors[i] = new Color(Color.HSBtoRGB((float)(0.5 - 1.0 * i/valueRange), 0.5f, 0.85f));
		}
	} else {
		// distribute provided colors equally
		for (int i = 0; i < valueRange; i++){
			runtimeColors[i] = definedColors[i * definedColors.length / valueRange];		
		}		
	}
}
/**
 * 	Assume 
 *	1. the vector length is the same as xEffectvePixelRange
 *  2. One vector is for one vertical pixel
 */
public void pushScaledVector(int vect[]) {
	if (!initialized)
		return;

	//if (gr == null || chartWidth == 0 || chartHeight == 0)
		//return;
		

	//System.out.println("\n\n\nChartHeight=" + chartHeight);
	//System.out.println("compHeight=" + compHeight);
	////System.out.println("yPixelsInc=" + yPixelsInc);
	////System.out.println("yValueInc=" + yValueInc);
	//System.out.println("yMaxValue=" + yMaxValue);
	//System.out.println("yMinValue=" + yMinValue);

	//System.out.println("VLen=" + vect.length);
	

	int y1 = (compHeight - downMargin) - yPos;
	if (y1 >  y) {
		copyInitImage();
	} else {
		Color originalColor = gr.getColor();
		int v;
		for (int i = 0; i < vect.length; i++){
			v = vect[i];
			if (v < 0 || v >= runtimeColors.length) {
				continue;
			}
			gr.setColor(runtimeColors[v]);
			gr.drawLine(leftMargin + 1 + i, y-1, leftMargin + 1 + i, y1-1);
		}
		
		//gr.setColor(Color.white);
		//gr.drawLine(leftMargin, y, leftMargin, y1);
		//gr.setColor(originalColor);
	}

	y = y1;

	yPos = (yPos + 1) % yEffectivePixelRange;
	
}
/**
 * 	Assume 
 *	1. the vector length is the same as xEffectvePixelRange
 *  2. One vector is for one vertical pixel
 */
public void pushScaledVector_Shift(int vect[]) {
	if (gr == null || chartWidth == 0 || chartHeight == 0)
		return;
		

	//System.out.println("\n\n\nChartHeight=" + chartHeight);
	//System.out.println("compHeight=" + compHeight);
	////System.out.println("yPixelsInc=" + yPixelsInc);
	////System.out.println("yValueInc=" + yValueInc);
	//System.out.println("yMaxValue=" + yMaxValue);
	//System.out.println("yMinValue=" + yMinValue);

	//System.out.println("VLen=" + vect.length);
	

	gr.copyArea(leftMargin + 1, topMargin + 1, vect.length, chartHeight, 0, -1);
	int y1 = (compHeight - downMargin) - 1;
	Color originalColor = gr.getColor();
	int v;
	for (int i = 0; i < vect.length; i++){
		v = vect[i];
		if (v < 0 || v >= runtimeColors.length) {
			continue;
		}
		gr.setColor(runtimeColors[v]);
		gr.drawLine(leftMargin + 1 + i, y1, leftMargin + 1 + i, y1);
	}
	

	yPos = (yPos + 1) % yEffectivePixelRange;
	
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public void reset() {
	if (yPos == 0)
		copyInitImage();
	else
		yPos = 0;
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public void setColors(Color t[]) {
	definedColors = t;
	distributeColors();
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public void setColorUnit(String colorU) {
	colorUnit = colorU;
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public void setValueRange(int nV) {
	if (nV <= 0)
		throw new RuntimeException("Not proper range value: " + nV);
	valueRange = nV;
	distributeColors();
}
}
