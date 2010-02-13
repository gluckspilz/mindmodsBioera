/* TimerStart.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.storage.*;
import bioera.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class TimerStart extends SingleElement {
	//public ComboProperty function = new ComboProperty(new String[] {
		//"SAMPLES",
		//});

	private final static String propertiesDescriptions[][] = {
//		{"function", "Count", ""},

	};

	//private final static int SAMPLE = 0;
	//private final static int RISING_EDGE = 1;
	//private final static int FALLING_EDGE = 2;

	//private int type = SAMPLE;
        private BufferedScalarPipe in1;
	private ScalarPipeDistributor out;
        int b1[];
	private long time = 0;
	private long startTime = 0;
        private boolean firstrun = true;
	protected static boolean debug = bioera.Debugger.get("impl.timer");
        private int n1;
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Calculates time, writes to output number of seconds since start";
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
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
/**
 * Element constructor comment.
 */
public final void process() {
    	n1 = in1.available();
            //n1 = b1[0];

        //SUPPORT SESSION START TIME FOR TIMER
        if ((n1 == 0)&&(firstrun==true))Element.mainStartTime = System.currentTimeMillis();
        //if ((n1 == 0)&&(firstrun==true))Element.mainStartTime = Element.sessionStartTime;  //timer with proper start

        if (n1 > 0) {
            firstrun=false;
            //System.out.println(mainStartTime);


            if (mainProcessingTime - time > 1000) {
                time = mainProcessingTime;
                Main.app.runtimeFrame.statusTimerf.setText( String.valueOf((int) ((time - mainStartTime) / 1000))    );
                //out.write((int) ((time - mainStartTime) / 1000));
            }
        }
    }

/**
 */
public void start()  throws Exception {
        firstrun=true;
	time = 0;
        n1=0;

}

/**
 * Element constructor comment.
 */
public TimerStart() {
	super();
	setName("TimerStart");
        in1 = (BufferedScalarPipe)inputs[0];
        in1.setName("IN1");
        b1 = in1.getBuffer();
	out = (ScalarPipeDistributor) outputs[0];
	out.setName("OUT");

}
}
