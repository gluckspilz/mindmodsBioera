/* ElementTest.java v 1.0.9   11/6/04 7:15 PM
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
import java.lang.reflect.*;
import bioera.processing.*;
import bioera.processing.impl.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class ElementTest
{
	Element tE;

	Element testWriter;
	Element testReader;

	int read1, read2, read3;
	int passedCounter = 1;
	int functionsCounter = 1;

	boolean showInfo = true;
	boolean debug = true;

	static int SCALAR_BUFFER_SIZE = 1000;
	static int VECTOR_BUFFER_SIZE = 100;
    public ElementTest()
    {
    }
public void a() throws Exception {
	Decimator vt = new Decimator();
	newTestElement(vt);
	connect();

//	setDebug();

	/* ----------------Decimator------------------*/

	vt.sampleOn = 1;
	vt.sampleOff = 3;
	reinit();
	start();

	for (int i = 1; i < 10; i++){
		write(i);
	}

	process();

	read();
	verify(1);
	read();
	verify(5);
	read();
	verify(9);

	passed();

	vt.sampleOn = 2;
	vt.sampleOff = 0;
	reinit();
	start();

	for (int i = 1; i < 10; i++){
		write(i);
	}

	process();

	for (int i = 1; i < 10; i++){
		read();
		verify(i);
	}

	passed();

	passed("Decimator");

}
public int available() throws Exception {
	return ((BufferedPipe)testReader.getInput(0)).available();
}
public void connect() throws Exception {
	testWriter.reinit();
	testReader.reinit();
}
    public static void main(String args[])
        throws Exception
    {
        try
        {
            ElementTest t = new ElementTest();
            //t.runScalarTransform();
//            t.runCMixer();
            t.runVectorToScalar();
//	        t.testAll();
//            t.a();
            System.out.println("finished");
        }
        catch(Exception e)
        {
            System.out.println("Error: " + e + "\n\n");
            e.printStackTrace();
        }
    }
