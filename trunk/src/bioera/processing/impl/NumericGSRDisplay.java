/* NumericGSRDisplay.java v 1.0.9   11/6/04 7:15 PM
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

import bioera.Main;
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
public final class NumericGSRDisplay extends Display {
        public SmallVerticalScale scale;
        public boolean showUnit = true;
        private boolean leftAlign = true;

        private final static String propertiesDescriptions[][] = {
                {"leftAlign", "Align to left", ""},
        };

        int mode = -1;
        int probeconf = -1;
        int sensitivity = -1;
        boolean gotmode = false;
        private int b1[], b2[], n1, n2, sum;
        private int computeN = 1;
        private BufferedScalarPipe in1, in2;
        private TextChart chart;
        private int digiRange, physRange;
        private long drawTimer = 0;
        private int numpktsinSec = 0;
        private int pktvalwithinSec = 0;
        public int channelnum = 0;

/**
 * VectorDisplay constructor comment.
 */
public NumericGSRDisplay() {
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
        return 2;
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
        setName("NumericGSRDisplay");
        in1 = (BufferedScalarPipe) inputs[0];
        in2 = (BufferedScalarPipe) inputs[1];
        in1.setName("IN");
        in2.setName("SES");
        b1 = in1.getBuffer();
        b2 = in2.getBuffer();

        scale = new SmallVerticalScale(this);

//	System.out.println("Scale=" + ProcessingTools.propertiesToString(scale));
}
/**
 * Element constructor comment.
 */
public final void process() {
        int n1 = in1.available();
        int n2 = in2.available();

        if (n1 == 0)
                return;

        if (gotmode == false) { //get mode & probeconf
                /*for (int i = 0; i < 8; i++){
                    int p = b2[i];
                 System.out.print(p+" ");}System.out.println(" is the session info from bar");*/

              try{chart.pushText(" ");chart.repaint();}catch(Exception Ex){} //clear element on startofsession packet
              mode = b2[4];
              probeconf = b2[5];
              sensitivity = b2[3];
              gotmode = true;
          }

          //gsr1
          if (channelnum==0){
              if (mode == -1) {
                  return;
              }
              if ((mode == 1) && (Main.app.displayAllChannels==false)) {
                  //no chart
                  chart.isChartVisible(false);
                  return;
              }

              if ((probeconf == 1) || (probeconf == 3) || (probeconf == 5) ||
                  (probeconf == 9) || (Main.app.displayAllChannels==true)) {
                  //display chart;
                  if (b1[n1 - 1] > 5000 | b1[n1 - 1] < -5000)
                      chart.pushTextLeft(" ");
                  else {
                      pushtoChart(b1[n1 - 1] * physRange / digiRange);
                  }
              } else {
                  //no chart
                  chart.isChartVisible(false);
                  return;
              }
              in1.purgeAll();
          }

          //gsr2
          if (channelnum==2){
              if (mode == -1) {
                  return;
              }
              if ((mode == 0) && (Main.app.displayAllChannels==false)) {
                  //no chart
                  chart.isChartVisible(false);
                  return;
              }

              if ((probeconf == 2) || (probeconf == 3) || (probeconf == 6) ||
                  (probeconf == 10) || (Main.app.displayAllChannels==true)) {
                  //display chart;
                  if (b1[n1 - 1] > 5000 | b1[n1 - 1] < -5000)
                      chart.pushTextLeft(" ");
                  else {
                      pushtoChart(b1[n1 - 1] * physRange / digiRange);
                  }
              } else {
                  //no chart
                  chart.isChartVisible(false);
                  return;
              }
              in1.purgeAll();
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




public void start() throws Exception {
    chart.isChartVisible(true);
    gotmode=false;
    mode = -1;probeconf = -1;sensitivity = -1;
    n1=0;n2=0;
}

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
        chart.isChartVisible(true);

        super.reinit();
}
}
