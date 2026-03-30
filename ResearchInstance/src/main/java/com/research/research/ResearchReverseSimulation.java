package com.research.research;

import com.research.algorithm.optimize.common.base.Individual;
import com.research.core.entity.Entity;
import com.research.core.entity.canal.CanalEntity;
import com.research.core.entity.gate.GateEntity;
import com.research.core.entity.intake.IntakeEntity;
import com.research.core.entity.pump.PumpEntity;
import com.research.core.entity.reservoir.ReservoirEntity;
import com.research.core.enums.EntityTypeEnum;
import com.research.core.project.Project;
import com.research.core.simulation.SimulationModel;
import com.research.research.gate.ResearchGateEntity;
import com.research.research.pump.ResearchPumpEntity;
import com.research.research.reservoir.ResearchReservoirEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Getter
@Setter
public class ResearchReverseSimulation extends SimulationModel {
    private double lastMarginFlow = 0;

    public ResearchReverseSimulation(Project pro) {
        super(pro);
    }

    @Override
    protected void assignSimulation(Individual individual, int timeLength) {
        for (int i = 0; i < this.pumpEntities.size(); i++) {
            this.pumpEntities.get(i).setAvgFlow(ArrayUtils.subarray(individual.getValue(), i * timeLength, (i + 1) * timeLength));
        }
        for (ReservoirEntity reservoirEntity : this.reservoirEntities) {
            double[] level = new double[timeLength + 1];
            level[0] = reservoirEntity.getInitReservoirLevel();
            reservoirEntity.setReservoirLevel(level);
        }
        double[] r = Arrays.stream(individual.getValue()).map(t -> 1 - t).toArray();
        this.canalEntities.stream().filter(x -> x.getName().equals("c_reservoir-x_gate")).forEach(x -> System.arraycopy(individual.getValue(), 108, x.getWatershedRate(), 0, 36));
        this.canalEntities.stream().filter(x -> x.getName().equals("l_gate-x_gate")).forEach(x -> System.arraycopy(r, 108, x.getWatershedRate(), 0, 36));
    }

