package com.research.frsim.adapter.wdp.bean.entity.pump;

import java.util.Arrays;
import java.util.List;

import com.research.frsim.adapter.wdp.bean.Project;
import com.research.frsim.adapter.wdp.bean.entity.canal.CanalEntity;
import com.research.frsim.adapter.wdp.bean.entity.entity.Entity;
import com.research.frsim.adapter.wdp.bean.entity.gate.GateEntity;
import com.research.frsim.adapter.wdp.bean.entity.intake.IntakeEntity;
import com.research.frsim.adapter.wdp.enumerate.EntityTypeEnum;
import com.research.frsim.adapter.wdp.enumerate.TimeScaleEnum;

public class PumpEntity extends Entity{

	private double[] maxflow;

	private double[] minflow;

	private double[] maxvolume;

	private double[] minvolume;

	private double[] avgflow;

	private double[] avgvolume;

	private double[] WlevelUp;

	private double[] Wleveldown;

	private double[] pumpH;

	private int[] pumpangleRS;

	private int[] pumpamonuts;

	private double[] efficiency;

	
	
	public PumpEntity(PumpEntityStat entityStat, Project project) {

		this.entityStat = entityStat;
		int timelen = project.getTimeUnits().size();
		if (project.getTimeScaleEnum() == TimeScaleEnum.SHORTERM) {
			maxflow = new double[timelen + 1];
			minflow = new double[timelen + 1];
			avgflow = new double[timelen + 1];
			WlevelUp = new double[timelen + 1];
			Wleveldown = new double[timelen + 1];
			pumpH=new double[timelen+1];
			pumpangleRS=new int[timelen+1];
			pumpamonuts=new int[timelen+1];
		} else {
			maxflow = new double[timelen];
			minflow = new double[timelen];
			avgflow = new double[timelen];
			WlevelUp = new double[timelen];
			Wleveldown = new double[timelen];
			pumpH=new double[timelen+1];
			pumpangleRS=new int[timelen+1];
			pumpamonuts=new int[timelen+1];
		}
	}
	
	
	@Override
	public void clean() {

		Arrays.fill(avgflow, 0);
		Arrays.fill(WlevelUp, 0);
		Arrays.fill(Wleveldown, 0);
		Arrays.fill(pumpamonuts, 0);
		Arrays.fill(pumpangleRS, 0);
		Arrays.fill(pumpH, 0);
		
	}


	public void Flowcontrol(PumpEntity pumpEntity,double outputflow,double sumintakeflow,double inflow,double losspara,int t,CanalEntity canalEntity) {
		
		double resultInflow=calinflow(outputflow, sumintakeflow, inflow, losspara);
		double rate;
		if(resultInflow>pumpEntity.getMaxflow()[t]) {
			rate=(resultInflow-pumpEntity.getMaxflow()[t])/sumintakeflow;
		}else if(resultInflow<pumpEntity.getMinflow()[t]) {
			rate=(pumpEntity.getMinflow()[t]-resultInflow)/sumintakeflow;
		}else {
			rate=-Double.MAX_VALUE;
		}
		if(resultInflow>pumpEntity.getMaxflow()[t]&&rate<=1) {
			double tempvolume=resultInflow-pumpEntity.getMaxflow()[t];
			constraintprocess(canalEntity, tempvolume, t, 1);
			pumpEntity.getAvgflow()[t]=pumpEntity.getMaxflow()[t];
		}else if(resultInflow>pumpEntity.getMaxflow()[t]&&rate>1) {

				resultInflow=calinflow(outputflow, 0, inflow, losspara);
				pumpEntity.getAvgflow()[t]=resultInflow;
				AssignmentDIV(canalEntity, t);

		}else if(resultInflow<pumpEntity.getMinflow()[t]&&rate<0.3) {
			double tempvolume=pumpEntity.getMinflow()[t]-resultInflow;
			constraintprocess(canalEntity, tempvolume, t, 0);
			pumpEntity.getAvgflow()[t]=pumpEntity.getMinflow()[t];
		}else if(resultInflow<pumpEntity.getMinflow()[t]&&rate>=0.3 && outputflow>0) {
			pumpEntity.getAvgflow()[t]=resultInflow;
		}else {
			pumpEntity.getAvgflow()[t]=resultInflow;
		}
	}

