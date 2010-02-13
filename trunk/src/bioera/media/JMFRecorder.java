/* JMFRecorder.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.media;

import java.io.*;
import java.util.*;
import java.net.*;

import java.awt.event.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.datasink.*;
import javax.media.format.*;
import javax.media.protocol.*;

import bioera.processing.impl.VideoRecorder;

/**
 * Creation date: (11/5/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class JMFRecorder extends VRecorder implements Runnable {
	// media Player
	private CaptureDeviceInfo	captureVideoDevice = null;
	private CaptureDeviceInfo	captureAudioDevice = null;
	private VideoFormat		captureVideoFormat = null;
	private AudioFormat		captureAudioFormat = null;

	Processor processor = null;
    JMFRecordingDataSinkListener dataSinkListener;
    DataSink dataSink;
    ProcessorModel processorModel;
    MediaLocator destMediaLocator;
    DataSource mixedDataSource;
    Format outputFormat[];
    FileTypeDescriptor outputType;

    private boolean reinitingProcessor = false;
	public JMFRecorder(VideoRecorder ivr) {
		super(ivr);
	}
private void checkDeviceList(Vector deviceListVector) throws Exception {
    if (deviceListVector == null) {
        throw new Exception("Error: media device list vector is null, init aborted");        
    }
    if (deviceListVector.size() == 0) {
        throw new Exception("Error: media device list vector size is 0, init aborted");
    }	
}
public void close() throws java.lang.Exception {
	if (dataSink != null) {
	    if (vr.debug)
	        System.out.println("Closing datasink");
	    try {
			dataSink.close();
	    } catch (Throwable e) {
	    }
		dataSink = null;
	}
	
	if (processor != null) {
	    if (vr.debug)
	        System.out.println("Closing processor");
	    try {
		    processor.deallocate();
			processor.close();
	    } catch (Throwable e) {
	    }
		processor = null;
	}

    if (vr.debug)
        System.out.println("Closed");	
}
public List getAudioDevices() throws java.lang.Exception {
	ArrayList ret = new ArrayList();
    Vector deviceListVector = CaptureDeviceManager.getDeviceList(null);
    checkDeviceList(deviceListVector);

    for (int x = 0; x < deviceListVector.size(); x++) {
        CaptureDeviceInfo deviceInfo = (CaptureDeviceInfo) deviceListVector.elementAt(x);
        Format deviceFormat[] = deviceInfo.getFormats();
        for (int y = 0; y < deviceFormat.length; y++) {
			if (deviceFormat[y] instanceof AudioFormat) {
				ret.add(deviceInfo.getName());
				break;
            }
        }
    }

	return ret;
}
public List getAudioFormats(String deviceName) throws java.lang.Exception {
	return getFormats(deviceName);
}
public List getFormats(String deviceName) throws java.lang.Exception {
	ArrayList ret = new ArrayList();

    java.util.Vector deviceListVector = CaptureDeviceManager.getDeviceList(null);
	checkDeviceList(deviceListVector);

    for (int x = 0; x < deviceListVector.size(); x++) {
        CaptureDeviceInfo deviceInfo = (CaptureDeviceInfo) deviceListVector.elementAt(x);
        String deviceInfoName = deviceInfo.getName();

		if (!deviceName.equals(deviceInfoName))
			continue;

        Format deviceFormat[] = deviceInfo.getFormats();
        for (int y = 0; y < deviceFormat.length; y++) {
            ret.add(RecordingTools.formatToString(deviceFormat[y]));
        }
    }

	return ret;
}
public List getVideoDevices() throws java.lang.Exception {
	ArrayList ret = new ArrayList();
    Vector deviceListVector = CaptureDeviceManager.getDeviceList(null);
    checkDeviceList(deviceListVector);

    for (int x = 0; x < deviceListVector.size(); x++) {
        CaptureDeviceInfo deviceInfo = (CaptureDeviceInfo) deviceListVector.elementAt(x);
        Format deviceFormat[] = deviceInfo.getFormats();
        for (int y = 0; y < deviceFormat.length; y++) {
			if (deviceFormat[y] instanceof VideoFormat) {
				ret.add(deviceInfo.getName());
				break;
            }
        }
    }

	return ret;
}
public List getVideoFormats(String deviceName) throws java.lang.Exception {
	return getFormats(deviceName);
}
/**
 * Element constructor comment.
 */
public void initProcessor() throws java.lang.Exception {
	if (vr.debug)
		System.out.println("Starting JMF processor init");
		
    MediaLocator videoMediaLocator = captureVideoDevice.getLocator();
    DataSource videoDataSource = Manager.createDataSource(videoMediaLocator);

	if (vr.debug)
		System.out.println("Setting video format (" + captureVideoFormat + ")");

    if (!RecordingTools.setFormat(videoDataSource, captureVideoFormat)) {
        throw new Exception("Error: unable to set video format - program aborted");
    }

    MediaLocator audioMediaLocator = captureAudioDevice.getLocator();
    DataSource audioDataSource = Manager.createDataSource(audioMediaLocator);

	if (vr.debug)
		System.out.println("Setting audio format: (" + captureAudioFormat + ")");
    
    if (!RecordingTools.setFormat(audioDataSource, captureAudioFormat)) {
        throw new Exception("Error: unable to set audio format - program aborted");
    }

    DataSource dArray[] = new DataSource[2];
    dArray[0] = videoDataSource;
    dArray[1] = audioDataSource;
    mixedDataSource = javax.media.Manager.createMergingDataSource(dArray);

    outputFormat = new Format[2];
    String name = vr.filePath.path;
    if ("AVI".equals(vr.outputFormat.getSelectedItem())) {
	    outputFormat[0] = new VideoFormat(VideoFormat.INDEO50);
	    outputFormat[1] = new AudioFormat(AudioFormat.GSM_MS);
	    outputType = new FileTypeDescriptor(FileTypeDescriptor.MSVIDEO);
	    if (!name.toLowerCase().endsWith(".avi"))
	    	name += ".avi";
    } else {
	    // MOV
        outputFormat[0] = new VideoFormat(VideoFormat.CINEPAK);
	    outputFormat[1] = new AudioFormat(AudioFormat.IMA4);
	    outputType = new FileTypeDescriptor(FileTypeDescriptor.QUICKTIME);
	    if (!name.toLowerCase().endsWith(".mov"))
	    	name += ".mov";
    }

	if (vr.debug)
		System.out.println("Creating processor");

    destMediaLocator = new MediaLocator("file:" + name);		
	dataSinkListener = new JMFRecordingDataSinkListener();	 
    
    processorModel = new ProcessorModel(mixedDataSource, outputFormat, outputType);	
    processor = Manager.createRealizedProcessor(processorModel);
    DataSource source = processor.getDataOutput();
    dataSink = Manager.createDataSink(source, destMediaLocator);    
    dataSink.addDataSinkListener(dataSinkListener);

	if (vr.debug)
		System.out.println("Processor created");    
}
/**
 * Element constructor comment.
 */
