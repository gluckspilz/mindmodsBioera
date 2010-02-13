/* HorizontalColorPointVectorChart.java v 1.0.9   11/6/04 7:15 PM
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
public class HorizontalColorPointVectorChart extends HorizontalChart {
	// Internal values
	protected int x, y; // position of points to draw

	protected Color colors[];
	public boolean bars = true;
	public boolean symmetrical = false;
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public HorizontalColorPointVectorChart() {
	super();
	leftMargin = 30;
}
/**
 * 	Print on chart
 */
public void pushVector(int values[]) {
	if (!initialized)
		return;
	
	//System.out.println("there is " + values[2] + " in " + getChartName());		
	copyInitImage();

	int startY = symmetrical ? topMargin + chartHeight / 2 : (compHeight - downMargin);
		
	int i, j;
	x = leftMargin;
	if (colors != null && colors.length != values.length)
		throw new RuntimeException("Colors length '" + colors.length + "' not equals to value length '" + values.length + "'");

	Color oldColor = gr.getColor();

	if (symmetrical) {
		gr.setColor(Color.white);
		gr.drawLine(x, startY, x + values.length * pixelIncrement, startY);
	}
	
	int perpY = 0;	
	for (i = 0; i < values.length; i += valueIncrement) {
		y = startY - values[i];
		if (y < topMargin)
			y = topMargin;
		if (y + downMargin > compHeight)
			y = compHeight - downMargin;

		if (colors != null && colors[i] != null)
			gr.setColor(colors[i]);
		
		if (bars) {
			//if (y == startY)
				//gr.drawLine(x, y, x + pixelIncrement, y);
			//else
				if (pixelIncrement == 1) {
					gr.drawLine(x, y, x, startY);
				} else {
					if (startY > y )
						gr.fillRect(x, y, pixelIncrement, startY - y);
					else
						gr.fillRect(x, startY, pixelIncrement, y - startY);
				}
		} else {
			if (i != 0)
				gr.drawLine(x - 1, perpY, x + pixelIncrement, y);
			perpY = y;
		}
					
		x += pixelIncrement;
	}

	gr.setColor(oldColor);
}
/**
 * 	Print on chart
 */
public void setColors(Color c[]) {
	colors = c;
}
}
