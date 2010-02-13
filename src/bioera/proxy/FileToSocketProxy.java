/* FileToSocketProxy.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.proxy;

import java.io.*;
import java.net.*;
import java.util.*;

/*
	This class can be useful on Unix, when reading data from serial port and sending to network.	
*/

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class FileToSocketProxy {
	FileOutputStream logFileStream;
	public String inputFilePath;
	public String host;
	public int port = 9789;
	public String checkConnectionHost = null;
	public int checkConnectionPort = -1;
	public boolean processInLoop = false;
	public int rate = 0;
	private int counter;
	public final static int BUFFER_LENGTH = 1024;
	public String soundPlayer = "wavp";
	public String soundExtension = ".wav";
	public String soundDir = "media" + File.separator + "ding" + File.separator;
	private bioera.SystemProcess process;
	protected static boolean debug = bioera.Debugger.get("proxy.file.to.socket");
    public FileToSocketProxy()
    {
	    process = new bioera.SystemProcess();
    }
private static void args() throws Exception {
    System.out.println("arguments: -s|c -file file_path [-host host_address] [-loop] [-rate rate_value]");
    System.exit(1);
}
public void client() throws Exception{
	if (host == null)
		args();

	initLog();
	int count = 0;
	boolean connected = false;
	do {
		// Try to connect every 1 second
		Socket socket = null;
		
		try {
			socket = new Socket(host, port);
			socket.setSoTimeout(1000);
			connected = true;
			println("Connected to " + host + " at " + new Date());
			play("connected");
			if (inputFilePath != null)
				sendFile(socket);
			else
				onlyReceive(socket);
		} catch(Exception e) {
	        //System.out.println("Client Error: " + e + "("+new Date()+")\n\n");
	        //e.printStackTrace();
	        if (socket != null) {
		        try {		        
			        socket.close();
		        } catch (Exception e1) {
		        }
	        }
	    }

		Thread.sleep(1000);
		
		if (connected) {
			println("Disconnected at " + new Date());
			play("disconnected");
			connected = false;
		} else {
			if (++count % 100 == 0)
				println("Attempted to connect " + count + " times");
			if (checkConnectionHost != null && checkConnectionPort != -1) {
				try {
					new Socket(checkConnectionHost, checkConnectionPort).close();
					// Connection is ok
				} catch (Exception e) {
					// Connection is broken
					println("Connection broken " + new Date());
					play("connectionbroken");
					// Do something here!!!
				}
			}
		}
	} while (processInLoop);

	println("disconnected at " + new Date());
}
private void initLog() throws Exception {
	if (logFileStream != null)
		return;
	int i = 1;
	File f = new File("socket_server_log" + i);
	while (f.exists()) {
		f = new File("socket_server_log" + ++i);
	}
	logFileStream = new FileOutputStream(f);
	println("Started at " + new Date());
}
public static void main(String args[]) throws Exception {
    try {
        if (args.length < 2) {
	        args();
        }
	    
        System.out.println("Started");        
        FileToSocketProxy t = new FileToSocketProxy();
        boolean isServer = true;
        for (int i = 0; i < args.length; i++){
        	String arg = args[i];
        	if ("-s".equals(arg)) {
	        	isServer = true;
        	} else if ("-c".equals(arg)) {
	        	isServer = false;
        	} else if ("-file".equals(arg)) {
	        	t.inputFilePath = args[++i];
        	} else if ("-debug".equals(arg)) {
	        	t.debug = true;
	        } else if ("-loop".equals(arg)) {
	        	t.processInLoop = true;
	        } else if ("-conn_host".equals(arg)) {
	        	t.checkConnectionHost = args[++i];
	        } else if ("-conn_port".equals(arg)) {
	        	t.checkConnectionPort = Integer.parseInt(args[++i]);
	        } else if ("-host".equals(arg)) {
		    	t.host =  args[++i];
	        } else if ("-port".equals(arg)) {
		        t.port =  Integer.parseInt(args[++i]);
	        } else if ("-rate".equals(arg)) {
		        t.rate =  Integer.parseInt(args[++i]);
	        } else if ("-player".equals(arg)) {
		        t.soundPlayer =  args[++i];
	        } else if ("-sounds".equals(arg)) {
		        t.soundDir =  args[++i];
		        if (!t.soundDir.endsWith(File.separator))
		        	t.soundDir += File.separator;
        	} else {
	        	System.out.println("Unrecognize parameter " + arg);
        	}
	        	
        }

//		t.keyb = new FileInputStream("/dev/tty0");

		t.play("start");
		if (isServer) {
            t.server();
        } else {
            t.client();
        }
        t.play("stop");

        System.out.println("finished");
    } catch (Exception e) {
        System.out.println("Error: " + e + "\n\n");
        e.printStackTrace();
    }
}
protected void play(int n) throws Exception{
	try {
		File file = new File(soundDir + n + soundExtension);
		if (!file.exists()) {
			// search for file name
			File folder = new File(soundDir);
			if (folder.exists() && folder.isDirectory()) {
				String list[] = folder.list();
				for (int i = 0; i < list.length; i++){
					String name = list[i];
					System.out.println("name=" + name);
					if (name.indexOf("_") != -1) {
						try {
							int num = Integer.parseInt(name.substring(0, name.indexOf("_")));
							if (num == n) {
								file = new File(soundDir + list[i]);
								System.out.println("found " + file);
								break;
							}
						} catch (Exception e) {
						}
					}
				}
			}
		}
		
		String cmd = soundPlayer + " " + file.getPath();
		if (debug)
			System.out.println("Playing " + cmd);
		int ret = process.execute(cmd);
		if (ret != 0)
			println("Player returned exit code " + ret);
	} catch (Exception e) {
		println("Player error: " + e);
	}	
}
protected void play(String name) throws Exception{
	try {
		String cmd = soundPlayer + " media/" + name + soundExtension;
		if (debug)
			System.out.println("Playing " + cmd);
		int ret = process.execute(cmd);
		if (ret != 0)
			println("Player returned exit code " + ret);
	} catch (Exception e) {
		println("Player error: " + e);
	}	
}
private void println(String s) throws Exception {
	logFileStream.write((s + "\n").getBytes());
	if (debug)
		System.out.println(s);
}
private void sendFile(Socket socket) throws Exception{
	InputStream in = null;
	InputStream remoteIn = null;
	OutputStream out = null;
	long time = System.currentTimeMillis();
	int byteCounter = 0;
	int noResponseCounter = 1;
	int k;
	try {
		in = new FileInputStream(inputFilePath);
		remoteIn = socket.getInputStream();
		out = socket.getOutputStream();
		int i, n;
		byte b[] = (rate == 0 ? new byte[BUFFER_LENGTH] : new byte[rate]);
		while (true) {
			i = in.available();
			if (i > 0) {
				if (i > BUFFER_LENGTH)
					i = BUFFER_LENGTH;
				if ((n = in.read(b, 0, i)) == -1)
					break;
				if (debug)
					println("Sent " + i);
				out.write(b, 0, i);
				out.flush();
				byteCounter += i;
				if (rate != 0) {
					Thread.sleep((1000 * byteCounter / rate) - (System.currentTimeMillis() - time));
				}
			} else {
				if (byteCounter == 0) {
					if (System.currentTimeMillis() - time > 1000) {
						println("Input stream is not responding " + noResponseCounter++);
						play("noserial");
						Thread.sleep(1000);
					}
				}

				// Check 
				i = remoteIn.available();
				if (i > 0) {
					// Read the last value
					while (--i > 0) {
						remoteIn.read();
					}
					k = remoteIn.read();
					play(k);
				}
				Thread.sleep(100);
			}
		}
	} finally {
		if (in != null)
			in.close();
		if (out != null)
			out.close();
	}
}
public void server() throws Exception{
	initLog();
	ServerSocket s = new ServerSocket(port);
	do {
		println("waiting for connection " + new Date());
		Socket socket = s.accept();
		try {
			println("accepted connection at " + new Date());
			sendFile(socket);
		} catch(Exception e) {
	        System.out.println("Server disconnected: " + "("+new Date()+")\n\n");
	        try {
		        socket.close();
	        } catch (Exception e1) {
	        }
	    }
	} while (processInLoop);
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}

private void onlyReceive(Socket socket) throws Exception{
	InputStream remoteIn = null;
	OutputStream out = null;
	long time = System.currentTimeMillis();
	int byteCounter = 0;
	int noResponseCounter = 1;
	int k;
	try {
		remoteIn = socket.getInputStream();
		out = socket.getOutputStream();
		int i, n;
		byte b[] = (rate == 0 ? new byte[BUFFER_LENGTH] : new byte[rate]);
		while (true) {
			// Check 
			i = remoteIn.available();
			if (i > 0) {
				// Read the last value
				while (--i > 0) {
					remoteIn.read();
				}
				k = remoteIn.read();
				play(k);
			}
			Thread.sleep(100);
		}
	} finally {
		if (out != null)
			out.close();
	}
}
}
