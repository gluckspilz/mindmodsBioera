/* BarDisplayDynScale.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.graph.designer.*;
import bioera.properties.*;
import bioera.fft.*;
import bioera.graph.chart.*;
import bioera.graph.runtime.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class BarDisplayDynScale extends Display {
//	public int range = 1000;

        //public FloatCompoundUnitScale amplitudeRange;

        private final static String propertiesDescriptions[][] = {
//		{"", "", ""},
        };

        private BufferedScalarPipe in1, in2;
        private VerticalBarChart chart;
        private int digiRange, chartLevel, b1[], b2[], trgt[], prev;
        private int processed = 0;
        private int chartRange;
        private int amplitudelow=0;
        private int amplitudehigh=250;
        int chan = 0;
        int mode = -1;
        int probeconf = -1;
        boolean gotmode = false;
        int sensitivity = -1;
        private long drawTimer = 0;
        private int numpktsinSec = 0;
        private int pktvalwithinSec = 0;
        private double pktvalwithinSecdub = 0;
        private double numpktsinSecdub = 0;
        RuntimeFrame runtimeframe;


/**
 * VectorDisplay constructor comment.
 */
public BarDisplayDynScale() {
        super();
        setName("BarDyn");
        in1 = (BufferedScalarPipe) inputs[0];
        in2 = (BufferedScalarPipe) inputs[1];
        in1.setName("IN");
        in2.setName("SES");
        String name = "";
        b1 = in1.getBuffer();
        b2 = in2.getBuffer();
        //amplitudeRange = new FloatCompoundUnitScale(new FloatCompoundScale(100, new SmallVerticalScale(this)), new SmallBalancedVertScale(this));
}

/**
 * Element constructor comment.
 */
