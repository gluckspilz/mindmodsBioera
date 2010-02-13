/* LastDesigns.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.config;

import java.util.*;
import java.io.*;


/**
 * Insert the type's description here.
 * Creation date: (5/30/2004 1:01:35 PM)
 * @author: Jarek
 */
public class LastDesigns implements bioera.processing.Propertable, Configurable {
	private static final int rememberNo = 10;
	public List list = new ArrayList();
/**
 * DesignInfo constructor comment.
 */
public LastDesigns() {
	super();
}
/**
 * DesignInfo constructor comment.
 */
public void add(File d) {
	if (d == null)
		return;
	//System.out.println("adding " + d);
	//System.out.println("adding " + d.getClass());	
	if (getLast() != null && getLast().equals(d))
		return;
	for (int i = 0; i < list.size(); i++){
		if (d.equals(list.get(i))) {
			list.remove(i);
			break;
		}
	}
	if (list.size() >= rememberNo) {
		list.remove(0);
		//System.out.println("removed above " + rememberNo);
	}
	list.add(d);
}
/**
 * DesignInfo constructor comment.
 */
public File getLast() {
	if (list.size() > 0)
		return (File) list.get(list.size() - 1);
	else
		return null;
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
 */
public final boolean load(XmlConfigSection config) throws Exception {
	try {
		list.clear();
		config = config.getSection("list");
		List l = (List) Config.importXMLProperties(null, config, false);
		for (int i = 0; i < l.size(); i++){
			File f = (File) l.get(i);
			if (f != null && f.exists())
				list.add(f);
		}
	} catch (Exception e) {
	}

	return true;
}
/**
 */
public final boolean save(XmlCreator config) throws Exception {
	return false;
}
/**
 * sendChangePropertyEvent method comment.
 */
public void sendChangePropertyEvent(java.lang.String fieldName) {}
/**
 * sendChangePropertyEvent method comment.
 */
public void sendChangePropertyEvent(String fieldName, Object oldValue) {
	
}
}
