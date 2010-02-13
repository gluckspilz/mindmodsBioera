/* AxisChart.java v 1.0.9   11/6/04 7:15 PM
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
public abstract class AxisChart extends BorderChart {
	public double xMaxValue = 100;
	public double xMinValue = 100;
	public String xUnit = "";

	protected int xPixelsInc = 4;
	protected int xValueInc = 1;
	public int xMinSpace = 15;

	public double yMaxValue = 100;
	public double yMinValue = 100;
	public String yUnit = "";

	protected int yPixelsInc = 4;
	protected int yValueInc = 1;

	public int yMinSpace = 5;


	// Below are calculation results, they should not be set

	public int yNewIndexInc;
	public int yNewPixelInc;

	public int xNewIndexInc;  // index in table
	public int xNewPixelInc;  // pixel on chart

	public int yEffectivePixelRange; // height of the window with signal can be smaller then then chartHeight
	public int xEffectivePixelRange; // width of the window with signal can be smaller then then chartWidth

	public double yValueRange;
	public double xValueRange;  // MAX - MIN

	public boolean grid = false;
/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 10:58:46 PM)
 */
public AxisChart() {
	super();
}
/**
 * 	Print on chart
 */
protected void createInitImage() {
	super.createInitImage();

	// Vertical axis
	drawInitVerticalAxis();

	// Vertical axis descriptions
	drawInitVerticalAxisDesc();

	// Horizontal axis
	drawInitHorizontalAxis();

	// Horizontal axis descriptions
	drawInitHorizontalAxisDesc();

	drawCornerDescription();
}
/**
 * 	Print on chart
 */
public String doubleToString(double v) {
	if (v == 0)
		return "0";
	if (v > 99 || v < -99)
		return "" + (int) v;
	if (v > 9 || v < -9) {
		v *= 10;
		v = Math.round(v);
		v /= 10;
	} else if (v > 0.01 || v < -0.01) {
		v *= 100;
		v = Math.round(v);
		v /= 100;
	} else {
		v *= 10000;
		v = Math.round(v);
		if (v == (int) v)
			return "'" + (int) v;
		return "'" + v;
	}
	if (v == (int) v)
		return "" + (int) v;
	String ret = "" + v;
	if (ret.startsWith("0."))
		return ret.substring(1);
	return ret;
}
/**
 * 	Print on chart
 *	Make sure the exact mid-range is always printed.
 *  The range can be from negative to positive
 */
protected void drawBalancedHorizontalAxisDesc() {
	int fontHeight = initGraphics.getFontMetrics().getAscent();
	int fontWidth = fontHeight; // for now
	xValueRange = xMaxValue - xMinValue;
	int n = (chartWidth / 2 / (fontWidth + xMinSpace)) * 2;
	if (n < 1)
		n = 1;
	int x, x1, len;
	double v, half = (xMaxValue + xMinValue) / 2;
	//System.out.println("step=" + step + " range=" + range + " n=" + n + " chartHeight=" + chartHeight + " yMinSpace=" + yMinSpace + " fontHeight=" + fontHeight);
	for (int i = 1 - n/2; i < 1 + n/2; i++){
		v = half + i * xValueRange / n;
		x = (int)(leftMargin + chartWidth * (v - xMinValue) / xValueRange );
		if (grid)
			initGraphics.drawLine(x, compHeight - downMargin, x, topMargin);
		else
			initGraphics.drawLine(x, compHeight - downMargin, x, compHeight - downMargin - 4);
		len = initGraphics.getFontMetrics().stringWidth(doubleToString(v));
		initGraphics.drawString(doubleToString(v), x - len / 2, compHeight - downMargin + fontHeight);
	}

	len = initGraphics.getFontMetrics().stringWidth("[" + xUnit + "]");
	if (xUnit != null && xUnit.trim().length() > 0)
		initGraphics.drawString("[" + xUnit + "]", leftMargin, compHeight - downMargin + fontHeight);

	xEffectivePixelRange = chartWidth;
}
/**
 * 	Print on chart
 *	Make sure the exact mid-range is always printed.
 *  The range can be from negative to positive
 */
