package com.research.frsim.adapter.wdp.enumerate;
public enum FloodSimDirectEnum {

	FORWARD("forward"),

	REVERSE("reverse"),

	NORMAL("normal");

	private String type;

	private FloodSimDirectEnum(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
	
	
	

}
