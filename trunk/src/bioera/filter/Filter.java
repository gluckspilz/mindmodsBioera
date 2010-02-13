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

package bioera.filter;

import java.lang.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public abstract class Filter {
	public final static String items[] = {
//		"INTEGER",
//		"FLOAT",
		"FIDLIB",
	};

	public static final int TYPE_FIDLIB = 0;

	public static final int C_INTEGER = 0;
	public static final int C_FLOAT = 1;
	public static final int C_NATIVE = 2;
	public static final int C_DOUBLE = 3;
	public static final int C_LONG = 4;

	public int type;

	public final static String types[] = {
	};
	
	protected static boolean debug = true;//bioera.Debugger.get("filter");
protected Filter() {
}
public abstract void close();
public abstract String [] getAvailableFilterTypes();
public abstract double getDelay();
public abstract String [] getFilterBands(int index);
public final static Filter getFilterImpl(int implementation) throws Exception {
	return newInstance(implementation);
}
public abstract void init(int filterType, int filterBand, int filterOrder, double rate, double freq0, double freq1, int minInputValue, int maxInputValue, int minOutputValue, int maxOutputValue) throws Exception;
public final static Filter newInstance() throws Exception {
	return newInstance(TYPE_FIDLIB);
}
public final static Filter newInstance(int itype) throws Exception {
	Filter ret;
	switch (itype) {
		case TYPE_FIDLIB:
			ret = new FidlibFilter();
			break;
		default:
			throw new RuntimeException("Unknown type: " + itype);
	}

	ret.type = itype;

	return ret;
}
public abstract void process(int input[], int output[], int size);
public abstract int process(int value);
public int type() throws Exception {
	return this.type;
}

public abstract double getResponse(double freq);
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
