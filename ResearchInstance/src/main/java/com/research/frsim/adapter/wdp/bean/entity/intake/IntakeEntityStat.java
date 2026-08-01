package com.research.frsim.adapter.wdp.bean.entity.intake;

import com.research.frsim.adapter.wdp.bean.entity.entity.EntityStat;


public class IntakeEntityStat extends EntityStat{

	private double intakeability;

	private int use;

	private double desertability;

	public double getIntakeability() {
		return intakeability;
	}

	public void setIntakeability(double intakeability) {
		this.intakeability = intakeability;
	}

	public int getUse() {
		return use;
	}

	public void setUse(int use) {
		this.use = use;
	}

	public double getDesertability() {
		return desertability;
	}

	public void setDesertability(double desertability) {
		this.desertability = desertability;
	}
	
}
