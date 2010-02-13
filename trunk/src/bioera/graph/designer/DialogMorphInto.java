/* DialogMorphInto.java v 1.0.9   11/6/04 7:15 PM
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
import java.awt.event.*;
import javax.swing.*;
import bioera.processing.*;
import bioera.*;
import bioera.graph.runtime.*;
import bioera.graph.chart.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class DialogMorphInto extends DialogNewElement {
	BoxItem prevItem;
//	BoxItem box;
/**
 * EdiDialog constructor comment.
 */
public DialogMorphInto(JFrame f) {
	super(f, "Replace With");
	prevItem = (BoxItem) Item.highlighted.get(0);
}
	/**
	 * Invoked when an action occurs.
	 */
public void actionAdd() {
}
	/**
	 * Invoked when an action occurs.
	 */
public void actionOK() {
	//System.out.println("here");
	
	newItem = null;
	super.actionOK();

	if (newItem == null) {
		//System.out.println("no new element");
		return;
	}

	// Get current element
	Element prevElem = prevItem.element;

	newItem.x = prevItem.x + 10;
	newItem.y = prevItem.y + 10;
	
	Element newElem = newItem.element;
	newElem.setName(prevElem.getName());
	
	// Now copy all graphic connections from old element to new
	TieSet ts = Main.app.designFrame.panel.connections;
	for (int i = 0; i < ts.allConnections.length; i++){
		TieItem ti = ts.allConnections[i];
		if (ti.src == prevItem) {
			int pi = prevItem.getPinIndex(ti.srcPin);
			if (pi >= 0 && newItem.outpins.length > pi)
				ti.srcPin = newItem.outpins[pi];
			else {
				ts.delete(ti);
				i--;
				continue;
			}
			ti.src = newItem;
		}
		if (ti.dest == prevItem) {
			int pi = prevItem.getPinIndex(ti.destPin);
			if (pi >= 0 && newItem.inpins.length > pi)
				ti.destPin = newItem.inpins[pi];
			else {
				ts.delete(ti);
				i--;
				continue;
			}				
			ti.dest = newItem;			
		}
	}	
	
	int n = Math.min(prevElem.getInputsCount(), newElem.getInputsCount());
	for (int i = 0; i < n; i++){
		BufferedPipe input = (BufferedPipe) prevElem.getInput(i);
		PipeDistributor dist[] = input.getConnectedDistributors();
		for (int d = 0; d < dist.length; d++){
			dist[d].register(newElem.getInput(i));
		}		
	}
	
	n = Math.min(prevElem.getOutputsCount(), newElem.getOutputsCount());
	for (int i = 0; i < n; i++){
		PipeDistributor dist = (PipeDistributor) prevElem.getOutput(i);
		for (int d = 0; d < dist.getConnectedCount(); d++){
			//System.out.println("registering " + dist.getConnectedPipe(d).getName() + "("+dist.getConnectedPipe(d).getId()+")" + " on " + newElem.getOutput(i).getName() + "(" + newElem.getOutput(i).getId() + ") ");
			if (dist.getConnectedPipe(d).compatibleWith(newElem.getOutput(i)))
				newElem.registerReceiver(newElem.getOutput(i), dist.getConnectedPipe(d));
		}		
	}	

	//System.out.println("deleted");
	try {
		newElem.setElementProperties(prevElem.getElementProperties());
	} catch (Exception e) {
		//System.out.println("Couldn't set properties");
	}
	Main.app.designFrame.panel.deleteElement(prevItem);
	newItem.recalculate();
	Main.app.processor.reInitializeAll();
	Main.app.designFrame.repaint();
}
}
