/* VectorPipeDistributor.java v 1.0.9   11/6/04 7:15 PM
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
public class VectorPipeDistributor extends VectorPipe implements PipeDistributor {
	BufferedVectorPipe pipes[] = new BufferedVectorPipe[0];
	private int size;
	
	protected static boolean debug = bioera.Debugger.get("processing.pipe.distributor.vector");
/**
 * PipeDistributor constructor comment.
 */
public VectorPipeDistributor(Element e) {
	super(e);
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/2003 4:40:22 PM)
 */
public final int availableSpace() {
	int ret = Integer.MAX_VALUE;
	for (int i = 0; i < size; i++){
		if (pipes[i].availableSpace() < ret)
			ret = pipes[i].availableSpace();
			
	}		
	return ret;
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2003 6:46:07 PM)
 */
public final int getConnectedCount() {
	return pipes.length;
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2003 6:46:07 PM)
 */
public final Pipe getConnectedPipe(int index) {
	return pipes[index];
}
/**
 * BufferedPipe constructor comment.
 */
public final boolean isConnected() {
	return size > 0;
}
/**
 * BufferedPipe constructor comment.
 */
public final void register(Pipe p) {
	BufferedVectorPipe outs1[] = new BufferedVectorPipe[pipes.length + 1];
	System.arraycopy(pipes, 0, outs1, 0, pipes.length);
	outs1[pipes.length] = (BufferedVectorPipe) p;
	pipes = outs1;
	size = pipes.length;

	//System.out.println("new pipe " + p + " from " + this + " new vp=" + ((BufferedVectorPipe) p).getVectorParameters() + " prev vp=" + this.getVectorParameters());
	
	//((BufferedVectorPipe) p).getVectorParameters().inheritVectorParametersFrom(this.getVectorParameters());
}
/**
 * Insert the method's description here.
 * Creation date: (7/29/2003 3:30:41 PM)
 */
public final boolean unregister(Pipe p) {
	if (debug)
		System.out.println("Unregistering " + p);
	
	for (int i = 0; i < pipes.length; i++){
		if (debug)
			System.out.println("list pipe: " + pipes[i]);
		
		if (pipes[i] == p) {
			// Unregister this distributor from the receiver pipe
			((BufferedPipe) p).disconnectDistributor(this);

			// Remove pipe from local table
			if (pipes.length == 1) {
				size = 0;
				pipes = new BufferedVectorPipe[0];
			} else {
				BufferedVectorPipe temp[] = new BufferedVectorPipe[pipes.length - 1];
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
 * Creation date: (7/29/2003 3:30:41 PM)
 */
public final void unregisterAll() {
	for (int i = 0; i < pipes.length; i++){
		// Unregister this distributor from the receiver pipe
		((BufferedPipe) pipes[i]).disconnectDistributor(this);
	}

	size = 0;
	pipes = new BufferedVectorPipe[0];	
}
/**
 * BufferedPipe constructor comment.
 */
public final void writeVector(int[] b) {
	for (int i = 0; i < size; i++){
		pipes[i].writeVector(b);
	}		
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}

/**
 * Insert the method's description here.
 * Creation date: (7/25/2003 4:40:22 PM)
 */
public int getBufferSize() {
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
public int maxAvailableSpace() {
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
public int maxBufferSize() {
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
public int maxOccupiedSpace() {
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
public int minAvailableSpace() {
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
public int minBufferSize() {
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
public int minOccupiedSpace() {
	int ret = Integer.MAX_VALUE;
	for (int i = 0; i < size; i++){
		if (pipes[i].occupiedSpace() < ret)
			ret = pipes[i].occupiedSpace();
			
	}		
	return ret;
}

/**
 * Insert the method's description here.
 * Creation date: (7/29/2003 3:30:41 PM)
 */
final void setVLength(int newVectorLength) {
	for (int i = 0; i < size; i++){
		pipes[i].setVLength(newVectorLength);
	}		
}

/**
 * BufferedPipe constructor comment.
 */
public SignalParameters getSignalParameters() {
	return element.getSignalParameters();
}

/**
 * Insert the method's description here.
 * Creation date: (7/29/2003 3:30:41 PM)
 */
public final void setVectorLength(int newVectorLength) {
	setVLength(newVectorLength);
	getSignalParameters().setVectorLength(newVectorLength);
}
}
