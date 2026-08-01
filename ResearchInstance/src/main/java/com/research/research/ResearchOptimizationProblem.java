package com.research.research;

import com.research.algorithm.optimize.common.base.DecisionSpace;
import com.research.algorithm.optimize.common.base.Fitness;
import com.research.algorithm.optimize.common.base.Individual;
import com.research.algorithm.optimize.common.base.Problem;
import com.research.algorithm.optimize.enums.CompareIndividualType;
import com.research.algorithm.optimize.enums.OptimizeType;
import com.research.core.entity.gate.GateEntity;
import com.research.core.entity.intake.IntakeEntity;
import com.research.core.entity.pump.PumpEntity;
import com.research.core.entity.reservoir.ReservoirEntity;
import com.research.core.project.Project;
import com.research.utils.numpy.ArrayMathUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static com.research.utils.numpy.ArrayUtil.zip;
import static com.research.utils.numpy.ArrayUtil.concat2DArray;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Slf4j
public class ResearchOptimizationProblem extends Problem {

    public ResearchReverseSimulation simulation;
    private Project project;


    public ResearchOptimizationProblem(int objectNum, int dimension, List<OptimizeType> optimizeType, ResearchReverseSimulation simulation, Project project) {
        super.objectNum = objectNum;
        super.dimension = dimension;
        super.optimizeType = optimizeType;
        this.simulation = simulation;
        this.project = project;
        double[][] limit = null;
        for (int i = 0; i < project.getPumpEntities().size(); i++) {
            if (limit == null) {
                limit = zip(project.getPumpEntities().get(i).getMinFlow(), project.getPumpEntities().get(i).getMaxFlow());
            } else {
                limit = concat2DArray(limit, zip(project.getPumpEntities().get(i).getMinFlow(), project.getPumpEntities().get(i).getMaxFlow()));
            }
        }
        double[][] rate = new double[36][];
        double[] range = {0, 1};
        for (int q = 0; q < 36; q++) {
            rate[q] = range.clone();
        }
        limit = concat2DArray(limit, rate);
        super.decisionSpace = new DecisionSpace(limit);
    }

