/* ExcelFileWriter.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.processing.impl;

import java.io.*;
import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.storage.*;
import bioera.*;
import bioera.properties.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class ExcelFileWriter extends SingleElement {
	public String filePath = "unknown";
	public boolean appendExisting;
	public ComboProperty separator = new ComboProperty(new String[]{
		"LF (\n)",
		"semicolon (;)",
		"space ( )",
		"CR (\r)",
		"TAB (\t)",
	});	

	private final static String propertiesDescriptions[][] = {
		{"appendExisting", "Append existing file", ""},
};	
	
	private BufferedOutputStream fout;
	private StringPipeDistributor sout;
	private BufferedScalarPipe in;
	private int buffer[];
	private char sep;

	protected static boolean debug = bioera.Debugger.get("impl.file.writer.excel");
/**
 * Element constructor comment.
 */
public ExcelFileWriter() {
	super();
	setName("ExcelWriter");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	buffer = in.getBuffer();
	outputs[0] = sout = new StringPipeDistributor(this);
}
/**
 * Element constructor comment.
 */
public void close() throws Exception {
    if (fout != null) {
        fout.close();
        fout = null;
    }
}
/**
 * Element constructor comment.
 */
public String getElementDescription() throws Exception {
	return
	"Stream values are written to a text file or output as strings.";
}
/**
 */
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
/**
 * Element constructor comment.
 */
public final void process() throws Exception {
	int n = in.available();
	if (n > 0) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < n; i++){
			b.append(buffer[i]);
			b.append(sep);
		}
		n = b.length();
		//System.out.println("n=" + n);
		if (fout != null) {
			for (int i = 0; i < n; i++){
				fout.write(b.charAt(i));
			}
		}
		if (sout.isConnected()) {
			for (int i = 0; i < n; i++){
				sout.write(b.charAt(i));
			}				
		}			
		in.purgeAll();		
	}

	
	//int n = in.available();
	//if (n > 0) {
		//byte b[] = new byte[15];
		//int ind = b.length;
		//int v;
		
		//for (int i = 0; i < n; i++){
			//v = buffer[i];
			//while (v != 0) {
				//b[--ind] = (byte) (v % 10);
				//v /= 10;
			//}
			//if (buffer[i] < 0)
				//b[--ind] = (byte) '-';
			//if (fout != null) {
				//fout.write(b, ind, b.length - ind);
				//fout.write(sep);
			//}
			//if (sout.isConnected()) {
				//sout.write(b, ind, b.length - ind);
				//sout.write(sep);
			//}
		//}
		//in.purgeAll();		
	//}		
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (separator.getSelectedIndex() == -1)
		separator.setSelectedIndex(0);

	switch (separator.getSelectedIndex()) {
		case 0:
			sep = '\n'; break;
		case 1:
			sep = ';'; break;
		case 2:
			sep = ' '; break;
		case 3:
			sep = '\r'; break;
		case 4:
			sep = '\t'; break;		
	};
	
	
	verifyDesignState(!TOOLS.isNone(filePath) || sout.isConnected());

	super.reinit();
}

/**
 * Element constructor comment.
 */
public final void start() throws Exception {
	// Check if the format of the existing file is the same
	if (!TOOLS.isNone(filePath))
		fout = new BufferedOutputStream(new FileOutputStream(filePath, appendExisting));
	else
		fout = null;
}

/**
 * Element constructor comment.
 */
public final void stop() throws Exception {
	close();
}
}
