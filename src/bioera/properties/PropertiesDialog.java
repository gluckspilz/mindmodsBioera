/* PropertiesDialog.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.properties;


import java.io.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import bioera.processing.*;
import bioera.*;
import bioera.layouts.*;
import bioera.graph.designer.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class PropertiesDialog extends GenericDialog implements ActionListener {
	JButton bOK;
	JButton bApply;
	JButton bCancel;
	Container panel;
	Propertable element;
	PFields propertiesFields;
	Object ret;
	int options;
	JPanel centerPanel;
	public boolean reinitializeProcessor = true;
/**
 * EdiDialog constructor comment.
 */
public PropertiesDialog(java.awt.Frame f, Propertable p) throws Exception {
	this(f, p, 0);
}
/**
 * EdiDialog constructor comment.
 */
public PropertiesDialog(java.awt.Frame f, Propertable p, int opt) throws Exception {
	super(f, "Properties");
	element = p;
	options = opt;

	start();
	init();
}
/**
 * Invoked when an action occurs.
 */
public void actionOK() {
	try {
		PTools.save(element, propertiesFields);
		//System.out.println("============start");
		if (reinitializeProcessor)
			Main.app.processor.reInitializeAll();
		//System.out.println("============end");
	} catch (Exception e) {
		System.out.println("Properties save error: " + e);
		e.printStackTrace();
	}
}
/**
 * Invoked when an action occurs.
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	ret = e.getSource();
	if (ret == bOK || ret == bApply) {
		actionOK();
	}

	if (ret == bApply) {
		centerPanel.setVisible(false);
		centerPanel.removeAll();
		start();
		centerPanel.add(panel);
		centerPanel.setVisible(true);
	} else if (ret == bCancel || ret == bOK) {
		dispose();
	}
}
/**
 * EdiDialog constructor comment.
 */
public void init() {
	bOK = new JButton("OK");
	bOK.addActionListener(this);
	bOK.addKeyListener(this);

	bCancel = new JButton("Cancel");
	bCancel.addActionListener(this);
	bCancel.addKeyListener(this);

	bApply = new JButton("Apply");
	bApply.addActionListener(this);
	bApply.addKeyListener(this);

	JPanel down = new JPanel();
	down.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 2, 2));
	down.add(bOK);
	down.add(bApply);
	down.add(bCancel);

	centerPanel = new JPanel();
	centerPanel.setLayout(new java.awt.BorderLayout());
	centerPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
	centerPanel.add(panel, java.awt.BorderLayout.CENTER);

	JScrollPane scroll = new JScrollPane();
	scroll.setViewportView(centerPanel);

	JPanel mainPanel = new JPanel();
	mainPanel.setLayout(new java.awt.BorderLayout());
	mainPanel.add(scroll, java.awt.BorderLayout.CENTER);
	mainPanel.add(down, java.awt.BorderLayout.SOUTH);

	getContentPane().setLayout(new java.awt.BorderLayout());
	getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);
}
/**
 * EdiDialog constructor comment.
 */
public boolean isApply() {
	return ret == bApply;
}
/**
 * EdiDialog constructor comment.
 */
public boolean isCancel() {
	return ret == bCancel;
}
/**
 * EdiDialog constructor comment.
 */
public boolean isOk() {
	return ret == bOK;
}
	/**
	 * Invoked when a key has been released.
	 */
public void keyReleased(KeyEvent e) {
	switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
			dispose();
			actionOK();
			break;
		case KeyEvent.VK_F4:
			actionOK();
			break;
		default:
			super.keyReleased(e);
			break;
	}
}
/**
 * EdiDialog constructor comment.
 */
public static void main(String args[]) throws Exception {
	try {
		System.out.println("started");
		PropertiesDialog.test1();
		System.out.println("Finished");
	} catch (Exception e) {
		e.printStackTrace();
	}
}
/**
 * EdiDialog constructor comment.
 */
//public void show() {
public void setVisible() {
	pack();

	int screenH = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	int screenW = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	//System.out.println("screen=" + screenH);
	if (getHeight() > screenH * 3 / 4)
		setSize(getWidth(), screenH * 3 / 4);
	if (getWidth() > screenW * 3 / 4)
		setSize(screenW * 3 / 4, getHeight());

	locateOnWindow();
        super.setVisible(true);
//	super.show();
//	repaint();
}
/**
 * EdiDialog constructor comment.
 */
public void start()  {
	try {
		if ((options & 1) > 0)
			propertiesFields = (PFields) PTools.createPFields(element, null, null);
		else
			propertiesFields = (PFields) PTools.createWithClass(element, null, null);

		JTabbedPane tp = new JTabbedPane();
		tp.insertTab("General", null, propertiesFields.container, "", 0);
		panel = tp;

		propertiesFields.parent = tp;

		// Fill panels
		PTools.fillContainer(propertiesFields);
	} catch (Exception exc) {
		if (SystemSettings.showStack)
			exc.printStackTrace();
	}
}
/**
 * EdiDialog constructor comment.
 */
private static void test1() {
/*
	Frame frame = new Frame();
	frame.addWindowListener(new CloseSaveWindowsListener());
	frame.setBackground(ConfigurableSystemSettings.backgroundColor.getAWTColor());
	frame.setLayout(new BorderLayout());
	frame.setBounds(50, 50, 400, 400);
	frame.show();

	PropertiesDialog dialog = new PropertiesDialog(frame, null);
	Vector labels = new Vector(10);
	Vector values = new Vector(10);
	for (int i = 0; i < 10; i++){
		labels.addElement("" + i);
		values.addElement("" + (i * 12345));
	}
	dialog.setLabels(labels);
	dialog.setValues(values);
	dialog.doNotEdit(0);
	dialog.doNotEdit(1);
	dialog.show();
	Vector v = dialog.values;
	for (int i = 0; i < v.size(); i++){
		System.out.println("v[" + i + "]=" + v.elementAt(i));
	}
*/
}
}
