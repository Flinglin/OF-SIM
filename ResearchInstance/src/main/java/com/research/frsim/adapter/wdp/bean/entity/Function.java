package com.research.frsim.adapter.wdp.bean.entity;

public class Function {

	public static final int Linear = 101;
	public static final int One_Quadratic = 102;
	public static final int Two_Quadratic = 202;
	public static final int Two_Cubic = 203;
	public static final int Exponential = 110;
	public static final int Eexponential = 120;
	public static final int Logarithmic = 130;

	private int linetype;

	private double[] para;

	public double calculateY(double x) {
		double y = 0;
		if (linetype == One_Quadratic) {
			y = para[0] * Math.pow(x, 2) + para[1] * x + para[2];
		} else if (linetype == Exponential) {
			y = para[0] * Math.pow(x, para[1]);
		} else if (linetype == Eexponential) {
			y = para[0] * Math.pow(x, para[1] * x);
		} else if (linetype == Logarithmic) {
			y = para[0] * Math.log(x) + para[1];
		} else if (linetype == Linear) {
			y = para[0] * x + para[1];
		}
		return y;
	}

	public double[] getPara() {
		return para;
	}

	public void setPara(double[] para) {
		this.para = para;
	}

	public int getLinetype() {
		return linetype;
	}

	public void setLinetype(int linetype) {
		this.linetype = linetype;
	}
	
}
