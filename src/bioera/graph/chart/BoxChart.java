/* BoxChart.java v 1.0.9   11/6/04 7:15 PM
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
public class BoxChart extends BorderChart {
	int pValue = 0;
	int cX, cY;
	public Color color;
	public int type;

/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public BoxChart() {
	super();
	leftMargin = 10;
	rightMargin = 10;
	topMargin = 10;
	downMargin = 10;
}
/**
 * 	Print on chart
 */
public void initWorkingImage() {
	cX = compWidth / 2;
	cY = compHeight / 2;
}
/**
 * 	Print on chart
 */


public void pushCenter(int value) {
	//if (!initialized)
	//	return;

	//if (value == pValue)
	//	return;



        Color c = gr.getColor();

	if (value < pValue) {
		gr.setColor(canvas.getBackground());
		gr.fillRect(cX - pValue, cY - pValue, pValue * 2, pValue * 2);
	}
	gr.setColor(color);
	if (type == 0) {
		// RECTANGLE
		gr.fillRect( cX - value, cY - value, value * 2, value * 2);
	} else if (type == 1) {
		// CIRCLE
		gr.fillOval(cX - (int)(value * 1.5), cY - value, (int)(value * 1.5), value * 2);
	} else if (type == 2) {
		// TRIANGLE
		int px = value * 86602 / 100000;
		int py = value / 2;
		gr.fillPolygon(new int[]{cX, cX + px, cX - px}, new int[]{cY - value, cY + py, cY + py}, 3);
	} else if (type == 3) {
		// STAR - this may be not very fast
		int n = 5;
		int xs[] = new int[n*2];
		int ys[] = new int[n*2];
		java.awt.geom.AffineTransform tr = new java.awt.geom.AffineTransform();
		xs[0] = cX + 0;
		ys[0] = cY + value;
		//ys[1] = cY + (int)(value * Math.cos(Math.PI / n)); // 36degree
		//xs[1] = cX + (int)((xs[1] - cY) * Math.cos(2 * Math.PI / n)); // 72degree
		xs[1] = cX;
		ys[1] = cY + value;

		double src[] = new double[4];
		double dest[] = new double[4];

		for (int i = 0; i < n - 1; i++){
			src[0] = xs[i*2] - cX;
			src[1] = ys[i*2] - cY;
			src[2] = xs[i*2+1] - cX;
			src[3] = xs[i*2+1] - cY;
			tr = new java.awt.geom.AffineTransform();
			tr.rotate(2 * Math.PI / (n+1));
			tr.transform(src, 0, dest, 0, 2);
			xs[i*2+2] = cX + (int) dest[0];
			ys[i*2+2] = cY + (int) dest[1];
			xs[i*2+3] = cX + (int) dest[2];
			ys[i*2+3] = cY + (int) dest[3];

		}

		//gr.fillPolygon(xs, ys, n*2);
		gr.drawPolygon(xs, ys, 10);
	}
	gr.setColor(c);

	pValue = value;
}
}
