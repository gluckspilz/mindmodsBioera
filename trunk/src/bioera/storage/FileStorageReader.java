/* FileStorageReader.java v 1.0.9   11/6/04 7:15 PM
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
import java.util.*;
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
public abstract class FileStorageReader extends SourceElement implements TimeRangeFeature {
	public BrowseFile filePath = new BrowseFile("archives/unknown");
	public boolean keepOriginalRate;
	public boolean keepOriginalTime;
	public boolean readInLoop = false;
	public boolean fillGaps = false;
	public int readChunk;
	public String loadedFile = "none";
	public double startTime = 0;
	public double endTime = 0;
	public int cutRatio = 3;

	private final static String propertiesDescriptions[][] = {
		{"filePath", "Archive file path", ""},		
		{"loadedFile", "Loaded file", "", "false"},
		{"startTime", "Start time [s]", ""},
		{"endTime", "End time [s]", ""},
		{"readChunk", "Read chunk", ""},
		{"readInLoop", "Read in loop", ""},
		{"keepOriginalTime", "Keep original time", ""},
		{"keepOriginalRate", "Keep original rate", ""},
		{"cutRatio", "Cut ratio [%]", ""},
		
	};	
	
	protected File file;
	protected StorageInputStream in;
	protected long counter;
	protected int originalRate;
	protected int chunkTimeIncrement;
	protected int endTimeIndex;
	protected int endTimeMarker;
	protected boolean exhausted;
	protected int lastTime = 0;
	protected Date startDate;
/**
 * Element constructor comment.
 */
public FileStorageReader() {
	super();
	setName("ArchReader");
}
/**
 * Element constructor comment.
 */
public void close() throws Exception {
	if (in != null) {
		in.close();
		in = null;
	}	
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 1;
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
public int parseTime(String comm) throws Exception {
	int len = comm.length();
	if (len > 0 && comm.charAt(0) == '+') {
		// Read original time
		int i = 1;
		while (i < len && Character.isDigit(comm.charAt(i)))
			i++;
		i = Integer.parseInt(comm.substring(1, i));
		return i;
	}

	return -1;
}
/**
 * Insert the method's description here.
 * Creation date: (3/23/2004 3:05:11 PM)
 */
public double physicalEnd() {
	return endTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/23/2004 3:05:11 PM)
 */
public double physicalStart() {
	return startTime;
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	//System.out.println("reiniting file storage");	
	file = filePath.getAbsoluteFile();
	String path = file.getAbsolutePath();
	if (debug)
		System.out.println("Archive file length=" + file.length());
	close();

	getSignalParameters().inheritValues(getSignalParameters());
	if (path.endsWith(FileStorageWriter.EXT) ){
		String base = path.substring(0, path.length() - FileStorageWriter.EXT.length());
		//if (base.length() > 4 && base.charAt(base.length() - 4) == '_') {
			//base = base.substring(0, base.length() - 4);
		//}
		file = Tools.searchMostRecentFilePath(base, FileStorageWriter.EXT);
	} else {
		file = Tools.searchMostRecentFilePath(path, "");
	}

	if (debug)
		System.out.println("Found archive file " + file);
	
	if (file == null) {	
		disactivate("File not found");
		throw new DesignException("File '" + filePath + "' not found");
	}

	loadedFile = file.getName();

	// Check if the format of the existing file is the same
	in = new StorageInputStream(new BufferedInputStream(new FileInputStream(file)));
	StorageFormat format = new StorageFormat();
	startDate = format.initInput(in, getSignalParameters());

	if (format.version < 3) {
		// Handle here older formats
		in = new StorageInputStream_OldVersion(new BufferedInputStream(new FileInputStream(file)));
		startDate = format.initInput(in, getSignalParameters());
		((StorageInputStream_OldVersion)in).zeroLevel = getSignalParameters().getDigitalRange() / 2;
		//int digimax = getSignalParameters().getDigitalRange() / 2;
		//getSignalParameters().setDigitalMax(digimax);
		//getSignalParameters().setDigitalMin(-digimax);		
	}
	
	if (startDate == null) {
		throw new DesignException("Could not properly initialize input storage '" + file + "'");
	}
	originalRate = Math.round(getSignalParameters().getSignalRate());
	chunkTimeIncrement = 1000 * readChunk / originalRate;

	counter = 0;
	
	if (startTime != 0) {
		// Calculate number of samples by rate
		int c = (int) Math.round(startTime * getSignalParameters().getSignalRate()), ch;
		String comm; int time;
		if (debug)
			System.out.println("Set start index=" + c);
		
		// Search either by rate or time marker
		while (--c > 0) {
			if (in.read2() == 0x80000000) {
				if ((comm=in.readComment()) == null) {
					// This is end-of-file
					throw new DesignException("Start time '" + startTime + "' not found");
				} else {
					//if (parseTime(comm) != -1) {
						//if (parseTime(comm) / 1000 >= startTime) {
							//// Found the time marker that is greater then start time
							//if (debug)
								//System.out.println("Start time marker=" + parseTime(comm));

							//break;
						//}
					//}
					continue;
				}
			}
			counter++;
		}	
	}

	if (debug)
		System.out.println("FileSourceSource counter=" + counter);

	if (endTime != 0) {
		endTimeIndex = (int) Math.round(endTime * getSignalParameters().getSignalRate());
		if (cutRatio > 0)
			endTimeIndex -= ((endTimeIndex-counter) * cutRatio / 100); // stop efore end
		endTimeMarker = (int)(endTime * 1000);
		if (debug) {
			System.out.println("Set end: index=" + endTimeIndex + ", time marker=" + endTimeMarker);
		}
	} else {
		endTimeIndex = Integer.MAX_VALUE;
		endTimeMarker = Integer.MAX_VALUE;		
	}

	if (keepOriginalTime) {
		mainStartTime = mainProcessingTime = startDate.getTime();
		mainTimeFromStart = 0;
	}

	getSignalParameters().setInfo("Source: Archive file " + file.getName());
	
	if (debug)
		System.out.println("counter=" + counter +  " endT=" +endTimeIndex + " rate=" + getSignalParameters().getSignalRate() + "  endTime=" + endTime);

	//System.out.println("arch0=" + getSignalParameters());
	//System.out.println("arch of elem " + getId() + " " + getSignalParameters().getId());		
	super.reinit();
}
/**
 * Element constructor comment.
 */
public final void setExhausted() throws Exception {
	exhausted = true;
	setStatusChar('E', 1, 10);

	if (debug) {
		System.out.println("Total samples: " + counter);
		System.out.println("Started at: " + startDate);	
		if (lastTime > 0)
			System.out.println("Last recorded marker: " + lastTime / 1000 + "[s] (" + lastTime / 60000 + "[min])");
	}
}
/**
 * Element constructor comment.
 */
public void start() throws Exception {
	reinit();

	//System.out.println("arch1=" + getSignalParameters());	
}
/**
 * Element constructor comment.
 */
public void stop() throws Exception {
	if (exhausted) {
		//activate();
		exhausted = false;
		setStatusChar(' ', 1, 10);
	}
}
}
