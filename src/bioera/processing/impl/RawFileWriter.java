/* RawFileWriter.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.processing.impl;

import java.io.*;
import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.storage.*;
import bioera.*;
import bioera.properties.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class RawFileWriter extends AbstractStreamElement {
	public BrowseFile filePath = new BrowseFile("archives/unknown");
	
	public boolean appendExisting;

	private final static String propertiesDescriptions[][] = {
	};	
	
	protected static boolean debug = bioera.Debugger.get("impl.rawfile.writer");
/**
 * Element constructor comment.
 */
public RawFileWriter() {
	super();
	setName("RawWriter");
}
/**
 * Element constructor comment.
 */
public void close()  throws Exception {
	if (streamOut != null) {
		streamOut.close();
		streamOut = null;
	}
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Writes bytes directly to file without any format conversion";
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 0;
}
/**
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
public void reinit()  throws Exception {
	verifyDesignState(!TOOLS.isNone(filePath.path));

	//System.out.println("fp=" + filePath.path);
	
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		throw new NotConnectedException();		
	}
	
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
	close();
	streamOut = new BufferedOutputStream(new FileOutputStream(filePath.getAbsoluteFile().getAbsolutePath(), appendExisting));
}
/**
 * Element constructor comment.
 */
public void stop()  throws Exception {
	close();
}
}
