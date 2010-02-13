/* Pipe.java v 1.0.9   11/6/04 7:15 PM
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

import java.lang.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public abstract class Pipe {
	public String name = "default";
	Element element;
	public int id;

	private static int idSeq = 1;
public Pipe(Element e) {
	element = e;
	id = idSeq++;
}
public boolean compatibleWith(int type) {
	switch (getType()) {
		case InterfacePipe.TYPE:
			return true;
		case ScalarPipe.TYPE:
		case LogicalPipe.TYPE:
			return type == LogicalPipe.TYPE 
				|| type == ScalarPipe.TYPE
				|| type == InterfacePipe.TYPE;
		case VectorPipe.TYPE:
		case ComplexPipe.TYPE:
			return type == VectorPipe.TYPE 
				|| type == ComplexPipe.TYPE
				|| type == InterfacePipe.TYPE;
		default:
			return false;
	}
}
public boolean compatibleWith(Pipe p) {
	return compatibleWith(p.getType());
}
public Element getElement() {
	return element;
}
public int getId() {
	return id;
}
public java.lang.String getName() {
	return name;
}
public abstract SignalParameters getSignalParameters();
/**
 * getType method comment.
 */
public int getType() {
	return 0;
}
public void setName(java.lang.String newName) {
	name = newName;
}
public String toString() {
	return "'" + name + "' (" + id + ")";
}
}
