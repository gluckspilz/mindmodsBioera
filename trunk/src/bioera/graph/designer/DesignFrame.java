/* DesignFrame.java v 1.0.9   11/6/04 7:15 PM
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


import java.lang.reflect.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import java.io.*;
import bioera.*;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.processing.SignalParameters;
import bioera.config.*;
import de.javasoft.plaf.synthetica.SyntheticaLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaStandardLookAndFeel;



/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class DesignFrame {
	public Main mainApp;
	public JFrame frame;

	public GraphPanel panel;
	public DesignEventHandler eventHandler;
	public JMenuBar menuBar;
	public JToolBar toolBar;

	public int zoom = 100;

	private Vector charts = new Vector();

	ToolDesigns toolDesigns = new ToolDesigns();

	// Each row contains: name and class
	public static Object processingElements[][];

	private static boolean debugLoading = bioera.Debugger.get("designer.loadimpl");

	private static boolean showDiagnosticMenu = bioera.Debugger.get("designer.diagnostic");
	private static boolean loadTools = bioera.Debugger.get("loadtools");



/**
 * Display constructor comment.
 */
public DesignFrame(Main m) {
	frame = new JFrame("Design editor");
        try {
            //SyntheticaLookAndFeel.setWindowsDecorated(true);
            //UIManager.setLookAndFeel(new SyntheticaStandardLookAndFeel());

        } catch (Exception e) {
            e.printStackTrace();
    }
//	app = this;
	mainApp = m;
}
/**
 * Display constructor comment.
 */
public void addPopup(JPopupMenu m) {
	frame.getContentPane().add(m);
}
/**
 * Display constructor comment.
 */
public void dispose() {
	eventHandler.disposed = true;
	frame.dispose();
	panel.dispose();
	charts.removeAllElements();
	toolDesigns.clear();
}
/**
 * Display constructor comment.
 */
public static final JMenuItem findMenu(String name, JMenu inside) throws Exception {
	//System.out.println("searching in " + inside.getText() + "  " + inside.getMenuComponentCount());
	if (inside == null || name == null)
		return null;
	for (int i = 0; i < inside.getMenuComponentCount(); i++){
		Component c = inside.getMenuComponent(i);
		if (c != null && c instanceof JMenuItem) {
			JMenuItem m = (JMenuItem) c;
			//System.out.println("jmenu=" + m.getText());
			if (m.getText().equals(name)) {
				return m;
			}
		}
	}

	return null;
}
/**
 * Display constructor comment.
 */
public static final JMenu findMenu(String name, JMenuBar bar) throws Exception {
	//System.out.println("searching in bar " + bar.getName());
	if (bar == null || name == null)
		return null;
	for (int i = 0; i < bar.getMenuCount(); i++){
		JMenu m = bar.getMenu(i);
		//System.out.println("bar jmenu="  + m.getText());
		if (m != null && m.getText() != null && m.getText().equals(name)) {
			return m;
		}
	}

	return null;
}
/**
 * Display constructor comment.
 */
public JMenuItem getMenuItem(String s) {
	return getMenuItem(s, null);
}
/**
 * Display constructor comment.
 */
public JMenuItem getMenuItem(String s, String s1) {
	//System.out.println("searching for " + s + "  " + s1);
	int n = menuBar.getMenuCount(), k;
	for (int i = 0; i < n; i++){
		JMenu m = menuBar.getMenu(i);
		//System.out.println("M=" + m.getText());
		k = m.getItemCount();
		for (int j = 0; j < k; j++){
			JMenuItem mi = m.getItem(j);
			if (mi == null)
				continue;
			//System.out.println("Mi=" + mi.getText());
			if (s.equals(mi.getText())) {
				return mi;
			} else if (s1 != null && s1.equals(mi.getText())) {
				return mi;
			}
		}
	}

	return null;
}
/**
 * Display constructor comment.
 */
public JButton getToolbarItem(String s, String s1) {
	//System.out.println("searching for " + s + "  " + s1);
	int n = toolBar.getComponentCount(), k;
	for (int i = 0; i < n; i++){
		Object o = toolBar.getComponent(i);
		//System.out.println("M=" + m.getText());
		if (o == null || !(o instanceof JButton))
			continue;
		JButton b = (JButton) o;
		//System.out.println("Mi=" + mi.getText());
		if (s.equals(b.getText())) {
			return b;
		} else if (s1 != null && s1.equals(b.getText())) {
			return b;
		}
	}

	return null;
}
/**
 * Display constructor comment.
 */
