public abstract class Form {

	private FormTypes formType;
	private int tabletNum;
	private int teamNum;
	
	public static enum FormTypes {
		MATCH_FORM, PRESCOUTING_FORM
	}

	public Form(FormTypes formType, int tabletNum, int teamNum) {
		this.formType = formType;
		this.tabletNum = tabletNum;
		this.teamNum = teamNum;
	}

	public FormTypes getFormType() {
		return formType;
	}

	public int getTabletNum() {
		return tabletNum;
	}
	public int getTeamNum() {
		return teamNum;
	}

}