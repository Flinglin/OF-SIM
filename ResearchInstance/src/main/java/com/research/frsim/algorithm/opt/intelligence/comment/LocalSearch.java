
package com.research.frsim.algorithm.opt.intelligence.comment;

import com.research.frsim.algorithm.opt.intelligence.comment.interfaces.LoacalSearchInterface;
import com.research.frsim.algorithm.opt.problem.Problem;

public abstract class LocalSearch implements LoacalSearchInterface{


	protected Problem problem;

	public LocalSearch(Problem problem) {
		this.problem = problem;
	}

}
