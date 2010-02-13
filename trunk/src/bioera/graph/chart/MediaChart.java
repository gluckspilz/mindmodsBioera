/* MediaChart.java v 1.0.9   11/6/04 7:15 PM
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
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (10/24/2004 9:31:24 PM)
 * @author: Jarek
 */
public class MediaChart extends Chart {
	public Panel container;
    // component in which video is playing
    public Component visualComponent = null;
    // controls gain, position, start, stop
    public Component controlComponent = null;
    // displays progress during download
    public Component progressBar = null;
//    public boolean firstTime = true;
//    public long CachingSize = 0L;
//    public Panel panel = null;
//    public int controlPanelHeight = 0;
/**
 * VideoChart constructor comment.
 */
public MediaChart() {
	super();

	container = new MediaComponent(this);
	container.setLayout(new BorderLayout());

	component = container;
}
/**
 * ChartElement constructor comment.
 */
public void close() {
	container.removeAll();
	visualComponent = null;
	controlComponent = null;
	progressBar = null;
	container.validate();
}
/**
 * ChartElement constructor comment.
 */
public void initialize(int width, int height, java.awt.Color bg) {
	initialized = true;
}
/**
 * ChartElement constructor comment.
 */
public boolean isInitialized() {
	return true;
}
/**
 * ChartElement constructor comment.
 */
public void resetChart() {
}
}
