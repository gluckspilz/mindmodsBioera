/* Processor.java v 1.0.9   11/6/04 7:15 PM
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


import java.util.*;
import bioera.*;
import bioera.graph.designer.*;
import bioera.graph.runtime.*;
import bioera.config.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class Processor {
	private static boolean debug = bioera.Debugger.get("processor.level1");
	private static boolean debug2 = bioera.Debugger.get("processor.level2");
	private static boolean debugReinit = bioera.Debugger.get("processor.reinit");

	public int processingSequenceNumber;

	// Main semafor for processing
	public boolean processInLoop = false;

	// How many times processing is to be done
	// Usually used to stop processing with a delay
	private int upToCounter = -1;

	// All elements
	private ElementSet elementSet;

	// Active elements are moved here during initialization
	private Element activeElements[] = new Element[0];

	// ================= Diagnostic ================= //

	// check if elements buffers are overloaded
	private static final boolean checkBuffers = true;

	// Contains buffers of all active elements for higher performance
	private BufferedPipe buffers[];

	private static boolean showTimes = bioera.Debugger.get("processor.showtimes");

	private static boolean showBuffers = bioera.Debugger.get("processor.showbuffers");
	private static final long showBuffersPeriod = 2000;	// Print every 2 seconds
	private long showBuffersTime;			// buffer with time

	// remember times of processing for each element
	private static boolean countTimes = bioera.Debugger.get("processor.countproctimes");
	private long singleProcessingTimes[];				// The same size as elements[]
	private int buffersPrevN[];							// lost packets due to buffer overload
	private long showDiagnosticTimeout = 15 * 60;		// [s] Diagnostic information are printed periodically
	private long showDiagnosticLastTime;
	private long timeCounter = 0;

	public boolean allReinitialized = false;
	private int signalMatrix = 0;
	private Object signalSynchrObject = new Object();
	public static final int SIGNAL_START = 1;
	public static final int SIGNAL_STOP = 2;
/**
 * Processor constructor comment.
 */
public Processor(ElementSet el) {
	super();
	elementSet = el;
	//el.designerBox = null;
}
/**
 * RuntimeManager constructor comment.
 */
public void add(Element e) {
	elementSet.addElement(e);

	if (countTimes) {
		singleProcessingTimes = new long[elementSet.elements.length];
	}
}
/**
 * RuntimeManager constructor comment.
 */
private void addBuffers(Element e) throws Exception {
	for (int i = 0; i < e.getInputsCount(); i++){
		if (e.getInput(i) instanceof BufferedPipe) {

			// First check if they were already added
			for (int k = 0; k < buffers.length; k++){
				if (e.getInput(i) == buffers[k]) {
					// Yes, there were, skip this element
					if (debug)
						System.out.println("Element " + e.getName() + " already added to buffers check");
					return;
				}
			}

			int ni[] = new int[buffersPrevN.length + 1];
			System.arraycopy(buffersPrevN, 0, ni, 0, buffersPrevN.length);
			ni[ni.length - 1] = 0;
			buffersPrevN = ni;

			BufferedPipe n[] = new BufferedPipe[buffers.length + 1];
			System.arraycopy(buffers, 0, n, 0, buffers.length);
			n[n.length - 1] = (BufferedPipe) e.getInput(i);
			buffers = n;
		}
	}
}
/**
 * RuntimeManager constructor comment.
 */
public void addToActives(Element e) {
	// First check if this element is already here
	for (int k = 0; k < activeElements.length; k++){
		if (e == activeElements[k]) {
			// Yes, it is here
			if (debug)
				System.out.println("Element " + e.getName() + " already added to active elements");
			return;
		}
	}

	for (int k = 0; k < activeElements.length; k++){
		if (activeElements[k] == null) {
			activeElements[k] = e;
			return;
		}
	}

	Element nA[] = new Element[activeElements.length + 1];
	System.arraycopy(activeElements, 0, nA, 0, activeElements.length);
	nA[activeElements.length] = e;
	activeElements = nA;
}
/**
 * RuntimeManager constructor comment.
 */
