package com.research.apca.gate;

import com.research.apca.core.entity.Entity;
import com.research.apca.core.entity.canal.CanalEntity;
import com.research.apca.core.entity.gate.GateEntity;
import com.research.apca.core.entity.intake.IntakeEntity;
import com.research.apca.core.entity.pump.PumpEntity;
import com.research.apca.core.entity.reservoir.ReservoirEntity;
import com.research.apca.core.enums.EntityTypeEnum;
import com.research.apca.core.project.Project;
import com.research.apca.ResearchReverseSimulation;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Getter
@Setter
public class ResearchGateEntity extends GateEntity {
    private boolean subtractFlowSource = false;
    private double[] subtractFlow = new double[4];
    private boolean adjustRate = false;
    private boolean transferFlow = false;
    private int step;
    public ResearchGateEntity(Project project) {
        super(project);
    }

    public ResearchGateEntity(Project project, Object[] param) {
        super(project, param);
        this.step=20;
    }

    public int flowControl(CanalEntity canalEntity, ResearchReverseSimulation sim, double outFlow, double sectionInFlow, double sumIntakeFlow, double lossParam, int time) {
        int num = 0;
        double inFlow = calculateInFlow(outFlow, sumIntakeFlow, sectionInFlow, lossParam);
        double overRate;
        if (inFlow > this.maxFlow[time]+1e-10 ) {
            overRate = (inFlow - this.maxFlow[time]) / sumIntakeFlow;
        } else if (inFlow < this.minFlow[time]) {
            overRate = (this.minFlow[time] - inFlow) / sumIntakeFlow;
        } else {
            overRate = -Double.MAX_VALUE;
        }
        if (inFlow > this.maxFlow[time]+1e-10  && overRate <= 1) {
            double tempFlow = inFlow - this.maxFlow[time];
            adjustIntakeFlow(canalEntity, sim, tempFlow, time, 1);
            this.avgFlow[time] = this.maxFlow[time];
            if (canalEntity.getUpStreamEntity().getName().equals("l_gate")) {
                ResearchGateEntity gateEntity1 = (ResearchGateEntity) canalEntity.getDownStreamEntity();
                if(gateEntity1.isAdjustRate()) {
                    gateEntity1.setAdjustRate(false);
                }
                if(gateEntity1.isTransferFlow()) {
                    gateEntity1.setTransferFlow(false);
                }
            }
        } else if (inFlow > this.maxFlow[time]+1e-10  && overRate > 1) {
            if (canalEntity.getUpStreamEntity().getName().equals("l_gate")) {
                ResearchGateEntity gateEntity1 = (ResearchGateEntity) canalEntity.getDownStreamEntity();
                if (gateEntity1.isAdjustRate()||gateEntity1.isTransferFlow()) {
                    double tempFlow = inFlow - this.maxFlow[time];
                    this.avgFlow[time] = this.maxFlow[time];
                    if(gateEntity1.isAdjustRate()) {
                        gateEntity1.setAdjustRate(false);
                    }
                    if(gateEntity1.isTransferFlow()) {
                        gateEntity1.setTransferFlow(false);
                    }
                    ResearchGateEntity gateEntity = (ResearchGateEntity) sim.seekEntityByNameAndType("x_gate", EntityTypeEnum.GATE);
                    gateEntity.setSubtractFlowSource(true);
                    List<CanalEntity> canalEntityList = gateEntity.getUpCanalEntities();
                    for (int rt = 0; rt < canalEntityList.size(); rt++) {
                        gateEntity.getSubtractFlow()[rt] = gateEntity.getAvgFlow()[time] * canalEntityList.get(rt).getWatershedRate()[time];
                    }
                    return adjustIntakeFlow(canalEntity, sim, tempFlow, time, 2);
                }
                double rate = (this.maxFlow[time] * canalEntity.getLossParam() - sumIntakeFlow + canalEntity.getSectionInFlow()[time]) / gateEntity1.getAvgFlow()[time];
                canalEntity.getWatershedRate()[time] = rate;
                gateEntity1.getUpCanalEntities().stream().filter(x -> x.getUpStreamEntity().getName().equals("c_reservoir")).forEach(canalEntity1 -> {
                    canalEntity1.getWatershedRate()[time] = 1 - rate;
                });
                this.avgFlow[time] = this.maxFlow[time];
                gateEntity1.setAdjustRate(true);
                return 2;
            }
            double tempFlow = inFlow - this.maxFlow[time];
            this.avgFlow[time] = this.maxFlow[time];
            return adjustIntakeFlow(canalEntity, sim, tempFlow, time, 2);
        } else if (inFlow < this.minFlow[time]  && overRate < 0.3) {
            double tempFlow = this.minFlow[time] - inFlow;
            adjustIntakeFlow(canalEntity, sim, tempFlow, time, 0);
            this.avgFlow[time] = this.minFlow[time];
        } else if (inFlow < this.minFlow[time] && overRate >= 0.3 && outFlow > 0) {
            this.avgFlow[time] = inFlow;
        } else {
            if (canalEntity.getUpStreamEntity().getName().equals("l_gate")) {
                ResearchGateEntity gateEntity1 = (ResearchGateEntity) canalEntity.getDownStreamEntity();
                if(gateEntity1.isAdjustRate()) {
                    gateEntity1.setAdjustRate(false);
                }
                if(gateEntity1.isTransferFlow()) {
                    gateEntity1.setTransferFlow(false);
                }
            }
            this.avgFlow[time] = inFlow;
        }
        return num;
    }

    private double calculateInFlow(double outFlow, double sumIntakeFlow, double sectionInFlow, double lossParam) {
        double result;
        if (outFlow < 0) {
            result = (outFlow + sumIntakeFlow - sectionInFlow) * lossParam;
        } else {
            result = outFlow / lossParam + sumIntakeFlow - sectionInFlow;
        }
        return result;
    }

    private int adjustIntakeFlow(CanalEntity canalEntity, ResearchReverseSimulation sim, double tempFlow, int time, int type) {
        List<IntakeEntity> intakeEntities = canalEntity.getIntakeEntities();
        double totalFlow = 0;
        for (IntakeEntity intakeEntity : intakeEntities) {
            totalFlow += intakeEntity.getTruthIntakeFlow()[time];
        }
        if (type == 0) {
            for (IntakeEntity entity : intakeEntities) {
                entity.getTruthIntakeFlow()[time] +=  tempFlow * (entity.getTruthIntakeFlow()[time] / totalFlow);
            }
            return 0;
        }else if(type==1){
            for (IntakeEntity entity : intakeEntities) {
                entity.getTruthIntakeFlow()[time] -=  tempFlow * (entity.getTruthIntakeFlow()[time] / totalFlow);
            }
            return 0;
        } else if(type==2) {
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
                    }else{
                        throw new RuntimeException("check the project parameter");
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
        }
        return 0;
    }

}