    public void reverseSimulate() {
        for (int time = 0; time < this.initialProject.getTimeUnits().size(); time++) {
            dynamicallyAdjustBoundaries(time);
            for (int canalEntityOrder = 0; canalEntityOrder < this.canalEntities.size(); canalEntityOrder++) {
                CanalEntity canalEntity = this.canalEntities.get(canalEntityOrder);
                Entity upEntity = canalEntity.getUpStreamEntity();
                Entity downEntity = canalEntity.getDownStreamEntity();
                if (upEntity.getEntityType() == EntityTypeEnum.RESERVOIR) {
                    if (downEntity.getEntityType() == EntityTypeEnum.GATE) {
                        ResearchReservoirEntity reservoirEntity = (ResearchReservoirEntity) upEntity;
                        ResearchGateEntity gateEntity = (ResearchGateEntity) downEntity;
                        double sumIntakeFlow = canalEntity.calculateSumIntakeFlow(time);
                        double truthFlow = calculateWatershed(downEntity.getUpCanalEntities(), gateEntity.getAvgFlow()[time], canalEntity, time);
                        int callback = reservoirEntity.flowControl(canalEntity, this, truthFlow, sumIntakeFlow, canalEntity.getSectionInFlow()[time], time);
                        if (callback != 0) {
                            time -= callback;
                            break;
                        }
                    } else {
                        throw new RuntimeException("check the project parameter");
                    }
                } else if (downEntity.getEntityType() == EntityTypeEnum.RESERVOIR) {
                    if (upEntity.getEntityType() == EntityTypeEnum.PUMP) {
                        ResearchReservoirEntity reservoirEntity = (ResearchReservoirEntity) downEntity;
                        ResearchPumpEntity pumpEntity = (ResearchPumpEntity) upEntity;
                        double needToChangeFlowWithLoss = reservoirEntity.reservoirLevelControl(canalEntity, pumpEntity.getAvgFlow()[time], time);
                        int callback = pumpEntity.flowControl(canalEntity, this, needToChangeFlowWithLoss, time);
                        if (callback != 0) {
                            time -= callback;
                            break;
                        }
                    } else if (upEntity.getEntityType() == EntityTypeEnum.GATE) {
                        ResearchReservoirEntity reservoirEntity = (ResearchReservoirEntity) downEntity;
                        ResearchGateEntity gateEntity = (ResearchGateEntity) upEntity;
                        double sumIntakeFlow = canalEntity.calculateSumIntakeFlow(time);
                        int callback = gateEntity.flowControl(canalEntity, this, reservoirEntity.getInFlow()[time], canalEntity.getSectionInFlow()[time], sumIntakeFlow, canalEntity.getLossParam(), time);
                        if (callback != 0) {
                            if (callback == 1) {
                                time -= callback;
                                break;
                            } else if (callback == 2) {
                                canalEntityOrder -= callback;
                            }
                        }
                    } else {
                        throw new RuntimeException("check the project parameter");
                    }
                } else if (upEntity.getEntityType() == EntityTypeEnum.GATE) {
                    if (downEntity.getEntityType() == EntityTypeEnum.PUMP) {
                        ResearchGateEntity gateEntity = (ResearchGateEntity) upEntity;
                        ResearchPumpEntity pumpEntity = (ResearchPumpEntity) downEntity;
                        double sumIntakeFlow = canalEntity.calculateSumIntakeFlow(time);
                        int callback = gateEntity.flowControl(canalEntity, this, pumpEntity.getAvgFlow()[time], canalEntity.getSectionInFlow()[time], sumIntakeFlow, canalEntity.getLossParam(), time);
                        if (callback != 0) {
                            if (callback == 1) {
                                time -= callback;
                                break;
                            } else if (callback == 2) {
                                canalEntityOrder -= callback;
                            }
                        }
                    } else if (downEntity.getEntityType() == EntityTypeEnum.GATE) {
                        ResearchGateEntity gateEntity = (ResearchGateEntity) upEntity;
                        ResearchGateEntity gateEntity1 = (ResearchGateEntity) downEntity;
                        double sumIntakeFlow = canalEntity.calculateSumIntakeFlow(time);
                        double truthFlow = calculateWatershed(downEntity.getUpCanalEntities(), gateEntity1.getAvgFlow()[time], canalEntity, time);
                        int callback = gateEntity.flowControl(canalEntity, this, truthFlow, canalEntity.getSectionInFlow()[time], sumIntakeFlow, canalEntity.getLossParam(), time);
                        if (callback != 0) {
                            if (callback == 1) {
                                time -= callback;
                                break;
                            } else if (callback == 2) {
                                canalEntityOrder -= callback;
                            }
                        }
                    } else {
                        throw new RuntimeException("check the project parameter");
                    }
                } else {
                    throw new RuntimeException("check the project parameter");
                }
            }
        }
    }

    public double calculateWatershed(List<CanalEntity> canalEntities, double avgFlow, CanalEntity canalEntity, int t) {
        if (canalEntities.size() == 1) {
            return avgFlow;
        } else {
            ResearchGateEntity gateEntity = (ResearchGateEntity) canalEntity.getDownStreamEntity();
            if (gateEntity.isSubtractFlowSource()) {
                if (canalEntity.getUpStreamEntity().getName().equals("l_gate")) {
                    double truthFlow = gateEntity.getSubtractFlow()[1] - gateEntity.getSubtractFlow()[3];
                    canalEntity.getWatershedRate()[t] = truthFlow / avgFlow;
                    gateEntity.getSubtractFlow()[1] = 0;
                    gateEntity.getSubtractFlow()[3] = 0;
                    gateEntity.setSubtractFlowSource(false);
                    return truthFlow;
                }
                if (canalEntity.getUpStreamEntity().getName().equals("c_reservoir")) {
                    double truthFlow = gateEntity.getSubtractFlow()[0] - gateEntity.getSubtractFlow()[2];
                    gateEntity.getSubtractFlow()[0] = 0;
                    gateEntity.getSubtractFlow()[2] = 0;
                    canalEntity.getWatershedRate()[t] = truthFlow / avgFlow;
                    return truthFlow;
                }
            } else if (gateEntity.isAdjustRate()) {
                return avgFlow * canalEntity.getWatershedRate()[t];
            } else if (gateEntity.isTransferFlow()) {
                return avgFlow * canalEntity.getWatershedRate()[t];
            } else {
                if (avgFlow <= gateEntity.getMaxFlow()[t]) {
                    canalEntities.forEach(entity -> {
                        entity.getWatershedRate()[t] = 0.5;
                    });
                }
                return avgFlow * canalEntity.getWatershedRate()[t];
            }
        }
        throw new RuntimeException("check the project parameter");
    }