private void checkBuffers() throws Exception {
	//System.out.println("Checking buffers");
	int n;
	for (int i = 0; i < buffers.length; i++){
		n = buffers[i].getLostPackets();
		if (n > buffersPrevN[i]) {
			Pipe p = (Pipe) buffers[i];
			System.out.println("Buffer lost "+(n - buffersPrevN[i])+" packets (" + p.getElement().getName() + ", " + p.getName() + "," + buffers[i].availableSpace() + ")");
			buffersPrevN[i] = n;
		}
	}
}
/**
 * RuntimeManager constructor comment.
 */
public boolean containsActiveElement(Element e) {
	for (int i = 0; i < activeElements.length; i++){
		if (e == activeElements[i])
			return true;
	}

	return false;
}
/**
 * RuntimeManager constructor comment.
 */
public boolean containsElement(Element e) {
	for (int i = 0; i < elementSet.elements.length; i++){
		if (e == elementSet.elements[i])
			return true;
	}

	return false;
}
public void dispose() throws Exception {
	if (isProcessing())
		internalStopProcessing();

	for (int i = 0; i < elementSet.elements.length; i++){
		if (elementSet.elements[i] != null) {
			elementSet.elements[i].destroy();
			elementSet.elements[i] = null;
		}
	}

	// Release all elements
	for (int i = 0; i < activeElements.length; i++){
		activeElements[i] = null;
	}

	for (int i = 0; i < buffers.length; i++){
		buffers[i] = null;
	}
}
/**
 * RuntimeManager constructor comment.
 */
public Element [] getActiveElements() {
	return activeElements;
}
public Element [] getAllElements() {
	return elementSet.elements;
}
/**
 * RuntimeManager constructor comment.
 */
public int getElementActiveOrder(Element e) {
	if (e == null)
		return -1;
	for (int i = 0; i < activeElements.length; i++){
		if (e == activeElements[i])
			return i;
	}

	return -1;
}
/**
 * RuntimeManager constructor comment.
 */
public ElementSet getElementSet() {
	return elementSet;
}
public long getRelativeTimeCounter() {
	if (timeCounter == 0) {
		timeCounter = System.currentTimeMillis();
		return 0;
	} else {
		long ret = System.currentTimeMillis() - timeCounter;
		timeCounter = System.currentTimeMillis();
		return ret;
	}
}
/**
 * RuntimeManager constructor comment.
 */
public void initializeAll() throws Exception {
	if (ConfigurableSystemSettings.backupDesign)
		Main.app.saveOnlyDesign(new java.io.File(Main.app.getDesignFolder(), "backup.bpd"));

	reinitProcessingElements();

	// Repaint graphics elements
	for (int i = 0; i < elementSet.elements.length; i++){
		Element e = elementSet.elements[i];
		if (e == null)
			System.out.println("Element " + i + " is null (2)");
		if (e instanceof Display) {
			((Display) e).getChart().resetChart();
			((Display) e).getChart().getComponent().repaint();
			int n = 30;
			//while (n-- > 0 && !((Display) e).getChart().isInitialized()) {
				//Thread.sleep(5);
			//}
			//if (n < 0)
				//System.out.println("-------Display not initialized fast enough2-------- " + e.getName());
		}
	}
}
/**
 * RuntimeManager constructor comment.
 */