public void reinit() throws java.lang.Exception {
    if (vr.debug)
        System.out.println("Reiniting");
	
    close();

	captureVideoDevice = null;
	captureVideoFormat = null;
	captureAudioDevice = null;
	captureAudioFormat = null;

    java.util.Vector deviceListVector = CaptureDeviceManager.getDeviceList(null);
	checkDeviceList(deviceListVector);

    for (int x = 0; x < deviceListVector.size(); x++) {
        // display device name
        CaptureDeviceInfo deviceInfo = (CaptureDeviceInfo) deviceListVector.elementAt(x);
        String deviceInfoName = deviceInfo.getName();
//        if (vr.debug)
//            System.out.println("device " + x + ": " + deviceInfoName);

        // display device formats
        Format deviceFormat[] = deviceInfo.getFormats();
        for (int y = 0; y < deviceFormat.length; y++) {
            // search for default video device
            if (captureVideoDevice == null) {
				if (deviceFormat[y] instanceof VideoFormat) {
					if (deviceInfo.getName().indexOf(vr.videoDevices.getSelectedItem()) >= 0) {
                        captureVideoDevice = deviceInfo;
                        if (vr.debug)
                            System.out.println(">>> capture video device = " + deviceInfo.getName());
                    }
                }
            }

            // search for default video format
            if (captureVideoDevice == deviceInfo) {
                if (captureVideoFormat == null) {
                    if (RecordingTools.formatToString(deviceFormat[y]).indexOf(vr.videoFormats.getSelectedItem()) >= 0) {
                        captureVideoFormat = (VideoFormat) deviceFormat[y];
                        if (vr.debug)
                            System.out.println(">>> capture video format = " + RecordingTools.formatToString(deviceFormat[y]));
                    }
                }
            }

            // serach for default audio device
            if (captureAudioDevice == null) {
                if (deviceFormat[y] instanceof AudioFormat) {
                    if (deviceInfo.getName().indexOf(vr.audioDevices.getSelectedItem()) >= 0) {
                        captureAudioDevice = deviceInfo;
//                        if (vr.debug)
//	                        System.out.println(">>> capture audio device = " + deviceInfo.getName());
                    }
                }
            }

            // search for default audio format
            if (captureAudioDevice == deviceInfo) {
                if (captureAudioFormat == null) {
                    if (RecordingTools.formatToString(deviceFormat[y]).indexOf(vr.audioFormats.getSelectedItem()) >= 0) {
                        captureAudioFormat = (AudioFormat) deviceFormat[y];
                        //if (vr.debug)
                            //System.out.println(">>> capture audio format = " + RecordingTools.formatToString(deviceFormat[y]));
                    }
                }
            }

//            if (vr.debug)
//                System.out.println("Format: " + RecordingTools.formatToString(deviceFormat[y]));
        }
    }

	if (captureVideoDevice == null || captureVideoFormat == null)
		throw new Exception("Video device not found");

	if (captureAudioDevice == null || captureAudioFormat == null)
		throw new Exception("Audio device not found");

	// Create thread so that GUI need not wait
    reinitingProcessor = true;
	new Thread(this).start();
}
/**
 */
public void run() {
    try {
		initProcessor();
    } catch (Exception e) {
 		System.out.println("JMF processor init error: " + e); 		
 	} finally {
 		reinitingProcessor = false;
 	}
}
/**
 * Element constructor comment.
 */
public void start() throws java.lang.Exception {
	int counter = 0;
	while (reinitingProcessor) {
		System.out.println("Waiting for the JMF processor initalization");
		Thread.sleep(1000);
		if (counter++ > 20) {
			System.out.println("--- Processor was not initialized withing 10 seconds ---");
			close();
			return;
		}		
	}
	
	if (vr.debug)
		System.out.println("Opening data sink");
    
    dataSink.open();

	if (vr.debug)
		System.out.println("Starting data sink");
    
    dataSink.start();

	if (vr.debug)
		System.out.println("Starting processor");
    
    processor.start();

	if (vr.debug)
 		System.out.println("recorder started");		    
}
/**
 * Element constructor comment.
 */
public void stop() throws Exception {
    processor.stop();
	processor.close();
	
    dataSinkListener.waitEndOfStream(10);
    dataSink.close();

    // Init again, so that next start can be quick
    // Do this in thread so that GUI need not wait
    reinitingProcessor = true;
    new Thread(this).start();
    
	if (vr.debug)
 		System.out.println("recorder stopped");		
}
}
