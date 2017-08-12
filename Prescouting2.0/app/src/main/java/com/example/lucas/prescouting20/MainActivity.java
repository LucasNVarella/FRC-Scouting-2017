package com.example.lucas.prescouting20;

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
import android.view.KeyEvent;
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

public class MainActivity extends Activity implements KeyEvent.Callback {

    // VISUAL COMPONENTS

    private TextView lblFormsPending;

    private Button btnSave;
    private Button btnTransfer;

    private Button btnTeleopStoreSub;
    private Button btnTeleopStoreAdd;
    private Button btnTeleopShootFullTimeSub;
    private Button btnTeleopShootFullTimeAdd;
    private Button btnTeleopClimbTimeSub;
    private Button btnTeleopClimbTimeAdd;
    private Button btnAutoShootTimeSub;
    private Button btnAutoShootTimeAdd;
    private Button btnEvalFriendSub;
    private Button btnEvalFriendAdd;

    private AutoCompleteTextView txtTeamNumber;
    private EditText txtAutoShootTime;
    private EditText txtTeleopStore;
    private EditText txtTeleopFullTime;
    private EditText txtTeleopClimbTime;
    private EditText txtEvalFriend;
    private EditText txtEvalDescription;
    private EditText txtEvalComments;
    private EditText txtAutoStrategies;

    private Spinner chooseName1;
    private Spinner chooseName2;
    private Spinner chooseName3;
    private Spinner chooseName4;
    private Spinner chooseName5;

    private CheckBox chkRobotFinished;
    private CheckBox chkAuto;
    private CheckBox chkAutoGears;
    private CheckBox chkAutoShootingHigh;
    private CheckBox chkAutoShootingLow;
    private CheckBox chkAutoStartingPositionKey;
    private CheckBox chkAutoStartingPositionLeft;
    private CheckBox chkAutoStartingPositionRight;
    private CheckBox chkAutoStartingPositionCenter;
    private CheckBox chkTeleopHandleGears;
    private CheckBox chkTeleopGearsGround;
    private CheckBox chkTeleopBallsGround;
    private CheckBox chkTeleopHandleBallsHigh;
    private CheckBox chkTeleopHandleBallsLow;
    private CheckBox chkTeleopClimb;
    private CheckBox chkEvalStudent;

    private RadioGroup grpTeleopDriveTrain1;
    private RadioGroup grpTeleopDriveTrain2;
    private RadioGroup grpEvalLang;

    private RadioButton radTeleopDriveTrainPneumatic;
    private RadioButton radTeleopDriveTrainMecanum;
    private RadioButton radTeleopDriveTrainSwerve;
    private RadioButton radTeleopDriveTrainTank;
    private RadioButton radEvalLangJava;
    private RadioButton radEvalLangCPP;
    private RadioButton radEvalLangLabview;

    // OTHER VARIABLES

    // Necessary items for data transfer
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static BluetoothDevice device = null;
    // The PC from which to receive and which to send
    private static String pcCompanion = "LUCASPC";
    final static String BLUETOOTH_FOLDER_PATH = "/storage/emulated/0/Download/";

    // These are the potential intentions for having opened an alert dialog.
    private enum Action {
        NONE, SAVE_FORM, CHOOSE_TRANSFER_ACTION, TRANSFER_FORMS, TRANSFER_LAST_FORMS,
        TRANSFER_ALL_ARCHIVES, RECEIVE_CONFIG, CHECK_TRANSFER, WARNING_TEAMNUM, WARNING_KEYWORD
    }
    private Action actionRequested = Action.NONE;

    private static final String STATE_SAVE_FILE = "stateSave.txt";
    private static final String TEMP_FILE = "tempFile.txt";
    private static final String ARCHIVE_FILE = "archiveFile.txt";
    private static final String BULK_FILE = "bulkFile.txt";
    private static final String CONFIG_FILE = "configFile.txt";
    private static int archivedFiles = 0;

    // Alert Dialog items
    private static String MESSAGE = "";
    private static String POSITIVE_BUTTON = "";
    private static String NEGATIVE_BUTTON = "";
    private static String NEUTRAL_BUTTON = "";

    private boolean firstForm = true;
    private int formsPending = 0;

    private String[] names = {"Scout"};

    private String[] teams = {"4048"};
    private String[] keywords = {};

    private String scoutName1;
    private String scoutName2;
    private String scoutName3;
    private String scoutName4;
    private String scoutName5;
    private int tabletNum;
    private int numOfScouts = 1;

    private boolean drivetrainClicked = false;

    private Record[] records = new Record[26];

