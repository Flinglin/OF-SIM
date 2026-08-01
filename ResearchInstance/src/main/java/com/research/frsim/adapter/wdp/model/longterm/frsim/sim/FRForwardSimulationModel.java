package com.research.frsim.adapter.wdp.model.longterm.frsim.sim;

import java.util.List;

import com.research.frsim.adapter.wdp.bean.Project;
import com.research.frsim.adapter.wdp.bean.entity.canal.CanalEntity;
import com.research.frsim.adapter.wdp.bean.entity.entity.Entity;
import com.research.frsim.adapter.wdp.bean.entity.gate.GateEntity;
import com.research.frsim.adapter.wdp.bean.entity.pump.PumpEntity;
import com.research.frsim.adapter.wdp.bean.entity.reservoir.ReservoirEntity;
import com.research.frsim.adapter.wdp.enumerate.EntityTypeEnum;
import com.research.frsim.adapter.wdp.enumerate.SectTypeEnum;
import com.research.frsim.adapter.wdp.model.longterm.frsim.util.FRProjectModel;
import com.research.frsim.core.util.DoubleCurve;

public class FRForwardSimulationModel extends FRProjectModel {
	
	
	

	public FRForwardSimulationModel(Project project) {
		super(project);
		
	}

	public double Forwardsimulation() {
		
		double result=1;
		for(int i=0;i<project.getTimeUnits().size();i++) {
			
			for(int j=0;j<canalEntities.size();j++) {
				
				CanalEntity canalEntity=canalEntities.get(j);
				Entity upEntity=canalEntity.getEntityStat().getUpstreammodel();
				Entity downEntity=canalEntity.getEntityStat().getDownstreammodel();

				if(upEntity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.RESERVOIR)) {
					
					ReservoirEntity reservoirEntity=(ReservoirEntity)upEntity;

					DoubleCurve curve=reservoirEntity.getEntityStat().getWlevel_storageCurve();
					DoubleCurve curve2=reservoirEntity.getEntityStat().getStorage_WlevelCurve();
					double reservoirinflow=reservoirEntity.getInflow()[i];
					double regionreservoirinflow=reservoirEntity.getSectionInflow()[i];
					double nowstorage=curve.getV1ByV0(reservoirEntity.getWaterlevel()[i+1]);
					double laststorage=curve.getV1ByV0(reservoirEntity.getWaterlevel()[i]);
					double wastewater=reservoirEntity.getWasteWater()[i];
					double deltaV=nowstorage-laststorage;
					double tempresult;
					if(reservoirEntity.getEntityStat().getName().equals("h_reservoir")) {
						tempresult=HHreservoirFlow(reservoirinflow,wastewater,reservoirEntity,i);
						if(tempresult>reservoirEntity.getOutmaxflow()[i]) {
							double tempwastewater=tempresult-reservoirEntity.getOutmaxflow()[i];
							wastewater+=tempwastewater;
							reservoirEntity.getWasteWater()[i]=wastewater;
							tempresult=reservoirEntity.getOutmaxflow()[i];
						}
						reservoirEntity.getOutflow()[i]=tempresult;
					}else {
						tempresult=calreservoirFlow(reservoirinflow, regionreservoirinflow, deltaV);

						ForwardWaterLevelControl(reservoirEntity, tempresult, i, reservoirinflow, regionreservoirinflow, reservoirEntity.getWaterlevel()[i], curve, curve2);
					}

					if(downEntity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.PUMP)) {
						
						PumpEntity pumpEntity2=(PumpEntity)downEntity;
						double inflow=reservoirEntity.getOutflow()[i];
						double regioninflow=canalEntity.getInflow()[i];
						double sumintakeflow=0;
						double losspara=canalEntity.getEntityStat().getLosspara();  
						for(int k=0;k<canalEntity.getEntityStat().getIntakeEntitys().size();k++) {
							sumintakeflow+=canalEntity.getEntityStat().getIntakeEntitys().get(k).getIntakeflow()[i];
						}
						double calresult=calwaterbalance(inflow, regioninflow, sumintakeflow, losspara);
						pumpEntity2.Forwardflowcontrol(inflow, pumpEntity2, regioninflow, i, calresult, canalEntity);
						

					}else {
						GateEntity pumpEntity2=(GateEntity)downEntity;
						double realcalresult=0;
						if(downEntity.getEntityStat().getSectTypeEnum().equals(SectTypeEnum.Converge)) {
							List<CanalEntity> canalEntities=downEntity.getEntityStat().getDowncanalEntitys();
							for(int s=0;s<canalEntities.size();s++) {
								CanalEntity canalEntity2=canalEntities.get(s);
								if(canalEntity2.getEntityStat().getUpstreammodel().getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.RESERVOIR)) {
									double inflow=reservoirEntity.getOutflow()[i];
									double regioninflow=canalEntity2.getInflow()[i];
									double sumintakeflow=0;
									double losspara=canalEntity2.getEntityStat().getLosspara();
									for(int k=0;k<canalEntity2.getEntityStat().getIntakeEntitys().size();k++) {
										sumintakeflow+=canalEntity2.getEntityStat().getIntakeEntitys().get(k).getIntakeflow()[i];
									}
									double calresult=calwaterbalance(inflow, regioninflow, sumintakeflow, losspara);
									realcalresult+=calresult;
								}else {
									GateEntity entity2=(GateEntity)canalEntity2.getEntityStat().getUpstreammodel();
									double inflow=entity2.getAvgflow()[i];
									double regioninflow=canalEntity.getInflow()[i];
									double sumintakeflow=0;
									double losspara=canalEntity2.getEntityStat().getLosspara();
									for(int k=0;k<canalEntity2.getEntityStat().getIntakeEntitys().size();k++) {
										sumintakeflow+=canalEntity2.getEntityStat().getIntakeEntitys().get(k).getIntakeflow()[i];
									}
									double calresult=calwaterbalance(inflow, regioninflow, sumintakeflow, losspara);
									realcalresult+=calresult;
								}
							}
							
							pumpEntity2.getAvgflow()[i]=realcalresult;
							
							

						}else {
							double inflow=reservoirEntity.getOutflow()[i];
							double regioninflow=canalEntity.getInflow()[i];
							double sumintakeflow=0;
							double losspara=canalEntity.getEntityStat().getLosspara();
							for(int k=0;k<canalEntity.getEntityStat().getIntakeEntitys().size();k++) {
								sumintakeflow+=canalEntity.getEntityStat().getIntakeEntitys().get(k).getIntakeflow()[i];
							}
							double calresult=calwaterbalance(inflow, regioninflow, sumintakeflow, losspara);

							pumpEntity2.Forwardflowcontrol(inflow, pumpEntity2, regioninflow, i, calresult, canalEntity);
							
						}
						
					}
				
					
					
				}else if(downEntity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.RESERVOIR)) {
					
					if(upEntity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.PUMP)) {
						
						PumpEntity entity=(PumpEntity)upEntity;
						ReservoirEntity pumpEntity2=(ReservoirEntity)downEntity;
						double inflow=entity.getAvgflow()[i];
						double regioninflow=canalEntity.getInflow()[i];
						double sumintakeflow=0;
						double losspara=canalEntity.getEntityStat().getLosspara();
						for(int s=0;s<canalEntity.getEntityStat().getIntakeEntitys().size();s++) {
							sumintakeflow+=canalEntity.getEntityStat().getIntakeEntitys().get(s).getIntakeflow()[i];
						}
						double calresult=calwaterbalance(inflow, regioninflow, sumintakeflow, losspara);

						pumpEntity2.Forwardflowcontrol(inflow, pumpEntity2, regioninflow, i, calresult, canalEntity);
						

					}else {
						GateEntity entity=(GateEntity)upEntity;
						ReservoirEntity pumpEntity2=(ReservoirEntity)downEntity;
						double inflow=entity.getAvgflow()[i];
						double regioninflow=canalEntity.getInflow()[i];
						double sumintakeflow=0;
						double losspara=canalEntity.getEntityStat().getLosspara();
						for(int s=0;s<canalEntity.getEntityStat().getIntakeEntitys().size();s++) {
							sumintakeflow+=canalEntity.getEntityStat().getIntakeEntitys().get(s).getIntakeflow()[i];
						}
						double calresult=calwaterbalance(inflow, regioninflow, sumintakeflow, losspara);

						pumpEntity2.Forwardflowcontrol(inflow, pumpEntity2, regioninflow, i, calresult, canalEntity);
						
					}
						
					
				}else {
					
					
					if(upEntity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.PUMP)&&downEntity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.PUMP)) {
						
						PumpEntity pumpEntity=(PumpEntity)upEntity;
						PumpEntity pumpEntity2=(PumpEntity)downEntity;			
						double inflow=pumpEntity.getAvgflow()[i];
						double regioninflow=canalEntity.getInflow()[i];
						double sumintakeflow=0;
						for(int k=0;k<canalEntity.getEntityStat().getIntakeEntitys().size();k++) {
							sumintakeflow+=canalEntity.getEntityStat().getIntakeEntitys().get(k).getIntakeflow()[i];
						}
						double losspara=canalEntity.getEntityStat().getLosspara();

						double calresult=calwaterbalance(inflow, regioninflow, sumintakeflow, losspara);
						
						pumpEntity2.Forwardflowcontrol(inflow, pumpEntity2, regioninflow, i, calresult, canalEntity);
						
	
						
					}else if(upEntity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.PUMP)&&downEntity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.GATE)) {
						
						PumpEntity pumpEntity=(PumpEntity)upEntity;
						GateEntity pumpEntity2=(GateEntity)downEntity;
						double inflow=pumpEntity.getAvgflow()[i];
						double regioninflow=canalEntity.getInflow()[i];
						double sumintakeflow=0;
						for(int k=0;k<canalEntity.getEntityStat().getIntakeEntitys().size();k++) {
							sumintakeflow+=canalEntity.getEntityStat().getIntakeEntitys().get(k).getIntakeflow()[i];
						}
						double losspara=canalEntity.getEntityStat().getLosspara();

						double calresult=calwaterbalance(inflow, regioninflow, sumintakeflow, losspara);

						pumpEntity2.Forwardflowcontrol(inflow, pumpEntity2, regioninflow, i, calresult, canalEntity);
						
						
	
					}else if(upEntity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.GATE)&&downEntity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.PUMP)) {
						
						GateEntity pumpEntity=(GateEntity)upEntity;
						PumpEntity pumpEntity2=(PumpEntity)downEntity;
						double inflow=pumpEntity.getAvgflow()[i];
						double regioninflow=canalEntity.getInflow()[i];
						double sumintakeflow=0;
						for(int k=0;k<canalEntity.getEntityStat().getIntakeEntitys().size();k++) {
							sumintakeflow+=canalEntity.getEntityStat().getIntakeEntitys().get(k).getIntakeflow()[i];
						}
						double losspara=canalEntity.getEntityStat().getLosspara();

						double calresult=calwaterbalance(inflow, regioninflow, sumintakeflow, losspara);

						pumpEntity2.Forwardflowcontrol(inflow, pumpEntity2, regioninflow, i, calresult, canalEntity);
						
	
						
					}else {
						
						GateEntity pumpEntity=(GateEntity)upEntity;
						GateEntity pumpEntity2=(GateEntity)downEntity;
						double inflow=pumpEntity.getAvgflow()[i];
						double regioninflow=canalEntity.getInflow()[i];
						double sumintakeflow=0;
						for(int k=0;k<canalEntity.getEntityStat().getIntakeEntitys().size();k++) {
							sumintakeflow+=canalEntity.getEntityStat().getIntakeEntitys().get(k).getIntakeflow()[i];
						}
						double losspara=canalEntity.getEntityStat().getLosspara();

						double calresult=calwaterbalance(inflow, regioninflow, sumintakeflow, losspara);

						pumpEntity2.Forwardflowcontrol(inflow, pumpEntity2, regioninflow, i, calresult, canalEntity);
						
					}
					
				}		
				
			}
			
		}
		return result;
		
	}

	public double calwaterbalance(double inflow,double regioninflow,double sumintakeflow,double losspara) {
		
		double result;
		if(inflow<0) {
			result=inflow/losspara-sumintakeflow+regioninflow;;
		}else {
			result=(inflow-sumintakeflow+regioninflow)*losspara;;
		}
		return result;
	}
	

	public double calreservoirFlow(double inflow,double regioninflow,double deltaV) {
		
		double result;
		
		if(deltaV>0) {
			double Inavgflow=(float)deltaV/(10*24*3600);
			result=inflow+regioninflow-Inavgflow;
		}else if(deltaV==0) {
			result=inflow+regioninflow;
		}else {
			double inavgflow=(float)deltaV/(10*24*3600);
			result=inflow+regioninflow-inavgflow;
		}
		return result;
	}

	public double HHreservoirFlow(double inflow,double wasterwater,ReservoirEntity reservoirEntity,int t) {
		double result;
		if(wasterwater<=inflow) {
			result=inflow-wasterwater;
		}else if(wasterwater>inflow&&inflow>0) {
			result=0;
			reservoirEntity.getWasteWater()[t]=inflow;
		}else {
			result=inflow-wasterwater;
		}
		return result;
	}

	public double reversecalreservoir(double outputflow,double inflow,double regioninflow,double lastlevel,DoubleCurve l_ccurve,DoubleCurve c_lcurve) {
		
		double lastcontent=l_ccurve.getV1ByV0(lastlevel);
		double nowcontent=(inflow+regioninflow-outputflow)*10*24*3600+lastcontent;
		double nowlevel=c_lcurve.getV1ByV0(nowcontent);
		return nowlevel;
	}

	public void ForwardWaterLevelControl(ReservoirEntity reservoirEntity,double resultflow,int t,double inflow,double regioninflow,double lastlevel,DoubleCurve curve,DoubleCurve curve2) {
		if(resultflow>reservoirEntity.getOutmaxflow()[t]) {

			resultflow=reservoirEntity.getOutmaxflow()[t];
			double level=reversecalreservoir(resultflow, inflow, regioninflow, lastlevel, curve, curve2);
			if(level>reservoirEntity.getEntityStat().getLevelNormal()) {
				level=reservoirEntity.getEntityStat().getLevelNormal();
				double deltav=curve.getV1ByV0(level)-curve.getV1ByV0(lastlevel);
				resultflow=calreservoirFlow(inflow, regioninflow, deltav);
				if(resultflow>reservoirEntity.getOutmaxflow()[t]) {
					level=reversecalreservoir(reservoirEntity.getOutmaxflow()[t], inflow, regioninflow, lastlevel, curve, curve2);
					reservoirEntity.getOutflow()[t]=reservoirEntity.getOutmaxflow()[t];
					reservoirEntity.getWaterlevel()[t+1]=level;
					reservoirEntity.getReservoirstorage()[t+1]=curve.getV1ByV0(level);
				}else {
					reservoirEntity.getOutflow()[t]=resultflow;
					reservoirEntity.getWaterlevel()[t+1]=level;
					reservoirEntity.getReservoirstorage()[t+1]=curve.getV1ByV0(level);
				}
			}else {
				reservoirEntity.getWaterlevel()[t+1]=level;
				reservoirEntity.getOutflow()[t] = reservoirEntity.getOutmaxflow()[t];
				reservoirEntity.getReservoirstorage()[t+1]=curve.getV1ByV0(level);
			}
			
		}else if(resultflow<reservoirEntity.getOutminflow()[t]) {

			resultflow=reservoirEntity.getOutminflow()[t];
			double level=reversecalreservoir(resultflow, inflow, regioninflow, lastlevel, curve, curve2);

			if(level<reservoirEntity.getEntityStat().getLevelDead()) {
				level=reservoirEntity.getEntityStat().getLevelDead();
				double deltav=curve.getV1ByV0(level)-curve.getV1ByV0(lastlevel);
				resultflow=calreservoirFlow(inflow, regioninflow, deltav);
				if(resultflow<reservoirEntity.getOutminflow()[t]) {
					reservoirEntity.getOutflow()[t]=reservoirEntity.getOutminflow()[t];
					level=reversecalreservoir(reservoirEntity.getOutminflow()[t], inflow, regioninflow, lastlevel, curve, curve2);
					reservoirEntity.getWaterlevel()[t+1]=level;
					reservoirEntity.getReservoirstorage()[t+1]=curve.getV1ByV0(level);
				}else {
					reservoirEntity.getOutflow()[t]=resultflow;
					reservoirEntity.getWaterlevel()[t+1]=level;
					reservoirEntity.getReservoirstorage()[t+1]=curve.getV1ByV0(level);
				}
				
			}else {
				reservoirEntity.getWaterlevel()[t+1]=level;
				reservoirEntity.getOutflow()[t] = resultflow;
				reservoirEntity.getReservoirstorage()[t+1]=curve.getV1ByV0(level);
			}
			
		}else {
			reservoirEntity.getOutflow()[t]=resultflow;
		}
	}
	
	public Project getProject() {
		return project;
	}


	public void setProject(Project project) {
		this.project = project;
	}
	
	
	

}
