package com.research.frsim.adapter.wdp.util;

import java.math.BigDecimal;

public class NumberUtil {

	public static double roundDecimal(double data,int pointnum) {
		 BigDecimal bg = new BigDecimal(data);
		 double data1 = bg.setScale(pointnum, BigDecimal.ROUND_HALF_UP).doubleValue();
		 return data1;
	}
	

	public static double objectToDouble(Object data) {
		if (data == null) {
			return 0;
		}
		double result = Double.valueOf(data.toString());
		return result;
	}

	public static int objectToInt(Object data) {
		if (data == null) {
			return 0;
		}
		Double result = Double.valueOf(data.toString());
		int intresult = result.intValue();
		return intresult;
	}

	public static Double root(double data, double num) {
		double result = 0;
		
		if (num%2 == 0.0) {

			if (data < 0) {
				return null;
			}else {
				result = Math.pow(data, 1.0/num);
			}
		}else if (num%2 == 1.0) {

			if (data < 0.0) {
				result = -Math.pow(-data, 1.0/num);
			}else {
				result = Math.pow(data, 1.0/num);
			}
		}else {

			result = Math.pow(data, 1.0/num);
		}
		return result;
	}
	public static double sumWithWeight(double[] values, double... weight) {
		if (weight.length == 1) {
			double sum = 0;
			for (double value : values) {
				sum += weight[0] * value;
			}
			return sum;
		} else {
			int length = Math.min(values.length, weight.length);
			double sum = 0;
			for (int i = 0; i < length; i++) {
				sum += weight[i] * values[i];
			}
			return sum;
		}
	}
	

}
