/* SocketConnectionStableTest.java v 1.0.9   11/6/04 7:15 PM
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

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class SocketConnectionStableTest  
{
	FileOutputStream logFileStream;
    public SocketConnectionStableTest()
    {
    }
public void client(String host) throws Exception{
	initLog();
	try {		
		Socket socket = new Socket(host, 111);
		println("Connected to " + host + " at " + new Date()); 
		OutputStream out = socket.getOutputStream();
		int counter = 1;
		while (true) {
			out.write(counter++);
			Thread.sleep(1000);
		}
	} catch(Exception e) {
        System.out.println("Client Error: " + e + "("+new Date()+")\n\n");
        e.printStackTrace();
    }

	println("disconnected at " + new Date());
}
public void initLog() throws Exception {
	if (logFileStream != null)
		return;
	int i = 1;
	File f = new File("socket_test_debug" + i);
	while (f.exists()) {
		f = new File("socket_test_debug" + ++i);
	}
	logFileStream = new FileOutputStream(f);
	println("Started at " + new Date());
}
    public static void main(String args[])
        throws Exception
    {
        try
        {
            System.out.println("Started");
            SocketConnectionStableTest t = new SocketConnectionStableTest();
            if (args.length == 0)
            	System.out.println("arguments");
            else if ("-s".equals(args[0])) {
	            t.server();
            } else if ("-c".equals(args[0])) {
	            t.client(args[1]);
            }
            System.out.println("finished");
        }
        catch(Exception e)
        {
            System.out.println("Error: " + e + "\n\n");
            e.printStackTrace();
        }
    }
public void println(String s) throws Exception {
	logFileStream.write((s + "\n").getBytes());
	//System.out.println(s);
}
public void server() throws Exception{
	initLog();
	int v = -2;
	int counter = -1;
	try {		
		ServerSocket s = new ServerSocket(111);
		Socket socket = s.accept();
		println("accepted connection");
		InputStream in = socket.getInputStream();
		long time = System.currentTimeMillis();
		long t1;
		while ((v = in.read()) != -1) {
			t1 = System.currentTimeMillis();
			if (t1 - time > 2000) {
				System.out.println("Long time was reported: " + ((t1 - time) / 1000) + "[s] at "+ new Date());
			}
			t1 = time;
			time = System.currentTimeMillis();
			if (counter != -1) {
				if (counter != v) {
					System.out.println("Wrong increment at "+ new Date() + "   is " + v + " should be " + counter);	
				}
			}

			counter = (v + 1) % 256;
			println(new Date().toString());
		}
		System.out.println("Socket closed at " + new Date());
	} catch(Exception e) {
        System.out.println("Server Error: " + e + "("+new Date()+")\n\n");
        e.printStackTrace();
    }
}
}
