/* DialogError.java v 1.0.9   11/6/04 7:15 PM

 */

package bioera.graph.designer;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class DialogError extends MessageDialog {
    public int errorcode = 0;
/**
 * HelpAboutDialog constructor comment.
 * @param d javax.swing.JDialog
 */
public DialogError(javax.swing.JDialog d) {
	super(d);
}
/**
 * HelpAboutDialog constructor comment.
 * @param d javax.swing.JDialog
 * @param title java.lang.String
 */
public DialogError(javax.swing.JDialog d, String title) {
	super(d, title);
}
/**
 * HelpAboutDialog constructor comment.
 * @param f javax.swing.JFrame
 */
public DialogError(javax.swing.JFrame f) {
	super(f);
}
/**
 * HelpAboutDialog constructor comment.
 * @param f javax.swing.JFrame
 * @param title java.lang.String
 */
public DialogError(javax.swing.JFrame f, String title) {
	super(f, title);
}

public DialogError(javax.swing.JFrame f, int error) {
    super(f);
    errorcode = error;
}
/**
 * HelpAboutDialog constructor comment.
 * @param f javax.swing.JFrame
 * @param title java.lang.String
 */
public void show() {
	setTitle("PreSage Device Error");
	JPanel mainPanel = new JPanel(new bioera.layouts.AdvancedGridLayout(0, 1, 2, 2));
	JLabel l;
	mainPanel.add((l=new JLabel("An Error Has Halted the System", JLabel.CENTER)));
	Font f = l.getFont();
	l.setFont(new Font(f.getName(), 1, f.getSize() * 4 / 3));
	mainPanel.add(l=new JLabel(""));
	mainPanel.add(l=new JLabel("Error:  " + errorcode, JLabel.CENTER));
        l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));

        if(errorcode==33){
	mainPanel.add(new JLabel("No Probes Detected", JLabel.CENTER));
        l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
        mainPanel.add(new JLabel(""));
	mainPanel.add(new JLabel("Please plug the sensors that you'd like to use into the PreSage, Re-Initialize and press Start"));
        }

        if(errorcode==00){
        mainPanel.add(new JLabel("Write Flash Sensitivity Index Returned Error", JLabel.CENTER));
        l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel("Please Power Cycle and Restart Session"));
        }

        if(errorcode==16){
        mainPanel.add(new JLabel("CALCULATE_TABLES : Write Flash Index Error for Single Probes", JLabel.CENTER));
        l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel("Please Power Cycle and Restart Session"));
        }

        if(errorcode==17){
        mainPanel.add(new JLabel("CALCULATE_TABLES : Write Flash Index Error for Dual Probes", JLabel.CENTER));
        l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel("Please Power Cycle and Restart Session"));
        }

        if(errorcode==32){
        mainPanel.add(new JLabel("CHK4PROBES : SHORT Detected in Probe", JLabel.CENTER));
        l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel("Please Make Sure Probe is Inserted Properly and Restart Session"));
        }

        if(errorcode==48){
        mainPanel.add(new JLabel("CALCULATE_INDEX: Too Many OPEN or >10% Jumps Detected", JLabel.CENTER));
        l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel("Please Make Sure That Probe is Being Used Properly.  Close this window and press Re-Initialize"));
        }

        if(errorcode==49){
        mainPanel.add(new JLabel("CALCULATE_INDEX: Probe Settings Do Not Match Any Known Configuration", JLabel.CENTER));
        l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel("Please Populate PreSage Tables With Proper Values and Restart Session"));
        }

        if(errorcode==100){
        mainPanel.add(new JLabel("You Cannot Compare GSR Sessions With Temp Sessions", JLabel.CENTER));
        l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel("Please Try Again"));
        }

        if(errorcode==102){
        mainPanel.add(new JLabel("There is a problem with saving your file", JLabel.CENTER));
        l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel("Please Try Again"));
        }

        if(errorcode==103){
        mainPanel.add(new JLabel("There is a problem loading your session", JLabel.CENTER));
        l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel("Please Try Again"));
        }

        if(errorcode==104){
        mainPanel.add(new JLabel("You can only load a maximum of 12 Single Mode Sessions or 6 Dual Mode Sessions", JLabel.CENTER));
        l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel("Please Reselect the Sessions You'd Like to Load"));
        }

        if(errorcode==105){
        mainPanel.add(new JLabel("There was an error while parsing session data for keyframe", JLabel.CENTER));
        l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel("Please Reselect the Sessions You'd Like to Load"));
        }

        if(errorcode==106){
        mainPanel.add(new JLabel("You do not have the Virtual Com Port drivers installed", JLabel.CENTER));
        l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel("Please exit Presage Monitor and install Virtual COM Port Drivers from install disc before using"));
        }

        if(errorcode==107){
        mainPanel.add(new JLabel("PreSage is not found or not connected to your computer", JLabel.CENTER));
        l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel("Please plug PreSage into the USB port of your computer and click Re-Initialize"));
        }

        if(errorcode==99){
        mainPanel.add(new JLabel("PreSage not responding to Start Command", JLabel.CENTER));
        l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel("Please Re-Initialize and Start Again"));
        }




	mainPanel.add(new JLabel(""));

	mainPanel.setBorder(BorderFactory.createCompoundBorder(
		BorderFactory.createLoweredBevelBorder(),
		BorderFactory.createEmptyBorder(10, 10, 10, 10))
		);

	bClose = new JButton("Close");
	bClose.addActionListener(this);

	JPanel down = new JPanel();
	down.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 2));
	down.add(bClose);

	getContentPane().setLayout(new java.awt.BorderLayout());
	getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);
	getContentPane().add(down, java.awt.BorderLayout.SOUTH);
	pack();
	locateOnWindow();
	super.show();
}
}
