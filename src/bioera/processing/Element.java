/* Element.java v 1.0.9   11/6/04 7:15 PM
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


import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import bioera.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public abstract class Element implements Propertable, bioera.config.Configurable {
	public final static ProcessingTools TOOLS = new ProcessingTools();

	public String name = "";

	private final static String propertiesDescriptions[][] = {
//		{"FIELD NAME", "LEFT DESCRIPTION", "RIGHT DESCRIPTION"}
		{"name", "Name", null}
	};

	// ->> This where other elements write values
	protected Pipe inputs[];

	// <<- This is where this element write values
	protected Pipe outputs[];

	// Design common settings
	SignalParameters signalParameters;

	protected boolean reinited = false;

	private boolean active = true; 	// Element is active by default

	protected static boolean debug = bioera.Debugger.get("processing.element");

	// Contains the first connected to input element
	protected Element predecessorElement;

	// Contains the first connected to output element
	protected Element successorElement;

	// Designer
	protected bioera.graph.designer.BoxItem designerBox;

	// Id
	private int ID;
	private static int idSequence = 100;

	// Listeners
	private ArrayList changePropertyListeners = new ArrayList();
	private ArrayList changeStatusListeners = new ArrayList();
	private ArrayList actionListeners = new ArrayList();

	// Error message
	private String designErrorMessage;

	// Process time
	protected static long mainProcessingTime;// Time updated in each processing loop
	protected static long mainStartTime;	 // Time after all elements were initialized and processing started
	protected static long mainStopTime;		 // Time after all elements stopped
	protected static long mainTimeFromStart;  // Can keep up to 24 days of continuous processing
        //protected static long sessionStartTime;  //time when presage device received session details
        //protected static long sessionTimeFromStart; //time since presage device received session details

	// Status
	public static final String STATUS_STRING = "          ";
	public static final int STATUS_LENGTH = STATUS_STRING.length();
	protected StringBuffer statusString = new StringBuffer(STATUS_STRING);
	protected int statusLevel[] = new int[STATUS_LENGTH];
/**
 * Element constructor comment.
 */
public Element() {
	super();
	ID = idSequence++;
	signalParameters = new SignalParameters(this);
	name = Tools.getClassName(this);
	if (name.endsWith(".class"))
		name = name.substring(0, name.length() - 6);  // remove .class
	name = name.substring(name.lastIndexOf('.') + 1); // remove package
	initScalarInputs();
	initScalarOutputs();
	initialize();

	designerBox = new bioera.graph.designer.DefaultBoxItem(this);
}
/**
 * Element constructor comment.
 */
public void activate() {
	if (!active) {
		active = true;

		Main.app.processor.addToActives(this);
		setStatusChar(' ', 0, 10);
	}
}
/**
 * Display constructor comment.
 */
public void addActionListener(ChangePropertyListener listener) {
	changePropertyListeners.add(listener);
}
/**
 * Display constructor comment.
 */
public void addAListener(AListener listener) {
	actionListeners.add(listener);
}
/**
 * Display constructor comment.
 */
public void addChangePropertyListener(ChangePropertyListener listener) {
	changePropertyListeners.add(listener);
}
/**
 * Display constructor comment.
 */
public void addChangeStatusListener(ChangeStatusListener listener) {
	changeStatusListeners.add(listener);
}
/**
 * 	All public are returned.
 */
public final static String arrayToString(int s[][]) {
	return ProcessingTools.arrayToString(s);
}
/**
 * 	All public are returned.
 */
public final static String arrayToString(int s[]) {
	return ProcessingTools.arrayToString(s);
}
/**
 * 	All public are returned.
 */
public final static String arrayToString(Object arra[]) {
	return ProcessingTools.arrayToString(arra);
}
/**
 * 	All public are returned.
 */
public void destroy() throws Exception {
}
/**
 * Element constructor comment.
 */
public void disactivate(Exception ex) {
	if (active) {
		active = false;

		setDesignErrorMessageExc(ex);

		Main.app.processor.removeFromActives(this);

		setStatusChar('X', 0, 10);
	}
}
/**
 * Element constructor comment.
 */
