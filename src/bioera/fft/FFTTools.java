/* FFTTools.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.fft;

import java.lang.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class FFTTools {
	public final static int NOISE_MARGIN = 0;
	
public final static int calculateMiddlePowerFreq(int spectrum[], int leftIndex, int rightIndex) {	
	double powerLeft = 0, powerRight = 0;
	while (leftIndex < rightIndex) {
		if (powerLeft < powerRight) {
			if (spectrum[leftIndex] < -NOISE_MARGIN || spectrum[leftIndex] > NOISE_MARGIN)
				powerLeft += spectrum[leftIndex];			
			leftIndex++;
		} else {
			if (spectrum[rightIndex] < -NOISE_MARGIN || spectrum[rightIndex] > NOISE_MARGIN)
				powerRight += spectrum[rightIndex];
			rightIndex--;
		}
	}
	//powerLeft += (spectrum[leftIndex] / 2);
	//powerRight += (spectrum[rightIndex] / 2);
	

//	System.out.println("Result medium freq is " + left);
//	System.out.println("Left power " + powerLeft);
//	System.out.println("Right power " + powerRight);

	return leftIndex;
}
public final static int calculateMiddlePowerFreq2(int spectrum[], int left, int right) {
	long powerLeft = 0, powerRight = 0;
	while (left < right) {
		if (powerLeft < powerRight) {
			if (spectrum[left] < -NOISE_MARGIN || spectrum[left] > NOISE_MARGIN) {
				powerLeft += (spectrum[left]*spectrum[left]);
//				System.out.println("-" + spectrum[left]*spectrum[left] + "\t " + spectrum[left] + "\t " + powerLeft + " (" + left + ")");
			}
			left++;			
		} else {
			if (spectrum[right] < -NOISE_MARGIN || spectrum[right] > NOISE_MARGIN) {
				powerRight += (spectrum[right] * spectrum[right]);
//				System.out.println("+" + spectrum[right]*spectrum[right] + "\t " + spectrum[right] + "\t " + powerRight + " (" + right + ")");
			}
			right--;			
		}
	}
	powerLeft += (spectrum[left] / 2);
	powerRight += (spectrum[right] / 2);
	

//	System.out.println("Result medium freq is " + left);
//	System.out.println("Left power " + powerLeft);
//	System.out.println("Right power " + powerRight);

	return left;
}
public final static int findMaxFreq(int spectrum[], int left, int right, int window) {
	if (right - left < window)
		return -1;

	int maxIndex = 0, sum, maxsum = 0;
	for (int i = left; i < right - window; i++){
		sum = 0;
		for (int j = 0; j < window; j++){
			sum += spectrum[i + j];
		}
		if (sum > maxsum) {
			maxIndex = i;
			maxsum = sum;
		}
	}
	

	return maxIndex + (window/2);
}
public final static int[] showSpectrum(int table[]) {
	for (int i = 0 ; i < table.length; i++){
		if (Math.abs(table[i]) > 5) {
			System.out.println("Freq " + i + "  amp " + table[i]);
		}
	}
	return table;
}
public final static int[] sqrt(int table[]) {
	for (int i = table.length - 1; i >= 0 ; i--){
		table[i] = FFTTools.sqrt(table[i]);
	}
	return table;
}
public final static int sqrt(int arg) {
    if (arg <= 0)
    	return 0;
	
    int x, y = Integer.MAX_VALUE;
    int c = arg >> 1;
    
    // First estimate    	
    x = 2;
    while (x < c) {
        x <<= 1;
        c >>= 1;
    }

    // Then find precisely
    while (x < y) {
        y = x;
        x = (x + arg/x) >> 1;
    }
    
    return (int) x;
}

public final static long[] sqrt(long table[]) {
	for (int i = table.length - 1; i >= 0 ; i--){
		table[i] = FFTTools.sqrt(table[i]);
	}
	return table;
}

public final static int sqrt(long arg) {
    if (arg <= 0)
    	return 0;
	
    long x, y = Long.MAX_VALUE;
    long c = arg >> 1;
    
    // First estimate    	
    x = 2;
    while (x < c) {
        x <<= 1;
        c >>= 1;
    }

    // Then find precisely
    while (x < y) {
        y = x;
        x = (x + arg/x) >> 1;
    }
    
    return (int) x;
}
}
