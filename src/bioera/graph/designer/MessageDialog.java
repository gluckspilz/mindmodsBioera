/* MessageDialog.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.graph.runtime.*;
import bioera.*;
/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class MessageDialog extends GenericDialog implements ActionListener {
	JButton bClose;
        JButton bSave;
/**
 * MessageDialog constructor comment.
 * @param f java.awt.Frame
 * @param name java.lang.String
 */
public MessageDialog(JDialog d) {
	this(d, "Message");

}
/**
 * MessageDialog constructor comment.
 * @param f java.awt.Frame
 * @param name java.lang.String
 */
public MessageDialog(JDialog d, String title) {
	super(d, title);
	setModal(true);
}
/**
 * MessageDialog constructor comment.
 * @param f java.awt.Frame
 * @param name java.lang.String
 */
public MessageDialog(JFrame f) {
	this(f, "Message");
}
/**
 * MessageDialog constructor comment.
 * @param f java.awt.Frame
 * @param name java.lang.String
 */
public MessageDialog(JFrame f, String title) {
	super(f, title);
	setModal(true);
}
/**
 * MessageDialog constructor comment.
 * @param f java.awt.Frame
 * @param name java.lang.String
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
     if (e.getSource() == bSave) {
         dispose();
          Main.app.runtimeFrame.saveMenuItem.doClick();
	}

	if (e.getSource() == bClose) {
		dispose();
	}
}
/**
 * MessageDialog constructor comment.
 * @param f java.awt.Frame
 * @param name java.lang.String
 */
public static void main(String msg[]) {
	JFrame f = new JFrame();
	MessageDialog d = new MessageDialog(f);
	d.show("test");
}
/**
 * MessageDialog constructor comment.
 * @param f java.awt.Frame
 * @param name java.lang.String
 */
public void show(String msg) {
	JLabel msgLabel = new JLabel(msg);

	JPanel mainPanel = new JPanel();
	mainPanel.add(msgLabel);

	bClose = new JButton("Close");
	bClose.addActionListener(this);

        bSave = new JButton("Save");
        bSave.addActionListener(this);

	JPanel down = new JPanel();
	down.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 2, 2));
        down.add(bSave);
	down.add(bClose);

	getContentPane().setLayout(new java.awt.BorderLayout());
	getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);
	getContentPane().add(down, java.awt.BorderLayout.SOUTH);
	pack();
	locateOnWindow();
	super.show();
}

/**
 * MessageDialog constructor comment.
 * @param f java.awt.Frame
 * @param name java.lang.String
 */
public MessageDialog(java.awt.Frame w, String title) {
	super(w, title);
	setModal(true);
}
}
