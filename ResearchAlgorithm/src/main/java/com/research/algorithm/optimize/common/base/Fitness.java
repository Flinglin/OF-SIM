package com.research.algorithm.optimize.common.base;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Fitness {

    private double[] value;

    private boolean feasible;

    private double terminalValue;

    private List<Double> constraintViolations;

    public Fitness(int objectiveNum) {
        this.value = new double[objectiveNum];
        this.constraintViolations = new ArrayList<>();
    }
}
