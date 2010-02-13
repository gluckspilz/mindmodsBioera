package bioera.graph.tableeditor;

import bioera.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;
import javax.swing.event.*;
import bioera.serial.*;
import bioera.graph.runtime.RuntimeEventHandler;
import com.borland.jbcl.layout.OverlayLayout2;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Font;
import com.borland.jbcl.layout.VerticalFlowLayout;
import java.awt.Dimension;
import com.borland.jbcl.layout.*;



import bioera.ConfigurableSystemSettings;
import java.io.*;
import java.awt.Insets;
import java.util.Hashtable;
import java.text.NumberFormat;
import javax.swing.text.NumberFormatter;
import javax.swing.table.*;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.util.*;


public class TableEditorFrame extends JFrame implements ActionListener, ChangeListener {

    public boolean gotTables = false;
    protected int inb[];
    protected InputStream streamIn;  //new InputStream(Main.app.impl.in);
    protected OutputStream streamOut; //new OutputStream(Main.app.impl.out);
    static final String[] mode1probes = { "GSR1", "TEMP1" };
    static final String[] mode2probes = { "GSR2", "TEMP2" };
    static final String[] mode3probes = { "GSR1+GSR2", "TEMP1+TEMP2" };
    static final String[] mode1SensPercent = { "0", "0", "0", "0", "0", "0", "0", "0" };
    static final String[] mode2SensPercent = { "0", "0", "0", "0", "0", "0", "0", "0" };
    static final String[] mode3SensPercent = { "0", "0", "0", "0", "0", "0", "0", "0" };
    static final int[] sampleRates = { 5, 10, 15, 20 };
    static int mode1Color[][] = new int[15][3];
    static int mode2Color[][] = new int[15][3];
    static int mode3Color[][] = new int[8][3];
    static float mode1Audio[] = new float[16];
    static float mode2Audio[] = new float[16];
    static float mode3Audio[][] = new float[8][2];
    static byte table0WriteByte[] = new byte[64];
    static byte table1WriteByte[] = new byte[64];
    static byte table2WriteByte[] = new byte[64];
    static byte table3WriteByte[] = new byte[64];
    static byte table4WriteByte[] = new byte[64];

    static final String[] tabnames = { "Mode 1","Mode 2","Mode 3", "Sensitivity", "Practice" };
//    Rectangle advBounds = new Rectangle(this.getX(), this.getY(), 800, 770);
    Rectangle advBounds = new Rectangle(this.getX(), this.getY(), 800, 755);
    Rectangle basBounds = new Rectangle(this.getX(), this.getY(), 800, 315);
//    Dimension advDim = new Dimension(800, 915);
    Dimension advDim = new Dimension( 800, 755);
    Dimension basDim = new Dimension(800, 315);;

    TableCellRenderer rend = new MyRenderer();


    JMenuBar jMenuBar1 = new JMenuBar();
    JMenu jMenu1 = new JMenu();
    JMenuItem jMenuItem1 = new JMenuItem();
    JMenu jMenu2 = new JMenu();
    static JRadioButtonMenuItem advancedMenuItem = new JRadioButtonMenuItem();
    JMenu jMenu3 = new JMenu();
    JMenuItem jMenuItem2 = new JMenuItem();
    JPanel readsendjpanel = new JPanel();
    JButton sButton = new JButton();
    TitledBorder titledBorder1 = new TitledBorder("");
    public JButton rButton = new JButton();
    JPanel basicPanel = new JPanel();
    JPanel mode1BasicPanel = new JPanel();
    JPanel mode2BasicPanel = new JPanel();
    JPanel mode3BasicPanel = new JPanel();
    JLabel mode1BasicLabel = new JLabel();
    JLabel mode2BasicLabel = new JLabel();
    JLabel mode3BasicLabel = new JLabel();
    JPanel advancedPanel = new JPanel();
    JPanel mode1AdvPanel = new JPanel();
    JLabel mode1AdvLabel = new JLabel();
    JPanel mode2AdvPanel = new JPanel();
    JLabel mode2AdvLabel = new JLabel();
    JPanel mode3AdvPanel = new JPanel();
    JLabel mode3AdvLabel = new JLabel();
    JPanel mode1BasicTitlePanel = new JPanel();
    JPanel mode1LengthPanel = new JPanel();
    JLabel mode1LengthLabel = new JLabel();
    VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
    VerticalFlowLayout verticalFlowLayout3 = new VerticalFlowLayout();
    //JTextField mode1LengthField = new JTextField();
    static JSlider mode1LengthSlider = new JSlider();
    JLabel mode1SensLabel = new JLabel();
    JPanel mode1SensitivityPanel = new JPanel();
    JPanel Mode2BasicTitlePanel = new JPanel();
    VerticalFlowLayout verticalFlowLayout2 = new VerticalFlowLayout();
    JPanel Mode2LengthPanel = new JPanel();
    JLabel mode2LengthLabel = new JLabel();
    //JTextField mode2LengthField = new JTextField();
    static JSlider mode2LengthSlider = new JSlider();
    JPanel mode2SensitivityPanel = new JPanel();
    JLabel mode2SensLabel = new JLabel();
    JPanel mode3BasicTitlePanel = new JPanel();
    JPanel mode3LengthPanel = new JPanel();
    JLabel mode3LengthLabel = new JLabel();
    static JSlider mode3LengthSlider = new JSlider();
    JPanel mode3SensitivityPanel = new JPanel();
    JLabel mode3SensLabel = new JLabel();
    JLabel advancedTitleLabel = new JLabel();
    VerticalFlowLayout verticalFlowLayout4 = new VerticalFlowLayout();
    JPanel mode1SensorPanel = new JPanel();
    VerticalFlowLayout verticalFlowLayout5 = new VerticalFlowLayout();
    VerticalFlowLayout verticalFlowLayout6 = new VerticalFlowLayout();
    VerticalFlowLayout verticalFlowLayout7 = new VerticalFlowLayout();
    JPanel mode2SensorPanel = new JPanel();
    JPanel mode3SensorPanel = new JPanel();
    JLabel mode1SensorLabel = new JLabel();
    static JComboBox mode1SensorCombo = new JComboBox(mode1probes);
    JLabel mode2SensorLabel = new JLabel();
    static JComboBox mode2SensorCombo = new JComboBox(mode2probes);
    JLabel mode3SensorLabel = new JLabel();
    static JComboBox mode3SensorCombo = new JComboBox(mode3probes);
    JPanel mode1SamplePanel = new JPanel();
    JPanel mode2SamplePanel = new JPanel();
    JPanel mode3SamplePanel = new JPanel();
    JLabel mode1SampleLabel = new JLabel();
    JLabel mode2SampleLabel = new JLabel();
    JLabel mode3SampleLabel = new JLabel();
    GridLayout gridLayout1 = new GridLayout();
    GridLayout gridLayout2 = new GridLayout();
    GridLayout gridLayout3 = new GridLayout();
    GridLayout gridLayout4 = new GridLayout();
    GridLayout gridLayout5 = new GridLayout();
    GridLayout gridLayout6 = new GridLayout();
    GridLayout gridLayout7 = new GridLayout();
    GridLayout gridLayout8 = new GridLayout();
    GridLayout gridLayout9 = new GridLayout();
    GridLayout gridLayout10 = new GridLayout();
    GridLayout gridLayout11 = new GridLayout();
    GridLayout gridLayout12 = new GridLayout();
    static JComboBox mode1SampleCombo = new JComboBox();
    static JComboBox mode2SampleCombo = new JComboBox();
    static JComboBox mode3SampleCombo = new JComboBox();
    JPanel spacerPanel = new JPanel();
    JPanel mode1SensPercPanel = new JPanel();
    JLabel mode1SensPercLabel = new JLabel();
    JTextField mode1SensPercField = new JTextField();
    GridLayout gridLayout13 = new GridLayout();
    JLabel jLabel1 = new JLabel();
    JPanel mode3SensPercPanel = new JPanel();
    JLabel mode3SensPercLabel = new JLabel();
    JTextField mode3SensPercField = new JTextField();
    JPanel mode2SensPercPanel = new JPanel();
    JLabel mode2SensPercLabel = new JLabel();
    JTextField mode2SensPercField = new JTextField();
    GridLayout gridLayout14 = new GridLayout();
    GridLayout gridLayout15 = new GridLayout();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    static JSlider mode1SensSlider = new JSlider();
    static JSlider mode2SensSlider = new JSlider();
    static JSlider mode3SensSlider = new JSlider();
    TitledBorder titledBorder2 = new TitledBorder("");
    JTextField mode1MinutesVal = new JTextField();
    JTextField blankField1 = new JTextField();
    JTextField blankField2 = new JTextField();
    JTextField mode2MinutesVal = new JTextField();
    JTextField mode3MinutesVal = new JTextField();
    JTextField blankField3 = new JTextField();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    JTabbedPane tabbedPane = new JTabbedPane();

    IntegerEditor intEdit = new IntegerEditor(0,31);
    IntegerEditor sensintEdit = new IntegerEditor(0,999);
    DoubleEditor dblEdit = new DoubleEditor(0.0,2499.0);
    DoubleEditor moddblEdit = new DoubleEditor(0.0,39.0);


    //static JTable mode1Table;
    //static JTable mode2Table;
    //static JTable mode3Table;
    //static JTable advSensTable;

    static JTable advSensTable = new JTable(new sensTableModel());
    static JTable mode1Table = new JTable(new mode1TableModel());
    static JTable mode2Table = new JTable(new mode2TableModel());
    static JTable mode3Table = new JTable(new mode3TableModel());


    JScrollPane scrollPaneTab1 = new JScrollPane(mode1Table);
    JScrollPane scrollPaneTab2 = new JScrollPane(mode2Table);
    JScrollPane scrollPaneTab3 = new JScrollPane(mode3Table);
    JScrollPane scrollPaneTab4 = new JScrollPane(advSensTable);
    JPanel closePanel = new JPanel();
    JButton closeButton = new JButton();
    static JLabel commMsgLabel = new JLabel();


