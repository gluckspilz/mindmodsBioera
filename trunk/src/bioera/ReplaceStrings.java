/* ReplaceStrings.java v 1.0.9   11/6/04 7:15 PM
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

import java.io.File;

/**
 * Insert the type's description here.
 * Creation date: (6/7/2001 2:48:54 PM)
 * @author: Jarek Foltynski
 */
class ReplaceStrings {
	String from, to;
/**
 * RegExp constructor comment.
 */
public ReplaceStrings() {

}
/**
 * RegExp constructor comment.
 */
public final static void main(String args[]) {
	try {
		new ReplaceStrings().start(args);
		if (args.length < 2) {
			System.out.println("Args");
			System.exit(1);
		}
		System.out.println("finished");
	} catch (Exception e) {
		System.out.println(e);
	}
}
/**
 * RegExp constructor comment.
 */
public void process(File d)  throws Exception {
	String list[] = d.list();
	for (int i = 0; i < list.length; i++){
		File f = new File(d, list[i]);
		if (f.isDirectory()) {
			process(f);
		} else if (!f.getName().endsWith(".gif") 
			&& !f.getName().endsWith(".jpg") 
			&& !f.getName().endsWith(".doc") 
			&& !f.getName().endsWith(".png") 
			&& !f.getName().endsWith(".zip") 
			&& !f.getName().endsWith(".exe")
			&& !f.getName().endsWith(".jar")
			&& !f.getName().endsWith(".dll")
			) {
			processFile(f);
		}
	}
}
/**
 * RegExp constructor comment.
 */
public void processFile(File f) throws Exception {
	byte b[] = Tools.readFile(f, 100000);
	String s = new String(b);
	String s1; boolean any = false;
	s = Tools.changeSubstr(s1 = s, from, to);
	if (!s.equals(s1))
		any = true;
	if (any)
		Tools.writeFile(f, s.getBytes());
}
/**
 * RegExp constructor comment.
 */
public void start(String s[])  throws Exception {
	from = s[0];
	to = s[1];

	File d = new File(".");
	process(d);
}
}
