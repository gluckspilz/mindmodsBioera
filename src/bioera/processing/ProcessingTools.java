/* ProcessingTools.java v 1.0.9   11/6/04 7:15 PM
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


import java.lang.reflect.*;
import bioera.*;

import java.util.*;
import java.io.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class ProcessingTools {
/**
 * Tools constructor comment.
 */
public ProcessingTools() {
	super();
}
/**
 * Tools constructor comment.
 */
public final static String arrayToString(Object s[]) {
	return arrayToString(s, -1);
}
/**
 * Tools constructor comment.
 */
public final static void checkVectorSize(int calculatedSize, BufferedVectorPipe input) throws Exception {
	if (calculatedSize != input.getSignalParameters().getVectorLength()) {
		throw new Exception("Vector size '" + input.getSignalParameters().getVectorLength() + "' doesn't match calculated size " + calculatedSize + " (" + input.getConnectedDistributors() + ")");
	}
}
/**
 * Tools constructor comment.
 */
public final static ElementProperty [] convertProperties(Vector names, Vector values) throws Exception {
	ElementProperty p[] = new ElementProperty[names.size()];
	for (int i = 0; i < p.length; i++){
		p[i] = new ElementProperty();
		p[i].name = "" + names.elementAt(i);
		p[i].value = values.elementAt(i);
	}
	return p;
}
/**
 * Tools constructor comment.
 */
public final static String formatNumber(long n) {
	StringBuffer sb = new StringBuffer();
	while (n > 0) {
		if (sb.length() > 0)
			sb.insert(0, ".");
		sb.insert(0, "" + (n % 1000));	
		n = n / 1000;
	}
	
	return sb.toString();
}
/**
 * Tools constructor comment.
 */
public static String getDescriptionFromFile(Element element) {
	String name = Tools.getClassName(element);
	// Strip .class
	int i = name.lastIndexOf('.');
	name = name.substring(0, i);
	i = name.lastIndexOf('.');
	name = name.substring(i + 1);

	File f = new File(Main.app.getDocFolder(), "element");
	f = new File(f, name);
	if (f.exists()) {
		try {
			return new String(Tools.readFile(f, -1));
		} catch (Exception e) {
		}
	}

	return null;
}
/**
 * Tools constructor comment.
 */
public final static ElementProperty [] getElementProperties(Propertable element) throws Exception {
	if (bioera.obfuscate.Mapper.inited) {
		ElementProperty ret[] = bioera.obfuscate.Mapper.getElementProperties(element);
		if (ret != null)
			return ret;
	}
	
	Field fields[] = getPublicFields(element.getClass());
	
	ElementProperty ret[] = new ElementProperty[fields.length];
	for (int n = 0; n < fields.length; n++){		
		ElementProperty prop = ret[n] = new ElementProperty();
		Field field = fields[n];
		prop.name = field.getName();
		prop.value = field.get(element);
		if (prop.value == null) {
			//System.out.println("-------------Prop " + prop.name + " is null in " + element.getClass());
			//Thread.dumpStack();
			//prop.value = "";
		}
		if (element.getPropertyDescription(prop.name) != null) {
			Object propertyDescription[] = element.getPropertyDescription(prop.name);
			prop.description = propertyDescription[1];
			if (propertyDescription.length > 3 && "false".equals(propertyDescription[3])) {
				prop.active = false;
			}
		}
	}
	
	return ret;
}
/**
 * Tools constructor comment.
 */
public final static boolean isNone(String s) {
	return s == null || s.length() == 0 || "null".equals(s);
}
/**
 * Tools constructor comment.
 */
