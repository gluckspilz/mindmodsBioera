/* PerformanceTest.java v 1.0.9   11/6/04 7:15 PM
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

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class PerformanceTest  
{
    public PerformanceTest()
    {
    }
    public static void main(String args[])
        throws Exception
    {
        try
        {
            PerformanceTest t = new PerformanceTest();
	        t.start();
            System.out.println("finished");
        }
        catch(Exception e)
        {
            System.out.println("Error: " + e + "\n\n");
            e.printStackTrace();
        }
    }
public void println(String s) throws Exception {
	System.out.println(s);
	
	//logFileStream.write((s + "\n").getBytes());
}
public void start() throws Exception{
	int t[] = new int[10000000];
	long time = System.currentTimeMillis();
	for (int i = 0; i < t.length; i++){
		t[i] = i;
	}
	System.out.println("diff=" + (System.currentTimeMillis() - time));
	time = System.currentTimeMillis();
	for (int i = 0; i < t.length; i++){
		t[i] = i;
	}
	System.out.println("diff=" + (System.currentTimeMillis() - time));
	time = System.currentTimeMillis();
	for (int i = 0; i < t.length; i++){
		t[i] = i;
	}
	System.out.println("diff=" + (System.currentTimeMillis() - time));
	time = System.currentTimeMillis();
	try {		
		for (int i = 0;; i++){
			t[i] = i;
		}
	} catch (ArrayIndexOutOfBoundsException e){
	}
	System.out.println("exdiff=" + (System.currentTimeMillis() - time));
	time = System.currentTimeMillis();
	try {		
		for (int i = 0;; i++){
			t[i] = i;
		}
	} catch (ArrayIndexOutOfBoundsException e){
	}
	System.out.println("exdiff=" + (System.currentTimeMillis() - time));
	time = System.currentTimeMillis();
	try {		
		for (int i = 0;; i++){
			t[i] = i;
		}
	} catch (ArrayIndexOutOfBoundsException e){
	}
	System.out.println("exdiff=" + (System.currentTimeMillis() - time));
	time = System.currentTimeMillis();
	int k = t.length;
	for (int i = 0;i<k; i++){
		t[i] = i;
	}
	System.out.println("l_diff=" + (System.currentTimeMillis() - time));
	time = System.currentTimeMillis();
	k = t.length;
	for (int i = 0;i<k; i++){
		t[i] = i;
	}
	System.out.println("l_diff=" + (System.currentTimeMillis() - time));
	time = System.currentTimeMillis();
	k = t.length;
	for (int i = 0;i<k; i++){
		t[i] = i;
	}
	System.out.println("l_diff=" + (System.currentTimeMillis() - time));
}
}