    public TableEditorFrame() {
        //super.setDefaultLookAndFeelDecorated(false);
        //advBounds = (this.getX(), this.getY(), 800, 869);
        //basBounds = (this.getX(), this.getY(), 800, 360);


        //Dimension d = getToolkit().getScreenSize();



        setBounds(basBounds);
        setResizable(false);
        getContentPane().setLayout(new GridBagLayout());
        setVisible(false);
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


public void sendTables() {

    byte[] buf0 = new byte[6];
    byte[] buf1 = new byte[6];
    byte[] buf2 = new byte[6];
    byte[] buf3 = new byte[6];
    byte[] buf4 = new byte[6];

    byte[] t0block = new byte[24];
    byte[] t1block = new byte[24];
    byte[] t2block = new byte[26];
    byte[] t3block = new byte[24];
    byte[] t4block = new byte[24];

    int[] eraseTable0cmd = {0xa3, 0x46, 0x07, 0x21, 0x00, 0x01, 0x11};
    int[] eraseTable1cmd = {0xa3, 0x46, 0x07, 0x21, 0x01, 0x01, 0x12};
    int[] eraseTable2cmd = {0xa3, 0x46, 0x07, 0x21, 0x02, 0x01, 0x13};
    int[] eraseTable3cmd = {0xa3, 0x46, 0x07, 0x21, 0x03, 0x01, 0x14};
    int[] eraseTable4cmd = {0xa3, 0x46, 0x07, 0x21, 0x04, 0x01, 0x15};
    streamOut = SerialMain.impl.out;
    streamIn = SerialMain.impl.in;
    BufferedInputStream bufin = new BufferedInputStream(streamIn);
    int sleepbetweentables = 800;
    int sleepbetweenbytes = 150;

//sample rates
    if (mode1SampleCombo.getSelectedIndex() == 0){table0WriteByte[50] = 5;}
    if (mode1SampleCombo.getSelectedIndex() == 1){table0WriteByte[50] = 10;}
    if (mode1SampleCombo.getSelectedIndex() == 2){table0WriteByte[50] = 15;}
    if (mode1SampleCombo.getSelectedIndex() == 3){table0WriteByte[50] = 20;}
    if (mode2SampleCombo.getSelectedIndex() == 0){table0WriteByte[53] = 5;}
    if (mode2SampleCombo.getSelectedIndex() == 1){table0WriteByte[53] = 10;}
    if (mode2SampleCombo.getSelectedIndex() == 2){table0WriteByte[53] = 15;}
    if (mode2SampleCombo.getSelectedIndex() == 3){table0WriteByte[53] = 20;}
    if (mode3SampleCombo.getSelectedIndex() == 0){table0WriteByte[56] = 5;}
    if (mode3SampleCombo.getSelectedIndex() == 1){table0WriteByte[56] = 10;}
    if (mode3SampleCombo.getSelectedIndex() == 2){table0WriteByte[56] = 15;}
    if (mode3SampleCombo.getSelectedIndex() == 3){table0WriteByte[56] = 20;}
//session lengths
    table0WriteByte[49] = (byte) mode1LengthSlider.getValue();
    table0WriteByte[52] = (byte) mode2LengthSlider.getValue();
    table0WriteByte[55] = (byte) mode3LengthSlider.getValue();
//last sensitivity index
    table1WriteByte[0] = (byte) mode1SensSlider.getValue();
    table1WriteByte[1] = (byte) mode2SensSlider.getValue();
    table1WriteByte[2] = (byte) mode3SensSlider.getValue();
//sensor type
    if (mode1SensorCombo.getSelectedIndex() == 0) {table0WriteByte[48] = 1;} else {table0WriteByte[48] = 4;}
    if (mode2SensorCombo.getSelectedIndex() == 1) {table0WriteByte[51] = 2;} else {table0WriteByte[51] = 8;}
    if (mode3SensorCombo.getSelectedIndex() == 0) {table0WriteByte[54] = 3;} else {table0WriteByte[54] = 12;}
//sensitivity values
//mode1
for (int i =0;i<8;i++){
    Integer sens = new Integer( advSensTable.getValueAt(i,1).toString() );
    //Integer sens = new Integer ((advSensTableData[i][1]).toString());
    int ia = (((sens.intValue()))>>8);
    int ib = (sens.intValue());
    table0WriteByte[2*i]= (byte)ia;
    table0WriteByte[2*i+1]= (byte)ib;
}
//mode2
for (int i =0;i<8;i++){
    Integer sens = new Integer( advSensTable.getValueAt(i,2).toString() );
    //Integer sens = new Integer ((advSensTableData[i][2]).toString());
    int ia = (((sens.intValue()))>>8);
    int ib = (sens.intValue());
    table0WriteByte[2*i+16]= (byte)ia;
    table0WriteByte[2*i+17]= (byte)ib;
}
//mode3
for (int i =0;i<8;i++){
    Integer sens = new Integer( advSensTable.getValueAt(i,3).toString() );
    //Integer sens = new Integer ((advSensTableData[i][3]).toString());
    int ia = (((sens.intValue()))>>8);
    int ib = (sens.intValue());
    table0WriteByte[2*i+32]= (byte)ia;
    table0WriteByte[2*i+33]= (byte)ib;
}
/*for RGB (modes1&2) (<8 for mode 3)*/
//RGBMODE1
    for (int i =0;i<15;i++){

        Integer tempr = new Integer( mode1Table.getValueAt(i,1).toString() );
        Integer tempg = new Integer(  mode1Table.getValueAt(i,2).toString()    );
        Integer tempb = new Integer( mode1Table.getValueAt(i,3).toString()    );

        int tempR = (0xefff&(tempr.intValue()));
        int tempG = ((0xefff&(tempg.intValue()))<<5);
        int tempB = ((0xefff&(tempb.intValue()))<<10);
        int temp = ((tempR + tempG + tempB)&0xffff)  ;
        table2WriteByte[2*i] = (byte)(0x00ff&(temp>>8));
        table2WriteByte[2*i+1] = (byte)(0x00ff&(temp));
    }
//RGBMODE2
    for (int i =0;i<15;i++){

        Integer tempr = new Integer( mode2Table.getValueAt(i,1).toString() );
        Integer tempg = new Integer(  mode2Table.getValueAt(i,2).toString()    );
        Integer tempb = new Integer( mode2Table.getValueAt(i,3).toString()    );

        int tempR = (0xefff&(tempr.intValue()));
        int tempG = ((0xefff&(tempg.intValue()))<<5);
        int tempB = ((0xefff&(tempb.intValue()))<<10);
        int temp = ((tempR + tempG + tempB)&0xffff)  ;
        table3WriteByte[2*i] = (byte)(0x00ff&(temp>>8));
        table3WriteByte[2*i+1] = (byte)(0x00ff&(temp));
    }
//RGBMODE3
    for (int i =0;i<8;i++){

        Integer tempr = new Integer( mode3Table.getValueAt(i,1).toString() );
        Integer tempg = new Integer(  mode3Table.getValueAt(i,2).toString()    );
        Integer tempb = new Integer( mode3Table.getValueAt(i,3).toString()    );

        int tempR = (0xefff&(tempr.intValue()));
        int tempG = ((0xefff&(tempg.intValue()))<<5);
        int tempB = ((0xefff&(tempb.intValue()))<<10);
        int temp = ((tempR + tempG + tempB)&0xffff)  ;
        table4WriteByte[2*i] = (byte)(0x00ff&(temp>>8));
        table4WriteByte[2*i+1] = (byte)(0x00ff&(temp));
    }
//AUDIOMODE1

    int counter = 32;

    for (int i = 0; i < 15; i++) {
        Float freq = new Float( mode1Table.getValueAt(i,4).toString());
//        Float freq = new Float(mode1TableData[i][4].toString());
//        System.out.println(i + " freq =" + freq);
        float ffreq = freq.floatValue() * 10;
        int ifreq = (int) ffreq;
        float tempf = 0xc0ff & (((ifreq * 256) / 10000) / 10);
        float tempr = ((((ifreq * 256) / 10) % 10000)) << 8;
        tempr = tempr / 10000;
//        System.out.println("from mode1tabledata = " + i + " tempf = " + tempf + " and tempr =" + tempr);
        tempf = Math.round(tempf);
        tempr = Math.round(tempr);

        byte cF = (byte) (tempf);
        byte cR = (byte) (tempr);
        table2WriteByte[counter] = cF;
        counter++;
        table2WriteByte[counter] = cR;
        counter++;
    }
//AUDIOMODE2
counter = 32;
    for (int i = 0; i < 15; i++) {
        Float freq = new Float( mode2Table.getValueAt(i,4).toString());
        //Float freq = new Float(mode2TableData[i][4].toString());
//        System.out.println(i + " freq =" + freq);
        float ffreq = freq.floatValue() * 10;
        int ifreq = (int) ffreq;
        float tempf = 0xc0ff & (((ifreq * 256) / 10000) / 10);
        float tempr = ((((ifreq * 256) / 10) % 10000)) << 8;
        tempr = tempr / 10000;
//        System.out.println("from mode2tabledata = " + i + " tempf = " + tempf + " and tempr =" + tempr);
        tempf = Math.round(tempf);
        tempr = Math.round(tempr);
        byte cF = (byte) (tempf);
        byte cR = (byte) (tempr);
        table3WriteByte[counter] = cF;
        counter++;
        table3WriteByte[counter] = cR;
        counter++;
    }
//AUDIOMODE3
    int modcounter = 18;
    counter = 18;
    for (int i = 0; i < 8; i++) {
        Float freq = new Float( mode3Table.getValueAt(i,4).toString());
        Float modu = new Float( mode3Table.getValueAt(i,5).toString());
        //Float freq = new Float(mode3TableData[i][4].toString());
        //Float modu = new Float(mode3TableData[i][5].toString());
//        System.out.println(i + " freq =" + freq);
//        System.out.println(i + " modu =" + modu);
        float ffreq = freq.floatValue() * 10;
        float fmodu = modu.floatValue();
        int ifreq = (int) ffreq;

        float tempf = 0xc0ff & (((ifreq * 256) / 10000) / 10);
        float tempr = ((((ifreq * 256) / 10) % 10000)) << 8;
        float tempm = (((fmodu * 256) / 10000) * 256);

        tempr = tempr / 10000;
//        System.out.println("from mode3tabledata = " + i + " tempf = " + tempf + " and tempr =" + tempr+" and imodu = "+tempm);
        tempf = Math.round(tempf);
        tempr = Math.round(tempr);
        int itempm = Math.round(tempm);
        byte cF = (byte) (tempf);
        byte cR = (byte) (tempr);
        byte mD = (byte) (itempm);
        table4WriteByte[counter] = cF;
        counter++;
        table4WriteByte[counter] = cR;
        counter++;
        table4WriteByte[counter] = mD;
        counter++;

    }

/*

for (int i =0;i<table0WriteByte.length;i++){
    System.out.println("table0WriteByte["+i+"] = "+table0WriteByte[i]);
}
for (int i =0;i<table1WriteByte.length;i++){
    System.out.println("table1WriteByte["+i+"] = "+table1WriteByte[i]);
}
for (int i =0;i<table2WriteByte.length;i++){
    System.out.println("table2WriteByte["+i+"] = "+table2WriteByte[i]);
}
for (int i =0;i<table3WriteByte.length;i++){
    System.out.println("table3WriteByte["+i+"] = "+table3WriteByte[i]);
}
for (int i =0;i<table4WriteByte.length;i++){
    System.out.println("table4WriteByte["+i+"] = "+(table4WriteByte[i])   );
}
*/


//table0
//erase table0
try  {for (int i=0; i < eraseTable0cmd.length; i++){streamOut.write(eraseTable0cmd[i]);Thread.sleep(sleepbetweenbytes);}
    bufin.read(buf0);Thread.sleep(sleepbetweentables);
   // if(buf0[3]==10){

//block0
t0block[0]=(byte)0xa3;t0block[1]=(byte)0x46;t0block[2]=(byte)0x18;t0block[3]=(byte)0x22;t0block[4]=(byte)0x00;t0block[5]=(byte)0x00;
for(int i=0;i<16;i++){t0block[i+6] = table0WriteByte[i];}
int checksum = 0;
for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t0block[i]);}
t0block[22] = (byte)(0xff&(checksum>>8));t0block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t0b0");for(int i=0;i<24;i++){System.out.print("+:"+t0block[i]);}
        for (int i=0; i < t0block.length; i++){streamOut.write(t0block[i]);Thread.sleep(sleepbetweenbytes);} //send t0block0
        bufin.read(buf0);Thread.sleep(sleepbetweentables);

//block1
t0block[5]=(byte)0x01;
for(int i=0;i<16;i++){t0block[i+6] = table0WriteByte[i+16];}
checksum = 0;
for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t0block[i]);}
t0block[22] = (byte)(0xff&(checksum>>8));t0block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t0b1");for(int i=0;i<24;i++){System.out.print("+:"+t0block[i]);}
        for (int i=0; i < t0block.length; i++){streamOut.write(t0block[i]);Thread.sleep(sleepbetweenbytes);} //send t0block1
        bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block2
t0block[5]=(byte)0x02;
for(int i=0;i<16;i++){t0block[i+6] = table0WriteByte[i+32];}
checksum = 0;
for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t0block[i]);}
t0block[22] = (byte)(0xff&(checksum>>8));t0block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t0b2");for(int i=0;i<24;i++){System.out.print("+:"+t0block[i]);}
        for (int i=0; i < t0block.length; i++){streamOut.write(t0block[i]);Thread.sleep(sleepbetweenbytes);} //send t0block2
        bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block3
t0block[5]=(byte)0x03;
for(int i=0;i<16;i++){t0block[i+6] = table0WriteByte[i+48];}
checksum = 0;
for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t0block[i]);}
t0block[22] = (byte)(0xff&(checksum>>8));t0block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t0b3");for(int i=0;i<24;i++){System.out.print("+:"+t0block[i]);}
        for (int i=0; i < t0block.length; i++){streamOut.write(t0block[i]);Thread.sleep(sleepbetweenbytes);} //send t0block3
        bufin.read(buf0);Thread.sleep(sleepbetweentables);
    //}
}
catch(Exception ex){ex.printStackTrace();}


//table2
try  {for (int i=0; i < eraseTable2cmd.length; i++){streamOut.write(eraseTable2cmd[i]);Thread.sleep(sleepbetweenbytes);} //erase table2
    bufin.read(buf0);Thread.sleep(sleepbetweentables);
   // if(buf0[3]==10){

//block0
t2block[0]=(byte)0xa3;t2block[1]=(byte)0x46;t2block[2]=(byte)0x18;t2block[3]=(byte)0x22;t2block[4]=(byte)0x02;t2block[5]=(byte)0x00;
for(int i=0;i<16;i++){t2block[i+6] = table2WriteByte[i];}
int checksum = 0;
for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t2block[i]);}
t2block[22] = (byte)(0xff&(checksum>>8));t2block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t2b0");for(int i=0;i<24;i++){System.out.print("+:"+t2block[i]);}
        for (int i=0; i < t2block.length; i++){streamOut.write(t2block[i]);Thread.sleep(sleepbetweenbytes);} //send t2block0
        bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block1
t2block[5]=(byte)0x01;
for(int i=0;i<16;i++){t2block[i+6] = table2WriteByte[i+16];}
checksum = 0;
for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t2block[i]);}
t2block[22] = (byte)(0xff&(checksum>>8));t2block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t2b1");for(int i=0;i<24;i++){System.out.print("+:"+t2block[i]);}
        for (int i=0; i < t2block.length; i++){streamOut.write(t2block[i]);Thread.sleep(sleepbetweenbytes);} //send t2block1
        bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block2
t2block[5]=(byte)0x02;
for(int i=0;i<16;i++){t2block[i+6] = table2WriteByte[i+32];}
checksum = 0;
for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t2block[i]);}
t2block[22] = (byte)(0xff&(checksum>>8));t2block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t2b2");for(int i=0;i<24;i++){System.out.print("+:"+t2block[i]);}
        for (int i=0; i < t2block.length; i++){streamOut.write(t2block[i]);Thread.sleep(sleepbetweenbytes);} //send t2block2
        bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block3
t2block[5]=(byte)0x03;
for(int i=0;i<16;i++){t2block[i+6] = table2WriteByte[i+48];}
checksum = 0;
for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t2block[i]);}
t2block[22] = (byte)(0xff&(checksum>>8));t2block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t2b3");for(int i=0;i<24;i++){System.out.print("+:"+t2block[i]);}
        for (int i=0; i < t2block.length; i++){streamOut.write(t2block[i]);Thread.sleep(sleepbetweenbytes);} //send t2block3
        bufin.read(buf0);Thread.sleep(sleepbetweentables);
    //}
}
catch(Exception ex){ex.printStackTrace();}

//table3
try  {for (int i=0; i < eraseTable3cmd.length; i++){streamOut.write(eraseTable3cmd[i]);Thread.sleep(sleepbetweenbytes);} //erase table3
    bufin.read(buf0);Thread.sleep(sleepbetweentables);
   // if(buf0[3]==10){

//block0
t3block[0]=(byte)0xa3;t3block[1]=(byte)0x46;t3block[2]=(byte)0x18;t3block[3]=(byte)0x22;t3block[4]=(byte)0x03;t3block[5]=(byte)0x00;
for(int i=0;i<16;i++){t3block[i+6] = table3WriteByte[i];}
int checksum = 0;
for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t3block[i]);}
t3block[22] = (byte)(0xff&(checksum>>8));t3block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t3b0");for(int i=0;i<24;i++){System.out.print("+:"+t3block[i]);}
        for (int i=0; i < t3block.length; i++){streamOut.write(t3block[i]);Thread.sleep(sleepbetweenbytes);} //send t3block0
        bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block1
t3block[5]=(byte)0x01;
for(int i=0;i<16;i++){t3block[i+6] = table3WriteByte[i+16];}
checksum = 0;
for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t3block[i]);}
t3block[22] = (byte)(0xff&(checksum>>8));t3block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t3b1");for(int i=0;i<24;i++){System.out.print("+:"+t3block[i]);}
        for (int i=0; i < t3block.length; i++){streamOut.write(t3block[i]);Thread.sleep(sleepbetweenbytes);} //send t3block1
        bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block2
t3block[5]=(byte)0x02;
for(int i=0;i<16;i++){t3block[i+6] = table3WriteByte[i+32];}
checksum = 0;
for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t3block[i]);}
t3block[22] = (byte)(0xff&(checksum>>8));t3block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t3b2");for(int i=0;i<24;i++){System.out.print("+:"+t3block[i]);}
        for (int i=0; i < t3block.length; i++){streamOut.write(t3block[i]);Thread.sleep(sleepbetweenbytes);} //send t3block2
        bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block3
t3block[5]=(byte)0x03;
for(int i=0;i<16;i++){t3block[i+6] = table3WriteByte[i+48];}
checksum = 0;
for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t3block[i]);}
t3block[22] = (byte)(0xff&(checksum>>8));t3block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t3b3");for(int i=0;i<24;i++){System.out.print("+:"+t3block[i]);}
        for (int i=0; i < t3block.length; i++){streamOut.write(t3block[i]);Thread.sleep(sleepbetweenbytes);} //send t3block3
        bufin.read(buf0);Thread.sleep(sleepbetweentables);
    //}
}
catch(Exception ex){ex.printStackTrace();}


//table4
try  {for (int i=0; i < eraseTable4cmd.length; i++){streamOut.write(eraseTable4cmd[i]);Thread.sleep(sleepbetweenbytes);} //erase table4
    bufin.read(buf0);Thread.sleep(sleepbetweentables);
   // if(buf0[3]==10){

//block0
t4block[0]=(byte)0xa3;t4block[1]=(byte)0x46;t4block[2]=(byte)0x18;t4block[3]=(byte)0x22;t4block[4]=(byte)0x04;t4block[5]=(byte)0x00;
for(int i=0;i<16;i++){t4block[i+6] = table4WriteByte[i];}
int checksum = 0;
for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t4block[i]);}
t4block[22] = (byte)(0xff&(checksum>>8));t4block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t3b0");for(int i=0;i<24;i++){System.out.print("+:"+t4block[i]);}
        for (int i=0; i < t4block.length; i++){streamOut.write(t4block[i]);Thread.sleep(sleepbetweenbytes);} //send t4block0
        bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block1
t4block[5]=(byte)0x01;
for(int i=0;i<16;i++){t4block[i+6] = table4WriteByte[i+16];}
checksum = 0;
for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t4block[i]);}
t4block[22] = (byte)(0xff&(checksum>>8));t4block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t3b1");for(int i=0;i<24;i++){System.out.print("+:"+t4block[i]);}
        for (int i=0; i < t4block.length; i++){streamOut.write(t4block[i]);Thread.sleep(sleepbetweenbytes);} //send t4block1
        bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block2
t4block[5]=(byte)0x02;
for(int i=0;i<16;i++){t4block[i+6] = table4WriteByte[i+32];}
checksum = 0;
for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t4block[i]);}
t4block[22] = (byte)(0xff&(checksum>>8));t4block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t3b2");for(int i=0;i<24;i++){System.out.print("+:"+t4block[i]);}
        for (int i=0; i < t4block.length; i++){streamOut.write(t4block[i]);Thread.sleep(sleepbetweenbytes);} //send t4block2
        bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block3
t4block[5]=(byte)0x03;
for(int i=0;i<16;i++){t4block[i+6] = table4WriteByte[i+48];}
checksum = 0;
for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t4block[i]);}
t4block[22] = (byte)(0xff&(checksum>>8));t4block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t3b3");for(int i=0;i<24;i++){System.out.print("+:"+t4block[i]);}
        for (int i=0; i < t4block.length; i++){streamOut.write(t4block[i]);Thread.sleep(sleepbetweenbytes);} //send t4block3
        bufin.read(buf0);Thread.sleep(sleepbetweentables);
    //}


}
catch(Exception ex){ex.printStackTrace();
            }



