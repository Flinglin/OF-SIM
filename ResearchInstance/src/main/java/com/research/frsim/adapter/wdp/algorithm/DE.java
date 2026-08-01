
package com.research.frsim.adapter.wdp.algorithm;

import com.research.frsim.algorithm.opt.commmon.Individual;
import com.research.frsim.algorithm.opt.commmon.Population;
import com.research.frsim.algorithm.opt.intelligence.IntelligenceAlgorithm;
import com.research.frsim.algorithm.opt.intelligence.comment.template.indiconstruct.DeIndividualCons;
import com.research.frsim.algorithm.opt.intelligence.comment.template.localsearch.DeLocalSearch;
import com.research.frsim.algorithm.opt.intelligence.comment.template.popupdate.DePopulationUpdate;
import com.research.frsim.algorithm.opt.problem.Problem;

public class DE extends IntelligenceAlgorithm{

	public  double  cr = 0.6;
	public  double  F = 0.4;

	public DE(Problem problem,int c) {
		super(problem,c);
	}

	@Override
	public void initComponent() {
		localSearch = new DeLocalSearch(problem);
		populationUpdate = new DePopulationUpdate(problem);
		individualConstruct = new DeIndividualCons(problem);
		((DeIndividualCons)individualConstruct).cr = cr;
		((DeIndividualCons)individualConstruct).F =  F;
	}


	@Override
	public Population updatePopulation(Population population) {
		Population nextPopulation = new Population();
		for(Individual individual:population.getIndividuals()) {
			Individual newindividual = updateIndividual(population, individual);
			nextPopulation.getIndividuals().add(newindividual);
		}
		currentIter++;
		return nextPopulation;
	}

	@Override
	public Individual updateIndividual(Population population,
			Individual individual) {
		return individualConstruct.updateIndividual(population, individual);
	}

	@Override
	public Individual localSearch(Individual individual) {

		return null;
	}


	@Override
	public Population updateArchive() {

		return null;
	}

}
