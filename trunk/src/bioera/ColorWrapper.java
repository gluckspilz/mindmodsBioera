/* ColorWrapper.java v 1.0.9   11/6/04 7:15 PM
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

package bioera;

public class ColorWrapper {
	public int red;
	public int green;
	public int blue;
	public int alpha;
/**
 * Color constructor comment.
 */
public ColorWrapper() {
	super();
}
/**
 * Color constructor comment.
 */
public ColorWrapper(java.awt.Color c) {
	red = c.getRed();
	green = c.getGreen();
	blue = c.getBlue();
	alpha = c.getAlpha();
}
/**
 * Color constructor comment.
 */
public ColorWrapper(String s) {
	s = s.trim();
	java.util.StringTokenizer tok = new java.util.StringTokenizer(s, "(),");
	if (tok.hasMoreTokens()) 
		red = Integer.parseInt(tok.nextToken());
	if (tok.hasMoreTokens())
		green = Integer.parseInt(tok.nextToken());
	if (tok.hasMoreTokens())
		blue = Integer.parseInt(tok.nextToken());

	//System.out.println("color set to " + this);
}
/**
 * Color constructor comment.
 */
public java.awt.Color getAWTColor() {
	return new java.awt.Color(red, green, blue);
}
/**
 * Color constructor comment.
 */
public String toString() {
	return "(" + red + "," + green + "," + blue + ")";
}
}
