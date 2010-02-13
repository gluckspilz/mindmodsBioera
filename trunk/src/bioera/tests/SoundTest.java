/* SoundTest.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.tests;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import javax.sound.midi.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class SoundTest {
	private SourceDataLine dataline;
	private int b[], chNo = 1, bitsNo, bytesNo, zeroLevel;
	public byte res[];
/**
 * VectorDisplay constructor comment.
 */
public SoundTest() {
	super();
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
private Instrument[] listAvailableInstruments(Synthesizer synth) {
    Instrument[] instr = synth.getAvailableInstruments();
    if (instr.length == 0)
    	return instr;
    String t[] = new String[instr.length];
    for (int i = 0; i < instr.length; i++) {
        //System.out.println(i + "   " + instr[i].getName());
        t[i] = instr[i].getName();
    }
    System.out.println("Loaded " + t.length + " instruments");
    if (t.length > 0)
    	System.out.println("Instr[0]=" + instr[0]);
    	
    return instr;
}
/**
 * Element constructor comment.
 */
private static void listAvailableMidiDevices() throws Exception {
	Vector v = new Vector();
	MidiDevice.Info[] aInfos = MidiSystem.getMidiDeviceInfo();
	for (int i = 0; i < aInfos.length; i++)
	{
		MidiDevice	device = MidiSystem.getMidiDevice(aInfos[i]);
		boolean		bAllowsInput = (device.getMaxTransmitters() != 0);
		boolean		bAllowsOutput = (device.getMaxReceivers() != 0);
		if (bAllowsOutput) {
			v.add(aInfos[i].getName());
		}
		System.out.println(""+i+"  "
			+(bAllowsInput?"IN ":"   ")
			+(bAllowsOutput?"OUT ":"    ")
			+aInfos[i].getName()+", "
			+aInfos[i].getVendor()+", "
			+aInfos[i].getVersion()+", "
			+aInfos[i].getDescription());
	}
}
/**
 * Element constructor comment.
 */
 public static void main(String args[])
        throws Exception
    {
        try
        {
            SoundTest t = new SoundTest();
            System.out.println("started");
            t.listSampledSources();
            //t.startSampled(args[0]);
            //listAvailableMidiDevices();
            //t.startMidi();
            System.out.println("done");
            System.exit(1);
        }
        catch(Exception e)
        {
            System.out.println("Error: " + e + "\n\n");
            e.printStackTrace();
        }
    }
/**
 * Element constructor comment.
 */
public void startMidi() throws java.lang.Exception {
	Synthesizer synth = MidiSystem.getSynthesizer();
	synth.open();

	Instrument instr[] = synth.getAvailableInstruments() ;
	System.out.println("Instr[0]=" + instr[0]);
	//listAvailableInstruments(synth);
	System.out.println("Instr loaded: " + synth.loadInstrument(instr[0]));
	
//	Receiver midiReceiver = synth.getReceiver();

//	midiReceiver.s

	//for (int ist = 0; ist < instr.length; ist++){
		//MidiChannel c[] = synth.getChannels();
		//for (int i = 0; i < c.length; i++){
			//c[i].programChange(ist);
		//}

		//System.out.println("Playing instrument #" + ist);
		//for (int i = 0; i < c.length; i++){
			//c[i].noteOn(64, 64);		
		//}
		//Thread.sleep(1000);
		//for (int i = 0; i < c.length; i++){
			//c[i].noteOff(64);		
		//}
	//}

	MidiChannel c[] = synth.getChannels();
	System.out.println("Channel numer=" + c.length);
	//for (int i = 0; i < c.length; i++){
		//c[i].programChange(ist);
	//}

	c[11].setSolo(true);
	c[11].noteOn(64, 100);
	Thread.sleep(2000);
	c[11].setPitchBend(4000);
	Thread.sleep(5000);
	c[11].noteOff(64);		

	
	for (int ins = 0; ins < c.length; ins++){
//		synth.loadInstrument(instr[ins]);
		System.out.println("Channel: " + ins);
		c[ins].noteOn(64, 32);
		Thread.sleep(1000);
		c[ins].noteOff(64);		
	}
	//for (int i = 0; i < c.length; i++){
		//c[i].noteOff(64);		
	//}
	
}
/**
 * Element constructor comment.
 */
public void startMidi1() throws java.lang.Exception {
	Synthesizer synth = MidiSystem.getSynthesizer();
	synth.open();

	Instrument instr[] = listAvailableInstruments(synth);
	
	Receiver midiReceiver = synth.getReceiver();

	//for (int ist = 0; ist < instr.length; ist++){
		//MidiChannel c[] = synth.getChannels();
		//for (int i = 0; i < c.length; i++){
			//c[i].programChange(ist);
		//}

		//System.out.println("Playing instrument #" + ist);
		//for (int i = 0; i < c.length; i++){
			//c[i].noteOn(64, 64);		
		//}
		//Thread.sleep(1000);
		//for (int i = 0; i < c.length; i++){
			//c[i].noteOff(64);		
		//}
	//}

	int ist = 19;
	
	MidiChannel c[] = synth.getChannels();
	for (int i = 0; i < c.length; i++){
		c[i].programChange(ist);
	}

	System.out.println("Playing instrument #" + ist);
	for (int i = 0; i < c.length; i++){
		c[i].noteOn(64, 64);		
	}
	Thread.sleep(3000);
	for (int i = 0; i < c.length; i++){
		c[i].noteOff(64);		
	}
	
}
/**
 * Element constructor comment.
 */
public void startSampled(String filepath) throws java.lang.Exception {
//	info();

	System.out.println("-------------------------------------");
	System.out.println("Sampled sound\n");
		
	AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filepath));
	AudioFormat	audioFormat = audioInputStream.getFormat();