public void newTestElement(Element e) throws Exception {
	tE = e;

	testWriter = new ElementTestWriter(e);
	testReader = new ElementTestReader(e);

	functionsCounter = 0;

	if (showInfo)
		System.out.println("==========Testing element " + e.getClass() + "===========");
}
public void passed() throws Exception {
	showInfo("----Passed: " + passedCounter++);
}
public void passed(String s) throws Exception {
	functionsCounter++;
	showInfo("----Passed: " + s);
}
public void passedA() throws Exception {
	showInfo("=========== Test passed: " + tE.getClass()+ "============");
}
public void process() throws Exception {
	tE.process();
}
public int read() throws Exception {
	int avail = ((BufferedPipe)testReader.getInput(0)).available();
	if (avail == 0)
		throw new Exception("Read error: No data in input buffer");
	if (testReader.getInput(0).getType() == bioera.processing.VectorPipe.TYPE) {
		int buff[][] = ((BufferedVectorPipe)testReader.getInput(0)).getVBuffer();
		int v[] = buff[0];
		//System.out.println("read=" + ProcessingTools.arrayToString(v));
		if (v.length > 0)
			read1 = v[0];
		else
			read1 = -1;
		if (v.length > 1)
			read2 = v[1];
		else
			read2 = -1;
		if (v.length > 2)
			read3 = v[2];
		else
			read3 = -1;
	} else {
		int v[] = ((BufferedScalarPipe)testReader.getInput(0)).getBuffer();
		read1 = v[0];
		read2 = -1;
		read3 = -1;
	}
	((BufferedPipe)testReader.getInput(0)).purge(1);
	return read1;
}
public void readLast() throws Exception {
	int avail = ((BufferedPipe)testReader.getInput(0)).available();
	if (avail == 0)
		throw new Exception("Read error: No data in input buffer");
	if (testReader.getInput(0).getType() == bioera.processing.VectorPipe.TYPE) {
		int buff[][] = ((BufferedVectorPipe)testReader.getInput(0)).getVBuffer();
		int v[] = buff[avail - 1];
		//System.out.println("read=" + ProcessingTools.arrayToString(v));
		if (v.length > 0)
			read1 = v[0];
		else
			read1 = -1;
		if (v.length > 1)
			read2 = v[1];
		else
			read2 = -1;
		if (v.length > 2)
			read3 = v[2];
		else
			read3 = -1;
	} else {
		int v[] = ((BufferedScalarPipe)testReader.getInput(0)).getBuffer();
		read1 = v[avail - 1];
		read2 = -1;
		read3 = -1;
	}
	((BufferedPipe)testReader.getInput(0)).purge(1);
}
public void reinit() throws Exception {
	tE.preReinit();
	tE.reinit();
}
public void runCMixer() throws Exception {
		//"ADD",
		//"SUBTRACT",
		//"MULTIPLY",
		//"MULTIPLY_BALANCED",
		//"DIVIDE",
		//"DIVIDE_BALANCED",
		//"AVERAGE",
		//"MAX",
		//"MIN",
	CMixer vt = new CMixer();
	newTestElement(vt);
	connect();

//	setDebug();

	/* ----------------ADD------------------*/

	vt.function.setSelectedItemThrow("ADD");
	vt.constantValue.value = 3;
	vt.constantValue.scale.setSelectedItem("digi");
	reinit();
	start();

	write(1);
	process();
	read();
	verify(4);
	passed();

	write(3);
	process();
	read();
	verify(6);
	passed();

	write(1);
	process();
	process();
	read();
	verify(4);
	passed();


	passed("ADD");

	/* ----------------SUBTRACT------------------*/
	vt.function.setSelectedItemThrow("SUBTRACT");
	reinit();
	start();

	write(4);
	process();
	read();
	verify(1);
	passed();

	write(3);
	process();
	read();
	verify(0);
	passed();

	write(1);
	process();
	process();
	read();
	verify(-2);
	passed();

	passed("SUBTRACT");

	/* ----------------MULTIPLY------------------*/

	vt.function.setSelectedItemThrow("MULTIPLY");
	reinit();
	start();

	write(1);
	process();
	process();
	read();
	verify(3);
	passed();

	write(3);
	process();
	process();
	read();
	verify(9);
	passed();

	write(-3);
	process();
	process();
	read();
	verify(-9);
	passed();

	passed("MULTIPLY");

	/* ----------------AVERAGE------------------*/
	vt.function.setSelectedItemThrow("AVERAGE");
	reinit();
	start();

	write(1);
	process();
	read();
	verify(2);
	passed();

	write(4);
	process();
	read();
	verify(3);
	passed();


	passed("AVERAGE");

	/* ----------------MAX------------------*/

	vt.function.setSelectedItemThrow("MAX");
	reinit();
	start();

	write(1);
	process();
	read();
	verify(3);
	passed();


	write(8);
	process();
	read();
	verify(8);
	passed();

	write(-1);
	process();
	read();
	verify(3);
	passed();

	passed("MAX");

	/* ----------------MIN------------------*/

	vt.function.setSelectedItemThrow("MIN");
	reinit();
	start();

	write(1);
	process();
	read();
	verify(1);
	passed();


	write(8);
	process();
	read();
	verify(3);
	passed();

	write(-1);
	process();
	read();
	verify(-1);
	passed();

	passed("MIN");


	/* ----------------MULTIPLY_BALANCED------------------*/

	vt.function.setSelectedItemThrow("MULTIPLY_BALANCED");
	testWriter.setOutputDigitalRange(20);
	reinit();
	start();

	write(10);
	process();
	read();
	verify(10);
	passed();

	write(9);
	process();
	read();
	verify(7);
	passed();

	write(12);
	process();
	read();
	verify(16);
	passed();

	write(7);
	process();
	read();
	verify(1);
	passed();


	passed("MULTIPLY_BALANCED");

	/* ----------------DIVIDE------------------*/

	vt.function.setSelectedItemThrow("DIVIDE");
	testWriter.setOutputDigitalRange(10);
	reinit();
	start();

	write(5);
	process();
	read();
	verify(1);
	passed();

	write(6);
	process();
	read();
	verify(2);
	passed();

	write(-7);
	process();
	read();
	verify(-2);
	passed();

	write(10);
	process();
	read();
	verify(3);
	passed();

	passed("DIVIDE");


	/* ----------------DIVIDE_BALANCED------------------*/

	testWriter.setOutputDigitalRange(10);
	vt.function.setSelectedItemThrow("DIVIDE_BALANCED");
	reinit();
	start();

	write(5);
	process();
	read();
	verify(5);
	passed();

	write(6);
	process();
	read();
	verify(5);
	passed();

	write(8);
	process();
	read();
	verify(6);
	passed();

	write(4);
	process();
	read();
	verify(5);
	passed();

	write(1);
	process();
	read();
	verify(4);
	passed();

	write(5);
	process();
	read();
	verify(5);
	passed();

	passed("DIVIDE_BALANCED");

	if (functionsCounter != vt.function.getItems().length)
		throw new Exception("Tested " + functionsCounter + " out of " + vt.function.getItems().length);
}
public void runCounter() throws Exception {
	Counter vt = new Counter();
	newTestElement(vt);
	connect();

//	setDebug();

	/* ----------------SAMPLES------------------*/

	vt.function.setSelectedItemThrow("SAMPLES");
	reinit();
	start();

	int n = (int)(Math.random() * 100.0);
	for (int i = 0; i < n; i++){
		write(i);
	}

	process();

	read();
	verify(n);
	passed("SAMPLES");

	/* ----------------INVOCATION------------------*/

	vt.function.setSelectedItemThrow("INVOCATION");
	reinit();
	start();

	n = (int)(Math.random() * 100.0);
	for (int i = 0; i < n; i++){
		process();
	}

	readLast();
	verify(n);
	passed("INVOCATION");

	if (functionsCounter != vt.function.getItems().length)
		throw new Exception("Tested " + functionsCounter + " out of " + vt.function.getItems().length);

}
public void runDecimator() throws Exception {
	Decimator vt = new Decimator();
	newTestElement(vt);
	connect();

//	setDebug();

	/* ----------------Decimator------------------*/

	vt.sampleOn = 1;
	vt.sampleOff = 3;
	reinit();
	start();

	for (int i = 1; i < 10; i++){
		write(i);
	}

	process();

	read();
	verify(1);
	read();
	verify(5);
	read();
	verify(9);

	passed();

	vt.sampleOn = 2;
	vt.sampleOff = 0;
	reinit();
	start();

	for (int i = 1; i < 10; i++){
		write(i);
	}

	process();

	for (int i = 1; i < 10; i++){
		read();
		verify(i);
	}

	passed();

	passed("Decimator");

}
public void runFileStorage() throws Exception {
	FileStorageWriter wr = new FileStorageWriter();
	newTestElement(wr);
	connect();

//	setDebug();

	/* ----------------FileStorage Integrity------------------*/

	File f = new File("justtesting");
	wr.filePath.path = f.getPath();
	wr.makeUniqueNames = false;
	wr.timeMarkerMillis = 1;
	reinit();
	start();

	int t[] = new int[1000];
	for (int i = 0; i < t.length; i++){
		t[i] = (int)(Math.random() * 30000.0);
		write(t[i]);
		process();
	}

	wr.stop();

	FileStorageSource src = new FileStorageSource();
	newTestElement(src);
	connect();

	src.filePath = wr.filePath;
	src.readChunk = 1;

	reinit();
	start();

	int tout[] = new int[t.length];
	int n = 0;
	while (src.isActive() && n < tout.length) {
		process();
		tout[n++] = read();
	}

	// now check integrity
	for (int i = 0; i < t.length; i++){
		if (t[i] != tout[i]) {
			throw new Exception("Integrity failed");
		}
	}

	f.delete();
	passed("FileStorage integrity");
}
public void runFormatter() throws Exception {
	bioera.processing.impl.Formatter fc = new bioera.processing.impl.Formatter();
	newTestElement(fc);
	connect();

//	setDebug();

	/* ----------------FileStorage Integrity------------------*/

	testWriter.setOutputDigitalRange(30000);

	int t[] = new int[100];
	for (int i = 0; i < t.length; i++){
		t[i] = (int)(Math.random() * 30000.0);
	}

	for (int k = 0; k < fc.function.getItems().length; k++){
		String item = fc.function.getItem(k);
		if (!item.startsWith("TO "))
			continue;

		fc.function.setSelectedItemThrow(item);
		reinit();
		start();

//		System.out.println("function: " + item);

		for (int i = 0; i < t.length; i++){
//			System.out.println("writing " + t[i]);
			write(t[i]);
			process();
		}

		passed(item);

		item = "FROM" + item.substring("TO".length());
		fc.function.setSelectedItemThrow(item);
		reinit();
		start();

		while (available() > 0) {
			int b = read();
//			System.out.println("(" + b + ")");
			write(b);
		}

		process();

		for (int i = 0; i < t.length; i++){
			if (t[i] != read()) {
				throw new Exception("Failed function '" + item + "', t[" + i + "]=" +  read1 + " should be " + t[i]);
			}
		}

		passed(item);
	}

	if (functionsCounter != fc.function.getItems().length)
		throw new Exception("Tested " + functionsCounter + " out of " + fc.function.getItems().length);

	passed("FormatConverter integrity");
}
public void runMixer() throws Exception {
	Mixer vt = new Mixer();
	newTestElement(vt);
	connect();

//	setDebug();

	/* ----------------SUM------------------*/
		//"SUM",
		//"BALANCED_SUM",
		//"DIFFERENCE",
		//"BALANCED_DIFFERENCE",
		//"MULTIPLICATION",
		//"BALANCED_MULTIPLICATION",
		//"AVERAGE",
		//"MAX",
		//"MIN",
		//"ONLY_INPUT_A",
		//"ONLY_INPUT_B",
		//"DIVISION",
		//"BALANCED_DIVISION",
		//"ABS DIFFERENCE",

	vt.function.setSelectedItemThrow("SUM");
	reinit();
	start();

	write1(1);
	write2(2);
	process();
	read();
	verify(3);
	passed();

	write1(3);
	write2(4);
	process();
	read();
	verify(7);
	passed();

	write1(1);
	process();
	write2(2);
	process();
	read();
	verify(3);
	passed();


	passed("SUM");

	/* ----------------DIFFERENCE------------------*/
	vt.function.setSelectedItemThrow("DIFFERENCE");
	reinit();
	start();

	write1(4);
	write2(2);
	process();
	read();
	verify(2);
	passed();

	write1(3);
	write2(4);
	process();
	read();
	verify(-1);
	passed();

	write1(1);
	process();
	write2(2);
	process();
	read();
	verify(-1);
	passed();


	passed("DIFFERENCE");

	/* ----------------MULTIPLICATION------------------*/

	vt.function.setSelectedItemThrow("MULTIPLICATION");
	reinit();
	start();

	write1(1);
	process();
	write2(2);
	process();
	read();
	verify(2);
	passed();

	write1(3);
	process();
	write2(2);
	process();
	read();
	verify(6);
	passed();

	write1(-3);
	process();
	write2(-5);
	process();
	read();
	verify(15);
	passed();

	passed("MULTIPLICATION");

	/* ----------------AVERAGE------------------*/
	vt.function.setSelectedItemThrow("AVERAGE");
	reinit();
	start();

	write1(1);
	write2(2);
	process();
	read();
	verify(1);
	passed();

	write1(4);
	write2(2);
	process();
	read();
	verify(3);
	passed();


	passed("AVERAGE");

	/* ----------------MAX------------------*/

	vt.function.setSelectedItemThrow("MAX");
	reinit();
	start();

	write1(1);
	write2(2);
	process();
	read();
	verify(2);
	passed();


	write1(8);
	write2(2);
	process();
	read();
	verify(8);
	passed();

	write1(-1);
	write2(-2);
	process();
	read();
	verify(-1);
	passed();

	passed("MAX");

	/* ----------------MIN------------------*/

	vt.function.setSelectedItemThrow("MIN");
	reinit();
	start();

	write1(1);
	write2(2);
	process();
	read();
	verify(1);
	passed();


	write1(8);
	write2(2);
	process();
	read();
	verify(2);
	passed();

	write1(-1);
	write2(-2);
	process();
	read();
	verify(-2);
	passed();

	passed("MIN");


	/* ----------------BALANCED_SUM------------------*/

	testWriter.setOutputDigitalRange(10);
	vt.function.setSelectedItemThrow("BALANCED_SUM");
	reinit();
	start();

	write1(5);
	write2(5);
	process();
	read();
	verify(5);
	passed();

	write1(6);
	write2(6);
	process();
	read();
	verify(7);
	passed();

	write1(6);
	write2(3);
	process();
	read();
	verify(4);
	passed();


	passed("BALANCED_SUM");


	/* ----------------BALANCED_DIFFERENCE------------------*/

	testWriter.setOutputDigitalRange(10);
	vt.function.setSelectedItemThrow("BALANCED_DIFFERENCE");
	reinit();
	start();

	write1(5);
	write2(5);
	process();
	read();
	verify(5);
	passed();

	write1(6);
	write2(6);
	process();
	read();
	verify(5);
	passed();

	write1(6);
	write2(3);
	process();
	read();
	verify(8);
	passed();


	passed("BALANCED_DIFFERENCE");


	/* ----------------BALANCED_MULTIPLICATION------------------*/

	testWriter.setOutputDigitalRange(10);
	vt.function.setSelectedItemThrow("BALANCED_MULTIPLICATION");
	reinit();
	start();

	write1(5);
	write2(5);
	process();
	read();
	verify(5);
	passed();

	write1(6);
	write2(6);
	process();
	read();
	verify(6);
	passed();

	write1(6);
	write2(3);
	process();
	read();
	verify(3);
	passed();

	write1(7);
	write2(7);
	process();
	read();
	verify(9);
	passed();


	passed("BALANCED_MULTIPLICATION");

	/* ----------------ONLY_INPUT_A------------------*/

	testWriter.setOutputDigitalRange(10);
	vt.function.setSelectedItemThrow("ONLY_INPUT_A");
	reinit();
	start();

	write1(5);
	write2(7);
	process();
	read();
	verify(5);
	passed();

	write1(6);
	write2(7);
	process();
	read();
	verify(6);
	passed();

	write1(6);
	write2(3);
	process();
	read();
	verify(6);
	passed();

	write1(7);
	write2(7);
	process();
	read();
	verify(7);
	passed();


	passed("ONLY_INPUT_A");

	/* ----------------ONLY_INPUT_B------------------*/

	testWriter.setOutputDigitalRange(10);
	vt.function.setSelectedItemThrow("ONLY_INPUT_B");
	reinit();
	start();

	write1(5);
	write2(7);
	process();
	read();
	verify(7);
	passed();

	write1(6);
	write2(7);
	process();
	read();
	verify(7);
	passed();

	write1(6);
	write2(3);
	process();
	read();
	verify(3);
	passed();

	write1(7);
	write2(7);
	process();
	read();
	verify(7);
	passed();

	passed("ONLY_INPUT_B");


	/* ----------------DIVISION------------------*/

	testWriter.setOutputDigitalRange(10);
	vt.function.setSelectedItemThrow("DIVISION");
	reinit();
	start();

	write1(5);
	write2(2);
	process();
	read();
	verify(2);
	passed();

	write1(6);
	write2(3);
	process();
	read();
	verify(2);
	passed();

	write1(6);
	write2(-3);
	process();
	read();
	verify(-2);
	passed();

	write1(10);
	write2(5);
	process();
	read();
	verify(2);
	passed();

	passed("DIVISION");


	/* ----------------BALANCED_DIVISION------------------*/

	testWriter.setOutputDigitalRange(10);
	vt.function.setSelectedItemThrow("BALANCED_DIVISION");
	reinit();
	start();

	write1(5);
	write2(2);
	process();
	read();
	verify(5);
	passed();

	write1(6);
	write2(3);
	process();
	read();
	verify(5);
	passed();

	write1(7);
	write2(4);
	process();
	read();
	verify(3);
	passed();

	write1(8);
	write2(5);
	process();
	read();
	verify(9);
	passed();

	write1(3);
	write2(5);
	process();
	read();
	verify(0);
	passed();

	write1(5);
	write2(5);
	process();
	read();
	verify(5);
	passed();

	passed("BALANCED_DIVISION");

	/* ----------------ABS DIFFERENCE------------------*/
	vt.function.setSelectedItemThrow("ABS DIFFERENCE");
	reinit();
	start();

	write1(2);
	write2(4);
	process();
	read();
	verify(2);
	passed();

	write1(3);
	write2(4);
	process();
	read();
	verify(1);
	passed();

	write1(7);
	process();
	write2(4);
	process();
	read();
	verify(3);
	passed();

	passed("ABS DIFFERENCE");

	/* ----------------ABS BALANCED DIFFERENCE------------------*/
	vt.function.setSelectedItemThrow("ABS BALANCED DIFFERENCE");
	reinit();
	start();

	write1(4);
	write2(2);
	process();
	read();
	verify(7);
	passed();

	write1(3);
	write2(4);
	process();
	read();
	verify(6);
	passed();

	write1(7);
	process();
	write2(6);
	process();
	read();
	verify(6);
	passed();

	passed("ABS BALANCED DIFFERENCE");

	if (functionsCounter != vt.function.getItems().length)
		throw new Exception("Tested " + functionsCounter + " out of " + vt.function.getItems().length);
}
public void runRangeMapper() throws Exception {
	RangeMapper vt = new RangeMapper();
	newTestElement(vt);
	connect();

//	setDebug();

	vt.srcFrom.value = 2;
	vt.srcFrom.scale.setSelectedItem("digi");
	vt.srcTo.value = 10;
	vt.srcTo.scale.setSelectedItem("digi");
	vt.destFrom.value = 11;
	vt.destFrom.scale.setSelectedItem("digi");
	vt.destTo.value = 15;
	vt.destTo.scale.setSelectedItem("digi");
	reinit();
	start();

	write(1);
	write(2);
	process();
	read();
	verify(11);
	read();
	verify(11);
	passed();

	write(4);
	process();
	read();
	verify(12);
	passed();

	write(6);
	write(7);
	write(8);
	process();
	read();
	verify(13);
	read();
	verify(13);
	read();
	verify(14);
	passed();

	write(10);
	process();
	read();
	verify(15);
	passed();

	write(11);
	process();
	read();
	verify(15);
	passed();

	write(9);
	process();
	read();
	verify(14);
	passed();

	passed("OK");

}
public void runRawFile() throws Exception {
	RawFileWriter wr = new RawFileWriter();
	newTestElement(wr);
	connect();

//	setDebug();

	/* ----------------FileStorage Integrity------------------*/

	File f = new File("justtesting");
	wr.filePath.path = f.getPath();
	reinit();
	start();

	int t[] = new int[70000];
	for (int i = 0; i < t.length; i++){
		t[i] = (int)(Math.random() * 220.0);
		write(t[i]);
		process();
	}

	wr.stop();

	RawFileReader src = new RawFileReader();
	newTestElement(src);
	connect();

	src.filePath = wr.filePath;

	reinit();
	start();

	int tout[] = new int[t.length];
	int n = 0;
	while (src.isActive() && n < tout.length) {
		process();
		tout[n++] = read();
	}

	// now check integrity
	for (int i = 0; i < t.length; i++){
		if (t[i] != tout[i]) {
			throw new Exception("Integrity failed");
		}
	}

	f.delete();
	passed("RawFile integrity OK");
}
public void runVectorCounter() throws Exception {
	VectorCounter vt = new VectorCounter();
	newTestElement(vt);
	connect();

//	setDebug();

	/* ----------------SAMPLES------------------*/

	vt.function.setSelectedItemThrow("SAMPLES");
	setInVLen(2);
	reinit();
	start();

	int n = (int)(Math.random() * 100.0);
	for (int i = 0; i < n; i++){
		write(i, i+1);
	}

	process();

	read();
	verify(n);
	passed("SAMPLES");

	if (functionsCounter != vt.function.getItems().length)
		throw new Exception("Tested " + functionsCounter + " out of " + vt.function.getItems().length);

}
public void runVectorMixer() throws Exception {
	VectorMixer vt = new VectorMixer();
	newTestElement(vt);
	setVLen(2);
	connect();

//	setDebug();

	/* ----------------SUM------------------*/
		//"DIFFERENCE",
		//"ABS_DIFFERENCE",
		//"MULTIPLICATION",
		//"AVERAGE",
		//"MAX",
		//"MIN",

	vt.function.setSelectedItemThrow("SUM");
	reinit();
	start();

	write1(1, 1);
	write2(2, 3);
	process();
	read();
	verify(3, 4);
	passed();

	write1(3, 8);
	write2(4, 2);
	process();
	read();
	verify(7, 10);
	passed();

	write1(1, 2);
	process();
	write2(2, 3);
	process();
	read();
	verify(3, 5);
	passed();


	passed("SUM");

	/* ----------------DIFFERENCE------------------*/
	vt.function.setSelectedItemThrow("DIFFERENCE");
	reinit();
	start();

	write1(4, 5);
	write2(2, 3);
	process();
	read();
	verify(2, 2);
	passed();

	write1(3, 8);
	write2(4, 2);
	process();
	read();
	verify(-1, 6);
	passed();

	write1(1, 2);
	process();
	write2(2, 3);
	process();
	read();
	verify(-1, -1);
	passed();


	passed("DIFFERENCE");


		/* ----------------ABS_DIFFERENCE------------------*/
	vt.function.setSelectedItemThrow("ABS_DIFFERENCE");
	vt.setOutputDigitalRange(10);
	reinit();
	start();

	write1(4, 3);
	write2(2, 5);
	process();
	read();
	verify(2, 2);
	passed();

	write1(3, 8);
	write2(4, 2);
	process();
	read();
	verify(1, 6);
	passed();

	write1(1, 2);
	process();
	write2(2, 3);
	process();
	read();
	verify(1, 1);
	passed();

	passed("ABS_DIFFERENCE");

	/* ----------------MULTIPLICATION------------------*/

	vt.function.setSelectedItemThrow("MULTIPLICATION");
	reinit();
	start();

	write1(1, 1);
	process();
	write2(2, 3);
	process();
	read();
	verify(2, 3);
	passed();

	write1(3, 6);
	process();
	write2(2, 4);
	process();
	read();
	verify(6, 24);
	passed();

	write1(-3, -2);
	process();
	write2(-5, 3);
	process();
	read();
	verify(15, -6);
	passed();

	passed("MULTIPLICATION");

	/* ----------------AVERAGE------------------*/
	vt.function.setSelectedItemThrow("AVERAGE");
	reinit();
	start();

	write1(1, 1);
	write2(2, 3);
	process();
	read();
	verify(1, 2);
	passed();

	write1(4, 6);
	write2(2, 3);
	process();
	read();
	verify(3, 4);
	passed();


	passed("AVERAGE");

	/* ----------------MAX------------------*/

	vt.function.setSelectedItemThrow("MAX");
	reinit();
	start();

	write1(1, 1);
	write2(2, 3);
	process();
	read();
	verify(2, 3);
	passed();


	write1(8, -1);
	write2(2, 3);
	process();
	read();
	verify(8, 3);
	passed();

	write1(-1, -8);
	write2(-2, -3);
	process();
	read();
	verify(-1, -3);
	passed();

	passed("MAX");

	/* ----------------MIN------------------*/

	vt.function.setSelectedItemThrow("MIN");
	reinit();
	start();

	write1(1, 1);
	write2(2, 3);
	process();
	read();
	verify(1, 1);
	passed();


	write1(8, -1);
	write2(2, 3);
	process();
	read();
	verify(2, -1);
	passed();

	write1(-1, -8);
	write2(-2, -3);
	process();
	read();
	verify(-2, -8);
	passed();

	passed("MIN");

	if (functionsCounter != vt.function.getItems().length)
		throw new Exception("Tested " + functionsCounter + " out of " + vt.function.getItems().length);
}
public void runVectorToScalar() throws Exception {
	VectorToScalar vt = new VectorToScalar();
	newTestElement(vt);

		//"AVERAGE",
		//"MAX",
		//"MIN",
		//"MIDDLE_POWER",
		//"MIDDLE_POWER_2",
		//"SUM",
		//"RMS"
		//"DOMINANT_INDEX",
		//"DIGITAL_AVERAGE",
		//"DIGITAL_RMS"



//	setDebug();


	// ----------------MIDDLE_POWER------------------

	vt.function.setSelectedItemThrow("MIDDLE_POWER");
	setVLen(5);
	connect();
	reinit();
	start();

	write(new int[]{1, 2, 3, 2, 1});
	process();
	read();
	verify(2);
	passed();

	write(new int[]{1, 1, 1, 1, 1});
	process();
	read();
	verify(2);
	passed();

	write(new int[]{1, 2, 3, 4, 5});
	process();
	read();
	verify(3);
	passed();

	passed("MIDDLE_POWER");


	// ----------------MIDDLE_POWER------------------

	vt.function.setSelectedItemThrow("MIDDLE_POWER_2");
	setVLen(5);
	reinit();
	start();

	write(new int[]{6, 1, 1, 1, 5});
	process();
	read();
	verify(1);
	passed();

	write(new int[]{1, 2, 3, 2, 1});
	process();
	read();
	verify(2);
	passed();

	passed("MIDDLE_POWER_2");

	// ----------------AVERAGE------------------

	vt.function.setSelectedItemThrow("AVERAGE");
	setVLen(2);
	reinit();
	start();

	write(4, 4);
	write(6, 7);
	write(3, 1);
	process();
	read();
	verify(4);
	read();
	verify(6);
	read();
	verify(2);
	passed();

	write(1, 1);
	process();
	read();
	verify(1);
	passed();

	write(-2, -4);
	process();
	read();
	verify(-3);
	passed();

	write(-4, 3);
	process();
	read();
	verify(0);
	passed();

	passed("AVERAGE");

	// ----------------MAX------------------

	vt.function.setSelectedItemThrow("MAX");
	setVLen(2);
	reinit();
	start();

	write(1, 1);
	process();
	read();
	verify(1);
	passed();

	write(2, 4);
	process();
	read();
	verify(4);
	passed();

	write(-3, 3);
	process();
	read();
	verify(3);
	passed();

	write(4, 7);
	write(5, 6);
	process();
	read();
	read();
	verify(6);
	passed();

	passed("MAX");


	// ----------------MIN------------------

	vt.function.setSelectedItemThrow("MIN");
	setVLen(2);
	reinit();
	start();

	write(1, 1);
	process();
	read();
	verify(1);
	passed();

	write(2, -3);
	process();
	read();
	verify(-3);
	passed();

	write(-3, -4);
	process();
	read();
	verify(-4);
	passed();

	write(4, 7);
	write(5, 6);
	process();
	read();
	read();
	verify(5);
	passed();

	passed("MIN");

	// ----------------SUM------------------

	vt.function.setSelectedItemThrow("SUM");
	setInVLen(2);
	reinit();
	start();

	write(1, 2);
	process();
	read();
	verify(3);
	passed();

	write(2, -3);
	process();
	read();
	verify(-1);
	passed();

	write(-3, -4);
	process();
	read();
	verify(-7);
	passed();

	write(4, 7);
	write(5, 6);
	process();
	read();
	read();
	verify(11);
	passed();

	passed("SUM");

	// ----------------RMS------------------

	vt.function.setSelectedItemThrow("RMS");
	setInVLen(2);
	reinit();
	start();

	write(1, 2);
	process();
	read();
	verify(1);
	passed();

	write(2, -3);
	process();
	read();
	verify(2);
	passed();

	write(-3, -4);
	process();
	read();
	verify(3);
	passed();

	write(4, 7);
	write(1, 6);
	process();
	read();
	read();
	verify(4);
	passed();

	passed("RMS");

	// ----------------RS------------------

	vt.function.setSelectedItemThrow("RS");
	setInVLen(2);
	reinit();
	start();

	write(1, 2);
	process();
	read();
	verify(2);
	passed();

	write(2, -3);
	process();
	read();
	verify(3);
	passed();

	write(-3, -4);
	process();
	read();
	verify(5);
	passed();

	write(4, 7);
	write(1, 6);
	process();
	read();
	read();
	verify(6);
	passed();

	passed("RS");


	// ----------------DOMINANT_INDEX------------------

	vt.function.setSelectedItemThrow("DOMINANT_INDEX");
	setInVLen(3);
	reinit();
	start();

	write(1, 2, 3);
	process();
	read();
	verify(2);
	passed();

	write(2, -3, 1);
	process();
	read();
	verify(0);
	passed();

	write(-3, -4, 1);
	process();
	read();
	verify(2);
	passed();

	write(4, 7, 1);
	write(1, 6, 3);
	process();
	read();
	read();
	verify(1);
	passed();

	passed("DOMINANT_INDEX");


	// ----------------DIGITAL_AVERAGE------------------

	vt.function.setSelectedItemThrow("DIGITAL_AVERAGE");
	setInVLen(2);
	reinit();
	start();

	write(4, 4);
	write(6, 7);
	write(3, 1);
	process();
	read();
	verify(4);
	read();
	verify(6);
	read();
	verify(2);
	passed();

	write(1, 1);
	process();
	read();
	verify(1);
	passed();

	write(-2, -4);
	process();
	read();
	verify(-3);
	passed();

	write(-4, 3);
	process();
	read();
	verify(0);
	passed();

	passed("DIGITAL_AVERAGE");

	// ----------------DIGITAL_RMS------------------

	vt.function.setSelectedItemThrow("DIGITAL_RMS");
	setInVLen(2);
	reinit();
	start();

	write(1, 2);
	process();
	read();
	verify(1);
	passed();

	write(2, -3);
	process();
	read();
	verify(2);
	passed();

	write(-3, -4);
	process();
	read();
	verify(3);
	passed();

	write(4, 7);
	write(1, 6);
	process();
	read();
	read();
	verify(4);
	passed();

	passed("DIGITAL_RMS");


	if (functionsCounter != vt.function.getItems().length)
		throw new Exception("Tested " + functionsCounter + " out of " + vt.function.getItems().length);

}
public void set(String field, int value) throws Exception {
	Field f = tE.getClass().getField(field);
	try {
		f.setInt(tE, value);
	} catch (Exception e) {
		throw new Exception("Property set error: " + e);
	}
}
public void set(String field, String value) throws Exception {
	ElementProperty ep = new ElementProperty();
	ep.name = field;
	ep.value = value;
	try {
		ProcessingTools.setElementProperties(tE, new ElementProperty[]{ep});
	} catch (Exception e) {
		throw new Exception("Property set error: " + e);
	}
}
public void setDebug() throws Exception {
	tE.setDebug(true);
}
public void setInVLen(int len) throws Exception {
	testWriter.setOutputVectorLength(len);
	//for (int i = 0; i < tE.getInputsCount(); i++){
		//if (tE.getInput(i).getType() == VectorPipe.TYPE) {
			//VectorPipe in = (VectorPipe) tE.getInput(i);
////			in.getVectorParameters().setVectorLength(len);
			////in.getVectorParameters().setPhysicalMax(100);
			////in.getVectorParameters().setPhysicalMin(0);
////			((VectorPipe) testWriter.getOutput(0)).getVectorParameters().setVectorLength(len);
		//} else {
			//System.out.println("Input not a vector");
		//}
	//}
}
public void setVLen(int len) throws Exception {
	testWriter.setOutputVectorLength(len);
	testWriter.getSignalParameters().setVectorMax(len);
	testWriter.getSignalParameters().setVectorMin(0);
	tE.setOutputVectorLength(len);
	tE.getSignalParameters().setVectorMax(len);
	tE.getSignalParameters().setVectorMin(0);

	//setInVLen(len);

	//for (int i = 0; i < tE.getOutputsCount(); i++){
		//if (tE.getOutput(i).getType() == VectorPipe.TYPE) {
			//VectorPipe out = (VectorPipe) tE.getOutput(i);
			//out.getElement().setOutputVectorLength(len);
			//out.getSignalParameters().setVectorMax(100);
			//out.getSignalParameters().setVectorMin(0);
		//} else {
			//System.out.println("Output not a vector");
		//}
	//}

}
public void showInfo(String s) throws Exception {
	if (showInfo)
		System.out.println(s);
}
public void start() throws Exception {
	tE.purgeInputBuffers();
	tE.start();
}
public void testAll() throws Exception {
	Method m[] = getClass().getMethods();
	int n = 0, rN = 0;
	for (int i = 0; i < m.length; i++){
		String name = m[i].getName();
		if (name.startsWith("run")) {
			rN++;
			try {
				m[i].invoke(this, new Object[]{});
			} catch (Exception e) {
				System.out.println("Test failed for " + name.substring(3) + "  (" + e + ")");
				e.printStackTrace();
				n++;
			}
		}
	}

	System.out.println("");
	if (n == 0)
		System.out.println("OK. All " + rN + " tests passed.");
	else
		System.out.println("" + n + " test(s) failed");
}
public void verify(int v1) throws Exception {
	if (v1 != read1)
		throw new Exception("Read verification failed: is(1) " + read1 + " should be " + v1);
}
public void verify(int v1, int v2) throws Exception {
	if (v1 != read1)
		throw new Exception("Read verification failed: is(1) " + read1 + " should be " + v1);
	if (v2 != read2)
		throw new Exception("Read verification failed: is(2) " + read2 + " should be " + v2);

}
public void verify(int v1, int v2, int v3) throws Exception {
	if (v1 != read1)
		throw new Exception("Read verification 1 failed: is " + read1 + " should be " + v1);
	if (v2 != read2)
		throw new Exception("Read verification 2 failed: is " + read2 + " should be " + v2);
	if (v3 != read3)
		throw new Exception("Read verification 3 failed: is " + read3 + " should be " + v3);

}
public void write(int v1[]) throws Exception {
	if (testWriter.getOutput(0).getType() == VectorPipeDistributor.TYPE) {
		((VectorPipe)testWriter.getOutput(0)).writeVector(v1);
	} else {
		throw new Exception("wrong type");
	}

}
public void write(int v1) throws Exception {
	write1(v1);
}
public void write(int v1, int v2) throws Exception {
	write1(v1, v2);
}
public void write(int v1, int v2, int v3) throws Exception {
	write1(v1, v2, v3);
}
public void write1(int v1) throws Exception {
	if (testWriter.getOutput(0).getType() == VectorPipeDistributor.TYPE) {
		((VectorPipe)testWriter.getOutput(0)).writeVector(new int[]{v1});
	} else {
		((ScalarPipe)testWriter.getOutput(0)).write(v1);
	}
}
public void write1(int v1, int v2) throws Exception {
	if (testWriter.getOutput(0).getType() == VectorPipeDistributor.TYPE) {
		((VectorPipe)testWriter.getOutput(0)).writeVector(new int[]{v1, v2});
	} else {
		((ScalarPipe)testWriter.getOutput(0)).write(v1);
		((ScalarPipe)testWriter.getOutput(0)).write(v2);
	}
}
public void write1(int v1, int v2, int v3) throws Exception {
	if (testWriter.getOutput(0).getType() == VectorPipeDistributor.TYPE) {
		((VectorPipe)testWriter.getOutput(0)).writeVector(new int[]{v1, v2, v3});
	} else {
		System.out.println("inappropriete type");
	}
}
public void write2(int v1) throws Exception {
	if (testWriter.getOutput(1).getType() == VectorPipeDistributor.TYPE) {
		((VectorPipe)testWriter.getOutput(1)).writeVector(new int[]{v1});
	} else {
		((ScalarPipe)testWriter.getOutput(1)).write(v1);
	}
}
public void write2(int v1, int v2) throws Exception {
	if (testWriter.getOutput(1).getType() == VectorPipeDistributor.TYPE) {
		((VectorPipe)testWriter.getOutput(1)).writeVector(new int[]{v1, v2});
	} else {
		System.out.println("inappropriete type");
	}
}
public void write2(int v1, int v2, int v3) throws Exception {
	if (testWriter.getOutput(1).getType() == VectorPipeDistributor.TYPE) {
		((VectorPipe)testWriter.getOutput(1)).writeVector(new int[]{v1, v2, v3});
	} else {
		System.out.println("inappropriete type");
	}
}

