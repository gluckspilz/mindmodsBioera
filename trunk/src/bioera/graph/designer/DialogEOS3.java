/* DialogHelpAboutPresage.java v 1.0.9   11/6/04 7:15 PM
 *
 * Presage Monitor - visual designer for biofeedback based off of BioEra (http://www.bioera.net)
 *
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

package bioera.graph.designer;


import javax.swing.*;

import bioera.Main;

import java.awt.event.*;
import java.awt.*;
import java.io.*;


public class DialogEOS3 extends MessageDialog implements ActionListener {
    int startSens=0; int endSens=0; int indTrav=0;
    int highest[]= new int[4];
    int lowest[]= new int[4];
    int avg[]= new int[4];
    int temp =0;
    int probeC=0;
    JButton bSave = new JButton("Save");

/**
 * DialogHelpAboutPresage constructor comment.
 * @param d javax.swing.JDialog
 */
public DialogEOS3(javax.swing.JDialog d) {
        super(d);
}
/**
 * DialogHelpAboutPresage constructor comment.
 * @param d javax.swing.JDialog
 * @param title java.lang.String
 */
public DialogEOS3(javax.swing.JDialog d, String title) {
        super(d, title);
}
/**
  * DialogHelpAboutPresage constructor comment.
 * @param f javax.swing.JFrame
 */
public DialogEOS3(javax.swing.JFrame f) {
        super(f);
}
/**
  * DialogHelpAboutPresage constructor comment.
 * @param f javax.swing.JFrame
 * @param title java.lang.String
 */
public  DialogEOS3(javax.swing.JFrame f, String title) {
        super(f, title);
}

public  DialogEOS3(javax.swing.JFrame f, int astartSens, int aendSens, int aindTrav, int[] ahighest, int[] alowest, int[] aavg, int probeConf) {
    super(f);
    startSens = astartSens;
    endSens = aendSens;
    indTrav = aindTrav;
    probeC = probeConf;
    for(int i=0;i<4;i++){highest[i]=ahighest[i];}
    for(int i=0;i<4;i++){lowest[i]=alowest[i];}
    for(int i=0;i<4;i++){avg[i]=aavg[i];}


}

public void actionPerformed(java.awt.event.ActionEvent e) {
     if (e.getSource() == bSave) {
         dispose();
          Main.app.runtimeFrame.saveMenuItem.doClick();
        }

        if (e.getSource() == bClose) {
                dispose();
        }
}


public void show() {
    System.out.println("probeconf is = "+probeC);
        setTitle("End of Session Details");
        JPanel mainPanel = new JPanel(new bioera.layouts.AdvancedGridLayout(0, 1, 2, 2));
        JLabel l;
        mainPanel.add((l=new JLabel("You Have Completed the Session.", JLabel.CENTER)));
        Font f = l.getFont();
        l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() * 4 / 3));
        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel("Starting Sensitivity:        "+startSens));
        mainPanel.add(new JLabel("Ending Sensitivity:          "+endSens));
        mainPanel.add(new JLabel("Sensitivity Recalibrations: "+indTrav));
        mainPanel.add(new JLabel(""));
        if (probeC==3){
            mainPanel.add(new JLabel("Probe 1:"));
            mainPanel.add(new JLabel("Lowest Reading:              "+lowest[0]+"K ohms"));
            mainPanel.add(new JLabel("Highest Reading:              "+highest[0]+"K ohms"));
            mainPanel.add(new JLabel("Running Average:              "+avg[0]+"K ohms"));
            mainPanel.add(new JLabel(""));
            mainPanel.add(new JLabel("Probe 2:"));
            mainPanel.add(new JLabel("Lowest Reading:              "+lowest[2]+"K ohms"));
            mainPanel.add(new JLabel("Highest Reading:              "+highest[2]+"K ohms"));
            mainPanel.add(new JLabel("Running Average:              "+avg[2]+"K ohms"));
        }

        if (probeC==12){
            mainPanel.add(new JLabel("Probe 1:"));
            mainPanel.add(new JLabel("Lowest Reading:              " + lowest[1]+" degrees"));
            mainPanel.add(new JLabel("Highest Reading:              " + highest[1]+" degrees"));
            mainPanel.add(new JLabel("Running Average:              " + avg[1]+" degrees"));
            mainPanel.add(new JLabel(""));
            mainPanel.add(new JLabel("Probe 2:"));
            mainPanel.add(new JLabel("Lowest Reading:              " + lowest[3]+" degrees"));
            mainPanel.add(new JLabel("Highest Reading:              " + highest[3]+" degrees"));
            mainPanel.add(new JLabel("Running Average:              " + avg[3]+" degrees"));

        }

        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel("Would you like to save your session to disk? "));
        mainPanel.add(new JLabel("You can save from the menu if you exit this dialog."));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10))
                );

        bSave = new JButton("Yes");
        bClose = new JButton("No");
        bClose.addActionListener(this);
        bSave.addActionListener(this);

        JPanel down = new JPanel();
        down.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 2));
        down.add(bClose);
        down.add(bSave);

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);
        getContentPane().add(down, java.awt.BorderLayout.SOUTH);
        pack();
        locateOnWindow();
        super.show();
}
}
