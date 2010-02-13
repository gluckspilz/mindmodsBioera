/* SoundDisplay.java v 1.0.9   11/6/04 7:15 PM
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

import java.io.*;
import javax.sound.sampled.*;
import bioera.fft.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class SoundDisplay {
	int b[];	
/**
 * Sound constructor comment.
 */
public SoundDisplay() throws Exception {
/*	
	int a = 0;
	// U -> S
	// 0 -> -128 
	// 1 -> -127
	// 127 -> -1
	// 128 -> 0
	// 254 -> 126
	// 255 -> 127
	
	byte b = (byte) a;
	System.out.println("b=" + b);

*/
/*
	// I   -> SB -> UB
	// 0   -> 0  => 128
	// 255 -> -1 => 127
	// 254 -> -2 => 126
	// 253 -> -3 => 125
	// 128 -> -128 => 0
	// 127 -> 127 => 255
	// 126 -> 126 => 254


*/

	
//	File f = new File("/temp/8bitwav");
//	File f = new File("/programs/ssh/audiosample");
//	File f = new File("/programs/ssh/dspsample");
//	File f = new File("/programs/ssh/8bitwav");
//	File f = new File("/programs/ssh/audioding");
	File f = new File("/programs/ssh/testout");
	b = new int[(int) f.length()];
//	byte b1[] = new byte[b.length];
	InputStream in = new FileInputStream(f);
//	in.read(b1);
	for (int i = 0; i < b.length; i++){
		b[i] = in.read();
		//b1[i];
		//if (b[i] < 0)
			//b[i] += 256;
	}
	//ChartDisplay disp = new ChartDisplay();
	//disp.add(new SignalChart(this, 240, 256));
	//disp.show();

	//disp.refresh();
	
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2003 8:32:31 AM)
 * @return int[]
 */
public int[] getSignalData() {
	return b;
}
public static void main(String args[]) throws Exception {
	
    try {
	        System.out.println("started");

	        new SoundDisplay();
	        
        System.out.println("finished");
    } catch (Exception e) {
        System.out.println("Error: " + e + "\n\n");
        e.printStackTrace();
    }
    
}
}