public void runScalarInstantTransform() throws Exception {
	ScalarInstantTransform st = new ScalarInstantTransform();
	((BufferedScalarPipe)st.getInput(0)).setBufferSize(30);
	newTestElement(st);
	connect();

//	setDebug();
		//"INVERTER",
		// BALANCED_INVERTER
		//"DIFFERENCE",
		//"ABS",
		//"BALANCED_ABS",
		//"WITHIN DEFAULT RANGE",


	// ----------------ABS------------------

	st.function.setSelectedItemThrow("ABS");
	reinit();
	start();

	write(3);
	write(-2);
	write(-4);
	process();
	read();
	verify(3);
	read();
	verify(2);
	read();
	verify(4);
	passed();

	write(-1);
	process();
	read();
	verify(1);
	write(1);
	write(1);
	process();
	read();
	verify(1);
	read();
	verify(1);
	passed();

	passed("ABS");


	// ----------------ABS BALANCED------------------

	st.function.setSelectedItemThrow("ABS BALANCED");
	testWriter.setOutputDigitalRange(10);
	reinit();
	start();

	write(3);
	write(8);
	write(9);
	process();
	read();
	verify(7);
	read();
	verify(8);
	read();
	verify(9);
	passed();

	write(1);
	process();
	read();
	verify(9);
	write(2);
	write(3);
	process();
	read();
	verify(8);
	read();
	verify(7);
	passed();

	passed("ABS BALANCED");

	// ----------------INVERTER------------------

	st.function.setSelectedItemThrow("INVERTER");
	reinit();
	start();

	write(3);
	write(8);
	write(-9);
	process();
	read();
	verify(-3);
	read();
	verify(-8);
	read();
	verify(9);
	passed();

	write(1);
	process();
	read();
	verify(-1);
	write(2);
	write(3);
	process();
	read();
	verify(-2);
	read();
	verify(-3);
	passed();

	passed("INVERTER");


	// ----------------INVERTER BALANCED------------------

	st.function.setSelectedItemThrow("INVERTER BALANCED");
	testWriter.setOutputDigitalRange(10);
	reinit();
	start();

	write(3);
	write(8);
	write(2);
	process();
	read();
	verify(7);
	read();
	verify(2);
	read();
	verify(8);
	passed();

	write(1);
	process();
	read();
	verify(9);
	write(2);
	write(3);
	process();
	read();
	verify(8);
	read();
	verify(7);
	passed();

	passed("INVERTER BALANCED");

	// ----------------BACK DIFFERENCE------------------

	st.function.setSelectedItemThrow("BACK DIFFERENCE");
	reinit();
	start();

	write(3);
	write(8);
	write(-9);
	process();
	read();
	verify(3);
	read();
	verify(5);
	read();
	verify(-17);
	passed();

	write(1);
	process();
	read();
	verify(10);
	write(2);
	write(3);
	process();
	read();
	verify(1);
	read();
	verify(1);
	passed();

	passed("BACK DIFFERENCE");

	// ----------------WITHIN DEFAULT RANGE------------------

	st.function.setSelectedItemThrow("WITHIN DEFAULT RANGE");
	testWriter.setOutputDigitalRange(10);
	reinit();
	start();

	write(3);
	write(8);
	write(-9);
	process();
	read();
	verify(3);
	read();
	verify(8);
	read();
	verify(0);
	passed();

	write(21);
	process();
	read();
	verify(9);
	write(-2);
	write(3);
	process();
	read();
	verify(0);
	read();
	verify(3);
	passed();

	passed("WITHIN DEFAULT RANGE");

	if (functionsCounter != st.function.getItems().length)
		throw new Exception("Tested " + functionsCounter + " out of " + st.function.getItems().length);
}

