package com.research.frsim.algorithm.opt.intelligence;
import java.util.Collections;
import java.util.Comparator;
import com.research.frsim.algorithm.opt.commmon.Algorithm;
import com.research.frsim.algorithm.opt.commmon.Individual;
import com.research.frsim.algorithm.opt.commmon.Population;
import com.research.frsim.algorithm.opt.intelligence.comment.IndividualConstruct;
import com.research.frsim.algorithm.opt.intelligence.comment.LocalSearch;
import com.research.frsim.algorithm.opt.intelligence.comment.PopulationUpdate;
import com.research.frsim.algorithm.opt.intelligence.comment.interfaces.IndividualConstructInterface;
import com.research.frsim.algorithm.opt.intelligence.comment.interfaces.LoacalSearchInterface;
import com.research.frsim.algorithm.opt.intelligence.comment.interfaces.PopulationUpdateInterface;
import com.research.frsim.algorithm.opt.problem.Problem;
import com.research.frsim.util.Information;

public abstract class IntelligenceAlgorithm extends Algorithm implements PopulationUpdateInterface,IndividualConstructInterface,LoacalSearchInterface{


	protected Population population;


	protected Population archive;


	protected  LocalSearch localSearch;


	protected IndividualConstruct individualConstruct;


	protected PopulationUpdate populationUpdate;

	protected int populationSize = 200;

	protected int archiveSize = 50;


	protected int iterations = 1000;


	protected int currentIter = 0;


	public int[] statics;
	public int c;
	public IntelligenceAlgorithm(Problem problem,int c) {
		super(problem);
		initComponent();
		statics=new int[iterations+3];
		this.c=c;
	}

	public abstract void initComponent();

	public Information execute(){
		initComponent();
		initAlgorithm();
		initPopulation();

		Individual in = new Individual(problem.getDimension());

		do {
			population = updatePopulation(population);
			for(Individual ind:population.getIndividuals()){
				if(ind.getFitness().isEstimated){
					this.statics[currentIter]++;
					ind.getFitness().isEstimated=false;
				}
			}
			updateArchive();
			rank(population);

			for(int i=0;i<problem.getDimension();i++){
				in.getValues()[i] = population.getIndividuals().get(0).getValues()[i];
			}
			

		}
		while(termination());

		setBestSolution();
		if (c==1){
			System.out.println("the best solution is：water deficit: "+population.getIndividuals().get(0).getFitness().getFitness()[0]+", surplus water: "+population.getIndividuals().get(0).getFitness().getFitness()[1]);
		}
		return information;
	}

	@Override
	public void setBestSolution(){
		solutionBest.getSolution().add(getPopulation().getIndividuals().get(0)); 
	}

	public void initAlgorithm(){


		currentIter = 0;

		population = problem.generatPopulation(populationSize);
		problem.calculateFitness(population);
		

		archive = problem.generatPopulation(archiveSize);
	}


	public void initPopulation(){

	}


	public abstract Population updateArchive();


	public void rank(Population pop) {

		Comparator<Individual> comp=new Comparator<Individual>(){

			@Override
			public int compare(Individual o1, Individual o2) {

				int result = 0;
				int compare = problem.Compare(o1, o2);
				if( compare == Problem.COMPARE_BETTER) {
					result = -1;
				} else  if ( compare == Problem.COMPARE_EQUAL){
					result = 0;
				}else {
					result = 1;
				}
				return result;
			}
		};

		Collections.sort(pop.getIndividuals(), comp);
	}



	protected boolean termination(){

		boolean condition = true;


		if( currentIter > iterations) {
			condition = false;
		}

		return condition;
	}



	public Population getArchive() {
		return archive;
	}

	public void setArchive(Population archive) {
		this.archive = archive;
	}


	public int getArchiveSize() {
		return archiveSize;
	}

	public void setArchiveSize(int archiveSize) {
		this.archiveSize = archiveSize;
	}

	public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public Population getPopulation() {
		return population;
	}

	public void setPopulation(Population population) {
		this.population = population;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

}
