package com.example.lucas.matchscouting20;

import java.util.ArrayList;
import java.util.Collection;

public class Form {

	private String rawForm;

	private FormType formType;
	private int tabletNum;
	private int teamNum;
	private int matchNum;
	private String scoutName;
	// database report id
	private int reportID;
	ArrayList<Record> records;

	public static final String FORM_DELIMITER = "||";
	public static final String ITEM_DELIMITER = "|";
	public static final String ID_DELIMITER = ",";

	public enum FormType {
		PRESCOUTING_FORM, MATCH_FORM
	}

	public static final class FormOrder {
		public static final int FORM_TYPE = 0;
		public static final int TABLET_NUM = 1;
		public static final int SCOUT_NAME = 2;
		public static final int TEAM_NUM = 3;
		public static final int MATCH_NUM = 4;

		public static final int highestIndex() {
			return MATCH_NUM;
		}
	}

	public Form(FormType formType, int tabletNum, int teamNum, String scoutName) {
		this.formType = formType;
		this.tabletNum = tabletNum;
		this.teamNum = teamNum;
		this.matchNum = -1;
		this.scoutName = scoutName;
		this.reportID = -1;
		records = new ArrayList<>();
		this.rawForm = null;
	}

	public Form(FormType formType, int tabletNum, int teamNum, int matchNum, String scoutName) {
		this.formType = formType;
		this.tabletNum = tabletNum;
		this.teamNum = teamNum;
		this.matchNum = matchNum;
		this.scoutName = scoutName;
		this.reportID = -1;
		records = new ArrayList<>();
		this.rawForm = null;
	}

	public Form(int reportID, FormType formType, int tabletNum, int teamNum, String scoutName) {
		this.formType = formType;
		this.tabletNum = tabletNum;
		this.teamNum = teamNum;
		this.matchNum = -1;
		this.scoutName = scoutName;
		this.reportID = reportID;
		records = new ArrayList<>();
		this.rawForm = null;
	}

	public Form(int reportID, FormType formType, int tabletNum, int teamNum, int matchNum, String scoutName) {
		this.formType = formType;
		this.tabletNum = tabletNum;
		this.teamNum = teamNum;
		this.matchNum = matchNum;
		this.scoutName = scoutName;
		this.reportID = reportID;
		records = new ArrayList<>();
		this.rawForm = null;
	}

	public Form(String rawForm) {
		this.rawForm = rawForm;
		records = new ArrayList<>();
		breakDownForm(this);
	}

	public String getRawForm() {
		return rawForm;
	}

	public void setRawForm(String rawForm) {
		this.rawForm = rawForm;
		breakDownForm(this);
	}

	public FormType getFormType() {
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

	public int getMatchNum() {
		return matchNum;
	}

	public void setMatchNum(int matchNum) {
		this.matchNum = matchNum;
	}

	public void setFormType(FormType formType) {
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
			String[] items = rawForm.split("\\" + ITEM_DELIMITER);
			int type = Integer.parseInt(items[FormOrder.FORM_TYPE]);
			if (type == FormType.MATCH_FORM.ordinal()) form.setFormType(FormType.MATCH_FORM);
			else if (type == FormType.PRESCOUTING_FORM.ordinal()) form.setFormType(FormType.PRESCOUTING_FORM);
			form.setTabletNum(Integer.parseInt(items[FormOrder.TABLET_NUM]));
			form.setTeamNum(Integer.parseInt(items[FormOrder.TEAM_NUM]));
			form.setScoutName(items[FormOrder.SCOUT_NAME]);
			form.setMatchNum(Integer.parseInt(items[FormOrder.MATCH_NUM]));
			String rawRecords = "";
			for (int i = 0; i < items.length-(FormOrder.highestIndex()+1); i++) {
				if (i == 0) rawRecords += items[FormOrder.highestIndex()+i+1];
				else rawRecords += ITEM_DELIMITER + items[FormOrder.highestIndex()+i+1];
			}
			form.addRecords(breakDownRecords(rawRecords, form.getFormType()));
		}
	}

	private static Record[] breakDownRecords(String rawRecords, FormType type) {
		ArrayList<Record> formRecords = new ArrayList<>();
		String[] records = rawRecords.split("\\" + ITEM_DELIMITER);
		for (String record : records) {
			String[] elements = record.split("\\" + ID_DELIMITER);
			formRecords.add(new Record(elements[1], Integer.parseInt(elements[0])));
		}
		return formRecords.toArray(new Record[0]);
	}

	@Override
	public String toString() {
		if (rawForm != null) return rawForm;
		else {
			rawForm = formType.ordinal() + ITEM_DELIMITER;
			rawForm += tabletNum + ITEM_DELIMITER;
			rawForm += scoutName + ITEM_DELIMITER;
			rawForm += teamNum + ITEM_DELIMITER;
			rawForm += matchNum;
			for (int i = 0; i < records.size(); i++)
				rawForm += ITEM_DELIMITER + records.get(i).toString();
			return rawForm;
		}
	}

}