public void runScalarTimeTransform() throws Exception {
	ScalarTimeTransform st = new ScalarTimeTransform();
	((BufferedScalarPipe)st.getInput(0)).setBufferSize(30);
	newTestElement(st);
	connect();

//	setDebug();

	/* ----------------AVERAGE------------------*/

	st.inputLength = 2;
	st.function.setSelectedItemThrow("AVERAGE");
	reinit();
	start();

	write(1);
	write(3);
	process();
	read();
	verify(1);
	read();
	verify(2);
	passed();

	write(2);
	process();
	read();
	verify(2);
	passed();

	write(3);
	process();
	read();
	verify(2);
	passed();

	write(4);
	write(7);
	process();
	read();
	read();
	verify(5);
	passed();

	passed("AVERAGE");

	// ----------------MAX------------------

	st.function.setSelectedItemThrow("MAX");
	reinit();
	start();

	write(1);
	process();

	read();
	verify(1);
	passed();

	write(3);
	process();
	read();
	verify(3);
	passed();

	write(2);
	process();
	read();
	verify(3);
	passed();

	write(2);
	process();
	read();
	verify(2);
	passed();

	write(4);
	write(5);
	process();
	read();
	read();
	verify(5);
	passed();

	passed("MAX");


	// ----------------MIN------------------

	st.function.setSelectedItemThrow("MIN");
	reinit();
	start();

	write(4);
	write(5);
	process();
	read();
	read();
	verify(4);
	passed();

	write(1);
	process();
	read();
	verify(1);
	passed();

	write(2);
	process();
	read();
	verify(1);
	passed();

	write(3);
	process();
	read();
	verify(2);
	passed();

	passed("MIN");

	// ----------------PEAK_TO_PEAK------------------

	st.function.setSelectedItemThrow("PEAK_TO_PEAK");
	reinit();
	start();

	st.inputLength = 3;

	write(3);
	write(8);
	write(11);
	process();
	read();
	verify(0);
	read();
	verify(5);
	read();
	verify(8);
	passed();

	write(1);
	process();
	read();
	verify(10);
	write(1);
	write(1);
	process();
	read();
	verify(10);
	read();
	verify(0);
	passed();

	passed("PEAK_TO_PEAK");

	if (functionsCounter != st.function.getItems().length)
		throw new Exception("Tested " + functionsCounter + " out of " + st.function.getItems().length);

}

