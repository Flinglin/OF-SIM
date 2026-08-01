package com.research.frsim.adapter.wdp.bean.entity.reservoir;

import java.util.Arrays;
import java.util.List;

import com.research.frsim.adapter.wdp.bean.Project;
import com.research.frsim.adapter.wdp.bean.entity.canal.CanalEntity;
import com.research.frsim.adapter.wdp.bean.entity.entity.Entity;
import com.research.frsim.adapter.wdp.bean.entity.gate.GateEntity;
import com.research.frsim.adapter.wdp.bean.entity.intake.IntakeEntity;
import com.research.frsim.adapter.wdp.bean.entity.pump.PumpEntity;
import com.research.frsim.adapter.wdp.enumerate.EntityTypeEnum;
import com.research.frsim.adapter.wdp.enumerate.FloodSimDirectEnum;
import com.research.frsim.core.util.DoubleCurve;

public class ReservoirEntity extends Entity{

	private double[] Inflow;

	private double[] Outflow;

	private double[] InflowV;

	private double[] OutflowV;
	

	private double[] SectionInflow;

	private double[] Fluctuatelimit;
	

	private double Startendlimit;
	

	private double[] WasteWater;
	

	private double[] Reservoirstorage;
	

	private double[] waterlevel;

	private double[] inmaxflow;
	

	private double[] inminflow;
	

	private double[] outmaxflow;
	

	private double[] outminflow;
	

	private double[] uplevel;

	private double[] downlevel;

	private double[] MaxLevel;

	private double[] MinLevel;
	
	
	public ReservoirEntity(ReservoirEntityStat entityStat, Project project) {
		this.entityStat = entityStat;
		int timelen = project.getTimeUnits().size();
		Inflow = new double[timelen+1];
		Outflow = new double[timelen+1];
		SectionInflow = new double[timelen+1];
		WasteWater = new double[timelen+1];
		Reservoirstorage = new double[timelen+1];
		waterlevel=new double[timelen+1];
		inminflow=new double[timelen+1];
		inmaxflow=new double[timelen+1];
		outminflow=new double[timelen+1];
		outmaxflow=new double[timelen+1];
		uplevel=new double[timelen+1];
		downlevel=new double[timelen+1];
		MinLevel=new double[timelen+1];
		MaxLevel=new double[timelen+1];
	}
	
	
	@Override
	public void clean() {
		Arrays.fill(Reservoirstorage, 0);
		Arrays.fill(WasteWater, 0);
		Arrays.fill(Inflow, 0);
		Arrays.fill(Outflow, 0);
		Arrays.fill(SectionInflow, 0);
		Arrays.fill(waterlevel, 0);
		Arrays.fill(uplevel, 0);
		Arrays.fill(downlevel, 0);
	}

