/* DesignInfo.java v 1.0.9   11/6/04 7:15 PM
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

public abstract class DesignInfo implements bioera.processing.Propertable {
//	public UserInfo author = new UserInfo();
	public String date = "";
/**
	private final static String propertiesDescriptions[][] = {
		{"author", "Author", "", "false"},
		{"date", "Creation date", "", "false"},
	};
**/
/**
 * DesignInfo constructor comment.
 */
public DesignInfo() {
	super();
}
/**
 */
//public Object [] getPropertyDescription(String name) {
//	return bioera.processing.ProcessingTools.searchPropertyDescription(name, propertiesDescriptions);
//}
/**
 * sendChangePropertyEvent method comment.
 */
public void sendChangePropertyEvent(java.lang.String fieldName) {}

/**
 * getPropertyNames method comment.
 */
public java.lang.String[] getPropertyNames() {
	return null;
}

/**
 * sendChangePropertyEvent method comment.
 */
public void sendChangePropertyEvent(String fieldName, Object oldValue) {

}

/**
 * Insert the method's description here.
 * Creation date: (10/22/2004 1:45:06 PM)
 * @return bioera.config.UserInfo
 */
//public UserInfo getAuthor() {
//	return author;
//}

/**
 * Insert the method's description here.
 * Creation date: (10/22/2004 1:45:06 PM)
 * @param newAuthor bioera.config.UserInfo
 */
//public void setAuthor(UserInfo newAuthor) {
//	author = newAuthor;
//}
}
