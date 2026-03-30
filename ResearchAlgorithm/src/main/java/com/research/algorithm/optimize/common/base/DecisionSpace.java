package com.research.algorithm.optimize.common.base;

import com.research.algorithm.optimize.enums.ValueType;
import lombok.Data;

@Data
public class DecisionSpace {
    private DecisionSpaceItem[] decisionSpaceItems;

    public DecisionSpace(double[][] decisionSpaceMatrix) {
        decisionSpaceItems = new DecisionSpaceItem[decisionSpaceMatrix.length];
        for (int i = 0; i < decisionSpaceMatrix.length; i++) {
            decisionSpaceItems[i] = new DecisionSpaceItem(Math.min(decisionSpaceMatrix[i][0], decisionSpaceMatrix[i][1]), Math.max(decisionSpaceMatrix[i][0], decisionSpaceMatrix[i][1]));
        }
    }

    public double[][] getDecisionSpaceAsArray() {
        double[][] result = new double[2][decisionSpaceItems.length];
        for (int i = 0; i < decisionSpaceItems.length; i++) {
            result[0][i]=decisionSpaceItems[i].getMinValue();
            result[1][i]=decisionSpaceItems[i].getMaxValue();
        }
        return result;
    }

    public double getRandomValue(int dimension){
        return decisionSpaceItems[dimension].getRandomValue();
    }

    public double[] getRandomValues() {
        double[] result = new double[decisionSpaceItems.length];
        for (int i = 0; i < decisionSpaceItems.length; i++) {
            result[i]=getRandomValue(i);
        }
        return result;
    }

    public boolean checkRange(int dimension,double value){
        return decisionSpaceItems[dimension].checkRange(value);
    }

    public double getBoundaryByTypeAndDimension(ValueType type, int dimension){
        switch (type){
            case MAX -> {
                return decisionSpaceItems[dimension].getMaxValue();
            }
            case MIN -> {
                return decisionSpaceItems[dimension].getMinValue();
            }
            default -> {
                return 0;
            }
        }
    }

    public double getRangeByDimension(int dimension){
        return decisionSpaceItems[dimension].getMaxValue()-decisionSpaceItems[dimension].getMinValue();
    }

}
