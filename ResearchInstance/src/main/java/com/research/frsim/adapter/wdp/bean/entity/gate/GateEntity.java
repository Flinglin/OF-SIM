package com.research.frsim.adapter.wdp.bean.entity.gate;

import java.util.Arrays;
import java.util.List;

import com.research.frsim.adapter.wdp.bean.Project;
import com.research.frsim.adapter.wdp.bean.entity.canal.CanalEntity;
import com.research.frsim.adapter.wdp.bean.entity.entity.Entity;
import com.research.frsim.adapter.wdp.bean.entity.intake.IntakeEntity;
import com.research.frsim.adapter.wdp.bean.entity.pump.PumpEntity;
import com.research.frsim.adapter.wdp.enumerate.EntityTypeEnum;
import com.research.frsim.adapter.wdp.enumerate.TimeScaleEnum;

public class GateEntity extends Entity{

	public static final double NULLE = -999.0;
	

	private double[] avgflow;

	private double[] uplevel;
	

	private double[] downlevel;

	private double[][] fitopenness;

	private double[] openness;
	

	private double[] maxflow;

	private double[] minflow;
	
	public GateEntity(GateEntityStat entityStat,Project project) {

		int timelen = project.getTimeUnits().size();
		if (project.getTimeScaleEnum() == TimeScaleEnum.SHORTERM) {
			maxflow = new double[timelen + 1];
			minflow = new double[timelen + 1];
			avgflow = new double[timelen + 1];
		} else {
			maxflow = new double[timelen];
			minflow = new double[timelen];
			avgflow = new double[timelen];
		}
		uplevel = new double[timelen + 1];
		downlevel = new double[timelen + 1];
		openness = new double[timelen + 1];
		fitopenness = new double[timelen + 1][3];
		for (int i = 0; i < fitopenness.length; i++) {
			for (int j = 0; j < fitopenness[i].length; j++) {
				fitopenness[i][j] = NULLE;
			}
		}
		this.entityStat = entityStat;
	}
	
	@Override
	public void clean() {
		Arrays.fill(avgflow, 0);
		Arrays.fill(downlevel, 0);
		Arrays.fill(uplevel, 0);
		Arrays.fill(openness, 0);
		Arrays.fill(avgflow, 0);
		for (int i = 0; i < fitopenness.length; i++) {
			Arrays.fill(fitopenness[i], NULLE);
		}
	}

	public void Flowcontrol(CanalEntity canalEntity,GateEntity gateEntity,double outputflow,double sumintakeflow,double inflow,double losspara,int t) {
		
		double resultInflow=calinflow(outputflow, sumintakeflow, inflow, losspara);
		double rate;
		if(resultInflow>gateEntity.getMaxflow()[t]) {
			rate=(resultInflow-gateEntity.getMaxflow()[t])/sumintakeflow;
		}else if(resultInflow<gateEntity.getMinflow()[t]) {
			rate=(gateEntity.getMinflow()[t]-resultInflow)/sumintakeflow;
		}else {
			rate=-Double.MAX_VALUE;
		}

		if(resultInflow>gateEntity.getMaxflow()[t]&&rate<=1) {
			double tempvolume=resultInflow-gateEntity.getMaxflow()[t];
			constraintprocess(canalEntity, tempvolume, t, 1);
			gateEntity.getAvgflow()[t]=gateEntity.getMaxflow()[t];
		}else if(resultInflow>gateEntity.getMaxflow()[t]&&rate>1) {

				resultInflow=calinflow(outputflow, 0, inflow, losspara);
				gateEntity.getAvgflow()[t]=resultInflow;
				AssignmentDIV(canalEntity, t);

		}else if(resultInflow<gateEntity.getMinflow()[t]&&rate<0.3) {
			double tempvolume=gateEntity.getMinflow()[t]-resultInflow;
			constraintprocess(canalEntity, tempvolume, t, 0);
			gateEntity.getAvgflow()[t]=gateEntity.getMinflow()[t];
		}else if(resultInflow<gateEntity.getMinflow()[t]&&rate>=0.3&&outputflow>0) {
			gateEntity.getAvgflow()[t]=resultInflow;
		}else {

			gateEntity.getAvgflow()[t]=resultInflow;
		}
		
	}

