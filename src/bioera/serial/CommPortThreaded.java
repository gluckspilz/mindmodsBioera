package bioera.serial;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashMap;

import javax.comm.CommPort;
import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.ParallelPort;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * This class will
 */

public class CommPortThreaded extends CommPortOpen {

  public static void main(String[] ap) throws IOException,
      NoSuchPortException, PortInUseException,
      UnsupportedCommOperationException {
    CommPortThreaded cp;
    try {
      cp = new CommPortThreaded();
      cp.converse();
    } catch (Exception e) {
      System.err.println("You lose!");
      System.err.println(e);
    }
  }

  public CommPortThreaded() throws IOException, NoSuchPortException,
      PortInUseException, UnsupportedCommOperationException {
    super(null);
  }

  /**
   * This version of converse() just starts a Thread in each direction.
   */
  protected void converse() throws IOException {

    String resp; // the modem response.

    new DataThread(is, System.out).start();
    new DataThread(new DataInputStream(System.in), os).start();

  }

  /** This inner class handles one side of a conversation. */
  class DataThread extends Thread {
    BufferedReader inStream;

    PrintStream pStream;

    /** Construct this object */
    DataThread(InputStream is, PrintStream os) {
      inStream = new BufferedReader(new InputStreamReader(is));
      pStream = os;
    }

    DataThread(BufferedReader is, PrintStream os) {
      inStream = is;
      pStream = os;
    }

    /** A Thread's run method does the work. */
    public void run() {
      byte ch = 0;
      try {
        while ((ch = (byte) inStream.read()) != -1)
          pStream.print((char) ch);
      } catch (IOException e) {
        System.err.println("Input or output error: " + e);
        return;
      }
    }
  }
}

/**
 * Open a serial port using Java Communications.
 *
 * @author Ian F. Darwin, http://www.darwinsys.com/
 */

class CommPortOpen {
  /** How long to wait for the open to finish up. */
  public static final int TIMEOUTSECONDS = 30;

  /** The baud rate to use. */
  public static final int BAUD = 19200;

  /** The parent JFrame, for the chooser. */
  protected JFrame parent;

  /** The input stream */
  protected BufferedReader is;

  /** The output stream */
  protected PrintStream os;

  /** The chosen Port Identifier */
  CommPortIdentifier thePortID;

  /** The chosen Port itself */
  CommPort thePort;

  public static void main(String[] argv) throws IOException,
      NoSuchPortException, PortInUseException,
      UnsupportedCommOperationException {

    new CommPortOpen(null).converse();

    System.exit(0);
  }

  /* Constructor */
  public CommPortOpen(JFrame f) throws IOException, NoSuchPortException,
      PortInUseException, UnsupportedCommOperationException {

    // Use the PortChooser from before. Pop up the JDialog.
    PortChooser chooser = new PortChooser(null);

    String portName = null;
    do {
      chooser.setVisible(true);

      // Dialog done. Get the port name.
      portName = chooser.getSelectedName();

      if (portName == null)
        System.out.println("No port selected. Try again.\n");
    } while (portName == null);

    // Get the CommPortIdentifier.
    thePortID = chooser.getSelectedIdentifier();

    // Now actually open the port.
    // This form of openPort takes an Application Name and a timeout.
    //
    System.out.println("Trying to open " + thePortID.getName() + "...");

    switch (thePortID.getPortType()) {
    case CommPortIdentifier.PORT_SERIAL:
      thePort = thePortID.open("DarwinSys DataComm",
          TIMEOUTSECONDS * 1000);
      SerialPort myPort = (SerialPort) thePort;

      // set up the serial port
      myPort.setSerialPortParams(BAUD, SerialPort.DATABITS_8,
          SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
      break;

    case CommPortIdentifier.PORT_PARALLEL:
      thePort = thePortID.open("DarwinSys Printing",
          TIMEOUTSECONDS * 1000);
      ParallelPort pPort = (ParallelPort) thePort;

      // Tell API to pick "best available mode" - can fail!
      // myPort.setMode(ParallelPort.LPT_MODE_ANY);

      // Print what the mode is
      int mode = pPort.getMode();
      switch (mode) {
      case ParallelPort.LPT_MODE_ECP:
        System.out.println("Mode is: ECP");
        break;
      case ParallelPort.LPT_MODE_EPP:
        System.out.println("Mode is: EPP");
        break;
      case ParallelPort.LPT_MODE_NIBBLE:
        System.out.println("Mode is: Nibble Mode.");
        break;
      case ParallelPort.LPT_MODE_PS2:
        System.out.println("Mode is: Byte mode.");
        break;
      case ParallelPort.LPT_MODE_SPP:
        System.out.println("Mode is: Compatibility mode.");
        break;
      // ParallelPort.LPT_MODE_ANY is a "set only" mode;
      // tells the API to pick "best mode"; will report the
      // actual mode it selected.
      default:
        throw new IllegalStateException("Parallel mode " + mode
            + " invalid.");
      }
      break;
    default: // Neither parallel nor serial??
      throw new IllegalStateException("Unknown port type " + thePortID);
    }

    // Get the input and output streams
    // Printers can be write-only
    try {
      is = new BufferedReader(new InputStreamReader(thePort
          .getInputStream()));
    } catch (IOException e) {
      System.err.println("Can't open input stream: write-only");
      is = null;
    }
    os = new PrintStream(thePort.getOutputStream(), true);
  }

  /**
   * This method will be overridden by non-trivial subclasses to hold a
   * conversation.
   */
  protected void converse() throws IOException {

    System.out.println("Ready to read and write port.");

    // Input/Output code not written -- must subclass.

    // Finally, clean up.
    if (is != null)
      is.close();
    os.close();
  }
}


class PortChooser extends JDialog implements ItemListener {
  /** A mapping from names to CommPortIdentifiers. */
  protected HashMap map = new HashMap();
  /** The name of the choice the user made. */
  protected String selectedPortName;
  /** The CommPortIdentifier the user chose. */
  protected CommPortIdentifier selectedPortIdentifier;
  /** The JComboBox for serial ports */
  protected JComboBox serialPortsChoice;
  /** The JComboBox for parallel ports */
  protected JComboBox parallelPortsChoice;
  /** The JComboBox for anything else */
  protected JComboBox other;
  /** The SerialPort object */
  protected SerialPort ttya;
  /** To display the chosen */
  protected JLabel choice;
  /** Padding in the GUI */
  protected final int PAD = 5;