public void disactivate(String errmsg) {
	if (active) {
		active = false;

		setDesignErrorMessage(errmsg);

		Main.app.processor.removeFromActives(this);

		setStatusChar('X', 0, 10);
	}
}
/**
 * Element constructor comment.
 */
public boolean equals(Object o) {
	if (o instanceof Element) {
		return ((Element) o).getId() == this.getId();
	}

	return false;
}
/**
 * 	All public are returned.
 */
public Pipe [] getAllInputs() {
	return inputs;
}
/**
 * 	All public are returned.
 */
public Pipe [] getAllOutputs() {
	return outputs;
}
/**
 * Insert the method's description here.
 * Creation date: (10/17/2004 5:58:32 PM)
 * @return bioera.graph.designer.BoxItem
 */
public bioera.graph.designer.BoxItem getDesignerBox() {
	return designerBox;
}
/**
 * Insert the method's description here.
 * Creation date: (2/7/2004 9:52:57 PM)
 * @return java.lang.String
 */
public java.lang.String getDesignErrorMessage() {
	return designErrorMessage;
}
/**
 * 	All public are returned.
 */
public PipeDistributor [] getDistributorsConnectedToInput(int index) throws Exception {
	if (index < inputs.length && inputs[index] instanceof BufferedPipe) {
		BufferedPipe in = (BufferedPipe) inputs[index];
		if (in.isConnected()) {
			return in.getConnectedDistributors();
		}
	}

	return null;
}
/**
 * 	All public are returned.
 */
public String getElementDescription() throws Exception {
	return "none";
}
/**
 * 	All public are returned.
 */
public ElementProperty [] getElementProperties() throws Exception {
	return ProcessingTools.getElementProperties(this);
}
/**
 * 	All public are returned.
 */
public Element [] getElementsConnectedToInput(int index) throws Exception {
	if (index >= 0 && index < inputs.length && inputs[index] instanceof BufferedPipe) {
		BufferedPipe in = (BufferedPipe) inputs[index];
		if (in != null && in.isConnected()) {
			PipeDistributor d[] = in.getConnectedDistributors();
			if (d != null) {
				ArrayList list = new ArrayList();
				for (int i = 0; i < d.length; i++){
					if (d[i] != null)
						list.add(((Pipe) d[i]).getElement());
				}
				return (Element[]) list.toArray(new Element[0]);
			}
		}
	}

	return null;
}
/**
 * 	All public are returned.
 */
public Element [] getElementsConnectedToOutput(int index) throws Exception {
	ArrayList list = new ArrayList();
	if (index >= 0 && index < outputs.length) {
		PipeDistributor out = (PipeDistributor) outputs[index];
		if (out.isConnected()) {
			for (int i = 0; i < out.getConnectedCount(); i++){
				Pipe pipe = out.getConnectedPipe(i);
				list.add(pipe.getElement());
			}
		} else {
			return new Element[0];
		}
	}

	Element ret[] = new Element[list.size()];
	for (int i = 0; i < list.size(); i++){
		ret[i] = (Element) list.get(i);
	}

	return ret;
}
/**
 * 	All public are returned.
 */
public PipeDistributor getFirstDistributorConnectedToInput(int index) throws Exception {
	if (index < inputs.length && inputs[index] instanceof BufferedPipe) {
		BufferedPipe in = (BufferedPipe) inputs[index];
		if (in.isConnected()) {
			return in.getConnectedDistributors()[0];
		}
	}

	return null;
}
/**
 * 	All public are returned.
 */
