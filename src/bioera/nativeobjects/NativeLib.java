/* NativeLib.java v 1.0.9   11/6/04 7:15 PM
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

public class NativeLib {
	public static boolean loaded = false;
/**
 * NativeLib constructor comment.
 */
public NativeLib() {
	super();
}
/**
 * NativeLib constructor comment.
 */
public static void load() {
	load("bioera_native");
}

/**
 * NativeLib constructor comment.
 */
public static void load(String name) {
	if (loaded)
		return;

	try {
		System.loadLibrary(name);
		loaded = true;
	} catch (Throwable e) {
		//e.printStackTrace();
		System.out.println("" + e.getMessage());
		System.out.println("Native library was '"+name+"' not found in "+System.getProperty("java.library.path"));
	}
}
}