public static String propertiesToString(Object element) {
	StringBuffer sb = new StringBuffer("[");
	try {
		Field f[] = element.getClass().getFields();
		int count = 0;
		for (int i = 0; i < f.length; i++){
			int modifiers = f[i].getModifiers();
			if (!Modifier.isFinal(modifiers)) {
				Object o = f[i].get(element);
				if (sb.length() > 1)
					sb.append(", ");
				if (o instanceof Object[]) {
					Object t[] = (Object[]) o;
					if (t.length > 3) {
						Object t1[] = new Object[4];
						System.arraycopy(t, 0, t1, 0, 3);
						t1[3] = "...";
						sb.append(f[i].getName() + "=" + arrayToString(t1));
					} else 
						sb.append(f[i].getName() + "=" + arrayToString((Object[]) o));
				} else {
					sb.append(f[i].getName() + "=" + o);
				}
			}
		}
		
		sb.append(", class=" + element.getClass());
	} catch (Exception e) {
		sb.append("Error: " + e.toString());
	}

	return sb.toString() + "]";
}
/**
 * Tools constructor comment.
 */
public final static void setElementProperties(Propertable element, ElementProperty props[]) {
	if (bioera.obfuscate.Mapper.inited) {
		try {
			if (bioera.obfuscate.Mapper.setElementProperties(element, props))
				return;
		} catch (Exception e) {
			if (SystemSettings.showStack)
				e.printStackTrace();
			return;
		}
	}
	
	for (int n = 0; n < props.length; n++){
		ElementProperty p = props[n];
		try {
			Field field = element.getClass().getField(p.name);
			Object o = field.get(element);
			field.set(element, p.value);
			if (o != null && !o.equals(p.value)) {
				element.sendChangePropertyEvent(p.name, o);
			}
		} catch (Exception e) {
			System.out.println("Can't set property '" + p.name + "' to '" + p.value + "'("+p.value.getClass()+") in class '" + element.getClass() + "': " + e);
			if (SystemSettings.showStack)
				e.printStackTrace();
		}
	}
}
/**
 * Tools constructor comment.
 */
public final static void showTimeCharacteristic(long t[]) {
	System.out.print("Times: ");
	for (int i = 1; i < t.length; i++){
		System.out.print((t[i] - t[i-1]) + " ");
	}
	System.out.println();
}
/**
 * Tools constructor comment.
 */
public final static void showTimengCharacteristic(long t[]) {
	System.out.print("Times: ");
	for (int i = 0; i < t.length; i++){
		System.out.print(t[i] + " ");
	}
	System.out.println();
}
/**
 * Tools constructor comment.
 */
public final static double[] stringToDoubleArray(String s) {
	Vector v = new Vector();
	StringTokenizer tok = new StringTokenizer(s, ",| []");
	while (tok.hasMoreTokens()) {
		String token = tok.nextToken();
		if (token != null)
			v.addElement(token);
	}
	
	double ret[] = new double[v.size()];
	for (int i = 0; i < ret.length; i++){
		try {	
			ret[i] = Double.parseDouble((String) v.elementAt(i));
		} catch (Exception e) {
			ret[i] = -1;
		}
	}
	
	return ret;
}
/**
 * Tools constructor comment.
 */
public final static int[][] stringToInt2Array(String s) {
	Vector rows = new Vector();
	//System.out.println("s=" + s);	
	StringTokenizer tok = new StringTokenizer(s, "[|]");
	while (tok.hasMoreTokens()) {
		String token = tok.nextToken();
		//System.out.println("tok_0=" + tok);
		if (token != null)
			rows.addElement(token);
	}
	
	int ret[][] = new int[rows.size()][];

	for (int r = 0; r < ret.length; r++){
		//System.out.println("tok=" + rows.elementAt(r));
		tok = new StringTokenizer((String) rows.elementAt(r), ",.; ");
		Vector v = new Vector();
		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			if (token != null) {
				v.addElement(token);
				//System.out.println("t1=" + token);				
			}
		}
				
		ret[r] = new int[v.size()];
			
		for (int i = 0; i < v.size(); i++){
			try {	
				ret[r][i] = Integer.parseInt((String) v.elementAt(i));
				//System.out.println("tok v=" + ret[r][i]);				
			} catch (Exception e) {
				ret[r][i] = -1;
			}
		}
	}
	
	return ret;
}
/**
 * Tools constructor comment.
 */
