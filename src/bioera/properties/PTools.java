/* PTools.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.properties;

import java.io.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import bioera.processing.*;
import bioera.*;
import bioera.layouts.*;
import bioera.graph.designer.*;


/**
 * Creation date: (5/26/2004 4:09:26 PM)
 * @author: Jarek Foltynski
 */
public class PTools {
/**
 * PropertiesLayout constructor comment.
 */
public PTools() {
	super();
}
/**
 * EdiDialog constructor comment.
 */
public static final void traverseComponents(Container panel, ComponentCallback callback) {
	for (int i = 0; i < panel.getComponentCount(); i++){		
		Component c = panel.getComponent(i);
		if (c instanceof Container)
			traverseComponents((Container) c, callback);
		else
			callback.set(c);
	}
}

/**
 * Returns either Component or PFields if this is panel
 */
public static final Object create(Object o) throws Exception {
	java.awt.Component ret = null;
	if (o instanceof Boolean) {
		ret = new JCheckBox("", ((Boolean)o).booleanValue());
	} else if (o instanceof SComponent) {
		SComponent sc = ((SComponent) o);
		ret = sc.getComponent();
	} else if (o instanceof JComponent) {
		ret = (JComponent) o;
	} else if (o instanceof Double) {
		double v = ((Double)o).doubleValue();
		if (v == (int) v)
			ret = new JTextField("" + (int) v);
		else
			ret = new JTextField("" + v);
	} else if (o instanceof Float) {
		float v = ((Float)o).floatValue();
		if (v == (int) v)
			ret = new JTextField("" + (int) v);
		else
			ret = new JTextField("" + v);
	} else if (o instanceof double[]) {
		ret = new JTextField(ProcessingTools.arrayToString((double[]) o));
	} else if (o instanceof Integer) {
		int v = ((Integer)o).intValue();
		if (v == Integer.MAX_VALUE)
			ret = new JTextField("MAX");
		else if (v == Integer.MIN_VALUE)
			ret = new JTextField("MIN");
		else
			ret = new JTextField("" + v);
	} else if (o instanceof Propertable[]) {
					
	} else if (o instanceof Propertable) {
		throw new Exception("Invalid structure, propertable should not be created here");
	} else if (o instanceof int[][]) {
		StringBuffer sb = new StringBuffer("[");
		int t[][] = (int[][]) o;
		for (int i = 0; i < t.length; i++){
			if (i > 0)
				sb.append("|");
			for (int j = 0; j < t[i].length; j++){
				if (j > 0)
					sb.append(",");
				sb.append(Integer.toString(t[i][j]));						
			}
		}
		ret = new JTextField(sb.toString() + "]");
	} else if (o instanceof int[]) {
		StringBuffer sb = new StringBuffer("[");
		int t[] = (int[]) o;
		for (int i = 0; i < t.length; i++){
			if (i > 0)
				sb.append("|");
			sb.append(Integer.toString(t[i]));
		}
		ret = new JTextField(sb.toString() + "]");
	} else if (o instanceof double[]) {
		StringBuffer sb = new StringBuffer("[");
		double t[] = (double[]) o;
		for (int i = 0; i < t.length; i++){
			if (i > 0)
				sb.append("|");
			if (Math.round(t[i]) == t[i])
				sb.append(Long.toString(Math.round(t[i])));
			else
				sb.append(Double.toString(t[i]));
		}
		ret = new JTextField(sb.toString() + "]");
	} else if (o instanceof String[]) {
		StringBuffer sb = new StringBuffer("[");
		String t[] = (String[]) o;
		for (int i = 0; i < t.length; i++){
			if (i > 0)
				sb.append("|");
			sb.append(t[i]);
		}
		ret = new JTextField(sb.toString() + "]");
	} else if (o instanceof Object[][]) {
		//System.out.println("converting obj[][]");		
		StringBuffer sb = new StringBuffer("[");
		Object t[][] = (Object[][]) o;
		for (int i = 0; i < t.length; i++){
			if (i > 0)
				sb.append("|");
			for (int j = 0; j < t[i].length; j++){
				if (j > 0)
					sb.append(",");
				sb.append("" + t[i][j]);						
			}
		}
		ret = new JTextField(sb.toString() + "]");
	} else if (o instanceof Object[]) {
		StringBuffer sb = new StringBuffer("[");
		Object t[] = (Object[]) o;
		for (int i = 0; i < t.length; i++){
			if (i > 0)
				sb.append("|");
			sb.append("" + t[i]);
		}
		ret = new JTextField(sb.toString() + "]");
	} else {
		ret = new JTextField("" + o);
	}

	if (ret == null)
		throw new RuntimeException("Unrecognized property " + o.getClass());

	return ret;
}

/**
 * EdiDialog constructor comment.
 */
public static final void fillContainer(PFields fields) throws Exception {
	fields.container.removeAll();

	if (fields.container instanceof JPanel)
		((JPanel)fields.container).setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		
	AdvancedGridLayout lay = new AdvancedGridLayout(0, 2, 2, 2);
	lay.setColWeight(1, 1);
	fields.container.setLayout(lay);

	if (fields.classInfo != null) {
		fields.container.add(fields.classInfo.label);
		fields.container.add((Component) fields.classInfo.graphComponent);
	}		

	for (int i = 0; i < fields.list.size(); i++){
		Object c = fields.getComponent(i);
		if (c instanceof Component) {
//			System.out.println("" + fields.getLabel(i));
			fields.container.add(fields.getLabel(i));
			fields.container.add((Component) c);
			if (!fields.getProperty(i).active) {
				disactivate((Component) c);
			}			
		} else if (c instanceof PFields) {
			PFields pfields = (PFields) c;
			JTabbedPane tabpane = fields.getTopTabbedPane();
			fillContainer(pfields);
			if (!((pfields.classObject instanceof ElementSet) || (pfields.classObject instanceof Propertable[])))
				tabpane.insertTab(fields.getLabelText(i), null, pfields.container, "", tabpane.getTabCount());
			if (!fields.getProperty(i).active) {
				disactivate(pfields.container);
			}			
			
			//PFields pfields = (PFields) c;
			//fields.container.add(fields.getLabel(i));
			//fillContainer(pfields);
			//fields.container.add(pfields.container);
			//if (!fields.getProperty(i).active) {
				//disactivate(pfields.container);
			//}			
		} else {
			throw new Exception("(fillContainer): unknown type: " +c);
		}		
	}
}

/**
 * EdiDialog constructor comment.
 */
public static final java.awt.Component getTopContainer(java.awt.Component c) throws Exception {
	Component ret = c;
	while (c != null) {
		ret = c;
		c = c.getParent();
	}
	return ret;
}

/**
 * EdiDialog constructor comment.
 */
public static final  JDialog getTopJDialog(java.awt.Component c) throws Exception {
	JDialog ret = null;
	while (c != null) {
		if (c instanceof JDialog)
			ret = (JDialog) c;
		c = c.getParent();
	}
	return ret;
}

/**
 * PropertiesLayout constructor comment.
 */
public static final void save(Propertable propertable, PFields fields) throws Exception {
	//System.out.println("--------------------");	
	ElementProperty properties[] = ProcessingTools.getElementProperties(propertable);
	for (int i = 0; i < properties.length; i++){
		ElementProperty p = properties[i];
		Object c = fields.getComponent(p.name);
		//System.out.println("setting " + p + " " + p.value.getClass() + "  " + c.getClass());		
		p.value = set(p.value, fields.get(p.name));
		//System.out.println("set to " + p.value);
	}	
		//System.out.println("------");
	
	ProcessingTools.setElementProperties(propertable, properties);
}

/**
 * EdiDialog constructor comment.
 */
public static final void disactivate(Component c) {
	if (c == null)
		return;
	if (c instanceof JTextField) {
		((JTextField) c).setEditable(false);
	} else if (c instanceof Container) {
		Container cont = (Container) c;
		for (int i = 0; i < cont.getComponentCount(); i++){
			disactivate(cont.getComponent(i));
		}
	} else {
		((Component)c).setEnabled(false);
	}

	//System.out.println("disactivated " + c.getName());	
}

/**
 * EdiDialog constructor comment.
 */
public static final Object createPFields(Object array[], Container container, PFields parent) throws Exception {
	if (array == null)
		throw new Exception("Array is null");

	// Process further only Propertable array
	if (!(array instanceof Propertable[]))
		return create(array);

	Propertable propertableArray[] = (Propertable[]) array;
		
	PFields ret = new PFields();
	ret.classInfo = null;;
	ret.classObject = array;
	ret.parent = parent;
			
	if (container == null) {
		container = new JPanel();
	}

	ret.container = container;
	
	// Set name of the panel
	container.setName("Panel for array" + array.getClass().getName());	
		
	for (int i = 0; i < array.length; i++){
		PFields pfields = createWithClass(propertableArray[i], null, parent);
		PField pf = new PField();
		Object c = pfields.getComponent("name");
		if (c != null && (c instanceof JTextField))
			pf.label = new JLabel(((JTextField)c).getText());
		else
			pf.label = new JLabel("Not known"); 
		pf.graphComponent = pfields;
		pf.property = new ElementProperty("arrayfield", propertableArray[i]);
		ret.add(pf);
	}

	return ret;	
}

/**
 * EdiDialog constructor comment.
 */
public static final PFields createPFields(Propertable propertable, Container container, PFields parent) throws Exception {
	if (propertable == null)
		throw new Exception("Propertable is null");

	PFields ret = new PFields();
	ret.parent = parent;
	ret.classObject = propertable;
			
	if (container == null) {
		container = new JPanel();
	}

	ret.container = container;
	
	// Set name of the panel
	if (propertable instanceof Element)
		container.setName("Panel for element: " + ((Element)propertable).getName());
	else
		container.setName("Panel for: " + Tools.getClassName(propertable));	
		
	JLabel label;	
	ElementProperty properties[] = ProcessingTools.getElementProperties(propertable);
	for (int i = 0; i < properties.length; i++){
		if (properties[i].description != null)
			label = new JLabel("" + properties[i].description + (("" + properties[i].description).length() > 0 ? ": " : ""), JLabel.RIGHT);
		else if (properties[i].name != null && ("" + properties[i].name).length() > 0)
			label = new JLabel("" + properties[i].name + ": ", JLabel.RIGHT);
		else
			label = new JLabel("", JLabel.RIGHT);
		label.setHorizontalAlignment(JLabel.LEFT);
		label.setVerticalAlignment(JLabel.TOP);
		label.setName("Label for property: " + properties[i].name);
		//System.out.println("comp=" + p.value.getClass());
		PField pf = new PField();
		pf.label = label;
		pf.property = properties[i];
		if (properties[i].value instanceof SComponent) {
			((SComponent) properties[i].value).setPField(pf);
			pf.graphComponent = ((SComponent) properties[i].value).getComponent();
		} else if (properties[i].value instanceof Propertable) {
			pf.graphComponent = createPFields((Propertable) properties[i].value, null, ret);
		} else if (properties[i].value instanceof Object[]) {
			pf.graphComponent = createPFields((Object[]) properties[i].value, null, ret);
		} else
			pf.graphComponent = create(properties[i].value);

		if (pf.graphComponent instanceof Component)
			((Component)pf.graphComponent).setName("Component for property: " + properties[i].name);
			
		if ("name".equals(properties[i].name.toLowerCase()))
			ret.list.add(0, pf);		
		else
			ret.list.add(pf);		
	}

	return ret;	
}

/**
 * EdiDialog constructor comment.
 */
public static final PFields createWithClass(Propertable propertable, Container panel, PFields parent) throws Exception {
	// Basic panel
	PFields ret = createPFields(propertable, panel, parent);

	// Add class name property
	JLabel label = new JLabel("Element: ", JLabel.LEFT);
	String cname = Tools.getClassName(propertable);
	if (cname.startsWith("bioera.")) {
		cname = cname.substring(cname.lastIndexOf('.') + 1);
	}
	JTextField tf = new JTextField(cname);
	tf.setEditable(false);
	ret.classInfo = new PField(label, tf);

	return ret;
}

/**
 * EdiDialog constructor comment.
 */
public static final Object set(Object obj, PField pf) {
	Object component = pf.graphComponent;
	Object ret = obj;
	try {
		if (obj == null) {
			return null;
		} else if (obj instanceof Boolean) {
			return new Boolean(((JCheckBox)component).isSelected());
		} else if (obj instanceof SComponent) {
			SComponent sc = ((SComponent) obj);
			sc.save();			
		} else if (obj instanceof Float) {
			return new Float(((JTextField)component).getText());
		} else if (obj instanceof Double) {
			return new Double(((JTextField)component).getText());
		} else if (obj.getClass().isArray()) {
			if (obj instanceof Propertable[]) {
				Propertable t[] = (Propertable[]) obj;
				PFields pfields = (PFields) pf.graphComponent;
				for (int i = 0; i < t.length; i++){
					save(t[i], (PFields) pfields.getComponent(i));
				}
			} else {
				return setArrayFromString(obj, ((JTextField) component).getText());
			}
		} else if (obj instanceof Integer) {
			String t = ((JTextField) component).getText();
			if ("MAX".equals(t))
				return new Integer(Integer.MAX_VALUE);
			else if ("MIN".equals(t))
				return new Integer(Integer.MIN_VALUE);
			else
				return new Integer(t);
		} else if (obj instanceof Propertable) {			
			if (!(component instanceof PFields))
				throw new Exception("Property is of type Propertable ("+obj.getClass()+"), but there is no corresponding PFields (" + component.getClass() + ")");
			//if (obj instanceof ElementSet) {
				////nothing
				////ElementSet es = (ElementSet) obj;
				////for (int i = 0; i < es.elements.length; i++){
					////set(es.elements[i], ((PFields) component).getComponent(i));
				////}
			//} else {
				save((Propertable) obj, (PFields) component);
//			}
		} else if (obj instanceof String) {
			ret = ((JTextField)component).getText();
		} else if (obj instanceof ColorWrapper) {
			ret = new ColorWrapper(((JTextField)component).getText());
		} else {
			throw new RuntimeException("Unrecognized property " + obj.getClass());
		}
	} catch (Exception e) {
		System.out.println("Couldn't set property " + obj.getClass() + " from " + component.getClass());
		e.printStackTrace();		
	}
	
	return ret;
}

/**
 * EdiDialog constructor comment.
 */
public static final Object setArrayFromString(Object obj, String s) throws Exception {
	if (obj instanceof int[][]) {
		return ProcessingTools.stringToInt2Array(s);
	} else if (obj instanceof int[]) {
		return ProcessingTools.stringToIntArray(s);
	} else if (obj instanceof double[]) {
		return ProcessingTools.stringToDoubleArray(s);
	} else if (obj instanceof String[][]) {
		return ProcessingTools.stringToString2Array(s);
	} else if (obj instanceof String[]) {
		return ProcessingTools.stringToStringArray(s);
	} else
		throw new Exception("Uknown array type");
}
}
