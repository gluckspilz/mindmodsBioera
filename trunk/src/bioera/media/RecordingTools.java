/* RecordingTools.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.media;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.protocol.*;

import java.awt.Dimension;

public class RecordingTools {

	public static Format formatMatches(Format format, Format supported[]) {
		if (supported == null)
			return null;
		for (int i = 0; i < supported.length; i++)
			if (supported[i].matches(format))
				return supported[i];
		return null;
	}

	public static boolean setFormat(DataSource dataSource, Format format) {
		boolean formatApplied = false;

		FormatControl formatControls[] = null;
		formatControls = ((CaptureDevice) dataSource).getFormatControls();
		for (int x = 0; x < formatControls.length; x++) {
			if (formatControls[x] == null)
				continue;

			Format supportedFormats[] = formatControls[x].getSupportedFormats();
			if (supportedFormats == null)
				continue;

			if (formatMatches(format, supportedFormats) != null) {
				formatControls[x].setFormat(format);
				formatApplied = true;
			}
		}

		return formatApplied;
	}

	public static boolean isVideo(Format format) {
		return (format instanceof VideoFormat);
	}

	public static boolean isAudio(Format format) {
		return (format instanceof AudioFormat);
	}

	public static String formatToString(Format format) {
		if (isVideo(format))
			return videoFormatToString((VideoFormat) format);

		if (isAudio(format))
			return audioFormatToString((AudioFormat) format);

		return ("--- unknown media device format ---");
	}

	public static String videoFormatToString(VideoFormat videoFormat) {
		StringBuffer result = new StringBuffer();

		// add width x height (size)
		Dimension d = videoFormat.getSize();
		result.append(
			"size=" + (int) d.getWidth() + "x" + (int) d.getHeight() + ", ");

		/*
		// try to add color depth
		if (videoFormat instanceof IndexedColorFormat)
		{
			IndexedColorFormat f = (IndexedColorFormat) videoFormat;
			result.append("color depth=" + f.getMapSize() + ", ");
		}
		*/

		// add encoding
		result.append("encoding=" + videoFormat.getEncoding() + ", ");

		// add max data length
		result.append("maxdatalength=" + videoFormat.getMaxDataLength() + "");

		return result.toString();
	}

	public static String audioFormatToString(AudioFormat audioFormat) {
		StringBuffer result = new StringBuffer();

		// short workaround
		result.append(audioFormat.toString().toLowerCase());

		return result.toString();
	}
}
