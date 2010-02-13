/* IDisplay.java
 */

package bioera.media;

import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.event.*;
import bioera.processing.impl.ImageDisplay;
//import bioera.processing.impl.MediaPlayer;
import bioera.graph.chart.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public abstract class IDisplay {
	ImageDisplay id;
	MediaChart chart;
	public String lastPath;
	//private ByteArrayOutputStream testO = new ByteArrayOutputStream();
/**
 * VectorDisplay constructor comment.
 */
public IDisplay(ImageDisplay imp) {
	super();
	id = imp;
	chart = (MediaChart) imp.getChart();
}
/**
 * Element constructor comment.
 */
public abstract void close() throws java.lang.Exception;
/**
 * Element constructor comment.
 */
public abstract void reinit() throws java.lang.Exception;
/**
 * Element constructor comment.
 */
//public abstract void setTimeRange(double start, double end) throws java.lang.Exception;
/**
 * Element constructor comment.
 */
//public abstract void setVolumeLevel(float v) throws java.lang.Exception;
/**
 * Element constructor comment.
 */
public abstract void start() throws java.lang.Exception;
/**
 * Element constructor comment.
 */
public abstract void stop() throws Exception ;
}
