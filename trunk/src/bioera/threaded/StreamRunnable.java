/* StreamRunnable.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.threaded;

import java.io.*;
import java.util.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public abstract class StreamRunnable extends ThreadedAdapter implements Runnable {
	int  buffer[] = new int[64*1024];
	int position = 0;
	
	protected ThreadedStreamElement threadedStreamElement;
	protected static boolean debug = bioera.Debugger.get("processing.streamrunnable");
/**
 * OutboundRunnable constructor comment.
 */
public StreamRunnable(ThreadedStreamElement e) {
	super();
	threadedStreamElement = e;
	runnable = this;
}
/**
 */
protected synchronized void reset(int i) {
	if (i >= position) {
		position = 0;
	} else {
		System.arraycopy(buffer, i, buffer, 0, position - i);
		position = i;
	}
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
