/* DynOscilloscope.java

 */

package bioera.processing.impl;


import java.io.*;
import java.util.*;

import bioera.Main;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.graph.chart.*;
import bioera.properties.*;


public final class DynOscilloscope2 extends Display {
	public float showSeconds = 50;
	//private FloatCompoundUnitScale amplitudeRange;
	public boolean highPrecision = true;
	//public boolean joinPoints = true;
	public boolean reflectTimeRange = true;
	public boolean grid = true;


	private final static String propertiesDescriptions[][] = {/*
		{"highPrecision", "High precision", ""},
		{"reflectTimeRange", "Reflect time range", ""},
		{"showSeconds", "Time range [s]", ""},
		{"amplitudeRange", "Amplitude range", ""},
		{"joinPoints", "Join points", ""}, */
	};

	int b1[], b2[], b3[], b4[], b5[], lastMM = Integer.MIN_VALUE, min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
	int counter, mean;
	int chartRange, chartWidth;
	private HorizontalScalarChart chart;
	private int digiRange;
	private int chartLevel;//, amax;
	private BufferedScalarPipe in1,in2,in3,in4,in5;
        boolean gotmode = false;
        boolean firstrun = true;
        boolean redrawchart = false;
        private int databufferIndex = 0;
        int b1MemBuff[] = new int[4090 * 16];
        int b2MemBuff[] = new int[4090 * 16];
        int elapsedMemBuff[] = new int[4090 * 16];
        private int amplitudehigh = 500;
        private int amplitudelow = 0;
        private int mode = -1;
        private int probeconf = -1;
        private int sessionlength = -1;
        private int samplecounter = 0;
        //private int increment = 0;
        private int samplerate;
        private int sensitivity = 0;
        private int x = 0;


