package com.research.frsim.adapter.wdp.zzp.bean.entity.pump;

import com.research.frsim.adapter.wdp.enumerate.TimeScaleEnum;
import com.research.frsim.adapter.wdp.zzp.bean.Project;

public class PumpEntityDynm {

	private double[] maxflow;


	private double[] minflow;

	private double[] avgflow;

	public PumpEntityDynm(Project project) {

		int timelen = project.getTimeUnits().size();
		if (project.getTimeScaleEnum() == TimeScaleEnum.SHORTERM) {
			maxflow = new double[timelen + 1];
			minflow = new double[timelen + 1];
			avgflow = new double[timelen + 1];
		} else {
			maxflow = new double[timelen];
			minflow = new double[timelen];
			avgflow = new double[timelen];
		}
	}

	public double[] getMaxflow() {
		return maxflow;
	}

	public void setMaxflow(double[] maxflow) {
		this.maxflow = maxflow;
	}

	public double[] getMinflow() {
		return minflow;
	}

	public void setMinflow(double[] minflow) {
		this.minflow = minflow;
	}

	public double[] getAvgflow() {
		return avgflow;
	}

	public void setAvgflow(double[] avgflow) {
		this.avgflow = avgflow;
	}

}