public void init(XmlConfigSection config) throws Exception {
	loadImplementations();
	loadSwingGraphics();

	XmlConfigSection settings = config.getSection("Design_frame");
	frame.setBounds(settings.getInteger("x", 100),
			  settings.getInteger("y", 100),
			  settings.getInteger("width", 600),
			  settings.getInteger("height", 400));
}
/**
 * Display constructor comment.
 */
private void loadConfigurationElements(String section, ArrayList names, ArrayList classes) throws Exception {
	// First load from configuration file
	File cf = mainApp.getConfigurationFile();
	if (cf.exists()) {
		XmlConfigSection config = new XmlConfigSection(cf);
		if (config.containsSection(section)) {
			XmlConfigSection elements = config.getSection(section);
			if (elements.containsSection("element")) {
				XmlConfigSection element = elements.getSection("element");
				while (element != null) {
					String className = element.getString("class");
					if (!names.contains(element.getString("name")) && !classes.contains(className)) {
						try {
							Class c = Tools.createClass(className);
							names.add(element.getString("name"));
							classes.add(c.getName());
							if (debugLoading)
								System.out.println("Loading config impl: " + className);
						} catch (Exception e) {
							if (debugLoading)
								System.out.println("Not found impl: " + className);
						}
					}
					element = element.getNextSectionByName();
				}
			}
		}
	}
}
/**
 * Loads default bioera implementations
 */
private void loadFolderImplementations(ArrayList names, ArrayList classes, String implFolder, String absPath) throws Exception {
	File folder = new File(absPath, implFolder);
	//System.out.println("folder: " + file + " list=" + file.listFiles());
	//System.out.println("file: " + folder);
	if (!folder.exists() || !folder.isDirectory()) {
		return;
	}

	String list[] = folder.list();
	//System.out.println("list=" + list.length);
	if (list != null) {
		for (int i = 0; i < list.length; i++){
			String n = list[i];
			if (n.endsWith(".class")) {
				// Strip extension
				String name = n.substring(0, n.length() - ".class".length());
				String cName = implFolder.replace('/', '.') + name;
				if (classes.contains(cName)) {
					// Do not load again the same class
					//System.out.println("Found " + name);
					if (debugLoading)
						System.out.println("Already loaded class: " + cName);
					continue;
				}

				if (debugLoading)
					System.out.println("loading default impl: " + cName);

				// Create name with package
				classes.add(cName);

				// Remove .class from name
				names.add(name);
			}
		}
	}
}
/**
 * Display constructor comment.
 */
private void loadImplementations() throws Exception {
	ArrayList names = new ArrayList();
	ArrayList classes = new ArrayList();

	// Load from config file
	loadConfigurationElements("Elements", names, classes);

	// Load from default impl folder
	File folder = mainApp.getImplFolder();
	if (folder.exists())
		loadRecursiveFolderImplementations(folder, names, classes);

	loadFolderImplementations(names, classes, "bioera/processing/impl/", Main.app.getRootFolder().getAbsolutePath());
	loadFolderImplementations(names, classes, "bioera/device/impl/", Main.app.getRootFolder().getAbsolutePath());
	loadFolderImplementations(names, classes, "bioera/processing/impl/", "/projects/java/classes/");
	loadFolderImplementations(names, classes, "bioera/device/impl/", "/projects/java/classes/");
	loadJarImplementations(names, classes, "bioera/processing/impl/");
	loadJarImplementations(names, classes, "bioera/device/impl/");
	if (loadTools) {
		loadFolderImplementations(names, classes, "bioera/tools/impl/", "/projects/java/classes/");
		loadJarImplementations(names, classes, "bioera/tools/impl/");
	}

	processingElements = new Object[names.size()][];
	for (int i = 0; i < names.size(); i++){
		processingElements[i] = new Object[2];
		processingElements[i][0] = names.get(i);
		processingElements[i][1] = classes.get(i);
		//System.out.println("class '" + classes.elementAt(i) + "'");
	}

	// Sort the records by name
	for (int i = 1; i < names.size(); i++){
		String s1 = (String) processingElements[i - 1][0];
		String s2 = (String) processingElements[i][0];
		if (s1.compareToIgnoreCase(s2) > 0) {
			Object r[] = processingElements[i];
			processingElements[i] = processingElements[i - 1];
			processingElements[i - 1] = r;
			i = 0;
		}
	}

	if (debugLoading) {
		for (int i = 0; i < names.size(); i++){
			System.out.println("name='" + names.get(i) + "'  class='" + classes.get(i) + "'");
		}
	}
}
/**
 * Loads default bioera implementations
 */
