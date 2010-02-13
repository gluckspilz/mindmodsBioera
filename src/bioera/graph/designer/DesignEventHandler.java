/* DesignEventHandler.java v 1.0.9   11/6/04 7:15 PM
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


import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import bioera.*;
import bioera.processing.*;
import bioera.graph.runtime.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class DesignEventHandler implements MouseListener, MouseMotionListener, ActionListener, WindowListener, KeyListener {
	boolean disposed = false;
	public static final int ELEM_STEP = 10;
	boolean resizeElement = false;
	boolean dragElement = false;
	boolean dragLine = false;
	boolean dragHighlight = false;
	long moveWhen = 0;
	GraphPanel panel;
	DesignFrame designFrame;
	BoxItem activeBox;
	int pressedX, pressedY, // Last point - relatively to panel
		releasedX, releasedY,
		draggedX, draggedY,
		anchorX, anchorY,	// anchor in component - location relatively to component
		anchorR, anchorD;	// anchor in component - location relatively to component

	BoxItem lineDragBox;
	protected static boolean debug = bioera.Debugger.get("designer.eventhandler");
/**
 * MouseDebug constructor comment.
 */
public DesignEventHandler(DesignFrame f, GraphPanel p) {
	super();
	designFrame = f;
	panel = p;
}
	/**
	 * Invoked when an action occurs.
	 */
public final void actionPerformed(java.awt.event.ActionEvent e) {
	if (disposed)
		System.out.println("=======Disposed Event handler=========");

	String command = e.getActionCommand();
//	System.out.println("command: '" + command + "' " + command.getClass() + " " + e.getSource().getClass());
//	Component c = (Component) e.getSource();
//	System.out.println("x=" + c.getX() + "  y=" + c.getY());

	try {
		if (e.getSource() instanceof CommandMenuItem) {
			ActionHandler.perform(command, ((CommandMenuItem) e.getSource()).actionType);
		} else {
			ActionHandler.perform(command, 0);
		}
	} catch (Exception exc) {
		System.out.println("Command '"+command +"' error: " + exc);
	}

	//System.out.println("source: " +e.getSource());
	panel.repaint();
}
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 */
public final void highlightItem(Item elem, int modifiers) {
	if ((modifiers & MouseEvent.CTRL_MASK) == 0) {
		elem.highlighted.clear();
		elem.highlighted.add(elem);
	} else {
		if (elem.highlighted.contains(elem))
			elem.highlighted.remove(elem);
		else
			elem.highlighted.add(elem);
	}
}
	/**
	 * Invoked when a key has been pressed.
	 */
public final void keyPressed(java.awt.event.KeyEvent e) {}
	/**
	 * Invoked when a key has been released.
	 */
public final void keyReleased(KeyEvent e) {
	//System.out.println("key is " + e.getKeyCode());
	try {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_DELETE:
				ActionHandler.perform(Commands.DELETE);
				e.consume();
				break;
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_SPACE:
				ActionHandler.performKey(' ');
				e.consume();
				break;
			case KeyEvent.VK_INSERT:
				ActionHandler.perform(Commands.NEW);
				e.consume();
				break;
			case KeyEvent.VK_F4:
				e.consume();
				break;
			default:
				ActionHandler.performKey(e.getKeyChar());
				break;
		}
	} catch (Exception exc) {
		System.out.println("Key command (code=" + e.getKeyCode() + ") error: " + exc);
	}
}
	/**
	 * Invoked when a key has been typed.
	 * This event occurs when a key press is followed by a key release.
	 */
public final void keyTyped(java.awt.event.KeyEvent e) {}
	/**
	 * Invoked when the mouse has been clicked on a component.
	 */
public final void mouseClicked(java.awt.event.MouseEvent e) {
	if ((e.getModifiers() & MouseEvent.CTRL_MASK) == 0 && Item.highlighted.contains(activeBox))
		highlightItem(activeBox, e.getModifiers());

	if (e.getClickCount() == 2) {
		try {
			ActionHandler.perform(Commands.PROPERTIES);
		} catch (Exception ex) {
			System.out.println("DoubleClick error: " + ex);
		}
	}
}
	/**
	 * Invoked when a mouse button is pressed on a component and then
	 * dragged.  Mouse drag events will continue to be delivered to
	 * the component where the first originated until the mouse button is
	 * released (regardless of whether the mouse position is within the
	 * bounds of the component).
	 */