void internalStartProcessing() {
	if (debug2)
		System.out.println("Starting processing ");
	//Thread.currentThread().dumpStack();

	Element.mainProcessingTime = System.currentTimeMillis();

	// Purge all internal input buffers
	if (debug)
		System.out.println("Purging input buffers");
	for (int i = 0; i < activeElements.length; i++){
		try {
			activeElements[i].purgeInputBuffers();
		} catch (Exception e){
		}
	}

	// Start all elements
	for (int i = 0; i < activeElements.length; i++){
		Element elem = activeElements[i];
		if (elem == null)
			continue;
		if (debug)
			System.out.println("Starting element: " + elem.getName());
		try {
			elem.start();
		} catch (Exception ex) {
			if (debug)
				System.out.println("Couldn't start element " + elem.getName() + ": " + ex);
			elem.disactivate(ex);
			elem.setDesignErrorMessageExc(ex);
		}
	}

	// Set menus and toolbars
	if (Main.app.designFrame != null) {
		javax.swing.JMenuItem mi = Main.app.designFrame.getMenuItem(Commands.STOP, Commands.START);
		if (mi != null) {
			mi.setText(Commands.STOP);
		} else {
			System.out.println("menu item not found");
		}
		try {
			Main.app.designFrame.setToolBarButton(Commands.START, false);
			Main.app.designFrame.setToolBarButton(Commands.STOP, true);
		} catch (Exception e) {
			System.out.println("StopProcessing error: " + e);
		}
	}


	Element.mainStartTime = System.currentTimeMillis();
//        Element.sessionStartTime = Main.app.sessionStartTime;
	processInLoop = true;
	if (debug)
		System.out.println("Processing started");
}
/**
 * RuntimeManager constructor comment.
 */


void internalStopProcessing() {
        processInLoop = false;
        for (int i = 0; i < activeElements.length; i++){
                Element elem = activeElements[i];
                if (elem == null)
                        continue;
                try {
                        elem.stop();
                } catch (Exception ex) {
                        if (debug)
                                System.out.println("Couldn't properly stop element " + elem.getName() + ": " + ex);
                        elem.disactivate(ex);
                        elem.setDesignErrorMessageExc(ex);
                }
        }

        if (Main.app.designFrame != null) {
                javax.swing.JMenuItem mi = Main.app.designFrame.getMenuItem(Commands.STOP, Commands.START);
                if (mi != null) {
                        mi.setText(Commands.START);
                } else {
                        System.out.println("menu item not found");
                }
                try {
                        Main.app.designFrame.setToolBarButton(Commands.START, true);
                        Main.app.designFrame.setToolBarButton(Commands.STOP, false);
                } catch (Exception e) {
                        System.out.println("StopProcessing error: " + e);
                }
        }


        Element.mainStopTime = System.currentTimeMillis();
        if (debug)
                System.out.println("Processing stopped");
}

public boolean isProcessing() {
	return processInLoop;
}
/**
 * RuntimeManager constructor comment.
 */
private void processAll() throws Exception {
	processingSequenceNumber++;
	for (int i = 0; i < activeElements.length; i++){
		if (activeElements[i] == null)
			continue;
		if (debug2)
			System.out.println("processing " + activeElements[i].getName());
		try {
			if (countTimes) {
				long time = System.currentTimeMillis();
				activeElements[i].process();
				singleProcessingTimes[i] += System.currentTimeMillis() - time;
			} else {
				activeElements[i].process();
			}
		} catch (Exception e) {
			// Something went wrong during processing
			if (debug || e.toString().indexOf("NullPointerException") != -1)
				e.printStackTrace();

			// Save error in file
			Main.app.errors.write(e);

			System.out.println("Processing failed in element " + activeElements[i].getName() + ": " + e);
			internalStopProcessing();
			if (DesignSettings.restartUponProcessingFailed) {
				System.out.println("Reinitializing.");
				reinitElement(activeElements[i]);
				internalStartProcessing();
			}
		}
	}

	if (checkBuffers) {
		checkBuffers();
	}
}
/**
 * RuntimeManager constructor comment.
 */
