package com.research.core.entity.canal;

import com.research.core.entity.Entity;
import com.research.core.entity.Function;
import com.research.core.entity.intake.IntakeEntity;
import com.research.core.enums.EntityTypeEnum;
import com.research.core.enums.FunctionTypeEnum;
import com.research.core.enums.TimeScaleEnum;
import com.research.core.project.Project;

import java.io.Serializable;
import java.util.*;


import com.research.utils.numpy.NumberUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class CanalEntity extends Entity implements Serializable {

    private String uuid = UUID.randomUUID().toString().replaceAll("-", "");

    private double lossParam;

    private List<IntakeEntity> intakeEntities = new ArrayList<>();

    private Entity upStreamEntity;

    private Entity downStreamEntity;

    private Function levelToStorageLine;

    private Function storageToLevelLine;
    private double[] intakeFlow;
    private double[] sectionInFlow;
    public CanalEntity(Project project) {
        int timeLength = project.getTimeUnits().size();
        switch (project.getTimeScaleEnum()) {
            case TimeScaleEnum.SHORTERM -> {
                this.sectionInFlow = new double[timeLength + 1];
                this.intakeFlow = new double[timeLength + 1];
            }
            case TimeScaleEnum.LONGTERM -> {
                this.sectionInFlow = new double[timeLength];
                this.intakeFlow = new double[timeLength];
                this.watershedRate=new double[timeLength];
            }
        }
    }

    public CanalEntity(Project project, Object[] param) {
        this(project);
        this.buildEntityType();
        Optional.ofNullable(param[0]).ifPresent(this::buildId);
        Optional.ofNullable(param[1]).ifPresent(c -> Optional.ofNullable(param[2]).ifPresent(k -> this.buildName(c + "-" + k)));
        Optional.ofNullable(param[1]).ifPresent(c -> Optional.ofNullable(param[2]).ifPresent(k -> this.buildUpAndDownEntity(project, c, k)));
        Optional.ofNullable(param[3]).ifPresent(this::buildLossParam);
        Optional.ofNullable(param[4]).ifPresent(c -> this.buildIntakeEntity(project, c));
        Optional.ofNullable(param[5]).ifPresent(c -> Optional.ofNullable(param[6]).ifPresent(k -> this.buildLevelToStorageLine(c, k)));
        Optional.ofNullable(param[7]).ifPresent(c -> Optional.ofNullable(param[8]).ifPresent(k -> this.buildStorageToLevelLine(c, k)));
        Optional.ofNullable(param[9]).ifPresent(this::buildSectionInFlow);
        Optional.ofNullable(param[10]).ifPresent(this::buildWaterShedRate);
    }

    protected void buildEntityType() {
        this.entityType = EntityTypeEnum.CANAL;
    }

    protected void buildUpAndDownEntity(Project project, Object upParam, Object downParam) {
        Entity upEntity = project.seekEntityByName(upParam.toString());
        Entity downEntity = project.seekEntityByName(downParam.toString());
        upEntity.getDownCanalEntities().add(this);
        downEntity.getUpCanalEntities().add(this);
        this.upStreamEntity = upEntity;
        this.downStreamEntity = downEntity;
    }

    protected void buildLossParam(Object param) {
        this.lossParam = NumberUtil.objectToDouble(param);
    }

    protected void buildSectionInFlow(Object param) {
        this.sectionInFlow = NumberUtil.objectWithDelimiterToArray(param);
    }

    protected void buildLevelToStorageLine(Object lineType, Object param) {
        Function function = new Function();
        function.setLineType(FunctionTypeEnum.valueOf(lineType.toString()));
        function.setParam(NumberUtil.objectWithDelimiterToArray(param));
        this.levelToStorageLine = function;
    }

    protected void buildStorageToLevelLine(Object lineType, Object param) {
        Function function = new Function();
        function.setLineType(FunctionTypeEnum.valueOf(lineType.toString()));
        function.setParam(NumberUtil.objectWithDelimiterToArray(param));
        this.storageToLevelLine = function;
    }

    protected void buildIntakeEntity(Project project, Object param) {
        String[] intakeEntities = param.toString().split(",");
        for (String str : intakeEntities) {
            IntakeEntity intake = (IntakeEntity) project.seekEntityByNameAndType(str, EntityTypeEnum.INTAKE);
            if (intake != null) {
                this.intakeEntities.add(intake);
            }
        }
    }

    protected void buildWaterShedRate(Object param) {
        if(!param.toString().contains(",")) {
            Arrays.fill(this.watershedRate,NumberUtil.objectToDouble(param));
        }else {
            this.watershedRate = NumberUtil.objectWithDelimiterToArray(param);
        }
    }
    public double calculateSumIntakeFlow(int period) {
        double sum = 0;
        for (IntakeEntity p : this.intakeEntities) {
            sum += p.getTruthIntakeFlow()[period];
        }
        return sum;
    }


}
