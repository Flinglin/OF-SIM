package com.research.core.simulation;

import com.research.algorithm.optimize.common.base.Individual;
import com.research.core.entity.Entity;
import com.research.core.entity.canal.CanalEntity;
import com.research.core.entity.gate.GateEntity;
import com.research.core.entity.intake.IntakeEntity;
import com.research.core.entity.pump.PumpEntity;
import com.research.core.entity.reservoir.ReservoirEntity;
import com.research.core.enums.EntityTypeEnum;
import com.research.core.project.Project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


import lombok.*;
import org.apache.commons.lang3.SerializationUtils;

@Getter
@Setter
@AllArgsConstructor
public abstract class SimulationModel {

    protected Project initialProject;

    protected List<PumpEntity> pumpEntities = new ArrayList<>();

    protected List<GateEntity> gateEntities = new ArrayList<>();

    protected List<ReservoirEntity> reservoirEntities = new ArrayList<>();

    protected List<CanalEntity> canalEntities = new ArrayList<>();

    protected List<IntakeEntity> intakeEntities = new ArrayList<>();

    public SimulationModel(Project pro) {
        this.initialProject = pro;
        this.pumpEntities=pro.getPumpEntities().stream().map(SerializationUtils::clone).collect(Collectors.toList());
        this.gateEntities=pro.getGateEntities().stream().map(SerializationUtils::clone).collect(Collectors.toList());
        this.reservoirEntities=pro.getReservoirEntities().stream().map(SerializationUtils::clone).collect(Collectors.toList());
        this.canalEntities=pro.getCanalEntities().stream().map(SerializationUtils::clone).collect(Collectors.toList());
        this.intakeEntities=pro.getIntakeEntities().stream().map(SerializationUtils::clone).collect(Collectors.toList());
        for (ReservoirEntity reservoirEntity:this.reservoirEntities) {
            Entity waste=reservoirEntity.getWasteEntity();
            if(waste!=null) {
                reservoirEntity.setWasteEntity(seekEntityByNameAndType(waste.getName(),waste.getEntityType()));
            }
            reservoirEntity.getIntakeEntities().replaceAll(intakeEntity -> (IntakeEntity) seekEntityByNameAndType(intakeEntity.getName(), intakeEntity.getEntityType()));
        }
        for (CanalEntity canalEntity:this.canalEntities) {
            canalEntity.getIntakeEntities().replaceAll(intakeEntity -> (IntakeEntity) seekEntityByNameAndType(intakeEntity.getName(), intakeEntity.getEntityType()));
            Entity up=seekEntityByNameAndType(canalEntity.getUpStreamEntity().getName(),canalEntity.getUpStreamEntity().getEntityType());
            for (int i = 0; i < up.getDownCanalEntities().size(); i++) {
                if(up.getDownCanalEntities().get(i).getName().equals(canalEntity.getName())) {
                    up.getDownCanalEntities().set(i,canalEntity);
                }
            }
            canalEntity.setUpStreamEntity(up);
            Entity down=seekEntityByNameAndType(canalEntity.getDownStreamEntity().getName(),canalEntity.getDownStreamEntity().getEntityType());
            for (int i = 0; i < down.getUpCanalEntities().size(); i++) {
                if(down.getUpCanalEntities().get(i).getName().equals(canalEntity.getName())) {
                    down.getUpCanalEntities().set(i,canalEntity);
                }
            }
            canalEntity.setUpStreamEntity(up);
            canalEntity.setDownStreamEntity(down);
        }
    }
    protected abstract void assignSimulation(Individual individual,int timeLength);

