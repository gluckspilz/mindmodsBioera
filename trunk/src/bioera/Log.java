/* Log.java v 1.0.9   11/6/04 7:15 PM
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

package bioera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class Log {
    public final static String LINESEP = "\r\n";
    public final static String BUFFER_FILE_EXTENSION = ".buffer";

    private FileOutputStream log = null;
    private FileInputStream logincoming = null;
    private File logFile;

    private FileOutputStream bufferLog = null;
    private File bufferFile;

    private String logDirectory;

    protected static boolean debug = Debugger.get("log");
    /**
     * Log constructor comment.
     */
    public Log(File file) throws Exception {
        super();
        logDirectory = file.getParent();
        logFile = file;
        bufferFile = new File(file.getParent(),
                              file.getName() + BUFFER_FILE_EXTENSION);
        reinitLog();
    }

    /**
     * Log constructor comment.
     */
    public Log(String dir, String filename) throws Exception {
        this(new File(dir, filename));
    }

    /**
     * Insert the method's description here.
     * Creation date: (7/26/2000 9:27:26 AM)
     */
    public void close() {
        closeBoth();
    }

    /**
     * Insert the method's description here.
     * Creation date: (7/26/2000 9:27:26 AM)
     */
    private synchronized void closeBoth() {
        if (log != null) {
            try {
                if (debug)
                    System.out.println("-- Closing log " + getLogFile().getName());
                log.close();
            } catch (Exception e) {
            }

            log = null;
        }

        if (bufferLog != null) {
            try {
                bufferLog.close();
            } catch (Exception e) {
            }

            bufferLog = null;
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (7/26/2000 9:28:41 AM)
     */
    public static String createDateLine() {
        return Tools.dateTimeFormatter().format(new java.util.Date()) + LINESEP;

    }

    /**
     * Code to perform when this object is garbage collected.
     *
     * Any exception thrown by a finalize method causes the finalization to
     * halt. But otherwise, it is ignored.
     */
    protected void finalize() throws Throwable {
        if (log != null) {
            log.close();
            log = null;
        }
        super.finalize();
    }

    /**
     * Insert the method's description here.
     * Creation date: (7/27/2000 2:38:10 PM)
     */
    public String getLogDir() {
        return logDirectory;
    }

    /**
     * Insert the method's description here.
     * Creation date: (7/27/2000 2:38:10 PM)
     */
    public File getLogFile() {
        return logFile;
    }

    /**
     * Insert the method's description here.
     * Creation date: (7/27/2000 2:47:06 PM)
     */
    private synchronized void initBufferLog() {
        try {
            if (debug)
                System.out.println("Initing buffer log " + bufferFile.getName());
            bufferLog = new FileOutputStream(bufferFile.getAbsolutePath(), true);
        } catch (Exception e) {
            System.out.println("Couldn't initialize buffer log " + bufferFile);
            bufferLog = null;
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (7/26/2000 9:27:26 AM)
     */
    public synchronized boolean isClosed() {
        return log == null;
    }

    /**
     *
     *
     * @param args java.lang.String[]
     */
    public static void main(String[] args) throws Exception {
        Log log = new Log(new File("temp.log"));
        log.print("ala ma kota\n");
        log.close();
        log.print("values after log closed\n");
        System.out.println("Waiting");
        Thread.sleep(10000);
        log.reinitLog();
        log.print("values after log re-inited\n");
        log.close();
        System.out.println("Finished");
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/18/2000 12:48:01 PM)
     */
    public void print(String msg) {
        write(msg.getBytes());
    }

    /**
     * Insert the method's description here.
     * Creation date: (7/26/2000 9:26:08 AM)
     */
    public void println(String msg) {
        print(msg + LINESEP);
    }

    /**
     * Insert the method's description here.
     * Creation date: (7/26/2000 9:26:08 AM)
     */
    private synchronized void recopyFromBuffer() throws Exception {
        if (debug)
            System.out.println("Recopying from buffer to " + logFile.getName());

        if (log != null) {
            InputStream in = new FileInputStream(bufferFile);
            try {
                byte b[] = new byte[1024 * 16];
                int count = in.read(b);
                while (count != -1) {
                    if (debug) {
                        String s = new String(b, 0, count);
                        System.out.println("Wrote from " + bufferFile.getName() +
                                           ": '" + s + "'");
                    }
                    log.write(b, 0, count);
                    count = in.read(b);
                }
            } finally {
                if (in != null)
                    in.close();
            }
        } else {
            System.out.println("Main log not initialized for recopying");
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (7/27/2000 2:47:06 PM)
     */


    public synchronized InputStream getLogstream() throws Exception {
        InputStream in = new FileInputStream(logFile);
        return in;
    }

    public synchronized void readLog() throws Exception {
        //closeBoth();

                    InputStream in = new FileInputStream(logFile);
                    try {
                        byte b[] = new byte[1024 * 16];
                        int count = in.read(b);
                        while (count != -1) {
                            //debuggy

                                String s = new String(b, 0, count);
                                System.out.println("Read from " + logFile.getName() +
                                                   ": '" + s + "'");

                            // log.write(b, 0, count);
                            count = in.read(b);
                        }
                    } finally {
                        if (in != null)
                            in.close();
                    }

        try {
            logincoming = new FileInputStream(logFile.getAbsolutePath());
        } catch (Exception e)  {
            throw new Exception("Couldn't open file for read: " + logFile);
        }
    }


    public synchronized void reinitLog() throws Exception {
        // Open log file to write
        if (debug)
            System.out.println("Re-init of log " + logFile.getName());

        // Close both main and buffer log
        closeBoth();

        try {
            // Open main log
            log = new FileOutputStream(logFile.getAbsolutePath(), false);
        } catch (Exception e) {
            throw new Exception("Couldn't open file for write: " + logFile);
        }

        // Copy data from buffer (if any)
        try {
            if (bufferFile.exists()) {
                recopyFromBuffer();
                if (!bufferFile.delete()) {
                    System.out.println("Couldn't delete buffer log file " +
                                       bufferFile.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(
                    "Couldn't recopy bytes from buffer to main log: " + e);
        }
    }

    public static void setDebug(boolean newValue) {
        debug = newValue;
    }

    /**
     * Insert the method's description here.
     * Creation date: (7/26/2000 9:28:41 AM)
     */
    public void write(byte b[]) {
        write(b, 0, b.length);
    }

    /**
     * Insert the method's description here.
     * Creation date: (7/26/2000 9:28:41 AM)
     */
    public synchronized void write(byte b[], int off, int count) {
        try {
            // Write to buffer until log is reinitialized
            if (log != null) {
                log.write(b, off, count);
            } else {
                if (bufferLog == null)
                    initBufferLog();
                if (debug)
                    System.out.println("Writing to buffer log " +
                                       bufferFile.getName());
                bufferLog.write(b, off, count);
            }
        } catch (Exception e) {
            System.out.println("Error occured while writing to log file: " +
                               (log != null ? logFile.toString() :
                                (bufferLog != null ? bufferFile.toString() :
                                 "nothing initialized")));
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (7/26/2000 9:28:41 AM)
     */
    public void write(byte b) {
        write(new byte[] {b});
    }


    /**
     * Insert the method's description here.
     * Creation date: (7/26/2000 9:28:41 AM)
     */
    public void write(int b) {
        write(new byte[] {(byte) b});
    }

    /**
     * Insert the method's description here.
     * Creation date: (7/26/2000 9:28:41 AM)
     */
    public void write(Throwable error) {
        write((error.toString() + LINESEP).getBytes());
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        error.printStackTrace(new PrintWriter(b));
        write(b.toByteArray());
        write(LINESEP.getBytes());
    }

}

