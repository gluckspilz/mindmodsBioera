/* MediaPlayer.java v 1.0.9   11/6/04 7:15 PM
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
public final class MediaPlayer extends Display {
	public BrowseFile filePath = new BrowseFile("");
	public boolean reflectTimeRange = false;
	public ComboProperty implementation = new ComboProperty(new String[]{
		"JMF",
		"QUICKTIME"
	});

	private final static String propertiesDescriptions[][] = {
		{"bufferLength", "Buffer length", ""},
		{"reflectTimeRange", "Reflect time range", ""},
		{"currentTime", "Current time", "", "false"},
	};

	int lastImpl = -1;

    // media Player
    MPlayer player = null;

    MediaChart chart;

    BufferedScalarPipe inVol;
    int inVolBuf[], maxVolLevel, prevVol = -1;

	public static final boolean debug = bioera.Debugger.get("impl.mediaplayer");
	//private ByteArrayOutputStream testO = new ByteArrayOutputStream();
/**
 * VectorDisplay constructor comment.
 */
public MediaPlayer() {
	super();
	setName("Media");
	inVol = (BufferedScalarPipe) inputs[0];
	inVol.setName("Vol");
	inVolBuf = inVol.getBuffer();
	//in2 = (BufferedScalarPipe) inputs[1];
	//in2.setName("right");
	//inb2 = in2.getBuffer();
}
/**
 * Element constructor comment.
 */
public void close() throws java.lang.Exception {
	if (player != null) {
		player.close();
	}
}
/**
 * createChart method comment.
 */
public Chart createChart() {
	try {
		return chart = new MediaChart();
	} catch (java.lang.NoClassDefFoundError e) {
		throw new RuntimeException("Object '" + Tools.getClassLastName(getClass())+"' couldn't be loaded. The most likely reason is that " +
			(implementation.getItem(0).equals(implementation.name) ? implementation.getItem(0) : (implementation.getItem(1).equals(implementation.name) ? implementation.getItem(1) : " Media library" ))
			+" has not been installed. For more information check the manual.");
	}
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Media player";
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
/**
 * Element constructor comment.
 */
public final void process() throws java.lang.Exception {
	if (inVol.isEmpty())
		return;

	int v = inVolBuf[inVol.available() - 1];
	if (v < 0)
		v = 0;

	if (v != prevVol) {
		player.setVolumeLevel(((float)v) / maxVolLevel);
		prevVol = v;
	}

	inVol.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit() throws java.lang.Exception {
	if (implementation.getSelectedIndex() == -1)
		implementation.setSelectedIndex(0);

	if (!new File(filePath.path).exists()) {
		throw new Exception("File not found: '" + filePath.path + "'");
	}

	if (lastImpl != implementation.getSelectedIndex()) {
		if (player != null)
			close();
		if (implementation.getSelectedIndex() == 0)
			player = new JMFPlayer(this);
		else
			player = new QTPlayer(this);
		lastImpl = implementation.getSelectedIndex();
	}

	if (reflectTimeRange) {
		setTimeRange();
	}

	if (filePath.path.equals(player.lastPath)) {
		super.reinit();
		return;
	}

	player.reinit();

	if (inVol.isConnected()) {
		maxVolLevel = inVol.getSignalParameters().getDigitalMax();
	} else
		maxVolLevel = 1;

	player.lastPath = filePath.path;

	if (reflectTimeRange) {
		setTimeRange();
	}

	super.reinit();
}
/**
 * Element constructor comment.
 */
public void setTimeRange() throws java.lang.Exception {
	TimeRangeFeature tFeature = (TimeRangeFeature) ProcessingTools.traversPredecessorsForFeature(this, TimeRangeFeature.class);
	if (tFeature != null) {
		double s = tFeature.physicalStart();
		double e;
		if (tFeature.physicalEnd() > 0)
			e = tFeature.physicalEnd();
		else
			e = -1;
		player.setTimeRange(s, e);
	}
}
/**
 * Element constructor comment.
 */
public void start() throws java.lang.Exception {
	if (player != null) {
		player.start();
		if (debug)
			System.out.println("player started");
	}
}
/**
 * Element constructor comment.
 */
public void stop() throws Exception {
	if (player != null) {
		player.stop();
		if (debug)
			System.out.println("player stopped");
	}
}
}
