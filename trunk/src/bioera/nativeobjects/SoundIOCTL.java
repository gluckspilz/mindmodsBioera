/* SoundIOCTL.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.nativeobjects;

public class SoundIOCTL {
	public FileAccess fa;
	public int fileHandle;

	// Defined formats
	public static final int AFMT_S16_LE = -2;
	public static final int AFMT_S8 = -3;
	
	
/**
 * SoundIOCTL constructor comment.
 */
public SoundIOCTL() {
	super();

	NativeLib.load();	
}
/**
 * SoundIOCTL constructor comment.
 */
public SoundIOCTL(FileAccess f, int filehandle) {
	this();

	fa = f;
	fileHandle = filehandle;
	
	init(filehandle);
}
/**
 * SoundIOCTL constructor comment.
 */
public native int dspReset();
/**
 * SoundIOCTL constructor comment.
 */
public native int dspSync();
/**
 * SoundIOCTL constructor comment.
 */
public native int getFormat();
/**
 * SoundIOCTL constructor comment.
 */
public native int getReadBits();
/**
 * SoundIOCTL constructor comment.
 */
public native int getReadBlockSize();
/**
 * SoundIOCTL constructor comment.
 */
public native int getReadBufferSize();
/**
 * SoundIOCTL constructor comment.
 */
public native int getReadChannels();
/**
 * SoundIOCTL constructor comment.
 */
public native int getReadRate();
/**
 * SoundIOCTL constructor comment.
 */
public native int getWriteBits();
/**
 * SoundIOCTL constructor comment.
 */
public native int getWriteBlockSize();
/**
 * SoundIOCTL constructor comment.
 */
public native int getWriteBufferSize();
/**
 * SoundIOCTL constructor comment.
 */
public native int getWriteChannels();
/**
 * SoundIOCTL constructor comment.
 */
public native int getWriteRate();
/**
 * SoundIOCTL constructor comment.
 */
public native void init(int filehandle);
/**
 * SoundIOCTL constructor comment.
 */
public native int initWriteBlockSize();
/**
 * SoundIOCTL constructor comment.
 */
public native int readAvailable();
/**
 * SoundIOCTL constructor comment.
 */
public native int setFormat(int n);
/**
 * SoundIOCTL constructor comment.
 */
public native int setReadBits(int n);
/**
 * SoundIOCTL constructor comment.
 */
public native int setReadChannels(int n);
/**
 * SoundIOCTL constructor comment.
 */
public native int setReadRate(int n);
/**
 * SoundIOCTL constructor comment.
 */
public native int setWriteBits(int n);
/**
 * SoundIOCTL constructor comment.
 */
public native int setWriteChannels(int n);
/**
 * SoundIOCTL constructor comment.
 */
public native int setWriteRate(int n);
/**
 * SoundIOCTL constructor comment.
 */
public native int writeAvailable();
}
