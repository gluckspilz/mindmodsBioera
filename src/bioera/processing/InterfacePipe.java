/* InterfacePipe.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.processing;

import java.lang.*;

/**
 * Creation date: (9/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski

	This pipe is abstract in the sense, that is doesn't physically exchange data
	It can only be used as a symbolic connection between two remote pipes (e.g. located in two different designs)
	Initially it is compatible with any pipe, but if connected from one side, then it can be connected from the other side only to a compatible pipe.
 
 */
public abstract class InterfacePipe extends Pipe {
	public final static int TYPE = PipeTypes.INTERFACE;
public InterfacePipe(Element e) {
	super(e);
}
public int getType() {
	return TYPE;
}
}
