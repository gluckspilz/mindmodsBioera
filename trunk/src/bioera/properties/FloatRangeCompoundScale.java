/* FloatRangeCompoundScale.java v 1.0.9   11/6/04 7:15 PM
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

public class FloatRangeCompoundScale extends LeftCompoundProperty {
	public FloatRange value;
	public Scale scale;
/**
 * CustomCompoundProperty constructor comment.
 */
public FloatRangeCompoundScale() {
	super();
}
/**
 * CustomCompoundProperty constructor comment.
 * @param ip1 java.lang.Object
 * @param ip2 java.lang.Object
 */
public FloatRangeCompoundScale(FloatRange r, Scale s) {
	value = r;
	scale = s;
}
/**
 * CompoundProperty constructor comment.
 */
public java.awt.Component getC1() {
	return value.getComponent();
}
/**
 * CompoundProperty constructor comment.
 */
public java.awt.Component getC2() {
	return scale.getComponent();
}
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:19:05 PM)
 */
public void save() {
	value.save();
	scale.save();
}
}