  /** This will be called from either of the JComboBoxen when the
   * user selects any given item.
   */
  public void itemStateChanged(ItemEvent e) {
    // Get the name
    selectedPortName = (String)((JComboBox)e.getSource()).getSelectedItem();
    // Get the given CommPortIdentifier
    selectedPortIdentifier = (CommPortIdentifier)map.get(selectedPortName);
    // Display the name.
    choice.setText(selectedPortName);
  }

  /* The public "getter" to retrieve the chosen port by name. */
  public String getSelectedName() {
    return selectedPortName;
  }

  /* The public "getter" to retrieve the selection by CommPortIdentifier. */
  public CommPortIdentifier getSelectedIdentifier() {
    return selectedPortIdentifier;
  }

  /** A test program to show up this chooser. */
  public static void main(String[] ap) {
    PortChooser c = new PortChooser(null);
    c.setVisible(true);  // blocking wait
    System.out.println("You chose " + c.getSelectedName() +
      " (known by " + c.getSelectedIdentifier() + ").");
    System.exit(0);
  }

  /** Construct a PortChooser --make the GUI and populate the ComboBoxes.
   */
  public PortChooser(JFrame parent) {
    super(parent, "Port Chooser", true);

    makeGUI();
    populate();
    finishGUI();
  }

  /** Build the GUI. You can ignore this for now if you have not
   * yet worked through the GUI chapter. Your mileage may vary.
   */
  protected void makeGUI() {
    Container cp = getContentPane();

    JPanel centerPanel = new JPanel();
    cp.add(BorderLayout.CENTER, centerPanel);

    centerPanel.setLayout(new GridLayout(0,2, PAD, PAD));

    centerPanel.add(new JLabel("Serial Ports", JLabel.RIGHT));
    serialPortsChoice = new JComboBox();
    centerPanel.add(serialPortsChoice);
    serialPortsChoice.setEnabled(false);

    centerPanel.add(new JLabel("Parallel Ports", JLabel.RIGHT));
    parallelPortsChoice = new JComboBox();
    centerPanel.add(parallelPortsChoice);
    parallelPortsChoice.setEnabled(false);

    centerPanel.add(new JLabel("Unknown Ports", JLabel.RIGHT));
    other = new JComboBox();
    centerPanel.add(other);
    other.setEnabled(false);

    centerPanel.add(new JLabel("Your choice:", JLabel.RIGHT));
    centerPanel.add(choice = new JLabel());

    JButton okButton;
    cp.add(BorderLayout.SOUTH, okButton = new JButton("OK"));
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PortChooser.this.dispose();
      }
    });

  }

  /** Populate the ComboBoxes by asking the Java Communications API
   * what ports it has.  Since the initial information comes from
   * a Properties file, it may not exactly reflect your hardware.
   */
  protected void populate() {
    // get list of ports available on this particular computer,
    // by calling static method in CommPortIdentifier.
    Enumeration pList = CommPortIdentifier.getPortIdentifiers();

    // Process the list, putting serial and parallel into ComboBoxes
    while (pList.hasMoreElements()) {
      CommPortIdentifier cpi = (CommPortIdentifier)pList.nextElement();
      // System.out.println("Port " + cpi.getName());
      map.put(cpi.getName(), cpi);
      if (cpi.getPortType() == CommPortIdentifier.PORT_SERIAL) {
        serialPortsChoice.setEnabled(true);
        serialPortsChoice.addItem(cpi.getName());
      } else if (cpi.getPortType() == CommPortIdentifier.PORT_PARALLEL) {
        parallelPortsChoice.setEnabled(true);
        parallelPortsChoice.addItem(cpi.getName());
      } else {
        other.setEnabled(true);
        other.addItem(cpi.getName());
      }
    }
    serialPortsChoice.setSelectedIndex(-1);
    parallelPortsChoice.setSelectedIndex(-1);
  }

  protected void finishGUI() {
    serialPortsChoice.addItemListener(this);
    parallelPortsChoice.addItemListener(this);
    other.addItemListener(this);
    pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}

