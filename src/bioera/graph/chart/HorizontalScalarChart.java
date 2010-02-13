/* HorizontalScalarChart.java v 1.0.9   11/6/04 7:15 PM
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
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import bioera.*;
import bioera.graph.designer.*;
import bioera.processing.impl.*;
import bioera.processing.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class HorizontalScalarChart extends HorizontalChart {
	// Internal values
	protected int x, y;//, x1, y1; // position of points to draw
        private int xprev = 0;
        private int yprev = 0;
        private int y1prev = -1;
        private int y2prev = -1;
        private int streamprev=0;
        private int y2last = 0;
	protected int xPos, xPos2;	// print position in buffer
	protected int valueCounter = 0;
	public boolean joinPoints = true;
        private int lastMode = -1;
        private boolean secondOfDual = false;




/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public HorizontalScalarChart() {
	super();
	leftMargin = 40;
}
/**
 * 	Print on chart
 */
protected void createInitImage() {
	super.createInitImage();

	x = leftMargin;
	y = topMargin + chartHeight;
}
/**
 * 	Print on chart
 */
public void pushMinMax(int min, int max) {
	if (gr == null || chartWidth == 0 || chartHeight == 0)
		return;
	int dy = (max - min) / 2;
	int y1 = (compHeight - downMargin) - (min + max) / 2;
	int x1 = leftMargin + xPos;
	if (y1 - dy < topMargin) {
		y1 = topMargin;
		dy = 0;
	}
	if (y1 + dy > topMargin + chartHeight) {
		y1 = topMargin + chartHeight;
		dy = 0;
	}
	if (x1 <  x) {
		copyInitImage();
	} else if (x + pixelIncrement == x1) {
		// Set progress indicator line
		Color c = gr.getColor();
		gr.setColor(Color.white);
		gr.drawLine(x, (chartHeight >> 1) + topMargin, x1, (chartHeight >> 1) + topMargin);
		gr.setColor(c);

		if (y >= topMargin && y <= topMargin + chartHeight) {
			if (joinPoints)
				gr.drawLine(x, y, x1, y1);
			gr.drawLine(x1, y1 - dy, x1, y1 + dy);
		}
	}
	x = x1;
	y = y1;

	xPos = (xPos + pixelIncrement) % xEffectivePixelRange;
}
/**
 * 	Print on chart
 */
public void pushMinMax2(int from, int to, int min, int max) {
	if (gr == null || chartWidth == 0 || chartHeight == 0)
		return;

	//if (from < min || from > max || to < min || to > max)
		//System.out.println("!!!: " + from + " " + to + " " + min + " " + max);
	//int dy = (max - min) / 2;
	int yL = (compHeight - downMargin);
	int x1 = leftMargin + xPos;
	max = yL - max;
	if (max < topMargin)
		max = topMargin;
	else if (max >= yL)
		max = yL - 1;
	min = yL - min;
	if (min < topMargin)
		min = topMargin;
	else if (min >= yL)
		min = yL - 1;
	from = yL - from;
	if (from < topMargin)
		from = topMargin;
	else if (from >= yL)
		from = yL - 1;
	to = yL - to;
	if (to < topMargin)
		to = topMargin;
	else if (to >= yL)
		to = yL - 1;

	if (x1 <  x) {
		copyInitImage();
	} else if (x + pixelIncrement == x1) {
		// Set progress indicator line
		Color c = gr.getColor();
		gr.setColor(Color.white);
		gr.drawLine(x, (chartHeight >> 1) + topMargin, x1, (chartHeight >> 1) + topMargin);
		gr.setColor(c);

		if (joinPoints) {
			////gr.setColor(Color.yellow);
			gr.drawLine(x, y, x1, from);
			////gr.setColor(Color.red);
			gr.drawLine(x1, min, x1, max);
			////gr.setColor(c);
			////gr.drawLine(x1, yL - from, x1, yL - to);
		} else {
			to = (from + to) / 2;
			gr.drawLine(x, to, x, to);
		}
	}
	x = x1;
	y = to;

	xPos = (xPos + pixelIncrement) % xEffectivePixelRange;
}
/**
 * 	Print on chart
 */
