/* ThroughputTest.java v 1.0.9   11/6/04 7:15 PM
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
public class ThroughputTest  
{
	FileOutputStream logFileStream;
	int clientShowEveryMillis = 2000;
	int serverShowEveryMillis = 4000;
    public ThroughputTest()
    {
    }
public void client(String host) throws Exception{
	System.out.println("Starting client");
	initLog();
	try {		
		Socket socket = new Socket(host, 9789);
		println("Connected to " + host + " at " + new Date()); 
		OutputStream out = socket.getOutputStream();
		InputStream in = socket.getInputStream();
		int counter = 1; 
		long time = System.currentTimeMillis(), t1;
		int timeout = 1000, n = 12000; 
		long incrementTime = 0;
		int lastCounter = 0;
		while (true) {
			while (in.available() > 0)
				in.read();
			t1 = System.currentTimeMillis();
			if (t1 - time > 2000) {
				System.out.println("Long client time was reported: " + ((t1 - time) / 1000) + "[s] at "+ new Date());
			}
			time = t1;
			for (int i = 0; i < n; i++){
				out.write(counter++);
			}

			long now = System.currentTimeMillis();
			if ((timeout - (now - time)) > 0)
				Thread.sleep(timeout - (now - time));			

			now = System.currentTimeMillis();
			if ((now - incrementTime) > clientShowEveryMillis) {
				n = n * 2;
				//timeout /= 2;
				println("timeout=" + timeout + ", count=" + n + "  rate=" + ((counter - lastCounter) * 1000 / (now - incrementTime)));
				lastCounter = counter;
				incrementTime = now;
			}

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
            ThroughputTest t = new ThroughputTest();
            if (args.length == 0)
            	System.out.println("arguments");
            else if ("-s".equals(args[0])) {
	            t.server();
	            if (args.length > 1)
	            	t.serverShowEveryMillis = Integer.parseInt(args[1]);
            } else if ("-c".equals(args[0])) {
	            t.client(args[1]);
	            if (args.length > 2)
	            	t.clientShowEveryMillis = Integer.parseInt(args[2]);
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
	System.out.println(s);
	
	//logFileStream.write((s + "\n").getBytes());
}
public void server() throws Exception{
	System.out.println("Starting server");
	initLog();
	int v = -2;
	int counter = 0;
	try {		
		ServerSocket s = new ServerSocket(9789);
		println("waiting for connection");
		Socket socket = s.accept();
		println("accepted connection");
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		long time = System.currentTimeMillis();
		long t1, printTime = 0; int lastCounter = 0;
		byte b[] = new byte[10240];
		while ((v = in.read(b)) != -1) {
			t1 = System.currentTimeMillis();
			if (t1 - time > 2000) {
				System.out.println("Long server time was reported: " + ((t1 - time) / 1000) + "[s] at "+ new Date());
			}
			time = t1;
			counter += v;
			//for (int i = 0; i < v; i++){
				//counter++;
				//if (((byte)(counter % 256)) != b[i]) {
					//System.out.println("Wrong increment at "+ new Date() + "   is " + v + " should be " + counter);	
				//}				
			//}

			long now = System.currentTimeMillis();
			if (now - printTime > serverShowEveryMillis) {
				println("ok " + counter + "   rate=" + ((counter - lastCounter) * 1000 / (now - printTime)) + "  " + new Date().toString());
				lastCounter = counter;
				printTime = now;

				//out.write(0);
			}
		}
		System.out.println("Socket closed at " + new Date());
	} catch(Exception e) {
        System.out.println("Server Error: " + e + "("+new Date()+")\n\n");
        e.printStackTrace();
    }
}
}
