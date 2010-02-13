/* StorageFormat.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.storage;

import java.io.*;
import bioera.processing.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class StorageFormat {
	protected static boolean debug = false;
	public int version = -1;
/**
 * StorageFormat constructor comment.
 */
public StorageFormat() {
	super();
}
/**
 * StorageFormat constructor comment.
 */
public Date initFormat1(StorageInputStream in, SignalParameters parameters) throws Exception {
	if (debug)
		System.out.println("reading new format 1");
	StringBuffer sb = new StringBuffer();
	String line;
	while ((line = in.readLineEol()) != null) {
		//System.out.println("line=" + line);		
		sb.append(line);		
		if (line.trim().equals("</Configuration>"))
			break;
	}

	//System.out.println("Read format: " + sb);

	bioera.config.XmlConfigSection config = new bioera.config.XmlConfigSection(sb.toString());
	bioera.config.Config.importXMLProperties(parameters, config.getSection("SignalParameters"), debug);
	
	if (debug)
		System.out.println("fixing format 1 parameters");
	//System.out.println("config: " + config);
	parameters.setDigitalRange(config.getSection("SignalParameters").getIntegerThrow("digitalRange"));
	parameters.setPhysicalRange(config.getSection("SignalParameters").getIntegerThrow("physicalRange"));
	parameters.physicalUnit="uV";
	parameters.vectorLength=1;
	parameters.vectorFieldDescriptions = new String[]{""};
	parameters.info = "Source: archive file";	

	//System.out.println("rate=" + parameters.getSignalRate());
	//System.out.println("OK!!!");

	// To allow reading comments
	in.buffer = in.in.read();

	// Just read
	while ((line = in.readComment()) != null && !line.startsWith("DATA")) {
		//System.out.println("line=" + line);
		// nothing
	}

	if (line == null) {
		if (debug)
			System.out.println("BPA 4: ");				
		return null;
	}
	
	//System.out.println("OK!!! " + line);

	if (debug)	
		System.out.println("Converted storage description from format 1");
	
	//System.out.println("arch1=" + ProcessingTools.propertiesToString(parameters));

	return parseDate(line);
}
/**
 * StorageFormat constructor comment.
 */
public Date initFormat2(StorageInputStream in, SignalParameters parameters) throws Exception {
	if (debug)
		System.out.println("reading newer format >= 2");
	String line;
	StringBuffer sb = new StringBuffer();
	while ((line = in.readLineEol()) != null) {
		//System.out.println("line=" + line);		
		sb.append(line);		
		if (line.trim().equals("</Configuration>"))
			break;
	}

	//System.out.println("Read format: " + sb);

	bioera.config.XmlConfigSection config = new bioera.config.XmlConfigSection(sb.toString());
	bioera.config.Config.importXMLProperties(parameters, config.getSection("SignalParameters"), true);

	//System.out.println("OK!!!");

	// To allow reading comments
	in.buffer = in.in.read();

	// Just read
	while ((line = in.readComment()) != null && !line.startsWith("DATA")) {
		//System.out.println("line=" + line);
		// nothing
	}

	if (line == null) {
		if (debug)
			System.out.println("BPA 4: ");				
		return null;
	}
	
	//System.out.println("OK!!! " + line);

	return parseDate(line);
}
/**
 * StorageFormat constructor comment.
 */
