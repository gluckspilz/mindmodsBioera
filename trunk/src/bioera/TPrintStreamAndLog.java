/* TPrintStreamAndLog.java v 1.0.9   11/6/04 7:15 PM
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

import java.io.PrintStream;
import java.util.Calendar;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class TPrintStreamAndLog extends PrintStream {
	long lastTimeShort = 0;
	long lastTimeLong = 0;
	public final static int SHORT_INTERVAL = 1 * 60 * 1000; // MINUTE
	public final static int LONG_INTERVAL = 1 * 60 * 60 * 1000; //HOUR;
	java.io.PrintStream printStream;
	Log logger = null;
/**
 */
public TPrintStreamAndLog(java.io.PrintStream p, Log l) {
	super(new java.io.OutputStream(){
		public void write(int b){}
	});
	logger = l;
	printStream = p;
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2000 10:02:22 AM)
 */
public void close() {
	if (logger != null) {
		
	}
}
/**
 * Code to perform when this object is garbage collected.
 * 
 * Any exception thrown by a finalize method causes the finalization to
 * halt. But otherwise, it is ignored.
 */
protected void finalize() throws Throwable {
	if (logger != null) {
		logger.close();
		logger = null;
	}

	super.finalize();
}
/**
 * 
 * 
 */
public Log getFileLogger() {
	return logger;
}
/**
 * 
 * 
 */
public Log getLogger() {
	return logger;
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/2001 1:44:23 PM)
 */
public void print(String msg) {
	if (printStream != null)
		printStream.print(msg);
	if (logger != null)
		logger.print(msg);
}
/**
 * Insert the method's description here.
 * Creation date: (5/25/2000 10:32:58 AM)
 */
public void println(String msg) {	
	if (printStream != null)
		printStream.println(msg);

	long now = System.currentTimeMillis();	
	if (now > lastTimeShort + SHORT_INTERVAL) {
		// Record time
		lastTimeShort = now;
		Calendar c = Calendar.getInstance();
		String pref = "" + c.get(c.HOUR_OF_DAY) + ":" + c.get(c.MINUTE);
		msg = pref + Log.LINESEP + msg;
		if (now > lastTimeLong + LONG_INTERVAL) {
			// Record date
			lastTimeLong = now;
			pref = "" + (c.get(c.MONTH)+1) + "/" + c.get(c.DAY_OF_MONTH);
			msg = pref + " " + msg;
		}		
	}	
		
	if (logger != null) {			
		logger.println(msg);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2000 11:45:15 AM)
 */
public void write(byte b[]) {
	if (printStream != null)
		try {
			printStream.write(b);
		} catch (java.io.IOException e) {
			// do nothing with exception
		}
	if (logger != null)		
		logger.write(b);		
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2000 11:45:15 AM)
 */
public void write(byte b[], int offset, int count) {
	if (printStream != null)
		printStream.write(b, offset, count);
	if (logger != null)		
		logger.write(b, offset, count);		
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2000 11:45:15 AM)
 */
public void write(int b) {
	if (printStream != null)
		printStream.write(b);
	if (logger != null)		
		logger.write(b);		
}
}