    protected void initializePumpEntities() {
        for (int i = 0; i < this.initialProject.getPumpEntities().size(); i++) {
            System.arraycopy(this.initialProject.getPumpEntities().get(i).getMinFlow(),0,this.pumpEntities.get(i).getMinFlow(),0,this.initialProject.getPumpEntities().get(i).getMinFlow().length);
            System.arraycopy(this.initialProject.getPumpEntities().get(i).getMaxVolume(),0,this.pumpEntities.get(i).getMaxVolume(),0,this.initialProject.getPumpEntities().get(i).getMaxVolume().length);
            System.arraycopy(this.initialProject.getPumpEntities().get(i).getMinVolume(),0,this.pumpEntities.get(i).getMinVolume(),0,this.initialProject.getPumpEntities().get(i).getMinVolume().length);
            System.arraycopy(this.initialProject.getPumpEntities().get(i).getAvgFlow(),0,this.pumpEntities.get(i).getAvgFlow(),0,this.initialProject.getPumpEntities().get(i).getAvgFlow().length);
            Arrays.fill(this.pumpEntities.get(i).getAvgVolume(),0);
        }
    }

    protected void initializeReservoirEntities() {
        for (int j = 0; j < this.initialProject.getReservoirEntities().size(); j++) {
            System.arraycopy(this.initialProject.getReservoirEntities().get(j).getSectionInFlow(),0,this.reservoirEntities.get(j).getSectionInFlow(),0,this.initialProject.getReservoirEntities().get(j).getSectionInFlow().length);
            System.arraycopy(this.initialProject.getReservoirEntities().get(j).getReservoirLevel(),0,this.reservoirEntities.get(j).getReservoirLevel(),0,this.initialProject.getReservoirEntities().get(j).getReservoirLevel().length);

            Arrays.fill(this.reservoirEntities.get(j).getWasteWater(),0);
            Arrays.fill(this.reservoirEntities.get(j).getInFlow(),0);
            Arrays.fill(this.reservoirEntities.get(j).getOutFlow(),0);
            Arrays.fill(this.reservoirEntities.get(j).getInVolume(),0);
            Arrays.fill(this.reservoirEntities.get(j).getOutVolume(),0);
        }
    }

    protected void initializeIntakeEntities() {
        for (int t = 0; t < this.initialProject.getIntakeEntities().size(); t++) {
            System.arraycopy(this.initialProject.getIntakeEntities().get(t).getTruthIntakeFlow(),0,this.intakeEntities.get(t).getTruthIntakeFlow(),0,this.initialProject.getIntakeEntities().get(t).getTruthIntakeFlow().length);
        }
    }

    protected void initializeGateEntities() {
        for (int r = 0; r < this.initialProject.getGateEntities().size(); r++) {
            System.arraycopy(this.initialProject.getGateEntities().get(r).getAvgFlow(),0,this.gateEntities.get(r).getAvgFlow(),0,this.initialProject.getGateEntities().get(r).getAvgFlow().length);
        }
    }

    protected void initializeCanalEntities() {
        for (int e = 0; e < this.initialProject.getCanalEntities().size(); e++) {
            Arrays.fill(this.canalEntities.get(e).getIntakeFlow(),0);
        }
    }

    public void initializeSimulationModel() {
        initializePumpEntities();
        initializeReservoirEntities();
        initializeGateEntities();
        initializeIntakeEntities();
        initializeCanalEntities();
    }

    public Entity seekEntityByNameAndType(String enName, EntityTypeEnum modelEntityTypeEnum) {
        Entity entity = null;
        switch (modelEntityTypeEnum) {
            case PUMP->{
                for (Entity value : this.pumpEntities) {
                    if (value.getName().equals(enName)) {
                        entity = value;
                    }
                }
            }
            case GATE->{
                for (Entity value : this.gateEntities) {
                    if (value.getName().equals(enName)) {
                        entity = value;
                    }
                }
            }
            case RESERVOIR->{
                for (Entity value : this.reservoirEntities) {
                    if (value.getName().equals(enName)) {
                        entity = value;
                    }
                }
            }
            case CANAL->{
                for (Entity value : this.canalEntities) {
                    if (value.getName().equals(enName)) {
                        entity = value;
                    }
                }
            }
            case INTAKE->{
                for (Entity value : this.intakeEntities) {
                    if (value.getName().equals(enName)) {
                        entity = value;
                    }
                }
            }

        }
        return entity;
    }
}
