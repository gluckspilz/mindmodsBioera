/* ConfigurableSystemSettings.java v 1.0.9   11/6/04 7:15 PM
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

package bioera;



public class ConfigurableSystemSettings implements bioera.processing.Propertable {
	public static boolean readOnly = true;
	public static int DESIGNER_ELEMENT_DEFAULT_WIDTH = 60;
	public static int DESIGNER_ELEMENT_DEFAULT_HEIGHT = 90;
	public static int DESIGNER_ZOOM = 100;
	public static boolean DESIGNER_SHOW_INOUT_NAMES = true;
	public static ColorWrapper backgroundColor = new ColorWrapper("(192,192,192)");
        public static ColorWrapper panelbgColor = new ColorWrapper("(192,192,192)");
	public static boolean backupDesign = false;

	private final static String propertiesDescriptions[][] = {
		{"DESIGNER_ELEMENT_DEFAULT_WIDTH","Designer element box default width",""},
		{"DESIGNER_ELEMENT_DEFAULT_HEIGHT","Designer element box default height",""},
		{"DESIGNER_ZOOM","Designer zoom [%]",""},
		{"DESIGNER_SHOW_INOUT_NAMES","Show designer element's input/output names",""},
		{"readOnly","Read only",""},
	};

public ConfigurableSystemSettings() {
	super();
}
/**
 * getPropertyNames method comment.
 */
public static int getDesignerElementHeight() {
	return DESIGNER_ELEMENT_DEFAULT_HEIGHT * DESIGNER_ZOOM / 100;
}
/**
 * getPropertyNames method comment.
 */
public static int getDesignerElementWidth() {
	return DESIGNER_ELEMENT_DEFAULT_WIDTH * DESIGNER_ZOOM / 100;
}
public java.lang.Object[] getPropertyDescription(java.lang.String name) {
	return bioera.processing.ProcessingTools.searchPropertyDescription(name, propertiesDescriptions);
}
/**
 * getPropertyNames method comment.
 */
public java.lang.String[] getPropertyNames() {
	return null;
}
public void sendChangePropertyEvent(String fieldName, Object oldValue) {
	if ("DESIGNER_ZOOM".equals(fieldName)) {
		//System.out.println("-------------: Rezooming all elements");
		if (Main.app.designFrame != null) {
			Main.app.designFrame.rezoom();
			Main.app.designFrame.repaint();
		}
	}
}
}
