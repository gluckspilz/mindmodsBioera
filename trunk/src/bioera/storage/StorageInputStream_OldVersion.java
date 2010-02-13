/* StorageInputStream_OldVersion.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.storage;

public class StorageInputStream_OldVersion extends StorageInputStream {
	int zeroLevel;
	int counter = 0;
/**
 * StorageInputStream_OldVersion constructor comment.
 * @param i java.io.InputStream
 * @exception java.lang.Exception The exception description.
 */
public StorageInputStream_OldVersion(java.io.InputStream i) throws Exception {
	super(i);
}
public final int read2() throws Exception {
	int ret = read();
	if (ret == -1)
		return 0x80000000;
		
	int a = read();
	if (a == -1)
		return 0x80000000;
	else {
		//System.out.println("zeroLevel=" + zeroLevel);
		return ret + (a << 8) - zeroLevel;
	}
}
}
