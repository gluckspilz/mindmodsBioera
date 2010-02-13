/* LEDDisplay.java v .1 mmods

 */

package bioera.processing.impl;

import java.io.*;
import java.util.*;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.graph.designer.*;
import bioera.properties.*;
import bioera.fft.*;
import bioera.graph.chart.*;
import java.awt.*;

/**
 * @author: mmods
 */
public final class LEDDisplay extends Display {
        public FloatCompoundScale amplitudeRange;
        public boolean showChartFrame = true;
        //public ComboProperty shape = new ComboProperty(new String[] {"rectangle", "circle", "triangle"});
        private final static String propertiesDescriptions[][] = {
//		{"", "", ""},
        };
        private BufferedScalarPipe in1;
        private String Phexy = "";
        private BoxChart chart;
        private int digiRange, b1[];
        private int chartHalf, chartRange;
/**
 * VectorDisplay constructor comment.
 */
public LEDDisplay() {
        super();
        setName("Orb");
        in1 = (BufferedScalarPipe) inputs[0];
        in1.setName("IDX");
        b1 = in1.getBuffer();
        amplitudeRange = new FloatCompoundScale(100, new SmallVerticalScale(this));

}
/**
 * Element constructor comment.
 */
public Chart createChart() {
        return chart = new BoxChart();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() throws Exception {
        return "Displays graphic box of size depended on input value";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
        return 1;
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
public final void process() {
        int n1 = in1.available();
        //int r = b1[0];

        if(b1[11] != 0){  //we have a full packet if there's a checksum
            /*
            for(int i =0;i<12;i++){
                System.out.print(b1[i]+" ");
            }
            */

           int Idx = (b1[2]);
           int ledRed = ((b1[3])>>2)*8;
           int ledGrn = ((b1[4])>>2)*8;
           int ledBlu = ((b1[5])>>2)*8;

           //String hexy = new String("#"+ledRed+ledGrn+ledBlu);
           Color orbColor = new Color(ledRed, ledGrn, ledBlu, 100);
           chart.color = orbColor;
           //chart.color = java.awt.Color.decode(hexy);
           chart.pushCenter(chartHalf * chartRange / digiRange + 30);


           System.out.print("Index = "+Idx+" Red = "+ledRed+" Green = "+ledGrn+" Blue = "+ledBlu);
           System.out.println();
           b1[11] = 0;in1.purgeAll();
        }




        /*
        String rd = java.lang.Integer.toHexString(r);
        String gr = java.lang.Integer.toHexString(g);
        String bl = java.lang.Integer.toHexString(b);
        r= r*2;g=g*2;b=b*2;
        if (rd.length() < 2){
             rd = rd.concat("0");
        }
        if (gr.length() < 2){
             gr = gr.concat("0");
        }
        if (bl.length() < 2){
             bl = bl.concat("0");
        }
        String hexy = new String(("#"+rd+gr+bl).toUpperCase());
        if (hexy.hashCode() == Phexy.hashCode()){
            in1.purgeAll();in2.purgeAll();in3.purgeAll();
            return;
        }*/



        chart.repaint();

        //in1.purgeAll();
        //Phexy = hexy;
}

/**
 * Element constructor comment.
 */


public void reinit() throws Exception {
        if (((SmallVerticalScale)(amplitudeRange.scale)).getSelectedIndex() == -1) {
                ((SmallVerticalScale)(amplitudeRange.scale)).setSelectedItemThrow("%");
        }

        predecessorElement = getFirstElementConnectedToInput(0);
        if (predecessorElement == null) {
                setReinited(true);
                return;
        } else if (!predecessorElement.isReinited()) {
                // Wait until the preceding element is inited
                return;
        }

//	chart.color = (java.awt.Color.magenta);



        chart.type = 1; //it's a circle!
        chart.showFrame = showChartFrame;

        amplitudeRange.scale.update(this);
        chartRange = Math.min(chart.getChartHeight(), chart.getChartWidth()) / 2;
        digiRange = (int) amplitudeRange.getChartDigiRange();
        chartHalf = (int)(chartRange * amplitudeRange.getChartRangeRatio());


        //System.out.println("chartHalf="+chartHalf);
        //System.out.println("digiRange="+digiRange);
        //System.out.println("getChartHeight()="+chart.getChartHeight());
        //System.out.println("getChartWidth()="+chart.getChartWidth());


        super.reinit();
}
}
