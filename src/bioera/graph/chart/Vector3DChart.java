/* Vector3DChart.java v 1.0.9   11/6/04 7:15 PM
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
public class Vector3DChart extends Matrix3DChart {
	// Internal values
	protected int y; 	// position of points to draw

	protected byte providedColors[][];
	protected byte runtimeColors[][];
	public String colorUnit = "";
	public int valueRange = 4;

	//public int valueMaxDesc = 1024;
	//public int valueMinDesc = 0;
	public boolean invert;

/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public Vector3DChart() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public void distributeColors() {
//	providedColors = null;
	runtimeColors = new byte[valueRange][3];
	if (providedColors == null || providedColors.length < 2) {
		// Colors not defined, create default
		for (int i = 0; i < valueRange; i++){
			Color c = new Color(Color.HSBtoRGB((float)(0.5 - 1.0 * i/valueRange), 0.5f, 0.85f));
			runtimeColors[i][0] = (byte) c.getRed();
			runtimeColors[i][1] = (byte) c.getGreen();
			runtimeColors[i][2] = (byte) c.getBlue();
		}
	} else {
		int m = valueRange - 1;
		int n = providedColors.length - 1;
		for (int i = 0; i <= m; i++){
			int j1 = (i * n) / m;
			int j2 = j1 < n ? j1 + 1 : j1;
			double f = (((double) i) * n) / m - j1;
			for (int k = 0; k < 3; k++){
				int v1 = (providedColors[j1][k] + 128) ^ 128;
				int v2 = (providedColors[j2][k] + 128) ^ 128;
				runtimeColors[i][k] = (byte) Math.round(v1 + (v2 - v1) * f);
				//System.out.println("i=" + i + " j1=" + j1 + " v1=" + v1 + " v2=" + v2 + " res=" + ((runtimeColors[i][k] + 128) ^ 128) + " f=" + f);
			}
		}
	}

	floorColor = new Color(
		(runtimeColors[0][0] + 128) ^ 128, 
		(runtimeColors[0][1] + 128) ^ 128, 
		(runtimeColors[0][2] + 128) ^ 128);
	
	//System.out.println("colors distributed " + runtimeColors.length);
}
/**
 */
public void pushRedraw(int mvect[][], int count) {
	if (!initialized)
		return;

	int k, v;
	byte col[];
	int vect[];
	float fv;
	for (int n = 0; n < count; n++){
		vect = mvect[n];
		y = (y + 1) % mHeight;
		if (y == 0)
			clear();
		for (int i = 0; i < mWidth; i++){
			indexOf(i, y);
			v = vect[invert ? mWidth - i - 1 : i];
			if (v >= valueRange)
				v = valueRange - 1;
			col = runtimeColors[v];
			fv = (scaleZ * v) / valueRange + shiftZ;
			while (--sind >= 0){
				k = sindv[sind];
				setZ(k, fv);
				setColor(k, col);
				//System.out.println("set z=" + (fv + shiftZ) +" at " + k);						setColor(k, col);
			}
		}
	}

	fanArray.updateData(gup);
}
/**
 */
public void pushShift(int mvect[][], int count) {
	if (!initialized)
		return;

	// This can be optimized here, so that for n vectors, the shift is done only once, not n-time.
	// But lets leave it now, since the shift takes relatively short time
	// comparing to rendering
	
	//System.out.println("fansNo="+fansNo + " fansXno=" + fansXno + " fansYno=" + fansYno);
	
	// Shift all vectors back
	int k, p;
	float fv;
	int vect[];
	int v;
	byte col[];
	for (int n = 0; n < count; n++){
		for (int y = mHeight - 1; y > 0; y--){
			for (int x = 0; x < mWidth; x++){
				indexOf(x, y);
				k = sindv[0];
				p = lessY(k);
				fv = getZ(p);
				//System.out.println("k=" + k + " p=" + p + " si=" + si + " x=" + x + " y=" + y);				
				while (true) {
					setZ(k, fv);
					copyColor(p, k);
					if (--sind==0)
						break;
					k = sindv[sind];
				}				
			}
		}
		
		// Draw front vector	
		vect = mvect[n];
		for (int i = 0; i < mWidth; i++){
			indexOf(i, 0);
			v = vect[invert ? mWidth - i - 1 : i];
			if (v >= valueRange)
				v = valueRange - 1;
			col = runtimeColors[v];
			fv = (scaleZ * v) / valueRange + shiftZ;
			if (fv > 1)
				fv = 1;
			while (--sind >= 0){
				k = sindv[sind];
				setZ(k, fv);
				//System.out.println("set z=" + (fv + shiftZ) +" at " + k);			
				setColor(k, col);
			}					
		}
	}

	fanArray.updateData(gup);
}
/**
 * 	Assume 
 *	1. the vector length is the same as xEffectvePixelRange
 *  2. One vector is for one vertical pixel
 */
public void reset() {
	y = -1;
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public void setColors(byte c[][]) {
	providedColors = c;
	distributeColors();
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public void setColors(Color cls[]) {
	providedColors = new byte[cls.length][3];
	for (int i = 0; i < cls.length; i++){
		Color c = cls[i];
		if (c == null)
			throw new RuntimeException("Color not set at " + i);
		providedColors[i][0] = (byte)c.getRed();
		providedColors[i][1] = (byte)c.getGreen();
		providedColors[i][2] = (byte)c.getBlue();
	}
	distributeColors();
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
