/* QTPlayer.java v 1.0.9   11/6/04 7:15 PM
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

import quicktime.*;
import quicktime.io.*;
import quicktime.qd.*;
import quicktime.std.*;
import quicktime.std.movies.*;
import quicktime.std.movies.media.*;
import quicktime.app.view.*;
import quicktime.std.clocks.*;

import bioera.processing.impl.MediaPlayer;

/**
 * Creation date: (10/27/2004 12:26:36 PM)
 * @author: Jarek Foltynski
 */
public class QTPlayer extends MPlayer {
	private Movie movie;
	private MovieController mc;
	QTComponent qtc = null;

	private static int instanceCounter = 0;
	TimeRecord startTime, endTime;
/**
 * QTPlayer constructor comment.
 * @param ivp bioera.processing.impl.VideoPlayer
 */
public QTPlayer(MediaPlayer imp) throws Exception {
	super(imp);

	if (instanceCounter++ == 0) {
	    QTSession.open();
   		if (mp.debug)
			System.out.println("QT Session opened");
	}
}
/**
 * Element constructor comment.
 */
public void close() throws Exception {
	chart.close();

	if (--instanceCounter == 0) {
		QTSession.close();
		if (mp.debug)
			System.out.println("QT Session closed");
	}
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
    String url = "file://" + mp.filePath.path;
    // create the DataRef that contains the information about where the movie is
    DataRef urlMovie = new DataRef(url);
    // create the movie
    movie = Movie.fromDataRef(urlMovie, StdQTConstants.newMovieActive);
    // create the movie controller
    mc = new MovieController(movie);
    // create and add a QTComponent if we haven't done so yet, otherwise set qtc's movie controller
    if (qtc == null) {
        qtc = QTFactory.makeQTComponent(mc);
		chart.visualComponent = (java.awt.Component) qtc;
		chart.container.add(chart.visualComponent, java.awt.BorderLayout.CENTER);
	    chart.container.validate();
	    if (mp.debug)
	    	System.out.println("QT component added");
    } else {
        qtc.setMovieController(mc);
    }

    //System.out.println("dur: " + movie.getDuration());
    //System.out.println("scale: " + movie.getTimeScale());

    ////movie.setSelection(movie.getDuration() / 4, movie.getDuration() / 2);
    //mc.goToTime(startTime);
}
/**
 * Element constructor comment.
 */
public void setTimeRange(double start, double end) throws java.lang.Exception {
	if (movie == null) {
		return;
	}

	startTime = new TimeRecord(movie.getTimeScale(), (int)(start * movie.getTimeScale()));
	mc.goToTime(startTime);
}
/**
 * Element constructor comment.
 */
public void setVolumeLevel(float v) throws Exception {
	movie.setVolume(v);
System.out.println("set to " + v);
}
/**
 * Element constructor comment.
 */
public void start() throws Exception {
	if (startTime != null)
		mc.goToTime(startTime);
	movie.start();
	if (mp.debug)
		System.out.println("QT movie started");
}
/**
 * Element constructor comment.
 */
public void stop() throws Exception {
	movie.stop();
	if (mp.debug)
		System.out.println("QT movie stopped");
}
}
