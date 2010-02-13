/* FullVertScale.java v 1.0.9   11/6/04 7:15 PM
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

public class FullVertScale extends Scale {
	public static final String scales[] = new String[] {
		"microvolt",
		"numeric",
		"%",
		"balanced micro-volt",
		"balanced numeric",
		"balanced %",
	};

	public static final int MICRO_VOLT = 0;
	public static final int NUMERIC = 1;
	public static final int PERCENT = 2;
	public static final int FLOOR_MICRO_VOLT = 3;
	public static final int FLOOR_NUMERIC = 4;
	public static final int FLOOR_PERCENT = 5;
	public static final int POSITIVE_MICRO_VOLT = 6;
	public static final int POSITIVE_NUMERIC = 7;
	public static final int POSITIVE_PERCENT = 8;
	public static final int NEGATIVE_MICRO_VOLT = 9;
	public static final int NEGATIVE_NUMERIC = 10;
	public static final int NEGATIVE_PERCENT = 11;
	
//	protected int type = MICRO_VOLT;
public FullVertScale(String sc[], Element e) {
	super(sc);

//	update(e);

	//System.out.println("" + ProcessingTools.propertiesToString(this));	
}
public FullVertScale(Element e) {
	this(scales, e);
}
public double calculatePreciseRangeRatio(double value) {
	switch (index) {
		case MICRO_VOLT:
		case POSITIVE_MICRO_VOLT:
		case NEGATIVE_MICRO_VOLT:
			if (sp.isPhysBalanced())
				return 2 * value / sp.getPhysRange();
			else
				return value / sp.getPhysRange();
		case NUMERIC:
		case POSITIVE_NUMERIC:
		case NEGATIVE_NUMERIC:
			return 2 * value / sp.getDigitalRange();
		case PERCENT:
		case POSITIVE_PERCENT:
		case NEGATIVE_PERCENT:
			return value / 100.0;		

		case FLOOR_MICRO_VOLT:
			return value / sp.getPhysRange();
		case FLOOR_NUMERIC:
			return value / sp.getDigitalRange();
		case FLOOR_PERCENT:
			return value / 100.0;
	}		

	return -1;
}
public double calculatePreciseScaleDigitalRatio() {
	switch (index) {
		case MICRO_VOLT:
		case NUMERIC:
		case PERCENT:
		
		case FLOOR_MICRO_VOLT:
		case FLOOR_NUMERIC:
		case FLOOR_PERCENT:
			return 1;
		case POSITIVE_MICRO_VOLT:
		case POSITIVE_NUMERIC:
		case POSITIVE_PERCENT:
		case NEGATIVE_MICRO_VOLT:
		case NEGATIVE_NUMERIC:
		case NEGATIVE_PERCENT:
			return 0.5;
	}		

	return -1;
	
}
public double calculatePreciseScaleMaxValue() {
	switch (index) {
		case MICRO_VOLT:
			return sp.getPhysMax();
		case NUMERIC:
			return sp.getDigitalMax();
		case PERCENT:
			return 100.0;

		//case MICRO_VOLT:
			//return sp.getPhysRange() / 2;
		//case NUMERIC:
			//return sp.getDigitalRange() / 2;
		//case PERCENT:
			//return 100.0;			
				
		case FLOOR_MICRO_VOLT:
			return sp.getPhysRange();
		case FLOOR_NUMERIC:
			return sp.getDigitalRange();
		case FLOOR_PERCENT:
			return 100.0;

		case POSITIVE_MICRO_VOLT:
			return sp.getPhysMax();
		case POSITIVE_NUMERIC:
			return sp.getDigitalMax();
		case POSITIVE_PERCENT:
			return 100.0;
			
		case NEGATIVE_MICRO_VOLT:
			return 0;
		case NEGATIVE_NUMERIC:
			return 0;
		case NEGATIVE_PERCENT:
			return 0;			
	}		

	return -1;
}
public double calculatePreciseScaleMinValue() {
	switch (index) {
		case MICRO_VOLT:
			return sp.getPhysMin();
		case NUMERIC:
			return sp.getDigitalMin();
		case PERCENT:
			return -100.0;

		//case MICRO_VOLT:
			//return -sp.getPhysRange() / 2;
		//case NUMERIC:
			//return -sp.getDigitalRange() / 2;
		//case PERCENT:
			//return -100.0;
				
		case FLOOR_MICRO_VOLT:
			return 0;
		case FLOOR_NUMERIC:
			return 0;
		case FLOOR_PERCENT:
			return 0;
			
		case POSITIVE_MICRO_VOLT:
			return 0;
		case POSITIVE_NUMERIC:
			return 0;
		case POSITIVE_PERCENT:
			return 0;
			
		case NEGATIVE_MICRO_VOLT:
			return sp.getPhysMin();
		case NEGATIVE_NUMERIC:
			return sp.getDigitalMin();
		case NEGATIVE_PERCENT:
			return -100.0;			
	}		

	return -1;
}
/**
 * calculatePreciseScaleStartRatio method comment.
 */
