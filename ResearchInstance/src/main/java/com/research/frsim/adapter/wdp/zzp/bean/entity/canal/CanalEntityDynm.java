package com.research.frsim.adapter.wdp.zzp.bean.entity.canal;

import com.research.frsim.adapter.wdp.enumerate.TimeScaleEnum;
import com.research.frsim.adapter.wdp.zzp.bean.Project;
import com.research.frsim.adapter.wdp.zzp.bean.entity.entity.EntityDynm;

public class CanalEntityDynm extends EntityDynm {
	

	private double[] storage;
	

	private double[] inflow;

	private double[] intakeflow;
	
	public CanalEntityDynm(Project project) {
		int timelen = project.getTimeUnits().size();
		storage = new double[timelen+1];
		if (project.getTimeScaleEnum() == TimeScaleEnum.SHORTERM) {
			inflow = new double[timelen+1];
			intakeflow = new double[timelen+1];
		}else {
			inflow = new double[timelen];
			intakeflow = new double[timelen];
		}
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
	

	
	
	
	
	
	

}
