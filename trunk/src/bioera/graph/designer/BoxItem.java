/* BoxItem.java v 1.0.9   11/6/04 7:15 PM
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

import bioera.processing.*;
import bioera.*;
import bioera.graph.runtime.*;
import bioera.graph.chart.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.lang.reflect.*;
import bioera.config.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public abstract class BoxItem extends Item implements ChangePropertyListener, ChangeStatusListener {
	public final static int RESIZE_LENGTH = 15;
	public final static String TYPE = "proc";

	//public final static int DEFAULT_WIDTH = 60;
	//public final static int DEFAULT_HEIGHT = 90;

	int x, y, height, width, titleHeight, titleWidth;
	GraphPanel panel;
	BoxPin inpins[] = new BoxPin[0];
	BoxPin outpins[] = new BoxPin[0];
	BoxPin activePin;

	Element element;
/**
 * ChartElement constructor comment.
 */
private void addPin(int index, boolean input) {
	BoxPin pins[] = (input ? inpins : outpins);
	BoxPin t[] = new BoxPin[pins.length + 1];
	System.arraycopy(pins, 0, t, 0, pins.length);
	Pipe pipe = input ? element.getInput(index) : element.getOutput(index);
	BoxPin p = new BoxPin(pipe, input);
	t[pins.length] = p;
	if (input) {
		inpins = t;
	} else {
		outpins = t;
	}

//	recalculate();
}
/**
 * ChartElement constructor comment.
 */
public void addPopupMenuItems(javax.swing.JPopupMenu menu) {
	menu.add(new JMenuItem(Commands.DELETE));
	if (highlighted.getAll().size() == 1 && highlighted.get(0) == this)
		menu.add(new JMenuItem(Commands.MORPH_INTO));
	//menu.add(new JMenuItem(Commands.EXCHANGE_WITH));

	//menu.add(new JMenuItem(Commands.ADVANCED));
	menu.add(new JMenuItem(Commands.DESCRIPTION));
	menu.add(new JMenuItem(Commands.PROPERTIES));
	//System.out.println("el: " + element);
	//System.out.println("err: " + element.getDesignErrorMessage() + "  " + element.getId());
	if (element != null && element.getDesignErrorMessage() != null) {
		menu.add(new JMenuItem(Commands.ERROR_DESCRIPTION));
	}
}
/**
 * ChartElement constructor comment.
 */
public void destroy() {

}
/**
 * ChartElement constructor comment.
 */
public GenericDialog edit() throws Exception {
	return new bioera.properties.PropertiesDialog(panel.frame, element);
}
/**
 * ChartElement constructor comment.
 */
public BoxPin getActivePin() {
	return activePin;
}
/**
 * ChartElement constructor comment.
 */
public BoxPin getPin(int x, int y) {
	for (int i = 0; i < inpins.length; i++){
		if (inpins[i].contains(x, y)) {
			return inpins[i];
		}
	}
	for (int i = 0; i < outpins.length; i++){
		if (outpins[i].contains(x, y)) {
			return outpins[i];
		}
	}
	return null;
}
/**
 * ChartElement constructor comment.
 */
public BoxPin getPin(boolean input, String name) {
	if (input) {
		for (int i = 0; i < inpins.length; i++){
			if (inpins[i].pipe.getName().equals(name)) {
				return inpins[i];
			}
		}
	} else {
		for (int i = 0; i < outpins.length; i++){
			if (outpins[i].pipe.getName().equals(name)) {
				return outpins[i];
			}
		}
	}
	return null;
}
/**
 * ChartElement constructor comment.
 */
public BoxPin getPinAbs(int px, int py) {
	return getPin(px - this.x, py - this.y);
}
/**
 * ChartElement constructor comment.
 */
public int getPinIndex(BoxPin pin) {
	if (pin.input) {
		for (int i = 0; i < inpins.length; i++){
			if (inpins[i] == pin) {
				return i;
			}
		}
	} else {
		for (int i = 0; i < outpins.length; i++){
			if (outpins[i] == pin) {
				return i;
			}
		}
	}

	return -1;
}
/**
 * ChartElement constructor comment.
 */
public int getX1() {
	return x;
}
/**
 * ChartElement constructor comment.
 */
public int getX2() {
	return x + width;
}
/**
 * ChartElement constructor comment.
 */
public int getY1() {
	return y;
}
/**
 * ChartElement constructor comment.
 */
public int getY2() {
	return y + height;
}
/**
 * ChartElement constructor comment.
 */
public boolean isResizing(int x, int y) {
	if (x < width - RESIZE_LENGTH || y < height - RESIZE_LENGTH)
		return false;

	return (RESIZE_LENGTH - (width - 1 - x) >= (height - 1 - y));
}
/**
 * ChartElement constructor comment.
 */
public void load(XmlConfigSection section) throws Exception {
	super.load(section);

	x = section.getInteger("x", 10);
	y = section.getInteger("y", 10);
	width = section.getInteger("width", ConfigurableSystemSettings.DESIGNER_ELEMENT_DEFAULT_WIDTH);
	height = section.getInteger("height", ConfigurableSystemSettings.DESIGNER_ELEMENT_DEFAULT_HEIGHT);

	setBounds(x, y, width, height);
}
/**
 * ChartElement constructor comment.
 */
