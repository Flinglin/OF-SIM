package com.research.research.pump;

import com.research.core.entity.Entity;
import com.research.core.entity.canal.CanalEntity;
import com.research.core.entity.intake.IntakeEntity;
import com.research.core.entity.pump.PumpEntity;
import com.research.core.entity.reservoir.ReservoirEntity;
import com.research.core.enums.EntityTypeEnum;
import com.research.core.project.Project;
import com.research.research.ResearchReverseSimulation;
import com.research.research.gate.ResearchGateEntity;
import com.research.research.reservoir.ResearchReservoirEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ResearchPumpEntity extends PumpEntity {

    public ResearchPumpEntity(Project project) {
        super(project);
    }

    public ResearchPumpEntity(Project project, Object[] param) {
        super(project, param);
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
            if (this.avgFlow[time] > this.maxFlow[time]) {
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
                if (this.name.equals("p_pump")) {
                    throw new RuntimeException("check the project parameter");
                }
                if (this.name.equals("f_pump")) {
                    double transferFlow = (tempFlow - truthIntakeFlowSum) * lossParamListCumSum.get(2);
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
                    boolean wabuAbleAdjust = (wabu.getReservoirLevel()[time + 1] - wabu.getReservoirLevel()[time]) > 0;
                    if (wabuAbleAdjust) {
                        double marginFlow1 = tempFlow - truthIntakeFlowSum;
                        adjustFlow = marginFlow1 * lossParamListCumSum.get(4);
                        tempFlow = truthIntakeFlowSum;
                    } else {
                        throw new RuntimeException(this.name + "无法削减水量");
                    }
                }
            }
            flag = -1;
            int flag2 = 0;
            double[] tempSubtractFlow = new double[intakeEntityList.size()];
            for (int w = 0; w < intakeEntityList.size(); w++) {
                if (intakeEntityList.get(w).getEntityType() == EntityTypeEnum.CATCHMENT) {
                    tempSubtractFlow[w] = Double.MAX_VALUE;
                    flag++;
                } else {
                    if (truthIntakeFlowSum == 0) {
                        tempSubtractFlow[w] = 0;
                    } else {
                        tempSubtractFlow[w] = (tempFlow * (truthIntakeFlowList.get(flag2) / truthIntakeFlowSum)) * lossParamListCumSum.get(flag);
                        intakeEntityList.get(w).getTruthIntakeFlow()[time] -= tempSubtractFlow[w];
                        if (intakeEntityList.get(w).getTruthIntakeFlow()[time] > -1e-10 && intakeEntityList.get(w).getTruthIntakeFlow()[time] < 0) {
                            intakeEntityList.get(w).getTruthIntakeFlow()[time] = 0;
                        } else if (intakeEntityList.get(w).getTruthIntakeFlow()[time] < -1e-10) {
                            throw new RuntimeException("check the project parameter");
                        }
                    }
                    flag2++;
                }
            }
            flag = 0;
            double lossSum = 1;
            for (int r = 0; r < intakeEntityList.size(); r++) {
                if (intakeEntityList.get(r).getEntityType() == EntityTypeEnum.CATCHMENT && intakeEntityList.get(r).getPlanIntakeFlow() != null) {
                    lossSum = 1;
                    flag++;
                } else if (intakeEntityList.get(r).getEntityType() == EntityTypeEnum.CATCHMENT && intakeEntityList.get(r).getPlanIntakeFlow() == null && r != 0) {
                    lossSum *= lossParamList.get(flag);
                    flag++;
                } else {
                    tempSubtractFlow[r] /= lossSum;
                }
            }
            double tempSubtractFlow1 = adjustFlow;
            IntakeEntity intake = null;
            if (!this.name.equals("p_pump")) {
                for (int q = 0; q < intakeEntityList.size(); q++) {
                    if (intakeEntityList.get(q).getEntityType() == EntityTypeEnum.CATCHMENT && intakeEntityList.get(q).getPlanIntakeFlow() != null) {
                        if (intake != null) {
                            if (intake.getName().equals("p_pump") && this.name.equals("f_pump")) {
                                ResearchGateEntity gateEntity = (ResearchGateEntity) sim.seekEntityByNameAndType("x_gate", EntityTypeEnum.GATE);
                                gateEntity.getSubtractFlow()[2] = tempSubtractFlow1 / gateEntity.getDownCanalEntities().getFirst().getLossParam() + (tempSubtractFlow[5] + tempSubtractFlow[6] + tempSubtractFlow[7]) / lossParamListCumSum.get(2);
                            }
                            if (intake.getName().equals("p_pump") && this.name.equals("z_pump")) {
                                ResearchGateEntity gateEntity = (ResearchGateEntity) sim.seekEntityByNameAndType("x_gate", EntityTypeEnum.GATE);
                                gateEntity.getSubtractFlow()[3] = tempSubtractFlow1 / gateEntity.getDownCanalEntities().getFirst().getLossParam() + (tempSubtractFlow[20] + tempSubtractFlow[21] + tempSubtractFlow[22]) / lossParamListCumSum.get(3);
                            }
                            intake.getPlanIntakeFlow()[time] -= tempSubtractFlow1;
                        }
                        intake = intakeEntityList.get(q);
                        tempSubtractFlow1 = adjustFlow;
                    } else if (intakeEntityList.get(q).getEntityType() != EntityTypeEnum.CATCHMENT) {
                        tempSubtractFlow1 += tempSubtractFlow[q];
                    }
                    if (q + 1 == intakeEntityList.size()) {
                        if (intake != null) {
                            if (intake.getName().equals("p_pump") && this.name.equals("f_pump")) {
                                ResearchGateEntity gateEntity = (ResearchGateEntity) sim.seekEntityByNameAndType("x_gate", EntityTypeEnum.GATE);
                                gateEntity.getSubtractFlow()[2] = tempSubtractFlow1 / gateEntity.getDownCanalEntities().getFirst().getLossParam() + (tempSubtractFlow[5] + tempSubtractFlow[6] + tempSubtractFlow[7]) * lossParamListCumSum.get(2);
                            }
                            if (intake.getName().equals("p_pump") && this.name.equals("z_pump")) {
                                ResearchGateEntity gateEntity = (ResearchGateEntity) sim.seekEntityByNameAndType("x_gate", EntityTypeEnum.GATE);
                                gateEntity.getSubtractFlow()[3] = tempSubtractFlow1 / gateEntity.getDownCanalEntities().getFirst().getLossParam() + (tempSubtractFlow[20] + tempSubtractFlow[21] + tempSubtractFlow[22]) * lossParamListCumSum.get(3);
                            }
                            intake.getPlanIntakeFlow()[time] -= tempSubtractFlow1;
                        }
                    }
                }
            }
            return 1;
        }
    }


}
