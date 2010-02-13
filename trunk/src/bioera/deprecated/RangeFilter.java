/* RangeFilter.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.deprecated;

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
public final class RangeFilter extends SingleElement {
	public FloatRangeCompoundScale range;
	public boolean inclusive = true;

	private final static String propertiesDescriptions[][] = {
		{"range", "Range", ""},
	};		

	private int from, to;
		
	private ScalarPipeDistributor out;
	private BufferedScalarPipe in;
	private int inb[];

	protected static boolean debug = bioera.Debugger.get("impl.Scalar.range.filter");
/**
 * Element constructor comment.
 */
public RangeFilter() {
	super();
	setName("Range filter");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	inb = in.getBuffer();
	out = (ScalarPipeDistributor) outputs[0];
	out.setName("OUT");

	range = new FloatRangeCompoundScale(new FloatRange(), new SmallBalancedVertScale(this));
	
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Writes to output only values that are within (if inclusive mode) defined ranges";
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
	int n = in.available();
	if (n == 0)
		return;

	int v;
	if (inclusive) {
		for (int i = 0; i < n; i++){
			v = inb[i];
			if (v >= from && v < to) {
				out.write(v);
			}
		}		
	} else {
		for (int i = 0; i < n; i++){
			v = inb[i];
			if (v < from || v >= to) {
				out.write(v);
			}
		}				
	}

	in.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (((SmallBalancedVertScale)range.scale).getSelectedIndex() == -1) {
		((SmallBalancedVertScale)range.scale).setSelectedItem("=uV");
	}

	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}
	
	range.scale.update(this);
	
	from = ((SmallBalancedVertScale)range.scale).toDigit(((FloatRange)range.value).from);
	to = ((SmallBalancedVertScale)range.scale).toDigit(((FloatRange)range.value).to);
	
	super.reinit();
}
}
