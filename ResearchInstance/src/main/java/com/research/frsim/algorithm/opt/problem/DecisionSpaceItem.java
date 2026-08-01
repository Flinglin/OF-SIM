
package com.research.frsim.algorithm.opt.problem;

public class DecisionSpaceItem {


	protected double maxValue;

	protected double minValue;

	public double getRandomValue(){
		double random = Math.random()*(maxValue - minValue)+minValue;
		return random;
	}

	public boolean checkRange(double value){

		if(value < maxValue+1e-5 && value > minValue-1e-5) {
			return true;
		} else {
			return false;
		}
	}

	public double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
	public double getMinValue() {
		return minValue;
	}
	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}




}
