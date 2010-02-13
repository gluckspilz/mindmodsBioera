/* Highlight.java v 1.0.9   11/6/04 7:15 PM
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

import java.util.*;

/**
 * Creation date: (4/22/2004 2:21:40 PM)
 * @author: Jarek Foltynski
 */
public class Highlight {
	private List list = new ArrayList();	// Contains list of highlighted items
	int x1, y1, w, h;
	private List markedCoordinates = new ArrayList();
/**
 * Highlighted constructor comment.
 */
public Highlight() {
	super();
}
/**
 * Highlighted constructor comment.
 */
public void add(Item i) {
	if (!list.contains(i)) {
		list.add(i);
//		System.out.println("added " + i.uniqueId);
	}
}
/**
 * Highlighted constructor comment.
 */
public void add(ItemSet set) {
	for (int i = 0; i < set.size(); i++){
		Item it = set.get(i);
		if (contains(it)) {
			add(it);
		}
	}
}
/**
 * Highlighted constructor comment.
 */
public void clear() {
	list.clear();
}
/**
 * Highlighted constructor comment.
 */
public boolean contains(Item o) {
	if (o == null)
		return false;
	AbsRect r = new AbsRect(x1, y1, w, h);
	AbsRect r1 = new AbsRect(o.getX1(), o.getY1(), o.getX2() - o.getX1(), o.getY2() - o.getY1());
	return r.contains(r1) || list.contains(o);
}
/**
 * Highlighted constructor comment.
 */
public Item get(int i) {
	return (Item) list.get(i);
}
/**
 * Highlighted constructor comment.
 */
public java.awt.Point getMarkedPoint(int i) {
	return (java.awt.Point) markedCoordinates.get(i);			
}
/**
 * Highlighted constructor comment.
 */
public java.awt.Point getMarkedPoint(Item it) {
	for (int i = 0; i < list.size(); i++){
		if (it == get(i)) {
			return (java.awt.Point) markedCoordinates.get(i);			
		}
	}

	return null;
}
/**
 * Highlighted constructor comment.
 */
public void markPoints() {
	markedCoordinates.clear();
	for (int i = 0; i < list.size(); i++){
		Item it = get(i);
//		System.out.println("got " + it.uniqueId);
		markedCoordinates.add(new java.awt.Point(it.getX1(), it.getY1()));
//		System.out.println("marked " + it.getX1() + " " +  it.getY1() + "  unique=" + it.uniqueId);
	}

	//System.out.println("marked " + list.size());	
}
/**
 * Highlighted constructor comment.
 */
public void paint(java.awt.Graphics g) {
	if (x1 >= 0 && y1 >= 0) {		
		int x = x1, y = y1, wi = w, he = h;
		if (w < 0) {
			wi = -w;
			x = x1 - wi;
		}
		if (h < 0) {
			he = -h;
			y = y1 - he;
		}
		
		g.drawRect(x, y, wi, he);
	}
}
/**
 * Highlighted constructor comment.
 */
public void remove(int i) {
	list.remove(i);
}
/**
 * Highlighted constructor comment.
 */
public void remove(Item i) {
	list.remove(i);
}
/**
 * Highlighted constructor comment.
 */
public void reset() {
	x1 = y1 = -1;
	w = h = 0;
}
/**
 * Highlighted constructor comment.
 */
public int size() {
	return list.size();
}

/**
 * Highlighted constructor comment.
 */
public List getAll() {
	return list;
}
}
