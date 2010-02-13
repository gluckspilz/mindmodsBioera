/* MPlayer.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.processing.impl.MediaPlayer;
import bioera.graph.chart.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public abstract class MPlayer {
	MediaPlayer mp;
	MediaChart chart;
	public String lastPath;
	//private ByteArrayOutputStream testO = new ByteArrayOutputStream();
/**
 * VectorDisplay constructor comment.
 */
public MPlayer(MediaPlayer imp) {
	super();
	mp = imp;
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
public abstract void setTimeRange(double start, double end) throws java.lang.Exception;
/**
 * Element constructor comment.
 */
public abstract void setVolumeLevel(float v) throws java.lang.Exception;
/**
 * Element constructor comment.
 */
public abstract void start() throws java.lang.Exception;
/**
 * Element constructor comment.
 */
public abstract void stop() throws Exception ;
}
