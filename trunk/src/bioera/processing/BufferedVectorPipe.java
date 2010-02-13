/* BufferedVectorPipe.java v 1.0.9   11/6/04 7:15 PM
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
public class BufferedVectorPipe extends VectorPipe implements BufferedPipe {
	protected int vBuffer[][];
	protected int lostPackets;
	protected int globalCounter;
	protected int i, n, temp[], vSize, index, bSize;
	
	protected VectorPipeDistributor connectedDistributors[] = new VectorPipeDistributor[0];
/**
 * BufferedPipe constructor comment.
 */
public BufferedVectorPipe(Element e) {
	this(e, bioera.DesignSettings.defaultVectorBufferLength, VectorPipe.DEFAULT_VECTOR_LENGTH);
}
/**
 * BufferedPipe constructor comment.
 */
public BufferedVectorPipe(Element e, int s, int vL) {
	super(e);
	bSize = s;
	vSize = vL;
	vBuffer = new int[bSize][];
	for (int i = 0; i < bSize; i++){
		vBuffer[i] = new int[vSize];
	}
}
/**
 * BufferedPipe constructor comment.
 */
public final int available() {
	return index;
}
/**
 * BufferedPipe constructor comment.
 */
public final int availableSpace() {
	return bSize - index - 1;
}
/**
 * Insert the method's description here.
 * Creation date: (7/20/2003 12:48:35 PM)
 */
public void connectDistributor(PipeDistributor p) {
	if (p == null)
		return;

	connectedDistributors = (VectorPipeDistributor[]) ProcessingTools.appendArray(connectedDistributors, p);
}
/**
 * Insert the method's description here.
 * Creation date: (7/20/2003 12:48:35 PM)
 */
public void disconnectDistributor(PipeDistributor p) {
	if (p == null)
		return;

	connectedDistributors = (VectorPipeDistributor[]) ProcessingTools.removeAllFromArray(connectedDistributors, p);

	if (connectedDistributors.length > 0) {
		getElement().predecessorElement = connectedDistributors[0].getElement();
	} else {
		getElement().predecessorElement = null;
	}	
}
/**
 * BufferedPipe constructor comment.
 */
public final int getBufferSize() {
	return bSize;
}
/**
 * BufferedPipe constructor comment.
 */
public final PipeDistributor [] getConnectedDistributors() {
	return connectedDistributors;
}
/**
 * BufferedPipe constructor comment.
 */
public final int getLostPackets() {
	return lostPackets;
}
/**
 * BufferedPipe constructor comment.
 */
public SignalParameters getSignalParameters() {
	if (element.predecessorElement == null)
		throw new RuntimeException("Predecessor not connected, therefore SP not available");
	return element.predecessorElement.getSignalParameters();
}
/**
 * BufferedPipe constructor comment.
 */
public final int [][] getVBuffer() {
	return vBuffer;
}
/**
 * BufferedPipe constructor comment.
 */
public final int globalCounter() {
	return globalCounter;
}
/**
 * BufferedPipe constructor comment.
 */
public final boolean isConnected() {
	return connectedDistributors.length > 0;
}
/**
 * BufferedPipe constructor comment.
 */
public boolean isEmpty() {
	return index == 0;
}
/**
 * BufferedPipe constructor comment.
 */
public static void main(String args[]) {
	try {
		System.out.println("started");
		new BufferedVectorPipe(null).test();
		System.out.println("Finished");
	} catch (Exception e) {
		e.printStackTrace();
	}
}
/**
 * BufferedPipe constructor comment.
 */
public final int occupiedSpace() {
	return index;
}
/**
 * BufferedPipe constructor comment.
 */
public final void purge(int n) {
	if (n >= index) {
		index = 0;
		return;
	}

	// Shift data in buffer
	for (int i = 0; i < index - n; i++){
		System.arraycopy(vBuffer[n + i], 0, vBuffer[i], 0, vSize);
	}
	
	index -= n;
}
/**
 * BufferedPipe constructor comment.
 */
public final void purgeAll() {
	index = 0;
}
/**
 * BufferedPipe constructor comment.
 */
public final int readVectors(int b[][], int from, int size) {
	n = Math.min(size, this.index);
	for (i = 0; i < n; i++){
		System.arraycopy(vBuffer[i], 0, b[from + i], 0, vSize);
	}
	
	if (n < index) {
		int temp[];
		for (int i = 0; i < (index - n); i++){
			temp = vBuffer[i];
			vBuffer[i] = vBuffer[i + n];
			vBuffer[i + n] = temp;
		}		
		index = index - n;
	} else {
		index = 0;
	}

	return n;
}
/**
 * BufferedPipe constructor comment.
 */
public final void reset() {
	index = 0;
}
/**
 * BufferedPipe constructor comment.
 */
public final void setBufferSize(int nsize) {
	if (nsize == bSize)
		return;
	this.bSize = nsize;
	vBuffer = new int[nsize][];
	for (int i = 0; i < bSize; i++){
		vBuffer[i] = new int[vSize];
	}	
	index = 0;

	element.initialize();
}
/**
 * BufferedPipe constructor comment.
 */
final void setVLength(int newVectorLength) {
	if (newVectorLength == vSize)
		return;

	vSize = newVectorLength;

//	getVectorParameters().setVectorLength(newVectorLength);
		
//System.out.println("set length " + vSize + " in " + getName() + " of " + getElement().getName());
	try {
		for (int i = 0; i < bSize; i++){
			vBuffer[i] = new int[newVectorLength];
		}
	} catch (OutOfMemoryError e) {
		throw new RuntimeException("OutOfMemory error: could not set vector length to " + newVectorLength + " buffer length is " + getBufferSize());
	}
//System.out.println("s2");	
}
/**
 * BufferedPipe constructor comment.
 */
public final void test() throws Exception {
	for (int i = 0; i < 10; i++){
//		write(i);
	}

	purge(1);

	//for (int i = 0; i < 10; i++){
		//System.out.println("p=" + read());
	//}
	
}
/**
 * BufferedPipe constructor comment.
 */
public final void writeVector(int b[]) {
//	try {
		System.arraycopy(b, 0, vBuffer[index], 0, vSize);
		globalCounter++;
		if (++index == bSize) {
			index = 0;
			lostPackets++;
		}
//	} catch (RuntimeException e) {
//		System.out.println("from b.length=" + b.length + " to " + vSize);
//		System.out.println("element=" + getElement().getName());
//		throw e;
//	}
}
}
