/* DialogHelpPresageHelp.java v 1.0.9   11/6/04 7:15 PM
 *
 * Presage Monitor - visual designer for biofeedback based off of BioEra (http://www.bioera.net)
 *
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
import bioera.*;

public class DialogHelpPresageHelp extends MessageDialog{
/**
 * DialogHelpPresageHelp constructor comment.
 * @param d javax.swing.JDialog
 */
public DialogHelpPresageHelp(javax.swing.JDialog d) {
	super(d);
}
/**
 * DialogHelpPresageHelp constructor comment.
 * @param d javax.swing.JDialog
 * @param title java.lang.String
 */
public DialogHelpPresageHelp(javax.swing.JDialog d, String title) {
	super(d, title);
}
/**
  * DialogHelpPresageHelp constructor comment.
 * @param f javax.swing.JFrame
 */
public DialogHelpPresageHelp(javax.swing.JFrame f) {
	super(f);
}
/**
  * DialogHelpPresageHelp constructor comment.
 * @param f javax.swing.JFrame
 * @param title java.lang.String
 */
public  DialogHelpPresageHelp(javax.swing.JFrame f, String title) {
	super(f, title);
}
/**
  * DialogHelpPresageHelp constructor comment.
 * @param f javax.swing.JFrame
 * @param title java.lang.String
 */
public void show() {
	setTitle("Help With The Presage Device");
	JPanel mainPanel = new JPanel(new bioera.layouts.AdvancedGridLayout(0, 1, 2, 2));
	JLabel l;
	mainPanel.add((l=new JLabel("Presage Device Help", JLabel.CENTER)));
	Font f = l.getFont();
	l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
	mainPanel.add(new JLabel(""));
	mainPanel.add(new JLabel(" "));//+ Main.app.presageTable0));
	mainPanel.add(new JLabel("")); // + bioera.config.Version.getReleaseDate()));
	mainPanel.add(new JLabel(""));
	mainPanel.add(new JLabel(""));
	mainPanel.add(new JLabel(""));
	mainPanel.add(new JLabel("Access to device documentation will be provided here"));
	mainPanel.add(new JLabel(""));
	mainPanel.add(new JLabel("MindPlace"));//+bioera.config.Version.getYear()+" Jarek Foltynski"));
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
