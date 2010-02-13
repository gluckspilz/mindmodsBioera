/* XmlConfig.java v 1.0.9   11/6/04 7:15 PM
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

import java.io.*;
import java.util.*;

// Exception Classes
import java.io.FileNotFoundException;
import java.io.IOException;

// XML Parser
import javax.xml.parsers.*;

// W3 XML Document objects
import org.w3c.dom.*;
import org.xml.sax.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class XmlConfig {
	protected Node root;
/**
 * ConfigFile constructor comment.
 */
private XmlConfig() {
	super();
}
/**
 * ConfigFile constructor comment.
 */
public XmlConfig(File ifile) throws Exception {
	if (ifile == null || ifile.getAbsolutePath().length() == 0) {
		throw new Exception("Wrong filename required for constructor '" + ifile + "'");
	} else {
		root = parse(ifile);
	}
}
/**
 * ConfigFile constructor comment.
 */
XmlConfig(Node n) throws Exception {
	if (n == null)
		throw new Exception("Root node must not be null");
	root = n;
}
/**
 * Returns names of all sections (only ELEMENT_NODE type).
 */
public Vector elementsNames() {
	NodeList childs = root.getChildNodes();
	int length = childs.getLength();
	Vector ret = new Vector();
	for (int i = 0; i < length; i++) {
		Node n = childs.item(i);
		if (n.getNodeType() == Node.ELEMENT_NODE)
			ret.addElement(n.getNodeName());
	}

	return ret;
}
/**
 * ConfigFile constructor comment.
 */
public String getAllText() throws Exception {
	NodeList list = root.getChildNodes();
	StringBuffer sb = new StringBuffer();
	for (int i = list.getLength() - 1; i >= 0; i--){
		Node n = list.item(i);
		// Sum all TEXTs to the return value
		if (n.getNodeType() == Node.TEXT_NODE) {
			String s = n.getNodeValue();
			// Skip values that contains only white characters
			if (s.trim().length() > 0)
				sb.append(s);
		}
	}

	return sb.toString();
}
/**
 * Returns first found attribute.
 */
public String getAttribute(String str) {
	return (String) getAttributes().get(str);
}
/**
 * Returns sections names.
 */
public Map getAttributes() {
	HashMap map = new HashMap();
	NamedNodeMap m = root.getAttributes();
	if (m != null) {
		for (int i = 0; i < m.getLength(); i++){
			map.put(m.item(i).getNodeName(), m.item(i).getNodeValue());
		}
	}

	return map;
}
/**
 * ConfigFile constructor comment.
 */
public XmlConfig getChildConfigByAttribute(String attr, String value) throws Exception {
	if (attr == null || value == null)
		throw new Exception("Null parameter");

	List list = getChildConfigs();
	for (int i = 0; i < list.size(); i++){
		XmlConfig c = (XmlConfig) list.get(i);
		String v = c.getAttribute(attr);
		if (v != null && v.equals(value)) {
			return c;
		}
	}

	throw new Exception("Child node with attribute '" + attr + "'='" + value + "' not found on '" + getName() + "'");
}
/**
 * ConfigFile constructor comment.
 */
public XmlConfig getChildConfigByName(String path) throws Exception {
	try {
		return getChildConfigByName(root, path);
	} catch (Exception e) {
		throw new Exception("Node '" + path + "' on '" + getName()  + "' not found");
	}
}
/**
 * ConfigFile constructor comment.
 */
private XmlConfig getChildConfigByName(Node node, String path) throws Exception {
	int i = path.indexOf('.');
	if (i == -1) {
		XmlConfig c = new XmlConfig();
		c.root = getSubNodeByName(node, path);
		return c;
	} else {
		String nodename = path.substring(0, i);
		Node subnode = getSubNodeByName(node, nodename);
		return getChildConfigByName(subnode, path.substring(i + 1));
	}
}
/**
 * List of all subnodes (wrapped in XmlConfig).
 */
public List getChildConfigs() throws Exception {
	List ret = new ArrayList();
	NodeList nodeList = root.getChildNodes();
	for (int i = 0; i < nodeList.getLength(); i++) {
		ret.add(new XmlConfig(nodeList.item(i)));
	}

	return ret;
}
/**
 * List of all subnodes (wrapped in XmlConfig).
 */
public List getChildElementConfigs() throws Exception {
	List ret = new ArrayList();
	NodeList nodeList = root.getChildNodes();
	for (int i = 0; i < nodeList.getLength(); i++) {
		Node n = nodeList.item(i);
		if (n.getNodeType() == Node.ELEMENT_NODE)
			ret.add(new XmlConfig(n));
	}

	return ret;
}
/**
 * Returns the most deep config in xml tree.
 */
public XmlConfig getGreatRootConfig() throws Exception {

	return new XmlConfig(root.getOwnerDocument().getFirstChild());

}
/**
 * ConfigFile constructor comment.
 */
public String getName() {
	return root.getNodeName();
}
/**
 * Returns next config on the same level.
 */
