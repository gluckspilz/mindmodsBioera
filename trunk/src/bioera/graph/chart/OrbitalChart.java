/* OrbitalChart.java v 1.0.9   11/6/04 7:15 PM
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

/**
 * Insert the type's description here.
 * Creation date: (3/17/2004 9:42:01 PM)
 * @author: Jarek
 */
public class OrbitalChart extends bioera.graph.chart.BorderChart implements java.awt.image.ImageObserver {
	int position = 0;
	java.awt.Image images[];
	int trajX[][];					// x points of the trajectory
	int trajY[][];					// y points of the trajectory
	int widths[] = {5, 5};			// contains widths of the planet icons
	int xRanges[] = {100, 80};
	int yRanges[] = {100, 80};
	int increments[];				// Indicates velocity for each planet
	private int orbitNo = 0;		// Number of orbits
	
	public int interSpace = 5;		// Additional space between orbits
	public boolean showOrbits = true;
	public Color orbitColor = Color.white;	// Color of the orbit's line
	private int velocity = 5;		// Velocity factor common for all planets

	private int counters[];			// Holds index of trajectory for each planet
	private int smallCounters[];	// Holds index of the velocity for each planet
	
	private boolean rotateRight = false;
	private Image centerImage;
/**
 * RaceChart constructor comment.
 */
public OrbitalChart() {
	super();
	leftMargin = 10;
	rightMargin = 10;
	topMargin = 10;
	downMargin = 10;

	// = java.awt.Toolkit.getDefaultToolkit().getImage(bioera.Main.app.getImagesFolder() + java.io.File.separator + "round_red.gif");
}
/**
 * RaceChart constructor comment.
 */
protected void createInitImage() {
	super.createInitImage();

	if (orbitNo == 0)
		return;
	
	int tot = 0, pos;
	for (int i = orbitNo - 1; i >= 0; i--){
		pos = tot + widths[i] / 2;
		xRanges[i] = chartWidth / 2 - pos;
		yRanges[i] = chartHeight / 2 - pos;
		updateTrajectory(i);
		gr.setColor(orbitColor);
		initGraphics.setColor(orbitColor);
		if (showOrbits)
			initGraphics.drawPolygon(trajX[i], trajY[i], trajX[i].length);
		tot += widths[i] + interSpace;
	}

	//if (centerImage != null) {
		//System.out.println("her 2");
		//initGraphics.drawImage(centerImage, chartWidth / 2 - 4, chartHeight / 2 - 4, null);
		//gr.drawImage(centerImage, chartWidth / 2 - 4, chartHeight / 2 - 4, null);
	//}

	//for (int i = 0; i < orbitNo; i++){
		//gr.drawImage(images[i], trajX[i][0] - (widths[i]>>1), trajY[i][0] - (widths[i]>>1), null);
		//initGraphics.drawImage(images[i], trajX[i][0] - (widths[i]>>1), trajY[i][0] - (widths[i]>>1), null);		
	//}

	//try {
		//Thread.sleep(100);
	//} catch (Exception e) {
	//}		
	
}
/**
 * RaceChart constructor comment.
 */
public int function(int x, int xRange, int yRange) {
	return (int)(yRange * Math.sqrt(1.0 - (double) x*x/(xRange*xRange)));
//	return (int)(yRange * (1.0 - (double) x/(xRange)));
}
/**
 * RaceChart constructor comment.
 */
public boolean imageUpdate(java.awt.Image img, int infoflags, int x, int y, int width, int height) {
	synchronized (this) {
		//if (img == centerImage)
			//System.out.println("here 3");
		for (int i = 0; i < orbitNo; i++){
			if (images[i] == img) {
				//System.out.println("w=" + width +  "h=" + height);
				widths[i] = Math.max(width, height);
				break;
			}
		}
		if (initialized) {
			//System.out.println("again");
			//createInitImage();
			//copyInitImage();
			//repaint();
			resetChart();
			repaint();	
		}
		return false;   		
	}
}
/**
 * RaceChart constructor comment.
 */
public void pushVector(int v[]) throws Exception {
	for (int i = 0; i < orbitNo; i++){
		if (rotateRight)
			increments[i] = v[i];
		else
			increments[i] = -v[i];			
	}
}
/**
 * RaceChart constructor comment.
 */
public final void rp() throws Exception {
	if (!initialized)
		return;
		
//copyInitImage();
		
	int c, w, x, y, r;	
	for (int i = 0; i < orbitNo; i++){
		smallCounters[i] += increments[i];
		if (Math.abs(smallCounters[i]) > velocity) {
			c = counters[i];
			w = widths[i];
			x = trajX[i][c];
			y = trajY[i][c];
			r = trajX[i].length;
			//System.out.println("or1=" + i + "  c=" + c);
			gr.clearRect(x - (w>>1), y - (w>>1), w, w);
			if (showOrbits) {
				gr.drawPolygon(trajX[i], trajY[i], trajX[i].length);
			}
			c = counters[i] = (counters[i] + smallCounters[i] / velocity + r) % r;
			gr.drawImage(images[i], trajX[i][c] - (w>>1), trajY[i][c] - (w>>1), null);	
			smallCounters[i] = smallCounters[i] % velocity;	
		}
	}	
}
/**
 * RaceChart constructor comment.
 */
public void setCenterImage(java.awt.Image t) {
	centerImage = t;
	t.getHeight(this);
}
/**
 * RaceChart constructor comment.
 */
public void setImages(java.awt.Image t[]) {
	resetChart();
	images = t;
	orbitNo = t.length;
	widths = new int[orbitNo];
	xRanges = new int[orbitNo];
	yRanges = new int[orbitNo];	
	trajX = new int[orbitNo][];
	trajY = new int[orbitNo][];
	increments = new int[orbitNo];
	counters = new int[orbitNo];
	smallCounters = new int[orbitNo];
	for (int i = 0; i < orbitNo; i++){
		widths[i] = 0;
		increments[i] = 1;
		counters[i] = 0;
		smallCounters[i] = 0;
		int w = images[i].getWidth(this);
		int h = images[i].getHeight(this);
		if (w == -1 || h == -1)
			continue;
		//System.out.println("w=" + w +  "h=" + h);
		widths[i] = Math.max(w, h);
	}
}
/**
 * RaceChart constructor comment.
 */
public synchronized void setRotateRight(boolean t) throws Exception {
	if (t == rotateRight)
		return;

	rotateRight = t;

	for (int i = 0; i < orbitNo; i++){
		smallCounters[i] = -smallCounters[i];
	}

/*	
	if (trajX != null && trajX.length > 0) {
		for (int p = 0; p < trajX.length; p++){
			int k, n2 = trajX[p].length, n = n2 / 2;
			for (int i = 0; i < n; i++) {
				k = trajX[p][i];
				trajX[p][i] = trajX[p][n2 - i - 1];				
			}
		}
	}

	if (trajY != null && trajY.length > 0) {
		for (int p = 0; p < trajY.length; p++){
			int k, n2 = trajY[p].length, n = n2 / 2;
			for (int i = 0; i < n; i++){
				k = trajY[p][i];
				trajY[p][i] = trajY[p][n2 - i - 1];
			}
		}
	}
*/	
}
/**
 * RaceChart constructor comment.
 */
public void setVelocity(int s) throws Exception {
	if (s < 1)
		throw new Exception("Velocity out of range: " + s);
	velocity = s;
}
/**
 * Calculates absolute Y position on eclipse. 
 * 0 <= x < (4 x xRange[index])
 */
protected int trajectoryX(int index, int x) {
	int cx = leftMargin + chartWidth / 2;
	int r = xRanges[index];
	int v = x % r;
	if (x < r)
		return cx + v;
	if (x < 2*r)
		return cx + r - v;
	if (x < 3*r)
		return cx - v;
	return cx - r + v;
}
/**
 * Calculates absolute Y position on eclipse. 
 * 0 <= x < (4 x xRange[index])
 */
protected int trajectoryY(int index, int x) {
	int cy = topMargin + chartHeight / 2;
	int r = xRanges[index];
	int y = function(x % r, r, yRanges[index]);
	int ry = function(r - (x % r), r, yRanges[index]);
	if (x < r)
		return cy + y;
	if (x < 2*r)
		return cy - ry;
	if (x < 3*r)
		return cy - y;
	return cy + ry;
}
/**
 * RaceChart constructor comment.
 */
protected void updateTrajectory(int index) {
	java.util.Vector v = new java.util.Vector();
	int px = trajectoryX(index, 0);
	int py = trajectoryY(index, 0);
	for (int i = 1; i < 4 * xRanges[index]; i++){
		int x = trajectoryX(index, i);
		int y = trajectoryY(index, i);
		while (Math.abs(y - py) > 1) {
			// Add all points between
			py = (y > py ? py + 1 : py - 1);
			v.addElement(new Point(x, py));	
		}
		v.addElement(new Point(x, y));
		px = x;
		py = y;
	}

	trajX[index] = new int[v.size()];
	trajY[index] = new int[v.size()];

	//System.out.println("updated " + v.size());	

	int s = v.size();
	for (int i = 0; i < s; i++){
		trajX[index][i] = ((Point)v.elementAt(s - i - 1)).x;
		trajY[index][i] = ((Point)v.elementAt(s - i - 1)).y;
	}
}
}
