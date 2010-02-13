/* SignalParameters.java v 1.0.9   11/6/04 7:15 PM
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

import java.lang.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class SignalParameters implements Propertable {

	// Digital parameters
	public float signalRate = 256;	// samples per second
	public int signalResolutionBits = 10;	
	public int digitalMin = 0;
	public int digitalMax = 1023;

	// Phusical parameters
	public float physicalMin = -256;
	public float physicalMax = 256;
	public String physicalUnit = "uV";

	// Vector parameters, used only in vector streams
	public int vectorLength = 1;	// If vector is 1, then this means scalar
	public float vectorMax = 1.0f;
	public float vectorMin = 0.0f;
	public String vectorUnit = "Hz";
	public String vectorFieldDescriptions[] = new String[]{""};
	
	public String info = "Default Parameters";	
		
	private static final String propertiesDescriptions[][] = {
		{"signalRate", "Rate", ""},
		{"signalResolutionBits", "Bits", ""},
		{"digitalMax", "Digital max", ""},
		{"digitalMin", "Digital min", ""},		
		{"physicalMax", "Physical max", ""},		
		{"physicalMin", "Physical min", ""},		
		{"vectorLength", "Length", ""},
		{"vectorDescriptions", "Vector fields names", ""},
		{"info", "Info", ""}
	};

	protected SignalParameters parentSignalParameters = null;		
	protected Element element;

	private int id = 0;
	private static int inSeq = 1;
	private static Element track;
	private boolean inLoop;
SignalParameters() {
	id = inSeq++;
	digitalMin = Integer.MIN_VALUE;
	digitalMax = Integer.MAX_VALUE;
	signalResolutionBits = -1;
	
	physicalMin = Float.MIN_VALUE;
	physicalMax = Float.MAX_VALUE;
	physicalUnit = null;
	signalRate = -1;
	info = null;
	
	vectorLength = -1;	
	vectorMin = Float.MIN_VALUE;
	vectorMax = Float.MAX_VALUE;
	vectorUnit = null;
	vectorFieldDescriptions = null;
}
public SignalParameters(Element e) {
	this();
	element = e;
	if (e.signalParameters != null)
		this.inheritReferences(e.signalParameters);
}
public SignalParameters(String name, int rate, int range, int bits, String unit) {
	this();

	if (range < 0 && bits < 0)
		throw new RuntimeException("SP: Bad params");

	if (name == null)
		info = "none";
	else 
		info = name;
	signalRate = rate;
	if (unit == null)
		physicalUnit = "none";
	else
		physicalUnit = unit;
	if (range >= 0) {
		physicalMax = digitalMax = range;
		physicalMin = digitalMin = 0;		
	} else {
		physicalMax = digitalMax = 1 << bits;
		physicalMin = digitalMin = 0;
	}

	if (bits >= 0) {
		signalResolutionBits = bits;
	} else {
		signalResolutionBits = 0;
		while ((1 << signalResolutionBits) < range) {
			signalResolutionBits++;
		}
	}
	
	
}
public SignalParameters createCopyOfValues() {
	try {
		SignalParameters ret = new SignalParameters();
		
		ret.signalRate = getSignalRate();
		ret.signalResolutionBits = getSignalResolutionBits();
		ret.digitalMin = getDigitalMin();
		ret.digitalMax = getDigitalMax();

		// Phusical parameters
		ret.physicalMin = getPhysMin();
		ret.physicalMax = getPhysMax();
		ret.physicalUnit = getPhysicalUnit();

		// Vector parameters, used only in vector streams
		ret.vectorLength = getVectorLength();
		ret.vectorMax = getVectorMax();
		ret.vectorMin = getVectorMin();
		ret.vectorUnit = getVectorUnit();
		String t[] = getVectorFieldDescriptions();
		ret.vectorFieldDescriptions = new String[t.length];
		System.arraycopy(t, 0, ret.vectorFieldDescriptions, 0, t.length);
	
		ret.info = getInfo();
		ret.element = element;

		return ret;
	} catch (Exception e) {
		throw new RuntimeException("Clone error: " + e);
	}
}
public boolean equals(Object o) {
	if (!(o instanceof SignalParameters))
		return false;

	SignalParameters p = (SignalParameters) o;

	return signalRate == p.signalRate &&
	digitalMax == p.digitalMax  &&
	digitalMin == p.digitalMin  &&
	physicalMax == p.physicalMax  &&
	physicalMin == p.physicalMin  &&
	signalResolutionBits == p.signalResolutionBits &&
	vectorLength == p.vectorLength;			
}
public static SignalParameters getDefaultSettings() {
	SignalParameters defaultSettings = new SignalParameters();
	defaultSettings.digitalMin = 0;
	defaultSettings.digitalMax = 1023;
	defaultSettings.signalResolutionBits = 10;
	defaultSettings.physicalMin = -256;
	defaultSettings.physicalMax = 256;
	defaultSettings.physicalUnit = "uV";
	defaultSettings.signalRate = 256;
	defaultSettings.vectorLength = 1;
	defaultSettings.vectorMin = -1;
	defaultSettings.vectorMax = 0;
	defaultSettings.vectorUnit = "";
	defaultSettings.info = "Default parameters";
	defaultSettings.vectorFieldDescriptions = new String[]{""};	
	return defaultSettings;
}
/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 2:16:09 PM)
 * @return int
 */
