/* QTDisplay.java
 */

package bioera.media;

import java.io.*;
import java.util.*;

import quicktime.*;
import quicktime.std.image.*;
import quicktime.io.*;
import quicktime.qd.*;
import quicktime.std.*;
import quicktime.std.movies.*;
import quicktime.std.movies.media.*;
import quicktime.app.view.*;
import quicktime.app.display.*;
import quicktime.std.clocks.*;

import bioera.processing.impl.ImageDisplay;

/**
 * Creation date: (10/27/2004 12:26:36 PM)
 * @author: Jarek Foltynski
 */
public class QTDisplay extends IDisplay {
	private Movie movie;
	private MovieController mc;
	QTComponent qtc = null;

        private GraphicsImporter gi;

	private static int instanceCounter = 0;
	TimeRecord startTime, endTime;
/**
 * QTPlayer constructor comment.
 * @param ivp bioera.processing.impl.VideoPlayer
 */
public QTDisplay(ImageDisplay imp) throws Exception {
	super(imp);

	if (instanceCounter++ == 0) {
	    QTSession.open();
   		if (id.debug)
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
		if (id.debug)
			System.out.println("QT Session closed");
	}
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
    String url = "file://" + id.filePath.path;
    // create the DataRef that contains the information about where the movie is
    DataRef urlMovie = new DataRef(url);

    // create the image




//    QTFile imageFile = new QTFile(
//        QTFactory.findAbsolutePath(url));
//    GraphicsImporter myGraphicsImporter = new GraphicsImporter (imageFile);
//    GraphicsImporterDrawer myDrawer = new GraphicsImporterDrawer(myGraphicsImporter);

    // create the movie
    movie = Movie.fromDataRef(urlMovie, StdQTConstants.newMovieActive);

    // create the movie controller
    mc = new MovieController(movie);


    // create and add a QTComponent if we haven't done so yet, otherwise set qtc's movie controller
    if (qtc == null) {

        qtc = QTFactory.makeQTComponent(movie);
	chart.visualComponent = (java.awt.Component) qtc;
	chart.container.add(chart.visualComponent, java.awt.BorderLayout.CENTER);
	    chart.container.validate();
	    if (id.debug)
	    	System.out.println("QT component added");
    } else {
        qtc.setMovieController(mc);
    }



    //System.out.println("dur: " + movie.getDuration());
    //System.out.println("scale: " + movie.getTimeScale());

    ////movie.setSelection(movie.getDuration() / 4, movie.getDuration() / 2);
    //mc.goToTime(startTime);

}

public void start() throws Exception {
	if (startTime != null)
		mc.goToTime(startTime);
	movie.start();
	if (id.debug)
		System.out.println("QT movie started");
}
/**
 * Element constructor comment.
 */
public void stop() throws Exception {
	movie.stop();
	if (id.debug)
		System.out.println("QT movie stopped");
}
}
