import java.util.ArrayList;
import java.util.Collection;

public abstract class Form {

	private FormTypes formType;
	private int tabletNum;
	private int teamNum;
	// database report id
	private int reportID;
	ArrayList<Record<? extends Object>> records;
	
	public static enum FormTypes {
		MATCH_FORM, PRESCOUTING_FORM
	}

	public Form(FormTypes formType, int tabletNum, int teamNum) {
		this.formType = formType;
		this.tabletNum = tabletNum;
		this.teamNum = teamNum;
		this.reportID = -1;
	}

	public Form(int reportID, FormTypes formType, int tabletNum, int teamNum) {
		this.formType = formType;
		this.tabletNum = tabletNum;
		this.teamNum = teamNum;
		this.reportID = reportID;
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
	
	public ArrayList<Record<? extends Object>> getAllRecords() {
		return records;
	}
	
	public Record<? extends Object> getRecord(int index) {
		return records.get(index);
	}
	
	public boolean addRecord(Record<? extends Object> record) {
		return records.add(record);
	}
	
	public boolean addRecords(Collection<? extends Record<? extends Object>> records) {
		return this.records.addAll(records);
	}
	
	public boolean addRecords(Record<? extends Object>[] records) {
		boolean bool = true;
		for (int i = 0; i < records.length; i++) 
			if (this.records.add(records[i]) == false) bool = false;
		return bool;
	}
	
	public boolean removeRecord(Record<? extends Object> record) {
		return records.remove(record);
	}
	
	public Record<? extends Object> removeRecord(int index) {
		return records.remove(index);
	}
	
	public boolean removeRecords(Collection<? extends Record<? extends Object>> records) {
		return this.records.removeAll(records);
	}
	
	public boolean removeRecords(Record<? extends Object>[] records) {
		boolean bool = true;
		for (int i = 0; i < records.length; i++) 
			if (this.records.remove(records[i]) == false) bool = false;
		return bool;
	}

}