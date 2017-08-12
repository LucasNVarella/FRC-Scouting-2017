/**************************************************************************************************
 * MatchScouting2.0
 * @Author Lucas Varella
 * @Author Dan K.
 *
 * This app is meant to be a dummy scouting data collector, partially specific to STEAMWORKS.
 * It is meant to transfer scouting forms to another machine, a master machine whose purpose is to
 * maintain and organize the data from all dummy data collectors. The information from the scouting
 * forms is pre-formatted for storage in a specific database schema, included in this project.
 *
 * The main content view is the scouting form, fit specifically for a 10.1" horizontal screen.
 * In 4048, we utilized Android laptops for the luxury of a physical keyboard (recommended).
 * Each form pertains to a match, only one form may be in use at any one time, and forms cannot be
 * reopened after they have been saved. This is to preserve data integrity: if there is a need to
 * modify, scale, or perform any other transformations on the data, it can be done so on the master
 * machine.
 *
 * All pending forms are stored locally to a file until the next transfer. Once the user completes
 * the process to make a transfer, the text file is transferred via bluetooth to the master machine.
 *
 * The bluetooth transfer activity on Android does not indicate to the calling activity whether the
 * bluetooth transfer was complete or not. Thus, an archiving system was implemented to allow users
 * to transfer past files, in case a particular bluetooth transfer is unsuccessful.
 *
 * Not only do the data collectors send information to the master machine, the master machine must
 * send preliminary information to the data collectors. Each data collector must receive a config
 * file, which will contain the teams currently at the competition, the names of the scouts at work,
 * any keywords the form should not accept on text fields, and the name of the laptop to which it
 * should send files over Bluetooth.
 *************************************************************************************************/

