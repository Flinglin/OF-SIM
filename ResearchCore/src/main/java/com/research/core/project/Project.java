package com.research.core.project;


import com.research.core.entity.Entity;
import com.research.core.entity.TimeUnit;
import com.research.core.entity.gate.GateEntity;
import com.research.core.entity.intake.IntakeEntity;
import com.research.core.entity.pump.PumpEntity;
import com.research.core.entity.reservoir.ReservoirEntity;
import com.research.core.entity.canal.CanalEntity;
import com.research.utils.data.ExcelTool;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import com.research.core.enums.EntityTypeEnum;
import com.research.core.enums.NodeTypeEnum;
import com.research.core.enums.ProjectModeEnum;
import com.research.core.enums.TimeScaleEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;


@Setter
@Getter
@Slf4j
@SuperBuilder
public class Project {
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected int timeStep;
    protected ProjectModeEnum projectModeEnum;
    protected TimeScaleEnum timeScaleEnum;
    @Builder.Default
    protected List<TimeUnit> timeUnits = new ArrayList<>();
    @Builder.Default
    protected List<Entity> entities = new ArrayList<>();

    @Builder.Default
    protected List<PumpEntity> pumpEntities = new ArrayList<>();

    @Builder.Default
    protected List<GateEntity> gateEntities = new ArrayList<>();

    @Builder.Default
    protected List<ReservoirEntity> reservoirEntities = new ArrayList<>();

    @Builder.Default
    protected List<CanalEntity> canalEntities = new ArrayList<>();

    @Builder.Default
    protected List<IntakeEntity> intakeEntities = new ArrayList<>();

    public void buildShortTimeSequence() {
        LocalDateTime tempTime = this.startTime;
        while (!tempTime.isEqual(this.endTime)) {
            TimeUnit timeUnit = new TimeUnit();
            timeUnit.setStartTime(tempTime);
            tempTime = tempTime.plusMinutes(timeStep);
            timeUnit.setEndTime(tempTime);
            timeUnit.calculateTimeLength();
            this.timeUnits.add(timeUnit);
        }
    }
    public void buildLongTimeSequence() {
        LocalDateTime tempTime = this.startTime;
        while (!tempTime.isEqual(this.endTime)) {
            TimeUnit timeUnit1 = new TimeUnit();
            TimeUnit timeUnit2 = new TimeUnit();
            TimeUnit timeUnit3 = new TimeUnit();
            timeUnit1.setStartTime(tempTime);
            tempTime = tempTime.plusDays(9);
            timeUnit1.setEndTime(tempTime);
            timeUnits.add(timeUnit1);
            tempTime = tempTime.plusDays(1);
            timeUnit2.setStartTime(tempTime);
            tempTime = tempTime.plusDays(9);
            timeUnit2.setEndTime(tempTime);
            timeUnits.add(timeUnit2);
            tempTime = tempTime.plusDays(1);
            timeUnit3.setStartTime(tempTime);
            tempTime = tempTime.plusDays(tempTime.getMonth().length(tempTime.toLocalDate().isLeapYear()) - 21);
            timeUnit3.setEndTime(tempTime);
            timeUnits.add(timeUnit3);
            tempTime = tempTime.plusDays(1);
        }
    }

    public Entity seekEntityByNameAndType(String enName, EntityTypeEnum modelEntityTypeEnum) {
        Entity entity = null;
        switch (modelEntityTypeEnum) {
            case PUMP -> {
                for (Entity value : this.pumpEntities) {
                    if (value.getName().equals(enName)) {
                        entity = value;
                    }
                }
            }
            case GATE -> {
                for (Entity value : this.gateEntities) {
                    if (value.getName().equals(enName)) {
                        entity = value;
                    }
                }
            }
            case RESERVOIR -> {
                for (Entity value : this.reservoirEntities) {
                    if (value.getName().equals(enName)) {
                        entity = value;
                    }
                }
            }
            case CANAL -> {
                for (Entity value : this.canalEntities) {
                    if (value.getName().equals(enName)) {
                        entity = value;
                    }
                }
            }
            case INTAKE -> {
                for (Entity value : this.intakeEntities) {
                    if (value.getName().equals(enName)) {
                        entity = value;
                    }
                }
            }

        }
        return entity;
    }
    public Entity seekEntityByName(String ennm) {
        Entity entity = null;
        for (Entity value : entities) {
            if (value.getName().equals(ennm)) {
                entity = value;
            }
        }
        return entity;
    }

    public void buildProjectParam(Project project, String path) {
        buildGateEntity(project,path);
        buildPumpEntity(project,path);
        buildIntakeEntity(project,path);
        buildReservoirEntity(project,path);
        buildCanalEntity(project,path);
        buildNodeType(project);
    }

