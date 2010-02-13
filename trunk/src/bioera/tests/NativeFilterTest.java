/* NativeFilterTest.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.tests;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import bioera.nativeobjects.*;
import bioera.filter.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class NativeFilterTest
{
    public NativeFilterTest()
    {
    }
    public static void main(String args[])
        throws Exception
    {
        try
        {
	        System.out.println("started");
            NativeFilterTest t = new NativeFilterTest();
            t.start();
            System.out.println("finished");
        }
        catch(Throwable e)
        {
            System.out.println("Error: " + e + "\n\n");
            e.printStackTrace();
        }
    }
public void start() throws Exception {
	FidlibFilter f = (FidlibFilter) Filter.newInstance(Filter.TYPE_FIDLIB);
	f.init(
		0, 		// filter type
		0, 		// band type
		2, 		// filter order
		100.0, 	// rate	
		10.0, 	// freq0
		20.0, 	// freq1
		0, 		// min input integer value
		100, 	// max input integer value
		0, 		// min output integer value
		100);	// max output integer value

	int t[] = {100, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50};
	int t1[] = new int[t.length];

	f.process(t, t1, t.length);

	for (int i = 0; i < t.length; i++){
		System.out.println(": " + t1[i]);
	}
	
	System.out.println("filter delay: " + f.getDelay());

	System.out.println("filter response: ");
	double base = 10;
	for (int i = 0; i < 10; i++){
		System.out.println("f[" + (base + i) + "]=" + f.getResponse(base + i));
	}
}
public void start1() throws Exception {
	FidlibCFilter f = new FidlibCFilter();
	System.out.println("Id before=" + f.id);
	f.init(f.id, "LpBu4/20.4", 100.0, 10.0, 25.0, 0);
	System.out.println("Id after=" + f.id);

	System.out.println(": " + f.run(f.id, 1.0f));
	for (int i = 0; i < 10; i++){
		System.out.println(": " + f.run(f.id, 0f));
	}

	f = new FidlibCFilter();
	System.out.println("Id before=" + f.id);
	f.init(f.id, "LpBu4/20.4", 100.0, 10.0, 25.0, 0);
	System.out.println("Id after=" + f.id);

	System.out.println(": " + f.run(f.id, 1.0f));
	for (int i = 0; i < 10; i++){
		System.out.println(": " + f.run(f.id, 0f));
	}
	
}
}