public void paint(java.awt.Graphics g) {
	//super.paint(g);
	g.clearRect(0, 0, width - 1, height - 1);

	// Title
	if (titleHeight == 0) {
		titleHeight = g.getFontMetrics().getHeight();
		titleWidth = g.getFontMetrics().stringWidth(element.getName());
		recalculate();
	}

	g.drawString(element.getName(), (width - titleWidth) / 2 , titleHeight - 1);

	// Rectangle
	g.drawRoundRect(0, 0, this.width - 1, this.height - 1, 13, 13);
	g.drawLine(0, titleHeight + 2, this.width - 1, titleHeight + 2);

	// Drag triangle
	g.drawLine(width - 1, height - 1 - RESIZE_LENGTH, width - 1 - RESIZE_LENGTH, height - 1);

	// Status
	java.awt.Color c = g.getColor();
	String status = element.getStatusString();
	if (status.trim().length() > 0) {
		int len = status.length();
		//System.out.println("len is " + len);
		int levels[] = element.getStatusLevels();
		int pc = 0;
		g.setColor(Color.black);
		int cw = g.getFontMetrics().stringWidth("X") + 2;
		for (int i = 0; i < len; i++){
			if (levels[i] != pc) {
				pc = levels[i];
				if (pc == 0)
					g.setColor(Color.black);
				else if (pc == 10)
					g.setColor(Color.red);
				else if (pc == 1)
					g.setColor(Color.yellow);
			}
			g.drawString("" + status.charAt(i), 2 + i * cw , height - 2);
			//System.out.println("" + status.charAt(i) + "  " + (2 + i * cw) + "  " + (height - 2) );
		}
	}
	g.setColor(c);

	// Pins
	for (int i = 0; i < inpins.length; i++){
		inpins[i].paint(g, width, height, inpins[i] == activePin, element.getInput(i).getType());
	}

	for (int i = 0; i < outpins.length; i++){
		outpins[i].paint(g, width, height, outpins[i] == activePin, element.getOutput(i).getType());
	}
}
/**
 * ChartElement constructor comment.
 */
public void propertyChanged(ChangePropertyEvent event) {
	titleHeight = 0;
	titleWidth = 0;
}
/**
 * ChartElement constructor comment.
 */
public void recalculate() {
	addPins();

	// Relocate pins
	int step = (height-titleHeight-RESIZE_LENGTH) / (inpins.length + 1);
	for (int i = 0; i < inpins.length; i++){
		inpins[i].y = titleHeight +  (i + 1) * step;
		inpins[i].x = 0;
		inpins[i].pipe = element.getInput(i);
	}

	step = (height-titleHeight-RESIZE_LENGTH) / (outpins.length + 1);
	for (int i = 0; i < outpins.length; i++){
		outpins[i].y = titleHeight + (i + 1) * step;
		outpins[i].x = width - BoxPin.PIN_LENGTH;
		outpins[i].pipe = element.getOutput(i);
	}
}
/**
 * ChartElement constructor comment.
 */
public void reset() {
	super.reset();
	activePin = null;
}
/**
 * ChartElement constructor comment.
 */
public void rezoom() {
	int zoom = Main.app.designFrame.zoom;
	x = x * ConfigurableSystemSettings.DESIGNER_ZOOM / zoom;
	y = y * ConfigurableSystemSettings.DESIGNER_ZOOM / zoom;
	width = width * ConfigurableSystemSettings.DESIGNER_ZOOM / zoom;
	height = height * ConfigurableSystemSettings.DESIGNER_ZOOM / zoom;
	recalculate();
}
/**
 * ChartElement constructor comment.
 */
public void save(XmlCreator designSection) throws Exception {
	designSection.addTextValue("x", "" + (x * 100 / ConfigurableSystemSettings.DESIGNER_ZOOM));
	designSection.addTextValue("y", "" + (y * 100 / ConfigurableSystemSettings.DESIGNER_ZOOM));
	designSection.addTextValue("width", "" + (width * 100 / ConfigurableSystemSettings.DESIGNER_ZOOM));
	designSection.addTextValue("height", "" + (height * 100 / ConfigurableSystemSettings.DESIGNER_ZOOM));
}
/**
 * ChartElement constructor comment.
 */
public void setBounds(int ix, int iy, int w, int h) {
	this.x = ix;
	this.y = iy;
	width = w;
	height = h;
}
/**
 * ChartElement constructor comment.
 */
public void setX1(int i) {
	x = i;
}
/**
 * ChartElement constructor comment.
 */
public void setY1(int i) {
	y = i;
}
/**
 * ChartElement constructor comment.
 */
public void statusChanged(ChangeStatusEvent event) {
	//System.out.println("should");

        if ( Main.app.designFrame != null){
            panel.frame.repaint();
        }
}

/**
 * Elem constructor comment.
 */
public BoxItem(Element e) {
	super("\nproc");

	element = e;

	e.addChangePropertyListener(this);
	e.addChangeStatusListener(this);

	if (e == null) {
		throw new RuntimeException("Processing element is null");
	}

	//addPins();

	//addPin("IN1", true);
	//addPin("IN2", true);
	//addPin("OUT", false);
}

/**
 * Elem constructor comment.
 */
public void addPins() {
	if (inpins.length != element.getInputsCount()) {
		if (inpins.length > 0)
			inpins = new BoxPin[0];
		for (int i = 0; i < element.getInputsCount(); i++){
			addPin(i, true);
		}
	}

	if (outpins.length != element.getOutputsCount()) {
		if (outpins.length > 0)
			outpins = new BoxPin[0];
		for (int i = 0; i < element.getOutputsCount(); i++){
			addPin(i, false);
		}
	}
}

/**
 * ChartElement constructor comment.
 */
public String toString() {
	return element.getName() + "[" + x + "," + y + ";" + width + "," + height + "]";
}
}
