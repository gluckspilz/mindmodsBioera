/* DynamicSound.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.sound;

import java.io.*;
import bioera.fft.FFT;
import javax.sound.sampled.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class DynamicSound {
	public final static String SAMPLE_FILE_PATH = "/tmp/sample.wav";
	public final static int SAMPLE_RATE = 8000;
	public final static int SAMPLE_CHANNELS = 1;
	public final static int SAMPLE_BITS = 8;	// Unsigned
	public final static int SOUND_CARD_BUFFER = 1024;
	
	public final int soundSample[];
	public final byte outputSample[]; // about 1 second

	public final static int VOLUME_MIN = 1;
	public final static int VOLUME_MED = 5;
	public final static int VOLUME_MAX = 9;

	public final static double SQRT_12 = 1.0594630943592952645618252949463;
	public final static int FACTOR_BITS = 10;
	public final int FACTOR = (int) Math.round(SQRT_12 * (1 << FACTOR_BITS));

	public final int MAX_PITCH_INCREASE = 4;
		
	private int OUTPUT_DEVICE;
	public final static int OUTPUT_DEVICE_STREAM = 1;
	OutputStream outputStream;
	
	public final static int OUTPUT_DEVICE_JAVASOUND = 2;
	SourceDataLine	line = null;
/**
 * Sound constructor comment.
 */
public DynamicSound() throws Exception {
	super();

	loadSample();

	soundSample = loadSample();
	
	outputSample = new byte[(soundSample.length * MAX_PITCH_INCREASE / SOUND_CARD_BUFFER + 1) * SOUND_CARD_BUFFER];

	
		
	//if ("x86".equals(System.getProperty("os.arch"))) {
		//initJavaSound();
	//} else {
		OUTPUT_DEVICE = OUTPUT_DEVICE_STREAM;
//		outputStream = new FileOutputStream("/dev/sound/dsp");
		outputStream = new FileOutputStream("/programs/ssh/testout");
//	}
}
/**
 * 	Volume 1 - 9
 *	Freq are negative or positive integers
 */
public void beep(int volume, int freq) throws Exception {
	if (freq >= MAX_PITCH_INCREASE * 12)
		throw new RuntimeException("Match pitch increase it to high: " + freq + " allowed max is " + (MAX_PITCH_INCREASE * 12));

	if (volume > VOLUME_MAX || volume < VOLUME_MIN)
		throw new RuntimeException("Volume out of range: " + volume);

	// Calculate destination sample length
	int length = soundSample.length;

	// Positive increase
	for (int i = 0; i < freq; i++){
		length = (length * FACTOR) >> FACTOR_BITS;
	}

	// Negative decrease
	for (int i = 0; i > freq ; i--){
		length = (length << FACTOR_BITS) / FACTOR;
	}

	System.out.println("sound sample = " + soundSample.length);
	System.out.println("dest sample = " + length);


	if (length > soundSample.length) {
		int d1 = 0, d2;
		for (int i = 0; i < length; i++){
			d2 = i * soundSample.length / length;
			if (d2 == d1) {
				// approximate
				outputSample[i] = (byte) ((soundSample[d2] + soundSample[d2+1]) / 2);
				
			} else {
				outputSample[i] = (byte) soundSample[d2];
			}
			
			d1 = d2;
		}		
	} else {	
		for (int i = 0; i < length; i++){
			outputSample[i] = (byte) soundSample[i * soundSample.length / length];
		}
	}

	playSample(length);
}
/**
 * Sound constructor comment.
 */
public void beepSin(int milliseconds, int hertz) throws Exception {
/*	
	// Fill sample with sound wave
	int t[] = new int[outputSample.length];
	int obw[] = {0, 0, 2, 7, 9, 9, 9, 9, 8, 7, 5, 4, 5, 3, 2, 1, 0, 0, 0};	
	for (int i = 0; i < SAMPLE_LENGTH; i++){
		outputSample[i] = (byte) (BASE + (
			(obw[(i * obw.length) / SAMPLE_LENGTH] 
			*AMPLITUDE 
			* tabsin[(hertz * tabsin.length * i / SAMPLE_LENGTH) % tabsin.length]
			/ 10) 
		    >> SIN_FACTOR_BITS));
	}

	playSample();
*/	
/*		
	while (milliseconds > TIME_OF_SAMPLE_MILLIS) {
		playSample();
		milliseconds -= TIME_OF_SAMPLE_MILLIS;
	}

	if (milliseconds > 0) {
		int silentFrom = SAMPLE_LENGTH * milliseconds / TIME_OF_SAMPLE_MILLIS;
		for (int i = silentFrom; i < SAMPLE_LENGTH; i++){
			sample[i] = (byte) BASE;
		}
		playSample();
	}
*/	
}
/**
 * Sound constructor comment.
 */
public void close() throws Exception {
	if (outputStream != null)
		outputStream.close();
	if (line != null)
		line.close();
}
/**
 * Sound constructor comment.
 */
