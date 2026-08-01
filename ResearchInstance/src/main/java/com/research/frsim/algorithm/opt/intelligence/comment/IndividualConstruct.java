
package com.research.frsim.algorithm.opt.intelligence.comment;

import com.research.frsim.algorithm.opt.intelligence.comment.interfaces.IndividualConstructInterface;
import com.research.frsim.algorithm.opt.problem.Problem;

public abstract class IndividualConstruct  implements IndividualConstructInterface{

	protected Problem problem;

	public IndividualConstruct(Problem problem) {
		this.problem = problem;
	}
}
