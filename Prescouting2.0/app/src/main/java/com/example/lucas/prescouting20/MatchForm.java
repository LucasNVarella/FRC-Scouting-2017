package com.example.lucas.prescouting20;

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
		public static final Item AUTO_PILOT_PERFORMANCE = new Item(87, "Auto: Pilot Performance?", Item.Datatype.BOOLEAN);
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
	
	public static final class AUTO_GEAR_PLACEMENT {
		public static final Option LEFT = new Option("Left", -1, 86);
		public static final Option CENTER = new Option("Center", 0, 86);
		public static final Option RIGHT = new Option("Right", 1, 86);
		public static final Option NA = new Option("N/A", -2, 86);
	}
	
	public static final class STRATEGY {
		public static final Option GEAR = new Option("Gear", 2, 92);
		public static final Option FUEL = new Option("Fuel", 1, 92);
		public static final Option DEFENSE = new Option("Defense", 0, 92);
	}
	
	public static final class SHOOTING_SPEED {
		public static final Option SLOW = new Option("Slow", 0, 93);
		public static final Option MEDIUM = new Option("Medium", 1, 93);
		public static final Option FAST = new Option("Fast", 2, 93);
		public static final Option NA = new Option("N/A", -2, 93);
	}
	
	public static final class SHOTS_MADE {
		public static final Option FROM_0_TO_30 = new Option("0-30", 0, 94);
		public static final Option FROM_31_TO_60 = new Option("31-60", 1, 94);
		public static final Option FROM_61_TO_90 = new Option("61-90", 2, 94);
		public static final Option FROM_90 = new Option("90+", 3, 94);
	}
	
	public static final class SHOOTER_ACCURACY {
		public static final Option FROM_0_TO_25 = new Option("0-25%", 0, 95);
		public static final Option FROM_26_TO_50 = new Option("26-50%", 1, 95);
		public static final Option FROM_51_TO_75 = new Option("51-75%", 2, 95);
		public static final Option FROM_76_TO_100 = new Option("76-100%", 3, 95);
	}
	
	public static final class PILOT_PERFORMANCE {
		public static final Option GOOD = new Option("Good", 1, 97);
		public static final Option BAD = new Option("Bad", 0, 97);
		public static final Option NA = new Option("N/A", -2, 97);
	}
	
	public static final class CLIMBING_SPEED {
		public static final Option FAST = new Option("Fast", 2, 103);
		public static final Option MEDIUM = new Option("Medium", 1, 103);
		public static final Option SLOW = new Option("Slow", 0, 103);
		public static final Option NA = new Option("N/A", -2, 103);
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
