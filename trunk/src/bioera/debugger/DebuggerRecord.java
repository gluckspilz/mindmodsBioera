/* DebuggerRecord.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.debugger;

public class DebuggerRecord implements Comparable {
	public String className;
	public String field;
	public String property;
	public boolean isSet;
/**
 * DebuggerRecord constructor comment.
 */
public DebuggerRecord() {
	super();
}
public int compareTo(java.lang.Object o) {
	try {
		DebuggerRecord r = (DebuggerRecord) o;		
		int ret = className.compareTo(r.className);
		if (ret == 0)
			ret = className.compareTo(r.className);
		return ret;
	} catch (Exception e) {		
	}

	return 0;
}
/**
 * DebuggerRecord constructor comment.
 */
public boolean equals(Object o) {
	try {
		DebuggerRecord r = (DebuggerRecord) o;		
		return className.equals(r.className) 
		&& property.equals(r.property)
		&& field.equals(r.field)
		&& isSet == r.isSet;
	} catch (Exception e) {		
	}
	return false;
}
}
