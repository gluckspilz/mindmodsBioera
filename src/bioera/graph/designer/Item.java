/* Item.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.config.*;

public abstract class Item {
//	static Item highlighted = null;
	static Highlight highlighted = new Highlight();
	private static int uniqueCounter = 1;
	int uniqueId = uniqueCounter++;

	public static final int UP = 1;
	public static final int DOWN = 2;
	public static final int RIGHT = 3;
	public static final int LEFT = 4;
	public static final int NEXT = 5;
	public static final int PREV = 6;
public Item(String uniqueMark) {
	super();
}
public void addPopupMenuItems(javax.swing.JPopupMenu menu) {
}
public void destroy() {
}
public abstract int getX1();
public abstract int getX2();
public abstract int getY1();
public abstract int getY2();
public void load(XmlConfigSection xml) throws Exception {

}
public void reset() {
	//highlighted = null;
}
public void save(XmlCreator xml) throws Exception {

}
public abstract void setX1(int x);
public abstract void setY1(int x);
}
