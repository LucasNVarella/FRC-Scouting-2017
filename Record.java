public final class Record {

	private String value;
	// database item id
	private int itemID;
	
	public Record(String value, int itemID) {
		this.value = value;
		this.itemID = itemID;
	}
	
	public Record(String value) {
		String[] values = value.split(Form.ID_DELIMITER);
		this.itemID = Integer.valueOf(values[0]);
		this.value = values[1];
	}

	public String getValue() {
		return value;
	}

	public int getItemID() {
		return itemID;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}
	
	@Override
	public String toString() {
		return itemID + Form.ID_DELIMITER + value;
	}
	
}