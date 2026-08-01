package com.research.frsim.adapter.wdp.model.longterm.frsim.sim;

import java.util.List;

import com.research.frsim.adapter.wdp.bean.Project;

import com.research.frsim.adapter.wdp.bean.entity.canal.CanalEntity;
import com.research.frsim.adapter.wdp.bean.entity.entity.Entity;
import com.research.frsim.adapter.wdp.bean.entity.gate.GateEntity;
import com.research.frsim.adapter.wdp.bean.entity.intake.IntakeEntity;
import com.research.frsim.adapter.wdp.bean.entity.pump.PumpEntity;
import com.research.frsim.adapter.wdp.bean.entity.reservoir.ReservoirEntity;
import com.research.frsim.adapter.wdp.enumerate.EntityTypeEnum;

import com.research.frsim.adapter.wdp.model.longterm.frsim.util.FRProjectModel;
import com.research.frsim.core.util.DoubleCurve;

public class FRReverseSimulationModel extends FRProjectModel {

	public FRReverseSimulationModel(Project project) {
		super(project);

	}
	
	public int ReverseSimulation() {
		
		int result=0;
		for(int i=0;i<project.getTimeUnits().size();i++) {
			for(int j=canalEntities.size()-1;j!=-1;j--) {
				CanalEntity canalEntity=canalEntities.get(j);
				Entity upentity=canalEntity.getEntityStat().getUpstreammodel();
				Entity downentity=canalEntity.getEntityStat().getDownstreammodel();
				if(upentity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.RESERVOIR)) {
					if(downentity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.PUMP)) {

						ReservoirEntity reservoirEntity=(ReservoirEntity)upentity;
						PumpEntity pumpEntity=(PumpEntity)downentity;
						double outputflow=CalentitiesSize(downentity.getEntityStat().getDowncanalEntitys(), pumpEntity.getAvgflow()[i], canalEntity, i);
						double sumintakeflow=0;
						List<IntakeEntity> entities = canalEntity.getEntityStat().getIntakeEntitys();
						for(int k=0;k<entities.size();k++) {
							sumintakeflow+=entities.get(k).getIntakeflow()[i];
						}
						double inflow=canalEntity.getInflow()[i];
						double losspara=canalEntity.getEntityStat().getLosspara();
						reservoirEntity.CanalFlowControl(canalEntity, reservoirEntity, outputflow, sumintakeflow, inflow, losspara, i);
					}else {

						ReservoirEntity reservoirEntity=(ReservoirEntity)upentity;
						GateEntity gateEntity=(GateEntity)downentity;
						double outputflow=CalentitiesSize(downentity.getEntityStat().getDowncanalEntitys(), gateEntity.getAvgflow()[i],canalEntity,i);
						double sumintakeflow=0;
						List<IntakeEntity> entities = canalEntity.getEntityStat().getIntakeEntitys();
						for(int k=0;k<entities.size();k++) {
							sumintakeflow+=entities.get(k).getIntakeflow()[i];
						}
						double inflow=canalEntity.getInflow()[i];
						double losspara=canalEntity.getEntityStat().getLosspara();

						reservoirEntity.CanalFlowControl(canalEntity, reservoirEntity, outputflow, sumintakeflow, inflow, losspara, i);
					}
					
				}else if(downentity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.RESERVOIR)) {
					if(upentity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.PUMP)) {

						ReservoirEntity reservoirEntity=(ReservoirEntity)downentity;
						PumpEntity pumpEntity=(PumpEntity)upentity;
						double outputflow=reservoirEntity.getOutflow()[i];
						double regioninflow=reservoirEntity.getSectionInflow()[i];
						double nowWlevel=reservoirEntity.getWaterlevel()[i+1];
						double lastlevel=reservoirEntity.getWaterlevel()[i];
						DoubleCurve curve=reservoirEntity.getEntityStat().getWlevel_storageCurve();
						DoubleCurve curve2=reservoirEntity.getEntityStat().getStorage_WlevelCurve();
						if(reservoirEntity.getEntityStat().getName().equals("h_reservoir")) {
							HHreservoircal(outputflow, reservoirEntity, i);
						}else {
							reservoirEntity.ReservoirLevelControl(reservoirEntity, outputflow, regioninflow, nowWlevel, lastlevel, curve, curve2, i);
						}

						double canaloutputflow=reservoirEntity.getInflow()[i];
						double losspara=canalEntity.getEntityStat().getLosspara();
						List<IntakeEntity> entities = canalEntity.getEntityStat().getIntakeEntitys();
						double sumintakeflow=0;
						for(int k=0;k<entities.size();k++) {
							sumintakeflow+=entities.get(k).getIntakeflow()[i];
						}
						double suminflow=canalEntity.getInflow()[i];
						pumpEntity.Flowcontrol(pumpEntity, canaloutputflow, sumintakeflow, suminflow, losspara, i, canalEntity);
					}else {

						ReservoirEntity reservoirEntity=(ReservoirEntity)downentity;
						GateEntity gateEntity=(GateEntity)upentity;
						double outputflow=reservoirEntity.getOutflow()[i];
						double regioninflow=reservoirEntity.getSectionInflow()[i];
						double nowWlevel=reservoirEntity.getWaterlevel()[i+1];
						double lastlevel=reservoirEntity.getWaterlevel()[i];
						DoubleCurve curve=reservoirEntity.getEntityStat().getWlevel_storageCurve();
						DoubleCurve curve2=reservoirEntity.getEntityStat().getStorage_WlevelCurve();

						if(reservoirEntity.getEntityStat().getName().equals("h_reservoir")) {
							HHreservoircal(outputflow, reservoirEntity, i);
						}else {
							reservoirEntity.ReservoirLevelControl(reservoirEntity, outputflow, regioninflow, nowWlevel, lastlevel, curve, curve2, i);
						}

						double canaloutputflow=reservoirEntity.getInflow()[i];
						double losspara=canalEntity.getEntityStat().getLosspara();
						List<IntakeEntity> entities = canalEntity.getEntityStat().getIntakeEntitys();
						double sumintakeflow=0;
						for(int k=0;k<entities.size();k++) {
							sumintakeflow+=entities.get(k).getIntakeflow()[i];
						}
						double suminflow=canalEntity.getInflow()[i];
						gateEntity.Flowcontrol(canalEntity, gateEntity, canaloutputflow, sumintakeflow, suminflow, losspara, i);
					}
					
				}else {
					if(downentity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.PUMP)&&upentity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.PUMP)) {
						PumpEntity pumpEntity=(PumpEntity)downentity;
						PumpEntity pumpEntity2=(PumpEntity)upentity;
						List<IntakeEntity> entities = canalEntity.getEntityStat().getIntakeEntitys();
						double sumintakeflow=0;
						for(int k=0;k<entities.size();k++) {
							sumintakeflow+=entities.get(k).getIntakeflow()[i];
						}

						double outputflow=CalentitiesSize(downentity.getEntityStat().getDowncanalEntitys(), pumpEntity.getAvgflow()[i],canalEntity,i);
						double suminflow=canalEntity.getInflow()[i];
						double losspara=canalEntity.getEntityStat().getLosspara();
						pumpEntity2.Flowcontrol(pumpEntity2, outputflow, sumintakeflow, suminflow, losspara, i, canalEntity);
					}else if(downentity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.PUMP)&&upentity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.GATE)) {
						PumpEntity pumpEntity=(PumpEntity)downentity;
						GateEntity pumpEntity2=(GateEntity)upentity;
						List<IntakeEntity> entities = canalEntity.getEntityStat().getIntakeEntitys();
						double sumintakeflow=0;
						for(int k=0;k<entities.size();k++) {
							sumintakeflow+=entities.get(k).getIntakeflow()[i];
						}

						double outputflow=CalentitiesSize(downentity.getEntityStat().getDowncanalEntitys(), pumpEntity.getAvgflow()[i],canalEntity,i);
						double suminflow=canalEntity.getInflow()[i];
						double losspara=canalEntity.getEntityStat().getLosspara();
						pumpEntity2.Flowcontrol(canalEntity, pumpEntity2, outputflow, sumintakeflow, suminflow, losspara, i);
					}else if(downentity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.GATE)&&upentity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.PUMP)) {
						GateEntity pumpEntity=(GateEntity)downentity;
						PumpEntity pumpEntity2=(PumpEntity)upentity;
						List<IntakeEntity> entities = canalEntity.getEntityStat().getIntakeEntitys();
						double sumintakeflow=0;
						for(int k=0;k<entities.size();k++) {
							sumintakeflow+=entities.get(k).getIntakeflow()[i];
						}

						double outputflow=CalentitiesSize(downentity.getEntityStat().getDowncanalEntitys(), pumpEntity.getAvgflow()[i],canalEntity,i);
						double suminflow=canalEntity.getInflow()[i];
						double losspara=canalEntity.getEntityStat().getLosspara();
						pumpEntity2.Flowcontrol(pumpEntity2, outputflow, sumintakeflow, suminflow, losspara, i, canalEntity);
						
					}else {
						
						GateEntity pumpEntity=(GateEntity)downentity;
						GateEntity pumpEntity2=(GateEntity)upentity;
						List<IntakeEntity> entities = canalEntity.getEntityStat().getIntakeEntitys();
						double sumintakeflow=0;
						for(int k=0;k<entities.size();k++) {
							sumintakeflow+=entities.get(k).getIntakeflow()[i];
						}

						double outputflow=CalentitiesSize(downentity.getEntityStat().getDowncanalEntitys(), pumpEntity.getAvgflow()[i],canalEntity,i);
						double suminflow=canalEntity.getInflow()[i];
						double losspara=canalEntity.getEntityStat().getLosspara();
						pumpEntity2.Flowcontrol(canalEntity, pumpEntity2, outputflow, sumintakeflow, suminflow, losspara, i);

						
					}	
				}	
			}	
		}
		return result;
		
	}

	public void HHreservoircal(double outputflow,ReservoirEntity reservoirEntity,int t) {
		double inflow=outputflow;
		reservoirEntity.getInflow()[t]=inflow;
	}

	public double calinflow(double outflow,double sumintakeflow,double suminflow,double losspara) {
		
		double result=(outflow+sumintakeflow-suminflow)/losspara;
		return result;
		
	}

	public double calreservoir(double outputflow,double regioninflow,double nowwaterlevel,double lastwaterlevel,DoubleCurve curve) {

		double nowstorage=curve.getV1ByV0(nowwaterlevel);
		double laststorage=curve.getV1ByV0(lastwaterlevel);
		double deltaV=nowstorage-laststorage;
		double sumresult=deltaV/(10*24*3600)-regioninflow+outputflow;
		return sumresult;
		
	}

	public double CalentitiesSize(List<CanalEntity> canalEntities,double avgflow,CanalEntity canalEntity,int t) {
		
		double reflow=0;
		if(canalEntities.size()==1) {
			return avgflow;
		}else {
			for(int i=0;i<canalEntities.size();i++) {
				CanalEntity entity=canalEntities.get(i);
				if(entity.getEntityStat().getUpstreammodel().getEntityStat().getName().equals("l_gate")) {
					GateEntity gateEntity=(GateEntity)entity.getEntityStat().getUpstreammodel();
					double flow=gateEntity.getMaxflow()[t];
					flow=flow*entity.getEntityStat().getLosspara();
					reflow=flow;
				}
			}
			if(avgflow>reflow) {
				if(canalEntity.getEntityStat().getUpstreammodel().getEntityStat().getName().equals("l_gate")) {
					return reflow;
				}else {
					double key=avgflow-reflow;
					return key;
				}
			}else {
				if(canalEntity.getEntityStat().getUpstreammodel().getEntityStat().getName().equals("l_gate")) {
					return avgflow;
				}else {
					double key=0;
					return key;
				}
			}
			
		}
	}

	public void constraintprocess(CanalEntity canalEntity,double reducerate,int t) {
		
		List<IntakeEntity> intakeEntities=canalEntity.getEntityStat().getIntakeEntitys();
		
		for(int i=0;i<intakeEntities.size();i++) {
			IntakeEntity entity=intakeEntities.get(i);
			entity.getIntakeflow()[t]=intakeEntities.get(i).getIntakeflow()[t]*reducerate;
		}
		
		
		
	}

	public double summarydemand(CanalEntity canalEntity,int t) {
		
		double result=0;
		List<IntakeEntity> intakeEntities=canalEntity.getEntityStat().getIntakeEntitys();
		for(int i=0;i<intakeEntities.size();i++) {
			IntakeEntity entity=intakeEntities.get(i);
			result=result+entity.getIntakeflow()[t];
		}
		
		return result;
	}

	public double forwardlevelcontrol(double inflow,double outputflow,double regioninflow,double beginlevel,DoubleCurve l_ccurve,DoubleCurve c_lcurve) {
		

		double begaincontent=l_ccurve.getV1ByV0(beginlevel);
		double result=(inflow+regioninflow-outputflow)*10*24*3600+begaincontent;
		double endlevel=c_lcurve.getV1ByV0(result);
		return endlevel;
		
	}
	

	public double reservoirLevelcontrol(double tempresult,ReservoirEntity reservoirEntity,double outputflow,double regioninflow,double lastlevel,DoubleCurve curve,DoubleCurve curve2,int i) {
		if(tempresult>reservoirEntity.getInmaxflow()[i]) {

			if(tempresult>0) {
				tempresult=reservoirEntity.getInmaxflow()[i];
			}else {
				tempresult=-reservoirEntity.getInmaxflow()[i];
			}
			double waterlevel=forwardlevelcontrol(tempresult, outputflow, regioninflow, lastlevel, curve, curve2);
			if(waterlevel<reservoirEntity.getEntityStat().getLevelDead()) {
				waterlevel=reservoirEntity.getEntityStat().getLevelDead();
				reservoirEntity.getWaterlevel()[i+1]=waterlevel;

				tempresult=calreservoir(outputflow, regioninflow, waterlevel, lastlevel, curve);
				reservoirEntity.getInflow()[i]=tempresult;
			}else if(waterlevel>reservoirEntity.getEntityStat().getLevelNormal()) {

				double content=curve.getV1ByV0(waterlevel)-curve.getV1ByV0(reservoirEntity.getEntityStat().getLevelNormal());
				double tempflow=content/(10*24*3600);
				waterlevel=reservoirEntity.getEntityStat().getLevelNormal();
				reservoirEntity.getWaterlevel()[i+1]=waterlevel;
				reservoirEntity.getInflow()[i]=tempresult;
				return tempflow;
			}else {
				reservoirEntity.getInflow()[i]=tempresult;
				reservoirEntity.getWaterlevel()[i+1]=waterlevel;
			}
		}else if(tempresult<reservoirEntity.getInminflow()[i]) {
			
			tempresult=reservoirEntity.getInminflow()[i];
			
			double waterlevel=forwardlevelcontrol(tempresult, outputflow, regioninflow, lastlevel, curve, curve2);
			if(waterlevel>reservoirEntity.getEntityStat().getLevelNormal()) {

				double content=curve.getV1ByV0(waterlevel)-curve.getV1ByV0(reservoirEntity.getEntityStat().getLevelNormal());
				double tempflow=content/(10*24*3600);
				waterlevel=reservoirEntity.getEntityStat().getLevelNormal();
				reservoirEntity.getWaterlevel()[i+1]=waterlevel;
				reservoirEntity.getInflow()[i]=tempresult;

				return tempflow;
			}else if(waterlevel<reservoirEntity.getEntityStat().getLevelDead()) {
				waterlevel=reservoirEntity.getEntityStat().getLevelDead();
				reservoirEntity.getWaterlevel()[i+1]=waterlevel;

				tempresult=calreservoir(outputflow, regioninflow, waterlevel, lastlevel, curve);
				reservoirEntity.getInflow()[i]=tempresult;
			}else {
				reservoirEntity.getInflow()[i]=tempresult;
				reservoirEntity.getWaterlevel()[i+1]=waterlevel;
			}
			
		}else {
			reservoirEntity.getInflow()[i]=tempresult;
		}
		return 0;
	}

	public void Forwardfloodsim(ReservoirEntity reservoirEntity,double tempwatercontent,int i,DoubleCurve curve,DoubleCurve curve2) {
		
		Entity entity=reservoirEntity;
		String id=reservoirEntity.getEntityStat().getWasteentity().getEntityStat().getId();
		double testflow=tempwatercontent+reservoirEntity.getOutflow()[i];
		do {
			List<CanalEntity> canalEntities=entity.getEntityStat().getUpcanalEntitys();
			CanalEntity canalEntity=canalEntities.get(0);
			entity=canalEntity.getEntityStat().getDownstreammodel();
			if(testflow>reservoirEntity.getOutmaxflow()[i]) {
				double content=curve.getV1ByV0(reservoirEntity.getEntityStat().getLevelNormal())+tempwatercontent*10*24*3600;
				reservoirEntity.getWaterlevel()[i+1]=curve2.getV1ByV0(content);
				break;
			}else {

				if(entity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.PUMP)) {
					PumpEntity pumpEntity=(PumpEntity)entity;
					double lastflow=pumpEntity.getAvgflow()[i];
					pumpEntity.getAvgflow()[i]=lastflow+testflow*canalEntity.getEntityStat().getLosspara();
					testflow=testflow*canalEntity.getEntityStat().getLosspara();
				}else if(entity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.RESERVOIR)) {
					ReservoirEntity reservoirEntity2=(ReservoirEntity)entity;
					double lastflow=reservoirEntity2.getInflow()[i];
					reservoirEntity2.getInflow()[i]=lastflow+testflow*canalEntity.getEntityStat().getLosspara();
					testflow=testflow*canalEntity.getEntityStat().getLosspara();
				}else {
					GateEntity gateEntity=(GateEntity)entity;
					double lastflow=gateEntity.getAvgflow()[i];
					gateEntity.getAvgflow()[i]=lastflow+testflow*canalEntity.getEntityStat().getLosspara();
					testflow=testflow*canalEntity.getEntityStat().getLosspara();
				}
			}
			
		}while(entity.getEntityStat().getId()!=id);
		
		
	}
	
	
	
	
	
	
	public Project getProject() {
		return project;
	}


	public void setProject(Project project) {
		this.project = project;
	}

}