public Date initInput(StorageInputStream in, SignalParameters parameters) throws Exception {
	String comm = in.readComment();
	if (comm == null || !comm.startsWith("FORMAT")) {
		if (debug)
			System.out.println("BPA format error: " + comm);
		return null;
	}

	if (comm.equals("FORMAT 3")) {
		try {
			version = 3;
			if (debug)
				System.out.println("VERSION 3");
			return initFormat2(in, parameters);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// Parse newer format
	if (comm.equals("FORMAT 2")) {
		try {
			if (debug)
				System.out.println("VERSION 2");
			version = 2;
			return initFormat2(in, parameters);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	} 

	parameters.physicalUnit="uV";
	parameters.vectorLength=1;
	parameters.vectorFieldDescriptions = new String[]{""};
	parameters.info = "Source: archive file";	
		
	// Parse older format
	if (comm.equals("FORMAT 1")) {
		try {
			version = 1;
			if (debug)
				System.out.println("VERSION 1");
			return initFormat1(in, parameters);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	} 

	version = 0;
	if (debug)
		System.out.println("VERSION 0");
	
	// Looks like this is very old format, do this manually
	Vector names = new Vector();
	Vector values = new Vector();
	while ((comm = in.readComment()) != null && !comm.startsWith("DATA")) {
		int i = comm.indexOf('=');
		if (i == -1) {
			if (debug)
				System.out.println("2: " + comm);
			return null;
		}

		String name = comm.substring(0, i);
		String value = comm.substring(i+1);
		
		if ("signalRange".equals(name))
			parameters.setDigitalRange(Integer.parseInt(value));
		else if ("uVrange".equals(name))
			parameters.setPhysicalRange(Float.parseFloat(value));
		else if ("signalRate".equals(name))
			parameters.signalRate = Integer.parseInt(value);
		else if ("signalResolutionBits".equals(name))
			parameters.signalResolutionBits = Integer.parseInt(value);
		else if ("info".equals(name))
			parameters.info = value;
	}

	if (comm == null) {
		if (debug)
			System.out.println("BPA 3: " + comm);		
		return null;
	}
	
	return parseDate(comm);
}
/**
 * StorageFormat constructor comment.
 */
public void initOutput(StorageOutputStream out, SignalParameters parameters, Pipe inPipe) throws Exception {
	version = 3;
	out.writeComment("FORMAT 3");

	bioera.config.XmlCreator config = new bioera.config.XmlCreator();
	bioera.config.Config.exportXMLProperties(parameters.createCopyOfValues(), "SignalParameters", config);
	//System.out.println("p2=" + parameters);	
	
	out.writeString(config.toString() + "\r\n");
}
/**
 * StorageFormat constructor comment.
 */
public static void main(String args[]) throws Exception {
	try {
		System.out.println("started");
		//test10bitStreams();
		int i;
		for (i = 0; i < 1000; i++){
			random10bitStreams();
		}
		System.out.println("Checked " + i);

		//testFormat();
		System.out.println("Finished");
	} catch (Exception e) {
		e.printStackTrace();
	}	
}
/**
 * StorageFormat constructor comment.
 */
public Date parseDate(String line) throws Exception {
	line = line.substring("DATA".length()).trim();
	return bioera.Tools.dateTimeFormatter().parse(line);
	
}
/**
 * StorageFormat constructor comment.
 */
private static void random10bitStreams() throws Exception {
	StorageOutputStream out = new StorageOutputStream(new Archive10bitOutputStream(new FileOutputStream("c:\\projects\\eeg\\storage.test")));
	int c[] = new int[10];
	Random r = new Random();
	int v;
	for (int i = 1; i < c.length; i++){
		v = r.nextInt(250);
		if ((v % 4) != 0) {
			c[i] = v;
			out.write2(c[i]);
		} else {
			c[i] = 0;
			out.writeComment("" + i);
		}
	}
	out.close();
	StorageInputStream in = new StorageInputStream(new Archive10bitInputStream(new FileInputStream("c:\\projects\\eeg\\storage.test")));
	int ch = -1, counter = 1;
	String comment = null; boolean finished = false;
	while (!in.isEof()) {
		if ((ch = in.read2()) != -1) {
			if (ch != c[counter++]) {
				System.out.println("wrong byte " + counter);
				System.exit(10);
			}
		}
		if ((comment = in.readComment()) != null) {
			if (c[counter++] != 0) {
				System.out.println("wrong comment " + counter );
				System.exit(10);
			}
		}
	}
	in.close();
}
/**
 * StorageFormat constructor comment.
 */
private static void randomStreams() throws Exception {
	StorageOutputStream out = new StorageOutputStream(new FileOutputStream("c:\\projects\\eeg\\storage.test"));
	int c[] = new int[10];
	Random r = new Random();
	int v;
	for (int i = 1; i < c.length; i++){
		v = r.nextInt(250);
		if ((v % 4) != 0) {
			c[i] = v;
			out.write2(c[i]);
		} else {
			c[i] = 0;
			out.writeComment("" + i);
		}
	}
	out.close();
	StorageInputStream in = new StorageInputStream(new FileInputStream("c:\\projects\\eeg\\storage.test"));
	int ch = -1, counter = 1;
	String comment = null; boolean finished = false;
	while (!in.isEof()) {
		if ((ch = in.read2()) != -1) {
			if (ch != c[counter++]) {
				System.out.println("wrong byte " + counter);
				System.exit(10);
			}
		}
		if ((comment = in.readComment()) != null) {
			if (c[counter++] != 0) {
				System.out.println("wrong comment " + counter );
				System.exit(10);
			}
		}
	}
	in.close();
}
/**
 * StorageFormat constructor comment.
 */
private static void test10bitStreams() throws Exception {
	StorageOutputStream out = new StorageOutputStream(new Archive10bitOutputStream(new FileOutputStream("c:\\projects\\eeg\\storage.test")));

	out.write2('1');
	out.writeComment("2");
	out.write2('#');
	out.write2('3');
	out.close();
	StorageInputStream in = new StorageInputStream(new Archive10bitInputStream(new FileInputStream("c:\\projects\\eeg\\storage.test")));
	int ch = -1;
	String comment = null;
	while ((ch = in.read2()) != -1 | (comment = in.readComment()) != null) {
		if (ch != -1)
			System.out.println("byte=" + (char) ch);
		if (comment != null)
			System.out.println("comment=" + comment);
	}
	in.close();
}
/**
 * StorageFormat constructor comment.
 */
private static void testFormat() throws Exception {
	//StorageOutputStream out = new StorageOutputStream(new FileOutputStream("c:\\projects\\eeg\\storage.test"));
	//StorageFormat format = new StorageFormat();
	//format.initOutput(out, SignalParameters.getDefaultSettings());
	//out.close();
	StorageInputStream in = new StorageInputStream(new FileInputStream("c:\\projects\\eeg\\storage.test"));
	StorageFormat format1 = new StorageFormat();
	SignalParameters s = new SignalParameters(null);
	format1.initInput(in, s);	
	in.close();

System.out.println("printing:" + ProcessingTools.propertiesToString(s));
}
/**
 * StorageFormat constructor comment.
 */
private static void testStreams() throws Exception {
	StorageOutputStream out = new StorageOutputStream(new FileOutputStream("c:\\projects\\eeg\\storage.test"));

	out.write2('1');
	out.writeComment("2");
	out.write2('#');
	out.write2('3');
	out.close();
	StorageInputStream in = new StorageInputStream(new FileInputStream("c:\\projects\\eeg\\storage.test"));
	int ch = -1;
	String comment = null;
	while ((ch = in.read2()) != -1 | (comment = in.readComment()) != null) {
		if (ch != -1)
			System.out.println("byte=" + (char) ch);
		if (comment != null)
			System.out.println("comment=" + comment);
	}
	in.close();
}
}
