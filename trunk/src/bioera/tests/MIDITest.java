/* MIDITest.java v 1.0.9   11/6/04 7:15 PM
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

import javax.sound.midi.*;
import java.io.*;
import java.util.*;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.graph.designer.*;
import bioera.fft.*;
import bioera.*;
import bioera.sound.*;
import bioera.properties.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class MIDITest extends Element {
	public int velocity = 127;
	public boolean restartSound = true;

	private final static String propertiesDescriptions[][] = {
		{"velocity", "Velocity [0-127]", ""},		
	};
	
	public ComboProperty midiDevice = new ComboProperty(new String[] {
		"none"
	});

	public ComboProperty instrument = new ComboProperty(new String[] {
		"none"
	});
		
	private Receiver midiReceiver;
	private Synthesizer synth;
	private ShortMessage message = new ShortMessage();	

	private int prevNote = -1;
				
	private int pB[], vB[], vol = 128;
	private BufferedScalarPipe in, in2;
	private WavSoundPlayer soundPlayer;

	protected static boolean debug = bioera.Debugger.get("impl.midi");
/**
 * VectorDisplay constructor comment.
 */
public MIDITest() {
	super();
	setName("MIDI");
	inputs = new BufferedScalarPipe[2];
	inputs[0] = in = new BufferedScalarPipe(this);
	in.setName("PITCH");
	pB = in.getBuffer();
	inputs[1] = in2 = new BufferedScalarPipe(this);
	in2.setName("VOL");
	vB = in2.getBuffer();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "MIDI sound feedback";
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
private static MidiDevice.Info getMidiDeviceInfo(String strDeviceName, boolean forOutput) throws MidiUnavailableException {
    MidiDevice.Info[] aInfos = MidiSystem.getMidiDeviceInfo();
    for (int i = 0; i < aInfos.length; i++) {
        if (aInfos[i].getName().equals(strDeviceName)) {
            MidiDevice device = MidiSystem.getMidiDevice(aInfos[i]);
            boolean bAllowsInput = (device.getMaxTransmitters() != 0);
            boolean bAllowsOutput = (device.getMaxReceivers() != 0);
            if ((bAllowsOutput && forOutput) || (bAllowsInput && !forOutput)) {
                return aInfos[i];
            }
        }
    }
    return null;
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
private void listAvailableInstruments() {
    Instrument[] instr = synth.getAvailableInstruments();
    if (instr.length == 0)
    	return;
    String t[] = new String[instr.length];
    for (int i = 0; i < instr.length; i++) {
        //System.out.println(i + "   " + instr[i].getName());
        t[i] = instr[i].getName();
    }
    this.instrument.setItems(t);    
}
/**
 * Element constructor comment.
 */
private void listAvailableMidiDevices() throws Exception {
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
		//System.out.println(""+i+"  "
			//+(bAllowsInput?"IN ":"   ")
			//+(bAllowsOutput?"OUT ":"    ")
			//+aInfos[i].getName()+", "
			//+aInfos[i].getVendor()+", "
			//+aInfos[i].getVersion()+", "
			//+aInfos[i].getDescription());
	}

	if (v.size() == 0)
		return;

	String t[] = new String[v.size() + 1];
	t[0] = "Default Synthesizer";
	for (int i = 0; i < v.size(); i++){
		t[i + 1] = "" + v.elementAt(i);
	}
	midiDevice.setItems(t);
}
/**
 * Element constructor comment.
 */
public final void process() throws java.lang.Exception {
/*	
	int n = in.available();

	if (n == 0)
		return;

	// Play only the last value
	n = in.getBuffer()[n - 1];
	
	if (!restartSound && n == prevNote)
		return;
	
	// Turn off the previous note
	if (prevNote != -1) {
		message.setMessage(ShortMessage.NOTE_OFF, 0, prevNote, velocity);
		midiReceiver.send(message, -1);
	}	

	if (debug)	
		System.out.println("#" + n);

	if (in2.available() > 0) {
		vol = vB[in2.available() - 1];
		in2.purgeAll();
	}
	
	message.setMessage(ShortMessage.NOTE_ON, 0, n, vol);
	midiReceiver.send(message, -1);
	
	prevNote = n;
		
	in.purgeAll();
*/	
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	verifyDesignState(velocity > 0 && velocity < 128);
	
	listAvailableMidiDevices();

	if (in2.isConnected())
		vol = 25;
	else
		vol = velocity;

	if (midiDevice.getSelectedIndex() > 0 && midiDevice.getSelectedIndex() < midiDevice.getItems().length) {
		if (debug)
			System.out.println("loading midi device");				
		MidiDevice.Info	info = getMidiDeviceInfo(midiDevice.getItems()[midiDevice.getSelectedIndex()], true);
		MidiDevice dev = MidiSystem.getMidiDevice(info);
		midiReceiver = dev.getReceiver();
		synth = null;
		instrument.setItems(new String[] {"none"});
		instrument.setSelectedIndex(0);
	} else {
		if (debug)
			System.out.println("loading synthesizer");		
		synth = MidiSystem.getSynthesizer();
		synth.open();
		midiReceiver = synth.getReceiver();

		if (midiDevice.getSelectedIndex() == -1)
			midiDevice.setSelectedIndex(0);
		
		listAvailableInstruments();

		if (instrument.getSelectedIndex() == -1) {
			// Load default instrument if it exists
			instrument.setSelectedIndex(0);
			String instr = "Nylon Str Guitar";
			for (int i = 0; i < instrument.getItems().length; i++){
				if (instr.equals(instrument.getItems()[i])) {
					instrument.setSelectedIndex(i);
					break;
				}
			}			
		}
		
		MidiChannel c[] = synth.getChannels();
		for (int i = 0; i < c.length; i++){
			c[i].programChange(instrument.getSelectedIndex());
		}
	}
			
	super.reinit();	
}
/**
 * Element constructor comment.
 */
public void stop() throws Exception {
	if (prevNote != -1) {
		message.setMessage(ShortMessage.NOTE_OFF, 0, prevNote, velocity);
		midiReceiver.send(message, -1);
		prevNote = -1;
	}	
}
}
