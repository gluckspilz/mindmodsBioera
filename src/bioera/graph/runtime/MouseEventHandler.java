/* MouseEventHandler.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.graph.runtime;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import bioera.*;
import bioera.graph.chart.*;
import bioera.processing.*;
import bioera.graph.runtime.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class MouseEventHandler  implements MouseListener {
	RuntimeFrame frame;
	int x,y;
	Component component;

	protected static boolean debug = bioera.Debugger.get("runtime.eventhandler.mouse");
/**
 * MouseEventHandler constructor comment.
 */
public MouseEventHandler(RuntimeFrame f) {
	super();
	frame = f;
}
	/**
	 * Invoked when the mouse has been clicked on a component.
	 */
public void mouseClicked(java.awt.event.MouseEvent e) {
}
	/**
	 * Invoked when the mouse enters a component.
	 */
public void mouseEntered(java.awt.event.MouseEvent e) {}
	/**
	 * Invoked when the mouse exits a component.
	 */
public void mouseExited(java.awt.event.MouseEvent e) {}
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 */
public void mousePressed(java.awt.event.MouseEvent e) {

		//System.out.println("mouse pressed");

	component = (Component) e.getSource();

	x = e.getX();
	y = e.getY();

	if (component != frame.panel) {
		x += component.getX();
		y += component.getY();
	}

	PopupMenu menu = new PopupMenu();
	menu.add(new MenuItem("Nothing"));
	if (component == frame.panel) {
		menu.add(new MenuItem("Start"));
		menu.add(new MenuItem("Stop"));
		menu.add(new MenuItem("Reinit"));
		menu.add(new MenuItem("Exit"));
	} if (component != frame.panel) {
		menu.add(new MenuItem("Properties"));
	}
	frame.frame.add(menu);
	menu.addActionListener(frame.runtimeEventHandler);
	menu.show(frame.frame, x, y);
}
	/**
	 * Invoked when a mouse button has been released on a component.
	 */
public void mouseReleased(java.awt.event.MouseEvent e) {
//	System.out.println("mouse released");

}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
