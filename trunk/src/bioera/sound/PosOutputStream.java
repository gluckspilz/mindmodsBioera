/* PosOutputStream.java v 1.0.9   11/6/04 7:15 PM
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
import java.net.URL;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class PosOutputStream extends BufferedOutputStream{

  public PosOutputStream(OutputStream out) throws IOException{
    super(out);
  }
public void write2(int i) throws IOException {
    write(i & 0x000000FF);
    write((i >> 8) & 0x000000FF);
}
  public void write4(int i) throws IOException{
    write(i&0x000000FF);
    write((i>>8)&0x000000FF);
    write((i>>16)&0x000000FF);
    write((i>>24)&0x000000FF);
  }
  public void write8(long i) throws IOException{
    write((int)i&0x000000FF);
    write((int)(i>>8)&0x000000FF);
    write((int)(i>>16)&0x000000FF);
    write((int)(i>>24)&0x000000FF);

    write((int)(i>>32)&0x000000FF);
    write((int)(i>>40)&0x000000FF);
    write((int)(i>>48)&0x000000FF);
    write((int)(i>>56)&0x000000FF);
  }
}
