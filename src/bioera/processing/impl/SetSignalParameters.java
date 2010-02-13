/* SetSignalParameters.java v 1.0.9   11/6/04 7:15 PM
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
import java.util.*;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.graph.designer.*;
import bioera.properties.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class SetSignalParameters extends SingleElement {
    public bioera.processing.SignalParameters parameters =
        new bioera.processing.SignalParameters("none", 0, 0, 0, "none");
    public boolean controlBuffers = true;

    private final static String propertiesDescriptions[][] =
        { { "parameters", "Signal parameters", "" }, };

    int buf[];
    private BufferedScalarPipe in;
    private ScalarPipeDistributor out;
/**
 * SignalDisplay constructor comment.
 */
public SetSignalParameters() {
	super();
	setName("Parameters");
	in = (BufferedScalarPipe)inputs[0];
	in.setName("IN");
	buf = in.getBuffer();
	out = (ScalarPipeDistributor) outputs[0];
	outputs[0].setName("OUT");	
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Set signal parameters";
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
public final void process() {
	if (in.isEmpty())
		return;

	if (controlBuffers) {
		int n = Math.min(in.available(), out.minAvailableSpace());
		out.write(buf, 0, n);
		in.purge(n);	
	} else {
		out.write(buf, 0, in.available());
		in.purgeAll();
	}
}
/**
 * SignalDisplay constructor comment.
 */
public void reinit() throws Exception {
	getSignalParameters().inheritReferences(parameters);
	super.reinit();
}
}
