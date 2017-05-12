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
import javax.swing.JScrollPane;

import static java.nio.file.StandardWatchEventKinds.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class FileSystemWatcher {
    
    // SQL Database connection object
    public static Connection conn;
    
    // Database constants 
    final int MATCH_NUM_INDEX = 4;
    final int TEAM_NUM_INDEX = 2;
    final int TABLET_NUM_INDEX = 0;
    // The current output file number
    private static int extFileNum = 0;
    
    // GUI constants 
    // The current string to display in the JFrame
    private static String dispString = "";
    // The number of lines displayed in the JFrame
    private static int lines;
    private static JFrame frame;
    private static JTextArea console;
    
    private static FileSystemWatcher instance;
    
    public static void main(String[] args) throws IOException {
        instance = new FileSystemWatcher();
    } // End main
    
    /** 
     * Finds and stores any new forms in the directory folder by: 
     * 1) checking for a new file of the appropriate type that contains form data 
     * 2) converting the file's form data into Form objects
     * 3) transferring the newly read forms into the database
     * 
     * If there is a USB mounted, this method also transfers the file containing the form data to this USB. 
     */
    public static void checkFolderForFile() {
        output("Checking folder for file...");
        
        /** the WatchService will continue to check for File System events until the program is closed.
         * Look up WatchService for more info.
         */
        while (true) {
            
            WatchService watcher = null;
            try {
                watcher = FileSystems.getDefault().newWatchService();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // The main folder where changes will be monitored.
            // This section of the code looks for a new change in this folder, i.e. a new form being added. 
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
            
            // Analyzing/handling the changes that the WatchService detects 
            for (WatchEvent<?> event : key.pollEvents()) {
                
            	// Obtaining and verifying the file
                // The filename is the context of the event/new change.
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();
                
                 // Verify that the new file is a text file.
                 try {
	                 Path child = dir.resolve(filename);
	                 if (!Files.probeContentType(child).equals("text/plain")) {
		                 String message = String.format("New file '%s'" + " is not a
		                 plain text file.%n", filename);
		                 output(message);
	                 }
                 } catch (IOException x) {
	                 System.err.println(x);
	                 continue;
                 }
                
                 
                // Obtains the file that was added to the folder by accessing the file in the directory that matches
                // the file name corresponding to the event
                File inputFile = new File(new File(System.getProperty("user.home"), "Desktop"),
                		filename.getFileName().toString());
                // Transfers the input file to a USB, if there is one. 
                writeToUSB(inputFile);
                
                // Processes the file and stores all the forms in the file in the database. 
                processFileFromUSB(inputFile); 

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
    
    /** 
     * Finds the mounted USB and processes all files in the USB. 
     */
    public static void checkForUSBs() {
    	
    	// Looks for the directory of the mounted USB 
        String outputFilePath = "";
        while (outputFilePath.equals("")) {
            outputFilePath = findMountedUSB();
        }
        
        // Accesses and processes all files from the mounted USB 
        File dir = new File(outputFilePath);
        File[] filesInUSB = dir.listFiles();
        for (File file : filesInUSB) {
            output(file.getName());
            processFileFromUSB(file);
        }
    }
    
    /** 
     * Processes all the forms from a single file and stores these forms in the database. 
     */
    public static void processFileFromUSB(File inputFile) {
       // Reads the content of the file into a String representation of the form. 
    	String content = "";
        try {
            content = readFromFile(inputFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // Stores all the forms in the file 
        ArrayList<Form> forms = new ArrayList<>();
        // Continues reading the form until it finds all the forms in the file 
        boolean done = false;
        while (!done) {
            // double pipes delimit forms in the file.
            int index = content.indexOf(Form.FORM_DELIMITER);
            if (index == -1)
            	// If no further forms are found, stop loooking for more forms. 
                done = true;
            else {
            	// This must remain substring and not split, though that might seem like an easier implementation.
            	// Split ends up splitting all the characters in the string, thus destroying the form data. 
                forms.add(new Form(content.substring(0, index)));
                content = content.substring(index + 2);
            }
        }
        
        // Store all forms read from the file into the database. 
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
        console.setVisible(true);
        
        // Set key bindings fpr getting the prescouting form, average form, and team comments 
        console.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK),
                                                                   "get prescouting form");
        console.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK),
                                                                   "get average form");
        console.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK),
                													"get team comments");
        console.getActionMap().put("get prescouting form", new PrescoutingAction("get prescouting form", null,
                                                                                 "gets a prescouting form", KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK).getKeyCode()));
        console.getActionMap().put("get average form", new AverageAction("get average form", null,
                                                                         "gets an average form", KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK).getKeyCode()));
        console.getActionMap().put("get team comments", new CommentAction("get team comments", null,
                "gets all comments for a team", KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK).getKeyCode()));
        
        // Scrollbar 
        JScrollPane scroll = new JScrollPane(console);
        scroll.setFocusable(true);
        scroll.setSize(945, 805);
        scroll.setLocation(10, 10);
        frame.add(scroll);
        
        // Button choices 
        String[] buttons = { "Check for New Form", "Read from USB" };
        int response = JOptionPane.showOptionDialog(frame, "Do you want to transfer to or read from the USB?",
                                                    "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[1]);
        // Handling user choice 
        output("Ready");
        if (response == JOptionPane.YES_OPTION) {
            output("Checking for a new form");
            checkFolderForFile();
        } else if (response == JOptionPane.NO_OPTION) {
            output("Reading from a USB");
            checkForUSBs();
        } else {
            output("Error reading input");
        }
        
    }
    
    /** 
     * Finds a mounted USB. 
     */
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
    
    /** 
     * Transfers a file to a USB. 
     */
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
    
    /** 
     * Transfers a file to the desktop.  
     */
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
    
    /**
     * Converts the contents of a file to a String. 
     */
    public static String readFromFile(File inputFile) throws IOException {
        Scanner in = new Scanner(inputFile);
        String content = "";
        while (in.hasNextLine()) {
            content += in.nextLine();
        }
        in.close();
        return content;
    }
    
    /** 
     * Outputs a message to the console in Eclipse and the GUI console. 
     */
    public static void output(String s) {
        
        System.out.println(s);
        dispString += s + "\n";
        String[] array = dispString.split("\n");
        lines = array.length;
        if (lines < 148) {
            lines++;
        } else {
            lines = 0;
            dispString = "";
            for (int i = array.length-1; i >= array.length-148; i--) {
                dispString = array[i] + "\n" + dispString;
                lines++;
            }
        }
        
        console.setText(dispString);
        
    } 
    
    /**
     * Stores a form in the database. 
     */
    public static void storeInDB(Form form) throws SQLException {
        
    	// Checks if there is an open connection 
        if (!getConnection()) {
            output("DB broken!");
        } else {
            CallableStatement stmt = null;
            stmt = conn.prepareCall("{call procInsertReport(?,?,?,?,?,?)}");
            // The ordinal denotes whether the form is a prescouting or match scouting form 
            stmt.setInt(1, form.getFormType().ordinal());
            // The tablet number references the number of the machine from which the form originated 
            stmt.setInt(2, form.getTabletNum());
            // The scout name referneces the scout who filled in the form 
            stmt.setString(3, form.getScoutName());
            // teamNum references the number of the team that the form corresponds to 
            stmt.setInt(4, form.getTeamNum());
            // matchNum references the number of the match that the form corresponds to
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
        
    } 
    
    /** 
     * Checks if there is a connection 
     */
    public static boolean getConnection() {
        
        boolean connected = false;
        
        try {
        	// Initializes the connection 
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/scouting?useSSL=false", "lucas", "lucas");
            output("Connected to database");
            connected = true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        return connected;
        
    }
    
    /** 
     * Obtains the prescouting form corresponding specifically to the given team number. 
     */
    public static ResultSet[] getPrescoutingForm(int teamNum) {
        ResultSet[] resultSets = new ResultSet[2];
        
        // Checks if there is an open connection 
        if (!getConnection()) {
            output("DB Broken!");
        } else {
        	// Queries for the prescouting form that corresponds to the team
            int reportID = 0;
            String sql = "SELECT ID, TabletNum, ScoutName, TeamNum FROM scouting.report WHERE (TeamNum = " + teamNum
            + ") AND (FormType = " + Form.FormType.PRESCOUTING_FORM.ordinal() + ")";
            try {
            	// Gets the report information 
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
            // Returns all the values and item ids of the records of the prescouting form 
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
        }
        return resultSets;
    }
    
    /** 
     * Obtains all the comments associated with a specific team. 
     */
    public static ResultSet getTeamComments(int teamNum) 
    {
    	ResultSet comments = null; 
    	
    	// Checks for an open connection 
    	if (!getConnection()) {
            output("DB Broken!");
        } else {
        	String sql = "CALL scouting.procComments(" + teamNum + ")";
            try {
                PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                               ResultSet.CONCUR_READ_ONLY);
                stmt.executeQuery();
                comments = stmt.getResultSet();
            } catch (SQLException e) {
                output(e.getMessage() + " error code:" + e.getErrorCode() + " sql state:" + e.getSQLState());
                return null;
            }
        }
    	return comments;
    }
    
    /** 
     * Visualizes a prescouting form. 
     */
    public static PrescoutingForm visualizePrescoutingForm(ResultSet[] resultSets) {
    	// First checks if there were any results sets (any data) returned 
    	if (resultSets == null) return null;
    	
    	// Creates a prescouting form object from the data obtained from the query 
    	// Converts the result set into a String representation of a form 
        String rawForm = String.valueOf(Form.FormType.PRESCOUTING_FORM.ordinal())+"|";
        // Obtains the report information -- tablet number, team number, scout name, match number, separated by a delimeter 
        ResultSet identifyingInfo = resultSets[0];
        try {
            identifyingInfo.first();
            try {
                rawForm += (identifyingInfo.getString(2) + "|" + identifyingInfo.getString(3) + "|"
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
        
        // Obtains the record information - all the item ids and their values, with each item id separated by a delimeter
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
        
        // Creates a prescouting form from the raw form String representation 
        PrescoutingForm form = new PrescoutingForm(rawForm);
        return form;
    }
    
    /** 
     * Obtains the average information for a specific team. 
     */
    public static ResultSet[] getAverageForm(int teamNum) {
        ResultSet[] resultSets = new ResultSet[2];
        // Checks if there's an open connection 
        if (!getConnection()) {
            output("DB Broken!");
        } else {
        	// Obtains the average data of a team 
            String sql = "CALL scouting.procAverages(" + teamNum + ")";
            try {
                PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                               ResultSet.CONCUR_READ_ONLY);
                stmt.executeQuery();
                resultSets[0] = stmt.getResultSet();
            } catch (SQLException e) {
                output(e.getMessage() + " error code:" + e.getErrorCode() + " sql state:" + e.getSQLState());
                return null;
            }
            // Obtains data for items that are represented as proportions (True/False items or accuracy) 
            sql = "CALL scouting.procProportions(" + teamNum + ")";
            try {
                PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                               ResultSet.CONCUR_READ_ONLY);
                stmt.executeQuery();
                stmt.getMoreResults();
                resultSets[1] = stmt.getResultSet();
            } catch (SQLException e) {
                output(e.getMessage() + " error code:" + e.getErrorCode() + " sql state:" + e.getSQLState());
                return null;
            }
        }
        return resultSets;
    }
    
    /** 
     * Visualizes all the comments about a specific team 
     */
    public static void visualizeTeamComments(ResultSet comments) 
    {
    	// Stores all the comments about a team
    	ArrayList<String> commentBlocks = new ArrayList<String>(); 
        try {
        	comments.first(); 
        	while (!comments.isAfterLast())
        	{
        		commentBlocks.add(comments.getString(1)); 
        		comments.next(); 
        	}
        }
        catch(SQLException e) { 
        	e.printStackTrace();
        }
        
        // Outputs all the comments about a team
        String commentData = ""; 
        for (String comment: commentBlocks) 
        {
        	commentData += comment + "\n";
        }
        output("Comments: "+"\n"+commentData); 
    }
    
    /** 
     * Visualizes an average form. 
     */
    public static String visualizeAverageForm(ResultSet[] resultSets)
    {
    	// Gets all the average data for items better represented as averages (average gear makes, average climb attempts, etc.) 
        ResultSet averages = resultSets[0];
        try {
			if (!averages.first()) return null;
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    
        // Stores the item IDs of all these items, the average values, standard deviations, and sample sizes for the 
        // purpose of presenting the information in a more beneficial way 
        ArrayList<Integer> itemIDs = new ArrayList<Integer>();
        ArrayList<Double> averageVals = new ArrayList<Double>();
        ArrayList<Double> standardDevs = new ArrayList<Double>();
        ArrayList<Integer> sampleSizes = new ArrayList<Integer>();
        try {
            averages.first();
            while (!averages.isAfterLast())
            {
                itemIDs.add(averages.getInt(1));
                averageVals.add(averages.getDouble(2));
                standardDevs.add(averages.getDouble(3));
                sampleSizes.add(averages.getInt(4));
                averages.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Gets all the data for items better represented as proportions 
        ResultSet proportions = resultSets[1];
        // Stores the item IDs, sums, sample sizes, and success rates for these items 
        ArrayList<Integer> itemsIDs = new ArrayList<Integer>();
        ArrayList<Integer> sums = new ArrayList<Integer>();
        ArrayList<Integer> samplesSizes = new ArrayList<Integer>();
        ArrayList<Integer> successRates = new ArrayList<Integer>();
        try {
            proportions.first();
            while (!proportions.isAfterLast())
            {
                itemsIDs.add(proportions.getInt(1));
                sums.add(proportions.getInt(2));
                samplesSizes.add(proportions.getInt(3));
                successRates.add(proportions.getInt(4));
                proportions.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Returns the averages and proportions data as a String 
        String rawData = "";
        for (int i = 0; i < itemIDs.size(); i++)
        {
            rawData += itemIDs.get(i) + "," + averageVals.get(i) + "," + standardDevs.get(i) + "," + sampleSizes.get(i) + "|";
        }
        rawData += "##";
        for (int i = 0; i < itemsIDs.size(); i++)
        {
            rawData += itemsIDs.get(i) + "," + sums.get(i) + "," + samplesSizes.get(i) + "," + successRates.get(i) + "|";
        }
        return rawData;
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
    
    public class AverageAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        public AverageAction (String text, ImageIcon icon, String desc, Integer mnemonic) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }
        public void actionPerformed(ActionEvent e) {
            String teamNumber = JOptionPane.showInputDialog("Please input a team number.");
            int teamNum = 0;
            try {
                teamNum = Integer.parseInt(teamNumber);
                String form = 
                visualizeAverageForm(getAverageForm(teamNum));
                if (form != null) output(MatchForm.averageFormVisualizer(form));
                else output("Team/Form not found.");
            } catch (NumberFormatException e1) {
                output("Invalid team number.");
            }
        }
    }
    
    public class CommentAction extends AbstractAction { 
    	private static final long serialVersionUID = 1L; 
    	public CommentAction(String text, ImageIcon icon, String desc, Integer mnemonic) { 
    		super(text, icon); 
    		putValue(SHORT_DESCRIPTION, desc); 
    		putValue(MNEMONIC_KEY, mnemonic); 
    	}
    	public void actionPerformed(ActionEvent e) {
    		String teamNumber = JOptionPane.showInputDialog("Please input a team number."); 
    		int teamNum = 0; 
    		try {
    			teamNum = Integer.parseInt(teamNumber); 
    			visualizeTeamComments(getTeamComments(teamNum));
            } catch (NumberFormatException e1) {
                output("Invalid team number.");
            }
    	}
    }
    
}