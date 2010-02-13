/* DialogNewElement.java v 1.0.9   11/6/04 7:15 PM
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
public class DialogNewElement extends GenericDialog implements ActionListener {
	JButton bAdd;
	JButton bOK;
	JButton bCancel;
	JList list;
	int cX, cY;
	StringBuffer keys = new StringBuffer();
	BoxItem newItem;

	private static boolean debug = bioera.Debugger.get("designer.newdialog");
//	BoxItem box;
/**
 * EdiDialog constructor comment.
 */
public DialogNewElement(JFrame f) {
	super(f, "New Element");
}
//	BoxItem box;
/**
 * EdiDialog constructor comment.
 */
public DialogNewElement(JFrame f, String name) {
	super(f, name);
}
	/**
	 * Invoked when an action occurs.
	 */
public void actionAdd() {
	int i = list.getSelectedIndex();

	if (i == -1)
		return;

	newItem = createNewElement((String) DesignFrame.processingElements[list.getSelectedIndex()][1], cX, cY);
}
	/**
	 * Invoked when an action occurs.
	 */
public void actionOK() {
	int i = list.getSelectedIndex();

	if (i == -1)
		return;

	newItem = createNewElement((String) DesignFrame.processingElements[list.getSelectedIndex()][1], cX, cY);

	dispose();
}
	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent event) {
	//System.out.println("command=" + event.getActionCommand());

	if (event.getSource() == bCancel) {
		dispose();
		return;
	}

	if (event.getSource() == bAdd)
		actionAdd();
	else
		actionOK();
}
	/**
	 * Invoked when an action occurs.
	 */
public static BoxItem createNewElement(String className, int posX, int posY) {
	BoxItem ret = null;
	Element elem = null;
	try {
		if (debug)
			System.out.println("Creating new element '" + className + "'");
		elem = Element.newInstance(className);

		if (elem == null) {
			if (debug)
				System.out.println("Element '" + className + "' not found");
			return null;
		}

		ret = elem.getDesignerBox();
		ret.setBounds(
			(posX / DesignEventHandler.ELEM_STEP) * DesignEventHandler.ELEM_STEP,
			(posY / DesignEventHandler.ELEM_STEP) * DesignEventHandler.ELEM_STEP,
			ConfigurableSystemSettings.getDesignerElementWidth(),
			ConfigurableSystemSettings.getDesignerElementHeight());
		Main.app.designFrame.addElement(ret);
		Item.highlighted.clear();
		Item.highlighted.add(ret);
		Main.app.processor.add(elem);
		//elem.setActive(false);
		Main.app.designFrame.panel.repaint();

		if (elem instanceof Display) {
			Chart chart = ((Display)elem).newChart();
			((Display)elem).setChart(chart);
			chart.setAtTopLayer();
			//System.out.println("c1 " + chart.getBounds());
			Main.app.runtimeFrame.addChart(chart);
			//Main.app.runtimeFrame.show();
                        Main.app.runtimeFrame.setVisible();
			//Main.app.runtimeFrame.repaint();

			// Change runtime to edit mode if not now
			if (!Main.app.runtimeFrame.framedCharts) {
				Main.app.runtimeFrame.framedCharts = true;
				try {
					Main.app.runtimeFrame.reload();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		elem.reinit();
	} catch (Exception e) {
		if (elem != null)
			elem.disactivate(e);
		System.out.println("New element error: " + e);
		//e.printStackTrace();
	} catch (Throwable e) {
		System.out.println("Critical error occurred while creating new element: " + e);
		e.printStackTrace();
	}

	return ret;
}
/**
 * EdiDialog constructor comment.
 */
public String getSelectedElementName(Object src) {
	String name = null;
	if (src == list) {
		name = (String) list.getSelectedValue();
	} else if (src == bOK) {
		name = (String) list.getSelectedValue();
		if (name == null) {
			// Button ok was pressed, but no selection was done, do nothing
			return null;
		}
	} else {
		dispose();
		return null;
	}

	return name;
}
/**
 * EdiDialog constructor comment.
 */
public void keyReleased(java.awt.event.KeyEvent e) {
	if (e.getSource() == list) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_DELETE:
			case KeyEvent.VK_BACK_SPACE:
				if (keys.length() > 0)
					keys.setLength(keys.length() - 1);
				break;
			case KeyEvent.VK_ESCAPE:
				dispose();
				break;
			case KeyEvent.VK_ENTER:
				dispose();
				actionOK();
				break;
			case KeyEvent.VK_SPACE:
				actionAdd();
				break;
			default:
				//keys.append((char) e.getKeyChar());
				list.ensureIndexIsVisible(list.getSelectedIndex());
		}

//		if (debug)
//			System.out.println("keys: " + keys);
		return;
	}

	switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
		case KeyEvent.VK_ESCAPE:
			dispose();
			break;
		default:
			break;
	}

	super.keyReleased(e);
}
	/**
	 * Invoked when an action occurs.
	 */
public void locateOnComponent(int x, int y, java.awt.Component f) {
	cX = x;
	cY = y;
	super.locateOnComponent(x, y, f);
}
/**
 * EdiDialog constructor comment.
 */
public static void main(String args[]) throws Exception {
	try {
		System.out.println("started");
		DialogNewElement.test();
		System.out.println("Finished");
	} catch (Exception e) {
		e.printStackTrace();
	}
}
/**
 * EdiDialog constructor comment.
 */
public void show() {
	getContentPane().setLayout(new java.awt.BorderLayout());
	JScrollPane center = new JScrollPane();
	getContentPane().add(center, java.awt.BorderLayout.CENTER);
	list = new JList();
	list.setBackground(parentWindow.getBackground());
	DefaultListModel model = new DefaultListModel();
	list.setModel(model);
	for (int i = 0; i < DesignFrame.processingElements.length; i++){
		model.addElement((String) DesignFrame.processingElements[i][0]);
	}

	//list.ActionListener((ActionListener) this);
	center.setViewportView(list);

	JPanel down = new JPanel();
	getContentPane().add(down, java.awt.BorderLayout.SOUTH);
	down.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 2, 2));
	bOK = new JButton("OK");
	bOK.addActionListener(this);
	bAdd = new JButton("Add");
	bAdd.addActionListener(this);
	bCancel = new JButton("Cancel");
	bCancel.addActionListener(this);
	down.add(bOK);
	down.add(bAdd);
	down.add(bCancel);
	pack();
	setSize(getWidth(), getHeight() * 2);

	list.addKeyListener(this);
	super.show();
}
/**
 * EdiDialog constructor comment.
 */
public static void test() {
	JFrame frame = new JFrame();
	frame.addWindowListener(new CloseSaveWindowsListener());
	frame.setBackground(ConfigurableSystemSettings.backgroundColor.getAWTColor());
	frame.getContentPane().setLayout(new java.awt.BorderLayout());
	frame.setBounds(50, 50, 400, 400);
	frame.show();

	DialogNewElement dialog = new DialogNewElement(frame);
	dialog.show();
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