protected void drawBalancedVerticalAxisDesc() {
	int fontHeight = initGraphics.getFontMetrics().getAscent();
	yValueRange = yMaxValue - yMinValue;
	if (yValueRange < 0) {
		yValueRange = 0;
	}
	int n = (chartHeight / 2 / (fontHeight + yMinSpace)) * 2;
	if (n < 1)
		n = 1;
	int y, y1, len;
	double v, half = (yMaxValue + yMinValue) / 2;
	//System.out.println("step=" + step + " range=" + range + " n=" + n + " chartHeight=" + chartHeight + " yMinSpace=" + yMinSpace + " fontHeight=" + fontHeight);
	for (int i = 1 - n/2; i < 1 + n/2; i++){
		v = half + i * yValueRange / n;
		y = (int)(compHeight - downMargin - chartHeight * (v - yMinValue) / yValueRange);
		if (grid)
			initGraphics.drawLine(leftMargin, y, compWidth - rightMargin, y);
		else
			initGraphics.drawLine(leftMargin, y, leftMargin + 4, y);
		len = initGraphics.getFontMetrics().stringWidth(doubleToString(v));
		initGraphics.drawString(doubleToString(v), leftMargin - len - 1, y + fontHeight / 2);
	}

	len = initGraphics.getFontMetrics().stringWidth("[" + yUnit + "]");
	if (yUnit != null && yUnit.trim().length() > 0)
		initGraphics.drawString("[" + yUnit + "]", leftMargin - len - 3, compHeight - downMargin);

	yEffectivePixelRange = chartHeight;
}
/**
 * 	Print on chart
 */
protected void drawCornerDescription() {
	if (chartWidth < 20)
		return;

        if (chartName.contains("Osc"))
                return;

	int fontHeight = initGraphics.getFontMetrics().getAscent();
	int descWidth = initGraphics.getFontMetrics().stringWidth(chartName);

	initGraphics.clearRect(compWidth - (descWidth + 3), 1, descWidth + 1, fontHeight + 3);
	initGraphics.drawLine(compWidth - 1, fontHeight+2, compWidth - (descWidth + 2), fontHeight+2);
	initGraphics.drawLine(compWidth - (descWidth + 2), 0, compWidth - (descWidth + 2), fontHeight+2);
	initGraphics.drawString(chartName, compWidth - (descWidth + 1), fontHeight);
	//System.out.println("ok");
}
/**
 * 	Print on chart
 */
protected void drawInitHorizontalAxis() {
	initGraphics.drawLine(leftMargin, compHeight - downMargin, compWidth - rightMargin, compHeight - downMargin);
}
/**
 * 	Print on chart
 */
protected abstract void drawInitHorizontalAxisDesc();
/**
 * 	Print on chart
 *	Make sure the exact mid-range is always printed.
 *  The range can be from negative to positive
 */
protected void drawInitVerticalAxis() {
	initGraphics.drawLine(leftMargin, topMargin, leftMargin, compHeight - downMargin);
}
/**
 * 	Print on chart
 *	Make sure the exact mid-range is always printed.
 *  The range can be from negative to positive
 */
protected abstract void drawInitVerticalAxisDesc();
/**
 * 	Equal number of pixels is assigned to values.
 *  For example if chart width is 99 and value range is 20,
 *  then the effective pixel range will be 80.
 */