System.out.println("format: " + audioFormat);	
	
	DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
	SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
	line.open(audioFormat, 8192*2);
	line.start();
	int	nBytesRead = 0;
	byte[]	abData = new byte[8192];
	int i = 0, total = 0;
	System.out.println("line buf=" + line.getBufferSize());
	while ((nBytesRead = audioInputStream.read(abData, 0, abData.length)) != -1) {
		if (line.available() < nBytesRead)
			Thread.sleep(100);
		line.write(abData, 0, nBytesRead);
		System.out.println("wrote " + i++ + "  " + (total+=nBytesRead));
//		out.write(abData, 0, nBytesRead);
	}

	line.drain();
	line.close();

	System.out.println("Sampled sound was played");
	System.out.println("-------------------------------------");
}

/**
 * Element constructor comment.
 */
public void listSampledSources() throws java.lang.Exception {
//    info();

	AudioFormat	format = null;
	 // 8 kHz, 8 bit, mono
	// audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000.0F, 8, 1, 1, 8000.0F, true);
	// 44.1 kHz, 16 bit, stereo
	format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, false);
	DataLine.Info	info = new DataLine.Info(TargetDataLine.class, format);

	TargetDataLine line;

	System.out.println("getting");
    line = (TargetDataLine) AudioSystem.getLine(info);
    line.open(format, line.getBufferSize());
    System.out.println("Opened: " + line + "\ninfo: " + info);
    line.start();

	Control ct[] = line.getControls();
	System.out.println("controls: " + ct.length);
	for (int i = 0; i < ct.length; i++){
		System.out.println("c: " + ct[i]);
	}
    
    byte b[] = new byte[88];
    int ret = line.read(b, 0, b.length);
    System.out.println("" + ret);
	
}

/**
 * Element constructor comment.
 */
public void listSampledSourcesMixers() throws java.lang.Exception {
//	info();
		
	Mixer.Info mi[] = AudioSystem.getMixerInfo();

	Mixer mixer = null;
	for (int i = 0; i < mi.length; i++){
		if (mi[i].getName().indexOf("AK5370") != -1) {
			mixer = AudioSystem.getMixer(mi[i]);
			Line lines[] = mixer.getSourceLines();
			System.out.println("listed source " + lines.length);
			for (int j = 0; j < lines.length; j++){
				System.out.println("mixer line: " + lines[j]);
			}

			lines = mixer.getTargetLines();
			System.out.println("listed target " + lines.length);
			for (int j = 0; j < lines.length; j++){
				System.out.println("mixer line: " + lines[j]);
			}
			
			Line.Info info = mixer.getLineInfo();
			System.out.println("line info: " + info);
		}
	}

	if (mixer == null)
		throw new Exception("No mixer");

	//AudioFormat	audioFormat = null;
	 //// 8 kHz, 8 bit, mono
	//// audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000.0F, 8, 1, 1, 8000.0F, true);
	//// 44.1 kHz, 16 bit, stereo
	//audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, false);
	//DataLine.Info	info = new DataLine.Info(SourceDataLine.class, audioFormat);

	
	//SourceDataLine	targetDataLine = null;
	//try
	//{
		//targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
		//targetDataLine.open(audioFormat);
	//}
	//catch (LineUnavailableException e)
	//{
		//System.out.println("unable to get a recording line");
		//e.printStackTrace();
		//System.exit(1);
	//}
	
		

	//DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
	//SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
	//line.open(audioFormat, 8192*2);
	//line.start();
	//int	nBytesRead = 0;
	//byte[]	abData = new byte[8192];
	//int i = 0, total = 0;
	//System.out.println("line buf=" + line.getBufferSize());
	//while ((nBytesRead = audioInputStream.read(abData, 0, abData.length)) != -1) {
		//if (line.available() < nBytesRead)
			//Thread.sleep(100);
		//line.write(abData, 0, nBytesRead);
		//System.out.println("wrote " + i++ + "  " + (total+=nBytesRead));
////		out.write(abData, 0, nBytesRead);
	//}

	//line.drain();
	//line.close();

	//System.out.println("Sampled sound was played");
	//System.out.println("-------------------------------------");
	
}
}
