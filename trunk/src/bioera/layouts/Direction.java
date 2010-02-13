/* Direction.java v 1.0.9   11/6/04 7:15 PM
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

interface Direction {
	public final static int CENTER = 0;
	public final static int NORTH = 1;
	public final static int NORTHEAST = 2;
	public final static int EAST = 3;
	public final static int SOUTHEAST = 4;
	public final static int SOUTH = 5;
	public final static int SOUTHWEST = 6;
	public final static int WEST = 7;
	public final static int NORTHWEST = 8;

	public final static int LEFT = WEST;
	public final static int RIGHT = EAST;
	public final static int TOP	= NORTH;
	public final static int BOTTOM = SOUTH;
	public final static int UP	= NORTH;
	public final static int DOWN = SOUTH;
}