/*
try{
    for (int i=0; i < eraseTable0cmd.length; i++){streamOut.write(eraseTable0cmd[i]);Thread.sleep(sleepbetweenbytes);} //erase table0
    bufin.read(buf0);Thread.sleep(sleepbetweentables);
    if(buf0[3]==10){for (int i=0; i < t0block.length; i++){streamOut.write(t0block[i]);Thread.sleep(sleepbetweenbytes);} //send block0table
        bufin.read(buf0);Thread.sleep(sleepbetweentables);
    }
}


    catch(Exception ex){ex.printStackTrace();}

*/




//erase table get ok
//send blocks
//checksum 2 bytes - add data + command bytes




}

public void reqTable() {


    //try{streamIn.close();}catch(Exception e){e.printStackTrace();};


    streamOut = SerialMain.impl.out;
    streamIn = SerialMain.impl.in;


    BufferedInputStream bufin = new BufferedInputStream(streamIn);


    try{bufin.skip(streamIn.available());}catch(Exception e){e.printStackTrace();};



    int[] getTable0cmd = {0xa3, 0x46, 0x07, 0x20, 0x00, 0x01, 0x10};
    int[] getTable1cmd = {0xa3, 0x46, 0x07, 0x20, 0x01, 0x01, 0x11};
    int[] getTable2cmd = {0xa3, 0x46, 0x07, 0x20, 0x02, 0x01, 0x12};
    int[] getTable3cmd = {0xa3, 0x46, 0x07, 0x20, 0x03, 0x01, 0x13};
    int[] getTable4cmd = {0xa3, 0x46, 0x07, 0x20, 0x04, 0x01, 0x14};
    byte[] table0 = new byte[65];
    byte[] table1 = new byte[64];
    byte[] table2 = new byte[71];
    byte[] table3 = new byte[71];
    byte[] table4 = new byte[71];

    int sleepbetweentables = 10;
    int sleepbetweenbytes = 150;

    try{



        //send command to get table0
        for (int i=0; i < getTable0cmd.length; i++){streamOut.write(getTable0cmd[i]);Thread.sleep(sleepbetweenbytes);}
        bufin.read(table0);
        Thread.sleep(sleepbetweentables);
        //send command to get table1
        for (int z = 0; z < getTable1cmd.length; z++) {streamOut.write(getTable1cmd[z]);Thread.sleep(sleepbetweenbytes);}
        bufin.read(table1);
        Thread.sleep(sleepbetweentables);
        //send command to get table2
        for (int z=0; z < getTable2cmd.length; z++){streamOut.write(getTable2cmd[z]);Thread.sleep(sleepbetweenbytes);}
        bufin.read(table2);
        Thread.sleep(sleepbetweentables);
        //send command to get table3
        for (int z=0; z < getTable3cmd.length; z++){streamOut.write(getTable3cmd[z]);Thread.sleep(sleepbetweenbytes);}
        bufin.read(table3);
        Thread.sleep(sleepbetweentables);
        //send command to get table4
        for (int z=0; z < getTable4cmd.length; z++){streamOut.write(getTable4cmd[z]);Thread.sleep(sleepbetweenbytes);}
        bufin.read(table4);
        Thread.sleep(sleepbetweentables);


        //set session length in minutes
            mode1LengthSlider.setValue(table0[54]);
            mode2LengthSlider.setValue(table0[57]);
            mode3LengthSlider.setValue(table0[60]);

        //set modeXSensPercent array for displaying modeXSensPercField display value
        for (int i =0;i<mode1SensPercent.length;i++){
            int m1 = (i*2)+5;  //find mode1 senspercent location in table
            int m2 = (i*2)+21; //find mode2 senspercent location in table
            int m3 = (i*2)+37; //find mode3 senspercent location in table
            mode1SensPercent[i]= String.valueOf(  (  ((table0[m1])  &0xff)  <<8)  + ((table0[m1+1])&0xff)  );
            mode2SensPercent[i]= String.valueOf(  (  ((table0[m2])  &0xff)  <<8)  +(table0[m2+1])&0xff);
            mode3SensPercent[i]= String.valueOf(  (  ((table0[m3])  &0xff)  <<8)  +(table0[m3+1])&0xff);
        }

        //get version information
        byte[] versb = new byte[6];
        byte[] dateb = new byte[6];
        String version = "";
        String date = "";
        for(int i = 8;i<14;i++){
            version = version+(char)table1[i];
        }
        for(int i = 14;i<20;i++){
            date = date+((char)(table1[i]&0xff));
        }

        //fill advSensTable with sensitivity percentages
        for(int i=0;i<mode1SensPercent.length;i++){
            advSensTable.setValueAt(new Integer(mode1SensPercent[i]), i, 1 );
            advSensTable.setValueAt(new Integer(mode2SensPercent[i]), i, 2 );
            advSensTable.setValueAt(new Integer(mode3SensPercent[i]), i, 3 );
        }


        //get RGB for mode 1
        for(int i=3;i<18;i++){
            int word = 0;
            word = (( (0x00ff&table2[(2*i)-1])<<8 ) + (0x00ff&table2[(2*i)]));
            mode1Color[i-3][0] = word&0x001f;
            mode1Color[i-3][1] = (word>>5)&0x001f;
            mode1Color[i-3][2] = (word>>10)&0x001f;
            //System.out.println("(table2[(2*i)-1]) = "+ (0x00ff& (table2[(2*i)-1])));
            //System.out.println("table2[(2*i)] ="+ (0x00ff& (table2[(2*i)])));
            //System.out.println("word = "+ (word));
            //System.out.println("red = "+ mode1Color[i-3][0]);
            //System.out.println("green = "+ mode1Color[i-3][1]);
            //System.out.println("blue = "+ mode1Color[i-3][2]);
        }

        //get RGB for mode 2
        for(int i=3;i<18;i++){
            int word = 0;
            word = (( (0x00ff&table3[(2*i)-1])<<8 ) + (0x00ff&table3[(2*i)]));
            mode2Color[i-3][0] = word&0x001f;
            mode2Color[i-3][1] = (word>>5)&0x001f;
            mode2Color[i-3][2] = (word>>10)&0x001f;
            //System.out.println("(table3[(2*i)-1]) = "+ (0x00ff& (table3[(2*i)-1])));
            //System.out.println("table3[(2*i)] ="+ (0x00ff& (table3[(2*i)])));
            //System.out.println("word = "+ (word));
            //System.out.println("red = "+ mode2Color[i-3][0]);
            //System.out.println("green = "+ mode2Color[i-3][1]);
            //System.out.println("blue = "+ mode2Color[i-3][2]);
        }

        //get RGB for mode 3
        for(int i=3;i<11;i++){
            int word = 0;
            word = (( (0x00ff&table4[(2*i)-1])<<8 ) + (0x00ff&table4[(2*i)]));
            mode3Color[i-3][0] = word&0x001f;
            mode3Color[i-3][1] = (word>>5)&0x001f;
            mode3Color[i-3][2] = (word>>10)&0x001f;
            //System.out.println("(table4[(2*i)-1]) = "+ (0x00ff& (table4[(2*i)-1])));
            //System.out.println("table4[(2*i)] ="+ (0x00ff& (table4[(2*i)])));
            //System.out.println("word = "+ (word));
            //System.out.println("red = "+ mode3Color[i-3][0]);
            //System.out.println("green = "+ mode3Color[i-3][1]);
            //System.out.println("blue = "+ mode3Color[i-3][2]);
        }


        //get audio for mode1
        int counterer =0;
        for(int i=37;i<67;i=i+2 ){
        float cF = 0x003f&table2[i];
        float cR = 0x00ff&table2[i+1];
        float temp = 0;
        temp = (((cF*10000)/256) + ((( cR /256 ) * 10000) /256));
        //System.out.println(counterer+"= (mode1)for "+i+" cF = ["+cF+"] cR =["+cR+"]  straight from table");
        //System.out.println("(mode1)freq is ="+temp);
        mode1Audio[counterer] = temp;
        counterer++;
        }

        //get audio for mode2
        counterer=0;
        for(int i=37;i<67;i=i+2 ){
        float cF = 0x003f&table3[i];
        float cR = 0x00ff&table3[i+1];
        //System.out.println("for "+i+" cF = ["+cF+"] cR =["+cR+"]");
        float temp = 0;
        temp = (((cF*10000)/256) + ((( cR /256 ) * 10000) /256));
        mode2Audio[counterer] = temp;
        counterer++;
        }

        //get audio for mode3
        counterer=0;
        for(int i=23;i<47;i=i+3 ){
            float cF = 0x003f & table4[i];
            float cR = 0x00ff & table4[i + 1];
            float mR = 0x00ff & table4[i + 2];
            float temp = 0;
            temp = (((cF*10000)/256) + ((( cR /256 ) * 10000) /256));
            float tempm = (((mR / 256) * 10000) / 256);
            System.out.println("for "+i+" mR = "+mR);
            mode3Audio[counterer][0] = temp;
            mode3Audio[counterer][1] = tempm;
            System.out.println("for "+i+" cF = "+cF+"] cR =["+cR+"]"+" and mod ="+tempm);
            System.out.println("(mode3)freq is ="+mode3Audio[counterer][0]);
            System.out.println("mode3Audio[counterer][0]  = "+mode3Audio[counterer][0]);
            System.out.println("mode3Audio[counterer][1]  = "+mode3Audio[counterer][1]);
            counterer++;
        }


        //fill mode1Table with rgb values
        for(int i=0;i<15;i++){
            mode1Table.setValueAt(new Integer(mode1Color[i][0]), i,1);
            mode1Table.setValueAt(new Integer(mode1Color[i][1]), i, 2);
            mode1Table.setValueAt(new Integer(mode1Color[i][2]), i, 3);
        }
        //fill mode2Table with rgb values
        for(int i=0;i<15;i++){
            mode2Table.setValueAt( new Integer(mode2Color[i][0] ), i, 1);
            mode2Table.setValueAt( new Integer(mode2Color[i][1]), i, 2);
            mode2Table.setValueAt( new Integer(mode2Color[i][2]), i, 3);
        }
        //fill mode3Table with rgb values
        for(int i=0;i<8;i++){
            mode3Table.setValueAt(new Integer(mode3Color[i][0]), i, 1);
            mode3Table.setValueAt(new Integer(mode3Color[i][1]), i, 2);
            mode3Table.setValueAt(new Integer(mode3Color[i][2]), i, 3);
        }

        //fill allmodes with audio values
        for(int i=0;i<15;i++){
            mode1Table.setValueAt(new Double (((Math.round((mode1Audio[i])*100)))/100.0 ), i, 4);
            mode2Table.setValueAt(new Double(((Math.round((mode2Audio[i])*100)))/100.0 ), i, 4);
        }

        for(int i=0;i<8;i++){
            mode3Table.setValueAt(new Double (((Math.round((mode3Audio[i][0])*100)))/100.0), i, 4 );
            mode3Table.setValueAt(new Double (((Math.round((mode3Audio[i][1])*100)))/100.0 ), i, 5 );
        }



        //set lastsensitivity index sliders
        if(mode1SensSlider.getValue() == table1[5]){mode1SensSlider.setValue(mode1SensSlider.getMaximum()-1);}
        if(mode2SensSlider.getValue() == table1[6]){mode2SensSlider.setValue(mode2SensSlider.getMaximum()-1);}
        if(mode3SensSlider.getValue() == table1[7]){mode3SensSlider.setValue(mode3SensSlider.getMaximum()-1);}
        mode1SensSlider.setValue(table1[5]);
        mode2SensSlider.setValue(table1[6]);
        mode3SensSlider.setValue(table1[7]);


        //set sensor type for modes
        if(table0[53] == 1)mode1SensorCombo.setSelectedIndex(0);else mode1SensorCombo.setSelectedIndex(1);
        if(table0[56] == 8)mode2SensorCombo.setSelectedIndex(1);else mode2SensorCombo.setSelectedIndex(0);
        if(table0[59] == 3)mode3SensorCombo.setSelectedIndex(0);else mode3SensorCombo.setSelectedIndex(1);

        //set sample rates
        int mode1SampleRate, mode2SampleRate, mode3SampleRate = 0;
        for(int i=0;i<sampleRates.length;i++){
            if( ((table0[55])&0xff) == sampleRates[i]) {
                mode1SampleCombo.setSelectedIndex(i);
            }
            if( ((table0[58])&0xff) == sampleRates[i]) {
                mode2SampleCombo.setSelectedIndex(i);
            }
            if( ((table0[61])&0xff) == sampleRates[i]) {
                mode3SampleCombo.setSelectedIndex(i);
            }
        }


        mode1LengthSlider.setEnabled(true);
        mode1SensSlider.setEnabled(true);
        mode2LengthSlider.setEnabled(true);
        mode2SensSlider.setEnabled(true);
        mode3LengthSlider.setEnabled(true);
        mode3SensSlider.setEnabled(true);

        advancedMenuItem.setEnabled(true); //allow advanced panel menuoption after basic panel is enabled


        //table0[54]
/*
        for (int a = 0; a < table11.length; a++) {
            System.out.print((int)table11[a]+"  ");
        }
        System.out.println();
*/


    }catch(Exception ex){
            ex.printStackTrace();}



        }





  private Object makeObj(final String item)  {
      return new Object() { public String toString() { return item; } };
   }


   private void jbInit() throws Exception {

        for(int i=0;i<sampleRates.length;i++){
            mode1SampleCombo.addItem(makeObj(String.valueOf(sampleRates[i])));
            mode2SampleCombo.addItem(makeObj(String.valueOf(sampleRates[i])));
            mode3SampleCombo.addItem(makeObj(String.valueOf(sampleRates[i])));
        }

        this.setDefaultCloseOperation(this.DO_NOTHING_ON_CLOSE);

        this.getRootPane().setWindowDecorationStyle( JRootPane. PLAIN_DIALOG );
        this.setJMenuBar(jMenuBar1);
        this.setTitle("Presage Table Editor");
        this.getContentPane().setLayout(verticalFlowLayout4);
        jMenu1.setText("File");
        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(this);
        jMenu2.setText("Settings");
        advancedMenuItem.setText("Show Advanced Tables");
        advancedMenuItem.addActionListener(this);
        //advancedMenuItem.setEnabled(false);
        advancedMenuItem.setEnabled(true);
        jMenuItem2.setText("Table Editor Help");
        jMenuItem2.addActionListener(this);
        jMenu3.setText("Help");
        sButton.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 14));
        sButton.setPreferredSize(new Dimension(123, 35));
        sButton.setRequestFocusEnabled(false);
        sButton.setText("Send Tables");
        sButton.addActionListener(this);
        rButton.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 14));
        rButton.setPreferredSize(new Dimension(123, 35));
        rButton.setRequestFocusEnabled(false);
        rButton.setToolTipText("Read Tables");
        rButton.setText("Read Tables");
        rButton.setVisible(false); //read now occurs when opening table editor
        rButton.addActionListener(this);
        mode2BasicLabel.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12));
        mode2BasicLabel.setAlignmentX((float) 0.0);
        mode2BasicLabel.setLabelFor(null);
        mode2BasicLabel.setText("Mode 2");
        mode3BasicLabel.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12));
        mode3BasicLabel.setAlignmentX((float) 0.0);
        mode3BasicLabel.setLabelFor(null);
        mode3BasicLabel.setText("Mode 3");
        mode1AdvLabel.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12));
        mode1AdvLabel.setAlignmentY((float) 0.0);
        mode1AdvLabel.setDisplayedMnemonic('0');
        mode1AdvLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mode1AdvLabel.setLabelFor(mode1BasicPanel);
        mode1AdvLabel.setText("Mode 1");
        mode1AdvLabel.setVerticalAlignment(SwingConstants.TOP);
        mode1AdvLabel.setVerticalTextPosition(SwingConstants.TOP);
        mode2AdvLabel.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12));
        mode2AdvLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mode2AdvLabel.setText("Mode 2");
        mode3AdvLabel.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12));
        mode3AdvLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mode3AdvLabel.setLabelFor(mode3BasicPanel);
        mode3AdvLabel.setText("Mode 3");
        mode1BasicLabel.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12));
        mode1BasicLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        mode1BasicLabel.setHorizontalTextPosition(SwingConstants.LEADING);
        mode1BasicLabel.setLabelFor(null);
        mode1BasicLabel.setText("Mode 1");
        mode1LengthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mode1LengthLabel.setText("Session Length:");
        mode1BasicPanel.setLayout(verticalFlowLayout1);
        mode2BasicPanel.setLayout(verticalFlowLayout2);
        mode3BasicPanel.setLayout(verticalFlowLayout3);

        /*
        //Create the label table
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 1 ), new JLabel("1 Min") );
        labelTable.put( new Integer( 15 ), new JLabel("15 Min") );
        mode1LengthField.setLabelTable( labelTable );

        mode1LengthField.setPaintLabels(true);
*/
        mode1LengthSlider.setMajorTickSpacing(5);
        mode1LengthSlider.setMaximum(30);
        mode1LengthSlider.setMinimum(1);
        mode1LengthSlider.setMinorTickSpacing(1);
        mode1LengthSlider.setPaintTicks(true);
        mode1LengthSlider.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 11));
        mode1LengthSlider.setMaximumSize(new Dimension(10, 105));
        mode1LengthSlider.setMinimumSize(new Dimension(10, 45));
        mode1LengthSlider.setPreferredSize(new Dimension(10, 45));
        mode1LengthSlider.setRequestFocusEnabled(false);
        mode1LengthSlider.setToolTipText("");
        mode1LengthSlider.setValue(29);
        mode1SensLabel.setPreferredSize(new Dimension(96, 14));
        mode1SensLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mode1SensLabel.setText("Last Sensitivity IDX:");
        mode1SensitivityPanel.setLayout(gridLayout2);
        mode2LengthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mode2LengthLabel.setText("Session Length:");
        mode2LengthSlider.setMajorTickSpacing(5);
        mode2LengthSlider.setMaximum(30);
        mode2LengthSlider.setMinimum(1);
        mode2LengthSlider.setMinorTickSpacing(1);
        mode2LengthSlider.setPaintTicks(true);
        mode2LengthSlider.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 11));
        mode2LengthSlider.setMaximumSize(new Dimension(10, 105));
        mode2LengthSlider.setMinimumSize(new Dimension(10, 45));
        mode2LengthSlider.setPreferredSize(new Dimension(10, 45));
        mode2LengthSlider.setRequestFocusEnabled(false);
        mode2LengthSlider.setToolTipText("");
        mode2LengthSlider.setValue(29);
        mode2SensLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mode2SensLabel.setText("Last Sensitivity IDX:");
        mode3LengthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mode3LengthLabel.setText("Session Length:");
        mode3LengthSlider.setMajorTickSpacing(5);
        mode3LengthSlider.setMaximum(30);
        mode3LengthSlider.setMinimum(1);
        mode3LengthSlider.setMinorTickSpacing(1);
        mode3LengthSlider.setPaintTicks(true);
        mode3LengthSlider.setMaximumSize(new Dimension(10, 105));
        mode3LengthSlider.setMinimumSize(new Dimension(10, 45));
        mode3LengthSlider.setPreferredSize(new Dimension(10, 45));
        mode3LengthSlider.setRequestFocusEnabled(false);
        mode3LengthSlider.setValue(29);
        mode3SensLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mode3SensLabel.setText("Last Sensitivity IDX:");
        advancedTitleLabel.setFont(new java.awt.Font("Tahoma", Font.BOLD, 17));
        advancedTitleLabel.setBorder(null);
        advancedTitleLabel.setMaximumSize(new Dimension(84, 41));
        advancedTitleLabel.setMinimumSize(new Dimension(84, 41));
        advancedTitleLabel.setPreferredSize(new Dimension(94, 41));
        advancedTitleLabel.setText("Advanced");
        mode1AdvPanel.setLayout(verticalFlowLayout5);
        mode2AdvPanel.setLayout(verticalFlowLayout6);
        mode3AdvPanel.setLayout(verticalFlowLayout7);
        mode1SensorLabel.setText("Sensor Type:  ");
        mode1SampleCombo.setSelectedIndex(0);
        mode2SampleCombo.setSelectedIndex(0);
        mode3SampleCombo.setSelectedIndex(0);
        mode1SensorCombo.setSelectedIndex(0);
        mode2SensorCombo.setSelectedIndex(0);
        mode3SensorCombo.setSelectedIndex(0);
        mode1SensorCombo.addActionListener(new
                                     TableEditorFrame_mode1SensorCombo_actionAdapter(this));
        mode1SampleCombo.addActionListener(new
                                     TableEditorFrame_mode1SampleCombo_actionAdapter(this));
        mode2SensorCombo.addActionListener(new
                                     TableEditorFrame_mode2SensorCombo_actionAdapter(this));
        mode2SampleCombo.addActionListener(new
                                     TableEditorFrame_mode2SampleCombo_actionAdapter(this));
        mode3SensorCombo.addActionListener(new
                                     TableEditorFrame_mode3SensorCombo_actionAdapter(this));
        mode3SampleCombo.addActionListener(new
                                     TableEditorFrame_mode3SampleCombo_actionAdapter(this));
        mode1SensSlider.addChangeListener(this);
        mode2SensSlider.addChangeListener(this);
        mode3SensSlider.addChangeListener(this);
        mode1LengthSlider.addChangeListener(this);
        mode2LengthSlider.addChangeListener(this);
        mode3LengthSlider.addChangeListener(this);
        closeButton.addActionListener(this);

        mode2SensorLabel.setText("Sensor Type:  ");
        mode3SensorLabel.setText("Sensor Type:  ");
        mode1SampleLabel.setText("Sample Rate:");
        mode2SampleLabel.setText("Sample Rate:");
        mode3SampleLabel.setText("Sample Rate:");
        mode1LengthPanel.setLayout(gridLayout1);
        mode3LengthPanel.setLayout(gridLayout3);
        mode3SensitivityPanel.setLayout(gridLayout4);
        Mode2LengthPanel.setLayout(gridLayout5);
        mode2SensitivityPanel.setLayout(gridLayout6);
        mode1SensorPanel.setLayout(gridLayout7);
        mode2SensorPanel.setLayout(gridLayout8);
        mode3SensorPanel.setLayout(gridLayout9);
        mode1SamplePanel.setLayout(gridLayout10);
        mode2SamplePanel.setLayout(gridLayout11);
        mode3SamplePanel.setLayout(gridLayout12);

        spacerPanel.setMinimumSize(new Dimension(60, 35));

        spacerPanel.setPreferredSize(new Dimension(150, 35));
        mode1SensPercLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mode1SensPercLabel.setHorizontalTextPosition(SwingConstants.LEADING);
        mode1SensPercLabel.setText("Sensitivity:");
        mode1SensPercField.setPreferredSize(new Dimension(32, 20));
        mode1SensPercField.setEditable(false);
        mode1SensPercField.setText("%");
        mode1SensPercField.setHorizontalAlignment(SwingConstants.CENTER);
        mode1SensPercPanel.setLayout(gridLayout13);
        jLabel1.setText("jLabel1");
        mode3SensPercLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mode3SensPercLabel.setHorizontalTextPosition(SwingConstants.LEADING);
        mode3SensPercLabel.setText("Sensitivity:");
        mode3SensPercField.setPreferredSize(new Dimension(32, 20));
        mode3SensPercField.setEditable(false);
        mode3SensPercField.setText("%");
        mode3SensPercField.setHorizontalAlignment(SwingConstants.CENTER);
        mode2SensPercLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mode2SensPercLabel.setHorizontalTextPosition(SwingConstants.LEADING);
        mode2SensPercLabel.setText("Sensitivity:");
        mode2SensPercField.setPreferredSize(new Dimension(32, 20));
        mode2SensPercField.setEditable(false);
        mode2SensPercField.setText("%");
        mode2SensPercField.setHorizontalAlignment(SwingConstants.CENTER);
        mode2SensPercPanel.setLayout(gridLayout14);
        mode3SensPercPanel.setLayout(gridLayout15);
        basicPanel.setLayout(gridBagLayout1);
        verticalFlowLayout2.setHgap(3);
        verticalFlowLayout2.setVgap(3);
        verticalFlowLayout3.setHgap(3);
        verticalFlowLayout3.setVgap(3);
        mode1SensSlider.setValue(0);
        mode1SensSlider.setMaximum(7);
        mode1SensSlider.setMinorTickSpacing(1);
        mode1SensSlider.setMajorTickSpacing(1);
        mode1SensSlider.setPaintLabels(true);
        mode1SensSlider.setPaintTicks(true);
        mode1SensSlider.setMaximumSize(new Dimension(10, 24));
        mode1SensSlider.setMinimumSize(new Dimension(10, 24));
        mode1SensSlider.setPreferredSize(new Dimension(10, 45));
        mode1SensSlider.setRequestFocusEnabled(false);
        mode1SensSlider.setActionMap(null);
        mode2SensSlider.setValue(0);
        mode2SensSlider.setMaximum(7);
        mode2SensSlider.setMinorTickSpacing(1);
        mode2SensSlider.setMajorTickSpacing(1);
        mode2SensSlider.setPaintLabels(true);
        mode2SensSlider.setPaintTicks(true);
        mode2SensSlider.setMaximumSize(new Dimension(10, 24));
        mode2SensSlider.setMinimumSize(new Dimension(10, 24));
        mode2SensSlider.setPreferredSize(new Dimension(10, 45));
        mode2SensSlider.setRequestFocusEnabled(false);
        mode3SensSlider.setValue(0);
        mode3SensSlider.setMaximum(7);
        mode3SensSlider.setMinorTickSpacing(1);
        mode3SensSlider.setMajorTickSpacing(1);
        mode3SensSlider.setPaintLabels(true);
        mode3SensSlider.setPaintTicks(true);
        mode3SensSlider.setMaximumSize(new Dimension(10, 24));
        mode3SensSlider.setMinimumSize(new Dimension(10, 24));
        mode3SensSlider.setPreferredSize(new Dimension(10, 45));
        mode3SensSlider.setRequestFocusEnabled(false);
        verticalFlowLayout1.setHgap(3);
        verticalFlowLayout1.setVgap(3);
        mode1LengthPanel.setBorder(null);
        mode1LengthPanel.setMinimumSize(new Dimension(162, 54));
        mode1LengthPanel.setPreferredSize(new Dimension(214, 40));
        mode1LengthPanel.setRequestFocusEnabled(false);
        mode3LengthPanel.setBorder(null);
        mode3LengthPanel.setPreferredSize(new Dimension(214, 40));
        mode3LengthPanel.setRequestFocusEnabled(false);
        Mode2LengthPanel.setBorder(null);
        Mode2LengthPanel.setPreferredSize(new Dimension(214, 40));
        Mode2LengthPanel.setRequestFocusEnabled(false);
        mode1SensPercPanel.setPreferredSize(new Dimension(214, 20));
        mode3SensPercPanel.setPreferredSize(new Dimension(214, 20));
        mode2SensPercPanel.setPreferredSize(new Dimension(214, 20));
        mode1BasicPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        mode1BasicPanel.setPreferredSize(new Dimension(250, 170));
        mode3BasicPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        mode3BasicPanel.setPreferredSize(new Dimension(250, 170));
        mode2BasicPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        mode2BasicPanel.setPreferredSize(new Dimension(250, 170));
        mode1SensitivityPanel.setMinimumSize(new Dimension(214, 45));
        mode1SensitivityPanel.setPreferredSize(new Dimension(214, 45));
        readsendjpanel.setBorder(BorderFactory.createRaisedBevelBorder());
        mode3SensitivityPanel.setMinimumSize(new Dimension(214, 45));
        gridLayout1.setColumns(2);
        gridLayout1.setHgap(6);
        gridLayout1.setRows(2);
        blankField1.setVisible(false);
        blankField1.setPreferredSize(new Dimension(6, 10));
        mode1MinutesVal.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 9));
        mode1MinutesVal.setMinimumSize(new Dimension(6, 17));
        mode1MinutesVal.setPreferredSize(new Dimension(43, 10));
        mode1MinutesVal.setEditable(false);
        mode1MinutesVal.setHorizontalAlignment(SwingConstants.CENTER);
        gridLayout5.setColumns(2);
        gridLayout5.setHgap(6);
        gridLayout5.setRows(2);
        blankField2.setText("Minutes");
        blankField2.setVisible(false);
        blankField2.setPreferredSize(new Dimension(43, 10));
        mode2MinutesVal.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 9));
        mode2MinutesVal.setEditable(false);
        mode2MinutesVal.setHorizontalAlignment(SwingConstants.CENTER);
        gridLayout3.setColumns(2);
        gridLayout3.setHgap(6);
        gridLayout3.setRows(2);
        mode3MinutesVal.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 9));
        mode3MinutesVal.setEditable(false);
        mode3MinutesVal.setHorizontalAlignment(SwingConstants.CENTER);
        blankField3.setVisible(false);
        blankField3.setPreferredSize(new Dimension(6, 10));
        advancedPanel.setLayout(gridBagLayout2);
        advancedPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        advancedPanel.setPreferredSize(new Dimension(759, 450));
        tabbedPane.addChangeListener(new
                                     TableEditorFrame_tabbedpane_changeAdapter(this));
        tabbedPane.setBorder(null);
        tabbedPane.setMinimumSize(new Dimension(400, 128));
        tabbedPane.setPreferredSize(new Dimension(375, 192));
        tabbedPane.setToolTipText("");
        scrollPaneTab1.setAutoscrolls(true);
        scrollPaneTab1.setPreferredSize(new Dimension(400, 240));
        scrollPaneTab2.setAutoscrolls(true);
        scrollPaneTab2.setPreferredSize(new Dimension(400, 240));

        scrollPaneTab3.setAutoscrolls(true);
        scrollPaneTab3.setPreferredSize(new Dimension(420, 128));
        scrollPaneTab4.setAutoscrolls(true);
        scrollPaneTab4.setPreferredSize(new Dimension(375, 192));

        mode3Table.setPreferredSize(new Dimension(420, 128));
        mode3Table.setCellSelectionEnabled(true);
        mode3AdvPanel.setMaximumSize(new Dimension(185, 85));
        mode3AdvPanel.setMinimumSize(new Dimension(185, 85));
        mode3AdvPanel.setPreferredSize(new Dimension(185, 85));
        mode1AdvPanel.setMaximumSize(new Dimension(185, 85));
        mode1AdvPanel.setMinimumSize(new Dimension(185, 85));
        mode1AdvPanel.setPreferredSize(new Dimension(185, 85));
        mode2AdvPanel.setMaximumSize(new Dimension(185, 85));
        mode2AdvPanel.setMinimumSize(new Dimension(185, 85));
        mode2AdvPanel.setPreferredSize(new Dimension(185, 85));
        advSensTable.setPreferredSize(new Dimension(355, 128));

        advSensTable.setCellSelectionEnabled(true);
        closePanel.setPreferredSize(new Dimension(10, 50));
        closeButton.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 14));
        closeButton.setPreferredSize(new Dimension(123, 35));
        closeButton.setActionCommand("Close");

        closeButton.setText("Close");
        mode2Table.setCellSelectionEnabled(true);
        commMsgLabel.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12));
        commMsgLabel.setForeground(Color.red);
        commMsgLabel.setOpaque(true);
        commMsgLabel.setPreferredSize(new Dimension(123, 25));
        commMsgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        commMsgLabel.setText("");
        mode1BasicTitlePanel.setAlignmentY((float) 0.0);
        Mode2BasicTitlePanel.setAlignmentY((float) 0.0);
        mode3BasicTitlePanel.setAlignmentY((float) 0.0);
        verticalFlowLayout5.setVgap(2);
        verticalFlowLayout6.setVgap(2);
        verticalFlowLayout7.setVgap(2);

        jMenuBar1.add(jMenu1);
        jMenuBar1.add(jMenu2);
        jMenuBar1.add(jMenu3);
        jMenu1.add(jMenuItem1);
        jMenu2.add(advancedMenuItem);
        jMenu3.add(jMenuItem2);
        readsendjpanel.add(rButton);
        readsendjpanel.add(sButton);
        readsendjpanel.add(spacerPanel);
        spacerPanel.add(commMsgLabel);
        readsendjpanel.add(closeButton);
        this.getContentPane().add(readsendjpanel, null);
        this.getContentPane().add(basicPanel, null);
        mode1AdvPanel.add(mode1AdvLabel);
        mode1AdvPanel.add(mode1SensorPanel);
        mode1AdvPanel.add(mode1SamplePanel);
        mode1SamplePanel.add(mode1SampleLabel);
        mode1SamplePanel.add(mode1SampleCombo);
        mode1SensorPanel.add(mode1SensorLabel);
        mode1SensorPanel.add(mode1SensorCombo);
        mode2AdvPanel.add(mode2AdvLabel);
        mode2AdvPanel.add(mode2SensorPanel);
        mode2AdvPanel.add(mode2SamplePanel);
        mode2SamplePanel.add(mode2SampleLabel);
        mode2SamplePanel.add(mode2SampleCombo);
        mode2SensorPanel.add(mode2SensorLabel);
        mode2SensorPanel.add(mode2SensorCombo);
        mode3AdvPanel.add(mode3AdvLabel);
        mode3AdvPanel.add(mode3SensorPanel);
        mode3AdvPanel.add(mode3SamplePanel);
        mode3SamplePanel.add(mode3SampleLabel);
        mode3SamplePanel.add(mode3SampleCombo);
        mode3SensorPanel.add(mode3SensorLabel);
        mode3SensorPanel.add(mode3SensorCombo);
        mode1BasicPanel.add(mode1BasicTitlePanel);
        mode1BasicTitlePanel.add(mode1BasicLabel);
        mode1BasicPanel.add(mode1LengthPanel);
        mode2BasicPanel.add(Mode2BasicTitlePanel);
        Mode2BasicTitlePanel.add(mode2BasicLabel);
        mode2BasicPanel.add(Mode2LengthPanel);
        mode2SensitivityPanel.add(mode2SensLabel);
        mode2SensitivityPanel.add(mode2SensSlider);
        Mode2LengthPanel.add(mode2LengthLabel);
        Mode2LengthPanel.add(mode2LengthSlider);
        Mode2LengthPanel.add(blankField2);
        Mode2LengthPanel.add(mode2MinutesVal);

        mode2BasicPanel.add(mode2SensitivityPanel);
        mode2BasicPanel.add(mode2SensPercPanel);
        mode2SensPercPanel.add(mode2SensPercLabel);
        mode2SensPercPanel.add(mode2SensPercField);
        mode3BasicPanel.add(mode3BasicTitlePanel);
        mode3BasicTitlePanel.add(mode3BasicLabel);
        mode3BasicPanel.add(mode3LengthPanel);
        mode3SensitivityPanel.add(mode3SensLabel);
        mode3SensitivityPanel.add(mode3SensSlider);
        mode3LengthPanel.add(mode3LengthLabel);
        mode3LengthPanel.add(mode3LengthSlider);
        mode3LengthPanel.add(blankField3);
        mode3LengthPanel.add(mode3MinutesVal);
        mode3BasicPanel.add(mode3SensitivityPanel);
        mode3BasicPanel.add(mode3SensPercPanel);
        mode3SensPercPanel.add(mode3SensPercLabel);
        mode3SensPercPanel.add(mode3SensPercField);
        mode1LengthPanel.add(mode1LengthLabel);
        mode1LengthPanel.add(mode1LengthSlider);
        mode1LengthPanel.add(blankField1);
        mode1LengthPanel.add(mode1MinutesVal);
        mode1BasicPanel.add(mode1SensitivityPanel);
        mode1SensPercPanel.add(mode1SensPercLabel);
        mode1SensPercPanel.add(mode1SensPercField);
        mode1SensitivityPanel.add(mode1SensLabel, null);
        mode1SensitivityPanel.add(mode1SensSlider);
        mode1BasicPanel.add(mode1SensPercPanel);
        mode1Table.setDefaultEditor(Integer.class, intEdit);
        mode1Table.setMaximumSize(new Dimension(420, 240));
        mode1Table.setToolTipText("");
        mode1Table.setCellSelectionEnabled(true);


        mode1Table.getColumnModel().getColumn(1).setCellEditor(intEdit);
        mode1Table.getColumnModel().getColumn(2).setCellEditor(intEdit);
        mode1Table.getColumnModel().getColumn(3).setCellEditor(intEdit);
        mode1Table.getColumnModel().getColumn(4).setCellEditor(dblEdit);
        mode2Table.getColumnModel().getColumn(1).setCellEditor(intEdit);
        mode2Table.getColumnModel().getColumn(2).setCellEditor(intEdit);
        mode2Table.getColumnModel().getColumn(3).setCellEditor(intEdit);
        mode2Table.getColumnModel().getColumn(4).setCellEditor(dblEdit);
        mode3Table.getColumnModel().getColumn(1).setCellEditor(intEdit);
        mode3Table.getColumnModel().getColumn(2).setCellEditor(intEdit);
        mode3Table.getColumnModel().getColumn(3).setCellEditor(intEdit);
        mode3Table.getColumnModel().getColumn(4).setCellEditor(dblEdit);
        mode3Table.getColumnModel().getColumn(5).setCellEditor(moddblEdit);
        advSensTable.getColumnModel().getColumn(1).setCellEditor(sensintEdit);
        advSensTable.getColumnModel().getColumn(2).setCellEditor(sensintEdit);
        advSensTable.getColumnModel().getColumn(3).setCellEditor(sensintEdit);




        formatTable(mode1Table);
        formatTable(mode2Table);
        formatTable(mode3Table);
        formatTable(advSensTable);

        tabbedPane.addTab(tabnames[0], scrollPaneTab1);
        tabbedPane.addTab(tabnames[1], scrollPaneTab2);
        tabbedPane.addTab(tabnames[2], scrollPaneTab3);
        tabbedPane.addTab(tabnames[3], scrollPaneTab4);


        this.getContentPane().add(advancedPanel, null);
        this.getContentPane().add(closePanel);
        basicPanel.add(mode2BasicPanel,
                       new GridBagConstraints(1, 0,
                                              GridBagConstraints.REMAINDER,
                                              GridBagConstraints.REMAINDER, 1.0,
                                              1.0
                                              , GridBagConstraints.CENTER,
                                              GridBagConstraints.VERTICAL,
                                              new Insets(0, 0, 0, 0), 0, 0));
        advancedPanel.add(advancedTitleLabel,
                          new GridBagConstraints(0, 0,
                                                 GridBagConstraints.REMAINDER,
                                                 GridBagConstraints.REMAINDER,
                                                 0.0, 0.0
                                                 , GridBagConstraints.NORTH,
                                                 GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
        basicPanel.add(mode3BasicPanel,
                       new GridBagConstraints(2, 0,
                                              GridBagConstraints.REMAINDER,
                                              GridBagConstraints.REMAINDER, 1.0,
                                              1.0
                                              , GridBagConstraints.EAST,
                                              GridBagConstraints.VERTICAL,
                                              new Insets(0, 0, 0, 7), 0, 0));
        basicPanel.add(mode1BasicPanel,
                       new GridBagConstraints(0, 0,
                                              GridBagConstraints.REMAINDER,
                                              GridBagConstraints.REMAINDER, 1.0,
                                              1.0
                                              , GridBagConstraints.WEST,
                                              GridBagConstraints.VERTICAL,
                                              new Insets(0, 7, 0, 0), 0, 0));
        advancedPanel.add(mode1AdvPanel,
                          new GridBagConstraints(3, 2,
                                                 GridBagConstraints.REMAINDER,
                                                 GridBagConstraints.REMAINDER,
                                                 2.0, 2.0
                                                 , GridBagConstraints.NORTHWEST,
                                                 GridBagConstraints.NONE,
                                                 new Insets(40, 60, 10, 0), 0,
                                                 0));
        advancedPanel.add(mode2AdvPanel,
                          new GridBagConstraints(4, 2,
                                                 GridBagConstraints.REMAINDER,
                                                 GridBagConstraints.REMAINDER,
                                                 2.0, 2.0
                                                 , GridBagConstraints.NORTH,
                                                 GridBagConstraints.NONE,
                                                 new Insets(40, 0, 0, 0), 0, 0));
        advancedPanel.add(mode3AdvPanel,
                          new GridBagConstraints(5, 2,
                                                 GridBagConstraints.REMAINDER,
                                                 GridBagConstraints.REMAINDER,
                                                 2.0, 2.0
                                                 , GridBagConstraints.NORTHEAST,
                                                 GridBagConstraints.NONE,
                                                 new Insets(40, 0, 0, 60), 0, 1));

        scrollPaneTab1.add(mode1Table.getTableHeader());
        scrollPaneTab2.add(mode2Table.getTableHeader());
        scrollPaneTab3.add(mode3Table.getTableHeader());
//        scrollPaneTab3.getViewport().add(advSensTable);
        scrollPaneTab4.add(advSensTable.getTableHeader());
        advancedPanel.add(tabbedPane,
                          new GridBagConstraints(0, 3,
                                                 GridBagConstraints.REMAINDER,
                                                 GridBagConstraints.REMAINDER,
                                                 4.0, 4.0
                                                 , GridBagConstraints.NORTH,
                                                 GridBagConstraints.NONE,
                                                 new Insets(135, 0, 0, 0), 0, 0));
        mode1LengthSlider.setEnabled(false);
        mode1SensSlider.setEnabled(false);
        mode2LengthSlider.setEnabled(false);
        mode2SensSlider.setEnabled(false);
        mode3LengthSlider.setEnabled(false);
        mode3SensSlider.setEnabled(false);
        advancedPanel.setVisible(true);
//        advancedPanel.setVisible(false);

    }

    private void resizeTabbedPane(int tabnumber) throws Exception {
        if(tabnumber==0){tabbedPane.setPreferredSize(new Dimension(395, 289) );}
        if(tabnumber==1){tabbedPane.setPreferredSize(new Dimension(395, 289) );}
        if(tabnumber==2){tabbedPane.setPreferredSize(new Dimension(440, 177) );} //192
        if(tabnumber==3){tabbedPane.setPreferredSize(new Dimension(375, 173) );}

    }

    public void actionPerformed(ActionEvent e) {
      if(e.getSource().equals(rButton)) {
          //reqTable();
          new reqTable().start();
        //System.exit(0);
      }
      if( (e.getSource().equals(jMenuItem1) ) || (e.getSource().equals(closeButton))){
          Main.app.runtimeFrame.frame.setBounds(this.getX(), this.getY(), Main.app.runtimeFrame.frame.getWidth(), Main.app.runtimeFrame.frame.getHeight());
          Main.app.runtimeFrame.frame.setVisible(true);
          Main.app.runtimeFrame.frame.toFront();
          setVisible(false);
        this.dispose();
      }
      if(e.getSource().equals(advancedMenuItem)) {
          //don't show advanced settings unless the basic panel is showing
          if( advancedMenuItem.isSelected() == true ){
              advBounds.x = this.getRootPane().getX();advBounds.y = this.getRootPane().getY();
              advancedPanel.setVisible(true);
              this.getRootPane().setBounds(advBounds);
              this.getRootPane().setSize(advDim);
              advBounds.x = this.getX();advBounds.y = this.getY();
              this.setBounds(advBounds);
              this.setSize(advDim);


          }
          else{
              basBounds.x = this.getX();basBounds.y = this.getY();
              advancedPanel.setVisible(false);
              this.setBounds(basBounds);
              this.setSize(basDim);
              advBounds.x = this.getRootPane().getX();advBounds.y = this.getRootPane().getY();
              this.getRootPane().setBounds(basBounds);
              this.getRootPane().setSize(basDim);
              advancedPanel.setVisible(true);
          }
      }
      if(e.getSource().equals(sButton)) {
          new sendTables().start();
      }

  }



    public void stateChanged(ChangeEvent e) {

      //System.out.println("event equals =" + e.toString()+" from ="+e.getSource().getClass().getName());

      JSlider source = (JSlider)e.getSource();

      if (source.equals(mode1SensSlider)){
          //change mode1SensPercField display value
          mode1SensPercField.setText(String.valueOf(mode1SensPercent[(int)source.getValue()])+"%"  );
      }

      if (source.equals(mode2SensSlider)){
          //change mode2SensPercField display value
          mode2SensPercField.setText(String.valueOf(mode2SensPercent[(int)source.getValue()])+"%"  );
      }

      if (source.equals(mode3SensSlider)){
          //change mode3SensPercField display value
          mode3SensPercField.setText(String.valueOf(mode3SensPercent[(int)source.getValue()])+"%"  );
      }

      if (source.equals(mode1LengthSlider)){
          mode1MinutesVal.setText( (String.valueOf(mode1LengthSlider.getValue()) )+" Minutes");
      }

      if (source.equals(mode2LengthSlider)){
          mode2MinutesVal.setText( (String.valueOf(mode2LengthSlider.getValue()) )+" Minutes");
      }

      if (source.equals(mode3LengthSlider)){
          mode3MinutesVal.setText( (String.valueOf(mode3LengthSlider.getValue()) )+" Minutes");
      }
  }


    public void mode1SensorCombo_actionPerformed(ActionEvent e) {
//        System.out.println("a mode1SensorCombo action was performed");
    }

    public void mode2SensorCombo_actionPerformed(ActionEvent e) {
//        System.out.println("a mode2SensorCombo action was performed");
    }

    public void mode3SensorCombo_actionPerformed(ActionEvent e) {
//        System.out.println("a mode3SensorCombo action was performed");
    }

    public void mode1SampleCombo_actionPerformed(ActionEvent e) {
//        System.out.println("a mode1samplecombo action was performed");
    }

    public void mode2SampleCombo_actionPerformed(ActionEvent e) {
//        System.out.println("a mode2samplecombo action was performed");
    }

    public void mode3SampleCombo_actionPerformed(ActionEvent e) {
//        System.out.println("a mode3samplecombo action was performed");
    }

    public void mainTabbedPanel_stateChanged(ChangeEvent e) {
        String tab = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
        if (tab.equals(tabnames[0])){
            try{resizeTabbedPane(0);}catch(Exception ex){ex.printStackTrace();}
        }
        if (tab.equals(tabnames[1])){
            try{resizeTabbedPane(1);}catch(Exception ex){ex.printStackTrace();}
        }
        if (tab.equals(tabnames[2])){
            try{resizeTabbedPane(2);}catch(Exception ex){ex.printStackTrace();}
        }
        if (tab.equals(tabnames[3])){
            try{resizeTabbedPane(3);}catch(Exception ex){ex.printStackTrace();}
        }



    }

    public void formatTable(JTable table){
        for(int i=0;i<table.getColumnCount();i++){
            table.getColumnModel().getColumn(i).setCellRenderer(rend);
            ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            //set widths
            if (i==0){table.getColumnModel().getColumn(i).setMaxWidth(33);}     //.setPreferredWidth(8);}
            if ( (i==1)||(i==2)||(i==3) ){table.getColumnModel().getColumn(i).setPreferredWidth(45);}
            if ((i == 4)||(i == 5)){table.getColumnModel().getColumn(i).setPreferredWidth(120);}
            else{table.getColumnModel().getColumn(i).setPreferredWidth(35);}
        }



    }

    static class mode1TableModel extends AbstractTableModel implements TableModelListener {
        String[] columnNames  = {"Step", "Red", "Green", "Blue", "Carrier Frequency"};
        Object[][] data  = {
            {new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(1), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(2), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(3), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(4), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(5), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(6), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(7), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(8), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(9), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(10), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(11), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(12), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(13), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(14), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
        };
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            return data.length;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                //TableModel model = this;
                //String columnName = model.getColumnName(column);
                //Object data = model.getValueAt(row, column);

                // Do something with the data...
      }
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 1) {
                return false;
            } else {
                return true;
            }
        }

        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
        private void printDebugData() {
             int numRows = getRowCount();
             int numCols = getColumnCount();

             for (int i=0; i < numRows; i++) {
                 System.out.print("    row " + i + ":");
                 for (int j=0; j < numCols; j++) {
                     System.out.print("  " + data[i][j]);
                 }
                 System.out.println();
             }
             System.out.println("--------------------------");
        }



    }

    static class mode2TableModel extends AbstractTableModel implements TableModelListener {
        String[] columnNames = {"Step", "Red", "Green", "Blue", "Carrier Frequency"};
        Object[][] data = {
            {new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(1), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(2), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(3), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(4), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(5), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(6), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(7), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(8), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(9), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(10), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(11), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(12), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(13), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
            {new Integer(14), new Integer(0), new Integer(0), new Integer(0), new Double(0)},
        };

        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            return data.length;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
        public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                //TableModel model = this;
                //String columnName = model.getColumnName(column);
                //Object data = model.getValueAt(row, column);

                // Do something with the data...
      }
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 1) {
                return false;
            } else {
                return true;
            }
        }
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }


    static class mode3TableModel extends AbstractTableModel implements TableModelListener {
        String[] columnNames = {"Step", "Red", "Green", "Blue", "Carrier Frequency", "Modulation Frequency"};
        Object[][] data = {
                {new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Double(0), new Double(0)},
                {new Integer(1), new Integer(0), new Integer(0), new Integer(0), new Double(0), new Double(0)},
                {new Integer(2), new Integer(0), new Integer(0), new Integer(0), new Double(0), new Double(0)},
                {new Integer(3), new Integer(0), new Integer(0), new Integer(0), new Double(0), new Double(0)},
                {new Integer(4), new Integer(0), new Integer(0), new Integer(0), new Double(0), new Double(0)},
                {new Integer(5), new Integer(0), new Integer(0), new Integer(0), new Double(0), new Double(0)},
                {new Integer(6), new Integer(0), new Integer(0), new Integer(0), new Double(0), new Double(0)},
                {new Integer(7), new Integer(0), new Integer(0), new Integer(0), new Double(0), new Double(0)},
};

        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            return data.length;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
        public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                //TableModel model = this;
                //String columnName = model.getColumnName(column);
                //Object data = model.getValueAt(row, column);

                // Do something with the data...
      }
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 1) {
                return false;
            } else {
                return true;
            }
        }
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }

    static class sensTableModel extends AbstractTableModel implements TableModelListener {
        String[] columnNames = {"Level", "Mode 1", "Mode 2", "Mode 3"};
        Object[][] data = {
                    {new Integer(0), new Integer(0), new Integer(0), new Integer(0)},
                    {new Integer(1), new Integer(0), new Integer(0), new Integer(0)},
                    {new Integer(2), new Integer(0), new Integer(0), new Integer(0)},
                    {new Integer(3), new Integer(0), new Integer(0), new Integer(0)},
                    {new Integer(4), new Integer(0), new Integer(0), new Integer(0)},
                    {new Integer(5), new Integer(0), new Integer(0), new Integer(0)},
                    {new Integer(6), new Integer(0), new Integer(0), new Integer(0)},
                    {new Integer(7), new Integer(0), new Integer(0), new Integer(0)},
};

        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            return data.length;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
        public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                //TableModel model = this;
                //String columnName = model.getColumnName(column);
                //Object data = model.getValueAt(row, column);

                // Do something with the data...
      }
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 1) {
                return false;
            } else {
                return true;
            }
        }
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }


}





