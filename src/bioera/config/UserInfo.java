/* UserInfo.java v 1.0.9   11/6/04 7:15 PM
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

public abstract class UserInfo implements bioera.processing.Propertable {
	public String name = "MindPlace";
	public String phone = "360-663-1613";
	public String email = "sales@mindplace.com";

	private final static String propertiesDescriptions[][] = {
		{"name", "Name", "MindPlace",},
		{"phone", "Phone No", "360-663-1613"},
		{"email", "E-Mail", "sales@mindplace.com"},
	};
/**
 * UserInfo constructor comment.
 */
public UserInfo() {
	super();
}
/**
 * getPropertyDescription method comment.
 
public Object [] getPropertyDescription(String name) {
	return bioera.processing.ProcessingTools.searchPropertyDescription(name, propertiesDescriptions);
}
*/

/**
 * sendChangePropertyEvent method comment.
*nuked
public static final java.io.File getUserInfoFile(bioera.Main app) {
	return new java.io.File(app.getConfigFolder(), "userinfo.xml");
} */

/*
public final void loadUserInfo(bioera.Main app) throws Exception {
	

	java.io.File f = getUserInfoFile(app);
	if (!f.exists())
		return;
	XmlConfigSection config = new XmlConfigSection(f);
	Config.importXMLPropertiesA(app.userInfo, config);
	
}


/**
 * sendChangePropertyEvent method comment.
 
public static final void saveUserInfo(bioera.Main app) throws Exception {
	XmlCreator c = new XmlCreator();
	Config.exportXMLPropertiesA(app.userInfo, c);

	// Save config content
	java.io.FileWriter w = new java.io.FileWriter(new java.io.File(app.getConfigFolder(), "userinfo.xml"));
	w.write(c.toString());
	w.close();
}
*/

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
}
