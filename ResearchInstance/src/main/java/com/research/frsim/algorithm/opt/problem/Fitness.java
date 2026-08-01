package com.research.frsim.algorithm.opt.problem;

import java.util.ArrayList;
import java.util.List;

public class Fitness {

	private double[] fitness;

	private boolean feasible;

	private double teminaValue;

	private List<Double> constraintViolations ;

	public boolean isEstimated;
	public Fitness(double[] fitness) {}

	public Fitness(int objectiveNum) {
		fitness = new double[objectiveNum];
		constraintViolations = new ArrayList<Double>();
	}


	@Override
	public String toString() {

		StringBuilder fitnessString = new StringBuilder();
        for (double v : fitness) {
            fitnessString.append(v).append("\t");
        }
		return fitnessString.toString();
	}

	public double[] getFitness() {
		return fitness;
	}

	public void setFitness(double[] fitness) {
		this.fitness = fitness;
	}



	public double getTeminaValue() {
		return teminaValue;
	}


	public void setTeminaValue(double teminaValue) {
		this.teminaValue = teminaValue;
	}


	public boolean isFeasible() {
		return feasible;
	}

	public void setFeasible(boolean feasible) {
		this.feasible = feasible;
	}

	public List<Double> getConstraintViolations() {
		return constraintViolations;
	}

	public void setConstraintViolations(List<Double> constraintViolations) {
		this.constraintViolations = constraintViolations;
	}

}
