/* NativeFileAccessTest.java v 1.0.9   11/6/04 7:15 PM
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

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class NativeFileAccessTest
{
    public NativeFileAccessTest()
    {
    }
    public static void main(String args[])
        throws Exception
    {
        try
        {
	        System.out.println("started");
            NativeFileAccessTest t = new NativeFileAccessTest();
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
	File file = new File("a.txt");
	System.out.println("File exists: " + file.exists());
	FileAccess f = new FileAccess();
	System.out.println("Passed file name '" + file.getName() + "'");
	int errcode = f.open(file.getName(), f.F_WRITEONLY);
	if (errcode < 0)
		throw new Exception("Couldn't open file: " + errcode);
	String data = "Test data";
	f.write(data.getBytes(), 0, data.length());
	f.close();

	errcode = f.open(file.getName(), f.F_READONLY);
	if (errcode < 0)
		throw new Exception("Couldn't open file for read: " + errcode);
	
	byte buf[] = new byte[data.length()];
	f.read(buf, 0, buf.length);

	String s = new String(buf);
	if (s.equals(data))
		System.out.println("OK");
	else {
		System.out.println("Test failed:");
		System.out.println("Original='" + data + "'");
		System.out.println("Result='" + s + "'");
	}		
}
}
