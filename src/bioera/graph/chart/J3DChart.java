/* J3DChart.java v 1.0.9   11/6/04 7:15 PM
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
import com.sun.j3d.utils.universe.*;

/**
 * Creation date: (8/11/2004 9:20:02 AM)
 * @author: Jarek Foltynski
 */
public class J3DChart extends Chart {
    public float coord[];
    public byte colors[];

    Canvas3D canvas;
    SimpleUniverse u;
    BranchGroup scene;
    TransformGroup transGroup;
    TriangleFanArray fanArray;

	public static final GeometryUpdater gup = new GeometryUpdater() {
		public void updateData(Geometry geometry) {
			// do nothing, the coordinates will be uploaded directly in the array
		}
	};
/**
 * Fast3DChart constructor comment.
 */
public J3DChart() {
	super();

	canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());

	BorderedComponent c = new BorderedComponent(this);
	c.center = canvas;
	c.lay();

	component = c;
}
public void createScene(java.awt.Color bg) {
	scene = new BranchGroup();

	coord = new float[] {
		0f, 0f, 0f,
		0.5f, 0f, 0f,
		0.6f, 0.5f, 0f,
		0.1f, 0.4f, 0f,

		0f, 0f, 0f,
		-0.5f, -0.5f, 0f,
		0.0f, -0.5f, 0f,
		0.1f, -0.4f, 0f,

	};


	int vertexCounts[] = {4, 4};

	colors = new byte[] {
		(byte)255, 0, 0,
		0, (byte)255, 0,
		0, 0, (byte)255,
		(byte)255, (byte)255, (byte)255,

		(byte)255, 0, 0,
		0, (byte)255, 0,
		0, 0, (byte)255,
		(byte)255, (byte)255, (byte)255,
	};

	//System.out.println("coord=" + coord.length);
	//System.out.println("colors=" + colors.length);
	//System.out.println("vertexCounts=" + vertexCounts.length);


	fanArray = new TriangleFanArray(
		8,
		//GeometryArray.NORMALS |
		GeometryArray.COORDINATES
		| GeometryArray.COLOR_3
		| GeometryArray.BY_REFERENCE,
		vertexCounts);

	fanArray.setCoordRefFloat(coord);
	fanArray.setColorRefByte(colors);
	fanArray.setCapability(TriangleFanArray.ALLOW_REF_DATA_WRITE);

	Shape3D shape = new Shape3D();
	shape.setGeometry(fanArray);
	scene.addChild(shape);

	// Set background
	Background backg = new Background(new Color3f(bioera.Main.app.runtimeFrame.getFrame().getBackground()));
	backg.setApplicationBounds(new BoundingSphere());
	scene.addChild(backg);

	scene.compile();
}
/**
 * Fast3DChart constructor comment.
 */
public void initialize(int width, int height, java.awt.Color bg) {
	if (initialized) {
		u.removeAllLocales();
		u.cleanup();
	}

	createScene(bg);
	u = new SimpleUniverse(canvas);
    u.getViewingPlatform().setNominalViewingTransform();
	u.addBranchGraph(scene);
	initialized = true;
}
/**
 * Insert the method's description here.
 * Creation date: (8/11/2004 1:18:57 PM)
 * @return boolean
 */
public boolean isInitialized() {
	return true;
}
/**
 * ChartElement constructor comment.
 */
public void pushVector(int vect[]) {
	coord[0] -= 0.1f;
	if (coord[0] < -1.0f)
		coord[0] = 1f;
	fanArray.updateData(gup);
}
/**
 * ChartElement constructor comment.
 */
public void resetChart() {}
}
