package com.research.frsim.adapter.wdp.model.longterm.frsim.opt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.research.frsim.adapter.wdp.bean.Project;
import com.research.frsim.adapter.wdp.bean.entity.canal.CanalEntity;
import com.research.frsim.adapter.wdp.bean.entity.gate.GateEntity;
import com.research.frsim.adapter.wdp.bean.entity.intake.IntakeEntity;
import com.research.frsim.adapter.wdp.bean.entity.pump.PumpEntity;
import com.research.frsim.adapter.wdp.bean.entity.reservoir.ReservoirEntity;
import com.research.frsim.adapter.wdp.model.longterm.frsim.sim.FRReverseSimulationModel;
import com.research.frsim.algorithm.opt.commmon.Individual;
import com.research.frsim.algorithm.opt.problem.DecisionSpace;
import com.research.frsim.algorithm.opt.problem.Fitness;
import com.research.frsim.algorithm.opt.problem.Problem;

public class FRReverseOptProblem extends Problem {

    private FRReverseSimulationModel model;

    private HashMap<Integer, Integer> countt;

    private Project project;


    private List<CanalEntity> canalEntities;

    private List<IntakeEntity> intakeEntities;

    private List<ReservoirEntity> reservoirEntities;

    private List<PumpEntity> pumpEntities;

    private List<GateEntity> gateEntities;

    private double dispatchtarget;

    private Map<String, List<double[]>> lellimit;

    public FRReverseOptProblem(FRReverseSimulationModel model, Map<String, List<double[]>> lellimit) {

        this.model = model;
        this.project = model.getProject();
        this.lellimit = lellimit;
        canalEntities = model.getCanalEntities();
        intakeEntities = model.getIntakeEntities();
        reservoirEntities = model.getReservoirEntities();
        pumpEntities = model.getPumpEntities();
        gateEntities = model.getGateEntities();
        dimension=144;
        objectiveNum = 4;
        optimalType = new int[]{Problem.MINIMUM, Problem.MINIMUM, Problem.MINIMUM, Problem.MINIMUM};

        double[][] despace = new double[144][2];
        int key = 0;
        for (int i = 0; i < reservoirEntities.size(); i++) {
            String id = reservoirEntities.get(i).getEntityStat().getId();
            List<double[]> temp = lellimit.get(id);
            for (int j = 0; j < temp.size(); j++) {
                despace[key] = temp.get(j);
                key++;
            }
        }
        decisionSpace = new DecisionSpace(despace);
        this.countt = new HashMap<>();
    }


    @Override
    public Fitness calculateFitness(Individual individual) {
        model.clean();
        model.prepare();
        double[] despace = individual.getValues();
        for (int i = 0; i < reservoirEntities.size(); i++) {

            ReservoirEntity reservoirEntity = reservoirEntities.get(i);
            double[] waterlevel = new double[37];
            for (int j = 1; j < 37; j++) {
                waterlevel[j] = despace[i * 36 + (j - 1)];
            }
            waterlevel[0] = reservoirEntity.getEntityStat().getIniwaterlevel();
            reservoirEntity.setWaterlevel(waterlevel);
        }

        ReservoirEntity reservoirEntity = (ReservoirEntity) project.seekEntityByName("h_reservoir");
        double[] HHwaterlevel = new double[reservoirEntity.getWaterlevel().length];
        Arrays.fill(HHwaterlevel, reservoirEntity.getEntityStat().getIniwaterlevel());
        reservoirEntity.setWaterlevel(HHwaterlevel);

        model.ReverseSimulation();
        double delta = 0;
        for (IntakeEntity entity : intakeEntities) {
            for (int j = 0; j < entity.getDemandflow().length; j++) {
                delta += entity.getDemandflow()[j] - entity.getIntakeflow()[j];
            }
        }
        double waste = 0;
        for (ReservoirEntity entity : reservoirEntities) {
            for (int j = 0; j < entity.getWasteWater().length; j++) {
                waste += entity.getWasteWater()[j];
            }
        }


        Fitness fitness = new Fitness(objectiveNum);

        for (int i = 0; i < intakeEntities.size(); i++) {
            IntakeEntity intakeEntity = intakeEntities.get(i);
            for (int j = 0; j < intakeEntity.getIntakeflow().length; j++) {
                if (intakeEntity.getIntakeflow()[j] < intakeEntity.getMinflow()[j] || intakeEntity.getIntakeflow()[j] > 1.2 * intakeEntity.getEntityStat().getIntakeability()) {
                    fitness.isEstimated = true;
                }
            }
        }

        for (int i = 0; i < pumpEntities.size(); i++) {
            PumpEntity entity = pumpEntities.get(i);
            for (int j = 0; j < entity.getAvgflow().length; j++) {
                if (entity.getEntityStat().getName().equals("f_pump") || entity.getEntityStat().getName().equals("z_pump")) {
                    continue;
                }
                if (entity.getAvgflow()[j] < entity.getMinflow()[j] || entity.getAvgflow()[j] > entity.getMaxflow()[j]) {
                    fitness.isEstimated = true;
                }
            }
        }

        for (int i = 0; i < gateEntities.size(); i++) {
            GateEntity gateEntity = gateEntities.get(i);
            for (int j = 0; j < gateEntity.getAvgflow().length; j++) {
                if (Math.abs(gateEntity.getAvgflow()[j]) > gateEntity.getMaxflow()[j]) {
                    fitness.isEstimated = true;
                }
            }
        }

        for (int i = 0; i < reservoirEntities.size(); i++) {
            ReservoirEntity entity = reservoirEntities.get(i);
            for (int j = 0; j < entity.getOutflow().length; j++) {
                if (entity.getOutflow()[j] > entity.getOutmaxflow()[j] || entity.getOutflow()[j] < entity.getOutminflow()[j]) {
                    fitness.isEstimated = true;
                }
            }
            for (int j = 0; j < entity.getInflow().length; j++) {
                if (entity.getInflow()[j] > entity.getInmaxflow()[j] || entity.getInflow()[j] < entity.getInminflow()[j]) {
                    fitness.isEstimated = true;
                }
            }
        }

        for (int i = 0; i < reservoirEntities.size(); i++) {
            ReservoirEntity reservoirEntity2 = reservoirEntities.get(i);
            for (int j = 0; j < reservoirEntity2.getWaterlevel().length; j++) {
                if (reservoirEntity2.getWaterlevel()[j] < reservoirEntity2.getEntityStat().getLevelDead() || reservoirEntity2.getWaterlevel()[j] > reservoirEntity2.getEntityStat().getLevelNormal()) {
                    fitness.isEstimated = true;
                }
            }
        }

        fitness.setFeasible(true);
        fitness.setFitness(new double[]{delta*10*24*3600, waste, 0, 0});
        individual.setFitness(fitness);
        return fitness;
    }

    @Override
    public int Compare(Individual a, Individual b) {

        return CompareFiti(a, b);
    }

    @Override
    public Fitness calculateFitness(Individual individual, int index) {

        return null;
    }

    @Override
    public Object[][] printSolution(Individual individual) {

        return null;
    }


    public List<CanalEntity> getCanalEntities() {
        return canalEntities;
    }


    public List<IntakeEntity> getIntakeEntities() {
        return intakeEntities;
    }


    public List<ReservoirEntity> getReservoirEntities() {
        return reservoirEntities;
    }

}
