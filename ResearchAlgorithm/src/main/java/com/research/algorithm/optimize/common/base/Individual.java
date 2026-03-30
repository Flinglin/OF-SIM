package com.research.algorithm.optimize.common.base;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Individual {

    protected double[] value;

    protected Fitness fitness;

    public Individual(int dimension){
        this.value = new double[dimension];
    }

}
