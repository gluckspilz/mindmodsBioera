/* WavOutputStream.java v 1.0.9   11/6/04 7:15 PM
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

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class WavOutputStream extends FilterOutputStream {

  private final static int MONO	=	1;
  private final static int STEREO=	2;

  private final static int MONO8=	1;
  private final static int STEREO8=	2;
  private final static int MONO16=	3;
  private final static int STEREO16=4;

  private int rate;
 
  private BufferedOutputStream buffer;   // Buffer data bytes here. We need to know the size of the data chunk.
  private File tempFile;
public WavOutputStream(OutputStream o, int irate) throws IOException {
	super(new PosOutputStream(o));
    rate = irate;

    // Stream data will be written to file buffer until closed
    tempFile = File.createTempFile("tmp", ".bioera");
    buffer = new BufferedOutputStream(new FileOutputStream(tempFile));
}
public void close() throws IOException { // save file
	buffer.close();

	int dataLength = (int) tempFile.length();
	if (dataLength > 0) {
	    writeRiff(dataLength);
		writeFormat(MONO, rate, STEREO8, 16);  // PCM
		writeData();
	}

	tempFile.delete();
    super.close();
}
public void write(int b) throws IOException {
    buffer.write(b); // buffer data.
}
public void write2(int b) throws IOException {
    buffer.write(b);
    buffer.write(b >> 8);
}
private void writeData() throws IOException {
    PosOutputStream out = (PosOutputStream) this.out;
    writeFact((int)tempFile.length());
    out.write("data".getBytes());
    out.write4((int)tempFile.length());
    BufferedInputStream in = null;
    try {
	    in = new BufferedInputStream(new FileInputStream(tempFile));
	    int c;
	    while ((c = in.read()) != -1) {
		    out.write(c);
	    }        
    } finally {
	    if (in != null)
	    	in.close();
    }
}
private void writeFact(int size) throws IOException {
    PosOutputStream out = (PosOutputStream) this.out;
    out.write("fact".getBytes());
    out.write4(4); //	length of fact chunk
    out.write4(size); // file size
}
private void writeFormat(int channelNumbers, int sampleRate, int bytesPerSample, int bitsPerSample) throws IOException {
    PosOutputStream out = (PosOutputStream) this.out;
    out.write("fmt ".getBytes());
    out.write4(0x12); //	length of the format frame
    out.write2(1); //	PCM=1
    out.write2(channelNumbers); //	MONO - 1, STEREO - 2
    out.write4(sampleRate); //	sampleRate [hz]
    out.write4(sampleRate * bytesPerSample); // bytes/second
    out.write2(bytesPerSample);
    //	Bytes Per Sample: 1=8 bit Mono, 2=8 bit Stereo or 16 bit Mono, 4=16 bit Stereo
    out.write2(bitsPerSample); //	bits/sample
    out.write2(0);
}
private void writeRiff(int length) throws IOException {
    PosOutputStream out = (PosOutputStream) this.out;
    out.write("RIFF".getBytes());
    out.write4(length + 50);
    out.write("WAVE".getBytes());
}
}
