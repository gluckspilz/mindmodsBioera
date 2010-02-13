/* Test.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.layouts;

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


public class Test extends GenericDialog {
	ElementProperty properties[];

	JButton bOK;
	JButton bApply;
	JButton bCancel;
	JPanel valuePanel;
	Propertable element;
public Test(java.awt.Frame f, Propertable elem) throws Exception {
	super(f, "Properties");
	element = elem;
	properties = ProcessingTools.getElementProperties(element);

	// sort
	boolean any = true;
	while (any) {
		any = false;
		for (int i = 0; i < properties.length - 1; i++){
			if (properties[i+1].name.equals("name")) {
				ElementProperty p = properties[i];
				properties[i] = properties[i + 1];
				properties[i + 1] = p;
				any = true;
			}
		}
	}
}
public static void main(String args[]) throws Exception {
	try {
		System.out.println("started");
		Test.test1();
		System.out.println("Finished");
	} catch (Exception e) {
		e.printStackTrace();
	}
}
private static void test1() {
	JPanel p = new JPanel();
	AdvancedGridLayout l = new AdvancedGridLayout(0, 2, 2, 2);
	l.setColWeight(1, 1);
//	l.setRowWeight(0, 1);
	p.setLayout(l);
	p.add(new JButton("name1"));
	p.add(new JButton("name2"));
	p.add(new JButton("name3"));
	p.add(new JButton("name4"));

	JFrame frame = new JFrame();
	frame.addWindowListener(new CloseSaveWindowsListener());
	frame.setBackground(ConfigurableSystemSettings.backgroundColor.getAWTColor());
	frame.getContentPane().setLayout(new BorderLayout(2, 2));
	frame.getContentPane().add(p, BorderLayout.CENTER);
	frame.setBounds(50, 50, 400, 400);
        frame.setVisible(true);
	//frame.show();
}
}
