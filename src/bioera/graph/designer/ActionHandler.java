/* ActionHandler.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.graph.designer;

import bioera.*;
import bioera.processing.*;
import java.io.*;
import java.util.*;

/**
 * Creation date: (6/11/2004 2:17:55 PM)
 * @author: Jarek Foltynski
 */
public class ActionHandler {
	public final static int LAST_DESIGN = 1;
	public final static int TOOL = 2;
	private static boolean hiddenMode = false;
	protected static boolean debug = bioera.Debugger.get("action.handler");
/**
 * ActionHandler constructor comment.
 */
public ActionHandler() {
	super();
}
/**
 * ActionHandler constructor comment.
 */
public static void loadDesign(File file, boolean isTool) throws Exception {
	if (Main.app.processor.isProcessing())
		Main.app.processor.stopProcessing();
	if (Main.app.processor.allReinitialized) {
		int result = javax.swing.JOptionPane.showConfirmDialog(
			Main.app.designFrame.frame,
			"Do you want to save current design?",
			"Confirm",
			javax.swing.JOptionPane.YES_NO_CANCEL_OPTION);
		if (result == javax.swing.JOptionPane.YES_OPTION) {
			Main.app.saveAsDesign(Main.app.designToSave);
		} else if (result == javax.swing.JOptionPane.NO_OPTION) {
			// ok, do nothing
		} else {
			return;
		}
	}

	Main.app.newIsTool = isTool;
	Main.app.newDesign = file;
}
/**
 * ActionHandler constructor comment.
 */
public static void perform(String command) throws Exception {
	if (Commands.PASTE.equals(command)) {
		//Clipboard.list.add(Item.highlighted.getAll());
		//System.out.println("past now");
		// Find the lowest x,y
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		for (int i = 0; i < Clipboard.list.size(); i++){
			//System.out.println("class=" + Clipboard.list.get(i));
			Item it = (Item) Clipboard.list.get(i);
			if (it.getX1() < minX)
				minX = it.getX1();
			if (it.getY1() < minY)
				minY = it.getY1();
		}

		// Create new elements
		List list = new ArrayList();
		for (int i = 0; i < Clipboard.list.size(); i++){
			//System.out.println("class=" + Clipboard.list.get(i));
			Item it = (Item) Clipboard.list.get(i);
			if (it instanceof BoxItem) {
				//System.out.println("adding element " + it);
				Element e = ((BoxItem)it).element;
				BoxItem box = DialogNewElement.createNewElement(e.getClass().getName(), it.getX1() - minX + 15, it.getY1() - minY + 15);
				// Copy all properties
				ProcessingTools.setElementProperties(box.element, ProcessingTools.getElementProperties(e));
				list.add(box);
			} else {
				// Connections are not copied at this moment
			}
		}

		// Highlight all added elements
		for (int i = 0; i < list.size(); i++){
			Item.highlighted.add((Item) list.get(i));
		}

		//System.out.println("minx=" + minX);
		//System.out.println("miny=" + minY);

	} else if (Commands.COPY.equals(command)) {
		Clipboard.list.clear();
		//System.out.println("high=" + Item.highlighted.getAll());
		Clipboard.list.addAll(Item.highlighted.getAll());
		//System.out.println("high=" + Clipboard.list);
		Item.highlighted.clear();
		Main.app.designFrame.repaint();
	} else if (Commands.MOVE.equals(command)) {
		boolean any = false;
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		for (int i = 0; i < Item.highlighted.size(); i++){
			//System.out.println("class=" + Clipboard.list.get(i));
			Item it = (Item) Item.highlighted.get(i);
			if (it instanceof BoxItem) {
				if (it.getX1() < minX)
					minX = it.getX1();
				if (it.getY1() < minY)
					minY = it.getY1();
				any = true;
			}
		}

		if (!any)
			return;

		int diffX = Main.app.designFrame.eventHandler.pressedX - minX;
		int diffY = Main.app.designFrame.eventHandler.pressedY - minY;

		for (int i = 0; i < Item.highlighted.getAll().size(); i++){
			//System.out.println("class=" + Clipboard.list.get(i));
			Item it = Item.highlighted.get(i);
			it.setX1(it.getX1() + diffX);
			it.setY1(it.getY1() + diffY);
			//System.out.println("itx=" + it.getX1());
			//System.out.println("diff=" + diffX);
		}

		Main.app.designFrame.repaint();
	} else if (Commands.DELETE.equals(command)) {
		if (Item.highlighted.size() > 0) {
			//System.out.println("Deleting element");
			while (Item.highlighted.size() > 0){
				Main.app.designFrame.panel.deleteElement(Item.highlighted.get(0));
				Item.highlighted.remove(0);
			}

			Main.app.designFrame.frame.requestFocus();
			Main.app.processor.reInitializeAll();
		} else {
			//System.out.println("nothing highlighted");
		}
	} else if (Commands.INFO_ABOUT_BIOERA.equals(command)) {
			try {
				DialogHelpAbout dialog = new DialogHelpAbout(Main.app.designFrame.frame);
				dialog.locateOnComponent(Main.app.designFrame.frame);
				((DialogHelpAbout)dialog).show();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

/*	} else if (Commands.INFO_ABOUT_DESIGN.equals(command)) {
			try {
				bioera.properties.PropertiesDialog dialog = new bioera.properties.PropertiesDialog(Main.app.designFrame.frame, Main.app.designInfo, 1);
				dialog.setTitle("About design");
				dialog.locateOnComponent(Main.app.designFrame.frame);
				dialog.show();
			} catch (Exception ex) {
				ex.printStackTrace();
			}*/
/*        } else if (Commands.INFO_ABOUT_PRESAGE.equals(command)) {
                        try {
                            DialogHelpAbout dialog = new DialogHelpAbout(Main.app.runtimeFrame.frame);
                            dialog.locateOnComponent(Main.app.runtimeFrame.frame);
                            ((DialogHelpAbout)dialog).show();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
*/
/*		
	} else if (Commands.INFO_ABOUT_USER.equals(command)) {
			try {
				bioera.properties.PropertiesDialog dialog = new bioera.properties.PropertiesDialog(Main.app.designFrame.frame, Main.app.userInfo, 1);
				dialog.setTitle("About user");
				dialog.locateOnComponent(Main.app.designFrame.frame);
				dialog.reinitializeProcessor = false;
				dialog.setModal(true);
				dialog.show();
				if (dialog.isOk()) {
					//System.out.println("saving");
					//bioera.config.UserInfo.saveUserInfo(Main.app);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
*/
	} else if (Commands.PRINT_DETAILS.equals(command)) {
		for (int i = 0; i < Item.highlighted.size(); i++){
			if (Item.highlighted.get(i) instanceof BoxItem) {
				BoxItem it = (BoxItem) Item.highlighted.get(i);
				System.out.println(it.element.printAdvancedDetails());
			}
		}
	} else if (Commands.PRINT_PROPERTIES.equals(command)) {
		for (int i = 0; i < Item.highlighted.size(); i++){
			if (Item.highlighted.get(i) instanceof BoxItem) {
				BoxItem it = (BoxItem) Item.highlighted.get(i);
				System.out.println("Element properties:\n" + it.element.printAllProperties());
			}
		}
	} else if (Commands.PRINT_SIGNAL_PARAMS.equals(command)) {
		for (int i = 0; i < Item.highlighted.size(); i++){
			if (Item.highlighted.get(i) instanceof BoxItem) {
				BoxItem it = (BoxItem) Item.highlighted.get(i);
				ProcessingTools.printSignalParameters(it.element);
			}
		}
	} else if (Commands.REINIT.equals(command)) {
		for (int i = 0; i < Item.highlighted.size(); i++){
			if (Item.highlighted.get(i) instanceof BoxItem) {
				BoxItem it = (BoxItem) Item.highlighted.get(i);
				//System.out.println("Element properties:\n" + it.element.printAllProperties());
				boolean runn = Main.app.processor.isProcessing();
				if (runn)
					it.element.stop();
				Main.app.processor.reinitElement(it.element);
				if (runn)
					it.element.start();
			}
		}
	} else if (Commands.DESCRIPTION.equals(command)) {
		if (Main.app.designFrame.eventHandler.activeBox != null) {
			GenericDialog dialog = null;
			try {
				// Check if exists description in file for this element
				String s = ProcessingTools.getDescriptionFromFile(Main.app.designFrame.eventHandler.activeBox.element);
				if (s == null)
					s = Main.app.designFrame.eventHandler.activeBox.element.getElementDescription();
				dialog = new MessageDialog(Main.app.designFrame.frame);
				dialog.locateOnComponent(Main.app.designFrame.eventHandler.pressedX, Main.app.designFrame.eventHandler.pressedY, Main.app.designFrame.frame);
				((MessageDialog)dialog).show(s);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	} else if (Commands.NEW.equals(command)) {
		GenericDialog dialog = null;
		try {
			dialog = new DialogNewElement(Main.app.designFrame.frame);
			dialog.pack();
			dialog.locateOnComponent(Main.app.designFrame.eventHandler.pressedX, Main.app.designFrame.eventHandler.pressedY, Main.app.designFrame.frame);
                        dialog.setVisible(true);
			//dialog.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	} else if (Commands.MORPH_INTO.equals(command)) {
		GenericDialog dialog = null;
		try {
			dialog = new DialogMorphInto(Main.app.designFrame.frame);
			dialog.pack();
			dialog.locateOnComponent(Main.app.designFrame.eventHandler.pressedX, Main.app.designFrame.eventHandler.pressedY, Main.app.designFrame.frame);
			dialog.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	} else if (Commands.PROPERTIES.equals(command)) {
		if (Main.app.designFrame.eventHandler.activeBox != null) {
			GenericDialog dialog = null;
			try {
				dialog = Main.app.designFrame.eventHandler.activeBox.edit();
				dialog.pack();
				dialog.locateOnComponent(Main.app.designFrame.eventHandler.pressedX, Main.app.designFrame.eventHandler.pressedY, Main.app.designFrame.frame);
				dialog.show();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			//System.out.println("no object selected");
		}
	} else if (Commands.START.equals(command)) {
		if (Main.app != null) {
			Main.app.processor.startProcessing();
		}
	} else if (Commands.STOP.equals(command)) {
		if (Main.app != null) {
			Main.app.processor.stopProcessing();
		}
	} else if (Commands.SIGNAL_SETTINGS.equals(command)) {
		GenericDialog dialog = null;
		try {
			dialog = new bioera.properties.PropertiesDialog(Main.app.designFrame.frame, SignalParameters.getDefaultSettings());
			dialog.pack();
			dialog.locateOnComponent(Main.app.designFrame.eventHandler.pressedX, Main.app.designFrame.eventHandler.pressedY, Main.app.designFrame.frame);
			dialog.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	} else if (Commands.DESIGN_SETTINGS.equals(command)) {
		GenericDialog dialog = null;
		try {
			dialog = new bioera.properties.PropertiesDialog(Main.app.designFrame.frame, new DesignSettings());
			dialog.pack();
			dialog.locateOnComponent(Main.app.designFrame.eventHandler.pressedX, Main.app.designFrame.eventHandler.pressedY, Main.app.designFrame.frame);
			dialog.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	} else if (Commands.SYSTEM_SETTINGS.equals(command)) {
		GenericDialog dialog = null;
		try {
			dialog = new bioera.properties.PropertiesDialog(Main.app.designFrame.frame, new ConfigurableSystemSettings());
			dialog.pack();
			dialog.locateOnComponent(Main.app.designFrame.eventHandler.pressedX, Main.app.designFrame.eventHandler.pressedY, Main.app.designFrame.frame);
			dialog.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	} else if (Commands.EXIT.equals(command)) {
		//Main.app.saveAndExit(); mmods
                Main.app.systemExit();
		return;
	} else if (Commands.EDIT_MODE.equals(command)
				|| Commands.VIEW_MODE.equals(command)
				|| Commands.VIEW.equals(command)
				|| Commands.EDIT.equals(command)
				) {
		Main.app.runtimeFrame.framedCharts = !Main.app.runtimeFrame.framedCharts;
		try {
			Main.app.runtimeFrame.reload();
			if (Main.app.runtimeFrame.framedCharts) {
				Main.app.designFrame.getMenuItem(Commands.EDIT_MODE, Commands.VIEW_MODE).setText(Commands.VIEW_MODE);
				Main.app.designFrame.setToolBarButton(Commands.EDIT, false);
				Main.app.designFrame.setToolBarButton(Commands.VIEW, true);
			} else {
				Main.app.designFrame.getMenuItem(Commands.EDIT_MODE, Commands.VIEW_MODE).setText(Commands.EDIT_MODE);
				Main.app.designFrame.setToolBarButton(Commands.EDIT, true);
				Main.app.designFrame.setToolBarButton(Commands.VIEW, false);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return;
	} else if (Commands.LOAD.equals(command)) {
		javax.swing.JFileChooser d = new javax.swing.JFileChooser();
		d.setFileSelectionMode(d.FILES_ONLY);
		d.setCurrentDirectory(Main.app.getDesignFolder());
		d.setDialogTitle("Load design");
		d.setFileFilter(new ExtFileFilter("BioEra designs", "bpd"));
		int ret = d.showOpenDialog(Main.app.designFrame.frame);
		if (ret == d.APPROVE_OPTION) {
			java.io.File f = d.getSelectedFile();
			if (f != null && f.exists() && f.isFile()) {
				try {
					loadDesign(new File(f.getAbsolutePath()), false);
				} catch (Exception ex) {
					System.out.println("Couldn't load design from file '" + f + "' : " + ex);
					ex.printStackTrace();
				}
			}
		}
	} else if (Commands.SAVE_AS.equals(command)) {
		javax.swing.JFileChooser d = new javax.swing.JFileChooser();

		d.setFileSelectionMode(d.FILES_ONLY);
		d.setCurrentDirectory(Main.app.getDesignFolder());
		d.setDialogTitle("Save design");
		d.setFileFilter(new ExtFileFilter("BioEra designs", "bpd"));
		int ret = d.showSaveDialog(Main.app.designFrame.frame);
		if (ret == d.APPROVE_OPTION) {
			java.io.File f = d.getSelectedFile();
			System.out.println("saving " + f);
			if (f != null) {
				if (!f.getName().toLowerCase().endsWith(".bpd"))
					f = new java.io.File(f.getPath() + ".bpd");
				try {
					System.out.println("saving1 " + f);
					Main.app.saveAsDesign(f);
				} catch (Exception ex) {
					System.out.println("Can't save design in file '" + f + "' : " + ex);
				}
			}
		}
	} else if (Commands.ERROR_DESCRIPTION.equals(command)) {
		if (Main.app.designFrame.eventHandler.activeBox != null) {
			MessageDialog dialog = null;
			try {
				dialog = new MessageDialog(Main.app.designFrame.frame);
				dialog.locateOnComponent(Main.app.designFrame.eventHandler.pressedX, Main.app.designFrame.eventHandler.pressedY, Main.app.designFrame.frame);
				dialog.show(Main.app.designFrame.eventHandler.activeBox.element.getDesignErrorMessage());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	} else if (Commands.DIAGNOSTIC_DEBUGGER_FIELDS.equals(command)) {
		GenericDialog dialog = null;
		try {
			dialog = new bioera.properties.PropertiesDialog(Main.app.designFrame.eventHandler.designFrame.frame, new bioera.debugger.DebuggerFields());
			dialog.pack();
			dialog.locateOnComponent(Main.app.designFrame.eventHandler.pressedX, Main.app.designFrame.eventHandler.pressedY, Main.app.designFrame.frame);
			dialog.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
/**
 * ActionHandler constructor comment.
 */
public static void perform(String command, int type) throws Exception {
	switch (type) {
		case 0:
			perform(command);
			break;
		case TOOL:
			java.io.File f = Main.app.designFrame.toolDesigns.get(command);
			if (f != null) {
				//System.out.println("TOOL: " + command);
				if (!f.exists())
					f = new java.io.File(f.getAbsolutePath() + ".bpd");
				if (f.exists() && f.isFile()) {
					try {
						loadDesign(f, true);
						//new Thread(new ThreadNewDesign(new java.io.File(f.getAbsolutePath()), true)).start();
						//MainApp.loadNewDesign(new java.io.File(d.getDirectory(), filename));
					} catch (Exception ex) {
						System.out.println("Couldn't load tool design from file '" + f + "' : " + ex);
						ex.printStackTrace();
					}
				}
			}
			break;
		case LAST_DESIGN:
			//System.out.println("\"last design\" MenuItem clicked");
			f = new File(Main.app.getDesignFolder(), command);
			if (!f.exists())
				f = new File(command);
			if (f.exists()) {
				try {
					loadDesign(f, false);
					//new Thread(new ThreadNewDesign(f, false)).start();
					//MainApp.loadNewDesign(new java.io.File(d.getDirectory(), filename));
				} catch (Exception ex) {
					System.out.println("Couldn't load design from file '" + f + "' : " + ex);
					ex.printStackTrace();
				}
			} else {
				System.out.println("Design file '" + f + "' not found");
			}
			break;
		default:
			System.out.println("Unknown action command=" + command + " type=" + type);
			return;
	}
}
/**
 * ActionHandler constructor comment.
 */
public static boolean performHiddenKey(char key) throws Exception {
	System.out.println("hidden key is " + key);

	try {
		switch (key) {
			case 'e':
				// Show all processor elements
				ElementSet es = Main.processor.getElementSet();
				for (int i = 0; i < es.elements.length; i++){
					System.out.println("pe: " + es.elements[i]);
				}
				break;
			case 'd':
				// Show all designer items
				BoxSet set = Main.designFrame.panel.boxes;
				for (int i = 0; i < set.size(); i++){
					System.out.println("de: " + set.get(i));
				}
				break;
			default:
				return false;
		}
	} catch (Exception exc) {
		System.out.println("Hidden key char (code=" + key + ") error: " + exc);
		if (debug)
			exc.printStackTrace();
	}

	return true;
}
/**
 * ActionHandler constructor comment.
 */
public static boolean performKey(char key) throws Exception {
	//System.out.println("key is " + e.getKeyCode());
	if (hiddenMode) {
		hiddenMode = false;
		if (performHiddenKey(key))
			return true;
	}

	try {
		switch (key) {
			case 'a':
				ActionHandler.perform(Commands.PRINT_DETAILS);
				break;
			case 'd':
				ActionHandler.perform(Commands.DESCRIPTION);
				break;
			case 'e':
				ActionHandler.perform(Commands.ERROR_DESCRIPTION);
				break;
			case 'm':
				System.out.println("Memory status:");
				System.out.println("Total memory: " + Runtime.getRuntime().totalMemory());
				System.out.println("Free memory : " + Runtime.getRuntime().freeMemory());
				break;
			case 'p':
				ActionHandler.perform(Commands.PRINT_PROPERTIES);
				break;
			case 'r':
				ActionHandler.perform(Commands.REINIT);
				break;
			case 's':
				ActionHandler.perform(Commands.PRINT_SIGNAL_PARAMS);
				break;
			case ' ':
				ActionHandler.perform(Commands.PROPERTIES);
				break;
			case '\\':
				hiddenMode = true;
				return true;
			default:
				return false;
		}
	} catch (Exception exc) {
		System.out.println("Key char (code=" + key + ") error: " + exc);
		if (debug)
			exc.printStackTrace();
	}

	return true;
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
