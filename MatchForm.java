public class MatchForm extends Form {
	
	public static final class ItemIDs {
		public static final int PRESENT = 1;
		public static final int SCORE = 2;
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
		super(FormTypes.MATCH_FORM, tabletNum, teamNum, matchNum, scoutName);
	}
	
	public MatchForm(int reportID, int tabletNum, int teamNum, int matchNum, String scoutName) {
		super(reportID, FormTypes.MATCH_FORM, tabletNum, teamNum, matchNum, scoutName);
	}
	
	public MatchForm(String rawForm) {
		super(rawForm);
	}
	
}
