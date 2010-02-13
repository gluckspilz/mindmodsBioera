/* DialogHelpAbout.java v 1.0.9   11/6/04 7:15 PM
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

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class DialogHelpAbout extends MessageDialog {
/**
 * HelpAboutDialog constructor comment.
 * @param d javax.swing.JDialog
 */
public DialogHelpAbout(javax.swing.JDialog d) {
	super(d);
}
/**
 * HelpAboutDialog constructor comment.
 * @param d javax.swing.JDialog
 * @param title java.lang.String
 */
public DialogHelpAbout(javax.swing.JDialog d, String title) {
	super(d, title);
}
/**
 * HelpAboutDialog constructor comment.
 * @param f javax.swing.JFrame
 */
public DialogHelpAbout(javax.swing.JFrame f) {
	super(f);
}
/**
 * HelpAboutDialog constructor comment.
 * @param f javax.swing.JFrame
 * @param title java.lang.String
 */
public DialogHelpAbout(javax.swing.JFrame f, String title) {
	super(f, title);
}
/**
 * HelpAboutDialog constructor comment.
 * @param f javax.swing.JFrame
 * @param title java.lang.String
 */
public void show() {
	setTitle("About");
	JPanel mainPanel = new JPanel(new bioera.layouts.AdvancedGridLayout(0, 1, 2, 2));
	JLabel l;
	mainPanel.add((l=new JLabel("BioEra", JLabel.CENTER)));
	Font f = l.getFont();
	l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3)); 
	mainPanel.add(new JLabel(""));
	mainPanel.add(new JLabel("Version: " + bioera.config.Version.getVersion()));
	mainPanel.add(new JLabel("Release date: " + bioera.config.Version.getReleaseDate()));
	mainPanel.add(new JLabel("Start date: March 2003"));
	mainPanel.add(new JLabel(""));
	mainPanel.add(new JLabel(""));
	mainPanel.add(new JLabel("Web site: http://www.bioera.net"));
	mainPanel.add(new JLabel(""));
	mainPanel.add(new JLabel("Copyright © 2003-"+bioera.config.Version.getYear()+" Jarek Foltynski"));
	mainPanel.setBorder(BorderFactory.createCompoundBorder(
		BorderFactory.createLoweredBevelBorder(),
		BorderFactory.createEmptyBorder(10, 10, 10, 10))	
		);
	
	bClose = new JButton("Close");
	bClose.addActionListener(this);

	JPanel down = new JPanel();
	down.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 2));
	down.add(bClose);
	
	getContentPane().setLayout(new java.awt.BorderLayout());		
	getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);
	getContentPane().add(down, java.awt.BorderLayout.SOUTH);
	pack();
	locateOnWindow();
	super.show();	
}
}
