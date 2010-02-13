/* JChartDialog.java v 1.0.9   11/6/04 7:15 PM
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
import java.awt.event.*;
import java.util.*;
import bioera.*;
import bioera.graph.designer.*;
import bioera.processing.impl.*;
import bioera.processing.*;
import bioera.graph.runtime.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class JChartDialog extends JDialog {
	public JChart chart;
	public int relativeX, relativeY;  // relatively to panel
	public RuntimeFrame parentFrame;
/**
 * ChartDialog constructor comment.
 */
public JChartDialog(RuntimeFrame f, JChart c) {
	super(f.getFrame(), c.getChartName());
	parentFrame = f;
	chart = c;
	c.setParentDialog(this);

	setBackground(f.getFrame().getBackground());
	setLayout(new BorderLayout());
	setBounds(c.getComponent().getBounds());
	add(c.getComponent(), BorderLayout.CENTER);

	JChartEventHandler e = new JChartEventHandler(this);
	addComponentListener(e);
	addWindowListener(e);
	addFocusListener(e);
}
/**
 * ChartDialog constructor comment.
 */
public void dispose() {
	parentFrame = null;
	chart.setParentDialog(null);
	chart = null;
	super.dispose();
}
/**
 * ChartDialog constructor comment.
 */
//public void show() {
public void setVisible() {
	setLocation(parentFrame.getFrame().getX() + parentFrame.panel.getX() + relativeX,
		parentFrame.getFrame().getY() + parentFrame.panel.getY() + relativeY);

	//super.show();
        super.setVisible(true);
}
/**
 * ChartDialog constructor comment.
 */
public void updateRelativeCoordinates() {
	relativeX = getX() - parentFrame.getFrame().getX() - parentFrame.panel.getX();
	relativeY = getY() - parentFrame.getFrame().getY() - parentFrame.panel.getY();
}
}
