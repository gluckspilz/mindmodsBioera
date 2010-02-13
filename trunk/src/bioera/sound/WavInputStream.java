/* WavInputStream.java v 1.0.9   11/6/04 7:15 PM
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
public class WavInputStream extends FilterInputStream {

    private final static int MONO = 1;
    private final static int STEREO = 2;

    private final static int MONO8 = 1;
    private final static int STEREO8 = 2;
    private final static int MONO16 = 3;
    private final static int STEREO16 = 4;

    public int rate;
    public int format;
    public int length;
    public int channelNumbers;
    public int sampleRate;
    public int bytesPerSecond;
    public int bytesPerSample;
    public int bitsPerSample;
public WavInputStream(InputStream in) throws IOException {
	super(new PosInputStream(in));

	readRiff();
	readFormat();
	readFact();
}
private void readFact() throws IOException {
    PosInputStream in = (PosInputStream) this.in;
    validate("fact", in.readStr(4));
    int i = in.read4(); //	length of fact piece
    if (i != 4)
    	throw new IOException("Fact != 4: " + i);
    length = in.read4();
}
private void readFormat() throws IOException {
    PosInputStream in = (PosInputStream) this.in;
    validate("fmt ", in.readStr(4));
    int formatFrameLength = in.read4();
    format = in.read2(); // PCM=1
    if (format != -1)
        throw new IOException("Format " + format + " not supported, only PCM=1");
    channelNumbers = in.read2(); //	MONO - 1, STEREO - 2
    sampleRate = in.read4(); //	sampleRate [hz]
    bytesPerSecond = in.read4();
    bytesPerSample = in.read2(); // bytesPerSecond / sampleRate;
    if (bytesPerSample * rate != bytesPerSecond)
        throw new IOException("bytesPerSample"+ bytesPerSample+ ", rate="+ rate+ " ,bytesPerSecond="+ bytesPerSecond);

    //	Bytes Per Sample: 1=8 bit Mono, 2=8 bit Stereo or 16 bit Mono, 4=16 bit Stereo
    bitsPerSample = in.read2(); //	bits/sample

    // Read remaining bytes in the frame
    for (int i = 0; i < formatFrameLength - 16; i++) {
        in.read();
    }
}
private void readRiff() throws IOException {
    PosInputStream in = (PosInputStream) this.in;
    validate("RIFF", in.readStr(4));
    length  = in.read4() - 50;
    validate("WAVE", in.readStr(4));
}
private void validate(String shouldBe, String is) throws IOException {
	if (!shouldBe.equals(is)) {
		throw new IOException("Wave format '" + is + "' not recognized, expected " + shouldBe);
	}
}
}
