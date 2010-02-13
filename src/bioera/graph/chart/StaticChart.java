/* StaticChart.java v 1.0.9   11/6/04 7:15 PM
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

public class StaticChart extends AxisChart {
	ChartAction chartable;
	public int values[];
/**
 * StaticChart constructor comment.
 */
public StaticChart(ChartAction ichartable) {
	super();
	chartable = ichartable;

	leftMargin = 15; 
	rightMargin = 12; 
	topMargin = 15; 
	downMargin = 15;

	grid = true;
}
/**
 * 	Print on chart
 *	Make sure the exact mid-range is always printed.
 *  The range can be from negative to positive
 */
protected void createInitImage() {
	super.createInitImage();

	if (values.length < 1)
		return;

	int bottom = compHeight - downMargin;
	for (int i = 0; i < chartWidth; i++){
//		initGraphics.drawLine(leftMargin + i-1, bottom - values[i-1], leftMargin + i, bottom - values[i]);
		initGraphics.drawLine(leftMargin + i, bottom, leftMargin + i, bottom - values[i]);
	}
}
/**
 * 	Print on chart
 *	Make sure the exact mid-range is always printed.
 *  The range can be from negative to positive
 */
protected void drawCornerDescription() {
}
/**
 * 	Print on chart
 */
protected void drawInitHorizontalAxisDesc() {
	drawBalancedHorizontalAxisDesc();

	String s = doubleToString(xMinValue);
	int len = initGraphics.getFontMetrics().stringWidth(s);
	int fontHeight = initGraphics.getFontMetrics().getAscent();
	initGraphics.drawString(s, 1, compHeight - downMargin + fontHeight);	
}
/**
 * 	Print on chart
 *	Make sure the exact mid-range is always printed.
 *  The range can be from negative to positive
 */
protected void drawInitVerticalAxisDesc() {
	drawBalancedVerticalAxisDesc();
}
/**
 * 	Print on chart
 *	Make sure the exact mid-range is always printed.
 *  The range can be from negative to positive
 */
protected void repopulate() {
	values = new int[getChartWidth()];
	if (chartable != null)
		chartable.repopulateChart();
}
}
