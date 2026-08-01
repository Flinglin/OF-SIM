package com.research.apca.algorithm.optimize.common.base;

import com.research.apca.algorithm.optimize.utils.Information;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
public abstract class Algorithm {

    protected Problem problem;

    @Builder.Default
    protected Solution solutionBest=new Solution();

    @Builder.Default
    protected List<Solution> solutions=new ArrayList<>();

    @Builder.Default
    protected Information information=new Information();

    protected abstract void setBestSolution();
    public abstract Information execute() throws FileNotFoundException;

}
