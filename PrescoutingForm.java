import java.util.ArrayList;

public class PrescoutingForm extends Form {
    
    public static final class Items {
        public static final Item CAN_CLIMB = new Item(7, "Can climb?", Item.Datatype.BOOLEAN);
        public static final Item COMMENTS = new Item(44, "Comments", Item.Datatype.STRING);
        public static final Item DRIVE_TEAM_STUDENT_ONLY = new Item(46, "Drive Team Student Only?", Item.Datatype.BOOLEAN);
        public static final Item FRIENDLINESS = new Item(48, "Friendliness", Item.Datatype.INTEGER);
        public static final Item DRIVETRAIN_TYPE = new Item(58, "Drivetrain Type", Item.Datatype.OPTIONS);
        public static final Item CODE_LANGUAGE_USED = new Item(59, "Code Language Used", Item.Datatype.OPTIONS);
        public static final Item DESCRIPTION_OF_ROBOT = new Item(78, "Description Of Robot", Item.Datatype.STRING);
        public static final Item RATE_DRIVING = new Item(79, "Rate driving", Item.Datatype.INTEGER);
        public static final Item SHOOTS_HIGH = new Item(80, "Shoots High?", Item.Datatype.BOOLEAN);
        public static final Item SHOOTS_LOW = new Item(81, "Shoots Low?", Item.Datatype.BOOLEAN);
        public static final Item AUTO_HANDLE_GEARS = new Item(84, "Auto: Handle Gears?", Item.Datatype.BOOLEAN);
        public static final Item AUTO_SHOOTS_HIGH = new Item(88, "Auto: Shoots High?", Item.Datatype.BOOLEAN);
        public static final Item AUTO_SHOOTS_LOW = new Item(89, "Auto: Shoots Low?", Item.Datatype.BOOLEAN);
        public static final Item HANDLE_GEARS = new Item(96, "Handle Gears?", Item.Datatype.BOOLEAN);
        public static final Item IS_ROBOT_FINISHED = new Item(108, "Is Robot Finished?", Item.Datatype.BOOLEAN);
        public static final Item GEARS_FROM_THE_GROUND = new Item(109, "Gears From The Ground?", Item.Datatype.BOOLEAN);
        public static final Item FUEL_FROM_THE_GROUND = new Item(110, "Fuel From The Ground?", Item.Datatype.BOOLEAN);
        public static final Item MAX_FUEL_STORAGE = new Item(111, "Max Fuel Storage", Item.Datatype.INTEGER);
        public static final Item TIME_TO_EMPTY_STORAGE = new Item(112, "Time To Empty Storage", Item.Datatype.INTEGER);
        public static final Item SHOOTS_MULTIPLE_DIRECTIONS = new Item(113, "Shoots Multiple Directions?", Item.Datatype.BOOLEAN);
        public static final Item TIME_TO_CLIMB = new Item(114, "Time To Climb", Item.Datatype.INTEGER);
        public static final Item AUTO = new Item(115, "Auto?", Item.Datatype.BOOLEAN);
        public static final Item AUTO_STARTS_NEXT_TO_KEY = new Item(116, "Auto: Starts Next To Key?", Item.Datatype.BOOLEAN);
        public static final Item AUTO_STARTS_NEXT_TO_BOILER = new Item(117, "Auto: Starts Next To Boiler?", Item.Datatype.BOOLEAN);
        public static final Item AUTO_STARTS_CENTER = new Item(118, "Auto: Starts Center?", Item.Datatype.BOOLEAN);
        public static final Item AUTO_STARTS_IN_LINE_LEFT_GEAR = new Item(119, "Auto: Starts In Line Left Gear?", Item.Datatype.BOOLEAN);
        public static final Item AUTO_STARTS_IN_LINE_RIGHT_GEAR = new Item(120, "Auto: Starts In Line Right Gear?", Item.Datatype.BOOLEAN);
        public static final Item AUTO_TIME_TO_SHOOT = new Item(121, "Auto: Time To Shoot", Item.Datatype.INTEGER);
        public static final Item AUTO_STRATEGIES = new Item(122, "Auto: Strategies", Item.Datatype.STRING);
    }
    
