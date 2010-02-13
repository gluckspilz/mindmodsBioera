/* HorizontalColorVectorLinearChart.java v 1.0.9   11/6/04 7:15 PM
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
public class HorizontalColorVectorLinearChart extends HorizontalChart {
	// Internal values
	protected int x, y[], x1, y1; // position of points to draw
	protected int xPos;	// print position in buffer
	protected Color colors[];
	protected String vectorDescriptions[];
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public HorizontalColorVectorLinearChart() {
	super();
}
/**
 * 	Print on chart
 */
protected void createInitImage() {
	super.createInitImage();

	x = leftMargin;

	//System.out.println("ChartW=" + chartWidth);
	//System.out.println("ChartH=" + chartHeight);
}
/**
 * 	Print on chart
 */
protected void drawCornerDescription() {
	super.drawCornerDescription();

	if (chartWidth < 20)
		return;

	int fontHeight = initGraphics.getFontMetrics().getAscent();
	
	//initGraphics.drawLine(compWidth - 1, fontHeight+2, compWidth - (descWidth + 2), fontHeight+2);
	//initGraphics.drawLine(compWidth - (descWidth + 2), 0, compWidth - (descWidth + 2), fontHeight+2);
	//initGraphics.drawString(name, compWidth - (descWidth + 1), fontHeight);
	//System.out.println("ok");

	int yref = 5;
	int xref = 5;		
	
	int y = topMargin + fontHeight + yref;
	if (colors != null && vectorDescriptions != null) {
//System.out.println("vectorDescriptions.length=" + vectorDescriptions.length + " colors=" + colors.length + "  name=" + getChartName() + "  min=" +  Math.min(vectorDescriptions.length, colors.length));		
		Color original = initGraphics.getColor();
		for (int i = 0; i < Math.min(vectorDescriptions.length, colors.length); i++){
			if (vectorDescriptions[i] != null && colors[i] != null) {
				initGraphics.setColor(colors[i]);
				int descWidth = initGraphics.getFontMetrics().stringWidth(vectorDescriptions[i]);
				initGraphics.clearRect(compWidth - (descWidth + 2 + xref), y, descWidth + 1 + xref, (fontHeight + 2));
				initGraphics.drawString(vectorDescriptions[i], compWidth - (descWidth + 1 + xref), y);
				y += (fontHeight + 2);
//System.out.println("printing " + vectorDescriptions[i]);				
			} else {
//				System.out.println("" + i + "  is null " + colors[i] + " " + vectorDescriptions[i]);
			}
		}
		initGraphics.setColor(original);
	}
}
/**
 * 	Print on chart
 */
protected void drawInitVerticalAxisDesc() {
	super.drawInitVerticalAxisDesc();

	if (colors != null && vectorDescriptions != null) {
		for (int i = 0; i < Math.min(vectorDescriptions.length, colors.length); i++){
			if (vectorDescriptions[i] != null && colors[i] != null) {
			}
		}
	}
}
/**
 * 	Print on chart
 */
public void pushVector(int v[]) {
	if (!initialized)
		return;
	
	//if (gr == null || chartWidth == 0 || chartHeight == 0)
		//return;

	x1 = leftMargin + xPos;
	if (x1 <  x) {
		copyInitImage();
	} else {
		Color originalColor = gr.getColor();
		gr.setColor(Color.white);
		if (x + pixelIncrement == x1)
			gr.drawLine(x, chartHeight + topMargin, x1, chartHeight + topMargin);
		gr.setColor(originalColor);		
		for (int i = 0; i < v.length; i++){
			if (v[i] < 0) {
				continue;
			}
			if (colors != null && colors[i] != null)
				gr.setColor(colors[i]);				
			y1 = (compHeight - downMargin) - v[i];
			if (y1 < topMargin)
				y1 = topMargin;
			if (y1 > topMargin + chartHeight)
				y1 = topMargin + chartHeight;
			if (x + pixelIncrement == x1 && y[i] >= topMargin &&  y[i] <= topMargin + chartHeight)
				gr.drawLine(x, y[i], x1, y1);
			y[i] = y1;			
		}					
	}

	x = x1;

	xPos = (xPos + pixelIncrement) % xEffectivePixelRange;
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public void reset() {
	if (xPos == 0)
		copyInitImage();
	else
		xPos = 0;
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public void setColors(Color c[]) {
	colors = c;
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public void setVectorDesc(String d[]) {
	vectorDescriptions = d;
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public void setVectorSize(int ivectorSize) {
	y = new int[ivectorSize];
}
}