public void processLoop() throws Exception {
	if (debug)
		System.out.println("Started loop processing");

	if (debug2) {
		System.out.println("Thread id=" + Thread.currentThread().getName());
		Thread.currentThread().dumpStack();
		System.out.print("Active elements: ");
		for (int i = 0; i < activeElements.length; i++){
			if (activeElements[i] != null) {
				System.out.print(", " + activeElements[i].getName());
			}
		}
		System.out.println();
	}

	long diff = 0, time1, sum = 0;
	int counter = 0;

	while (true){
		if (debug2)
			System.out.println("-------------Start processing loop---------------");
		if (upToCounter != -1) {
			if (upToCounter > 0) {
				upToCounter--;
			} else {
				internalStopProcessing();
				upToCounter = -1;
				break;
			}
		}

		if (signalMatrix != 0) {
			serveSignal();
		}

		if (!processInLoop)
			break;

		Element.mainProcessingTime = System.currentTimeMillis();
//                Element.sessionTimeFromStart = (int) (Element.mainProcessingTime - Main.app.sessionStartTime);
		Element.mainTimeFromStart = (int) (Element.mainProcessingTime - Element.mainStartTime);

		processAll();

		time1 = System.currentTimeMillis();
		diff = time1 - Element.mainProcessingTime;
		sum += diff;
		counter++;

		// Check if there was an action from keyboard
		if (System.in.available() > 0) {
			if (Keyboard.handle(this))
				break;
		}

		// Show buffers
		if (showBuffers) {
			if (time1 - showBuffersTime > showBuffersPeriod) {
				showBuffersTime = time1;
				showBuffers();
			}
		}

		// Show times
		if (showTimes && counter > 100) {
			System.out.println("total average time " + (sum / counter));
			counter = 0;
			sum = 0;
			if (countTimes) {
				System.out.print("elem times: ");
				for (int i = 0; i < activeElements.length; i++){
					if (activeElements[i] != null)
						System.out.print(activeElements[i].getName() + "=" + singleProcessingTimes[i] + ", ");
					singleProcessingTimes[i] = 0;
				}
				System.out.println();
			}
		}

		// Show diagnostic info
		if (debug && showDiagnosticTimeout > 0) {
			if (time1 - showDiagnosticLastTime > (showDiagnosticTimeout * 1000)) {
				long total = Runtime.getRuntime().totalMemory();
				long free = Runtime.getRuntime().freeMemory();
				System.out.println("Memory: f= " + ProcessingTools.formatNumber(free) + ", t= " + ProcessingTools.formatNumber(total));

				showDiagnosticLastTime = time1;
			}
		}

		// Sleep for minimal time
		Thread.sleep(DesignSettings.sleepTimeMillis);
		if (debug2)
			System.out.println("-------------End processing loop---------------");
	}

	if (debug)
		System.out.println("Stopped loop processing");
}
/**
 * RuntimeManager constructor comment.
 */
public void reinitElement(Element e) throws Exception {
	if (debugReinit)
		System.out.println("Reiniting 1 element " + e.getName());

	boolean wasReinited = e.isReinited();
	boolean wasActive = e.isActive();

	e.activate();
	try {
		e.preReinit();
		e.reinit();
		e.resetDesignErrorMessage();
	} catch (Exception ex) {
		if (debug)
			System.out.println("Initialization error (" + e.getName() + "): " + ex);
		e.setReinited(true);
		e.disactivate(ex);

		// Save error in file
		Main.app.errors.write(ex);

		if (debug) {
			ex.printStackTrace();
		}
	}

	if (e.isReinited()) {
		if (checkBuffers) {
			addBuffers(e);
		}
		if (e.isActive()) {
			addToActives(e);
		}
	}

	//if (e instanceof Display) {
		//if (debug)
			//System.out.println("Reinitializing display");
		////((Display) e).getChart().invalidate();
		//((Display) e).getChart().resetChart();
		//((Display) e).getChart().getComponent().repaint();
		//int n = 30;
		//while (n-- > 0 && !((Display) e).getChart().isInitialized()) {
			//Thread.sleep(5);
		//}

		//if (n < 0)
			//System.out.println("-------Displays not initialized fast enough in " + e.getName());
	//}

	if (debugReinit)
		System.out.println("Reinited 1 element " + e.getName());
}
/**
 * RuntimeManager constructor comment.
 */
