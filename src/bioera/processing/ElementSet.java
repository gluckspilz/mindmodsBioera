/* ElementSet.java v 1.0.9   11/6/04 7:15 PM
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

import bioera.*;
import bioera.config.*;

public class ElementSet implements Propertable, bioera.config.Configurable {
	public Element elements[] = new Element[0];
	public ElementConnection connections[] = new ElementConnection[0];

	private static boolean debug = bioera.Debugger.get("processor.elementset");	
/**
 * ElementSet constructor comment.
 */
public ElementSet() {
	super();
}
/**
 * ElementSet constructor comment.
 */
public ElementSet(Element e[]) {
	super();
	elements = e;
}
/**
 * Element constructor comment.
 */
public void addConnection(ElementConnection con) {
	connections = (ElementConnection[]) ProcessingTools.appendArray(connections, con);
}
/**
 * Element constructor comment.
 */
public void addElement(Element e) {
	elements = (Element[]) ProcessingTools.appendArray(elements, e);
}
/**
 * RuntimeManager constructor comment.
 */
public void connectElements(XmlConfigSection xmlElement) throws Exception {
	int srcId = xmlElement.getIntegerThrow("src_id");
	int destId = xmlElement.getIntegerThrow("dest_id");
	Element e1 = getElementById(srcId);
	if (e1 == null)
		throw new Exception("Element not found " + srcId);
	Element e2 = getElementById(destId);	
	if (e2 == null)
		throw new Exception("Element not found " + destId);
	XmlConfigSection pin1 = xmlElement.getSection("srcpin");
	XmlConfigSection pin2 = xmlElement.getSection("destpin");
	boolean input1 = "in".equals(pin1.getString("direction"));
	boolean input2 = "in".equals(pin2.getString("direction"));
	Pipe p1, p2;
	if (input1) {
		p1 = e1.getInputByNameOrIndex(pin1.getString("name"), pin1.getInteger("index", -1));
		p2 = e2.getOutputByNameOrIndex(pin2.getString("name"), pin2.getInteger("index", -1));
		e2.registerReceiver(p2, p1);
	} else if (input2) {
		p1 = e1.getOutputByNameOrIndex(pin1.getString("name"), pin1.getInteger("index", -1));
		p2 = e2.getInputByNameOrIndex(pin2.getString("name"), pin2.getInteger("index", -1));
		e1.registerReceiver(p1, p2);
	} else {
		throw new Exception("Input-Output error");
	}

	if (debug)
		System.out.println("Connected elements: " + e1.getName() + "(" + p1.getName()+ ") with " + e2.getName() + "(" + p2.getName() + " )");					
}
/**
 * RuntimeManager constructor comment.
 */
public void connectPipes(Pipe src, Pipe dest) {
	Element e1 = src.getElement();
	Element e2 = dest.getElement();
	e1.registerReceiver(src, dest);

	if (debug)
		System.out.println("Connected elements: " + e1.getName() + "(" + src.getName()+ ") with " + e2.getName() + "(" + dest.getName() + " )");					
}
/**
 * Element constructor comment.
 */
public Element getElementById(int id) {
	for (int i = 0; i < elements.length; i++){
		if (elements[i] != null && id == elements[i].getId())
			return elements[i];
	}
	
	return null;
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
	return 0;
}
/**
 * getPropertyDescription method comment.
 */
public java.lang.Object[] getPropertyDescription(java.lang.String name) {
	return null;
}
/**
 * getPropertyNames method comment.
 */
public java.lang.String[] getPropertyNames() {
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 4:38:32 PM)
 */
public boolean load(bioera.config.XmlConfigSection section) throws java.lang.Exception {
	return false;
}
/**
 * Element constructor comment.
 */
public void process() throws Exception {}
/**
 * RuntimeManager constructor comment.
 */
public Element removeElement(Element e) throws Exception {
	Element ret = null;
	
	// Search connection lines
	elements = (Element[]) ProcessingTools.removeAllFromArray(elements, e);
	
	// Remove all connections to this element
	for (int i = 0; i < e.getInputsCount(); i++){
		BufferedPipe in = (BufferedPipe) e.getInput(i);
		if (in.isConnected()) {
			PipeDistributor pipe[] = in.getConnectedDistributors();
			for (int pi = 0; pi < pipe.length; pi++){
				pipe[pi].unregister((Pipe) in);
			}			
		}
	}

	// Remove all references in receivers
	for (int i = 0; i < e.getOutputsCount(); i++){
		PipeDistributor out = (PipeDistributor) e.getOutput(i);
		if (out.isConnected()) {
			out.unregisterAll();
		}
	}

	return ret;
}
/**
 * RuntimeManager constructor comment.
 */
public boolean save(bioera.config.XmlCreator section) throws Exception {
	java.util.List list = new java.util.ArrayList();
	for (int i = 0; i < elements.length; i++){
		Element e = elements[i];
		for (int k = 0; k < e.getOutputsCount(); k++){
			PipeDistributor pd = (PipeDistributor) e.getOutput(k);
			for (int cp = 0; cp < pd.getConnectedCount(); cp++){
				Pipe p = pd.getConnectedPipe(cp);
				list.add(new ElementConnection((Pipe) pd, p));
			}				
		}
	}
	
	connections = new ElementConnection[list.size()];
	connections = (ElementConnection[]) list.toArray(connections);

//	return super.save(section);
	return false;
}
/**
 * sendChangePropertyEvent method comment.
 */
public void sendChangePropertyEvent(java.lang.String fieldName, java.lang.Object oldValue) {}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
