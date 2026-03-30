package com.research.algorithm.optimize.intelligence;

import com.research.algorithm.optimize.common.base.Individual;
import com.research.algorithm.optimize.common.base.Population;
import com.research.algorithm.optimize.enums.CompareIndividualType;
import com.research.algorithm.optimize.enums.ValueType;
import com.research.algorithm.optimize.intelligence.common.IntelligenceAlgorithm;

import com.research.utils.numpy.RandomUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.List;
import java.util.Random;

@Getter
@Setter
@SuperBuilder
public class DE extends IntelligenceAlgorithm {

    @Builder.Default
    private double cr = 0.4;

    @Builder.Default
    private double F = 0.4;

    @Builder.Default
    RandomDataGenerator generator = new RandomDataGenerator();

    private final Random selectRand = new Random();
    private final Random switchRand = new Random();

    @Override
    public Population updatePopulation(Population population) {

        Population nextPopulation = new Population();
        for (int ind = 0; ind < population.getIndividuals().size(); ind++) {
            Individual newIndividual = updateIndividual(population, population.getIndividuals().get(ind), ind);
            nextPopulation.getIndividuals().add(newIndividual);
        }
        super.currentIteration++;
        return nextPopulation;
    }

    @Override
    public Individual updateIndividual(Population population, Individual individual, int index) {
        int dimension = problem.getDimension();
        Individual newIndividual = new Individual(dimension);
        int individualNum = population.getIndividuals().size();
        List<Individual> individuals = population.getIndividuals();
        for (int j = 0; j < dimension; j++) {
            int[] r1 = RandomUtil.randomArrayInt(0, individualNum, 3, false);
            newIndividual.getValue()[j] = individuals.get(r1[0]).getValue()[j] + F * (individuals.get(r1[1]).getValue()[j] - individuals.get(r1[2]).getValue()[j]);
            if (newIndividual.getValue()[j] > problem.getDecisionSpace().getBoundaryByTypeAndDimension(ValueType.MAX, j)) {
                newIndividual.getValue()[j] = (problem.getDecisionSpace().getBoundaryByTypeAndDimension(ValueType.MAX, j) + individual.getValue()[j]) / 2;
            } else if (newIndividual.getValue()[j] < problem.getDecisionSpace().getBoundaryByTypeAndDimension(ValueType.MIN, j)) {
                newIndividual.getValue()[j] = (problem.getDecisionSpace().getBoundaryByTypeAndDimension(ValueType.MIN, j) + individual.getValue()[j]) / 2;
            }
        }
        for (int i = 0; i < dimension; i++) {
            if (switchRand.nextDouble() > cr) {
                newIndividual.getValue()[i] = individual.getValue()[i];
            }
        }

        newIndividual.setFitness(problem.calculateIndividualFitness(newIndividual));
        individual.setFitness(problem.calculateIndividualFitness(individual));
        CompareIndividualType result = problem.compareIndividual(newIndividual, individual);
        if (result == CompareIndividualType.COMPARE_BETTER) {
            return newIndividual;
        } else {
            return individual;
        }
    }

    @Override
    public Individual updateIndividual(Population population, Individual individual, int index, int strategy, List<Integer> cluster) {
        return null;
    }


    @Override
    public Individual updateIndividual(Population population, Individual individual) {
        return null;
    }

    @Override
    public Individual updateIndividual(Population population, Individual individual, Individual otherIndividual) {
        return null;
    }

}