public double calculatePreciseScaleStartRatio(double rangeRatio) {
	switch (index) {
		case FLOOR_MICRO_VOLT:
		case FLOOR_NUMERIC:
		case FLOOR_PERCENT:
			return 0.5 / rangeRatio;
		case POSITIVE_MICRO_VOLT:
		case POSITIVE_NUMERIC:
		case POSITIVE_PERCENT:
			return 0;
		case MICRO_VOLT:
		case NUMERIC:
		case PERCENT:
			return 0.5;
		case NEGATIVE_MICRO_VOLT:
		case NEGATIVE_NUMERIC:
		case NEGATIVE_PERCENT:
			return 1;
	}		

	return -1;
}
public String getUnitStr() {
	switch (index) {
		case FLOOR_MICRO_VOLT:
		case MICRO_VOLT:
		case POSITIVE_MICRO_VOLT:
		case NEGATIVE_MICRO_VOLT:
			return "uV";
		case FLOOR_PERCENT:
		case PERCENT:
		case POSITIVE_PERCENT:
		case NEGATIVE_PERCENT:
			return "%";
	}		

	return "";
}
/**
 * isBalanced method comment.
 */
public boolean isBalanced() {
	switch (index) {
		case MICRO_VOLT:
		case NUMERIC:
		case PERCENT:
			return true;
		default:
			return false;
	}
}
public int toDigit(double value) {
	//System.out.println("index=" + index + " singalRange=" + singalRange + "  microvoltRange=" + microvoltRange + " value=" + value);	
	switch (index) {
		case NUMERIC:
			return (int) (value);
		case MICRO_VOLT:
			return (int)(value * sp.getDigitalRange() / sp.getPhysRange());
		case PERCENT:
			return (int)(value * (sp.getDigitalRange()/2) / 100);
		case FLOOR_NUMERIC:
			return (int) (value - sp.getDigitalMin());
		case FLOOR_MICRO_VOLT:
			return (int) ((value - sp.getPhysMin()) * sp.getDigitalRange() / sp.getPhysRange());
		case FLOOR_PERCENT:
			return (int) (value *sp.getDigitalRange() / 100);
	}		

	return 0;
}

public double calculatePrecisePhysicalRange() {
	switch (index) {
		case MICRO_VOLT:
		case POSITIVE_MICRO_VOLT:
		case NEGATIVE_MICRO_VOLT:
		case FLOOR_MICRO_VOLT:
			return sp.getPhysRange();
		case NUMERIC:
		case POSITIVE_NUMERIC:
		case NEGATIVE_NUMERIC:
		case FLOOR_NUMERIC:
			return sp.getDigitalRange();
		case PERCENT:
		case POSITIVE_PERCENT:
		case NEGATIVE_PERCENT:
		case FLOOR_PERCENT:
			return 200.0;		
	}		

	return -1;
}
}
