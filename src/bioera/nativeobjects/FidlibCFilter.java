/* FidlibCFilter.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.nativeobjects;

public class FidlibCFilter {
	public int id = -1;
/**
 * Filter constructor comment.
 */
public FidlibCFilter() {
	super();

	NativeLib.load("fidlib");
}
/**
 * Returns handle to the filter
 */
public native void close(int id);
/**
 * Returns handle to the filter
 */
public native int init(int id, String spec, double rate, double freq0, double freq1, int f_adj);
/**
 * Returns handle to the filter
 */
public native double run(int id, double value);

/**
 * Returns handle to the filter
 */
public native int getFilterDelay(int id);

/**
 * Returns handle to the filter
 */
public native double getFilterResponse(int id, double frequency);
}
