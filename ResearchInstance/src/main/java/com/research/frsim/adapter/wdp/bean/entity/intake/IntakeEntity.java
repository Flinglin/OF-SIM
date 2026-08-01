package com.research.frsim.adapter.wdp.bean.entity.intake;

import java.util.Arrays;

import com.research.frsim.adapter.wdp.bean.Project;
import com.research.frsim.adapter.wdp.bean.entity.entity.Entity;
import com.research.frsim.adapter.wdp.enumerate.TimeScaleEnum;

public class IntakeEntity extends Entity{

	private double sumdemandwater;

	private double sumsupplywater;
	

	private double[] maxflow;

	private double[] minflow;
	

	private double[] maxvolume;
	

	private double[] minvolume;

	private double[] demandflow;

	private double[] intakeflow;

	private double[] intakevolume;

	private double[] watershortageRate;
	
	public IntakeEntity(IntakeEntityStat entityStat,Project project) {
		int timelen = project.getTimeUnits().size();
		if (project.getTimeScaleEnum() == TimeScaleEnum.SHORTERM) {
			timelen++;
		}
		maxflow = new double[timelen];
		minflow = new double[timelen];
		demandflow = new double[timelen];
		intakeflow = new double[timelen];
		watershortageRate=new double[timelen];
		this.entityStat = entityStat;
	}
	@Override
	public void clean() {
		Arrays.fill(intakeflow, 0);
	}
	

	public IntakeEntityStat getEntityStat() {
		return (IntakeEntityStat) entityStat;
	}

	public double getSumdemandwater() {
		return sumdemandwater;
	}

	public void setSumdemandwater(double sumdemandwater) {
		this.sumdemandwater = sumdemandwater;
	}

	public double getSumsupplywater() {
		return sumsupplywater;
	}

	public void setSumsupplywater(double sumsupplywater) {
		this.sumsupplywater = sumsupplywater;
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

	public double[] getDemandflow() {
		return demandflow;
	}

	public void setDemandflow(double[] demandflow) {
		this.demandflow = demandflow;
	}

	public double[] getIntakeflow() {
		return intakeflow;
	}

	public void setIntakeflow(double[] intakeflow) {
		this.intakeflow = intakeflow;
	}

	public double[] getWatershortageRate() {
		return watershortageRate;
	}

	public void setWatershortageRate(double[] watershortageRate) {
		this.watershortageRate = watershortageRate;
	}
	public double[] getIntakevolume() {
		return intakevolume;
	}
	public void setIntakevolume(double[] intakevolume) {
		this.intakevolume = intakevolume;
	}
	public double[] getMaxvolume() {
		return maxvolume;
	}
	public void setMaxvolume(double[] maxvolume) {
		this.maxvolume = maxvolume;
	}
	public double[] getMinvolume() {
		return minvolume;
	}
	public void setMinvolume(double[] minvolume) {
		this.minvolume = minvolume;
	}
	
	

}
