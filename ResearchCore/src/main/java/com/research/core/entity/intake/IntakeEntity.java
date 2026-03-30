package com.research.core.entity.intake;

import com.research.core.entity.Entity;
import com.research.core.enums.EntityTypeEnum;
import com.research.core.enums.TimeScaleEnum;
import com.research.core.project.Project;
import com.research.utils.numpy.NumberUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

@Setter
@Getter
public class IntakeEntity extends Entity implements Serializable {
    protected double designFlow;
    protected boolean use;

    protected double[] maxIntakeVolume;

    protected double[] minIntakeVolume;

    protected double[] maxIntakeFlow;

    protected double[] minIntakeFlow;

    protected double[] planIntakeFlow;

    protected double[] planIntakeVolume;

    protected double[] demandIntakeFlow;

    protected double[] demandIntakeVolume;

    protected double[] planSupplyFlow;

    protected double[] planSupplyVolume;


    protected double[] truthIntakeFlow;

    protected double[] truthIntakeVolume;

    protected double[] waterShortageRate;

    public IntakeEntity(){}

    public IntakeEntity(Project project) {
        int timeLength = project.getTimeUnits().size();
        if (project.getTimeScaleEnum() == TimeScaleEnum.SHORTERM) {
            timeLength++;
        }
        this.minIntakeFlow = new double[timeLength];
        this.maxIntakeFlow = new double[timeLength];
        this.maxIntakeVolume = new double[timeLength];
        this.minIntakeVolume = new double[timeLength];
        this.demandIntakeFlow= new double[timeLength];
        this.planSupplyFlow=new double[timeLength];
        this.planIntakeFlow = new double[timeLength];
        this.truthIntakeFlow = new double[timeLength];
        this.planIntakeVolume = new double[timeLength];
        this.truthIntakeVolume = new double[timeLength];
        this.waterShortageRate = new double[timeLength];
        this.planSupplyVolume=new double[timeLength];
        this.demandIntakeVolume=new double[timeLength];
    }

    public IntakeEntity(Project project, Object[] param) {
        this(project);
        this.buildEntityType();
        Optional.ofNullable(param[0]).ifPresent(this::buildId);
        Optional.ofNullable(param[1]).ifPresent(this::buildName);
        Optional.ofNullable(param[2]).ifPresent(this::buildStatusDecision);
        Optional.ofNullable(param[3]).ifPresent(this::buildUse);
        Optional.ofNullable(param[4]).ifPresent(this::buildDesignFlow);
        Optional.ofNullable(param[5]).ifPresent(this::buildPlanIntakeFlow);
        Optional.ofNullable(param[6]).ifPresent(this::buildPlanIntakeVolume);
        Optional.ofNullable(param[7]).ifPresent(this::buildMinIntakeFlow);
        Optional.ofNullable(param[8]).ifPresent(this::buildMaxIntakeFlow);
        Optional.ofNullable(param[9]).ifPresent(this::buildMinIntakeVolume);
        Optional.ofNullable(param[10]).ifPresent(this::buildMaxIntakeVolume);
        Optional.ofNullable(param[11]).ifPresent(this::buildDemandIntakeFlow);
        Optional.ofNullable(param[12]).ifPresent(this::buildDemandIntakeVolume);
        Optional.ofNullable(param[13]).ifPresent(this::buildPlanSupplyFlow);
        Optional.ofNullable(param[14]).ifPresent(this::buildPlanSupplyVolume);
    }

    protected void buildEntityType() {
        this.entityType = EntityTypeEnum.INTAKE;
    }

    protected void buildDesignFlow(Object param) {
        this.designFlow = NumberUtil.objectToDouble(param);
    }

    protected void buildUse(Object param) {
        this.use = NumberUtil.objectToBoolean(param);
    }

    protected void buildPlanIntakeFlow(Object param) {
        if(!param.toString().contains(",")) {
            Arrays.fill(this.planIntakeFlow,NumberUtil.objectToDouble(param));
            Arrays.fill(this.truthIntakeFlow,NumberUtil.objectToDouble(param));
        }else {
            double[] f = NumberUtil.objectWithDelimiterToArray(param);
            this.planIntakeFlow = f;
            this.truthIntakeFlow = f.clone();
        }
    }

    protected void buildMinIntakeFlow(Object param) {
        if(!param.toString().contains(",")) {
            Arrays.fill(this.minIntakeFlow,NumberUtil.objectToDouble(param));
        }else {
            this.minIntakeFlow = NumberUtil.objectWithDelimiterToArray(param);
        }
    }

    protected void buildMaxIntakeFlow(Object param) {
        if(!param.toString().contains(",")) {
            Arrays.fill(this.maxIntakeFlow,NumberUtil.objectToDouble(param));
        }else {
            this.maxIntakeFlow = NumberUtil.objectWithDelimiterToArray(param);
        }
    }

    protected void buildMaxIntakeVolume(Object param) {
        if(!param.toString().contains(",")) {
            Arrays.fill(this.maxIntakeVolume,NumberUtil.objectToDouble(param));
        }else {
            this.maxIntakeVolume = NumberUtil.objectWithDelimiterToArray(param);
        }
    }

    protected void buildMinIntakeVolume(Object param) {
        if(!param.toString().contains(",")) {
            Arrays.fill(this.minIntakeVolume,NumberUtil.objectToDouble(param));
        }else {
            this.minIntakeVolume = NumberUtil.objectWithDelimiterToArray(param);
        }
    }

    protected void buildPlanIntakeVolume(Object param) {
        if(!param.toString().contains(",")) {
            Arrays.fill(this.planIntakeVolume,NumberUtil.objectToDouble(param));
            Arrays.fill(this.truthIntakeVolume,NumberUtil.objectToDouble(param));
        }else {
            double[] v = NumberUtil.objectWithDelimiterToArray(param);
            this.planIntakeVolume = v;
            this.truthIntakeVolume = v.clone();
        }
    }

    protected void buildDemandIntakeFlow(Object param) {
        if(!param.toString().contains(",")) {
            Arrays.fill(this.demandIntakeFlow,NumberUtil.objectToDouble(param));
        }else {
            this.demandIntakeFlow = NumberUtil.objectWithDelimiterToArray(param);
        }
    }

    protected void buildDemandIntakeVolume(Object param) {
        if(!param.toString().contains(",")) {
            Arrays.fill(this.demandIntakeVolume,NumberUtil.objectToDouble(param));
            Arrays.fill(this.truthIntakeVolume,NumberUtil.objectToDouble(param));
        }else {
            double[] temp = NumberUtil.objectWithDelimiterToArray(param);
            this.demandIntakeVolume = temp;
            this.truthIntakeVolume = temp.clone();
        }
    }

    protected void buildPlanSupplyFlow(Object param) {
        if(!param.toString().contains(",")) {
            Arrays.fill(this.planSupplyFlow,NumberUtil.objectToDouble(param));
        }else {
            this.planSupplyFlow = NumberUtil.objectWithDelimiterToArray(param);
        }
    }

    protected void buildPlanSupplyVolume(Object param) {
        if(!param.toString().contains(",")) {
            Arrays.fill(this.planSupplyVolume,NumberUtil.objectToDouble(param));
        }else {
            this.planSupplyVolume = NumberUtil.objectWithDelimiterToArray(param);
        }
    }
}
