package com.research.core.entity.gate;

import com.research.core.entity.Entity;
import com.research.core.enums.EntityTypeEnum;
import com.research.core.enums.TimeScaleEnum;
import com.research.core.project.Project;
import com.research.utils.numpy.NumberUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class GateEntity extends Entity implements Serializable {

    protected double designFlow;

    protected double[] avgFlow;

    protected double[] maxFlow;

    protected double[] minFlow;

    public GateEntity(int timeLength, TimeScaleEnum timeScaleEnum) {
        switch (timeScaleEnum) {
            case SHORTERM -> {
                this.avgFlow = new double[timeLength + 1];
                this.maxFlow = new double[timeLength + 1];
                this.minFlow = new double[timeLength + 1];
            }
            case LONGTERM -> {
                this.avgFlow = new double[timeLength];
                this.maxFlow = new double[timeLength];
                this.minFlow = new double[timeLength];
            }
        }
    }

    public GateEntity(Project project) {
        int timeLength = project.getTimeUnits().size();
        switch (project.getTimeScaleEnum()) {
            case SHORTERM -> {
                this.avgFlow = new double[timeLength + 1];
                this.maxFlow = new double[timeLength + 1];
                this.minFlow = new double[timeLength + 1];
            }
            case LONGTERM -> {
                this.avgFlow = new double[timeLength];
                this.maxFlow = new double[timeLength];
                this.minFlow = new double[timeLength];
            }
        }
    }

    public GateEntity(Project project, Object[] param) {
        this(project);
        this.buildEntityType();
        Optional.ofNullable(param[0]).ifPresent(this::buildId);
        Optional.ofNullable(param[1]).ifPresent(this::buildName);
        Optional.ofNullable(param[2]).ifPresent(this::buildStatusDecision);
        Optional.ofNullable(param[3]).ifPresent(this::buildDesignFlow);
        Optional.ofNullable(param[4]).ifPresent(this::buildMinFlow);
        Optional.ofNullable(param[5]).ifPresent(this::buildMaxFlow);
    }

    protected void buildEntityType() {
        this.entityType = EntityTypeEnum.GATE;
    }

    protected void buildDesignFlow(Object param) {
        this.designFlow = NumberUtil.objectToDouble(param);
    }

    protected void buildMinFlow(Object param) {
        if (!param.toString().contains(",")) {
            Arrays.fill(this.minFlow, NumberUtil.objectToDouble(param));
        } else {
            this.minFlow = NumberUtil.objectWithDelimiterToArray(param);
        }
    }
    protected void buildMaxFlow(Object param) {
        if (!param.toString().contains(",")) {
            Arrays.fill(this.maxFlow, NumberUtil.objectToDouble(param));
        } else {
            this.maxFlow = NumberUtil.objectWithDelimiterToArray(param);
        }
    }
}
