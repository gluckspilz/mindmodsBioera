/* FidlibFilter.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.filter;

public class FidlibFilter extends Filter {
	public final static int FILTER_BESSEL = 0;
	public final static int FILTER_BUTTERWORTH = 1;
	public final static int FILTER_CHEBYSHEV = 2;
	
	public final static String filterTypes[] = {
		"Bessel",
		"Butterworth",
		"Chebyshev",
	};

	public final static String filterBands[] = {
		"BandPass",
		"LowPass",
		"HighPass",
		"BandStop",
	};

	bioera.nativeobjects.FidlibCFilter fidlib = new bioera.nativeobjects.FidlibCFilter();

	int id = -1;
	int inputHalfValue, outputHalfValue;
	double inputHalfRange, outputHalfRange;
	double rate;
/**
 * FidlibNative constructor comment.
 * @param itype int
 */
protected FidlibFilter() {
	super();
}
/**
 * close method comment.
 */
public void close() {
	fidlib.close(id);
}
/**
 * getAvailableFilterTypes method comment.
 */
public java.lang.String[] getAvailableFilterTypes() {
	return filterTypes;
}
/**
 * getDelay method comment.
 */
public double getDelay() {
	return ((double)fidlib.getFilterDelay(id)) / rate;
}
/**
 * getFilterBands method comment.
 */
public java.lang.String[] getFilterBands(int index) {
	return filterBands;
}
/**
 * perform method comment.
 */
public void init(int filterType, int filterBand, int filterOrder, double irate, double freq0, double freq1, int minValue, int maxValue, int minOutputValue, int maxOutputValue) throws Exception {
	if (filterType < 0 || filterType >= filterTypes.length)
		throw new RuntimeException("Out of range");

	rate = irate;
		
	String spec = "";
	switch (filterBand) {
		case 0:
			spec += "Bp";
			if (filterOrder > 32)
				throw new RuntimeException("Max order for band pass filters is 32");
			break;
		case 1:
			spec += "Lp";
			break;
		case 2:
			spec += "Hp";
			break;
		case 3:
			spec += "Bs";
			break;
		default:
			throw new Exception("Unrecognized band type");
	}
	
	switch (filterType) {
		case 0:
			spec += "Be";
			if (filterOrder > 10)
				throw new RuntimeException("Max order for Bessel filter is 10");
			break;
		case 1:
			spec += "Bu";
			break;
		case 2:
			spec += "Ch";
			break;
	}

	spec += Integer.toString(filterOrder);

	// This is additional parameter required for Chebyshev filter
	if (filterType == FILTER_CHEBYSHEV)
		spec += "/-1";

	//if (debug)
		//System.out.println("FidlibFilter params spec='" + spec + "', freq0=" + freq0 + ", freq1=" + freq1 + " adj=0");

	id = fidlib.init(id, spec, rate, freq0, freq1, 0);

	if (id == -1)
		throw new Exception("Fidlib initialization failed");
	
	inputHalfValue = (maxValue + minValue) / 2;
	outputHalfValue = (maxOutputValue + minOutputValue) / 2;
	inputHalfRange = (maxValue - minValue) / 2.0;
	outputHalfRange = (maxOutputValue - minOutputValue) / 2.0;
}
/**
 * perform method comment.
 */
public void process(int[] input, int[] output, int size) {
	for (int i = 0; i < size; i++){
		output[i] = outputHalfValue + (int) (fidlib.run(id, (input[i] - inputHalfValue) / inputHalfRange) * outputHalfRange);
		//System.out.println("f: in=" + input[i] + " out=" + output[i]);
	}
}
/**
 * process method comment.
 */
public int process(int value) {
	return outputHalfValue + (int) (fidlib.run(id, (value - inputHalfValue) / inputHalfRange) * outputHalfRange);
}

/**
 * getResponse method comment.
 */
public double getResponse(double freq) {
	double f = freq / rate;
	if (f < 0 || f > rate / 2)
		throw new RuntimeException("Response range exceeded: " + freq);
	return fidlib.getFilterResponse(id, f);
}
}
