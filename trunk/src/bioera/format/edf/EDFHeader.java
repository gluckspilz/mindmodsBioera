/* EDFHeader.java v 1.0.9   11/6/04 7:15 PM
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

import java.lang.reflect.*;

/**
 * Creation date: (3/15/2004 12:47:40 PM)
 * @author: Jarek Foltynski
 */
public class EDFHeader {
	public int version[] = new int[8];			// version of this data format (0) 
	public int localPatient[] = new int[80];	// local patient identification 
	public int localRecording[] = new int[80];	// local recording identification 
	public int startDate[] = new int[8];		// startdate of recording (dd.mm.yy)
	public int startTime[] = new int[8];		// starttime of recording  (hh.mm.ss)
	public int noBytesHeader[] = new int[8];	// number of bytes in header record
	public int reserved[] = new int[44];		// reserved
	public int noDataRecords[] = new int[8];	// number of data records (-1 if unknown)
	public int durDataRecord[] = new int[8]; 	// duration of a data record, in seconds
	public int noSignals[] = new int[4]; 		// number of signals (ns) in data record 

	int NS = 1;

	public int sLabel[][] = new int[NS][16];	// ns * 16 ascii : ns * label (e.g. EEG FpzCz or Body temp) 
	public int sType[][] = new int[NS][80];		// ns * 80 ascii : ns * transducer type (e.g. AgAgCl electrode) 
	public int sPhysDim[][] = new int[NS][8];	// ns * 8 ascii : ns * physical dimension (e.g. uV or degreeC) 
	public int sPhysMin[][] = new int[NS][8];	// ns * 8 ascii : ns * physical minimum (e.g. -500 or 34) 
	public int sPhysMax[][] = new int[NS][8];	// ns * 8 ascii : ns * physical maximum (e.g. 500 or 40) 
	public int sDigMin[][] = new int[NS][8];	// ns * 8 ascii : ns * digital minimum (e.g. -2048) 
	public int sDigMax[][] = new int[NS][8];	// ns * 8 ascii : ns * digital maximum (e.g. 2047) 
	public int sPrefilter[][] = new int[NS][80];// ns * 80 ascii : ns * prefiltering (e.g. HP:0.1Hz LP:75Hz)
	public int sNoSamples[][] = new int[NS][8]; // ns * 8 ascii : ns * nr of samples in each data record 
	public int sReserved[][] = new int[NS][32]; // ns * 32 ascii : ns * reserved			
/**
 * EDFHeader constructor comment.
 */
public EDFHeader(int noChannels) {
	super();

	NS = noChannels;

	if (NS < 0)
		throw new RuntimeException("EDF header must have at least 1 channel");

	sLabel = new int[NS][16];
	sType = new int[NS][80];
	sPhysDim = new int[NS][8];
	sPhysMin = new int[NS][8];
	sPhysMax = new int[NS][8];
	sDigMin = new int[NS][8];
	sDigMax = new int[NS][8];
	sPrefilter = new int[NS][80];
	sNoSamples = new int[NS][8];
	sReserved = new int[NS][32];
}
/**
 * EDFHeader constructor comment.
 */
public void fill(int field[][], String value) {
	if (value.length() > field[0].length) {
		value = value.substring(0, field[0].length);
	}

	for (int i = 0; i < value.length(); i++){
		for (int j = 0; j < field.length; j++){
			field[j][i] = value.charAt(i);
		}
		
	}

	for (int i = value.length(); i < field[0].length; i++){
		for (int j = 0; j < field.length; j++){
			field[j][i] = ' ';
		}
	}
}
/**
 * EDFHeader constructor comment.
 */
public void fill(int field[], String value) {
	if (value.length() > field.length) {
		value = value.substring(0, field.length);
	}

	for (int i = 0; i < value.length(); i++){
		field[i] = value.charAt(i);
	}

	for (int i = value.length(); i < field.length; i++){
		field[i] = ' ';
	}	
}
/**
 * EDFHeader constructor comment.
 */
public int getHeaderLength() throws Exception {
	Field f[] = getClass().getFields();
	int total = 0;
	for (int i = 0; i < f.length; i++){
		Object o = f[i].get(this);
		if (o instanceof int[]) {
			total += ((int[])o).length;
		}

		if (o instanceof int[][]) {
			int b[][] = (int[][]) o;
			total += b.length * b[0].length;
		}		
	}

	return total;
}
/**
 * EDFHeader constructor comment.
 */
public String getString() throws Exception {
	StringBuffer sb = new StringBuffer();
	Field f[] = getClass().getFields();
	for (int i = 0; i < f.length; i++){
		Object o = f[i].get(this);
		// System.out.println("name " + f[i].getName() + " " + o.getClass());		
		if (o instanceof int[]) {
			int b[] = (int[]) o;
			for (int n = 0; n < b.length; n++){
				sb.append((char) b[n]);
			}
		}

		if (o instanceof int[][]) {
			int b[][] = (int[][]) o;
			for (int n = 0; n < b.length; n++){
				for (int p = 0; p < b[n].length; p++){
					sb.append((char) b[n][p]);
				}
			}
		}
	}

	return sb.toString();
}
}
