/**************************************************************************************************
 * Scouting0.8 : Database version
 * @Author Lucas Varella
 *
 * This program is meant to handle input from dummy data collectors, and properly insert said info
 * into the database. The GUI is limited: it merely indicates when a singular form has been
 * processed, not if the program ever catches an exception. Thus, it is recommended to run this
 * program from the safety of an IDE.
 *
 * The program utilizes a WatchService in order to monitor changes to the children of a specific
 * folder. A single WatchKey waits for a change to a specified folder in an endless loop, meant to
 * be terminated only by closing the program. Whenever the WatchKey register an event or group of
 * events (for our purposes, it will most likely be a single event), the event is expected to be
 * the creation of a file or modification of a file. As soon as the event is registered, the file
 * is located and its contents are broken down into constituent forms, and forms are broken down
 * into constituent items for storage into the database. Items are stored in the database one at
 * a time. The connection object to the database resets every time the program saves an item.
 *
 * The text file must follow a specific format dictated by the tablet software.
 *************************************************************************************************/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JOptionPane;

import static java.nio.file.StandardWatchEventKinds.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class FileSystemWatcher {
    
    // SQL Database connection object
    public static Connection conn;
    final int MATCH_NUM_INDEX = 4;
    final int TEAM_NUM_INDEX = 2;
    final int TABLET_NUM_INDEX = 0;
    final String Shreya_PW = "vanshika";
    final String Shreya_user = "root";
    final String Lucas_PW = "Lucas";
    final String Lucas_user = "Lucas";
    final String Shreya_MySQL = "jdbc:mysql://localhost:3306/scouting?useSSL=false";
    final String Lucas_MySQL = "jdbc:mysql://LucasPC:3306/scouting?useSSL=false";
    final String Shreya_Desktop = "/Users/local/Desktop";
    
    // The current string to display in the JFrame
    private static String dispString = "";
    // The current output file number
    private static int extFileNum = 0;
    // The number of lines displayed in the JFrame
    private static int lines;
    private static JFrame frame;
    private static JTextArea console;
    private static FileSystemWatcher instance;
    
    public static void main(String[] args) throws IOException {
        instance = new FileSystemWatcher();
    } // End main
    
    public static void checkFolderForFile() {
        output("Checking folder for file...");
        
        // the WatchService will continue to check for File System events until
        // the program is closed.
        // Look up WatchService for more info.
        while (true) {
            
            WatchService watcher = null;
            try {
                watcher = FileSystems.getDefault().newWatchService();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // The main folder where changes will be monitored.
            Path dir = new File(System.getProperty("user.home"), "Desktop").toPath();
            WatchKey key = null;
            try {
                key = dir.register(watcher, ENTRY_CREATE);
                key = watcher.take();
                output("Found change...");
            } catch (IOException | InterruptedException x) {
                x.printStackTrace();
            }
            
            output("Reading a file...");
            
            // This code will only be reached when the WatchService has found a
            // change in the monitored folder.
            // It will hold the main thread until it has found a change.
            for (WatchEvent<?> event : key.pollEvents()) {
                
                // The filename is the context of the event.
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();
                
                // // Verify that the new file is a text file.
                // try {
                // // Resolve the filename against the directory.
                // // This will indicate whether a new file found is a text
                // // file.
                // // Checks for extraneous new files in the monitored folder.
                // // Look up resolving file names for more info.
                // Path child = dir.resolve(filename);
                // if (!Files.probeContentType(child).equals("text/plain")) {
                // String message = String.format("New file '%s'" + " is not a
                // plain text file.%n", filename);
                // output(message);
                // }
                // } catch (IOException x) {
                // System.err.println(x);
                // continue;
                // }
                
                // The tablets will always send all forms as a single line, to
                // be contained in this string.
                File inputFile = new File(new File(System.getProperty("user.home"), "Desktop"),
                                          filename.getFileName().toString());
                writeToUSB(inputFile);
                
                String content = "";
                try {
                    content = readFromFile(inputFile);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // We do not know how many forms will be present in the file.
                // The next loop will read all of the forms it finds onto this
                // array.
                ArrayList<Form> forms = new ArrayList<>();
                // A count of all forms present in this file.
                // flag
                boolean done = false;
                while (!done) {
                    // double pipes delimit forms in the file.
                    int index = content.indexOf(Form.FORM_DELIMITER);
                    if (index == -1) {
                        done = true;
                        forms.add(new Form(content));
                    } else {
                        forms.add(new Form(content.substring(0, index)));
                        content = content.substring(index + 2);
                    }
                }
                // We now know the number of forms we've read.
                // Now we will iterate through each item in each form
                for (Form form : forms) {
                    try {
                        storeInDB(form);
                        conn.close();
                    } catch (SQLException ev1) {
                        ev1.printStackTrace();
                    }
                    output("File read successfully.");
                }
            }
            
            // Reset the key -- this step is critical if you want to
            // receive further watch events. If the key is no longer valid,
            // the directory is inaccessible so exit the loop.
            boolean valid = key.reset();
            if (!valid) {
                System.err.format("Directory inaccessible");
                break;
            }
            
        }
    }
    
    public static void checkForUSBs() {
        String outputFilePath = "";
        while (outputFilePath.equals("")) {
            outputFilePath = findMountedUSB();
        }
        File dir = new File(outputFilePath);
        File[] filesInUSB = dir.listFiles();
        for (File file : filesInUSB) {
            output(file.getName());
            processFileFromUSB(file);
        }
    }
    
    public static void processFileFromUSB(File inputFile) {
        String content = "";
        try {
            content = readFromFile(inputFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // We do not know how many forms will be present in the file.
        // The next loop will read all of the forms it finds onto this
        // array.
        ArrayList<Form> forms = new ArrayList<>();
        // A count of all forms present in this file.
        // flag
        boolean done = false;
        while (!done) {
            // double pipes delimit forms in the file.
            int index = content.indexOf(Form.FORM_DELIMITER);
            if (index == -1)
                done = true;
            else {
                forms.add(new Form(content.substring(0, index)));
                content = content.substring(index + 2);
            }
        }
        // We now know the number of forms we've read.
        // Now we will iterate through each item in each form
        for (Form form : forms) {
            try {
                storeInDB(form);
                conn.close();
            } catch (SQLException ev1) {
                ev1.printStackTrace();
            }
            output("File read successfully.");
        }
    }
    
    public FileSystemWatcher() {
        // Initiating the UI
        frame = new JFrame();
        // intiating the frame
        frame.setTitle("Scouting File System Watcher");
        frame.setLayout(null);
        frame.setLocation(100, 100);
        frame.setSize(980, 870);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        console = new JTextArea("");
        console.setEditable(false);
        console.setFocusable(true);
        console.setLayout(null);
        console.setLineWrap(true);
        console.setSize(945, 805);
        console.setLocation(10, 10);
        frame.add(console);
        console.setVisible(true);
        console.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK),
             "get prescouting form");
        console.getActionMap().put("get prescouting form",
                                   new PrescoutingAction("get prescouting form", null, "gets a prescouting form",
                                                         KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK).getKeyCode()));
        
        // Initialize UI
        String[] buttons = { "Read from Folder", "Read from USB" };
        // String num = JOptionPane.showInputDialog("Enter team number");
        // PrescoutingForm prescouting =
        // visualizePrescoutingForm(getPrescoutingForm(Integer.parseInt(num)));
        // prescouting.prescoutingFormVisualizer();
        int response = JOptionPane.showOptionDialog(frame, "Do you want to transfer to or read from the USB?",
                                                    "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[1]);
        output("Ready");
        if (response == JOptionPane.YES_OPTION) {
            output("Reading from a Folder");
            checkFolderForFile();
        } else if (response == JOptionPane.NO_OPTION) {
            output("Reading from a USB");
            checkForUSBs();
        } else {
            output("Error reading input");
        }
        
    }
    
    public static String findMountedUSB() {
        output("Finding mounted USBs...");
        // Finds USBs mounted
        File files[] = File.listRoots();
        FileSystemView fsv = FileSystemView.getFileSystemView();
        String outputFilePath = "";
        try {
            for (File file : files) {
                if (fsv.getSystemTypeDescription(file).equals("USB Drive"))
                    outputFilePath = file.getAbsolutePath();
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            output("Failed to Find USB(s)");
        }
        return outputFilePath;
    }
    
    public static void writeToUSB(File inputFile) {
        String outputToFile = "";
        String outputFilePath = findMountedUSB();
        // Creates file and checks if it is modifiable
        File outputToUSB = new File(outputFilePath, "scoutingfile" + extFileNum + ".txt");
        outputToUSB.setWritable(true);
        try {
            if (!outputToUSB.exists())
                if (!outputToUSB.createNewFile())
                    throw new IOException();
            outputToFile = readFromFile(inputFile);
        } catch (IOException e) {
            output("Failed to create/use usb file");
            e.printStackTrace();
        }
        
        // Outputs the information to the file
        output(outputToFile);
        try {
            FileWriter outputInfoToUSBFile = new FileWriter(outputToUSB, false);
            outputInfoToUSBFile.write(outputToFile);
            outputInfoToUSBFile.close();
        } catch (FileNotFoundException ioe) {
            System.err.println("FileNotFound: " + outputToUSB);
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe);
        }
        extFileNum++;
    }
    
    public static void writeToDesktop(File inputFile) {
        String outputToFile = "";
        try {
            outputToFile = readFromFile(inputFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Outputs the information to the file
        output(outputToFile);
        try {
            FileWriter outputInfoToUSBFile = new FileWriter(new File(System.getProperty("user.home"), "Desktop"),
                                                            false);
            outputInfoToUSBFile.write(outputToFile);
            outputInfoToUSBFile.close();
        } catch (FileNotFoundException ioe) {
            System.err.println("FileNotFound: " + new File(System.getProperty("user.home"), "Desktop"));
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe);
        }
    }
    
    public static String readFromFile(File inputFile) throws IOException {
        Scanner in = new Scanner(inputFile);
        String content = "";
        while (in.hasNextLine()) {
            content += in.nextLine();
        }
        in.close();
        return content;
    }
    
    // method output() takes in a string to write it to the JFrame (the
    // "console").
    public static void output(String s) {
        
        System.out.println(s);
        dispString += s + "%n";
        
        if (lines < 48) {
            lines++;
        } else {
            int loc = dispString.indexOf("%n");
            dispString = dispString.substring(loc + 2);
        }
        
        console.setText(String.format(dispString));
        
    } // End output
    
    public static void storeInDB(Form form) throws SQLException {
        
        if (!getConnection()) {
            output("DB broken!");
        } else {
            CallableStatement stmt = null;
            stmt = conn.prepareCall("{call procInsertReport(?,?,?,?,?,?)}");
            stmt.setInt(1, form.getFormType().ordinal());
            stmt.setInt(2, form.getTabletNum());
            stmt.setString(3, form.getScoutName());
            stmt.setInt(4, form.getTeamNum());
            stmt.setInt(5, form.getMatchNum());
            stmt.registerOutParameter(6, Types.INTEGER);
            
            try {
                stmt.executeQuery();
                form.setFormID(stmt.getInt(6));
            } catch (SQLException e) {
                e.printStackTrace();
                output("broken");
                System.exit(0);
            } finally {
                if (stmt != null)
                    stmt.close();
            }
            for (int i = 0; i < form.getAllRecords().size(); i++) {
                stmt = conn.prepareCall("{call procInsertRecord(?,?,?)}");
                stmt.setString(1, form.getAllRecords().get(i).getValue());
                stmt.setInt(2, form.getFormID());
                stmt.setInt(3, form.getAllRecords().get(i).getItemID());
                
                try {
                    stmt.executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                    output("broken");
                    System.exit(0);
                }
            }
            if (stmt != null) {
                stmt.close();
            }
            
        }
        
    } // End storeInDB
    
    public static boolean getConnection() {
        
        boolean connected = false;
        
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/scouting?useSSL=false", "lucas", "lucas");
            output("Connected to database");
            connected = true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        return connected;
        
    } // End getConnection
    
    public static ResultSet[] getPrescoutingForm(int teamNum) {
        ResultSet[] resultSets = new ResultSet[3];
        if (!getConnection()) {
            output("DB Broken!");
        } else {
            int reportID = 0;
            String sql = "SELECT ID, TabletNum, ScoutName, TeamNum FROM scouting.report WHERE (TeamNum = " + teamNum
            + ") AND (FormType = " + Form.FormType.PRESCOUTING_FORM.ordinal() + ")";
            try {
                PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                               ResultSet.CONCUR_READ_ONLY);
                stmt.executeQuery();
                resultSets[0] = stmt.getResultSet();
                resultSets[0].first();
                reportID = resultSets[0].getInt(1);
            } catch (SQLException e) {
                output(e.getMessage() + " error code:" + e.getErrorCode() + " sql state:" + e.getSQLState());
                return null;
            }
            sql = "SELECT `Value`, ITEM_ID FROM scouting.record WHERE (REPORT_ID = " + reportID + ")";
            try {
                PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                               ResultSet.CONCUR_READ_ONLY);
                stmt.executeQuery();
                resultSets[1] = stmt.getResultSet();
            } catch (SQLException e) {
                output(e.getMessage() + " error code:" + e.getErrorCode() + " sql state:" + e.getSQLState());
                return null;
            }
            sql = "SELECT ID, `Name`, DATATYPE_ID FROM scouting.item WHERE (scouting.item.`Active` = 1);";
            try {
                PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                               ResultSet.CONCUR_READ_ONLY);
                stmt.executeQuery();
                resultSets[2] = stmt.getResultSet();
            } catch (SQLException e) {
                output(e.getMessage() + " error code:" + e.getErrorCode() + " sql state:" + e.getSQLState());
                return null;
            }
        }
        return resultSets;
    }
    
    public static PrescoutingForm visualizePrescoutingForm(ResultSet[] resultSets) {
        if (resultSets == null) return null;
        String rawForm = String.valueOf(Form.FormType.PRESCOUTING_FORM.ordinal())+"|";
        ResultSet identifyingInfo = resultSets[0];
        try {
            identifyingInfo.first();
            try {
                rawForm += (identifyingInfo.getString(1) + "|" + identifyingInfo.getString(2) + "|" + identifyingInfo.getString(3) + "|"
                            + identifyingInfo.getString(4) + "|");
                rawForm += "-1";
                identifyingInfo.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ResultSet itemsCollection = resultSets[1];
        try {
            itemsCollection.first();
            while (!itemsCollection.isAfterLast()) {
                try {
                    rawForm += ("|" + itemsCollection.getInt(2) + "," + itemsCollection.getString(1));
                    itemsCollection.next();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PrescoutingForm form = new PrescoutingForm(rawForm);
        return form;
    }
    
    public class PrescoutingAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        public PrescoutingAction (String text, ImageIcon icon, String desc, Integer mnemonic) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }
        public void actionPerformed(ActionEvent e) {
            String teamNumber = JOptionPane.showInputDialog("Please input a team number.");
            int teamNum = 0;
            try {
                teamNum = Integer.parseInt(teamNumber);
                PrescoutingForm form = 
                visualizePrescoutingForm(getPrescoutingForm(teamNum));
                if (form != null) output(form.prescoutingFormVisualizer());
                else output("Team/Form not found.");
            } catch (NumberFormatException e1) {
                output("Invalid team number.");
            }
        }
    }
    
}
