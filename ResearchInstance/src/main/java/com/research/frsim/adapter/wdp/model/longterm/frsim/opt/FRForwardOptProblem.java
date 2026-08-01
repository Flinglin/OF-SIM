package com.research.frsim.adapter.wdp.model.longterm.frsim.opt;

import java.util.List;
import java.util.Map;

import com.research.frsim.adapter.wdp.bean.Project;
import com.research.frsim.adapter.wdp.bean.entity.canal.CanalEntity;
import com.research.frsim.adapter.wdp.bean.entity.entity.Entity;
import com.research.frsim.adapter.wdp.bean.entity.gate.GateEntity;
import com.research.frsim.adapter.wdp.bean.entity.intake.IntakeEntity;
import com.research.frsim.adapter.wdp.bean.entity.pump.PumpEntity;
import com.research.frsim.adapter.wdp.bean.entity.reservoir.ReservoirEntity;
import com.research.frsim.adapter.wdp.enumerate.EntityTypeEnum;
import com.research.frsim.adapter.wdp.model.longterm.frsim.sim.FRForwardSimulationModel;
import com.research.frsim.algorithm.opt.commmon.Individual;
import com.research.frsim.algorithm.opt.problem.DecisionSpace;
import com.research.frsim.algorithm.opt.problem.Fitness;
import com.research.frsim.algorithm.opt.problem.Problem;

public class FRForwardOptProblem extends Problem {

	private Project project;

	private FRForwardSimulationModel model;
	

	private List<CanalEntity> canalEntities;

	private List<IntakeEntity> intakeEntities;

	private List<ReservoirEntity> reservoirEntities;

	private List<PumpEntity> pumpEntities;

	private List<GateEntity> gateEntities;

	private Map<String, List<Integer>> codename;
	

	private Map<String, List<double[]>> datamap;
	

	private double[] HHwasterwater;


	public FRForwardOptProblem(FRForwardSimulationModel model, Map<String, List<Integer>> codename, double[] HHwastewater) {
		
		 this.model=model;
		 this.project=model.getProject();
		 this.HHwasterwater=HHwastewater;
		 canalEntities=model.getCanalEntities();
		 intakeEntities=model.getIntakeEntities();
		 reservoirEntities=model.getReservoirEntities();
		 pumpEntities=model.getPumpEntities();
		 gateEntities=model.getGateEntities();
		 this.codename=codename;
		 int tempsize=0;
		 for(String key:codename.keySet()) {
			 List<Integer> integers=codename.get(key);
			 tempsize+=integers.size();
		 }


		 objectiveNum=3;
		 optimalType=new int[] {Problem.MINIMUM,Problem.MINIMUM,Problem.MINIMUM};

		 dimension=tempsize*intakeEntities.size();

		 double[][] despace=new double[dimension][2];
		 for(int i=0;i<dimension;i++) {
			 despace[i][0]=0;
			 despace[i][1]=1;
		 }
		 
		 decisionSpace=new DecisionSpace(despace);
	}


