/* JBorderChart.java v 1.0.9   11/6/04 7:15 PM
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

import javax.swing.*;
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
public class JBorderChart extends JPureChart {
	// Empty margin on chart, those are also the positions of x,y axis
	protected int 	leftMargin = 30,
					rightMargin = 15,
					topMargin = 10,
					downMargin = 20;

	protected int chartHeight, chartWidth;


	public boolean showFrame = true;
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public JBorderChart() {
	super();
}


/**
 * 	Print on chart
 */
protected void createInitImage() {
	chartWidth = getChartWidth();
	if (chartWidth < 0)
		chartWidth = 0;

	chartHeight = getChartHeight();
	if (chartHeight < 0)
		chartHeight = 0;

	if (showFrame)
		initGraphics.drawRect(0, 0, compWidth - 1, compHeight - 1);
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 7:39:52 PM)
 * @param data int[]
 * @param from int
 * @param length int
 */
public int getChartHeight() {
	return compHeight - (downMargin + topMargin);
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 9:47:20 PM)
 * @param data int[]
 * @param from int
 * @param length int
 */
public int getChartWidth() {
	return compWidth - (leftMargin + rightMargin);
}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2003 12:27:55 PM)
 * @return int
 */
public int getDownMargin() {
	return downMargin;
}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2003 12:27:55 PM)
 * @return int
 */
public int getLeftMargin() {
	return leftMargin;
}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2003 12:27:55 PM)
 * @return int
 */
public int getRightMargin() {
	return rightMargin;
}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2003 12:27:55 PM)
 * @return int
 */
public int getTopMargin() {
	return topMargin;
}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2003 12:27:55 PM)
 * @param newDownMargin int
 */
public void setDownMargin(int newDownMargin) {
	downMargin = newDownMargin;
}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2003 12:27:55 PM)
 * @param newLeftMargin int
 */
public void setLeftMargin(int newLeftMargin) {
	leftMargin = newLeftMargin;
}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2003 12:27:55 PM)
 * @param newRightMargin int
 */
public void setRightMargin(int newRightMargin) {
	rightMargin = newRightMargin;
}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2003 12:27:55 PM)
 * @param newTopMargin int
 */
public void setTopMargin(int newTopMargin) {
	topMargin = newTopMargin;
}
}
