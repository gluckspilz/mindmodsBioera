/* DeprecatedFunctions.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.deprecated;

import bioera.processing.*;
import java.util.*;
import java.io.*;
import bioera.*;
import bioera.graph.designer.*;
import bioera.graph.runtime.*;
import bioera.graph.chart.*;
import bioera.config.*;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2004 8:08:23 PM)
 * @author: Jarek
 */
public class DeprecatedFunctions {
/**
 * Functions constructor comment.
 */
public DeprecatedFunctions() {
	super();
}
public static final ElementSet loadElementSetOldFormat(XmlConfigSection xmlElements) throws Exception {
	ElementSet ret = new ElementSet();
	XmlConfigSection xmlElement = null;
	if (xmlElements.containsSection("element"))
		xmlElement = xmlElements.getSection("element");
	Vector notLoaded = new Vector();
	String className = null;
	while (xmlElement != null) {
		String type = xmlElement.getType();
		try {
		if ("proc".equals(type)) {
			XmlConfigSection properties = xmlElement.getSection("properties");
			className = properties.getAttribute("class");
			if (className == null)
				className = xmlElement.getString("class");			
			Element elem = Element.newInstance(className);
			elem.setId(xmlElement.getIntegerThrow("id"));
			ConfigOld.importXMLProperties(elem, properties, true);
			if (elem instanceof Display){
				Chart chart = ((Display)elem).newChart();
				chart.load(xmlElement.getSection("chart"));
			}
			elem.getDesignerBox().load(xmlElement.getSection("design"));
			ret.addElement(elem);
		} else if ("conn".equals(type)) {
			// Check if this elem was loaded
			boolean nL = false;
			for (int i = 0; i < notLoaded.size(); i++){
				Element nle = (Element) notLoaded.get(i);
				if (nle.getId() == xmlElement.getIntegerThrow("src_id") ||
					nle.getId() == xmlElement.getIntegerThrow("dest_id")) {
					// It was not loaded, skip connecting
					nL = true;
					break;
				}
			}

			if (!nL) {
				ret.connectElements(xmlElement);
			} else {
				//if (debug)
					//System.out.println("Connection skept");
			}
		}
		} catch (Exception e) {
			System.out.println("Load design error:" + e + " while loading '" + className + "' in section '" + xmlElement + "'");
			e.printStackTrace();
		}

		xmlElement = xmlElement.getNextSectionByName();		
	}

	return ret;
}
}