protected void drawScaledHorizontalAxisDesc() {
	if (chartWidth < 20)
		return;

	if (xMinValue == Float.MIN_VALUE || xMaxValue == Float.MAX_VALUE)
		return;

	xValueRange = xMaxValue - xMinValue;
	xNewIndexInc = xValueInc;

	// Look for maximum window length that can be fit on chart
	int xWindowWidth = (int)(xValueRange * xPixelsInc / xValueInc);
	if (xWindowWidth < 1)
		xWindowWidth = 1;

	// Scale down if needed
	while (xWindowWidth > chartWidth) {
		xNewIndexInc += 1;
		xWindowWidth = (int)(xValueRange * xPixelsInc / xNewIndexInc);
	}

	int xWindowsCount = chartWidth / xWindowWidth;
	xNewPixelInc = xPixelsInc * xWindowsCount;
	xEffectivePixelRange = xWindowsCount * xWindowWidth;

	int fontHeight = initGraphics.getFontMetrics().getAscent();
	int descWidth = initGraphics.getFontMetrics().stringWidth(doubleToString(xMaxValue));

	int n = xWindowWidth / (descWidth);// + xMinSpace);
	if (n < 1)
		n = 1;
	int x, len, i = 0;
	int stepV = (int)(xValueRange / n / xWindowsCount);
	if (stepV < 1)
		stepV = 1;
	int stepP = stepV *	xNewPixelInc / xNewIndexInc;
	if (stepP < 1)
		stepP = 1;
	//System.out.println("-------name="+getChartName());
	//System.out.println("stepP=" + stepP + " stepV=" + stepV +" xWindowWidth=" + xWindowWidth + " xWindowsCount=" + xWindowsCount + " xLengh=" + chartWidth);
	//System.out.println(" valueRange=" + xValueRange + " n=" + n + " chartWidth=" + chartWidth + " xMinSpace=" + xMinSpace + " fontHeight=" + fontHeight + " descWidth=" + descWidth + " xMaxValue=" + xMaxValue + " xMinValue=" + xMinValue);
	//System.out.println(" xNewIndexInc=" + xNewIndexInc + " xNewPixelsInc=" + (xPixelsInc * xWindowsCount));
	//System.out.println("xEffectivePixelRange="+xEffectivePixelRange);

	// Place unit
	//len = initGraphics.getFontMetrics().stringWidth("[" + xUnit + "]");
        len = initGraphics.getFontMetrics().stringWidth("[Seconds]");   //mmods
	int unitXPos = compWidth - rightMargin - len;//mmods
        //int unitXPos = compWidth - rightMargin - len/2;
	if (xUnit != null && xUnit.trim().length() > 0)
		initGraphics.drawString("[Seconds]", unitXPos, compHeight - downMargin + fontHeight);//mmods
            //initGraphics.drawString("[" + xUnit + "]", unitXPos, compHeight - downMargin + fontHeight);

	//System.out.println("xMinValue="+xMinValue);
	//System.out.println("unitXPos="+unitXPos);
	// Place values

	double v = xMinValue;
	x = leftMargin;
	int totalX = 0;
	len = initGraphics.getFontMetrics().stringWidth(doubleToString(9999));
	//System.out.println("len=" + len);
	int space = stepP;
	while (space < len)
		space += stepP;
	while (space / 2 > len)
		space = space / 2;
	//System.out.println("space=" + space);
	while (true) {
		len = initGraphics.getFontMetrics().stringWidth(doubleToString(v));
		//System.out.println("x="+x);
		//System.out.println("v="+v);
		//System.out.println("len="+len);

		if (x > compWidth - rightMargin)
			break;
		if (grid)
			initGraphics.drawLine(x, compHeight - downMargin, x, topMargin);
		else
			initGraphics.drawLine(x, compHeight - downMargin, x, compHeight - downMargin - 4);
		if (x + len / 2 >= unitXPos)
			break;

		initGraphics.drawString(doubleToString(v), x - len/2, compHeight - downMargin + fontHeight);

		x += space;
		totalX += space;
		v = xMinValue + (((double)xValueRange) * totalX) / xEffectivePixelRange;
	}
}
/**
 * 	Equal number of pixels is assigned to values.
 *  For example if chart width is 99 and value range is 20,
 *  then the effective pixel range will be 80.
 */
protected void drawScaledVerticalAxisDesc() {
	if (chartHeight < 20)
		return;

	if (yMinValue == Float.MIN_VALUE || yMaxValue == Float.MAX_VALUE)
		return;

	yValueRange = yMaxValue - yMinValue;
	yNewIndexInc = yValueInc;

	// Look for maximum window length that can be fit on chart
	int yWindowHeight = (int)(yValueRange * yPixelsInc / yValueInc);
	if (yWindowHeight < 1)
		yWindowHeight = 1;

	// Scale down if needed
	while (yWindowHeight > chartHeight) {
		yNewIndexInc += 1;
		yWindowHeight = (int)(yValueRange * yPixelsInc / yNewIndexInc);
	}

	int yWindowsCount = chartHeight / yWindowHeight;
	yNewPixelInc = yPixelsInc * yWindowsCount;
	yEffectivePixelRange = yWindowsCount * yWindowHeight;

	int fontHeight = initGraphics.getFontMetrics().getAscent();
//	int descW = initGraphics.getFontMetrics().stringWidth("" + xMaxValue);

	int n = yWindowHeight / (fontHeight + yMinSpace);
	if (n < 1)
		n = 1;
	int y, len, i = 0;
	int stepV = (int)(yValueRange / n / yWindowsCount);
	if (stepV < 1)
		stepV = 1;
	int stepP = stepV *	yNewPixelInc / yNewIndexInc;
	if (stepP < 1)
		stepP = 1;
	//System.out.println("stepP=" + stepP + " stepV=" + stepV +" xWindowWidth=" + xWindowWidth + " xWindowsCount=" + xWindowsCount + " xLengh=" + chartWidth);
	//System.out.println(" valueRange=" + valueRange + " n=" + n + " chartWidth=" + chartWidth + " xMinSpace=" + xMinSpace + " fontHeight=" + fontHeight + " descWidth=" + descWidth + " xMaxValue=" + xMaxValue + " xMinValue=" + xMinValue);
	//System.out.println(" xNewValueInc=" + xNewValueInc + " xNewPixelsInc=" + (xPixelsInc * xWindowsCount));
	double v = yMaxValue;
	while (v > yMinValue + stepV) {
		v = yMaxValue - i * stepV;
		y = (compHeight - downMargin) - yEffectivePixelRange + stepP * i++;
		if (grid)
			initGraphics.drawLine(leftMargin, y, leftMargin + chartWidth, y);
		else
			initGraphics.drawLine(leftMargin, y, leftMargin + 4, y);
		len = initGraphics.getFontMetrics().stringWidth(doubleToString(v));
		initGraphics.drawString(doubleToString(v), leftMargin - len - 2, y + fontHeight / 2);
		//System.out.println("v=" + v + " x=" + (x-leftMargin));
	}

	len = initGraphics.getFontMetrics().stringWidth("[" + xUnit + "]");
	if (yUnit != null && yUnit.trim().length() > 0)
		initGraphics.drawString("[" + yUnit + "]", (leftMargin - len) / 2, compHeight - downMargin + 2);
	//System.out.println("ok");
}
/**
 * 	Print on chart
 *	Make sure the exact mid-range is always printed.
 *  The range can be from negative to positive
 */
