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

package bioera.graph.runtime;

import java.awt.*;
import bioera.processing.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class DBCanvas extends Canvas {
	protected int compWidth;
	protected int compHeight;
	protected Image initImage;
	protected Graphics initGraphics;
	protected Image workingImage;
	protected Graphics gr;
/**
 * ChartElement constructor comment.
 */
public DBCanvas(int w, int h) {
	super();
	compWidth = w;
	compHeight = h;
	setSize(w, h);
}
/**
 * Copy init image to working image
 */
public void copyInitImage() {
	if (gr != null && initImage != null)
		gr.drawImage(initImage, 0, 0, this);
}
/**
 * LinesPanel constructor comment.
 */
protected synchronized void createInitImage() {
}
/**
 * ChartElement constructor comment.
 */
public final void paint(Graphics g){
	Rectangle r = g.getClipBounds();
	if (r.width < 5 || r.height < 5)
		return;
	if(compWidth != r.width || compHeight != r.height || workingImage == null || g == null) {
		compWidth = r.width;
		compHeight = r.height;
		resetBuffers();
	}
	//System.out.println("painting on main");
	g.drawImage(workingImage, 0, 0, this);
}
/**
 * ChartElement constructor comment.
 */
private void resetBuffers() {
    //clean up the previous image
    if (gr != null) {
        gr.dispose();
        gr = null;
    }
    if (workingImage != null) {
        workingImage.flush();
        workingImage = null;
    }

    //create the new image with the size of the panel
    workingImage = createImage(compWidth, compHeight);
    gr = workingImage.getGraphics();

    if (initGraphics != null) {
        initGraphics.dispose();
        initGraphics = null;
    }
    if (initImage != null) {
        initImage.flush();
        initImage = null;
    }

    //create the new image with the size of the panel
    initImage = createImage(compWidth, compHeight);
    initGraphics = initImage.getGraphics();

    // Clear init image
    initGraphics.clearRect(0, 0, compWidth, compHeight);

    // Paint init image
    createInitImage();

    // Copy init image to working image
    copyInitImage();
}
/**
 * ChartElement constructor comment.
 */
public void update(Graphics g){
	paint(g);
}
}