	protected static boolean debug = bioera.Debugger.get("impl.Oscilloscope");
/**
 * SignalDisplay constructor comment.
 */
public DynOscilloscope2() {
	super();
	setName("DynOsc");
	in1 = (BufferedScalarPipe)inputs[0];
        in2 = (BufferedScalarPipe)inputs[1];
        in3 = (BufferedScalarPipe)inputs[2];
        in4 = (BufferedScalarPipe)inputs[3];
        in5 = (BufferedScalarPipe)inputs[4];
	in1.setName("IN1");
        in2.setName("IN2");
        in3.setName("SES");
        in4.setName("TIME");
        in5.setName("SESDATA");
	b1 = in1.getBuffer();
        b2 = in2.getBuffer();
        b3 = in3.getBuffer();
        b4 = in4.getBuffer();
        b5 = in5.getBuffer();

//	amplitudeRange = new FloatCompoundUnitScale(new FloatCompoundScale(100, new SmallVerticalScale(this)), new SmallBalancedVertScale(this));
	//amplitudeRange = new FloatCompoundUnitScale(new FloatCompoundScale(100, new SmallVerticalScale(this)), new SmallBalancedVertScale(this));
}
/**
 * Element constructor comment.
 */
public Chart createChart() {
	return chart = new HorizontalScalarChart();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Displays linear stream data on graphic chart";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 5;
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
 * SignalDisplay constructor comment.
 */
public String printAdvancedDetails() throws Exception {
	return super.printAdvancedDetails()
	+ ">counter=" + counter + "\n"
	+ ">chartHeight=" + chart.getChartHeight() + "\n"
	+ ">chartWidth=" + chart.getChartWidth() + "\n"
	+ ">chart.valueIncrement=" + chart.valueIncrement + "\n";
}
/**
 * Element constructor comment.
 */
public final void process() {
	int n1 = in1.available();
        int n2 = in2.available();
        int n3 = in3.available();
        int n4 = in4.available();
        int n5 = in5.available();
        double factor;


        if (n5 != 0) {

            //get session info first

            if (gotmode == false) { //grab mode & probeconf on first packet
                mode = b3[4];
                probeconf = b3[5];
                sensitivity = b3[3];
                sessionlength = b3[7];
                samplerate = b3[9];
                //increment = (sessionlength * samplerate) / chart.getChartWidth();
                gotmode = true;
            }



        }

        else{ //live session


	if ((n1 == 0) || (n2 == 0)) return;
        n1 = b1[n1-1];
        n2 = b2[n2-1];



        if (gotmode == false) { //grab mode & probeconf on first packet
            mode = b3[4];
            probeconf = b3[5];
            sensitivity = b3[3];
            sessionlength = b3[7];
            samplerate = b3[9];
            //increment = (sessionlength * samplerate) / chart.getChartWidth();
            gotmode = true;
        }

        if (n3 > 0) { //if theres a session packet


            //sessionlength = b3[7];

            if (mode == 2) { //if mode 2
                if (firstrun == true){
                if (n1 > n2) {
                    double percentsens = (1 + ((double) sensitivity / 100));
                    double temp = n1 * percentsens;
                    amplitudehigh = (int) (1.5 * temp);
                    if (amplitudehigh > 1100) {
                        amplitudehigh = 1100;
                    }
                    percentsens = 1 / percentsens;
                    temp = n2 * percentsens;
                    amplitudelow = (int) (temp / 1.5);

                    chart.yMaxValue = amplitudehigh;
                    chart.yMinValue = amplitudelow;
                    chartRange = chart.getChartHeight();
                    chartWidth = chart.getChartWidth();
                    chart.xMinSpace = 15;
                    chart.xMinValue = 0;
                    chart.xMaxValue = sessionlength;
                    chart.xUnit = "s";
                    double ratio = chart.rescaleX();
                    chart.rescaleY();
                    chart.resetChart();
                    n3 = 0;
                    in2.purgeAll();
                    if (firstrun == true){firstrun = false;  Main.app.sessionStartTime = System.currentTimeMillis();}
                    else{
                        redrawchart=true;}  //redraw chart lines after new x/y chartredraw on next processloop
                } else {
                    double percentsens = (1 + ((double) sensitivity / 100));
                    double temp = n2 * percentsens;
                    amplitudehigh = (int) (1.5 * temp);
                    if (amplitudehigh > 1100) {
                        amplitudehigh = 1100;
                    }
                    percentsens = 1 / percentsens;
                    temp = n1 * percentsens;
                    amplitudelow = (int) (temp / 1.5);

                    chart.yMaxValue = amplitudehigh;
                    chart.yMinValue = amplitudelow;
                    chartRange = chart.getChartHeight();
                    chartWidth = chart.getChartWidth();
                    chart.xMinSpace = 15;
                    chart.xMinValue = 0;
                    chart.xMaxValue = sessionlength;
                    chart.xUnit = "s";
                    double ratio = chart.rescaleX();
                    chart.rescaleY();
                    chart.resetChart();
                    n3 = 0;
                    in2.purgeAll();
                    if (firstrun == true){firstrun = false; Main.app.sessionStartTime = System.currentTimeMillis();}
                    else{
                        redrawchart=true;}  //redraw chart lines after new x/y chartredraw on next processloop

                }
            }
            }

            else { //if mode 0 or 1
                if ((probeconf == 1) || (probeconf == 2)) {
                    factor = 1;
                } else {
                    factor = 1;
                }
/*
                double percentsens = (1 + ((double) sensitivity / 100));
                double temp = n1 * percentsens;
                amplitudehigh = (int) (factor * temp);
                if (amplitudehigh > 1100) {
                    amplitudehigh = 1100;
                }
                percentsens = 1 / percentsens;
                temp = n1 * percentsens;
                temp = temp / factor;
                amplitudelow = (int) temp;
 */
                amplitudehigh = (int)(b3[2] * factor);
                amplitudelow = (int)(b3[1] / factor);

                chart.yMaxValue = amplitudehigh;
                chart.yMinValue = amplitudelow;
                chartRange = chart.getChartHeight();

                chartWidth = chart.getChartWidth();
                chart.xMinSpace = 15;
                chart.xMinValue = 0;
                chart.xMaxValue = sessionlength;
                chart.xUnit = "s";
                double ratio = chart.rescaleX();
                chart.rescaleY();
                chart.resetChart();
                n3 = 0;
                in2.purgeAll();
                try{Thread.sleep(1000);}catch(Exception f){}
                if (firstrun == true){firstrun = false; Main.app.sessionStartTime = System.currentTimeMillis();}
                else{
                    redrawchart=true;}  //redraw chart lines after new x/y chartredraw on next processloop
            }

        }
        else{  //if there's a datapacket

            if (redrawchart==true){
                while (!chart.isInitialized()){}  //wait until chart re-scales/redraws
                redrawChart();redrawchart=false;
            }

                //write line(s) on chart
                //if (samplecounter++ % increment == 0) {

                        int var1 = ( chartRange * (n1 - amplitudelow) / (amplitudehigh-amplitudelow ) );
                        int var2 = 0;
                        float elapsed = b4[0];
                        float slen = sessionlength*1000;
                        float xxxx = elapsed/slen;
                        float xxxxx = chartWidth*xxxx;

                        if (mode == 2) {
                        var2 = ( chartRange * (n2 - amplitudelow) / (amplitudehigh-amplitudelow ) );
                        while (!chart.isInitialized()){}  //wait until chart re-scales/redraws
                        chart.pushValuewDualTime(var1, var2, (int)xxxxx);
                    }
                        else {
                            chart.pushValuewTime(var1, (int)xxxxx);
                            while (!chart.isInitialized()){}  //wait until chart re-scales/redraws
                        }

                        addtoMemBuff(n1, n2, (int)xxxxx);   //add to membuff for redrawchart(when index changes)
                        //chart.pushValue(chartLevel + (mean / chart.valueIncrement) * chartRange / digiRange);
                        mean = 0;
            //}
        }


        in1.purgeAll();in2.purgeAll();in3.purgeAll();in4.purgeAll();
	chart.repaint();

    }


/*	if (highPrecision) {
		int v;
		//System.out.println("arrived " + n + " data");
		for (int i = 0; i < n; i++) {
			v = b0[i];
			if (v < min)
				min = v;
			if (v > max)
				max = v;
			if (lastMM == Integer.MIN_VALUE)
				lastMM = v;
			if ((counter++ % chart.valueIncrement) == 0) {
				chart.pushMinMax2(
					chartLevel + lastMM * chartRange / digiRange,
					chartLevel + v * chartRange / digiRange,
					chartLevel + min * chartRange / digiRange,
					chartLevel + max * chartRange / digiRange);
				min = Integer.MAX_VALUE;
				max = Integer.MIN_VALUE;
				lastMM = Integer.MIN_VALUE;
			}
		}
	} else {
		for (int i = 0; i < n; i++) {
			mean += b0[i];
			if (counter++ % chart.valueIncrement == 0) {
				chart.pushValue(chartLevel + (mean / chart.valueIncrement) * chartRange / digiRange);
				mean = 0;
			}
		}
	}*/


}


private synchronized void redrawChart() {

    for (int i = 0; i < databufferIndex; i++){

        if (mode == 2) {
            int var1 = ( chartRange * (b1MemBuff[i] - amplitudelow) / (amplitudehigh-amplitudelow ) );
            int var2 = ( chartRange * (b2MemBuff[i] - amplitudelow) / (amplitudehigh-amplitudelow ) );
            chart.pushValuewDualTime(var1, var2, elapsedMemBuff[i]);
        }
        else{
            int var1 = ( chartRange * (b1MemBuff[i] - amplitudelow) / (amplitudehigh-amplitudelow ) );
            chart.pushValuewTime(var1, elapsedMemBuff[i]);
        }
    }
}


private final void addtoMemBuff(int ch1, int ch2, int elapsed) {
    b1MemBuff[databufferIndex] = ch1;
    b2MemBuff[databufferIndex] = ch2;
    elapsedMemBuff[databufferIndex] = elapsed;
    databufferIndex++;
   }

/**
 * SignalDisplay constructor comment.
 */
public void reinit() throws Exception {
/*	if (((SmallVerticalScale)(amplitudeRange.scaledValue.scale)).getSelectedIndex() == -1) {
		((SmallVerticalScale)(amplitudeRange.scaledValue.scale)).setSelectedItemThrow("%");
	}

	if (((SmallBalancedVertScale)amplitudeRange.unit).getSelectedIndex() == -1) {
		((SmallBalancedVertScale)amplitudeRange.unit).setSelectedIndex(0);
	}
*/

	//System.out.println("1.5");
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		disactivate("Not connected");
		reinited = true;
		//System.out.println("1.6");
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		//System.out.println("1.7");
		return;
	}


	//amplitudeRange.update(this);
        //chart.yMaxValue = amplitudeRange.getScaleMax();
	//chart.yMinValue = amplitudeRange.getScaleMin();


        chart.yMaxValue = amplitudehigh;
        chart.yMinValue = amplitudelow;




	chartRange = chart.getChartHeight();

//	chartLevel = (int)(chartRange * amplitudeRange.getChartRangeRatio());
//	digiRange = (int) amplitudeRange.getChartDigiRange();
 //       System.out.println("chartlevel= "+chartLevel+"digirange= "+digiRange);

	//System.out.println("chart.yMaxValue="+chart.yMaxValue);
	//System.out.println("chart.yMinValue="+chart.yMinValue);
	//System.out.println("chartLevel="+chartLevel);
	//System.out.println("digiRange="+digiRange);

	lastMM = getSignalParameters().getDigitalZero();

	chart.xMinSpace = 15;
	chart.xMinValue = 0;
	chart.xMaxValue = showSeconds;
	chart.xUnit = "s";
	chart.setXPixelInc(getSignalParameters().getSignalRate());


	//chart.yUnit = "k"; //fixthis

        //amplitudeRange.unit.getUnitStr();



	chart.grid = this.grid;
	//chart.joinPoints = this.joinPoints;

/*
	if (reflectTimeRange) {
		TimeRangeFeature tFeature = (TimeRangeFeature) ProcessingTools.traversPredecessorsForFeature(this, TimeRangeFeature.class);
		if (tFeature != null) {
			if (tFeature.physicalStart() > 0)
				chart.xMinValue = (float) tFeature.physicalStart();
			if (tFeature.physicalEnd() > 0)
				chart.xMaxValue =  (float) tFeature.physicalEnd();
		}
	}
*/

	double ratio = chart.rescaleX();
	chart.setXPixelInc(getSignalParameters().getSignalRate() / ratio);

	chart.rescaleY();

	super.reinit();
}
/**
 * Element constructor comment.
 */
public final void start() {

	chart.reset();
        gotmode=false;
        gotmode = false;
        firstrun = true;
        redrawchart = false;
        databufferIndex = 0;
        int b1MemBuff[] = new int[4090 * 16];
        int b2MemBuff[] = new int[4090 * 16];
        int elapsedMemBuff[] = new int[4090 * 16];
	lastMM = Integer.MIN_VALUE;
}
}