class TableEditorFrame_tabbedpane_changeAdapter implements ChangeListener {
    private TableEditorFrame adaptee;
    TableEditorFrame_tabbedpane_changeAdapter(TableEditorFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void stateChanged(ChangeEvent e) {
        adaptee.mainTabbedPanel_stateChanged(e);
    }
}

class TableEditorFrame_mode1SensorCombo_actionAdapter implements ActionListener {
    private TableEditorFrame adaptee;
    TableEditorFrame_mode1SensorCombo_actionAdapter(TableEditorFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.mode1SensorCombo_actionPerformed(e);
    }
}

class TableEditorFrame_mode1SampleCombo_actionAdapter implements ActionListener {
    private TableEditorFrame adaptee;
    TableEditorFrame_mode1SampleCombo_actionAdapter(TableEditorFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.mode1SampleCombo_actionPerformed(e);
    }
}


class TableEditorFrame_mode2SensorCombo_actionAdapter implements ActionListener {
    private TableEditorFrame adaptee;
    TableEditorFrame_mode2SensorCombo_actionAdapter(TableEditorFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.mode2SensorCombo_actionPerformed(e);
    }
}

class TableEditorFrame_mode2SampleCombo_actionAdapter implements ActionListener {
    private TableEditorFrame adaptee;
    TableEditorFrame_mode2SampleCombo_actionAdapter(TableEditorFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.mode2SampleCombo_actionPerformed(e);
    }
}


class TableEditorFrame_mode3SensorCombo_actionAdapter implements ActionListener {
    private TableEditorFrame adaptee;
    TableEditorFrame_mode3SensorCombo_actionAdapter(TableEditorFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.mode3SensorCombo_actionPerformed(e);
    }
}

class TableEditorFrame_mode3SampleCombo_actionAdapter implements ActionListener {
    private TableEditorFrame adaptee;
    TableEditorFrame_mode3SampleCombo_actionAdapter(TableEditorFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.mode3SampleCombo_actionPerformed(e);
    }
}


class sendTables extends Thread {

