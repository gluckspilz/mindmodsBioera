/* ElementOrderSorter.java v 1.0.9   11/6/04 7:15 PM
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

import bioera.processing.impl.*;

public class ElementOrderSorter {
	java.util.ArrayList list;
	boolean taken[];
	Element in[];

	public static final boolean debug = false;
/**
 * Sorter constructor comment.
 */
public ElementOrderSorter() {
	super();
}
/**
 * Sorter constructor comment.
 */
private void finAndAddAllConnectedOutputElements() throws Exception {
//	System.out.println("all: " + list);
	
	boolean any = true;
	while (any) {
		any = false;
		for(int i = 0; i < in.length; i++){
			if (taken[i])
				continue;

			Element e = in[i];
			int inCount = e.getInputsCount();
//			System.out.println("e: " + e.getName() + "  " + inCount);
			boolean shouldAdd = false;
			main: for (int j = 0; j < inCount; j++){
				Element pred[] = e.getElementsConnectedToInput(j);
				//if (pred != null)
					//System.out.println("pred: " + pred.getName() + "  " + j);

				if (pred != null) {
					for (int ei = 0; ei < pred.length; ei++){
						if (pred[ei] != null) {
							if (list.contains(pred[ei])) {
								shouldAdd = true;
							} else {
								shouldAdd = false;
								break main;								
							}
						}
					}
					
				}
			}

			if (shouldAdd) {
//				System.out.println("-------added " + e.getName());
				list.add(e);
				taken[i] = true;
				any = true;
			}
			
		}
	}

}
/**
 * Sorter constructor comment.
 */
public Element [] sortProcessingElements(Element elements[]) throws Exception {
	in = elements;
	list = new java.util.ArrayList();
	taken = new boolean[in.length];
	for (int i = 0; i < taken.length; i++){
		taken[i] = false;
	}

	if (debug)
		System.out.println("Sorting " + in.length + " elements");
		
	// First find and add elements that have no inputs
	for (int i = 0; i < in.length; i++){
		Element e = in[i];
		if (e.getInputsCount() == 0 || e.getInputsConnectedCount() == 0) {
			list.add(e);
			taken[i] = true;
		}
	}

	finAndAddAllConnectedOutputElements();

	if (debug && list.size() < in.length)
		System.out.println("Sorter: resolving elements in loops " + (in.length - list.size()));
	
	// Find if there is any element that we know needs no priority although they may have connected inputs
	for(int i = 0; i < in.length; i++){
		if (taken[i])
			continue;

		Element e = in[i];
		if (e instanceof SerialPort 
			|| e instanceof NetworkClient 
			|| e instanceof NetworkServer
			|| e instanceof SetSignalParameters) {
				
			list.add(e);
			taken[i] = true;

			// And immediately repeat adding all connected to output
			finAndAddAllConnectedOutputElements();
		}

	}

	if (debug && list.size() < in.length)
		System.out.println("Unresolved elements: " + (in.length - list.size()) + " sorting randomly");

	// Now add all elements that has input but also input (avoid Displays)
	for(int i = 0; i < in.length; i++){
		if (taken[i])
			continue;

		Element e = in[i];
		if (e.getOutputsCount() == 0)
			continue;

		list.add(e);
		taken[i] = true;
				
		// And immediately repeat adding all connected to output
		finAndAddAllConnectedOutputElements();
	}

	if (debug && list.size() < in.length)
		System.out.println("Unresolved elements: " + (in.length - list.size()) + " sorting randomly");

	// Take all others
	for(int i = 0; i < in.length; i++){
		if (taken[i])
			continue;

		Element e = in[i];
		list.add(e);
		taken[i] = true;

		// And immediately repeat adding all connected to output
		finAndAddAllConnectedOutputElements();
	}

	if (list.size() < in.length)
		throw new Exception("Error: Unresolved elements: " + (in.length - list.size()) + " sorting randomly");

	if (debug)
		System.out.println("Sorted " + in.length + " elements");
		
	Element out[] = (Element[]) list.toArray(new Element[0]);

	if (debug) {
		System.out.println("Sorted elements:");
		for (int i = 0; i < out.length; i++){
			System.out.print("" + out[i] + ",");
		}
		System.out.println();
	}
	
	return out;
}
}