public void initJavaSound() throws Exception {
	OUTPUT_DEVICE = OUTPUT_DEVICE_JAVASOUND;
	AudioFormat audioFormat = new AudioFormat(
		AudioFormat.Encoding.PCM_SIGNED,
		(float) 22050,
		16,
		2,
		4,
		(float) 22050,
		true
	);

	DataLine.Info	info = new DataLine.Info(SourceDataLine.class, audioFormat);
	line = (SourceDataLine) AudioSystem.getLine(info);
	line.open(audioFormat);
	line.start();
}
/**
 * Sound constructor comment.
 */
public int[] loadSample() throws Exception {
	File f = new File(SAMPLE_FILE_PATH);

    AudioInputStream audioInputStream = null;//new WaveFileReader().getAudioInputStream(f);
    AudioFormat audioFormat = audioInputStream.getFormat();
	//System.out.println("Audio format: " + audioFormat + "  class: " + audioFormat.getClass());

	int out[] = new int[(int) f.length()];
	int ch, index = 0;
	while ((ch = audioInputStream.read()) != -1) {
		out[index++] = ch;
	}

	int ret[] = new int[index];
	System.arraycopy(out, 0, ret, 0, index);
	
	return ret;
}
/**
 * Sound constructor comment.
 */
public int[] loadSample1() throws Exception {
	
	int b[] = new int[120];
	for (int i = 0; i < b.length; i++){
		b[i] = (int) (127.0 + (100 * Math.sin(Math.PI * 2 * i / b.length)));
	}
		
	return b;
}
public static void main(String args[]) throws Exception {
	
    try {
	        System.out.println("started");

	        //Sound s = new Sound(VOLUME_MED);
	        new DynamicSound().beep(5, 2);
	        
        System.out.println("finished");
        System.exit(0);
    } catch (Exception e) {
        System.out.println("Error: " + e + "\n\n");
        e.printStackTrace();
    }
    
}
/**
 * Sound constructor comment.
 */
public void playSample() throws Exception {
/*	
	switch (OUTPUT_DEVICE) {
		case OUTPUT_DEVICE_JAVASOUND:
//			byte b[] = new byte[outputSample.length * 4];
			byte b[] = new byte[18300];
			for (int i = 0; i < b.length - 4; i+=4){
				b[i] = b[i+2] = outputSample[i*SAMPLE_LENGTH/b.length];;;
				b[i+1] = b[i+3] = 0;
			}
			line.write(b, 0, b.length);
			line.drain();
			break;
		case OUTPUT_DEVICE_STREAM:
			outputStream.write(outputSample);
			break;
		default:
			throw new Exception("Unknown output device");
	};
	
	
/*	
	for (int i = 0; i < sample.length; i++){
		System.out.print("" + (sample[i] < 0 ? 256+sample[i] : sample[i]) + " ");
	}
	System.out.println("");
*/	
}
/**
 * Sound constructor comment.
 */
public void playSample(int length) throws Exception {
	// Fill table
	int total = (length / SOUND_CARD_BUFFER + 1) * SOUND_CARD_BUFFER;
	for (int i = length; i < total; i++){
		outputSample[i] = 127;
	}
	outputStream.write(outputSample, 0, total);
	outputStream.flush();
}
/**
 * Sound constructor comment.
 */
public void test(String args[]) throws Exception {
	//double i = (1000 * tabsin[(30 * tabsin.length)/360]) >> SIN_FACTOR_BITS;
	//System.out.println("i=" + i);
	//i = 1000.0 * Math.sin(30 * 2 * Math.PI / 360);
	//System.out.println("i=" + i);
	

	if (args.length > 0)
		beep(1200, Integer.parseInt(args[0]));
	else
		beep(1200, 800);
	
	//for (int i = 0; i < outputSample.length; i++){
		//System.out.print(" " + outputSample[i]);
	//}

	//playSample();

/*	        
	        byte t[] = new byte[8192];
	        
	        byte obw[] = {110, 100, 100, 95, 70, 60, 50, 40, 30, 20, 10, 5, 0};
	        
	        for (int i = 0; i < t.length; i++){
	        	t[i] = (byte) (200 + obw[obw.length * i /t.length] / 2 * Math.cos(2*Math.PI*(i % 30) / 30));
	        }

	        FileOutputStream out = new FileOutputStream("/dev/sound/audio");	        
	        out.write(t);
	        
//	        System.out.print("|" + t[0] + "|");
/*	        
			FileInputStream in = new FileInputStream("/dev/sound/dsp");
			for (int i = 0; i < 1; ){
				int v = 0;
				for (int j = 0; j < 1024; j++) {
					v += in.read();					
				}
				System.out.println(" " + (v/1024));
			}
			System.out.println("");
*/			


// System


	//Object o = AudioSystem.getAudioFileFormat(new File("C:\\temp\\sound\\ding.wav"));
	//System.out.println("Audio file format: " + o);

	//Object b[] = AudioSystem.getAudioFileReaders();
	//for (int i = 0; i < b.length; i++){
		//System.out.println("Readers: " + b[i]);
	//}

/*	
	Object b[] = AudioSystem.getAudioFileTypes();
	for (int i = 0; i < b.length; i++){
		System.out.println("Type: " + b[i]);
	}

	b = AudioSystem.getMixerInfo();
	for (int i = 0; i < b.length; i++){
		System.out.println("Mixer: " + b[i]);
	}

*/



}
}
