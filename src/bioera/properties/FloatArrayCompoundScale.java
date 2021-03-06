/* FloatArrayCompoundScale.java v 1.0.9   11/6/04 7:15 PM
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

public class FloatArrayCompoundScale extends LeftCompoundProperty{
	public double value[];
	public Scale scale;
/**
 * IntegerVertScale constructor comment.
 */
public FloatArrayCompoundScale(int v[], Scale s) {
	value = new double[v.length];
	for (int i = 0; i < v.length; i++){
		value[i] = v[i];
	}
	scale = s;
}
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:11:50 PM)
 */
public java.awt.Component getC1() throws java.lang.Exception {
	return tf1 = new javax.swing.JTextField(bioera.processing.ProcessingTools.arrayToString(value));
}
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:11:50 PM)
 */
public java.awt.Component getC2() throws java.lang.Exception {
	return scale.getComponent();
}
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 2:56:36 PM)
 */
public void save() {
	value = bioera.processing.ProcessingTools.stringToDoubleArray(tf1.getText());
	scale.save();
}
}