    @Override
    public Fitness calculateIndividualFitness(Individual individual) {
        int timeLength = this.project.getTimeUnits().size();
        this.simulation.initializeSimulationModel();
        this.simulation.assignSimulation(individual, timeLength);

        this.simulation.reverseSimulate();

        double sumWaterShortage = 0;
        double[] waterShortage = new double[timeLength];
        for (int r = 0; r < timeLength; r++) {
            double sumintake = 0;
            double sumdemand = 0;
            for (int i = 0; i < this.simulation.getIntakeEntities().size(); i++) {
                sumintake += this.simulation.getIntakeEntities().get(i).getTruthIntakeFlow()[r];
                sumdemand += this.simulation.getIntakeEntities().get(i).getPlanIntakeFlow()[r];
            }
            waterShortage[r] = Math.abs(sumdemand - sumintake) * 10 * 24 * 3600;
        }
        for (int i = 0; i < this.simulation.getIntakeEntities().size(); i++) {
            double[] intakeFlow = this.simulation.getIntakeEntities().get(i).getTruthIntakeFlow();
            double[] demandFlow = this.simulation.getIntakeEntities().get(i).getPlanIntakeFlow();
            for (int j = 0; j < intakeFlow.length; j++) {
                if (demandFlow[j] > intakeFlow[j]) {
                    sumWaterShortage += Math.abs(demandFlow[j] - intakeFlow[j]) * 10 * 24 * 3600 / Math.pow(10, 4);
                }
            }
        }
        double levelStd = 0;
        for (ReservoirEntity entity : this.simulation.getReservoirEntities()) {
            if (entity.getName().equals("h_reservoir")) {
                continue;
            }
            levelStd += Arrays.stream(entity.getWasteWater()).sum() / Math.pow(10, 4);
        }

        Fitness fitness = new Fitness(super.objectNum);
        for (int i = 0; i < this.simulation.getIntakeEntities().size(); i++) {
            IntakeEntity intakeEntity = this.simulation.getIntakeEntities().get(i);
            for (int j = 0; j < intakeEntity.getTruthIntakeFlow().length; j++) {
                if (intakeEntity.getTruthIntakeFlow()[j] < intakeEntity.getMinIntakeFlow()[j] || intakeEntity.getTruthIntakeFlow()[j] > intakeEntity.getMaxIntakeFlow()[j] + 1e-10) {
                    fitness.setFeasible(false);
                    fitness.setValue(new double[]{sumWaterShortage * 1000, levelStd * 1000});
                    individual.setFitness(fitness);
                    return fitness;
                }
            }
        }
        for (int i = 0; i < this.simulation.getGateEntities().size(); i++) {
            GateEntity gateEntity = this.simulation.getGateEntities().get(i);
            for (int j = 0; j < gateEntity.getAvgFlow().length; j++) {
                if (Math.abs(gateEntity.getAvgFlow()[j]) > gateEntity.getMaxFlow()[j] + 1e-10) {
                    fitness.setFeasible(false);
                    fitness.setValue(new double[]{sumWaterShortage * 1000, levelStd * 1000});
                    individual.setFitness(fitness);
                    return fitness;
                }
            }
        }

        for (int i = 0; i < this.simulation.getPumpEntities().size(); i++) {
            PumpEntity pumpEntity = this.simulation.getPumpEntities().get(i);
            for (int j = 0; j < pumpEntity.getAvgFlow().length; j++) {
                if (Math.abs(pumpEntity.getAvgFlow()[j]) > pumpEntity.getMaxFlow()[j] + 1e-10) {
                    fitness.setFeasible(false);
                    fitness.setValue(new double[]{sumWaterShortage * 1000, levelStd * 1000});
                    individual.setFitness(fitness);
                    return fitness;
                }
            }
        }

        for (int i = 0; i < this.simulation.getReservoirEntities().size(); i++) {
            ReservoirEntity entity = this.simulation.getReservoirEntities().get(i);
            for (int j = 0; j < entity.getOutFlow().length; j++) {
                if (Double.isNaN(entity.getReservoirLevel()[j])) {
                    throw new RuntimeException("check the project parameter");
                }
                if (entity.getOutFlow()[j] > entity.getOutMaxFlow()[j] + 1e-10 || entity.getOutFlow()[j] < entity.getOutMinFlow()[j]) {
                    fitness.setFeasible(false);
                    fitness.setValue(new double[]{sumWaterShortage * 1000, levelStd * 1000});
                    individual.setFitness(fitness);
                    return fitness;
                }
            }
            for (int j = 0; j < entity.getInFlow().length; j++) {
                if (entity.getInFlow()[j] > entity.getInMaxFlow()[j] + 1e-10 || entity.getInFlow()[j] < entity.getInMinFlow()[j]) {
                    fitness.setFeasible(false);
                    fitness.setValue(new double[]{sumWaterShortage * 1000, levelStd * 1000});
                    individual.setFitness(fitness);
                    return fitness;
                }
            }
            for (int j = 0; j < entity.getMinLevel().length; j++) {
                if (!entity.isLatency()&&entity.getReservoirLevel()[j+1]<entity.getMinLevel()[j] || entity.getReservoirLevel()[j+1] > entity.getMaxLevel()[j]) {
                    fitness.setFeasible(false);
                    fitness.setValue(new double[]{sumWaterShortage * 1000, levelStd * 1000});
                    individual.setFitness(fitness);
                    return fitness;
                }
            }
        }
        fitness.setFeasible(true);
        fitness.setValue(new double[]{sumWaterShortage, levelStd});
        individual.setFitness(fitness);
        return fitness;
    }

    @Override
    public CompareIndividualType compareIndividual(Individual a, Individual b) {
        double A = ArrayMathUtil.sumWithWeight(a.getFitness().getValue(), 0.7, 0.3);
        double B = ArrayMathUtil.sumWithWeight(b.getFitness().getValue(), 0.7, 0.3);
        a.getFitness().setTerminalValue(A);
        b.getFitness().setTerminalValue(B);
        return A==B? CompareIndividualType.COMPARE_EQUAL : A<B? CompareIndividualType.COMPARE_BETTER : CompareIndividualType.COMPARE_WORSE;
    }
}
