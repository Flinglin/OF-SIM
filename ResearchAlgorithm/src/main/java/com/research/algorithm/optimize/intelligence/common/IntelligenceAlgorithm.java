package com.research.algorithm.optimize.intelligence.common;

import com.research.algorithm.optimize.common.base.Algorithm;
import com.research.algorithm.optimize.common.base.Individual;
import com.research.algorithm.optimize.common.base.Population;
import com.research.algorithm.optimize.enums.CompareIndividualType;
import com.research.algorithm.optimize.utils.Information;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Getter
@Setter
@SuperBuilder
public abstract class IntelligenceAlgorithm extends Algorithm {
    protected Population population;
    protected Population archive;
    @Builder.Default
    protected int populationSize = 200;
    @Builder.Default
    protected int archiveSize = 50;
    @Builder.Default
    protected int iterations = 1000;
    @Builder.Default
    protected int currentIteration = 1;
    public abstract Population updatePopulation(Population pop);
    public abstract Individual updateIndividual(Population pop, Individual ind);
    public abstract Individual updateIndividual(Population pop, Individual ind, int i);

    public abstract Individual updateIndividual(Population population, Individual individual, int index, int strategy, List<Integer> cluster);
    public abstract Individual updateIndividual(Population pop, Individual ind, Individual otherInd);
    public void initialAlgorithm() {
        this.population = generatePopulation(this.populationSize);
        this.problem.calculatePopulationFitness(this.population);
        this.archive = new Population();
    }
    public Population generatePopulation(int populationSize) {
        Population population = new Population(populationSize);
        for (int i = 0; i < populationSize; i++) {
            Individual individual = new Individual();
            individual.setValue(this.problem.getDecisionSpace().getRandomValues());
            population.getIndividuals().add(individual);
        }
        return population;
    }
    public void rankIndividualByFitness(List<? extends Individual> individualList) {

        Comparator<Individual> individualComparator = (o1, o2) -> {
            int result = 0;
            CompareIndividualType comp = problem.compareIndividual(o1, o2);
            switch (comp) {
                case COMPARE_BETTER -> result = -1;
                case COMPARE_WORSE -> result = 1;
            }
            return result;
        };
        individualList.sort(individualComparator);
    }
    protected boolean terminate() {
        return this.currentIteration <= this.iterations;
    }
    @Override
    protected void setBestSolution() {
        solutionBest.getBestIndividualList().add(population.getIndividuals().getFirst());
    }
    @Override
    public Information execute(){
        initialAlgorithm();
        rankIndividualByFitness(population.getIndividuals());
        setBestSolution();
        do {
            population = updatePopulation(population);
            rankIndividualByFitness(population.getIndividuals());
            setBestSolution();
            if (this.currentIteration>this.iterations) {
                double[] fitness=population.getIndividuals().getFirst().getFitness().getValue();
                System.out.println("the best solution is：water deficit: "+fitness[0]*10000+", surplus water: "+fitness[1]*10000);
            }
        } while (terminate());
        return super.getInformation();
    }
}
