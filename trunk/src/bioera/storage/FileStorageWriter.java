/* FileStorageWriter.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.storage;

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
public abstract class FileStorageWriter extends SingleElement {
	public BrowseFile filePath = new BrowseFile("archives/unknown");

	public boolean appendExisting;
	public boolean makeUniqueNames = true;
	public int timeMarkerMillis = 1000;
	public String savedFile = "";

	public static final String EXT = ".bpa";

	protected final static String propertiesDescriptions[][] = {
		{"timeMarkerMillis", "Time marker period [ms]", ""},
		{"savedFile", "Saved file", "", "false"},		

	};
	
	protected File file;
	protected StorageOutputStream out;

	protected long lastTime = 0;
	protected int initialLength = 0;
/**
 * Element constructor comment.
 */
public FileStorageWriter() {
	super();
	setName("ArchWriter");
}
/**
 * Element constructor comment.
 */
public void close() throws Exception {
	if (out != null) {
		out.close();
		out = null;
	}

	if (file.length() == initialLength)
		file.delete();
}
/**
 * Element constructor comment.
 */
protected void fileinit()  throws Exception {
	if (out != null) {
		return;
	}

	file = filePath.getAbsoluteFile();
	String path = file.getAbsolutePath();
	SignalParameters params = parametersToSave();
	if (file.exists() && appendExisting) {
		// Check if the format of the existing file is the same
		StorageInputStream sIn = new StorageInputStream(new FileInputStream(file));
		SignalParameters params1 = new SignalParameters(this);
		new StorageFormat().initInput(sIn, params1);
		sIn.close();
		if (!params1.equals(params)) {
			throw new DesignException("File format not compatible");
		}
		out = new StorageOutputStream(new BufferedOutputStream(new FileOutputStream(file.getAbsolutePath(), appendExisting)));
	} else {
		if (makeUniqueNames) {
			if (path.toLowerCase().endsWith(EXT))
				file = Tools.createUniqueFilePath(path.substring(0, path.length() - EXT.length()), EXT);
			else
				file = Tools.createUniqueFilePath(path, EXT);
		}
		
		out = new StorageOutputStream(new BufferedOutputStream(new FileOutputStream(file.getAbsolutePath(), appendExisting)));

		//System.out.println("params: " + params);	
		
		new StorageFormat().initOutput(out, params, inputs[0]);
	} 

	out.writeComment("DATA " + Tools.dateTimeFormatter().format(new java.util.Date()));
	out.flush();

	initialLength = (int) file.length();

	savedFile = file.getName();
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
protected SignalParameters parametersToSave()  throws Exception {
	SignalParameters ret = getSignalParameters().createCopyOfValues();
	ret.setVectorDescriptions(new String[0]);
	return ret;
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		// Nothing is connected to the input so no initiation is required
		reinited = true;
		disactivate("Not connected");
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}
	
	verifyDesignState(filePath != null);
	
	super.reinit();
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
	fileinit();
}
/**
 * Element constructor comment.
 */
public void stop()  throws Exception {
	close();
}
}