    public static final Item[] items = {PrescoutingForm.Items.CAN_CLIMB, PrescoutingForm.Items.COMMENTS,
    PrescoutingForm.Items.DRIVE_TEAM_STUDENT_ONLY, PrescoutingForm.Items.FRIENDLINESS,
    PrescoutingForm.Items.DRIVETRAIN_TYPE, PrescoutingForm.Items.CODE_LANGUAGE_USED, PrescoutingForm.Items.DESCRIPTION_OF_ROBOT,
    PrescoutingForm.Items.RATE_DRIVING, PrescoutingForm.Items.SHOOTS_HIGH,
    PrescoutingForm.Items.SHOOTS_LOW, PrescoutingForm.Items.AUTO_HANDLE_GEARS, PrescoutingForm.Items.AUTO_SHOOTS_HIGH,
    PrescoutingForm.Items.AUTO_SHOOTS_LOW, PrescoutingForm.Items.HANDLE_GEARS, PrescoutingForm.Items.IS_ROBOT_FINISHED,
    PrescoutingForm.Items.GEARS_FROM_THE_GROUND, PrescoutingForm.Items.FUEL_FROM_THE_GROUND, PrescoutingForm.Items.MAX_FUEL_STORAGE,
    PrescoutingForm.Items.TIME_TO_EMPTY_STORAGE, PrescoutingForm.Items.SHOOTS_MULTIPLE_DIRECTIONS, PrescoutingForm.Items.TIME_TO_CLIMB,
    PrescoutingForm.Items.AUTO, PrescoutingForm.Items.AUTO_STARTS_NEXT_TO_KEY,
    PrescoutingForm.Items.AUTO_STARTS_NEXT_TO_KEY, PrescoutingForm.Items.AUTO_STARTS_NEXT_TO_BOILER, PrescoutingForm.Items.AUTO_STARTS_CENTER,
    PrescoutingForm.Items.AUTO_STARTS_IN_LINE_LEFT_GEAR, PrescoutingForm.Items.AUTO_STARTS_IN_LINE_RIGHT_GEAR,
    PrescoutingForm.Items.AUTO_TIME_TO_SHOOT, PrescoutingForm.Items.AUTO_STRATEGIES};
    
    
    public static final class DRIVETRAIN_TYPE {
        public static final Option PNEUMATIC = new Option("Pneumatic", 2, 58);
        public static final Option SWERVE = new Option("Swerve", 3, 58);
        public static final Option MECANUM = new Option("Mecanum", 1, 58);
        public static final Option TANK = new Option("Tank", 0, 58);
    }
    
    public static final class CODE_LANGUAGE_USED {
        public static final Option JAVA = new Option("Java", 2, 59);
        public static final Option C = new Option("C++", 1, 59);
        public static final Option LABVIEW = new Option("LabVIEW", 0, 59);
    }
    
    public static final Option[] options = {PrescoutingForm.DRIVETRAIN_TYPE.PNEUMATIC, PrescoutingForm.DRIVETRAIN_TYPE.SWERVE,
    PrescoutingForm.DRIVETRAIN_TYPE.MECANUM, PrescoutingForm.DRIVETRAIN_TYPE.TANK,
    PrescoutingForm.CODE_LANGUAGE_USED.JAVA, PrescoutingForm.CODE_LANGUAGE_USED.C,
    PrescoutingForm.CODE_LANGUAGE_USED.LABVIEW};
    
    
    public PrescoutingForm(int tabletNum, int teamNum, String scoutNames) {
        super(FormType.PRESCOUTING_FORM, tabletNum, teamNum, scoutNames);
    }
    
    public PrescoutingForm(int reportID, int tabletNum, int teamNum, String scoutNames) {
        super(reportID, FormType.PRESCOUTING_FORM, tabletNum, teamNum, scoutNames);
    }
    
    public PrescoutingForm(String rawForm) {
        super(rawForm);
    }
    
    public String prescoutingFormVisualizer() {
        
        String visualizedForm = "\n";
        String[] items = getRawForm().split("\\|");
        String[] identifyingInfo = new String[5];
        for (int i = 0; i < identifyingInfo.length; i++)
        {
            identifyingInfo[i] = items[i];
        }
        visualizedForm += identifyingInfo(identifyingInfo) + "\n";
        
        ArrayList<Record> formItems = this.getAllRecords();
        for (Record item : formItems)
        {
            int recordItem=0;
            for (int i = 0; i < this.items.length; i++)
            {
                if (this.items[i].getId() == item.getItemID())
                    recordItem = i;
            }
            visualizedForm += this.items[recordItem].getName()+": ";
            if (this.items[recordItem].getDatatype().equals(Item.Datatype.INTEGER)) visualizedForm += item.getValue() + "\n";
            else if (this.items[recordItem].getDatatype().equals(Item.Datatype.STRING)) visualizedForm += item.getValue() + "\n";
            else if (this.items[recordItem].getDatatype().equals(Item.Datatype.BOOLEAN))
            {
                if (item.getValue() == "1") visualizedForm += "True"+"\n";
                else visualizedForm += "False"+"\n";
            }
            else if (this.items[recordItem].getDatatype().equals(Item.Datatype.OPTIONS))
            {
                for (int k = 0; k < options.length; k++)
                {
                    if (options[k].getItemID() == item.getItemID()) 
                    {
                        if (options[k].getValue() == Integer.parseInt(item.getValue()))
                        {
                            visualizedForm += options[k].getName()+"\n";
                        }
                    }
                }
            }
            
        }
        
        return visualizedForm; 
        
    }
    
    private String identifyingInfo(String[] identifyingInfo) 
    {
        // Ignores the match number and whether it's a prescouting/matching form
        return "Tablet Number: "+identifyingInfo[1] + "\n"
        + "Scout Names: "+identifyingInfo[2] + "\n"
        + "Team Number: "+identifyingInfo[3];
    }
    
}