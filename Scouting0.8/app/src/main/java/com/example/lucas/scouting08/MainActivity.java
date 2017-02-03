/**************************************************************************************************
 * Scouting0.8 : Database version
 * @Author Lucas Varella
 *
 * This app is meant to be a dummy scouting data collector, specifically for STRONGHOLD. It is meant
 * to transfer scouting forms to another machine, a master machine whose purpose is to maintain
 * and organize the data from all dummy data collectors. Each dummy data collector has a unique int
 * id. The information from the scouting forms is pre-formatted for storage in a specific database
 * schema, included in this project.
 *
 * The main content view is the scouting form, fit specifically for a 7" Kindle Fire HD. Each form
 * pertains to a match, only one form may be in use at any one time, and forms cannot be reopened
 * after they have been saved. This is to preserve data integrity: if there is a need to modify,
 * scale, or perform any other transformations on the data, it can be done so from the master
 * machine.
 *
 * All pending forms are kept in RAM until the next transfer. Once the user completes the process to
 * make a transfer, the forms are written to a bulk text file that is transferred via bluetooth to
 * the master machine.
 *
 * The bluetooth transfer activity on Android does not indicate to the calling activity whether the
 * bluetooth transfer was complete or not. A fail-safe system was implemented to make sure forms
 * were not overwritten in RAM.
 *************************************************************************************************/

