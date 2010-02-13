/* Keyboard.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.processing;


import java.util.*;
import bioera.*;
import bioera.graph.designer.*;
import bioera.graph.runtime.*;
import bioera.config.*;

/**
 * Insert the type's description here.
 * Creation date: (7/17/2004 10:03:08 PM)
 * @author: Jarek
 */
public class Keyboard {
/**
 * Keyboard constructor comment.
 */
public Keyboard() {
	super();
}
/**
 * Keyboard constructor comment.
 */
public static boolean handle(Processor p) throws Exception {
	while (System.in.available() > 0) {
		int ch = System.in.read();
		if (ch != '/' && !Character.isLetterOrDigit((char)ch))
			continue;
			
		if (ch == 'q' || ch == 'x') {
			System.out.println("Processing interrupted from keyboard");
			p.internalStopProcessing();
			if (ch == 'x') {
				System.out.println("Exited");
				Main.systemExit();
			} else					
				return true;
		}

		if (Main.app.mode == Main.MODE_ONLY_PROCESSING) {
			if (ch == 'k') {
				// Send key code to all keyboards in the design
				specialKeyboardCode(p);
			} else if (ch == '/') {
				specialActionCode(p);
			} else {
				System.out.println("KeybAction " + (char) ch); 
			
				// Action
				ActionHandler.performKey((char)ch);
			}
		}
	}

	return false;
}
/**
 * Keyboard constructor comment.
 */
public static int readNumber() throws Exception {
	int no = 0;
	while (System.in.available() > 0) {
		int ch = System.in.read();
		if (Character.isDigit((char)ch)) {
			no = (no * 10) + (ch - '0'); 
		} else {
			break;
		}
	}

	return no;
}
/**
 * Keyboard constructor comment.
 */
public static void specialActionCode(Processor p) throws Exception {
	System.out.println("Special code ");
	int no = readNumber();
	if (no <= 0)
		return;

	System.out.println("Special code " + no);
	switch (no) {
		case 100:
			System.out.println("Special action codes:");
			System.out.println("1: Only active elements");
			System.out.println("2: All elements");
			System.out.println("3: Only active elements list");
			System.out.println("4: All elements list");
			System.out.println("5: Only non-active elements list");
			System.out.println("6: Connections");
			System.out.println("7: Active and not connected elements:");
			break;
		case 1:
			System.out.println("Only active elements: \n" + ProcessingTools.processorActiveElementsToString());
			break;
		case 2:
			System.out.println("All elements: \n" + ProcessingTools.processorActiveElementsToString());
			break;
		case 3:
			System.out.println("Only active elements list:");
			for (int i = 0; i < p.getActiveElements().length; i++){
				Element e = p.getActiveElements()[i];
				System.out.println("e: " + e.getName() + "\t" + e.getClass());
			}
			break;
		case 4:
			System.out.println("All elements list:");
			for (int i = 0; i < p.getAllElements().length; i++){
				Element e = p.getAllElements()[i];
				System.out.println("e: " + e.getName() + "\t" + e.getClass());
			}
			break;
		case 5:
			System.out.println("Only not-active elements list:");
			for (int i = 0; i < p.getAllElements().length; i++){
				Element e = p.getAllElements()[i];
				if (!p.containsActiveElement(e))
					System.out.println("e: " + e.getName() + "\t" + e.getClass());
			}
			break;
		case 6:
			System.out.println("Only not-active elements list:");
			for (int i = 0; i < p.getAllElements().length; i++){
				Element e = p.getAllElements()[i];
				int n = e.getOutputsCount();
				for (int k = 0; k < n; k++){
					if (!(e.getOutput(k) instanceof PipeDistributor)) {
						System.out.println("Pipe " + e.getOutput(k).getName() + " is not a distributor");
						continue;
					}
					PipeDistributor pipe = (PipeDistributor) e.getOutput(k);
					int m = pipe.getConnectedCount();
					for (int j = 0; j < m; j++){
						Pipe destpipe = pipe.getConnectedPipe(j);
						System.out.println("c: " + pipe.getElement().getName() + " -> " + destpipe.getElement().getName());
					}
				}
			}
			break;
		case 7:
			System.out.println("Active and not connected elements:");
			for (int i = 0; i < p.getActiveElements().length; i++){
				Element e = p.getActiveElements()[i];
				boolean connected = false;
				int n = e.getOutputsCount();
				// Outputs
				for (int k = 0; k < n; k++){
					if (!(e.getOutput(k) instanceof PipeDistributor)) {
						System.out.println("Pipe " + e.getOutput(k).getName() + " is not a distributor");
						continue;
					}
					PipeDistributor pipe = (PipeDistributor) e.getOutput(k);
					int m = pipe.getConnectedCount();
					if (m > 0) {
						connected = true;
						break;
					}
				}
				// Inputs
				n = e.getInputsCount();
				for (int k = 0; k < n; k++){
					if (!(e.getInput(k) instanceof BufferedPipe)) {
						System.out.println("Pipe " + e.getInput(k).getName() + " is not a BufferedPipe");
						continue;
					}
					BufferedPipe pipe = (BufferedPipe) e.getInput(k);
					PipeDistributor pin[] = pipe.getConnectedDistributors();
					if (pin.length > 0) {
						connected = true;
						break;
					}
				}
				if (!connected)
					System.out.println("e: " + e.getName() + "\t" + e.getClass());
			}
			break;
		default:
			System.out.println("Unrecognized code '" + no + "'");
			break;
	}
}
/**
 * Keyboard constructor comment.
 */
public static void specialKeyboardCode(Processor p) throws Exception {
	int no = readNumber();
	if (no <= 0)
		return;
		
	for (int i = 0; i < p.getActiveElements().length; i++){
		Element e = p.getActiveElements()[i];
		if (e instanceof bioera.processing.impl.KeyboardSource) {
			((bioera.processing.impl.KeyboardSource) e).keyPressed(no);
		}
	}
	
	System.out.println("Passed code to keyboards: " + no);
}
}