    protected void buildGateEntity(Project project,String path) {
        Object[][] gateParam = null;
        try {
            gateParam = ExcelTool.readXlsxExcel(path, "parameter_of_gate");
        } catch (IOException e) {
            throw new RuntimeException("check the project parameter");
        }
        if (gateParam != null) {
            for (Object[] objects : gateParam) {
                GateEntity gateEntity = buildGateEntityInstance(project, objects);
                project.getEntities().add(gateEntity);
                project.getGateEntities().add(gateEntity);
            }
        }else {
            throw new RuntimeException("check the project parameter");
        }
    }
    protected void buildPumpEntity(Project project,String path) {
        Object[][] pumpParam = null;
        try {
            pumpParam=ExcelTool.readXlsxExcel(path,"parameter_of_pump");
        }catch (Exception e) {
            throw new RuntimeException("check the project parameter");
        }
        if (pumpParam != null) {
            for (Object[] objects : pumpParam) {
                PumpEntity pumpEntity = buildPumpEntityInstance(project, objects);
                project.getEntities().add(pumpEntity);
                project.getPumpEntities().add(pumpEntity);
            }
        }else {
            throw new RuntimeException("check the project parameter");
        }
    }
    protected void buildIntakeEntity(Project project,String path) {
        Object[][] intakeParam = null;
        try {
            intakeParam=ExcelTool.readXlsxExcel(path,"parameter_of_intake");
        }catch (Exception e) {
            throw new RuntimeException("check the project parameter");
        }
        if (intakeParam != null) {
            for (Object[] objects : intakeParam) {
                IntakeEntity intakeEntity=buildIntakeEntityInstance(project, objects);
                project.getEntities().add(intakeEntity);
                project.getIntakeEntities().add(intakeEntity);
            }
        }else {
            throw new RuntimeException("check the project parameter");
        }
    }
    protected void buildReservoirEntity(Project project,String path) {
        Object[][] reservoirParam = null;
        try {
            reservoirParam=ExcelTool.readXlsxExcel(path,"parameter_of_reservoir");
        }catch (Exception e) {
            throw new RuntimeException("check the project parameter");
        }
        if (reservoirParam != null) {
            for (Object[] objects : reservoirParam) {
                ReservoirEntity reservoirEntity=buildReservoirEntityInstance(project, objects);
                project.getEntities().add(reservoirEntity);
                project.getReservoirEntities().add(reservoirEntity);
            }
        }else {
            throw new RuntimeException("check the project parameter");
        }
    }
    protected void buildCanalEntity(Project project,String path) {
        Object[][] canalParam = null;
        try {
            canalParam=ExcelTool.readXlsxExcel(path,"parameter_of_canal");
        }catch (Exception e) {
            throw new RuntimeException("check the project parameter");
        }
        if (canalParam != null) {
            for (Object[] objects : canalParam) {
                CanalEntity canalEntity=buildCanalEntityInstance(project, objects);
                project.getEntities().add(canalEntity);
                project.getCanalEntities().add(canalEntity);
            }
        }else {
            throw new RuntimeException("check the project parameter");
        }
    }

    protected GateEntity buildGateEntityInstance(Project project,Object[] param) {
        return new GateEntity(project, param);
    }
    protected PumpEntity buildPumpEntityInstance(Project project,Object[] param) {
        return new PumpEntity(project, param);
    }
    protected IntakeEntity buildIntakeEntityInstance(Project project,Object[] param) {
        return new IntakeEntity(project, param);
    }
    protected ReservoirEntity buildReservoirEntityInstance(Project project,Object[] param) {
        return new ReservoirEntity(project, param);
    }
    protected CanalEntity buildCanalEntityInstance(Project project,Object[] param) {
        return new CanalEntity(project, param);
    }
    protected void buildNodeType(Project project) {
        for (int i = 0; i < project.getEntities().size(); i++) {
            Entity entity = project.getEntities().get(i);
            if (entity.getEntityType() == EntityTypeEnum.GATE
                    || entity.getEntityType() == EntityTypeEnum.PUMP
                    || entity.getEntityType() == EntityTypeEnum.RESERVOIR) {
                if (entity.getUpCanalEntities().size() > 1 && entity.getDownCanalEntities().isEmpty()) {
                    entity.setNodeTypeEnum(NodeTypeEnum.HEAD_DIVERSION);
                    continue;
                }
                if (entity.getDownCanalEntities().size() > 1 && entity.getUpCanalEntities().isEmpty()) {
                    entity.setNodeTypeEnum(NodeTypeEnum.END_CONVERGE);
                    continue;
                }
                if (entity.getUpCanalEntities().size() > 1) {
                    entity.setNodeTypeEnum(NodeTypeEnum.DIVERSION);
                    continue;
                }
                if (entity.getDownCanalEntities().size() > 1) {
                    entity.setNodeTypeEnum(NodeTypeEnum.CONVERGE);
                    continue;
                }
                if (entity.getUpCanalEntities().size() == 1 && entity.getDownCanalEntities().isEmpty()) {
                    entity.setNodeTypeEnum(NodeTypeEnum.HEAD);
                    continue;
                }
                if (entity.getUpCanalEntities().isEmpty() && entity.getDownCanalEntities().size() == 1) {
                    entity.setNodeTypeEnum(NodeTypeEnum.END);
                    continue;
                }
                entity.setNodeTypeEnum(NodeTypeEnum.NORMAL);
            }
        }
    }
}