    OutputStream streamOut;
    InputStream streamIn;
    byte[] buf0 = new byte[6];
    byte[] buf1 = new byte[6];
    byte[] buf2 = new byte[6];
    byte[] buf3 = new byte[6];
    byte[] buf4 = new byte[6];

    byte[] t0block = new byte[24];
    byte[] t1block = new byte[24];
    byte[] t2block = new byte[26];
    byte[] t3block = new byte[24];
    byte[] t4block = new byte[24];

    int[] eraseTable0cmd = {0xa3, 0x46, 0x07, 0x21, 0x00, 0x01, 0x11};
    int[] eraseTable1cmd = {0xa3, 0x46, 0x07, 0x21, 0x01, 0x01, 0x12};
    int[] eraseTable2cmd = {0xa3, 0x46, 0x07, 0x21, 0x02, 0x01, 0x13};
    int[] eraseTable3cmd = {0xa3, 0x46, 0x07, 0x21, 0x03, 0x01, 0x14};
    int[] eraseTable4cmd = {0xa3, 0x46, 0x07, 0x21, 0x04, 0x01, 0x15};

    sendTables() {

        streamOut = SerialMain.impl.out;
        streamIn = SerialMain.impl.in;
        TableEditorFrame.commMsgLabel.setText("Writing Tables");


    }

