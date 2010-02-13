/* PureChart.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.graph.chart;

import java.awt.*;
import bioera.processing.*;
import bioera.*;
import java.lang.reflect.*;
import bioera.config.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class PureChart extends Chart {
	protected PureCanvas canvas;
	Image initImage;
	protected Graphics initGraphics;
	protected Graphics gr, gr2;

/**
 * ChartElement constructor comment.
 */
public PureChart() {
	super();

	super.component = canvas = new PureCanvas(this);

	// Set default sizes
	canvas.setBounds(10, 10, 90, 90);
}
/**
 * Copy init image to working image
 */
public final void copyInitImage() {
	if (gr != null && initImage != null)
		gr.drawImage(initImage, 0, 0, canvas);
}


/**
 * LinesPanel constructor comment.
 */
protected void createInitImage() {
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 11:03:24 PM)
 * @param data int[]
 * @param from int
 * @param length int
 */
public final void drawOnImage(int length) {}
/**
 * ChartElement constructor comment.
 */
protected void initWorkingImage(){
}
/**
 * ChartElement constructor comment.
 */
public final void repaint() {
	canvas.repaint();
}
/**
 * ChartElement constructor comment.
 */
public final void isChartVisible(boolean vis){
    if (vis == false){
        canvas.setVisible(false);
        canvas.repaint();
    }
    else{
        canvas.setVisible(true);
        canvas.repaint();
    }

}

public final void resetBuffers() {
    //clean up the previous image
    if (gr != null) {
        gr.dispose();
        gr = null;
    }
    if (canvas.dynamicImage != null) {
        canvas.dynamicImage.flush();
        canvas.dynamicImage = null;
    }

    //create the new image with the size of the panel
    canvas.dynamicImage = canvas.createImage(compWidth, compHeight);
    gr = canvas.dynamicImage.getGraphics();

    if (initGraphics != null) {
        initGraphics.dispose();
        initGraphics = null;
    }
    if (initImage != null) {
        initImage.flush();
        initImage = null;
    }

    //create the new image with the size of the panel
    initImage = canvas.createImage(compWidth, compHeight);
    initGraphics = initImage.getGraphics();

    // Clear init image
    initGraphics.clearRect(0, 0, compWidth, compHeight);

    // Paint init image
    createInitImage();

    // Copy init image to working image
    copyInitImage();

    // OK
    initialized = canvas.initialized = true;

    if (element instanceof DependentOnRepaint)
    	((DependentOnRepaint)element).chartRepainted();

    // Init working image
    initWorkingImage();
}
/**
 * ChartElement constructor comment.
 */
public void resetChart() {
	initialized = canvas.initialized = false;
}
/**
 * setElement method comment.
 */
public void setElement(bioera.processing.Element e) {
	element = e;
}
/**
 * setParentDialog method comment.
 */
public void setParentDialog(ChartDialog d) {
	parentDialog = d;
}

/**
 * ChartElement constructor comment.
 */
protected void repopulate() {
}
}