public void pushValue(int value) {
	if (!initialized)
		return;

	//if (gr == null || chartWidth == 0 || chartHeight == 0)
		//return;

	int y1 = (compHeight - downMargin) - value;
	int x1 = leftMargin + xPos;
	if (y1 < topMargin)
		y1 = topMargin;
	if (y1 > topMargin + chartHeight)
		y1 = topMargin + chartHeight;
	if (x1 <  x) {
		copyInitImage();
	} else if (x + pixelIncrement == x1) {
		// Set progress indicator line
		Color c = gr.getColor();
		gr.setColor(Color.white);
		//gr.drawLine(x, (chartHeight >> 1) + topMargin, x1, (chartHeight >> 1) + topMargin);
		gr.setColor(Color.green);

		if (y >= topMargin && y <= topMargin + chartHeight) {  //if y is on chart


				gr.drawLine(x1, (y1-2), x1, y1);
		}
	}
	x = x1;
	y = y1;


	xPos = (xPos + pixelIncrement) % xEffectivePixelRange;
}



//    public void pushDataChannel(


    public void pushStreams(int value, int xspot, int stream, boolean first, int mode) {
        if (!initialized)
		return;
        int y1 = (compHeight - downMargin) - value;
        int x1 = leftMargin + xspot;

        if (first==true){
            xprev= x1;  yprev= y1;

            if (  (lastMode == 2) && (mode ==2)  ){
                secondOfDual=true;
            }
            else{
                secondOfDual=false;
            }
        }

        if (stream==1){gr.setColor(Main.app.c1);}
        if (stream==2){if (secondOfDual==true){gr.setColor(Main.app.c1b);}
            else{gr.setColor(Main.app.c2);}        }
        if (stream==3){gr.setColor(Main.app.c3);}
        if (stream==4){if (secondOfDual==true){gr.setColor(Main.app.c3b);}
            else{gr.setColor(Main.app.c4);}        }
        if (stream==5){gr.setColor(Main.app.c5);}
        if (stream==6){if (secondOfDual==true){gr.setColor(Main.app.c5b);}
            else{gr.setColor(Main.app.c6);}        }
        if (stream==7){gr.setColor(Main.app.c7);}
        if (stream==8){if (secondOfDual==true){gr.setColor(Main.app.c7b);}
            else{gr.setColor(Main.app.c8);}      }
        if (stream==9){gr.setColor(Main.app.c9);}
        if (stream==10){if (secondOfDual==true){gr.setColor(Main.app.c9b);}
            else{gr.setColor(Main.app.c10);}      }
        if (stream==11){gr.setColor(Main.app.c11);}
        if (stream==12){if (secondOfDual==true){gr.setColor(Main.app.c11b);}
            else{gr.setColor(Main.app.c12);}      }




        gr.drawLine(xprev, yprev, x1, y1);
        //System.out.println("from push:    xprev = "+xprev+" yprev = "+yprev+" x1= "+x1+" y1= "+y1);
        gr.drawLine(xprev-1, yprev-1, x1-1, y1-1);

        xprev = x1;
        yprev = y1;
        streamprev = stream;
        lastMode = mode;
        //xPos = (xPos + pixelIncrement) % xEffectivePixelRange;
    }


    public void pushValuewTime(int value, int xspot) {
            if (!initialized)
                    return;

            int y1 = (compHeight - downMargin) - value;
            int x1 = (leftMargin + xspot);
            if (y1 < topMargin) {y1 = topMargin;}
            if (y1 > topMargin + chartHeight) {y1 = topMargin + chartHeight;}

            if (x1 < (xprev-5)) {
                xprev=leftMargin; //set drawfrom pnt (xprev) to leftmargin if more than this x1
                yprev = y1; //above signifies redraw. assign current y to last
            }


            gr.setColor(Main.app.c3);

            if (yprev >= topMargin && yprev <= topMargin + chartHeight) { //if yprev is on chart

                gr.drawLine(xprev, yprev, x1, y1); //use last values to connect pixels
                gr.drawLine(xprev, yprev, x1 + 1, y1 + 1); //wide line
            }

            xprev = x1;
            yprev = y1;

}


        public void pushValuewDualTime(int value, int value2, int xspot) {
            if (!initialized)
                return;

            int y1 = (compHeight - downMargin) - value;
            int y2 = (compHeight - downMargin) - value2;

            int x1 = leftMargin + xspot;

            if (y1 < topMargin)y1 = topMargin;
            if (y2 < topMargin)y2 = topMargin;

            if (y1 > topMargin + chartHeight)y1 = topMargin + chartHeight;
            if (y2 > topMargin + chartHeight)y2 = topMargin + chartHeight;

            if (x1 < (xprev-5)) {
                xprev=leftMargin; //set drawfrom pnt (xprev) to leftmargin if more than this x1
                y1prev = y1; //above signifies redraw. assign current y to last
                y2prev = y2; //above signifies redraw. assign current y to last
            }

            if ((y1prev >= topMargin) && (y1prev <= (topMargin + chartHeight))) { //if yprev is on chart
                gr.setColor(Main.app.c1);
                gr.drawLine(xprev, y1prev, x1, y1); //use last values to connect pixels for value1
                gr.drawLine(xprev, y1prev, x1 + 1, y1 + 1); //wide line

                gr.setColor(Main.app.c3);
                gr.drawLine(xprev, y2prev, x1, y2); //use last values to connect pixels for value2
                gr.drawLine(xprev, y2prev, x1 + 1, y2 + 1); //wide line
                //gr.drawLine(leftMargin+xspot, (y1-2), leftMargin+xspot, y1);
                //gr.drawLine(x1, (y1-2), x1, y1);
            }


            xprev = x1;
            y1prev = y1;
            y2prev = y2;


        }



    public void pushValueDual(int value, int value2) {
            if (!initialized)
                    return;

            //if (gr == null || chartWidth == 0 || chartHeight == 0)
                    //return;

            int y1 = (compHeight - downMargin) - value;
            int y2 = (compHeight - downMargin) - value2;
            int x1 = leftMargin + xPos;
            if (y1 < topMargin)
                    y1 = topMargin;
            if (y1 > topMargin + chartHeight)
                    y1 = topMargin + chartHeight;

            if (y2 < topMargin)
                    y2 = topMargin;
            if (y2 > topMargin + chartHeight)
                    y2 = topMargin + chartHeight;

            if (x1 <  x) {
                    copyInitImage();
            }
            else if (x + pixelIncrement == x1) {
                    // Set progress indicator line
                    Color c = gr.getColor();
                    gr.setColor(Color.black);
                    //gr.drawLine(x, (chartHeight >> 1) + topMargin, x1, (chartHeight >> 1) + topMargin);

                    if (y >= topMargin && y <= topMargin + chartHeight) {
                        gr.setColor(Color.green);
                                    gr.drawLine(x1, (y1-2), x1, y1);
                        gr.setColor(Color.yellow);
                                    gr.drawLine(x1, (y2-2), x1, y2);
                    }
                    gr.setColor(c);
            }

            x = x1;
            y = (y1+y2)/2; //average y values for margin calculation

            xPos = (xPos + pixelIncrement) % xEffectivePixelRange;







}
/**
 * 	Print on chart
 */
