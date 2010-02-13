/* ModeDisplay.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.graph.designer.*;
import bioera.fft.*;
import bioera.graph.chart.*;
import bioera.properties.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class ModeDisplay extends Display {
	public SmallVerticalScale scale;
	public boolean showUnit = true;
	private boolean leftAlign = true;

	private final static String propertiesDescriptions[][] = {
		{"leftAlign", "Align to left", ""},
	};

	private int b[], n, sum;
	private int computeN = 1;
	private BufferedScalarPipe in;
	private TextChart chart;
	private int digiRange, physRange;
        private long drawTimer = 0;
        private int numpktsinSec = 0;
        private int pktvalwithinSec = 0;
        private boolean gotmode = false;
        int mode = 0;

/**
 * VectorDisplay constructor comment.
 */
public ModeDisplay() {
	super();
}
/**
 * Element constructor comment.
 */
public Chart createChart() {
	return chart = new TextChart();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "A number is shown on graphic chart";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 1;
}
/**
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
public final void initialize() {
	setName("Numeric");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	b = in.getBuffer();

	scale = new SmallVerticalScale(this);
//	System.out.println("Scale=" + ProcessingTools.propertiesToString(scale));
}
/**
 * Element constructor comment.
 */
public final void process() {

    int n = in.available();
    if (n == 0) {
        return;
    } else {
        if (gotmode == false) { //get mode & probeconf
            mode = b[4];
            //probeconf = b[5];
            //sensitivity = b[3];
            //gotmode = true;
        }
        in.purgeAll();
        String modeDescrip = null;
        if (mode==0){
            modeDescrip = "Probe 1";
        }
        if (mode==1){
            modeDescrip = "Probe 2";
        }
        if (mode==2){
            modeDescrip = "Probe 1 & 2";
        }
        chart.pushTextLeft(modeDescrip);
        chart.repaint();
        n=0;
    }

}

    public void pushtoChart(int val) {
        if (drawTimer == 0){ //push on first datapacket
            chart.pushTextLeft(Integer.toString(val));
            chart.repaint();
            drawTimer = System.currentTimeMillis();
        }
        else if ( (System.currentTimeMillis() < drawTimer+1000) ) { //avg value to push until a second passes
            pktvalwithinSec = (pktvalwithinSec+val);
            numpktsinSec++;
        }
        else{                                 //push avg val to chart
            pktvalwithinSec = (pktvalwithinSec+val);
            numpktsinSec++;
            val = (pktvalwithinSec/numpktsinSec);

            chart.pushTextLeft(Integer.toString(val));
            chart.repaint();

            //chart.push(val);chart.repaint();
            pktvalwithinSec = 0;
            numpktsinSec = 0;
            drawTimer = System.currentTimeMillis();
        }
    }




/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	if (scale.getSelectedIndex() == -1)
		scale.setSelectedIndex(0);

	if (showUnit)
		chart.unit = scale.getUnitStr();
	else
		chart.unit = null;

	chart.pushText("0");
	chart.alignLeft = leftAlign;

	scale.update(this);

	physRange = (int) scale.calculatePrecisePhysicalRange();
	digiRange = (int)(getSignalParameters().getDigitalRange() * scale.calculatePreciseScaleDigitalRatio());

	//System.out.println("digiRange="+digiRange);
	//System.out.println("physRange="+physRange);

	super.reinit();
}
}
