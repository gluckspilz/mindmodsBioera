/* WavFileWriter.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.*;
import bioera.sound.*;
import bioera.properties.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class WavFileWriter extends SingleElement {
	public BrowseFile filePath = new BrowseFile("archives/a.wav");
	//public boolean appendExisting;

	private final static String propertiesDescriptions[][] = {
//		{"", "", ""},
	};	

	
	private WavOutputStream out;
	private BufferedScalarPipe in;
	private int buffer[];
	private int counter;

	protected static boolean debug = bioera.Debugger.get("impl.wav.writer");
/**
 * Element constructor comment.
 */
public WavFileWriter() {
	super();
	setName("WAV");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	buffer = in.getBuffer();
}
/**
 * Element constructor comment.
 */
public void close() throws Exception {
	if (out != null) {
		out.close();
		out = null;
	}		
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Writes to file in WAV sound format";
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 0;
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
public final void process() throws Exception {
	int n = in.available();
	if (n > 0) {
		//System.out.println("Found " + n + " bytes for write to file " + file);
		//out.write(buffer, 0, n);
		for (int i = 0; i < n; i++){
			out.write2(buffer[i]);
		}
		counter += n;
		in.purgeAll();		
	}		
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	verifyDesignState(!TOOLS.isNone(filePath.path));

	//in.setBufferSize(10);
	//buffer = in.getBuffer();
	super.reinit();
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2003 1:22:12 PM)
 */
public void start() throws Exception {
	Element element = getFirstElementConnectedToInput(0);
	if (element == null)
		return;	
	out = new WavOutputStream(new FileOutputStream(filePath.getAbsoluteFile()), (int) element.getSignalParameters().getSignalRate());	
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2003 1:22:12 PM)
 */
public void stop() throws Exception {
	close();
}
}
