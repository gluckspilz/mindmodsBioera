/* FrameGrabber.java v 1.0.9   11/6/04 7:15 PM
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
import java.awt.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import javax.media.*;
import javax.media.control.*;
import javax.media.protocol.*;
import javax.media.util.*;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;

/**
 *  Frame grabber class
 **/
public class FrameGrabber implements ControllerListener {
  /*  Default device name and format parameters to use if no properties file
   *  is provided
   */
  private final static String DEFAULT_DEV_NAME = "vfw:Logitech USB Video Camera:0";
  private final static String DEFAULT_X_RES = "160";
  private final static String DEFAULT_Y_RES = "120";
  private final static String DEFAULT_DEPTH = "24";

  private Properties videoProperties;

  private Object stateLock = new Object();

  private PushBufferStream camStream;
  private BufferToImage converter;

public FrameGrabber() throws Exception {
    /*  We use a properties file to allow the user to define what kind of 
     *  camera and resolution they want to use for the vision input
     */
    String videoPropFile =
        System.getProperty("video.properties", "video.properties");

    try {
        FileInputStream fis = new FileInputStream(new File(videoPropFile));
        videoProperties = new Properties();
        videoProperties.load(fis);
    } catch (IOException ioe) {
        throw new Exception(ioe.getMessage());
    }

    Dimension viewSize = null;
    int viewDepth = 0;

    String cameraDevice =
        videoProperties.getProperty("device-name", DEFAULT_DEV_NAME);

    /*  Get the parameters for the video capture device from the properties
     *  file.  If not defined use default values
     */
    try {
        String pValue = videoProperties.getProperty("resolution-x", DEFAULT_X_RES);
        int xRes = Integer.parseInt(pValue);
        pValue = videoProperties.getProperty("resolution-y", DEFAULT_Y_RES);
        int yRes = Integer.parseInt(pValue);
        viewSize = new Dimension(xRes, yRes);
        pValue = videoProperties.getProperty("colour-depth", DEFAULT_DEPTH);
        viewDepth = Integer.parseInt(pValue);
    } catch (NumberFormatException nfe) {
        System.out.println("Bad numeric value in video properties file");
        System.exit(1);
    }

    System.out.println("Searching for [" + cameraDevice + "]");

    /*  Try to get the CaptureDevice that matches the name supplied by the
     *  user
     */
    CaptureDeviceInfo device = CaptureDeviceManager.getDevice(cameraDevice);

    if (device == null)
        throw new Exception("No device found [ " + cameraDevice + "]");

    RGBFormat userFormat = null;
    Format[] cfmt = device.getFormats();

    /*  Find the format that the user has requested (if available)  */
    for (int i = 0; i < cfmt.length; i++) {
        if (cfmt[i] instanceof RGBFormat) {
            userFormat = (RGBFormat) cfmt[i];
            Dimension d = userFormat.getSize();
            int bitsPerPixel = userFormat.getBitsPerPixel();

            if (viewSize.equals(d) && bitsPerPixel == viewDepth)
                break;

            userFormat = null;
        }
    }

    /*  Throw an exception if we can't find a format that matches the 
     *  user's criteria
     */
    if (userFormat == null)
        throw new Exception("Requested format not supported");

    /*  To use this device we need a MediaLocator  */
    MediaLocator loc = device.getLocator();

    if (loc == null)
        throw new Exception("Unable to get MediaLocator for device");

    DataSource formattedSource = null;

    /*  Now create a dataSource for this device and set the format to
     *  the one chosen by the user.
     */
    try {
        formattedSource = Manager.createDataSource(loc);
    } catch (IOException ioe) {
        throw new Exception("IO Error creating dataSource");
    } catch (NoDataSourceException ndse) {
        throw new Exception("Unable to create dataSource");
    }

    /*  Setting the format is rather complicated.  Firstly we need to get
     *  the format controls from the dataSource we just created.  In order
     *  to do this we need a reference to an object implementing the 
     *  CaptureDevice interface (which DataSource objects can).
     */
    if (!(formattedSource instanceof CaptureDevice))
        throw new Exception("DataSource not a CaptureDevice");

    FormatControl[] fmtControls =
        ((CaptureDevice) formattedSource).getFormatControls();

    if (fmtControls == null || fmtControls.length == 0)
        throw new Exception("No FormatControl available");

    Format setFormat = null;

    /*  Now we need to loop through the available FormatControls and try
     *  to set the format to the one we want.  According to the documentation
     *  even though this may appear to work, it may fail later on.  Since
     *  we know that the format is supported we hope that this won't happen
     */
    for (int i = 0; i < fmtControls.length; i++) {
        if (fmtControls[i] == null)
            continue;

        if ((setFormat = fmtControls[i].setFormat(userFormat)) != null)
            break;
    }

    /*  Throw an exception if we couldn't set the format  */
    if (setFormat == null)
        throw new Exception("Failed to set camera format");

    /*  Connect to the DataSource  */
    try {
        formattedSource.connect();
    } catch (IOException ioe) {
        throw new Exception("Unable to connect to DataSource");
    }

    System.out.println("Data source created and format set");

    /*  Since we don't want to display the output to the user at this stage
     *  we use a processor rather than a player to get frame access
     */
    Processor deviceProc = null;

    try {
        deviceProc = Manager.createProcessor(formattedSource);
    } catch (IOException ioe) {
        throw new Exception(
            "Unable to get Processor for device: " + ioe.getMessage());
    } catch (NoProcessorException npe) {
        throw new Exception(
            "Unable to get Processor for device: " + npe.getMessage());
    }

    /*  In order to use the controller we have to put it in the realized
     *  state.  We do this by calling the realize method, but this will
     *  return immediately so we must register a listener (this class) to
     *  be notified when the controller is ready.
     */
    deviceProc.addControllerListener(this);
    deviceProc.realize();
    System.out.println("Realizing capture device");

    /*  Wait for the device to send an event telling us that it has
     *  reached the realized state
     */
    while (deviceProc.getState() != Controller.Realized) {
        synchronized (stateLock) {
            try {
                stateLock.wait();
            } catch (InterruptedException ie) {
                throw new Exception("Failed to get to realized state");
            }
        }
    }

    deviceProc.start();

    /*  Get access to the PushBufferDataSource which will provide us with
     *  a means to get at the frame grabber
     */
    PushBufferDataSource source = null;

    try {
        source = (PushBufferDataSource) deviceProc.getDataOutput();
    } catch (NotRealizedError nre) {
        /*  Should never happen  */
        throw new Exception("Processor not realized");
    }

    /*  Now we can retrieve the PushBufferStreams that will enable us to 
     *  access the data from the camera
     */
    PushBufferStream[] streams = source.getStreams();
    camStream = null;

    for (int i = 0; i < streams.length; i++) {
        /*  Use the first Stream that is RGBFormat (there should be only one  */
        if (streams[i].getFormat() instanceof RGBFormat) {
            camStream = streams[i];
            RGBFormat rgbf = (RGBFormat) streams[i].getFormat();
            converter = new BufferToImage(rgbf);
            break;
        }
    }

    System.out.println("Capture device ready");
}

/**
 *  Get an image from the camera
 *
 * @returns The current image from the camera
 * @throws Exception If there is a problem
 **/
public Image getImage() throws Exception {
    Buffer b = new Buffer();

    try {
        camStream.read(b);
    } catch (IOException ioe) {
        throw new Exception("Unable to capture frame from camera");
    }

    Image i = converter.createImage(b);
    return i;
}

/**
 *  Method called when a controller event is received (implements
 *  ControllerListener interface)
 *
 * @param ce The controller event
 **/
public void controllerUpdate(ControllerEvent ce) {
    if (ce instanceof RealizeCompleteEvent) {
        System.out.println("Realize transition completed");

        synchronized (stateLock) {
            stateLock.notifyAll();
        }
    }
}
}
