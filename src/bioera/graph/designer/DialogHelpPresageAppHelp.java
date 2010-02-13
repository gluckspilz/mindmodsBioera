/* DialogHelpPresageAppHelp.java v 1.0.9   11/6/04 7:15 PM
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


public class DialogHelpPresageAppHelp extends MessageDialog{
/**
 * DialogHelpPresageAppHelp constructor comment.
 * @param d javax.swing.JDialog
 */
public DialogHelpPresageAppHelp(javax.swing.JDialog d) {
	super(d);
}
/**
 * DialogHelpPresageAppHelp constructor comment.
 * @param d javax.swing.JDialog
 * @param title java.lang.String
 */
public DialogHelpPresageAppHelp(javax.swing.JDialog d, String title) {
	super(d, title);
}
/**
  * DialogHelpPresageAppHelp constructor comment.
 * @param f javax.swing.JFrame
 */
public DialogHelpPresageAppHelp(javax.swing.JFrame f) {
	super(f);
}
/**
  * DialogHelpPresageAppHelp constructor comment.
 * @param f javax.swing.JFrame
 * @param title java.lang.String
 */
public  DialogHelpPresageAppHelp(javax.swing.JFrame f, String title) {
	super(f, title);
}
/**
  * DialogHelpPresageAppHelp constructor comment.
 * @param f javax.swing.JFrame
 * @param title java.lang.String
 */
public void show() {
	setTitle("Presage Monitor Help");
	JPanel mainPanel = new JPanel(new bioera.layouts.AdvancedGridLayout(0, 1, 2, 2));
	JLabel l;
	mainPanel.add((l=new JLabel("Presage Monitor Help", JLabel.CENTER)));
	Font f = l.getFont();
	l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
	mainPanel.add(new JLabel(""));
	mainPanel.add(new JLabel("This will be the location of the help interface"));// + bioera.config.Version.getVersion()));
	mainPanel.add(new JLabel("")); // + bioera.config.Version.getReleaseDate()));
	mainPanel.add(new JLabel(""));
	mainPanel.add(new JLabel(""));
	mainPanel.add(new JLabel(""));
	mainPanel.add(new JLabel("Web site: http://www.mindplace.com"));
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
