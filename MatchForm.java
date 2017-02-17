public class MatchForm extends Form {

	private String scoutName;
	private int matchNum;
	
	public MatchForm(int tabletNum, int teamNum, String scoutName, int matchNum) {
		super(FormTypes.MATCH_FORM, tabletNum, teamNum);
		this.scoutName = scoutName;
		this.matchNum = matchNum;
	}
	
	public MatchForm(int tabletNum, int teamNum) {
		super(FormTypes.MATCH_FORM, tabletNum, teamNum);
		this.scoutName = "";
		this.matchNum = -1;
	}

	public String getScoutName() {
		return scoutName;
	}

	public void setScoutName(String scoutName) {
		this.scoutName = scoutName;
	}

	public int getMatchNum() {
		return matchNum;
	}

	public void setMatchNum(int matchNum) {
		this.matchNum = matchNum;
	}
	
}
