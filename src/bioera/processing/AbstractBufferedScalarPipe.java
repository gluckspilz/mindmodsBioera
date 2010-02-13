/* AbstractBufferedScalarPipe.java v 1.0.9   11/6/04 7:15 PM
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

import java.io.*;
import java.util.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public abstract class AbstractBufferedScalarPipe extends ScalarPipe implements BufferedPipe {
	protected ScalarPipeDistributor connectedDistributors[] = new ScalarPipeDistributor[0];
/**
 * BufferedPipe constructor comment.
 */
public AbstractBufferedScalarPipe(Element e) {
	super(e);
}
/**
 * Insert the method's description here.
 * Creation date: (7/20/2003 12:48:03 PM)
 */
public void connectDistributor(PipeDistributor p) {
	if (p == null)
		return;
		
	connectedDistributors = (ScalarPipeDistributor[]) ProcessingTools.appendArray(connectedDistributors, p);
}
/**
 * Insert the method's description here.
 * Creation date: (7/20/2003 12:48:03 PM)
 */
public void disconnectDistributor(PipeDistributor p) {
	if (p == null)
		return;
	//System.out.println("removing " + ((Pipe) p).getName() + "("+((Pipe) p).getId()+")" + " from " + getName() + "(" + getId() + ") ");
	//System.out.println("len=" +  connectedDistributors.length);
	connectedDistributors = (ScalarPipeDistributor[]) ProcessingTools.removeAllFromArray(connectedDistributors, p);
	//System.out.println("len=" +  connectedDistributors.length);
	if (connectedDistributors.length > 0) {
		//System.out.println("OK");
		getElement().predecessorElement = connectedDistributors[0].getElement();
	} else {
		//System.out.println("empty");
		getElement().predecessorElement = null;
	}
}
/**
 * BufferedPipe constructor comment.
 */
public PipeDistributor[] getConnectedDistributors() {
	return connectedDistributors;
}
/**
 * BufferedPipe constructor comment.
 */
public boolean isConnected() {
	return connectedDistributors.length > 0;
}
}
