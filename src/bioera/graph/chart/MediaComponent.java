/* MediaComponent.java v 1.0.9   11/6/04 7:15 PM
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

import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.*;

/**
 * Creation date: (8/11/2004 1:31:49 PM)
 * @author: Jarek Foltynski
 */
public class MediaComponent extends Panel implements ChartComponent {
	Chart chart;
/**
 * Fast3DCanvas constructor comment.
 */
public MediaComponent(MediaChart ichart) {
	super();
	chart = ichart;
}
/**
 * Insert the method's description here.
 * Creation date: (8/11/2004 1:35:05 PM)
 */
public Chart getChart() {
	return chart;
}
}
