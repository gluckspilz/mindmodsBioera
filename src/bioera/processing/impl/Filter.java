/* Filter.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.graph.chart.*;
import bioera.properties.*;
import bioera.processing.*;
import bioera.storage.*;
import bioera.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class Filter extends SingleElement implements ChartAction {
	public ComboProperty filterTypes = new ComboProperty(new String[]{"none"});
	public ComboProperty filterBands = new ComboProperty(new String[]{"none"});
	public double lowFrequency = 1;
	public double highFrequency = 10;
	public int order = 4;
	public boolean amplitudePrecision = true;
	public double filterDelay = 0;
	public GraphChart response;
	public ComboProperty implementation = new ComboProperty(bioera.filter.Filter.items);

	private final static String propertiesDescriptions[][] = {
		{"implementation", "Implementation", ""},
		{"filterTypes", "Filter type", ""},
		{"filterBands", "Band", ""},
		{"lowFrequency", "Low/middle freq [Hz]", ""},
		{"highFrequency", "High frequency [Hz]", ""},
		{"order", "Filter order", ""},
		{"response", "Filter response", ""},
		{"filterDelay", "Filter delay [ms]", "", "false"},
		{"amplitudePrecision", "Amplitude precision", ""},
	};	

	private bioera.filter.Filter filter;
	
	private ScalarPipeDistributor out;
	private BufferedScalarPipe in;
	private int inb[], implPrev = -2;
	private StaticChart staticChart;

	protected static boolean debug = bioera.Debugger.get("impl.filter");
/**
 * Element constructor comment.
 */
