/* TestSound.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.sound;

import java.io.*;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class TestSound
{
	private static final int	EXTERNAL_BUFFER_SIZE = 128000;



public static void main(String[] args) {
	try {
		//String s = "/usr/bin/wavp " + args[0];
		String s = "/tools/wav.exe " + args[0];
		System.out.println("executing '" + s + "'");
		Process p = Runtime.getRuntime().exec(s);
		//InputStream in = p.getInputStream();
		//InputStream err = p.getErrorStream();
		//while (in.read() != -1)
			//;
		p.waitFor();
		System.exit(0);
	} catch (Exception e) {
		e.printStackTrace();
	}
}
public static void previous(String[] args) {
    //if (args.length != 1) {
        //System.out.println("SimpleAudioPlayer: usage:");
        //System.out.println("\tjava SimpleAudioPlayer <soundfile>");
        //System.exit(1);
    //}

    String strFilename = "C:\\programs\\ssh\\ding8.wav";
    //args[0];
    
    File soundFile = new File(strFilename);

    AudioInputStream audioInputStream = null;
    try {
//        audioInputStream = new WaveFileReader().getAudioInputStream(soundFile);
System.out.println("Done Reading from wave reader");	    
    } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
    }

    AudioFormat audioFormat = audioInputStream.getFormat();
System.out.println("Audio format: " + audioFormat + "  class: " + audioFormat.getClass());


int nBytesRead = 0;
int count;

byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
        try {
			//OutputStream out = new FileOutputStream("/dev/sound/dsp");
			OutputStream out = new FileOutputStream("/programs/ssh/testout");
			while ((count = audioInputStream.read(abData, 0, abData.length)) > 0) {
				nBytesRead += count;
				//int k;
				//for (int i = 0; i < count; i++){
					//k = abData[i];
					//k = k + 128;
					//abData[i] = (byte) k;
				//}
				out.write(abData, 0, count);
			}

			while (nBytesRead % 1024 != 0) {
				nBytesRead++;
				out.write((byte) 127);
			}
			out.flush();
			out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

System.out.println("read bytes: " + nBytesRead);

/*
    SourceDataLine line = null;
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
    try {
        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(audioFormat);
    } catch (LineUnavailableException e) {
        e.printStackTrace();
        System.exit(1);
    } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
    }

    line.start();

    int nBytesRead = 0;
    byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
    while (nBytesRead != -1) {
        try {
            nBytesRead = audioInputStream.read(abData, 0, abData.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (nBytesRead >= 0) {
            int nBytesWritten = line.write(abData, 0, nBytesRead);
        }
    }

    line.drain();
    line.close();
*/    
    System.exit(0);
}
}
