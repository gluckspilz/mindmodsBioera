/* FFTFloat.java v 1.0.9   11/6/04 7:15 PM
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

public class FFTFloat extends FFT {
	int n, nu;
    float[] xre;
    float[] xim;
  	float tabsin[];
	float tabcos[];

/**
 * FloatFFT constructor comment.
 */
public FFTFloat(int size, int amp, int bits) {
	super(size, amp, bits);
	n = samplesNumber;
	xre = new float[n];
	xim = new float[n];
}
private int bitrev(int j) {
    int j2;
    int j1 = j;
    int k = 0;
    for (int i = 1; i <= nu; i++) {
        j2 = j1 / 2;
        k = 2 * k + j1 - 2 * j2;
        j1 = j2;
    }
    return k;
}
/**
 * getPrecisionFactor method comment.
 */
public int getPrecisionFactor() {
	return 0;
}
/**
 * init method comment.
 */
public void init() throws java.lang.Exception {
	super.init();
	
	tabsin = new float[n];
	tabcos = new float[n];

	double PI2 = 2 * Math.PI;
	for (int i = 0; i < n; i++){
		tabsin[i] = (float) Math.sin(PI2 * i / n);
		tabcos[i] = (float) Math.cos(PI2 * i / n);
	}	
}
public static void main(String args[]) throws Exception {
	try {
		System.out.println("Started ");
		Tests t = new Tests();
		t.simpleFFTFloat();
		t.performanceFFTFloat();
		System.out.println("finished");
	} catch (Exception e) {
		System.out.println("Error: " + e + "\n\n");
		e.printStackTrace();
	}
}
public final void perform(int[] x, int out[]) {
    nu = NO_OF_BITS;
    int n2 = n / 2;
    int nu1 = nu - 1;
    float tr, ti, arg, c, s;
    for (int i = 0; i < n; i++) {
        xre[i] = x[i];
        xim[i] = 0.0f;
    }
    int k = 0;
    int p;
    for (int l = 1; l <= nu; l++) {
        while (k < n) {
            for (int i = 1; i <= n2; i++) {
                p = bitrev(k >> nu1);
                c = tabcos[p];
                s = tabsin[p];
                tr = xre[k + n2] * c + xim[k + n2] * s;
                ti = xim[k + n2] * c - xre[k + n2] * s;
                xre[k + n2] = xre[k] - tr;
                xim[k + n2] = xim[k] - ti;
                xre[k] += tr;
                xim[k] += ti;
                k++;
            }
            k += n2;
        }
        k = 0;
        nu1--;
        n2 = n2 / 2;
    }
    k = 0;
    int r;
    while (k < n) {
        r = bitrev(k);
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

    out[0] = (int) Math.round(Math.sqrt(xre[0] * xre[0] + xim[0] * xim[0]) * oPBV / n);
    for (int i = 1; i < n / 2; i++)
        out[i] = (int) Math.round(2 * Math.sqrt(xre[i] * xre[i] + xim[i] * xim[i]) * oPBV / n);    
}

public final void performComplex(int[] inputRe, int[] inputIm, int outputRe[], int outputIm[]) {
    nu = NO_OF_BITS;
    int n2 = n / 2;
    int nu1 = nu - 1;
    float tr, ti, arg, c, s;

    // Copy input table from int[] to float[]
    if (inputIm != null) {
	    for (int i = 0; i < n; i++)
	        xre[i] = inputRe[i];	    
    } else {
	    for (int i = 0; i < n; i++)
			xre[i] = 0.0f;
    }    
    if (inputIm != null) {
	    for (int i = 0; i < n; i++)
	        xim[i] = inputIm[i];	    
    } else {
	    for (int i = 0; i < n; i++)
			xim[i] = 0.0f;
    }    
    int k = 0;
    int p;
    for (int l = 1; l <= nu; l++) {
        while (k < n) {
            for (int i = 1; i <= n2; i++) {
                p = bitrev(k >> nu1);
                c = tabcos[p];
                s = tabsin[p];
                tr = xre[k + n2] * c + xim[k + n2] * s;
                ti = xim[k + n2] * c - xre[k + n2] * s;
                xre[k + n2] = xre[k] - tr;
                xim[k + n2] = xim[k] - ti;
                xre[k] += tr;
                xim[k] += ti;
                k++;
            }
            k += n2;
        }
        k = 0;
        nu1--;
        n2 = n2 / 2;
    }
    k = 0;
    int r;
    while (k < n) {
        r = bitrev(k);
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

    float ratio = oPBV / n;
    outputRe[0] = Math.round(xre[0] * ratio);
    outputIm[0] = Math.round(xim[0] * ratio);
    ratio *= 2;
    for (int i = n / 2 - 1; i >= 1; i--) {
        outputRe[i] = Math.round(xre[i] * ratio);
        outputIm[i] = Math.round(xim[i] * ratio);
    }
}
}
