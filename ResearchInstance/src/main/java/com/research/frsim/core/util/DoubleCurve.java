package com.research.frsim.core.util;

import java.io.Serializable;


public class DoubleCurve implements Serializable{

	private static final long serialVersionUID = 6415277668713282847L;
	private BaseStatistics v0;
	private BaseStatistics v1;
	private double[][] curveData;


	public DoubleCurve(double[][] data){
		curveData = new double [data.length][data[0].length];
		v0 = new BaseStatistics();
		v1 = new BaseStatistics();

		for(int i=0;i<data.length;i++)
		{
			v0.add(data[i][0]);
			v1.add(data[i][1]);
		}
		for(int i=0;i<data.length;i++)
		{
			for (int j=0;j<data[0].length;j++) {
				curveData[i][j] = data[i][j];
			}
		}
	}


	public double getDeltaByV0(double value){
		int l = HalfSearch.halfLocation(value, v0);
		double[] y = v1.getArray();
		double[] x = v0.getArray();
		double delta=0;


		if(l == -1) {
			l=0;
		} else if(l==-2) {
			l = x.length-1;
		}

		if(l == x.length-1){
			delta = (y[l]-y[l-1])/(x[l]-x[l-1]);
		}else{
			delta = (y[l+1]-y[l])/(x[l+1]-x[l]);
		}
		return delta;
	}

	public double getDeltaByV1(double value){
		int l = HalfSearch.halfLocation(value, v1);
		double[] y = v0.getArray();
		double[] x = v1.getArray();
		double delta=0;

		if(l == -1) {
			l=0;
		} else if(l==-2) {
			l = x.length-1;
		}

		if(l == x.length-1){
			delta = (y[l]-y[l-1])/(x[l]-x[l-1]);
		}else{
			delta = (y[l+1]-y[l])/(x[l+1]-x[l]);
		}
		return delta;
	}


	public double getV1ByV0(double value) {

		return HalfSearch.halfSearch(value, v0, v1);
	}
	
	public double getV1ByV0temp(double value) {

		if(value==0.0){
			return 0;
		}else{
		   return HalfSearch.halfSearch(value, v0, v1);
		}
	}

	public double getV0ByV1(double value) {

		return HalfSearch.halfSearch(value, v1, v0);
	}

	public double[] getV1Array() {

		return v1.getArray();
	}

	public double[] getV0Array() {

		return v0.getArray();
	}

	public double getMinV0() {

		return v0.getMin();
	}

	public double getMaxV0() {

		return v0.getMax();
	}

	public double[][] getCurveData() {
		return curveData;
	}

	public void setCurveData(double[][] curveData) {
		this.curveData = curveData;
	}


}
