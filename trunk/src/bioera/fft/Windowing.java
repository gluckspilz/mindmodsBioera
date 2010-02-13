/* Windowing.java v 1.0.9   11/6/04 7:15 PM
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

public class Windowing {
	public final static String items[] = {
		"NONE",
		"TRIANGULAR",
		"HANNING",
		"HAMMING",
		"BLACKMAN",
	};

	
	public static final int W_NONE = 0;
	public static final int W_TRIANGULAR = 1;
	public static final int W_HANNING = 2;
	public static final int W_HAMMING = 3;
	public static final int W_BLACKMAN = 4;

	int type;
	int length;
	int fact;

	int window[];
/**
 * Windowing constructor comment.
 */
public Windowing(int itype, int ilength, int ratio) {
	super();

	type = itype;
	length = ilength;
	fact = ratio;
}
/**
 * 
 */
public int getRatio(int base) {
	int below = 0;
	for (int j = 0; j < window.length; j++){
		below += window[j];
	}

	return (int) Math.round(1.0 * base * below / (length * fact));
}
/**
 * Windowing constructor comment.
 */
public int [] getWindow() {
	window = new int[length];
	int half = length / 2;
	int h = half - 1;
	int l = h * 2;

	switch (type) {
		case W_NONE:
			for (int i = 0; i < half; i++){
				window[i] = fact;
			}
			break;
		case W_TRIANGULAR:
			for (int i = 0; i < half; i++){
				window[i] = (int) (1.0 * fact * i / h);
			}			
			break;
		case W_HAMMING:
			for (int i = 0; i < half; i++){
				window[i] = (int) (fact * (0.54 - 0.46 * Math.cos(2 * Math.PI * i / l)));
			}			
			break;
		case W_HANNING:
			for (int i = 0; i < half; i++){
				window[i] = (int) (fact * (0.5 - 0.5 * Math.cos(2 * Math.PI * i / l)));
			}			
			break;
		case W_BLACKMAN:
			for (int i = 0; i < half; i++){
				window[i] = (int) (fact * (0.42 - 0.5 * Math.cos(2 * Math.PI * i / length) + 0.08 * Math.cos(4 * Math.PI * i / l)));
			}			
			break;
		default:
			throw new RuntimeException("Unknown type " + type);
			
	}

	window[half] = fact;  // This is in case of uneven sample number

	for (int i = 0; i < half; i++){
		window[length - i - 1] = window[i];
	}
	
	return window;
}
public static void main(String args[]) throws Exception {
	try {
		System.out.println("Started ");
		int div = 1024;
		int len = 128;
		for (int i = 0; i < 5; i++){
			Windowing w = new Windowing(i, len, div);
			int t[] = w.getWindow();
			System.out.println(items[i] + " ratio is " + w.getRatio(100) +  "%");
		}
		System.out.println("finished");
	} catch (Exception e) {
		System.out.println("Error: " + e + "\n\n");
		e.printStackTrace();
	}
}

/**
 * 	Returns window with normalized power, 
 *  total power before and after is the same
 *  That means, that max amplitude can be greater then 1.0
 */
public int [] getPowerWindow() {
	getWindow();

	double power = 0;
	for (int j = 0; j < length; j++){
		power += window[j];
	}

	double ratio = power / (length * fact);

	for (int i = 0; i < length; i++){
		window[i] = (int) Math.round(window[i] / ratio);
	}

	return window;
}
}
