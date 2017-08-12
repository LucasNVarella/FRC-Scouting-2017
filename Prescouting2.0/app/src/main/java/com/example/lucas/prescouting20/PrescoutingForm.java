package com.example.lucas.prescouting20;

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
	
	public static final class ItemIDs {
		public static final int CAN_CLIMB = 7;
		public static final int COMMENTS = 44;
		public static final int DRIVE_TEAM_STUDENT_ONLY = 46;
		public static final int FRIENDLINESS = 48;
		public static final int DRIVETRAIN_TYPE = 58;
		public static final int CODE_LANGUAGE_USED = 59;
		public static final int DESCRIPTION_OF_ROBOT = 78;
		public static final int AUTO_HANDLE_GEARS = 84;
		public static final int AUTO_SHOOTS_HIGH = 88;
		public static final int AUTO_SHOOTS_LOW = 89;
		public static final int HANDLE_GEARS = 96;
		public static final int IS_ROBOT_FINISHED = 108;
		public static final int GEARS_FROM_THE_GROUND = 109;
		public static final int FUEL_FROM_THE_GROUND = 110;
		public static final int MAX_FUEL_STORAGE = 111;
		public static final int TIME_TO_EMPTY_STORAGE = 112;
		public static final int SHOOTS_MULTIPLE_DIRECTIONS = 113;
		public static final int TIME_TO_CLIMB = 114;
		public static final int AUTO = 115;
		public static final int AUTO_STARTS_NEXT_TO_KEY = 116;
		public static final int AUTO_STARTS_NEXT_TO_BOILER = 117;
		public static final int AUTO_STARTS_CENTER = 118;
		public static final int AUTO_STARTS_IN_LINE_LEFT_GEAR = 119;
		public static final int AUTO_STARTS_IN_LINE_RIGHT_GEAR = 120;
		public static final int AUTO_TIME_TO_SHOOT = 121;
		public static final int AUTO_STRATEGIES = 122;
	}
	
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
	
	public PrescoutingForm(int tabletNum, int teamNum, String scoutNames) {
		super(FormType.PRESCOUTING_FORM, tabletNum, teamNum, scoutNames);
	}
	
	public PrescoutingForm(int reportID, int tabletNum, int teamNum, String scoutNames) {
		super(reportID, FormType.PRESCOUTING_FORM, tabletNum, teamNum, scoutNames);
	}
	
	public PrescoutingForm(String rawForm) {
		super(rawForm);
	}
	
	public void prescoutingFormVisualizer() {
		
		String[] items = getRawForm().split("|"); 
		String[] identifyingInfo = new String[5]; 
		for (int i = 0; i < 6; i++) 
		{
			identifyingInfo[i] = items[i]; 
		}
		
		String[] formItems = new String[26];
		for (int k = 0; k < 27; k++) 
		{
			for (int i = 6; i < 33; i++) 
			{
				formItems[k] = items[i];
			}
		}
		
		printIdentifyingInfo(identifyingInfo);
		printFormItems(formItems); 
		
	}
	
	private void printIdentifyingInfo(String[] identifyingInfo) 
	{
		// Ignores the match number and whether it's a prescouting/matching form 
		System.out.println("Tablet Number: "+identifyingInfo[1]);
		System.out.println("Scout Names: "+identifyingInfo[2]);
		System.out.println("Team Number: "+identifyingInfo[3]);
	}
	
	private void printFormItems(String[] formItems) 
	{
		String returnString = ""; 
		for (int i = 0; i < formItems.length; i++) 
		{
			String[] itemInfo = formItems[i].split(","); 
			returnString += identifyItem(itemInfo[0])+itemInfo[1]+"\n"; 
		}
		System.out.println(returnString);
	}
	
	private String identifyItem(String itemNum) 
	{
		if (itemNum.equals(ItemIDs.CAN_CLIMB)) return "Can climb?: "; 
		else if (itemNum.equals(ItemIDs.COMMENTS)) return "Comments: "; 
		else if (itemNum.equals(ItemIDs.DRIVE_TEAM_STUDENT_ONLY)) return "Is the drive team student only?"; 
		else if (itemNum.equals(ItemIDs.FRIENDLINESS)) return "Friendliness rating: "; 
		else if (itemNum.equals(ItemIDs.DRIVETRAIN_TYPE)) return "Drive train type: "; 
		else if (itemNum.equals(ItemIDs.CODE_LANGUAGE_USED)) return "Code language used: "; 
		else if (itemNum.equals(ItemIDs.DESCRIPTION_OF_ROBOT)) return "Description of robot: "; 
		else if (itemNum.equals(ItemIDs.AUTO)) return "Has auto?: "; 
		else if (itemNum.equals(ItemIDs.AUTO_HANDLE_GEARS)) return "Can handle gears in auto?: "; 
		else if (itemNum.equals(ItemIDs.AUTO_SHOOTS_HIGH)) return "Can shoot in the high goal in auto?: "; 
		else if (itemNum.equals(ItemIDs.AUTO_SHOOTS_LOW)) return "Can shoot in the low goal in auto?: "; 
		else if (itemNum.equals(ItemIDs.AUTO_STARTS_CENTER)) return "Starts at the center line in auto?: "; 
		else if (itemNum.equals(ItemIDs.AUTO_STARTS_IN_LINE_LEFT_GEAR)) return "Starts in line with the left gear in auto?: "; 
		else if (itemNum.equals(ItemIDs.AUTO_STARTS_IN_LINE_RIGHT_GEAR)) return "Starts in line with the right gear in auto?: "; 
		else if (itemNum.equals(ItemIDs.AUTO_STARTS_NEXT_TO_BOILER)) return "Starts next to the boiler in auto?: "; 
		else if (itemNum.equals(ItemIDs.AUTO_STARTS_NEXT_TO_KEY)) return "Starts next to the key in auto?: "; 
		else if (itemNum.equals(ItemIDs.AUTO_STRATEGIES)) return "Auto strategies: "; 
		else if (itemNum.equals(ItemIDs.AUTO_TIME_TO_SHOOT)) return "Time spent shooting: "; 
		else if (itemNum.equals(ItemIDs.FUEL_FROM_THE_GROUND)) return "Can intake fuel from the ground?: "; 
		else if (itemNum.equals(ItemIDs.GEARS_FROM_THE_GROUND)) return "Can intake gear from the ground?: "; 
		else if (itemNum.equals(ItemIDs.HANDLE_GEARS)) return "Can handle gears in auto?: "; 
		else if (itemNum.equals(ItemIDs.IS_ROBOT_FINISHED)) return "Is the robot finished?: "; 
		else if (itemNum.equals(ItemIDs.MAX_FUEL_STORAGE)) return "Max fuel storage: "; 
		else if (itemNum.equals(ItemIDs.SHOOTS_MULTIPLE_DIRECTIONS)) return "Can shoot in multiple directions?: "; 
		else if (itemNum.equals(ItemIDs.TIME_TO_CLIMB)) return "Time required to climb: "; 
		else if (itemNum.equals(ItemIDs.TIME_TO_EMPTY_STORAGE)) return "Time taken to empty all its storage: "; 
		return ""; 
	}
	
}