private void loadJarImplementations(ArrayList names, ArrayList classes, String folder) throws Exception {
	File f = new File(Main.app.getRootFolder(), "bioera.jar");
	if (!f.exists()) {
		f = new File(Main.app.getRootFolder(), "ext/bioera.jar");
		if (!f.exists()) {
			return;
		}
	}

	java.util.jar.JarFile jf = new java.util.jar.JarFile(f);
	Enumeration en = jf.entries();
	while (en.hasMoreElements()) {
		ZipEntry entry = (ZipEntry) en.nextElement();
		//System.out.println("url=" + url);
		if (entry == null || (!entry.getName().startsWith(folder)))
			continue;

		String n = entry.getName();
		//System.out.println("Entry=" + n);
		if (n.endsWith(".class")) {
			n = n.replace('/', '.');
			n = n.substring(0, n.length() - ".class".length());
			if (classes.contains(n)) {
				if (debugLoading)
					System.out.println("Already loaded impl class: " + n);
				// Do not load again the same class
				continue;
			}

			// Create name with package
			classes.add(n);

			if (debugLoading)
				System.out.println("Loading jar impl: " + n);

			// Strip package
			int g = n.lastIndexOf('.');
			if (g != -1)
				n = n.substring(g + 1);

			// Remove .class from name
			names.add(n);
		}
	}
}
/**
 * Display constructor comment.
 */
private void loadMenus() throws Exception {

	menuBar = new JMenuBar();
	menuBar.setBackground(frame.getBackground());
	menuBar.setForeground(frame.getForeground());
	menuBar.setName("MainMenuBar");
	frame.setJMenuBar(menuBar);

	// -------------- SYSTEM ----------------

	JMenu m = new JMenu(Commands.SYSTEM);
	menuBar.add(m);

	JMenuItem item = new JMenuItem(Commands.LOAD);
	item.addActionListener(eventHandler);
	m.add(item);

	item = new JMenuItem(Commands.SAVE_AS);
	item.addActionListener(eventHandler);
	m.add(item);

	m.addSeparator();

	item = new JMenuItem(Commands.DESIGN_SETTINGS);
	item.addActionListener(eventHandler);
	m.add(item);

	m.addSeparator();

	item = new JMenuItem(Commands.SYSTEM_SETTINGS);
	item.addActionListener(eventHandler);
	m.add(item);

	m.addSeparator();

	JMenu r = new JMenu(Commands.RECENT_DESIGNS);
	r.addActionListener(eventHandler);
	m.add(r);

	updateRecentDesignMenu();

	m.addSeparator();

	item = new JMenuItem(Commands.EXIT);
	item.addActionListener(eventHandler);
	m.add(item);


	// -------------- RUNTIME ----------------

	m = new JMenu(Commands.RUNTIME);
	menuBar.add(m);

	item = new JMenuItem(Commands.START);
	item.addActionListener(eventHandler);
	m.add(item);

	item = new JMenuItem(Commands.EDIT_MODE);
	item.addActionListener(eventHandler);
	m.add(item);

	//item = new JMenuItem(Commands.SIGNAL_SETTINGS);
	//item.addActionListener(eventHandler);
	//m.add(item);


	// ---------------- TOOLS -----------------------

	m = new JMenu(Commands.TOOLS);
	menuBar.add(m);

	loadMenuTools(m, Main.app.getToolsFolder());

	// -----------------  INFO ------------------
	m = new JMenu(Commands.INFO);
	menuBar.add(m);

	item = new JMenuItem(Commands.INFO_ABOUT_BIOERA);
	item.addActionListener(eventHandler);
	m.add(item);

	item = new JMenuItem(Commands.INFO_ABOUT_DESIGN);
	item.addActionListener(eventHandler);
	m.add(item);

	item = new JMenuItem(Commands.INFO_ABOUT_USER);
	item.addActionListener(eventHandler);
	m.add(item);

	// -----------------  DIAGNOSTIC ------------------

	if (showDiagnosticMenu) {
		m = new JMenu(Commands.DIAGNOSTIC);
		menuBar.add(m);

		item = new JMenuItem(Commands.DIAGNOSTIC_DEBUGGER_FIELDS);
		item.addActionListener(eventHandler);
		m.add(item);
	}
}
/**
 * Display constructor comment.
 */
