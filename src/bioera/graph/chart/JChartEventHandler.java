/* ChartEventHandler.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.graph.chart;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import bioera.*;
import bioera.processing.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class JChartEventHandler implements ComponentListener, WindowListener, FocusListener {
	protected JChartDialog chartDialog;
	protected boolean isActive;
/**
 * DisplayWindowEventHandler constructor comment.
 */
public JChartEventHandler(JChartDialog w) {
	super();
	chartDialog = w;
}
	/**
	 * Invoked when the component has been made invisible.
	 */
public void componentHidden(java.awt.event.ComponentEvent e) {
//	window.updateRelatives();
}
	/**
	 * Invoked when the component's position changes.
	 */
public void componentMoved(java.awt.event.ComponentEvent e) {
	if (isActive) {
		chartDialog.updateRelativeCoordinates();
	}
}
	/**
	 * Invoked when the component's size changes.
	 */
public void componentResized(java.awt.event.ComponentEvent e) {}
	/**
	 * Invoked when the component has been made visible.
	 */
public void componentShown(java.awt.event.ComponentEvent e) {}
	/**
	 * Invoked when a component gains the keyboard focus.
	 */
public void focusGained(java.awt.event.FocusEvent e) {
	chartDialog.chart.setAtTopLayer();
	//System.out.println("Set at layer " +chartDialog.chart.getLayer() + "  " + chartDialog.chart.getClass());
}
	/**
	 * Invoked when a component loses the keyboard focus.
	 */
public void focusLost(java.awt.event.FocusEvent e) {}
	/**
	 * Invoked when the window is set to be the user's
	 * active window, which means the window (or one of its
	 * subcomponents) will receive keyboard events.
	 */
public void windowActivated(java.awt.event.WindowEvent e) {
	isActive = true;
}
	/**
	 * Invoked when a window has been closed as the result
	 * of calling dispose on the window.
	 */
public void windowClosed(java.awt.event.WindowEvent e) {}
	/**
	 * Invoked when the user attempts to close the window
	 * from the window's system menu.  If the program does not
	 * explicitly hide or dispose the window while processing
	 * this event, the window close operation will be cancelled.
	 */
public void windowClosing(java.awt.event.WindowEvent e) {
	System.out.println("closing chart");
}
	/**
	 * Invoked when a window is no longer the user's active
	 * window, which means that keyboard events will no longer
	 * be delivered to the window or its subcomponents.
	 */
public void windowDeactivated(java.awt.event.WindowEvent e) {
	isActive = false;
}
	/**
	 * Invoked when a window is changed from a minimized
	 * to a normal state.
	 */
public void windowDeiconified(java.awt.event.WindowEvent e) {}
	/**
	 * Invoked when a window is changed from a normal to a
	 * minimized state. For many platforms, a minimized window
	 * is displayed as the icon specified in the window's
	 * iconImage property.
	 * @see Frame#setIconImage
	 */
public void windowIconified(java.awt.event.WindowEvent e) {}
	/**
	 * Invoked the first time a window is made visible.
	 */
public void windowOpened(java.awt.event.WindowEvent e) {}
}