    public void run() {



            BufferedInputStream bufin = new BufferedInputStream(streamIn);
            int sleepbetweentables = 800;
            int sleepbetweenbytes = 150;

//sample rates
            if (TableEditorFrame.mode1SampleCombo.getSelectedIndex() == 0){TableEditorFrame.table0WriteByte[50] = 5;}
            if (TableEditorFrame.mode1SampleCombo.getSelectedIndex() == 1){TableEditorFrame.table0WriteByte[50] = 10;}
            if (TableEditorFrame.mode1SampleCombo.getSelectedIndex() == 2){TableEditorFrame.table0WriteByte[50] = 15;}
            if (TableEditorFrame.mode1SampleCombo.getSelectedIndex() == 3){TableEditorFrame.table0WriteByte[50] = 20;}
            if (TableEditorFrame.mode2SampleCombo.getSelectedIndex() == 0){TableEditorFrame.table0WriteByte[53] = 5;}
            if (TableEditorFrame.mode2SampleCombo.getSelectedIndex() == 1){TableEditorFrame.table0WriteByte[53] = 10;}
            if (TableEditorFrame.mode2SampleCombo.getSelectedIndex() == 2){TableEditorFrame.table0WriteByte[53] = 15;}
            if (TableEditorFrame.mode2SampleCombo.getSelectedIndex() == 3){TableEditorFrame.table0WriteByte[53] = 20;}
            if (TableEditorFrame.mode3SampleCombo.getSelectedIndex() == 0){TableEditorFrame.table0WriteByte[56] = 5;}
            if (TableEditorFrame.mode3SampleCombo.getSelectedIndex() == 1){TableEditorFrame.table0WriteByte[56] = 10;}
            if (TableEditorFrame.mode3SampleCombo.getSelectedIndex() == 2){TableEditorFrame.table0WriteByte[56] = 15;}
            if (TableEditorFrame.mode3SampleCombo.getSelectedIndex() == 3){TableEditorFrame.table0WriteByte[56] = 20;}
//session lengths
            TableEditorFrame.table0WriteByte[49] = (byte) TableEditorFrame.mode1LengthSlider.getValue();
            TableEditorFrame.table0WriteByte[52] = (byte) TableEditorFrame.mode2LengthSlider.getValue();
            TableEditorFrame.table0WriteByte[55] = (byte) TableEditorFrame.mode3LengthSlider.getValue();
//last sensitivity index
            TableEditorFrame.table1WriteByte[0] = (byte) TableEditorFrame.mode1SensSlider.getValue();
            TableEditorFrame.table1WriteByte[1] = (byte) TableEditorFrame.mode2SensSlider.getValue();
            TableEditorFrame.table1WriteByte[2] = (byte) TableEditorFrame.mode3SensSlider.getValue();
//sensor type
            if (TableEditorFrame.mode1SensorCombo.getSelectedIndex() == 0) {TableEditorFrame.table0WriteByte[48] = 1;} else {TableEditorFrame.table0WriteByte[48] = 4;}
            if (TableEditorFrame.mode2SensorCombo.getSelectedIndex() == 1) {TableEditorFrame.table0WriteByte[51] = 2;} else {TableEditorFrame.table0WriteByte[51] = 8;}
            if (TableEditorFrame.mode3SensorCombo.getSelectedIndex() == 0) {TableEditorFrame.table0WriteByte[54] = 3;} else {TableEditorFrame.table0WriteByte[54] = 12;}
//sensitivity values
//mode1
        for (int i =0;i<8;i++){
            Integer sens = new Integer( TableEditorFrame.advSensTable.getValueAt(i,1).toString() );
            //Integer sens = new Integer ((advSensTableData[i][1]).toString());
            int ia = (((sens.intValue()))>>8);
            int ib = (sens.intValue());
            TableEditorFrame.table0WriteByte[2*i]= (byte)ia;
            TableEditorFrame.table0WriteByte[2*i+1]= (byte)ib;
        }
//mode2
        for (int i =0;i<8;i++){
            Integer sens = new Integer( TableEditorFrame.advSensTable.getValueAt(i,2).toString() );
            //Integer sens = new Integer ((advSensTableData[i][2]).toString());
            int ia = (((sens.intValue()))>>8);
            int ib = (sens.intValue());
            TableEditorFrame.table0WriteByte[2*i+16]= (byte)ia;
            TableEditorFrame.table0WriteByte[2*i+17]= (byte)ib;
        }
//mode3
        for (int i =0;i<8;i++){
            Integer sens = new Integer( TableEditorFrame.advSensTable.getValueAt(i,3).toString() );
            //Integer sens = new Integer ((advSensTableData[i][3]).toString());
            int ia = (((sens.intValue()))>>8);
            int ib = (sens.intValue());
            TableEditorFrame.table0WriteByte[2*i+32]= (byte)ia;
            TableEditorFrame.table0WriteByte[2*i+33]= (byte)ib;
        }
        /*for RGB (modes1&2) (<8 for mode 3)*/
//RGBMODE1
            for (int i =0;i<15;i++){

                Integer tempr = new Integer( TableEditorFrame.mode1Table.getValueAt(i,1).toString() );
                Integer tempg = new Integer(  TableEditorFrame.mode1Table.getValueAt(i,2).toString()    );
                Integer tempb = new Integer( TableEditorFrame.mode1Table.getValueAt(i,3).toString()    );

                int tempR = (0xefff&(tempr.intValue()));
                int tempG = ((0xefff&(tempg.intValue()))<<5);
                int tempB = ((0xefff&(tempb.intValue()))<<10);
                int temp = ((tempR + tempG + tempB)&0xffff)  ;
                TableEditorFrame.table2WriteByte[2*i] = (byte)(0x00ff&(temp>>8)); //System.out.print(" "+(0xff&TableEditorFrame.table2WriteByte[2*i]));
                TableEditorFrame.table2WriteByte[2*i+1] = (byte)(0x00ff&(temp)); //System.out.print(" "+(0xff&TableEditorFrame.table2WriteByte[2*i+1]));

            }                //System.out.println("---- was table2");
//RGBMODE2
            for (int i =0;i<15;i++){

                Integer tempr = new Integer( TableEditorFrame.mode2Table.getValueAt(i,1).toString() );
                Integer tempg = new Integer(  TableEditorFrame.mode2Table.getValueAt(i,2).toString()    );
                Integer tempb = new Integer( TableEditorFrame.mode2Table.getValueAt(i,3).toString()    );

                int tempR = (0xefff&(tempr.intValue()));
                int tempG = ((0xefff&(tempg.intValue()))<<5);
                int tempB = ((0xefff&(tempb.intValue()))<<10);
                int temp = ((tempR + tempG + tempB)&0xffff)  ;
                TableEditorFrame.table3WriteByte[2*i] = (byte)(0x00ff&(temp>>8));//System.out.print(" "+(0xff&TableEditorFrame.table3WriteByte[2*i]));
                TableEditorFrame.table3WriteByte[2*i+1] = (byte)(0x00ff&(temp));//System.out.print(" "+(0xff&TableEditorFrame.table3WriteByte[2*i+1]));

            }               // System.out.println("---- was table3");
//RGBMODE3
            for (int i =0;i<8;i++){

                Integer tempr = new Integer( TableEditorFrame.mode3Table.getValueAt(i,1).toString() );
                Integer tempg = new Integer(  TableEditorFrame.mode3Table.getValueAt(i,2).toString()    );
                Integer tempb = new Integer( TableEditorFrame.mode3Table.getValueAt(i,3).toString()    );

                int tempR = (0xefff&(tempr.intValue()));
                int tempG = ((0xefff&(tempg.intValue()))<<5);
                int tempB = ((0xefff&(tempb.intValue()))<<10);
                int temp = ((tempR + tempG + tempB)&0xffff)  ;
                TableEditorFrame.table4WriteByte[2*i] = (byte)(0x00ff&(temp>>8));//System.out.print(" "+(0xff&TableEditorFrame.table4WriteByte[2*i]));
                TableEditorFrame.table4WriteByte[2*i+1] = (byte)(0x00ff&(temp));//System.out.print(" "+(0xff&TableEditorFrame.table4WriteByte[2*i+1]));

            }//System.out.println("---- was table4");
//AUDIOMODE1

            int counter = 32;

            for (int i = 0; i < 15; i++) {
                Float freq = new Float( TableEditorFrame.mode1Table.getValueAt(i,4).toString());
//        Float freq = new Float(mode1TableData[i][4].toString());
//        System.out.println(i + " freq =" + freq);
                float ffreq = freq.floatValue() * 10;
                int ifreq = (int) ffreq;
                float tempf = 0xc0ff & (((ifreq * 256) / 10000) / 10);
                float tempr = ((((ifreq * 256) / 10) % 10000)) << 8;
                tempr = tempr / 10000;
//        System.out.println("from mode1tabledata = " + i + " tempf = " + tempf + " and tempr =" + tempr);
                tempf = Math.round(tempf);
                tempr = Math.round(tempr);

                byte cF = (byte) (tempf);
                byte cR = (byte) (tempr);
                TableEditorFrame.table2WriteByte[counter] = cF;
                counter++;
                TableEditorFrame.table2WriteByte[counter] = cR;
                counter++;
            }
//AUDIOMODE2
        counter = 32;
            for (int i = 0; i < 15; i++) {
                Float freq = new Float( TableEditorFrame.mode2Table.getValueAt(i,4).toString());
                //Float freq = new Float(mode2TableData[i][4].toString());
//        System.out.println(i + " freq =" + freq);
                float ffreq = freq.floatValue() * 10;
                int ifreq = (int) ffreq;
                float tempf = 0xc0ff & (((ifreq * 256) / 10000) / 10);
                float tempr = ((((ifreq * 256) / 10) % 10000)) << 8;
                tempr = tempr / 10000;
//        System.out.println("from mode2tabledata = " + i + " tempf = " + tempf + " and tempr =" + tempr);
                tempf = Math.round(tempf);
                tempr = Math.round(tempr);
                byte cF = (byte) (tempf);
                byte cR = (byte) (tempr);
                TableEditorFrame.table3WriteByte[counter] = cF;
                counter++;
                TableEditorFrame.table3WriteByte[counter] = cR;
                counter++;
            }
//AUDIOMODE3
            int modcounter = 18;
            counter = 18;
            for (int i = 0; i < 8; i++) {
                Float freq = new Float( TableEditorFrame.mode3Table.getValueAt(i,4).toString());
                Float modu = new Float( TableEditorFrame.mode3Table.getValueAt(i,5).toString());
                //Float freq = new Float(mode3TableData[i][4].toString());
                //Float modu = new Float(mode3TableData[i][5].toString());
//        System.out.println(i + " freq =" + freq);
//        System.out.println(i + " modu =" + modu);
                float ffreq = freq.floatValue() * 10;
                float fmodu = modu.floatValue();
                int ifreq = (int) ffreq;

                float tempf = 0xc0ff & (((ifreq * 256) / 10000) / 10);
                float tempr = ((((ifreq * 256) / 10) % 10000)) << 8;
                float tempm = (((fmodu * 256) / 10000) * 256);

                tempr = tempr / 10000;
//        System.out.println("from mode3tabledata = " + i + " tempf = " + tempf + " and tempr =" + tempr+" and imodu = "+tempm);
                tempf = Math.round(tempf);
                tempr = Math.round(tempr);
                int itempm = Math.round(tempm);
                byte cF = (byte) (tempf);
                byte cR = (byte) (tempr);
                byte mD = (byte) (itempm);
                TableEditorFrame.table4WriteByte[counter] = cF;
                counter++;
                TableEditorFrame.table4WriteByte[counter] = cR;
                counter++;
                TableEditorFrame.table4WriteByte[counter] = mD;
                counter++;

            }

        /*

        for (int i =0;i<table0WriteByte.length;i++){
            System.out.println("table0WriteByte["+i+"] = "+table0WriteByte[i]);
        }
        for (int i =0;i<table1WriteByte.length;i++){
            System.out.println("table1WriteByte["+i+"] = "+table1WriteByte[i]);
        }
        for (int i =0;i<table2WriteByte.length;i++){
            System.out.println("table2WriteByte["+i+"] = "+table2WriteByte[i]);
        }
        for (int i =0;i<table3WriteByte.length;i++){
            System.out.println("table3WriteByte["+i+"] = "+table3WriteByte[i]);
        }
        for (int i =0;i<table4WriteByte.length;i++){
            System.out.println("table4WriteByte["+i+"] = "+(table4WriteByte[i])   );
        }
        */


//table0
//erase table0
        try  {for (int i=0; i < eraseTable0cmd.length; i++){streamOut.write(eraseTable0cmd[i]);Thread.sleep(sleepbetweenbytes);}
            bufin.read(buf0);Thread.sleep(sleepbetweentables);
           // if(buf0[3]==10){

//block0
        t0block[0]=(byte)0xa3;t0block[1]=(byte)0x46;t0block[2]=(byte)0x18;t0block[3]=(byte)0x22;t0block[4]=(byte)0x00;t0block[5]=(byte)0x00;
        for(int i=0;i<16;i++){t0block[i+6] = TableEditorFrame.table0WriteByte[i];}
        int checksum = 0;
        for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t0block[i]);}
        t0block[22] = (byte)(0xff&(checksum>>8));t0block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t0b0");for(int i=0;i<24;i++){System.out.print("+:"+t0block[i]);}
                for (int i=0; i < t0block.length; i++){streamOut.write(t0block[i]);Thread.sleep(sleepbetweenbytes);} //send t0block0
                bufin.read(buf0);Thread.sleep(sleepbetweentables);

//block1
        t0block[5]=(byte)0x01;
        for(int i=0;i<16;i++){t0block[i+6] = TableEditorFrame.table0WriteByte[i+16];}
        checksum = 0;
        for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t0block[i]);}
        t0block[22] = (byte)(0xff&(checksum>>8));t0block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t0b1");for(int i=0;i<24;i++){System.out.print("+:"+t0block[i]);}
                for (int i=0; i < t0block.length; i++){streamOut.write(t0block[i]);Thread.sleep(sleepbetweenbytes);} //send t0block1
                bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block2
        t0block[5]=(byte)0x02;
        for(int i=0;i<16;i++){t0block[i+6] = TableEditorFrame.table0WriteByte[i+32];}
        checksum = 0;
        for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t0block[i]);}
        t0block[22] = (byte)(0xff&(checksum>>8));t0block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t0b2");for(int i=0;i<24;i++){System.out.print("+:"+t0block[i]);}
                for (int i=0; i < t0block.length; i++){streamOut.write(t0block[i]);Thread.sleep(sleepbetweenbytes);} //send t0block2
                bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block3
        t0block[5]=(byte)0x03;
        for(int i=0;i<16;i++){t0block[i+6] = TableEditorFrame.table0WriteByte[i+48];}
        checksum = 0;
        for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t0block[i]);}
        t0block[22] = (byte)(0xff&(checksum>>8));t0block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t0b3");for(int i=0;i<24;i++){System.out.print("+:"+t0block[i]);}
                for (int i=0; i < t0block.length; i++){streamOut.write(t0block[i]);Thread.sleep(sleepbetweenbytes);} //send t0block3
                bufin.read(buf0);Thread.sleep(sleepbetweentables);
            //}
        }
        catch(Exception ex){ex.printStackTrace();}