	private int ClWDivFlowRestriction(GateEntity pumpEntity,double tempvolume,int t,CanalEntity canalEntity) {
		

		double allintakeability=0;
		Entity entity=pumpEntity;
		CanalEntity tempCanalEntity=canalEntity;
		do {
			tempCanalEntity=entity.getEntityStat().getUpcanalEntitys().get(0);
			double sumintakeablility=TotWaterVolume(tempCanalEntity, t);
			allintakeability+=sumintakeablility;
			entity=tempCanalEntity.getEntityStat().getDownstreammodel();
		}while(!entity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.RESERVOIR));

		double type=(float)tempvolume/allintakeability;
		if(type<0.3) {
			ReCalOfForward(tempvolume, allintakeability, pumpEntity, canalEntity, t);
			return 1;
		}else {
			return 0;
		}
		
	}

	private void ReCalOfForward(double tempvolume,double allintakeability,GateEntity pumpEntity,CanalEntity canalEntity,int t) {
		

		pumpEntity.getAvgflow()[t]=pumpEntity.getMaxflow()[t]+tempvolume;

		Entity entity=pumpEntity;
		CanalEntity tempCanalEntity=canalEntity;
		double allvolume=tempvolume;
		do {
			tempCanalEntity=entity.getEntityStat().getUpcanalEntitys().get(0);
			double sumintakeablility=TotWaterVolume(tempCanalEntity, t);
			double volume=tempvolume*(sumintakeablility/allintakeability);

			constraintprocess(tempCanalEntity, volume, t, 1);

			if(entity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.PUMP)) {
				PumpEntity pumpEntity2=(PumpEntity)entity;
				pumpEntity2.getAvgflow()[t]=pumpEntity2.getAvgflow()[t]-allvolume;
			}else {
				GateEntity gateEntity=(GateEntity)entity;
				gateEntity.getAvgflow()[t]=gateEntity.getAvgflow()[t]-allvolume;
			}
			allvolume-=volume;
			entity=tempCanalEntity.getEntityStat().getDownstreammodel();
		}while(!entity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.RESERVOIR));
		
		
	}

	private double TotWaterVolume(CanalEntity canalEntity, int t) {
		double sumintakeflow=0;
		List<IntakeEntity> intakeEntities=canalEntity.getEntityStat().getIntakeEntitys();
		for(int i=0;i<intakeEntities.size();i++) {
			sumintakeflow+=intakeEntities.get(i).getIntakeflow()[t];
		}
		return sumintakeflow;
	}
	private void AssignmentDIV(CanalEntity canalEntity,int t) {
		List<IntakeEntity> intakeEntities=canalEntity.getEntityStat().getIntakeEntitys();
		for(int i=0;i<intakeEntities.size();i++) {
			intakeEntities.get(i).getIntakeflow()[t]=0;
		}
	}

	public void Forwardflowcontrol(double inflow,GateEntity pumpEntity2,double regioninflow,int i,double calresult,CanalEntity canalEntity) {

		double rate;
		List<IntakeEntity> intakeEntities=canalEntity.getEntityStat().getIntakeEntitys();
		double intakeablility=0;
		for(int j=0;j<intakeEntities.size();j++) {
			intakeablility+=intakeEntities.get(j).getIntakeflow()[i];
		}
		if(calresult>pumpEntity2.getMaxflow()[i]) {
			rate=(calresult-pumpEntity2.getMaxflow()[i])/intakeablility;
		}else if(calresult<pumpEntity2.getMinflow()[i] && inflow>0) {
			rate=(pumpEntity2.getMinflow()[i]-calresult)/intakeablility;
		}else {
			rate=-Double.MAX_VALUE;
		}
		
		if(calresult>pumpEntity2.getMaxflow()[i]&&rate<0.3) {
			double reducerate=calresult-pumpEntity2.getMaxflow()[i];
			constraintHandling(canalEntity, reducerate/canalEntity.getEntityStat().getLosspara(), i, intakeablility,0);
			pumpEntity2.getAvgflow()[i]=pumpEntity2.getMaxflow()[i];
		}else if(calresult<pumpEntity2.getMinflow()[i]&&rate<=1&&rate>0 && inflow>0) {
			double reducerate=pumpEntity2.getMinflow()[i]-calresult;
			constraintHandling(canalEntity, reducerate/canalEntity.getEntityStat().getLosspara(), i, intakeablility, 1);
			pumpEntity2.getAvgflow()[i]=pumpEntity2.getMinflow()[i];
		}else if(calresult<pumpEntity2.getMinflow()[i] && inflow==0) {
			AssignmentDIV(canalEntity, i);
			pumpEntity2.getAvgflow()[i]=pumpEntity2.getMinflow()[i];
		}else {

			pumpEntity2.getAvgflow()[i]=calresult;
		}
			
	}

	private void constraintHandling(CanalEntity canalEntity,double reducerate,int t,double intakeability,int type) {
		
		List<IntakeEntity> intakeEntities=canalEntity.getEntityStat().getIntakeEntitys();
		for(int i=0;i<intakeEntities.size();i++) {
			IntakeEntity intakeEntity=intakeEntities.get(i);
			double rate=intakeEntity.getIntakeflow()[t]/intakeability;
			if(type==0) {
				intakeEntity.getIntakeflow()[t]=intakeEntities.get(i).getIntakeflow()[t]+reducerate*rate;
			}else {
				intakeEntity.getIntakeflow()[t]=intakeEntities.get(i).getIntakeflow()[t]-reducerate*rate;
				if(intakeEntity.getIntakeflow()[t]<0) {
					intakeEntity.getIntakeflow()[t]=0;
				}
			}
		}
		
	}

	private double calinflow(double outflow,double sumintakeflow,double suminflow,double losspara) {
		
		double result;
		if(outflow<0) {
			result=(outflow+sumintakeflow-suminflow)*losspara;
		}else {
			result=outflow/losspara+sumintakeflow-suminflow;
		}
		return result;
		
	}

	private void constraintprocess(CanalEntity canalEntity,double tempvolume,int t,int type) {
		
		List<IntakeEntity> intakeEntities=canalEntity.getEntityStat().getIntakeEntitys();
		double totalvolume=0;
		for(int i=0;i<intakeEntities.size();i++) {
			totalvolume+=intakeEntities.get(i).getIntakeflow()[t];
		}
		if(type==0) {
			for(int i=0;i<intakeEntities.size();i++) {
				IntakeEntity entity=intakeEntities.get(i);
				entity.getIntakeflow()[t]=entity.getIntakeflow()[t]+tempvolume*(entity.getIntakeflow()[t]/totalvolume);
			}
		}else {
			for(int i=0;i<intakeEntities.size();i++) {
				IntakeEntity entity=intakeEntities.get(i);
				entity.getIntakeflow()[t]=entity.getIntakeflow()[t]-tempvolume*(entity.getIntakeflow()[t]/totalvolume);
				if(entity.getIntakeflow()[t]<0) {
					entity.getIntakeflow()[t]=0;
				}
			}
		}
		
	}

	public double calwaterbalance(double inflow,double regioninflow,double sumintakeflow,double losspara) {
		
		double result=(float)(inflow-sumintakeflow+regioninflow)/losspara;
		
		return result;
	}

	public GateEntityStat getEntityStat() {
		return (GateEntityStat) entityStat;
	}

	public double[] getAvgflow() {
		return avgflow;
	}

	public void setAvgflow(double[] avgflow) {
		this.avgflow = avgflow;
	}

	public double[] getUplevel() {
		return uplevel;
	}

	public void setUplevel(double[] uplevel) {
		this.uplevel = uplevel;
	}

	public double[] getDownlevel() {
		return downlevel;
	}

	public void setDownlevel(double[] downlevel) {
		this.downlevel = downlevel;
	}

	public double[][] getFitopenness() {
		return fitopenness;
	}

	public void setFitopenness(double[][] fitopenness) {
		this.fitopenness = fitopenness;
	}

	public double[] getOpenness() {
		return openness;
	}

	public void setOpenness(double[] openness) {
		this.openness = openness;
	}

	public double[] getMaxflow() {
		return maxflow;
	}

	public void setMaxflow(double[] maxflow) {
		this.maxflow = maxflow;
	}

	public double[] getMinflow() {
		return minflow;
	}

	public void setMinflow(double[] minflow) {
		this.minflow = minflow;
	}

}
