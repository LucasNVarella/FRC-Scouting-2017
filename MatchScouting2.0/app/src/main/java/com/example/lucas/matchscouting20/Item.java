package com.example.lucas.matchscouting20;

public class Item {

	public enum Datatype {
		INTEGER, BOOLEAN, STRING, OPTIONS
	}

	private int id;
	private String name;
	private Datatype datatype;

	public Item(int id, String name, Datatype datatype) {
		this.id = id;
		this.name = name;
		this.datatype = datatype;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Datatype getDatatype() {
		return datatype;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
