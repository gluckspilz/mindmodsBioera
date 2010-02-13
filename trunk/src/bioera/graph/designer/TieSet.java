/* TieSet.java v 1.0.9   11/6/04 7:15 PM
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

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import bioera.*;
import bioera.config.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class TieSet extends ItemSet {
	TieItem allConnections[] = new TieItem[0];
/**
 * ConnectionElements constructor comment.
 */
public TieSet(GraphPanel p) {
	super(p);
}
/**
 * LinesPanel constructor comment.
 */
public Item delete(Item e) {
	// Search connection lines
	for (int i = 0; i < allConnections.length; i++){
		TieItem el = allConnections[i];
		if (el == e) {
			TieItem t[] = new TieItem[allConnections.length - 1];
			System.arraycopy(allConnections, 0, t, 0, i);
			if (i + 1 < allConnections.length)
				System.arraycopy(allConnections, i+1, t, i, allConnections.length - (i + 1));
			allConnections = t;
			return el;
		}
	}
	
	return null;
}
/**
 * LinesPanel constructor comment.
 */
public void deleteConnectedTo(Item e) {
	// Search connection lines
	for (int i = 0; i < allConnections.length; i++){
		TieItem el = allConnections[i];
		if (el.src == e || el.dest == e) {
			delete(el);
			i--;
		}
	}
}
/**
 * LinesPanel constructor comment.
 */
public void dispose() {
	for (int i = 0; i < allConnections.length; i++){
		allConnections[i] = null;
	}
}
/**
 * LinesPanel constructor comment.
 */
public boolean exists(TieItem c) {
	for (int i = 0; i < allConnections.length; i++){
		TieItem el = allConnections[i];
		if (el.equals(c))
			return true;
	}

	return false;
}
/**
 * Elements constructor comment.
 */
public Item get(int i) {
	return allConnections[i];
}
/**
 * LinesPanel constructor comment.
 */
public Item getElementAt(int x, int y) {
	// Search connection lines
	for (int i = 0; i < allConnections.length; i++){
		TieItem el = allConnections[i];
		if (el.contains(x, y))
			return el;
	}
	
	return null;
}
/**
 * LinesPanel constructor comment.
 */
public void paint(Graphics g) {
	int cx1, cy1, cx2, cy2;
	for (int i = 0; i < allConnections.length; i++){
		TieItem conn = allConnections[i];
		if (conn.highlighted.contains(conn))
			g.setColor(panel.highlightColor);
		g.drawLine(conn.getX1(), conn.getY1(), conn.getX2(), conn.getY2());
		if (conn.highlighted.contains(conn))
			g.setColor(panel.defaultColor);		
	}
}
/**
 * LinesPanel constructor comment.
 */
public void reset() throws Exception {
	for (int i = 0; i < allConnections.length; i++)
		allConnections[i].reset();
}
/**
 * LinesPanel constructor comment.
 */
public void save(XmlCreator xml) throws Exception {
	for (int i = 0; i < allConnections.length; i++) {
		XmlCreator elem = xml.addSection("element");
		elem.addAttribute("type", TieItem.TYPE);
		allConnections[i].save(elem);
	}
}
/**
 * Elements constructor comment.
 */
public int size() {
	return allConnections.length;
}

/**
 * LinesPanel constructor comment.
 */
public Item add(TieItem item) {
	allConnections = (TieItem[]) bioera.processing.ProcessingTools.appendArray(allConnections, item);
	return item;
}
}
