package com.research.core.entity.reservoir;

import com.research.core.entity.Entity;
import com.research.core.entity.curve.DoubleCurve;
import com.research.core.entity.intake.IntakeEntity;
import com.research.core.enums.EntityTypeEnum;
import com.research.core.enums.FloodSimDirectEnum;
import com.research.core.project.Project;
import com.research.utils.numpy.ArrayUtil;
import com.research.utils.numpy.NumberUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Setter
@Getter
public class ReservoirEntity extends Entity implements Serializable {

    protected boolean latency;

    protected double levelReturnRange;

    protected double[] levelFluctuationRange;

    protected double levelNormal;

    protected double levelDead;

    protected double initReservoirLevel;

    protected Entity wasteEntity;

    protected FloodSimDirectEnum wasteWaterDirect;

    protected DoubleCurve levelToStorageCurve;

    protected DoubleCurve storageToLevelCurve;

    protected double[] inFlow;

    protected double[] outFlow;

    protected double[] inVolume;

    protected double[] outVolume;

    protected double[] sectionInFlow;

    protected double[] wasteWater;

    protected double[] reservoirLevel;

    protected double[] inMaxFlow;

    protected double[] inMinFlow;

    protected double[] outMaxFlow;

    protected double[] outMinFlow;

    protected double[] maxLevel;

    protected double[] minLevel;

    protected List<IntakeEntity> intakeEntities=new ArrayList<>();


    public ReservoirEntity(Project project) {
        int timeLength = project.getTimeUnits().size();
        this.inFlow = new double[timeLength];
        this.inVolume = new double[timeLength];
        this.outFlow = new double[timeLength];
        this.outVolume = new double[timeLength];
        this.sectionInFlow = new double[timeLength];
        this.wasteWater = new double[timeLength];
        this.reservoirLevel = new double[timeLength + 1];
        this.inMaxFlow = new double[timeLength];
        this.inMinFlow = new double[timeLength];
        this.outMaxFlow = new double[timeLength];
        this.outMinFlow = new double[timeLength];
        this.maxLevel = new double[timeLength + 1];
        this.minLevel = new double[timeLength + 1];
        this.levelFluctuationRange=new double[timeLength + 1];
    }

    public ReservoirEntity(Project project, Object[] param) {
        this(project);
        this.buildEntityType();
        Optional.ofNullable(param[0]).ifPresent(this::buildId);
        Optional.ofNullable(param[1]).ifPresent(this::buildName);
        Optional.ofNullable(param[2]).ifPresent(this::buildStatusDecision);
        Optional.ofNullable(param[3]).ifPresent(this::buildLevelNormal);
        Optional.ofNullable(param[4]).ifPresent(this::buildLevelDead);
        Optional.ofNullable(param[5]).ifPresent(this::buildWasteWaterDirect);
        Optional.ofNullable(param[6]).ifPresent(c -> this.buildWasteEntity(project, c));
        Optional.ofNullable(param[7]).ifPresent(this::buildMinLevel);
        Optional.ofNullable(param[8]).ifPresent(this::buildMaxLevel);
        Optional.ofNullable(param[9]).ifPresent(this::buildLevelFluctuationRange);
        Optional.ofNullable(param[10]).ifPresent(this::buildLevelReturnRange);
        Optional.ofNullable(param[11]).ifPresent(this::buildSectionInflow);
        Optional.ofNullable(param[12]).ifPresent(this::buildReservoirLevel);
        Optional.ofNullable(param[13]).ifPresent(this::buildInitReservoirLevel);
        Optional.ofNullable(param[14]).ifPresent(c -> Optional.ofNullable(param[15]).ifPresent(k -> this.buildLevelToStorage(c, k)));
        Optional.ofNullable(param[16]).ifPresent(c -> Optional.ofNullable(param[17]).ifPresent(k -> this.buildStorageToLevel(c, k)));
        Optional.ofNullable(param[18]).ifPresent(this::buildMinInFlow);
        Optional.ofNullable(param[19]).ifPresent(this::buildMaxInFlow);
        Optional.ofNullable(param[20]).ifPresent(this::buildMinOutFlow);
        Optional.ofNullable(param[21]).ifPresent(this::buildMaxOutFlow);
        Optional.ofNullable(param[22]).ifPresent(c -> this.buildIntakeEntities(project, c));
        Optional.ofNullable(param[23]).ifPresent(this::buildLatency);
    }

