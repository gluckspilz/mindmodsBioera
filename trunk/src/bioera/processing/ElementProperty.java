/* ElementProperty.java v 1.0.9   11/6/04 7:15 PM
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
public class ElementProperty implements Comparable {
	public String name;
	public Object value;
	public Object description;
	public boolean active = true;
public ElementProperty() {
	super();
}
public int compareTo(java.lang.Object o) {
	try {
		ElementProperty p = (ElementProperty) o;
		Comparable c = null, c1 = null;
		if (description != null && description instanceof String) {
			String s = (String) description;
			if (s.length() > 0)
				c = s.toLowerCase();
		}
		if (c == null)
			c = name.toLowerCase();

		if (p.description != null && p.description instanceof String) {
			String s = (String) p.description;
			if (s.length() > 0)
				c1 = s.toLowerCase();
		}
		if (c1 == null)
			c1 = p.name.toLowerCase();		

		//System.out.println("comp " + c + " " + c1);	
		return c.compareTo(c1);
	} catch (Exception e) {
	}

	return 0;
}

public String toString() {
	return "[element property, name=" + name + "]";
}

public ElementProperty(String n, Object v) {
	super();
	this.name = n;
	value = v;
}
}