//table2
        try  {for (int i=0; i < eraseTable2cmd.length; i++){streamOut.write(eraseTable2cmd[i]);Thread.sleep(sleepbetweenbytes);} //erase table2
            bufin.read(buf0);Thread.sleep(sleepbetweentables);
           // if(buf0[3]==10){

//block0
        t2block[0]=(byte)0xa3;t2block[1]=(byte)0x46;t2block[2]=(byte)0x18;t2block[3]=(byte)0x22;t2block[4]=(byte)0x02;t2block[5]=(byte)0x00;
        for(int i=0;i<16;i++){t2block[i+6] = TableEditorFrame.table2WriteByte[i];}
        int checksum = 0;
        for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t2block[i]);}
        t2block[22] = (byte)(0xff&(checksum>>8));t2block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t2b0");for(int i=0;i<24;i++){System.out.print("+:"+t2block[i]);}
                for (int i=0; i < t2block.length; i++){streamOut.write(t2block[i]);Thread.sleep(sleepbetweenbytes);} //send t2block0
                bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block1
        t2block[5]=(byte)0x01;
        for(int i=0;i<16;i++){t2block[i+6] = TableEditorFrame.table2WriteByte[i+16];}
        checksum = 0;
        for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t2block[i]);}
        t2block[22] = (byte)(0xff&(checksum>>8));t2block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t2b1");for(int i=0;i<24;i++){System.out.print("+:"+t2block[i]);}
                for (int i=0; i < t2block.length; i++){streamOut.write(t2block[i]);Thread.sleep(sleepbetweenbytes);} //send t2block1
                bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block2
        t2block[5]=(byte)0x02;
        for(int i=0;i<16;i++){t2block[i+6] = TableEditorFrame.table2WriteByte[i+32];}
        checksum = 0;
        for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t2block[i]);}
        t2block[22] = (byte)(0xff&(checksum>>8));t2block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t2b2");for(int i=0;i<24;i++){System.out.print("+:"+t2block[i]);}
                for (int i=0; i < t2block.length; i++){streamOut.write(t2block[i]);Thread.sleep(sleepbetweenbytes);} //send t2block2
                bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block3
        t2block[5]=(byte)0x03;
        for(int i=0;i<16;i++){t2block[i+6] = TableEditorFrame.table2WriteByte[i+48];}
        checksum = 0;
        for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t2block[i]);}
        t2block[22] = (byte)(0xff&(checksum>>8));t2block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t2b3");for(int i=0;i<24;i++){System.out.print("+:"+t2block[i]);}
                for (int i=0; i < t2block.length; i++){streamOut.write(t2block[i]);Thread.sleep(sleepbetweenbytes);} //send t2block3
                bufin.read(buf0);Thread.sleep(sleepbetweentables);
            //}
        }
        catch(Exception ex){ex.printStackTrace();}

//table3
        try  {for (int i=0; i < eraseTable3cmd.length; i++){streamOut.write(eraseTable3cmd[i]);Thread.sleep(sleepbetweenbytes);} //erase table3
            bufin.read(buf0);Thread.sleep(sleepbetweentables);
           // if(buf0[3]==10){

//block0
        t3block[0]=(byte)0xa3;t3block[1]=(byte)0x46;t3block[2]=(byte)0x18;t3block[3]=(byte)0x22;t3block[4]=(byte)0x03;t3block[5]=(byte)0x00;
        for(int i=0;i<16;i++){t3block[i+6] = TableEditorFrame.table3WriteByte[i];}
        int checksum = 0;
        for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t3block[i]);}
        t3block[22] = (byte)(0xff&(checksum>>8));t3block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t3b0");for(int i=0;i<24;i++){System.out.print("+:"+t3block[i]);}
                for (int i=0; i < t3block.length; i++){streamOut.write(t3block[i]);Thread.sleep(sleepbetweenbytes);} //send t3block0
                bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block1
        t3block[5]=(byte)0x01;
        for(int i=0;i<16;i++){t3block[i+6] = TableEditorFrame.table3WriteByte[i+16];}
        checksum = 0;
        for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t3block[i]);}
        t3block[22] = (byte)(0xff&(checksum>>8));t3block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t3b1");for(int i=0;i<24;i++){System.out.print("+:"+t3block[i]);}
                for (int i=0; i < t3block.length; i++){streamOut.write(t3block[i]);Thread.sleep(sleepbetweenbytes);} //send t3block1
                bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block2
        t3block[5]=(byte)0x02;
        for(int i=0;i<16;i++){t3block[i+6] = TableEditorFrame.table3WriteByte[i+32];}
        checksum = 0;
        for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t3block[i]);}
        t3block[22] = (byte)(0xff&(checksum>>8));t3block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t3b2");for(int i=0;i<24;i++){System.out.print("+:"+t3block[i]);}
                for (int i=0; i < t3block.length; i++){streamOut.write(t3block[i]);Thread.sleep(sleepbetweenbytes);} //send t3block2
                bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block3
        t3block[5]=(byte)0x03;
        for(int i=0;i<16;i++){t3block[i+6] = TableEditorFrame.table3WriteByte[i+48];}
        checksum = 0;
        for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t3block[i]);}
        t3block[22] = (byte)(0xff&(checksum>>8));t3block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t3b3");for(int i=0;i<24;i++){System.out.print("+:"+t3block[i]);}
                for (int i=0; i < t3block.length; i++){streamOut.write(t3block[i]);Thread.sleep(sleepbetweenbytes);} //send t3block3
                bufin.read(buf0);Thread.sleep(sleepbetweentables);
            //}
        }
        catch(Exception ex){ex.printStackTrace();}


//table4
        try  {for (int i=0; i < eraseTable4cmd.length; i++){streamOut.write(eraseTable4cmd[i]);Thread.sleep(sleepbetweenbytes);} //erase table4
            bufin.read(buf0);Thread.sleep(sleepbetweentables);
           // if(buf0[3]==10){

//block0
        t4block[0]=(byte)0xa3;t4block[1]=(byte)0x46;t4block[2]=(byte)0x18;t4block[3]=(byte)0x22;t4block[4]=(byte)0x04;t4block[5]=(byte)0x00;
        for(int i=0;i<16;i++){t4block[i+6] = TableEditorFrame.table4WriteByte[i];}
        int checksum = 0;
        for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t4block[i]);}
        t4block[22] = (byte)(0xff&(checksum>>8));t4block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t3b0");for(int i=0;i<24;i++){System.out.print("+:"+t4block[i]);}
                for (int i=0; i < t4block.length; i++){streamOut.write(t4block[i]);Thread.sleep(sleepbetweenbytes);} //send t4block0
                bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block1
        t4block[5]=(byte)0x01;
        for(int i=0;i<16;i++){t4block[i+6] = TableEditorFrame.table4WriteByte[i+16];}
        checksum = 0;
        for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t4block[i]);}
        t4block[22] = (byte)(0xff&(checksum>>8));t4block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t3b1");for(int i=0;i<24;i++){System.out.print("+:"+t4block[i]);}
                for (int i=0; i < t4block.length; i++){streamOut.write(t4block[i]);Thread.sleep(sleepbetweenbytes);} //send t4block1
                bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block2
        t4block[5]=(byte)0x02;
        for(int i=0;i<16;i++){t4block[i+6] = TableEditorFrame.table4WriteByte[i+32];}
        checksum = 0;
        for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t4block[i]);}
        t4block[22] = (byte)(0xff&(checksum>>8));t4block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t3b2");for(int i=0;i<24;i++){System.out.print("+:"+t4block[i]);}
                for (int i=0; i < t4block.length; i++){streamOut.write(t4block[i]);Thread.sleep(sleepbetweenbytes);} //send t4block2
                bufin.read(buf0);Thread.sleep(sleepbetweentables);
//block3
        t4block[5]=(byte)0x03;
        for(int i=0;i<16;i++){t4block[i+6] = TableEditorFrame.table4WriteByte[i+48];}
        checksum = 0;
        for(int i=0;i<22;i++){checksum = checksum+(0x00ff&t4block[i]);}
        t4block[22] = (byte)(0xff&(checksum>>8));t4block[23] = (byte)  (0x00ff&checksum);
//System.out.println("t3b3");for(int i=0;i<24;i++){System.out.print("+:"+t4block[i]);}
                for (int i=0; i < t4block.length; i++){streamOut.write(t4block[i]);Thread.sleep(sleepbetweenbytes);} //send t4block3
                bufin.read(buf0);Thread.sleep(sleepbetweentables);
            //}


        }
        catch(Exception ex){ex.printStackTrace();
                    }



        /*
        try{
            for (int i=0; i < eraseTable0cmd.length; i++){streamOut.write(eraseTable0cmd[i]);Thread.sleep(sleepbetweenbytes);} //erase table0
            bufin.read(buf0);Thread.sleep(sleepbetweentables);
            if(buf0[3]==10){for (int i=0; i < t0block.length; i++){streamOut.write(t0block[i]);Thread.sleep(sleepbetweenbytes);} //send block0table
                bufin.read(buf0);Thread.sleep(sleepbetweentables);
            }
        }


            catch(Exception ex){ex.printStackTrace();}

        */




//erase table get ok
//send blocks
//checksum 2 bytes - add data + command bytes


        TableEditorFrame.commMsgLabel.setText("");



    }


}


class reqTable extends Thread {

    OutputStream streamOut;
    InputStream streamIn;


  reqTable() {
      streamOut = SerialMain.impl.out;
      streamIn = SerialMain.impl.in;
      TableEditorFrame.commMsgLabel.setText("Reading Tables");
  }

