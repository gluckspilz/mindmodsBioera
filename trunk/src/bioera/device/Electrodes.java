/* Electrodes.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.device;

import java.lang.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class Electrodes {	
public Electrodes() {
	super();
}
public final static boolean checkAmplitude(int t[], int threshold) {
	int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
	for (int i = 0; i < t.length; i++){
		if (t[i] < min)
			min = t[i];
		if (t[i] > max)
			max = t[i];
	}

	return max - min > threshold;
}
public final static boolean checkConnection(int t[], int downThresh, int upThresh, int minCount) {
	int downCounter = 0, upCounter = 0;
	for (int i = 0; i < t.length; i++){
		if (t[i] < downThresh)
			downCounter++;
		if (t[i] > upThresh)
			upCounter++;
		if (downCounter > minCount || upCounter > minCount)
			return false;
	}

	return true;
}
}
