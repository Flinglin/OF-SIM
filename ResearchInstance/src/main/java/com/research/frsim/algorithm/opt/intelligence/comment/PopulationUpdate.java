
package com.research.frsim.algorithm.opt.intelligence.comment;

import com.research.frsim.algorithm.opt.intelligence.comment.interfaces.PopulationUpdateInterface;
import com.research.frsim.algorithm.opt.problem.Problem;

public abstract class PopulationUpdate  implements PopulationUpdateInterface{

	protected Problem problem;

	public PopulationUpdate(Problem problem) {
		this.problem = problem;
	}
}
