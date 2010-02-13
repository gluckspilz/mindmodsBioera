/* JBoxSet.java v 1.0.9   11/6/04 7:15 PM
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
public class JBoxSet extends JItemSet {
	BoxItem allComp[] = new BoxItem[0];
/**
 * ConnectionElements constructor comment.
 */
public JBoxSet(JGraphPanel p) {
	super(p);
}
/**
 * LinesPanel constructor comment.
 */
public void add(BoxItem comp) {
	BoxItem temp[] = new BoxItem[allComp.length + 1];
	System.arraycopy(allComp, 0, temp, 0, allComp.length);
	temp[allComp.length] = comp;
	allComp = temp;
}
/**
 * LinesPanel constructor comment.
 */
public Item delete(Item e) {
	// Search connection lines
	for (int i = 0; i < allComp.length; i++){
		BoxItem el = allComp[i];
		if (el == e) {
			BoxItem t[] = new BoxItem[allComp.length - 1];
			System.arraycopy(allComp, 0, t, 0, i);
			if (i + 1 < allComp.length)
				System.arraycopy(allComp, i+1, t, i, allComp.length - i - 1);
			allComp = t;
			return el;
		}
	}

	return null;
}
/**
 * LinesPanel constructor comment.
 */
public void dispose() {
	for (int i = 0; i < allComp.length; i++){
		allComp[i] = null;
	}
}
/**
 * LinesPanel constructor comment.
 */
public Item findBoxRelatedTo(BoxItem it, int dir) {
	// Search connection lines
	BoxItem ret = null;
	for (int i = 0; i < allComp.length; i++){
		BoxItem el = allComp[i];
		if (el == it)
			continue;

		switch (dir) {
			case Item.UP:
				if (el.y < it.y || (el.y == it.y && el.x > it.x)) {
					if (ret == null || el.y > ret.y || (el.y == ret.y && el.x < ret.x))
						ret = el;
				}
				break;
			case Item.DOWN:
				if (el.y > it.y || (el.y == it.y && el.x < it.x)) {
					if (ret == null || el.y < ret.y || (el.y == ret.y && el.x > ret.x))
						ret = el;
				}
				break;
			case Item.LEFT:
				if (el.x < it.x || (el.x == it.x && el.y < it.y)) {
					if (ret == null || el.x > ret.x || (el.x == ret.x && el.y > ret.y))
						ret = el;
				}
				break;
			case Item.RIGHT:
				if (el.x > it.x || (el.x == it.x && el.y > it.y)) {
					if (ret == null || el.x < ret.x || (el.x == ret.x && el.y < ret.y))
						ret = el;
				}
				break;
			case Item.NEXT:
				if (el.element.getId() > it.element.getId()) {
					if (ret == null || el.element.getId() < ret.element.getId())
						ret = el;
				}
				break;
			case Item.PREV:
				if (el.element.getId() < it.element.getId()) {
					if (ret == null || el.element.getId() > ret.element.getId())
						ret = el;
				}
				break;
		}
	}

	return ret;
}
/**
 * Elements constructor comment.
 */
public Item get(int i) {
	return allComp[i];
}
/**
 * LinesPanel constructor comment.
 */
public Item getElementAt(int x, int y) {
	for (int i = 0; i < allComp.length; i++){
		BoxItem c = allComp[i];
		if (x >= c.x && x < c.x + c.width
			&& y >= c.y && y < c.y + c.height) {
			return allComp[i];
		}
	}

	// Search connection lines
	return null;
}
/**
 * LinesPanel constructor comment.
 */
public Item getElementById(int id) {
	// Search connection lines
	for (int i = 0; i < allComp.length; i++){
		BoxItem el = allComp[i];
		if (el.element.getId() == id)
			return el;
	}

	return null;
}
/**
 * LinesPanel constructor comment.
 */
public void paint(Graphics g) {
	for (int i = allComp.length - 1; i >= 0; i--){
		BoxItem c = allComp[i];
		Graphics cg = g.create(c.x, c.y, c.width, c.height);
		try {
			if (c.highlighted.contains(c))
				cg.setColor(panel.highlightColor);
		    c.paint(cg);
	    	if (c.highlighted.contains(c))
				g.setColor(panel.defaultColor);
		} finally {
	 	   cg.dispose();
		}
	}
}
/**
 * LinesPanel constructor comment.
 */
public void reset() throws Exception {
	for (int i = 0; i < allComp.length; i++)
		allComp[i].reset();
}
/**
 * LinesPanel constructor comment.
 */
public void rezoom() {
	for (int i = 0; i < allComp.length; i++) {
		allComp[i].rezoom();
	}
}
/**
 * LinesPanel constructor comment.
 */
public void save(XmlCreator xml) throws Exception {
	for (int i = 0; i < allComp.length; i++) {
		XmlCreator elem = xml.addSection("element");
		elem.addAttribute("type", BoxItem.TYPE);
		allComp[i].save(elem);
	}
}
/**
 * Elements constructor comment.
 */
public int size() {
	return allComp.length;
}
/**
 * LinesPanel constructor comment.
 */
public void up(BoxItem comp) {
	for (int i = 1; i < allComp.length; i++){
		if (allComp[i] == comp) {
			allComp[i] = allComp[0];
			allComp[0] = comp;
		}
	}
}
}
