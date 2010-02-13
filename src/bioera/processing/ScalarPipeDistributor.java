/* ScalarPipeDistributor.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.processing;

import java.io.*;
import java.util.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class ScalarPipeDistributor extends ScalarPipe implements PipeDistributor {
	AbstractBufferedScalarPipe pipes[] = new AbstractBufferedScalarPipe[0];
	private int size;

	protected static boolean debug = bioera.Debugger.get("processing.pipe.distributor.linear");
/**
 * PipeDistributor constructor comment.
 */
public ScalarPipeDistributor(Element e) {
	super(e);
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2003 6:44:50 PM)
 */
public int getConnectedCount() {
	return pipes.length;
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2003 6:44:50 PM)
 */
public Pipe getConnectedPipe(int index) {
	return pipes[index];
}
/**
 * Insert the method's description here.
 * Creation date: (7/20/2003 12:49:14 PM)
 */
public boolean isConnected() {
	return size > 0;
}
/**
 * BufferedPipe constructor comment.
 */
public void register(Pipe p) {
	if (debug)
		System.out.println("Registering pipe " + p + " in " + this);

	AbstractBufferedScalarPipe outs1[] = new AbstractBufferedScalarPipe[pipes.length + 1];
	System.arraycopy(pipes, 0, outs1, 0, pipes.length);
	outs1[pipes.length] = (AbstractBufferedScalarPipe) p;
	pipes = outs1;
	size = pipes.length;
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (7/29/2003 3:24:50 PM)
 */
public boolean unregister(Pipe p) {
	if (debug)
		System.out.println("Unregistering " + p + " from " + this);
	for (int i = 0; i < pipes.length; i++){
		if (pipes[i] == p) {
			if (debug)
				System.out.println("Found pipe");
			
			// Unregister this distributor from the receiver pipe
			((BufferedPipe) p).disconnectDistributor(this);

			// Remove pipe from local table
			if (pipes.length == 1) {
				size = 0;
				pipes = new BufferedScalarPipe[0];
			} else {
				BufferedScalarPipe temp[] = new BufferedScalarPipe[pipes.length - 1];
				System.arraycopy(pipes, 0, temp, 0, i);
				System.arraycopy(pipes, i + 1, temp, i, size - i - 1);
				size = temp.length;
				pipes = temp;
			}
			
			return true;
		}
	}

	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (7/29/2003 3:24:50 PM)
 */
public void unregisterAll() {
	for (int i = 0; i < pipes.length; i++){
		// Unregister this distributor from the receiver pipe
		((BufferedPipe) pipes[i]).disconnectDistributor(this);
	}

	size = 0;
	pipes = new BufferedScalarPipe[0];	
}
/**
 * BufferedPipe constructor comment.
 */
public final void write(int t[], int from, int count) {
	for (int i = 0; i < size; i++){
		pipes[i].write(t, from, count);
	}	
}
/**
 * BufferedPipe constructor comment.
 */
public final void write(int b) {
	for (int i = 0; i < size; i++){
		pipes[i].write(b);
	}	
}

/**
 * Insert the method's description here.
 * Creation date: (7/25/2003 4:36:14 PM)
 */
public final int maxAvailableSpace() {
	int ret = Integer.MIN_VALUE;
	for (int i = 0; i < size; i++){
		if (pipes[i].availableSpace() > ret)
			ret = pipes[i].availableSpace();
			
	}		
	return ret;
}

/**
 * Insert the method's description here.
 * Creation date: (7/25/2003 4:36:14 PM)
 */
public final int maxBufferSize() {
	int ret = Integer.MIN_VALUE;
	for (int i = 0; i < size; i++){
		if (pipes[i].getBufferSize() > ret)
			ret = pipes[i].getBufferSize();
			
	}		
	return ret;
}

/**
 * Insert the method's description here.
 * Creation date: (7/25/2003 4:36:14 PM)
 */
public final int maxOccupiedSpace() {
	int ret = Integer.MIN_VALUE;
	for (int i = 0; i < size; i++){
		if (pipes[i].occupiedSpace() > ret)
			ret = pipes[i].occupiedSpace();
			
	}		
	return ret;
}

/**
 * Insert the method's description here.
 * Creation date: (7/25/2003 4:36:14 PM)
 */
public final int minAvailableSpace() {
	int ret = Integer.MAX_VALUE;
	for (int i = 0; i < size; i++){
		if (pipes[i].availableSpace() < ret)
			ret = pipes[i].availableSpace();
			
	}		
	return ret;
}

/**
 * Insert the method's description here.
 * Creation date: (7/25/2003 4:36:14 PM)
 */
public final int minBufferSize() {
	int ret = Integer.MAX_VALUE;
	for (int i = 0; i < size; i++){
		if (pipes[i].getBufferSize() < ret)
			ret = pipes[i].getBufferSize();
			
	}		
	return ret;
}

/**
 * Insert the method's description here.
 * Creation date: (7/25/2003 4:36:14 PM)
 */
public final int minOccupiedSpace() {
	int ret = Integer.MAX_VALUE;
	for (int i = 0; i < size; i++){
		if (pipes[i].occupiedSpace() < ret)
			ret = pipes[i].occupiedSpace();
			
	}		
	return ret;
}

/**
 * BufferedPipe constructor comment.
 */
public final void writeByte(byte b) {
	for (int i = 0; i < size; i++){
		pipes[i].write((b + 128) ^ 128);
	}	
}

/**
 * BufferedPipe constructor comment.
 */
public final void writeBytes(byte t[], int from, int count) {
	for (int i = 0; i < size; i++){
		for (int j = from; j < from + count; j++){
			pipes[i].write((t[j] + 128) ^ 128);
		}		
	}	
}

/**
 * BufferedPipe constructor comment.
 */
public SignalParameters getSignalParameters() {
	return element.getSignalParameters();
}
}
