/* PCMAudioSource.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.*;
import bioera.sound.*;
import bioera.properties.*;
import javax.sound.sampled.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class PCMAudioSource extends SingleElement {
	private ComboProperty encoding = new ComboProperty(new String[] {
		"linear",
		"ulaw",
		"alaw"
	});

	public ComboProperty rate = new ComboProperty(new String[] {
		"8000",
		"11025",
		"16000",
		"22050",
		"44100",
	});

	public ComboProperty sampleSize = new ComboProperty(new String[] {
		"8",
		"16",
	});

	public String soundDevice;
		
	private ComboProperty sign = new ComboProperty(new String[] {
		"signed",
		"unsigned",
	});
	
	private ComboProperty endian = new ComboProperty(new String[] {
		"little endian",
		"big endian",
	});

	public ComboProperty channels = new ComboProperty(new String[] {
		"mono",
		"stereo",
	});	
	
	private final static String propertiesDescriptions[][] = {
//		{"", "", ""},
	};	

	
	private ScalarPipeDistributor out[];
	private int inb[];
	private int chNo, counter, ssize;//, zeroLevel;
	private boolean lEndian;
	protected TargetDataLine line;
	private AudioFormat audioFormat;
	private byte fBuf[];

	private int rateValue, bitsValue, channelsValue;

	protected static boolean debug = bioera.Debugger.get("impl.audio.source");
/**
 * Element constructor comment.
 */
public PCMAudioSource() {
	super();
	setName("SoundReader");
	outputs = out = new ScalarPipeDistributor[2];
	outputs[0] = out[0] = new ScalarPipeDistributor(this);
	out[0].setName("1");
	outputs[1] = out[1] = new ScalarPipeDistributor(this);
	out[1].setName("2");
}
/**
 * Element constructor comment.
 */
public void close() throws Exception {
	if (line != null) {
		if (debug)
			System.out.println("Line closed");
		line.stop();
		line.close();
		line = null;
	}		
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Audio stream source";
}
/**
 * Element constructor comment.
 */
