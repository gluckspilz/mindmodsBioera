/* CloseWindowsListener.java v 1.0.9   11/6/04 7:15 PM
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


import java.awt.event.WindowListener;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class CloseWindowsListener implements WindowListener  {
/**
 * CloseWindowsListener constructor comment.
 */
public CloseWindowsListener() {
	super();
}
	/**
	 * Invoked when the window is set to be the user's
	 * active window, which means the window (or one of its
	 * subcomponents) will receive keyboard events.
	 */
public void windowActivated(java.awt.event.WindowEvent e) {}
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
	Main.systemExit();
}
	/**
	 * Invoked when a window is no longer the user's active
	 * window, which means that keyboard events will no longer
	 * be delivered to the window or its subcomponents.
	 */
public void windowDeactivated(java.awt.event.WindowEvent e) {}
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