public int getDigitalMax() {
	int ret;
	inLoop = true;
	if (digitalMax == Integer.MAX_VALUE)
		ret = getParentSignalParameters().getDigitalMax();
	else {
		ret = digitalMax;
		track = element;
	}
	inLoop = false;
	return ret;
}
/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 2:16:09 PM)
 * @return int
 */
public int getDigitalMin() {
	int ret;
	inLoop = true;
	if (digitalMin == Integer.MIN_VALUE)
		ret = getParentSignalParameters().getDigitalMin();
	else {
		ret = digitalMin;
		track = element;
	}
	inLoop = false;
	return ret;		
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:21:13 PM)
 * @return int
 */
public int getDigitalRange() {
	int ret;
	inLoop = true;
	if (digitalMax == Integer.MAX_VALUE || digitalMin == Integer.MIN_VALUE)
		ret = getParentSignalParameters().getDigitalRange();
	else {
		ret = digitalMax - digitalMin + 1;
		track = element;
	}
	inLoop = false;
	return ret;		
}
public int getId() {
	return id;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:21:13 PM)
 * @return java.lang.String
 */
public java.lang.String getInfo() {
	String ret;
	inLoop = true;
	if (info == null)
		ret = getParentSignalParameters().getInfo();
	else {
		ret = info;
		track = element;
	}
	inLoop = false;
	return ret;		
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:36:38 PM)
 * @return bioera.processing.SignalParameters
 */
public SignalParameters getParentSignalParameters() {
	if (parentSignalParameters == null || parentSignalParameters.inLoop) {
		//System.out.println("ParentSignalParameters not set in element '" + element + "'");
		//Thread.dumpStack();
		return getDefaultSettings();
	}

	return parentSignalParameters;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:30:08 PM)
 * @return java.lang.String
 */
public java.lang.String getPhysicalUnit() {
	String ret;
	inLoop = true;
	if (physicalUnit == null)
		ret = getParentSignalParameters().getPhysicalUnit();
	else {
		ret = physicalUnit;
		track = element;
	}
	inLoop = false;
	return ret;
}
/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 2:16:09 PM)
 * @return float
 */
public float getPhysMax() {
	float ret;
	inLoop = true;
	if (physicalMax == Float.MAX_VALUE)
		ret = getParentSignalParameters().getPhysMax();
	else {
		ret = physicalMax;
		track = element;
	}
	inLoop = false;
	return ret;
}
/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 2:16:09 PM)
 * @return float
 */
public float getPhysMin() {
	float ret;
	inLoop = true;
	if (physicalMin == Float.MIN_VALUE)
		ret = getParentSignalParameters().getPhysMin();
	else {
		ret = physicalMin;
		track = element;
	}
	inLoop = false;
	return ret;	
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:21:13 PM)
 * @return int
 */
