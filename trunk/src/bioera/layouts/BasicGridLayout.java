/* BasicGridLayout.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.layouts;

import java.awt.*;

class BasicGridLayout extends ConstraintLayout {

	protected int hGap = 0, vGap = 0;
	protected int rows, cols, reqRows, reqCols;
	protected int[] rowHeights, colWidths;
	protected int alignment = Alignment.WEST;
	protected int fill = Alignment.FILL_HORIZONTAL;
	protected int colWeight = 0;
	protected int rowWeight = 0;

	public BasicGridLayout() {
		this(0, 1, 2, 2);
	}
	public BasicGridLayout(int rows, int cols) {
		this(rows, cols, 2, 2);
	}
	public BasicGridLayout(int rows, int cols, int hGap, int vGap) {
		this(rows, cols, hGap, vGap, 0, 0);
	}
	public BasicGridLayout(int irows, int icols, int ihGap, int ivGap, int ihMargin, int ivMargin) {
		this.reqRows = rows = irows;
		this.reqCols = cols = icols;
		this.hGap = ihGap;
		this.vGap = ivGap;
		this.hMargin = ihMargin;
		this.vMargin = ivMargin;
	}
	/**
	 * Override this to set alignment on a per-component basis.
	 */
	protected int alignmentFor(Component c, int row, int col) {
		return alignment;
	}
	protected void calcCellSizes(Container target, int type) {
		int i;
		int count = target.getComponentCount();

		if (reqCols <= 0) {
			rows = reqRows;
			cols = (count+reqRows-1)/reqRows;
		} else {
			rows = (count+reqCols-1)/reqCols;
			cols = reqCols;
		}
		
		colWidths = new int[cols];
		for (i = 0; i < cols; i++)
			colWidths[i] = 0;
		rowHeights = new int[rows];
		for (i = 0; i < rows; i++)
			rowHeights[i] = 0;

		int index = 0;
		for (i = 0; i < count; i++) {
			Component c = target.getComponent(i);
			if (includeComponent(c)) {
				Dimension size = getComponentSize(c, type);
				int row = index / cols;
				int col = index % cols;
				if (colWidths[col] < size.width)
					colWidths[col] = size.width;
				if (rowHeights[row] < size.height)
					rowHeights[row] = size.height;
				index++;
			}
		}

		Dimension size = target.getSize();
		Insets insets = target.getInsets();
		int totalWeight, totalSize, remainder;

		size.width -= insets.left+insets.right+2*hMargin;
		size.height -= insets.top+insets.bottom+2*vMargin;
		totalWeight = totalSize = 0;
		for (i = 0; i < cols; i++) {
			totalWeight += getColWeight(i);
			totalSize += colWidths[i];
			if (i != 0)
				totalSize += hGap;
		}
		if (totalWeight != 0 && totalSize < size.width) {
			remainder = size.width - totalSize;
			for (i = 0; i < cols; i++)
				colWidths[i] += remainder * getColWeight(i) / totalWeight;
		}

		totalWeight = totalSize = 0;
		for (i = 0; i < rows; i++) {
			totalWeight += getRowWeight(i);
			totalSize += rowHeights[i];
			if (i != 0)
				totalSize += vGap;
		}
		if (totalWeight != 0 && totalSize < size.height) {
			remainder = size.height - totalSize;
			for (i = 0; i < rows; i++)
				rowHeights[i] += remainder * getRowWeight(i) / totalWeight;
		}
	}
	/**
	 * Override this to set fill on a per-component basis.
	 */
	protected int fillFor(Component c, int row, int col) {
		return fill;
	}
	public int getAlignment() {
		return alignment;
	}
	public int getColumns() {
		return cols;
	}
	public int getColWeight() {
		return colWeight;
	}
	/**
	 * Override this to set weights on a per-column basis.
	 */
	protected int getColWeight(int col) {
		return getColWeight();
	}
	public int getFill() {
		return fill;
	}
/**
 * Insert the method's description here.
 * Creation date: (4/20/2001 6:32:00 PM)
 * @return int
 */
public int getHGap() {
	return hGap;
}
	public int getRows() {
		return rows;
	}
	public int getRowWeight() {
		return rowWeight;
	}
	/**
	 * Override this to set weights on a per-row basis.
	 */
	protected int getRowWeight(int row) {
		return getRowWeight();
	}
/**
 * Insert the method's description here.
 * Creation date: (4/20/2001 6:32:00 PM)
 * @return int
 */
public int getVGap() {
	return vGap;
}
	public void measureLayout(Container target, Dimension dimension, int type)  {
		if (dimension != null) {
			calcCellSizes(target, type);
			dimension.width = sumArray(colWidths, hGap, cols);
			dimension.height = sumArray(rowHeights, vGap, rows);
			rowHeights = colWidths = null;
		} else {
			int count = target.getComponentCount();
			if (count > 0) {
				Insets insets = target.getInsets();
				Dimension size = target.getSize();
				int index = 0;

				calcCellSizes(target, type);

				for (int i = 0; i < count; i++) {
					Component c = target.getComponent(i);
					if (includeComponent(c)) {
						Dimension d = getComponentSize(c, type);
						Rectangle r = new Rectangle(0, 0, d.width, d.height);
						int row = index / cols;
						int col = index % cols;
						int x, y;

						x = insets.left+sumArray(colWidths, hGap, col)+hMargin;
						y = insets.top+sumArray(rowHeights, vGap, row)+vMargin;
						if (col > 0)
							x += hGap;
						if (row > 0)
							y += vGap;
						Rectangle cell = new Rectangle(x, y, colWidths[col], rowHeights[row]);
						Alignment.alignInCell(r, cell, alignmentFor(c, row, col), fillFor(c, row, col));
						c.setBounds(r.x, r.y, r.width, r.height);
						index++;
					}
				}
			}
			rowHeights = colWidths = null;
		}
	}
	public void setAlignment(int a) {
		alignment = a;
	}
	public void setColumns(int icols) {
		if (cols < 1)
			cols = 1;
		this.cols = reqCols = icols;
	}
	public void setColWeight(int colWeight) {
		this.colWeight = colWeight;
	}
	public void setFill(int f) {
		fill = f;
	}
/**
 * Insert the method's description here.
 * Creation date: (4/20/2001 6:32:00 PM)
 * @param newHGap int
 */
public void setHGap(int newHGap) {
	hGap = newHGap;
}
	public void setRows(int irows) {
		if (rows < 1)
			rows = 1;
		this.rows = reqRows = irows;
	}
	public void setRowWeight(int rowWeight) {
		this.rowWeight = rowWeight;
	}
/**
 * Insert the method's description here.
 * Creation date: (4/20/2001 6:32:00 PM)
 * @param newVGap int
 */
public void setVGap(int newVGap) {
	vGap = newVGap;
}
	protected int sumArray(int[] array, int spacing, int size) {
		int i, total = 0;

		for (i = 0; i < size; i++)
			total += array[i];
		if (size > 1)
			total += (size-1) * spacing;
		return total;
	}
}
