/* FileAccess.java v 1.0.9   11/6/04 7:15 PM
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

public class FileAccess {
	public static final int F_READONLY		= 1;
	public static final int F_WRITEONLY		= 2;
	public static final int F_READ_WRITE	= 3;
/**
 * IoCtlFile constructor comment.
 */
public FileAccess() {
	super();

	NativeLib.load();
}
/**
 * IoCtlFile constructor comment.
 */
public native int close();
/**
 * IoCtlFile constructor comment.
 */
public native int getBufferLength();
/**
 * IoCtlFile constructor comment.
 */
public native int open(String path, int attributes);
/**
 * IoCtlFile constructor comment.
 */
public native int read(byte values[], int ofs, int count);
/**
 * IoCtlFile constructor comment.
 */
public native void setBufferLength(int len);
/**
 * IoCtlFile constructor comment.
 */
public native int write(byte values[], int ofs, int count);
}