    private Record canClimb;
    private Record comments;
    private Record driveTeamStudentOnly;
    private Record friendliness;
    private Record drivetrainType;
    private Record codeLanguageUsed;
    private Record descriptionOfRobot;
    private Record shootsHigh;
    private Record shootsLow;
    private Record autoHandleGears;
    private Record autoShootsHigh;
    private Record autoShootsLow;
    private Record handleGears;
    private Record isRobotFinished;
    private Record gearsFromTheGround;
    private Record fuelFromTheGround;
    private Record maxFuelStorage;
    private Record timeToEmptyStorage;
    private Record timeToClimb;
    private Record auto;
    private Record autoStartsNextToKey;
    private Record autoStartsCenter;
    private Record autoStartsInLineLeftGear;
    private Record autoStartsInLineRightGear;
    private Record autoTimeToShoot;
    private Record autoStrategies;

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
                numOfScouts = Integer.parseInt(contents.get(2));
                names = contents.get(3).split(Form.ID_DELIMITER);
                teams = contents.get(4).split(Form.ID_DELIMITER);
                keywords = contents.get(5).split(Form.ID_DELIMITER);
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
        records[0] = canClimb = new Record(null, PrescoutingForm.Items.CAN_CLIMB.getId());
        records[1] = comments = new Record(null, PrescoutingForm.Items.COMMENTS.getId());
        records[2] = driveTeamStudentOnly =
                new Record(null, PrescoutingForm.Items.DRIVE_TEAM_STUDENT_ONLY.getId());
        records[3] = friendliness = new Record(null, PrescoutingForm.Items.FRIENDLINESS.getId());
        records[4] = drivetrainType =
                new Record(null, PrescoutingForm.Items.DRIVETRAIN_TYPE.getId());
        records[5] = codeLanguageUsed =
                new Record(null, PrescoutingForm.Items.CODE_LANGUAGE_USED.getId());
        records[6] = descriptionOfRobot =
                new Record(null, PrescoutingForm.Items.DESCRIPTION_OF_ROBOT.getId());
        records[7] = shootsHigh = new Record(null, PrescoutingForm.Items.SHOOTS_HIGH.getId());
        records[8] = shootsLow = new Record(null, PrescoutingForm.Items.SHOOTS_LOW.getId());
        records[9] = autoHandleGears =
                new Record(null, PrescoutingForm.Items.AUTO_HANDLE_GEARS.getId());
        records[10] = autoShootsHigh =
                new Record(null, PrescoutingForm.Items.AUTO_SHOOTS_HIGH.getId());
        records[11] = autoShootsLow =
                new Record(null, PrescoutingForm.Items.AUTO_SHOOTS_LOW.getId());
        records[12] = handleGears = new Record(null, PrescoutingForm.Items.HANDLE_GEARS.getId());
        records[13] = isRobotFinished =
                new Record(null, PrescoutingForm.Items.IS_ROBOT_FINISHED.getId());
        records[14] = gearsFromTheGround =
                new Record(null, PrescoutingForm.Items.GEARS_FROM_THE_GROUND.getId());
        records[15] = fuelFromTheGround =
                new Record(null, PrescoutingForm.Items.FUEL_FROM_THE_GROUND.getId());
        records[16] = maxFuelStorage =
                new Record(null, PrescoutingForm.Items.MAX_FUEL_STORAGE.getId());
        records[17] = timeToEmptyStorage =
                new Record(null, PrescoutingForm.Items.TIME_TO_EMPTY_STORAGE.getId());
        records[18] = timeToClimb = new Record(null, PrescoutingForm.Items.TIME_TO_CLIMB.getId());
        records[19] = auto = new Record(null, PrescoutingForm.Items.AUTO.getId());
        records[20] = autoStartsNextToKey =
                new Record(null, PrescoutingForm.Items.AUTO_STARTS_NEXT_TO_KEY.getId());
        records[21] = autoStartsCenter =
                new Record(null, PrescoutingForm.Items.AUTO_STARTS_CENTER.getId());
        records[22] = autoStartsInLineLeftGear =
                new Record(null, PrescoutingForm.Items.AUTO_STARTS_IN_LINE_LEFT_GEAR.getId());
        records[23] = autoStartsInLineRightGear =
                new Record(null, PrescoutingForm.Items.AUTO_STARTS_IN_LINE_RIGHT_GEAR.getId());
        records[24] = autoTimeToShoot =
                new Record(null, PrescoutingForm.Items.AUTO_TIME_TO_SHOOT.getId());
        records[25] = autoStrategies =
                new Record(null, PrescoutingForm.Items.AUTO_STRATEGIES.getId());
    }

    private void initLayout() {
        chooseName1 = (Spinner) findViewById(R.id.chooseName1);
        ArrayAdapter<String> pickNameAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        chooseName1.setAdapter(pickNameAdapter);
        chooseName1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View spinner, int position,
                                       long id) {
                scoutName1 = (String) adapterView.getItemAtPosition(position);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });

        chooseName2 = (Spinner) findViewById(R.id.chooseName2);
        pickNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        chooseName2.setAdapter(pickNameAdapter);
        chooseName2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View spinner, int position,
                                       long id) {
                scoutName1 = (String) adapterView.getItemAtPosition(position);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });
        if (numOfScouts < 2) chooseName2.setEnabled(false);

        chooseName3 = (Spinner) findViewById(R.id.chooseName3);
        pickNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        chooseName3.setAdapter(pickNameAdapter);
        chooseName3.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View spinner, int position,
                                       long id) {
                scoutName1 = (String) adapterView.getItemAtPosition(position);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });
        if (numOfScouts < 3) chooseName3.setEnabled(false);

        chooseName4 = (Spinner) findViewById(R.id.chooseName4);
        pickNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        chooseName4.setAdapter(pickNameAdapter);
        chooseName4.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View spinner, int position,
                                       long id) {
                scoutName1 = (String) adapterView.getItemAtPosition(position);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });
        if (numOfScouts < 4) chooseName4.setEnabled(false);

        chooseName5 = (Spinner) findViewById(R.id.chooseName5);
        pickNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        chooseName5.setAdapter(pickNameAdapter);
        chooseName5.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View spinner, int position,
                                       long id) {
                scoutName1 = (String) adapterView.getItemAtPosition(position);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });
        if (numOfScouts < 5) chooseName5.setEnabled(false);

        radTeleopDriveTrainPneumatic =
                (RadioButton) findViewById(R.id.radTeleopDriveTrainPneumatic);
        radTeleopDriveTrainMecanum = (RadioButton) findViewById(R.id.radTeleopDriveTrainMecanum);
        radTeleopDriveTrainSwerve = (RadioButton) findViewById(R.id.radTeleopDriveTrainSwerve);
        radTeleopDriveTrainTank = (RadioButton) findViewById(R.id.radTeleopDriveTrainTank);
        radEvalLangJava = (RadioButton) findViewById(R.id.radEvalLangJava);
        radEvalLangCPP = (RadioButton) findViewById(R.id.radEvalLangCPP);
        radEvalLangLabview = (RadioButton) findViewById(R.id.radEvalLangLabview);

        grpTeleopDriveTrain1 = (RadioGroup) findViewById(R.id.grpTeleopDriveTrain1);
        grpTeleopDriveTrain1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                if (!drivetrainClicked) {
                    drivetrainClicked = true;
                    grpTeleopDriveTrain2.clearCheck();
                    switch (itemID) {
                        case R.id.radTeleopDriveTrainPneumatic:
                            drivetrainType.setValue(PrescoutingForm.DRIVETRAIN_TYPE.PNEUMATIC
                                    .toString());
                            break;
                        case R.id.radTeleopDriveTrainMecanum:
                            drivetrainType.setValue(PrescoutingForm.DRIVETRAIN_TYPE.MECANUM
                                    .toString());
                    }
                    drivetrainClicked = false;
                }
            }
        });

        grpTeleopDriveTrain2 = (RadioGroup) findViewById(R.id.grpTeleopDriveTrain2);
        grpTeleopDriveTrain2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                if (!drivetrainClicked) {
                    drivetrainClicked = true;
                    grpTeleopDriveTrain1.clearCheck();
                    switch (itemID) {
                        case R.id.radTeleopDriveTrainSwerve:
                            drivetrainType.setValue(PrescoutingForm.DRIVETRAIN_TYPE.SWERVE
                                    .toString());
                            break;
                        case R.id.radTeleopDriveTrainTank:
                            drivetrainType.setValue(PrescoutingForm.DRIVETRAIN_TYPE.TANK
                                    .toString());
                    }
                    drivetrainClicked = false;
                }
            }
        });

        grpEvalLang = (RadioGroup) findViewById(R.id.grpEvalLang);
        grpEvalLang.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int itemID) {
                switch (itemID) {
                    case R.id.radEvalLangJava:
                        codeLanguageUsed.setValue(PrescoutingForm.CODE_LANGUAGE_USED.JAVA
                                .toString());
                        break;
                    case R.id.radEvalLangCPP:
                        codeLanguageUsed.setValue(PrescoutingForm.CODE_LANGUAGE_USED.C
                                .toString());
                        break;
                    case R.id.radEvalLangLabview:
                        codeLanguageUsed.setValue(PrescoutingForm.CODE_LANGUAGE_USED.LABVIEW
                                .toString());
                }
            }
        });

        chkRobotFinished = (CheckBox) findViewById(R.id.chkRobotFinished);
        chkRobotFinished.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                isRobotFinished.setValue(value);
            }
        });

        chkAuto = (CheckBox) findViewById(R.id.chkAuto);
        chkAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                auto.setValue(value);
            }
        });

        chkAutoGears = (CheckBox) findViewById(R.id.chkAutoGears);
        chkAutoGears.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                autoHandleGears.setValue(value);
            }
        });

        chkAutoShootingHigh = (CheckBox) findViewById(R.id.chkAutoShootingHigh);
        chkAutoShootingHigh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                autoShootsHigh.setValue(value);
            }
        });

        chkAutoShootingLow = (CheckBox) findViewById(R.id.chkAutoShootingLow);
        chkAutoShootingLow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                autoShootsLow.setValue(value);
            }
        });

        chkAutoStartingPositionKey = (CheckBox) findViewById(R.id.chkAutoStartingPositionKey);
        chkAutoStartingPositionKey.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                autoStartsNextToKey.setValue(value);
            }
        });

        chkAutoStartingPositionLeft = (CheckBox) findViewById(R.id.chkAutoStartingPositionLeft);
        chkAutoStartingPositionLeft.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                autoStartsInLineLeftGear.setValue(value);
            }
        });

        chkAutoStartingPositionRight = (CheckBox) findViewById(R.id.chkAutoStartingPositionRight);
        chkAutoStartingPositionRight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                autoStartsInLineRightGear.setValue(value);
            }
        });

        chkAutoStartingPositionCenter = (CheckBox) findViewById(R.id.chkAutoStartingPositionCenter);
        chkAutoStartingPositionCenter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                autoStartsCenter.setValue(value);
            }
        });

        chkTeleopHandleGears = (CheckBox) findViewById(R.id.chkTeleopHandleGears);
        chkTeleopHandleGears.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                handleGears.setValue(value);
            }
        });

        chkTeleopGearsGround = (CheckBox) findViewById(R.id.chkTeleopGearsGround);
        chkTeleopGearsGround.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                gearsFromTheGround.setValue(value);
            }
        });

        chkTeleopBallsGround = (CheckBox) findViewById(R.id.chkTeleopBallsGround);
        chkTeleopBallsGround.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                fuelFromTheGround.setValue(value);
            }
        });

        chkTeleopHandleBallsHigh = (CheckBox) findViewById(R.id.chkTeleopHandleBallsHigh);
        chkTeleopHandleBallsHigh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                shootsHigh.setValue(value);
            }
        });

        chkTeleopHandleBallsLow = (CheckBox) findViewById(R.id.chkTeleopHandleBallsLow);
        chkTeleopHandleBallsLow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                shootsLow.setValue(value);
            }
        });

        chkTeleopClimb = (CheckBox) findViewById(R.id.chkTeleopClimb);
        chkTeleopClimb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                canClimb.setValue(value);
            }
        });

        chkEvalStudent = (CheckBox) findViewById(R.id.chkEvalStudent);
        chkEvalStudent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = "1";
                if (!isChecked) value = "0";
                driveTeamStudentOnly.setValue(value);
            }
        });

        btnTeleopStoreSub = (Button) findViewById(R.id.btnTeleopStoreSub);
        btnTeleopStoreSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtTeleopStore.getText().toString();
                int num = 0;
                if (!str.isEmpty()) {
                    num = Integer.parseInt(str);
                    if (num != 0) num--;
                }
                txtTeleopStore.setText(String.valueOf(num));
            }
        });

        btnTeleopStoreAdd = (Button) findViewById(R.id.btnTeleopStoreAdd);
        btnTeleopStoreAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtTeleopStore.getText().toString();
                int num = 1;
                if (!str.isEmpty()) num = Integer.parseInt(str) + 1;
                txtTeleopStore.setText(String.valueOf(num));
            }
        });

        btnTeleopShootFullTimeSub = (Button) findViewById(R.id.btnTeleopShootFullTimeSub);
        btnTeleopShootFullTimeSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtTeleopFullTime.getText().toString();
                int num = 0;
                if (!str.isEmpty()) {
                    num = Integer.parseInt(str);
                    if (num != 0) num--;
                }
                txtTeleopFullTime.setText(String.valueOf(num));
            }
        });

        btnTeleopShootFullTimeAdd = (Button) findViewById(R.id.btnTeleopShootFullTimeAdd);
        btnTeleopShootFullTimeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtTeleopFullTime.getText().toString();
                int num = 1;
                if (!str.isEmpty()) num = Integer.parseInt(str) + 1;
                txtTeleopFullTime.setText(String.valueOf(num));
            }
        });

        btnTeleopClimbTimeSub = (Button) findViewById(R.id.btnTeleopClimbTimeSub);
        btnTeleopClimbTimeSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtTeleopClimbTime.getText().toString();
                int num = 0;
                if (!str.isEmpty()) {
                    num = Integer.parseInt(str);
                    if (num != 0) num--;
                }
                txtTeleopClimbTime.setText(String.valueOf(num));
            }
        });

        btnTeleopClimbTimeAdd = (Button) findViewById(R.id.btnTeleopClimbTimeAdd);
        btnTeleopClimbTimeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtTeleopClimbTime.getText().toString();
                int num = 1;
                if (!str.isEmpty()) num = Integer.parseInt(str) + 1;
                txtTeleopClimbTime.setText(String.valueOf(num));
            }
        });

        btnAutoShootTimeSub = (Button) findViewById(R.id.btnAutoShootTimeSub);
        btnAutoShootTimeSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtAutoShootTime.getText().toString();
                int num = 0;
                if (!str.isEmpty()) {
                    num = Integer.parseInt(str);
                    if (num != 0) num--;
                }
                txtAutoShootTime.setText(String.valueOf(num));
            }
        });

        btnAutoShootTimeAdd = (Button) findViewById(R.id.btnAutoShootTimeAdd);
        btnAutoShootTimeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtAutoShootTime.getText().toString();
                int num = 1;
                if (!str.isEmpty()) num = Integer.parseInt(str) + 1;
                txtAutoShootTime.setText(String.valueOf(num));
            }
        });

        btnEvalFriendSub = (Button) findViewById(R.id.btnEvalFriendSub);
        btnEvalFriendSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtEvalFriend.getText().toString();
                int num = 0;
                if (!str.isEmpty()) {
                    num = Integer.parseInt(str);
                    if (num != 0) num--;
                }
                txtEvalFriend.setText(String.valueOf(num));
            }
        });

        btnEvalFriendAdd = (Button) findViewById(R.id.btnEvalFriendAdd);
        btnEvalFriendAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtEvalFriend.getText().toString();
                int num = 1;
                if (!str.isEmpty()) num = Integer.parseInt(str) + 1;
                if (num > 5) num--;
                txtEvalFriend.setText(String.valueOf(num));
            }
        });

        lblFormsPending = (TextView) findViewById(R.id.lblFormsPending);

        txtTeamNumber = (AutoCompleteTextView) findViewById(R.id.txtTeamNumber);
        //ArrayAdapter<String> txtTeamAdapter = new ArrayAdapter<>(this,
        //        android.R.layout.simple_spinner_item, teams);
        //txtTeamNumber.setAdapter(txtTeamAdapter);

        txtAutoShootTime = (EditText) findViewById(R.id.txtAutoShootTime);
        txtAutoShootTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (autoTimeToShoot.getValue() == null) {
                    if (s.length() > 0)
                        autoTimeToShoot.setValue(txtAutoShootTime.getText().toString());
                } else {
                    if (!autoTimeToShoot.getValue().equals(txtAutoShootTime.getText().toString()))
                        if (s.length() > 0)
                            autoTimeToShoot.setValue(txtAutoShootTime.getText().toString());
                        else autoTimeToShoot.setValue(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtTeleopStore = (EditText) findViewById(R.id.txtTeleopStore);
        txtTeleopStore.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (maxFuelStorage.getValue() == null) {
                    if (s.length() > 0)
                        maxFuelStorage.setValue(txtTeleopStore.getText().toString());
                } else {
                    if (!maxFuelStorage.getValue().equals(txtTeleopStore.getText().toString()))
                        if (s.length() > 0)
                            maxFuelStorage.setValue(txtTeleopStore.getText().toString());
                        else maxFuelStorage.setValue(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtTeleopFullTime = (EditText) findViewById(R.id.txtTeleopFullTime);
        txtTeleopFullTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timeToEmptyStorage.getValue() == null) {
                    if (s.length() > 0)
                        timeToEmptyStorage.setValue(txtTeleopFullTime.getText().toString());
                } else {
                    if (!timeToEmptyStorage.getValue().equals(txtTeleopFullTime.getText().toString()))
                        if (s.length() > 0)
                            timeToEmptyStorage.setValue(txtTeleopFullTime.getText().toString());
                        else timeToEmptyStorage.setValue(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtTeleopClimbTime = (EditText) findViewById(R.id.txtTeleopClimbTime);
        txtTeleopClimbTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timeToClimb.getValue() == null) {
                    if (s.length() > 0)
                        timeToClimb.setValue(txtTeleopClimbTime.getText().toString());
                } else {
                    if (!timeToClimb.getValue().equals(txtTeleopClimbTime.getText().toString()))
                        if (s.length() > 0)
                            timeToClimb.setValue(txtTeleopClimbTime.getText().toString());
                        else timeToClimb.setValue(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtEvalFriend = (EditText) findViewById(R.id.txtEvalFriend);
        txtEvalFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (friendliness.getValue() == null) {
                    if (s.length() > 0)
                        friendliness.setValue(txtEvalFriend.getText().toString());
                } else {
                    if (!friendliness.getValue().equals(txtEvalFriend.getText().toString()))
                        if (s.length() > 0)
                            friendliness.setValue(txtEvalFriend.getText().toString());
                        else friendliness.setValue(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtEvalDescription = (EditText) findViewById(R.id.txtEvalDescription);
        txtEvalDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (descriptionOfRobot.getValue() == null) {
                    if (s.length() > 0) descriptionOfRobot.setValue(s.toString()
                            .replaceAll(Form.ID_DELIMITER, "~"));
                } else {
                    if (!descriptionOfRobot.getValue().equals(s)) if (s.length() > 0)
                        descriptionOfRobot.setValue(s.toString().replaceAll(Form.ID_DELIMITER, "~"));
                    else descriptionOfRobot.setValue(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtEvalComments = (EditText) findViewById(R.id.txtEvalComments);
        txtEvalComments.addTextChangedListener(new TextWatcher() {
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

        txtAutoStrategies = (EditText) findViewById(R.id.txtAutoStrategies);
        txtAutoStrategies.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (autoStrategies.getValue() == null) {
                    if (s.length() > 0) autoStrategies.setValue(s.toString()
                            .replaceAll(Form.ID_DELIMITER, "~"));
                } else {
                    if (!autoStrategies.getValue().equals(s)) if (s.length() > 0)
                        autoStrategies.setValue(s.toString().replaceAll(Form.ID_DELIMITER, "~"));
                    else autoStrategies.setValue(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readyToSave()) {
                    actionRequested = Action.SAVE_FORM;
                    showAlertDialog("Are you sure you want to save?", "Yes", "No");
                } else showAlertDialog("FORM NOT SAVED: required fields are missing.", "Ok");
            }
        });

        btnTransfer = (Button) findViewById(R.id.btnTransfer);
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionRequested = Action.TRANSFER_FORMS;
                showAlertDialog("Are you sure you want to transfer?", "Yes", "No");
            }
        });
        btnTransfer.setOnLongClickListener(new View.OnLongClickListener() {
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
                if (string.isEmpty()) return;
                String[] items = string.split("\\" + Form.ITEM_DELIMITER);
                formsPending = Integer.parseInt(items[0]);
                if (formsPending > 0) firstForm = false;
                lblFormsPending.setText(formsPending + " Form(s) Pending");
                txtTeamNumber.setText(items[1]);
                numOfScouts = Integer.parseInt(items[2]);
                chooseName1.setSelection(Integer.parseInt(items[3]));
                if (numOfScouts >= 2) chooseName2.setSelection(Integer.parseInt(items[4]));
                if (numOfScouts >= 3) chooseName3.setSelection(Integer.parseInt(items[5]));
                if (numOfScouts >= 4) chooseName4.setSelection(Integer.parseInt(items[6]));
                if (numOfScouts >= 5) chooseName5.setSelection(Integer.parseInt(items[7]));
                setState(items, 8);
                reader.close();
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "There has been an I/O issue! \n" +
                    "STATE RESTORE FAILED. LAST UNSAVED FORM LOST.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void setState(String[] records, int startingIndex) {
        // canClimb
        if (records[startingIndex].split(Form.ID_DELIMITER)[1].equals("1"))
            chkTeleopClimb.setChecked(true);
        // comments
        txtEvalComments.setText(records[startingIndex+1].split(Form.ID_DELIMITER)[1]
                .replace("null", "").replaceAll("~", ","));
        // driveTeamStudentOnly
        if (records[startingIndex+2].split(Form.ID_DELIMITER)[1].equals("1"))
            chkEvalStudent.setChecked(true);
        // friendliness
        txtEvalFriend.setText(records[startingIndex+3].split(Form.ID_DELIMITER)[1]
                .replace("null", ""));
        // drivetrainType
        String value = records[startingIndex+4].split(Form.ID_DELIMITER)[1];
        if (value.equals(PrescoutingForm.DRIVETRAIN_TYPE.PNEUMATIC.toString()))
            radTeleopDriveTrainPneumatic.setChecked(true);
        else if (value.equals(PrescoutingForm.DRIVETRAIN_TYPE.MECANUM.toString()))
            radTeleopDriveTrainMecanum.setChecked(true);
        else if (value.equals(PrescoutingForm.DRIVETRAIN_TYPE.SWERVE.toString()))
            radTeleopDriveTrainSwerve.setChecked(true);
        else if (value.equals(PrescoutingForm.DRIVETRAIN_TYPE.TANK.toString()))
            radTeleopDriveTrainTank.setChecked(true);
        // codeLanguageUsed
        value = records[startingIndex+5].split(Form.ID_DELIMITER)[1];
        if (value.equals(PrescoutingForm.CODE_LANGUAGE_USED.JAVA.toString()))
            radEvalLangJava.setChecked(true);
        else if (value.equals(PrescoutingForm.CODE_LANGUAGE_USED.C.toString()))
            radEvalLangCPP.setChecked(true);
        else if (value.equals(PrescoutingForm.CODE_LANGUAGE_USED.LABVIEW.toString()))
            radEvalLangLabview.setChecked(true);
        // descriptionOfRobot
        txtEvalDescription.setText(records[startingIndex+6].split(Form.ID_DELIMITER)[1]
                .replace("null", "").replaceAll("~", ","));
        // shootsHigh
        if (records[startingIndex+7].split(Form.ID_DELIMITER)[1].equals("1"))
            chkTeleopHandleBallsHigh.setChecked(true);
        // shootsLow
        if (records[startingIndex+8].split(Form.ID_DELIMITER)[1].equals("1"))
            chkTeleopHandleBallsLow.setChecked(true);
        // autoHandleGears
        if (records[startingIndex+9].split(Form.ID_DELIMITER)[1].equals("1"))
            chkAutoGears.setChecked(true);
        // autoShootsHigh
        if (records[startingIndex+10].split(Form.ID_DELIMITER)[1].equals("1"))
            chkAutoShootingHigh.setChecked(true);
        // autoShootsLow
        if (records[startingIndex+11].split(Form.ID_DELIMITER)[1].equals("1"))
            chkAutoShootingLow.setChecked(true);
        // handleGear
        if (records[startingIndex+12].split(Form.ID_DELIMITER)[1].equals("1"))
            chkTeleopHandleGears.setChecked(true);
        // isRobotFinished
        if (records[startingIndex+13].split(Form.ID_DELIMITER)[1].equals("1"))
            chkRobotFinished.setChecked(true);
        // gearsFromTheGround
        if (records[startingIndex+14].split(Form.ID_DELIMITER)[1].equals("1"))
            chkTeleopGearsGround.setChecked(true);
        // fuelFromTheGround
        if (records[startingIndex+15].split(Form.ID_DELIMITER)[1].equals("1"))
            chkTeleopBallsGround.setChecked(true);
        // maxFuelStorage
        txtTeleopStore.setText(records[startingIndex+16].split(Form.ID_DELIMITER)[1]
                .replace("null", ""));
        // timeToEmptyStorage
        txtTeleopFullTime.setText(records[startingIndex+17].split(Form.ID_DELIMITER)[1]
                .replace("null", ""));
        // timeToClimb
        txtTeleopClimbTime.setText(records[startingIndex+18].split(Form.ID_DELIMITER)[1]
                .replace("null", ""));
        // auto
        if (records[startingIndex+19].split(Form.ID_DELIMITER)[1].equals("1"))
            chkAuto.setChecked(true);
        // autoStartsNextToKey
        if (records[startingIndex+20].split(Form.ID_DELIMITER)[1].equals("1"))
            chkAutoStartingPositionKey.setChecked(true);
        // autoStartsCenter
        if (records[startingIndex+21].split(Form.ID_DELIMITER)[1].equals("1"))
            chkAutoStartingPositionCenter.setChecked(true);
        // autoStartsInLineLeftGear
        if (records[startingIndex+22].split(Form.ID_DELIMITER)[1].equals("1"))
            chkAutoStartingPositionLeft.setChecked(true);
        // autoStartsInLineRightGear
        if (records[startingIndex+23].split(Form.ID_DELIMITER)[1].equals("1"))
            chkAutoStartingPositionRight.setChecked(true);
        // autoTimeToShoot
        txtAutoShootTime.setText(records[startingIndex+24].split(Form.ID_DELIMITER)[1]
                .replace("null", ""));
        // autoStrategies
        txtAutoStrategies.setText(records[startingIndex+25].split(Form.ID_DELIMITER)[1]
                .replace("null", "").replaceAll("~", ","));
    }

    private boolean readyToSave() {
        if (txtTeamNumber.getText().toString().isEmpty()) return false;
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
                String text = txtEvalComments.getText().toString().toLowerCase();
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
                    txtEvalComments.setText(text);
                }
                i++;
            }
        }
        i = 0;
        done = false;
        while (!done) {
            if (i >= keywords.length) done = true;
            else {
                String text = txtEvalDescription.getText().toString().toLowerCase();
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
                    txtEvalDescription.setText(text);
                }
                i++;
            }
        }
        i = 0;
        done = false;
        while (!done) {
            if (i >= keywords.length) done = true;
            else {
                String text = txtAutoStrategies.getText().toString().toLowerCase();
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
                    txtAutoStrategies.setText(text);
                }
                i++;
            }
        }
        return true;
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

    private Form makeForm() {
        String scouts = "";
        scouts += scoutName1;
        if (numOfScouts >= 2) scouts += Form.ID_DELIMITER + scoutName2;
        if (numOfScouts >= 3) scouts += Form.ID_DELIMITER + scoutName3;
        if (numOfScouts >= 4) scouts += Form.ID_DELIMITER + scoutName4;
        if (numOfScouts == 5) scouts += Form.ID_DELIMITER + scoutName5;
        PrescoutingForm form = new PrescoutingForm(tabletNum,
                Integer.parseInt(txtTeamNumber.getText().toString()), scouts);
        for (Record record : records) if (record.getValue() != null) form.addRecord(record);
        return form;
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

    private void resetForm() {
        txtTeamNumber.setText("");
        txtEvalComments.setText("");
        txtAutoStrategies.setText("");
        txtTeleopStore.setText("");
        txtAutoShootTime.setText("");
        txtTeleopClimbTime.setText("");
        txtEvalDescription.setText("");
        txtEvalFriend.setText("");
        txtTeleopFullTime.setText("");
        resetRadiogroups();
        resetCheckboxes();
        initRecords();
        initLayout();
    }

    private void resetRadiogroups() {
        grpEvalLang.setOnCheckedChangeListener(null);
        grpTeleopDriveTrain1.setOnCheckedChangeListener(null);
        grpTeleopDriveTrain2.setOnCheckedChangeListener(null);

        grpEvalLang.clearCheck();
        grpTeleopDriveTrain1.clearCheck();
        grpTeleopDriveTrain2.clearCheck();
    }

    private void resetCheckboxes() {
        chkRobotFinished.setOnCheckedChangeListener(null);
        chkAuto.setOnCheckedChangeListener(null);
        chkAutoGears.setOnCheckedChangeListener(null);
        chkAutoShootingHigh.setOnCheckedChangeListener(null);
        chkAutoShootingLow.setOnCheckedChangeListener(null);
        chkAutoStartingPositionKey.setOnCheckedChangeListener(null);
        chkAutoStartingPositionLeft.setOnCheckedChangeListener(null);
        chkAutoStartingPositionRight.setOnCheckedChangeListener(null);
        chkAutoStartingPositionCenter.setOnCheckedChangeListener(null);
        chkTeleopHandleGears.setOnCheckedChangeListener(null);
        chkTeleopGearsGround.setOnCheckedChangeListener(null);
        chkTeleopBallsGround.setOnCheckedChangeListener(null);
        chkTeleopHandleBallsHigh.setOnCheckedChangeListener(null);
        chkTeleopHandleBallsLow.setOnCheckedChangeListener(null);
        chkTeleopClimb.setOnCheckedChangeListener(null);
        chkEvalStudent.setOnCheckedChangeListener(null);

        chkRobotFinished.setChecked(false);
        chkAuto.setChecked(false);
        chkAutoGears.setChecked(false);
        chkAutoShootingHigh.setChecked(false);
        chkAutoShootingLow.setChecked(false);
        chkAutoStartingPositionKey.setChecked(false);
        chkAutoStartingPositionLeft.setChecked(false);
        chkAutoStartingPositionRight.setChecked(false);
        chkAutoStartingPositionCenter.setChecked(false);
        chkTeleopHandleGears.setChecked(false);
        chkTeleopGearsGround.setChecked(false);
        chkTeleopBallsGround.setChecked(false);
        chkTeleopHandleBallsHigh.setChecked(false);
        chkTeleopHandleBallsLow.setChecked(false);
        chkTeleopClimb.setChecked(false);
        chkEvalStudent.setChecked(false);
    }

    private void saveState() {
        try {
            File file = new File(getFilesDir().getAbsolutePath(), STATE_SAVE_FILE);
            if (!file.exists()) if (!file.createNewFile()) throw new IOException();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            // include forms pending, matchnum, teamnum, name, score
            String output = String.valueOf(formsPending) + Form.ITEM_DELIMITER +
                    numOfScouts + Form.ITEM_DELIMITER +
                    txtTeamNumber.getText().toString() + Form.ITEM_DELIMITER +
                    chooseName1.getSelectedItemPosition() + Form.ITEM_DELIMITER +
                    chooseName2.getSelectedItemPosition() + Form.ITEM_DELIMITER +
                    chooseName3.getSelectedItemPosition() + Form.ITEM_DELIMITER +
                    chooseName4.getSelectedItemPosition() + Form.ITEM_DELIMITER +
                    chooseName5.getSelectedItemPosition();
            for (Record record : records) output += Form.ITEM_DELIMITER + record.toString();
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),
                    "There has been an I/O issue! Current Form lost...", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
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

    private boolean checkConfigFile() {
        File file = new File(getFilesDir().getAbsolutePath(), CONFIG_FILE);
        return file.exists();
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
            Toast.makeText(getApplicationContext(), message + "\n"
                    + e.getMessage(), Toast.LENGTH_LONG).show();
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
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            switch (actionRequested) {
                                case CHOOSE_TRANSFER_ACTION:
                                    actionRequested = Action.RECEIVE_CONFIG;
                                    executeRequest();
                                    break;
                                default:
                                    executeRequest();
                            }
                            actionRequested = Action.NONE;
                            dismiss();
                            return true;
                        case KeyEvent.KEYCODE_ESCAPE:
                            dismiss();
                            return true;
                    }
                    return true;
                }
            });
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_S:
                if (event.isCtrlPressed()) {
                    btnSave.callOnClick();
                }
                return true;
            case KeyEvent.KEYCODE_T:
                if (event.isCtrlPressed()) {
                    btnTransfer.callOnClick();
                }
                return true;
            case KeyEvent.KEYCODE_L:
                if (event.isCtrlPressed()) {
                    actionRequested = Action.TRANSFER_LAST_FORMS;
                    executeRequest();
                }
                return true;
            case KeyEvent.KEYCODE_R:
                if (event.isCtrlPressed()) {
                    actionRequested = Action.RECEIVE_CONFIG;
                    executeRequest();
                }
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

}