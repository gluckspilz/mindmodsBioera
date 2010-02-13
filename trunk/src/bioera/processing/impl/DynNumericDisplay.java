/* DynNumericDisplay.java

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


public final class DynNumericDisplay extends Display {
	public SmallVerticalScale scale;
	public boolean showUnit = true;
	private boolean leftAlign = true;

	private final static String propertiesDescriptions[][] = {
		//{"leftAlign", "Align to left", ""},
	};

	private int b1[], b2[], n1, n2, sum;
	private int computeN = 1;
	private BufferedScalarPipe in1, in2;
	private TextChart chart;
	private int digiRange, physRange;
        private boolean gotses = false;
        private int mode, probeconf;
/**
 * VectorDisplay constructor comment.
 */
public DynNumericDisplay() {
	super();
}
/**
 * Element constructor comment.
 */
public Chart createChart() {
	return chart = new TextChart();
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
	return 2;
}
/**
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
public final void initialize() {
	setName("Numeric");
	in1 = (BufferedScalarPipe) inputs[0];
        in2 = (BufferedScalarPipe) inputs[1];
	in1.setName("IN");
        in2.setName("SES");
	b1 = in1.getBuffer();
        b2 = in2.getBuffer();

	scale = new SmallVerticalScale(this);
//	System.out.println("Scale=" + ProcessingTools.propertiesToString(scale));
}
/**
 * Element constructor comment.
 */
public final void process() {
	int n1 = in1.available();
        int n2 = in2.available();
	if (n1 == 0)
		return;

	//System.out.println("v=" + scale.toScale(b[n-1]) + "  " + b[n-1]);
        if (gotses==false) //get mode & probeconf
        {
            /*for (int i = 0; i < 8; i++){
                int p = b2[i];
                System.out.print(p+" ");}System.out.println(" is the session info");*/
            mode = b2[4];
            probeconf = b2[5];
            gotses=true;
        }



        if((b1[n1-1]>1000000) && (b1[n1-1]<-1000000))chart.pushTextLeft("");
	else
            chart.pushTextLeft(Integer.toString(b1[n1-1] * physRange / digiRange));


	in1.purgeAll();
	in2.purgeAll();

	chart.repaint();

}
/**
 * Element constructor comment.
 */
public void reinit()  throws Exception {
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	if (scale.getSelectedIndex() == -1)
		scale.setSelectedIndex(0);

	if (showUnit)
		chart.unit = scale.getUnitStr();
	else
		chart.unit = null;

	chart.pushText("0");
	chart.alignLeft = leftAlign;

	scale.update(this);

	physRange = (int) scale.calculatePrecisePhysicalRange();
	digiRange = (int)(getSignalParameters().getDigitalRange() * scale.calculatePreciseScaleDigitalRatio());

	//System.out.println("digiRange="+digiRange);
	//System.out.println("physRange="+physRange);

	super.reinit();
}
}
