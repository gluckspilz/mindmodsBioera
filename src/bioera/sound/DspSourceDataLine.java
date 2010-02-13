/* DspSourceDataLine.java v 1.0.9   11/6/04 7:15 PM
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

import javax.sound.sampled.*;

/**
 * Insert the type's description here.
 * Creation date: (7/12/2004 7:31:29 PM)
 * @author: Jarek
 */
public abstract class DspSourceDataLine implements SourceDataLine {
	bioera.nativeobjects.SoundIOCTL soundDev;
/**
 * DspSourceDataLine constructor comment.
 */
public DspSourceDataLine(bioera.nativeobjects.SoundIOCTL sdev) {
	super();

	soundDev = sdev;
}
/**
 * addLineListener method comment.
 */
public void addLineListener(javax.sound.sampled.LineListener arg1) {}
/**
 * available method comment.
 */
public int available() {
	return soundDev.writeAvailable();
}
/**
 * close method comment.
 */
public void close() {
	soundDev.fa.close();
}
/**
 * drain method comment.
 */
public void drain() {}
/**
 * flush method comment.
 */
public void flush() {}
/**
 * getBufferSize method comment.
 */
public int getBufferSize() {
	return soundDev.getWriteBufferSize();
}
/**
 * getControl method comment.
 */
public javax.sound.sampled.Control getControl(javax.sound.sampled.Control.Type arg1) {
	return null;
}
/**
 * getControls method comment.
 */
public javax.sound.sampled.Control[] getControls() {
	return null;
}
/**
 * getFormat method comment.
 */
public javax.sound.sampled.AudioFormat getFormat() {
	return new AudioFormat(
		soundDev.getWriteRate(),
		soundDev.getWriteBits(),
		soundDev.getWriteChannels(),	// number of channels
		true,	// signed
		false);	// big endian
}
/**
 * getFramePosition method comment.
 */
public int getFramePosition() {
	return 0;
}
/**
 * getLevel method comment.
 */
public float getLevel() {
	return 0;
}
/**
 * getLineInfo method comment.
 */
public javax.sound.sampled.Line.Info getLineInfo() {
	return null;
}
/**
 * getMicrosecondPosition method comment.
 */
public long getMicrosecondPosition() {
	return 0;
}
/**
 * isActive method comment.
 */
public boolean isActive() {
	return true;
}
/**
 * isControlSupported method comment.
 */
public boolean isControlSupported(javax.sound.sampled.Control.Type arg1) {
	return false;
}
/**
 * isOpen method comment.
 */
public boolean isOpen() {
	return true;
}
/**
 * isRunning method comment.
 */
public boolean isRunning() {
	return true;
}
/**
 * open method comment.
 */
public void open() throws javax.sound.sampled.LineUnavailableException {

}
/**
 * open method comment.
 */
public void open(javax.sound.sampled.AudioFormat arg1) throws javax.sound.sampled.LineUnavailableException {}
/**
 * open method comment.
 */
public void open(javax.sound.sampled.AudioFormat arg1, int arg2) throws javax.sound.sampled.LineUnavailableException {}
/**
 * removeLineListener method comment.
 */
public void removeLineListener(javax.sound.sampled.LineListener arg1) {}
/**
 * start method comment.
 */
public void start() {}
/**
 * stop method comment.
 */
public void stop() {}
/**
 * write method comment.
 */
public int write(byte[] arg1, int offs, int count) {
	return soundDev.fa.write(arg1, offs, count);
}
}
