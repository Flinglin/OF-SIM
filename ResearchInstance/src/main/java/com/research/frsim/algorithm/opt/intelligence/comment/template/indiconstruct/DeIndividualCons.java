
package com.research.frsim.algorithm.opt.intelligence.comment.template.indiconstruct;

import java.util.List;
import java.util.Random;
import com.research.frsim.algorithm.opt.commmon.Individual;
import com.research.frsim.algorithm.opt.commmon.Population;
import com.research.frsim.algorithm.opt.intelligence.comment.IndividualConstruct;
import com.research.frsim.algorithm.opt.problem.Problem;

public class DeIndividualCons extends IndividualConstruct{

	public  double  cr=0.6;
	public  double  F=0.4;

	public static Random selectrand = new Random();
	public static Random switchrand = new Random();

	public DeIndividualCons(Problem problem) {
		super(problem);

	}

	@Override
	public Individual updateIndividual(Population population,
			Individual individual) {

		Individual newIndividual = new Individual(problem.getDimension());


		int len = problem.getDimension();
		int size = population.getIndividuals().size();
		List<Individual> inds = population.getIndividuals();

		int id =0;
		for(int i=0;i<len;i++){
			if(individual == population.getIndividuals().get(i)){
				id = i;
				break;
			}
		}

		int[] r=new int[3];
		for(int j=0;j< len;j++)
		{
			int count = 0;
			do
			{
				r[0]=selectrand.nextInt(size);
				r[1]=selectrand.nextInt(size);
				r[2]=selectrand.nextInt(size);
				newIndividual.getValues()[j]= inds.get(r[0]).getValues()[j]+F*(inds.get(r[1]).getValues()[j]-inds.get(r[2]).getValues()[j]);
				count++;
				if(count>100){

					newIndividual.setValues(problem.getDecisionSpace().getRandomValues());
					break;
				}
			}while(newIndividual.getValues()[j] > problem.getDecisionSpace().getDecisionSpaceItems()[j].getMaxValue() || 
					newIndividual.getValues()[j] < problem.getDecisionSpace().getDecisionSpaceItems()[j].getMinValue());
			
		}
		


		for(int j=0;j<len;j++)
		{

			if(switchrand.nextDouble() > cr) {
				newIndividual.getValues()[j]=individual.getValues()[j];
			}
		}
		

		problem.calculateFitness(newIndividual);
		problem.calculateFitness(individual);
		int result = problem.Compare(newIndividual,individual);
		if(result == Problem.COMPARE_BETTER){
			return newIndividual;
		}else{
			return individual;
		}
		

	}

}
