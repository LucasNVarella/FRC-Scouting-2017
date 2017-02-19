public final class Record {

	private String value;
	// database item id
	private int itemID;
	
	public Record(String value, int itemID) {
		this.value = value;
		this.itemID = itemID;
	}

	public String getValue() {
		return value;
	}

	public int getItemID() {
		return itemID;
	}
	
}
