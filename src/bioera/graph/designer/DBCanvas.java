/* DBCanvas.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.graph.designer;

import java.awt.*;
import javax.swing.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class DBCanvas extends JPanel {
	protected int width;
	protected int height;
	private Image bufferImage;
	private Graphics bufferGraphics;

	private boolean redraw = false;
/**
 * ChartElement constructor comment.
 */
public DBCanvas() {
	super();
}
/**
 * ChartElement constructor comment.
 */
public void paint(Graphics g){
	if (redraw) {
		g.drawImage(bufferImage,0,0,this);
		return;
	}
	Rectangle r = g.getClipBounds();
	if(width!=r.width || height!=r.height || bufferImage==null || bufferGraphics==null)
		resetBuffer(r);
		
	if(bufferGraphics!=null){
		//this clears the offscreen image, not the onscreen one
		bufferGraphics.clearRect(0,0,width,height);

		//calls the paintbuffer method with the offscreen graphics as a param
		paintBuffer(bufferGraphics);

		//we finaly paint the offscreen image onto the onscreen image
		g.drawImage(bufferImage,0,0,this);
	}
}
/**
 * ChartElement constructor comment.
 */
public void paintBuffer(Graphics g) {
	super.paint(g);
}
/**
 * LinesPanel constructor comment.
 */
public void redraw() {
	if (!redraw)
		redraw = true;
}
/**
 * LinesPanel constructor comment.
 */
public void repaint() {
	if (redraw)
		redraw = false;
	super.repaint();
}
/**
 * ChartElement constructor comment.
 */
private void resetBuffer(Rectangle r) {
    // always keep track of the image size
    width = r.width;
    height = r.height;
    //clean up the previous image
    if (bufferGraphics != null) {
        bufferGraphics.dispose();
        bufferGraphics = null;
    }
    if (bufferImage != null) {
        bufferImage.flush();
        bufferImage = null;
    }

    //create the new image with the size of the panel
    bufferImage = createImage(width, height);
    bufferGraphics = bufferImage.getGraphics();

}
/**
 * ChartElement constructor comment.
 */
public void update(Graphics g){
	paint(g);
}
}