public Filter() {
	super();

	response = new GraphChart(staticChart = new StaticChart(this));
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Converts input sample according to chosen function";
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
public void initialize() {
	setName("Filter");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	inb = in.getBuffer();
	out = (ScalarPipeDistributor) outputs[0];
	out.setName("OUT");	
}
/**
 * Element constructor comment.
 */
public final void process() {
	if (in.isEmpty())
		return;

	int n = in.available();
	for (int i = 0; i < n; i++){
		out.write(filter.process(inb[i]));
	}	

	in.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (implementation.getSelectedIndex() == -1) {		
		implementation.setSelectedIndex(0);
	}

	verifyDesignState(order > 0 && order < 99);	
		
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		throw new DesignException("Not connected");
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	if (implPrev != implementation.getSelectedIndex()) {
		reInitializeFilter();
		implPrev = implementation.getSelectedIndex();
	}

	if (lowFrequency < 0)
		throw new Exception("Low frequency is too low");
	if (lowFrequency > predecessorElement.getSignalParameters().getSignalRate() / 2)
		throw new Exception("Low frequency is too high");

	if (highFrequency < 0 || highFrequency > predecessorElement.getSignalParameters().getSignalRate() / 2)
		throw new Exception("High frequency is out of range");

		if (lowFrequency > highFrequency && (filterBands.getSelectedIndex() == 0 || filterBands.getSelectedIndex() == 3) )
			throw new Exception("Low frequency must smaller then high frequency");
		
	if (filterTypes.getItems().length == 1 && "none".equals(filterTypes.getItem(0))) {
		reInitializeFilter();
	}

	int precisionIncreaseBits = 0;
	if (amplitudePrecision) {
		int inr = predecessorElement.getSignalParameters().getSignalResolutionBits();
		precisionIncreaseBits = 15 - inr;
		if (precisionIncreaseBits > inr - 1)
			precisionIncreaseBits = inr - 1;
		if (precisionIncreaseBits < 0)
			throw new Exception("Input range should be up to 15 bits, now is " + inr);
		if (precisionIncreaseBits > 0) {
			//System.out.println("precisionIncreaseBits="+precisionIncreaseBits);
			setOutputDigitalRange(predecessorElement.getSignalParameters().getDigitalRange() << precisionIncreaseBits);
			setOutputResolutionBits(predecessorElement.getSignalParameters().getSignalResolutionBits() + precisionIncreaseBits);
		} else {
			setOutputDigitalRange(predecessorElement.getSignalParameters().getDigitalRange());
			setOutputResolutionBits(predecessorElement.getSignalParameters().getSignalResolutionBits());
		}
		if (debug)
			System.out.println("Filter output range is " + getSignalParameters().getDigitalRange());
	}
	
	try {
		if (debug) {
			System.out.println("Initializing filter:");
			System.out.println("type: " + filterTypes.getSelectedItem());
			System.out.println("pass type: " +filterBands.getSelectedIndex());
			System.out.println("order: " + order);
			System.out.println("rate: " + predecessorElement.getSignalParameters().getSignalRate());
			System.out.println("lowFrequency: " + lowFrequency);
			System.out.println("highFrequency: " + highFrequency);
		}
		
		filter.init(
			filterTypes.getSelectedIndex(),
			filterBands.getSelectedIndex(), 
			order, 
			predecessorElement.getSignalParameters().getSignalRate(), 
			lowFrequency, 
			highFrequency,
			predecessorElement.getSignalParameters().getDigitalMin(),
			predecessorElement.getSignalParameters().getDigitalMax(),
			getSignalParameters().getDigitalMin(),
			getSignalParameters().getDigitalMax());
	} catch (UnsatisfiedLinkError e) {
		throw new Exception("Native filter support for this machine was not found");
	} catch (Throwable e) {
		throw new Exception("Filter initialization error: " + e);
	}

	//System.out.println("1 " + implementation.getSelectedIndex() + " " + implPrev);
	
	//System.out.println("Filter delay=" + filter.getDelay());

	if ((filterBands.getSelectedIndex() == 0 || filterBands.getSelectedIndex() == 3) && highFrequency == lowFrequency)
		filterDelay = 999999999;
	else
		filterDelay = ((double)Math.round(filter.getDelay() * 10000)) / 10;
	staticChart.xMinValue = 0;
	staticChart.xMaxValue = 100;
	staticChart.yMinValue = 0;
	staticChart.yMaxValue = 1;
//	staticChart.xUnit = "Hz";

	//repopulateChart();
	staticChart.resetChart();
	staticChart.getComponent().validate();

	if (debug)
		System.out.println("Filter reinited");
	
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void reInitializeFilter() throws Exception {
	if (filter != null)
		filter.close();
	filter = bioera.filter.Filter.newInstance(implementation.getSelectedIndex());
	filterTypes.setItems(filter.getAvailableFilterTypes());
	if (filterTypes.getSelectedIndex() == -1)
		filterTypes.setSelectedIndex(0);
	filterBands.setItems(filter.getFilterBands(filterTypes.getSelectedIndex()));
	if (filterBands.getSelectedIndex() == -1)
		filterBands.setSelectedIndex(0);
	if (debug)
		System.out.println("Initialized filter");
}
/**
 * Insert the method's description here.
 * Creation date: (11/1/2004 6:33:01 PM)
 */
public void repopulateChart() {
	if (filter == null) {
		System.out.println("filter not initialized");
		Thread.dumpStack();
		return;
	}
	
	int h = staticChart.getChartHeight();
	int w = staticChart.getChartWidth();

	//System.out.println("repopulating");

	float middle, right, left, span;
	switch (filterBands.getSelectedIndex()) {
		case 0:
			// Band pass
			middle = (float) (lowFrequency+highFrequency) / 2;
			right = searchFreq(middle, 0.5f, 1, -1);
			left = searchFreq(middle, 0.5f, -1, -1);
			span = Math.max(middle - left, right - middle);
			break;
		case 3:
			// Band stop
			middle = (float) (lowFrequency+highFrequency) / 2;
			right = searchFreq(middle, 0.5f, 1, 1);
			left = searchFreq(middle, 0.5f, -1, 1);
			span = Math.max(middle - left, right - middle);
			break;
		case 1:
			// Low pass
			middle = (float) lowFrequency;
			right = searchFreq(middle, 0.5f, 1, -1);
			left = searchFreq(middle, 0.5f, -1, 1);
			span = Math.max(middle - left, right - middle) * 2;
			break;
		case 2:
			// High pass
			middle = (float) lowFrequency;
			right = searchFreq(middle, 0.5f, 1, 1);
			left = searchFreq(middle, 0.5f, -1, -1);
			span = Math.max(middle - left, right - middle) * 2;
			break;
		default:
			return;
	}

	//System.out.println("left=" + left);
	//System.out.println("right=" + right);

	float from = middle - span * 2;
	if (from < 0)
		from = 0;
	float to = middle + span * 2;
	if (to > getSignalParameters().getSignalRate() / 2)
		to = getSignalParameters().getSignalRate() / 2;
//System.out.println("from=" + from);
//System.out.println("to=" + to);

	staticChart.xMinValue = from;
	staticChart.xMaxValue = to;

	for (int i = 0; i < w; i++){
		//System.out.println("arg=" + (from + i * (span*4)/ w));
		staticChart.values[i] = (int)(h * filter.getResponse(from + i * (span*4)/ w));
	}
	
	//staticChart.getComponent().validate();
	
}
/**		   _ _
		 . 
		.  
	   .
	 --
 */
public float searchFreq(float startFreq, float destValue, int step, int ori) {
	float ie = startFreq, is;
	int i = 100;
	while (filter.getResponse(ie) * ori < destValue * ori  && i-- > 0) {
		ie += step;
		if (ie < 0) {
			ie = 0;
			break;
		}
	}
	
	is = ie - step;
	float im = (is + ie) / 2;
	for (i = 0; i < 100; i++){
		if (filter.getResponse(im) * ori < destValue * ori)
			is = im;
		else
			ie = im;
		im = (is + ie) / 2;			
	}
	
	return im;
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
}
}