public final static int[] stringToIntArray(String s) {
	Vector v = new Vector();
	StringTokenizer tok = new StringTokenizer(s, ",| []");
	while (tok.hasMoreTokens()) {
		String token = tok.nextToken();
		if (token != null)
			v.addElement(token);
	}
	
	int ret[] = new int[v.size()];
	for (int i = 0; i < ret.length; i++){
		try {	
			ret[i] = Integer.parseInt((String) v.elementAt(i));
		} catch (Exception e) {
			ret[i] = -1;
		}
	}
	
	return ret;
}
/**
 * Tools constructor comment.
 */
public final static String[] stringToStringArray(String s) {
	Vector v = new Vector();
	StringTokenizer tok = new StringTokenizer(s, ",|[]");
	while (tok.hasMoreTokens()) {
		String token = tok.nextToken();
		if (token != null)
			v.addElement(token);
	}
	
	String ret[] = new String[v.size()];
	for (int i = 0; i < ret.length; i++){
		try {	
			ret[i] = (String) v.elementAt(i);
		} catch (Exception e) {
			ret[i] = null;
		}
	}
	
	return ret;
}
/**
 * Tools constructor comment.
 */
public final static void verifyDesignState(boolean state, Element e) throws bioera.graph.designer.DesignException {
	if (!state)
		throw new bioera.graph.designer.DesignException("Required settings not set in element '" + e.getName() + "'");
}

/**
 * Tools constructor comment.
 */
public final static Object [] searchPropertyDescription(String name, Object table[][]) {
	if (table == null || name == null)
		return null;

	for (int i = 0; i < table.length; i++){
		if (name.equals(table[i][0])) {
			return table[i];
		}
	}
	
	return null;
}

/**
 * Tools constructor comment.
 */
public final static Object traversAllForFeature(Class c) throws Exception {
	Element e[] = Main.app.processor.getAllElements();
	for (int i = 0; i < e.length; i++){
		if (c.isInstance(e[i])) {
			return e[i];
		} else {
			//System.out.println("not " + e[i].getClass());
		}
	}
	return null;
}

/**
 * Tools constructor comment.
 */
public final static String arrayToString(int s[][]) {
	StringBuffer sb = new StringBuffer("[");
	for (int i = 0; i < s.length; i++){
		sb.append("" + arrayToString(s[i]));
	}
	return sb.toString() + "]";
}

/**
 * Tools constructor comment.
 */
public final static String arrayToString(int s[]) {
	StringBuffer sb = new StringBuffer("[");
	for (int i = 0; i < s.length; i++){
		if (i > 0)
			sb.append(", ");
		sb.append("" + s[i]);
	}
	return sb.toString() + "]";
}

/**
 * Tools constructor comment.
 */
public final static String arrayToString(double s[]) {
	StringBuffer sb = new StringBuffer("[");
	for (int i = 0; i < s.length; i++){
		if (i > 0)
			sb.append(", ");
		sb.append("" + s[i]);
	}
	return sb.toString() + "]";
}

/**
 * Tools constructor comment.
 */
public final static String toBinaryString(int s) {
	StringBuffer sb = new StringBuffer(Integer.toBinaryString(s));
	while (sb.length() < 8)
		sb.insert(0, "0");
	return sb.toString();
}

/**
 * Tools constructor comment.
 */
public final static String arrayToString(String s[][]) {
	StringBuffer sb = new StringBuffer("[");
	for (int i = 0; i < s.length; i++){
		sb.append("" + arrayToString(s[i]));
	}
	return sb.toString() + "]";
}

/**
 * Tools constructor comment.
 */
public final static String[][] stringToString2Array(String s) {
	Vector rows = new Vector();
	//System.out.println("s=" + s);	
	StringTokenizer tok = new StringTokenizer(s, "[|]");
	while (tok.hasMoreTokens()) {
		String token = tok.nextToken();
		//System.out.println("tok_0=" + tok);
		if (token != null)
			rows.addElement(token);
	}
	
	String ret[][] = new String[rows.size()][];

	for (int r = 0; r < ret.length; r++){
		//System.out.println("tok=" + rows.elementAt(r));
		tok = new StringTokenizer((String) rows.elementAt(r), ",.; ");
		Vector v = new Vector();
		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			if (token != null) {
				v.addElement(token);
				//System.out.println("t1=" + token);				
			}
		}
				
		ret[r] = new String[v.size()];
			
		for (int i = 0; i < v.size(); i++){
			ret[r][i] = (String) v.elementAt(i);
		}
	}
	
	return ret;
}

