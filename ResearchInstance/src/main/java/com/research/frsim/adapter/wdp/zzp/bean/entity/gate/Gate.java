package com.research.frsim.adapter.wdp.zzp.bean.entity.gate;

import com.research.frsim.adapter.wdp.zzp.bean.entity.entity.Entity;

import java.util.Arrays;

public class Gate extends Entity {
	public static final int Q = 1;
	public static final int HUP = 2;
	public static final int HDOWN = 3;
	public static final int OPEN = 4;
	public static final int QMAX = 5;
	public static final int QMIN = 6;

	private final GateStat stat;


	public Gate(GateStat gateStat, int timelen) {
		maxflow = new double[timelen];
		minflow = new double[timelen];
		avgflow = new double[timelen];

		uplevel = new double[timelen+1];
		downlevel = new double[timelen+1];
		openness = new double[timelen+1];
		fitopenness = new double[timelen+1][3];
		for (int i = 0; i < fitopenness.length; i++) {
			Arrays.fill(fitopenness[i], -999);
		}
		steps = new Step[timelen];
		for (int t = 0; t < steps.length; t++) {
			steps[t] = new Step(t);
		}
		stat = gateStat;
	}

	public Gate(int timelen) {
		this(new GateStat(), timelen);
	}


	private class Step {
		private final int t;
		public Step(int t) {
			this.t = t;
		}
		public double getAvgflow() {return Gate.this.avgflow[t];}
		public void setAvgflow(double aveflow) {Gate.this.avgflow[t] = aveflow;}
		public double getMaxflow() {return Gate.this.maxflow[t];}
		public void setMaxflow(double value) {Gate.this.maxflow[t] = value;}
		public double getMinflow() {return Gate.this.minflow[t];}
		public void setMinflow(double value) {Gate.this.minflow[t] = value;}
		public double getUpLevel0(){return Gate.this.uplevel[t];}
		public void setUpLevel0(double value){Gate.this.uplevel[t] = value;}
		public double getUpLevel1(){return Gate.this.uplevel[t+1];}
		public void setUpLevel1(double value){Gate.this.uplevel[t+1] = value;}
		public double getDownLevel0(){return Gate.this.downlevel[t];}
		public void setDownLevel0(double value){Gate.this.downlevel[t] = value;}
		public double getDownLevel1(){return Gate.this.downlevel[t+1];}
		public void setDownLevel1(double value){Gate.this.downlevel[t+1] = value;}
	}

	private Step[] steps;

	public Step getStep(int t) {
		return steps[t];
	}


	private double[] avgflow;

	private double[] uplevel;

	private double[] downlevel;


	private double[][] fitopenness;

	private double[] openness;

	private double[] maxflow;

	private double[] minflow;

	public void clean() {
		Arrays.fill(avgflow,0);
		Arrays.fill(downlevel,0);
		Arrays.fill(uplevel,0);
		Arrays.fill(openness,0);
		for (int i = 0; i < fitopenness.length; i++) {
			Arrays.fill(fitopenness[i], -999);
		}
	}

	public double[] getAvgflow() {
		return avgflow;
	}

	public void setAvgflow(double[] avgflow) {
		this.avgflow = avgflow;
	}

	public double[] getUplevel() {
		return uplevel;
	}

	public void setUplevel(double[] uplevel) {
		this.uplevel = uplevel;
	}

	public double[] getDownlevel() {
		return downlevel;
	}

	public void setDownlevel(double[] downlevel) {
		this.downlevel = downlevel;
	}

	public double[][] getFitopenness() {
		return fitopenness;
	}

	public void setFitopenness(double[][] fitopenness) {
		this.fitopenness = fitopenness;
	}

	public double[] getOpenness() {
		return openness;
	}

	public void setOpenness(double[] openness) {
		this.openness = openness;
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

	public GateStat getStat() {
		return stat;
	}
}
