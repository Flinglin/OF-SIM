package com.research.frsim.adapter.wdp.util.curve;

import java.io.Serializable;

public class TribleCurve implements Serializable{

	private static final long serialVersionUID = -8054616606545380434L;
	private BaseStatistics[] value1;
	private BaseStatistics[] value2;
	private BaseStatistics value0;
	private double[][] tribleData;

	public TribleCurve(double[][] data) {

		tribleData = new double [data.length][data[0].length];
		for(int i=0;i<data.length;i++)
		{
			for (int j=0;j<data[0].length;j++) {
				tribleData[i][j] = data[i][j];
			}
		}

		value0 = new BaseStatistics();

		double currentValue0 = data[0][0];
		value0.add(currentValue0);
		for (int i = 0; i < data.length; i++) {
			if (currentValue0 != data[i][0]) {
				currentValue0 = data[i][0];
				value0.add(data[i][0]);
			}
		}

		value1 = new BaseStatistics[value0.getArray().length];
		value2 = new BaseStatistics[value0.getArray().length];

		currentValue0 = data[0][0];
		int index = 0;
		value1[0] = new BaseStatistics();
		value2[0] = new BaseStatistics();
		for (int i = 0; i < data.length; i++) {
			if (currentValue0 != data[i][0]) {
				currentValue0 = data[i][0];
				index++;
				value1[index] = new BaseStatistics();
				value2[index] = new BaseStatistics();
			}

			value1[index].add(data[i][1]);
			value2[index].add(data[i][2]);
		}
	}

	public static void main(String[] args) {



	}

	public double getDeltaByV0V1(double v0,double v1){

		int l0 = HalfSearch.halfLocation(v0, value0);

		if(l0 == -1) {
			l0=0;
		} else if(l0==-2) {
			l0 = value0.getArray().length-2;
		}

		if(v0 == value0.getArray()[l0]){

			return  getDelta(v1, value1[l0], value2[l0]);
		}else{


			double delta1 = getDelta(v1, value1[l0], value2[l0]);
			double delta2 = getDelta(v1, value1[l0+1], value2[l0+1]);

			return (v0-value0.getArray()[l0])/(value0.getArray()[l0+1]-value0.getArray()[l0])
					*(delta2-delta1)+delta1;
		}
	}

	public double getDeltaByV0V2(double v0,double v2){

		int l0 = HalfSearch.halfLocation(v0, value0);


		if(l0 == -1) {
			l0=0;
		} else if(l0==-2) {
			l0 = value0.getArray().length-2;
		}

		if(v0 == value0.getArray()[l0]){

			return  getDelta(v2, value2[l0], value1[l0]);
		}else{


			double delta1 = getDelta(v2, value2[l0], value1[l0]);
			double delta2 = getDelta(v2, value2[l0+1], value1[l0+1]);

			return (v0-value0.getArray()[l0])/(value0.getArray()[l0+1]-value0.getArray()[l0])
					*(delta2-delta1)+delta1;
		}
	}

	public double getDelta(double value,BaseStatistics v0,BaseStatistics v1){
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

	public double getV2ByV0V1(double head, double flow) {

		double[] harray = value0.getArray();

		if (head < harray[0]) {
			head = harray[0];
		} else if (head > (harray[harray.length - 1])) {
			head = harray[harray.length - 1];
		}
		
		int position = HalfSearch.halfLocation(head, harray);
		if (harray[position] < head) {

			double temp1, temp2;
			temp1 = HalfSearch.halfSearch(flow, value1[position],
					value2[position]);
			temp2 = HalfSearch.halfSearch(flow, value1[position + 1],
					value2[position + 1]);
			double pi = (head - harray[position])
					/ (harray[position + 1] - harray[position]);
			return pi * (temp2 - temp1) + temp1;

		} else if (harray[position] > head) {

			double temp1, temp2;
			temp1 = HalfSearch.halfSearch(flow, value1[position - 1],
					value2[position - 1]);
			temp2 = HalfSearch.halfSearch(flow, value1[position],
					value2[position]);
			double pi = (head - harray[position - 1])
					/ (harray[position] - harray[position - 1]);
			return pi * (temp2 - temp1) + temp1;

		} else {
			return HalfSearch.halfSearch(flow, value1[position],
					value2[position]);
		}

	}

	public double getV1ByV0V2(double head, double power) {

		double[] harray = value0.getArray();

		if (head < harray[0]) {
			head = harray[0];
		} else if (head > (harray[harray.length - 1])) {
			head = harray[harray.length - 1];
		}

		int position = HalfSearch.halfLocation(head, harray);
		if (harray[position] < head) {
			double temp1, temp2;
			temp1 = HalfSearch.halfSearch(power, value2[position],
					value1[position]);
			temp2 = HalfSearch.halfSearch(power, value2[position + 1],
					value1[position + 1]);
			double pi = (head - harray[position])
					/ (harray[position + 1] - harray[position]);
			return pi * (temp2 - temp1) + temp1;

		} else if (harray[position] > head) {
			double temp1, temp2;
			temp1 = HalfSearch.halfSearch(power, value2[position - 1],
					value1[position - 1]);
			temp2 = HalfSearch.halfSearch(power, value2[position],
					value1[position]);
			double pi = (head - harray[position - 1])
					/ (harray[position] - harray[position - 1]);
			return pi * (temp2 - temp1) + temp1;

		} else {
			return HalfSearch.halfSearch(power, value2[position],
					value1[position]);
		}
	}

	public double[] getV1sByV0(double head) {

		double[] harray = value0.getArray();

		int position;
		if (head < harray[0])
			position = HalfSearch.halfLocation(harray[0], harray);
		else if (head > (harray[harray.length - 1]))
			position = HalfSearch.halfLocation(harray[harray.length - 1],
					harray);
		else
			position = HalfSearch.halfLocation(head, harray);
		return value1[position].getArray();

	}

	public double[] getV2sByV0(double head) {

		double[] harray = value0.getArray();

		int position;
		if (head < harray[0])
			position = HalfSearch.halfLocation(harray[0], harray);
		else if (head > (harray[harray.length - 1]))
			position = HalfSearch.halfLocation(harray[harray.length - 1],
					harray);
		else
			position = HalfSearch.halfLocation(head, harray);
		return value2[position].getArray();

	}

	public double getMinV0() {

		return value0.getMin();
	}

	public double getMaxV0() {

		return value0.getMax();
	}

	public double[][] getTribleData() {
		return tribleData;
	}

	public void setTribleData(double[][] tribleData) {
		this.tribleData = tribleData;
	}

	public BaseStatistics[] getValue1() {
		return value1;
	}

	public void setValue1(BaseStatistics[] value1) {
		this.value1 = value1;
	}

	public BaseStatistics[] getValue2() {
		return value2;
	}

	public void setValue2(BaseStatistics[] value2) {
		this.value2 = value2;
	}

	public BaseStatistics getValue0() {
		return value0;
	}

	public void setValue0(BaseStatistics value0) {
		this.value0 = value0;
	}



}
