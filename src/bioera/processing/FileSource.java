/* FileSource.java v 1.0.9   11/6/04 7:15 PM
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

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class FileSource extends SourceElement {
	File file;
	InputStream in;
/**
 * Element constructor comment.
 */
public java.lang.String getOutputName(int index) {
	return file.getName();
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 1;
}
/**
 * Element constructor comment.
 */
public void init() {
	
}
/**
 * Element constructor comment.
 */
public void process() {
	
}

/**
 * FileSource constructor comment.
 */
public FileSource() {
	super();
	file = new File("none");
//	in = new BufferedInputStream(new FileInputStream(file));
}
}