public XmlConfig getNextConfig() throws Exception {
	try {
		//System.out.println("Node=" + root.getNodeName());
		Node parent = root.getParentNode();
		//System.out.println("Parent=" + parent.getNodeName());
		if (parent == null)
			return null;

		NodeList list = parent.getChildNodes();
		int size = list.getLength();
		for (int i = 0; i < size; i++){
		//System.out.println("Child=" + list.item(i).getNodeName());
			if (list.item(i) == root) {
				// Found this branch
				// Search next that is an element
				for (int j = i + 1; j < size; j++){
					if (list.item(j).getNodeType() == Node.ELEMENT_NODE)
						return new XmlConfig(list.item(j));
				}

				break;
			}
		}
	} catch (Exception e) {
	}

	return null;
}
/**
 * Returns sections names.
 */
public XmlConfig getParentConfig() throws Exception {
	Node node = root.getParentNode();
	if (node == null)
		throw new RuntimeException("Parent config for '" + getName() + "' doesn't exist");
	else
		return new XmlConfig(node);
}
/**
 * ConfigFile constructor comment.
 */
public String getPathFromTop() throws Exception {
	StringBuffer sb = new StringBuffer(getName());
	Node n = root;
	while ((n = n.getParentNode()) != null)
		sb.insert(0, n.getNodeName() + ".");
	return sb.toString();
}
/**
 *
 *
 * @return org.w3c.dom.Node
 */
public org.w3c.dom.Node getRootNode() {
	return root;
}
/**
 * ConfigFile constructor comment.
 */
public String getString(String key) throws Exception {
	Node node = getSubNodeByName(key);
	if (node == null)
		throw new Exception("Key '" + key + "' not found in section '" + getName() + "'");
	return new XmlConfig(node).getAllText();
}
/**
 * ConfigFile constructor comment.
 */
private Node getSubNodeByName(String nodeName) throws Exception {
	return getSubNodeByName(root, nodeName);
}
/**
 * ConfigFile constructor comment.
 */
private Node getSubNodeByName(Node parent, String nodeName) throws Exception {
	NodeList nodeList = parent.getChildNodes();
	for (int i = 0; i < nodeList.getLength(); i++){
		Node node = nodeList.item(i);
		if (node.getNodeName().equals(nodeName))
			return node;
	}

	throw new Exception("SubNode '" + nodeName + "' not found in '" + parent.getNodeName() + "'");
}
/**
 * ConfigFile constructor comment.
 */
public static void main(String[] args) {
	try {
		XmlConfig c = new XmlConfig(new java.io.File("c:\\projects\\eeg\\design\\default.xml"));
		c.printTestValues();
		System.out.println("node: " + c.root);
	//	System.out.println("Result:\n" + c);
	} catch (Exception e) {
		System.out.println("error: " + e);
		e.printStackTrace();
	}
}
/**
 * ConfigFile constructor comment.
 */
public static Node parse(File f) throws Exception{
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setValidating(false);
    dbf.setNamespaceAware(false);
    dbf.setIgnoringComments(true);
    dbf.setIgnoringElementContentWhitespace(true);

	DocumentBuilder docBuilder = dbf.newDocumentBuilder();

	Document doc = docBuilder.parse(f);

        NodeList nodelisty = doc.getChildNodes();

        //root = nodelisty.item(0);

        return nodelisty.item(0);
        //System.out.println("from parse return node : :"+doc.getFirstChild());
	//return doc.getFirstChild();
}
/**
 * ConfigFile constructor comment.
 */
private void printTestValues() throws Exception {
	XmlConfig config = getChildConfigByName("Elements");
	System.out.println(config + " " + config.getName());

	//config = config.getNextConfig();
	System.out.println(config.getName());

}
/**
 * ConfigFile constructor comment.
 */
private void saveAs(File destFile) throws Exception {
/*
//	XMLDocument doc = new XMLDocument();
	java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
	if (root != null){
		java.io.FileOutputStream l_fos = new java.io.FileOutputStream(destFile);
		root.getOwnerDocument(). print(baos);
		baos.close();
		l_fos.write(baos.toByteArray());
		l_fos.close();
	}
*/
}

/**
 * ConfigFile constructor comment.
 */
public XmlConfig(String s) throws Exception {
	root = parseString(s);
}

/**
 * ConfigFile constructor comment.
 */
public static Node parseString(String s) throws Exception{
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setValidating(false);
    dbf.setNamespaceAware(false);
    dbf.setIgnoringComments(true);
    dbf.setIgnoringElementContentWhitespace(true);

	DocumentBuilder docBuilder = dbf.newDocumentBuilder();

	//System.out.println("parsing " + s);
	Document doc = docBuilder.parse(new ByteArrayInputStream(s.getBytes()));
	//System.out.println("pared " + s);

        NodeList nodelisty = doc.getChildNodes();
        return  nodelisty.item(0);
//	return doc.getFirstChild();
}
}