public boolean pushValueScaleX(int value) {
	if (!initialized)
		return false;

	//if (gr == null || chartWidth == 0 || chartHeight == 0)
		//return false;

	if (valueCounter++ % valueIncrement == 0) {
		int y1 = (compHeight - downMargin) - value;
		int x1 = leftMargin + xPos;
		if (y1 < topMargin)
			y1 = topMargin;
		if (y1 > topMargin + chartHeight)
			y1 = topMargin + chartHeight;
		if (x1 <  x) {
			copyInitImage();
		} else {
			gr.drawLine(x, y, x1, y1);
		}
		x = x1;
		y = y1;

		xPos = (xPos + pixelIncrement) % xEffectivePixelRange;

		return true;
	}

	return false;
}
/**
 * 	Print on chart
 */
public void reset() {
	if (xPos == 0)
		copyInitImage();
	else
		xPos = 0;
}

/**
 * 	Print on chart
 */
public void pushMinMax3(int from, int to, int min, int max) {
	if (gr == null || chartWidth == 0 || chartHeight == 0)
		return;
	//int dy = (max - min) / 2;
	int yL = (compHeight - downMargin);
	int x1 = leftMargin + xPos;
	if (yL - max < topMargin)
		max = yL - topMargin;
	if (min < 0)
		min = 0;
	if (yL - from < topMargin)
		from = yL - topMargin;
	if (yL - to < topMargin)
		to = yL - topMargin;
	if (x1 <  x) {
		copyInitImage();
	} else if (x + pixelIncrement == x1) {
		// Set progress indicator line
		Color c = gr.getColor();
		gr.setColor(Color.white);
		gr.drawLine(x, (chartHeight >> 1) + topMargin, x1, (chartHeight >> 1) + topMargin);
		gr.setColor(c);

		if (y >= topMargin && y <= topMargin + chartHeight) {
			if (joinPoints) {
				gr.drawLine(x, y, x1, yL - from);
				gr.drawLine(x1, yL - min, x1, yL - max);
				////gr.drawLine(x1, yL - from, x1, yL - to);
			} else {
				from = (from + to) / 2;
				gr.drawLine(x1, yL - from, x1, yL - from);
			}
		} else {
			System.out.println("out of range");
		}
	}
	x = x1;
	y = yL - to;

	xPos = (xPos + pixelIncrement) % xEffectivePixelRange;
}
}