public float getPhysRange() {
	float ret;
	inLoop = true;
	if (physicalMax == Float.MAX_VALUE || physicalMin == Float.MIN_VALUE)
		ret = getParentSignalParameters().getPhysRange();
	else {
		ret = physicalMax - physicalMin;
		track = element;
	}
	inLoop = false;
	return ret;	
}
public Object [] getPropertyDescription(String name) {
	return ProcessingTools.searchPropertyDescription(name, propertiesDescriptions);
}
/**
 * getPropertyNames method comment.
 */
public java.lang.String[] getPropertyNames() {
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:21:13 PM)
 * @return int
 */
public float getSignalRate() {
	float ret;
	inLoop = true;
	if (signalRate == -1)
		ret = getParentSignalParameters().getSignalRate();
	else {
		ret = signalRate;
		track = element;
	}
	inLoop = false;
	return ret;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:21:13 PM)
 * @return int
 */
public int getSignalResolutionBits() {
	int ret;
	inLoop = true;
	if (signalResolutionBits == -1)
		ret = getParentSignalParameters().getSignalResolutionBits();
	else {
		ret = signalResolutionBits;
		track = element;
	}
	inLoop = false;
	return ret;	
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:36:38 PM)
 * @return bioera.processing.SignalParameters
 */
public String[] getVectorFieldDescriptions() {
	String ret[];
	inLoop = true;
	if (vectorFieldDescriptions == null)
		ret = getParentSignalParameters().getVectorFieldDescriptions();
	else {
		ret = vectorFieldDescriptions;
		track = element;
	}
	inLoop = false;
	return ret;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:36:38 PM)
 * @return bioera.processing.SignalParameters
 */
public int getVectorLength() {
	int ret;
	inLoop = true;
	if (vectorLength == -1)
		ret = getParentSignalParameters().getVectorLength();
	else {
		ret = vectorLength;
		track = element;
	}
	inLoop = false;
	return ret;
}
/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 2:16:09 PM)
 * @return float
 */
public float getVectorMax() {
	float ret;
	inLoop = true;
	if (vectorMax == Float.MAX_VALUE)
		ret = getParentSignalParameters().getVectorMax();
	else {
		ret = vectorMax;
		track = element;
	}
	inLoop = false;
	return ret;	
}
/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 2:16:09 PM)
 * @return float
 */
public float getVectorMin() {
	float ret;
	inLoop = true;
	if (vectorMin == Float.MIN_VALUE)
		ret = getParentSignalParameters().getVectorMin();
	else {
		ret = vectorMin;
		track = element;
	}
	inLoop = false;
	return ret;
}
public double getVectorPhysicalPrecision() {
	return 1.0 / getVectorResolution();
}
public double getVectorResolution() {
	return (double)getVectorLength() / (getVectorMax() - getVectorMin());
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:30:08 PM)
 * @return java.lang.String
 */