    protected void buildLatency(Object param) {
        this.latency = NumberUtil.objectToBoolean(param);
    }
    protected void buildLevelReturnRange() {}
    protected void buildEntityType() {
        this.entityType = EntityTypeEnum.RESERVOIR;
    }
    protected void buildMaxInFlow(Object param) {
        if (!param.toString().contains(",")) {
            Arrays.fill(this.inMaxFlow, NumberUtil.objectToDouble(param));
        } else {
            this.inMaxFlow = NumberUtil.objectWithDelimiterToArray(param);
        }
    }
    protected void buildMinInFlow(Object param) {
        if (!param.toString().contains(",")) {
            Arrays.fill(this.inMinFlow, NumberUtil.objectToDouble(param));
        } else {
            this.inMinFlow = NumberUtil.objectWithDelimiterToArray(param);
        }
    }
    protected void buildMinOutFlow(Object param) {
        if (!param.toString().contains(",")) {
            Arrays.fill(this.outMinFlow, NumberUtil.objectToDouble(param));
        } else {
            this.outMinFlow = NumberUtil.objectWithDelimiterToArray(param);
        }
    }
    protected void buildMaxOutFlow(Object param) {
        if (!param.toString().contains(",")) {
            Arrays.fill(this.outMaxFlow, NumberUtil.objectToDouble(param));
        } else {
            this.outMaxFlow = NumberUtil.objectWithDelimiterToArray(param);
        }
    }
    protected void buildLevelNormal(Object param) {
        this.levelNormal = NumberUtil.objectToDouble(param);
    }

    protected void buildLevelDead(Object param) {
        this.levelDead = NumberUtil.objectToDouble(param);
    }

    protected void buildWasteWaterDirect(Object param) {
        this.wasteWaterDirect = FloodSimDirectEnum.valueOf(param.toString());
    }

    protected void buildMinLevel(Object param) {
        if (!param.toString().contains(",")) {
            Arrays.fill(this.minLevel, NumberUtil.objectToDouble(param));
        } else {
            this.minLevel = NumberUtil.objectWithDelimiterToArray(param);
        }
    }

    protected void buildMaxLevel(Object param) {
        if (!param.toString().contains(",")) {
            Arrays.fill(this.maxLevel, NumberUtil.objectToDouble(param));
        } else {
            this.maxLevel = NumberUtil.objectWithDelimiterToArray(param);
        }
    }

    protected void buildLevelFluctuationRange(Object param) {
        if (!param.toString().contains(",")) {
            Arrays.fill(this.levelFluctuationRange, NumberUtil.objectToDouble(param));
        } else {
            this.levelFluctuationRange = NumberUtil.objectWithDelimiterToArray(param);
        }
    }

    protected void buildLevelReturnRange(Object param) {
        this.levelReturnRange = NumberUtil.objectToDouble(param);
    }

    protected void buildSectionInflow(Object param) {
        if (!param.toString().contains(",")) {
            Arrays.fill(this.sectionInFlow, NumberUtil.objectToDouble(param));
        } else {
            this.sectionInFlow = NumberUtil.objectWithDelimiterToArray(param);
        }
    }

    protected void buildReservoirLevel(Object param) {
        if (!param.toString().contains(",")) {
            Arrays.fill(this.reservoirLevel, NumberUtil.objectToDouble(param));
        } else {
            this.reservoirLevel = NumberUtil.objectWithDelimiterToArray(param);
        }
    }

    protected void buildInitReservoirLevel(Object param) {
        this.initReservoirLevel = NumberUtil.objectToDouble(param);
    }

    protected void buildLevelToStorage(Object level, Object storage) {
        double[] levelArray = NumberUtil.objectWithDelimiterToArray(level);
        double[] storageArray = NumberUtil.objectWithDelimiterToArray(storage);
        this.levelToStorageCurve = new DoubleCurve(ArrayUtil.zip(levelArray, storageArray));
    }

    protected void buildStorageToLevel(Object storage, Object level) {
        double[] storageArray = NumberUtil.objectWithDelimiterToArray(storage);
        double[] levelArray = NumberUtil.objectWithDelimiterToArray(level);
        this.storageToLevelCurve = new DoubleCurve(ArrayUtil.zip(storageArray, levelArray));
    }

    protected void buildWasteEntity(Project project, Object param) {
        this.wasteEntity = project.seekEntityByName(param.toString());
    }
    protected void buildIntakeEntities(Project project,Object param){
        String[] intakeEntities = param.toString().split(",");
        for (String str : intakeEntities) {
            IntakeEntity intake = (IntakeEntity) project.seekEntityByNameAndType(str, EntityTypeEnum.INTAKE);
            if (intake != null) {
                this.intakeEntities.add(intake);
            }
        }
    }
}
