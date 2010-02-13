/* DynOscilloscope.java

 */

package bioera.processing.impl;

import java.io.*;
import java.util.*;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.graph.chart.*;
import bioera.properties.*;


public final class DynOscilloscope extends Display {
	public float showSeconds = 50;
	//private FloatCompoundUnitScale amplitudeRange;
	public boolean highPrecision = true;
	public boolean joinPoints = true;
	public boolean reflectTimeRange = true;
	public boolean grid = true;


	private final static String propertiesDescriptions[][] = {/*
		{"highPrecision", "High precision", ""},
		{"reflectTimeRange", "Reflect time range", ""},
		{"showSeconds", "Time range [s]", ""},
		{"amplitudeRange", "Amplitude range", ""},
		{"joinPoints", "Join points", ""}, */
	};

	int b1[], b2[], lastMM = Integer.MIN_VALUE, min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
	int counter, mean;
	int chartRange;
	private HorizontalScalarChart chart;
	private int digiRange;
	private int chartLevel;//, amax;
	private BufferedScalarPipe in1,in2;
        boolean gotmode = false;
        private int amplitudehigh = 100;
        private int amplitudelow = 0;
        private int mode = -1;
        private int probeconf = -1;
        private int sessionlength = -1;

	protected static boolean debug = bioera.Debugger.get("impl.Oscilloscope");
/**
 * SignalDisplay constructor comment.
 */
public DynOscilloscope() {
	super();
	setName("DynOsc");
	in1 = (BufferedScalarPipe)inputs[0];
        in2 = (BufferedScalarPipe)inputs[1];
	in1.setName("IN");
        in2.setName("SES");
	b1 = in1.getBuffer();
        b2 = in2.getBuffer();

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
	return 2;
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

	if (n1 == 0)
		return;
        n1 = b1[n1-1];

        if (gotmode==false) //get mode & probeconf
        {
                /*for (int i = 0; i < 8; i++){
                    int p = b2[i];
                    System.out.print(p+" ");}System.out.println(" is the session info");*/
                mode = b2[4];
                probeconf = b2[5];
                gotmode=true;
        }

        if (n2 > 0) //if theres a session packet
        {

            amplitudehigh = b2[2];
            amplitudelow = b2[1];
            sessionlength = b2[7];

            chart.yMaxValue = amplitudehigh;
            chart.yMinValue = amplitudelow;
            chartRange = chart.getChartHeight();

       //wtf

            lastMM = getSignalParameters().getDigitalZero();

            chart.xMinSpace = 15;
            chart.xMinValue = 0;
            chart.xMaxValue = sessionlength;
            chart.xUnit = "s";
            chart.setXPixelInc(getSignalParameters().getSignalRate());
            ///System.out.println("sigrate= "+getSignalParameters().getSignalRate());

            double ratio = chart.rescaleX();
            //System.out.println("ratio ="+ratio);
            chart.setXPixelInc(getSignalParameters().getSignalRate() / ratio);
	    chart.rescaleY();



            chart.resetChart();
            n2 = 0;in2.purgeAll();
        }



        for (int i = 0; i < n1; i++) {
                mean += b1[i];
                if (counter++ % chart.valueIncrement == 0) {
                        if (n1 > 10000){n1=amplitudelow;}
                        int var = ( chartRange * (n1 - amplitudelow) / (amplitudehigh-amplitudelow ) );
                        chart.pushValue(var);
                        //System.out.println("n1 value= "+n1);
                        //System.out.println("push value= "+ var );
                        //chart.pushValue(chartLevel + (mean / chart.valueIncrement) * chartRange / digiRange);
                        mean = 0;
                }
            }

        in1.purgeAll();
        in2.purgeAll();
	chart.repaint();

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
	chart.joinPoints = this.joinPoints;

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
	lastMM = Integer.MIN_VALUE;
}
}