public double rescaleX() {
	double ratio = 1;
	while (xMinValue != 0 && xMinValue * ratio < 0.1 && xMinValue * ratio > -0.1) {
		if ("s".equals(xUnit)) {
			xUnit = "ms";
			ratio *= 1000.0;
		} else if ("ms".equals(xUnit)) {
			xUnit = "us";
			ratio *= 1000.0;
		} else if ("us".equals(xUnit)) {
			xUnit = "ns";
			ratio *= 1000.0;
		} else if ("ns".equals(xUnit)) {
			xUnit = "ps";
			ratio *= 1000.0;
		} else {
			xUnit = "m" + xUnit;
			ratio *= 1000.0;
		}
	}
	while (xMaxValue*ratio > 999 || xMinValue*ratio < -999) {
		if ("s".equals(xUnit)) {
			xUnit = "min";
			ratio /= 60.0;
		} else if ("min".equals(xUnit)) {
			xUnit = "h";
			ratio /= 60.0;
		} else {
			xUnit = "k" + xUnit;
			ratio /= 1000.0;
		}
	}

	xMaxValue *= ratio;
	xMinValue *= ratio;

	return ratio;
}
/**
 * 	Print on chart
 *	Make sure the exact mid-range is always printed.
 *  The range can be from negative to positive
 */
public double rescaleY() {
	double ratio = 1;
	if (yMaxValue > 9999 || yMinValue < -9999) {
		if ("uV".equals(yUnit))
			yUnit = "mV";
		else if ("mV".equals(yUnit))
			yUnit = "V";
		else if ("".equals(yUnit))
			yUnit = "x 1k";
		else
			yUnit = "k" + yUnit;
		ratio /= 1000;
	}

	yMaxValue *= ratio;
	yMinValue *= ratio;

	return ratio;
}
/**
 * 	Print on chart
 */
public void setXPixelInc(double v) {
	if (v >= Float.MAX_VALUE || v <= Float.MIN_VALUE)
		return;
	if (v > 1) {
		xValueInc = 1;
		xPixelsInc = (int) v;
	} else if (v > 0) {
		xValueInc = (int) (1/v);
		xPixelsInc = 1;
	}
}
/**
 * 	Print on chart
 */
public void setXValueInc(double v) {
	if (v >= Float.MAX_VALUE || v <= Float.MIN_VALUE)
		return;
	if (v > 1) {
		xValueInc = (int) v;
		xPixelsInc = 1;
	} else if (v > 0) {
		xValueInc = 1;
		xPixelsInc = (int) (1/v);
	}
}
/**
 * 	Print on chart
 */
public void setYPixelInc(double v) {
	if (v >= Float.MAX_VALUE || v <= Float.MIN_VALUE)
		return;
	if (v > 1) {
		yValueInc = 1;
		yPixelsInc = (int) v;;
	} else if (v > 0){
		yValueInc = (int) (1/v);
		yPixelsInc = 1;
	}
}
/**
 * 	Print on chart
 */
public void setYValueInc(double v) {
	if (v >= Float.MAX_VALUE || v <= Float.MIN_VALUE)
		return;
	if (v > 1) {
		yValueInc = (int) v;
		yPixelsInc = 1;
	} else if (v > 0){
		yValueInc = 1;
		yPixelsInc = (int) (1/v);
	}
}
/**
 * 	Print on chart
 */
private String showXValue(int n) {
	return "" + n;
}
/**
 * 	Print on chart
 */
protected String showYValue(int n) {
	return "" + n;
}
/**
 * 	Print on chart
 */
private String widestXValue(int n) {
	return showXValue(n);
}
/**
 * 	Print on chart
 */
protected String widestYValue(int n) {
	return showYValue(n);
}
}
