package com.research.apca.pump;

import com.research.apca.core.entity.Entity;
import com.research.apca.core.entity.canal.CanalEntity;
import com.research.apca.core.entity.intake.IntakeEntity;
import com.research.apca.core.entity.pump.PumpEntity;
import com.research.apca.core.entity.reservoir.ReservoirEntity;
import com.research.apca.core.enums.EntityTypeEnum;
import com.research.apca.core.project.Project;
import com.research.apca.ResearchReverseSimulation;
import com.research.apca.gate.ResearchGateEntity;
import com.research.apca.reservoir.ResearchReservoirEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ResearchPumpEntity extends PumpEntity {
    private int step;
    public ResearchPumpEntity(Project project) {
        super(project);

    }

    public ResearchPumpEntity(Project project, Object[] param) {
        super(project, param);
        this.step=20;
    }

    public int flowControl(CanalEntity canalEntity, ResearchReverseSimulation sim, double needToChangeFlow, int time) {
        ResearchReservoirEntity reservoirEntity = (ResearchReservoirEntity) canalEntity.getDownStreamEntity();
        if (needToChangeFlow > 0) {
            if (this.avgFlow[time] < needToChangeFlow) {
                double temp = needToChangeFlow - this.avgFlow[time];
                this.avgFlow[time] = 0;
                reservoirEntity.calculateWasteWater(temp, time);
            } else {
                this.avgFlow[time] -= needToChangeFlow;
                reservoirEntity.getInFlow()[time]=this.avgFlow[time]*canalEntity.getLossParam();
            }
            return 0;
        } else if (needToChangeFlow < 0) {
            this.avgFlow[time] -= needToChangeFlow;
            if (this.avgFlow[time] > this.maxFlow[time]+1e-4) {
                double unSatisfyFlow = this.avgFlow[time] - this.maxFlow[time];
                this.avgFlow[time] = this.maxFlow[time];
                if (!this.name.equals("p_pump")) {
                    ResearchGateEntity gateEntity = (ResearchGateEntity) sim.seekEntityByNameAndType("x_gate", EntityTypeEnum.GATE);
                    gateEntity.setSubtractFlowSource(true);
                    List<CanalEntity> canalEntityList = gateEntity.getUpCanalEntities();
                    for (int rt = 0; rt < canalEntityList.size(); rt++) {
                        gateEntity.getSubtractFlow()[rt] = gateEntity.getAvgFlow()[time] * canalEntityList.get(rt).getWatershedRate()[time];
                    }
                }
                return adjustIntakeFlow(canalEntity, sim, unSatisfyFlow, time, 1);
            }
            ResearchReservoirEntity reservoir = (ResearchReservoirEntity) canalEntity.getDownStreamEntity();
            double sumIntakeFlow = canalEntity.calculateSumIntakeFlow(time);
            reservoir.getInFlow()[time] = this.avgFlow[time] * canalEntity.getLossParam() + canalEntity.getSectionInFlow()[time] - sumIntakeFlow;
        }
        return 0;
    }

    private int adjustIntakeFlow(CanalEntity canalEntity, ResearchReverseSimulation sim, double tempFlow, int time, int type) {
        List<IntakeEntity> intakeEntities = canalEntity.getIntakeEntities();
        ResearchReservoirEntity reservoirEntity = (ResearchReservoirEntity) canalEntity.getDownStreamEntity();

        double totalIntakeFlowReservoir = 0;
        for (IntakeEntity intakeEntity : reservoirEntity.getIntakeEntities()) {
            totalIntakeFlowReservoir += intakeEntity.getTruthIntakeFlow()[time] / canalEntity.getLossParam();
        }
        double totalvolume = totalIntakeFlowReservoir;
        for (IntakeEntity intakeEntity : intakeEntities) {
            totalvolume += intakeEntity.getTruthIntakeFlow()[time];
        }

        if (type == 0) {
            for (IntakeEntity entity : intakeEntities) {
                entity.getTruthIntakeFlow()[time] += tempFlow * (entity.getTruthIntakeFlow()[time] / totalvolume);
            }
            for (IntakeEntity entity : reservoirEntity.getIntakeEntities()) {
                entity.getTruthIntakeFlow()[time] += tempFlow * (entity.getTruthIntakeFlow()[time] / totalvolume) * canalEntity.getLossParam();
            }
            return 0;
        } else {
            Entity e = this;
            List<IntakeEntity> intakeEntityList = new ArrayList<>();
            List<Double> lossParamList = new ArrayList<>();
            while (!e.getDownCanalEntities().isEmpty()) {
                CanalEntity c = e.getDownCanalEntities().getFirst();
                lossParamList.add(c.getLossParam());
                IntakeEntity i = new IntakeEntity();
                i.setEntityType(EntityTypeEnum.CATCHMENT);
                if (e.getEntityType() == EntityTypeEnum.PUMP && e != this) {
                    PumpEntity pumpEntity = (PumpEntity) e;
                    i.setName(pumpEntity.getName());
                    i.setPlanIntakeFlow(pumpEntity.getAvgFlow());
                }
                intakeEntityList.add(i);
                if (e.getEntityType() == EntityTypeEnum.RESERVOIR) {
                    ReservoirEntity er = (ReservoirEntity) e;
                    intakeEntityList.addAll(er.getIntakeEntities());
                }
                intakeEntityList.addAll(c.getIntakeEntities());

                e = e.getDownCanalEntities().getFirst().getDownStreamEntity();
            }
            List<Double> lossParamListCumSum = new ArrayList<>();
            double sum = 1;
            lossParamListCumSum.add(sum);
            for (Double num : lossParamList) {
                sum *= num;
                lossParamListCumSum.add(sum);
            }
            List<Double> truthIntakeFlowList = new ArrayList<>();
            int flag = -1;
            for (IntakeEntity intakeEntity : intakeEntityList) {
                if (intakeEntity.getEntityType() == EntityTypeEnum.CATCHMENT) {
                    flag++;
                } else {
                    truthIntakeFlowList.add(intakeEntity.getTruthIntakeFlow()[time] / lossParamListCumSum.get(flag));
                }
            }
            double truthIntakeFlowSum = truthIntakeFlowList.stream().mapToDouble(x -> x).sum();
            double adjustFlow = 0;
            if (tempFlow > truthIntakeFlowSum) {
                if (this.name.equals("w_reservoir")) {
                    throw new RuntimeException("check the project parameter");
                }
                if (this.name.equals("c_reservoir")) {
                    double transferFlow = (tempFlow - truthIntakeFlowSum) * lossParamListCumSum.get(1) + 1e-6;
                    ResearchGateEntity gateEntity = (ResearchGateEntity) sim.seekEntityByNameAndType("x_gate", EntityTypeEnum.GATE);
                    Arrays.fill(gateEntity.getSubtractFlow(), 0);
                    gateEntity.setSubtractFlowSource(false);
                    CanalEntity chaoHuCanal = gateEntity.getUpCanalEntities().getFirst();
                    CanalEntity luJiangCanal = gateEntity.getUpCanalEntities().get(1);
                    chaoHuCanal.getWatershedRate()[time] = (chaoHuCanal.getWatershedRate()[time] * gateEntity.getAvgFlow()[time] - transferFlow) / gateEntity.getAvgFlow()[time];
                    luJiangCanal.getWatershedRate()[time] = (luJiangCanal.getWatershedRate()[time] * gateEntity.getAvgFlow()[time] + transferFlow) / gateEntity.getAvgFlow()[time];
                    gateEntity.setTransferFlow(true);
                    return 1;
                } else {
                    ReservoirEntity wabu = (ReservoirEntity) sim.seekEntityByNameAndType("w_reservoir", EntityTypeEnum.RESERVOIR);
                    if (wabu.getReservoirLevel()[time + 1]>wabu.getMinLevel()[time+1]){
                        double marginFlow1 = tempFlow - truthIntakeFlowSum;
                        wabu.getReservoirLevel()[time + 1] = wabu.getStorageToLevelCurve().getV1ByV0(wabu.getLevelToStorageCurve().getV1ByV0(wabu.getReservoirLevel()[time + 1])-marginFlow1 * lossParamListCumSum.get(5)*24*10*3600);
                        tempFlow = truthIntakeFlowSum;
                    }else if (tempFlow-truthIntakeFlowSum<0.5){
                        tempFlow=truthIntakeFlowSum;
                    }
                }
            }
            double sum2=0;
            while (sum2<tempFlow) {
                int flag2 = -1;
                for (IntakeEntity intakeEntity : intakeEntityList) {
                    if (intakeEntity.getEntityType() == EntityTypeEnum.CATCHMENT) {
                        flag2++;
                    } else {
                        sum2+=Math.min(intakeEntity.getTruthIntakeFlow()[time],this.step) / lossParamListCumSum.get(flag2);
                        intakeEntity.getTruthIntakeFlow()[time]-=Math.min(intakeEntity.getTruthIntakeFlow()[time],this.step);
                    }
                }
            }
            return Integer.MAX_VALUE;
        }
    }


}
