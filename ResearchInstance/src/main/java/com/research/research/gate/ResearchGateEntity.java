package com.research.research.gate;

import com.research.core.entity.Entity;
import com.research.core.entity.canal.CanalEntity;
import com.research.core.entity.gate.GateEntity;
import com.research.core.entity.intake.IntakeEntity;
import com.research.core.entity.pump.PumpEntity;
import com.research.core.entity.reservoir.ReservoirEntity;
import com.research.core.enums.EntityTypeEnum;
import com.research.core.project.Project;
import com.research.research.ResearchReverseSimulation;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
public class ResearchGateEntity extends GateEntity {
    private boolean subtractFlowSource = false;
    private double[] subtractFlow = new double[4];
    private boolean adjustRate = false;
    private boolean transferFlow = false;

    public ResearchGateEntity(Project project) {
        super(project);
    }

    public ResearchGateEntity(Project project, Object[] param) {
        super(project, param);
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
                ReservoirEntity wabu = (ReservoirEntity) sim.seekEntityByNameAndType("w_reservoir", EntityTypeEnum.RESERVOIR);
                boolean wabuAbleAdjust = (wabu.getReservoirLevel()[time + 1] - wabu.getReservoirLevel()[time]) > 0;
                if (wabuAbleAdjust) {
                    double marginFlow1 = tempFlow - truthIntakeFlowSum;
                    if (this.name.equals("x_gate")) {
                        adjustFlow = marginFlow1 * lossParamListCumSum.get(1);
                    } else if (this.name.equals("l_gate")) {
                        adjustFlow = marginFlow1 * lossParamListCumSum.get(2);
                    }
                    tempFlow=truthIntakeFlowSum;
                } else {
                    throw new RuntimeException("check the project parameter");
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
                    if(truthIntakeFlowSum==0){
                        tempSubtractFlow[w]=0;
                    }else{
                        tempSubtractFlow[w] = (tempFlow * (truthIntakeFlowList.get(flag2) / truthIntakeFlowSum)) * lossParamListCumSum.get(flag);
                        intakeEntityList.get(w).getTruthIntakeFlow()[time] -= tempSubtractFlow[w];
                        if(intakeEntityList.get(w).getTruthIntakeFlow()[time]>-1e-10&&intakeEntityList.get(w).getTruthIntakeFlow()[time]<0){
                            intakeEntityList.get(w).getTruthIntakeFlow()[time]=0;
                        }else if(intakeEntityList.get(w).getTruthIntakeFlow()[time]<-1e-10){
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

            if (!this.name.equals("d_gate")) {
                for (int q = 0; q < intakeEntityList.size(); q++) {
                    if (intakeEntityList.get(q).getEntityType() == EntityTypeEnum.CATCHMENT && intakeEntityList.get(q).getPlanIntakeFlow() != null) {
                        if (intake != null) {
                            intake.getPlanIntakeFlow()[time] -= tempSubtractFlow1;
                            if (intake.getName().equals("p_pump") && this.name.equals("l_gate")) {
                                ResearchGateEntity gateEntity = (ResearchGateEntity) sim.seekEntityByNameAndType("x_gate", EntityTypeEnum.GATE);
                                gateEntity.getSubtractFlow()[3] = tempSubtractFlow1 / gateEntity.getDownCanalEntities().getFirst().getLossParam() + (tempSubtractFlow[5] + tempSubtractFlow[6] + tempSubtractFlow[7]) / lossParamListCumSum.get(2);
                            }
                        }
                        intake = intakeEntityList.get(q);
                        tempSubtractFlow1 = adjustFlow;
                    } else if (intakeEntityList.get(q).getEntityType() != EntityTypeEnum.CATCHMENT) {
                        tempSubtractFlow1 += tempSubtractFlow[q];
                    }
                    if (q + 1 == intakeEntityList.size()) {
                        if (intake != null) {
                            if (intake.getName().equals("p_pump") && this.name.equals("l_gate")) {
                                ResearchGateEntity gateEntity = (ResearchGateEntity) sim.seekEntityByNameAndType("x_gate", EntityTypeEnum.GATE);
                                gateEntity.getSubtractFlow()[3] = tempSubtractFlow1 / gateEntity.getDownCanalEntities().getFirst().getLossParam() + (tempSubtractFlow[5] + tempSubtractFlow[6] + tempSubtractFlow[7]) * lossParamListCumSum.get(2);
                            }
                            intake.getPlanIntakeFlow()[time] -= tempSubtractFlow1;
                        }
                    }
                }
            }
            return 1;
        }
        return 0;
    }

}