    private void dynamicallyAdjustBoundaries(int time) {
        double tempFlow = 0;
        for (int canalEntityOrder = this.canalEntities.size() - 1; canalEntityOrder > -1; canalEntityOrder--) {
            CanalEntity canalEntity = this.canalEntities.get(canalEntityOrder);
            if (canalEntity.getUpStreamEntity().getEntityType() == EntityTypeEnum.PUMP) {
                PumpEntity pumpEntity = (PumpEntity) canalEntity.getUpStreamEntity();
                ReservoirEntity reservoirEntity = (ReservoirEntity) canalEntity.getDownStreamEntity();
                double maxFlow = pumpEntity.getMaxFlow()[time] * canalEntity.getLossParam() + canalEntity.getSectionInFlow()[time];
                reservoirEntity.getInMaxFlow()[time] = Math.max(0, Math.min(maxFlow, reservoirEntity.getInMaxFlow()[time]));
            } else if (canalEntity.getUpStreamEntity().getEntityType() == EntityTypeEnum.RESERVOIR) {
                ReservoirEntity reservoirEntity = (ReservoirEntity) canalEntity.getUpStreamEntity();
                GateEntity gateEntity = (GateEntity) canalEntity.getDownStreamEntity();
                double maxFlow = reservoirEntity.getInMaxFlow()[time] + reservoirEntity.getSectionInFlow()[time] + (reservoirEntity.getLevelToStorageCurve().getV1ByV0(reservoirEntity.getReservoirLevel()[time]) - reservoirEntity.getLevelToStorageCurve().getV1ByV0(reservoirEntity.getMinLevel()[time])) / (24 * 3600 * 10);
                reservoirEntity.getOutMaxFlow()[time] = Math.max(0, Math.min(maxFlow, reservoirEntity.getOutMaxFlow()[time]));
                double nextNodeMaxFlow = reservoirEntity.getOutMaxFlow()[time] * canalEntity.getLossParam() + canalEntity.getSectionInFlow()[time];
                if (gateEntity.getName().equals("x_gate")) {
                    tempFlow += nextNodeMaxFlow;
                    gateEntity.getMaxFlow()[time] = Math.max(0, Math.min(tempFlow, gateEntity.getMaxFlow()[time]));
                    tempFlow = 0;
                } else {
                    gateEntity.getMaxFlow()[time] = Math.max(0, Math.min(nextNodeMaxFlow, gateEntity.getMaxFlow()[time]));
                }
            } else if (canalEntity.getUpStreamEntity().getEntityType() == EntityTypeEnum.GATE) {
                if (canalEntity.getDownStreamEntity().getEntityType() == EntityTypeEnum.GATE) {
                    GateEntity gateEntity = (GateEntity) canalEntity.getUpStreamEntity();
                    double maxFlow = gateEntity.getMaxFlow()[time] * canalEntity.getLossParam() + canalEntity.getSectionInFlow()[time];
                    tempFlow += maxFlow;
                } else if (canalEntity.getDownStreamEntity().getEntityType() == EntityTypeEnum.RESERVOIR) {
                    ReservoirEntity reservoirEntity = (ReservoirEntity) canalEntity.getDownStreamEntity();
                    GateEntity gateEntity = (GateEntity) canalEntity.getUpStreamEntity();
                    double maxFlow = gateEntity.getMaxFlow()[time] * canalEntity.getLossParam() + canalEntity.getSectionInFlow()[time];
                    reservoirEntity.getInMaxFlow()[time] = Math.max(0, Math.min(maxFlow, reservoirEntity.getInMaxFlow()[time]));
                    double maxFlow2 = reservoirEntity.getInMaxFlow()[time] + reservoirEntity.getSectionInFlow()[time] + (reservoirEntity.getLevelToStorageCurve().getV1ByV0(reservoirEntity.getReservoirLevel()[time]) - reservoirEntity.getLevelToStorageCurve().getV1ByV0(reservoirEntity.getMinLevel()[time])) / (24 * 3600 * 10);
                    reservoirEntity.getOutMaxFlow()[time] = Math.max(0, Math.min(maxFlow2, reservoirEntity.getOutMaxFlow()[time]));
                } else {
                    PumpEntity pumpEntity = (PumpEntity) canalEntity.getDownStreamEntity();
                    GateEntity gateEntity = (GateEntity) canalEntity.getUpStreamEntity();
                    double maxFlow = gateEntity.getMaxFlow()[time] * canalEntity.getLossParam() + canalEntity.getSectionInFlow()[time];
                    pumpEntity.getMaxFlow()[time] = Math.max(0, Math.min(maxFlow, pumpEntity.getMaxFlow()[time]));
                }
            }
        }
    }

