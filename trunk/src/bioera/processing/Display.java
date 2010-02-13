/* Display.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.processing;

import bioera.graph.runtime.*;
import bioera.graph.chart.*;
import bioera.*;

public abstract class Display extends SingleElement implements DependentOnRepaint{
	Chart dChart;
public Display() {
	super();
	getChart();
}
public void chartRepainted() {
}
protected abstract Chart createChart();
public void destroy() throws Exception {
	dChart.setElement(null);
	dChart = null;

	super.destroy();
}
public Chart getChart() {
	return dChart;
}
public final java.lang.String getOutputName(int index) {
	throw new RuntimeException("Display doesn't have outputs");
}
public int getOutputsCount() {
	return 0;
}
public final Chart newChart() {
	Chart ret = createChart();
	ret.setElement(this);
	ret.setName(getName());
	setChart(ret);
	return ret;
}
public void sendChangePropertyEvent(String fieldName, Object oV) {
	super.sendChangePropertyEvent(fieldName, oV);

	if ("name".equals(fieldName)) {
		//System.out.println("here ");
		if (getChart() != null) {
			//System.out.println("here1 ");
			getChart().setName(name);
			getChart().resetChart();
			//getChart().repaint();
		}
	}
}
public void setChart(Chart d) {
	dChart = d;
}

public boolean loadCustom(bioera.config.XmlConfigSection section) throws Exception {
	dChart = newChart();
	if (dChart != null && section.containsSection("chart_properties")) {
		dChart.load(section.getSection("chart_properties"));
	}

	return false;
}

public boolean saveCustom(bioera.config.XmlCreator section) throws Exception {
	if (dChart != null) {
		dChart.save(section.addSection("chart_properties"));
	}
	return false;
}
}
