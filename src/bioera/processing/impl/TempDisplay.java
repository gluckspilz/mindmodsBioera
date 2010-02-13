/* TempDisplay.java
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


public final class TempDisplay extends Display {
        public SmallVerticalScale scale;
        public boolean showUnit = true;
        public boolean leftAlign = true;
        public int channelnum = 0;
        int mode = -1;
        int probeconf = -1;
        int sensitivity = -1;
        boolean gotmode = false;
        private final static String propertiesDescriptions[][] = {
                {"leftAlign", "Align to left", ""}
        };

        private int b1[], b2[], n1, n2, sum;
        private int computeN = 1;
        private BufferedScalarPipe in1, in2;
        private TextChart chart;
        private int digiRange, physRange;
        private int packetsproc;
        private long drawTimer = 0;
        private int numpktsinSec = 0;
        private double pktvalwithinSec = 0;

/**
 * VectorDisplay constructor comment.
 */
public TempDisplay() {
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


        setName("Temp");
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

        //System.out.println("v=" + scale.toScale(b[n-1]) + "  " + b[n-1]);
        if (n1 == 0){return;}
        double t = b1[n1-1];

        if (gotmode == false) { //get mode & probeconf
            /*for (int i = 0; i < 8; i++){
                int p = b2[i];
             System.out.print(p+" ");}System.out.println(" is the session info from bar");*/
            mode = b2[4];
            probeconf = b2[5];
            sensitivity = b2[3];
            gotmode = true;
        }



        if (channelnum == 1) {

            if (mode == -1)
                return;
            if ((mode == 1) && (Main.app.displayAllChannels==false)) {
                //no chart
                chart.isChartVisible(false);
                return;
            }

            //temp1 chan1
            if ((probeconf == 4) || (probeconf == 5) || (probeconf == 6) ||
                (probeconf == 12) || (Main.app.displayAllChannels==true)) {
                //display chart
                if (Main.app.isTempC()==false) {
                    t = t / 100;
                    t = (t * 9 / 5) + 32;
                    t = t * 100;
                    t = Math.round(t);
                    t = t / 100;
                } else {
                    t = t / 100;
                }
                //String str = new String(Double.toString(t));
                if (t > 5000 | t < -5000) {
                    chart.pushTextLeft(" ");
                } else
                    pushtoChart(t);
                    //chart.pushTextLeft(str);

            } else {
                //no chart
                chart.isChartVisible(false);
            }
        }

        //temp2 chan3
        if (channelnum == 3) {

            if (mode == -1)
                return;
            if ((mode == 0) && (Main.app.displayAllChannels==false)) {
                //no chart
                chart.isChartVisible(false);
                return;
            }

            //temp1 chan1
            if (    (probeconf == 8) || (probeconf == 9) || (probeconf == 10) || (probeconf == 12) || (Main.app.displayAllChannels==true)    ) {

                //display chart
                if (Main.app.isTempC()==false) {
                    t = t / 100;
                    t = (t * 9 / 5) + 32;
                    t = t * 100;
                    t = Math.round(t);
                    t = t / 100;
                } else {
                    t = t / 100;
                }
                //String str = new String(Double.toString(t));
                if (t > 5000 | t < -5000) {
                    chart.pushTextLeft(" ");
                } else
                    pushtoChart(t);
                    //chart.pushTextLeft(str);

            } else {
                //no chart
                chart.isChartVisible(false);
            }
        }




        //chart.pushTextLeft(Double.toString(t * physRange / digiRange));

        packetsproc++;
        in1.purgeAll();
        chart.repaint();

}


    //String str = new String(Double.toString(t));

    public void pushtoChart(double t) {
        if (drawTimer == 0) { //push on first datapacket
            t = Math.rint(t*10.)/10;
            chart.pushTextLeft(Double.toString(t));
            chart.repaint();
            drawTimer = System.currentTimeMillis();
        } else if ((System.currentTimeMillis() < drawTimer + 1000)) { //avg value to push until a second passes
            pktvalwithinSec = (pktvalwithinSec + t);
            numpktsinSec++;
        } else { //push avg val to chart
            pktvalwithinSec = (pktvalwithinSec + t);
            numpktsinSec++;
            t = (pktvalwithinSec / numpktsinSec);
            t = Math.rint(t*10.)/10;
            chart.pushTextLeft(Double.toString(t));
            chart.repaint();

            //chart.push(val);chart.repaint();
            pktvalwithinSec = 0;
            numpktsinSec = 0;
            drawTimer = System.currentTimeMillis();
        }
    }



public void start() throws Exception {
    gotmode = false;
    mode = -1;
    probeconf = -1;
    sensitivity = -1;
    n1 = 0;
    n2 = 0;
    chart.isChartVisible(true);
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


        if (Main.app.isTempC()==false){

            setName("Fdeg");
            //chart.drawCornerDescription();
            //chart.resetChart();
        }
        else{
            setName("Cdeg");
            //chart.drawCornerDescription();
            //chart.resetChart();
        }

        //System.out.println("tempdisplayname= "+chart.getChartName());

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

        chart.isChartVisible(true);

        super.reinit();
}
}
