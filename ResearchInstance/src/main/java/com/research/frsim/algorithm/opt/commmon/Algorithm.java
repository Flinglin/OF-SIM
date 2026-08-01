
package com.research.frsim.algorithm.opt.commmon;

import java.util.HashMap;
import java.util.Map;
import com.research.frsim.algorithm.opt.problem.Problem;
import com.research.frsim.util.Information;



public abstract class Algorithm {


	protected Problem problem;


	protected Solution solutionBest;


	protected Map<Integer,Solution> solutions;
	

	protected Information information;

	public Algorithm(Problem problem) {
		this.problem = problem;
		solutionBest = new Solution();
		solutions = new HashMap<Integer,Solution>();
		information = new Information();
	}

	public abstract Information execute();
	
	
	public abstract void setBestSolution();
	
	public Solution getSolutionBest() {
		return solutionBest;
	}

	public void setSolutionBest(Solution solutionBest) {
		this.solutionBest = solutionBest;
	}

	public Map<Integer, Solution> getSolutions() {
		return solutions;
	}

	public void setSolutions(Map<Integer, Solution> solutions) {
		this.solutions = solutions;
	}

	public Information getInformation() {
		return information;
	}

	public void setInformation(Information information) {
		this.information = information;
	}
	
	
}

