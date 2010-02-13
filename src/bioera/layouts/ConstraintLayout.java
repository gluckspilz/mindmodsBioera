/* ConstraintLayout.java v 1.0.9   11/6/04 7:15 PM
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
import java.util.*;

class ConstraintLayout implements LayoutManager2 {

	protected final static int PREFERRED = 0;
	protected final static int MINIMUM = 1;
	protected final static int MAXIMUM = 2;

	protected int hMargin = 0;
	protected int vMargin = 0;
	private OTable constraints;
	protected boolean includeInvisible = false;

	public void addLayoutComponent(Component c, Object constraint) {
		setConstraint(c, constraint);
	}
	public void addLayoutComponent(String constraint, Component c) {
		setConstraint(c, constraint);
	}
	public Dimension calcLayoutSize (Container target, int type) {
		Dimension dim = new Dimension(0, 0);
		measureLayout(target, dim, type);
		Insets insets = target.getInsets();
		dim.width += insets.left + insets.right + 2*hMargin;
		dim.height += insets.top + insets.bottom + 2*vMargin;
		return dim;
	}
	protected Dimension getComponentSize(Component c, int type) {
		if (type == MINIMUM)
			return c.getMinimumSize();
		if (type == MAXIMUM)
			return c.getMaximumSize();
		return c.getPreferredSize();
	}
	public Object getConstraint(Component c) {
		if (constraints != null)
			return constraints.get(c);
		return null;
	}
public int getHMargin() {
	return hMargin;
}
	public boolean getIncludeInvisible() {
		return includeInvisible;
	}
	public float getLayoutAlignmentX(Container parent) {
		return 0.5f;
	}
	public float getLayoutAlignmentY(Container parent) {
		return 0.5f;
	}
public int getVMargin() {
	return vMargin;
}
	protected boolean includeComponent(Component c) {
		return includeInvisible || c.isVisible();
	}
	public void invalidateLayout(Container target) {
	}
	public void layoutContainer(Container target)  {
		measureLayout(target, null, PREFERRED);
	}
	public Dimension maximumLayoutSize (Container target) {
		return calcLayoutSize(target, MAXIMUM);
	}
	public void measureLayout(Container target, Dimension dimension, int type)  {
	}
	public Dimension minimumLayoutSize (Container target) {
		return calcLayoutSize(target, MINIMUM);
	}
	public Dimension preferredLayoutSize (Container target) {
		return calcLayoutSize(target, PREFERRED);
	}
	public void removeLayoutComponent(Component c) {
		if (constraints != null)
			constraints.remove(c);
	}
	public void setConstraint(Component c, Object constraint) {
		if (constraint != null) {
			if (constraints == null)
				constraints = new OTable();
			constraints.put(c, constraint);
		} else if (constraints != null)
			constraints.remove(c);
	}
public void setHMargin(int newHMargin) {
	hMargin = newHMargin;
}
	public void setIncludeInvisible(boolean includeInvisible) {
		this.includeInvisible = includeInvisible;
	}
public void setVMargin(int newVMargin) {
	vMargin = newVMargin;
}
}
