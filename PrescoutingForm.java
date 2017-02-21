public class PrescoutingForm extends Form {

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

	public PrescoutingForm(int tabletNum, int teamNum, String scoutNames) {
		super(FormTypes.PRESCOUTING_FORM, tabletNum, teamNum, scoutNames);
	}
	
	public PrescoutingForm(int reportID, int tabletNum, int teamNum, String scoutNames) {
		super(reportID, FormTypes.PRESCOUTING_FORM, tabletNum, teamNum, scoutNames);
	}
	
	public PrescoutingForm(String rawForm) {
		super(rawForm);
	}
	
}
