/* DynOscilloscopeDisplay.java
Element for displaying saved sessions

 */

package bioera.processing.impl;


import java.io.*;
import java.util.*;

import bioera.Main;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.graph.chart.*;
import bioera.properties.*;


public final class DynOscilloscopeDisplay extends Display {
	public float showSeconds = 50;
	//private FloatCompoundUnitScale amplitudeRange;
	public boolean highPrecision = true;
	//public boolean joinPoints = true;
	public boolean reflectTimeRange = true;
	public boolean grid = true;
        int lastMatrixMode = 0;
        boolean secondDual = false;
        byte fileByteMatrix[][];// = Main.app.runtimeFrame.fileByteMatrix;
        int fileByteMatrixSize[][];// = Main.app.runtimeFrame.fileByteMatrixSize;
        int numberofSessions;// = Main.app.runtimeFrame.numberofSessions;

	private final static String propertiesDescriptions[][] = {/*
		{"highPrecision", "High precision", ""},
		{"reflectTimeRange", "Reflect time range", ""},
		{"showSeconds", "Time range [s]", ""},
		{"amplitudeRange", "Amplitude range", ""},
		{"joinPoints", "Join points", ""}, */
	};




	int b1[], b2[], b3[], b4[], b5[], lastMM = Integer.MIN_VALUE, min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
	int counter, mean;
	int chartRange, chartWidth;
	private HorizontalScalarChart chart;
	private int digiRange;
	private int chartLevel;//, amax;
	private BufferedScalarPipe in1,in2,in3,in4,in5;
        boolean gotData = false;
        boolean firstrun = true;
        boolean redrawchart = false;
        private int databufferIndex = 0;
        private int amplitudehigh = 500;
        private int amplitudelow = 0;
        private int mode = -1;
        private int probeconf = -1;
        private int sessionseconds = -1;
        private int samplecounter = 0;
        private int increment = 0;
        private int samplerate;
        private int sensitivity = 0;
        private int x = 0;
        int sessFileIndex = 0;
        int sessFiledpkt[] = new int[16];


        private final static int HEADER_1 = 0xa3; // always - decimal 163
        private final static int HEADER_2_16ds = 0x48; // 16bit datastream - decimal  72
        private final static int HDRSlTrgt = 0x49; // Single Probe Target Params
        private final static int HDRDlTrgt = 0x4A; // Dual Probe Target Params
        private final static int HDRSesDtls = 0x4B; // Start of session details
        private final static int HDREndses = 0x4C; // End of session data
        int dataMax =0;
        int dataMin =0;
        int matrixIndex = 0;
        byte matrixMode[] = new byte[30];
        byte matrixDataChannel[]  = new byte[30];
        boolean matrixGSR[] = new boolean[30];
        boolean dualMode = false;
        int matrixSeconds[] = new int[30];
        long matrixSessionDate[] = new long[30];
        int matrixStartSens[] = new int[30];
        int matrixFinSens[] = new int[30];
        int matrixFinRunIdx[] = new int[30];
        int matrixStartSensIdx[] = new int[30];
        int matrixFinSensIdx[] = new int[30];
        byte matrixData[][] = new byte[30][24576 * 16];
        int matrixDataSize[] = new int[30];
        int matrixDataMin[] = new int[30];
        int matrixDataMax[] = new int[30];
        int matrixSize = 0;
        int dataIndex = 0;
        boolean finishParse = false;
        boolean chartScaled = false;
        boolean finishScale = false;
        boolean chartDrawn = false;

