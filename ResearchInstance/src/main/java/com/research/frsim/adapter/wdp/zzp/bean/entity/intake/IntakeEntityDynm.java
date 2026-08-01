package com.research.frsim.adapter.wdp.zzp.bean.entity.intake;

import com.research.frsim.adapter.wdp.zzp.bean.Project;
import com.research.frsim.adapter.wdp.zzp.bean.entity.entity.EntityDynm;

public class IntakeEntityDynm extends EntityDynm {
	
	public static final int INTAKEQ = 1;
	public static final int DEMANDQ = 3;
	public static final int DEMANDW = 3;
	public static final int QMAX = 2;
	public static final int QMIN = 3;

	private double sumdemandwater;

	private double sumsupplywater;

	private double[] maxflow;
	

	private double[] minflow;

	private double[] demandflow;
	

	private double[] intakeflow;
	
	public IntakeEntityDynm(Project project) {
		int timelen = project.getTimeUnits().size();
		maxflow = new double[timelen];
		minflow = new double[timelen];
		demandflow = new double[timelen];
		intakeflow = new double[timelen];
	}
	
	@Override
	public void prepare() {
		for (int key:boundary.keySet()) {
			double[] value = boundary.get(key);
			if (key == INTAKEQ) {
				intakeflow = value;
			}
		}
	}
	
	@Override
	public void clean() {
		intakeflow = new double [intakeflow.length];
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

}