public final void mouseDragged(java.awt.event.MouseEvent e) {
	if ((!dragElement && !dragLine && !resizeElement && !dragHighlight) || e.getWhen() <= moveWhen)
		return;

	int x1 = e.getX(), y1 = e.getY();

	//System.out.println("dragged to " + x1 + " " + y1);

	if (!dragLine && Math.abs(x1 - draggedX) < ELEM_STEP / 2 && Math.abs(y1 - draggedY) < ELEM_STEP / 2)
		return;

	if (dragElement) {
		int diffX = ((x1 - pressedX) / ELEM_STEP) * ELEM_STEP,
			diffY = ((y1 - pressedY) / ELEM_STEP) * ELEM_STEP;
		for (int i = Item.highlighted.size() - 1; i >= 0; i--){
			Item it = Item.highlighted.get(i);
			java.awt.Point p = Item.highlighted.getMarkedPoint(i);
			it.setX1(p.x + diffX);
			it.setY1(p.y + diffY);
			//System.out.println("moved " + i + "  " + p.x);
		}
		//System.out.println("moved " + Item.highlighted.size());
	} else if (dragLine) {
		panel.x2 = x1;
		panel.y2 = y1;
	} else if (resizeElement) {
		activeBox.width = ((x1 + anchorR - activeBox.x) / ELEM_STEP) * ELEM_STEP;
		activeBox.height = ((y1 + anchorD - activeBox.y) / ELEM_STEP) * ELEM_STEP;
		activeBox.recalculate();
		//System.out.println("w=" + comp.width + "   " + "h=" + comp.height);
	} else if (dragHighlight) {
		Item.highlighted.w = x1 - Item.highlighted.x1;
		Item.highlighted.h = y1 - Item.highlighted.y1;
	}

	panel.repaint();

	draggedX = x1; draggedY = y1;
	moveWhen = System.currentTimeMillis();
}
	/**
	 * Invoked when the mouse enters a component.
	 */
public final void mouseEntered(java.awt.event.MouseEvent e) {
	panel.repaint();
}
	/**
	 * Invoked when the mouse exits a component.
	 */
public final void mouseExited(java.awt.event.MouseEvent e) {
	//Component c = e.getComponent();
	//if (c == panel)
		//return;
	//Elem elem = (Elem) c;
	//if (elem.highlightSwitch != -1) {
		//elem.highlightSwitch = -1;
		//elem.repaint();
	//}
}
	/**
	 * Invoked when the mouse button has been moved on a component
	 * (with no buttons no down).
	 */
