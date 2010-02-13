/* SliderChart.java v 1.0.9   11/6/04 7:15 PM
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
public class SliderChart extends BorderChart implements MouseListener {
	public int value;
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public SliderChart() {
	super();
	leftMargin = 5;
	rightMargin = 5;
	topMargin = 5;
	downMargin = 5;

	canvas.addMouseListener(this);
}
/**
 * 	Print on chart
 */
protected void createInitImage() {
	super.createInitImage();

	drawCornerDescription();

	//initGraphics.drawLine(leftMargin + chartWidth / 2, topMargin, leftMargin + chartWidth / 2, topMargin + chartHeight);
	initGraphics.drawLine(leftMargin, topMargin + chartHeight / 2, leftMargin + chartWidth, topMargin + chartHeight / 2);
	
	//int s = chartHeight;
	
	////System.out.println("s=" + s);
	//font = new Font("Arial", Font.BOLD, s);
	//initGraphics.setFont(font);
	//FontMetrics mtx = initGraphics.getFontMetrics();
	//int w = mtx.stringWidth(chartName);
	//while (w >= (chartWidth - leftMargin - rightMargin) && s > 1) {
		//s--;
		//font = new Font("Arial", Font.BOLD, s);
		//initGraphics.setFont(font);
		//mtx = initGraphics.getFontMetrics();
		//w = mtx.stringWidth(chartName);		
	//}
	
	//int h = mtx.getAscent();
	//if (chartWidth > w)
		//x = (chartWidth - w) / 2;
	//else
		//x = leftMargin;
	//y = topMargin + chartHeight / 2 + h / 2;
	//initGraphics.drawString(chartName, x, y);
}
/**
 * 	Print on chart
 */
protected void drawCornerDescription() {
	if (chartWidth < 20)
		return;

	int fontHeight = initGraphics.getFontMetrics().getAscent();
	int descWidth = initGraphics.getFontMetrics().stringWidth(chartName);

	initGraphics.clearRect(compWidth - (descWidth + 3), 1, descWidth + 1, fontHeight + 3);	
	initGraphics.drawLine(compWidth - 1, fontHeight+2, compWidth - (descWidth + 2), fontHeight+2);
	initGraphics.drawLine(compWidth - (descWidth + 2), 0, compWidth - (descWidth + 2), fontHeight+2);
	initGraphics.drawString(chartName, compWidth - (descWidth + 1), fontHeight);
	//System.out.println("ok");
}
/**
 * 	Print on chart
 */
public final void initWorkingImage() {
	push(leftMargin + value);
}
	/**
	 * Invoked when the mouse has been clicked on a component.
	 */
public void mouseClicked(java.awt.event.MouseEvent e) {}
	/**
	 * Invoked when the mouse enters a component.
	 */
public void mouseEntered(java.awt.event.MouseEvent e) {}
	/**
	 * Invoked when the mouse exits a component.
	 */
public void mouseExited(java.awt.event.MouseEvent e) {}
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 */
public void mousePressed(java.awt.event.MouseEvent e) {
	int x = e.getX();
	if (x >= leftMargin && x <= leftMargin + chartWidth) {
		push(x);
		repaint();
		if (element != null)
			element.sendActionEvent(AEvent.BUTTON_PRESSED, this);
	}
}
	/**
	 * Invoked when a mouse button has been released on a component.
	 */
public void mouseReleased(java.awt.event.MouseEvent e) {
	if (element != null)
		element.sendActionEvent(AEvent.BUTTON_RELEASED, this);
	repaint();
}
/**
 * 	Print on chart
 */
private void push(int nx) {
	if (!initialized)
		return;

	value = nx - leftMargin;
		
	copyInitImage();

	gr.drawLine(nx, topMargin, nx, topMargin + chartHeight);
}
/**
 * 	Print on chart
 */
public void pushValue(int nvalue, int min, int max) {
	if (!initialized)
		return;
	
	value = (nvalue - min) * chartWidth / (max - min + 1);
		
	copyInitImage();

	gr.drawLine(value + leftMargin, topMargin, value + leftMargin, topMargin + chartHeight);
}
}
