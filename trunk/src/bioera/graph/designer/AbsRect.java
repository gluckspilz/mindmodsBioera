/* AbsRect.java v 1.0.9   11/6/04 7:15 PM
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

public class AbsRect {
	public int x, y, w, h;
/**
 * AbsRect constructor comment.
 */
public AbsRect(int x1, int y1, int w1, int h1) {
	x = x1;
	y = y1;
	w = w1;
	h = h1;
}
/**
 * AbsRect constructor comment.
 */
public boolean contains(AbsRect r) {
	return toAbsRectangle().contains(r.toAbsRectangle());
}
/**
 * AbsRect constructor comment.
 */
public java.awt.Rectangle toAbsRectangle() {
	int x1 = w < 0 ? x + w : x;
	int y1 = h < 0 ? y + h : y;
	return new java.awt.Rectangle(x1, y1, Math.abs(w), Math.abs(h));
}
}
