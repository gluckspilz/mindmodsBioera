/* TempBarDisplayDynScale.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.properties.*;
import bioera.fft.*;
import bioera.graph.chart.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class TempBarDisplayDynScale extends Display {
//	public int range = 1000;

	//public FloatCompoundUnitScale amplitudeRange;
        public boolean tempType = false;
	private final static String propertiesDescriptions[][] = {
                {"tempType", "Fahrenheit", ""},
	};

	private BufferedScalarPipe in1, in2;
	private VerticalBarChart chart;
	private int digiRange, chartLevel, b1[], b2[], trgt[], prev;
        private int processed = 0;
	private int chartRange;
        private int amplitudelow=20;
        private int amplitudehigh=40;

/**
 * VectorDisplay constructor comment.
 */
public TempBarDisplayDynScale() {
	super();
	setName("TempBarDyn");
	in1 = (BufferedScalarPipe) inputs[0];
        in2 = (BufferedScalarPipe) inputs[1];
	in1.setName("IN");
        in2.setName("SCALE");

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


private int CtoF(int t) throws Exception {
    t = t/100;t = (t*9/5)+32;t = t*100;t = Math.round(t);t = t/100;
    return t;
}

public final void process() throws Exception {

	int t = in1.available();
        int n2 = in2.available();

        if (t == 0){return;}

        if (n2 > 0)
        {
            amplitudehigh = b2[2];
            amplitudelow = b2[1];


           chart.yMaxValue = amplitudehigh;
           chart.yMinValue = amplitudelow;

           chartRange = chart.getChartHeight();

           chart.resetChart();
           super.reinit();
        }

        t = b1[t-1];


/*
        if (processed > 2){
            if (n1 == prev)
                return;
        }
*/

        if (tempType==true){
            t = CtoF(t);
        }
            else{t = t/100;}


	prev = t;
        int val;
        val = (   chartRange*(t - amplitudelow) / (amplitudehigh-amplitudelow)  );
        chart.push(val) ;
	chart.repaint();
	in1.purgeAll();
        in2.purgeAll();
        processed++;

}
/**
 * Element constructor comment.
 */
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

	super.reinit();

	//System.out.println("computeN=" + computeN + ", averageLastNSamples=" + averageLastNSamples);

}
}