/**
 * Tools constructor comment.
 */
public final static Field[] getPublicFields(Class c) throws Exception {
	ArrayList list = new ArrayList();
	Field fields[] = c.getFields();
	for (int i = 0; i < fields.length; i++){
		int modifiers = fields[i].getModifiers();
		if (Modifier.isPublic(modifiers) && !Modifier.isFinal(modifiers))
			list.add(fields[i]);
	}
	
	return (Field[]) list.toArray(new Field[0]);
}

/**
 * Tools constructor comment.
 */
public static String processorActiveElementsToString() {
	StringBuffer sb = new StringBuffer("Elements\n");
	for (int i = 0; i < Main.app.processor.getActiveElements().length; i++){
		sb.append("" + Main.app.processor.getActiveElements()[i]);
	}

	return sb.toString();
}

/**
 * Tools constructor comment.
 */
public static String processorAllElementsToString() {
	StringBuffer sb = new StringBuffer("Elements\n");
	for (int i = 0; i < Main.app.processor.getAllElements().length; i++){
		sb.append("" + Main.app.processor.getAllElements()[i]);
	}

	return sb.toString();
}

/**
 * Tools constructor comment.
 */
public final static Object appendArray(Object a[], Object value) {
    Object[] ret = (Object[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), a.length + 1);
	System.arraycopy(a, 0, ret, 0, a.length);
	ret[a.length] = value;
	return ret;
}

/**
 * Tools constructor comment.
 */
public final static Object removeAllFromArray(Object a[], Object value) {
	Object[] ret = a;
	for (int i = 0; i < a.length; i++){
		if (value == a[i]) {
		    ret = (Object[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), a.length - 1);
			System.arraycopy(a, 0, ret, 0, i);
			if (i + 1 < a.length)
				System.arraycopy(a, i+1, ret, i, a.length - (i + 1));
			a = ret;
			i--;
		}
	}

	return ret;
}

/**
 * Tools constructor comment.
 */
public final static String arrayToString(Object s[], int maxSize) {
	if (s == null)
		return "null";
	StringBuffer sb = new StringBuffer("[");
	for (int i = 0; i < s.length; i++){
		if (i > 0)
			sb.append(", ");
		if (maxSize > 0) {
			if (i >= maxSize) {
				sb.append("...");
				break;
			}
		}
		sb.append("" + s[i]);
	}
	return sb.toString() + "]";
}

/**
 * Tools constructor comment.
 */
public static void printSignalParameters(Element element) {
	if (element != null)
		System.out.println("" + new SignalParameters(element));
}

/**
 * Tools constructor comment.
 */
public final static Object traversPredecessorsForFeature(Element e, Class c) throws Exception {
	List list = new ArrayList();
	return traversPredecessorsForFeature(e, c, list);
}

/**
 * Tools constructor comment.
 */
private final static Object traversPredecessorsForFeature(Element e, Class c, List list) throws Exception {
	if (list.contains(e)) {
		// A loop was found, exit
		return null;
	}

	if (c.isInstance(e)) {
		//System.out.println("found " + e.getName());
		return e;
	}
		
	list.add(e);
	//System.out.println("trav added=" + e.getName());	
	for (int i = 0; i < e.getInputsCount(); i++){
		Element elems[] = e.getElementsConnectedToInput(i);
		//System.out.println("elems=" + elems.length);					
		if (elems == null)
			continue;
		for (int j = 0; j < elems.length; j++){
			Element elem = elems[j];
			//System.out.println("trav in=" + elem.getName());			
			Object o = traversPredecessorsForFeature(elem, c, list);
			if (o != null)
				return o;
			
		}
	}

	return null;
}
}