public Element getFirstElementConnectedToInput(int index) throws Exception {
	if (index < inputs.length && inputs[index] instanceof BufferedPipe) {
		BufferedPipe in = (BufferedPipe) inputs[index];
		if (in.isConnected()) {
			Pipe pipe = (Pipe) in.getConnectedDistributors()[0];
			return pipe.getElement();
		}
	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 9:45:12 AM)
 * @return bioera.processing.Element
 */
public Element getFirstPredecessorElement() {
	if (predecessorElement == null) {
		// Return first element connected to input
		for (int i = 0; i < getInputsCount(); i++){
			BufferedPipe p = (BufferedPipe) inputs[i];
			if (p != null && p.isConnected())
				return p.getConnectedDistributors()[0].getElement();
		}
	}
	return predecessorElement;
}
/**
 * Element constructor comment.
 */
public int getId() {
	return ID;
}
/**
 * Element constructor comment.
 */
public Pipe getInput(int index) {
	return inputs[index];
}
/**
 * Element constructor comment.
 */
public Pipe getInputByNameOrIndex(String name, int index) {
	for (int i = 0; i < getInputsCount(); i++){
		if (getInput(i).getName().equals(name)) {
			return inputs[i];
		}
	}

	if (index >= 0 && index < getInputsCount())
		return getInput(index);

	throw new RuntimeException("Input '" + name + "' doesn't exist");
}
/**
 * Element constructor comment.
 */
public int getInputIndexById(int id) {
	for (int i = 0; i < getInputsCount(); i++){
		if (getInput(i).getId() == id) {
			return i;
		}
	}

	throw new RuntimeException("Input pipe id=" + id + " doesn't exist");
}
/**
 * Element constructor comment.
 */
public int getInputIndexByName(String name) {
	for (int i = 0; i < getInputsCount(); i++){
		if (getInput(i).getName().equals(name)) {
			return i;
		}
	}

	throw new RuntimeException("Input '" + name + "' doesn't exist");
}
/**
 * Element constructor comment.
 */
public int getInputsConnectedCount() {
	if (inputs == null)
		return 0;

	if (!(inputs instanceof BufferedPipe[]))
		return -1;

	int ret = 0;
	for (int i = 0; i < inputs.length; i++){
		if (((BufferedPipe)inputs[i]) != null && ((BufferedPipe)inputs[i]).isConnected())
			ret ++;
	}

	return ret;
}
/**
 * Element constructor comment.
 */
public abstract int getInputsCount();
/**
 * Element constructor comment.
 */
public String getName() {
	return name;
}
/**
 * Element constructor comment.
 */
public Pipe getOutput(int index) {
	return outputs[index];
}
/**
 * Element constructor comment.
 */
public Pipe getOutputByNameOrIndex(String name, int index) {
	for (int i = 0; i < getOutputsCount(); i++){
		if (getOutput(i).getName().equals(name)) {
			return outputs[i];
		}
	}

	if (index >= 0 && index < getOutputsCount())
		return getOutput(index);

	throw new RuntimeException("Output '" + name + "' doesn't exist");
}
/**
 * Element constructor comment.
 */
public abstract int getOutputsCount();
/**
 * Element constructor comment.
 */
public Pipe getPipeById(int id) {
	for (int i = 0; i < outputs.length; i++){
		if (outputs[i].getId() == id) {
			return outputs[i];
		}
	}

	for (int i = 0; i < inputs.length; i++){
		if (inputs[i].getId() == id) {
			return inputs[i];
		}
	}

	return null;
}
/**
 * Element constructor comment.
 */
public Pipe getPipeByName(String name) {
	if (name == null)
		return null;

	for (int i = 0; i < outputs.length; i++){
		if (name.equals(outputs[i].getName())) {
			return outputs[i];
		}
	}

	for (int i = 0; i < inputs.length; i++){
		if (name.equals(inputs[i].getName())) {
			return inputs[i];
		}
	}

	return null;
}
/**
 * Element constructor comment.
 */
public int getPipeIndex(Pipe p) {
	for (int i = 0; i < getInputsCount(); i++){
		if (getInput(i) == p) {
			return i;
		}
	}

	for (int i = 0; i < getOutputsCount(); i++){
		if (getOutput(i) == p) {
			return i;
		}
	}

	System.out.println("pipe=" + p.getId());
	for (int i = 0; i < getInputsCount(); i++){
		System.out.println("in="+ getInput(i).getId());
	}

	throw new RuntimeException("Pipe '" + p.getName() + "' in '" + name + "' doesn't exist");
}
/**
 * 	All public are returned.
 */
public Element getPredecessorElement() {
	if (predecessorElement == null)
		throw new RuntimeException("Predecessor not set, no elements connected to input 1");
	return predecessorElement;
}
/**
 * 	All public are returned.
 */
public Object [] getPropertyDescription(String name) {
	return ProcessingTools.searchPropertyDescription(name, propertiesDescriptions);
}
/**
 * 	All public are returned.
 */
public String [] getPropertyNames() {
	return null;
}
/**
 * Element constructor comment.
 */
public SignalParameters getSignalParameters() {
	//if (signalParameters == null) {
		//if (predecessorElement != null && predecessorElement != this)
			//return predecessorElement.getSignalParameters();
		//else if (successorElement != null && successorElement != this)
			//return successorElement.getSignalParameters();
		//else {
			//Element p = getFirstPredecessorElement();
			//if (p != null && p.isReinited())
				//return p.getSignalParameters();
			//return SignalParameters.getDefaultSettings();
		//}
	//}
	if (signalParameters == null) {
		System.out.println("==============SP is null=== in element '" + getName() +"'");
		Thread.dumpStack();
	}

	return signalParameters;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 9:45:12 AM)
 * @param newSuccessorElement bioera.processing.Element
 */
public int[] getStatusLevels() {
	return statusLevel;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 9:45:12 AM)
 * @param newSuccessorElement bioera.processing.Element
 */
public String getStatusString() {
	return statusString.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 9:45:12 AM)
 * @return bioera.processing.Element
 */
public Element getSuccessorElement() {
	if (successorElement == null) {
		// Return first element connected to first output
		for (int i = 0; i < getOutputsCount(); i++){
			PipeDistributor p = (PipeDistributor) outputs[i];
			if (p != null && p.isConnected()) {
				for (int j = 0; j < p.getConnectedCount(); j++) {
					return p.getConnectedPipe(j).getElement();
				}
			}
		}
	}

	return successorElement;
}
/**
 * This method should contain all initial initializations of the element
 * that do not depend on public (changable) fields)
 * In particular it must contain pipe initialization,
 * so that element can be connected before invoking reinit()

 * No initialization should be done in constructors,
 * because some references (e.g. to buffers) may change dynamically and need to be reinitialized
 */
protected void initialize() {
}
/**
 * Element constructor comment.
 */
public void initScalarInputs() {
	inputs = new BufferedScalarPipe[getInputsCount()];
	for (int i = 0; i < inputs.length; i++){
		inputs[i] = new BufferedScalarPipe(this);
		inputs[i].setName("" + i);
	}
}
/**
 * Element constructor comment.
 */
public void initScalarOutputs() {
	outputs = new ScalarPipeDistributor[getOutputsCount()];
	for (int i = 0; i < outputs.length; i++){
		outputs[i] = new ScalarPipeDistributor(this);
		outputs[i].setName("" + i);
	}
}
/**
 * Element constructor comment.
 */
public void initVectorInputs() {
	inputs = new BufferedVectorPipe[getInputsCount()];
	for (int i = 0; i < inputs.length; i++){
		inputs[i] = new BufferedVectorPipe(this);
		inputs[i].setName("" + i);
	}
}
/**
 * Element constructor comment.
 */
public void initVectorOutputs() {
	outputs = new VectorPipeDistributor[getOutputsCount()];
	for (int i = 0; i < outputs.length; i++){
		outputs[i] = new VectorPipeDistributor(this);
		outputs[i].setName("" + i);
	}
}
/**
 * Element constructor comment.
 */
public final boolean isActive() {
	return active;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2003 1:14:28 PM)
 * @param newName java.lang.String
 */
public boolean isReinited() {
	return reinited;
}
/**
 * 	All public are returned.
 */
public final boolean load(bioera.config.XmlConfigSection section) throws Exception {
	ID = section.getIntegerThrow("id");
	if (idSequence <= ID)
		idSequence = ID + 1;
	if (designerBox != null && section.containsSection("designer_box")) {
		designerBox.load(section.getSection("designer_box"));
	}
	return loadCustom(section);
}
/**
 * Display constructor comment.
 */
public static Element newInstance(String className) throws Exception {
	//System.out.println("creating " + className);
	// Map old names to new names
	if (className.startsWith("jbiopro."))
		className = className.substring(1);
	if (className.startsWith("biopro."))
		className = "bioera" + className.substring(6);
	int i1 = className.lastIndexOf(".");
	String n = className.substring(i1 + 1);
	//System.out.println("n=" + n);
	String names[][] = {
		{"LinearMixer", 		"Mixer"},
		{"LinearCMixer", 		"CMixer"},
		{"LinearCounter", 		"Counter"},
		{"LinearFormatter", 	"Formatter"},
		{"FormatConverter", 	"Formatter"},
		{"LinearMixer", 		"Mixer"},
		{"LinearMultiplexer", 	"Multiplexer"},
		{"MultiLinearToVector", "ScalarsToVector"},
		{"MultiScalarToVector", "ScalarsToVector"},
		{"LinearRangeFilter", 	"RangeFilter"},
		{"LinearRangeMapper", 	"RangeMapper"},
		{"LinearRateLimiter", 	"RateLimiter"},
		{"LinearTransform", 	"ScalarTransform"},
		{"LinearDisplay", 		"Oscilloscope"},
		{"LinearToVector", 		"StreamToVector"},
		{"VectorToLinear", 		"VectorToScalar"},
		{"StreamDisplay",		"Oscilloscope"},
		{"Osciloscope",			"Oscilloscope"},
		{"SerialPortDevice",	"SerialPortDepreciated", "bioera.serial."},
		{"ModEEG_v21",			"ModEEG_v21", "bioera.device.impl."},
		{"ModEEG_P2",			"ModEEG_P2", "bioera.device.impl."},
		{"RangeFilter",			"RangeFilter", "bioera.deprecated."},
		{"ScalarTransform",		"ScalarTimeTransform"},
		{"ScalarSingleTransform","ScalarInstantTransform"},
		{"VectorTransform",		"VectorTimeTransform"},
		{"VectorSingleTransform","VectorInstantTransform"},
	};

	for (int i = 0; i < names.length; i++){
		if (names[i][0].equals(n)) {
			if (names[i].length == 3 && className.startsWith(names[i][2]))
				continue;

			if (names[i].length == 3)
				className = names[i][2] + names[i][1];
			else
				className = className.substring(0, i1 + 1) + names[i][1];
			System.out.println("Converted element name from " + n + " to " + className);
			break;
		}
	}

	Class c = null;
	try {
		c = Tools.createClass(className);
	} catch (Throwable e) {
		//System.out.println("CP: " + System.getProperty("java.class.path"));
		System.out.println("Class '" + className + "' not loaded");
		//System.out.println("Classloader '" + Element.class.getClassLoader() + "' not found");

		String implFolderPath = Main.app.implFolderPath;
		if (implFolderPath == null)
			implFolderPath = "impl/";
		else {
			if (!implFolderPath.endsWith("/"))
				implFolderPath += "/";
		}

		ClassLoader cl = null;
		try {
			cl = new java.net.URLClassLoader(new java.net.URL[]{new java.net.URL("file:///" + implFolderPath)});
			c = cl.loadClass(className);
		} catch (Throwable e1) {
			System.out.println("Class URL not loaded: '" + implFolderPath + "'");
			throw new Exception("Class not loaded: " + cl);
		}
	}

	Constructor constr;
	try {
		constr = c.getConstructor(new Class[] {});
	} catch (Exception e) {
		System.out.println("No-parameter contructor not found for class '" + className + "'");
		throw e;
	}

	// Setting here are default.
	// Eventually they will be read from design Source

	try {
		return (Element) constr.newInstance(new Object[]{});
	} catch (InvocationTargetException e) {
		System.out.println("Error while contructing '" + className + "'");
		throw (Exception) e.getTargetException();
	}
}
/**
 *
 */
public void preReinit() throws Exception {
	// Check if the buffers in input VectorBufferedPipes are the same as in SignalParameters
	// If not then set it

	for (int i = 0; i < getInputsCount(); i++){
		Pipe p = getInput(i);
		if (p instanceof BufferedVectorPipe) {
			BufferedVectorPipe bp = (BufferedVectorPipe) p;
			bp.setVLength(bp.getSignalParameters().getVectorLength());
		}
	}
}
/**
 * 	All public are returned.
 */
public String printAdvancedDetails() throws Exception {
	StringBuffer sb = new StringBuffer("Element advanced details:\n");
	sb.append(">name=" + getName() + "\n");
	sb.append(">id=" + getId() + "\n");
	sb.append(">order=" + Main.app.processor.getElementActiveOrder(this) + "\n");
	sb.append(">status=" + statusString + "\n");
	for (int j = 0; j < getInputsCount(); j++){
		Pipe p = (Pipe) getInput(j);
		if (p instanceof BufferedPipe) {
			sb.append(" >" + p.getName() + ": global=" + ((BufferedPipe)p).globalCounter() + "\n");
			sb.append(" >" + p.getName() + ": occupied space=" + ((BufferedPipe)p).occupiedSpace() + "\n");
			sb.append(" >" + p.getName() + ": free space=" + ((BufferedPipe)p).availableSpace() + "\n");
		} else {
			sb.append(" >" + p.getName() + " " + p.getClass() + "\n");
		}
	}

	return sb.toString();
}
/**
 * 	All public are returned.
 */
public String printAllProperties() throws Exception {
	StringBuffer sb = new StringBuffer("[");
	Field f[] = getClass().getDeclaredFields();
	int count = 0;
	for (int i = 0; i < f.length; i++){
		try {
			Object o = f[i].get(this);
			if (sb.length() > 1)
				sb.append(", ");
			sb.append(f[i].getName() + "=" + o);
		} catch (Exception e) {
		}
	}

	sb.append(", class=" + getClass() + "]");

	return sb.toString();
}
/**
 * Element constructor comment.
 */
public abstract void process() throws Exception ;
/**
 * Element constructor comment.
 */
public void purgeInputBuffers() {
	if (inputs != null) {
		for (int i = 0; i < inputs.length; i++){
			Pipe p = inputs[i];
			if (p instanceof BufferedPipe) {
				((BufferedPipe) p).purgeAll();
			}
		}
	}
}
/**
 * Element constructor comment.
 */
public void registerReceiver(Pipe local, Pipe dest) {
	if (!local.compatibleWith(dest)) {
		System.out.println("Can't register pipe '" + dest.getName() + "' type " + dest.getType() + " to distributor '"+local.getName() + "' type " + local.getType());
		return;
	}

	if (debug)
		System.out.println("Connecting " + local.getName() + " with " + dest.getName());

	try {
		((PipeDistributor) local).register(dest);
		((BufferedPipe) dest).connectDistributor((PipeDistributor) local);
		Element elem = dest.getElement();
		if (0 == elem.getInputIndexById(dest.getId())) {
			elem.getSignalParameters().setParentSignalParameters(this.getSignalParameters());
			elem.predecessorElement = this;
			//System.out.println("connected '" + elem + "' from '" + this + "'");
		} else {
			//System.out.println("sorry " + elem + " : " + elem.getInputIndexById(dest.getId()));
		}

		//System.out.println("parent " + (elem.signalParameters.parentSignalParameters != null));
		//System.out.println("parent " + (elem.getSignalParameters().getParentSignalParameters() != null));

	} catch (RuntimeException e) {
		System.out.println("Couldn't connect BufferedPipe " + dest.getClass() + " to distributor " + local.getClass());
		if (!(local instanceof PipeDistributor))
			System.out.println("Class '" + local.getClass() + " is not PipeDistributor");
		if (!(dest instanceof BufferedPipe))
			System.out.println("Class '" + dest.getClass() + " is not BufferedPipe");
		throw e;
	}

	if (debug)
		System.out.println("Successfully registered element " + dest);
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	reinited = true;

	if (debug)
		System.out.println("Element " + getClass() + " reinited");
}
/**
 * Insert the method's description here.
 * Creation date: (2/7/2004 9:52:57 PM)
 * @param newDesignErrorMessage java.lang.String
 */
public void resetDesignErrorMessage() {
	designErrorMessage = null;
}
/**
 * 	All public are returned.
 */
public final boolean save(bioera.config.XmlCreator section) throws Exception {
	section.addTextValue("id", Integer.toString(ID));
	if (designerBox != null) {
		bioera.config.XmlCreator designSection = section.addSection("designer_box");
		designerBox.save(designSection);
	}
	return saveCustom(section);
}
/**
 * 	All public are returned.
 */
protected final static Object [] searchPropertyDescription(String name, String table[][]) {
	return ProcessingTools.searchPropertyDescription(name, table);
}
/**
 * Element constructor comment.
 */
public void sendActionEvent(int type, Object o) {
	AEvent ev = new AEvent();
	ev.element = this;
	ev.type = type;
	ev.value = o;
	for (int i = 0; i < actionListeners.size(); i++){
		((AListener)actionListeners.get(i)).doAction(ev);
	}
}
/**
 * Element constructor comment.
 */
public void sendChangePropertyEvent(String fieldName, Object oldValue) {
	ChangePropertyEvent ev = new ChangePropertyEvent();
	ev.id = fieldName;
	ev.element = this;
	for (int i = 0; i < changePropertyListeners.size(); i++){
		((ChangePropertyListener)changePropertyListeners.get(i)).propertyChanged(ev);
	}
}
/**
 * Element constructor comment.
 */
public void sendChangeStatusEvent() {
	ChangeStatusEvent ev = new ChangeStatusEvent();
	ev.element = this;
	for (int i = 0; i < changeStatusListeners.size(); i++){
		((ChangeStatusListener)changeStatusListeners.get(i)).statusChanged(ev);
	}
}
/**
 * Element constructor comment.
 */
private void setActive(boolean state) {
	if (active != state) {
		active = state;

		if (!active)
			Main.app.processor.removeFromActives(this);

		setStatusChar('X', 0, 10);
	}
}
/**
 * 	All public are returned.
 */
public void setAllInputs(Pipe p[]) {
	inputs = p;
}
/**
 * 	All public are returned.
 */
public void setAllOutputs(Pipe p[]) {
	outputs = p;
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (10/17/2004 5:58:32 PM)
 * @param newDesignerItem bioera.graph.designer.BoxItem
 */
public void setDesignerBox(bioera.graph.designer.BoxItem newDesignerItem) {
	designerBox = newDesignerItem;
}
/**
 * Insert the method's description here.
 * Creation date: (2/7/2004 9:52:57 PM)
 * @param newDesignErrorMessage java.lang.String
 */
public void setDesignErrorMessage(java.lang.String newDesignErrorMessage) {
	designErrorMessage = newDesignErrorMessage;
	if (designErrorMessage == null || designErrorMessage.length() == 0) {
		System.out.println("set to none");
		designErrorMessage = "set to none";
	}

}
/**
 * Insert the method's description here.
 * Creation date: (2/7/2004 9:52:57 PM)
 * @param newDesignErrorMessage java.lang.String
 */
public void setDesignErrorMessageExc(Exception exc) {
	designErrorMessage = exc.getMessage();
	if (designErrorMessage == null)
		designErrorMessage = exc.toString();
	if (designErrorMessage.length() == 0)
		exc.printStackTrace();
}
/**
 * Element constructor comment.
 */
public void setElementProperties(ElementProperty props[]) {
	ProcessingTools.setElementProperties(this, props);
}
/**
 * Element constructor comment.
 */
public void setId(int i) {
	ID = i;
	if (i >= idSequence)
		idSequence = i + 1;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2003 1:14:28 PM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Element constructor comment.
 */
public void setOutputDigitalRange(int r) {
	signalParameters.setDigitalRange(r);
}
/**
 * Element constructor comment.
 */
public void setOutputPhysicalRange(int r) {
	signalParameters.setPhysicalRange(r);
}
/**
 * Element constructor comment.
 */
public void setOutputPhysicalUnit(String unit) {
	signalParameters.setPhysicalUnit(unit);
}
/**
 * Element constructor comment.
 */
public void setOutputResolutionBits(int n) {
	signalParameters.setSignalResolutionBits(n);
}
/**
 * Element constructor comment.
 */
public void setOutputSignalRate(float r) {
	signalParameters.setSignalRate(r);
}
/**
 * Element constructor comment.
 */
public void setOutputSignalRate(int r) {
	signalParameters.setSignalRate(r);
}
/**
 * Element constructor comment.
 */
public void setOutputVectorLength(int len) {
	signalParameters.setVectorLength(len);

	for (int i = 0; i < getOutputsCount(); i++){
		Pipe p = getOutput(i);
		if (p instanceof VectorPipeDistributor) {
			((VectorPipeDistributor) p).setVLength(len);
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 9:45:12 AM)
 * @param newPredecessorElement bioera.processing.Element
 */
public void setPredecessorElement(Element newPredecessorElement) {
	predecessorElement = newPredecessorElement;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2003 1:14:28 PM)
 * @param newName java.lang.String
 */
public void setReinited(boolean state) {
	reinited = state;
}
/**
 * Element constructor comment.
 */
void setSignalParameters(SignalParameters sp) {
	signalParameters = sp;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 9:45:12 AM)
 * @param newSuccessorElement bioera.processing.Element
 */
public void setStatusChar(char c, int index, int lev) {
	if (index >= STATUS_LENGTH || index < 0)
		return;

	char pChar = statusString.charAt(index);
	int pLevel = statusLevel[index];
	if (pChar != c || pLevel != lev) {
		statusString.setCharAt(index, c);
		statusLevel[index] = lev;
		sendChangeStatusEvent();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 9:45:12 AM)
 * @param newSuccessorElement bioera.processing.Element
 */
public void setStatusLevels(int lev []) {
	for (int i = 0; i < Math.min(lev.length, STATUS_LENGTH); i++){
		statusLevel[i] = lev[i];
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 9:45:12 AM)
 * @param newSuccessorElement bioera.processing.Element
 */
public void setStatusString(String s) {
	if (s.length() > STATUS_LENGTH)
		s = s.substring(s.length() - STATUS_LENGTH);

	statusString.setLength(0);
	statusString.append(s);
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 9:45:12 AM)
 * @param newSuccessorElement bioera.processing.Element
 */
public void setSuccessorElement(Element newSuccessorElement) {
	successorElement = newSuccessorElement;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2003 1:14:28 PM)
 * @param newName java.lang.String
 */
public void start() throws Exception {

}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2003 1:14:28 PM)
 * @param newName java.lang.String
 */
public void stop() throws Exception {

}
/**
 * 	All public are returned.
 */
public final File toRootFolder(File f) {
	if (f.isAbsolute())
		return f;
	else
		return new File(Main.app.getRootFolder(), f.getPath());
}
/**
 * 	All public are returned.
 */
public final String toRootFolder(String folder) {
	File f = new File(folder);
	if (f.isAbsolute())
		return folder;
	else
		return new File(Main.app.getRootFolder(), folder).getAbsolutePath();
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2003 1:14:28 PM)
 * @param newName java.lang.String
 */
public String toString() {
	return getName();
	//StringBuffer sb = new StringBuffer("Element: " + getName() + "\nInputs: \n");
	//for (int i = 0; i < getInputsCount(); i++){
		//sb.append(getInput(i) + "\n");
	//}
	//sb.append("Outputs:\n");
	//for (int i = 0; i < getOutputsCount(); i++){
		//sb.append(getOutput(i) + "\n");
	//}
	//return sb.toString();
}
/**
	This method is invoked after intialize() during runtime
	to make sure that all new pipes are connected the same as previous
 */
void transformPipes(Pipe[] from, Pipe [] to) {
	for (int i = Math.min(from.length, to.length) - 1; i >=0 ; i--){
		Pipe src = from[i];
	}
}
/**
 * 	All public are returned.
 */
protected final void verifyDesignState(boolean state) throws bioera.graph.designer.DesignException {
	TOOLS.verifyDesignState(state, this);
}

/**
 * 	All public are returned.
 */
public boolean loadCustom(bioera.config.XmlConfigSection section) throws Exception {
	return false;
}

/**
 * 	This can be overridden
 */
public boolean saveCustom(bioera.config.XmlCreator section) throws Exception {
	return false;
}
}