private void loadMenuTools(JMenu menu, File folder) throws Exception {
	String list[] = folder.list();
	if (list == null || list.length == 0) {
		menu.add(new JMenuItem(""));
	} else {
		for (int i = 0; i < list.length; i++){
			String name = list[i];
			File f = new File(folder, name);
			if (f.isFile()) {
				if (name.toLowerCase().endsWith(".bpd")) {
					name = name.substring(0, name.length() - ".bpd".length());
				} else
					continue;
				if (toolDesigns.get(name) == null) {
					toolDesigns.add(name, f);
					CommandMenuItem item = new CommandMenuItem(name);
					item.actionType = ActionHandler.TOOL;
					item.addActionListener(eventHandler);
					menu.add(item);
				} else
					System.out.println("Tool design '" + name + "' already exists");
			} else if (f.isDirectory() && f.getName().length() > 0 && Character.isUpperCase(f.getName().charAt(0))) {
				JMenu m = new JMenu(name);
				menu.add(m);
				loadMenuTools(m, f);
			}
		}
	}
}
/**
 * Display constructor comment.
 */
private void loadRecursiveFolderImplementations(File folder, ArrayList names, ArrayList classes) throws Exception {
	String list[] = Tools.listRecursive(folder);
	if (list != null) {
		for (int i = 0; i < list.length; i++){
			String n = list[i];
			if (n.endsWith(".class")) {
				n = n.replace(File.pathSeparatorChar, '.');
				n = n.substring(0, n.length() - ".class".length());
				if (classes.contains(n)) {
					if (debugLoading)
						System.out.println("Already loaded impl class: " + n);
					// Do not load again the same class
					continue;
				}

				// Create name with package
				classes.add(n);

				if (debugLoading)
					System.out.println("Loading impl: " + n);

				// Strip package
				int g = n.lastIndexOf('.');
				if (g != -1)
					n = n.substring(g + 1);

				// Remove .class from name
				names.add(n);
			}
		}
	}
}
/**
 * Display constructor comment.
 */
private void loadSwingGraphics() throws Exception {
	JFrame frame = (JFrame) this.frame;
	frame.setBackground(ConfigurableSystemSettings.backgroundColor.getAWTColor());

	frame.addWindowListener(new CloseSaveWindowsListener());

	panel = new GraphPanel(frame);
	eventHandler = new DesignEventHandler(this, panel);
	panel.addMouseListener(eventHandler);
	panel.addMouseMotionListener(eventHandler);

	frame.getContentPane().removeAll();
	frame.getContentPane().setLayout(new java.awt.BorderLayout());
	frame.getContentPane().add(panel, java.awt.BorderLayout.CENTER);

	panel.addKeyListener(eventHandler);
	frame.addKeyListener(eventHandler);

	loadMenus();

	frame.getContentPane().add(loadToolBar(), java.awt.BorderLayout.NORTH);

	panel.resetElements();
}
/**
 * Display constructor comment.
 */
private JToolBar loadToolBar() throws Exception {
	final String toolBarNames[][] = {
		{Commands.START, "Start processing", "on"},
		{Commands.STOP, "Stop processing", "off"},
		{"",""},
		{Commands.VIEW, "View mode", "off"},
		{Commands.EDIT, "Edit mode", "on"},
		{"",""},
	};

	toolBar = new JToolBar();
	toolBar.setBackground(frame.getBackground());
	toolBar.setForeground(frame.getForeground());
	toolBar.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.black));
	toolBar.setBorderPainted(true);
	for (int i = 0; i < toolBarNames.length; i++){
		if (toolBarNames[i][0].length() == 0) {
			toolBar.addSeparator();
		} else {
			JButton button = new JButton();
			button.setName(toolBarNames[i][0]);
			button.setText(toolBarNames[i][0]);
			File f = new File(Main.app.getImagesFolder(), toolBarNames[i][0]);
			button.setIcon(new ImageIcon(f.getAbsolutePath() + ".gif"));
			button.setDisabledIcon(new ImageIcon(f.getAbsolutePath() + "_disabled.gif"));
			button.setMargin(new Insets(0, 0, 0, 0));
			button.setToolTipText(toolBarNames[i][1]);
			if ("off".equals(toolBarNames[i][2]))
				button.setEnabled(false);
			button.addActionListener(eventHandler);
			button.addMouseListener(eventHandler);
			toolBar.add(button);
		}
	}

	return toolBar;
}
/**
 * Display constructor comment.
 */