	public void ReservoirLevelControl(ReservoirEntity reservoirEntity,double outputflow,double regioninflow,double nowlevel,double lastlevel,DoubleCurve l_ccurve,DoubleCurve c_lcurve,int t) {

		double tempresult=calreservoir(outputflow, regioninflow, nowlevel, lastlevel, l_ccurve);

		double tempwatercontent=reservoirLevelcontrol(tempresult, reservoirEntity, outputflow, regioninflow, lastlevel, l_ccurve, c_lcurve, t);

		if(tempwatercontent!=0) {
			if(reservoirEntity.getEntityStat().getDirectEnum().equals(FloodSimDirectEnum.REVERSE)) {
				if(tempwatercontent>reservoirEntity.getInmaxflow()[t]) {

					double content=l_ccurve.getV1ByV0(reservoirEntity.getWaterlevel()[t+1])+10*24*3600*(tempwatercontent-reservoirEntity.getInmaxflow()[t]);
					double level=c_lcurve.getV1ByV0(content);
					reservoirEntity.getWaterlevel()[t+1]=level;
					tempwatercontent=reservoirEntity.getInmaxflow()[t];
				}
				reservoirEntity.getWasteWater()[t]=tempwatercontent*10*24*3600;
				reservoirEntity.getInflow()[t]=-tempwatercontent;
			}else if(reservoirEntity.getEntityStat().getDirectEnum().equals(FloodSimDirectEnum.NORMAL)) {
				reservoirEntity.getInflow()[t]=reservoirEntity.getInminflow()[t];
				reservoirEntity.getWasteWater()[t]=tempwatercontent;
			}else {
				Forwardfloodsim(reservoirEntity, tempwatercontent, t, l_ccurve, c_lcurve);
			}
			
		}
	}
	public void CanalFlowControl(CanalEntity canalEntity,ReservoirEntity reservoirEntity,double outputflow,double sumintakeflow,double inflow,double losspara,int t) {
		
		double resultInflow=calinflow(outputflow, sumintakeflow, inflow, losspara);
		double rate;
		if(resultInflow>reservoirEntity.getOutmaxflow()[t]) {
			rate=(resultInflow-reservoirEntity.getOutmaxflow()[t])/sumintakeflow;
		}else if(resultInflow<reservoirEntity.getOutminflow()[t]) {
			rate=(reservoirEntity.getOutminflow()[t]-resultInflow)/sumintakeflow;
		}else {
			rate=-Double.MAX_VALUE;
		}

		if(resultInflow>reservoirEntity.getOutmaxflow()[t]) {

			reservoirEntity.getOutflow()[t]=reservoirEntity.getOutmaxflow()[t];
		}else if(resultInflow<reservoirEntity.getOutminflow()[t]&&rate<0.3) {
			double tempvolume=reservoirEntity.getOutminflow()[t]-resultInflow;
			constraintprocess(canalEntity, tempvolume, t, 0);
			reservoirEntity.getOutflow()[t]=reservoirEntity.getOutminflow()[t];
		}else if(resultInflow<reservoirEntity.getOutminflow()[t]&&rate>=0.3) {
			reservoirEntity.getOutflow()[t]=resultInflow;
		}else {

			reservoirEntity.getOutflow()[t]=resultInflow;
		}
	}
	public void Flowcontrol(CanalEntity canalEntity,ReservoirEntity reservoirEntity,double outputflow,double sumintakeflow,double inflow,double losspara,int t) {
		
		double resultInflow=calinflow(outputflow, sumintakeflow, inflow, losspara);
		double rate;
		if(resultInflow>reservoirEntity.getOutmaxflow()[t]) {
			rate=(resultInflow-reservoirEntity.getOutmaxflow()[t])/sumintakeflow;
		}else if(resultInflow<reservoirEntity.getOutminflow()[t]) {
			rate=(reservoirEntity.getOutminflow()[t]-resultInflow)/sumintakeflow;
		}else {
			rate=-Double.MAX_VALUE;
		}
		if(resultInflow>reservoirEntity.getOutmaxflow()[t]&&rate<=1) {
			double tempvolume=resultInflow-reservoirEntity.getOutmaxflow()[t];
			constraintprocess(canalEntity, tempvolume, t, 1);
			reservoirEntity.getOutflow()[t]=reservoirEntity.getOutmaxflow()[t];
		}else if(resultInflow>reservoirEntity.getOutmaxflow()[t]&&rate>1) {
				resultInflow=calinflow(outputflow, 0, inflow, losspara);
				reservoirEntity.getOutflow()[t]=resultInflow;
				AssignmentDIV(canalEntity, t);

		}else if(resultInflow<reservoirEntity.getOutminflow()[t]&&rate<0.3) {
			double tempvolume=reservoirEntity.getOutminflow()[t]-resultInflow;
			constraintprocess(canalEntity, tempvolume, t, 0);
			reservoirEntity.getOutflow()[t]=reservoirEntity.getOutminflow()[t];
		}else if(resultInflow<reservoirEntity.getOutminflow()[t]&&rate>=0.3) {
			reservoirEntity.getOutflow()[t]=resultInflow;
		}else {

			reservoirEntity.getOutflow()[t]=resultInflow;
		}
		
	}

