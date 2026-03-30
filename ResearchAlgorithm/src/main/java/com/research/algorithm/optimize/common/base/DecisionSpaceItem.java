package com.research.algorithm.optimize.common.base;

import lombok.Data;
import java.lang.Math;

@Data
public class DecisionSpaceItem {

    private double minValue;

    private double maxValue;

    public DecisionSpaceItem(double minValue, double maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    public boolean IsBetweenBoundary(double value) {
        return value < maxValue + 1e-5 && value > minValue - 1e-5;
    }

    public double getRandomValue() {
        return Math.random() * (maxValue - minValue) + minValue;
    }

    public boolean checkRange(double value){
        return value < maxValue + 1e-5 && value > minValue - 1e-5;
    }
}
