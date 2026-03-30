package com.research.research.reservoir;

import com.research.core.entity.Entity;
import com.research.core.entity.canal.CanalEntity;
import com.research.core.entity.intake.IntakeEntity;
import com.research.core.entity.pump.PumpEntity;
import com.research.core.entity.reservoir.ReservoirEntity;
import com.research.core.enums.EntityTypeEnum;
import com.research.core.project.Project;
import com.research.research.ResearchReverseSimulation;
import com.research.research.gate.ResearchGateEntity;
import com.research.research.pump.ResearchPumpEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Slf4j
public class ResearchReservoirEntity extends ReservoirEntity {

    private boolean subtractFlowSource = false;


    public ResearchReservoirEntity(Project project) {
        super(project);
    }

    public ResearchReservoirEntity(Project project, Object[] param) {
        super(project, param);
    }

    public int flowControl(CanalEntity canalEntity, ResearchReverseSimulation sim, double outPutFlow, double sumIntakeFlow, double sectionInFlow, int time) {
        double theoryFlow = calculateInFlow(outPutFlow, sumIntakeFlow, sectionInFlow, canalEntity.getLossParam());
        double overRate;
        if (theoryFlow > this.outMaxFlow[time] + 1e-10) {
            overRate = (theoryFlow - this.outMaxFlow[time]) / sumIntakeFlow;
        } else if (theoryFlow < this.outMinFlow[time]) {
            overRate = (this.outMinFlow[time] - theoryFlow) / sumIntakeFlow;
        } else {
            overRate = -Double.MAX_VALUE;
        }
        if (theoryFlow > this.outMaxFlow[time] + 1e-10 && overRate <= 1) {
            if (canalEntity.getDownStreamEntity().getName().equals("x_gate")) {
                ResearchGateEntity gateEntity = (ResearchGateEntity) canalEntity.getDownStreamEntity();
                double rate = (this.outMaxFlow[time] * canalEntity.getLossParam() - sumIntakeFlow + canalEntity.getSectionInFlow()[time]) / gateEntity.getAvgFlow()[time];
                canalEntity.getWatershedRate()[time] = rate;
                canalEntity.getDownStreamEntity().getUpCanalEntities().stream().filter(x -> x.getUpStreamEntity().getName().equals("l_gate")).forEach(canalEntity1 -> {
                    canalEntity1.getWatershedRate()[time] = 1 - rate;
                });
            }
            double tempFlow = theoryFlow - this.outMaxFlow[time];
            adjustIntakeFlow(canalEntity, sim, tempFlow, time, 1);
            this.outFlow[time] = this.outMaxFlow[time];
            return 0;
        } else if (theoryFlow > this.outMaxFlow[time] + 1e-10 && overRate > 1) {
            double tempFlow = theoryFlow - this.outMaxFlow[time];
            this.outFlow[time] = this.outMaxFlow[time];
            if (!this.name.equals("w_reservoir")) {
                ResearchGateEntity gateEntity = (ResearchGateEntity) sim.seekEntityByNameAndType("x_gate", EntityTypeEnum.GATE);
                gateEntity.setSubtractFlowSource(true);
                List<CanalEntity> canalEntityList = gateEntity.getUpCanalEntities();
                for (int rt = 0; rt < canalEntityList.size(); rt++) {
                    gateEntity.getSubtractFlow()[rt] = gateEntity.getAvgFlow()[time] * canalEntityList.get(rt).getWatershedRate()[time];
                }
            }
            return adjustIntakeFlow(canalEntity, sim, tempFlow, time, 2);
        } else if (theoryFlow < this.outMinFlow[time]&& overRate < 0.3) {
            double tempFlow = this.outMinFlow[time] - theoryFlow;
            adjustIntakeFlow(canalEntity, sim, tempFlow, time, 0);
            this.outFlow[time] = this.outMinFlow[time];
            return 0;
        }
        else if (theoryFlow < this.outMinFlow[time] && overRate >= 0.3) {
            this.outFlow[time] = theoryFlow;
            return 0;
        }
        else {
            this.outFlow[time] = theoryFlow;
            return 0;
        }
    }

    private double calculateInFlow(double outPutFlow, double sumIntakeFlow, double sectionInFlow, double lossParam) {
        return outPutFlow / lossParam + sumIntakeFlow - sectionInFlow;
    }

