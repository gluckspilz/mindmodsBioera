/* REMSimulatorFrame.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.rem;

import java.awt.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import bioera.*;
//import bioera.processing.impl.*;
import bioera.device.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class REMSimulatorFrame extends Frame implements ActionListener, MouseListener {
	Button realityButton;
	Button remButton;
	Button showButton;
	REMSimulationSource element;
	long lastTime = 0;
/**
 * Simulator constructor comment.
 */
public REMSimulatorFrame(REMSimulationSource simulationElement) {
	super("REM Simulator");

	element = simulationElement;

	realityButton = new Button("Reality button");
	realityButton.addMouseListener(this);

	remButton = new Button("REM");
	remButton.addMouseListener(this);

	showButton = new Button("Show values");
	showButton.addActionListener(this);

	Panel p = new Panel();
	p.setLayout(new FlowLayout());
	p.add(realityButton);
	p.add(remButton);
	p.add(showButton);

	add(p);

	setBackground(Color.gray);
	pack();
}
	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	if (e.getSource() == showButton) {
		if (REMEngine.debugREMEngine.passiveTime < System.currentTimeMillis())
			System.out.println("passiveTime not set");
		else
			System.out.println("remaining passiveTime=" + (REMEngine.debugREMEngine.passiveTime - System.currentTimeMillis()) / (1000) + "s");
		System.out.println("timeIndex=" + REMEngine.debugREMEngine.timeIndex);
		System.out.print("times=[");
		for (int i = 0; i < REMEngine.debugREMEngine.times.length; i++){
			if (i > 0)
				System.out.print(",");
			System.out.print("" + REMEngine.debugREMEngine.times[i]);
		}
		System.out.println("]");
//		System.out.println("passiveTime=" + element.passiveTime);
	}
}
	/**
	 * Invoked when an action occurs.
	 */
public static void main(String args[]) throws Exception {
	try {
		System.out.println("started");
		REMSimulatorFrame app = new REMSimulatorFrame(null);
                app.setVisible(true);
//		app.show();
		System.out.println("Finished");
	} catch (Throwable e) {
		e.printStackTrace();
	}
}
	/**
	 * Invoked when the mouse has been clicked on a component.
	 */
public void mouseClicked(java.awt.event.MouseEvent e) {
}
	/**
	 * Invoked when the mouse enters a component.
	 */
public void mouseEntered(java.awt.event.MouseEvent e) {}
	/**
	 * Invoked when the mouse exits a component.
	 */
public void mouseExited(java.awt.event.MouseEvent e) {}
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 */
public void mousePressed(java.awt.event.MouseEvent e) {
	if (e.getSource() == realityButton) {
		element.realityButtonPressed = true;
		String t = lastTime == 0 ? "" : "" + (System.currentTimeMillis() - lastTime);
		lastTime = System.currentTimeMillis();
		//System.out.println("Reality button event - pressed " + t);
	} else if (e.getSource() == remButton) {
		element.remTakingPlace = true;
		String t = lastTime == 0 ? "" : "" + (System.currentTimeMillis() - lastTime);
		lastTime = System.currentTimeMillis();
		//System.out.println("Rem button event - pressed " + t);
	}
}
	/**
	 * Invoked when a mouse button has been released on a component.
	 */
public void mouseReleased(java.awt.event.MouseEvent e) {
	if (e.getSource() == realityButton) {
		element.realityButtonPressed = false;
		String t = lastTime == 0 ? "" : "" + (System.currentTimeMillis() - lastTime);
		lastTime = System.currentTimeMillis();
		//System.out.println("Reality button event - released " + t);
	} else if (e.getSource() == remButton) {
		element.remTakingPlace = false;
		String t = lastTime == 0 ? "" : "" + (System.currentTimeMillis() - lastTime);
		lastTime = System.currentTimeMillis();
		//System.out.println("Rem button event - released " + t);
	}
}
}