	protected static boolean debug = bioera.Debugger.get("impl.DynOscilloscopeDisplay");
/**
 * SignalDisplay constructor comment.
 */
public DynOscilloscopeDisplay() {
	super();
	setName("DynOsc");
	in1 = (BufferedScalarPipe)inputs[0];
        in2 = (BufferedScalarPipe)inputs[1];
        in3 = (BufferedScalarPipe)inputs[2];
        in4 = (BufferedScalarPipe)inputs[3];
        in5 = (BufferedScalarPipe)inputs[4];
	in1.setName("IN1");
        in2.setName("IN2");
        in3.setName("SES");
        in4.setName("TIME");
        in5.setName("SESDATA");
	b1 = in1.getBuffer();
        b2 = in2.getBuffer();
        b3 = in3.getBuffer();
        b4 = in4.getBuffer();
        b5 = in5.getBuffer();


}
/**
 * Element constructor comment.
 */
public Chart createChart() {
	return chart = new HorizontalScalarChart();
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Displays linear stream data on graphic chart";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 5;
}
/**
 * Element constructor comment.
 */
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
/**
 * SignalDisplay constructor comment.
 */
public String printAdvancedDetails() throws Exception {
	return super.printAdvancedDetails()
	+ ">counter=" + counter + "\n"
	+ ">chartHeight=" + chart.getChartHeight() + "\n"
	+ ">chartWidth=" + chart.getChartWidth() + "\n"
	+ ">chart.valueIncrement=" + chart.valueIncrement + "\n";
}






private int rCalc(int n) {
    double r;
    Integer N = new Integer(n);
    r = (( (N.doubleValue()*100000)/(65535-N.doubleValue()) )/1000);
    r = Math.round(r);
    Double R = new Double(r);
    int p = R.intValue();
    return p;
}

private int tCalc(int n) {
    double k;
    int i=0;
    double tempd=0;
    int tempi=0;
    Integer N = new Integer(n);
    k = (( (N.doubleValue()*100000)/(65535-N.doubleValue()) /1000 ));
    while (Main.app.tlookup[i]>k){i++;}
    tempd = ( (i+10) + ( ( (Main.app.tlookup[i])-k) / (Main.app.tlookup[i] - Main.app.tlookup[i+1]))   );
    tempd = tempd*100;
    Double T = new Double(tempd);
    tempi = T.intValue();
    return tempi;
}



public final void process() {






fileByteMatrix = Main.app.fileByteMatrix;
fileByteMatrixSize = Main.app.fileByteMatrixSize;
numberofSessions = Main.app.numberofSessions;
dataIndex = 0;
dataMax = 0;
dataMin = 65536;
for(int i =0;i<matrixDataMin.length;i++){
    matrixDataMin[i]=65536;
}


for (int i=0; i < numberofSessions; i++){ //grab data for each session
    dataIndex=0;
    dualMode=false;

    matrixMode[matrixIndex] = fileByteMatrix[i][2];

        if (matrixMode[matrixIndex]==2){  //establish if session is single/dual
            dualMode=true;
            matrixMode[matrixIndex+1] = fileByteMatrix[i][2];  //matrixMode needs to be set for the second matrix too if dual
        }
        else{dualMode=false;}
        if (dualMode==true){  //if dual grab length & starting sensitivity index, for both datachans (and filedate)

            if(matrixIndex<1){
                matrixSessionDate[matrixIndex] = Main.app.sessionFileDate[matrixIndex];
                matrixSessionDate[matrixIndex+1] = Main.app.sessionFileDate[matrixIndex];
            }
            else{
                matrixSessionDate[matrixIndex] = Main.app.sessionFileDate[ (matrixIndex/2)   ];  //get date for key
                matrixSessionDate[matrixIndex+1] = Main.app.sessionFileDate[  matrixIndex/2 ];
            }

            matrixSeconds[matrixIndex]= (fileByteMatrix[i][5] * 256) + fileByteMatrix[i][6];
            //System.out.println("matrixlength matrixindex= "+matrixSeconds[matrixIndex]);
            matrixSeconds[matrixIndex+1]= (fileByteMatrix[i][5] * 256) + fileByteMatrix[i][6];
            //System.out.println("matrixlength matrixindex+1= "+ matrixSeconds[matrixIndex+1]);
            matrixStartSensIdx[matrixIndex] = (fileByteMatrix[i][4]);
            //System.out.println("matrixStartSensIdx[matrixIndex]= "+ matrixStartSensIdx[matrixIndex]);
            matrixStartSensIdx[matrixIndex+1] = (fileByteMatrix[i][4]);
            //System.out.println("matrixStartSensIdx[matrixIndex+1]= "+ matrixStartSensIdx[matrixIndex+1]);
            if (fileByteMatrix[i][3] == 3){  //if probeconf is dualGSR:  set matrixdatachannel and set matrixgsr to true
                matrixDataChannel[matrixIndex] = 1;
                //System.out.println("matrixDataChannel[matrixIndex]= "+ matrixDataChannel[matrixIndex]);
                matrixDataChannel[matrixIndex+1] = 3;
                //System.out.println("matrixDataChannel[matrixIndex+1]= "+ matrixDataChannel[matrixIndex+1]);
                matrixGSR[matrixIndex] = true;
                matrixGSR[matrixIndex+1] = true;
            }
            else{  //if probeconf is dual temp:
                matrixDataChannel[matrixIndex] = 2;  //if probeconf is dualTemp:  set matrixdatachannel and set matrixgsr to true
                //System.out.println("matrixDataChannel[matrixIndex]= "+ matrixDataChannel[matrixIndex]);
                matrixDataChannel[matrixIndex+1] = 4;
                //System.out.println("matrixDataChannel[matrixIndex+1]= "+ matrixDataChannel[matrixIndex+1]);
                matrixGSR[matrixIndex] = false;
                matrixGSR[matrixIndex+1] = false;
            }
        }
        else{ //if single grab length & starting sensitivity index for session (1 datachan)

            matrixSessionDate[matrixIndex] = Main.app.sessionFileDate[matrixIndex];
            matrixSeconds[matrixIndex]= (fileByteMatrix[i][5] * 256) + fileByteMatrix[i][6];
            //System.out.println("matrixLength[matrixIndex]= "+ matrixSeconds[matrixIndex]);
            matrixStartSensIdx[matrixIndex] = (fileByteMatrix[i][4]);
            //System.out.println("matrixStartSensIdx[matrixIndex]= "+ matrixStartSensIdx[matrixIndex]);
            matrixDataChannel[matrixIndex] = fileByteMatrix[i][3];  //
            //System.out.println("matrixDataChannel[matrixIndex]= "+ matrixDataChannel[matrixIndex]);
            if ((matrixDataChannel[matrixIndex] == 1) || (matrixDataChannel[matrixIndex] ==2)){  //if probeconf is gsr1 or gsr2 then matrixGSR to true
                matrixGSR[matrixIndex]=true;
                //System.out.println("matrixGSR[matrixIndex]= "+ matrixGSR[matrixIndex]);
            }
            else{ //if probeconf is anything else it is temp
                matrixGSR[matrixIndex]=false;
                //System.out.println("matrixGSR[matrixIndex]= "+ matrixGSR[matrixIndex]);
            }
        }


        //System.out.println(fileByteMatrixSize[i][0]-48);
        for (int j =16;j<(fileByteMatrixSize[i][0]-48);j=j+16){  //get data from datastream packets only from session file
            int tempo= 0;
            if (dualMode==true){  //grab data for two datachans if dual (j is byte number in packet)
                matrixData[matrixIndex][dataIndex] = (fileByteMatrix[i][j+(2*matrixDataChannel[matrixIndex])]);  //put first byte of data into matrixData
                matrixData[matrixIndex][dataIndex+1] = (fileByteMatrix[i][j+1+(2*matrixDataChannel[matrixIndex])]); //put second byte of data info matrixData
                tempo = (((matrixData[matrixIndex][dataIndex])&0xff)<<8) + ((matrixData[matrixIndex][dataIndex+1])&0xff); //use tempo to find max&min for all data
                if ( tempo > dataMax ){
                    dataMax = tempo;
                }
                if (tempo < dataMin ){
                    dataMin = tempo;
                }
                //same for matrixdatamin/max
                if ( tempo > matrixDataMax[matrixIndex] ){
                    matrixDataMax[matrixIndex] = tempo;
                }
                if (tempo < matrixDataMin[matrixIndex] ){
                    matrixDataMin[matrixIndex] = tempo;
                }
                matrixData[matrixIndex][dataIndex+2] = (fileByteMatrix[i][j+12]);  //put timestamp into matrix data
                matrixData[matrixIndex][dataIndex+3] = (fileByteMatrix[i][j+13]);  //put timestamp into matrix data
                matrixData[matrixIndex][dataIndex+4] = (fileByteMatrix[i][j+14]);  //put timestamp into matrix data
                matrixData[matrixIndex][dataIndex+5] = (fileByteMatrix[i][j+15]);  //put timestamp into matrix data
                //for(int z=0;z<2;z++){System.out.print("|matrixData[matrixIndex][dataIndex+z] = "+(matrixData[matrixIndex][dataIndex+z] & 0xff )  )  ;}
                //System.out.println();
                matrixData[matrixIndex+1][dataIndex] = (fileByteMatrix[i][j+(2*matrixDataChannel[matrixIndex+1])]); //put first byte of 2nd chan into matrixData
                matrixData[matrixIndex+1][dataIndex+1] = (fileByteMatrix[i][j+1+(2*matrixDataChannel[matrixIndex+1])]); //put second byte of 2nd chan into matrixData
                tempo = (((matrixData[matrixIndex+1][dataIndex])&0xff)<<8) + ((matrixData[matrixIndex+1][dataIndex+1])&0xff);  //use tempo to find max&min for all data
                if ( tempo > dataMax ){
                    dataMax = tempo;
                }
                if (tempo < dataMin ){
                    dataMin = tempo;
                }
                //same for matrixdatamin/max
                if ( tempo > matrixDataMax[matrixIndex+1] ){
                    matrixDataMax[matrixIndex+1] = tempo;
                }
                if (tempo < matrixDataMin[matrixIndex+1] ){
                    matrixDataMin[matrixIndex+1] = tempo;
                }
                matrixData[matrixIndex+1][dataIndex+2] = (fileByteMatrix[i][j+12]);  //put timestamp into matrix data
                matrixData[matrixIndex+1][dataIndex+3] = (fileByteMatrix[i][j+13]);  //put timestamp into matrix data
                matrixData[matrixIndex+1][dataIndex+4] = (fileByteMatrix[i][j+14]);  //put timestamp into matrix data
                matrixData[matrixIndex+1][dataIndex+5] = (fileByteMatrix[i][j+15]);  //put timestamp into matrix data
                //for(int z=0;z<2;z++){System.out.print("|matrixData[matrixIndex+1][dataIndex+z] = "+(matrixData[matrixIndex+1][dataIndex+z] & 0xff ));}
                //System.out.println();
            }
            else{ //dualMode==false
                matrixData[matrixIndex][dataIndex] = (fileByteMatrix[i][j+1+matrixDataChannel[matrixIndex]]);  //put first byte of data into matrixData
                matrixData[matrixIndex][dataIndex+1] = (fileByteMatrix[i][j+2+matrixDataChannel[matrixIndex]]);  //put second byte of data into matrixData
                tempo = (((matrixData[matrixIndex][dataIndex])&0xff)<<8) + ((matrixData[matrixIndex][dataIndex+1])&0xff);  //use tempo to find max&min for all data
                if ( tempo > dataMax ){
                    dataMax = tempo;
                }
                if (tempo < dataMin ){
                    dataMin = tempo;
                }
                //same for matrixdatamin/max
                if ( tempo > matrixDataMax[matrixIndex] ){
                    matrixDataMax[matrixIndex] = tempo;
                }
                if (tempo < matrixDataMin[matrixIndex] ){
                    matrixDataMin[matrixIndex] = tempo;
                }
                //System.out.println("tempo = "+tempo);
                //System.out.println("dataMax = "+dataMax +"  and   dataMin = "+dataMin);
                matrixData[matrixIndex][dataIndex+2] = (fileByteMatrix[i][j+12]);  //put timestamp into matrix data
                matrixData[matrixIndex][dataIndex+3] = (fileByteMatrix[i][j+13]);  //put timestamp into matrix data
                matrixData[matrixIndex][dataIndex+4] = (fileByteMatrix[i][j+14]);  //put timestamp into matrix data
                matrixData[matrixIndex][dataIndex+5] = (fileByteMatrix[i][j+15]);  //put timestamp into matrix data
                //for(int z=0;z<2;z++){System.out.print("|matrixData[matrixIndex][dataIndex+z] = "+(matrixData[matrixIndex][dataIndex+z] & 0xff ));}
                //System.out.println("dataIndex = "+dataIndex+"  j = "+j);
            }
            dataIndex=dataIndex+6;
        }
        if (dualMode==true){  //grab data from targets (startsensitivity, finishsensitivity) for both channels
            matrixStartSens[matrixIndex] = (fileByteMatrix[i][fileByteMatrixSize[i][0]-40]*256) + (fileByteMatrix[i][fileByteMatrixSize[i][0]-39]);
            //System.out.println("matrixStartSens[matrixIndex] = "+ matrixStartSens[matrixIndex]);
            matrixFinSens[matrixIndex] = (fileByteMatrix[i][fileByteMatrixSize[i][0]-24]*256) + (fileByteMatrix[i][fileByteMatrixSize[i][0]-23]);
            //System.out.println("matrixFinSens[matrixIndex] = "+ matrixFinSens[matrixIndex]);
            matrixStartSens[matrixIndex+1] = (fileByteMatrix[i][fileByteMatrixSize[i][0]-40]*256) + (fileByteMatrix[i][fileByteMatrixSize[i][0]-39]);
            //System.out.println("matrixStartSens[matrixIndex+1] = "+ matrixStartSens[matrixIndex+1]);
            matrixFinSens[matrixIndex+1] = (fileByteMatrix[i][fileByteMatrixSize[i][0]-24]*256) + (fileByteMatrix[i][fileByteMatrixSize[i][0]-23]);
            //System.out.println("matrixFinSens[matrixIndex+1] = "+ matrixFinSens[matrixIndex+1]);
        }
        else{  //grab data from targets (startsensitivity, finishsensitivity) for one channel
            matrixStartSens[matrixIndex] = (fileByteMatrix[i][fileByteMatrixSize[i][0]-40]*256) + (fileByteMatrix[i][fileByteMatrixSize[i][0]-39]);
            //System.out.println("matrixStartSens[matrixIndex] = "+ matrixStartSens[matrixIndex]);
            matrixFinSens[matrixIndex] = (fileByteMatrix[i][fileByteMatrixSize[i][0]-24]*256) + (fileByteMatrix[i][fileByteMatrixSize[i][0]-23]);
            //System.out.println("matrixFinSens[matrixIndex] = "+ matrixFinSens[matrixIndex]);
        }

        if (dualMode==true){  //grab data from end of session packet for both channels
            matrixFinSensIdx[matrixIndex] = (fileByteMatrix[i][fileByteMatrixSize[i][0]-13]);
            //System.out.println("matrixFinSensIdx[matrixIndex] = "+ matrixFinSensIdx[matrixIndex]);
            matrixFinSensIdx[matrixIndex+1] = (fileByteMatrix[i][fileByteMatrixSize[i][0]-13]);
            //System.out.println("matrixFinSensIdx[matrixIndex+1] = "+ matrixFinSensIdx[matrixIndex+1]);
            matrixFinRunIdx[matrixIndex] = (fileByteMatrix[i][fileByteMatrixSize[i][0]-14]);
            //System.out.println("matrixFinRunIdx[matrixIndex] = "+ matrixFinRunIdx[matrixIndex]);
            matrixFinRunIdx[matrixIndex+1] = (fileByteMatrix[i][fileByteMatrixSize[i][0]-14]);
            //System.out.println("matrixFinRunIdx[matrixIndex+1] = "+ matrixFinRunIdx[matrixIndex+1]);
        }
        else {  //grab data from end of session packet for one channel
            matrixFinSensIdx[matrixIndex] = (fileByteMatrix[i][fileByteMatrixSize[i][0]-13]);
            //System.out.println("matrixFinSensIdx[matrixIndex] = "+ matrixFinSensIdx[matrixIndex]);
            matrixFinRunIdx[matrixIndex] = (fileByteMatrix[i][fileByteMatrixSize[i][0]-14]);
            //System.out.println("matrixFinRunIdx[matrixIndex] = "+ matrixFinRunIdx[matrixIndex]);
        }


        if (dualMode==true){
            matrixDataSize[matrixIndex] = dataIndex; //set number of bytes of 1st datachan to matrixDataSize
            matrixDataSize[matrixIndex+1] = dataIndex; //set number of bytes of 2nd datachan to matrixDataSize
            matrixIndex = matrixIndex+2;
            //System.out.println("matrixIndex = "+ matrixIndex);
        }
        else{
            matrixDataSize[matrixIndex] = dataIndex; //set number of bytes of 1st datachan to matrixDataSize
            matrixIndex = matrixIndex+1;
            //System.out.println("matrixIndex = "+ matrixIndex);
        }
        matrixSize=matrixIndex;
        //System.out.println("matrixSize = "+ matrixSize);

}

    finishParse=true;








    //ensure all f's are of same probe type
     boolean isGSR;
     isGSR = matrixGSR[0];
     for (int i = 1; i < matrixSize; i++) {
         if (matrixGSR[i] != isGSR) {
             Main.app.runtimeFrame.errorDialog(100);
         }
     }

     //determine max sessionseconds size
     sessionseconds = matrixSeconds[0];

     for (int i = 1; i < matrixSize; i++) {
         if (matrixSeconds[i] > sessionseconds) {
             sessionseconds = matrixSeconds[i];
         }
     }

     //determine max sensitivity
     sensitivity=matrixFinSens[0];
     for (int i = 1; i < matrixSize; i++) {
         if (matrixFinSens[i] > sensitivity) {
             sensitivity = matrixFinSens[i];
         }
     }


     //rescale chart for incoming datastreams
     double factor = 1.1;
     chart.yMaxValue = (rCalc(dataMax))*factor;  //conv datamax to kohms and set charts ymax
     chart.yMinValue = (rCalc(dataMin))/factor;  //conv datamin to kohms and set chargs ymin
     amplitudehigh = (int)chart.yMaxValue;
     amplitudelow = (int)chart.yMinValue;
     chartRange = chart.getChartHeight();
     chartWidth = chart.getChartWidth();
     chart.xMinSpace = 15;
     chart.xMinValue = 0;
     chart.xMaxValue = sessionseconds;  //set charts xmax using session length
     chart.xUnit = "s";
     double ratio = chart.rescaleX();
     chart.rescaleY();

     chart.resetChart();
     chart.repaint();
     finishScale =true;

     Main.app.sessionkeyframe.numofSessions=matrixSize; //tell key number of sessions

     //draw each datastream onto chart
     for (int i=0;i< matrixSize; i++){  //for each datastream
         boolean firstChartPixel = true;
         //System.out.println("matrixDataSize[i] = "+matrixDataSize[i] +"  and i=  "+i);
       for (int j=0;j < matrixDataSize[i];j=j+6){  //for length of datastream | each packet of data stream is 6 bytes (low, high, time3, time2, time1, time0)


           //System.out.println(  "rcalcd = "+rCalc ( ( ( matrixData[i][j]& 0xff)*256 ) + (matrixData[i][j+1] & 0xff) )  +" j *256 ="+ (matrixData[i][j]& 0xff)*256 +" j+1 =" + (matrixData[i][j+1])    );
           //System.out.println( ((matrixData[i][j+2] & 0xff)*16777216) + ((matrixData[i][j+3]& 0xff)*65536) + ((matrixData[i][j+4]& 0xff)*256) + (matrixData[i][j+5]& 0xff)   );

           int n1 = rCalc ( ( ( matrixData[i][j]& 0xff)*256 ) + (matrixData[i][j+1] & 0xff) ) ;
           float var1 = ( chartRange * (n1 - amplitudelow) / (amplitudehigh-amplitudelow ) );
           float elapsed = ((matrixData[i][j+2] & 0xff)*16777216) + ((matrixData[i][j+3]& 0xff)*65536) + ((matrixData[i][j+4]& 0xff)*256) + (matrixData[i][j+5]& 0xff);
           float slen = sessionseconds*1000;
           float xxxx = elapsed/slen;
           float xxxxx = chartWidth*xxxx;

           //System.out.println("amplitudehigh = "+amplitudehigh+" amplitudelow= "+amplitudelow+" chartRange= "+chartRange);
           //System.out.println("chartwidth = "+ chartWidth + " slen = "+slen + " sessionseconds ="+sessionseconds);
           //System.out.println("elapsed = "+elapsed+" n1 ="+n1);
           //System.out.println("xxxxx = "+(int)xxxxx+" var1= "+var1);

           while (!chart.isInitialized()){}  //wait until chart re-scales/redraws  (move right before datastream draws on chart)

           if (firstChartPixel == true){
               chart.pushStreams((int)var1, (int)xxxxx, i+1, true, matrixMode[i]);
               firstChartPixel = false;
           }
           else {
               chart.pushStreams((int)var1, (int)xxxxx, i+1, false,  matrixMode[i]);
           }
           chart.repaint();

           //System.out.println("packet data of stream "+i+"= "+    rCalc(  (matrixData[i][j] << 8) + (matrixData[i][j+1])  )     );
           //float elapsedf = 0;
           //elapsedf = ( ((matrixData[i][j+2]) << 24) + ((matrixData[i][j+3]) << 16 ) + ((matrixData[i][j+4]) << 8 ) + (matrixData[i][j+5]) );
           //System.out.println("packet time of stream "+i+"= "+elapsedf  );

                  //calculate for keyDataObj

       }
       //fill keyDataObj for each stream


       //for determining color
       if (  (lastMatrixMode == 2) && (matrixMode[i] == 2)  ){    secondDual=true;   }  else{ secondDual=false;  }
       //set color
       if(secondDual==false){
           Main.app.sessionkeyframe.keyDataObj[i][0] = makeObj("c"+(i+1)); }
       else{
           Main.app.sessionkeyframe.keyDataObj[i][0] = makeObj("c"+(i)+"b"); }

       //set dateF
       Main.app.sessionkeyframe.keyDataObj[i][1] = makeObj(String.valueOf(matrixSessionDate[i]));
       //set ModeF
       Main.app.sessionkeyframe.keyDataObj[i][2] = makeObj(String.valueOf(matrixMode[i]));
       //set StSenF
       Main.app.sessionkeyframe.keyDataObj[i][3] = makeObj(String.valueOf(matrixStartSens[i]));
       //set EndSenF
       Main.app.sessionkeyframe.keyDataObj[i][4] = makeObj(String.valueOf(matrixFinSens[i]));
       //set travF
       int trav = matrixFinSensIdx[i] - matrixStartSensIdx[i];
       Main.app.sessionkeyframe.keyDataObj[i][5] = makeObj(String.valueOf(trav));
       //getLowF
       int thislow = 0;
       if (matrixDataMin[i]==65536){Main.app.sessionkeyframe.keyDataObj[i][6] = "";
       }else{
           thislow = rCalc(matrixDataMin[i]);
           Main.app.sessionkeyframe.keyDataObj[i][6] = makeObj(String.valueOf(thislow));
       }
       //getHighF
       int thishigh = rCalc(matrixDataMax[i]);
       Main.app.sessionkeyframe.keyDataObj[i][7] = makeObj(String.valueOf(thishigh));
       //getAvgF
       int thistotal = 0;
       int thisaverage = 0;
       for (int j=0;j < matrixDataSize[i];j=j+6){
           thistotal = thistotal + ( ((matrixData[i][j]& 0xff)*256) + (matrixData[i][j+1] & 0xff) ) ;
       }
       int numOfPacks = matrixDataSize[i]/6;
       thisaverage = (thistotal / numOfPacks);
       Main.app.sessionkeyframe.keyDataObj[i][8] = makeObj(String.valueOf(rCalc(thisaverage)));
       //getprobetype
       //matrixGSR[0];  keyDataObj[0][9]
       if (matrixGSR[i] == true){
           if (secondDual == false){
               Main.app.sessionkeyframe.keyDataObj[i][9] = makeObj(String.valueOf("GSR1"));
           }
           else{
               Main.app.sessionkeyframe.keyDataObj[i][9] = makeObj(String.valueOf("GSR2"));
           }
       }
       else{
           if (secondDual == false){
               Main.app.sessionkeyframe.keyDataObj[i][9] = makeObj(String.valueOf("TMP1"));
           }
           else{
               Main.app.sessionkeyframe.keyDataObj[i][9] = makeObj(String.valueOf("TMP2"));
           }
       }


       if((secondDual==true) && (matrixMode[i] ==2) ){ lastMatrixMode=0; }
       else{lastMatrixMode = matrixMode[i];}

       secondDual=false;
     }


/*
     matrixData = Main.app.emptyByteMatrix;
     fileByteMatrix = Main.app.emptyByteMatrix;
     fileByteMatrixSize = Main.app.emptyByteMatrixSize;
     matrixDataSize=Main.app.emptymatrix30;matrixFinSensIdx=Main.app.emptymatrix30;matrixFinSensIdx=Main.app.emptymatrix30;matrixStartSensIdx=Main.app.emptymatrix30;
     matrixFinRunIdx=Main.app.emptymatrix30;matrixFinSens=Main.app.emptymatrix30;matrixStartSens=Main.app.emptymatrix30;matrixSeconds=Main.app.emptymatrix30;
     matrixGSR=Main.app.emptyboolean30;matrixDataChannel=Main.app.emptybyte30p;matrixMode=Main.app.emptybyte30p;
*/

/*
     for(int i=0;i<matrixSize;i++){
         System.out.println("matrix mode = "+matrixMode[i]+"  for matrix #"+i);
     }
*/


     gotData = false;firstrun = true;redrawchart = false;chartDrawn = false;chartScaled = false;finishParse = false;dualMode = false;finishScale = false;
     numberofSessions=0;matrixIndex=0;dataMin=65536;dataMax=0;matrixSize=0;counter=0;sessionseconds=0;dataIndex=0;lastMatrixMode=0;secondDual=false;
     chartRange = chart.getChartHeight();

     try{Main.app.sessionkeyframe.populateKey();}
     catch (Exception ex) {Main.app.runtimeFrame.errorDialog(103);};

     Main.app.processor.stopProcessing();
     //Main.app.runtimeFrame.stopMenuItem.doClick();






}


