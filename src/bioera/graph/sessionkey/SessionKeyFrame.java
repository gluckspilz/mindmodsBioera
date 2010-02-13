package bioera.graph.sessionkey;

import bioera.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;
import javax.swing.event.*;
import bioera.serial.SerialPortImpl;
import bioera.serial.SerialPortFactory;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import de.javasoft.plaf.synthetica.SyntheticaLookAndFeel;

public class SessionKeyFrame extends JFrame implements ActionListener, ChangeListener {


    Rectangle basBounds = new Rectangle(this.getX(), this.getY(), 850, 88);
    //Dimension basDim = new Dimension(850, 815);
    public Object[][] keyDataObj = {
        {new String(""), new String(""), new String(""), new String(""), new String(""),new String(""),new String(""),new String(""),new String(""),new String("")},
        {new String(""), new String(""), new String(""), new String(""), new String(""),new String(""),new String(""),new String(""),new String(""),new String("")},
        {new String(""), new String(""), new String(""), new String(""), new String(""),new String(""),new String(""),new String(""),new String(""),new String("")},
        {new String(""), new String(""), new String(""), new String(""), new String(""),new String(""),new String(""),new String(""),new String(""),new String("")},
        {new String(""), new String(""), new String(""), new String(""), new String(""),new String(""),new String(""),new String(""),new String(""),new String("")},
        {new String(""), new String(""), new String(""), new String(""), new String(""),new String(""),new String(""),new String(""),new String(""),new String("")},
        {new String(""), new String(""), new String(""), new String(""), new String(""),new String(""),new String(""),new String(""),new String(""),new String("")},
        {new String(""), new String(""), new String(""), new String(""), new String(""),new String(""),new String(""),new String(""),new String(""),new String("")},
        {new String(""), new String(""), new String(""), new String(""), new String(""),new String(""),new String(""),new String(""),new String(""),new String("")},
        {new String(""), new String(""), new String(""), new String(""), new String(""),new String(""),new String(""),new String(""),new String(""),new String("")},
        {new String(""), new String(""), new String(""), new String(""), new String(""),new String(""),new String(""),new String(""),new String(""),new String("")},
        {new String(""), new String(""), new String(""), new String(""), new String(""),new String(""),new String(""),new String(""),new String(""),new String("")},
        };
    public int numofSessions = 0;



    TitledBorder titledBorder1 = new TitledBorder("");
    JPanel advancedPanel = new JPanel();
    JPanel advancedTitlePanel = new JPanel();
    JLabel sessionKeyTitleLabel = new JLabel();
    //JPanel advTitlePanel = new JPanel();
    JLabel jLabel1 = new JLabel();
    TitledBorder titledBorder2 = new TitledBorder("");
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    //JPanel closePanel = new JPanel();
    //JButton closeButton = new JButton();
    JPanel sDataPanel = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JPanel colTitlePane = new JPanel();
    JTextField colNum = new JTextField();
    JTextField colCol = new JTextField();
    JTextField colDate = new JTextField();
    JTextField colMode = new JTextField();
    JTextField colStartSens = new JTextField();
    JTextField colEndSens = new JTextField();
    JTextField colTrav = new JTextField();
    JTextField colLowest = new JTextField();
    JTextField colHighest = new JTextField();
    JTextField colAvg = new JTextField();
    JTextField s1NumF = new JTextField();
    JTextField s1DateF = new JTextField();
    JTextField s1ModeF = new JTextField();
    JTextField s1StSenF = new JTextField();
    JTextField s1EndSenF = new JTextField();
    JTextField s1TravF = new JTextField();
    JTextField s1LowF = new JTextField();
    JTextField s1HighF = new JTextField();
    JTextField s1AvgF = new JTextField();
    JPanel s1Panel = new JPanel();
    VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
    JTextField s1ColF = new JTextField();
    JPanel s2Panel = new JPanel();
    JTextField s2NumF = new JTextField();
    JTextField s2ColF = new JTextField();
    JTextField s2DateF = new JTextField();
    JTextField s2ModeF = new JTextField();
    JTextField s2StSenF = new JTextField();
    JTextField s2EndSenF = new JTextField();
    JTextField s2TravF = new JTextField();
    JTextField s2LowF = new JTextField();
    JTextField s2HighF = new JTextField();
    JTextField s2AvgF = new JTextField();
    JPanel s3Panel = new JPanel();
    JTextField s3NumF = new JTextField();
    JTextField s3ColF = new JTextField();
    JTextField s3DateF = new JTextField();
    JTextField s3ModeF = new JTextField();
    JTextField s3StSenF = new JTextField();
    JTextField s3EndSenF = new JTextField();
    JTextField s3TravF = new JTextField();
    JTextField s3LowF = new JTextField();
    JTextField s3HighF = new JTextField();
    JTextField s3AvgF = new JTextField();
    JPanel s12Panel = new JPanel();
    JTextField s12NumF = new JTextField();
    JTextField s12ColF = new JTextField();
    JTextField s12DateF = new JTextField();
    JTextField s12ModeF = new JTextField();
    JTextField s12StSenF = new JTextField();
    JTextField s12EndSenF = new JTextField();
    JTextField s12TravF = new JTextField();
    JTextField s12LowF = new JTextField();
    JTextField s12HighF = new JTextField();
    JTextField s12AvgF = new JTextField();
    JPanel s11Panel = new JPanel();
    JTextField s11NumF = new JTextField();
    JTextField s11ColF = new JTextField();
    JTextField s11DateF = new JTextField();
    JTextField s11ModeF = new JTextField();
    JTextField s11StSenF = new JTextField();
    JTextField s11EndSenF = new JTextField();
    JTextField s11TravF = new JTextField();
    JTextField s11LowF = new JTextField();
    JTextField s11HighF = new JTextField();
    JTextField s11AvgF = new JTextField();
    JPanel s10Panel = new JPanel();
    JTextField s10NumF = new JTextField();
    JTextField s10ColF = new JTextField();
    JTextField s10DateF = new JTextField();
    JTextField s10ModeF = new JTextField();
    JTextField s10StSenF = new JTextField();
    JTextField s10EndSenF = new JTextField();
    JTextField s10TravF = new JTextField();
    JTextField s10LowF = new JTextField();
    JTextField s10HighF = new JTextField();
    JTextField s10AvgF = new JTextField();
    JPanel s9Panel = new JPanel();
    JTextField s9NumF = new JTextField();
    JTextField s9ColF = new JTextField();
    JTextField s9DateF = new JTextField();
    JTextField s9ModeF = new JTextField();
    JTextField s9StSenF = new JTextField();
    JTextField s9EndSenF = new JTextField();
    JTextField s9TravF = new JTextField();
    JTextField s9LowF = new JTextField();
    JTextField s9HighF = new JTextField();
    JTextField s9AvgF = new JTextField();
    JPanel s8Panel = new JPanel();
    JTextField s8NumF = new JTextField();
    JTextField s8ColF = new JTextField();
    JTextField s8DateF = new JTextField();
    JTextField s8ModeF = new JTextField();
    JTextField s8StSenF = new JTextField();
    JTextField s8EndSenF = new JTextField();
    JTextField s8TravF = new JTextField();
    JTextField s8LowF = new JTextField();
    JTextField s8HighF = new JTextField();
    JTextField s8AvgF = new JTextField();
    JPanel s7Panel = new JPanel();
    JTextField s7NumF = new JTextField();
    JTextField s7ColF = new JTextField();
    JTextField s7DateF = new JTextField();
    JTextField s7ModeF = new JTextField();
    JTextField s7StSenF = new JTextField();
    JTextField s7EndSenF = new JTextField();
    JTextField s7TravF = new JTextField();
    JTextField s7LowF = new JTextField();
    JTextField s7HighF = new JTextField();
    JTextField s7AvgF = new JTextField();
    JPanel s6Panel = new JPanel();
    JTextField s6NumF = new JTextField();
    JTextField s6ColF = new JTextField();
    JTextField s6DateF = new JTextField();
    JTextField s6ModeF = new JTextField();
    JTextField s6StSenF = new JTextField();
    JTextField s6EndSenF = new JTextField();
    JTextField s6TravF = new JTextField();
    JTextField s6LowF = new JTextField();
    JTextField s6HighF = new JTextField();
    JTextField s6AvgF = new JTextField();
    JPanel s5Panel = new JPanel();
    JTextField s5NumF = new JTextField();
    JTextField s5ColF = new JTextField();
    JTextField s5DateF = new JTextField();
    JTextField s5ModeF = new JTextField();
    JTextField s5StSenF = new JTextField();
    JTextField s5EndSenF = new JTextField();
    JTextField s5TravF = new JTextField();
    JTextField s5LowF = new JTextField();
    JTextField s5HighF = new JTextField();
    JTextField s5AvgF = new JTextField();
    JPanel s4Panel = new JPanel();
    JTextField s4NumF = new JTextField();
    JTextField s4ColF = new JTextField();
    JTextField s4DateF = new JTextField();
    JTextField s4ModeF = new JTextField();
    JTextField s4StSenF = new JTextField();
    JTextField s4EndSenF = new JTextField();
    JTextField s4TravF = new JTextField();
    JTextField s4LowF = new JTextField();
    JTextField s4HighF = new JTextField();
    JTextField s4AvgF = new JTextField();
    JTextField colProbe = new JTextField();
    JTextField s1ProbeF = new JTextField();
    JTextField s2ProbeF = new JTextField();
    JTextField s3ProbeF = new JTextField();
    JTextField s4ProbeF = new JTextField();
    JTextField s5ProbeF = new JTextField();
    JTextField s6ProbeF = new JTextField();
    JTextField s7ProbeF = new JTextField();
    JTextField s8ProbeF = new JTextField();
    JTextField s9ProbeF = new JTextField();
    JTextField s10ProbeF = new JTextField();
    JTextField s11ProbeF = new JTextField();
    JTextField s12ProbeF = new JTextField();

