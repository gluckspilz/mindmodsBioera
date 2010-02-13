/* UnitConcatenation.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.sound;

import java.lang.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class UnitConcatenation {

	// Each sample must be in file 'number'.wav
	// This table here is only to allow verification (test) if all files exists in folder
	public final static int numbers[] = {
		0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 
		11, 12, 13, 14, 15, 16, 17, 18, 19, 
		20, 30, 40, 50, 60, 70, 80, 90, 
		100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};

	public final static String MINUS = "minus";
	
	public final static String SECONDS = "seconds";
	public final static String MINUTES = "minutes";
	public final static String HOURS = "hours";

	public final static String HUNDRED = "100";
	public final static String THOUSAND = "1000";
	public final static String MILION = "1000000";	
	
public UnitConcatenation() {
	super();
}
private String [] append(String t[], String t1[]) {
	int i = 0, j = 0;

	// Find end in first table
	while (i < t.length && t[i] != null)
		i++;

	if (i > 0 && i == t.length) {
		for (i = 0; i < t.length; i++){
			System.out.println("" + i + "=" + t[i]);
		}		
		throw new RuntimeException("Length exceeded in UnitConcatenation2");
	}
		
	// Copy all elements
	while (i < t.length && j < t1.length && t1[j] != null) {
		t[i++] = t[j++];
	}
	
	return t;
}
private String [] append(String t[], String value) {
	int i = 0;

	// Find end in first table
	while (i < t.length && t[i] != null)
		i++;

	if (i > 0 && i == t.length) {
		for (i = 0; i < t.length; i++){
			System.out.println("" + i + "=" + t[i]);
		}
		throw new RuntimeException("Length exceeded in UnitConcatenation1 i=" + i + " t.len=" + t.length);
	}

	t[i] = value;
	
	return t;
}
public String [] concatenateNumber(int value) {
	return normalize(concatenateNumberUnormalized(value));
}
private String [] concatenateNumberUnormalized(int value) {
	String ret[] = init(25);

	int n;
	boolean minus = false;
		
	if (value < 0) {
		value = -value;
		minus = true;
	}
	
	if ((value % 100) < 21) {
		ret[0] = "" + (value % 100);
		value /= 100;
	} else {	
		n = value % 10;
		ret[1] = "" + n;	
		value /= 10;
		n = value % 10;
		ret[0] = "" + (n * 10);
		value /= 10;
	}
	
	if (value > 0) {
		//shift(ret, 2);
		//n = value % 10;
		//ret[0] = "" + n;
		//ret[1] = HUNDRED;
		
		shift(ret, 1);
		ret[0] = "" + ((value % 10) * 100);

		value /= 10;
		if (value > 0) {
			shift(ret, 2);
				
			n = value % 10;
			ret[0] = "" + n;
			ret[1] = THOUSAND;
		}
	}

	if (minus) {
		shift(ret, 1);
		ret[0] = MINUS;
	}

	return ret;
}
public String [] concatenateTime(int value) {
	String ret[] = init(25);

	boolean minus = false;
		
	if (value == 0) {
		ret[0] = "" + 0;
		return normalize(ret);
	}

	if (value < 0) {
		minus = true;
		value = -value;
	}
	
	int sec = value % 60;
	ret = concatenateNumberUnormalized(sec);
	append(ret, SECONDS);
	
	value /= 60;	
	if (value > 0) {
		int min = value % 60;
		insert(ret, MINUTES);
		insert(ret, concatenateNumberUnormalized(min));

		value /= 60;	
		if (value > 0) {
			shift(ret, 2);
				
			int hour = value % 24;
			ret[0] = "" + hour;
			ret[1] = HOURS;
			
			// Remove seconds
			// removeLast(2);
		}
	}

	if (minus) {
		shift(ret, 1);
		ret[0] = MINUS;
	}

	return normalize(ret);
}
private String [] init(int length) {
	String ret[] = new String[length];
	
	for (int i = 0; i < ret.length; i++){
		ret[i] = null;
	}
	
	return ret;
}
private String [] insert(String t[], String t1[]) {
	int n = 0;

	// Find end in first table
	while (t1[n] != null)
		n++;

	shift(t, n);

	for (int i = 0; i < n; i++){
		t[i] = t1[i];
	}

	return t;
}
private String [] insert(String t[], String value) {
	shift(t, 1);
	t[0] = value;
	return t;
}
public final static void main(String args[]) {
	UnitConcatenation u = new UnitConcatenation();
//	String ret[] = u.concatenateTime(4 * 60 * 60 + 25 * 60 + 34);
	String ret[] = u.concatenateNumber(1819);
	for (int i = 0; i < ret.length ; i++){
		System.out.print(" " + ret[i]);
	}
	System.out.println();
}
private String [] normalize(String s[]) {
	int n = 0;

	for (int i = 0; i < s.length; i++){
		if (s[i] != null)
			n = i + 1;
	}

	String ret[] = new String[n];
	System.arraycopy(s, 0, ret, 0, n);
	return ret;

}
private void shift(String table[], int pos) {
	// Find fist empty position
	int i;
	for (i = 0; i < table.length; i++){
		if (table[i] == null)
			break;
	}
	
	if (i == 0)
		return;
	
	while (--i >= 0) {
		table[i + pos] = table[i];
		table[i] = null;
	}
}
}
