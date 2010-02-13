/* ToolDesigns.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.graph.designer;

import java.util.*;
import java.io.*;

/**
 * Insert the type's description here.
 * Creation date: (6/3/2004 6:04:31 PM)
 * @author: Jarek
 */
public class ToolDesigns {
	public ArrayList names = new ArrayList();
	public ArrayList files = new ArrayList();
/**
 * ToolDesigns constructor comment.
 */
public ToolDesigns() {
	super();
}
/**
 * ToolDesigns constructor comment.
 */
public void add(String name, File f) {
	names.add(name);
	files.add(f);
}
/**
 * ToolDesigns constructor comment.
 */
public void clear() {
	names.clear();
	files.clear();
}
/**
 * ToolDesigns constructor comment.
 */
public File get(String name) {
	int i = names.indexOf(name);
	if (i == -1)
		return null;
	return (File) files.get(i);
}
}
