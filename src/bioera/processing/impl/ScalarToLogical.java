/* ScalarToLogical.java v 1.0.9   11/6/04 7:15 PM
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
public final class ScalarToLogical extends SingleElement {
	public FloatCompoundScale value;
	public ComboProperty condition = new ComboProperty(new String[] {
		">",
		"<",
		">=",
		"<=",
		"=",
		"<>",
		});

	public static final int GREATER = 0;
	public static final int LESS = 1;	
	public static final int GREATER_EQUAL = 2;
	public static final int LESS_EQUAL = 3;
	public static final int EQUAL = 4;
	public static final int DIFFERENT = 5;
		
	int type = 0;
		
	private LogicalPipeDistributor out;
	private BufferedScalarPipe in, inVar;
	private int inb[], tV;
	private boolean lastSent = false;

	private final static String propertiesDescriptions[][] = {
//		{"", "", ""},
	};
	
	protected static boolean debug = bioera.Debugger.get("impl.onoff");
/**
 * Element constructor comment.
 */
public ScalarToLogical() {
	super();
	setName("S-to-L");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	inb = in.getBuffer();

	inVar = (BufferedScalarPipe) inputs[1];
	inVar.setName("Var");
	inb = in.getBuffer();
	
	
	outputs[0] = out = new LogicalPipeDistributor(this);
	out.setName("OUT");

	value = new FloatCompoundScale(0, new SmallBalancedVertScale(this));
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Sends TRUE, if input sample meets condition, FALSE otherwise";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 2;
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
public final void process() {
	if (in.isEmpty() && inVar.isEmpty())
		return;

	if (!inVar.isEmpty()) {
		tV = inVar.getBuffer()[inVar.available() - 1];
		inVar.purgeAll();
	}
	
	int n = in.available();

	if (n == 0)
		return;

	int value = inb[n-1];
		
	switch (type) {
		case GREATER:
			send(value > tV);
			break;
		case LESS:
			send(value < tV);					
			break;
		case GREATER_EQUAL:
			send(value >= tV);
			break;		
		case LESS_EQUAL:
			send(value <= tV);
			break;		
		case DIFFERENT:
			send(value != tV);
			break;
		case EQUAL:
			send(value == tV);			
			break;
	}
		
	in.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (((SmallBalancedVertScale)value.scale).getSelectedIndex() == -1) {
		((SmallBalancedVertScale)value.scale).setSelectedItemThrow("uV");
	}
	
	if (condition.getSelectedIndex() == -1)
		condition.setSelectedIndex(type);
	else
		type = condition.getSelectedIndex();	

	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}
		
	value.scale.update(this);
	tV = value.scale.toDigit(value.value);
		
	super.reinit();
}
/**
 * Element constructor comment.
 */
public final void send(boolean v) {
	if (v != lastSent) {
		out.write(v);
		lastSent = v;
	}
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {

}
}
