/* TextMLChart.java v 1.0.9   11/6/04 7:15 PM
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
public class TextMLChart extends BorderChart {
	protected Font font;
	protected int x, y;
	int lineNo = 2;
	String tBuffer[] = new String[2];
	int fHeight;
	int vertIncr;
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public TextMLChart() {
	super();
	leftMargin = 5;
	rightMargin = 5;
	topMargin = 5;
	downMargin = 5;
}
/**
 * 	Print on chart
 */
protected void createInitImage() {
	super.createInitImage();

	drawCornerDescription();

	if (lineNo <= 0)
		throw new RuntimeException("To few lines: " + lineNo);
	
	fHeight = (chartHeight - topMargin - downMargin) / lineNo;
	
	//System.out.println("s=" + s);
	font = new Font("Arial", Font.BOLD, fHeight);
	initGraphics.setFont(font);
	FontMetrics mtx = initGraphics.getFontMetrics();
	int h = mtx.getAscent();
	vertIncr = h;
	x = leftMargin;
	y = topMargin + h;
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
protected void initWorkingImage() {
	//System.out.println("inited with '" + lastStr + "'");
//	pushText(l);
	//lastStr = "";
}
/**
 * 	Print on chart
 */
public void pushText(String str[]) {
	if (!initialized)
		return;

	if (str.length != lineNo)
		throw new RuntimeException("Fatal error, buffer len="+str.length+" should be " + lineNo);

	int i;
	for (i = 0; i < lineNo; i++){
		if (!tBuffer[i].equals(str[i]))
			break;		
	}

	if (i == lineNo)
		return;
	
	copyInitImage();

	Font oldFont = gr.getFont();
	gr.setFont(font);

	for (i = 0; i < lineNo; i++){
		tBuffer[i] = str[i];
		if (tBuffer[i].length() > 0)
			gr.drawString(tBuffer[i], leftMargin, y + i * vertIncr);
	}
	
	gr.setFont(oldFont);	
}
/**
 * 	Print on chart
 */
public void setLineNo(int newNo) {
	lineNo = newNo;
	tBuffer = new String[lineNo];
	for (int i = 0; i < lineNo; i++){
		tBuffer[i] = "";
	}
}
}
