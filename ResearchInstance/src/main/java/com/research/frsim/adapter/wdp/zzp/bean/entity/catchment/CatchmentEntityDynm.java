package com.research.frsim.adapter.wdp.zzp.bean.entity.catchment;

import com.research.frsim.adapter.wdp.zzp.bean.Project;
import com.research.frsim.adapter.wdp.zzp.bean.entity.entity.EntityDynm;

public class CatchmentEntityDynm extends EntityDynm {
	

	private double transwater;
	

	private double transindex;

	private double maxflow[];
	

	private double transflow[];
	
	public CatchmentEntityDynm(Project project) {
		int timelen = project.getTimeUnits().size();
		maxflow = new double[timelen];
		transflow = new double[timelen];

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
