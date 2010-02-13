/* PNPod.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.tools.impl;


import java.io.*;
import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.properties.*;
import bioera.storage.*;
import bioera.*;
import bioera.config.*;
import bioera.tools.pn.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class PNPod extends Programmer {
	public ComboProperty sps = new ComboProperty(new String[] {
		"512sps",
		"256sps",
		"128sps",
		"122sps",
	});

	private int spsType = 1;
	 
	public ComboProperty frequency = new ComboProperty(new String[] {
		"48Hz",
		"40Hz",
	});

	private int freqType = 0;
	
	private final static String propertiesDescriptions[][] = {
		{"frequency", "Frequency", ""},
		{"sps", "Samples per second", ""},
	};

	private File file;
	
	protected static boolean debug = bioera.Debugger.get("tools.pn.pendant.Pod");
/**
 * Element constructor comment.
 */
public PNPod() {
	super();
	setName("Pendant Pod");
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Pendant Pod";
}
/**
 * Element constructor comment.
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
public java.io.Reader getReader() throws java.lang.Exception {
	return new FileReader(file);
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	if (sps.getSelectedIndex() == -1)
		sps.setSelectedIndex(spsType);
	else
		spsType = sps.getSelectedIndex();

	if (frequency.getSelectedIndex() == -1)
		frequency.setSelectedIndex(spsType);
	else
		freqType = frequency.getSelectedIndex();
		
	String filename = sps.getSelectedItem() + frequency.getSelectedItem() + ".pod";
	
	file = new File(Main.app.getToolsFolder() + File.separator + "PocketNeurobics" + File.separator + filename);
	if (!file.exists())
		throw new DesignException("File not found '" + file + "'");

	super.reinit();
}
}