 private Object makeObj(final String item)  {
     return new Object() { public String toString() { return item; } };
   }



/**
 * SignalDisplay constructor comment.
 */
public void reinit() throws Exception {



	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		disactivate("Not connected");
		reinited = true;
		return;
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}



        chart.yMaxValue = amplitudehigh;
        chart.yMinValue = amplitudelow;
	chartRange = chart.getChartHeight();
	lastMM = getSignalParameters().getDigitalZero();
	chart.xMinSpace = 15;
	chart.xMinValue = 0;
	chart.xMaxValue = showSeconds;
	chart.xUnit = "s";
	chart.setXPixelInc(getSignalParameters().getSignalRate());
	chart.grid = this.grid;
	double ratio = chart.rescaleX();
	chart.setXPixelInc(getSignalParameters().getSignalRate() / ratio);
        chart.rescaleY();
	super.reinit();
}
/**
 * Element constructor comment.
 */
public final void start() {

	chart.reset();
        gotData=false;
        firstrun = true;
        redrawchart = false;
        databufferIndex = 0;
        int b1MemBuff[] = new int[4090 * 16];
        int b2MemBuff[] = new int[4090 * 16];
        int elapsedMemBuff[] = new int[4090 * 16];
	lastMM = Integer.MIN_VALUE;
}
}
