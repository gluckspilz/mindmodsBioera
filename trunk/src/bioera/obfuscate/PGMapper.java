/* PGMapper.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.obfuscate;

import java.io.*;
import java.util.*;

/**
 * Creation date: (6/30/2004 3:18:07 PM)
 * @author: Jarek Foltynski
 */
public class PGMapper {
	HashMap classes = new HashMap();
/**
 * Mapper constructor comment.
 */
public PGMapper() {
	super();
}
public String backwardClassName(String obfuscated) throws Exception {
	if (obfuscated == null || obfuscated.length() == 0)
		throw new Exception("Passed wrong value '" + obfuscated + "'");
	Iterator it = classes.keySet().iterator();
	while (it.hasNext()) {
		Assoc a = (Assoc) it.next();
		if (obfuscated.equals(a.obfuscated))
			return a.original;
	}

	return null;
}
public String backwardFieldName(String obfuscatedClass, String obfuscatedField) throws Exception {
	if (obfuscatedClass == null || obfuscatedClass.length() == 0)
		throw new Exception("Passed wrong class value '" + obfuscatedClass + "'");
	if (obfuscatedField == null || obfuscatedField.length() == 0)
		throw new Exception("Passed wrong field value '" + obfuscatedField + "'");
	Iterator it = classes.keySet().iterator();
	while (it.hasNext()) {
		Assoc a = (Assoc) it.next();
		if (obfuscatedClass.equals(a.obfuscated)) {
			ArrayList list = (ArrayList) classes.get(a);
			for (int i = 0; i < list.size(); i++){
				Assoc field = (Assoc) list.get(i);
				if (obfuscatedField.equals(field.obfuscated)) {
					return field.original;
				}
			}			
		}
	}

	return null;
}
public String forwardClassName(String original) throws Exception {
	if (original == null || original.length() == 0)
		throw new Exception("Passed wrong value '" + original + "'");
	Iterator it = classes.keySet().iterator();
	while (it.hasNext()) {
		Assoc a = (Assoc) it.next();
		if (original.equals(a.original))
			return a.obfuscated;
	}

	return null;
}
public String forwardFieldName(String originalClass, String originalField) throws Exception {
	if (originalClass == null || originalClass.length() == 0)
		throw new Exception("Passed wrong class value '" + originalClass + "'");
	if (originalField == null || originalField.length() == 0)
		throw new Exception("Passed wrong field value '" + originalField + "'");
	Iterator it = classes.keySet().iterator();
	while (it.hasNext()) {
		Assoc a = (Assoc) it.next();
		if (originalClass.equals(a.original)) {
			ArrayList list = (ArrayList) classes.get(a);
			for (int i = 0; i < list.size(); i++){
				Assoc field = (Assoc) list.get(i);
				if (originalField.equals(field.original)) {
					return field.obfuscated;
				}
			}
		}
	}

	return null;
}
public void load(File f) throws Exception {
	BufferedReader in = new BufferedReader(new FileReader(f));

	classes.clear();
	ArrayList oneClass = null;
	
	String line = in.readLine();
	while (line != null) {
		if (line.startsWith(" ")) {
			if (line.indexOf(")") == -1) {
				// Field, load it
				oneClass.add(loadFieldAssoc(line));
			} else {
				// Method, do not load
			}
		} else {
			// Class
			oneClass = new ArrayList();
			classes.put(loadClassAssoc(line), oneClass);
		}
		line = in.readLine();
	}
	in.close();	
}
public Assoc loadClassAssoc(String line) throws Exception {
	int i = line.indexOf("->");
	if (i == -1)
		throw new Exception("-> not found in line '" + line + "'");

	int j = line.indexOf(":");
	if (j == -1)
		throw new Exception(": not found in line '" + line + "'");

	String c = line.substring(0, i).trim();
    String nc = line.substring(i + 2, j).trim();

    return new Assoc(c, nc);
}
public Assoc loadFieldAssoc(String line) throws Exception {
	int i = line.indexOf("->");
	if (i == -1)
		throw new Exception("-> not found in line '" + line + "'");

	int j = line.lastIndexOf(" ", i - 2);

	String type = line.substring(0, j).trim();
	String n = line.substring(j + 1, i).trim();
	String nn = line.substring(i + 2).trim();

	return new Assoc(n, nn);
}
public static void main(String args[]) throws Exception {
        try
        {
	        System.out.println("started");
            PGMapper m = new PGMapper();
            m.load(new File("c:\\projects\\eeg\\obfusc\\obf.map"));
            m.print();
            System.out.println("finished");
        }
        catch(Throwable e)
        {
            System.out.println("Error: " + e + "\n\n");
            e.printStackTrace();
        }
    }
public void print() throws Exception {
	System.out.println("Classes: " + classes.keySet().size());
	Iterator it = classes.keySet().iterator();
	while (it.hasNext()) {
		Assoc a = (Assoc) it.next();
		System.out.println("" + a);
		ArrayList list = (ArrayList) classes.get(a);
		for (int i = 0; i < list.size(); i++){
			System.out.println("    " + list.get(i));
		}
	}
}
}
