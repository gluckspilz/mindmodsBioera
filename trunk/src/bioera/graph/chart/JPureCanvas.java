/* JPureCanvas.java v 1.0.9   11/6/04 7:15 PM
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
import javax.swing.*;
import java.awt.*;
import bioera.processing.*;
import bioera.*;
import java.lang.reflect.*;
import bioera.config.*;

/**
 * Creation date: (8/11/2004 12:36:01 PM)
 * @author: Jarek Foltynski
 */
public class JPureCanvas extends JPanel implements JChartComponent {
	JPureChart chart;
	Image dynamicImage;
	boolean initialized;
/**
 * PureCanvas constructor comment.
 */
public JPureCanvas(JPureChart ichart) {
	super();

	chart = ichart;
}
/**
 * Insert the method's description here.
 * Creation date: (8/11/2004 12:40:15 PM)
 */
public JChart getChart() {
	return chart;
}
/**
 * ChartElement constructor comment.
 */
public final void paint(Graphics g){
	int cw = getWidth();
	int ch = getHeight();

	if(!initialized || chart.compWidth != cw || chart.compHeight != ch || dynamicImage == null || g == null) {
		//System.out.println("chart reinitialized " + reinitializedCounter++);
		//if (!initialized)
			//System.out.println("deinitialized");
		//if (g == null)
			//System.out.println("g == null");
		//if (workingImage == null)
			//System.out.println("workingImage == null");
		//if (compWidth != cw)
			//System.out.println("compWidth=" + compWidth + "  r.width= " + cw);

		initialized = false;
		chart.compWidth = cw;
		chart.compHeight = ch;
		chart.repopulate();
		chart.resetBuffers();
	}

	g.drawImage(dynamicImage, 0, 0, this);
}
/**
 * ChartElement constructor comment.
 */
public final void update(Graphics g){
	paint(g);
}
}
