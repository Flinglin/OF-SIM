package com.research.algorithm.optimize.common.base;

import com.research.algorithm.optimize.enums.CompareIndividualType;
import com.research.algorithm.optimize.enums.OptimizeType;

import java.util.List;
import lombok.*;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Problem {

    protected int objectNum;

    protected int dimension;

    protected int stage;

    protected int subDimension;

    protected List<OptimizeType> optimizeType;
    protected DecisionSpace decisionSpace;

    public Problem(int objectNum, int dimension, int stage, int subDimension, List<OptimizeType> optimizeType,double[][] decisionSpaceMatrix) {
        this.objectNum = objectNum;
        this.dimension = dimension;
        this.stage = stage;
        this.subDimension = subDimension;
        this.optimizeType=optimizeType;
        this.decisionSpace=new DecisionSpace(decisionSpaceMatrix);
    }
    public abstract Fitness calculateIndividualFitness(Individual individual);

    public void calculatePopulationFitness(Population population) {
        for(Individual individual: population.getIndividuals()){
            individual.setFitness(calculateIndividualFitness(individual));
        }
    }
    public void calculatePopulationFitness(List<Individual> individualList) {
        for(Individual individual: individualList){
            individual.setFitness(calculateIndividualFitness(individual));
        }
    }

    public CompareIndividualType compareIndividual(Individual ind1, Individual ind2) {
        int objectNum=this.objectNum;
        List<OptimizeType> optimizeType=this.optimizeType;
        if(objectNum==1){
            if(optimizeType.getFirst() == OptimizeType.MAXIMUM){
                if(ind1.getFitness().getValue()[0]<ind2.getFitness().getValue()[0]){
                    return CompareIndividualType.COMPARE_WORSE;
                }else if(ind1.getFitness().getValue()[0]>ind2.getFitness().getValue()[0]) {
                    return CompareIndividualType.COMPARE_BETTER;
                } else if (ind1.getFitness().getValue()[0]==ind2.getFitness().getValue()[0]) {
                    return CompareIndividualType.COMPARE_EQUAL;
                }
            }else if(optimizeType.getFirst() ==OptimizeType.MINIMUM){
                if(ind1.getFitness().getValue()[0]<ind2.getFitness().getValue()[0]){
                    return CompareIndividualType.COMPARE_BETTER;
                }else if(ind1.getFitness().getValue()[0]>ind2.getFitness().getValue()[0]) {
                    return CompareIndividualType.COMPARE_WORSE;
                } else if (ind1.getFitness().getValue()[0]==ind2.getFitness().getValue()[0]) {
                    return CompareIndividualType.COMPARE_EQUAL;
                }
            }
        }else{
            int better = 0;
            int worse = 0;
            for(int i=0;i<objectNum;i++){
                if(ind1.getFitness().getValue()[i]<ind2.getFitness().getValue()[i]){
                    if(optimizeType.get(i) == OptimizeType.MAXIMUM){
                        worse++;
                    }else{
                        better++;
                    }
                }else{
                    if(optimizeType.get(i) == OptimizeType.MAXIMUM){
                        better++;
                    }else{
                        worse++;
                    }
                }
            }
            if(better == objectNum){
                return CompareIndividualType.COMPARE_BETTER;
            }
            if(worse == objectNum){
                return CompareIndividualType.COMPARE_WORSE;
            }
            return CompareIndividualType.COMPARE_EQUAL;
        }
        return null;
    }
    public CompareIndividualType CompareIndividualByDimension(Individual ind1, Individual ind2, int dimension) {
        List<OptimizeType> optimizeType=this.optimizeType;
        if (ind1.getFitness().getValue()[dimension] < ind2.getFitness().getValue()[dimension]) {

            if (optimizeType.get(dimension) == OptimizeType.MAXIMUM) {
                return CompareIndividualType.COMPARE_WORSE;
            } else {
                return CompareIndividualType.COMPARE_BETTER;
            }
        } else if (ind1.getFitness().getValue()[dimension] > ind2.getFitness().getValue()[dimension]) {
            if (optimizeType.get(dimension) == OptimizeType.MAXIMUM) {
                return CompareIndividualType.COMPARE_BETTER;
            } else {
                return CompareIndividualType.COMPARE_WORSE;
            }
        } else {
            return CompareIndividualType.COMPARE_EQUAL;
        }
    }
    public CompareIndividualType compareIndividualByTarget(Individual a, Individual b, int target) {

        if (a.getFitness().getValue()[target] < b.getFitness().getValue()[target]) {

            if (optimizeType.get(target) == OptimizeType.MAXIMUM) {
                return CompareIndividualType.COMPARE_WORSE;
            } else {
                return CompareIndividualType.COMPARE_BETTER;
            }
        } else if (a.getFitness().getValue()[target] > b.getFitness().getValue()[target]) {
            if (optimizeType.get(target) == OptimizeType.MAXIMUM) {
                return CompareIndividualType.COMPARE_BETTER;
            } else {
                return CompareIndividualType.COMPARE_WORSE;
            }
        } else {
            return CompareIndividualType.COMPARE_EQUAL;
        }
    }

}
