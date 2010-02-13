/* Alignment.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.layouts;

import java.awt.Rectangle;

public class Alignment implements Direction {

	public final static int FILL_NONE = 0;
	public final static int FILL_HORIZONTAL = 1;
	public final static int FILL_VERTICAL = 2;
	public final static int FILL_BOTH = 3;

	public static void alignInCell(Rectangle r, Rectangle cell, int alignment, int fill) {
		r.x = cell.x;
		r.y = cell.y;

		/* Horizontal fill */
		switch (fill) {
		  case FILL_BOTH:
		  case FILL_HORIZONTAL:
			r.width = cell.width;
			break;
		}

		/* Vertical fill */
		switch (fill) {
		  case FILL_BOTH:
		  case FILL_VERTICAL:
			r.height = cell.height;
			break;
		}

		/* Horizontal alignment */
		switch (alignment) {
		  case CENTER:
		  case NORTH:
		  case SOUTH:
			r.x += (cell.width - r.width)/2;
			break;
		  case WEST:
		  case NORTHWEST:
		  case SOUTHWEST:
			break;
		  case EAST:
		  case NORTHEAST:
		  case SOUTHEAST:
			r.x += cell.width - r.width;
			break;
		}

		/* Vertical alignment */
		switch (alignment) {
		  case CENTER:
		  case WEST:
		  case EAST:
			r.y += (cell.height - r.height)/2;
			break;
		  case NORTH:
		  case NORTHWEST:
		  case NORTHEAST:
			break;
		  case SOUTH:
		  case SOUTHWEST:
		  case SOUTHEAST:
			r.y += cell.height - r.height;
			break;
		}

	}
}