    public int adjustIntakeFlow(CanalEntity canalEntity, ResearchReverseSimulation sim, double tempFlow, int time, int type) {
        List<IntakeEntity> intakeEntities = canalEntity.getIntakeEntities();

        double totalvolume = 0;
        for (IntakeEntity intakeEntity : intakeEntities) {
            totalvolume += intakeEntity.getTruthIntakeFlow()[time];
        }
        if (type == 0) {
            for (IntakeEntity entity : intakeEntities) {
                entity.getTruthIntakeFlow()[time] += tempFlow * (entity.getTruthIntakeFlow()[time] / totalvolume);
            }
            return 0;
        } else if (type == 1) {
            for (IntakeEntity entity : intakeEntities) {
                entity.getTruthIntakeFlow()[time] -= tempFlow * (entity.getTruthIntakeFlow()[time] / totalvolume);
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
                    boolean wabuAbleAdjust = (wabu.getReservoirLevel()[time + 1] - wabu.getReservoirLevel()[time]) > 0;
                    if (wabuAbleAdjust) {
                        double marginFlow1 = tempFlow - truthIntakeFlowSum;
                        adjustFlow = marginFlow1 * lossParamListCumSum.get(3);
                        tempFlow = truthIntakeFlowSum;
                    } else {
                        throw new RuntimeException("check the project parameter");
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
            if (!this.name.equals("w_reservoir")) {
                for (int q = 0; q < intakeEntityList.size(); q++) {
                    if (intakeEntityList.get(q).getEntityType() == EntityTypeEnum.CATCHMENT && intakeEntityList.get(q).getPlanIntakeFlow() != null) {
                        if (intake != null) {
                            if (intake.getName().equals("p_pump") && this.name.equals("c_reservoir")) {
                                ResearchGateEntity gateEntity = (ResearchGateEntity) sim.seekEntityByNameAndType("x_gate", EntityTypeEnum.GATE);
                                gateEntity.getSubtractFlow()[2] = tempSubtractFlow1 / gateEntity.getDownCanalEntities().getFirst().getLossParam() + (tempSubtractFlow[2] + tempSubtractFlow[3] + tempSubtractFlow[4]) * lossParamListCumSum.get(1);
                            }
                            if (intake.getName().equals("p_pump") && this.name.equals("C_reservoir")) {
                                ResearchGateEntity gateEntity = (ResearchGateEntity) sim.seekEntityByNameAndType("x_gate", EntityTypeEnum.GATE);
                                gateEntity.getSubtractFlow()[3] = tempSubtractFlow1 / gateEntity.getDownCanalEntities().getFirst().getLossParam() + (tempSubtractFlow[18] + tempSubtractFlow[19] + tempSubtractFlow[20]) * lossParamListCumSum.get(2);
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
                            if (intake.getName().equals("p_pump") && this.name.equals("c_reservoir")) {
                                ResearchGateEntity gateEntity = (ResearchGateEntity) sim.seekEntityByNameAndType("x_gate", EntityTypeEnum.GATE);
                                gateEntity.getSubtractFlow()[2] = tempSubtractFlow1 / gateEntity.getDownCanalEntities().getFirst().getLossParam() + (tempSubtractFlow[2] + tempSubtractFlow[3] + tempSubtractFlow[4]) * lossParamListCumSum.get(1);
                            }
                            if (intake.getName().equals("p_pump") && this.name.equals("C_reservoir")) {
                                ResearchGateEntity gateEntity = (ResearchGateEntity) sim.seekEntityByNameAndType("x_gate", EntityTypeEnum.GATE);
                                gateEntity.getSubtractFlow()[3] = tempSubtractFlow1 / gateEntity.getDownCanalEntities().getFirst().getLossParam() + (tempSubtractFlow[18] + tempSubtractFlow[19] + tempSubtractFlow[20]) * lossParamListCumSum.get(2);
                            }
                            intake.getPlanIntakeFlow()[time] -= tempSubtractFlow1;
                        }
                    }
                }
            }
            return 1;
        }
    }
    public double reservoirLevelControl(CanalEntity canalEntity, double avgFlow, int time) {
        if (this.name.equals("h_reservoir")) {
            double sumIntakeFlow = canalEntity.calculateSumIntakeFlow(time);
            double reservoirIntakeFlow = 0;
            for (IntakeEntity entity : this.intakeEntities) {
                reservoirIntakeFlow += entity.getTruthIntakeFlow()[time];
            }
            double normalStorage = super.levelToStorageCurve.getV1ByV0(super.maxLevel[time]);
            double deadStorage = super.levelToStorageCurve.getV1ByV0(super.minLevel[time]);
            double nowStorage = super.levelToStorageCurve.getV1ByV0(super.reservoirLevel[time]);
            double nextStorage = nowStorage + this.sectionInFlow[time] * 60 * 60 * 24 * 10 - super.outFlow[time] * 60 * 60 * 24 * 10 - sumIntakeFlow * 60 * 60 * 24 * 10 - reservoirIntakeFlow * 60 * 60 * 24 * 10;
            if (nextStorage > normalStorage) {
                double m = nextStorage - normalStorage;
                this.wasteWater[time] = m;
                if (Double.isNaN(super.maxLevel[time])) {
                    throw new RuntimeException("check the project parameter");
                }
                super.reservoirLevel[time + 1] = super.maxLevel[time];
                super.inFlow[time] = 0;
            } else if (nextStorage < deadStorage) {
                double m = (deadStorage - nextStorage) / (24 * 3600 * 10);
                if (Double.isNaN(super.minLevel[time])) {
                    throw new RuntimeException("check the project parameter");
                }
                super.reservoirLevel[time + 1] = super.minLevel[time];
                super.inFlow[time] = canalEntity.getSectionInFlow()[time] + m;
                if (super.inFlow[time] > super.inMaxFlow[time]) {
                    double temp = super.inFlow[time] - super.inMaxFlow[time];
                    for (IntakeEntity entity : this.intakeEntities) {
                        entity.getTruthIntakeFlow()[time] -= (entity.getTruthIntakeFlow()[time] / reservoirIntakeFlow) * temp;
                        if (entity.getTruthIntakeFlow()[time] < 0) {
                            throw new RuntimeException("check the project parameter");
                        }
                    }
                    super.inFlow[time] = super.inMaxFlow[time];
                }
            } else {
                if (Double.isNaN(super.storageToLevelCurve.getV1ByV0(nextStorage))) {
                    System.out.println(0);
                }
                super.reservoirLevel[time + 1] = super.storageToLevelCurve.getV1ByV0(nextStorage);
                super.inFlow[time] = 0;
            }
            return 0;
        } else {
            double sumIntakeFlow = canalEntity.calculateSumIntakeFlow(time);
            double reservoirIntakeFlow = 0;
            for (IntakeEntity entity : this.intakeEntities) {
                reservoirIntakeFlow += entity.getTruthIntakeFlow()[time];
            }
            double normalStorage = super.levelToStorageCurve.getV1ByV0(super.maxLevel[time]);
            double deadStorage = super.levelToStorageCurve.getV1ByV0(super.minLevel[time]);
            double nowStorage = super.levelToStorageCurve.getV1ByV0(super.reservoirLevel[time]);
            super.inFlow[time] = avgFlow * canalEntity.getLossParam() + canalEntity.getSectionInFlow()[time] - sumIntakeFlow;
            ResearchPumpEntity pumpEntity = (ResearchPumpEntity) canalEntity.getUpStreamEntity();
            if (super.inFlow[time] < super.getInMinFlow()[time]) {
                pumpEntity.getAvgFlow()[time] += (super.getInMinFlow()[time] - super.inFlow[time]) / canalEntity.getLossParam();
                if (pumpEntity.getAvgFlow()[time] > pumpEntity.getMaxFlow()[time]) {
                    double temp = pumpEntity.getAvgFlow()[time] - pumpEntity.getMaxFlow()[time];
                    adjustIntakeFlow(canalEntity, null, temp, time, 1);
                    pumpEntity.getAvgFlow()[time] = pumpEntity.getMaxFlow()[time];
                }
                super.inFlow[time] = super.getInMinFlow()[time];
            } else if (super.inFlow[time] > super.getInMaxFlow()[time]) {
                double temp = super.inFlow[time] - super.inMaxFlow[time];
                super.inFlow[time]= super.inMaxFlow[time];
                pumpEntity.getAvgFlow()[time]=pumpEntity.getAvgFlow()[time]-temp/canalEntity.getLossParam();
            }

            double nextStorage = nowStorage + pumpEntity.getAvgFlow()[time] * 60 * 60 * 24 * 10 * canalEntity.getLossParam() + this.sectionInFlow[time] * 60 * 60 * 24 * 10 + canalEntity.getSectionInFlow()[time] * 60 * 60 * 24 * 10 - super.outFlow[time] * 60 * 60 * 24 * 10 - sumIntakeFlow * 60 * 60 * 24 * 10 - reservoirIntakeFlow * 60 * 60 * 24 * 10;
            if (nextStorage > normalStorage) {
                super.reservoirLevel[time + 1] = super.maxLevel[time];
                return ((nextStorage - normalStorage) / canalEntity.getLossParam()) / (60 * 60 * 24 * 10);
            } else if (nextStorage < deadStorage) {
                super.reservoirLevel[time + 1] = super.minLevel[time];
                double temp=((nextStorage - deadStorage) / canalEntity.getLossParam()) / (60 * 60 * 24 * 10);
                if(-temp>this.outFlow[time]&&this.isLatency()){
                    super.reservoirLevel[time + 1] = this.getStorageToLevelCurve().getV1ByV0(nowStorage + pumpEntity.getAvgFlow()[time] * 60 * 60 * 24 * 10 * canalEntity.getLossParam() + this.sectionInFlow[time] * 60 * 60 * 24 * 10 + canalEntity.getSectionInFlow()[time] * 60 * 60 * 24 * 10 - sumIntakeFlow * 60 * 60 * 24 * 10 - reservoirIntakeFlow * 60 * 60 * 24 * 10);
                    return this.outFlow[time]<1e-6?0:this.outFlow[time];
                }else if(temp>this.outFlow[time]&&!this.isLatency()){
                    throw new RuntimeException("check the project parameter");
                }
                return temp;
            } else {
                super.reservoirLevel[time + 1] = super.storageToLevelCurve.getV1ByV0(nextStorage);
                if (time > 30) {
                    double theoryLevel = super.maxLevel[time];
                    if (theoryLevel > super.reservoirLevel[time + 1]) {
                        double addFlow = (super.levelToStorageCurve.getV1ByV0(theoryLevel) - super.levelToStorageCurve.getV1ByV0(super.reservoirLevel[time + 1])) / (60 * 60 * 24 * 10);
                        double theoryFlow = super.inFlow[time] + addFlow;
                        if (theoryFlow > super.inMaxFlow[time]) {
                            addFlow = super.inMaxFlow[time] - super.inFlow[time];
                        }
                        theoryFlow = addFlow / canalEntity.getLossParam() + pumpEntity.getAvgFlow()[time];
                        double temp = 0;
                        if (theoryFlow > pumpEntity.getMaxFlow()[time]) {
                            temp = (theoryFlow - pumpEntity.getMaxFlow()[time]) * canalEntity.getLossParam();
                            pumpEntity.getAvgFlow()[time] = pumpEntity.getMaxFlow()[time];
                        }
                        super.inFlow[time] += addFlow - temp;
                        super.reservoirLevel[time + 1] = super.storageToLevelCurve.getV1ByV0(super.levelToStorageCurve.getV1ByV0(super.reservoirLevel[time + 1]) + (addFlow - temp) * 60 * 60 * 24 * 10);
                    }
                }
                return 0;
            }
        }

    }

    public void calculateWasteWater(double wasteWaterFlow, int time) {
        switch (super.wasteWaterDirect) {
            case NORMAL -> {
                super.getInFlow()[time] = super.getInMinFlow()[time];
                super.getWasteWater()[time] = wasteWaterFlow * 60 * 60 * 24 * 10;
            }
            case REVERSE -> {
                if (wasteWaterFlow > super.getInMaxFlow()[time]/this.upCanalEntities.getFirst().getLossParam()) {

                    double inMax=super.inMaxFlow[time]/this.upCanalEntities.getFirst().getLossParam();
                    int k=0;
                    while (wasteWaterFlow>0&&time-k>=0){
                        double reduce=0;
                        if(wasteWaterFlow>inMax){
                            reduce=inMax;
                        }else{
                            reduce=wasteWaterFlow;
                        }
                        reduce=Math.min(reduce,inMax-super.getWasteWater()[time-k]/24/10/3600);
                        if(super.inFlow[time-k]!=0&&k!=0){
                            double temp=super.inFlow[time-k];
                            super.inFlow[time-k]=reduce>=super.inFlow[time-k]?0:super.inFlow[time-k]-reduce;
                            reduce=reduce>=super.inFlow[time-k]?reduce-super.inFlow[time-k]:0;
                            ResearchPumpEntity pp=(ResearchPumpEntity)this.upCanalEntities.getFirst().getUpStreamEntity();
                            if(pp.getAvgFlow()[time-k]<temp){
                                throw new RuntimeException("check the project parameter");
                            }
                            pp.getAvgFlow()[time-k]-=(temp-super.inFlow[time-k])/this.upCanalEntities.getFirst().getLossParam();
                            if(pp.getAvgFlow()[time-k]<-1e-8){
                                throw new RuntimeException("check the project parameter");
                            }
                        } else if (k==0) {
                            super.getInFlow()[time] = 0;
                        }
                        double content = super.levelToStorageCurve.getV1ByV0(super.reservoirLevel[time-k]) - 10 * 24 * 3600 * reduce;
                        double level = super.storageToLevelCurve.getV1ByV0(content);
                        super.reservoirLevel[time -k] = level;
                        super.getWasteWater()[time-k] += reduce * 10 * 24 * 60 * 60;
                        k++;
                        wasteWaterFlow-=reduce;
                    }
                    if(wasteWaterFlow>0){
                        double content = super.levelToStorageCurve.getV1ByV0(super.reservoirLevel[time + 1]) + 10 * 24 * 3600 * (wasteWaterFlow - super.inMaxFlow[time]);
                        double level = super.storageToLevelCurve.getV1ByV0(content);
                        super.reservoirLevel[time + 1] = level;
                    }
                }else{
                    super.getInFlow()[time] = 0;
                    super.getWasteWater()[time] = wasteWaterFlow * 10 * 24 * 60 * 60;
                }
            }
            case FORWARD -> {
                double allOutFlow = super.outFlow[time] + wasteWaterFlow;
                double reservoirAddWasteWater = 0;
                if (allOutFlow > super.getOutMaxFlow()[time]) {
                    wasteWaterFlow = super.getOutMaxFlow()[time] - super.outFlow[time];
                    reservoirAddWasteWater = allOutFlow - super.getOutMaxFlow()[time];
                }
                super.wasteWater[time] = wasteWaterFlow * 10 * 24 * 60 * 60;
                super.outFlow[time] = allOutFlow;
                Entity nowEntity = this;
                while (!nowEntity.getDownCanalEntities().isEmpty()) {
                    CanalEntity canalEntity = nowEntity.getDownCanalEntities().getFirst();
                    Entity e = canalEntity.getDownStreamEntity();
                    switch (e.getEntityType()) {
                        case GATE -> {
                            ResearchGateEntity gateEntity = (ResearchGateEntity) e;
                            wasteWaterFlow *= canalEntity.getLossParam();
                            double tempFlow = gateEntity.getAvgFlow()[time];
                            gateEntity.getAvgFlow()[time] += wasteWaterFlow;
                            if (gateEntity.getAvgFlow()[time] > gateEntity.getMaxFlow()[time]) {
                                reservoirAddWasteWater += (gateEntity.getAvgFlow()[time] - gateEntity.getMaxFlow()[time]) / canalEntity.getLossParam();
                                gateEntity.getAvgFlow()[time] = gateEntity.getMaxFlow()[time];
                                wasteWaterFlow = gateEntity.getAvgFlow()[time] - tempFlow;
                            }
                            nowEntity = gateEntity;
                        }
                        case RESERVOIR -> {
                            ResearchReservoirEntity reservoirEntity = (ResearchReservoirEntity) e;
                            wasteWaterFlow *= canalEntity.getLossParam();
                            reservoirEntity.getInFlow()[time] += wasteWaterFlow;
                            if (reservoirEntity.getInFlow()[time] > reservoirEntity.getInMaxFlow()[time]) {
                                reservoirAddWasteWater += (reservoirEntity.getInFlow()[time] - reservoirEntity.getInMaxFlow()[time]) / canalEntity.getLossParam() / canalEntity.getUpCanalEntities().getFirst().getLossParam();
                                ResearchGateEntity gateEntity = (ResearchGateEntity) nowEntity;
                                gateEntity.getAvgFlow()[time] -= (reservoirEntity.getInFlow()[time] - reservoirEntity.getInMaxFlow()[time]) / canalEntity.getLossParam();
                                reservoirEntity.getInFlow()[time] = reservoirEntity.getInMaxFlow()[time];
                            }
                            nowEntity = reservoirEntity;
                        }
                        default -> {
                        }
                    }
                }
                if (reservoirAddWasteWater > 0) {
                    double content = super.levelToStorageCurve.getV1ByV0(super.maxLevel[time]) + (reservoirAddWasteWater) * 10 * 24 * 3600;
                    super.reservoirLevel[time + 1] = super.storageToLevelCurve.getV1ByV0(content);
                    super.wasteWater[time] = wasteWaterFlow * 10 * 24 * 60 * 60 - (reservoirAddWasteWater) * 10 * 24 * 3600;
                }
            }
        }
    }
}
