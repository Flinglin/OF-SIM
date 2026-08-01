package com.research.frsim.algorithm.opt.commmon;

import java.util.ArrayList;
import java.util.List;

public class Population {


	private int size = 100;

	private List<Individual> individuals;

	public Population() {
		individuals = new ArrayList<Individual>();
	}

	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public List<Individual> getIndividuals() {
		return individuals;
	}
	public void setIndividuals(List<Individual> individuals) {
		this.individuals = individuals;
	}

}