public java.lang.String getVectorUnit() {
	String ret;
	inLoop = true;
	if (vectorUnit == null)
		ret = getParentSignalParameters().getVectorUnit();
	else {
		ret = vectorUnit;
		track = element;
	}
	inLoop = false;
	return ret;
}
public void inheritReferences(SignalParameters sp) {
	signalRate = sp.signalRate;
	signalResolutionBits = sp.signalResolutionBits;
	digitalMin = sp.digitalMin;
	digitalMax = sp.digitalMax;

	// Phusical parameters
	physicalMax = sp.physicalMax;
	physicalMin = sp.physicalMin;
	physicalUnit = sp.physicalUnit;

	// Vector parameters, used only in vector streams
	vectorLength = sp.vectorLength;
	vectorMax = sp.vectorMax;
	vectorMin = sp.vectorMin;
	vectorUnit = sp.vectorUnit;
	vectorFieldDescriptions = sp.vectorFieldDescriptions;
	
	info = sp.info;
	element = sp.element;
	parentSignalParameters = sp.parentSignalParameters;
}
public void inheritValues(SignalParameters sp) {
	signalRate = sp.getSignalRate();
	signalResolutionBits = sp.getSignalResolutionBits();
	digitalMin = sp.getDigitalMin();
	digitalMax = sp.getDigitalMax();

	// Phusical parameters
	physicalMin = sp.getPhysMin();
	physicalMax = sp.getPhysMax();
	physicalUnit = sp.getPhysicalUnit();

	// Vector parameters, used only in vector streams
	vectorLength = sp.getVectorLength();
	vectorMax = sp.getVectorMax();
	vectorMin = sp.getVectorMin();
	vectorUnit = sp.getVectorUnit();
	String t[] = sp.getVectorFieldDescriptions();;
	vectorFieldDescriptions = new String[t.length];
	System.arraycopy(t, 0, vectorFieldDescriptions, 0, t.length);

	info = sp.getInfo();
	element = sp.element;	
}
protected void normalizeFieldsDescriptions() {
	if (vectorLength < 0) {
		vectorFieldDescriptions = null;
		return;
	}

	String nf[] = new String[vectorLength];
	int i = 0;
	if (vectorFieldDescriptions != null) {
		for (; i < Math.min(vectorFieldDescriptions.length, vectorLength); i++){
			nf[i] = vectorFieldDescriptions[i];
		}
	}

	for (; i < vectorLength; i++){
		nf[i] = "";
	}

	vectorFieldDescriptions = nf;
}
/**
 * sendChangePropertyEvent method comment.
 */
public void sendChangePropertyEvent(java.lang.String fieldName, java.lang.Object oldValue) {}
/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 2:16:09 PM)
 * @param newDigitalMax int
 */
public void setDigitalMax(int newDigitalMax) {
	digitalMax = newDigitalMax;
}
/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 2:16:09 PM)
 * @param newDigitalMin int
 */
public void setDigitalMin(int newDigitalMin) {
	digitalMin = newDigitalMin;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:21:13 PM)
 * @param newDigitalRange int
 */
public void setDigitalRange(int newDigitalRange) {
	digitalMax = newDigitalRange/2 - 1;
	digitalMin = -newDigitalRange/2;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:21:13 PM)
 * @param newInfo java.lang.String
 */
public void setInfo(java.lang.String newInfo) {
	info = newInfo;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:36:38 PM)
 * @param newParentSignalParameters bioera.processing.SignalParameters
 */
void setParentSignalParameters(SignalParameters newParentSignalParameters) {
	if (newParentSignalParameters == null)
		System.out.println("???????? Parent SP set to null in " + element);
	parentSignalParameters = newParentSignalParameters;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:21:13 PM)
 * @param newPhysicalRange int
 */
public void setPhysicalRange(float newPhysicalRange) {
	physicalMax = newPhysicalRange / 2;
	physicalMin = - physicalMax;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:30:08 PM)
 * @param newPhysicalUnit java.lang.String
 */
public void setPhysicalUnit(java.lang.String newPhysicalUnit) {
	physicalUnit = newPhysicalUnit;
}
/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 2:16:09 PM)
 * @param newPhysicalMax float
 */
public void setPhysMax(float newPhysicalMax) {
	physicalMax = newPhysicalMax;
}
/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 2:16:09 PM)
 * @param newPhysicalMin float
 */
public void setPhysMin(float newPhysicalMin) {
	physicalMin = newPhysicalMin;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:21:13 PM)
 * @param newSignalRate int
 */
public void setSignalRate(float newSignalRate) {
	signalRate = newSignalRate;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:21:13 PM)
 * @param newSignalResolutionBits int
 */
public void setSignalResolutionBits(int newSignalResolutionBits) {
	signalResolutionBits = newSignalResolutionBits;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:36:38 PM)
 * @return bioera.processing.SignalParameters
 */
public void setVectorDescriptions(String desc[]) {
	vectorFieldDescriptions = desc;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:36:38 PM)
 * @return bioera.processing.SignalParameters
 */
void setVectorLength(int newLength) {
	if (newLength == vectorLength)
		return;

	vectorLength = newLength;

	normalizeFieldsDescriptions();
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:36:38 PM)
 * @return bioera.processing.SignalParameters
 */
