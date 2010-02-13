/* VerticalBarChart.java v 1.0.9   11/6/04 7:15 PM
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
public class VerticalBarChart extends HorizontalAxisChart {
        public Color niceblue = new Color( 97, 155, 211 );
	public int barLeftMargin = 10, barRightMargin = 15;
	public int barBottomMargin = 10, barTopMargin = 20;
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public VerticalBarChart() {
	super();
}
/**
 * 	Print on chart
 */
protected void drawInitHorizontalAxis() {
	initGraphics.drawLine(leftMargin, compHeight - downMargin, compWidth - rightMargin - barRightMargin, compHeight - downMargin);
}
/**
 * 	Print on chart
 */
public void push(int value) {
	if (!initialized)
		return;

	int y1 = (compHeight - downMargin) - value;
	if (y1 < topMargin)
		y1 = topMargin;
	if (y1 > topMargin + chartHeight)
		y1 = topMargin + chartHeight;

	copyInitImage();

        gr.setColor(niceblue);
	gr.fillRect(leftMargin + barLeftMargin, y1,
		chartWidth - barLeftMargin - barRightMargin,
		compHeight - downMargin - y1);
}
    public void push(double value) {
        if (!initialized)
                return;

        double y1 = ((double)compHeight - (double)downMargin) - value;
        if (y1 < topMargin)
                y1 = topMargin;
        if (y1 > topMargin + chartHeight)
                y1 = topMargin + chartHeight;

        copyInitImage();

        gr.setColor(niceblue);
        gr.fillRect((int)leftMargin + (int)barLeftMargin, (int)y1,
                chartWidth - barLeftMargin - barRightMargin,
                compHeight - downMargin - (int)y1);
}

}