public void repaint() {
	if (frame != null)
		frame.repaint();
	if (panel != null)
		panel.repaint();
}
public void rezoom() {
	panel.boxes.rezoom();
	zoom = ConfigurableSystemSettings.DESIGNER_ZOOM;

	//System.out.println("======== rezooming frame " + panel.boxes.size());
}
/**
 * Display constructor comment.
 */
public void save(XmlCreator config) throws Exception {
	//XmlCreator elements = config.addSection("Elements");
	//panel.boxes.save(elements);
	//panel.connections.save(elements);
	XmlCreator settings = config.addSection("Design_frame");
	settings.addTextValue("x", "" + frame.getX());
	settings.addTextValue("y", "" + frame.getY());
	settings.addTextValue("width", "" + frame.getWidth());
	settings.addTextValue("height", "" + frame.getHeight());
}
public static void setDebugLoading(boolean newValue) {
	debugLoading = newValue;
}
/**
 * Display constructor comment.
 */
public void setDesignTitle(String filename) throws Exception {
	frame.setTitle("BioEra design: " + filename);
}
public static void setShowDiagnosticMenu(boolean newValue) {
	showDiagnosticMenu = newValue;
}
/**
 * Display constructor comment.
 */
public void setToolBarButton(String name, boolean state) throws Exception {
	int n = toolBar.getComponentCount();
	for (int i = 0; i < n; i++){
		Component c = toolBar.getComponent(i);
		//System.out.println("comp=" + c.getName());
		if (c instanceof JButton && c.getName().equals(name)) {
			JButton b = (JButton) c;
			b.setEnabled(state);
			//System.out.println("disabled " + name + " " + state);
			break;
		}
	}
}
/**
 * Display constructor comment.
 */
//public void show() throws Exception {
public void setVisible() throws Exception {
        frame.setVisible(true);
	//frame.show();
	panel.resetElements();

}

/**
 * Display constructor comment.
 */
public void updateRecentDesignMenu() throws Exception {
	JMenu system = findMenu(Commands.SYSTEM, menuBar);
	if (system == null) {
		System.out.println("System menu not found");
		return;
	}

	JMenu designs = (JMenu) findMenu(Commands.RECENT_DESIGNS, system);
	if (designs == null) {
		System.out.println("Menu " + Commands.RECENT_DESIGNS + " not found");
		return;
	}

	designs.removeAll();
	for (int i = SystemSettings.lastDesigns.list.size() - 1; i >= 0; i--){
		//System.out.println("design: " + "" + SystemSettings.lastDesigns.list.get(i));
		File f = (File) SystemSettings.lastDesigns.list.get(i);
		if (f == null) {
			System.out.println("Recent menu ("+i+") is null");
			continue;
		}
		if (f.getParentFile() == null) {
			System.out.println("Recent parent menu is null for: " + f);
			continue;
		}

		if (f.getParentFile().equals(Main.app.getDesignFolder()))
			f = new File(f.getName());
		CommandMenuItem mi = new CommandMenuItem("" + f);
		mi.actionType = ActionHandler.LAST_DESIGN;
		mi.addActionListener(eventHandler);
		designs.add(mi);
	}
}

/**
 * Display constructor comment.
 */
public void addElement(BoxItem box) throws Exception {
	if (box == null)
		throw new RuntimeException("Is null");
	//System.out.println("added " + box.element.getName());
	box.panel = panel;
	box.recalculate();
	panel.boxes.add(box);
}
public static void setLoadTools(boolean newValue) {
	loadTools = newValue;
}
}
