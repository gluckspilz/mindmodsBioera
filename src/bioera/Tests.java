/* Tests.java v 1.0.9   11/6/04 7:15 PM
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

package bioera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class Tests implements Runnable 
{

    public Tests()
    {
    }
public void go() throws Exception {
	System.setOut(new TPrintStreamAndLog(System.out, new Log(new File("test"))));
	System.setIn(new EmptyInputStream());
	System.out.println("" + System.in);
	System.out.println("" + System.in.available());

	Thread.sleep(1000);
}
public void goWav() throws Exception {
	FileOutputStream o = new FileOutputStream("c:\\temp\\test.wav");
	bioera.sound.WavOutputStream wavOut = new bioera.sound.WavOutputStream(o, 256);
	for (int i = 0; i < 10; i++){
		wavOut.write2(i);
	}
	wavOut.close();
	
	System.out.println("\\n=" + (int) '\n');
/*	
	new Thread(this).start();

	Thread.sleep(100);

	Socket s = new Socket("localhost", 111);

	int i = 0;
	try {
		OutputStream out = s.getOutputStream();
		while (true) {
			if (i > 33009)
				System.out.println("i=" + i);			
			out.write(i);
			if (i % 1000 == 0 || i > 33000)
				System.out.println("i=" + i);
			i++;
		}
	} finally {
		System.out.println("finished with i=" + i);
	}
*/	
}
    public static void main(String args[])
        throws Exception
    {
        try
        {
            System.out.println("Started");
            Tests t = new Tests();
            t.go();
            System.out.println("finished");
        }
        catch(Exception e)
        {
            System.out.println("Error: " + e + "\n\n");
            e.printStackTrace();
        }
    }
public void run() {
	int i = 0;
	try {		
		ServerSocket s = new ServerSocket(111);
		Socket socket = s.accept();
		OutputStream out = socket.getOutputStream();
		InputStream in = socket.getInputStream();
		//socket.setSoLinger()
		Thread.sleep(5000);
		while (true) {
			in.read();
			////Thread.sleep(100);
			//out.write(i);
			i++;
		}
	} catch(Exception e) {
        System.out.println("Server Error: " + e + "("+i+")\n\n");
        e.printStackTrace();
    }
}
}
