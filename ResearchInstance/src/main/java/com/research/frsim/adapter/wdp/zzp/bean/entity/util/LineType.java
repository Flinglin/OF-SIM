package com.research.frsim.adapter.wdp.zzp.bean.entity.util;


public class LineType {
	
	public static final int Quadratic = 1;
	public static final int Exponential = 2;
	public static final int Eexponential = 3;
	public static final int Logarithmic = 4;
	public static final int Linear = 5;

	private int linetype;

	private double[] para;

	public double apply(double openness) {
		double m = 0;
		if (linetype == Quadratic) {
			m = para[0]*Math.pow(openness, 2) + para[1]*openness + para[2];
		}else if (linetype == Exponential) {
			m = para[0]*Math.pow(openness, para[1]);
		}else if (linetype == Eexponential) {
			m = para[0]*Math.pow(openness, para[1]*openness);
		}else if (linetype == Logarithmic) {
			m = para[0]*Math.log(openness)+para[1];
		}else if (linetype == Linear) {
			m = para[0]*openness + para[1];
		}
		return m;
	}


	public int getLinetype() {
		return linetype;
	}


	public void setLinetype(int linetype) {
		this.linetype = linetype;
	}


	public double[] getPara() {
		return para;
	}


	public void setPara(double[] para) {
		this.para = para;
	}
	
	
	

}
