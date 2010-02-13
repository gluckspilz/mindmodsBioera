/* PNPendantProgrammer.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.tools.impl;

import java.io.*;
import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.properties.*;
import bioera.storage.*;
import bioera.*;
import bioera.config.*;
import bioera.tools.pn.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class PNPendantProgrammer extends Programmer {
	public BrowseFile firmwareFile = new BrowseFile();

	final static String propertiesDescriptions[][] = {
		{"firmwareFile", "File path", ""},
	};
	
	static boolean debug = bioera.Debugger.get("tools.pn.pendant.Programmer");
/**
 * Element constructor comment.
 */
public PNPendantProgrammer() {
	super();
	setName("PendantProgrammer");
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Pendant programmer";
}
/**
 * Element constructor comment.
 */
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
/**
 * Element constructor comment.
 */
public java.io.Reader getReader() throws java.lang.Exception {
	return new FileReader(firmwareFile.getAbsoluteFile());
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	File file = firmwareFile.getAbsoluteFile();
	if (!file.exists())
		throw new DesignException("File not found '" + file + "'");
	super.reinit();
}
}
