public class MatchForm extends Form {
	
	public static final class Items {
		public static final Item PRESENT = new Item(1, "Present", Item.Datatype.BOOLEAN);
		public static final Item CAN_CLIMB = new Item(7, "Can climb?", Item.Datatype.BOOLEAN);
		public static final Item COMMENTS = new Item(44, "Comments", Item.Datatype.STRING);
		public static final Item RATE_DRIVING = new Item(79, "Rate driving", Item.Datatype.INTEGER);
		public static final Item SHOOTS_HIGH = new Item(80, "Shoots High?", Item.Datatype.BOOLEAN);
		public static final Item SHOOTS_LOW = new Item(81, "Shoots Low?", Item.Datatype.BOOLEAN);
		public static final Item AUTO_HANDLE_GEARS = new Item(84, "Auto: Handle Gears?", Item.Datatype.BOOLEAN);
		public static final Item AUTO_GEAR_SUCCESS = new Item(85, "Auto: Gear Success?", Item.Datatype.BOOLEAN);
		public static final Item AUTO_GEAR_PLACEMENT = new Item(86, "Auto: Gear Placement", Item.Datatype.OPTIONS);
		public static final Item AUTO_PILOT_PERFORMANCE = new Item(87, "Auto: Pilot Performance?", Item.Datatype.OPTIONS);
		public static final Item AUTO_SHOOTS_HIGH = new Item(88, "Auto: Shoots High?", Item.Datatype.BOOLEAN);
		public static final Item AUTO_SHOOTS_LOW = new Item(89, "Auto: Shoots Low?", Item.Datatype.BOOLEAN);
		public static final Item AUTO_SHOT_MAKES = new Item(90, "Auto: Shot Makes", Item.Datatype.INTEGER);
		public static final Item AUTO_CROSS_BASELINE = new Item(91, "Auto: Cross Baseline?", Item.Datatype.BOOLEAN);
		public static final Item STRATEGY = new Item(92, "Strategy", Item.Datatype.OPTIONS);
		public static final Item SHOOTING_SPEED = new Item(93, "Shooting Speed", Item.Datatype.OPTIONS);
		public static final Item SHOTS_MADE = new Item(94, "Shots Made", Item.Datatype.OPTIONS);
		public static final Item SHOOTER_ACCURACY = new Item(95, "Shooter Accuracy", Item.Datatype.OPTIONS);
		public static final Item HANDLE_GEARS = new Item(96, "Handle Gears?", Item.Datatype.BOOLEAN);
		public static final Item PILOT_PERFORMANCE = new Item(97, "Pilot Performance", Item.Datatype.OPTIONS);
		public static final Item GEAR_ATTEMPTS = new Item(98, "Gear Attempts", Item.Datatype.INTEGER);
		public static final Item GEAR_MAKES = new Item(99, "Gear Makes", Item.Datatype.INTEGER);
		public static final Item ROTORS_SPINNING = new Item(100, "Rotors Spinning", Item.Datatype.INTEGER);
		public static final Item CLIMB_SUCCESS = new Item(101, "Climb Success?", Item.Datatype.BOOLEAN);
		public static final Item STAYS_PUT_WHEN_POWER_CUT = new Item(102, "Stays Put When Power Cut?", Item.Datatype.BOOLEAN);
		public static final Item CLIMBING_SPEED = new Item(103, "Climbing Speed", Item.Datatype.OPTIONS);
		public static final Item DID_THEY_BREAK_DOWN = new Item(104, "Did They Break Down?", Item.Datatype.BOOLEAN);
		public static final Item FOUL_POINTS = new Item(105, "Foul Points?", Item.Datatype.BOOLEAN);
		public static final Item YELLOW_CARD = new Item(106, "Yellow Card?", Item.Datatype.BOOLEAN);
		public static final Item RED_CARD = new Item(107, "Red Card?", Item.Datatype.BOOLEAN);
	}
	
	public static final class ItemIDs {
		public static final int PRESENT = 1;
		public static final int CAN_CLIMB = 7;
		public static final int COMMENTS = 44;
		public static final int RATE_DRIVING = 79;
		public static final int SHOOTS_HIGH = 80;
		public static final int SHOOTS_LOW = 81;
		public static final int AUTO_HANDLE_GEARS = 84;
		public static final int AUTO_GEAR_SUCCESS = 85;
		public static final int AUTO_GEAR_PLACEMENT = 86;
		public static final int AUTO_PILOT_PERFORMANCE = 87;
		public static final int AUTO_SHOOTS_HIGH = 88;
		public static final int AUTO_SHOOTS_LOW = 89;
		public static final int AUTO_SHOT_MAKES = 90;
		public static final int AUTO_CROSS_BASELINE = 91;
		public static final int STRATEGY = 92;
		public static final int SHOOTING_SPEED = 93;
		public static final int SHOTS_MADE = 94;
		public static final int SHOOTER_ACCURACY = 95;
		public static final int HANDLE_GEARS = 96;
		public static final int PILOT_PERFORMANCE = 97;
		public static final int GEAR_ATTEMPTS = 98;
		public static final int GEAR_MAKES = 99;
		public static final int ROTORS_SPINNING = 100;
		public static final int CLIMB_SUCCESS = 101;
		public static final int STAYS_PUT_WHEN_POWER_CUT = 102;
		public static final int CLIMBING_SPEED = 103;
		public static final int DID_THEY_BREAK_DOWN = 104;
		public static final int FOUL_POINTS = 105;
		public static final int YELLOW_CARD = 106;
		public static final int RED_CARD = 107;
	}
	
	public MatchForm(int tabletNum, int teamNum, int matchNum, String scoutName) {
		super(FormType.MATCH_FORM, tabletNum, teamNum, matchNum, scoutName);
	}
	
	public MatchForm(int reportID, int tabletNum, int teamNum, int matchNum, String scoutName) {
		super(reportID, FormType.MATCH_FORM, tabletNum, teamNum, matchNum, scoutName);
	}
	
	public MatchForm(String rawForm) {
		super(rawForm);
	}
	
}