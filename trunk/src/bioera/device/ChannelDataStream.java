/* ChannelDataStream.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.device;

import java.io.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public abstract class ChannelDataStream implements ChannelFormatInfo {
	// Read errors status
	int lostBytes = 0;
	int lostPackets = 0;	
/**
 * ChannelDataStream constructor comment.
 */
public ChannelDataStream() {
	super();
}
/**
 * ChannelDataStream constructor comment.
 */
abstract public int getChannelData(int channel) throws IOException;
/**
 * Insert the method's description here.
 * Creation date: (5/21/2003 11:40:08 AM)
 * @return int
 */
public int getLostBytes() {
	return lostBytes;
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2003 11:40:08 AM)
 * @return int
 */
public int getLostPackets() {
	return lostPackets;
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2003 11:40:08 AM)
 * @return int
 */
public String getLostStatus() {
	return "Lost bytes: " + lostBytes + ", packets " + lostPackets;
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2003 11:40:08 AM)
 * @return int
 */
public abstract int getMaxAmplitude();
/**
 * Insert the method's description here.
 * Creation date: (5/21/2003 11:40:08 AM)
 * @return int
 */
public abstract int getSampleRate();
/**
 * ChannelDataStream constructor comment.
 */
abstract public void readPacket() throws IOException;
}