public void reInitializeAll() {
	allReinitialized = true;
	try {
		boolean b = isProcessing();
		if (b)
			internalStopProcessing();
		initializeAll();
		if (b)
			internalStartProcessing();
	} catch (Exception ex) {
		System.out.println("Error during reinit: " + ex);
		ex.printStackTrace();
	} catch (Throwable ex) {
		System.out.println("Fatal error: ");
		ex.printStackTrace();
	}
}
/**
 * RuntimeManager constructor comment.
 */
public void reinitProcessingElements() throws Exception {
	if (showTimes)
		System.out.println("------Initing processing elements------- " + getRelativeTimeCounter());
	elementSet.elements = (Element[]) new Sorter().sortProcessingElements(elementSet.elements);

	if (debug)
		System.out.println("Runtime reinitAll");
	if (debugReinit)
		System.out.println("--------------ReinitProcessingElement----------- " + new Date());
	buffers = new BufferedPipe[0];
	buffersPrevN = new int[0];
	Element e;
	for (int i = 0; i < elementSet.elements.length; i++){
		elementSet.elements[i].setReinited(false);
	}
	Vector activeElems = new Vector();
	int reinited = 0, prev;
	while (reinited < elementSet.elements.length ) {
		prev = reinited;
		for (int a = 0; a < elementSet.elements.length; a++){
			e = elementSet.elements[a];
			if (!e.isReinited()) {
				if (debugReinit)
					System.out.println("Trying reinit element '" + e.getName() + "'");
				e.activate();
				try {
					if (showTimes)
						System.out.println("Initing " + e.getName() + " : " + getRelativeTimeCounter());
					e.preReinit();
					e.reinit();
					if (showTimes)
						System.out.println("Inited " + e.getName() + ": " + getRelativeTimeCounter());
					e.resetDesignErrorMessage();
				} catch (Exception ex) {
					if (showTimes)
						System.out.println("Inited with error: " + e.getName() + ": " + getRelativeTimeCounter());
					if (debug)
						System.out.println("Initialization error (" + e.getName() + "): " + ex);
					e.setReinited(true);
					e.disactivate(ex);
					if (debug) {
						ex.printStackTrace();
					}
					// Save error in log file
					Main.app.errors.write(ex);
				} catch (Throwable ex) {
					e.destroy();
					remove(e);
					System.out.println("Fatal error in element " + e.getName());
					ex.printStackTrace();
					break;
				}

				if (e.isReinited()) {
					if (debugReinit)
						System.out.println("                   Reinited element '" + e.getName() + "'");
					reinited ++;
					if (checkBuffers) {
						addBuffers(e);
					}
					if (e.isActive()) {
						activeElems.addElement(e);
					}
				}
			}
		}

		if (prev == reinited) {
			for (int i = 0; i < elementSet.elements.length; i++){
				e = elementSet.elements[i];
				if (!e.isReinited()) {
					e.disactivate("Design loop detected");
					e.setReinited(true);
					if (debugReinit)
						System.out.println("Design loop detected");
				}
			}
			break;
		}
	}

	activeElements = new Element[activeElems.size()];
	for (int i = 0; i < activeElements.length; i++){
		activeElements[i] = (Element) activeElems.elementAt(i);
	}

	if (showTimes)
		System.out.println("--------Inited processing elements-------- " + getRelativeTimeCounter());

	if (debugReinit)
		System.out.println("--finished-ReinitProcessingElement-- ");

}
/**
 * RuntimeManager constructor comment.
 */
public Element remove(Element e) throws Exception {
	return elementSet.removeElement(e);
}
/**
 * RuntimeManager constructor comment.
 */