  public void run() {

      BufferedInputStream bufin = new BufferedInputStream(streamIn);
      try{bufin.skip(streamIn.available());}catch(Exception e){e.printStackTrace();};

      int[] getTable0cmd = {0xa3, 0x46, 0x07, 0x20, 0x00, 0x01, 0x10};
      int[] getTable1cmd = {0xa3, 0x46, 0x07, 0x20, 0x01, 0x01, 0x11};
      int[] getTable2cmd = {0xa3, 0x46, 0x07, 0x20, 0x02, 0x01, 0x12};
      int[] getTable3cmd = {0xa3, 0x46, 0x07, 0x20, 0x03, 0x01, 0x13};
      int[] getTable4cmd = {0xa3, 0x46, 0x07, 0x20, 0x04, 0x01, 0x14};
      byte[] table0 = new byte[65];
      byte[] table1 = new byte[64];
      byte[] table2 = new byte[71];
      byte[] table3 = new byte[71];
      byte[] table4 = new byte[71];

      int sleepbetweentables = 10;
      int sleepbetweenbytes = 150;

      try{



          //send command to get table0
          for (int i=0; i < getTable0cmd.length; i++){streamOut.write(getTable0cmd[i]);Thread.sleep(sleepbetweenbytes);}
          bufin.read(table0);
          Thread.sleep(sleepbetweentables);
          //send command to get table1
          for (int z = 0; z < getTable1cmd.length; z++) {streamOut.write(getTable1cmd[z]);Thread.sleep(sleepbetweenbytes);}
          bufin.read(table1);
          Thread.sleep(sleepbetweentables);
          //send command to get table2
          for (int z=0; z < getTable2cmd.length; z++){streamOut.write(getTable2cmd[z]);Thread.sleep(sleepbetweenbytes);}
          bufin.read(table2);
          Thread.sleep(sleepbetweentables);
          //send command to get table3
          for (int z=0; z < getTable3cmd.length; z++){streamOut.write(getTable3cmd[z]);Thread.sleep(sleepbetweenbytes);}
          bufin.read(table3);
          Thread.sleep(sleepbetweentables);
          //send command to get table4
          for (int z=0; z < getTable4cmd.length; z++){streamOut.write(getTable4cmd[z]);Thread.sleep(sleepbetweenbytes);}
          bufin.read(table4);
          Thread.sleep(sleepbetweentables);


          //set session length in minutes
              TableEditorFrame.mode1LengthSlider.setValue(table0[54]);
              TableEditorFrame.mode2LengthSlider.setValue(table0[57]);
              TableEditorFrame.mode3LengthSlider.setValue(table0[60]);

          //set modeXSensPercent array for displaying modeXSensPercField display value
          for (int i =0;i<TableEditorFrame.mode1SensPercent.length;i++){
              int m1 = (i*2)+5;  //find mode1 senspercent location in table
              int m2 = (i*2)+21; //find mode2 senspercent location in table
              int m3 = (i*2)+37; //find mode3 senspercent location in table
              TableEditorFrame.mode1SensPercent[i]= String.valueOf(  (  ((table0[m1])  &0xff)  <<8)  + ((table0[m1+1])&0xff)  );
              TableEditorFrame.mode2SensPercent[i]= String.valueOf(  (  ((table0[m2])  &0xff)  <<8)  +(table0[m2+1])&0xff);
              TableEditorFrame.mode3SensPercent[i]= String.valueOf(  (  ((table0[m3])  &0xff)  <<8)  +(table0[m3+1])&0xff);
          }

          //get version information
          byte[] versb = new byte[6];
          byte[] dateb = new byte[6];
          String version = "";
          String date = "";
          for(int i = 8;i<14;i++){
              version = version+(char)table1[i];
          }
          for(int i = 14;i<20;i++){
              date = date+((char)(table1[i]&0xff));
          }

          //fill advSensTable with sensitivity percentages
          for(int i=0;i<TableEditorFrame.mode1SensPercent.length;i++){
              TableEditorFrame.advSensTable.setValueAt(new Integer(TableEditorFrame.mode1SensPercent[i]), i, 1 );
              TableEditorFrame.advSensTable.setValueAt(new Integer(TableEditorFrame.mode2SensPercent[i]), i, 2 );
              TableEditorFrame.advSensTable.setValueAt(new Integer(TableEditorFrame.mode3SensPercent[i]), i, 3 );
          }


          //get RGB for mode 1
          for(int i=3;i<18;i++){
              int word = 0;
              word = (( (0x00ff&table2[(2*i)-1])<<8 ) + (0x00ff&table2[(2*i)]));
              TableEditorFrame.mode1Color[i-3][0] = word&0x001f;
              TableEditorFrame.mode1Color[i-3][1] = (word>>5)&0x001f;
              TableEditorFrame.mode1Color[i-3][2] = (word>>10)&0x001f;
              //System.out.println("(table2[(2*i)-1]) = "+ (0x00ff& (table2[(2*i)-1])));
              //System.out.println("table2[(2*i)] ="+ (0x00ff& (table2[(2*i)])));
              //System.out.println("word = "+ (word));
              //System.out.println("red = "+ mode1Color[i-3][0]);
              //System.out.println("green = "+ mode1Color[i-3][1]);
              //System.out.println("blue = "+ mode1Color[i-3][2]);
          }

          //get RGB for mode 2
          for(int i=3;i<18;i++){
              int word = 0;
              word = (( (0x00ff&table3[(2*i)-1])<<8 ) + (0x00ff&table3[(2*i)]));
              TableEditorFrame.mode2Color[i-3][0] = word&0x001f;
              TableEditorFrame.mode2Color[i-3][1] = (word>>5)&0x001f;
              TableEditorFrame.mode2Color[i-3][2] = (word>>10)&0x001f;
              //System.out.println("(table3[(2*i)-1]) = "+ (0x00ff& (table3[(2*i)-1])));
              //System.out.println("table3[(2*i)] ="+ (0x00ff& (table3[(2*i)])));
              //System.out.println("word = "+ (word));
              //System.out.println("red = "+ mode2Color[i-3][0]);
              //System.out.println("green = "+ mode2Color[i-3][1]);
              //System.out.println("blue = "+ mode2Color[i-3][2]);
          }

          //get RGB for mode 3
          for(int i=3;i<11;i++){
              int word = 0;
              word = (( (0x00ff&table4[(2*i)-1])<<8 ) + (0x00ff&table4[(2*i)]));
              TableEditorFrame.mode3Color[i-3][0] = word&0x001f;
              TableEditorFrame.mode3Color[i-3][1] = (word>>5)&0x001f;
              TableEditorFrame.mode3Color[i-3][2] = (word>>10)&0x001f;
              //System.out.println("(table4[(2*i)-1]) = "+ (0x00ff& (table4[(2*i)-1])));
              //System.out.println("table4[(2*i)] ="+ (0x00ff& (table4[(2*i)])));
              //System.out.println("word = "+ (word));
              //System.out.println("red = "+ mode3Color[i-3][0]);
              //System.out.println("green = "+ mode3Color[i-3][1]);
              //System.out.println("blue = "+ mode3Color[i-3][2]);
          }


          //get audio for mode1
          int counterer =0;
          for(int i=37;i<67;i=i+2 ){
          float cF = 0x003f&table2[i];
          float cR = 0x00ff&table2[i+1];
          float temp = 0;
          temp = (((cF*10000)/256) + ((( cR /256 ) * 10000) /256));
          //System.out.println(counterer+"= (mode1)for "+i+" cF = ["+cF+"] cR =["+cR+"]  straight from table");
          //System.out.println("(mode1)freq is ="+temp);
          TableEditorFrame.mode1Audio[counterer] = temp;
          counterer++;
          }

          //get audio for mode2
          counterer=0;
          for(int i=37;i<67;i=i+2 ){
          float cF = 0x003f&table3[i];
          float cR = 0x00ff&table3[i+1];
          //System.out.println("for "+i+" cF = ["+cF+"] cR =["+cR+"]");
          float temp = 0;
          temp = (((cF*10000)/256) + ((( cR /256 ) * 10000) /256));
          TableEditorFrame.mode2Audio[counterer] = temp;
          counterer++;
          }

          //get audio for mode3
          counterer=0;
          for(int i=23;i<47;i=i+3 ){
              float cF = 0x003f & table4[i];
              float cR = 0x00ff & table4[i + 1];
              float mR = 0x00ff & table4[i + 2];
              float temp = 0;
              temp = (((cF*10000)/256) + ((( cR /256 ) * 10000) /256));
              float tempm = (((mR / 256) * 10000) / 256);
              //System.out.println("for "+i+" mR = "+mR);
              TableEditorFrame.mode3Audio[counterer][0] = temp;
              TableEditorFrame.mode3Audio[counterer][1] = tempm;
              //System.out.println("for "+i+" cF = "+cF+"] cR =["+cR+"]"+" and mod ="+tempm);
              //System.out.println("(mode3)freq is ="+TableEditorFrame.mode3Audio[counterer][0]);
              //System.out.println("mode3Audio[counterer][0]  = "+TableEditorFrame.mode3Audio[counterer][0]);
              //System.out.println("mode3Audio[counterer][1]  = "+TableEditorFrame.mode3Audio[counterer][1]);
              counterer++;
          }


          //fill mode1Table with rgb values
          for(int i=0;i<15;i++){
              TableEditorFrame.mode1Table.setValueAt(new Integer(TableEditorFrame.mode1Color[i][0]), i,1);
              TableEditorFrame.mode1Table.setValueAt(new Integer(TableEditorFrame.mode1Color[i][1]), i, 2);
              TableEditorFrame.mode1Table.setValueAt(new Integer(TableEditorFrame.mode1Color[i][2]), i, 3);
          }
          //fill mode2Table with rgb values
          for(int i=0;i<15;i++){
              TableEditorFrame.mode2Table.setValueAt( new Integer(TableEditorFrame.mode2Color[i][0] ), i, 1);
              TableEditorFrame.mode2Table.setValueAt( new Integer(TableEditorFrame.mode2Color[i][1]), i, 2);
              TableEditorFrame.mode2Table.setValueAt( new Integer(TableEditorFrame.mode2Color[i][2]), i, 3);
          }
          //fill mode3Table with rgb values
          for(int i=0;i<8;i++){
              TableEditorFrame.mode3Table.setValueAt(new Integer(TableEditorFrame.mode3Color[i][0]), i, 1);
              TableEditorFrame.mode3Table.setValueAt(new Integer(TableEditorFrame.mode3Color[i][1]), i, 2);
              TableEditorFrame.mode3Table.setValueAt(new Integer(TableEditorFrame.mode3Color[i][2]), i, 3);
          }

          //fill allmodes with audio values
          for(int i=0;i<15;i++){
              TableEditorFrame.mode1Table.setValueAt(new Double (((Math.round((TableEditorFrame.mode1Audio[i])*100)))/100.0 ), i, 4);
              TableEditorFrame.mode2Table.setValueAt(new Double(((Math.round((TableEditorFrame.mode2Audio[i])*100)))/100.0 ), i, 4);
          }

          for(int i=0;i<8;i++){
              TableEditorFrame.mode3Table.setValueAt(new Double (((Math.round((TableEditorFrame.mode3Audio[i][0])*100)))/100.0), i, 4 );
              TableEditorFrame.mode3Table.setValueAt(new Double (((Math.round((TableEditorFrame.mode3Audio[i][1])*100)))/100.0 ), i, 5 );
          }



          //set lastsensitivity index sliders
          if(TableEditorFrame.mode1SensSlider.getValue() == table1[5]){TableEditorFrame.mode1SensSlider.setValue(TableEditorFrame.mode1SensSlider.getMaximum()-1);}
          if(TableEditorFrame.mode2SensSlider.getValue() == table1[6]){TableEditorFrame.mode2SensSlider.setValue(TableEditorFrame.mode2SensSlider.getMaximum()-1);}
          if(TableEditorFrame.mode3SensSlider.getValue() == table1[7]){TableEditorFrame.mode3SensSlider.setValue(TableEditorFrame.mode3SensSlider.getMaximum()-1);}
          TableEditorFrame.mode1SensSlider.setValue(table1[5]);
          TableEditorFrame.mode2SensSlider.setValue(table1[6]);
          TableEditorFrame.mode3SensSlider.setValue(table1[7]);


          //set sensor type for modes
          if(table0[53] == 1)TableEditorFrame.mode1SensorCombo.setSelectedIndex(0);else TableEditorFrame.mode1SensorCombo.setSelectedIndex(1);
          if(table0[56] == 8)TableEditorFrame.mode2SensorCombo.setSelectedIndex(1);else TableEditorFrame.mode2SensorCombo.setSelectedIndex(0);
          if(table0[59] == 3)TableEditorFrame.mode3SensorCombo.setSelectedIndex(0);else TableEditorFrame.mode3SensorCombo.setSelectedIndex(1);

          //set sample rates
          int mode1SampleRate, mode2SampleRate, mode3SampleRate = 0;
          for(int i=0;i<TableEditorFrame.sampleRates.length;i++){
              if( ((table0[55])&0xff) == TableEditorFrame.sampleRates[i]) {
                  TableEditorFrame.mode1SampleCombo.setSelectedIndex(i);
              }
              if( ((table0[58])&0xff) == TableEditorFrame.sampleRates[i]) {
                  TableEditorFrame.mode2SampleCombo.setSelectedIndex(i);
              }
              if( ((table0[61])&0xff) == TableEditorFrame.sampleRates[i]) {
                  TableEditorFrame.mode3SampleCombo.setSelectedIndex(i);
              }
          }


          TableEditorFrame.mode1LengthSlider.setEnabled(true);
          TableEditorFrame.mode1SensSlider.setEnabled(true);
          TableEditorFrame.mode2LengthSlider.setEnabled(true);
          TableEditorFrame.mode2SensSlider.setEnabled(true);
          TableEditorFrame.mode3LengthSlider.setEnabled(true);
          TableEditorFrame.mode3SensSlider.setEnabled(true);

          TableEditorFrame.advancedMenuItem.setEnabled(true); //allow advanced panel menuoption after basic panel is enabled

          TableEditorFrame.commMsgLabel.setText("");
          //table0[54]
  /*
          for (int a = 0; a < table11.length; a++) {
              System.out.print((int)table11[a]+"  ");
          }
          System.out.println();
  */


      }catch(Exception ex){
              ex.printStackTrace();}



        }

}



class MyRenderer implements TableCellRenderer {
  static final Font oneFont = new Font("Arial",Font.PLAIN,15);
  static final Font twoFont = new Font("CourierNew",Font.PLAIN,14);
  static final Font threeFont = new Font("Arial",Font.ITALIC,14);
  static final Color bg = new Color(200,200,250);
  static final Color col0bg = new Color(202,208,200);

  public Component getTableCellRendererComponent(JTable table,
                                                                                  Object value,
                                                                                  boolean isSelected,
                                                                                  boolean hasFocus,
                                                                                  int row, int column) {

      JLabel jl = new JLabel(value.toString());
      switch(column) {
      case 0: jl.setFont(oneFont);
          jl.setHorizontalAlignment(JLabel.CENTER);
          jl.setOpaque(true);
          jl.setBackground(col0bg);

          break;
      case 1:
          jl.setFont(oneFont);
          jl.setHorizontalAlignment(JLabel.CENTER);
          break;
      case 2: jl.setFont(oneFont);
          jl.setHorizontalAlignment(JLabel.CENTER);
          break;
      case 3: jl.setFont(oneFont);
          jl.setHorizontalAlignment(JLabel.CENTER);
          break;
      case 4: jl.setFont(oneFont);
          jl.setHorizontalAlignment(JLabel.CENTER); break;
      case 5: jl.setFont(oneFont);
          jl.setHorizontalAlignment(JLabel.CENTER); break;
      case 6: jl.setFont(oneFont);
          jl.setHorizontalAlignment(JLabel.CENTER); break;
      case 7: jl.setFont(oneFont);
          jl.setHorizontalAlignment(JLabel.CENTER); break;
      case 8: jl.setFont(oneFont);
          jl.setHorizontalAlignment(JLabel.CENTER); break;
      case 9: jl.setFont(oneFont);
          jl.setHorizontalAlignment(JLabel.CENTER); break;
      case 10: jl.setFont(oneFont);
          jl.setHorizontalAlignment(JLabel.CENTER); break;
      case 11: jl.setFont(oneFont);
          jl.setHorizontalAlignment(JLabel.CENTER); break;
      case 12: jl.setFont(oneFont);
          jl.setHorizontalAlignment(JLabel.CENTER); break;
      case 13: jl.setFont(oneFont);
          jl.setHorizontalAlignment(JLabel.CENTER); break;
      case 14: jl.setFont(oneFont);
          jl.setHorizontalAlignment(JLabel.CENTER); break;
      case 15: jl.setFont(oneFont);
          jl.setHorizontalAlignment(JLabel.CENTER); break;
      case 16: jl.setFont(oneFont);
          jl.setHorizontalAlignment(JLabel.CENTER); break;
      }

      if (isSelected) {
          jl.setOpaque(true);
          jl.setBackground(bg);
      }


      return(jl);
  }






}
