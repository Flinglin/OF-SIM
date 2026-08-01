package com.research.frsim.algorithm.opt.commmon;

import com.research.frsim.algorithm.opt.problem.Fitness;
import com.research.frsim.util.Information;

public class Individual {

	protected double[] values;

	protected Fitness fitness;

	private int grade;
	

	private double crowdingDistance;

	public Individual() {

	}

	public Individual(int dimension) {

		values = new double[dimension];
	}

	protected Object attachment;


	public static Information compare(Individual a,Individual b){

		double[] result = new double[a.getValues().length];
		double delta=0;
		for(int i=0;i<a.getValues().length;i++){

			result[i] = a.getValues()[i]-b.getValues()[i];
			delta = delta+result[i]*result[i];
		}
		return null;
	}

	public static void copyAtoB(Individual a,Individual b) {
		for (int i=0;i<a.getValues().length;i++) {
			b.getValues()[i] = a.getValues()[i];
		}
	}

	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
	}

	public Object getAttachment() {
		return attachment;
	}

	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	public Fitness getFitness() {
		return fitness;
	}

	public void setFitness(Fitness fitness) {
		this.fitness = fitness;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public double getCrowdingDistance() {
		return crowdingDistance;
	}

	public void setCrowdingDistance(double crowdingDistance) {
		this.crowdingDistance = crowdingDistance;
	}
}
