/* ImageDisplay.java
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


public final class ImageDisplay extends Display {
        public BrowseFile filePath = new BrowseFile("");

        private final static String propertiesDescriptions[][] = {  };

        int lastImpl = -1;

    // media Player
    IDisplay player = null;

    MediaChart chart;


        public static final boolean debug = bioera.Debugger.get("impl.mediaplayer");
        //private ByteArrayOutputStream testO = new ByteArrayOutputStream();
/**
 * VectorDisplay constructor comment.
 */
public ImageDisplay() {
        super();
        setName("ImageDisplay");

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
                throw new RuntimeException("Problem with Quicktime");
        }
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
        return "Image Display";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
        return 0;
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


}
/**
 * Element constructor comment.
 */
public void reinit() throws java.lang.Exception {


        if (!new File(filePath.path).exists()) {
                throw new Exception("File not found: '" + filePath.path + "'");
        }


        player = new QTDisplay(this);

        if (filePath.path.equals(player.lastPath)) {
                super.reinit();
                return;
        }

        player.reinit();
        player.lastPath = filePath.path;
        super.reinit();
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
