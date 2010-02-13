/* FloatCompoundUnitScale.java v 1.0.9   11/6/04 7:15 PM
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

public class FloatCompoundUnitScale extends LeftCompoundProperty {
	public FloatCompoundScale scaledValue;
	public Scale unit;
/**
 * FloatCompundUnitScale constructor comment.
 * @param v int
 * @param s bioera.properties.Scale
 */
public FloatCompoundUnitScale(FloatCompoundScale v, Scale u) {
	scaledValue = v;
	unit = u;
}
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:19:47 PM)
 */
public java.awt.Component getC1() throws java.lang.Exception {
	return scaledValue.getComponent();
}
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:19:47 PM)
 */
public java.awt.Component getC2() throws java.lang.Exception {
	return unit.getComponent();
}
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:19:47 PM)
 */
public void save() {
	scaledValue.save();
	unit.save();
}

/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:19:47 PM)
 */
public double getChartDigiRange() {
	double yRangeRatio = scaledValue.scale.calculatePreciseRangeRatio(scaledValue.value);
	return yRangeRatio * unit.calculatePreciseScaleDigitalRatio() * unit.sp.getDigitalRange();
}

/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:19:47 PM)
 */
public double getChartRangeRatio() {
	double yRangeRatio = scaledValue.scale.calculatePreciseRangeRatio(scaledValue.value);
	return unit.calculatePreciseScaleStartRatio(yRangeRatio);
}

/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:19:47 PM)
 */
public double getScaleMax() {
	double rangeRatio = scaledValue.scale.calculatePreciseRangeRatio(scaledValue.value);
	return rangeRatio * unit.calculatePreciseScaleMaxValue();
}

/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:19:47 PM)
 */
public double getScaleMin() {
	double rangeRatio = scaledValue.scale.calculatePreciseRangeRatio(scaledValue.value);
	return rangeRatio * unit.calculatePreciseScaleMinValue();	
}

/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:19:47 PM)
 */
public void update(bioera.processing.Element e) {
	scaledValue.scale.update(e);
	unit.update(e);	
}
}