public void removeFromActives(Element e) {
	if (activeElements.length > 0) {
		for (int k = 0; k < activeElements.length; k++){
			if (e == activeElements[k]) {
				//Element nA[] = new Element[activeElements.length - 1];
				//System.arraycopy(activeElements, 0, nA, 0, k);
				//System.arraycopy(activeElements, k + 1, nA, k, activeElements.length - k - 1);
				//activeElements = nA;
				activeElements[k] = null;
			}
		}
	}
}
/**
 * RuntimeManager constructor comment.
 */
public void repaintGraphics() throws Exception {
	//System.out.println("repainting");
	// Wait until all graphics elements are painted
	for (int i = 0; i < elementSet.elements.length; i++){
		Element e = elementSet.elements[i];
		if (e == null)
			System.out.println("Element " + i + " is null (1)");
		if (e instanceof Display) {
			int n = 30;
			while (n-- > 0 && !((Display) e).getChart().isInitialized()) {
				Thread.sleep(5);
			}

			if (n < 0){
                            //System.out.println("-------Displays not initialized fast enough1-------- " +  e.getName());
                        }
			//System.out.println("repainted " + e.getName());
		}
	}
}
/**
 * RuntimeManager constructor comment.
 */
public void serveSignal() throws Exception {
	if ((signalMatrix & SIGNAL_STOP) != 0) {
		internalStopProcessing();
		synchronized (signalSynchrObject) {
			signalMatrix = signalMatrix & ~SIGNAL_STOP;
		}
	}

	if ((signalMatrix & SIGNAL_START) != 0) {
		internalStartProcessing();
		synchronized (signalSynchrObject) {
			signalMatrix = signalMatrix & ~SIGNAL_START;
		}
	}

	signalMatrix = 0;
}
public static void setCountTimes(boolean newValue) {
	countTimes = newValue;
}
public static void setShowBuffers(boolean newValue) {
	showBuffers = newValue;
}
public void setSignal(int m) {
	synchronized (signalSynchrObject) {
		signalMatrix |= m;
	}

	if (m == SIGNAL_START)
		processInLoop = true;
}
public void setUpCounter(int c) {
	upToCounter = c;
}
/**
 * RuntimeManager constructor comment.
 */
private void showBuffers() throws Exception {
	StringBuffer sb = new StringBuffer("Buffers:\n");
	int n; Element e;
	for (int i = 0; i < elementSet.elements.length; i++){
		e = elementSet.elements[i];
		sb.append("> " + e.getName() + ": ");
		for (int j = 0; j < e.getInputsCount(); j++){
			Pipe p = (Pipe) e.getInput(j);
			if (p instanceof BufferedPipe) {
				sb.append("" + ((BufferedPipe)p).globalCounter() + " ");
			} else {
				sb.append("-1 ");
			}
		}
		sb.append("\n");
	}
	System.out.println(sb.toString());
}
/**
 * RuntimeManager constructor comment.
 */
public void startProcessing() {
	if (isProcessing())
		return;
	setSignal(SIGNAL_START);
	int n = 30;
	while (n-- > 0 && (signalMatrix & SIGNAL_START) != 0)
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}

	if (n == -1)
		System.out.println("Sending Presage Start Command..");//mmods
}

/**
 * RuntimeManager constructor comment.
 */
public void stopProcessing() {
	if (!isProcessing())
		return;
	setSignal(SIGNAL_STOP);
	int n = 30;
	while (n-- > 0 && (signalMatrix & SIGNAL_STOP) != 0)
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}

	if (n == -1)
		System.out.println("Processor not stopped properly");
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
public static void setDebug2(boolean newValue) {
	debug2 = newValue;
}
public static void setDebugReinit(boolean newValue) {
	debugReinit = newValue;
}
public static void setShowTimes(boolean newValue) {
	showTimes = newValue;
}
}
