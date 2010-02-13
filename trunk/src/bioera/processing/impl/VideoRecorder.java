/* VideoRecorder.java v 1.0.9   11/6/04 7:15 PM
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
import java.net.*;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.fft.*;
import bioera.*;
import bioera.properties.*;
import bioera.sound.*;
import bioera.graph.chart.*;
import bioera.media.*;
import java.awt.event.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class VideoRecorder extends Element {
	public BrowseFile filePath = new BrowseFile("none");

	public ComboProperty videoDevices = new ComboProperty(new String[]{
		"none"
	});

	public ComboProperty videoFormats = new ComboProperty(new String[]{
		"none"
	});

	public ComboProperty audioDevices = new ComboProperty(new String[]{
		"none"
	});

	public ComboProperty audioFormats = new ComboProperty(new String[]{
		"none"
	});

	public ComboProperty implementation = new ComboProperty(new String[]{
		"JMF",
//		"QUICKTIME"
	});

	public ComboProperty outputFormat = new ComboProperty(new String[]{
		"AVI",
		"MOV",
	});
	
	private final static String propertiesDescriptions[][] = {
		{"videoDevices", "Video device", ""},
		{"videoFormats", "Video format", ""},
		{"audioDevices", "Audio device", ""},
		{"audioFormats", "Audio format", ""},
		{"outputFormat", "Output format", ""},
		{"filePath", "Path", ""},
	};

	int lastVD = -2, lastVF = -2, lastAD = -2, lastAF = -2, lastIM = -2, lastOF = -2;
	
    // media Recorder
    VRecorder recorder = null;
 
    BufferedLogicalPipe inOnOff;
    int inOnOffBuf[];
    boolean isOn = false;

     
	public static final boolean debug = bioera.Debugger.get("impl.videorecorder");
/**
 * VectorDisplay constructor comment.
 */
public VideoRecorder() {
	super();
	setName("V_Recorder");

	inputs = new BufferedLogicalPipe[1];
	inputs[0] = inOnOff = new BufferedLogicalPipe(this);
	inOnOff.setName("On/Off");
	inOnOffBuf = inOnOff.getBuffer();
	
}
/**
 * Element constructor comment.
 */
public void close() throws java.lang.Exception {
	if (recorder != null) {
		recorder.close();
		recorder = null;
	}
}
/**
 * Element constructor comment.
 */
public void destroy() throws Exception {
	close();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Video Recorder";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 1;
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
private void initializeAudioDevicesAndFormats() throws Exception {
	JMFRecorder r = (JMFRecorder) recorder;
	
	// Audio
	List devices = r.getAudioDevices();
	if (devices.size() == 0) {
		audioDevices.setItems(new String[]{});
		audioFormats.setItems(new String[]{});
		return;
	}
	String ad[] = (String[]) devices.toArray(new String[0]);
	audioDevices.setItems(ad);
	if (audioDevices.getSelectedIndex() == -1) {
		audioDevices.setSelectedIndex(0);
	}
	String devName = audioDevices.getSelectedItem();
	List formats = r.getAudioFormats(devName);
	if (formats.size() == 0) {
		audioFormats.setItems(new String[]{});
		return;
	}
	String f[] = (String[]) formats.toArray(new String[0]);
	audioFormats.setItems(f);
	if (audioFormats.getSelectedIndex() == -1) {
		audioFormats.setSelectedIndex(0);
	}
	
//	// For debugging only
//	for (int i = 0; i < devices.size(); i++) {
//		String deviceName = (String) devices.get(i);
//		System.out.println("Device "+i+": " + deviceName);
//		List forms = r.getAudioFormats(deviceName);
//		for (int j = 0; j < formats.size(); j++) {
//			System.out.println("Format "+i+": " + forms.get(j));
//		}		
//	}
}
private void initializeDevicesAndFormats() throws Exception {
	initializeAudioDevicesAndFormats();
	initializeVideoDevicesAndFormats();
}
private void initializeVideoDevicesAndFormats() throws Exception {
	JMFRecorder r = (JMFRecorder) recorder;
	
	// Audio
	List devices = r.getVideoDevices();
	if (devices.size() == 0) {
		videoDevices.setItems(new String[]{});
		videoFormats.setItems(new String[]{});
		throw new Exception("Video devices not found");
	}
	String ad[] = (String[]) devices.toArray(new String[0]);
	videoDevices.setItems(ad);
	if (videoDevices.getSelectedIndex() == -1) {
		videoDevices.setSelectedIndex(0);
	}
	String devName = videoDevices.getSelectedItem();
	List formats = r.getVideoFormats(devName);
	if (formats.size() == 0) {
		videoFormats.setItems(new String[]{});
		throw new Exception("Video formats not found");
	}
	String f[] = (String[]) formats.toArray(new String[0]);
	videoFormats.setItems(f);
	if (videoFormats.getSelectedIndex() == -1) {
		videoFormats.setSelectedIndex(0);
	}
	
	// For debugging only
//	for (int i = 0; i < devices.size(); i++) {
//		String deviceName = (String) devices.get(i);
//		System.out.println("Device "+i+": " + deviceName);
//		List forms = r.getAudioFormats(deviceName);
//		for (int j = 0; j < formats.size(); j++) {
//			System.out.println("Format "+i+": " + forms.get(j));
//		}		
//	}
}
/**
 * Element constructor comment.
 */
public final void process() throws java.lang.Exception {
	if (inOnOff.isEmpty())
		return;

	int v = inOnOffBuf[inOnOff.available() - 1];

	if (isOn && v == 0) {
		stop();
		isOn = false;	
	} else if (!isOn && v != 0) {
		start();
		isOn = true;	
	}
	
	inOnOff.purgeAll();
	
}
public void reinit() throws java.lang.Exception {
	if (implementation.getSelectedIndex() == -1)
		implementation.setSelectedIndex(0);

	if (outputFormat.getSelectedIndex() == -1)
		outputFormat.setSelectedIndex(0);

	if (lastIM == implementation.getSelectedIndex()
		&& lastVD == videoDevices.getSelectedIndex()
		&& lastVF == videoFormats.getSelectedIndex()
		&& lastAD == audioDevices.getSelectedIndex()
		&& lastAF == audioFormats.getSelectedIndex()
		&& lastOF == outputFormat.getSelectedIndex() 
		&& recorder.lastPath.equals(filePath.path)) {
		super.reinit();
		return;
	}
		
	close();

	if (implementation.getSelectedIndex() == 0)
		recorder = new JMFRecorder(this);

	initializeDevicesAndFormats();
	recorder.reinit();
		
	lastIM = implementation.getSelectedIndex();
	lastVD = videoDevices.getSelectedIndex();
	lastVF = videoFormats.getSelectedIndex();
	lastAD = audioDevices.getSelectedIndex();
	lastAF = audioFormats.getSelectedIndex();
	lastOF = outputFormat.getSelectedIndex();
	recorder.lastPath = filePath.path;

	if (inOnOff.isConnected())
		isOn = true;
	
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start() throws java.lang.Exception {
	if (recorder != null && !isOn) {
		recorder.start();
		isOn = true;
		setStatusChar('R', 2, 1);
		if (debug)
			System.out.println("recorder started");
	}
}
/**
 * Element constructor comment.
 */
public void stop() throws Exception {
	if (recorder != null && isOn) {
		recorder.stop();
		setStatusChar('S', 2, 0);
		isOn = false;
		if (debug)
			System.out.println("recorder stopped");		
	}
}
}