public void runVectorInstantTransform() throws Exception {
	VectorInstantTransform vt = new VectorInstantTransform();
	newTestElement(vt);
	setVLen(3);
	connect();

//	setDebug();

	/* ----------------AVERAGE------------------*/

		//"AVERAGE",
		//"MAX",
		//"MIN",

	vt.function.setSelectedItemThrow("AVERAGE");
	vt.leftLength = 0;
	vt.rightLength = 0;
	reinit();
	start();

	write(1, 3, 5);
	process();
	read();
	verify(1, 3, 5);
	passed();

	vt.leftLength = 1;
	vt.rightLength = 1;

	write(2, 4, 4);
	process();
	read();
	verify(3, 3, 4);
	passed();

	passed("AVERAGE");

	/* ----------------MAX------------------*/

	vt.function.setSelectedItemThrow("MAX");
	vt.leftLength = 0;
	vt.rightLength = 0;
	reinit();
	start();

	write(1, 3, 5);
	process();
	read();
	verify(1, 3, 5);
	passed();

	vt.leftLength = 1;
	vt.rightLength = 1;

	write(1, 2, 3);
	process();
	read();
	verify(2, 3, 3);
	passed();

	passed("MAX");

	/* ----------------MIN------------------*/

	vt.function.setSelectedItemThrow("MIN");
	vt.leftLength = 0;
	vt.rightLength = 0;
	reinit();
	start();

	write(1, 3, 5);
	process();
	read();
	verify(1, 3, 5);
	passed();

	vt.leftLength = 1;
	vt.rightLength = 1;

	write(1, 2, 3);
	process();
	read();
	verify(1, 1, 2);
	passed();

	vt.leftLength = 2;
	vt.rightLength = 2;

	write(1, 2, 3);
	process();
	read();
	verify(1, 1, 1);
	passed();

	passed("MIN");

	/* ----------------RMS------------------*/

	vt.function.setSelectedItemThrow("RMS");
	setVLen(2);
	vt.leftLength = 0;
	vt.rightLength = 0;
	reinit();
	start();

	write(1, 3);
	process();
	read();
	verify(1, 3);
	passed();

	vt.leftLength = 1;
	vt.rightLength = 0;

	write(1, 2);
	process();
	read();
	verify(1, 1);
	passed();

	//vt.leftLength = 2;
	//vt.rightLength = 2;

	//write(1, 2, 3);
	//process();
	//read();
	//verify(1, 1, 1);
	//passed();

	passed("RMS");

	if (functionsCounter != vt.function.getItems().length)
		throw new Exception("Tested " + functionsCounter + " out of " + vt.function.getItems().length);
}

