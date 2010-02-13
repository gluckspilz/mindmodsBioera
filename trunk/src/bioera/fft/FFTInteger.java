/* FFTInteger.java v 1.0.9   11/6/04 7:15 PM
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
public class FFTInteger extends FFT {
	// Those values should not be modified
	public int FACTOR;
	public int FACTOR_DIVIDE;
	public int NO_OF_BITS_LESS_1;
	public int NO_OF_BITS_LESS_2;
	public int SAMPLES_NUMBER;
	public int SAMPLES_NUMBER_2;
	public int MAX_AMPLITUDE;
	public int MAX_AMPLITUDE_2;
		
	public int tabsin[];
	public int tabcos[];

    private int xre[];
	private int xim[];

	public int spectrumRe[];
	public int spectrumIm[];
	public int result[];
public FFTInteger(int sampleLength, int maxAmplitude, int bits) {
	super(sampleLength, maxAmplitude, bits);
}
private final int brev(int j) {
    int j2, k = 0, j1 = j;
    for (int i = 1; i <= NO_OF_BITS; i++) {
        j2 = j1>>1;
        k = (k<<1) + j1 - (j2<<1);
        j1 = j2;
    }
    return k;

}
public int getPrecisionFactor() {
	return FACTOR;
}
public int[] getSpectrumIm() {
	return spectrumIm;
}
public int[] getSpectrumRe() {
	return spectrumRe;
}
public void init() throws Exception {
	super.init();

	FACTOR = 31 - (NO_OF_BITS + MAX_AMPLITUDE_BITS);
	FACTOR_DIVIDE = 1 << FACTOR;
	NO_OF_BITS_LESS_1 = NO_OF_BITS - 1;
	NO_OF_BITS_LESS_2 = NO_OF_BITS - 2;
	SAMPLES_NUMBER = 1 << NO_OF_BITS;
	SAMPLES_NUMBER_2 = SAMPLES_NUMBER * SAMPLES_NUMBER;
	MAX_AMPLITUDE = 1 << MAX_AMPLITUDE_BITS;
	MAX_AMPLITUDE_2 = MAX_AMPLITUDE * MAX_AMPLITUDE;

	tabsin = new int[SAMPLES_NUMBER];
	tabcos = new int[SAMPLES_NUMBER];
		
	int fact = 1 << FACTOR;
	double PI2 = 2 * Math.PI;
	for (int i = 0; i < SAMPLES_NUMBER; i++){
		tabsin[i] = (int) (fact * Math.sin(PI2 * i / SAMPLES_NUMBER));
		tabcos[i] = (int) (fact * Math.cos(PI2 * i / SAMPLES_NUMBER));
	}

    xre = new int[SAMPLES_NUMBER];
	xim = new int[SAMPLES_NUMBER];		
	
    spectrumRe = new int[SAMPLES_NUMBER/2];
	spectrumIm = new int[SAMPLES_NUMBER/2];
	result = new int[SAMPLES_NUMBER/2];
}
public static void main(String args[]) throws Exception {
	try {
		System.out.println("Started ");
		Tests t = new Tests();
		t.simpleFFTInteger();
		t.performanceFFTInteger();
		System.out.println("finished");
	} catch (Exception e) {
		System.out.println("Error: " + e + "\n\n");
		e.printStackTrace();
	}
}
public final void perform(int[] input, int[] output) {
	performNoSqrt(input, output);
	FFTTools.sqrt(output);
}
public final void performComplex(int[] inputRe, int[] inputIm, int outputRe[], int outputIm[]) {
	int outputLength = SAMPLES_NUMBER >> 1;

    int bits_decr = NO_OF_BITS - 1;
    int tr, ti, arg, c, s, div;

    if (inputRe != null)
	    System.arraycopy(inputRe, 0, xre, 0, SAMPLES_NUMBER);
	else {
		for (int i = 0; i < SAMPLES_NUMBER; i++)
			xre[i] = 0;
	}

	if (inputIm != null)
	    System.arraycopy(inputIm, 0, xim, 0, SAMPLES_NUMBER);
	else {
		for (int i = 0; i < SAMPLES_NUMBER; i++)
			xim[i] = 0;
	}
   
    int k = 0;    
    int p2;
    div = 1 << FACTOR;
    for (int l = 1; l <= NO_OF_BITS; l++) {
        while (k < SAMPLES_NUMBER) {
            for (int i = 1; i <= outputLength; i++) {
				p2 = brev(k >> bits_decr);
                c = tabcos[p2];
                s = tabsin[p2];
                tr = ((xre[k + outputLength] * c + xim[k + outputLength] * s)) / div;
                ti = ((xim[k + outputLength] * c - xre[k + outputLength] * s)) / div;
                xre[k + outputLength] = xre[k] - tr;
                xim[k + outputLength] = xim[k] - ti;
                xre[k] += tr;
                xim[k] += ti;
                k++;
            }
            k += outputLength;
        }
        k = 0;
        bits_decr--;
        outputLength = outputLength / 2;
    }

    k = 0;
    int r;
    while (k < SAMPLES_NUMBER) {
        r = brev(k);
        if (r > k) {
            tr = xre[k];
            ti = xim[k];
            xre[k] = xre[r];
            xim[k] = xim[r];
            xre[r] = tr;
            xim[r] = ti;
        }
        k++;
    }

    div = 1 << (NO_OF_BITS - oPB);
    outputRe[0] = xre[0] / div;
    outputIm[0] = xim[0] / div;
    div >>= 1;
    for (int i = 1; i < SAMPLES_NUMBER >> 1; i++) {
	    outputRe[i] = xre[i] / div;
	    outputIm[i] = xim[i] / div;
    }
}
public final int[] performNoSqrt(int[] input) {
	performNoSqrt(input, result);
	return result;
}
public final void performNoSqrt(int input[], int output[]) {
	performReIm(input);
	for (int i = 0; i < result.length; i++){
		output[i] = spectrumRe[i] * spectrumRe[i] + spectrumIm[i] * spectrumIm[i];
	}
}
public final void performReIm(int[] input) {
	performReIm(input, spectrumRe, spectrumIm);
}
private final void performReIm(int[] input, int outputRe[], int outputIm[]) {
	int outputLength = SAMPLES_NUMBER >> 1;

    int bits_decr = NO_OF_BITS - 1;
    int tr, ti, arg, c, s;
    
    System.arraycopy(input, 0, xre, 0, SAMPLES_NUMBER);
    for (int i = 0; i < SAMPLES_NUMBER; i++) {
        xim[i] = 0;
    }
   
    int k = 0;
    int p2;
    for (int l = 1; l <= NO_OF_BITS; l++) {
        while (k < SAMPLES_NUMBER) {
            for (int i = 1; i <= outputLength; i++) {
				p2 = brev(k >> bits_decr);
                c = tabcos[p2];
                s = tabsin[p2];

                tr = (xre[k + outputLength] * c + xim[k + outputLength] * s);
                if (tr < 0)
	               tr = -((-tr) >> FACTOR);
	            else
	            	tr >>= FACTOR;
                ti = (xim[k + outputLength] * c - xre[k + outputLength] * s);
                if (ti < 0)
	            	ti = -((-ti) >> FACTOR);
	            else
	            	ti >>= FACTOR;                
                xre[k + outputLength] = xre[k] - tr;
                xim[k + outputLength] = xim[k] - ti;
                xre[k] += tr;
                xim[k] += ti;
                k++;
            }
            k += outputLength;
        }
        k = 0;
        bits_decr--;
        outputLength = outputLength / 2;
    }

    k = 0;
    int r;
    while (k < SAMPLES_NUMBER) {
        r = brev(k);
        if (r > k) {
            tr = xre[k];
            ti = xim[k];
            xre[k] = xre[r];
            xim[k] = xim[r];
            xre[r] = tr;
            xim[r] = ti;
        }
        k++;
    }

    outputRe[0] = Math.abs(xre[0]) >> (NO_OF_BITS - oPB);
    outputIm[0] = Math.abs(xim[0]) >> (NO_OF_BITS - oPB);
    for (int i = 1; i < SAMPLES_NUMBER >> 1; i++) {
		outputRe[i] = Math.abs(xre[i]) >> (NO_OF_BITS_LESS_1 - oPB);
		outputIm[i] = Math.abs(xim[i]) >> (NO_OF_BITS_LESS_1 - oPB);
    }     
}
}
