/* OrbitalDisplay.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.processing.impl;

import java.io.*;
import java.util.*;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.graph.designer.*;
import bioera.fft.*;
import bioera.graph.chart.*;
import bioera.properties.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class OrbitalDisplay extends Display {
	public ComboProperty planetSize = new ComboProperty(new String[] {
		"SMALL",
		"MEDIUM",
		"LARGE",
		});

	public FloatArrayCompoundScale thresholds;
//	public int sensitivityThreshold[] = {3};
	public int velocity = 5;
	public boolean rotateRight = false;
	public boolean showOrbitTrajectory = true;
	public String icons[] = {"venus.gif", "earth.gif", "mars.gif"};

	private final static String propertiesDescriptions[][] = {
		{"planetSize", "Planet size", ""},
		{"velocity", "Velocity (1=max, 2=slower, ...)", ""},
		{"thresholds", "Threshold array [uV]", ""},
		{"rotateRight", "Right rotation", ""},
		{"showOrbitTrajectory", "Show trajectory", ""},		
		{"icons", "Planet icons", ""},		
	};

	public static final int SIZE_SMALL = 0;
	public static final int SIZE_MEDIUM = 1;
	public static final int SIZE_LARGE = 2;
	
	private int planetSizeSelection = SIZE_MEDIUM;
		
	private int b[][], vSize, sT[], mP, mD;
	private BufferedVectorPipe in;
	private OrbitalChart chart;
/**
 * Element constructor comment.
 */
public Chart createChart() {
	return chart = new OrbitalChart();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "A number is shown on graphic chart";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 1;
}
/**
 * Element constructor comment.
 */
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
/**
 * Element constructor comment.
 */
public final void process() throws Exception {
	chart.rp();
	chart.repaint();
	int n = in.available();
	if (n == 0) {		
		return;
	}

	for (int i = 0; i < n; i++){
		for (int k = 0; k < vSize; k++){
			b[i][k] -= sT[k];
			//System.out.println("b[i]=" + b[i][k]);
		}
		chart.pushVector(b[i]);
	}

	in.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	if (planetSize.getSelectedIndex() == -1)
		planetSize.setSelectedIndex(planetSizeSelection);
	else
		planetSizeSelection = planetSize.getSelectedIndex();

	if (((SmallVerticalScale)thresholds.scale).getSelectedIndex() == -1) {
		((SmallVerticalScale)thresholds.scale).setSelectedItem("uV");
	}
			
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	PipeDistributor pd = getFirstDistributorConnectedToInput(0);
	if (pd == null || !(pd instanceof VectorPipe)) {
		throw new Exception("Element not connected properly");
	}

	VectorPipe vp = (VectorPipe) pd;
	vSize = vp.getSignalParameters().getVectorLength();
	
	if (debug)
		System.out.println("OrbitDisplay: elem="+ predecessorElement.getName() + "  rate=" + predecessorElement.getSignalParameters().getSignalRate());

	if (vSize > icons.length)
		throw new DesignException("To few icon images provided " + vSize + ":" + icons.length);
			

	int iconSize = planetSizeSelection == SIZE_SMALL ? 16 : planetSizeSelection == SIZE_LARGE ? 64 : 32;
	java.awt.Image ims[] = new java.awt.Image[vSize];
	for (int i = 0; i < vSize; i++){
		File f = new File(bioera.Main.app.getImagesFolder(), iconSize + "_" + icons[i]);
		if (!f.exists())
			throw new DesignException("Image '" + f.getName() + "' not found");
		ims[i] = java.awt.Toolkit.getDefaultToolkit().getImage(f.getPath());
	}

	File f = new File(bioera.Main.app.getImagesFolder(), "candle_light.gif");
	if (f.exists())
		chart.setCenterImage(java.awt.Toolkit.getDefaultToolkit().getImage(f.getPath()));

	chart.setRotateRight(rotateRight);
	chart.setImages(ims);
	chart.showOrbits = showOrbitTrajectory;
	chart.setVelocity(velocity);

	if (thresholds.value.length < icons.length) {
		if (thresholds.value.length < 1)
			throw new bioera.graph.designer.DesignException("Thresholds not set");
		double namp[] = new double[icons.length];
		System.arraycopy(thresholds.value, 0, namp, 0, thresholds.value.length);
		for (int i = thresholds.value.length; i < namp.length; i++){
			namp[i] = namp[i-1];
		}
		thresholds.value = namp;
	}

	thresholds.scale.update(this);
	sT = new int[thresholds.value.length];
	for (int i = 0; i < sT.length; i++){
		sT[i] = thresholds.scale.toDigit(thresholds.value[i]);
		if (debug)
			System.out.println("Orbital threshold=" + sT[i]);
	}
	
	super.reinit();
}

/**
 * VectorDisplay constructor comment.
 */
public OrbitalDisplay() {
	super();
	setName("Orbit");
	inputs = new BufferedVectorPipe[1];
	inputs[0] = in = new BufferedVectorPipe(this);
	in.setName("IN");		
	b = in.getVBuffer();
	thresholds = new FloatArrayCompoundScale(new int[]{0}, new SmallVerticalScale(this));
}
}
