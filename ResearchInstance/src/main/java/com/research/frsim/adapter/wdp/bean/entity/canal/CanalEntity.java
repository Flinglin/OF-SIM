package com.research.frsim.adapter.wdp.bean.entity.canal;

import java.util.Arrays;

import com.research.frsim.adapter.wdp.bean.Project;
import com.research.frsim.adapter.wdp.bean.entity.entity.Entity;
import com.research.frsim.adapter.wdp.enumerate.TimeScaleEnum;


public class CanalEntity extends Entity{

	private double[] storage;

	private double[] inflow;

	private double[] intakeflow;

	private double[] desertflow;

	public CanalEntity(Project project) {
		entityStat = new CanalEntityStat();
		int timelen = project.getTimeUnits().size();
		storage = new double[timelen+1];
		if (project.getTimeScaleEnum() == TimeScaleEnum.SHORTERM) {
			inflow = new double[timelen+1];
			intakeflow = new double[timelen+1];
			desertflow = new double[timelen+1];
		}else {
			inflow = new double[timelen];
			intakeflow = new double[timelen];
			desertflow = new double[timelen];
		}
	}
	
	@Override
	public void clean() {
		Arrays.fill(storage, 0);
		Arrays.fill(inflow, 0);
		Arrays.fill(intakeflow, 0);
		Arrays.fill(desertflow, 0);
	}
	

	public double calinflow(int period) {
		double inflow = 0;
		for (int k = 0; k < getEntityStat().getCatchmentEntitys().size(); k++) {
			inflow = inflow + getEntityStat().getCatchmentEntitys().get(k).getTransflow()[period];
		}
		return inflow;
	}
	

	public double calintakeflow(int period) {
		double demandflow = 0;
		for (int k = 0; k < getEntityStat().getIntakeEntitys().size(); k++) {
			demandflow = demandflow + getEntityStat().getIntakeEntitys().get(k).getIntakeflow()[period];
		}
		return demandflow;
	}

	public void balanceintakeflow(int period,double rate) {
		for (int k = 0; k < getEntityStat().getIntakeEntitys().size(); k++) {
			getEntityStat().getIntakeEntitys().get(k).getIntakeflow()[period] *= rate;
		}
	}

	public CanalEntityStat getEntityStat() {
		return (CanalEntityStat) entityStat;
	}
	
	public double[] getStorage() {
		return storage;
	}

	public void setStorage(double[] storage) {
		this.storage = storage;
	}

	public double[] getInflow() {
		return inflow;
	}

	public void setInflow(double[] inflow) {
		this.inflow = inflow;
	}

	public double[] getIntakeflow() {
		return intakeflow;
	}

	public void setIntakeflow(double[] intakeflow) {
		this.intakeflow = intakeflow;
	}

	public double[] getDesertflow() {
		return desertflow;
	}

	public void setDesertflow(double[] desertflow) {
		this.desertflow = desertflow;
	}

	


}
