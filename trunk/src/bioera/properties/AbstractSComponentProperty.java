/* AbstractSComponentProperty.java v 1.0.9   11/6/04 7:15 PM
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

public abstract class AbstractSComponentProperty extends AbstractStructuredProperty implements SComponent {
	PField pField;
/**
 * PFieldStructuredProperty constructor comment.
 */
public AbstractSComponentProperty() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 4:21:00 PM)
 */
public void setPField(PField pf) {
	pField = pf;
}

/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 4:21:00 PM)
 */
public PField getPField() {
	return pField;
}

/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 4:21:00 PM)
 */
public java.lang.String[] getPropertyNames() {
	return null;
}
}
