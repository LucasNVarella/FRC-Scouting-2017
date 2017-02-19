import java.util.ArrayList;
import java.util.Collection;

public abstract class Form {
	
	private String rawForm;

	private FormTypes formType;
	private int tabletNum;
	private int teamNum;
	private String scoutName;
	// database report id
	private int reportID;
	ArrayList<Record> records;
	
	public static enum FormTypes {
		MATCH_FORM, PRESCOUTING_FORM
	}
	
	public static final class RawFormOrder {
		public static final int FORM_TYPE = 0;
		public static final int TABLET_NUM = 1;
		public static final int SCOUT_NAME = 2;
		public static final int TEAM_NUM = 3;
		public static final int MATCH_NUM = 4;
		
		public static final int highestMatchScoutingIndex() {
			return MATCH_NUM;
		}
		
		public static final int highestPrescoutingIndex() {
			return TEAM_NUM;
		}
	}

	public Form(FormTypes formType, int tabletNum, int teamNum, String scoutName) {
		this.formType = formType;
		this.tabletNum = tabletNum;
		this.teamNum = teamNum;
		this.scoutName = scoutName;
		this.reportID = -1;
		this.rawForm = null;
	}

	public Form(int reportID, FormTypes formType, int tabletNum, int teamNum, String scoutName) {
		this.formType = formType;
		this.tabletNum = tabletNum;
		this.teamNum = teamNum;
		this.scoutName = scoutName;
		this.reportID = reportID;
		this.rawForm = null;
	}
	
	public Form(String rawForm) {
		this.rawForm = rawForm;
		breakDownForm(this);
	}

	public String getRawForm() {
		return rawForm;
	}

	public void setRawForm(String rawForm) {
		this.rawForm = rawForm;
		breakDownForm(this);
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

	public int getFormID() {
		return reportID;
	}

	public void setFormType(FormTypes formType) {
		this.formType = formType;
	}

	public void setTabletNum(int tabletNum) {
		this.tabletNum = tabletNum;
	}

	public void setTeamNum(int teamNum) {
		this.teamNum = teamNum;
	}

	public void setFormID(int reportID) {
		this.reportID = reportID;
	}
	
	public String getScoutName() {
		return scoutName;
	}

	public void setScoutName(String scoutName) {
		this.scoutName = scoutName;
	}

	public ArrayList<Record> getAllRecords() {
		return records;
	}
	
	public Record getRecord(int index) {
		return records.get(index);
	}
	
	public boolean addRecord(Record record) {
		return records.add(record);
	}
	
	public boolean addRecords(Collection<? extends Record> records) {
		return this.records.addAll(records);
	}
	
	public boolean addRecords(Record[] records) {
		boolean bool = true;
		for (int i = 0; i < records.length; i++) 
			if (this.records.add(records[i]) == false) bool = false;
		return bool;
	}
	
	public Record[] addRecords(String rawRecords) {
		rawForm += "|" + rawRecords;
		this.addRecords(breakDownRecords(rawRecords, this.formType));
		return null;
	}
	
	public boolean removeRecord(Record record) {
		return records.remove(record);
	}
	
	public Record removeRecord(int index) {
		return records.remove(index);
	}
	
	public boolean removeRecords(Collection<? extends Record> records) {
		return this.records.removeAll(records);
	}
	
	public boolean removeRecords(Record[] records) {
		boolean bool = true;
		for (int i = 0; i < records.length; i++) 
			if (this.records.remove(records[i]) == false) bool = false;
		return bool;
	}
	
	private static void breakDownForm(Form form) {
		String rawForm = form.getRawForm();
		if (rawForm != null) {
			String[] items = rawForm.split("|");
			int type = Integer.parseInt(items[RawFormOrder.FORM_TYPE]);
			int highestIndex = 0;
			if (type == FormTypes.MATCH_FORM.ordinal()) {
				form.setFormType(FormTypes.MATCH_FORM);
				highestIndex = RawFormOrder.highestMatchScoutingIndex();
			}
			else if (type == FormTypes.PRESCOUTING_FORM.ordinal()) {
				form.setFormType(FormTypes.PRESCOUTING_FORM);
				highestIndex = RawFormOrder.highestPrescoutingIndex();
			}
			form.setTabletNum(Integer.parseInt(items[RawFormOrder.TABLET_NUM]));
			form.setTeamNum(Integer.parseInt(items[RawFormOrder.TEAM_NUM]));
			form.setScoutName(items[RawFormOrder.SCOUT_NAME]);
			String rawRecords = "";
			for (int i = 0; i < items.length-(highestIndex+1); i++) {
				if (i == 0) rawRecords += items[highestIndex+i+1];
				else rawRecords += "|" + items[highestIndex+i+1];
			}
			form.addRecords(breakDownRecords(rawRecords, form.getFormType()));
		}
	}
	
	private static Record[] breakDownRecords(String rawRecords, FormTypes type) {
		ArrayList<Record> formRecords = new ArrayList<>();
		String[] records = rawRecords.split("|");
		for (String record : records) {
			String[] elements = record.split(",");
			formRecords.add(new Record(elements[1], Integer.parseInt(elements[0])));
		}
		return formRecords.toArray(new Record[0]);
	}

}