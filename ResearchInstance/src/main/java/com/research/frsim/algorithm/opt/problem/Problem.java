package com.research.frsim.algorithm.opt.problem;

import com.research.frsim.algorithm.opt.commmon.Individual;
import com.research.frsim.algorithm.opt.commmon.Population;

public abstract class Problem {

	public static final int MAXIMUM = 10001;
	public static final int MINIMUM = 10002;
	public static final int COMPARE_EQUAL = 11001;
	public static final int COMPARE_BETTER = 11002;
	public static final int COMPARE_WORSE = 11003;

	protected int objectiveNum;
	

	protected int dimension;

	protected int stage;

	protected int subdimension;

	protected int[] optimalType;

	protected DecisionSpace decisionSpace;

	public abstract Fitness calculateFitness(Individual individual);

	public Population generatPopulation(int size){
		Population population = new Population();
		for(int i=0;i<size;i++){
			Individual individual = new Individual();
			individual.setValues(decisionSpace.getRandomValues());
			population.getIndividuals().add(individual);
		}
		return population;


	}

	public abstract Fitness calculateFitness(Individual individual,int index);

	public void calculateFitness(Population population){

		for(Individual individual:population.getIndividuals()) {

			
			individual.setFitness(calculateFitness(individual));
		}

	}

	public int Compare(Individual a,Individual b){

		if(objectiveNum == 1){

			if(a.getFitness().getFitness()[0]<b.getFitness().getFitness()[0]){

				if(optimalType[0] == MAXIMUM){
					return  COMPARE_WORSE;
				}else{
					return COMPARE_BETTER;
				}
			} else if (a.getFitness().getFitness()[0]>b.getFitness().getFitness()[0]){
				if(optimalType[0] == MAXIMUM){
					return  COMPARE_BETTER;
				}else{
					return COMPARE_WORSE;
				}
			}else{
				return COMPARE_EQUAL;
			}
		}else {

			int better = 0;
			int worse = 0;
			for(int i=0;i<objectiveNum;i++){
				if(a.getFitness().getFitness()[i]<b.getFitness().getFitness()[i]){
					if(optimalType[i] == MAXIMUM){
						worse++;
					}else{
						better++;
					}
				}else{
					if(optimalType[i] == MAXIMUM){
						better++;
					}else{
						worse++;
					}
				}
			}


			if(better == objectiveNum){
				return COMPARE_BETTER;
			}

			if(worse == objectiveNum){
				return COMPARE_WORSE;
			}
			
			return COMPARE_EQUAL;
		}
	}
	

	public int CompareFiti(Individual a, Individual b) {

		double A = sumWithWeight(a.getFitness().getFitness(), 0.7, 0.3);
		double B = sumWithWeight(b.getFitness().getFitness(), 0.7, 0.3);
		return A==B? COMPARE_EQUAL : A<B? COMPARE_BETTER : COMPARE_WORSE;
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

	public abstract Object[][] printSolution(Individual individual);

	public int getObjectiveNum() {
		return objectiveNum;
	}


	public void setObjectiveNum(int objectiveNum) {
		this.objectiveNum = objectiveNum;
	}


	public int getDimension() {
		return dimension;
	}


	public void setDimension(int dimension) {
		this.dimension = dimension;
	}
	
	

	public int getSubdimension() {
		return subdimension;
	}

	public void setSubdimension(int subdimension) {
		this.subdimension = subdimension;
	}

	public DecisionSpace getDecisionSpace() {
		return decisionSpace;
	}


	public void setDecisionSpace(DecisionSpace decisionSpace) {
		this.decisionSpace = decisionSpace;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public int[] getOptimalType() {
		return optimalType;
	}

	public void setOptimalType(int[] optimalType) {
		this.optimalType = optimalType;
	}
}
