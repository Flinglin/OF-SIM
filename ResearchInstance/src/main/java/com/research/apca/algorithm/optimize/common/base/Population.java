package com.research.apca.algorithm.optimize.common.base;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Population {

    protected int defaultPopulationSize = 100;

    protected List<Individual> individuals = new ArrayList<>();

    private Random random;

    public Population(int defaultPopulationSize) {
        this.defaultPopulationSize = defaultPopulationSize;
    }

    public Individual getRandomIndividual() {
        if (random == null) {
            random = new Random();
        }
        return individuals.get(random.nextInt(individuals.size()));
    }
    public double[] getMeanIndividual(int dimension) {
        double[] result = new double[dimension];
        double sum = 0;
        for (int i = 0; i < dimension; i++) {
            for (Individual individual : individuals) {
                sum += individual.getValue()[i];
            }
            result[i] = sum / defaultPopulationSize;
            sum = 0;
        }
        return result;
    }

    public double[][] toArray() {
        double[][] result = new double[individuals.size()][145];
        for (int i = 0; i < individuals.size(); i++) {
            double[] temp=new double[145];
            System.arraycopy(individuals.get(i).getValue(),0,temp,0,individuals.get(i).getValue().length);
            result[i][144]=individuals.get(i).getFitness().getTerminalValue();
            result[i]=temp;
        }
        return result;
    }
}