    public SessionKeyFrame() {
        //super.setDefaultLookAndFeelDecorated(false);

        //Dimension d = getToolkit().getScreenSize();
        SyntheticaLookAndFeel.setWindowsDecorated(false);
        setBounds(0, 0, 850, 115);
        //setBounds(basBounds);
        //setResizable(false);
        getContentPane().setLayout(new GridBagLayout());
        setVisible(false);
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            //populateKey();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //   (currPort.charAt(3) == "2".charAt(0))

private void setColor(String s){
        if (s.endsWith("c1")){ // == "c1") {
            s1ColF.setBackground(Main.app.c1);
        }
        if (s.endsWith("c1b")) {
            s2ColF.setBackground(Main.app.c1b);
        }
        if (s.endsWith("c2")) {
            s2ColF.setBackground(Main.app.c2);
        }
        if (s.endsWith("c3")) {
            s3ColF.setBackground(Main.app.c3);
        }
        if (s.endsWith("c3b")) {
            s4ColF.setBackground(Main.app.c3b);
        }
        if (s.endsWith("c4")) {
            s4ColF.setBackground(Main.app.c4);
        }
        if (s.endsWith("c5")) {
            s5ColF.setBackground(Main.app.c5);
        }
        if (s.endsWith("c5b")) {
            s6ColF.setBackground(Main.app.c5b);
        }
        if (s.endsWith("c6")) {
            s6ColF.setBackground(Main.app.c6);
        }
        if (s.endsWith("c7")) {
            s7ColF.setBackground(Main.app.c7);
        }
        if (s.endsWith("c7b")) {
            s8ColF.setBackground(Main.app.c7b);
        }
        if (s.endsWith("c8")) {
            s8ColF.setBackground(Main.app.c8);
        }
        if (s.endsWith("c9")) {
            s9ColF.setBackground(Main.app.c9);
        }
        if (s.endsWith("c9b")) {
            s10ColF.setBackground(Main.app.c9b);
        }
        if (s.endsWith("c10")) {
            s10ColF.setBackground(Main.app.c10);
        }
        if (s.endsWith("c11")) {
            s11ColF.setBackground(Main.app.c11);
        }
        if (s.endsWith("c11b")) {
            s12ColF.setBackground(Main.app.c11b);
        }
        if (s.endsWith("c12")) {
            s12ColF.setBackground(Main.app.c12);
        }
}

private String whatDate(String s){

    if(s.length()>2){
        //SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy G 'at' HH:mm");
        //SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy");
        java.text.DateFormat formatter = new java.text.SimpleDateFormat("MM.dd.yyyy 'at' HH:mm");
        long sinceepoch = Long.parseLong(s);
        java.util.Date lastModified=new java.util.Date(sinceepoch);
        //sessDate.setTime(sinceepoch);
        return formatter.format(lastModified);
        //String dateString = sdf.format(sessDate.toString());
        //return dateString;
    }
    else{return " ";}
}

private Object makeObj(final String item)  {
    return new Object() { public String toString() { return item; } };
  }


public void populateKey() throws Exception  {

    setVisible(false);
    hidePanels();

    //setcolor
    setColor( String.valueOf(keyDataObj[0][0]) );
    setColor( String.valueOf(keyDataObj[1][0]) );
    setColor( String.valueOf(keyDataObj[2][0]) );
    setColor( String.valueOf(keyDataObj[3][0]) );
    setColor( String.valueOf(keyDataObj[4][0]) );
    setColor( String.valueOf(keyDataObj[5][0]) );
    setColor( String.valueOf(keyDataObj[6][0]) );
    setColor( String.valueOf(keyDataObj[7][0]) );
    setColor( String.valueOf(keyDataObj[8][0]) );
    setColor( String.valueOf(keyDataObj[9][0]) );
    setColor( String.valueOf(keyDataObj[10][0]) );
    setColor( String.valueOf(keyDataObj[11][0]) );
    //set date
    s1DateF.setText(whatDate(String.valueOf(keyDataObj[0][1])));
    s2DateF.setText(whatDate(String.valueOf(keyDataObj[1][1])));
    s3DateF.setText(whatDate(String.valueOf(keyDataObj[2][1])));
    s4DateF.setText(whatDate(String.valueOf(keyDataObj[3][1])));
    s5DateF.setText(whatDate(String.valueOf(keyDataObj[4][1])));
    s6DateF.setText(whatDate(String.valueOf(keyDataObj[5][1])));
    s7DateF.setText(whatDate(String.valueOf(keyDataObj[6][1])));
    s8DateF.setText(whatDate(String.valueOf(keyDataObj[7][1])));
    s9DateF.setText(whatDate(String.valueOf(keyDataObj[8][1])));
    s10DateF.setText(whatDate(String.valueOf(keyDataObj[9][1])));
    s11DateF.setText(whatDate(String.valueOf(keyDataObj[10][1])));
    s12DateF.setText(whatDate(String.valueOf(keyDataObj[11][1])));
    //set mode
    s1ModeF.setText( String.valueOf(keyDataObj[0][2]) );
    s2ModeF.setText( String.valueOf(keyDataObj[1][2]) );
    s3ModeF.setText( String.valueOf(keyDataObj[2][2]) );
    s4ModeF.setText( String.valueOf(keyDataObj[3][2]) );
    s5ModeF.setText( String.valueOf(keyDataObj[4][2]) );
    s6ModeF.setText( String.valueOf(keyDataObj[5][2]) );
    s7ModeF.setText( String.valueOf(keyDataObj[6][2]) );
    s8ModeF.setText( String.valueOf(keyDataObj[7][2]) );
    s9ModeF.setText( String.valueOf(keyDataObj[8][2]) );
    s10ModeF.setText( String.valueOf(keyDataObj[9][2]) );
    s11ModeF.setText( String.valueOf(keyDataObj[10][2]) );
    s12ModeF.setText( String.valueOf(keyDataObj[11][2]) );
    //set StSenF
    s1StSenF.setText(String.valueOf(keyDataObj[0][3]));
    s2StSenF.setText(String.valueOf(keyDataObj[1][3]));
    s3StSenF.setText(String.valueOf(keyDataObj[2][3]));
    s4StSenF.setText(String.valueOf(keyDataObj[3][3]));
    s5StSenF.setText(String.valueOf(keyDataObj[4][3]));
    s6StSenF.setText(String.valueOf(keyDataObj[5][3]));
    s7StSenF.setText(String.valueOf(keyDataObj[6][3]));
    s8StSenF.setText(String.valueOf(keyDataObj[7][3]));
    s9StSenF.setText(String.valueOf(keyDataObj[8][3]));
    s10StSenF.setText(String.valueOf(keyDataObj[9][3]));
    s11StSenF.setText(String.valueOf(keyDataObj[10][3]));
    s12StSenF.setText(String.valueOf(keyDataObj[11][3]));
    //set EndSenF
    s1EndSenF.setText(String.valueOf(keyDataObj[0][4]));
    s2EndSenF.setText(String.valueOf(keyDataObj[1][4]));
    s3EndSenF.setText(String.valueOf(keyDataObj[2][4]));
    s4EndSenF.setText(String.valueOf(keyDataObj[3][4]));
    s5EndSenF.setText(String.valueOf(keyDataObj[4][4]));
    s6EndSenF.setText(String.valueOf(keyDataObj[5][4]));
    s7EndSenF.setText(String.valueOf(keyDataObj[6][4]));
    s8EndSenF.setText(String.valueOf(keyDataObj[7][4]));
    s9EndSenF.setText(String.valueOf(keyDataObj[8][4]));
    s10EndSenF.setText(String.valueOf(keyDataObj[9][4]));
    s11EndSenF.setText(String.valueOf(keyDataObj[10][4]));
    s12EndSenF.setText(String.valueOf(keyDataObj[11][4]));
    //set TravF
    s1TravF.setText(String.valueOf(keyDataObj[0][5]));
    s2TravF.setText(String.valueOf(keyDataObj[1][5]));
    s3TravF.setText(String.valueOf(keyDataObj[2][5]));
    s4TravF.setText(String.valueOf(keyDataObj[3][5]));
    s5TravF.setText(String.valueOf(keyDataObj[4][5]));
    s6TravF.setText(String.valueOf(keyDataObj[5][5]));
    s7TravF.setText(String.valueOf(keyDataObj[6][5]));
    s8TravF.setText(String.valueOf(keyDataObj[7][5]));
    s9TravF.setText(String.valueOf(keyDataObj[8][5]));
    s10TravF.setText(String.valueOf(keyDataObj[9][5]));
    s11TravF.setText(String.valueOf(keyDataObj[10][5]));
    s12TravF.setText(String.valueOf(keyDataObj[11][5]));
    //set LowF
    s1LowF.setText(String.valueOf(keyDataObj[0][6]));
    s2LowF.setText(String.valueOf(keyDataObj[1][6]));
    s3LowF.setText(String.valueOf(keyDataObj[2][6]));
    s4LowF.setText(String.valueOf(keyDataObj[3][6]));
    s5LowF.setText(String.valueOf(keyDataObj[4][6]));
    s6LowF.setText(String.valueOf(keyDataObj[5][6]));
    s7LowF.setText(String.valueOf(keyDataObj[6][6]));
    s8LowF.setText(String.valueOf(keyDataObj[7][6]));
    s9LowF.setText(String.valueOf(keyDataObj[8][6]));
    s10LowF.setText(String.valueOf(keyDataObj[9][6]));
    s11LowF.setText(String.valueOf(keyDataObj[10][6]));
    s12LowF.setText(String.valueOf(keyDataObj[11][6]));
    //set HighF
    s1HighF.setText(String.valueOf(keyDataObj[0][7]));
    s2HighF.setText(String.valueOf(keyDataObj[1][7]));
    s3HighF.setText(String.valueOf(keyDataObj[2][7]));
    s4HighF.setText(String.valueOf(keyDataObj[3][7]));
    s5HighF.setText(String.valueOf(keyDataObj[4][7]));
    s6HighF.setText(String.valueOf(keyDataObj[5][7]));
    s7HighF.setText(String.valueOf(keyDataObj[6][7]));
    s8HighF.setText(String.valueOf(keyDataObj[7][7]));
    s9HighF.setText(String.valueOf(keyDataObj[8][7]));
    s10HighF.setText(String.valueOf(keyDataObj[9][7]));
    s11HighF.setText(String.valueOf(keyDataObj[10][7]));
    s12HighF.setText(String.valueOf(keyDataObj[11][7]));
    //set AvgF
    s1AvgF.setText(String.valueOf(keyDataObj[0][8]));
    s2AvgF.setText(String.valueOf(keyDataObj[1][8]));
    s3AvgF.setText(String.valueOf(keyDataObj[2][8]));
    s4AvgF.setText(String.valueOf(keyDataObj[3][8]));
    s5AvgF.setText(String.valueOf(keyDataObj[4][8]));
    s6AvgF.setText(String.valueOf(keyDataObj[5][8]));
    s7AvgF.setText(String.valueOf(keyDataObj[6][8]));
    s8AvgF.setText(String.valueOf(keyDataObj[7][8]));
    s9AvgF.setText(String.valueOf(keyDataObj[8][8]));
    s10AvgF.setText(String.valueOf(keyDataObj[9][8]));
    s11AvgF.setText(String.valueOf(keyDataObj[10][8]));
    s12AvgF.setText(String.valueOf(keyDataObj[11][8]));
    //setprobetype
    s1ProbeF.setText(String.valueOf(keyDataObj[0][9]));
    s2ProbeF.setText(String.valueOf(keyDataObj[1][9]));
    s3ProbeF.setText(String.valueOf(keyDataObj[2][9]));
    s4ProbeF.setText(String.valueOf(keyDataObj[3][9]));
    s5ProbeF.setText(String.valueOf(keyDataObj[4][9]));
    s6ProbeF.setText(String.valueOf(keyDataObj[5][9]));
    s7ProbeF.setText(String.valueOf(keyDataObj[6][9]));
    s8ProbeF.setText(String.valueOf(keyDataObj[7][9]));
    s9ProbeF.setText(String.valueOf(keyDataObj[8][9]));
    s10ProbeF.setText(String.valueOf(keyDataObj[9][9]));
    s11ProbeF.setText(String.valueOf(keyDataObj[10][9]));
    s12ProbeF.setText(String.valueOf(keyDataObj[11][9]));

    int keyHeight = 115;
    keyHeight = keyHeight + ( (numofSessions-1)*36 );

    //int yy = (Main.app.runtimeFrame.frame.getY() - Main.app.runtimeFrame.frame.getHeight() );
    setBounds(100, 700, 850,keyHeight);
    showPanels(numofSessions);

    setVisible(true);



   }


   private void jbInit() throws Exception {




        //this.setDefaultCloseOperation(this.DO_NOTHING_ON_CLOSE);
        //this.getRootPane().setWindowDecorationStyle ( JRootPane.INFORMATION_DIALOG);    //( JRootPane. PLAIN_DIALOG );

        this.setTitle("Session Key");
        this.getContentPane().setLayout(gridBagLayout1);
        sessionKeyTitleLabel.setFont(new java.awt.Font("Tahoma", Font.BOLD, 15));
        sessionKeyTitleLabel.setBorder(null);
        sessionKeyTitleLabel.setPreferredSize(new Dimension(150, 20));
        sessionKeyTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sessionKeyTitleLabel.setText("Session Key");
        //closeButton.addActionListener(this);
        //advTitlePanel.setAlignmentY((float) 0.5);
        //advTitlePanel.setPreferredSize(new Dimension(105, 28));
        jLabel1.setText("jLabel1");
        advancedPanel.setLayout(gridBagLayout2);
        advancedPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        //closePanel.setPreferredSize(new Dimension(10, 50));
        //closeButton.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 14));
        //closeButton.setPreferredSize(new Dimension(225, 36));
        //closeButton.setActionCommand("Close");
        //closeButton.setText("Close Session Key");
        sDataPanel.setAlignmentX((float) 0.0);
        sDataPanel.setAlignmentY((float) 0.0);
        sDataPanel.setMinimumSize(new Dimension(100, 100));
        sDataPanel.setLayout(verticalFlowLayout1);
        colNum.setPreferredSize(new Dimension(25, 20));
        colNum.setEditable(false);
        colNum.setText("#");
        colNum.setHorizontalAlignment(SwingConstants.CENTER);
        colCol.setPreferredSize(new Dimension(45, 20));
        colCol.setEditable(false);
        colCol.setText("Color");
        colCol.setHorizontalAlignment(SwingConstants.CENTER);
        colDate.setPreferredSize(new Dimension(150, 20));
        colDate.setEditable(false);
        colDate.setText("Date");
        colDate.setHorizontalAlignment(SwingConstants.CENTER);
        colMode.setPreferredSize(new Dimension(45, 20));
        colMode.setEditable(false);
        colMode.setText("Mode");
        colMode.setHorizontalAlignment(SwingConstants.CENTER);
        colStartSens.setPreferredSize(new Dimension(95, 20));
        colStartSens.setEditable(false);
        colStartSens.setText("Start Sensitivity");
        colStartSens.setHorizontalAlignment(SwingConstants.CENTER);
        colEndSens.setPreferredSize(new Dimension(85, 20));
        colEndSens.setEditable(false);
        colEndSens.setText("End Sensitivity");
        colEndSens.setHorizontalAlignment(SwingConstants.CENTER);
        colTrav.setPreferredSize(new Dimension(100, 20));
        colTrav.setEditable(false);
        colTrav.setText("Indexes Traversed");
        colTrav.setHorizontalAlignment(SwingConstants.CENTER);
        colLowest.setPreferredSize(new Dimension(45, 20));
        colLowest.setEditable(false);
        colLowest.setText("Lowest");
        colLowest.setHorizontalAlignment(SwingConstants.CENTER);
        colHighest.setPreferredSize(new Dimension(48, 20));
        colHighest.setEditable(false);
        colHighest.setText("Highest");
        colHighest.setHorizontalAlignment(SwingConstants.CENTER);
        colAvg.setPreferredSize(new Dimension(55, 20));
        colAvg.setEditable(false);
        colAvg.setText("Average");
        colAvg.setHorizontalAlignment(SwingConstants.CENTER);
        s1NumF.setPreferredSize(new Dimension(25, 20));
        s1NumF.setEditable(false);
        s1NumF.setText("1");
        s1NumF.setHorizontalAlignment(SwingConstants.CENTER);
        s1DateF.setPreferredSize(new Dimension(150, 20));
        s1DateF.setEditable(false);
        s1DateF.setHorizontalAlignment(SwingConstants.CENTER);
        s1ModeF.setPreferredSize(new Dimension(45, 20));
        s1ModeF.setEditable(false);
        s1ModeF.setHorizontalAlignment(SwingConstants.CENTER);
        s1StSenF.setPreferredSize(new Dimension(95, 20));
        s1StSenF.setEditable(false);
        s1StSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s1EndSenF.setPreferredSize(new Dimension(85, 20));
        s1EndSenF.setEditable(false);
        s1EndSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s1TravF.setPreferredSize(new Dimension(100, 20));
        s1TravF.setEditable(false);
        s1TravF.setHorizontalAlignment(SwingConstants.CENTER);
        s1LowF.setPreferredSize(new Dimension(45, 20));
        s1LowF.setEditable(false);
        s1LowF.setHorizontalAlignment(SwingConstants.CENTER);
        s1HighF.setPreferredSize(new Dimension(48, 20));
        s1HighF.setEditable(false);
        s1HighF.setHorizontalAlignment(SwingConstants.CENTER);
        s1AvgF.setPreferredSize(new Dimension(55, 20));
        s1AvgF.setEditable(false);
        s1AvgF.setHorizontalAlignment(SwingConstants.CENTER);
        verticalFlowLayout1.setHgap(0);
        verticalFlowLayout1.setVgap(0);
        s1ColF.setPreferredSize(new Dimension(45, 20));
        s1ColF.setEditable(false);
        s1ColF.setHorizontalAlignment(SwingConstants.CENTER);
        s1Panel.setBorder(BorderFactory.createLoweredBevelBorder());
        s1Panel.setMinimumSize(new Dimension(748, 30));
        s1Panel.setPreferredSize(new Dimension(802, 35));
        colTitlePane.setBorder(BorderFactory.createRaisedBevelBorder());
        colTitlePane.setMinimumSize(new Dimension(748, 30));
        s2Panel.setBorder(BorderFactory.createLoweredBevelBorder());
        s2Panel.setMinimumSize(new Dimension(748, 30));
        s2NumF.setPreferredSize(new Dimension(25, 20));
        s2NumF.setEditable(false);
        s2NumF.setText("2");
        s2NumF.setHorizontalAlignment(SwingConstants.CENTER);
        s2ColF.setPreferredSize(new Dimension(45, 20));
        s2ColF.setEditable(false);
        s2ColF.setHorizontalAlignment(SwingConstants.CENTER);
        s2DateF.setPreferredSize(new Dimension(150, 20));
        s2DateF.setEditable(false);
        s2DateF.setHorizontalAlignment(SwingConstants.CENTER);
        s2ModeF.setPreferredSize(new Dimension(45, 20));
        s2ModeF.setEditable(false);
        s2ModeF.setHorizontalAlignment(SwingConstants.CENTER);
        s2StSenF.setPreferredSize(new Dimension(95, 20));
        s2StSenF.setEditable(false);
        s2StSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s2EndSenF.setPreferredSize(new Dimension(85, 20));
        s2EndSenF.setEditable(false);
        s2EndSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s2TravF.setPreferredSize(new Dimension(100, 20));
        s2TravF.setEditable(false);
        s2TravF.setHorizontalAlignment(SwingConstants.CENTER);
        s2LowF.setPreferredSize(new Dimension(45, 20));
        s2LowF.setEditable(false);
        s2LowF.setHorizontalAlignment(SwingConstants.CENTER);
        s2HighF.setPreferredSize(new Dimension(48, 20));
        s2HighF.setEditable(false);
        s2HighF.setHorizontalAlignment(SwingConstants.CENTER);
        s2AvgF.setPreferredSize(new Dimension(55, 20));
        s2AvgF.setEditable(false);
        s2AvgF.setHorizontalAlignment(SwingConstants.CENTER);
        s3Panel.setBorder(BorderFactory.createLoweredBevelBorder());
        s3Panel.setMinimumSize(new Dimension(748, 30));
        s3NumF.setPreferredSize(new Dimension(25, 20));
        s3NumF.setEditable(false);
        s3NumF.setText("3");
        s3NumF.setHorizontalAlignment(SwingConstants.CENTER);
        s3ColF.setPreferredSize(new Dimension(45, 20));
        s3ColF.setEditable(false);
        s3ColF.setHorizontalAlignment(SwingConstants.CENTER);
        s3DateF.setPreferredSize(new Dimension(150, 20));
        s3DateF.setEditable(false);
        s3DateF.setHorizontalAlignment(SwingConstants.CENTER);
        s3ModeF.setPreferredSize(new Dimension(45, 20));
        s3ModeF.setEditable(false);
        s3ModeF.setHorizontalAlignment(SwingConstants.CENTER);
        s3StSenF.setPreferredSize(new Dimension(95, 20));
        s3StSenF.setEditable(false);
        s3StSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s3EndSenF.setPreferredSize(new Dimension(85, 20));
        s3EndSenF.setEditable(false);
        s3EndSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s3TravF.setPreferredSize(new Dimension(100, 20));
        s3TravF.setEditable(false);
        s3TravF.setHorizontalAlignment(SwingConstants.CENTER);
        s3LowF.setPreferredSize(new Dimension(45, 20));
        s3LowF.setEditable(false);
        s3LowF.setHorizontalAlignment(SwingConstants.CENTER);
        s3HighF.setPreferredSize(new Dimension(48, 20));
        s3HighF.setEditable(false);
        s3HighF.setHorizontalAlignment(SwingConstants.CENTER);
        s3AvgF.setPreferredSize(new Dimension(55, 20));
        s3AvgF.setEditable(false);
        s3AvgF.setHorizontalAlignment(SwingConstants.CENTER);
        s12Panel.setBorder(BorderFactory.createLoweredBevelBorder());
        s12Panel.setMinimumSize(new Dimension(748, 30));
        s12NumF.setPreferredSize(new Dimension(25, 20));
        s12NumF.setEditable(false);
        s12NumF.setText("12");
        s12NumF.setHorizontalAlignment(SwingConstants.CENTER);
        s12ColF.setPreferredSize(new Dimension(45, 20));
        s12ColF.setEditable(false);
        s12ColF.setHorizontalAlignment(SwingConstants.CENTER);
        s12DateF.setPreferredSize(new Dimension(150, 20));
        s12DateF.setEditable(false);
        s12DateF.setHorizontalAlignment(SwingConstants.CENTER);
        s12ModeF.setPreferredSize(new Dimension(45, 20));
        s12ModeF.setEditable(false);
        s12ModeF.setHorizontalAlignment(SwingConstants.CENTER);
        s12StSenF.setPreferredSize(new Dimension(95, 20));
        s12StSenF.setEditable(false);
        s12StSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s12EndSenF.setPreferredSize(new Dimension(85, 20));
        s12EndSenF.setEditable(false);
        s12EndSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s12TravF.setPreferredSize(new Dimension(100, 20));
        s12TravF.setEditable(false);
        s12TravF.setHorizontalAlignment(SwingConstants.CENTER);
        s12LowF.setPreferredSize(new Dimension(45, 20));
        s12LowF.setEditable(false);
        s12LowF.setHorizontalAlignment(SwingConstants.CENTER);
        s12HighF.setPreferredSize(new Dimension(48, 20));
        s12HighF.setEditable(false);
        s12HighF.setHorizontalAlignment(SwingConstants.CENTER);
        s12AvgF.setPreferredSize(new Dimension(55, 20));
        s12AvgF.setEditable(false);
        s12AvgF.setHorizontalAlignment(SwingConstants.CENTER);
        s11Panel.setBorder(BorderFactory.createLoweredBevelBorder());
        s11Panel.setMinimumSize(new Dimension(748, 30));
        s11NumF.setPreferredSize(new Dimension(25, 20));
        s11NumF.setEditable(false);
        s11NumF.setText("11");
        s11NumF.setHorizontalAlignment(SwingConstants.CENTER);
        s11ColF.setPreferredSize(new Dimension(45, 20));
        s11ColF.setEditable(false);
        s11ColF.setHorizontalAlignment(SwingConstants.CENTER);
        s11DateF.setPreferredSize(new Dimension(150, 20));
        s11DateF.setEditable(false);
        s11DateF.setHorizontalAlignment(SwingConstants.CENTER);
        s11ModeF.setPreferredSize(new Dimension(45, 20));
        s11ModeF.setEditable(false);
        s11ModeF.setHorizontalAlignment(SwingConstants.CENTER);
        s11StSenF.setPreferredSize(new Dimension(95, 20));
        s11StSenF.setEditable(false);
        s11StSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s11EndSenF.setPreferredSize(new Dimension(85, 20));
        s11EndSenF.setEditable(false);
        s11EndSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s11TravF.setPreferredSize(new Dimension(100, 20));
        s11TravF.setEditable(false);
        s11TravF.setHorizontalAlignment(SwingConstants.CENTER);
        s11LowF.setPreferredSize(new Dimension(45, 20));
        s11LowF.setEditable(false);
        s11LowF.setHorizontalAlignment(SwingConstants.CENTER);
        s11HighF.setPreferredSize(new Dimension(48, 20));
        s11HighF.setEditable(false);
        s11HighF.setHorizontalAlignment(SwingConstants.CENTER);
        s11AvgF.setPreferredSize(new Dimension(55, 20));
        s11AvgF.setEditable(false);
        s11AvgF.setHorizontalAlignment(SwingConstants.CENTER);
        s10Panel.setBorder(BorderFactory.createLoweredBevelBorder());
        s10Panel.setMinimumSize(new Dimension(748, 30));
        s10NumF.setPreferredSize(new Dimension(25, 20));
        s10NumF.setEditable(false);
        s10NumF.setText("10");
        s10NumF.setHorizontalAlignment(SwingConstants.CENTER);
        s10ColF.setPreferredSize(new Dimension(45, 20));
        s10ColF.setEditable(false);
        s10ColF.setHorizontalAlignment(SwingConstants.CENTER);
        s10DateF.setPreferredSize(new Dimension(150, 20));
        s10DateF.setEditable(false);
        s10DateF.setHorizontalAlignment(SwingConstants.CENTER);
        s10ModeF.setPreferredSize(new Dimension(45, 20));
        s10ModeF.setEditable(false);
        s10ModeF.setHorizontalAlignment(SwingConstants.CENTER);
        s10StSenF.setPreferredSize(new Dimension(95, 20));
        s10StSenF.setToolTipText("");
        s10StSenF.setEditable(false);
        s10StSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s10EndSenF.setPreferredSize(new Dimension(85, 20));
        s10EndSenF.setEditable(false);
        s10EndSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s10TravF.setPreferredSize(new Dimension(100, 20));
        s10TravF.setEditable(false);
        s10TravF.setHorizontalAlignment(SwingConstants.CENTER);
        s10LowF.setPreferredSize(new Dimension(45, 20));
        s10LowF.setEditable(false);
        s10LowF.setHorizontalAlignment(SwingConstants.CENTER);
        s10HighF.setPreferredSize(new Dimension(48, 20));
        s10HighF.setEditable(false);
        s10HighF.setHorizontalAlignment(SwingConstants.CENTER);
        s10AvgF.setPreferredSize(new Dimension(55, 20));
        s10AvgF.setEditable(false);
        s10AvgF.setHorizontalAlignment(SwingConstants.CENTER);
        s9Panel.setBorder(BorderFactory.createLoweredBevelBorder());
        s9Panel.setMinimumSize(new Dimension(748, 30));
        s9NumF.setPreferredSize(new Dimension(25, 20));
        s9NumF.setEditable(false);
        s9NumF.setText("9");
        s9NumF.setHorizontalAlignment(SwingConstants.CENTER);
        s9ColF.setPreferredSize(new Dimension(45, 20));
        s9ColF.setEditable(false);
        s9ColF.setHorizontalAlignment(SwingConstants.CENTER);
        s9DateF.setPreferredSize(new Dimension(150, 20));
        s9DateF.setEditable(false);
        s9DateF.setHorizontalAlignment(SwingConstants.CENTER);
        s9ModeF.setPreferredSize(new Dimension(45, 20));
        s9ModeF.setEditable(false);
        s9ModeF.setHorizontalAlignment(SwingConstants.CENTER);
        s9StSenF.setPreferredSize(new Dimension(95, 20));
        s9StSenF.setEditable(false);
        s9StSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s9EndSenF.setPreferredSize(new Dimension(85, 20));
        s9EndSenF.setEditable(false);
        s9EndSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s9TravF.setPreferredSize(new Dimension(100, 20));
        s9TravF.setEditable(false);
        s9TravF.setHorizontalAlignment(SwingConstants.CENTER);
        s9LowF.setPreferredSize(new Dimension(45, 20));
        s9LowF.setEditable(false);
        s9LowF.setHorizontalAlignment(SwingConstants.CENTER);
        s9HighF.setPreferredSize(new Dimension(48, 20));
        s9HighF.setEditable(false);
        s9HighF.setHorizontalAlignment(SwingConstants.CENTER);
        s9AvgF.setPreferredSize(new Dimension(55, 20));
        s9AvgF.setEditable(false);
        s9AvgF.setHorizontalAlignment(SwingConstants.CENTER);
        s8Panel.setBorder(BorderFactory.createLoweredBevelBorder());
        s8Panel.setMinimumSize(new Dimension(748, 30));
        s8NumF.setPreferredSize(new Dimension(25, 20));
        s8NumF.setEditable(false);
        s8NumF.setText("8");
        s8NumF.setHorizontalAlignment(SwingConstants.CENTER);
        s8ColF.setPreferredSize(new Dimension(45, 20));
        s8ColF.setEditable(false);
        s8ColF.setHorizontalAlignment(SwingConstants.CENTER);
        s8DateF.setPreferredSize(new Dimension(150, 20));
        s8DateF.setEditable(false);
        s8DateF.setHorizontalAlignment(SwingConstants.CENTER);
        s8ModeF.setPreferredSize(new Dimension(45, 20));
        s8ModeF.setEditable(false);
        s8ModeF.setHorizontalAlignment(SwingConstants.CENTER);
        s8StSenF.setPreferredSize(new Dimension(95, 20));
        s8StSenF.setEditable(false);
        s8StSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s8EndSenF.setPreferredSize(new Dimension(85, 20));
        s8EndSenF.setEditable(false);
        s8EndSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s8TravF.setPreferredSize(new Dimension(100, 20));
        s8TravF.setEditable(false);
        s8TravF.setHorizontalAlignment(SwingConstants.CENTER);
        s8LowF.setPreferredSize(new Dimension(45, 20));
        s8LowF.setEditable(false);
        s8LowF.setHorizontalAlignment(SwingConstants.CENTER);
        s8HighF.setPreferredSize(new Dimension(48, 20));
        s8HighF.setEditable(false);
        s8HighF.setHorizontalAlignment(SwingConstants.CENTER);
        s8AvgF.setPreferredSize(new Dimension(55, 20));
        s8AvgF.setEditable(false);
        s8AvgF.setHorizontalAlignment(SwingConstants.CENTER);
        s7Panel.setBorder(BorderFactory.createLoweredBevelBorder());
        s7Panel.setMinimumSize(new Dimension(748, 30));
        s7NumF.setPreferredSize(new Dimension(25, 20));
        s7NumF.setEditable(false);
        s7NumF.setText("7");
        s7NumF.setHorizontalAlignment(SwingConstants.CENTER);
        s7ColF.setPreferredSize(new Dimension(45, 20));
        s7ColF.setEditable(false);
        s7ColF.setHorizontalAlignment(SwingConstants.CENTER);
        s7DateF.setPreferredSize(new Dimension(150, 20));
        s7DateF.setEditable(false);
        s7DateF.setHorizontalAlignment(SwingConstants.CENTER);
        s7ModeF.setPreferredSize(new Dimension(45, 20));
        s7ModeF.setEditable(false);
        s7ModeF.setHorizontalAlignment(SwingConstants.CENTER);
        s7StSenF.setPreferredSize(new Dimension(95, 20));
        s7StSenF.setEditable(false);
        s7StSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s7EndSenF.setPreferredSize(new Dimension(85, 20));
        s7EndSenF.setEditable(false);
        s7EndSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s7TravF.setPreferredSize(new Dimension(100, 20));
        s7TravF.setEditable(false);
        s7TravF.setHorizontalAlignment(SwingConstants.CENTER);
        s7LowF.setPreferredSize(new Dimension(45, 20));
        s7LowF.setEditable(false);
        s7LowF.setHorizontalAlignment(SwingConstants.CENTER);
        s7HighF.setPreferredSize(new Dimension(48, 20));
        s7HighF.setEditable(false);
        s7HighF.setHorizontalAlignment(SwingConstants.CENTER);
        s7AvgF.setPreferredSize(new Dimension(55, 20));
        s7AvgF.setEditable(false);
        s7AvgF.setHorizontalAlignment(SwingConstants.CENTER);
        s6Panel.setBorder(BorderFactory.createLoweredBevelBorder());
        s6Panel.setMinimumSize(new Dimension(748, 30));
        s6NumF.setPreferredSize(new Dimension(25, 20));
        s6NumF.setEditable(false);
        s6NumF.setText("6");
        s6NumF.setHorizontalAlignment(SwingConstants.CENTER);
        s6ColF.setPreferredSize(new Dimension(45, 20));
        s6ColF.setEditable(false);
        s6ColF.setHorizontalAlignment(SwingConstants.CENTER);
        s6DateF.setPreferredSize(new Dimension(150, 20));
        s6DateF.setEditable(false);
        s6DateF.setHorizontalAlignment(SwingConstants.CENTER);
        s6ModeF.setPreferredSize(new Dimension(45, 20));
        s6ModeF.setEditable(false);
        s6ModeF.setHorizontalAlignment(SwingConstants.CENTER);
        s6StSenF.setPreferredSize(new Dimension(95, 20));
        s6StSenF.setEditable(false);
        s6StSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s6EndSenF.setPreferredSize(new Dimension(85, 20));
        s6EndSenF.setEditable(false);
        s6EndSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s6TravF.setPreferredSize(new Dimension(100, 20));
        s6TravF.setEditable(false);
        s6TravF.setHorizontalAlignment(SwingConstants.CENTER);
        s6LowF.setPreferredSize(new Dimension(45, 20));
        s6LowF.setEditable(false);
        s6LowF.setHorizontalAlignment(SwingConstants.CENTER);
        s6HighF.setPreferredSize(new Dimension(48, 20));
        s6HighF.setEditable(false);
        s6HighF.setHorizontalAlignment(SwingConstants.CENTER);
        s6AvgF.setPreferredSize(new Dimension(55, 20));
        s6AvgF.setEditable(false);
        s6AvgF.setHorizontalAlignment(SwingConstants.CENTER);
        s5Panel.setBorder(BorderFactory.createLoweredBevelBorder());
        s5Panel.setMinimumSize(new Dimension(748, 30));
        s5NumF.setPreferredSize(new Dimension(25, 20));
        s5NumF.setEditable(false);
        s5NumF.setText("5");
        s5NumF.setHorizontalAlignment(SwingConstants.CENTER);
        s5ColF.setPreferredSize(new Dimension(45, 20));
        s5ColF.setEditable(false);
        s5ColF.setHorizontalAlignment(SwingConstants.CENTER);
        s5DateF.setPreferredSize(new Dimension(150, 20));
        s5DateF.setEditable(false);
        s5DateF.setHorizontalAlignment(SwingConstants.CENTER);
        s5ModeF.setPreferredSize(new Dimension(45, 20));
        s5ModeF.setEditable(false);
        s5ModeF.setHorizontalAlignment(SwingConstants.CENTER);
        s5StSenF.setPreferredSize(new Dimension(95, 20));
        s5StSenF.setEditable(false);
        s5StSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s5EndSenF.setPreferredSize(new Dimension(85, 20));
        s5EndSenF.setEditable(false);
        s5EndSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s5TravF.setPreferredSize(new Dimension(100, 20));
        s5TravF.setEditable(false);
        s5TravF.setHorizontalAlignment(SwingConstants.CENTER);
        s5LowF.setPreferredSize(new Dimension(45, 20));
        s5LowF.setEditable(false);
        s5LowF.setHorizontalAlignment(SwingConstants.CENTER);
        s5HighF.setPreferredSize(new Dimension(48, 20));
        s5HighF.setEditable(false);
        s5HighF.setHorizontalAlignment(SwingConstants.CENTER);
        s5AvgF.setPreferredSize(new Dimension(55, 20));
        s5AvgF.setEditable(false);
        s5AvgF.setHorizontalAlignment(SwingConstants.CENTER);
        s4Panel.setBorder(BorderFactory.createLoweredBevelBorder());
        s4Panel.setMinimumSize(new Dimension(748, 30));
        s4NumF.setPreferredSize(new Dimension(25, 20));
        s4NumF.setEditable(false);
        s4NumF.setText("4");
        s4NumF.setHorizontalAlignment(SwingConstants.CENTER);
        s4ColF.setPreferredSize(new Dimension(45, 20));
        s4ColF.setEditable(false);
        s4ColF.setHorizontalAlignment(SwingConstants.CENTER);
        s4DateF.setPreferredSize(new Dimension(150, 20));
        s4DateF.setEditable(false);
        s4DateF.setHorizontalAlignment(SwingConstants.CENTER);
        s4ModeF.setPreferredSize(new Dimension(45, 20));
        s4ModeF.setEditable(false);
        s4ModeF.setHorizontalAlignment(SwingConstants.CENTER);
        s4StSenF.setPreferredSize(new Dimension(95, 20));
        s4StSenF.setEditable(false);
        s4StSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s4EndSenF.setPreferredSize(new Dimension(85, 20));
        s4EndSenF.setEditable(false);
        s4EndSenF.setHorizontalAlignment(SwingConstants.CENTER);
        s4TravF.setPreferredSize(new Dimension(100, 20));
        s4TravF.setEditable(false);
        s4TravF.setHorizontalAlignment(SwingConstants.CENTER);
        s4LowF.setPreferredSize(new Dimension(45, 20));
        s4LowF.setEditable(false);
        s4LowF.setHorizontalAlignment(SwingConstants.CENTER);
        s4HighF.setPreferredSize(new Dimension(48, 20));
        s4HighF.setEditable(false);
        s4HighF.setHorizontalAlignment(SwingConstants.CENTER);
        s4AvgF.setPreferredSize(new Dimension(55, 20));
        s4AvgF.setEditable(false);
        s4AvgF.setHorizontalAlignment(SwingConstants.CENTER);
        colProbe.setPreferredSize(new Dimension(45, 20));
        colProbe.setEditable(false);
        colProbe.setText("Probe");
        colProbe.setHorizontalAlignment(SwingConstants.CENTER);
        s1ProbeF.setPreferredSize(new Dimension(45, 20));
        s1ProbeF.setEditable(false);
        s1ProbeF.setHorizontalAlignment(SwingConstants.CENTER);
        s2ProbeF.setPreferredSize(new Dimension(45, 20));
        s2ProbeF.setEditable(false);
        s2ProbeF.setHorizontalAlignment(SwingConstants.CENTER);
        s3ProbeF.setPreferredSize(new Dimension(45, 20));
        s3ProbeF.setEditable(false);
        s3ProbeF.setHorizontalAlignment(SwingConstants.CENTER);
        s4ProbeF.setPreferredSize(new Dimension(45, 20));
        s4ProbeF.setEditable(false);
        s4ProbeF.setHorizontalAlignment(SwingConstants.CENTER);
        s5ProbeF.setPreferredSize(new Dimension(45, 20));
        s5ProbeF.setEditable(false);
        s5ProbeF.setHorizontalAlignment(SwingConstants.CENTER);
        s6ProbeF.setPreferredSize(new Dimension(45, 20));
        s6ProbeF.setEditable(false);
        s6ProbeF.setHorizontalAlignment(SwingConstants.CENTER);
        s7ProbeF.setPreferredSize(new Dimension(45, 20));
        s7ProbeF.setEditable(false);
        s7ProbeF.setHorizontalAlignment(SwingConstants.CENTER);
        s8ProbeF.setPreferredSize(new Dimension(45, 20));
        s8ProbeF.setEditable(false);
        s8ProbeF.setHorizontalAlignment(SwingConstants.CENTER);
        s9ProbeF.setPreferredSize(new Dimension(45, 20));
        s9ProbeF.setEditable(false);
        s9ProbeF.setHorizontalAlignment(SwingConstants.CENTER);
        s10ProbeF.setPreferredSize(new Dimension(45, 20));
        s10ProbeF.setEditable(false);
        s10ProbeF.setHorizontalAlignment(SwingConstants.CENTER);
        s11ProbeF.setPreferredSize(new Dimension(45, 20));
        s11ProbeF.setEditable(false);
        s11ProbeF.setHorizontalAlignment(SwingConstants.CENTER);
        s12ProbeF.setPreferredSize(new Dimension(45, 20));
        s12ProbeF.setEditable(false);
        s12ProbeF.setHorizontalAlignment(SwingConstants.CENTER);
        //advTitlePanel.add(sessionKeyTitleLabel);
        //advTitlePanel.add(advancedTitlePanel);
        //format tables and tabledata
        s2Panel.setVisible(false);
        s3Panel.setVisible(false);
        s4Panel.setVisible(false);
        s5Panel.setVisible(false);
        s6Panel.setVisible(false);
        s7Panel.setVisible(false);
        s8Panel.setVisible(false);
        s9Panel.setVisible(false);
        s10Panel.setVisible(false);
        s11Panel.setVisible(false);
        s12Panel.setVisible(false);

        //closePanel.add(closeButton);
        colTitlePane.add(colNum);
        colTitlePane.add(colCol);
        colTitlePane.add(colDate);
        colTitlePane.add(colMode);
        colTitlePane.add(colProbe);
        colTitlePane.add(colStartSens);
        colTitlePane.add(colEndSens);
        colTitlePane.add(colTrav);
        colTitlePane.add(colLowest);
        colTitlePane.add(colHighest);
        colTitlePane.add(colAvg);
        s3Panel.add(s3NumF);
        s3Panel.add(s3ColF);
        s3Panel.add(s3DateF);
        s3Panel.add(s3ModeF);
        s3Panel.add(s3ProbeF);
        s3Panel.add(s3StSenF);
        s3Panel.add(s3EndSenF);
        s3Panel.add(s3TravF);
        s3Panel.add(s3LowF);
        s3Panel.add(s3HighF);
        s3Panel.add(s3AvgF);
        s4Panel.add(s4NumF);
        s4Panel.add(s4ColF);
        s4Panel.add(s4DateF);
        s4Panel.add(s4ModeF);
        s4Panel.add(s4ProbeF);
        s4Panel.add(s4StSenF);
        s4Panel.add(s4EndSenF);
        s4Panel.add(s4TravF);
        s4Panel.add(s4LowF);
        s4Panel.add(s4HighF);
        s4Panel.add(s4AvgF);
        s5Panel.add(s5NumF);
        s5Panel.add(s5ColF);
        s5Panel.add(s5DateF);
        s5Panel.add(s5ModeF);
        s5Panel.add(s5ProbeF);
        s5Panel.add(s5StSenF);
        s5Panel.add(s5EndSenF);
        s5Panel.add(s5TravF);
        s5Panel.add(s5LowF);
        s5Panel.add(s5HighF);
        s5Panel.add(s5AvgF);
        s6Panel.add(s6NumF);
        s6Panel.add(s6ColF);
        s6Panel.add(s6DateF);
        s6Panel.add(s6ModeF);
        s6Panel.add(s6ProbeF);
        s6Panel.add(s6StSenF);
        s6Panel.add(s6EndSenF);
        s6Panel.add(s6TravF);
        s6Panel.add(s6LowF);
        s6Panel.add(s6HighF);
        s6Panel.add(s6AvgF);
        s7Panel.add(s7NumF);
        s7Panel.add(s7ColF);
        s7Panel.add(s7DateF);
        s7Panel.add(s7ModeF);
        s7Panel.add(s7ProbeF);
        s7Panel.add(s7StSenF);
        s7Panel.add(s7EndSenF);
        s7Panel.add(s7TravF);
        s7Panel.add(s7LowF);
        s7Panel.add(s7HighF);
        s7Panel.add(s7AvgF);
        s8Panel.add(s8NumF);
        s8Panel.add(s8ColF);
        s8Panel.add(s8DateF);
        s8Panel.add(s8ModeF);
        s8Panel.add(s8ProbeF);
        s8Panel.add(s8StSenF);
        s8Panel.add(s8EndSenF);
        s8Panel.add(s8TravF);
        s8Panel.add(s8LowF);
        s8Panel.add(s8HighF);
        s8Panel.add(s8AvgF);
        s9Panel.add(s9NumF);
        s9Panel.add(s9ColF);
        s9Panel.add(s9DateF);
        s9Panel.add(s9ModeF);
        s9Panel.add(s9ProbeF);
        s9Panel.add(s9StSenF);
        s9Panel.add(s9EndSenF);
        s9Panel.add(s9TravF);
        s9Panel.add(s9LowF);
        s9Panel.add(s9HighF);
        s9Panel.add(s9AvgF);
        s10Panel.add(s10NumF);
        s10Panel.add(s10ColF);
        s10Panel.add(s10DateF);
        s10Panel.add(s10ModeF);
        s10Panel.add(s10ProbeF);
        s10Panel.add(s10StSenF);
        s10Panel.add(s10EndSenF);
        s10Panel.add(s10TravF);
        s10Panel.add(s10LowF);
        s10Panel.add(s10HighF);
        s10Panel.add(s10AvgF);
        s11Panel.add(s11NumF);
        s11Panel.add(s11ColF);
        s11Panel.add(s11DateF);
        s11Panel.add(s11ModeF);
        s11Panel.add(s11ProbeF);
        s11Panel.add(s11StSenF);
        s11Panel.add(s11EndSenF);
        s11Panel.add(s11TravF);
        s11Panel.add(s11LowF);
        s11Panel.add(s11HighF);
        s11Panel.add(s11AvgF);
        s12Panel.add(s12NumF);
        s12Panel.add(s12ColF);
        s12Panel.add(s12DateF);
        s12Panel.add(s12ModeF);
        s12Panel.add(s12ProbeF);
        s12Panel.add(s12StSenF);
        s12Panel.add(s12EndSenF);
        s12Panel.add(s12TravF);
        s12Panel.add(s12LowF);
        s12Panel.add(s12HighF);
        s12Panel.add(s12AvgF);
        s2Panel.add(s2NumF);
        s2Panel.add(s2ColF);
        s2Panel.add(s2DateF);
        s2Panel.add(s2ModeF);
        s2Panel.add(s2ProbeF);
        s2Panel.add(s2StSenF);
        s2Panel.add(s2EndSenF);
        s2Panel.add(s2TravF);
        s2Panel.add(s2LowF);
        s2Panel.add(s2HighF);
        s2Panel.add(s2AvgF);
        s1Panel.add(s1NumF);
        s1Panel.add(s1ColF);
        s1Panel.add(s1DateF);
        s1Panel.add(s1ModeF);
        s1Panel.add(s1ProbeF);
        s1Panel.add(s1StSenF);
        s1Panel.add(s1EndSenF);
        s1Panel.add(s1TravF);
        s1Panel.add(s1LowF);
        s1Panel.add(s1HighF);
        s1Panel.add(s1AvgF);
        sDataPanel.add(colTitlePane);
        sDataPanel.add(s1Panel);
        sDataPanel.add(s2Panel);
        sDataPanel.add(s3Panel);
        sDataPanel.add(s4Panel);
        sDataPanel.add(s5Panel);
        sDataPanel.add(s6Panel);
        sDataPanel.add(s7Panel);
        sDataPanel.add(s8Panel);
        sDataPanel.add(s9Panel);
        sDataPanel.add(s10Panel);
        sDataPanel.add(s11Panel);
        sDataPanel.add(s12Panel);
        advancedPanel.add(sDataPanel,
                          new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.NORTH,
                                                 GridBagConstraints.BOTH,
                                                 new Insets(0, 0, 0, 0), 0, 0));
/*
        advancedPanel.add(advTitlePanel,
                          new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
                                                 , GridBagConstraints.NORTH,
                                                 GridBagConstraints.BOTH,
                                                 new Insets(0, 0, 0, 0), 0, 0));*/


        //closePanel.setVisible(false); //advTitlePanel.setVisible(false);//just temporary

    /*
        this.getContentPane().add(closePanel,
                                  new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0
                , GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 947, 0));*/
        this.getContentPane().add(advancedPanel,
                                  new GridBagConstraints(1, 0,
                GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1.0,
                1.0
                , GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
    }


    public void actionPerformed(ActionEvent e) {


  }



    public void stateChanged(ChangeEvent e) {

      //System.out.println("event equals =" + e.toString()+" from ="+e.getSource().getClass().getName());



  }


  private void hidePanels(){
      s2Panel.setVisible(false);
      s3Panel.setVisible(false);
      s4Panel.setVisible(false);
      s5Panel.setVisible(false);
      s6Panel.setVisible(false);
      s7Panel.setVisible(false);
      s8Panel.setVisible(false);
      s9Panel.setVisible(false);
      s10Panel.setVisible(false);
      s11Panel.setVisible(false);
      s12Panel.setVisible(false);
  }

  private void showPanels(int i){
      if(i>0){s1Panel.setVisible(true);}
      if(i>1){s2Panel.setVisible(true);}
      if(i>2){s3Panel.setVisible(true);}
      if(i>3){s4Panel.setVisible(true);}
      if(i>4){s5Panel.setVisible(true);}
      if(i>5){s6Panel.setVisible(true);}
      if(i>6){s7Panel.setVisible(true);}
      if(i>7){s8Panel.setVisible(true);}
      if(i>8){s9Panel.setVisible(true);}
      if(i>9){s10Panel.setVisible(true);}
      if(i>10){s11Panel.setVisible(true);}
      if(i>11){s12Panel.setVisible(true);}
}





}