    public void intakeControl(Entity entity, double marginFlow, int time) {

        if (entity.getDownCanalEntities().isEmpty()) {
            this.lastMarginFlow = marginFlow;
            return;
        }
        CanalEntity canalEntity = entity.getDownCanalEntities().getFirst();
        double lossParam = canalEntity.getLossParam();
        List<IntakeEntity> intakeEntityList = canalEntity.getIntakeEntities();
        Entity downEntity = entity.getDownCanalEntities().getFirst().getDownStreamEntity();
        for (IntakeEntity intakeEntity : intakeEntityList) {
            if (marginFlow == 0) {
                return;
            }
            if (marginFlow > intakeEntity.getTruthIntakeFlow()[time]) {
                marginFlow -= intakeEntity.getTruthIntakeFlow()[time];
                intakeEntity.getTruthIntakeFlow()[time] = 0;
            } else {
                intakeEntity.getTruthIntakeFlow()[time] -= marginFlow;
                marginFlow = 0;
            }
        }
        if (marginFlow > 0) {
            intakeControl(downEntity, marginFlow * lossParam, time);
        }
    }

    protected void initializePumpEntities() {
        for (int i = 0; i < this.initialProject.getPumpEntities().size(); i++) {
            System.arraycopy(this.initialProject.getPumpEntities().get(i).getMinFlow(), 0, this.pumpEntities.get(i).getMinFlow(), 0, this.initialProject.getPumpEntities().get(i).getMinFlow().length);
            System.arraycopy(this.initialProject.getPumpEntities().get(i).getMaxVolume(), 0, this.pumpEntities.get(i).getMaxVolume(), 0, this.initialProject.getPumpEntities().get(i).getMaxVolume().length);
            System.arraycopy(this.initialProject.getPumpEntities().get(i).getMinVolume(), 0, this.pumpEntities.get(i).getMinVolume(), 0, this.initialProject.getPumpEntities().get(i).getMinVolume().length);
            Arrays.fill(this.pumpEntities.get(i).getAvgVolume(), 0);
            Arrays.fill(this.pumpEntities.get(i).getAvgFlow(), 0);
        }
    }

    protected void initializeReservoirEntities() {
        for (int j = 0; j < this.initialProject.getReservoirEntities().size(); j++) {
            System.arraycopy(this.initialProject.getReservoirEntities().get(j).getSectionInFlow(), 0, this.reservoirEntities.get(j).getSectionInFlow(), 0, this.initialProject.getReservoirEntities().get(j).getSectionInFlow().length);
            Arrays.fill(this.reservoirEntities.get(j).getReservoirLevel(), 0);
            Arrays.fill(this.reservoirEntities.get(j).getWasteWater(), 0);
            Arrays.fill(this.reservoirEntities.get(j).getInFlow(), 0);
            Arrays.fill(this.reservoirEntities.get(j).getOutFlow(), 0);
            Arrays.fill(this.reservoirEntities.get(j).getInVolume(), 0);
            Arrays.fill(this.reservoirEntities.get(j).getOutVolume(), 0);
        }
    }
}
