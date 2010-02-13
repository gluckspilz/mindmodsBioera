/* JChart.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.graph.chart;

import java.awt.*;
import bioera.processing.*;
import bioera.*;
import java.lang.reflect.*;
import bioera.config.*;
import javax.swing.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public abstract class JChart {
	protected int layer = 0;
	private static int maxLayer = 0;
	protected boolean initialized;
	protected String chartName = "unknown";
	protected int compWidth = 90;
	protected int compHeight = 60;
	protected Element element;
	protected JChartDialog parentDialog;
	protected JComponent component;
	protected static boolean debug = false;
/**
 * ChartElement constructor comment.
 */
public JChart() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 7:39:52 PM)
 * @param data int[]
 * @param from int
 * @param length int
 */
public final String getChartName() {
	return chartName;
}
/**
 * getComponent method comment.
 */
public final JComponent getComponent() {
	return component;
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 7:39:52 PM)
 * @param data int[]
 * @param from int
 * @param length int
 */
public final int getComponentHeight() {
	return compHeight;
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 7:39:52 PM)
 * @param data int[]
 * @param from int
 * @param length int
 */
public final int getComponentWidth() {
	return compWidth;
}
/**
 * getElement method comment.
 */
public bioera.processing.Element getElement() {
	return element;
}
/**
 * getComponent method comment.
 */
public final int getLayer() {
	return layer;
}
/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 9:42:03 PM)
 * @return boolean
 */
public boolean isInitialized() {
	return initialized;
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/2003 11:46:02 AM)
 * @param data int[]
 * @param from int
 * @param length int
 */
public void load(XmlConfigSection section) throws java.lang.Exception {
	if (parentDialog != null) {
		parentDialog.setSize(section.getInteger("width", 100), section.getInteger("height", 80));
		parentDialog.relativeX = section.getIntegerThrow("x");
		parentDialog.relativeY = section.getIntegerThrow("y");
	} else {
		component.setSize(section.getInteger("width", 100), section.getInteger("height", 80));
		component.setLocation(section.getIntegerThrow("x"), section.getIntegerThrow("y"));
	}

	layer = section.getInteger("layer", 0);
	if (layer > maxLayer)
		maxLayer = layer;
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 7:39:52 PM)
 * @param data int[]
 * @param from int
 * @param length int
 */
public final static JChart newInstance(String className) throws Exception {
	try {
		Class c = Tools.createClass(className);
		JChart e = (JChart) c.newInstance();
		//Constructor constr = c.getConstructor(new Class[] {});
		//Chart e = (Chart) constr.newInstance(new Object[]{});
		return e;
	} catch (Exception e) {
		System.out.println("Problem with creating class '" + className + "'");
		throw e;
	}
}
/**
 * ChartElement constructor comment.
 */
public abstract void resetChart();
/**
 * 	Print on chart
 */
public void save(XmlCreator section) throws Exception {
	if (parentDialog != null) {
		section.addTextValue("x", "" + parentDialog.relativeX);
		section.addTextValue("y", "" + parentDialog.relativeY);
		section.addTextValue("width", "" + parentDialog.getWidth());
		section.addTextValue("height", "" + parentDialog.getHeight());
	} else {
		section.addTextValue("x", "" + component.getX());
		section.addTextValue("y", "" + component.getY());
		section.addTextValue("width", "" + component.getWidth());
		section.addTextValue("height", "" + component.getHeight());
	}
	section.addTextValue("layer", "" + layer);
	section.addTextValue("class", "" + Tools.getClassName(this));
}
/**
 * getComponent method comment.
 */
public final void setAtTopLayer() {
	layer = ++maxLayer;
}
/**
 * setElement method comment.
 */
public void setElement(bioera.processing.Element e) {
	element = e;
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 11:03:24 PM)
 * @param data int[]
 * @param from int
 * @param length int
 */
public final void setName(String s) {
	chartName = s;
}


/**
 * setParentDialog method comment.
 */
public void setParentDialog(JChartDialog d) {
	parentDialog = d;
}
}
