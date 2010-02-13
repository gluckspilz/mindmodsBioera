/* GenericDialog.java v 1.0.9   11/6/04 7:15 PM
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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import bioera.processing.*;
import bioera.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class GenericDialog extends JDialog implements KeyListener{
	Window parentWindow;
/**
 * GenericDialog constructor comment.
 */
public GenericDialog(Dialog d, String name) {
	super(d, name);
	parentWindow = d;
	addKeyListener(this);
}
/**
 * GenericDialog constructor comment.
 */
public GenericDialog(Frame f, String name) {
	super(f, name);
	parentWindow = f;
	addKeyListener(this);
}
/**
 * GenericDialog constructor comment.
 */
public GenericDialog(JDialog d, String name) {
	super(d, name);
	parentWindow = d;
	addKeyListener(this);
}
/**
 * GenericDialog constructor comment.
 */
public GenericDialog(JFrame f, String name) {
	super(f, name);
	parentWindow = f;
	addKeyListener(this);
}
/**
 * GenericDialog constructor comment.
 */
public void dispose() {
	setVisible(false);
	parentWindow.repaint();
	super.dispose();
}
	/**
	 * Invoked when a key has been pressed.
	 */
public void keyPressed(java.awt.event.KeyEvent e) {}
	/**
	 * Invoked when a key has been released.
	 */
public void keyReleased(java.awt.event.KeyEvent e) {
	switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
		case KeyEvent.VK_ESCAPE:
			dispose();
			break;
		default:
			break;
	}
}
	/**
	 * Invoked when a key has been typed.
	 * This event occurs when a key press is followed by a key release.
	 */
public void keyTyped(java.awt.event.KeyEvent e) {}
/**
 * GenericDialog constructor comment.
 */
public void locateOnComponent(int x, int y, Component f) {
	x += f.getX();
	y += f.getY();

	//Dimension ms = getMaximumSize();
	//System.out.println("x=" + x);
	//System.out.println("y=" + y);
	//System.out.println("f.getX=" + f.getX());
	//System.out.println(".fgetY=" + f.getY());

	if (x + getWidth() > f.getX() + f.getWidth())
		x = (f.getX() + f.getWidth() - getWidth());

	if (y + getHeight() > f.getY() + f.getHeight())
		y = (f.getY() + f.getHeight() - getHeight());

	if (x < f.getX())
		x = f.getX();

	if (y < f.getY())
		y = f.getY();

	setLocation(x, y);
}
/**
 * GenericDialog constructor comment.
 */
public void locateOnComponent(Component f) {
	int x = f.getX() + (f.getWidth() - getWidth()) / 2;
	int y = f.getY() + (f.getHeight() - getHeight()) / 2;

	setLocation(x, y);
}
/**
 * GenericDialog constructor comment.
 */
public void locateOnWindow() {
	int x = parentWindow.getX() + (parentWindow.getWidth() - getWidth()) / 2;
	int y = parentWindow.getY() + (parentWindow.getHeight() - getHeight()) / 2;

	setLocation(x, y);
}
/**
 * GenericDialog constructor comment.
 */
public void shiftBounds() {
	int x = getX();
	int y = getY();

	Window f = parentWindow;

	//Dimension ms = getMaximumSize();
	//System.out.println("x=" + x);
	//System.out.println("y=" + y);
	//System.out.println("f.getX=" + f.getX());
	//System.out.println(".fgetY=" + f.getY());

	if (x + getWidth() > f.getX() + f.getWidth())
		x = (f.getX() + f.getWidth() - getWidth());

	if (y + getHeight() > f.getY() + f.getHeight())
		y = (f.getY() + f.getHeight() - getHeight());

	if (x < f.getX())
		x = f.getX();

	if (y < f.getY())
		y = f.getY();

        this.setSize(400,400);
	setLocation(x, y);
}
/**
 * GenericDialog constructor comment.
 */
public void show() {
	super.show();
	addKeyListener(this);
	shiftBounds();
}
}
