public class MatchForm extends Form {
	
	public static final class MatchScoutingItems {
		
	}
	
	public MatchForm(int tabletNum, int teamNum, String scoutName) {
		super(FormTypes.MATCH_FORM, tabletNum, teamNum, scoutName);
	}
	
	public MatchForm(int reportID, int tabletNum, int teamNum, String scoutName) {
		super(reportID, FormTypes.MATCH_FORM, tabletNum, teamNum, scoutName);
	}
	
	public MatchForm(String rawForm) {
		super(rawForm);
	}
	
}
