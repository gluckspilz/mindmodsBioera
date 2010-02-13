/* Matrix3DChart.java v 1.0.9   11/6/04 7:15 PM
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
import com.sun.j3d.utils.geometry.*;
import java.awt.*;

/**
	This chart provides mechanisms to set value and color of each point in the matrix.
	The X-Y coordinates do not change, only value and color in the vertex.
	The TriangleFanArray of 9 points is used for performance.

 */
public class Matrix3DChart extends J3DChart {
	public int rotateX, rotateY, rotateZ;
	public float scale = 1.0f, scaleX = 1.0f, scaleY = 1.0f, scaleZ = 1.0f;
	public float shiftX = 0, shiftY = 0, shiftZ = 0;
	public Color floorColor = Color.white;
	int mWidth;
	int mHeight;
	int colorType = 3;
	int totalPointsNo;
	int totalFansPointsNo;
	int arrayPointsNo;
	final static int stripSize = 10;
	int fansNo;
	int fansXno;
	int fansYno;
 	int vertexCounts[]; 	

 	// Contains indexes of all strips that contains this points
 	final int sindv[] = new int[5];
 	int sind;
 	protected int si;

/**
 * Matrix3DChart constructor comment.
 */
public Matrix3DChart() {
	super();
}
/**
 * Matrix3DChart constructor comment.
 */
public void clear() {
	for (int i = 0; i < coord.length; i+=3){
		// Set all Z coordinates to 0
		coord[i+2] = 0f + shiftZ;
	}

	// Colors to floor color
	for (int i = 0; i < colors.length; i+=3){
		// Set all Z coordinates to 0
		colors[i] = (byte) floorColor.getRed();
		colors[i+1] = (byte) floorColor.getGreen();
		colors[i+2] = (byte) floorColor.getBlue();		
	}	
}
/**
 * Matrix3DChart constructor comment.
 */
public final void copyColor(int from, int to) {
	colors[to*3] = colors[from*3];
	colors[to*3+1] = colors[from*3+1];
	colors[to*3+2] = colors[from*3+2];
}
/**
 * Matrix3DChart constructor comment.
 */
public Background createBackground(Color color) {
	// Set background
	Background backg = new Background(new Color3f(color == null ? bioera.Main.app.runtimeFrame.getFrame().getBackground() : color));
	backg.setApplicationBounds(new BoundingSphere());
	return backg;
}
/**
 * Matrix3DChart constructor comment.
 */
public Shape3D createContentGraph() {
	fanArray = new TriangleFanArray(
		totalFansPointsNo, 
		GeometryArray.COORDINATES 
		| GeometryArray.COLOR_3 
		| GeometryArray.BY_REFERENCE,
		vertexCounts);

	fanArray.setCoordRefFloat(coord);
	fanArray.setColorRefByte(colors);
	fanArray.setCapability(TriangleFanArray.ALLOW_REF_DATA_WRITE);

	Shape3D shape = new Shape3D();
	shape.setGeometry(fanArray);

	return shape;
}
/**
 * Matrix3DChart constructor comment.
 */
public Node createDescription() {
	//BranchGroup objRoot = new BranchGroup();

//// Create a Text2D leaf node, add it to the scene graph.
	//Text2D text2D = new Text2D("text", new Color3f(1f, 0f, 0f), "Helvetica", 18, Font.ITALIC);
	//return text2D;

	
	Transform3D transf = new Transform3D();
	transf.setScale(0.1);

	TransformGroup container = new TransformGroup(transf);
		
	Font3D font3d = new Font3D(new Font("Helvetica", Font.PLAIN, 1), new FontExtrusion());
	Text3D textGeom = new Text3D(font3d, new String("3DText"), new Point3f(-1f, -10f, 0f));
	textGeom.setAlignment(Text3D.PATH_LEFT);
	container.addChild(new Shape3D(textGeom));

	return container;
}
/**
 * Matrix3DChart constructor comment.
 */
public void createScene(java.awt.Color color) {
	scene = new BranchGroup();

	Shape3D shape = createContentGraph();
	createTransformGroup();
	transGroup.addChild(shape);

	// Set background
	Background backg = createBackground(color);
	
	//testFields(scene);
	
	if (backg != null)	
		scene.addChild(backg);

	//Node node = createDescription();
	//if (node != null)
		//transGroup.addChild(node);
		
	scene.addChild(transGroup);		
	scene.compile();
}
/**
 * Matrix3DChart constructor comment.
 */
public void createTransformGroup() {
	transGroup = new TransformGroup();
	transGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

	Transform3D t3d = new Transform3D();
	if (rotateX != 0) {
		Transform3D t3dX = new Transform3D();
		t3dX.rotX(2*Math.PI*rotateX/360);
		t3d.mul(t3dX);
	}
	if (rotateY != 0) {
		Transform3D t3dY = new Transform3D();
		t3dY.rotY(2*Math.PI*rotateY/360);
		t3d.mul(t3dY);
	}
	if (rotateZ != 0) {
		Transform3D t3dZ = new Transform3D();
		t3dZ.rotZ(2*Math.PI*rotateZ/360);
		t3d.mul(t3dZ);
	}
	
	if (scale != 1.0) {
		t3d.setScale(scale);
	}	

	
	transGroup.setTransform(t3d);
}
/**
 * Returns index of the point in coord table (needs to multiply by 3 since there are 3 coordinates)
 */
public void drawLine(BranchGroup scene, float x, float y, float z, float x1, float y1, float z1) {
	LineArray line = new LineArray (2, LineArray.COORDINATES);
	line.setCoordinate(0, new Point3f(x, y, z));
	line.setCoordinate(1, new Point3f(x1,y1,z1));
	scene.addChild(new Shape3D(line));		
}
/**
 * Matrix3DChart constructor comment.
 */
public final float getZ(int k) {
	//if (debug)
		//System.out.println("Getting Z(" + k + ")=" + v);
	return coord[k*3+2];
}
/**
 * Returns index of the point in coord table (needs to multiply by 3 since there are 3 coordinates)
 */
public final void indexOf(int x, int y) {
	sind = 0;

	// Find the fan and get the index within it
	int sx, sy, isx, isy;
	sx = x / 2;
	sy = y / 2;
	isx = x % 2;
	isy = y % 2;
	//System.out.println("x=" + x + " y=" + y + " sx=" + sx + " sy=" + sy + " isx=" + isx + " isy=" + isy);	
	si = 0;
	if (isy == 0) {
		if (isx == 0)
			si = 8;
		else 
			si = 0;
	} else {
		if (isx == 0)
			si = 7;
		else 
			si = 6;		
	}
	
	//System.out.println("si(" + x + "," + y + ")=" + si);
	
	// Add the point inside this fan
	sindv[sind++] = sy * stripSize * fansXno + sx * stripSize + si;

	// Find points from neighbours
	if (si == 7) {
		// Right upper corner
		if (sy+1 < fansYno) {
			sindv[sind++] = (sy+1) * stripSize * fansXno + sx * stripSize + 1;
			sindv[sind++] = (sy+1) * stripSize * fansXno + sx * stripSize + 9;
		}
		
		if (sx > 0) {
			sindv[sind++] = sy * stripSize * fansXno + (sx-1) * stripSize + 5;
			
			if (sy+1 < fansYno) {
				sindv[sind++] = (sy+1) * stripSize * fansXno + (sx-1) * stripSize + 3;
			}
		}
	} else if (si == 6) {
		// Y
		if (sy+1 < fansYno) {
			sindv[sind++] = (sy+1) * stripSize * fansXno + sx * stripSize + 2;
		}		
	} else if (si == 8) {
		// X
		if (sx > 0) {
			sindv[sind++] = sy * stripSize * fansXno + (sx-1) * stripSize + 4;
		}
	}
}
/**
 * Returns index of the point in coord table (needs to multiply by 3 since there are 3 coordinates)
 */
public void indexOf1(int x, int y) {
	sind = 0;

	// Find the fan and get the index within it
	int sx = x / 2;
	int sy = y / 2;
	int isx = x % 2;
	int isy = y % 2;
	int si = 0;
	if (isy == 0) {
		if (isx == 0)
			si = 1;
		else 
			si = 2;
	} else {
		if (isx == 0)
			si = 8;
		else 
			si = 0;		
	}
	
	//System.out.println("si(" + x + "," + y + ")=" + si);
	
	// Add the point inside this fan
	sindv[sind++] = sy * stripSize * fansXno + sx * stripSize + si;

	// Find points from neighbours
	if (si == 1) {
		// Left upper corner

		// Add last index that is equal to fist index
		sindv[sind++] = sy * stripSize * fansXno + sx * stripSize + 9;
		
		if (sy > 0) {
			sindv[sind++] = (sy-1) * stripSize * fansXno + sx * stripSize + 7;
		}
		
		if (sx > 0) {
			sindv[sind++] = sy * stripSize * fansXno + (sx-1) * stripSize + 3;
			if (sy > 0) {
				sindv[sind++] = (sy-1) * stripSize * fansXno + (sx-1) * stripSize + 5;
			}
		}
	} else if (si == 2) {
		// Top
		if (sy > 0) {
			sindv[sind++] = (sy-1) * stripSize * fansXno + sx * stripSize + 6;
		}		
	} else if (si == 8) {
		// Left
		if (sx > 0) {
			sindv[sind++] = sy * stripSize * fansXno + (sx-1) * stripSize + 4;
		}
	}
}
/**
 * Returns index of the point in coord table (needs to multiply by 3 since there are 3 coordinates)
 */
public void indexOf2(int x, int y) {
	sind = 0;

	// Find the fan and get the index within it
	int sx = x / 2;
	int sy = y / 2;
	int isx = x % 2;
	int isy = y % 2;
	int si = 0;
	if (isy == 0) {
		if (isx == 0)
			si = 2;
		else 
			si = 3;
	} else {
		if (isx == 0)
			si = 0;
		else 
			si = 4;		
	}
	
	//System.out.println("si(" + x + "," + y + ")=" + si);
	
	// Add the point inside this fan
	sindv[sind++] = sy * stripSize * fansXno + sx * stripSize + si;

	// Find points from neighbours
	if (si == 3) {
		// Right upper corner
		if (sy > 0) {
			sindv[sind++] = (sy-1) * stripSize * fansXno + sx * stripSize + 5;
		}
		
		if (sx+1 < fansXno) {
			sindv[sind++] = sy * stripSize * fansXno + (sx+1) * stripSize + 1;
			sindv[sind++] = sy * stripSize * fansXno + (sx+1) * stripSize + 9;
			if (sy > 0) {
				sindv[sind++] = (sy-1) * stripSize * fansXno + (sx+1) * stripSize + 7;
			}
		}
	} else if (si == 2) {
		// Top
		if (sy > 0) {
			sindv[sind++] = (sy-1) * stripSize * fansXno + sx * stripSize + 6;
		}		
	} else if (si == 4) {
		// Right
		if (sx+1 < fansXno) {
			sindv[sind++] = sy * stripSize * fansXno + (sx+1) * stripSize + 8;
		}
	}
}
/**
 * Returns index of the point in coord table (needs to multiply by 3 since there are 3 coordinates)
 */
public void indexOf3(int x, int y) {
	sind = 0;

	// Find the fan and get the index within it
	int sx = x / 2;
	int sy = y / 2;
	int isx = x % 2;
	int isy = y % 2;
	int si = 0;
	if (isy == 0) {
		if (isx == 0)
			si = 8;
		else 
			si = 0;
	} else {
		if (isx == 0)
			si = 7;
		else 
			si = 6;		
	}
	
	//System.out.println("si(" + x + "," + y + ")=" + si);
	
	// Add the point inside this fan
	sindv[sind++] = sy * stripSize * fansXno + sx * stripSize + si;

	// Find points from neighbours
	if (si == 7) {
		// Right upper corner
		if (sy+1 < fansYno) {
			sindv[sind++] = (sy+1) * stripSize * fansXno + sx * stripSize + 1;
			sindv[sind++] = (sy+1) * stripSize * fansXno + sx * stripSize + 9;
		}
		
		if (sx > 0) {
			sindv[sind++] = sy * stripSize * fansXno + (sx-1) * stripSize + 5;
			
			if (sy+1 < fansYno) {
				sindv[sind++] = (sy+1) * stripSize * fansXno + (sx-1) * stripSize + 3;
			}
		}
	} else if (si == 6) {
		// Y
		if (sy+1 < fansYno) {
			sindv[sind++] = (sy+1) * stripSize * fansXno + sx * stripSize + 2;
		}		
	} else if (si == 8) {
		// X
		if (sx > 0) {
			sindv[sind++] = sy * stripSize * fansXno + (sx-1) * stripSize + 4;
		}
	}
}
/**
 * Returns index of the point in coord table (needs to multiply by 3 since there are 3 coordinates)
 */
public void indexOf7(int x, int y) {
	sind = 0;

	// Find the fan and get the index within it
	int sx = x / 2;
	int sy = y / 2;
	int isx = x % 2;
	int isy = y % 2;
	int si = 0;
	if (isy == 0) {
		if (isx == 0)
			si = 8;
		else 
			si = 7;
	} else {
		if (isx == 0)
			si = 0;
		else 
			si = 6;		
	}
	
	//System.out.println("si(" + x + "," + y + ")=" + si);
	
	// Add the point inside this fan
	sindv[sind++] = sy * stripSize * fansXno + sx * stripSize + si;

	// Find points from neighbours
	if (si == 7) {
		// Right upper corner
		if (sy > 0) {
			sindv[sind++] = (sy-1) * stripSize * fansXno + sx * stripSize + 5;
		}
		
		if (sx+1 < fansXno) {
			sindv[sind++] = sy * stripSize * fansXno + (sx+1) * stripSize + 1;
			sindv[sind++] = sy * stripSize * fansXno + (sx+1) * stripSize + 9;
			if (sy > 0) {
				sindv[sind++] = (sy-1) * stripSize * fansXno + (sx+1) * stripSize + 3;
			}
		}
	} else if (si == 8) {
		// Top
		if (sy > 0) {
			sindv[sind++] = (sy-1) * stripSize * fansXno + sx * stripSize + 4;
		}		
	} else if (si == 6) {
		// Right
		if (sx+1 < fansXno) {
			sindv[sind++] = sy * stripSize * fansXno + (sx+1) * stripSize + 2;
		}
	}
}
/**
 * Matrix3DChart constructor comment.
 */
public void initialize(int width, int height, Color fc) {
	if ((width % 2) == 1 || (height % 2) == 1)
		throw new RuntimeException("width and height must be even");

	if (fc != null)
		floorColor = fc;
	mWidth = width;
	mHeight = height;

	totalPointsNo = width * height;

	// For even size, there is one more point
	fansXno = width / 2;
	fansYno = height / 2;

	// Total fans number	
	fansNo = fansXno * fansYno;

	totalFansPointsNo = fansNo * stripSize;
	arrayPointsNo = totalFansPointsNo * 3;

	//if (debug)
		//System.out.println("arrayPointsNo=" + arrayPointsNo);	

	// Each fan has 10 points, each point 3 coordinates
	coord = new float[arrayPointsNo]; 
	colors = new byte[arrayPointsNo];
	vertexCounts = new int[totalFansPointsNo / stripSize];

	if (debug) {
		System.out.println("coord=" + coord.length);
		System.out.println("colors=" + colors.length);
		System.out.println("vertexCounts=" + vertexCounts.length);
	}
	
	// Initialize vertex counts
	for (int i = 0; i < vertexCounts.length; i++){
		vertexCounts[i] = stripSize;		
	}

	// Initialize coordinates and colors
	int k;
	for (int j = 0; j < height; j++){
		//System.out.println("line " + j);
		for (int i = 0; i < width; i++){
			indexOf(i, j);
			//if (debug) {// && i == 1 && j == 1) {
				//System.out.print("Index of (" + i + "," + j + ")=");
				//for (int p = 0; p < sind; p++){
					//System.out.print("" + sindv[p] + ", ");
				//}
				//System.out.println();
			//}
			while (--sind >= 0){
				k = sindv[sind];
				// Shift chart to the center
				setX(k, 2 * scaleX * i / width  - scaleX + shiftX);
				setY(k, 2 * scaleY * (j+1) / height - scaleY + shiftY);
				setZ(k, shiftZ);
				setColor(k, floorColor);
			}			
		}
	}

	// Initialize edge coordinates and colors
	int p;
	for (int i = 0; i < fansXno; i++){
		p = i * stripSize * 3;
		coord[p + 1*3] = coord[p + 9*3] = coord[p + 8*3];
		coord[p + 2*3] = coord[p + 0*3];
		coord[p + 3*3] = coord[p + 4*3];
		coord[p + 1*3+1] = coord[p + 2*3+1] = 
		coord[p + 3*3+1] = coord[p + 9*3+1] = -scaleY + shiftY;  // Y
		coord[p + 1*3+2] = coord[p + 2*3+2] = 
		coord[p + 3*3+2] = coord[p + 9*3+2] = 0f + shiftZ;	// Z
		colors[p + 1*3] = colors[p + 2*3] = 
		colors[p + 3*3] = colors[p + 9*3] = (byte) floorColor.getRed();
		colors[p + 1*3+1] = colors[p + 2*3+1] = 
		colors[p + 3*3+1] = colors[p + 9*3+1] = (byte) floorColor.getGreen();
		colors[p + 1*3+2] = colors[p + 2*3+2] = 
		colors[p + 3*3+2] = colors[p + 9*3+2] = (byte) floorColor.getBlue();
	}

	for (int i = 0; i < fansYno; i++){
		p = (i * fansXno + fansXno - 1) * stripSize * 3;
		coord[p + 3*3] = coord[p + 4*3] = coord[p + 5*3] = scaleX + shiftX;
		coord[p + 3*3+1] = coord[p + 2*3+1];
		coord[p + 4*3+1] = coord[p + 0*3+1];
		coord[p + 5*3+1] = coord[p + 6*3+1];
		coord[p + 3*3+2] = coord[p + 4*3+2] = coord[p + 5*3+2] = 0f + shiftZ;	// Z
		colors[p + 3*3] = colors[p + 4*3] = colors[p + 5*3] = (byte) floorColor.getRed();
		colors[p + 3*3+1] = colors[p + 4*3+1] = colors[p + 5*3+1] = (byte) floorColor.getGreen();
		colors[p + 3*3+2] = colors[p + 4*3+2] = colors[p + 5*3+2] = (byte) floorColor.getBlue();
	}
	
		
	// Paint test line across
	//for (int i = 0; i < Math.min(width, height); i++){
		//indexOf(i, i); // Results (1-4) are stored in local variables
		//while (--sind >= 0) {
			//k = sindv[sind];
			//setZ(k, 0.3f);
			//setColor(k, 0f, 0f, 1f); // Red
		//}			
	//}

	//if (debug) {
		//System.out.println("Coordinates table:");
		//for (int i = 0; i < coord.length; i++){
			//if ((i % 30) == 0)
				//System.out.print("\nfan " + (i / 30) + ": ");
			//if ((i % 3) == 0)
				//System.out.print("[");
			//if ((i % 3) == 1 || (i % 3) == 2)
				//System.out.print(",");			
			//System.out.print("" + coord[i]);
			
			//if ((i % 3) == 2)
				//System.out.print("] ");
		//}
	//}
	
	super.initialize(width, height, null);//Color.black);

	canvas.getGraphics().drawString("Test", 10, 10);

	
	//System.out.println("canvas_pw=" + canvas.getPhysicalWidth());
	//System.out.println("canvas_ph=" + canvas.getPhysicalHeight());
	Point3d po = new Point3d();
	canvas.getPixelLocationInImagePlate(0,0,po);
	//System.out.println("p=" + po);
	canvas.getCenterEyeInImagePlate(po);
	//System.out.println("c_eye=" + po);
	//System.out.println("cv=" + canvas.getWidth() + " ch=" + canvas.getHeight());
}
/**
 * Returns index of the point in coord table (needs to multiply by 3 since there are 3 coordinates)
 */
public final int lessX(int k) {
	if (si == 2 || si == 3)
		return k-1;
	if (si == 6 || si == 6)
		return k+1;
	if (si == 4)
		return k - 4;
	if (si == 0)
		return k +8;
	if (si == 1 || k == 9)
		return k - stripSize + 2;
	if (si == 8)
		return k - stripSize - 4;
	return k - stripSize - 2;
	
		
		
	
		
}
/**
 * Returns index of the point in coord table (needs to multiply by 3 since there are 3 coordinates)
 */
public final int lessY(int k) {
	if (si == 0)
		return k+2;
	if (si == 1)
		return k - stripSize*fansXno + 6;
	if (si == 2)
		return k - stripSize*fansXno + 4;
	if (si == 3)
		return k - stripSize*fansXno + 2;
	if (si == 4 || si == 5)
		return k - 1;
	if (si == 6)
		return k - 6;
	if (si == 7)
		return k + 1;
	if (si == 8)
		return k - 7;
	// if (si == 9)
	return k - stripSize*fansXno - 2;
}
/**
 * Matrix3DChart constructor comment.
 */
public final void setColor(int k, byte b[]) {
	colors[k*3] = b[0];
	colors[k*3+1] = b[1];
	colors[k*3+2] = b[2];
}
/**
 * Matrix3DChart constructor comment.
 */
public final void setColor(int k, byte r, byte g, byte b) {
	colors[k*3] = r;
	colors[k*3+1] = g;
	colors[k*3+2] = b;
}
/**
 * Matrix3DChart constructor comment.
 */
public final void setColor(int k, Color c) {
	colors[k*3] = (byte)c.getRed();
	colors[k*3+1] = (byte)c.getGreen();
	colors[k*3+2] = (byte)c.getBlue();
}
/**
 * Matrix3DChart constructor comment.
 */
public final void setX(int k, float v) {
	//if (debug)
		//System.out.println("Setting X(" + k + ")=" + v);
	coord[k*3] = v;
}
/**
 * Matrix3DChart constructor comment.
 */
public final void setY(int k, float v) {
	//if (debug)
		//System.out.println("Setting Y(" + k + ")=" + v);
	coord[k*3+1] = v;
}
/**
 * Matrix3DChart constructor comment.
 */
public final void setZ(int k, float v) {
	//if (debug)
		//System.out.println("Setting Z(" + k + ")=" + v);
	coord[k*3+2] = v;
}
/**
 * Returns index of the point in coord table (needs to multiply by 3 since there are 3 coordinates)
 */
public void testFields(BranchGroup scene) {
	drawLine(scene, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f);
	drawLine(scene, -0.5f, -0.02f, 0.0f, -0.5f, 0.02f, 0.0f);
	drawLine(scene, -1f, -0.02f, 0.0f, -1f, 0.02f, 0.0f);
	drawLine(scene, 0.5f, -0.02f, 0.0f, 0.5f, 0.02f, 0.0f);
	drawLine(scene, 1f, -0.02f, 0.0f, 1f, 0.02f, 0.0f);
	
	drawLine(scene, 0f, -1.0f, 0.0f, 0f, 1.0f, 0.0f);
	drawLine(scene, 0.02f, -1.0f, 0.0f, -0.02f, -1.0f, 0.0f);
	drawLine(scene, 0.02f, 1.0f, 0.0f, -0.02f, 1.0f, 0.0f);
	drawLine(scene, 0.02f, -0.5f, 0.0f, -0.02f, -0.5f, 0.0f);
	drawLine(scene, 0.02f, 0.5f, 0.0f, -0.02f, 0.5f, 0.0f);

//	drawLine(scene, 0f, 0f, -1.0f, 0f, 0f, 1.0f);
}
}
