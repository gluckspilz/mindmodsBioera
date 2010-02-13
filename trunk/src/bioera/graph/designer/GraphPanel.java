/* GraphPanel.java v 1.0.9   11/6/04 7:15 PM
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
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import bioera.*;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.graph.chart.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class GraphPanel extends DBCanvas {
	JFrame frame;
	public TieSet connections;
	public BoxSet boxes;
	java.awt.Color highlightColor = java.awt.Color.red;
	java.awt.Color defaultColor = java.awt.Color.black;

	boolean connecting = false;
	int x1, y1, x2, y2;
/**
 * LinesPanel constructor comment.
 */
public GraphPanel(JFrame f) {
	super();
	frame = f;
	connections = new TieSet(this);
	boxes = new BoxSet(this);
}
/**
 * LinesPanel constructor comment.
 */
public void deleteElement(Item elem) {
	if (elem instanceof BoxItem) {
		Element element = ((BoxItem)elem).element;
		if (element instanceof Display) {
			// Remove also from runtime
			Chart chart = ((Display)element).getChart();
			//System.out.println("c1 " + chart.getBounds());
			Main.app.runtimeFrame.removeChart(chart);
			//Main.app.runtimeFrame.show();
                        Main.app.runtimeFrame.setVisible();
		}

		boxes.delete(elem);

		// Delete all connections to this element
		connections.deleteConnectedTo(elem);

		// Delete from runtime processor
		try {
			Main.app.processor.remove(element);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	} else if (elem instanceof TieItem) {
		TieItem t = (TieItem) connections.delete(elem);
		if (t != null) {
			if (t.destPin.input) {
				Element e1 = t.src.element;
				Element e2 = t.dest.element;
				//Pipe p1 = e1.getOutputByName(t.srcPin.pipe.getName());
				//Pipe p2 = e2.getInputByName(t.destPin.pipe.getName());
				Pipe p1 = t.srcPin.pipe;
				Pipe p2 = t.destPin.pipe;
				//System.out.println("p1=" + p1);
				//System.out.println("p2=" + p2);
				if (p1 instanceof PipeDistributor) {
					boolean ret = ((PipeDistributor) p1).unregister(p2);
					//System.out.println("Unregistered tie " + ret);
					//System.out.println("All elements:\n" + bioera.runtime.RuntimeManager.runtimeManager.printAllElements());
				} else {
					System.out.println("Error: Source is not a distributor");
				}
			} else {
				System.out.println("Error: Source is not an output");
			}
		}
	}
}
/**
 * LinesPanel constructor comment.
 */
public void dispose() {
	connections.dispose();
	boxes.dispose();
}
/**
 * LinesPanel constructor comment.
 */
public Item getElementAt(int x, int y) {
	Item ret = boxes.getElementAt(x, y);
	if (ret != null)
		return ret;

	// Search connection lines
	return connections.getElementAt(x, y);
}
/**
 * LinesPanel constructor comment.
 */
public Item getElementById(int id) {
	return boxes.getElementById(id);
}
/**
 * LinesPanel constructor comment.
 */
public void loadConfiguration() throws Exception {

}
/**
 * LinesPanel constructor comment.
 */
public void paintBuffer(java.awt.Graphics g) {
//	Color c = g.getColor();
//	g.setColor(Color.black);
	boxes.paint(g);
//	g.setColor(Color.darkGray);
	connections.paint(g);
//	g.setColor(c);
	Item.highlighted.paint(g);


	if (connecting)
		g.drawLine(x1, y1, x2, y2);
}
/**
 * LinesPanel constructor comment.
 */
public void resetElements() throws Exception {
	boxes.reset();
	connections.reset();
}
}