public Chart createChart() {
        return chart = new VerticalBarChart();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() throws Exception {
        return "Bar shows current value on graphic chart";
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


public final void process() throws Exception {

        int n1 = in1.available();
        int n2 = in2.available();
        double ndub = 0;

        if (processed < 1){
            name = chart.getChartName();
            if (name.hashCode() == "GSR1".hashCode()) chan=0;
            if (name.hashCode() == "TEMP1".hashCode()) chan=1;
            if (name.hashCode() == "GSR2".hashCode())  chan=2;
            if (name.hashCode() == "TEMP2".hashCode()) chan=3;
            processed++;
        }

        if (n1 == 0){return;}
        n1 = b1[n1-1];


        if (gotmode==false) //get mode & probeconf
        {
            /*for (int i = 0; i < 8; i++){
                int p = b2[i];
                System.out.print(p+" ");}System.out.println(" is the session info from bar");*/
            mode = b2[4];
            probeconf = b2[5];
            sensitivity = b2[3];
            gotmode=true;
        }

/*  don't process if packet is same as last
        if (processed > 2){
            if (n1 == prev)
                return;
        }
*/


        //gsr1
        if (chan==0){
            if (mode == -1)return;
            if ((mode == 1) && (Main.app.displayAllChannels==false)){
                //no chart
                chart.isChartVisible(false);
                return;
            }
            if (n2 > 0) //if theres a session packet
            {

                if ((probeconf==1) || (probeconf==3) || (probeconf==5) || (probeconf==9) || Main.app.displayAllChannels==true){
                    //display chart;
                    if (mode==0){
                        amplitudehigh = b2[2];
                        amplitudelow = b2[1];
                    }
                    if (mode==2 || Main.app.displayAllChannels==true){
                        double percentsens = (1 + ((double) sensitivity / 100));
                        double temp = n1 * percentsens;
                        amplitudehigh = (int) (1.2 * temp);
                        percentsens = 1 / percentsens;
                        temp = n1 * percentsens;
                        amplitudelow = (int) (temp / 1.2);
                    }
                    chart.yMaxValue = amplitudehigh;
                    chart.yMinValue = amplitudelow;
                    chartRange = chart.getChartHeight();
                    chart.resetChart();
                    super.reinit();
                    chart.repaint();
                    n2 = 0;
                    in2.purgeAll();
                    drawTimer = 0; //forces next packet to be drawn immediately after bar redraw (no sec avg)
                }
                else { //no chart
                    chart.isChartVisible(false);
                }
            }
            else { //a datapacket
                int val = (chartRange * (n1 - amplitudelow) /
                           (amplitudehigh - amplitudelow));
                if (n1 < 200000){  //only push when data is below 200000
                    pushtoChart(val);
                }
                //chart.push(val);chart.repaint();
                prev = n1;
                in1.purgeAll();
                in2.purgeAll();
                //System.out.println("gsr1value = "+n1);
                return;
            }
      }

        //temp1
        if (chan==1){
            if (mode == -1)return;
            if ((mode == 1) && (Main.app.displayAllChannels==false)){
                //no chart
                chart.isChartVisible(false);
                return;
            }

            ndub =(double)n1;

            if (Main.app.isTempC()==false) { //if farhen
                 ndub = ndub/100;ndub = (ndub*9/5)+32;ndub = ndub*100;ndub = Math.round(ndub);ndub = ndub/100;
             }
                else{ndub = ndub/100;} //if cels

            if (n2 > 0) //if theres a session packet
            {
                if ((probeconf == 4) || (probeconf == 5) || (probeconf == 6) || (probeconf == 12) || Main.app.displayAllChannels==true) {
                    //display chart
                    if (mode == 0 ) {
                        amplitudehigh = b2[2];
                        amplitudelow = b2[1];
                    }
                    if (mode == 2 || Main.app.displayAllChannels==true) {
                        double percentsens = (1 + ((double) sensitivity / 100));
                        double temp = ndub * percentsens;
                        amplitudehigh = (int) (1.2 * temp);
                        percentsens = 1 / percentsens;
                        temp = ndub * percentsens;
                        amplitudelow = (int) (temp / 1.2);
                    }
                    chart.yMaxValue = amplitudehigh;
                    chart.yMinValue = amplitudelow;
                    chartRange = chart.getChartHeight();
                    chart.resetChart();
                    super.reinit();
                    chart.repaint();
                    n2 = 0;
                    in2.purgeAll();
                    drawTimer = 0; //forces next packet to be drawn immediately after bar redraw (no sec avg)
                }
                else {
                    //remove chart here
                    chart.isChartVisible(false);
                }
            }
            else { //datapacket

                //int val = (   chartRange*(n1 - amplitudelow) / (amplitudehigh-amplitudelow)  );
                double valdub = (   chartRange*(ndub - amplitudelow) / (amplitudehigh-amplitudelow)  );
                //System.out.println("nl for temp1 sharts ="+n1);
                if (n1 > 0){  //only push when data is above 0


                    chart.push(valdub);chart.repaint();

                    //pushtoChart(valdub);
                }
                prev = n1;
                in1.purgeAll();
                in2.purgeAll();
                ndub=0; valdub=0;
                return;
            }
        }

        //gsr2
        if (chan==2){
            if (mode == -1)return;
            if ((mode == 0) && (Main.app.displayAllChannels==false)){
                //no chart
                chart.isChartVisible(false);
                return;
            }
            if (n2 > 0) //if theres a session packet
            {

                if ((probeconf==2) || (probeconf==3) || (probeconf==6) || (probeconf==10) || Main.app.displayAllChannels==true){
                    //display chart;
                    if (mode==1){
                        amplitudehigh = b2[2];
                        amplitudelow = b2[1];
                    }
                    if (mode==2  || Main.app.displayAllChannels==true){
                        double percentsens = (1 + ((double) sensitivity / 100));
                        double temp = n1 * percentsens;
                        amplitudehigh = (int) (1.2 * temp);
                        percentsens = 1 / percentsens;
                        temp = n1 * percentsens;
                        amplitudelow = (int) (temp / 1.2);
                    }
                    chart.yMaxValue = amplitudehigh;
                    chart.yMinValue = amplitudelow;
                    chartRange = chart.getChartHeight();
                    chart.resetChart();
                    super.reinit();
                    chart.repaint();
                    n2 = 0;
                    in2.purgeAll();
                    drawTimer = 0; //forces next packet to be drawn immediately after bar redraw (no sec avg)
                }
                else { //no chart
                    chart.isChartVisible(false);
                }
            }
            else { //a datapacket
                int val = (chartRange * (n1 - amplitudelow) /
                           (amplitudehigh - amplitudelow));
                if (n1 < 200000){  //only push when data is below 200000
                    pushtoChart(val);
                }
                //chart.push(val);chart.repaint();
                prev = n1;
                in1.purgeAll();
                in2.purgeAll();
                //System.out.println("gsr1value = "+n1);
                return;
            }
      }


      //temp2
      if (chan == 3) {
          if (mode == -1)
              return;
          if ((mode == 0) && (Main.app.displayAllChannels==false)) {
              //no chart
              chart.isChartVisible(false);
              return;
          }

          ndub = (double) n1;

          if (Main.app.isTempC() == false) { //if farhen
              ndub = ndub / 100;
              ndub = (ndub * 9 / 5) + 32;
              ndub = ndub * 100;
              ndub = Math.round(ndub);
              ndub = ndub / 100;
          } else {
              ndub = ndub / 100;
          } //if cels

          if (n2 > 0) { //if theres a session packet
              if ((probeconf == 8) || (probeconf == 9) || (probeconf == 10) ||
                  (probeconf == 12) || Main.app.displayAllChannels == true) {
                  //display chart
                  if (mode == 1) {
                      amplitudehigh = b2[2];
                      amplitudelow = b2[1];
                  }
                  if (mode == 2 || Main.app.displayAllChannels == true) {
                      double percentsens = (1 + ((double) sensitivity / 100));
                      double temp = ndub * percentsens;
                      amplitudehigh = (int) (1.2 * temp);
                      percentsens = 1 / percentsens;
                      temp = ndub * percentsens;
                      amplitudelow = (int) (temp / 1.2);
                  }
                  chart.yMaxValue = amplitudehigh;
                  chart.yMinValue = amplitudelow;
                  chartRange = chart.getChartHeight();
                  chart.resetChart();
                  super.reinit();
                  chart.repaint();
                  n2 = 0;
                  in2.purgeAll();
                  drawTimer = 0; //forces next packet to be drawn immediately after bar redraw (no sec avg)
              } else {
                  //no chart
                  chart.isChartVisible(false);
              }
          } else { //datapacket

              //int val = (   chartRange*(n1 - amplitudelow) / (amplitudehigh-amplitudelow)  );
              double valdub = (chartRange * (ndub - amplitudelow) /
                               (amplitudehigh - amplitudelow));
              //System.out.println("nl for temp1 sharts ="+n1);
              if (n1 > 0) { //only push when data is above 0

                  chart.push(valdub);chart.repaint();

                  //pushtoChart(valdub);
              }
              prev = n1;
              in1.purgeAll();
              in2.purgeAll();
              ndub = 0;
              valdub = 0;
              return;
          }
      }



        /*
        //gsr2
        if (chan==2){
            if (mode == -1)return;
            if (mode == 0){return;}

            if (n2 > 0) //if theres a session packet
            {
                if ((mode == 1)&&(probeconf == 2)) {    //if dynamic
                    amplitudehigh = b2[2];
                    amplitudelow = b2[1];
                }
                else {    //if default
                    amplitudehigh = n1*2;
                    amplitudelow = n1/2;
                }
                if ((mode == 2)&&(probeconf == 3)){
                    double percentsens = (1+((double)sensitivity/100));
                    double temp = n1*percentsens;
                    amplitudehigh = (int)(1.2*temp);
                    percentsens = 1/percentsens;
                    temp = n1*percentsens;
                    amplitudelow = (int)(temp/1.2);
                }
                chart.yMaxValue = amplitudehigh;
                chart.yMinValue = amplitudelow;
                chartRange = chart.getChartHeight();
                chart.resetChart();
                super.reinit();
                n2 = 0;in2.purgeAll();
            }
            int val = (   chartRange*(n1 - amplitudelow) / (amplitudehigh-amplitudelow)  );
            //chart.push(val);chart.repaint();
            pushtoChart(val);
            prev = n1;
            in1.purgeAll();
            in2.purgeAll();
            //System.out.println("gsr2value = "+n1);
            return;
        }

        //temp2
        if (chan==3){
            if (mode == -1)return;
            if (mode == 0)return;
            if ((mode == 2)&& !(probeconf == 12))return;
            if (bioera.device.impl.Presagedevice.Fahrenheit==true) { //if farhen
                n1 = n1/100;n1 = (n1*9/5)+32;n1 = n1*100;n1 = Math.round(n1);n1 = n1/100;
            }
            else{n1 = n1/100;} //if cels
            if (n2 > 0) //if theres a session packet
            {
                if ((mode == 1) && (probeconf == 8 )){    //if dynamic
                    amplitudehigh = b2[2];
                    amplitudelow = b2[1];
                }
                else{    //if default
                    if (bioera.device.impl.Presagedevice.Fahrenheit==true) {amplitudehigh = 104;amplitudelow = 60;}
                    else {amplitudehigh=40;amplitudelow=20;}
                }
                if ((mode == 2) && (probeconf == 12)){
                    double percentsens = (1+((double)sensitivity/100));
                    double temp = n1*percentsens;
                    amplitudehigh = (int)(1.2*temp);
                    percentsens = 1/percentsens;
                    temp = n1*percentsens;
                    amplitudelow = (int)(temp/1.2);
                }
                chart.yMaxValue = amplitudehigh;
                chart.yMinValue = amplitudelow;
                chartRange = chart.getChartHeight();
                chart.resetChart();
                super.reinit();
                n2 = 0;in2.purgeAll();
            }

            //System.out.println("amplitudehigh "+amplitudelow);
            //System.out.println("amplitudelow "+amplitudehigh);
            int val = (   chartRange*(n1 - amplitudelow) / (amplitudehigh-amplitudelow)  );
            //chart.push(val);chart.repaint();
            pushtoChart(val);
            prev = n1;
            in1.purgeAll();
            in2.purgeAll();
            //System.out.println("temp1value = "+n1);
            return;
        }

*/

}
/**
 * Element constructor comment.
 */

public void pushtoChart(int val) throws Exception {
    //chart.push(val);chart.repaint();

    //System.out.println("val = "+val);


    if (drawTimer == 0) { //push on first datapacket
        drawTimer = System.currentTimeMillis();
        chart.push(val);chart.repaint();
        //System.out.println("val = "+val);
        return;
    }
    if ( (System.currentTimeMillis() < drawTimer+1000) ) { //avg value to push until a second passes
        pktvalwithinSec = (pktvalwithinSec+val);
        numpktsinSec++;
    }
    else {                                 //push avg val to chart
        pktvalwithinSec = (pktvalwithinSec+val);
        numpktsinSec++;
        val = (pktvalwithinSec/numpktsinSec);
        chart.push(val);chart.repaint();
        pktvalwithinSec = 0;
        numpktsinSec = 0;
        drawTimer = System.currentTimeMillis();
    }
}

public void pushtoChart(double val) throws Exception {
    //chart.push(val);chart.repaint();

    //System.out.println("val = "+val);


    if (drawTimer == 0) { //push on first datapacket
        drawTimer = System.currentTimeMillis();
        chart.push(val);chart.repaint();
        //System.out.println("val = "+val);
        return;
    }

    if ( (System.currentTimeMillis() < drawTimer+1000) ) { //avg value to push until a second passes
        pktvalwithinSecdub = (pktvalwithinSecdub+val);
        numpktsinSecdub = numpktsinSecdub+1;
    }
    else {                                 //push avg val to chart
        pktvalwithinSecdub = (pktvalwithinSecdub+val);
        numpktsinSecdub = numpktsinSecdub+1;
        val = (pktvalwithinSecdub/numpktsinSecdub);
        chart.push(val);chart.repaint();
        pktvalwithinSec = 0;
        numpktsinSec = 0;
        drawTimer = System.currentTimeMillis();
    }

}

public final void start() {
    gotmode=false; probeconf=0; mode=0; drawTimer=0; pktvalwithinSec=0; numpktsinSec=0;
    chart.isChartVisible(true);
}

public void reinit() throws Exception {
/*	if (((SmallVerticalScale)(amplitudeRange.scaledValue.scale)).getSelectedIndex() == -1) {
                ((SmallVerticalScale)(amplitudeRange.scaledValue.scale)).setSelectedItemThrow("%");
        }

        if (((SmallBalancedVertScale)amplitudeRange.unit).getSelectedIndex() == -1) {
                ((SmallBalancedVertScale)amplitudeRange.unit).setSelectedItemThrow("uV");
        }
*/
        predecessorElement = getFirstElementConnectedToInput(0);
        if (predecessorElement == null) {
                setReinited(true);
                return;
        } else if (!predecessorElement.isReinited()) {
                // Wait until the preceding element is inited
                return;
        }


        chart.yMaxValue = amplitudehigh;
        chart.yMinValue = amplitudelow;

        chartRange = chart.getChartHeight();
        chartLevel = 1;


        chart.xUnit = "";

        chart.xMaxValue = Float.MAX_VALUE;
        chart.xMinValue = Float.MIN_VALUE;


/*
        amplitudeRange.update(this);
        chart.yMaxValue = amplitudeRange.getScaleMax()/2;
        chart.yMinValue = amplitudeRange.getScaleMin();
        chartRange = chart.getChartHeight();
        chartLevel = (int)(chartRange * amplitudeRange.getChartRangeRatio());
        digiRange = (int) amplitudeRange.getChartDigiRange();


        chart.yUnit = amplitudeRange.unit.getUnitStr();

        chart.xUnit = "";
        chart.xMaxValue = Float.MAX_VALUE;
        chart.xMinValue = Float.MIN_VALUE;
*/


        //System.out.println("chart.yMaxValue="+chart.yMaxValue);
        //System.out.println("chart.yMinValue="+chart.yMinValue);
        //System.out.println("chartLevel="+chartLevel);
        //System.out.println("digiRange="+digiRange);

        chart.isChartVisible(true);  //show chart on reinit
        super.reinit();

        //System.out.println("computeN=" + computeN + ", averageLastNSamples=" + averageLastNSamples);

}
}
