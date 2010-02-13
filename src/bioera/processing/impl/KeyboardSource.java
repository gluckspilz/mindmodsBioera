/* KeyboardSource.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.processing.impl;

import java.io.*;
import java.util.*;
import java.awt.event.*;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.graph.designer.*;
import bioera.fft.*;
import bioera.graph.chart.*;
import bioera.properties.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class KeyboardSource extends Element implements KeyListener {
	public BrowseFile keyboardDevicePath = new BrowseFile("/dev/tty0");
	private final static String propertiesDescriptions[][] = {
	};

	private InputStream sysin;
	private ScalarPipeDistributor out;
	private BufferedScalarPipe in;
/**
 * VectorDisplay constructor comment.
 */
public KeyboardSource() {
	super();

	in = new BufferedScalarPipe(this);
	in.setBufferSize(1024);
	
	if (bioera.Main.app.designFrame != null) {
		bioera.Main.app.designFrame.panel.addKeyListener(this);
		bioera.Main.app.designFrame.frame.addKeyListener(this);
	}

	if (bioera.Main.app.runtimeFrame != null) {
		bioera.Main.app.runtimeFrame.frame.addKeyListener(this);
	}	
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Pressed keyboard keys codes are available here";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 0;
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 1;
}
/**
 */
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
/**
 * Element constructor comment.
 */
public final void initialize() {
	setName("Keyboard");

	outputs[0] = out = new ScalarPipeDistributor(this);
}
	/**
	 * Invoked when a key has been pressed.
	 */
public void keyPressed(java.awt.event.KeyEvent e) {
	synchronized (in) {
		keyPressed(e.getKeyCode());
	}	
}
	/**
	 * Invoked when a key has been released.
	 */
public void keyReleased(java.awt.event.KeyEvent e) {
	//synchronized (in) {
		//in.write(e.getKeyCode());
	//}
}
	/**
	 * Invoked when a key has been typed.
	 * This event occurs when a key press is followed by a key release.
	 */
public void keyTyped(java.awt.event.KeyEvent e) {}
/**
 * Element constructor comment.
 */
public final void process() throws Exception {
	if (sysin != null) {
		if (sysin.available() > 0) {
			for (int i = 0; i < sysin.available() ; i++){
				synchronized (in) {
					in.write(sysin.read());
				}
			}
		}
	}
	
	if (in.isEmpty())
		return;
		
	synchronized (in) {
		out.write(in.getBuffer(), 0, in.available());
		in.purgeAll();
	}
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	super.reinit();
}
/**
 * Element constructor comment.
 */
public final void start() {
	sysin = null;
	File f = keyboardDevicePath.getAbsoluteFile();
	if (f.exists()) {
		try {
			sysin = new FileInputStream(f);
		} catch (Exception e) {
			sysin = null;
		}
	}
}

	/**
	 * Invoked when a key has been pressed.
	 */
public void keyPressed(int code) {
	in.write(code);
}
}