public AudioFormat getFormat() {
	if (debug)
		System.out.println("Get the audio format");
    AudioFormat.Encoding encoding = AudioFormat.Encoding.ULAW;
    String encString = this.encoding.getSelectedItem();
    float rate = Float.valueOf(this.rate.getSelectedItem()).floatValue();
    ssize = Integer.valueOf(this.sampleSize.getSelectedItem()).intValue();
    String signedString = sign.getSelectedItem();
    lEndian = (endian.getSelectedItem()).startsWith("little");
    chNo = channels.getSelectedItem().equals("mono") ? 1 : 2;

    if (encString.equals("linear")) {
        if (signedString.equals("signed")) {
            encoding = AudioFormat.Encoding.PCM_SIGNED;
        } else {
            encoding = AudioFormat.Encoding.PCM_UNSIGNED;
        }
    } else
        if (encString.equals("alaw")) {
            encoding = AudioFormat.Encoding.ALAW;
        }

    return new AudioFormat(
        encoding,
        rate,
        ssize,
        chNo,
        (ssize / 8) * chNo,
        rate,
        !lEndian);
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 0;
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 2;
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
public void initLine()  throws Exception {
	if (!TOOLS.isNone(soundDevice) && new File(soundDevice).exists()) {
		initSoundDeviceLine();
		return;
	}

	//System.out.println("opening");
	DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
    line = (TargetDataLine) AudioSystem.getLine(info);
    line.open(audioFormat, line.getBufferSize());
   	fBuf = new byte[line.getBufferSize()];
    //System.out.println("Opened: " + line + "\ninfo: " + info);
    line.start();	
}
/**
 * Element constructor comment.
 */
public void initSoundDeviceLine() throws java.lang.Exception {
	if (debug)
		System.out.println("initing native sound line");
	bioera.nativeobjects.FileAccess fa = new bioera.nativeobjects.FileAccess();
	int handle;
	if ((handle = fa.open(soundDevice, fa.F_READONLY)) == -1)
		throw new Exception("Couldn't open native device file '" + soundDevice + "'");
	bioera.nativeobjects.SoundIOCTL ioctl = new bioera.nativeobjects.SoundIOCTL(fa, handle);
	if (0 != ioctl.dspReset())
		throw new DesignException("Couldn't reset native DSP");
	int format = audioFormat.getSampleSizeInBits() == 8 ? ioctl.AFMT_S8 : ioctl.AFMT_S16_LE;
	if (0 != ioctl.setFormat(format))
		throw new DesignException("Couldn't set native format to " + format); 
	if (0 != ioctl.setReadBits(audioFormat.getSampleSizeInBits()))
		throw new DesignException("Couldn't set native bits to " + audioFormat.getSampleSizeInBits());
	if (0 != ioctl.setReadChannels(audioFormat.getChannels()))
		throw new DesignException("Couldn't set native channels to " + audioFormat.getChannels());
	if (0 != ioctl.setReadRate((int) audioFormat.getSampleRate()))
		throw new DesignException("Couldn't set native channels to " + audioFormat.getSampleRate());
	if (0 != ioctl.dspSync())
		throw new DesignException("Couldn't synchronize native DSP device"); 

	if (debug) {
		System.out.println("Set read bits: " + ioctl.getReadBits());
		System.out.println("Set read channels: " + ioctl.getReadChannels());
		System.out.println("Set read rate: " + ioctl.getReadRate());
		System.out.println("Set format: " + ioctl.getFormat());
	}

	setOutputSignalRate(ioctl.getReadRate());
	setOutputResolutionBits(ioctl.getReadBits());	
	
	line = new DspTargetDataLine(ioctl);
	fBuf = new byte[line.getBufferSize()];
	fa.setBufferLength(fBuf.length);

	if (debug)
		System.out.println("buff=" + fBuf.length);
}
/**
 * Element constructor comment.
 */
public void process() throws Exception {
//    int n = line.available();
//	if (n == 0)
//    	return;

	//System.out.println("processing"); 
    int i, ind = 0;
    int n = line.read(fBuf, 0, fBuf.length);

    //System.out.println("avail=" + n); 
    //System.out.println("received " + n);
    while (ind < n) {
	    counter++;
	    i = (((int) fBuf[ind++]) + 128) ^ 128;
	    if (ssize == 16) {
	        i += (((((int) fBuf[ind++]) + 128) ^ 128) << 8);
	        i = (i ^ 32768) - 32768;
	    } else {
	        i = (i ^ 128) - 128;
	    }
	    out[0].write(i);

	    if (chNo > 1) {
		    i = (((int) fBuf[ind++]) + 128) ^ 128;
		    if (ssize == 16) {
		        i += (((((int) fBuf[ind++]) + 128) ^ 128) << 8);
		        i = (i ^ 32768) - 32768;
		    } else {
		        i = (i ^ 128) - 128;
		    }
		    out[1].write(i);		    
	    }
    }   
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (encoding.getSelectedIndex() == -1)
		encoding.setSelectedIndex(0);

	if (rate.getSelectedIndex() == -1)
		rate.setSelectedIndex(0);

	if (sampleSize.getSelectedIndex() == -1)
		sampleSize.setSelectedIndex(1);

	if (sign.getSelectedIndex() == -1)
		sign.setSelectedIndex(0);

	if (endian.getSelectedIndex() == -1)
		endian.setSelectedIndex(0);

	if (channels.getSelectedIndex() == -1)
		channels.setSelectedIndex(0);
	
	audioFormat = getFormat();

	setOutputSignalRate((int) audioFormat.getSampleRate());
	setOutputResolutionBits(ssize);
	setOutputDigitalRange(1 << ssize);
	setOutputPhysicalRange(1 << ssize);

	if (ssize != 8 && ssize != 16) {
		System.out.println("Sample size " + ssize + " not implemented, only 8 or 16 allowed");
	}

	if (debug) {
		System.out.println("Sample rate: " + audioFormat.getSampleRate());
		System.out.println("Channels   : " + chNo);
		System.out.println("Encoding   : " + audioFormat.getEncoding());
		System.out.println("Frame rate : " + audioFormat.getFrameRate());
		System.out.println("Frame size : " + audioFormat.getFrameSize());
		System.out.println("Sample size: " + ssize);
		System.out.println("Big endian : " + !lEndian);
		System.out.println("Format     : " + audioFormat);
	}

	// To make sure there are no problems
	initLine();	
	close();

	super.reinit();
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2003 1:22:12 PM)
 */
public void start() throws Exception {
	if (debug)
		System.out.println("Starting PCMAudioSource");
	
	initLine();

	if (debug)
		System.out.println("Started PCMAudioSource");	
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2003 1:22:12 PM)
 */
public void stop() throws Exception {
	close();
}
}