	private int ClWDivFlowRestriction(PumpEntity pumpEntity,double tempvolume,int t,CanalEntity canalEntity) {

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

	private void ReCalOfForward(double tempvolume,double allintakeability,PumpEntity pumpEntity,CanalEntity canalEntity,int t) {

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

	public void Forwardflowcontrol(double inflow,PumpEntity pumpEntity2,double regioninflow,int i,double calresult,CanalEntity canalEntity) {

		double rate;
		List<IntakeEntity> intakeEntities=canalEntity.getEntityStat().getIntakeEntitys();
		double intakeablility=0;
		for(int j=0;j<intakeEntities.size();j++) {
			intakeablility+=intakeEntities.get(j).getIntakeflow()[i];
		}
		if(calresult>pumpEntity2.getMaxflow()[i]) {
			rate=(calresult-pumpEntity2.getMaxflow()[i])/intakeablility/canalEntity.getEntityStat().getLosspara();
		}else if(calresult<pumpEntity2.getMinflow()[i] && inflow>0) {
			rate=(pumpEntity2.getMinflow()[i]-calresult)/intakeablility/canalEntity.getEntityStat().getLosspara();
		}else {
			rate=-Double.MAX_VALUE;
		}
		
		if(calresult>pumpEntity2.getMaxflow()[i]&&rate<0.3) {
			double reducerate=calresult-pumpEntity2.getMaxflow()[i];
			constraintHandling(canalEntity, reducerate/canalEntity.getEntityStat().getLosspara(), i, intakeablility,0);
			pumpEntity2.getAvgflow()[i]=pumpEntity2.getMaxflow()[i];
		}else if(calresult<pumpEntity2.getMinflow()[i]&&rate<=1 && rate>0 && inflow>0) {
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

	public PumpEntityStat getEntityStat() {
		return (PumpEntityStat) entityStat;
	}
	
	public double[] getAvgflow() {
		return avgflow;
	}

	public void setAvgflow(double[] avgflow) {
		this.avgflow = avgflow;
	}

	public double[] getWlevelUp() {
		return WlevelUp;
	}

	public void setWlevelUp(double[] wlevelUp) {
		WlevelUp = wlevelUp;
	}

	public double[] getWleveldown() {
		return Wleveldown;
	}

	public void setWleveldown(double[] wleveldown) {
		Wleveldown = wleveldown;
	}


	public double[] getPumpH() {
		return pumpH;
	}


	public void setPumpH(double[] pumpH) {
		this.pumpH = pumpH;
	}


	public int[] getPumpangleRS() {
		return pumpangleRS;
	}


	public void setPumpangleRS(int[] pumpangleRS) {
		this.pumpangleRS = pumpangleRS;
	}


	public int[] getPumpamonuts() {
		return pumpamonuts;
	}


	public void setPumpamonuts(int[] pumpamonuts) {
		this.pumpamonuts = pumpamonuts;
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
	public double[] getAvgvolume() {
		return avgvolume;
	}
	public void setAvgvolume(double[] avgvolume) {
		this.avgvolume = avgvolume;
	}
	public double[] getEfficiency() {
		return efficiency;
	}
	public void setEfficiency(double[] efficiency) {
		this.efficiency = efficiency;
	}
	public double[] getMaxvolume() {
		return maxvolume;
	}
	public void setMaxvolume(double[] maxvolume) {
		this.maxvolume = maxvolume;
	}
	public double[] getMinvolume() {
		return minvolume;
	}
	public void setMinvolume(double[] minvolume) {
		this.minvolume = minvolume;
	}

}