public void runVectorTimeTransform() throws Exception {
	VectorTimeTransform vt = new VectorTimeTransform();
	newTestElement(vt);
	setVLen(2);
	connect();

//	setDebug();

	/* ----------------AVERAGE------------------*/

	vt.samplesNo = 3;
	vt.function.setSelectedItemThrow("AVERAGE");
	reinit();
	start();

	write(4, 4);
	write(6, 7);
	write(3, 1);
	process();
	read();
	verify(4, 4);
	read();
	verify(5, 5);
	read();
	verify(4, 4);
	passed();

	vt.samplesNo = 2;
	reinit();
	start();

	write(1, 1);
	process();
	read();
	verify(1, 1);
	passed();

	write(2, 2);
	process();
	read();
	verify(1, 1);
	passed();

	write(3, 3);
	process();
	read();
	verify(2, 2);
	passed();

	passed("AVERAGE");

	/* ----------------MAX------------------*/

	vt.function.setSelectedItemThrow("MAX");
	setVLen(2);
	reinit();
	start();

	write(1, 1);
	process();
	read();
	verify(1, 1);
	passed();

	write(2, 2);
	process();
	read();
	verify(2, 2);
	passed();

	write(3, 3);
	process();
	read();
	verify(3, 3);
	passed();

	write(4, 7);
	write(5, 6);
	process();
	read();
	read();
	verify(5, 7);
	passed();

	passed("MAX");


	/* ----------------MIN------------------*/

	vt.function.setSelectedItemThrow("MIN");
	setVLen(2);
	reinit();
	start();

	write(1, 1);
	process();
	read();
	verify(1, 1);
	passed();

	write(2, 2);
	process();
	read();
	verify(1, 1);
	passed();

	write(3, 3);
	process();
	read();
	verify(2, 2);
	passed();

	write(4, 7);
	write(5, 6);
	process();
	read();
	read();
	verify(4, 6);
	passed();

	passed("MIN");

}
}
