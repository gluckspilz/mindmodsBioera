/* VectorStorageWriter.java v 1.0.9   11/6/04 7:15 PM
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
public final class VectorStorageWriter extends bioera.storage.FileStorageWriter {
	public boolean saveDescriptions = false;

	private final static String propertiesDescriptions[][] = {
		{"saveDescriptions", "Save descriptions", ""},
	};

	
	private BufferedVectorPipe in;
	private int vSize, buf[];
	protected static boolean debug = bioera.Debugger.get("impl.file.writer.storage");
/**
 * Element constructor comment.
 */
public VectorStorageWriter() {
	super();
	setName("ArchWriter");
	inputs = new BufferedVectorPipe[getInputsCount()];
	inputs[0] = in = new BufferedVectorPipe(this);
	in.setName("IN");
}
/**
 * Element constructor comment.
 */
public String getElementDescription() throws Exception {
	return "Vector stream is stored in a file. It can be later retrieved with using VectorStorageSource";
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
protected SignalParameters parametersToSave()  throws Exception {
	if (saveDescriptions)
		return getSignalParameters().createCopyOfValues();
	else
		return super.parametersToSave();
}
/**
 * Element constructor comment.
 */
public final void process() throws Exception {
	if (in.isEmpty())
		return;

	int n = in.available();
		
	//System.out.println("Found " + n + " bytes for write to file " + file);	
	if (timeMarkerMillis > 0 && mainProcessingTime - lastTime > timeMarkerMillis) {
		out.writeComment("+" + Long.toString(mainTimeFromStart));
		lastTime = mainProcessingTime;
	}
	for (int i = 0; i < n; i++){
		buf = in.getVBuffer()[i];
		for (int j = 0; j < vSize; j++){
			out.write2(buf[j]);
		}
	}
	in.purgeAll();		
}
/**
 * Element constructor comment.
 */
public final void reinit() throws Exception {
	if (getSignalParameters().getSignalResolutionBits() > 16 || getSignalParameters().getDigitalRange() > (1<<16))
		throw new DesignException("Only vectors up to 16bits can be saved");
	
	vSize = getSignalParameters().getVectorLength();
	super.reinit();
}
}