package com.example.lucas.matchscouting20;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity {

    // VISUAL COMPONENTS

    private TextView lblFormsPending;

    private Button btnFormsPendingSave;
    private Button btnFormsPendingTransfer;

    private Button btnAutoShooterSub;
    private Button btnAutoShooterAdd;
    private Button btnTeleopGearAttemptSub;
    private Button btnTeleopGearAttemptAdd;
    private Button btnTeleopGearMakeSub;
    private Button btnTeleopGearMakeAdd;

    private AutoCompleteTextView txtTeamNumber;
    private AutoCompleteTextView txtMatchNumber;
    private EditText txtAutoShooterMakes;
    private EditText txtTeleopGearAttempt;
    private EditText txtTeleopGearMake;
    private EditText txtEvalScoreAlly;
    private EditText txtCommentsBox;

    private Spinner chooseName;

    private CheckBox chkShow;
    private CheckBox chkAutoGearSuccess;
    private CheckBox chkAutoHighGoal;
    private CheckBox chkAutoMovingBaseline;
    private CheckBox chkAutoLowGoal;
    private CheckBox chkAutoGear;
    private CheckBox chkTeleopShootingHigh;
    private CheckBox chkTeleopShootingLow;
    private CheckBox chkTeleopClimbingSucc;
    private CheckBox chkTeleopClimbingPower;
    private CheckBox chkEvalBreak;
    private CheckBox chkTeleopRotorHandleGear;
    private CheckBox chkEvalFoul;
    private CheckBox chkEvalCardYellow;
    private CheckBox chkEvalCardRed;
    private CheckBox chkTeleopClimbingAttempt;

    private RadioGroup grpAutoGearLocation;
    private RadioGroup grpTeleopStrategyOptions;
    private RadioGroup grpTeleopShootingOptions1;
    private RadioGroup grpTeleopShootingRate;
    private RadioGroup grpTeleopShootingAccuracy1;
    private RadioGroup grpTeleopShootingAccuracy2;
    private RadioGroup grpTeleopShootingOptions2;
    private RadioGroup grpTeleopRotorPilot;
    private RadioGroup grpTeleopRotorOptions1;
    private RadioGroup grpTeleopRotorOptions2;
    private RadioGroup grpTeleopClimbing;
    private RadioGroup grpEvalDriverRate;

    private RadioButton radTeleopShootingNumber1;
    private RadioButton radTeleopShootingNumber2;
    private RadioButton radTeleopShootingNumber3;
    private RadioButton radTeleopShootingNumber4;
    private RadioButton radTeleopShootingAcc1;
    private RadioButton radTeleopShootingAcc2;
    private RadioButton radTeleopShootingAcc3;
    private RadioButton radTeleopShootingAcc4;
    private RadioButton radTeleopRotorNumber0;
    private RadioButton radTeleopRotorNumber1;
    private RadioButton radTeleopRotorNumber2;
    private RadioButton radTeleopRotorNumber3;
    private RadioButton radTeleopRotorNumber4;
    private RadioButton radAutoGearLeftSide;
    private RadioButton radAutoGearRightSide;
    private RadioButton radAutoGearMiddle;
    private RadioButton radAutoGearNA;
    private RadioButton radTeleopStrategyGear;
    private RadioButton radTeleopStrategyShoot;
    private RadioButton radTeleopStrategyDefense;
    private RadioButton radTeleopShootingRateSlow;
    private RadioButton radTeleopShootingRateFast;
    private RadioButton radTeleopShootingRateNA;
    private RadioButton radTeleopRotorPilotGood;
    private RadioButton radTeleopRotorPilotBad;
    private RadioButton radTeleopRotorPilotNA;
    private RadioButton radTeleopClimbingSlow;
    private RadioButton radTeleopClimbingFast;
    private RadioButton radTeleopClimbingNA;
    private RadioButton radEvalDriverRate5;
    private RadioButton radEvalDriverRate4;
    private RadioButton radEvalDriverRate3;
    private RadioButton radEvalDriverRate2;
    private RadioButton radEvalDriverRate1;

    // OTHER VARIABLES

    // Necessary items for data transfer
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static BluetoothDevice device = null;
    // The PC from which to receive and which to send - this is meant to be overwritten by the
    // config file. The user must set up the data collector to receive the file from the desired
    // computer.
    private static String pcCompanion = "LUCASPC";
    // The folder where incoming bluetooth files are stored. We changed this to the Downloads folder
    // in our collectors.
    final static String BLUETOOTH_FOLDER_PATH = "/storage/emulated/0/Download/";

    // These are the potential intentions for having opened an alert dialog.
    private enum Action {
        NONE, SAVE_FORM, CHOOSE_TRANSFER_ACTION, TRANSFER_FORMS, TRANSFER_LAST_FORMS,
        RECEIVE_CONFIG, CHECK_TRANSFER, WARNING_TEAMNUM, WARNING_KEYWORD, TRANSFER_ALL_ARCHIVES
    }
    private Action actionRequested = Action.NONE;

    private static final String STATE_SAVE_FILE = "stateSave.txt";
    private static final String TEMP_FILE = "tempFile.txt";
    private static final String ARCHIVE_FILE = "archiveFile.txt";
    private static final String CONFIG_FILE = "configFile.txt";
    private static int archivedFiles = 0;

    // Alert Dialog items
    private static String MESSAGE = "";
    private static String POSITIVE_BUTTON = "";
    private static String NEGATIVE_BUTTON = "";
    private static String NEUTRAL_BUTTON = "";

    private boolean firstForm = true;
    private int formsPending = 0;

    // The names of the available scouts. Meant to be overridden by the config file.
    private String[] names = {"Scout"};
    // The teams competing in this competition. Meant to be overridden by the config file.
    private String[] teams = {"4048"};
    // The keywords that must be excluded from text fields. Meant to be overridden by the config
    // file.
    private String[] keywords = {"hots"};

    // The name of the current scout.
    private String scoutName;
    // The identifier number given to this machine.
    private int tabletNum;

    // The following booleans are use to maintain multiple RadioGroups in sync. Essentially, when
    // we have two RadioGroups that are associated, we want to deselect the contents of one when the
    // other is selected. Each boolean represents a pair of RadioGroups.
    private boolean shootingOptionsClicked = false;
    private boolean shootingAccClicked = false;
    private boolean rotorsClicked = false;

    // All the records in the form.
    private Record[] records = new Record[29];
    private Record present;
    private Record canClimb;
    private Record comments;
    private Record rateDriving;
    private Record shootsHigh;
    private Record shootsLow;
    private Record autoHandleGears;
    private Record autoGearSuccess;
    private Record autoGearPlacement;
    private Record autoShootsHigh;
    private Record autoShootsLow;
    private Record autoShotMakes;
    private Record autoCrossBaseline;
    private Record strategy;
    private Record shootingSpeed;
    private Record shotsMade;
    private Record shooterAccuracy;
    private Record handleGears;
    private Record pilotPerformance;
    private Record gearAttempts;
    private Record gearMakes;
    private Record rotorsSpinning;
    private Record climbSuccess;
    private Record staysPutWhenPowerCut;
    private Record climbingSpeed;
    private Record didTheyBreakDown;
    private Record foulPoints;
    private Record yellowCard;
    private Record redCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initRecords();
        if (checkConfigFile()) {
            initConfigs();
            initLayout();
            initSaveState();
            initArchiveSystem();
        } else {
            initLayout();
            initSaveState();
            initArchiveSystem();
            actionRequested = Action.RECEIVE_CONFIG;
            showAlertDialog("A configuration file from the master computer is required to continue."
                    + "\nPlease transfer the file to this machine.", "I've transferred the file");
        }
    }

    private void initConfigs() {
        String message = "There has been an I/O issue!\nCONFIG FAILED";
        try {
            File file = new File(getFilesDir().getAbsolutePath(), CONFIG_FILE);
            if (!file.exists()) {
                message = "There has been an I/O issue!\n" +
                        "CONFIG FILE DOES NOT EXIST (PAST FILE CHECK)";
                throw new IOException();
            } else {
                ArrayList<String> contents = new ArrayList<>();
                String str;
                BufferedReader reader = new BufferedReader(new FileReader(file));
                while (!((str = reader.readLine()) == null)) contents.add(str);
                reader.close();

                tabletNum = Integer.parseInt(contents.get(0));
                pcCompanion = contents.get(1);
                if (pcCompanion.contains(Form.ID_DELIMITER)) throw new InputMismatchException();
                names = contents.get(2).split(Form.ID_DELIMITER);
                teams = contents.get(3).split(Form.ID_DELIMITER);
                keywords = contents.get(4).split(Form.ID_DELIMITER);
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } catch (IndexOutOfBoundsException | InputMismatchException e) {
            message = "CONFIG FILE FORMATTED INCORRECTLY.\n"
                    + "PLEASE FORMAT THE CONFIG FILE CORRECTLY";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    public void initArchiveSystem() {
        boolean done = false;
        while (!done) {
            String fileName = ARCHIVE_FILE.split("\\.")[0] + archivedFiles
                    + ARCHIVE_FILE.split("\\.")[1];
            File file = new File(getFilesDir().getAbsolutePath(), fileName);
            if (!file.exists()) done = true;
            else archivedFiles++;
        }
    }

    private void initRecords() {
        records[0] = present = new Record(null, MatchForm.Items.PRESENT.getId());
        records[1] = canClimb = new Record(null, MatchForm.Items.CAN_CLIMB.getId());
        records[2] = comments = new Record(null, MatchForm.Items.COMMENTS.getId());
        records[3] = rateDriving = new Record(null, MatchForm.Items.RATE_DRIVING.getId());
        records[4] = shootsHigh = new Record(null, MatchForm.Items.SHOOTS_HIGH.getId());
        records[5] = shootsLow = new Record(null, MatchForm.Items.SHOOTS_LOW.getId());
        records[6] = autoHandleGears = new Record(null, MatchForm.Items.AUTO_HANDLE_GEARS.getId());
        records[7] = autoGearSuccess = new Record(null, MatchForm.Items.AUTO_GEAR_SUCCESS.getId());
        records[8] = autoGearPlacement = new Record(null,
                MatchForm.Items.AUTO_GEAR_PLACEMENT.getId());
        records[9] = autoShootsHigh = new Record(null, MatchForm.Items.AUTO_SHOOTS_HIGH.getId());
        records[10] = autoShootsLow = new Record(null, MatchForm.Items.AUTO_SHOOTS_LOW.getId());
        records[11] = autoShotMakes = new Record(null, MatchForm.Items.AUTO_SHOT_MAKES.getId());
        records[12] = autoCrossBaseline = new Record(null,
                MatchForm.Items.AUTO_CROSS_BASELINE.getId());
        records[13] = strategy = new Record(null, MatchForm.Items.STRATEGY.getId());
        records[14] = shootingSpeed = new Record(null, MatchForm.Items.SHOOTING_SPEED.getId());
        records[15] = shotsMade = new Record(null, MatchForm.Items.SHOTS_MADE.getId());
        records[16] = shooterAccuracy = new Record(null, MatchForm.Items.SHOOTER_ACCURACY.getId());
        records[17] = handleGears = new Record(null, MatchForm.Items.HANDLE_GEARS.getId());
        records[18] = pilotPerformance = new Record(null,
                MatchForm.Items.PILOT_PERFORMANCE.getId());
        records[19] = gearAttempts = new Record(null, MatchForm.Items.GEAR_ATTEMPTS.getId());
        records[20] = gearMakes = new Record(null, MatchForm.Items.GEAR_MAKES.getId());
        records[21] = rotorsSpinning = new Record(null, MatchForm.Items.ROTORS_SPINNING.getId());
        records[22] = climbSuccess = new Record(null, MatchForm.Items.CLIMB_SUCCESS.getId());
        records[23] = staysPutWhenPowerCut = new Record(null,
                MatchForm.Items.STAYS_PUT_WHEN_POWER_CUT.getId());
        records[24] = climbingSpeed = new Record(null, MatchForm.Items.CLIMBING_SPEED.getId());
        records[25] = didTheyBreakDown = new Record(null,
                MatchForm.Items.DID_THEY_BREAK_DOWN.getId());
        records[26] = foulPoints = new Record(null, MatchForm.Items.FOUL_POINTS.getId());
        records[27] = yellowCard = new Record(null, MatchForm.Items.YELLOW_CARD.getId());
        records[28] = redCard = new Record(null, MatchForm.Items.RED_CARD.getId());
    }

    private void initLayout() {
        chooseName = (Spinner) findViewById(R.id.chooseName);
        ArrayAdapter<String> pickNameAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        chooseName.setAdapter(pickNameAdapter);
        chooseName.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View spinner, int position,
                                       long id) {
                scoutName = (String) adapterView.getItemAtPosition(position);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });

        radTeleopShootingNumber1 = (RadioButton) findViewById(R.id.radTeleopShootingNumber1);
        radTeleopShootingNumber2 = (RadioButton) findViewById(R.id.radTeleopShootingNumber2);
        radTeleopShootingNumber3 = (RadioButton) findViewById(R.id.radTeleopShootingNumber3);
        radTeleopShootingNumber4 = (RadioButton) findViewById(R.id.radTeleopShootingNumber4);
        radTeleopShootingAcc1 = (RadioButton) findViewById(R.id.radTeleopShootingAcc1);
        radTeleopShootingAcc2 = (RadioButton) findViewById(R.id.radTeleopShootingAcc2);
        radTeleopShootingAcc3 = (RadioButton) findViewById(R.id.radTeleopShootingAcc3);
        radTeleopShootingAcc4 = (RadioButton) findViewById(R.id.radTeleopShootingAcc4);
        radTeleopRotorNumber0 = (RadioButton) findViewById(R.id.radTeleopRotorNumber0);
        radTeleopRotorNumber1 = (RadioButton) findViewById(R.id.radTeleopRotorNumber1);
        radTeleopRotorNumber2 = (RadioButton) findViewById(R.id.radTeleopRotorNumber2);
        radTeleopRotorNumber3 = (RadioButton) findViewById(R.id.radTeleopRotorNumber3);
        radTeleopRotorNumber4 = (RadioButton) findViewById(R.id.radTeleopRotorNumber4);
        radAutoGearLeftSide = (RadioButton) findViewById(R.id.radAutoGearLeftSide);
        radAutoGearRightSide = (RadioButton) findViewById(R.id.radAutoGearRightSide);
        radAutoGearMiddle = (RadioButton) findViewById(R.id.radAutoGearMiddle);
        radAutoGearNA = (RadioButton) findViewById(R.id.radAutoGearNA);
        radTeleopStrategyGear = (RadioButton) findViewById(R.id.radTeleopStrategyGear);
        radTeleopStrategyShoot = (RadioButton) findViewById(R.id.radTeleopStrategyShoot);
        radTeleopStrategyDefense = (RadioButton) findViewById(R.id.radTeleopStrategyDefense);
        radTeleopShootingRateSlow = (RadioButton) findViewById(R.id.radTeleopShootingRateSlow);
        radTeleopShootingRateFast = (RadioButton) findViewById(R.id.radTeleopShootingRateFast);
        radTeleopShootingRateNA = (RadioButton) findViewById(R.id.radTeleopShootingRateNA);
        radTeleopRotorPilotGood = (RadioButton) findViewById(R.id.radTeleopRotorPilotGood);
        radTeleopRotorPilotBad = (RadioButton) findViewById(R.id.radTeleopRotorPilotBad);
        radTeleopRotorPilotNA = (RadioButton) findViewById(R.id.radTeleopRotorPilotNA);
        radTeleopClimbingSlow = (RadioButton) findViewById(R.id.radTeleopClimbingSlow);
        radTeleopClimbingFast = (RadioButton) findViewById(R.id.radTeleopClimbingFast);
        radTeleopClimbingNA = (RadioButton) findViewById(R.id.radTeleopClimbingNA);
        radEvalDriverRate5 = (RadioButton) findViewById(R.id.radEvalDriverRate5);
        radEvalDriverRate4 = (RadioButton) findViewById(R.id.radEvalDriverRate4);
        radEvalDriverRate3 = (RadioButton) findViewById(R.id.radEvalDriverRate3);
        radEvalDriverRate2 = (RadioButton) findViewById(R.id.radEvalDriverRate2);
        radEvalDriverRate1 = (RadioButton) findViewById(R.id.radEvalDriverRate1);

        grpAutoGearLocation = (RadioGroup) findViewById(R.id.grpAutoGearLocation);
        grpAutoGearLocation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                switch (itemID) {
                    case R.id.radAutoGearLeftSide:
                        autoGearPlacement.setValue(MatchForm.AUTO_GEAR_PLACEMENT.LEFT.toString());
                        break;
                    case R.id.radAutoGearMiddle:
                        autoGearPlacement.setValue(MatchForm.AUTO_GEAR_PLACEMENT.CENTER.toString());
                        break;
                    case R.id.radAutoGearRightSide:
                        autoGearPlacement.setValue(MatchForm.AUTO_GEAR_PLACEMENT.RIGHT.toString());
                        break;
                    case R.id.radAutoGearNA:
                        autoGearPlacement.setValue(MatchForm.AUTO_GEAR_PLACEMENT.NA.toString());
                }
            }
        });

        grpTeleopStrategyOptions = (RadioGroup) findViewById(R.id.grpTeleopStrategyOptions);
        grpTeleopStrategyOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                switch (itemID) {
                    case R.id.radTeleopStrategyGear:
                        strategy.setValue(MatchForm.STRATEGY.GEAR.toString());
                        break;
                    case R.id.radTeleopStrategyShoot:
                        strategy.setValue(MatchForm.STRATEGY.FUEL.toString());
                        break;
                    case R.id.radTeleopStrategyDefense:
                        strategy.setValue(MatchForm.STRATEGY.DEFENSE.toString());
                }
            }
        });

        grpTeleopShootingOptions1 = (RadioGroup) findViewById(R.id.grpTeleopShootingOptions1);
        grpTeleopShootingOptions1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                if (!shootingOptionsClicked) {
                    shootingOptionsClicked = true;
                    grpTeleopShootingOptions2.clearCheck();
                    switch (itemID) {
                        case R.id.radTeleopShootingNumber1:
                            shotsMade.setValue(MatchForm.SHOTS_MADE.FROM_0_TO_30.toString());
                            break;
                        case R.id.radTeleopShootingNumber2:
                            shotsMade.setValue(MatchForm.SHOTS_MADE.FROM_31_TO_60.toString());
                    }
                    shootingOptionsClicked = false;
                }
            }
        });

        grpTeleopShootingOptions2 = (RadioGroup) findViewById(R.id.grpTeleopShootingOptions2);
        grpTeleopShootingOptions2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                if (!shootingOptionsClicked) {
                    shootingOptionsClicked = true;
                    grpTeleopShootingOptions1.clearCheck();
                    switch (itemID) {
                        case R.id.radTeleopShootingNumber3:
                            shotsMade.setValue(MatchForm.SHOTS_MADE.FROM_61_TO_90.toString());
                            break;
                        case R.id.radTeleopShootingNumber4:
                            shotsMade.setValue(MatchForm.SHOTS_MADE.FROM_90.toString());
                    }
                    shootingOptionsClicked = false;
                }
            }
        });

        grpTeleopShootingRate = (RadioGroup) findViewById(R.id.grpTeleopShootingRate);
        grpTeleopShootingRate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                switch (itemID) {
                    case R.id.radTeleopShootingRateFast:
                        shootingSpeed.setValue(MatchForm.SHOOTING_SPEED.FAST.toString());
                        break;
                    case R.id.radTeleopShootingRateMed:
                        shootingSpeed.setValue(MatchForm.SHOOTING_SPEED.MEDIUM.toString());
                        break;
                    case R.id.radTeleopShootingRateSlow:
                        shootingSpeed.setValue(MatchForm.SHOOTING_SPEED.SLOW.toString());
                }
            }
        });

        grpTeleopShootingAccuracy1 = (RadioGroup) findViewById(R.id.grpTeleopShootingAccuracy1);
        grpTeleopShootingAccuracy1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                if (!shootingAccClicked) {
                    shootingAccClicked = true;
                    grpTeleopShootingAccuracy2.clearCheck();
                    switch (itemID) {
                        case R.id.radTeleopShootingAcc1:
                            shooterAccuracy.setValue(MatchForm.SHOOTER_ACCURACY.FROM_0_TO_25.toString());
                            break;
                        case R.id.radTeleopShootingAcc2:
                            shooterAccuracy.setValue(MatchForm.SHOOTER_ACCURACY.FROM_26_TO_50.toString());
                    }
                    shootingAccClicked = false;
                }
            }
        });

        grpTeleopShootingAccuracy2 = (RadioGroup) findViewById(R.id.grpTeleopShootingAccuracy2);
        grpTeleopShootingAccuracy2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                if (!shootingAccClicked) {
                    shootingAccClicked = true;
                    grpTeleopShootingAccuracy1.clearCheck();
                    switch (itemID) {
                        case R.id.radTeleopShootingAcc3:
                            shooterAccuracy.setValue(MatchForm.SHOOTER_ACCURACY.FROM_51_TO_75.toString());
                            break;
                        case R.id.radTeleopShootingAcc4:
                            shooterAccuracy.setValue(MatchForm.SHOOTER_ACCURACY.FROM_76_TO_100.toString());
                    }
                    shootingAccClicked = false;
                }
            }
        });

        grpTeleopRotorPilot = (RadioGroup) findViewById(R.id.grpTeleopRotorPilot);
        grpTeleopRotorPilot.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                switch (itemID) {
                    case R.id.radTeleopRotorPilotBad:
                        pilotPerformance.setValue(MatchForm.PILOT_PERFORMANCE.BAD.toString());
                        break;
                    case R.id.radTeleopRotorPilotGood:
                        pilotPerformance.setValue(MatchForm.PILOT_PERFORMANCE.GOOD.toString());
                        break;
                    case R.id.radTeleopRotorPilotNA:
                        pilotPerformance.setValue(MatchForm.PILOT_PERFORMANCE.NA.toString());
                }
            }
        });

        grpTeleopRotorOptions1 = (RadioGroup) findViewById(R.id.grpTeleopRotorOptions1);
        grpTeleopRotorOptions1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                if (!rotorsClicked) {
                    rotorsClicked = true;
                    grpTeleopRotorOptions2.clearCheck();
                    rotorsSpinning.setValue(((RadioButton) (findViewById(itemID))).getText()
                            .toString());
                    rotorsClicked = false;
                }
            }
        });

        grpTeleopRotorOptions2 = (RadioGroup) findViewById(R.id.grpTeleopRotorOptions2);
        grpTeleopRotorOptions2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                if (!rotorsClicked) {
                    rotorsClicked = true;
                    grpTeleopRotorOptions1.clearCheck();
                    rotorsSpinning.setValue(((RadioButton) (findViewById(itemID))).getText()
                            .toString());
                    rotorsClicked = false;
                }
            }
        });

        grpTeleopClimbing = (RadioGroup) findViewById(R.id.grpTeleopClimbing);
        grpTeleopClimbing.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                switch (itemID) {
                    case R.id.radTeleopClimbingSlow:
                        climbingSpeed.setValue(MatchForm.CLIMBING_SPEED.SLOW.toString());
                        break;
                    case R.id.radTeleopClimbingFast:
                        climbingSpeed.setValue(MatchForm.CLIMBING_SPEED.FAST.toString());
                        break;
                    case R.id.radTeleopClimbingNA:
                        climbingSpeed.setValue(MatchForm.CLIMBING_SPEED.NA.toString());
                }
            }
        });

        grpEvalDriverRate = (RadioGroup) findViewById(R.id.grpEvalDriverRate);
        grpEvalDriverRate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                switch (itemID) {
                    case R.id.radEvalDriverRate1:
                        rateDriving.setValue("1");
                        break;
                    case R.id.radEvalDriverRate2:
                        rateDriving.setValue("2");
                        break;
                    case R.id.radEvalDriverRate3:
                        rateDriving.setValue("3");
                        break;
                    case R.id.radEvalDriverRate4:
                        rateDriving.setValue("4");
                        break;
                    case R.id.radEvalDriverRate5:
                        rateDriving.setValue("5");
                }
            }
        });

        chkShow = (CheckBox) findViewById(R.id.chkShow);
        chkShow.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (isChecked) value = "0";
                present.setValue(value);
            }
        });

        chkAutoGearSuccess = (CheckBox) findViewById(R.id.chkAutoGearSuccess);
        chkAutoGearSuccess.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                autoGearSuccess.setValue(value);
            }
        });

        chkAutoHighGoal = (CheckBox) findViewById(R.id.chkAutoHighGoal);
        chkAutoHighGoal.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                autoShootsHigh.setValue(value);
            }
        });

        chkAutoMovingBaseline = (CheckBox) findViewById(R.id.chkAutoMovingBaseline);
        chkAutoMovingBaseline.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                autoCrossBaseline.setValue(value);
            }
        });

        chkAutoLowGoal = (CheckBox) findViewById(R.id.chkAutoLowGoal);
        chkAutoLowGoal.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                autoShootsLow.setValue(value);
            }
        });

        chkAutoGear = (CheckBox) findViewById(R.id.chkAutoGear);
        chkAutoGear.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                autoHandleGears.setValue(value);
            }
        });

        chkTeleopShootingHigh = (CheckBox) findViewById(R.id.chkTeleopShootingHigh);
        chkTeleopShootingHigh.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                shootsHigh.setValue(value);
            }
        });

        chkTeleopShootingLow = (CheckBox) findViewById(R.id.chkTeleopShootingLow);
        chkTeleopShootingLow.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                shootsLow.setValue(value);
            }
        });

        chkTeleopClimbingSucc = (CheckBox) findViewById(R.id.chkTeleopClimbingSucc);
        chkTeleopClimbingSucc.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                climbSuccess.setValue(value);
            }
        });

        chkTeleopClimbingPower = (CheckBox) findViewById(R.id.chkTeleopClimbingPower);
        chkTeleopClimbingPower.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                staysPutWhenPowerCut.setValue(value);
            }
        });

        chkEvalBreak = (CheckBox) findViewById(R.id.chkEvalBreak);
        chkEvalBreak.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                didTheyBreakDown.setValue(value);
            }
        });

        chkTeleopRotorHandleGear = (CheckBox) findViewById(R.id.chkTeleopRotorHandleGear);
        chkTeleopRotorHandleGear.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                handleGears.setValue(value);
            }
        });

        chkEvalFoul = (CheckBox) findViewById(R.id.chkEvalFoul);
        chkEvalFoul.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                foulPoints.setValue(value);
            }
        });

        chkEvalCardYellow = (CheckBox) findViewById(R.id.chkEvalCardYellow);
        chkEvalCardYellow.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                yellowCard.setValue(value);
            }
        });

        chkEvalCardRed = (CheckBox) findViewById(R.id.chkEvalCardRed);
        chkEvalCardRed.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                redCard.setValue(value);
            }
        });

        chkTeleopClimbingAttempt = (CheckBox) findViewById(R.id.chkTeleopClimbingAttempt);
        chkTeleopClimbingAttempt.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                canClimb.setValue(value);
            }
        });

        btnAutoShooterSub = (Button) findViewById(R.id.btnAutoShooterSub);
        btnAutoShooterSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtAutoShooterMakes.getText().toString();
                int num = 0;
                if (!str.isEmpty()) {
                    num = Integer.parseInt(str);
                    if (num != 0) num--;
                }
                txtAutoShooterMakes.setText(String.valueOf(num));
            }
        });

        btnAutoShooterAdd = (Button) findViewById(R.id.btnAutoShooterAdd);
        btnAutoShooterAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtAutoShooterMakes.getText().toString();
                int num = 1;
                if (!str.isEmpty()) num = Integer.parseInt(str) + 1;
                txtAutoShooterMakes.setText(String.valueOf(num));
            }
        });

        btnTeleopGearAttemptSub = (Button) findViewById(R.id.btnTeleopGearAttemptSub);
        btnTeleopGearAttemptSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtTeleopGearAttempt.getText().toString();
                int num = 0;
                if (!str.isEmpty()) {
                    num = Integer.parseInt(str);
                    if (num != 0) num--;
                }
                txtTeleopGearAttempt.setText(String.valueOf(num));
            }
        });

        btnTeleopGearAttemptAdd = (Button) findViewById(R.id.btnTeleopGearAttemptAdd);
        btnTeleopGearAttemptAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtTeleopGearAttempt.getText().toString();
                int num = 1;
                if (!str.isEmpty()) num = Integer.parseInt(str) + 1;
                txtTeleopGearAttempt.setText(String.valueOf(num));
            }
        });

        btnTeleopGearMakeSub = (Button) findViewById(R.id.btnTeleopGearMakeSub);
        btnTeleopGearMakeSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtTeleopGearMake.getText().toString();
                int num = 0;
                if (!str.isEmpty()) {
                    num = Integer.parseInt(str);
                    if (num != 0) num--;
                }
                txtTeleopGearMake.setText(String.valueOf(num));
            }
        });

        btnTeleopGearMakeAdd = (Button) findViewById(R.id.btnTeleopGearMakeAdd);
        btnTeleopGearMakeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtTeleopGearMake.getText().toString();
                int num = 1;
                if (!str.isEmpty()) num = Integer.parseInt(str) + 1;
                txtTeleopGearMake.setText(String.valueOf(num));
            }
        });

        lblFormsPending = (TextView) findViewById(R.id.lblFormsPending);

        txtTeamNumber = (AutoCompleteTextView) findViewById(R.id.txtTeamNumber);
        ArrayAdapter<String> txtTeamAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, teams);
        txtTeamNumber.setAdapter(txtTeamAdapter);

        txtMatchNumber = (AutoCompleteTextView) findViewById(R.id.txtMatchNumber);

        txtAutoShooterMakes = (EditText) findViewById(R.id.txtAutoShooterMakes);
        txtAutoShooterMakes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (autoShotMakes.getValue() == null) {
                    if (s.length() > 0)
                        autoShotMakes.setValue(txtAutoShooterMakes.getText().toString());
                } else {
                    if (!autoShotMakes.getValue().equals(txtAutoShooterMakes.getText().toString()))
                        if (s.length() > 0)
                            autoShotMakes.setValue(txtAutoShooterMakes.getText().toString());
                        else autoShotMakes.setValue(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtTeleopGearAttempt = (EditText) findViewById(R.id.txtTeleopGearAttempt);
        txtTeleopGearAttempt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (gearAttempts.getValue() == null) {
                    if (s.length() > 0)
                        gearAttempts.setValue(txtTeleopGearAttempt.getText().toString());
                } else {
                    if (!gearAttempts.getValue().equals(txtTeleopGearAttempt.getText().toString()))
                        if (s.length() > 0)
                            gearAttempts.setValue(txtTeleopGearAttempt.getText().toString());
                    else gearAttempts.setValue(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtTeleopGearMake = (EditText) findViewById(R.id.txtTeleopGearMake);
        txtTeleopGearMake.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (gearMakes.getValue() == null) {
                    if (s.length() > 0)
                        gearMakes.setValue(txtTeleopGearMake.getText().toString());
                } else {
                    if (!gearMakes.getValue().equals(txtTeleopGearMake.getText().toString()))
                        if (s.length() > 0)
                            gearMakes.setValue(txtTeleopGearMake.getText().toString());
                    else gearMakes.setValue(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtEvalScoreAlly = (EditText) findViewById(R.id.txtEvalScoreAlly);

        txtCommentsBox = (EditText) findViewById(R.id.txtCommentsBox);
        txtCommentsBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (comments.getValue() == null) {
                    if (s.length() > 0) comments.setValue(s.toString()
                            .replaceAll(Form.ID_DELIMITER, "~"));
                } else {
                    if (!comments.getValue().equals(s)) if (s.length() > 0)
                        comments.setValue(s.toString().replaceAll(Form.ID_DELIMITER, "~"));
                    else comments.setValue(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnFormsPendingSave = (Button) findViewById(R.id.btnFormsPendingSave);
        btnFormsPendingSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readyToSave()) {
                    actionRequested = Action.SAVE_FORM;
                    showAlertDialog("Are you sure you want to save?", "Yes", "No");
                }
            }
        });

        btnFormsPendingTransfer = (Button) findViewById(R.id.btnFormsPendingTransfer);
        btnFormsPendingTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionRequested = Action.TRANSFER_FORMS;
                showAlertDialog("Are you sure you want to transfer?", "Yes", "No");
            }
        });
        btnFormsPendingTransfer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                actionRequested = Action.CHOOSE_TRANSFER_ACTION;
                showAlertDialog("What do you want to do?", "Receive Config", "Retry last transfer",
                        "Transfer all archives");
                return false;
            }
        });
    }

    private void initSaveState() {
        try {
            File file = new File(getFilesDir().getAbsolutePath(), STATE_SAVE_FILE);
            if (!file.exists()) {
                if (!file.createNewFile()) throw new IOException();
            } else {
                String string = "";
                String str;
                BufferedReader reader = new BufferedReader(new FileReader(file));
                while (!((str = reader.readLine()) == null)) string += str;
                String[] items = string.split("\\" + Form.ITEM_DELIMITER);
                formsPending = Integer.parseInt(items[0]);
                if (formsPending > 0) firstForm = false;
                lblFormsPending.setText(formsPending + " Form(s) Pending");
                txtMatchNumber.setText(items[1]);
                txtTeamNumber.setText(items[2]);
                chooseName.setSelection(Integer.parseInt(items[3]));
                txtEvalScoreAlly.setText(items[4]);
                setState(items, 5);
                reader.close();
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "There has been an I/O issue! \n" +
                    "STATE RESTORE FAILED. LAST UNSAVED FORM LOST.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void setState(String[] records, int startingIndex) {
        // present
        if (records[startingIndex].split(Form.ID_DELIMITER)[1].equals("1"))
            chkShow.setChecked(true);
        // canClimb
        if (records[startingIndex+1].split(Form.ID_DELIMITER)[1].equals("1"))
            chkTeleopClimbingAttempt.setChecked(true);
        // comment
        txtCommentsBox.setText(records[startingIndex+2].split(Form.ID_DELIMITER)[1]
                .replace("null", "").replaceAll("~", ","));
        // rateDriving
        switch (records[startingIndex+3].split(Form.ID_DELIMITER)[1]) {
            case "1":
                radEvalDriverRate1.setChecked(true);
                break;
            case "2":
                radEvalDriverRate2.setChecked(true);
                break;
            case "3":
                radEvalDriverRate3.setChecked(true);
                break;
            case "4":
                radEvalDriverRate4.setChecked(true);
                break;
            case "5":
                radEvalDriverRate5.setChecked(true);
                break;
        }
        // shootsHigh
        if (records[startingIndex+4].split(Form.ID_DELIMITER)[1].equals("1"))
            chkTeleopShootingHigh.setChecked(true);
        // shootsLow
        if (records[startingIndex+5].split(Form.ID_DELIMITER)[1].equals("1"))
            chkTeleopShootingLow.setChecked(true);
        // autoHandlesGear
        if (records[startingIndex+6].split(Form.ID_DELIMITER)[1].equals("1"))
            chkAutoGear.setChecked(true);
        // autoGearSuccess
        if (records[startingIndex+7].split(Form.ID_DELIMITER)[1].equals("1"))
            chkAutoGearSuccess.setChecked(true);
        // autoGearPlacement
        String value = records[startingIndex+8].split(Form.ID_DELIMITER)[1];
        if (value.equals(MatchForm.AUTO_GEAR_PLACEMENT.LEFT.toString()))
            radAutoGearLeftSide.setChecked(true);
        else if (value.equals(MatchForm.AUTO_GEAR_PLACEMENT.RIGHT.toString()))
            radAutoGearRightSide.setChecked(true);
        else if (value.equals(MatchForm.AUTO_GEAR_PLACEMENT.CENTER.toString()))
            radAutoGearMiddle.setChecked(true);
        else if (value.equals(MatchForm.AUTO_GEAR_PLACEMENT.NA.toString()))
            radAutoGearNA.setChecked(true);
        // autoShootsHigh
        if (records[startingIndex+9].split(Form.ID_DELIMITER)[1].equals("1"))
            chkAutoHighGoal.setChecked(true);
        // autoShootsLow
        if (records[startingIndex+10].split(Form.ID_DELIMITER)[1].equals("1"))
            chkAutoLowGoal.setChecked(true);
        // comment
        txtAutoShooterMakes.setText(records[startingIndex+11].split(Form.ID_DELIMITER)[1]
                .replace("null", ""));
        // autoCrossBaseline
        if (records[startingIndex+12].split(Form.ID_DELIMITER)[1].equals("1"))
            chkAutoMovingBaseline.setChecked(true);
        // strategy
        value = records[startingIndex+13].split(Form.ID_DELIMITER)[1];
        if (value.equals(MatchForm.STRATEGY.GEAR.toString()))
            radTeleopStrategyGear.setChecked(true);
        else if (value.equals(MatchForm.STRATEGY.FUEL.toString()))
            radTeleopStrategyShoot.setChecked(true);
        else if (value.equals(MatchForm.STRATEGY.DEFENSE.toString()))
            radTeleopStrategyDefense.setChecked(true);
        // shootingSpeed
        value = records[startingIndex+14].split(Form.ID_DELIMITER)[1];
        if (value.equals(MatchForm.SHOOTING_SPEED.FAST.toString()))
            radTeleopShootingRateFast.setChecked(true);
        else if (value.equals(MatchForm.SHOOTING_SPEED.SLOW.toString()))
            radTeleopShootingRateSlow.setChecked(true);
        else if (value.equals(MatchForm.SHOOTING_SPEED.NA.toString()))
            radTeleopShootingRateNA.setChecked(true);
        // shotsMade
        value = records[startingIndex+15].split(Form.ID_DELIMITER)[1];
        if (value.equals(MatchForm.SHOTS_MADE.FROM_0_TO_30.toString()))
            radTeleopShootingNumber1.setChecked(true);
        else if (value.equals(MatchForm.SHOTS_MADE.FROM_31_TO_60.toString()))
            radTeleopShootingNumber2.setChecked(true);
        else if (value.equals(MatchForm.SHOTS_MADE.FROM_61_TO_90.toString()))
            radTeleopShootingNumber3.setChecked(true);
        else if (value.equals(MatchForm.SHOTS_MADE.FROM_90.toString()))
            radTeleopShootingNumber4.setChecked(true);
        // shooterAccuracy
        value = records[startingIndex+16].split(Form.ID_DELIMITER)[1];
        if (value.equals(MatchForm.SHOOTER_ACCURACY.FROM_0_TO_25.toString()))
            radTeleopShootingAcc1.setChecked(true);
        else if (value.equals(MatchForm.SHOOTER_ACCURACY.FROM_26_TO_50.toString()))
            radTeleopShootingAcc2.setChecked(true);
        else if (value.equals(MatchForm.SHOOTER_ACCURACY.FROM_51_TO_75.toString()))
            radTeleopShootingAcc3.setChecked(true);
        else if (value.equals(MatchForm.SHOOTER_ACCURACY.FROM_76_TO_100.toString()))
            radTeleopShootingAcc4.setChecked(true);
        // handleGears
        if (records[startingIndex+17].split(Form.ID_DELIMITER)[1].equals("1"))
            chkTeleopRotorHandleGear.setChecked(true);
        // pilotPerformance
        value = records[startingIndex+18].split(Form.ID_DELIMITER)[1];
        if (value.equals(MatchForm.PILOT_PERFORMANCE.GOOD.toString()))
            radTeleopRotorPilotGood.setChecked(true);
        else if (value.equals(MatchForm.PILOT_PERFORMANCE.BAD.toString()))
            radTeleopRotorPilotBad.setChecked(true);
        else if (value.equals(MatchForm.PILOT_PERFORMANCE.NA.toString()))
            radTeleopRotorPilotNA.setChecked(true);
        // gearAttempts
        txtTeleopGearAttempt.setText(records[startingIndex+19].split(Form.ID_DELIMITER)[1]
                .replace("null", ""));
        // gearMakes
        txtTeleopGearMake.setText(records[startingIndex+20].split(Form.ID_DELIMITER)[1]
                .replace("null", ""));
        // rotorsSpinning
        value = records[startingIndex+21].split(Form.ID_DELIMITER)[1];
        switch (value) {
            case "0":
                radTeleopRotorNumber0.setChecked(true);
                break;
            case "1":
                radTeleopRotorNumber1.setChecked(true);
                break;
            case "2":
                radTeleopRotorNumber2.setChecked(true);
                break;
            case "3":
                radTeleopRotorNumber3.setChecked(true);
                break;
            case "4":
                radTeleopRotorNumber4.setChecked(true);
                break;
        }
        // climbSuccess
        if (records[startingIndex+22].split(Form.ID_DELIMITER)[1].equals("1"))
            chkTeleopClimbingSucc.setChecked(true);
        // staysPutWhenPowerCut
        if (records[startingIndex+23].split(Form.ID_DELIMITER)[1].equals("1"))
            chkTeleopClimbingPower.setChecked(true);
        // climbingSpeed
        value = records[startingIndex+24].split(Form.ID_DELIMITER)[1];
        if (value.equals(MatchForm.CLIMBING_SPEED.FAST.toString()))
            radTeleopClimbingFast.setChecked(true);
        else if (value.equals(MatchForm.CLIMBING_SPEED.SLOW.toString()))
            radTeleopClimbingSlow.setChecked(true);
        else if (value.equals(MatchForm.CLIMBING_SPEED.NA.toString()))
            radTeleopClimbingNA.setChecked(true);
        // didTheyBreakDown
        if (records[startingIndex+25].split(Form.ID_DELIMITER)[1].equals("1"))
            chkEvalBreak.setChecked(true);
        // didTheyBreakDown
        if (records[startingIndex+26].split(Form.ID_DELIMITER)[1].equals("1"))
            chkEvalFoul.setChecked(true);
        // yellowCard
        if (records[startingIndex+27].split(Form.ID_DELIMITER)[1].equals("1"))
            chkEvalCardYellow.setChecked(true);
        // redCard
        if (records[startingIndex+28].split(Form.ID_DELIMITER)[1].equals("1"))
            chkEvalCardRed.setChecked(true);
    }

    private boolean readyToSave() {
        if (txtTeamNumber.getText().toString().isEmpty() ||
                txtMatchNumber.getText().toString().isEmpty() ||
                txtEvalScoreAlly.getText().toString().isEmpty()) {
            showAlertDialog("FORM NOT SAVED: required fields are missing.", "Ok");
            return false;
        }
        boolean done = false;
        boolean ok = false;
        int i = 0;
        while (!done) {
            if (i >= teams.length) done = true;
            else {
                if (txtTeamNumber.getText().toString().contains(teams[i])) {
                    done = true;
                    ok = true;
                } else i++;
            }
        }
        if (!ok) {
            showAlertDialog("Team " + txtTeamNumber.getText().toString()
                    + " is not competing.", "Ok");
            return false;
        }
        i = 0;
        done = false;
        while (!done) {
            if (i >= keywords.length) done = true;
            else {
                String text = txtCommentsBox.getText().toString().toLowerCase();
                if (text.contains(keywords[i])) {
                    int index = text.indexOf(keywords[i]);
                    while (index != -1) {
                        if (text.equals(keywords[i])) text = "";
                        else if ((index == 0) &&
                                (text.charAt(index+keywords[i].length()) == ' '))
                            text = text.substring(index+keywords[i].length());
                        else if ((text.charAt(index-1) == ' ') &&
                                (index+keywords[i].length() == text.length()))
                            text = text.substring(0, index);
                        else if ((text.charAt(index-1) == ' ') &&
                                (text.charAt(index+keywords[i].length()) == ' '))
                            text = text.substring(0, index) +
                                    text.substring(index+keywords[i].length());
                        index = text.indexOf(keywords[i], index+1);
                    }
                    txtCommentsBox.setText(text);
                }
                i++;
            }
        }
        return true;
    }

    private Form makeForm() {
        MatchForm form = new MatchForm(tabletNum,
                Integer.parseInt(txtTeamNumber.getText().toString()),
                Integer.parseInt(txtMatchNumber.getText().toString()), scoutName);
        for (Record record : records) if (record.getValue() != null) form.addRecord(record);
        return form;
    }

    private boolean saveForm() {
        String message = "There has been an I/O issue! FORM NOT SAVED.";
        try {
            File file = new File(getFilesDir().getAbsolutePath(), TEMP_FILE);
            if (!file.exists()) {
                if (!file.createNewFile()) throw new IOException();
            } else {
                if (firstForm) {
                    if (!file.delete()) {
                        message = "There has been an I/O issue! \n" +
                                "FAILED TO DELETE OLD TEMP FILE";
                        throw new IOException();
                    }
                    file.createNewFile();
                    firstForm = false;
                }
            }
            String content = "";
            String str;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (!((str = reader.readLine())== null)) content += str;
            reader.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            if (content.isEmpty()) writer.append(makeForm().toString());
            else writer.append(content + Form.FORM_DELIMITER + makeForm().toString());
            writer.close();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            e.printStackTrace();

            return false;
        }
        return true;
    }

    private void resetForm() {
        txtMatchNumber.setText("");
        txtTeamNumber.setText("");
        txtAutoShooterMakes.setText("");
        txtTeleopGearAttempt.setText("");
        txtTeleopGearMake.setText("");
        txtEvalScoreAlly.setText("");
        txtCommentsBox.setText("");
        resetRadiogroups();
        resetCheckboxes();
        initRecords();
        initLayout();
    }

    private void resetRadiogroups() {
        grpEvalDriverRate.setOnCheckedChangeListener(null);
        grpTeleopRotorOptions1.setOnCheckedChangeListener(null);
        grpTeleopRotorOptions2.setOnCheckedChangeListener(null);
        grpTeleopStrategyOptions.setOnCheckedChangeListener(null);
        grpTeleopShootingRate.setOnCheckedChangeListener(null);
        grpTeleopShootingOptions1.setOnCheckedChangeListener(null);
        grpTeleopShootingOptions2.setOnCheckedChangeListener(null);
        grpTeleopShootingAccuracy1.setOnCheckedChangeListener(null);
        grpTeleopShootingAccuracy2.setOnCheckedChangeListener(null);
        grpTeleopRotorPilot.setOnCheckedChangeListener(null);
        grpTeleopClimbing.setOnCheckedChangeListener(null);
        grpAutoGearLocation.setOnCheckedChangeListener(null);

        grpEvalDriverRate.clearCheck();
        grpTeleopRotorOptions1.clearCheck();
        grpTeleopRotorOptions2.clearCheck();
        grpTeleopStrategyOptions.clearCheck();
        grpTeleopShootingRate.clearCheck();
        grpTeleopShootingOptions1.clearCheck();
        grpTeleopShootingOptions2.clearCheck();
        grpTeleopShootingAccuracy1.clearCheck();
        grpTeleopShootingAccuracy2.clearCheck();
        grpTeleopRotorPilot.clearCheck();
        grpTeleopClimbing.clearCheck();
        grpAutoGearLocation.clearCheck();
    }

    private void resetCheckboxes() {
        chkEvalFoul.setOnCheckedChangeListener(null);
        chkEvalCardYellow.setOnCheckedChangeListener(null);
        chkEvalCardRed.setOnCheckedChangeListener(null);
        chkEvalBreak.setOnCheckedChangeListener(null);
        chkTeleopClimbingAttempt.setOnCheckedChangeListener(null);
        chkTeleopClimbingSucc.setOnCheckedChangeListener(null);
        chkTeleopClimbingPower.setOnCheckedChangeListener(null);
        chkTeleopClimbingAttempt.setOnCheckedChangeListener(null);
        chkTeleopClimbingSucc.setOnCheckedChangeListener(null);
        chkTeleopClimbingPower.setOnCheckedChangeListener(null);
        chkTeleopRotorHandleGear.setOnCheckedChangeListener(null);
        chkTeleopShootingHigh.setOnCheckedChangeListener(null);
        chkTeleopShootingLow.setOnCheckedChangeListener(null);
        chkAutoMovingBaseline.setOnCheckedChangeListener(null);
        chkAutoHighGoal.setOnCheckedChangeListener(null);
        chkAutoLowGoal.setOnCheckedChangeListener(null);
        chkShow.setOnCheckedChangeListener(null);
        chkAutoGear.setOnCheckedChangeListener(null);
        chkAutoGearSuccess.setOnCheckedChangeListener(null);

        chkEvalFoul.setChecked(false);
        chkEvalCardYellow.setChecked(false);
        chkEvalCardRed.setChecked(false);
        chkEvalBreak.setChecked(false);
        chkTeleopClimbingAttempt.setChecked(false);
        chkTeleopClimbingSucc.setChecked(false);
        chkTeleopClimbingPower.setChecked(false);
        chkTeleopClimbingAttempt.setChecked(false);
        chkTeleopClimbingSucc.setChecked(false);
        chkTeleopClimbingPower.setChecked(false);
        chkTeleopRotorHandleGear.setChecked(false);
        chkTeleopShootingHigh.setChecked(false);
        chkTeleopShootingLow.setChecked(false);
        chkAutoMovingBaseline.setChecked(false);
        chkAutoHighGoal.setChecked(false);
        chkAutoLowGoal.setChecked(false);
        chkShow.setChecked(false);
        chkAutoGear.setChecked(false);
        chkAutoGearSuccess.setChecked(false);
    }

    private void saveState() {
        try {
            File file = new File(getFilesDir().getAbsolutePath(), STATE_SAVE_FILE);
            if (!file.exists()) if (!file.createNewFile()) throw new IOException();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            // include forms pending, matchnum, teamnum, name, score
            String output = String.valueOf(formsPending) + Form.ITEM_DELIMITER +
                    txtMatchNumber.getText().toString() + Form.ITEM_DELIMITER +
                    txtTeamNumber.getText().toString() + Form.ITEM_DELIMITER +
                    chooseName.getSelectedItemPosition() + Form.ITEM_DELIMITER +
                    txtEvalScoreAlly.getText().toString();
            for (Record record : records) output += Form.ITEM_DELIMITER + record.toString();
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),
                    "There has been an I/O issue! Current Form lost...", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public String prepareFormTransfer(String filename) {
        String content = "";
        String message = "There has been an I/O issue!\nTRANSFER FAILED";
        try {
            File file = new File(getFilesDir().getAbsolutePath(), filename);
            if (!file.exists()) {
                message = "There has been an I/O issue!\n" +
                        "TRANSFER FAILED: " + filename + " DOES NOT EXIST.";
                throw new IOException();
            }
            String str;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (!((str = reader.readLine())== null)) content += str;
            reader.close();
            if (!file.delete()) throw new IOException();
            FileOutputStream fos = openFileOutput(filename, Context.MODE_WORLD_READABLE);
            fos.write(content.getBytes());
            fos.close();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        return content;
    }

    public void prepareToTransfer(String fileName) {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(pcCompanion)) {
                    MainActivity.device = device;
                    btTransfer(fileName);
                }
            }
        } else Toast.makeText(this, "Please pair master computer to this device.",
                Toast.LENGTH_SHORT).show();
    }

    public void btTransfer(String fileName){
        File file = getFileStreamPath(fileName);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

        PackageManager pm = getPackageManager();
        List appsList = pm.queryIntentActivities(intent, 0);
        if(appsList.size() > 0) {
            String packageName = null;
            String className = null;
            boolean found = false;
            for(int i = 0; i < appsList.size(); i++) {
                ResolveInfo info = (ResolveInfo) appsList.get(i);
                packageName = info.activityInfo.packageName;
                if (packageName.equals("com.android.bluetooth")) {
                    className = info.activityInfo.name;
                    found = true;
                    break;// found
                }
            }
            if (!found) Toast.makeText(this, "Not found!", Toast.LENGTH_SHORT).show();
            else {
                intent.setClassName(packageName, className);
                startActivityForResult(intent, 1);
                firstForm = true;
            }
        }
    }

    private boolean checkConfigFile() {
        File file = new File(getFilesDir().getAbsolutePath(), CONFIG_FILE);
        return file.exists();
    }

    public void archiveCurrentFile() {
        String message = "There has been an I/O issue!";
        try {
            File file = new File(getFilesDir().getAbsolutePath(), TEMP_FILE);
            if (!file.exists()) {
                message = "There has been an I/O issue! \n" +
                        "TEMP FILE NOT FOUND (LAST TRANSFER NOT ARCHIVED)";
                throw new IOException();
            }
            String content = "";
            String str;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (!((str = reader.readLine())== null)) content += str;
            reader.close();

            String fileName = ARCHIVE_FILE.split("\\.")[0] + archivedFiles
                    + ARCHIVE_FILE.split("\\.")[1];
            file = new File(getFilesDir().getAbsolutePath(), fileName);
            if (!file.exists()) if (!file.createNewFile()) {
                message = "There has been an I/O issue! \n" +
                        "FAILED TO CREATE ARCHIVE FILE";
                throw new IOException();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.close();
            archivedFiles++;
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private boolean retrieveComputerFile(String fileName) {
        String message = "There has been an I/O issue!\nCONFIG FILE RETRIEVE FAILED";
        try {
            File file = new File(BLUETOOTH_FOLDER_PATH, fileName);
            if (!file.exists()) {
                showAlertDialog("There is no received file.", "OK");
                return false;
            } else {
                ArrayList<String> contents = new ArrayList<>();
                String str;
                BufferedReader reader = new BufferedReader(new FileReader(file));
                while (!((str = reader.readLine()) == null)) contents.add(str);
                reader.close();
                if (!file.delete()) {
                    message = "There has been an I/O issue!\n" +
                            "BLUETOOTH DIR CONFIG FILE DELETE FAILED";
                    throw new IOException();
                }

                file = new File(getFilesDir().getAbsolutePath(), fileName);
                if (file.exists()) {
                    if (!file.delete()) {
                        message = "There has been an I/O issue!\nOLD CONFIG FILE DELETE FAILED";
                        throw new IOException();
                    }
                }
                if (!file.createNewFile()) {
                    message = "There has been an I/O issue!\n" +
                            "CONFIG FILE CREATION FAILED";
                    throw new IOException();
                }
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                for (int i = 0; i < contents.size(); i++) {
                    if (i > 0) writer.newLine();
                    writer.write(contents.get(i));
                }
                writer.close();
                initConfigs();
                showAlertDialog("SUCCESS!", "OK");
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),
                    message + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void showAlertDialog(String message, String positive) {
        MESSAGE = message;
        POSITIVE_BUTTON = positive;
        NEGATIVE_BUTTON = null;
        NEUTRAL_BUTTON = null;
        DialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "alertDialog");
    }

    public void showAlertDialog(String message, String positive, String negative) {
        MESSAGE = message;
        POSITIVE_BUTTON = positive;
        NEGATIVE_BUTTON = negative;
        NEUTRAL_BUTTON = null;
        DialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "alertDialog");
    }

    public void showAlertDialog(String message, String positive, String negative, String neutral) {
        MESSAGE = message;
        POSITIVE_BUTTON = positive;
        NEGATIVE_BUTTON = negative;
        NEUTRAL_BUTTON = neutral;
        DialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "alertDialog");
    }

    @Override
    public void onPause() {
        saveState();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        saveState();
        super.onBackPressed();
    }

    private void executeRequest() {
        switch (actionRequested) {
            case SAVE_FORM:
                if (saveForm()) {
                    Toast.makeText(getApplicationContext(), "FORM SAVED",
                            Toast.LENGTH_SHORT).show();
                    formsPending++;
                    resetForm();
                    lblFormsPending.setText(formsPending + " Form(s) Pending");
                } else showAlertDialog("FORM NOT SAVED: " +
                        "I/O problem encountered. Try again - if the problem persists, " +
                        "TALK TO LUCAS!", "Ok", null);
                break;
            case TRANSFER_FORMS:
                if (formsPending > 0) {
                    prepareFormTransfer(TEMP_FILE);
                    prepareToTransfer(TEMP_FILE);
                    actionRequested = Action.CHECK_TRANSFER;
                    formsPending = 0;
                    lblFormsPending.setText(formsPending + " Form(s) Pending");
                    firstForm = true;
                    archiveCurrentFile();
                } else showAlertDialog("No pending forms!", "Ok");
                break;
            case TRANSFER_LAST_FORMS:
                if (archivedFiles > 0) {
                    String fileName = ARCHIVE_FILE.split("\\.")[0] + (archivedFiles - 1)
                            + ARCHIVE_FILE.split("\\.")[1];
                    prepareFormTransfer(fileName);
                    prepareToTransfer(fileName);
                } else showAlertDialog("There has not been a transfer yet.", "Ok");
                break;
            case TRANSFER_ALL_ARCHIVES:
                break;
            case RECEIVE_CONFIG:
                retrieveComputerFile(CONFIG_FILE);
                initLayout();
                break;
            case WARNING_TEAMNUM:
                txtTeamNumber.setText("");
                txtTeamNumber.requestFocus();
                break;
            case WARNING_KEYWORD:
                txtCommentsBox.requestFocus();
        }
    }

    private class AlertDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(MESSAGE);
            builder.setPositiveButton(POSITIVE_BUTTON, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    switch (actionRequested) {
                        case CHOOSE_TRANSFER_ACTION:
                            actionRequested = Action.RECEIVE_CONFIG;
                            executeRequest();
                            break;
                        default:
                            executeRequest();
                    }
                    actionRequested = Action.NONE;
                }
            });
            try {
                if (null != NEGATIVE_BUTTON)
                    builder.setNegativeButton(NEGATIVE_BUTTON,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    switch (actionRequested) {
                                        case CHOOSE_TRANSFER_ACTION:
                                            actionRequested = Action.TRANSFER_LAST_FORMS;
                                            executeRequest();
                                    }
                                    actionRequested = Action.NONE;
                                }
                            });
                if (null != NEUTRAL_BUTTON)
                    builder.setNeutralButton(NEUTRAL_BUTTON,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (actionRequested) {
                                        case CHOOSE_TRANSFER_ACTION:
                                            actionRequested = Action.TRANSFER_ALL_ARCHIVES;
                                            executeRequest();
                                    }
                                    actionRequested = Action.NONE;
                                }
                            });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return builder.create();
        }
        @Override
        public void dismiss() {
            if (actionRequested.equals(Action.RECEIVE_CONFIG)) if (!checkConfigFile())
                showAlertDialog("A configuration file from the master computer is required to" +
                        "continue.\nPlease transfer the file to this machine.",
                        "I've transferred the file");
            super.dismiss();
        }
        @Override
        public void dismissAllowingStateLoss() {
            if (actionRequested.equals(Action.RECEIVE_CONFIG)) if (!checkConfigFile())
                showAlertDialog("A configuration file from the master computer is required to" +
                                "continue.\nPlease transfer the file to this machine.",
                        "I've transferred the file");
            super.dismissAllowingStateLoss();
        }
    }

}