
package com.research.frsim.algorithm.opt.problem;

public class DecisionSpace {

	private DecisionSpaceItem[] decisionSpaceItems;

	public DecisionSpace(double[][] decisionSpace) {
		int len = decisionSpace.length;
		decisionSpaceItems =  new DecisionSpaceItem[len];

		for(int i=0;i<len;i++){
			decisionSpaceItems[i] = new DecisionSpaceItem();
			int max =0;
			int min =1;

			if(decisionSpace[i][0]<decisionSpace[i][1]){
				max = 1;
				min = 0;
			}
			decisionSpaceItems[i] .setMaxValue(decisionSpace[i][max]);
			decisionSpaceItems[i] .setMinValue(decisionSpace[i][min]);
		}
	}

	public double[][] getArray(){
		
		double[][] data = new double[decisionSpaceItems.length][2];
		for(int i=0;i<data.length;i++) {
			data[i][0] = decisionSpaceItems[i].getMaxValue();
			data[i][1] = decisionSpaceItems[i].getMinValue();
		}
			
		return data;
	}

	public double getRandomValue(int dimension){
		return decisionSpaceItems[dimension].getRandomValue();
	}

	public double[] getValueByType(ValueType valueType){
	
		double[] values = new double[decisionSpaceItems.length];
		
		for(int i=0;i<values.length;i++){
			values[i] = getValueByType(valueType, i);
		}
		return values;
	}

	public double getValueByType(ValueType valueType,int dimension){
		
		
		switch (valueType) {
		case MAX:
			return decisionSpaceItems[dimension].getMaxValue();
		case MID:
			double value = (decisionSpaceItems[dimension].getMaxValue()+decisionSpaceItems[dimension].getMinValue())/2;
			return value;
		case MIN:
			return decisionSpaceItems[dimension].getMinValue();
		default:
			break;
		}
		
		return decisionSpaceItems[dimension].getRandomValue();
	}


	public double[] getRandomValues(){

		int dimension = decisionSpaceItems.length;
		double[] values = new double[dimension];
		for(int i=0;i<dimension;i++){
			values[i] = getRandomValue(i);
		}

		return values;
	}

	public boolean checkRange(int dimension,double value){

		return decisionSpaceItems[dimension].checkRange(value);
	}

	public DecisionSpaceItem[] getDecisionSpaceItems() {
		return decisionSpaceItems;
	}

	public void setDecisionSpaceItems(DecisionSpaceItem[] decisionSpaceItems) {
		this.decisionSpaceItems = decisionSpaceItems;
	}

}
