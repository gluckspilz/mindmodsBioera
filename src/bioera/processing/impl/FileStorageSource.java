/* FileStorageSource.java v 1.0.9   11/6/04 7:15 PM
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
public final class FileStorageSource extends FileStorageReader {
	protected int zeroLevel;
	protected ScalarPipeDistributor out;
	protected static boolean debug = bioera.Debugger.get("impl.file.reader.storage");
/**
 * Element constructor comment.
 */
public FileStorageSource() {
	super();

	setName("ArchReader");
	out = (ScalarPipeDistributor) outputs[0];
	out.setName("OUT");
}
/**
 * Element constructor comment.
 */
public String getElementDescription() throws Exception {
	return
	"Linear stream can be then retrieved from a file written before with using FileStorageWriter";
}
/**
 * Element constructor comment.
 */
public final static void main(String s[]) throws Exception {
	StorageInputStream in = new StorageInputStream(new FileInputStream("c:\\projects\\eeg\\fsss"));
	SignalParameters p = new SignalParameters(null);
	//StorageFormat.debug = true;
	java.util.Date ok = new StorageFormat().initInput(in, p);

	if (ok == null)
		System.out.println("Initialization failed");
	int c = 0, ch;
	while ((ch = in.read2()) != -1) {
		c++;
		System.out.print((char) ch);
	}

	System.out.println("c=" + c + "\n\n" + ProcessingTools.propertiesToString(p));
}
/**
 * Element constructor comment.
 */
public final void process() throws Exception {
	if (exhausted)
		return;
	
	if (counter >= endTimeIndex) {
		if (debug)
			System.out.println("Archive: counter > endIndex " + counter + " > " + endTimeIndex);
		setExhausted();
		return;
	}

	if (fillGaps && lastTime > 0) {
		// Counter should reflect the time marker,
		// If it is lower, then fill remaining data with empty values
		//System.out.println("counter x 1000=" + (counter * 1000));
		//System.out.println("lastTime x origRate=" + (lastTime * originalRate));
		if (counter / originalRate < lastTime / 1000) {
			// Add one more second to avoid occassional gaps due to precision rounding
			int n = Math.min(out.minAvailableSpace() / 2, originalRate + (int)((lastTime / 1000) - (counter /originalRate)));
			n = Math.min(out.minBufferSize() / 3, n);
			for (int i = 0; i < n; i++){
				out.write(0);
			}
			counter += n;
			//System.out.println("Storage: added " + n + " gap at " + (lastTime / 1000) + " counter=" + counter);
			return;
		}
		
	}					

	
	int ch = 0;
	if (keepOriginalRate) {
		int toSend = (int) (mainProcessingTime - mainStartTime + startTime * 1000) * originalRate / 1000;
		while (counter < toSend && counter < endTimeIndex && (ch = in.read2()) != 0x80000000) {
			out.write(ch - zeroLevel);
			counter++;
		}
	} else {
		int n;
		if (readChunk > 0)
			n = readChunk;
		else
			n = Math.min(out.minBufferSize() / 3, out.minAvailableSpace());

		if (counter + n > endTimeIndex) {
			n = (int)(endTimeIndex - counter);
		}

		for (int i = 0; i < n; i++){
			ch = in.read2();
			if (ch == 0x80000000) {
				if (keepOriginalTime)
					mainProcessingTime = mainStartTime + 1000 * counter / originalRate;
				break;
			}
			out.write(ch - zeroLevel);
			counter++;
		}
		//System.out.println("sent " + counter);		
	}
	
	if (ch == 0x80000000) {
		String comm;
		if ((comm = in.readComment()) == null) {
			if (readInLoop) {
				reinit();
				if (debug)
					System.out.println("Storage source reinited " + counter);
			} else {
				setExhausted();	
				if (debug)
					System.out.println("Storage source exhausted (comment=null) at" + counter);
			}
		} else {
			if (debug)
				System.out.println("comment=" + comm);
			if (keepOriginalTime) {
				lastTime = parseTime(comm);
				//System.out.println("last=" + lastTime);
				if (lastTime != -1) {
					mainProcessingTime = mainStartTime + lastTime;
					//if (lastTime >= endTimeMarker) {
						//counter = endTimeIndex;
					//}
				}
			}			
		}
	}
	//System.out.println("processed " + counter);	
}
/**
 * Element constructor comment.
 */
public final void reinit() throws Exception {
	super.reinit();

	//System.out.println("arch2=" + getSignalParameters());

	zeroLevel = 0;//getSignalParameters().getDigitalZero();
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
