/* JMFPlayer.java v 1.0.9   11/6/04 7:15 PM
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

import javax.media.*;
import bioera.processing.*;
import java.awt.event.*;
import bioera.processing.impl.MediaPlayer;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class JMFPlayer extends MPlayer implements ControllerListener {
    // media Player
    Player player = null;
    GainControl gainControl;
	Time startTime = new Time(0), endTime;
	//private ByteArrayOutputStream testO = new ByteArrayOutputStream();
/**
 * VectorDisplay constructor comment.
 */
public JMFPlayer(MediaPlayer ivp) {
	super(ivp);
}
/**
 * Element constructor comment.
 */
public void close() throws java.lang.Exception {
	if (player != null) {
		player.deallocate();
		player.close();
		player = null;
	}

	chart.close();
}
/**
 * controllerUpdate method comment.
 */
public void controllerUpdate(javax.media.ControllerEvent event) {
	// If we're getting messages from a dead player,
	// just leave
	//System.out.println("event: " + event.getClass());

	if (player == null)
	    return;


	// When the player is Realized, get the visual
	// and control components and add them to the Applet
	if (event instanceof RealizeCompleteEvent) {
		setTimeRange();

	    if (chart.progressBar != null) {
			chart.container.remove(chart.progressBar);
			chart.progressBar = null;
	    }

	    if (chart.controlComponent == null) {
			if ((chart.controlComponent = player.getControlPanelComponent()) != null) {
			    //chart.controlPanelHeight = chart.controlComponent.getPreferredSize().height;
			    chart.container.add(chart.controlComponent, java.awt.BorderLayout.SOUTH);
			}
	    }
	    if (chart.visualComponent == null)
			if ((chart.visualComponent = player.getVisualComponent()) != null) {
			    chart.container.add(chart.visualComponent, java.awt.BorderLayout.CENTER);
			    gainControl = player.getGainControl();
			}

	    if (chart.controlComponent != null) {
			chart.controlComponent.validate();
	    }

	    //java.awt.Dimension videoSize = chart.visualComponent.getPreferredSize();
	    chart.container.validate();

 	} else if (event instanceof CachingControlEvent) {
	    if (player.getState() > Controller.Realizing)
			return;

	    // Put a progress bar up when downloading starts,
	    // take it down when downloading ends.
	    CachingControlEvent e = (CachingControlEvent) event;
	    CachingControl cc = e.getCachingControl();

	    // Add the bar if not already there ...
	    if (chart.progressBar == null) {
	        if ((chart.progressBar = cc.getControlComponent()) != null) {
			    chart.container.add(chart.progressBar, java.awt.BorderLayout.SOUTH);
			    chart.getComponent().validate();
			}
	    }
	} else if (event instanceof EndOfMediaEvent) {
	    // We've reached the end of the media;
	    // rewind and start over
	    //if (vp.rewind) {
		    //player.setMediaTime(startTime);
		    //player.start();
	    //}
	} else if (event instanceof ControllerErrorEvent) {
	    player = null;
	    String msg = ((ControllerErrorEvent)event).getMessage();
	    mp.disactivate("Error: " + msg);
	}

}
/**
 * Element constructor comment.
 */
public void reinit() throws java.lang.Exception {
	close();

	MediaLocator mrl = null;

	try {
	    // Create a media locator from the file name
	    if ((mrl = new MediaLocator("file:" + mp.filePath.path)) == null)
			throw new Exception("Can't build URL for " + mp.filePath.path);

	    // Create an instance of a player for this media
	    try {
			player = Manager.createPlayer(mrl);
			if (mp.debug)
				System.out.println("JMF player created");
	    } catch (NoPlayerException e) {
			System.out.println(e);
			throw new Exception("Could not create player for: '" + mrl + "'");
	    }

	    // Add ourselves as a listener for a player's events
	    player.addControllerListener(this);

		player.realize();
	} catch (MalformedURLException e) {
	    throw new Exception("Invalid media file URL!");
	} catch (IOException e) {
	    throw new Exception("IO exception creating player for " + mrl);
	}
}
/**
 * Element constructor comment.
 */
public void setTimeRange() {
	if (player != null && player.getState() != player.Unrealized) {
	    player.setMediaTime(startTime);
	    //if (endTime != null)
		    //player.setStopTime(endTime);
	}
}
/**
 * Element constructor comment.
 */
public void setTimeRange(double start, double end) {
	startTime = new Time(start);
	endTime = new Time(end);
	setTimeRange();
}
/**
 * Element constructor comment.
 */
public void setVolumeLevel(float v) throws java.lang.Exception {
	gainControl.setLevel(v);
}
/**
 * Element constructor comment.
 */
public void start() throws java.lang.Exception {
	if (player != null) {
		setTimeRange();
		player.start();//startTime);
		if (mp.debug)
			System.out.println("player started");
	}
}
/**
 * Element constructor comment.
 */
public void stop() {
	if (player != null) {
		player.stop();
		if (mp.debug)
			System.out.println("player stopped");
	}
}
}
