/* PField.java v 1.0.9   11/6/04 7:15 PM
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

import java.io.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import bioera.processing.*;
import bioera.*;
import bioera.layouts.*;
import bioera.graph.designer.*;

/**
 * Creation date: (5/27/2004 1:41:12 PM)
 * @author: Jarek Foltynski
 */
public class PField {
	// Label describing a property
	JLabel label;

	// Graphic component (JComponent) or PFields (for a set of components)
	Object graphComponent;

	// Contains Single property description (ELementProperty) or PFields
	ElementProperty property;
/**
 * PField constructor comment.
 */
public PField() {
	super();
}
/**
 * PField constructor comment.
 */
public PField(JLabel l) {
	super();
	label = l;
}
/**
 * PField constructor comment.
 */
public PField(JLabel l, Object c) {
	super();
	label = l;
	graphComponent = c;	
}
}
