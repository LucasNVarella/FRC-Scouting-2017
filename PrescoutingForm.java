public class PrescoutingForm extends Form {

	public static final class PrescoutingItems {
		
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
