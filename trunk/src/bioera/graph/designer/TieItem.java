/* TieItem.java v 1.0.9   11/6/04 7:15 PM
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
import javax.swing.*;
import bioera.config.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class TieItem extends Item {
	public final static int MARGIN = 5;
	public final static String TYPE = "conn";

	BoxItem src;
	BoxPin srcPin;
	BoxItem dest;
	BoxPin destPin;
/**
 * Connection constructor comment.
 */
public void addPopupMenuItems(javax.swing.JPopupMenu menu) {
	menu.add(new JMenuItem(Commands.DELETE));
	//menu.add(new JMenuItem(Commands.ADVANCED));
}
/**
 * Connection constructor comment.
 */
public boolean contains(int x, int y) {
	int x1 = getX1(), x2 = getX2(), y1 = getY1(), y2 = getY2();

	if ((x > x1 + MARGIN && x > x2 + MARGIN) || (x + MARGIN < x1 && x + MARGIN < x2)
		|| (y > y1 + MARGIN && y > y2 + MARGIN) || (y + MARGIN < y1 && y + MARGIN < y2))
		return false;
	
	if (x1 == x2)
		return Math.abs(x - x1) < MARGIN;
	else if (y1 == y2)
		return Math.abs(y - y1) < MARGIN;
	else
		return Math.abs((x - x1) * (y2 - y1) / (x2 - x1) + y1 - y) < MARGIN;
//	return Math.abs((x - x1) * (y2 - y1) / MARGIN < (x2 - x1) + y1 - y);
}
/**
 * Connection constructor comment.
 */
public void destroy() {
}
/**
 * Connection constructor comment.
 */
public boolean equals(Object o) {
	if (o instanceof TieItem) {
		TieItem e = (TieItem) o;
		if (e.src != null && e.dest != null 
			&& e.src == src && e.dest == dest
			&& e.srcPin == srcPin
			&& e.destPin == destPin)
			return true;
	}

	return false;
}
/**
 * Connection constructor comment.
 */
public int getX1() {	
	return src.x + srcPin.x + BoxPin.PIN_LENGTH;
}
/**
 * Connection constructor comment.
 */
public int getX2() {
	return dest.x + destPin.x;
}
/**
 * Connection constructor comment.
 */
public int getY1() {	
	return src.y + srcPin.y + BoxPin.PIN_LENGTH / 2;
}
/**
 * Connection constructor comment.
 */
public int getY2() {	
	return dest.y + destPin.y + BoxPin.PIN_LENGTH / 2;
}
/**
 * Connection constructor comment.
 */
public void reset() {
	super.reset();
}
/**
 * Connection constructor comment.
 */
public void save(XmlCreator xml) throws Exception {
	super.save(xml);
	xml.addTextValue("src_id", "" + src.element.getId());
	XmlCreator c = xml.addSection("srcpin");
	srcPin.save(c);
	xml.addTextValue("dest_id", "" + dest.element.getId());
	c = xml.addSection("destpin");
	destPin.save(c);
}
/**
 * setX1 method comment.
 */
public void setX1(int x) {}
/**
 * setY1 method comment.
 */
public void setY1(int x) {}

/**
 * Connection constructor comment.
 */
public TieItem(bioera.processing.Pipe s, bioera.processing.Pipe d) {
	super("\nline");
	src = s.getElement().getDesignerBox();
	srcPin = src.getPin(false, s.getName());	
	dest = d.getElement().getDesignerBox();;
	destPin = dest.getPin(true, d.getName());

	if (srcPin == null)
		throw new RuntimeException("Src pin '" + s.getName() + "' not found");
	if (destPin == null)
		throw new RuntimeException("Dest pin '" + d.getName() + "' not found");
		
}
}
