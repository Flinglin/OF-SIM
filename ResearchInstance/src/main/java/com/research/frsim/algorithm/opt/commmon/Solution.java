package com.research.frsim.algorithm.opt.commmon;

import java.util.ArrayList;
import java.util.List;

public class Solution {

	protected List<Individual> solution;

	public Solution() {
		solution = new ArrayList<Individual>();
	}


	public List<Individual> getSolution() {
		return solution;
	}

	public void setSolution(List<Individual> solution) {
		this.solution = solution;
	}
}
