/* ModularEEGHeader.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.format.edf;

public class ModularEEGHeader extends EDFHeader {
/**
 * ModularEEGHeader constructor comment.
 * @param noChannels int
 */
public ModularEEGHeader() throws Exception {
	this(6);
}
/**
 * ModularEEGHeader constructor comment.
 * @param noChannels int
 */
public ModularEEGHeader(int noChannels) throws Exception {
	super(noChannels);

	fill(version, "0"); 
	fill(localPatient, "Local patient");
	fill(localRecording, "Local recording");
	fill(startDate, "");
	fill(startTime, "");
	fill(noBytesHeader, "" + getHeaderLength());
	fill(reserved, "");
	fill(noDataRecords, "-1");
	fill(durDataRecord, "" + 1);
	fill(noSignals, "" + NS);

	fill(sLabel, "EEG");
	fill(sType, "Active electrode");
	fill(sPhysDim, "uV");
	fill(sPhysMin, "-256");
	fill(sPhysMax, "+256");
	fill(sDigMin, "0");
	fill(sDigMax, "1023");
	fill(sPrefilter, "");
	fill(sNoSamples, "1");
	fill(sReserved, "");
}
/**
 */
public static void main(String args[]) throws Exception {
	try {
		System.out.println("Main started");
		System.out.println("header: " + new ModularEEGHeader(1).getString());
		System.out.println("Main finished");
	} catch (Throwable e) {
		e.printStackTrace();
	}	
}
}
