public class Record<T extends Object> {

	private T value;
	// database item id
	private int itemID;
	
	public Record(T value, int itemID) {
		this.value = value;
		this.itemID = itemID;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}
	
}
