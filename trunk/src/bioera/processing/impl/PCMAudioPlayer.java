/* PCMAudioPlayer.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.processing.impl;

import java.io.*;
import java.util.*;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.fft.*;
import bioera.*;
import bioera.properties.*;
import bioera.sound.*;

import javax.sound.sampled.*;



/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class PCMAudioPlayer extends Element {
	public double bufferLength = 0.5;
	public String soundDevice;
	
	private final static String propertiesDescriptions[][] = {
		{"bufferLength", "Buffer length", ""},
	};

	private SourceDataLine dataline;
	private int inb1[], inb2[], chNo = 1, bitsNo, bytesNo, blockSize;
	private byte buf[] = new byte[1024];
	private BufferedScalarPipe in1, in2;

	//private ByteArrayOutputStream testO = new ByteArrayOutputStream();
/**
 * VectorDisplay constructor comment.
 */
public PCMAudioPlayer() {
	super();
	setName("Sound");
	in1 = (BufferedScalarPipe) inputs[0];
	in1.setName("left");
	inb1 = in1.getBuffer();
	in2 = (BufferedScalarPipe) inputs[1];
	in2.setName("right");
	inb2 = in2.getBuffer();	
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Sound player, external or internal";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 2;
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 0;
}
/**
 * Element constructor comment.
 */
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
/**
 * Element constructor comment.
 */
public void info() throws java.lang.Exception {
	System.out.println("Source line info:");
	Line.Info t[] = AudioSystem.getSourceLineInfo(new Line.Info( SourceDataLine.class));
	for (int i = 0; i < t.length; i++){
		System.out.println("  " + t[i]);
	}
	System.out.println("Target line info:");
	t = AudioSystem.getTargetLineInfo(new Line.Info( TargetDataLine.class));
	for (int i = 0; i < t.length; i++){
		System.out.println("  " + t[i]);
	}
	
	System.out.println("Mixer info:");
	javax.sound.sampled.Mixer.Info t1[] = AudioSystem.getMixerInfo();
	for (int i = 0; i < t1.length; i++){
		System.out.println("  " + t1[i]);
	}

	System.out.println("Unsigned PCM encodings info:");
	AudioFormat.Encoding t2[] = AudioSystem.getTargetEncodings(AudioFormat.Encoding.PCM_UNSIGNED);
	for (int i = 0; i < t2.length; i++){
		System.out.println("  " + t2[i]);
	}

	System.out.println("Signed PCM encodings info:");
	t2 = AudioSystem.getTargetEncodings(AudioFormat.Encoding.PCM_SIGNED);
	for (int i = 0; i < t2.length; i++){
		System.out.println("  " + t2[i]);
	}

	AudioFormat	audioFormat = new AudioFormat(
		2000, 
		16,
		1,		// number of channels
		false,	// signed
		false);	// big endian
	
	System.out.println("Target formats for: " + audioFormat);
	AudioFormat t3[] = AudioSystem.getTargetFormats(AudioFormat.Encoding.PCM_UNSIGNED, audioFormat);
	for (int i = 0; i < t3.length; i++){
		System.out.println("  " + t3[i]);
	}
	
}
/**
 * Element constructor comment.
 */
public void initLine() throws java.lang.Exception {
	if (!TOOLS.isNone(soundDevice) && new File(soundDevice).exists()) {
		initSoundDeviceLine();
		return;
	}
	
	AudioFormat	audioFormat = new AudioFormat(
		getSignalParameters().getSignalRate(), 
		bitsNo,
		chNo,	// number of channels
		true,	// signed
		false);	// big endian

	DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
	dataline = (SourceDataLine) AudioSystem.getLine(info);
	dataline.open(audioFormat);//, inb.length << 1);
	dataline.start();

	//System.out.println("buff=" + dataline.getBufferSize());
}
/**
 * Element constructor comment.
 */