package com.example.lucas.scouting08;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity {

    // Item IDs (for Database)
    private static class ItemIDs {
        final static String Present = "1";
        final static String Score = "2";
        final static String Autoreachesoverwalls = "3";
        final static String Autocrossesouterworks = "4";
        final static String Autoshooting = "5";
        final static String Canshoot = "6";
        final static String Canclimb = "7";
        final static String Rateshooting = "8";
        final static String Attemptportcullis = "9";
        final static String AttemptChevalDeFrise = "10";
        final static String AttemptMoat = "11";
        final static String AttemptRamparts = "12";
        final static String AttemptDrawbridge = "13";
        final static String AttemptRockWall = "14";
        final static String AttemptSallyPort = "15";
        final static String AttemptRoughTerrain = "16";
        final static String AttemptLowBar = "17";
        final static String TimescrossedPortcullis = "18";
        final static String TimescrossedChevalDeFrise = "19";
        final static String TimescrossedMoat = "20";
        final static String TimescrossedRamparts = "21";
        final static String TimescrossedDrawbridge = "22";
        final static String TimescrossedRockWall = "23";
        final static String TimescrossedSallyPort = "24";
        final static String TimescrossedRoughTerrain = "25";
        final static String TimescrossedLowBar = "26";
        final static String GotstuckPortcullis = "27";
        final static String GotstuckChevalDeFrise = "28";
        final static String GotstuckMoat = "29";
        final static String GotstuckRamparts = "30";
        final static String GotstuckDrawbridge = "31";
        final static String GotstuckRockWall = "32";
        final static String GotstuckSallyPort = "33";
        final static String GotstuckRoughTerrain = "34";
        final static String GotstuckLowBar = "35";
        final static String Comments = "44";
        final static String Ratedriving = "79";
        final static String ShootsHigh = "80";
        final static String ShootsLow = "81";
        final static String TimesShot = "82";
    } // End ItemIDs

    // FORM COMPONENTS

    Spinner pickName;
    Spinner rateShooting;
    Spinner rateDriving;

    RadioGroup grpPortcullis;
    RadioGroup grpChevalDeFrise;
    RadioGroup grpMoat;
    RadioGroup grpRamparts;
    RadioGroup grpDrawbridge;
    RadioGroup grpRockWall;
    RadioGroup grpSallyPort;
    RadioGroup grpRoughTerrain;
    RadioGroup grpLowBar;

    RadioButton failedPortcullis;
    RadioButton failedChevalDeFrise;
    RadioButton failedMoat;
    RadioButton failedRamparts;
    RadioButton failedDrawbridge;
    RadioButton failedRockWall;
    RadioButton failedSallyPort;
    RadioButton failedRoughTerrain;
    RadioButton failedLowBar;

    CheckBox chkReachOuterWorks;
    CheckBox chkCrossAuto;
    CheckBox chkShootAuto;
    CheckBox chkCanShoot;
    CheckBox chkCanClimb;
    CheckBox chkHighGoal;
    CheckBox chkLowGoal;

    CheckBox chkPortcullis;
    CheckBox chkChevalDeFrise;
    CheckBox chkMoat;
    CheckBox chkRamparts;
    CheckBox chkDrawbridge;
    CheckBox chkRockWall;
    CheckBox chkSallyPort;
    CheckBox chkRoughTerrain;
    CheckBox chkLowBar;

    Button addPortcullis;
    Button addChevalDeFrise;
    Button addMoat;
    Button addRamparts;
    Button addDrawbridge;
    Button addRockWall;
    Button addSallyPort;
    Button addRoughTerrain;
    Button addLowBar;
    Button addShooting;

    Button subPortcullis;
    Button subChevalDeFrise;
    Button subMoat;
    Button subRamparts;
    Button subDrawbridge;
    Button subRockWall;
    Button subSallyPort;
    Button subRoughTerrain;
    Button subLowBar;
    Button subShooting;

    Button btnNotPresent;
    Button btnSave;
    Button btnTransfer;

    EditText txtTeam;
    EditText txtMatch;
    EditText txtScore;

    EditText txtPortcullis;
    EditText txtChevalDeFrise;
    EditText txtMoat;
    EditText txtRamparts;
    EditText txtDrawbridge;
    EditText txtRockWall;
    EditText txtSallyPort;
    EditText txtRoughTerrain;
    EditText txtLowBar;
    EditText txtShooting;

    EditText txtComments;

    TextView lblFormsPending;

    // PASSWORD DIALOG COMPONENTS

    EditText txtPassword;
    Button btnGo;
    Button btnBack;

    // GLOBAL VARIBALES

    // Necessary for Bluetooth file transfer
    Intent intent = new Intent();
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    static BluetoothDevice device = null;
    // flag that indicates whether this is the first form to be saved to the current file or not.
    boolean firstForm = true;
    // String that holds all of the forms until btnTransfer is pressed. This string is then written
    // to a file.
    String forms = "";

    final int TABLET_NUMBER = 6;
    // the name of the file to be transferred.
    String fileName = "Redshift4048Fire6TEXTFILE.txt";
    // number of forms on device, waiting to be transferred.
    int formsPending = 0;
    // the scout's name
    String scoutName;
    // for the following ints: 0 = false and 1 = true
    int reachOuterWorks = 0;
    int autoCross = 0;
    int autoShoot = 0;
    int canShoot = 0;
    int canClimb = 0;
    int highGoal = 0;
    int lowGoal = 0;
    // selected rating for shooting & driving.
    int ratingShooting;
    int ratingDriving;
    // Can Robot Cross Defenses (options) (3 = N/A, disregarded by computer side)
    // 0 = crossed, 1 = avoided, 2 = failed
    int attemptPortcullis = 3;
    int attemptChevalDeFrise = 3;
    int attemptMoat = 3;
    int attemptRamparts = 3;
    int attemptDrawbridge = 3;
    int attemptRockWall = 3;
    int attemptSallyPort = 3;
    int attemptRoughTerrain = 3;
    int attemptLowBar = 3;
    // Got Stuck on defenses (0 = false and 1 = true)
    int stuckPortcullis = 0;
    int stuckChevalDeFrise = 0;
    int stuckMoat = 0;
    int stuckRamparts = 0;
    int stuckDrawbridge = 0;
    int stuckRockWall = 0;
    int stuckSallyPort = 0;
    int stuckRoughTerrain = 0;
    int stuckLowBar = 0;
    // Necessary items for Spinner pickName
    String[] names = new String[]{"Megan", "Rakesh", "Jameel", "Dhruv", "Lucas", "Drew", "Shreya",
            "Vanshika", "Elise", "Isha", "Paige", "Lydia", "Aliza", "Sarthak", "Max V.", "Dan K.",
            "Eric A.", "Jason T.", "Wonjae", "David T.", "Aidan", "Matt S.", "Peter", "Kyle",
            "Jason C.", "Alec", "John", "Jake", "Ben", "Mohamed", "Max P.", "Nick", "Veronica",
            "Chris", "Other"};
    ArrayAdapter<String> pickNameAdapter;
    // Necessary items for Spinner rateShooting
    String[] ratings = new String[]{"0", "1", "2", "3", "4", "5"};
    ArrayAdapter<String> rateShootingAdapter;
    // Necessary item for Spinner rateDriving (uses String[] ratings)
    ArrayAdapter<String> rateDrivingAdapter;
    // the number of times the user has pressed "Not Present" in the current form. Press 7 times to
    // save a form as Not Present.
    int timesPressed = 0;

    // The function of these items, it's a secret...
    // (tells the app that bluetooth transfer was unsuccessful)
    TextView lblTeleop;
    int timesClicked = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Auto generated code
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initiateForm();

    } // End onCreate

    /**
     * Checks whether there are required fields missing in the current form.
     * @return ready: true if the form is ready to save, false if required fields are missing.
     */
    public boolean isFormReady() {
        boolean ready = true;
        String teamNum = txtTeam.getText().toString();
        String matchNum = txtMatch.getText().toString();
        if (teamNum.length() < 2) ready = false;
        if (matchNum.length() < 1) ready = false;
        return ready;
    } // End isFormReady

    /**
     * Saves the current form. In actuality, it concatenates the current form to a String containing
     * all forms pending, until transfer.
     * @param present: false if the robot of the current form is not present in the current match.
     * @return true if the save was successful.
     */
    public boolean saveForm(boolean present) {
        timesPressed = 0;
        String form;
        if (present) form = prepareForm();
        else form = notPresentForm();
        if (firstForm) {
            forms = form;
            firstForm = false;
            formsPending = 1;
        } else {
            forms += form;
            formsPending++;
        }
        return true;
    } // End saveForm

    /**
     * Formats the current form into the Not Present format, acknowledging that this robot was not
     * present during this match. Forms are delimited by double bars (||), items are delimited by
     * single bars (|), and database item IDs following form items are delimited by a single comma
     * (,).
     * @return form: String containing the form items pre-formatted for storage in the database.
     */
    public String notPresentForm() {
        String form = "";

        // Header
        form += String.valueOf(TABLET_NUMBER) + "|";
        form += scoutName + "|";
        form += txtTeam.getText().toString() + "|";
        form += txtMatch.getText().toString() + "|";

        form += ItemIDs.Present + ",0";

        // End of the form
        form += "||";

        return form;
    } // End notPresentForm

    /**
     * Formats the current form into the database-defined format for storage. Forms are delimited
     * by double bars (||), items are delimited by single bars (|), and database item IDs following
     * form items are delimited by a single comma (,).
     * @return form: String containing the form items pre-formatted for storage in the database.
     */
    public String prepareForm() {

        String form = "";

        // Header
        form += String.valueOf(TABLET_NUMBER) + "|";
        form += scoutName + "|";
        form += txtTeam.getText().toString() + "|";
        form += "1|";
        form += txtMatch.getText().toString() + "|";
        if (txtScore.getText().toString().equals("")) form += ItemIDs.Score + "," + " |";
        else form += ItemIDs.Score + "," + txtScore.getText().toString() + "|";

        // Auto
        form += ItemIDs.Autoreachesoverwalls + "," + reachOuterWorks + "|";
        form += ItemIDs.Autocrossesouterworks + "," + autoCross + "|";
        form += ItemIDs.Autoshooting + "," + autoShoot + "|";

        // Teleop
        form += ItemIDs.Canshoot + "," + canShoot + "|";
        form += ItemIDs.Canclimb + "," + canClimb + "|";
        form += ItemIDs.ShootsHigh + "," + highGoal + "|";
        form += ItemIDs.ShootsLow + "," + lowGoal + "|";
        form += ItemIDs.Rateshooting + "," + ratingShooting + "|";
        form += ItemIDs.Ratedriving + "," + ratingDriving + "|";

        // Can Robot Cross Defenses?
        // Options
        form += ItemIDs.Attemptportcullis + "," + attemptPortcullis + "|";
        form += ItemIDs.AttemptChevalDeFrise + "," + attemptChevalDeFrise + "|";
        form += ItemIDs.AttemptMoat + "," + attemptMoat + "|";
        form += ItemIDs.AttemptRamparts + "," + attemptRamparts + "|";
        form += ItemIDs.AttemptDrawbridge + "," + attemptDrawbridge + "|";
        form += ItemIDs.AttemptRockWall + "," + attemptRockWall + "|";
        form += ItemIDs.AttemptSallyPort + "," + attemptSallyPort + "|";
        form += ItemIDs.AttemptRoughTerrain + "," + attemptRoughTerrain + "|";
        form += ItemIDs.AttemptLowBar + "," + attemptLowBar + "|";
        // Times Crossed
        if (txtPortcullis.getText().toString().equals(""))
            form += ItemIDs.TimescrossedPortcullis + "," + " |";
        else
            form += ItemIDs.TimescrossedPortcullis + "," + txtPortcullis.getText().toString() + "|";
        if (txtChevalDeFrise.getText().toString().equals(""))
            form += ItemIDs.TimescrossedChevalDeFrise + "," + " |";
        else
            form += ItemIDs.TimescrossedChevalDeFrise + "," + txtChevalDeFrise.getText().toString()
                    + "|";
        if (txtMoat.getText().toString().equals("")) form += ItemIDs.TimescrossedMoat + "," + " |";
        else form += ItemIDs.TimescrossedMoat + "," + txtMoat.getText().toString() + "|";
        if (txtRamparts.getText().toString().equals(""))
            form += ItemIDs.TimescrossedRamparts + "," + " |";
        else form += ItemIDs.TimescrossedRamparts + "," + txtRamparts.getText().toString() + "|";
        if (txtDrawbridge.getText().toString().equals(""))
            form += ItemIDs.TimescrossedDrawbridge + "," + " |";
        else
            form += ItemIDs.TimescrossedDrawbridge + "," + txtDrawbridge.getText().toString() + "|";
        if (txtRockWall.getText().toString().equals(""))
            form += ItemIDs.TimescrossedRockWall + "," + " |";
        else form += ItemIDs.TimescrossedRockWall + "," + txtRockWall.getText().toString() + "|";
        if (txtSallyPort.getText().toString().equals(""))
            form += ItemIDs.TimescrossedSallyPort + "," + " |";
        else form += ItemIDs.TimescrossedSallyPort + "," + txtSallyPort.getText().toString() + "|";
        if (txtRoughTerrain.getText().toString().equals(""))
            form += ItemIDs.TimescrossedRoughTerrain + "," + " |";
        else
            form += ItemIDs.TimescrossedRoughTerrain + "," + txtRoughTerrain.getText().toString() +
                    "|";
        if (txtLowBar.getText().toString().equals(""))
            form += ItemIDs.TimescrossedLowBar + "," + " |";
        else form += ItemIDs.TimescrossedLowBar + "," + txtLowBar.getText().toString() + "|";
        if (txtShooting.getText().toString().equals("")) form += ItemIDs.TimesShot + "," + " |";
        else form += ItemIDs.TimesShot + "," + txtShooting.getText().toString() + "|";
        // Got Stuck
        form += ItemIDs.GotstuckPortcullis + "," + stuckPortcullis + "|";
        form += ItemIDs.GotstuckChevalDeFrise + "," + stuckChevalDeFrise + "|";
        form += ItemIDs.GotstuckMoat + "," + stuckMoat + "|";
        form += ItemIDs.GotstuckRamparts + "," + stuckRamparts + "|";
        form += ItemIDs.GotstuckDrawbridge + "," + stuckDrawbridge + "|";
        form += ItemIDs.GotstuckRockWall + "," + stuckRockWall + "|";
        form += ItemIDs.GotstuckSallyPort + "," + stuckSallyPort + "|";
        form += ItemIDs.GotstuckRoughTerrain + "," + stuckRoughTerrain + "|";
        form += ItemIDs.GotstuckLowBar + "," + stuckLowBar + "|";

        // Footer
        if (txtComments.getText().toString().equals("")) form += ItemIDs.Comments + "," + " ";
        else form += ItemIDs.Comments + "," + txtComments.getText().toString();

        // End of the form
        form += "||";

        return form;

    }  // End prepareForm

    /**
     * Resets all components of the form.
     */
    public void resetForm() {
        // Header
        txtTeam.setText("");
        txtMatch.setText("");
        txtScore.setText("");

        // Auto
        chkReachOuterWorks.setChecked(false);
        chkCrossAuto.setChecked(false);
        // chkShootAuto is unchecked upon unchecking of chkCanShoot

        // Teleop
        chkCanShoot.setChecked(false);
        chkCanClimb.setChecked(false);
        chkHighGoal.setChecked(false);
        chkLowGoal.setChecked(false);
        rateShooting.setAdapter(rateShootingAdapter);
        rateDriving.setAdapter(rateDrivingAdapter);

        // Can Robot Cross Defenses?
        // Options
        grpPortcullis.clearCheck();
        grpChevalDeFrise.clearCheck();
        grpMoat.clearCheck();
        grpRamparts.clearCheck();
        grpDrawbridge.clearCheck();
        grpRockWall.clearCheck();
        grpSallyPort.clearCheck();
        grpRoughTerrain.clearCheck();
        grpLowBar.clearCheck();
        // TimesCrossed
        txtPortcullis.setText("");
        txtChevalDeFrise.setText("");
        txtMoat.setText("");
        txtRamparts.setText("");
        txtDrawbridge.setText("");
        txtRockWall.setText("");
        txtSallyPort.setText("");
        txtRoughTerrain.setText("");
        txtLowBar.setText("");
        txtShooting.setText("");
        // Got Stuck
        chkPortcullis.setChecked(false);
        chkChevalDeFrise.setChecked(false);
        chkMoat.setChecked(false);
        chkRamparts.setChecked(false);
        chkDrawbridge.setChecked(false);
        chkRockWall.setChecked(false);
        chkSallyPort.setChecked(false);
        chkRoughTerrain.setChecked(false);
        chkLowBar.setChecked(false);

        txtComments.setText("");
    } // End resetForm

    /**
     * Saves all pending forms into a bulk file. This is the first time the forms are saved in
     * storage rather than RAM.
     * @return false is the file save was unsuccessful, true otherwise.
     */
    public boolean saveFile() {
        try {
            FileOutputStream fos;
            fos = openFileOutput(fileName, Context.MODE_WORLD_READABLE);
            fos.write(forms.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    } // End saveFile

    /**
     * Call on the device's bluetooth activity in order to transfer a bulk file with all pending
     * forms. The bluetooth activity returns a result, but it only indicates finalization of the
     * intent used to start it, not whether the file was sent successfully or not, therefore the
     * result is ignored. This method also does some cleanup (resets the form, the forms pending).
     */
    public void btTransfer() {
        resetForm();

        File file = getFileStreamPath(fileName);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

        PackageManager pm = getPackageManager();
        List appsList = pm.queryIntentActivities(intent, 0);
        if (appsList.size() > 0) {
            String packageName = null;
            String className = null;
            boolean found = false;
            for (int i = 0; i < appsList.size(); i++) {
                ResolveInfo info = (ResolveInfo) appsList.get(i);
                packageName = info.activityInfo.packageName;
                if (packageName.equals("com.android.bluetooth")) {
                    className = info.activityInfo.name;
                    found = true;
                    break;// found
                }
            }

            if (!found) {
                Toast.makeText(this, "Not found!", Toast.LENGTH_SHORT).show();
            } else {
                intent.setClassName(packageName, className);
                startActivityForResult(intent, 1);
                lblFormsPending.setText("0 Form(s) Pending");
                firstForm = true;
            }
        }
    } // End btTransfer

    /**
     * Initiate all components of the Password Dialog. This dialog is loaded by switching the
     * content view of MainActivity. This dialog was a last-minute solution to a sensitivity issue
     * with the tablets used as dummy collectors, where touch events would be registered randomly
     * without any actual touches to the screen.
     */
    public void initiateDialog() {

        txtPassword = (EditText) findViewById(R.id.txtPassword);

        // If the password is correct, switch back to the form content view.
        btnGo = (Button) findViewById(R.id.btnGo);
        btnGo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (txtPassword.getText().toString().equals("15973")) {
                    setContentView(R.layout.activity_main);
                    initiateForm();
                    prepareForTransfer();
                } else {
                    setContentView(R.layout.activity_main);
                    initiateForm();
                    Toast.makeText(getApplicationContext(),
                            "Incorrect Password! Form(s) NOT transferred.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setContentView(R.layout.activity_main);
                initiateForm();
                Toast.makeText(getApplicationContext(), "Form(s) NOT transferred.",
                        Toast.LENGTH_SHORT).show();
            }
        });

    } // End initiateDialog

    /**
     * Initiate all components of the form.
     */
    public void initiateForm() {

        grpPortcullis = (RadioGroup) findViewById(R.id.grpPortcullis);
        grpPortcullis.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                switch (itemID) {
                    case R.id.crossedPortcullis:
                        attemptPortcullis = 0;
                        break;
                    case R.id.avoidedPortcullis:
                        attemptPortcullis = 1;
                        break;
                    case R.id.failedPortcullis:
                        attemptPortcullis = 2;
                        break;
                    case R.id.NAPortcullis:
                        attemptPortcullis = 3;
                        break;
                }
            }
        });
        grpChevalDeFrise = (RadioGroup) findViewById(R.id.grpChevalDeFrise);
        grpChevalDeFrise.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                switch (itemID) {
                    case R.id.crossedChevalDeFrise:
                        attemptChevalDeFrise = 0;
                        break;
                    case R.id.avoidedChevalDeFrise:
                        attemptChevalDeFrise = 1;
                        break;
                    case R.id.failedChevalDeFrise:
                        attemptChevalDeFrise = 2;
                        break;
                    case R.id.NAChevalDeFrise:
                        attemptChevalDeFrise = 3;
                        break;
                }
            }
        });
        grpMoat = (RadioGroup) findViewById(R.id.grpMoat);
        grpMoat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                switch (itemID) {
                    case R.id.crossedMoat:
                        attemptMoat = 0;
                        break;
                    case R.id.avoidedMoat:
                        attemptMoat = 1;
                        break;
                    case R.id.failedMoat:
                        attemptMoat = 2;
                        break;
                    case R.id.NAMoat:
                        attemptMoat = 3;
                        break;
                }
            }
        });
        grpRamparts = (RadioGroup) findViewById(R.id.grpRamparts);
        grpRamparts.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                switch (itemID) {
                    case R.id.crossedRamparts:
                        attemptRamparts = 0;
                        break;
                    case R.id.avoidedRamparts:
                        attemptRamparts = 1;
                        break;
                    case R.id.failedRamparts:
                        attemptRamparts = 2;
                        break;
                    case R.id.NARamparts:
                        attemptRamparts = 3;
                        break;
                }
            }
        });
        grpDrawbridge = (RadioGroup) findViewById(R.id.grpDrawbridge);
        grpDrawbridge.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                switch (itemID) {
                    case R.id.crossedDrawbridge:
                        attemptDrawbridge = 0;
                        break;
                    case R.id.avoidedDrawbridge:
                        attemptDrawbridge = 1;
                        break;
                    case R.id.failedDrawbridge:
                        attemptDrawbridge = 2;
                        break;
                    case R.id.NADrawbridge:
                        attemptDrawbridge = 3;
                        break;
                }
            }
        });
        grpRockWall = (RadioGroup) findViewById(R.id.grpRockWall);
        grpRockWall.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                switch (itemID) {
                    case R.id.crossedRockWall:
                        attemptRockWall = 0;
                        break;
                    case R.id.avoidedRockWall:
                        attemptRockWall = 1;
                        break;
                    case R.id.failedRockWall:
                        attemptRockWall = 2;
                        break;
                    case R.id.NARockWall:
                        attemptRockWall = 3;
                        break;
                }
            }
        });
        grpSallyPort = (RadioGroup) findViewById(R.id.grpSallyPort);
        grpSallyPort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                switch (itemID) {
                    case R.id.crossedSallyPort:
                        attemptSallyPort = 0;
                        break;
                    case R.id.avoidedSallyPort:
                        attemptSallyPort = 1;
                        break;
                    case R.id.failedSallyPort:
                        attemptSallyPort = 2;
                        break;
                    case R.id.NASallyPort:
                        attemptSallyPort = 3;
                        break;
                }
            }
        });
        grpRoughTerrain = (RadioGroup) findViewById(R.id.grpRoughTerrain);
        grpRoughTerrain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                switch (itemID) {
                    case R.id.crossedRoughTerrain:
                        attemptRoughTerrain = 0;
                        break;
                    case R.id.avoidedRoughTerrain:
                        attemptRoughTerrain = 1;
                        break;
                    case R.id.failedRoughTerrain:
                        attemptRoughTerrain = 2;
                        break;
                    case R.id.NARoughTerrain:
                        attemptRoughTerrain = 3;
                        break;
                }
            }
        });
        // Low Bar is always present
        grpLowBar = (RadioGroup) findViewById(R.id.grpLowBar);
        grpLowBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                switch (itemID) {
                    case R.id.crossedLowBar:
                        attemptLowBar = 0;
                        break;
                    case R.id.avoidedLowBar:
                        attemptLowBar = 1;
                        break;
                    case R.id.failedLowBar:
                        attemptLowBar = 2;
                        break;
                }
            }
        });

        failedPortcullis = (RadioButton) findViewById(R.id.failedPortcullis);
        failedChevalDeFrise = (RadioButton) findViewById(R.id.failedChevalDeFrise);
        failedMoat = (RadioButton) findViewById(R.id.failedMoat);
        failedRamparts = (RadioButton) findViewById(R.id.failedRamparts);
        failedDrawbridge = (RadioButton) findViewById(R.id.failedDrawbridge);
        failedRockWall = (RadioButton) findViewById(R.id.failedRockWall);
        failedSallyPort = (RadioButton) findViewById(R.id.failedSallyPort);
        failedRoughTerrain = (RadioButton) findViewById(R.id.failedRoughTerrain);
        failedLowBar = (RadioButton) findViewById(R.id.failedLowBar);

        chkReachOuterWorks = (CheckBox) findViewById(R.id.chkReachOuterWorks);
        chkReachOuterWorks.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton chkBox, boolean checked) {
                if (checked) reachOuterWorks = 1;
                else reachOuterWorks = 0;
            }
        });
        chkCrossAuto = (CheckBox) findViewById(R.id.chkCrossAuto);
        chkCrossAuto.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton chkBox, boolean checked) {
                if (checked) autoCross = 1;
                else autoCross = 0;
            }
        });
        // If the robot can shoot in auto, it can shoot.
        chkShootAuto = (CheckBox) findViewById(R.id.chkShootAuto);
        chkShootAuto.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton chkBox, boolean checked) {
                if (checked) {
                    chkCanShoot.setChecked(true);
                    autoShoot = 1;
                } else autoShoot = 0;
            }
        });
        // If the robot cannot shoot, it cannot shoot in auto.
        chkCanShoot = (CheckBox) findViewById(R.id.chkCanShoot);
        chkCanShoot.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton chkBox, boolean checked) {
                if (!checked) {
                    chkShootAuto.setChecked(false);
                    canShoot = 1;
                } else canShoot = 0;
            }
        });
        chkCanClimb = (CheckBox) findViewById(R.id.chkCanClimb);
        chkCanClimb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton chkBox, boolean checked) {
                if (checked) canClimb = 1;
                else canClimb = 0;
            }
        });
        chkLowGoal = (CheckBox) findViewById(R.id.chkLowGoal);
        chkLowGoal.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton chkBox, boolean checked) {
                if (!checked) {
                    chkLowGoal.setChecked(false);
                    lowGoal = 1;
                } else lowGoal = 0;
            }
        });
        chkHighGoal = (CheckBox) findViewById(R.id.chkHighGoal);
        chkHighGoal.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton chkBox, boolean checked) {
                if (!checked) {
                    chkHighGoal.setChecked(false);
                    highGoal = 1;
                } else highGoal = 0;
            }
        });

        // If they got stuck on a defense, they have failed that defense.

        chkPortcullis = (CheckBox) findViewById(R.id.chkPortcullis);
        chkPortcullis.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton chkBox, boolean checked) {
                if (checked) {
                    failedPortcullis.setChecked(true);
                    stuckPortcullis = 1;
                } else stuckRockWall = 0;
            }
        });
        chkChevalDeFrise = (CheckBox) findViewById(R.id.chkChevalDeFrise);
        chkChevalDeFrise.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton chkBox, boolean checked) {
                if (checked) {
                    failedChevalDeFrise.setChecked(true);
                    stuckChevalDeFrise = 1;
                } else stuckChevalDeFrise = 0;
            }
        });
        chkMoat = (CheckBox) findViewById(R.id.chkMoat);
        chkMoat.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton chkBox, boolean checked) {
                if (checked) {
                    failedMoat.setChecked(true);
                    stuckMoat = 1;
                } else stuckMoat = 0;
            }
        });
        chkRamparts = (CheckBox) findViewById(R.id.chkRamparts);
        chkRamparts.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton chkBox, boolean checked) {
                if (checked) {
                    failedRamparts.setChecked(true);
                    stuckRamparts = 1;
                } else stuckRamparts = 0;
            }
        });
        chkDrawbridge = (CheckBox) findViewById(R.id.chkDrawbridge);
        chkDrawbridge.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton chkBox, boolean checked) {
                if (checked) {
                    failedDrawbridge.setChecked(true);
                    stuckDrawbridge = 1;
                } else stuckDrawbridge = 0;
            }
        });
        chkRockWall = (CheckBox) findViewById(R.id.chkRockWall);
        chkRockWall.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton chkBox, boolean checked) {
                if (checked) {
                    failedRockWall.setChecked(true);
                    stuckRockWall = 1;
                } else stuckRockWall = 0;
            }
        });
        chkSallyPort = (CheckBox) findViewById(R.id.chkSallyPort);
        chkSallyPort.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton chkBox, boolean checked) {
                if (checked) {
                    failedSallyPort.setChecked(true);
                    stuckSallyPort = 1;
                } else stuckSallyPort = 0;
            }
        });
        chkRoughTerrain = (CheckBox) findViewById(R.id.chkRoughTerrain);
        chkRoughTerrain.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton chkBox, boolean checked) {
                if (checked) {
                    failedRoughTerrain.setChecked(true);
                    stuckRoughTerrain = 1;
                } else stuckRoughTerrain = 0;
            }
        });
        chkLowBar = (CheckBox) findViewById(R.id.chkLowBar);
        chkLowBar.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton chkBox, boolean checked) {
                if (checked) {
                    failedLowBar.setChecked(true);
                    stuckLowBar = 1;
                } else stuckLowBar = 0;
            }
        });

        txtTeam = (EditText) findViewById(R.id.txtTeam);
        txtMatch = (EditText) findViewById(R.id.txtMatch);
        txtScore = (EditText) findViewById(R.id.txtScore);

        txtPortcullis = (EditText) findViewById(R.id.txtPortcullis);
        txtChevalDeFrise = (EditText) findViewById(R.id.txtChevalDeFrise);
        txtMoat = (EditText) findViewById(R.id.txtMoat);
        txtRamparts = (EditText) findViewById(R.id.txtRamparts);
        txtDrawbridge = (EditText) findViewById(R.id.txtDrawbridge);
        txtRockWall = (EditText) findViewById(R.id.txtRockWall);
        txtSallyPort = (EditText) findViewById(R.id.txtSallyPort);
        txtRoughTerrain = (EditText) findViewById(R.id.txtRoughTerrain);
        txtLowBar = (EditText) findViewById(R.id.txtLowBar);
        txtShooting = (EditText) findViewById(R.id.txtShooting);

        txtComments = (EditText) findViewById(R.id.txtComments);

        lblFormsPending = (TextView) findViewById(R.id.lblFormsPending);
        lblFormsPending.setText(formsPending + " Form(s) Pending");

        pickName = (Spinner) findViewById(R.id.pickName);
        pickNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        pickName.setAdapter(pickNameAdapter);
        pickName.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View spinner, int position,
                                       long id) {
                scoutName = (String) adapterView.getItemAtPosition(position);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rateShooting = (Spinner) findViewById(R.id.rateShooting);
        rateShootingAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, ratings);
        rateShooting.setAdapter(rateShootingAdapter);
        rateShooting.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View spinner, int position,
                                       long id) {
                ratingShooting = position;
                if (position != 0) chkCanShoot.setChecked(true);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rateDriving = (Spinner) findViewById(R.id.rateDriving);
        rateDrivingAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                ratings);
        rateDriving.setAdapter(rateDrivingAdapter);
        rateDriving.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View spinner, int position,
                                       long id) {
                ratingDriving = position;
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // The textboxes holding the number of defenses crossed are initialized with "". The number
        // of times a robot has crossed a defense cannot go negative.

        addPortcullis = (Button) findViewById(R.id.addPortcullis);
        addPortcullis.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtPortcullis.getText().toString().equals("")) num = 1;
                else {
                    num = Integer.parseInt(txtPortcullis.getText().toString());
                    num += 1;
                }
                txtPortcullis.setText(String.valueOf(num));
            }
        });

        subPortcullis = (Button) findViewById(R.id.subPortcullis);
        subPortcullis.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtPortcullis.getText().toString().equals("") ||
                        txtPortcullis.getText().toString().equals("0")) txtPortcullis.setText("0");
                else {
                    num = Integer.parseInt(txtPortcullis.getText().toString());
                    num -= 1;
                    txtPortcullis.setText(String.valueOf(num));
                }
            }
        });

        addChevalDeFrise = (Button) findViewById(R.id.addChevalDeFrise);
        addChevalDeFrise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtChevalDeFrise.getText().toString().equals("")) num = 1;
                else {
                    num = Integer.parseInt(txtChevalDeFrise.getText().toString());
                    num += 1;
                }
                txtChevalDeFrise.setText(String.valueOf(num));
            }
        });

        subChevalDeFrise = (Button) findViewById(R.id.subChevalDeFrise);
        subChevalDeFrise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtChevalDeFrise.getText().toString().equals("") ||
                        txtChevalDeFrise.getText().toString().equals("0")) {
                    txtChevalDeFrise.setText("0");
                } else {
                    num = Integer.parseInt(txtChevalDeFrise.getText().toString());
                    num -= 1;
                    txtChevalDeFrise.setText(String.valueOf(num));
                }
            }
        });

        addMoat = (Button) findViewById(R.id.addMoat);
        addMoat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtMoat.getText().toString().equals("")) num = 1;
                else {
                    num = Integer.parseInt(txtMoat.getText().toString());
                    num += 1;
                }
                txtMoat.setText(String.valueOf(num));
            }
        });

        subMoat = (Button) findViewById(R.id.subMoat);
        subMoat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtMoat.getText().toString().equals("") ||
                        txtMoat.getText().toString().equals("0")) txtMoat.setText("0");
                else {
                    num = Integer.parseInt(txtMoat.getText().toString());
                    num -= 1;
                    txtMoat.setText(String.valueOf(num));
                }
            }
        });

        addRamparts = (Button) findViewById(R.id.addRamparts);
        addRamparts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtRamparts.getText().toString().equals("")) num = 1;
                else {
                    num = Integer.parseInt(txtRamparts.getText().toString());
                    num += 1;
                }
                txtRamparts.setText(String.valueOf(num));
            }
        });

        subRamparts = (Button) findViewById(R.id.subRamparts);
        subRamparts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtRamparts.getText().toString().equals("") ||
                        txtRamparts.getText().toString().equals("0")) txtRamparts.setText("0");
                else {
                    num = Integer.parseInt(txtRamparts.getText().toString());
                    num -= 1;
                    txtRamparts.setText(String.valueOf(num));
                }
            }
        });

        addDrawbridge = (Button) findViewById(R.id.addDrawbridge);
        addDrawbridge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtDrawbridge.getText().toString().equals("")) num = 1;
                else {
                    num = Integer.parseInt(txtDrawbridge.getText().toString());
                    num += 1;
                }
                txtDrawbridge.setText(String.valueOf(num));
            }
        });

        subDrawbridge = (Button) findViewById(R.id.subDrawbridge);
        subDrawbridge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtDrawbridge.getText().toString().equals("") ||
                        txtDrawbridge.getText().toString().equals("0")) txtDrawbridge.setText("0");
                else {
                    num = Integer.parseInt(txtDrawbridge.getText().toString());
                    num -= 1;
                    txtDrawbridge.setText(String.valueOf(num));
                }
            }
        });

        addRockWall = (Button) findViewById(R.id.addRockWall);
        addRockWall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtRockWall.getText().toString().equals("")) num = 1;
                else {
                    num = Integer.parseInt(txtRockWall.getText().toString());
                    num += 1;
                }
                txtRockWall.setText(String.valueOf(num));
            }
        });

        subRockWall = (Button) findViewById(R.id.subRockWall);
        subRockWall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtRockWall.getText().toString().equals("") ||
                        txtRockWall.getText().toString().equals("0")) txtRockWall.setText("0");
                else {
                    num = Integer.parseInt(txtRockWall.getText().toString());
                    num -= 1;
                    txtRockWall.setText(String.valueOf(num));
                }
            }
        });

        addSallyPort = (Button) findViewById(R.id.addSallyPort);
        addSallyPort.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtSallyPort.getText().toString().equals("")) num = 1;
                else {
                    num = Integer.parseInt(txtSallyPort.getText().toString());
                    num += 1;
                }
                txtSallyPort.setText(String.valueOf(num));
            }
        });

        subSallyPort = (Button) findViewById(R.id.subSallyPort);
        subSallyPort.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtSallyPort.getText().toString().equals("") ||
                        txtSallyPort.getText().toString().equals("0")) txtSallyPort.setText("0");
                else {
                    num = Integer.parseInt(txtSallyPort.getText().toString());
                    num -= 1;
                    txtSallyPort.setText(String.valueOf(num));
                }
            }
        });

        addRoughTerrain = (Button) findViewById(R.id.addRoughTerrain);
        addRoughTerrain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtRoughTerrain.getText().toString().equals("")) num = 1;
                else {
                    num = Integer.parseInt(txtRoughTerrain.getText().toString());
                    num += 1;
                }
                txtRoughTerrain.setText(String.valueOf(num));
            }
        });

        subRoughTerrain = (Button) findViewById(R.id.subRoughTerrain);
        subRoughTerrain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtRoughTerrain.getText().toString().equals("") ||
                        txtRoughTerrain.getText().toString().equals("0")) {
                    txtRoughTerrain.setText("0");
                } else {
                    num = Integer.parseInt(txtRoughTerrain.getText().toString());
                    num -= 1;
                    txtRoughTerrain.setText(String.valueOf(num));
                }
            }
        });

        addLowBar = (Button) findViewById(R.id.addLowBar);
        addLowBar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtLowBar.getText().toString().equals("")) num = 1;
                else {
                    num = Integer.parseInt(txtLowBar.getText().toString());
                    num += 1;
                }
                txtLowBar.setText(String.valueOf(num));
            }
        });

        subLowBar = (Button) findViewById(R.id.subLowBar);
        subLowBar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtLowBar.getText().toString().equals("") ||
                        txtLowBar.getText().toString().equals("0")) txtLowBar.setText("0");
                else {
                    num = Integer.parseInt(txtLowBar.getText().toString());
                    num -= 1;
                    txtLowBar.setText(String.valueOf(num));
                }
            }
        });

        addShooting = (Button) findViewById(R.id.addShooting);
        addShooting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtShooting.getText().toString().equals("")) num = 1;
                else {
                    num = Integer.parseInt(txtShooting.getText().toString());
                    num += 1;
                }
                txtShooting.setText(String.valueOf(num));
            }
        });

        subShooting = (Button) findViewById(R.id.subShooting);
        subShooting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int num;
                if (txtShooting.getText().toString().equals("") ||
                        txtShooting.getText().toString().equals("0")) txtShooting.setText("0");
                else {
                    num = Integer.parseInt(txtShooting.getText().toString());
                    num -= 1;
                    txtShooting.setText(String.valueOf(num));
                }
            }
        });

        // Here we have the random touch events issue again. This button saves the current for as a
        // Not Present form, indicating the current robot is not present. If this button is pressed
        // a total of 7 times, then save the form as Not Present.
        btnNotPresent = (Button) findViewById(R.id.btnNotPresent);
        btnNotPresent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timesPressed++;
                Context context = getApplicationContext();
                CharSequence text;
                if (timesPressed == 7) {
                    if (isFormReady()) {
                        if (saveForm(false)) {
                            text = "Robot is Not Present. FORM SAVED.";
                            timesPressed = 0;
                        } else text = "FORM NOT SAVED: I/O problem encountered. TALK TO LUCAS!!";
                    } else text = "FORM NOT SAVED: required fields are missing.";
                    timesPressed = 0;
                } else
                    text = "You've pressed 'NotPresent' " + timesPressed +
                            " times.\nPress7 times in total to declare this robot Not Present.";
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(context, text, duration).show();
            }
        });

        // This button triggers and manages the saving of the current form.
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Context context = getApplicationContext();
                CharSequence text;
                int duration = Toast.LENGTH_SHORT;
                if (isFormReady()) {
                    if (saveForm(true)) {
                        text = "FORM SAVED";
                        resetForm();
                        lblFormsPending.setText(formsPending + " Form(s) Pending");
                        firstForm = false;
                    } else text = "FORM NOT SAVED: I/O problem encountered. TALK TO LUCAS!!";
                } else text = "FORM NOT SAVED: required fields are missing.";
                Toast.makeText(context, text, duration).show();
            }
        });

        // This button triggers and manages the bluetooth transfer of the pending forms.
        btnTransfer = (Button) findViewById(R.id.btnTransfer);
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (formsPending == 0)
                    Toast.makeText(com.example.lucas.scouting08.MainActivity.this,
                            "No forms to transfer!", Toast.LENGTH_SHORT).show();
                else {
                    if (isFormReady()) {
                        CharSequence text;
                        if (saveForm(true)) {
                            text = "FORM SAVED";
                            resetForm();
                            lblFormsPending.setText(formsPending + " Form(s) Pending");
                            firstForm = false;
                        } else text = "FORM NOT SAVED: I/O problem encountered. TALK TO LUCAS!!";
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    }
                    setContentView(R.layout.password_dialog);
                    initiateDialog();
                }
            }
        });

        // This label, when pressed 7 times, will infporm the app that the bluetooth transfer has
        // not been successful, preventing the app form overwriting the string containing the forms.
        lblTeleop = (TextView) findViewById(R.id.lblTeleop);
        lblTeleop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (timesClicked == 7) {
                    lblFormsPending.setText(formsPending + " Form(s) Pending");
                    if (formsPending > 0) firstForm = false;
                    Toast.makeText(com.example.lucas.scouting08.MainActivity.this, "STATE RESET",
                            Toast.LENGTH_SHORT).show();
                    timesClicked = 0;
                } else timesClicked++;
            }
        });
    } // End initiateForm

    /**
     * Checks to see whether bluetooth transfer is possible before proceeding with the transfer.
     * The Bluetooth radio must be enabled, and the master PC must be paired to this device.
     */
    public void prepareForTransfer() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("LUCASPC")) {
                    MainActivity.device = device;
                    if (saveFile()) btTransfer();
                }
            }
        }
        Toast.makeText(getApplicationContext(), "ATTEMPTING TRANSFER", Toast.LENGTH_SHORT).show();
    }  // End End prepareForTransfer

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

}