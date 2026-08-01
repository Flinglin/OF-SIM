package com.research.apca.core.entity.pump;

import com.research.apca.core.entity.Entity;
import com.research.apca.core.enums.EntityTypeEnum;
import com.research.apca.core.project.Project;
import com.research.utils.numpy.NumberUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

@Setter
@Getter
public class PumpEntity extends Entity implements Serializable {

    protected double[] maxFlow;

    protected double[] minFlow;

    protected double[] maxVolume;

    protected double[] minVolume;

    protected double[] avgFlow;

    protected double[] avgVolume;

    public PumpEntity(Project project) {
        int timeLength = project.getTimeUnits().size();
        switch (project.getTimeScaleEnum()) {
            case SHORTERM -> {
                this.maxFlow = new double[timeLength + 1];
                this.minFlow = new double[timeLength + 1];
                this.avgFlow = new double[timeLength + 1];
                this.avgVolume = new double[timeLength + 1];
                this.minVolume = new double[timeLength + 1];
                this.maxVolume = new double[timeLength + 1];
            }
            case LONGTERM -> {
                this.maxFlow = new double[timeLength];
                this.minFlow = new double[timeLength];
                this.avgFlow = new double[timeLength];
                this.avgVolume = new double[timeLength];
                this.minVolume = new double[timeLength];
                this.maxVolume = new double[timeLength];
            }
        }
    }

    public PumpEntity(Project project, Object[] param) {
        this(project);
        this.buildEntityType();
        Optional.ofNullable(param[0]).ifPresent(this::buildId);
        Optional.ofNullable(param[1]).ifPresent(this::buildName);
        Optional.ofNullable(param[2]).ifPresent(this::buildStatusDecision);
        Optional.ofNullable(param[3]).ifPresent(this::buildMinFlow);
        Optional.ofNullable(param[4]).ifPresent(this::buildMaxFlow);
        Optional.ofNullable(param[5]).ifPresent(this::buildMinVolume);
        Optional.ofNullable(param[6]).ifPresent(this::buildMaxVolume);
    }

    public void buildEntityType() {
        this.entityType = EntityTypeEnum.PUMP;
    }

    public void buildMinFlow(Object param) {
        if (!param.toString().contains(",")) {
            Arrays.fill(this.minFlow, NumberUtil.objectToDouble(param));
        } else {
            this.minFlow = NumberUtil.objectWithDelimiterToArray(param);
        }
    }

    public void buildMaxFlow(Object param) {
        if (!param.toString().contains(",")) {
            Arrays.fill(this.maxFlow, NumberUtil.objectToDouble(param));
        } else {
            this.maxFlow = NumberUtil.objectWithDelimiterToArray(param);
        }
    }

    public void buildMinVolume(Object param) {
        if (!param.toString().contains(",")) {
            Arrays.fill(this.minVolume, NumberUtil.objectToDouble(param));
        } else {
            this.minVolume = NumberUtil.objectWithDelimiterToArray(param);
        }
    }

    public void buildMaxVolume(Object param) {
        if (!param.toString().contains(",")) {
            Arrays.fill(this.maxVolume, NumberUtil.objectToDouble(param));
        } else {
            this.maxVolume = NumberUtil.objectWithDelimiterToArray(param);
        }
    }

}
