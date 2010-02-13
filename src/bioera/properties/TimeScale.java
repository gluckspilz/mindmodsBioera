/* TimeScale.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.properties;

import bioera.graph.designer.*;
import bioera.processing.*;

public class TimeScale extends Scale {
	public static final String items[] = new String[] {
		"s",
		"ms",
		"min",
		"h",
		"inv",
	};

	protected static final int SEC = 0;
	protected static final int MILLI_SEC = 1;
	protected static final int MIN = 2;
	protected static final int HOUR = 3;
	protected static final int INV = 4;	
public TimeScale(Element e) {
	super(items);

	setSelectedIndex(SEC);
}
/**
 * getMaxDescValue method comment.
 */
public int getMaxDescValue() {
	return 0;
}
/**
 * getMaxDescValue method comment.
 */
public int getMaxDescValue(double d) {
	return 0;
}
/**
 * getMinDescValue method comment.
 */
public int getMinDescValue() {
	return 0;
}
/**
 * getMinDescValue method comment.
 */
public int getMinDescValue(double d) {
	return 0;
}
/**
 * getRange method comment.
 */
public int getRange(int r) {
	return 0;
}
/**
 * getUnitStr method comment.
 */
public java.lang.String getUnitStr() {
	String r = getSelectedItem();
	if (r != null && r.startsWith("="))
		return r.substring(1);
	return r;
}
/**
 * isBalanced method comment.
 */
public boolean isBalanced() {
	return false;
}
/**
 * toDigit method comment.
 */
public int toDigit(double n) {
	switch (index) {
		case MILLI_SEC: return (int) n;
		case SEC: return (int)(n * 1000);
		case MIN: return (int) (n * 60000);
		case HOUR: return (int)(n * 3600000);
		default:
		return -1;
	}
}
/**
 * toDigit method comment.
 */
public int toDigit(int n) {
	switch (index) {
		case MILLI_SEC: return n;
		case SEC: return n * 1000;
		case MIN: return n * 60000;
		case HOUR: return n * 3600000;
		default:
		return -1;
	}
}
/**
 * toScale method comment.
 */
public int toScale(int n) {
	switch (index) {
		case MILLI_SEC: return n;
		case SEC: return n / 1000;
		case MIN: return n / 60000;
		case HOUR: return n / 3600000;
		default:
		return -1;
	}
}
/**
 * update method comment.
 */
public void update(bioera.processing.Element e) {}

/**
 * calculatePreciseRangeRatio method comment.
 */
public double calculatePreciseRangeRatio(double value, bioera.processing.SignalParameters p) {
	switch (index) {
		case MILLI_SEC: 
			return value * 1000;
		case SEC: 
			return value;
		case MIN: 
			return value / 60;
		case HOUR: 
			return value / 3600;
		default:
			return -1;
	}
}

/**
 * toScale method comment.
 */
public float toFloatScale(float n) {
	switch (index) {
		case MILLI_SEC: return n;
		case SEC: return n / 1000;
		case MIN: return n / 60000;
		case HOUR: return n / 3600000;
		default:
		return -1;
	}
}

/**
 * calculatePreciseScaleMaxValue method comment.
 */
public double calculatePreciseScaleMaxValue(bioera.processing.SignalParameters p) {
	return 0;
}

/**
 * calculatePreciseScaleMinValue method comment.
 */
public double calculatePreciseScaleMinValue(bioera.processing.SignalParameters p) {
	return 0;
}

/**
 * calculatePreciseRangeRatio method comment.
 */
public double calculatePreciseRangeRatio(double value) {
	return 0;
}

/**
 * calculatePreciseScaleDigitalRatio method comment.
 */
public double calculatePreciseScaleDigitalRatio() {
	return 0;
}

/**
 * calculatePreciseScaleMaxValue method comment.
 */
public double calculatePreciseScaleMaxValue() {
	return 0;
}

/**
 * calculatePreciseScaleMinValue method comment.
 */
public double calculatePreciseScaleMinValue() {
	return 0;
}

/**
 * calculatePreciseScaleStartRatio method comment.
 */
public double calculatePreciseScaleStartRatio(double rangeRatio) {
	return 0;
}

/**
 * calculatePreciseScaleStartRatio method comment.
 */
public double calculatePreciseScaleStartRatio(double rangeRatio, bioera.processing.SignalParameters p) {
	return 0;
}

/**
 * calculatePreciseScaleStartRatio method comment.
 */
public double calculatePreciseScaleStartRatio(SignalParameters p) {
	return 0;
}

/**
 * calculatePrecisePhysicalRange method comment.
 */
public double calculatePrecisePhysicalRange() {
	return 0;
}
}