	@Override
	public Fitness calculateFitness(Individual individual) {
		
		double[] despace=individual.getValues();
		List<Integer> FHJ=codename.get("f_pump");
		List<Integer> ZY=codename.get("z_pump");
		List<Integer> allcontain=codename.get("same");

		model.clean();
		model.prepare();
		for(String key:datamap.keySet()) {
			Entity entity=project.seekEntityByName(key);
			if(entity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.PUMP)) {
				PumpEntity pumpEntity=(PumpEntity)entity;
				pumpEntity.setAvgflow(datamap.get(key).get(0).clone());
			}else if(entity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.INTAKE)) {
				IntakeEntity intakeEntity=(IntakeEntity)entity;
				intakeEntity.setIntakeflow(datamap.get(key).get(0).clone());
			}else {
				ReservoirEntity reservoirEntity=(ReservoirEntity)entity;
				reservoirEntity.setWaterlevel(datamap.get(key).get(2).clone());
			}
		}

		int number=0;
		if(FHJ!=null) {
			for(int i=0;i<FHJ.size();i++) {
				int key=FHJ.get(i);
				PumpEntity entity=(PumpEntity)project.seekEntityByName("f_pump");
				entity.getAvgflow()[key]=100;
				for(int j=0;j<intakeEntities.size();j++) {
					IntakeEntity intakeEntity=intakeEntities.get(j);
					intakeEntity.getWatershortageRate()[key]=despace[number];
					number++;
				}
			}
		}
		if(ZY!=null) {
			for(int i=0;i<ZY.size();i++) {
				int key=ZY.get(i);
				PumpEntity entity=(PumpEntity)project.seekEntityByName("z_pump");

				entity.getAvgflow()[key]=100;

				for(int j=0;j<intakeEntities.size();j++) {
					IntakeEntity intakeEntity=intakeEntities.get(j);
					intakeEntity.getWatershortageRate()[key]=despace[number];
					number++;
				}
			}
		}

		if(allcontain!=null) {
			for(int i=0;i<allcontain.size();i++) {
				int key=allcontain.get(i);
				PumpEntity entity=(PumpEntity)project.seekEntityByName("z_pump");
				PumpEntity entity2=(PumpEntity)project.seekEntityByName("f_pump");

				entity.getAvgflow()[key]=100;
				entity2.getAvgflow()[key]=100;

				for(int j=0;j<intakeEntities.size();j++) {
					IntakeEntity intakeEntity=intakeEntities.get(j);
					intakeEntity.getWatershortageRate()[key]=despace[number];
					number++;
				}
			}
		}

		ReservoirEntity reservoirEntity=(ReservoirEntity)project.seekEntityByName("h_reservoir");
		reservoirEntity.setWasteWater(HHwasterwater.clone());

		model.Forwardsimulation();
		Fitness fitness=new Fitness(objectiveNum);
		for(int i=0;i<intakeEntities.size();i++) {
			IntakeEntity intakeEntity=intakeEntities.get(i);
			for(int j=0;j<intakeEntity.getIntakeflow().length;j++) {
				if(intakeEntity.getIntakeflow()[j]<intakeEntity.getMinflow()[j]||(intakeEntity.getIntakeflow()[j]>1.2*intakeEntity.getMaxflow()[j]&&intakeEntity.getMaxflow()[j]>0)) {
					fitness.setFeasible(false);
					fitness.setFitness(new double[] {Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE});
					individual.setFitness(fitness);
					return fitness;
				}
			}
		}

		for(int i=0;i<pumpEntities.size();i++) {
			PumpEntity entity=pumpEntities.get(i);
			for(int j=0;j<entity.getAvgflow().length;j++) {
				if(entity.getEntityStat().getName().equals("f_pump")||entity.getEntityStat().getName().equals("z_pump")) {
					if((entity.getAvgflow()[j]<entity.getMinflow()[j]||entity.getAvgflow()[j]>entity.getMaxflow()[j])&&entity.getAvgflow()[j]>0) {
						fitness.setFeasible(false);
						fitness.setFitness(new double[] {Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE});
						individual.setFitness(fitness);
						return fitness;
					}else if((Math.abs(entity.getAvgflow()[j])>600)&&entity.getAvgflow()[j]<0) {
						fitness.setFeasible(false);
						fitness.setFitness(new double[] {Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE});
						individual.setFitness(fitness);
						return fitness;
					}
				}else {
					if((entity.getAvgflow()[j]<entity.getMinflow()[j]||entity.getAvgflow()[j]>entity.getMaxflow()[j])&&entity.getAvgflow()[j]>0) {
						fitness.setFeasible(false);
						fitness.setFitness(new double[] {Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE});
						individual.setFitness(fitness);
						return fitness;
					}else if((entity.getAvgflow()[j]>entity.getMaxflow()[j]||entity.getAvgflow()[j]<entity.getMinflow()[j])&&entity.getAvgflow()[j]<0) {
						fitness.setFeasible(false);
						fitness.setFitness(new double[] {Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE});
						individual.setFitness(fitness);
						return fitness;
					}
				}
				
			}
		}

		for(int i=0;i<gateEntities.size();i++) {
			GateEntity gateEntity=gateEntities.get(i);
			for(int j=0;j<gateEntity.getAvgflow().length;j++) {
				if(Math.abs(gateEntity.getAvgflow()[j])>gateEntity.getMaxflow()[j]) {
					fitness.setFeasible(false);
					fitness.setFitness(new double[] {Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE});
					individual.setFitness(fitness);
					return fitness;
				}
			}
		}
		double delta=0;
		for (IntakeEntity entity : intakeEntities) {
			for (int j = 0; j < entity.getDemandflow().length; j++) {
				delta += entity.getDemandflow()[j] - entity.getIntakeflow()[j];
			}
		}
		double waste=0;
		for (ReservoirEntity entity : reservoirEntities) {
			for (int j = 0; j < entity.getWasteWater().length; j++) {
				waste += entity.getWasteWater()[j];
			}
		}

		fitness.setFeasible(true);
		fitness.setFitness(new double[] {delta*10*24*3600,waste,0});
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


	public void setCanalEntities(List<CanalEntity> canalEntities) {
		this.canalEntities = canalEntities;
	}


	public List<IntakeEntity> getIntakeEntities() {
		return intakeEntities;
	}


	public void setIntakeEntities(List<IntakeEntity> intakeEntities) {
		this.intakeEntities = intakeEntities;
	}


	public List<ReservoirEntity> getReservoirEntities() {
		return reservoirEntities;
	}


	public void setReservoirEntities(List<ReservoirEntity> reservoirEntities) {
		this.reservoirEntities = reservoirEntities;
	}


	public List<PumpEntity> getPumpEntities() {
		return pumpEntities;
	}


	public void setPumpEntities(List<PumpEntity> pumpEntities) {
		this.pumpEntities = pumpEntities;
	}


	public List<GateEntity> getGateEntities() {
		return gateEntities;
	}


	public void setGateEntities(List<GateEntity> gateEntities) {
		this.gateEntities = gateEntities;
	}


	public Map<String, List<double[]>> getDatamap() {
		return datamap;
	}


	public void setDatamap(Map<String, List<double[]>> datamap) {
		this.datamap = datamap;
	}
	

	
	
	
	

}
