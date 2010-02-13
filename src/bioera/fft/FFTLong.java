/* FFTLong.java v 1.0.9   11/6/04 7:15 PM
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
public class FFTLong extends FFT {
	// Those values can be changed
	public int NO_OF_BITS; //	number of bits in sample number	
	public int MAX_AMPLITUDE_BITS; // max number of bits in signal amplitude
	
	// Those values should not be modified
	public int FACTOR;
	public int FACTOR_DIVIDE;
	public int NO_OF_BITS_LESS_1;
	public int SAMPLES_NUMBER;
	public int SAMPLES_NUMBER_2;
	public int MAX_AMPLITUDE;
	public int MAX_AMPLITUDE_2;
		
	public long tabsin[];
	public long tabcos[];

    private long xre[];
	private long xim[];

	public long spectrumRe[];
	public long spectrumIm[];
	public int result[];
public FFTLong(int sampleLength, int maxAmplitude, int bits) {
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
/**
 * getSpectrumIm method comment.
 */
public int[] getSpectrumIm() {
	return null;
}
/**
 * getSpectrumRe method comment.
 */
public int[] getSpectrumRe() {
	return null;
}
public void init() throws Exception {
	super.init();

	FACTOR = 63 - (NO_OF_BITS + MAX_AMPLITUDE_BITS);
	FACTOR_DIVIDE = 1 << FACTOR;
	NO_OF_BITS_LESS_1 = NO_OF_BITS - 1;
	SAMPLES_NUMBER = 1 << NO_OF_BITS;
	SAMPLES_NUMBER_2 = SAMPLES_NUMBER * SAMPLES_NUMBER;
	MAX_AMPLITUDE = 1 << MAX_AMPLITUDE_BITS;
	MAX_AMPLITUDE_2 = MAX_AMPLITUDE * MAX_AMPLITUDE;

	tabsin = new long[SAMPLES_NUMBER];
	tabcos = new long[SAMPLES_NUMBER];
		
	int fact = 1 << FACTOR;
	double PI2 = 2 * Math.PI;
	for (int i = 0; i < SAMPLES_NUMBER; i++){
		tabsin[i] = (long) (fact * Math.sin(PI2 * i / SAMPLES_NUMBER));
		tabcos[i] = (long) (fact * Math.cos(PI2 * i / SAMPLES_NUMBER));
	}

    xre = new long[SAMPLES_NUMBER];
	xim = new long[SAMPLES_NUMBER];		
	
    spectrumRe = new long[SAMPLES_NUMBER/2];
	spectrumIm = new long[SAMPLES_NUMBER/2];
	result = new int[SAMPLES_NUMBER/2];
}
public static void main(String args[]) throws Exception {
	try {
		System.out.println("Started ");
		Tests t = new Tests();
		t.simpleFFTLong();
		t.performanceFFTLong();
		System.out.println("finished");
	} catch (Exception e) {
		System.out.println("Error: " + e + "\n\n");
		e.printStackTrace();
	}
}
public final void perform(int[] input, int[] output) {
	performReIm(input);
	for (int i = 0; i < result.length; i++){
		output[i] = FFTTools.sqrt(spectrumRe[i] * spectrumRe[i] + spectrumIm[i] * spectrumIm[i]);
	}
}
public final void performReIm(int[] input) {
	performReIm(input, spectrumRe, spectrumIm);
}
private final void performReIm(int[] input, long outputRe[], long outputIm[]) {
	int outputLength = SAMPLES_NUMBER >> 1;

    int bits_decr = NO_OF_BITS - 1;
    for (int i = 0; i < SAMPLES_NUMBER; i++) {
        xre[i] = input[i];
        xim[i] = 0;
    }

    long c, s, tr, ti; 
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

    outputRe[0] = Math.abs(xre[0]) >> NO_OF_BITS;
    outputIm[0] = Math.abs(xim[0]) >> NO_OF_BITS;
    for (int i = 1; i < SAMPLES_NUMBER >> 1; i++) {
		outputRe[i] = Math.abs(xre[i]) >> NO_OF_BITS_LESS_1;
		outputIm[i] = Math.abs(xim[i]) >> NO_OF_BITS_LESS_1;
    }     
}
}