public void setVectorMax(float v) {
	vectorMax = v;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:36:38 PM)
 * @return bioera.processing.SignalParameters
 */
public void setVectorMin(float v) {
	vectorMin = v;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:30:08 PM)
 * @return java.lang.String
 */
public void setVectorUnit(String v) {
	vectorUnit = v;
}
public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append("Element's signal params of '" + element + "':"+" (" + (element != null ? "" + (element.signalParameters.parentSignalParameters != null) : "undefined") + ")\n");
	sb.append("\tRate=" + getSignalRate()+" (" + track + ")\n");
	sb.append("\tDigMin=" + getDigitalMin()+" (" + track + ")\n");
	sb.append("\tDigMax=" + getDigitalMax()+" (" + track + ")\n");
	sb.append("\tDigRange=" + getDigitalRange()+" (" + track + ")\n");
	sb.append("\tBits=" + getSignalResolutionBits()+" (" + track + ")\n");
	sb.append("\tPhysMin=" + getPhysMin()+" (" + track + ")\n");
	sb.append("\tPhysMax=" + getPhysMax()+" (" + track + ")\n");
	sb.append("\tPhysRange=" + getPhysRange()+" (" + track + ")\n");
	sb.append("\tPhysUnit=" + getPhysicalUnit()+" (" + track + ")\n");
	sb.append("\tVectorLength=" + getVectorLength()+" (" + track + ")\n");
	sb.append("\tVectorMin=" + getVectorMin()+" (" + track + ")\n");
	sb.append("\tVectorMax=" + getVectorMax()+" (" + track + ")\n");
	sb.append("\tVectorUnit=" + getVectorUnit()+" (" + track + ")\n");
	sb.append("\tVectorFieldDescriptions=" + ProcessingTools.arrayToString(getVectorFieldDescriptions(), 3)+" (" + track + ")\n");
	sb.append("\tInfo=" + getInfo()+" (" + track + ")\n");
	sb.append("\tId=" + id+" " + (element != null ? ""+element.getId() : "-1") + (inLoop ? " inLoop" : " noLoop") + "\n");
	if (element != null && element.signalParameters != null && element.signalParameters.parentSignalParameters != null)
		sb.append("\tParent element=" + element.signalParameters.parentSignalParameters.element + "\n");
	return sb.toString();
}

/**
 *	This is the neutral level (if the signal is balanced)
 */
public int getDigitalZero() {
	return (getDigitalMax() + 1 + getDigitalMin()) / 2;
}

/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:21:13 PM)
 * @return int
 */
public int getDigitalAmplitude() {
	int ret;
	inLoop = true;
	if (digitalMax == Integer.MAX_VALUE || digitalMin == Integer.MIN_VALUE)
		ret = getParentSignalParameters().getDigitalAmplitude();
	else {
		ret = (digitalMax - digitalMin + 1) / 2;
		track = element;
	}
	inLoop = false;
	return ret;		
}

/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:21:13 PM)
 * @return int
 */
public int getDigitalPeakToPeak() {
	int ret;
	inLoop = true;
	if (digitalMax == Integer.MAX_VALUE || digitalMin == Integer.MIN_VALUE)
		ret = getParentSignalParameters().getDigitalPeakToPeak();
	else {
		ret = digitalMax - digitalMin + 1;
		track = element;
	}
	inLoop = false;
	return ret;		
}

/**
 *	This is the neutral level (if the signal is balanced)
 */
public double getPhysZero() {
	return (getPhysMax() + getPhysMin()) / 2;
}

/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:21:13 PM)
 * @param newDigitalRange int
 */
public boolean isDigiBalanced() {
	return Math.abs(getDigitalZero()) < 2;
}

/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:21:13 PM)
 * @param newDigitalRange int
 */
public boolean isPhysBalanced() {
	return Math.abs(getPhysZero()) < 1;
}

/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 6:21:13 PM)
 * @param newDigitalRange int
 */
public void setDigitalPositiveRange(int newDigitalRange) {
	digitalMax = newDigitalRange - 1;
	digitalMin = 0;
}
}