	private void AssignmentDIV(CanalEntity canalEntity,int t) {
		List<IntakeEntity> intakeEntities=canalEntity.getEntityStat().getIntakeEntitys();
		for(int i=0;i<intakeEntities.size();i++) {
			intakeEntities.get(i).getIntakeflow()[t]=0;
		}
	}

	private double calinflow(double outflow,double sumintakeflow,double suminflow,double losspara) {
		
		double result=outflow/losspara+sumintakeflow-suminflow;
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

	private double calreservoir(double outputflow,double regioninflow,double nowwaterlevel,double lastwaterlevel,DoubleCurve curve) {
		double nowstorage=curve.getV1ByV0(nowwaterlevel);
		double laststorage=curve.getV1ByV0(lastwaterlevel);
		double deltaV=nowstorage-laststorage;
		double sumresult=deltaV/(10*24*3600)-regioninflow+outputflow;
		return sumresult;
		
	}

	public double calreservoir1(ReservoirEntity reservoirEntity,double deltaV,double regioninflow,double lastlevel,DoubleCurve curve1,DoubleCurve curve2,int i) {
		
		double result = 0;
		double para = reservoirEntity.getFluctuatelimit()[i];
		double laststorage=curve1.getV1ByV0(lastlevel);
		double nowstorage=laststorage+deltaV+regioninflow;
		double maxstorage=curve1.getV1ByV0(reservoirEntity.getEntityStat().getLevelNormal());
		double maxstoragenext=curve1.getV1ByV0(lastlevel+para);
		double minstorage=curve1.getV1ByV0(reservoirEntity.getEntityStat().getLevelDead());
		double minstoragenext=curve1.getV1ByV0(lastlevel-para);
		if(lastlevel+para >= reservoirEntity.getEntityStat().getLevelNormal() && lastlevel-para <= reservoirEntity.getEntityStat().getLevelDead()) {
			if(nowstorage>maxstorage) {
				reservoirEntity.getWaterlevel()[i+1]=reservoirEntity.getEntityStat().getLevelNormal();
				result=nowstorage-maxstorage;
			}else if(nowstorage<minstorage){
				reservoirEntity.getWaterlevel()[i+1]=reservoirEntity.getEntityStat().getLevelDead();
				result=nowstorage-minstorage;
			} else{
				reservoirEntity.getWaterlevel()[i+1]=curve2.getV1ByV0(nowstorage);
				result=0;
			}
		}else if(lastlevel+para < reservoirEntity.getEntityStat().getLevelNormal() && lastlevel-para <= reservoirEntity.getEntityStat().getLevelDead()){
			if(nowstorage>maxstoragenext) {
				reservoirEntity.getWaterlevel()[i+1]=lastlevel+para;
				result=nowstorage-maxstoragenext;
			}else if(nowstorage<minstorage){
				reservoirEntity.getWaterlevel()[i+1]=reservoirEntity.getEntityStat().getLevelDead();
				result=nowstorage-minstorage;
			}else{
				reservoirEntity.getWaterlevel()[i+1]=curve2.getV1ByV0(nowstorage);
				result=0;
			}
		}else if(lastlevel+para >= reservoirEntity.getEntityStat().getLevelNormal() && lastlevel-para > reservoirEntity.getEntityStat().getLevelDead()){
			if(nowstorage>maxstorage) {
				reservoirEntity.getWaterlevel()[i+1]=reservoirEntity.getEntityStat().getLevelNormal();
				result=nowstorage-maxstorage;
			}else if(nowstorage<minstoragenext){
				reservoirEntity.getWaterlevel()[i+1]=lastlevel-para;
				result=nowstorage-minstoragenext;
			}else{
				reservoirEntity.getWaterlevel()[i+1]=curve2.getV1ByV0(nowstorage);
				result=0;
			}
		}else if(lastlevel+para < reservoirEntity.getEntityStat().getLevelNormal() && lastlevel-para >= reservoirEntity.getEntityStat().getLevelDead()){
			if(nowstorage>maxstoragenext) {
				reservoirEntity.getWaterlevel()[i+1]=lastlevel+para;
				result=nowstorage-maxstoragenext;
			}else if(nowstorage<minstoragenext){
				reservoirEntity.getWaterlevel()[i+1]=lastlevel-para;
				result=nowstorage-minstoragenext;
			}else{
				reservoirEntity.getWaterlevel()[i+1]=curve2.getV1ByV0(nowstorage);
				result=0;
			}
		}
		return result;
	}

	public double calreservoir2(double nowwaterlevel,double lastlevel,double inflowV,double regioninflow,double sumintakevolume,DoubleCurve curve1,int i) {
		
		double nowstorage=curve1.getV1ByV0(nowwaterlevel);
		double laststorage=curve1.getV1ByV0(lastlevel);
		double deltaV=nowstorage-laststorage;
		double result=inflowV+regioninflow-sumintakevolume-deltaV;
		return result;
	}

	private double reservoirLevelcontrol(double tempresult,ReservoirEntity reservoirEntity,double outputflow,double regioninflow,double lastlevel,DoubleCurve curve,DoubleCurve curve2,int i) {
		if(tempresult>reservoirEntity.getInmaxflow()[i]) {

			if(tempresult>0) {
				tempresult=reservoirEntity.getInmaxflow()[i];
			}
			double waterlevel=forwardlevelcontrol(tempresult, outputflow, regioninflow, lastlevel, curve, curve2);
			if(waterlevel<reservoirEntity.getMinLevel()[i]) {
				double tempwaterlevel=reservoirEntity.getMinLevel()[i];

				double atempresult=calreservoir(outputflow, regioninflow, tempwaterlevel, lastlevel, curve);
				if(atempresult>reservoirEntity.getInmaxflow()[i]) {
					reservoirEntity.getWaterlevel()[i+1]=tempwaterlevel;
					reservoirEntity.getInflow()[i]=atempresult;
				}else {
					reservoirEntity.getInflow()[i]=atempresult;
					reservoirEntity.getWaterlevel()[i+1]=tempwaterlevel;
				}
				
			}else {
				reservoirEntity.getInflow()[i]=tempresult;
				reservoirEntity.getWaterlevel()[i+1]=waterlevel;
			}
		}else if(tempresult<reservoirEntity.getInminflow()[i]) {
			double waterlevel;
			if(reservoirEntity.getEntityStat().getDirectEnum().equals(FloodSimDirectEnum.REVERSE)) {
				waterlevel=reservoirEntity.getWaterlevel()[i+1];
				if(Math.abs(tempresult)>reservoirEntity.getInmaxflow()[i]) {
					double tempQ=Math.abs(tempresult)-reservoirEntity.getInmaxflow()[i];
					double level=curve.getV1ByV0(reservoirEntity.getWaterlevel()[i+1])+10*24*3600*tempQ;
					double realevel=curve2.getV1ByV0(level);
					reservoirEntity.getWaterlevel()[i+1]=realevel;
					tempresult=-reservoirEntity.getInmaxflow()[i];
				}
				return Math.abs(tempresult);
				
			}else {
				tempresult=reservoirEntity.getInminflow()[i];
				waterlevel=forwardlevelcontrol(tempresult, outputflow, regioninflow, lastlevel, curve, curve2);
				if(waterlevel>reservoirEntity.getMaxLevel()[i]) {

					double content=curve.getV1ByV0(waterlevel)-curve.getV1ByV0(reservoirEntity.getMaxLevel()[i]);
					double tempflow=content/(10*24*3600);
					waterlevel=reservoirEntity.getMaxLevel()[i];
					reservoirEntity.getWaterlevel()[i+1]=waterlevel;
					reservoirEntity.getInflow()[i]=tempresult;
					return tempflow;
				}else {
					reservoirEntity.getInflow()[i]=tempresult;
					reservoirEntity.getWaterlevel()[i+1]=waterlevel;
				}
			}
		}else {
			reservoirEntity.getInflow()[i]=tempresult;
		}
		return 0;
	}

	private double forwardlevelcontrol(double inflow,double outputflow,double regioninflow,double beginlevel,DoubleCurve l_ccurve,DoubleCurve c_lcurve) {

		double begaincontent=l_ccurve.getV1ByV0(beginlevel);
		double result=(inflow+regioninflow-outputflow)*10*24*3600+begaincontent;
		double endlevel=c_lcurve.getV1ByV0(result);
		return endlevel;
		
	}

	private void Forwardfloodsim(ReservoirEntity reservoirEntity,double tempwatercontent,int i,DoubleCurve curve,DoubleCurve curve2) {
		
		Entity entity=reservoirEntity;
		String id=reservoirEntity.getEntityStat().getWasteentity().getEntityStat().getId();
		double testflow=tempwatercontent+reservoirEntity.getOutflow()[i];
		double addvolume=tempwatercontent;
		if(testflow<=reservoirEntity.getOutmaxflow()[i]&&testflow>0) {
			reservoirEntity.getWasteWater()[i]=tempwatercontent*10*24*3600;
			reservoirEntity.getOutflow()[i]=testflow;
			while(entity.getEntityStat().getId()!=id) {
				List<CanalEntity> canalEntities=entity.getEntityStat().getUpcanalEntitys();
				CanalEntity canalEntity=canalEntities.get(0);
				entity=canalEntity.getEntityStat().getDownstreammodel();
				if(entity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.PUMP)) {
					PumpEntity pumpEntity=(PumpEntity)entity;
					double lastflow=pumpEntity.getAvgflow()[i];
					pumpEntity.getAvgflow()[i]=lastflow+addvolume*canalEntity.getEntityStat().getLosspara();
					addvolume=addvolume*canalEntity.getEntityStat().getLosspara();
				}else if(entity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.RESERVOIR)) {
					ReservoirEntity reservoirEntity2=(ReservoirEntity)entity;
					double lastflow=reservoirEntity2.getInflow()[i];
					reservoirEntity2.getInflow()[i]=lastflow+addvolume*canalEntity.getEntityStat().getLosspara();
					reservoirEntity2.getWasteWater()[i]=addvolume*canalEntity.getEntityStat().getLosspara();
				}else {
					GateEntity gateEntity=(GateEntity)entity;
					double lastflow=gateEntity.getAvgflow()[i];
					gateEntity.getAvgflow()[i]=lastflow+addvolume*canalEntity.getEntityStat().getLosspara();
					addvolume=addvolume*canalEntity.getEntityStat().getLosspara();
				}
			}
		}else {
			double content=curve.getV1ByV0(reservoirEntity.getEntityStat().getLevelNormal())+tempwatercontent*10*24*3600;
			reservoirEntity.getWaterlevel()[i+1]=curve2.getV1ByV0(content);
		}
		
	}

	public void Forwardflowcontrol(double inflow,ReservoirEntity pumpEntity2,double regioninflow,int i,double calresult,CanalEntity canalEntity) {

		double rate;
		List<IntakeEntity> intakeEntities=canalEntity.getEntityStat().getIntakeEntitys();
		double intakeablility=0;
		for(int j=0;j<intakeEntities.size();j++) {
			intakeablility+=intakeEntities.get(j).getIntakeflow()[i];
		}
		if(calresult>pumpEntity2.getInmaxflow()[i]) {
			rate=(calresult-pumpEntity2.getInmaxflow()[i])/intakeablility;
		}else if(calresult<pumpEntity2.getInminflow()[i] && inflow>0) {
			rate=(pumpEntity2.getInminflow()[i]-calresult)/intakeablility;
		}else {
			rate=-Double.MAX_VALUE;
		}
		
		if(calresult>pumpEntity2.getInmaxflow()[i]&&rate<0.3) {
			double reducerate=calresult-pumpEntity2.getInmaxflow()[i];
			constraintHandling(canalEntity, reducerate/canalEntity.getEntityStat().getLosspara(), i, intakeablility,0);
			pumpEntity2.getInflow()[i]=pumpEntity2.getInmaxflow()[i];
		}else if(calresult<pumpEntity2.getInminflow()[i]&&rate<=1&&rate>0 && inflow>0) {
			double reducerate=pumpEntity2.getInminflow()[i]-calresult;
			constraintHandling(canalEntity, reducerate/canalEntity.getEntityStat().getLosspara(), i, intakeablility, 1);
			pumpEntity2.getInflow()[i]=pumpEntity2.getInminflow()[i];
		}else if(calresult<pumpEntity2.getInminflow()[i] && inflow==0) {
			AssignmentDIV(canalEntity, i);
			pumpEntity2.getInflow()[i]=pumpEntity2.getInminflow()[i];
		}else {
			pumpEntity2.getInflow()[i]=calresult;
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

	public ReservoirEntityStat getEntityStat() {
		return (ReservoirEntityStat) entityStat;
	}

	public double[] getInflow() {
		return Inflow;
	}

	public void setInflow(double[] inflow) {
		this.Inflow = inflow;
	}

	public double[] getOutflow() {
		return Outflow;
	}

	public void setOutflow(double[] outflow) {
		this.Outflow = outflow;
	}

	public double[] getSectionInflow() {
		return SectionInflow;
	}

	public void setSectionInflow(double[] sectionInflow) {
		SectionInflow = sectionInflow;
	}

	public double[] getWasteWater() {
		return WasteWater;
	}

	public void setWasteWater(double[] wasteWater) {
		WasteWater = wasteWater;
	}

	public double[] getReservoirstorage() {
		return Reservoirstorage;
	}

	public void setReservoirstorage(double[] reservoirstorage) {
		Reservoirstorage = reservoirstorage;
	}

	public double[] getWaterlevel() {
		return waterlevel;
	}

	public void setWaterlevel(double[] waterlevel) {
		this.waterlevel = waterlevel;
	}

	public double[] getInmaxflow() {
		return inmaxflow;
	}

	public void setInmaxflow(double[] inmaxflow) {
		this.inmaxflow = inmaxflow;
	}

	public double[] getInminflow() {
		return inminflow;
	}

	public void setInminflow(double[] inminflow) {
		this.inminflow = inminflow;
	}

	public double[] getOutmaxflow() {
		return outmaxflow;
	}

	public void setOutmaxflow(double[] outmaxflow) {
		this.outmaxflow = outmaxflow;
	}

	public double[] getOutminflow() {
		return outminflow;
	}

	public void setOutminflow(double[] outminflow) {
		this.outminflow = outminflow;
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

	public double[] getMaxLevel() {
		return MaxLevel;
	}

	public void setMaxLevel(double[] maxLevel) {
		MaxLevel = maxLevel;
	}

	public double[] getMinLevel() {
		return MinLevel;
	}

	public void setMinLevel(double[] minLevel) {
		MinLevel = minLevel;
	}
	public double[] getInflowV() {
		return InflowV;
	}
	public void setInflowV(double[] inflowV) {
		InflowV = inflowV;
	}
	public double[] getOutflowV() {
		return OutflowV;
	}
	public void setOutflowV(double[] outflowV) {
		OutflowV = outflowV;
	}
	public double[] getFluctuatelimit() {
		return Fluctuatelimit;
	}
	public void setFluctuatelimit(double[] fluctuatelimit) {
		Fluctuatelimit = fluctuatelimit;
	}
	public double getStartendlimit() {
		return Startendlimit;
	}
	public void setStartendlimit(double startendlimit) {
		Startendlimit = startendlimit;
	}



	
	
}
