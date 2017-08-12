package com.example.lucas.prescouting20;

public class Option {

	private String name;
	private int value;
	private int itemID;
	
	public Option(String name, int value, int itemID) {
		this.name = name;
		this.value = value;
		this.itemID = itemID;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	public int getItemID() {
		return itemID;
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.value);
	}
	
}
