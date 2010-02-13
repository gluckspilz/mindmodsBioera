/* SystemProcess.java v 1.0.9   11/6/04 7:15 PM
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



public class SystemProcess
{
	public static boolean isNative;
	
	static {
		try {
			System.loadLibrary("sysproc");
			isNative = true;
		} catch (Exception e) {
			isNative = false;
			System.out.println("Library '" + "sysproc" + "' not found");
		}
	}
public SystemProcess()
{
}
public int execute(String s) throws Exception {
	if (isNative)
		return executeNative(s);
	else {
		Process p = Runtime.getRuntime().exec(s);
		return p.waitFor();
	}
}
public native int executeNative(String arg);
public static void main(String args[]) throws Exception {
    try {
        System.out.println("Started");
        SystemProcess p = new SystemProcess();
        int ret = args.length == 0 ? p.executeNative("ls") : p.executeNative(args[0]);
        System.out.println("finished with exit code=" + ret);
    } catch (Exception e) {
        System.out.println("Error: " + e + "\n\n");
        e.printStackTrace();
    }
}
}