public void initSoundDeviceLine() throws java.lang.Exception {
	bioera.nativeobjects.FileAccess fa = new bioera.nativeobjects.FileAccess();
	int handle;
	if ((handle = fa.open(soundDevice, fa.F_WRITEONLY)) == -1)
		throw new Exception("Couldn't open device file '" + soundDevice + "'");

	bioera.nativeobjects.SoundIOCTL ioctl = new bioera.nativeobjects.SoundIOCTL(fa, handle);
	ioctl.dspReset();
	ioctl.setWriteBits(bitsNo);
	ioctl.setWriteChannels(chNo);
	ioctl.setWriteRate(Math.round(getSignalParameters().getSignalRate()));
	ioctl.initWriteBlockSize();
	blockSize = ioctl.getWriteBlockSize();
	ioctl.dspSync();

	//dataline = new DspSourceDataLine(ioctl);

	System.out.println("buff=" + dataline.getBufferSize());
	System.out.println("block=" + blockSize);
}
/**
 * Element constructor comment.
 */
public final void process() throws java.lang.Exception {
	if (in1.isEmpty())
		return;
		
	int n = in1.available();

	if (chNo == 2)
		n = Math.min(n, in2.available());
	
	if (n == 0)
		return;

	if (n > (dataline.available() >> chNo)) {
		n = dataline.available() >> chNo;
	}

	if (n == 0) 
		return;

	if (blockSize != 0) {
		// Write only block sizes
		n = (n * bytesNo * chNo / blockSize) * blockSize;
		n = (n / bytesNo) / chNo;

		if (n == 0)
			return;
	}

	int p = 0;
	if (chNo == 2) {
		for (int i = 0; i < n; i++){
			
			// Left channel
			int m = (inb1[i] + 32768) ^ 32768;
			buf[p++] = (byte) m;
			buf[p++] = (byte) (m >> 8);
			
			// Right channel
			m =  (inb2[i] + 32768) ^ 32768;
			buf[p++] = (byte) m;
			buf[p++] = (byte) (m >> 8);			
		}		
	} else {
		for (int i = 0; i < n; i++){
			int m = (inb1[i] + 32768) ^ 32768;
			buf[p++] = (byte) m;
			buf[p++] = (byte) (m >> 8);
		}
	}

	//System.out.println("Written " + p);	
	dataline.write(buf, 0, p);

	in1.purge(n);
	in2.purge(n);	
}
/**
 * Element constructor comment.
 */
public void reinit() throws java.lang.Exception {
	//info();
	
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		setReinited(true);
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	if (getFirstElementConnectedToInput(1) != null) {		
		chNo = 2;
	} else {
		chNo = 1;
	}
	
	if (bufferLength > 0) {
		in1.setBufferSize((int)(bufferLength * predecessorElement.getSignalParameters().getSignalRate()));
		inb1 = in1.getBuffer();
		if (chNo == 2) {
			in2.setBufferSize((int)(bufferLength * predecessorElement.getSignalParameters().getSignalRate()));
			inb2 = in2.getBuffer();
		}
		buf = new byte[inb1.length << chNo];
		if (debug)
			System.out.println("PCM buff length=" + buf.length);
	}
	
	bitsNo = getSignalParameters().getSignalResolutionBits();
	if (bitsNo <= 8)
		bitsNo = 8;
	else if (bitsNo <= 16)
		bitsNo = 16;
	else
		bitsNo = 24;

	//System.out.println("Bits: " + bitsNo);
		
	bytesNo = bitsNo / 8;

	// Open and close data line to check if it is available
	initLine();	
	dataline.stop();
	dataline.close();

	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start() throws java.lang.Exception {
	initLine();

	//testO = new ByteArrayOutputStream();
}
/**
 * Element constructor comment.
 */
public void stop() throws java.lang.Exception {
	dataline.stop();
	dataline.close();

	//System.out.println("comparing");
	//bioera.tests.TestSoundPlayer t = new bioera.tests.TestSoundPlayer();
	//t.start("C:\\projects\\eeg\\archives\\chimes2.wav");

	//byte b[] = testO.toByteArray();

	//System.out.println("b.len=" + b.length);
	//System.out.println("t.len=" + t.res.length);

	//if (b.length == t.res.length) {
		//for (int i = 0; i < b.length; i++){
			//if (b[i] != t.res[i]) {
				//System.out.println("Different!!!!");
			//}
		//}
		//System.out.println("Results are identical");
	//}
}
}
