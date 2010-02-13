/* Tests.java v 1.0.9   11/6/04 7:15 PM
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
public class Tests {
public Tests() {
	super();
}
public void calculatePower(int freq[], double params[][]) throws Exception {
	int MIN_FREQ = 1;
	int MAX_FREQ = 50;
	int NOISE_MARGIN = 15;
	
	int left = MIN_FREQ, right = MAX_FREQ;
	double powerLeft = 0, powerRight = 0;
	while (left < right) {
		if (powerLeft < powerRight) {
			if (freq[left] < -NOISE_MARGIN || freq[left] > NOISE_MARGIN)
				powerLeft += freq[left];			
			left++;
		} else {
			if (freq[right] < -NOISE_MARGIN || freq[right] > NOISE_MARGIN)
				powerRight += freq[right];
			right--;
		}
	}
	powerLeft += (freq[left] / 2);
	powerRight += (freq[right] / 2);
	

	System.out.println("Result medium freq is " + left);
	System.out.println("Left power " + powerLeft);
	System.out.println("Right power " + powerRight);

	// Assume frequencies are sorted from low to high in params table
	left = 0;
	right = params.length - 1;
	while (params[right][1] > MAX_FREQ)
		right--;
	powerLeft = 0; 
	powerRight = 0;
	while(left <= right) {
		if (powerLeft < powerRight) {
			powerLeft += params[left++][0];
		} else {
			powerRight += params[right--][0];
		}
	}

	System.out.println("Original medium freq is " + (params[left][1] + params[right][1]) / 2);
	System.out.println("Left power " + powerLeft);
	System.out.println("Right power " + powerRight);	
}
public void calculatePrecision(int f1[], double params[][]) throws Exception {
	boolean any; int sum;
	
	System.out.print("Int shift is " + f1[0]);
	sum = 0;
	for (int j = 0; j < params.length; j++){
		sum += params[j][2];
	}
	System.out.println("\terr is " + (((float)sum - f1[0])/sum));
	
	for (int i = 1; i < f1.length; i++){
		if (f1[i] < -15 || f1[i] > 15) {
			System.out.print("int:\t" + i + " is " + + f1[i]);
			any = false;
			for (int j = 0; j < params.length; j++){
				if (params[j][1] == i) {
					System.out.println("\terr " + ((params[j][0]  - f1[i]) / params[j][0]) * 100 + "%");
					any = true;					
				}
			}
			if (!any)
				System.out.println("\tfreq " + i + " not found in source"); 
		}
	}
}
public void filterFFT() throws Exception {
	//FFT fft = new FFT(128, 2048);
	//FFTFilter filter = new FFTFilter(fft);

	//int f[] = new int[fft.SAMPLES_NUMBER];
	//for (int i = 0; i < f.length; i++){
		//f[i] = 100 + (int) (512 * (Math.sin(7 * 2*Math.PI * i / fft.SAMPLES_NUMBER)))
		//+ f[i] = 50 + (int) (512 * (Math.cos(23 * 2*Math.PI * i / fft.SAMPLES_NUMBER)))
		//+ f[i] = 33 + (int) (512 * (Math.cos(47 * 2*Math.PI * i / fft.SAMPLES_NUMBER)))
		//;
	//}	

	//fft.performReIm(f);
	//int f1[] = fft.spectrumRe;
	//int f2[] = fft.spectrumIm;

	//int v;
	//for (int i = 0; i < f1.length; i++){
		//if (Math.abs(f1[i]) > 5 || Math.abs(f2[i]) > 5) {			
			//System.out.println("Freq " + i + " is " + f1[i] + " " + f2[i]);			
		//}
			
	//}

	//int output[] = new int[f.length];
	//filter.bandwidth(0, f.length-1, output);

	//double diff = 0;
	//for (int i = 1; i < 10; i++) {
		//diff += Math.abs(output[i] - f[i]);
////		System.out.println(of[i] + " : " + f[i]);
	//}

	//System.out.println("Total difference " + diff);
	//System.out.println("Average difference " + (diff/f.length));

	//// Calculating fft of the output
	//f1 = fft.perform(output);
	//FFTTools.showSpectrum(f1);
	////for (int i = 0; i < f1.length; i++){
		////if (f1[i] > 5)
			////System.out.println("Freq " + i + " is " + f1[i]);
	////}	
}
public static void main(String args[]) throws Exception {
	try {
		System.out.println("Started ");
		Tests t = new Tests();
		t.t1();
//		t.simpleAll();
//		t.performanceAll();
//		t.simpleFFTInteger();
//		t.performanceFFTCompareWihtNative();
//		t.performanceFFTInteger();
//		t.performanceFFTFloat();
//		for (int i = 5; i < 13; i++){
//			FastFourierTransform1.FACTOR = i;
//			t.fftPerformance();
//		}
		
		//t.sqrt();
		//t.reverse();
		System.out.println("finished");
	} catch (Exception e) {
		System.out.println("Error: " + e + "\n\n");
		e.printStackTrace();
	}
}
public void performanceFFTCompareWihtNative() throws Exception {
	int output[] = new int[1024 / 2];
	FFT fft = FFT.getFFT(FFT.C_INTEGER, 1024, 2048, 0);

	int f[] = new int[fft.samplesNumber];
	for (int i = 0; i < fft.samplesNumber; i++){
		f[i] = 512 + (int) (512 * (Math.sin(5 * 2*Math.PI * i / fft.samplesNumber)))
		+ 512 + (int) (512 * (Math.sin(25 * 2*Math.PI * i / fft.samplesNumber)))
		;
	}	

	long time = System.currentTimeMillis();
	
	int n = 1;
	for (int i = 0; i < n; i++){
		fft.perform(f, output);
	}
	
	System.out.println("Java Total  : " + (System.currentTimeMillis() - time) + " ms");
	System.out.println("Java Average: " + ((System.currentTimeMillis() - time) / n) + " ms");
	
	FFT fftN = FFT.getFFT(FFT.C_NATIVE, 1024, 2048, 0);
	
	time = System.currentTimeMillis();
	
	for (int i = 0; i < n; i++){
		fftN.perform(f, output);
	}
	
	System.out.println("C Total  : " + (System.currentTimeMillis() - time) + " ms");
	System.out.println("C Average: " + ((System.currentTimeMillis() - time) / n) + " ms");
			
}
public void simpleFFTNative() throws Exception {
//	FFT fft_p = new FFT(1024, 2048);
	FFTNative fft_p = new FFTNative(1024, 2048, 0);

	int f[] = new int[1024];
	int output[] = new int[1024/2];
	for (int i = 0; i < 1024; i++){
		f[i] = 512 + (int) (512 * (Math.sin(5 * 2*Math.PI * i / 1024)))
		+ (int) (512 * (Math.sin(25 * 2*Math.PI * i / 1024)))
		;
	}	

	fft_p.perform(f, output);

	for (int i = 0; i < output.length; i++){
		if (output[i] > 5)
			System.out.println("Freq " + i + " is " + FFTTools.sqrt(output[i]));
	}
}
public void sqrt() throws Exception {
	int from = 0, number = 1024*1024;	int a;
	for (int i = from; i < number; i++){
		//System.out.println("i=" + i);
		long s = FFTTools.sqrt(i);
		int m = (int) Math.floor(Math.sqrt(i));		
		if (s != m && s != m + 1) {
			System.out.println("error for " + i);
			System.out.println("int  sqrt = " + FFTTools.sqrt(i));
			System.out.println("real sqrt = " + Math.floor(Math.sqrt(i)));
			return;
		} else {
			//System.out.println("" + i);
		}
	}

	
	System.out.println("OK");
		
}

public void t1() throws Exception {
	int t[] = {1, 2, 3, 2, 1};
	int i = FFTTools.calculateMiddlePowerFreq(t, 0, t.length - 1);
	System.out.println("" + i);
}

public void t2() throws Exception {
	int f[] = new int[1024];
	int f1[] = new int[f.length/2];
	for (int i = 0; i < f.length; i++){
		f[i] = 0
		+ ((int) (512 * (Math.sin(5.5 * 2*Math.PI * i / f.length))))
//		+ ((int) (512 * (Math.sin(25 * 2*Math.PI * i / fft_p.samplesNumber))))
		;
	}	

	int f2[] = new int[f.length * 2];
	for (int i = 0; i < f.length; i++){
		f2[i] = f[i];
	}

	for (int i = f.length; i < f2.length; i++){
		f2[i] = 0;
	}			
	
//	fft_p.performNoSqrt(f, f1);

//	f2 = f;
	//for (int i = 0; i < 10; i++){
		//f2[i] = 0;
		//f2[f.length - 1 - i] = 0;
	//}
	

	int f3[] = new int[f2.length / 2];
	FFT ftn = FFT.getFFT(FFT.C_INTEGER, 1024, 2048, 0);
	ftn.perform(f2, f3);

	//for (int i = 0; i < f1.length; i++){
		//if (f1[i] > 5)
			//System.out.println("Freq " + i + " is " + FFTTools.sqrt(f1[i]));
	//}
	for (int i = 1; i < f3.length; i++){
		if (f3[i] > 25)
			System.out.println("Freq " + i + " is " + f3[i]);
	}
	
}

public void performanceAll() throws Exception {
	performanceFFTInteger();
	performanceFFTLong();
	performanceFFTFloat();
	performanceFFTDouble();
}

public void performanceFFTDouble() throws Exception {
	int output[] = new int[1024 / 2];
	FFT fft = FFT.getFFT(FFT.C_DOUBLE, 1024, 2048, 0);

	int f[] = new int[fft.samplesNumber];
	int par[][] = {
		{5, 227},
		{25, 14}
	};
	for (int i = 0; i < fft.samplesNumber; i++){
		f[i] = 0;
		for (int pi = 0; pi < par.length; pi++){
			f[i] += ((int) (par[pi][1] * (Math.sin(par[pi][0] * 2 * Math.PI * i / fft.samplesNumber))));
		}
	}	

	long time = System.currentTimeMillis();
	
	int n = 200;
	for (int i = 0; i < n; i++){
		fft.perform(f, output);
	}

	long t = System.currentTimeMillis();
	System.out.println("Double Java Total  : " + (t - time) + " ms");
	System.out.println("Double Java Average: " + (((double)t - time) / n) + " ms");
}

public void performanceFFTFloat() throws Exception {
	int output[] = new int[1024 / 2];
	FFT fft = FFT.getFFT(FFT.C_FLOAT, 1024, 2048, 0);

	int f[] = new int[fft.samplesNumber];
	int par[][] = {
		{5, 227},
		{25, 14}
	};
	for (int i = 0; i < fft.samplesNumber; i++){
		f[i] = 0;
		for (int pi = 0; pi < par.length; pi++){
			f[i] += ((int) (par[pi][1] * (Math.sin(par[pi][0] * 2 * Math.PI * i / fft.samplesNumber))));
		}
	}	

	long time = System.currentTimeMillis();
	
	int n = 200;
	for (int i = 0; i < n; i++){
		fft.perform(f, output);
	}

	long t = System.currentTimeMillis();
	System.out.println("Float Java Total  : " + (t - time) + " ms");
	System.out.println("Float Java Average: " + (((double)t - time) / n) + " ms");
}

public void performanceFFTInteger() throws Exception {
	int output[] = new int[1024 / 2];
	FFT fft = FFT.getFFT(FFT.C_INTEGER, 1024, 2048, 0);

	int f[] = new int[fft.samplesNumber];
	int par[][] = {
		{5, 227},
		{25, 14}
	};
	for (int i = 0; i < fft.samplesNumber; i++){
		f[i] = 0;
		for (int pi = 0; pi < par.length; pi++){
			f[i] += ((int) (par[pi][1] * (Math.sin(par[pi][0] * 2 * Math.PI * i / fft.samplesNumber))));
		}
	}	

	long time = System.currentTimeMillis();
	
	int n = 200;
	for (int i = 0; i < n; i++){
		fft.perform(f, output);
	}

	long t = System.currentTimeMillis();
	System.out.println("Int Java Total  : " + (t - time) + " ms");
	System.out.println("Int Java Average: " + (((double)t - time) / n) + " ms");
}

public void performanceFFTLong() throws Exception {
	int output[] = new int[1024 / 2];
	FFT fft = FFT.getFFT(FFT.C_LONG, 1024, 2048, 0);

	int f[] = new int[fft.samplesNumber];
	int par[][] = {
		{5, 227},
		{25, 14}
	};
	for (int i = 0; i < fft.samplesNumber; i++){
		f[i] = 0;
		for (int pi = 0; pi < par.length; pi++){
			f[i] += ((int) (par[pi][1] * (Math.sin(par[pi][0] * 2 * Math.PI * i / fft.samplesNumber))));
		}
	}	

	long time = System.currentTimeMillis();
	
	int n = 200;
	for (int i = 0; i < n; i++){
		fft.perform(f, output);
	}

	long t = System.currentTimeMillis();
	System.out.println("Long Java Total  : " + (t - time) + " ms");
	System.out.println("Long Java Average: " + (((double)t - time) / n) + " ms");
}

public void simpleAll() throws Exception {
	simpleFFTInteger();
	simpleFFTLong();
	simpleFFTFloat();
	simpleFFTDouble();
}

public void simpleFFTDouble() throws Exception {
	FFT fft_p = FFT.getFFT(FFT.C_DOUBLE, 64, 128, 0);

	int f[] = new int[fft_p.samplesNumber];
	int f1[] = new int[fft_p.samplesNumber/2];
	int par[][] = {
		{5, 227},
		{25, 14}
	};
	for (int i = 0; i < fft_p.samplesNumber; i++){
		f[i] = 0;
		for (int pi = 0; pi < par.length; pi++){
			f[i] += ((int) (par[pi][1] * (Math.sin(par[pi][0] * 2 * Math.PI * i / fft_p.samplesNumber))));
		}
	}	

	fft_p.perform(f, f1);

	boolean found = false;
	for (int i = 0; i < f1.length; i++){
		if (f1[i] > 0) {
			System.out.println("Double: freq " + i + " is " + f1[i]);
			found = true;
		}
	}

	if (!found)
		System.out.println("Double: Not found any freq");
}

public void simpleFFTFloat() throws Exception {
	FFT fft_p = FFT.getFFT(FFT.C_FLOAT, 64, 128, 0);

	int f[] = new int[fft_p.samplesNumber];
	int f1[] = new int[fft_p.samplesNumber/2];
	int par[][] = {
		{5, 227},
		{25, 14}
	};
	for (int i = 0; i < fft_p.samplesNumber; i++){
		f[i] = 0;
		for (int pi = 0; pi < par.length; pi++){
			f[i] += ((int) (par[pi][1] * (Math.sin(par[pi][0] * 2 * Math.PI * i / fft_p.samplesNumber))));
		}
	}	

	fft_p.perform(f, f1);

	boolean found = false;
	for (int i = 0; i < f1.length; i++){
		if (f1[i] > 0) {
			System.out.println("Float freq " + i + " is " + f1[i]);
			found = true;
		}
	}

	if (!found)
		System.out.println("Float: Not found any freq");
}

public void simpleFFTInteger() throws Exception {
	FFT fft_p = FFT.getFFT(FFT.C_INTEGER, 1024, 1024, 0);

	int f[] = new int[fft_p.samplesNumber];
	int f1[] = new int[fft_p.samplesNumber/2];
	int par[][] = {
		{5, 227},
		{25, 14}
	};
	for (int i = 0; i < fft_p.samplesNumber; i++){
		f[i] = -400;
		for (int pi = 0; pi < par.length; pi++){
			f[i] += ((int) (par[pi][1] * (Math.sin(par[pi][0] * 2 * Math.PI * i / fft_p.samplesNumber))));
		}
	}	

	fft_p.perform(f, f1);

	for (int i = 0; i < f1.length; i++){
		if (f1[i] > 0)
			System.out.println("Int freq " + i + " is " + f1[i]);
	}
}

public void simpleFFTLong() throws Exception {
	FFT fft_p = FFT.getFFT(FFT.C_LONG, 64, 128, 0);

	int f[] = new int[fft_p.samplesNumber];
	int f1[] = new int[fft_p.samplesNumber/2];
	int par[][] = {
		{5, 227},
		{25, 14}
	};
	for (int i = 0; i < fft_p.samplesNumber; i++){
		f[i] = 0;
		for (int pi = 0; pi < par.length; pi++){
			f[i] += ((int) (par[pi][1] * (Math.sin(par[pi][0] * 2 * Math.PI * i / fft_p.samplesNumber))));
		}
	}	

	fft_p.perform(f, f1);

	for (int i = 0; i < f1.length; i++){
		if (f1[i] > 0)
			System.out.println("Long: freq " + i + " is " + f1[i]);
	}
}
}
