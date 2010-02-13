/* XmlCreator.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.config;

// XML Parser
import javax.xml.parsers.*;



// W3 XML Document objects
import org.w3c.dom.*;
//import org.xml.sax.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class XmlCreator {
	public static Document doc;
	public Node root;
	public int intendLevel = 0;
	public static String EOL = System.getProperty("line.separator");
	public static String TAB = "\t";
/**
 * XmlCreator constructor comment.
 * @param ifile java.io.File
 * @exception java.lang.Exception The exception description.
 */
public XmlCreator() throws Exception {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();


    dbf.setValidating(false);
    dbf.setNamespaceAware(false);
    dbf.setIgnoringComments(true);
    dbf.setIgnoringElementContentWhitespace(true);



	DocumentBuilder docBuilder = dbf.newDocumentBuilder();
	doc = docBuilder.newDocument();

        Element configuration = doc.createElement("Configuration");
        doc.appendChild(configuration);

//	doc.appendChild(doc.createElement("Configuration"));


        NodeList nodelisty = doc.getChildNodes();
        root = nodelisty.item(0);



	//root = doc.getFirstChild();
	root.appendChild(doc.createTextNode(EOL));

}
/**
 * XmlCreator constructor comment.
 * @param ifile java.io.File
 * @exception java.lang.Exception The exception description.
 */
public XmlCreator(XmlConfigSection s) throws Exception {
	this();
	root = doc.importNode(s.config.getRootNode(), true);
}
public void addAttribute(String name, String value) throws Exception {
	((Element)root).setAttribute(name, value);
}
public XmlCreator addSection(XmlConfigSection section) throws Exception {
	boolean hasSubs = section.getElementSections().size() > 0;
	Node n = doc.importNode(section.config.getRootNode(), !hasSubs);
	XmlCreator ret = new XmlCreator();
	ret.root = n;
	ret.doc = n.getOwnerDocument();
	ret.intendLevel = intendLevel + 1;

	String space = "";
	for (int i = 0; i < ret.intendLevel; i++){
		space += TAB;
	}

	if (hasSubs) {
		root.appendChild(doc.createTextNode(TAB));
		root.appendChild(n);
		n.appendChild(doc.createTextNode(EOL + space));
		root.appendChild(doc.createTextNode(EOL + space.substring(TAB.length())));
	} else {
		root.appendChild(doc.createTextNode(TAB));
		root.appendChild(n);
		root.appendChild(doc.createTextNode(EOL + space.substring(TAB.length())));
	}

	if (hasSubs) {
		java.util.List list = section.getElementSections();
		for (int i = 0; i < list.size(); i++){
			XmlConfigSection s = (XmlConfigSection) list.get(i);
			ret.addSection(s);
		}
	}

	return ret;
}
public XmlCreator addSection(String name) throws Exception {
	Node elem = doc.createElement(name);
	XmlCreator ret = new XmlCreator();
	ret.root = elem;
	ret.doc = elem.getOwnerDocument();
	ret.intendLevel = intendLevel + 1;
	String space = "";
	for (int i = 0; i < ret.intendLevel; i++){
		space += TAB;
	}
	root.appendChild(doc.createTextNode(TAB));
	root.appendChild(elem);
	elem.appendChild(doc.createTextNode(EOL + space));
	root.appendChild(doc.createTextNode(EOL + space.substring(TAB.length())));

	return ret;
}
public XmlCreator addTextValue(String name, String value) throws Exception {
	Element e = doc.createElement(name);
	XmlCreator ret = new XmlCreator();
	ret.root = e;
	ret.doc = e.getOwnerDocument();
	ret.intendLevel = 0;

	e.appendChild(doc.createTextNode("" + value));
	root.appendChild(doc.createTextNode(TAB));
	root.appendChild(e);
	String space = "";
	for (int i = 0; i < intendLevel; i++) {
		space += TAB;
	}
	root.appendChild(doc.createTextNode(EOL + space));
	return ret;
}
private void doTesting() throws Exception {
//	root.get Na removeChild() doc.removeChild(root);// "Configuration");
//	System.out.println("node: " + root);
//	Element elem = doc.createElement("TEST");
//	elem.appendChild(doc.createTextNode("test"));
	//root.removeChild(getChildConfigByName("Settings").root);
	//Element elem = doc.createElement("aaa");
	//elem.appendChild(doc.createElement("bbb"));
	//root.appendChild(elem);

	//doc.appendChild(doc.createElement("test"));
	//Node n = doc.getFirstChild();
	//root.appendChild(doc.createElement("test1"));
	//root.appendChild(doc.createElement("test2"));

//	doc.removeChild(getChildConfigByName("Settings").root);
//	System.out.println("\n\nnode: " + getChildConfigByName("Settings").root);

/*
	XmlDocument d = new XmlDocument();
	Element elem = d.createElement("TEST");
	elem.appendChild(new TextNode("AA"));
	d.write(System.out);
	System.out.println("" + d);
*/

/*
	XmlCreator c = addSection("ppp");
	c = c.addSection("mmmm");
	c.addAttribute("aaa", "bbb");

	XmlConfigSection sect = new XmlConfigSection(new java.io.File("c:\\projects\\eeg\\design\\1.xml"));

	sect = sect.getSection("Elements");

	Node n = sect.config.getRootNode();

	c.addSection(sect);
//	c.addNode(n);

	System.out.println("node: \n" + root);

*/

	XmlConfigSection sect = new XmlConfigSection(new java.io.File("c:\\projects\\eeg\\design\\1.xml"));

	XmlCreator c = new XmlCreator(sect);
	//c.removeSection("Runtime_frame");
	System.out.println("node: \n" + c);

}
public static void main(String[] args) {
	try {
		XmlCreator c = new XmlCreator();
		c.doTesting();
		System.out.println("finished");
	} catch (Exception e) {
		System.out.println("error: " + e);
		e.printStackTrace();
	}
}
public boolean removeSection(String name) throws Exception {
	NodeList l = root.getChildNodes();
	for (int i = 0; i < l.getLength(); i++){
		Node n = l.item(i);
		if (n.getNodeName().equals(name)) {
			root.removeChild(n);
			return true;
		}
	}

	return false;
}
public String toString() {
	return root.toString();
}

// If section already exists, return the one
// Otherwise create new

public XmlCreator putSection(String name) throws Exception {
	// Check if it exists

	NodeList l = root.getChildNodes();
	for (int i = 0; i < l.getLength(); i++){
		Node n = l.item(i);
		if (n.getNodeName().equals(name)) {
			XmlCreator ret = new XmlCreator();
			ret.root = n;
			ret.doc = n.getOwnerDocument();
			ret.intendLevel = intendLevel + 1;
			return ret;
		}
	}

	return addSection(name);
}
}
