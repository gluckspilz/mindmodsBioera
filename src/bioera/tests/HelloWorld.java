/* HelloWorld.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.tests;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class HelloWorld
{
    public HelloWorld()
    {
    }
    public static void main(String args[])
        throws Exception
    {
//		Class type = bioera.processing.properties.ComboProperty.class;
		Class type = Object.class;
//		System.out.println("re: " + (type.isAssignableFrom(bioera.processing.StructuredProperty.class)));
//		System.out.println("re: " + (type.isAssignableFrom(bioera.processing.properties.AbstractStructuredProperty.class)));
		System.out.println("re: " + (type.isAssignableFrom(String.class)));


        try
        {
            HelloWorld t = new HelloWorld();
            if (args.length == 0)
            	System.out.println("test ok");
            else if ("-x".equals(args[0])) {
	            t.xWindow();
            } else {
            	System.out.println("test ok");
            }
            System.out.println("finished");
        }
        catch(Exception e)
        {
            System.out.println("Error: " + e + "\n\n");
            e.printStackTrace();
        }
    }
public void println(String s) throws Exception {
	System.out.println(s);

	//logFileStream.write((s + "\n").getBytes());
}
public void xWindow() throws Exception{
	try {
		Frame f = new Frame();
		f.setBounds(10, 10, 100, 100);
                f.setVisible(true);
		//f.show();
	} catch(Exception e) {
        System.out.println("Error: " + e + "("+new Date()+")\n\n");
        e.printStackTrace();
    }
}
}
