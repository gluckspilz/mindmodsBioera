/* BufferedScalarPipe.java v 1.0.9   11/6/04 7:15 PM
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
public class BufferedScalarPipe extends AbstractBufferedScalarPipe {
	protected int buffer[];
	protected int index;
	protected int size;
	protected int lostPackets = 0;
	protected int globalCounter = 0;
	protected int i, n;
/**
 * BufferedPipe constructor comment.
 */
public BufferedScalarPipe(Element e) {
	this(e, bioera.DesignSettings.defaultScalarBufferLength);
}
/**
 * BufferedPipe constructor comment.
 */
public BufferedScalarPipe(Element e, int len) {
	super(e);
	size = len;
	buffer = new int[size];
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
	return size - index - 1;
}
/**
 * BufferedPipe constructor comment.
 */
public final int [] getBuffer() {
	return buffer;
}
/**
 * BufferedPipe constructor comment.
 */
public final int getBufferSize() {
	return size;
}
/**
 * BufferedPipe constructor comment.
 */
public final int getLostPackets() {
	return lostPackets;
}
/**
 * Insert the method's description here.
 * Creation date: (1/20/2004 8:03:21 PM)
 */
public final int globalCounter() {
	return globalCounter;
}
/**
 * BufferedPipe constructor comment.
 */
public static void main(String args[]) {
	try {
		System.out.println("started");
		new BufferedScalarPipe(null).test();
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
	System.arraycopy(buffer, n, buffer, 0, index - n);
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
public final int read() {
	if (index == 0)
		return -1;

	int ret = buffer[0];
	index--;
	System.arraycopy(buffer, 1, buffer, 0, index);
	return ret;
}
/**
 * BufferedPipe constructor comment.
 */
public final int read(int b[], int from, int size) {
	n = Math.min(size, this.index);
	System.arraycopy(buffer, 0, b, from, n);
	if (n < index) {
		System.arraycopy(buffer, n, buffer, 0, index - n);
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
public final void setBufferSize(int nlen) {
	size = nlen;
	buffer = new int[size];
	index = 0;

	element.initialize();
}
/**
 * BufferedPipe constructor comment.
 */
public final void test() throws Exception {
	for (int i = 0; i < 10; i++){
		write(i);
	}

	purge(1);

	for (int i = 0; i < 10; i++){
		System.out.println("p=" + read());
	}
	
}
/**
 * BufferedPipe constructor comment.
 */
public final void write(int t[], int from, int count) {
	if (count > (size - index)) {
		// Since there is no room for data so reset the whole buffer
		lostPackets++;
		index = 0;
	}

	if (count > size - index)
		System.out.println("Buffer too small " + (size - index) + " in element " + getElement().getName() + " for " + count + " bytes");
	System.arraycopy(t, from, buffer, index, count);
	index += count;
	globalCounter += count;
}
/**
 * BufferedPipe constructor comment.
 */
public final void write(int i) {
	//System.out.println("writing to " + element.getName() + "(" + getName() + ", " + getId() + "): " + i);
	buffer[index++] = i;
	globalCounter++;
	if (index == size) {
		index = 0;
		lostPackets++;
	}
}

/**
 * isEmpty method comment.
 */
public boolean isEmpty() {
	return index == 0;
}

/**
 * BufferedPipe constructor comment.
 */
public SignalParameters getSignalParameters() {
	if (element.predecessorElement == null)
		throw new RuntimeException("Predecessor not connected, therefore SP not available");
	return element.predecessorElement.getSignalParameters();
}
}
