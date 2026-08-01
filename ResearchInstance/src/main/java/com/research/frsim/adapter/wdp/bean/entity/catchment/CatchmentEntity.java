package com.research.frsim.adapter.wdp.bean.entity.catchment;

import com.research.frsim.adapter.wdp.bean.Project;
import com.research.frsim.adapter.wdp.bean.entity.entity.Entity;
import com.research.frsim.adapter.wdp.enumerate.TimeScaleEnum;

public class CatchmentEntity extends Entity{

	private double transwater;

	private double transindex;

	private double maxflow[];
	

	private double transflow[];
	
	public CatchmentEntity(CatchmentEntityStat entityStat, Project project) {
		int timelen = project.getTimeUnits().size();
		if (project.getTimeScaleEnum() == TimeScaleEnum.SHORTERM) {
			timelen++;
		}
		maxflow = new double[timelen];
		transflow = new double[timelen];
		this.entityStat = entityStat;
	}

	public CatchmentEntityStat getEntityStat() {
		return (CatchmentEntityStat) entityStat;
	}
	
	public double getTranswater() {
		return transwater;
	}

	public void setTranswater(double transwater) {
		this.transwater = transwater;
	}

	public double getTransindex() {
		return transindex;
	}

	public void setTransindex(double transindex) {
		this.transindex = transindex;
	}

	public double[] getMaxflow() {
		return maxflow;
	}

	public void setMaxflow(double[] maxflow) {
		this.maxflow = maxflow;
	}

	public double[] getTransflow() {
		return transflow;
	}

	public void setTransflow(double[] transflow) {
		this.transflow = transflow;
	}

}
