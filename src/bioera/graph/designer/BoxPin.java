/* BoxPin.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.graph.designer;

import bioera.*;
import bioera.processing.*;
import bioera.config.*;

public class BoxPin {
	boolean input;
	Pipe pipe;
	
	int x, y;

	public final static int PIN_LENGTH = 8;	
public BoxPin(Pipe p, boolean iinput) {
	super();
	pipe = p;
	input = iinput;
}
public boolean contains(int px, int py) {
	return px >= x && py >= y && px < x + PIN_LENGTH && py < y + PIN_LENGTH;
}
public void paint(java.awt.Graphics g, int maxWidth, int maxHeight, boolean active, int type) {
	if (active) {
		g.fillRect(x, y, PIN_LENGTH, PIN_LENGTH);
	} else {
		g.drawRect(x, y, PIN_LENGTH, PIN_LENGTH);
		if (type == bioera.processing.VectorPipe.TYPE)
			g.drawLine(x, y + PIN_LENGTH/2, x + PIN_LENGTH, y + PIN_LENGTH/2);
		else if (type == LogicalPipe.TYPE)
			g.drawLine(x + PIN_LENGTH/2, y, x + PIN_LENGTH / 2, y + PIN_LENGTH);
		else if (type == ComplexPipe.TYPE) {
			g.drawLine(x, y + PIN_LENGTH/2, x + PIN_LENGTH, y + PIN_LENGTH/2);
				
			if (input) {
				g.drawLine(x + PIN_LENGTH/3, y + PIN_LENGTH/2, x + PIN_LENGTH, y);
				g.drawLine(x + PIN_LENGTH/3, y + PIN_LENGTH/2, x + PIN_LENGTH, y+ PIN_LENGTH);
			} else {
				g.drawLine(x + PIN_LENGTH*2/3, y + PIN_LENGTH/2, x, y);
				g.drawLine(x + PIN_LENGTH*2/3, y + PIN_LENGTH/2, x, y+ PIN_LENGTH);
			}
		}
	}

	if (ConfigurableSystemSettings.DESIGNER_SHOW_INOUT_NAMES) {
		if (input && x + PIN_LENGTH + 10 < maxWidth) {
			g.drawString(pipe.getName(), x + 2 + PIN_LENGTH, y + PIN_LENGTH);
		} else if (x - 10 > 0) {
			int w = g.getFontMetrics().stringWidth(pipe.getName());
			g.drawString(pipe.getName(), x - w - 2, y + PIN_LENGTH);
		}
	}
}
public void save(XmlCreator xml) throws Exception {
	xml.addTextValue("name", pipe.getName());
	xml.addTextValue("index", "" + pipe.getElement().getPipeIndex(pipe));
	xml.addTextValue("direction", input ? "in" : "out");
}
public String toString() {
	return "pin '" + pipe.getName() + "'";
}
}
