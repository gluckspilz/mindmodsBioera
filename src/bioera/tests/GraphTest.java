/* GraphTest.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.tests;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class GraphTest extends Canvas implements java.awt.event.WindowListener, java.awt.image.ImageObserver
{
	Frame f;
	Image im;
    public GraphTest()
    {

    }
    public static void main(String args[])
        throws Exception
    {
        try
        {
            GraphTest t = new GraphTest();
            t.start();
            System.out.println("finished");
        }
        catch(Exception e)
        {
            System.out.println("Error: " + e + "\n\n");
            e.printStackTrace();
        }
    }
public void paint(Graphics g) {
	g.drawLine(10, 10, 400, 12);

	//Color tr = new Color(0, 255, 255, 100);

	//if (im == null) {
		//im = createImage(50, 50);
		//Graphics gr = im.getGraphics();
		//gr.setColor(tr);
		//gr.fillRect(2, 2, 45, 45);
		//gr.setColor(Color.red);
		//gr.fillRect(10, 10, 10, 10);
	//}

	//g.setColor(Color.green);
	//g.fillRect(10, 10, 100, 100);
	////Color c = new Color(255, 255, 255, 0);
////	g.setColor(c);
////	g.fillRect(30, 30, 50, 50);

	//g.drawImage(im, 20, 20, tr, this);
}
public void start() throws Exception {
	f = new Frame();
	f.setBounds(10, 10, 500, 200);
	f.add(this, BorderLayout.CENTER);
  	f.addWindowListener(this);
        f.setVisible(true);
	//f.show();

	//Dialog d1 = new Dialog(f, "1");
	//d1.setBounds(20, 20, 200, 200);
	//d1.setModal(false);
	//Dialog d2 = new Dialog(f, "2");
	//d2.setBounds(30, 30, 200, 200);
	//d2.setModal(false);
	//d2.show();
	//d1.show();


	//logFileStream.write((s + "\n").getBytes());
}
	/**
	 * Invoked when the window is set to be the user's
	 * active window, which means the window (or one of its
	 * subcomponents) will receive keyboard events.
	 */
public void windowActivated(java.awt.event.WindowEvent e) {}
	/**
	 * Invoked when a window has been closed as the result
	 * of calling dispose on the window.
	 */
public void windowClosed(java.awt.event.WindowEvent e) {

}
	/**
	 * Invoked when the user attempts to close the window
	 * from the window's system menu.  If the program does not
	 * explicitly hide or dispose the window while processing
	 * this event, the window close operation will be cancelled.
	 */
public void windowClosing(java.awt.event.WindowEvent e) {
	f.setVisible(false);
	f.dispose();
	System.exit(0);
}
	/**
	 * Invoked when a window is no longer the user's active
	 * window, which means that keyboard events will no longer
	 * be delivered to the window or its subcomponents.
	 */
public void windowDeactivated(java.awt.event.WindowEvent e) {}
	/**
	 * Invoked when a window is changed from a minimized
	 * to a normal state.
	 */
public void windowDeiconified(java.awt.event.WindowEvent e) {}
	/**
	 * Invoked when a window is changed from a normal to a
	 * minimized state. For many platforms, a minimized window
	 * is displayed as the icon specified in the window's
	 * iconImage property.
	 * @see Frame#setIconImage
	 */
public void windowIconified(java.awt.event.WindowEvent e) {}
	/**
	 * Invoked the first time a window is made visible.
	 */
public void windowOpened(java.awt.event.WindowEvent e) {}
}
