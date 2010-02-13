/* NativePCMTest.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.nativeobjects.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class NativePCMTest
{
    public NativePCMTest()
    {
    }
    public static void main(String args[])
        throws Exception
    {
        try
        {
	        System.out.println("started");
            NativePCMTest t = new NativePCMTest();
            t.startRead();
            System.out.println("finished");
        }
        catch(Throwable e)
        {
            System.out.println("Error: " + e + "\n\n");
            e.printStackTrace();
        }
    }

public void startRead() throws Exception {
	File file = new File("/dev/sound/dsp");
	System.out.println("File exists: " + file.exists());
	FileAccess dsp = new FileAccess();
	System.out.println("Passed file name '" + file.getAbsolutePath() + "'");
	int handle = dsp.open(file.getAbsolutePath(), dsp.F_READONLY);
	if (handle < 0)
		throw new Exception("Couldn't open file: " + handle);

	// Set device specific parameters
	SoundIOCTL ioctl = new SoundIOCTL(dsp, handle);
	ioctl.dspReset();
	ioctl.setReadBits(16);
	ioctl.setReadChannels(1);
	ioctl.setReadRate(8000);
//	ioctl.initWriteBlockSize();
//	int blockSize = ioctl.getWriteBlockSize();
	ioctl.dspSync();

	byte buf[] = new byte[100];

	System.out.println("reading");
	
	int ret = dsp.read(buf, 0, buf.length);

	System.out.println("ret=" + ret);
	
	dsp.close();
}

public void startWrite() throws Exception {
	File file = new File("/dev/sound/dsp");
	System.out.println("File exists: " + file.exists());
	FileAccess dsp = new FileAccess();
	System.out.println("Passed file name '" + file.getAbsolutePath() + "'");
	int handle = dsp.open(file.getAbsolutePath(), dsp.F_WRITEONLY);
	if (handle < 0)
		throw new Exception("Couldn't open file: " + handle);

	// Set device specific parameters
	SoundIOCTL ioctl = new SoundIOCTL(dsp, handle);
	ioctl.dspReset();
	ioctl.setWriteBits(16);
	ioctl.setWriteChannels(1);
	ioctl.setWriteRate(8000);
	ioctl.initWriteBlockSize();
	int blockSize = ioctl.getWriteBlockSize();
	ioctl.dspSync();

	byte buf[] = new byte[blockSize];
	
	FileInputStream in = new FileInputStream("b2.wav");
	
	// Read header
	in.read(buf, 0, 44);

	int i;
	while ((i = in.read(buf)) != -1) {
		dsp.write(buf, 0, i);
	}
	
	dsp.close();
}
}
