/* CMixer.java v 1.0.9   11/6/04 7:15 PM
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
public final class CMixer extends Element {
	public FloatCompoundScale constantValue;
	public boolean resend = true;
	public ComboProperty function = new ComboProperty(new String[] {
		"ADD",
		"SUBTRACT",
		"MULTIPLY",
		"DIVIDE",
		"AVERAGE",
		"MAX",
		"MIN",
		"NEGATIVE SUBTRACT",
	});

	private final static String propertiesDescriptions[][] = {
		{"constantValue", "Value", ""},
		{"resend", "Resend on var change", ""},
	};	

	private final static int SUM = 0;
	private final static int DIFFERENCE = 1;
	private final static int MULTIPLICATION = 2;
	private final static int DIVISION = 3;
	private final static int AVERAGE = 4;
	private final static int MAX = 5;
	private final static int MIN = 6;
	private final static int REV_DIFFERENCE = 7;
	
	private int type = SUM;
	
	private int bufA[], bufC[], maxValue, minValue = 0, cV, last;
	private BufferedScalarPipe inA, inC;
	private ScalarPipeDistributor out;
	private int bCValue;
	private boolean isVar = false;
/**
 * SignalDisplay constructor comment.
 */
public CMixer() {
	super();
	//System.out.println("her");	
	setName("C_Mixer");
	inA = (BufferedScalarPipe)inputs[0];
	inA.setName("IN");
	bufA = inA.getBuffer();
	inC = (BufferedScalarPipe)inputs[1];
	inC.setName("Var");
	bufC = inC.getBuffer();
	out = (ScalarPipeDistributor) outputs[0];
	outputs[0].setName("O");

	constantValue = new FloatCompoundScale(0, new SmallVerticalScale(this));
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Modifies signal with constant value";
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
public int getOutputsCount() {
	return 1;
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
	if (inA.isEmpty() && inC.isEmpty())
		return;
	
	if (!inC.isEmpty()) {
		cV = bufC[inC.available() - 1];
		inC.purgeAll();
		if (resend && inA.isEmpty()) {
			inA.write(last);
		}
	}

	int n = inA.available();

	if (n == 0)
		return;

	switch (type) {
		case SUM:
			for (int i = 0; i < n; i++) {
				last = bufA[i] + cV;
				out.write(last);
			}
			break;
		case DIFFERENCE:
			for (int i = 0; i < n; i++) {
				last = bufA[i] - cV;
				out.write(last);
			}
			break;
		case MULTIPLICATION:
			for (int i = 0; i < n; i++) {
				last = bufA[i] * cV;
				out.write(last);				
			}
			break;
		case AVERAGE:
			for (int i = 0; i < n; i++) {
				last = (bufA[i] + cV) / 2;
				out.write(last);				
			}
			break;
		case MAX:
			for (int i = 0; i < n; i++) {
				last = Math.max(bufA[i], cV);
				out.write(last);
			}
			break;
		case MIN:
			for (int i = 0; i < n; i++) {
				last = Math.min(bufA[i], cV);
				out.write(last);				
			}
			break;
		case DIVISION:
			for (int i = 0; i < n; i++) {
				if (cV == 0) {
					out.write(last = maxValue);
				} else {
					last = bufA[i] / cV;
					out.write(last);
				}
			}
			break;
		case REV_DIFFERENCE:
			for (int i = 0; i < n; i++) {
				last = cV - bufA[i];
				out.write(last);
			}
			break;
	}

	if (resend)
		last = bufA[n - 1];
	
	inA.purgeAll();	
}
/**
 * SignalDisplay constructor comment.
 */
public void reinit() throws Exception {
	if (function.getSelectedIndex() == -1)
		function.setSelectedIndex(type);
	else
		type = function.getSelectedIndex();

	if (((SmallVerticalScale)constantValue.scale).getSelectedIndex() == -1) {
		((SmallVerticalScale)constantValue.scale).setSelectedItem("uV");
	}
		
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		throw new DesignException("Input must be connected");
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	isVar = getFirstElementConnectedToInput(1) != null;
		
	maxValue = getSignalParameters().getDigitalRange();

	constantValue.scale.update(this);
	cV = constantValue.scale.toDigit(constantValue.value);
	//System.out.println("cv=" + cV);	
	bCValue = cV;
	//System.out.println("bcValue=" + bCValue);

	if (!out.isConnected()) {
		throw new DesignException("Output must be connected too");
	}

	super.reinit();
}
}