public final void mouseMoved(java.awt.event.MouseEvent e) {
	if (!dragLine)
		return;

	panel.x2 = e.getX();
	panel.y2 = e.getY();

	panel.repaint();

//	System.out.println("moved " + e.getComponent().getClass().getName());
//	System.out.println("switch " + i);
}
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 */
public final void mousePressed(java.awt.event.MouseEvent e) {

	draggedX = pressedX = e.getX();
	draggedY = pressedY = e.getY();

	Item elem = panel.getElementAt(pressedX, pressedY);
	int modifiers = e.getModifiers();

	// --------- POPUP menu -------------

	if ((modifiers & MouseEvent.BUTTON3_MASK) > 0) {
		if (elem instanceof BoxItem)
			activeBox = (BoxItem) elem;
		JPopupMenu menu = new PopupMenu((ActionListener) this);
		menu.add(new JMenuItem(Commands.NEW));
		menu.add(new JMenuItem(Commands.START));
		menu.add(new JMenuItem(Commands.STOP));
		menu.add(new JMenuItem(Commands.EDIT_MODE));
		menu.add(new JMenuItem(Commands.VIEW_MODE));
		if (elem == null && Clipboard.list.size() > 0)
			menu.add(new JMenuItem(Commands.PASTE));
		if (elem != null && Item.highlighted.size() > 0) {
			menu.add(new JMenuItem(Commands.COPY));
		}
		if (Item.highlighted.size() > 0)
			menu.add(new JMenuItem(Commands.MOVE));
		if (elem != null)
			elem.addPopupMenuItems(menu);
		designFrame.addPopup(menu);
//		menu.show(designFrame.frame, x, y);
		menu.show(panel, pressedX, pressedY);
		return;
	}

	// ----------- EMPTY AREA CLICKED --------------

	if (elem == null) {
		if ((modifiers & MouseEvent.CTRL_MASK) == 0) {
			elem.highlighted.clear();
		}
		reset();
		Item.highlighted.x1 = pressedX;
		Item.highlighted.y1 = pressedY;
		dragHighlight = true;
		//System.out.println("high");
		return;
	}

	// ------------- ELEMENT CLICKED ------------

	if (elem instanceof BoxItem) {
		activeBox = (BoxItem) elem;
	} else {
		highlightItem(elem, modifiers);
		panel.repaint();
		return;
	}


	// ------------- BOX_ITEM CLICKED ------------

	// COORDINATES withing the element
	anchorX = pressedX - activeBox.x;
	anchorY = pressedY - activeBox.y;

	//System.out.println("Anchor: " + anchorX + " " + anchorY);

	BoxPin sw = activeBox.getPin(anchorX, anchorY);
	if (sw != null) {
		if (sw == activeBox.activePin) {
			activeBox.activePin = null;  // Clicked again the same pin
			//System.out.println("unmark pin " + sw);
		} else if (lineDragBox == null) {
			// Start connecting
			activeBox.activePin = sw;
			lineDragBox = activeBox;
			panel.x1 = panel.x2 = pressedX;
			panel.y1 = panel.y2 = pressedY;
			panel.connecting = true;
			dragLine = true;
			if (debug)
				System.out.println("connecting " + sw);
		} else if (lineDragBox != null) {
			// We have connection
			activeBox.activePin = sw;
			if (activeBox == lineDragBox) {
				new ErrorDialog(designFrame.frame).show("Connecting to the same component not implemented");
			} else if (activeBox.activePin.input == lineDragBox.activePin.input) {
				new ErrorDialog(designFrame.frame).show("Must connect output to input");
			} else if (!activeBox.activePin.pipe.compatibleWith(lineDragBox.activePin.pipe)) {
				new ErrorDialog(designFrame.frame).show("Input-Output types are not compatible");
			} else {
				// source is output, destination is input
				Pipe src, dest;
				if (lineDragBox.activePin.input) {
					src = activeBox.activePin.pipe;
					dest = lineDragBox.activePin.pipe;
				} else {
					src = lineDragBox.activePin.pipe;
					dest = activeBox.activePin.pipe;
				}

				panel.connections.add(new TieItem(src, dest));
				Main.app.processor.getElementSet().connectPipes(src, dest);
				Main.app.processor.reInitializeAll();
				//System.out.println("Add connection to set");
			}
			reset();
		}
	} else if (activeBox.isResizing(anchorX, anchorY)) {
		anchorR = activeBox.width - anchorX;
		anchorD = activeBox.height - anchorY;
		resizeElement = true;
		//System.out.println("Resizing " + anchorX + " " + anchorY);
	} else {
		if ((modifiers & MouseEvent.CTRL_MASK) > 0 || !elem.highlighted.contains(elem))
			highlightItem(elem, modifiers);
		panel.boxes.up(activeBox);
		Item.highlighted.markPoints();	// Remember location of all highlighted elements
		dragElement = true;
		if (debug)
			System.out.println("Component at " + activeBox.x + " " + activeBox.y);
	}

	panel.repaint();
}
	/**
	 * Invoked when a mouse button has been released on a component.
	 */
