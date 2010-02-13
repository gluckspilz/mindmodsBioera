/* FolderBatch.java v 1.0.9   11/6/04 7:15 PM
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
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class FolderBatch {
	String pref;
	String post;
/**
 * FolderBatch constructor comment.
 */
public FolderBatch() {
	super();
}
/**
 * RuntimeManager constructor comment.
 */
public static void main(String args[]) throws Exception {
	try {
		FolderBatch app = new FolderBatch();
		app.start(args);	
	} catch (Throwable e) {
		e.printStackTrace();
	}	
}
/**
 * RuntimeManager constructor comment.
 */
public void recurse(String path) throws Exception {
	boolean any = false;
	File folder = new File(path);
	if (!folder.exists())
		System.out.println("Folder '" + folder + "' doesn't exist");
	String list[] = folder.list();
	for (int i = 0; i < list.length; i++){
		File f = new File(folder, list[i]);
		if (f.isDirectory()) {
			recurse(f.getPath());
		} else {
			if (list[i].endsWith(".java"))
				any = true;
		}
	}

	if (any)
		System.out.println("" + (pref != null ? pref : "") + " " + path + "" + (post != null ? post : ""));

}
/**
 * RuntimeManager constructor comment.
 */
public void start(String args[]) throws Exception {
	if (args.length != 3) {
		System.out.println("Args ");
		return;
	}

	String rootFolder = args[0];
	pref = args[1];
	if (pref.equals("none"))
		pref = null;
	post = args[2];	
	if (post.equals("none"))
		post = null;
	
	recurse(rootFolder);
}
}
