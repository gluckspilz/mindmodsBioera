/* Connection.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.processing;

public abstract class Connection implements Propertable {
	public int srcId;
	public int destId;

	public static final int ELEMENT_CONNECTION = 1;
/**
 * Connection constructor comment.
 */
public Connection() {
	super();
}
/**
 * getPropertyDescription method comment.
 */
public java.lang.Object[] getPropertyDescription(java.lang.String name) {
	return null;
}
/**
 * getPropertyNames method comment.
 */
public java.lang.String[] getPropertyNames() {
	return null;
}
/**
 * sendChangePropertyEvent method comment.
 */
public void sendChangePropertyEvent(java.lang.String fieldName, java.lang.Object oldValue) {}
/**
 * Connection constructor comment.
 */
public abstract int type();
}
