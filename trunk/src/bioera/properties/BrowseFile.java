/* BrowseFile.java v 1.0.9   11/6/04 7:15 PM
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

import bioera.processing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class BrowseFile extends AbstractSComponentProperty implements ActionListener {
	public String path = "";
	private JButton browse;
	private JTextField tf;
	private JPanel panel;
public BrowseFile() {
	this("");
}
public BrowseFile(String msg) {
	super();

	browse = new JButton("...");
	Insets m = browse.getMargin();
	m.right = m.left = 1;
	browse.setMargin(m);

	browse.addActionListener(this);
	path = msg;
}
	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	if (e.getSource() != browse) {
		return;
	}
	
	javax.swing.JFileChooser d = new javax.swing.JFileChooser();
	d.setFileSelectionMode(d.FILES_ONLY);
	d.setCurrentDirectory(new java.io.File("."));
	d.setDialogTitle("Load design");
//	d.setFileFilter(new ExtFileFilter("Open file", ""));
	int ret = d.showOpenDialog(browse);
	if (ret == d.APPROVE_OPTION) {
		java.io.File f = d.getSelectedFile();
		if (f != null && f.exists() && f.isFile()) {
			path = f.getAbsolutePath();
			tf.setText(path);
		}
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 9:06:04 PM)
 */
public java.io.File getAbsoluteFile() {
	if (path != null && path.length() > 0 
		&& !path.startsWith(java.io.File.separator) 
		&& (path.indexOf(':') == -1 || path.indexOf('\\') == -1))
		return new java.io.File(bioera.Main.app.getRootFolder(), path);
	return new java.io.File(path);
}
/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 9:06:04 PM)
 */
public java.awt.Component getComponent() throws java.lang.Exception {
	if (panel == null) {
		panel = new JPanel(new BorderLayout());
		panel.add(browse, BorderLayout.EAST);
		panel.add(tf = new JTextField(), BorderLayout.CENTER);
		tf.setText(path);
	}
	return panel;
}
/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 9:06:04 PM)
 */
public void save() {
	path = tf.getText();
}
}
