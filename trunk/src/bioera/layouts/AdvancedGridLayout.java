/* AdvancedGridLayout.java v 1.0.9   11/6/04 7:15 PM
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

public class AdvancedGridLayout extends BetterGridLayout {

	protected int[] rowWeights, colWeights, colFlags;

	public AdvancedGridLayout() {
		super(0, 1, 2, 2);
	}
	public AdvancedGridLayout(int rows, int cols) {
		super(rows, cols, 2, 2);
	}
	public AdvancedGridLayout(int rows, int cols, int hGap, int vGap) {
		super(rows, cols, hGap, vGap, 0, 0);
	}
	public AdvancedGridLayout(int rows, int cols, int hGap, int vGap, int hMargin, int vMargin) {
		super(rows, cols, hGap, vGap, hMargin, vMargin);
	}
	public void addLayoutComponent (String name, Component comp) {
	}
	protected int alignmentFor(Component c, int row, int col) {
		return getColAlignment(col);
	}
	protected int fillFor(Component c, int row, int col) {
		return fill;
	}
	protected int getColAlignment(int col) {
		if (colFlags != null && col < colFlags.length)
			return colFlags[col];
		return alignment;
	}
	protected int getColWeight(int col) {
		if (colWeights != null && col < colWeights.length)
			return colWeights[col];
		return 0;
	}
	protected int getRowWeight(int row) {
		if (rowWeights != null && row < rowWeights.length)
			return rowWeights[row];
		return 0;
	}
	public void removeLayoutComponent (Component comp) {
	}
	public void setColAlignment(int col, int v) {
		colFlags = setWeight(colFlags, col, v);
	}
	public void setColWeight(int col, int weight) {
		colWeights = setWeight(colWeights, col, weight);
	}
	public void setRowWeight(int row, int weight) {
		rowWeights = setWeight(rowWeights, row, weight);
	}
	private int[] setWeight(int[]w, int index, int weight) {
		if (w == null)
			w = new int[index+1];
		else if (index >= w.length) {
			int[] n = new int[index+1];
			System.arraycopy(w, 0, n, 0, w.length);
			w = n;
		}
		w[index] = weight;
		return w;
	}
	protected int weightForColumn(int col) {
		return 1;
	}
	protected int weightForColumn(int row, int col) {
		return 1;
	}
}
