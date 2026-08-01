package com.research.frsim.adapter.wdp.enumerate;

public enum EntityTypeEnum {
	

	CANAL("01"),

	GATE("02"),

	PUMP("03"),

	INTAKE("04"),

	CATCHMENT("05"),

	LAKE("06"),

	RESERVOIR("07");
	

	private String id;

	private EntityTypeEnum(String id) {

		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