public final void mouseReleased(java.awt.event.MouseEvent e) {
//	System.out.println("mouse released");

	releasedX = e.getX();
	releasedY = e.getY();

	if (dragHighlight) {
		Item.highlighted.add(panel.boxes);
		Item.highlighted.add(panel.connections);
	}

	if (dragElement) {
		//System.out.println("dragged " + (pressedX - releasedX) + "  " + (pressedY - releasedY));
		//System.out.println("pressedX=" + pressedX + "releasedX=" + releasedX);
		if (Math.abs(pressedX - releasedX) > ELEM_STEP || Math.abs(pressedY - releasedY) > ELEM_STEP) {
			// Was shifted, do not change highlighting
			//System.out.println("here");
		} else {
			//if ((e.getModifiers() & MouseEvent.CTRL_MASK) == 0 && activeBox.highlighted.contains(activeBox))
				//highlightItem(activeBox, e.getModifiers());
		}
	}

/*
	// --------- POPUP menu -------------

	if ((modifiers & MouseEvent.BUTTON3_MASK) > 0) {
		if (elem instanceof BoxItem)
			activeBox = (BoxItem) elem;
		JPopupMenu menu = new PopupMenu((ActionListener) this);
		menu.add(new JMenuItem(Commands.NEW));
		if (elem != null)
			elem.addPopupMenuItems(menu);
		designFrame.addPopup(menu);
//		menu.show(designFrame.frame, x, y);
		menu.show(panel, pressedX, pressedY);
		return;
	}

	// ----------- EMPTY AREA CLICKED --------------

	if (elem == null) {
		Item.highlighted.list.clear();
		reset();
		return;
	}

*/

/*
	// ------------- ELEMENT CLICKED ------------

	if (elem instanceof BoxItem) {
		activeBox = (BoxItem) elem;
	} else {
		if ((modifiers & MouseEvent.CTRL_MASK) == 0) {
			elem.highlighted.clear();
		}
		elem.highlighted.add(elem);
		panel.repaint();
		return;
	}


	// ------------- BOX_ITEM CLICKED ------------

	// COORDINATES withing the element
	anchorX = pressedX - activeBox.x;
	anchorY = pressedY - activeBox.y;

	//System.out.println("Anchor: " + anchorX + " " + anchorY);

	BoxPin sw = activeBox.getPin(anchorX, anchorY);
	if (sw != null) {
		if (sw == activeBox.activePin) {
			activeBox.activePin = null;  // Clicked again the same pin
			//System.out.println("unmark pin " + sw);
		} else if (lineDragBox == null) {
			// Start connecting
			activeBox.activePin = sw;
			lineDragBox = activeBox;
			panel.x1 = panel.x2 = pressedX;
			panel.y1 = panel.y2 = pressedY;
			panel.connecting = true;
			dragLine = true;
			if (debug)
				System.out.println("connecting " + sw);
		} else if (lineDragBox != null) {
			// We have connection
			activeBox.activePin = sw;
			if (activeBox == lineDragBox) {
				new ErrorDialog(designFrame.frame).show("Connecting to the same component not implemented");
			} else if (activeBox.activePin.input == lineDragBox.activePin.input) {
				new ErrorDialog(designFrame.frame).show("Must connect output to input");
			} else if (activeBox.activePin.type != lineDragBox.activePin.type) {
				new ErrorDialog(designFrame.frame).show("Input-Output types are not compatible");
			} else {
				// source is output, destination is input
				if (lineDragBox.activePin.input)
					panel.addConnection(activeBox, lineDragBox);
				else
					panel.addConnection(lineDragBox, activeBox);


				//System.out.println("Add connection to set");
			}
			reset();
		}
	} else if (activeBox.isResizing(anchorX, anchorY)) {
		anchorR = activeBox.width - anchorX;
		anchorD = activeBox.height - anchorY;
		resizeElement = true;
		//System.out.println("Resizing " + anchorX + " " + anchorY);
	} else {
		if ((modifiers & MouseEvent.CTRL_MASK) == 0) {
			elem.highlighted.clear();
		}
		elem.highlighted.add(elem);
		if (debug)
			System.out.println("Component at " + activeBox.x + " " + activeBox.y);
		panel.boxes.up(activeBox);
		dragElement = true;
	}
*/
	panel.requestFocus();

	if (dragElement || dragHighlight) {
		reset();
	}
}
	/**
	 * Invoked when a mouse button has been released on a component.
	 */
public final void reset() {
	try {
		Item.highlighted.reset();
		dragHighlight = false;
		dragLine = false;
		dragElement = false;
		resizeElement = false;
		panel.connecting = false;
		lineDragBox = null;
		panel.resetElements();
		panel.repaint();
	} catch (Exception e) {
		e.printStackTrace();
	}
}
	/**
	 * Invoked when a mouse button has been released on a component.
	 */
public final void resetHighlighted() {

}
	/**
	 * Invoked when the window is set to be the user's
	 * active window, which means the window (or one of its
	 * subcomponents) will receive keyboard events.
	 */
public final void windowActivated(java.awt.event.WindowEvent e) {}
	/**
	 * Invoked when a window has been closed as the result
	 * of calling dispose on the window.
	 */
public final void windowClosed(java.awt.event.WindowEvent e) {}
	/**
	 * Invoked when the user attempts to close the window
	 * from the window's system menu.  If the program does not
	 * explicitly hide or dispose the window while processing
	 * this event, the window close operation will be cancelled.
	 */
public final void windowClosing(java.awt.event.WindowEvent e) {
	((java.awt.Window) e.getSource()).dispose();
}
	/**
	 * Invoked when a window is no longer the user's active
	 * window, which means that keyboard events will no longer
	 * be delivered to the window or its subcomponents.
	 */
public final void windowDeactivated(java.awt.event.WindowEvent e) {}
	/**
	 * Invoked when a window is changed from a minimized
	 * to a normal state.
	 */
public final void windowDeiconified(java.awt.event.WindowEvent e) {}
	/**
	 * Invoked when a window is changed from a normal to a
	 * minimized state. For many platforms, a minimized window
	 * is displayed as the icon specified in the window's
	 * iconImage property.
	 * @see Frame#setIconImage
	 */
public final void windowIconified(java.awt.event.WindowEvent e) {}
	/**
	 * Invoked the first time a window is made visible.
	 */
public final void windowOpened(java.awt.event.WindowEvent e) {}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
