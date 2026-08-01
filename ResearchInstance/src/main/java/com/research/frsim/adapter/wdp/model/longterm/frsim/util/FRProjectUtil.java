package com.research.frsim.adapter.wdp.model.longterm.frsim.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.research.frsim.adapter.wdp.bean.Project;
import com.research.frsim.adapter.wdp.bean.entity.canal.CanalEntity;
import com.research.frsim.adapter.wdp.bean.entity.gate.GateEntity;
import com.research.frsim.adapter.wdp.bean.entity.intake.IntakeEntity;
import com.research.frsim.adapter.wdp.bean.entity.pump.PumpEntity;
import com.research.frsim.adapter.wdp.bean.entity.reservoir.ReservoirEntity;
import com.research.frsim.adapter.wdp.enumerate.BoundaryTypeEnum;
import com.research.frsim.adapter.wdp.util.NumberUtil;
import com.research.frsim.util.ExcelTool;

public class FRProjectUtil {
	
	public static void readinputproject(Project project,String filepath,Map<String, List<double[]>> limitmap) throws IOException {


		Object[][] inipumpQ=ExcelTool.read07Excel(filepath, "pump");
		Object[][] canalinflow=ExcelTool.read07Excel(filepath, "canal");
		Object[][] reservoirpredict=ExcelTool.read07Excel(filepath, "reservoir");
		Object[][] intakepara=ExcelTool.read07Excel(filepath, "intake");
		Object[][] gatepara=ExcelTool.read07Excel(filepath, "gate");
		Object[][] projectinflo=ExcelTool.read07Excel(filepath, "project");

		Map<String, Map<String, List<Double>>> tempmap=new LinkedHashMap<String, Map<String,List<Double>>>();
		Map<String, List<Double>> tempdata;
		int c=0;
		String[] splitwaterlevel=projectinflo[4][0].toString().split(",");
		for(int i=1;i<reservoirpredict.length;i++) {

			String name=reservoirpredict[i][0].toString();
			
			if(tempmap.containsKey(name)) {
				tempmap.get(name).get("water_predict").add(NumberUtil.objectToDouble(reservoirpredict[i][3]));
				tempmap.get(name).get("inflow_low").add(NumberUtil.objectToDouble(reservoirpredict[i][4].toString().split(",")[0]));
				tempmap.get(name).get("inflow_upper").add(NumberUtil.objectToDouble(reservoirpredict[i][4].toString().split(",")[1]));
				tempmap.get(name).get("outflow_low").add(NumberUtil.objectToDouble(reservoirpredict[i][5].toString().split(",")[0]));
				tempmap.get(name).get("outflow_upper").add(NumberUtil.objectToDouble(reservoirpredict[i][5].toString().split(",")[1]));
			}else {

				ReservoirEntity reservoirEntity=(ReservoirEntity)project.seekEntityById(name);
				reservoirEntity.getWaterlevel()[0]=NumberUtil.objectToDouble(splitwaterlevel[c]);
				reservoirEntity.getEntityStat().setIniwaterlevel(NumberUtil.objectToDouble(splitwaterlevel[c]));
				c++;
				tempdata=new LinkedHashMap<String, List<Double>>();
				List<Double> predict=new ArrayList<Double>();
				List<Double> limitdownin=new ArrayList<Double>();
				List<Double> limitupin=new ArrayList<Double>();
				List<Double> limitdownout=new ArrayList<Double>();
				List<Double> limitupout=new ArrayList<Double>();
				predict.add(NumberUtil.objectToDouble(reservoirpredict[i][3]));
				limitdownin.add(NumberUtil.objectToDouble(reservoirpredict[i][4].toString().split(",")[0]));
				limitupin.add(NumberUtil.objectToDouble(reservoirpredict[i][4].toString().split(",")[1]));
				limitdownout.add(NumberUtil.objectToDouble(reservoirpredict[i][5].toString().split(",")[0]));
				limitupout.add(NumberUtil.objectToDouble(reservoirpredict[i][5].toString().split(",")[1]));
				tempdata.put("water_predict", predict);
				tempdata.put("inflow_low", limitdownin);
				tempdata.put("inflow_upper", limitupin);
				tempdata.put("outflow_low", limitdownout);
				tempdata.put("outflow_upper", limitupout);
				tempmap.put(name, tempdata);
			}
		}

		Map<String, Map<String, List<Double>>> tempmappump=new LinkedHashMap<String, Map<String,List<Double>>>();
		Map<String, List<Double>> tempdatapump;
		for(int i=1;i<inipumpQ.length;i++) {
			String name=inipumpQ[i][0].toString();
			if(tempmappump.containsKey(name)) {
				tempmappump.get(name).get("limit_low").add(NumberUtil.objectToDouble(inipumpQ[i][3].toString().split(",")[0]));
				tempmappump.get(name).get("limit_upper").add(NumberUtil.objectToDouble(inipumpQ[i][3].toString().split(",")[1]));
			}else {
				tempdatapump=new LinkedHashMap<String, List<Double>>();
				List<Double> limitdown=new ArrayList<Double>();
				List<Double> limitup=new ArrayList<Double>();
				limitdown.add(NumberUtil.objectToDouble(inipumpQ[i][3].toString().split(",")[0]));
				limitup.add(NumberUtil.objectToDouble(inipumpQ[i][3].toString().split(",")[1]));
				tempdatapump.put("limit_low", limitdown);
				tempdatapump.put("limit_upper", limitup);
				tempmappump.put(name, tempdatapump);
			}
		}

		Map<String, Map<String, List<Double>>> tempmapintake=new LinkedHashMap<String, Map<String,List<Double>>>();
		Map<String, List<Double>> tempdataintake;
		for(int i=1;i<intakepara.length;i++) {
			String name=intakepara[i][0].toString();
			if(tempmapintake.containsKey(name)) {
				tempmapintake.get(name).get("intake_flow").add(NumberUtil.objectToDouble(intakepara[i][3]));
				tempmapintake.get(name).get("limit_low").add(NumberUtil.objectToDouble(intakepara[i][4].toString().split(",")[0]));
				tempmapintake.get(name).get("limit_upper").add(NumberUtil.objectToDouble(intakepara[i][4].toString().split(",")[1]));
			}else {
				tempdataintake=new LinkedHashMap<String, List<Double>>();
				List<Double> intakeflow=new ArrayList<Double>();
				List<Double> limitdown=new ArrayList<Double>();
				List<Double> limitup=new ArrayList<Double>();
				intakeflow.add(NumberUtil.objectToDouble(intakepara[i][3]));
				limitdown.add(NumberUtil.objectToDouble(intakepara[i][4].toString().split(",")[0]));
				limitup.add(NumberUtil.objectToDouble(intakepara[i][4].toString().split(",")[1]));
				tempdataintake.put("intake_flow", intakeflow);
				tempdataintake.put("limit_low", limitdown);
				tempdataintake.put("limit_upper", limitup);
				tempmapintake.put(name, tempdataintake);
			}
		}

		Map<String, Map<String, List<Double>>> tempmapgate=new LinkedHashMap<String, Map<String,List<Double>>>();
		Map<String, List<Double>> tempdatagate;
		for(int i=1;i<gatepara.length;i++) {
			String name=gatepara[i][0].toString();
			if(tempmapgate.containsKey(name)) {
				tempmapgate.get(name).get("limit_low").add(NumberUtil.objectToDouble(gatepara[i][3].toString().split(",")[0]));
				tempmapgate.get(name).get("limit_upper").add(NumberUtil.objectToDouble(gatepara[i][3].toString().split(",")[1]));
			}else {
				tempdatagate=new LinkedHashMap<String, List<Double>>();
				List<Double> limitdown=new ArrayList<Double>();
				List<Double> limitup=new ArrayList<Double>();
				limitdown.add(NumberUtil.objectToDouble(gatepara[i][3].toString().split(",")[0]));
				limitup.add(NumberUtil.objectToDouble(gatepara[i][3].toString().split(",")[1]));
				tempdatagate.put("limit_low",limitdown);
				tempdatagate.put("limit_upper", limitup);
				tempmapgate.put(name, tempdatagate);
			}
		}

		Map<String, Map<String, List<Double>>> tempmapcanal=new LinkedHashMap<String, Map<String,List<Double>>>();
		Map<String, List<Double>> tempdatacanal;
		for(int i=1;i<canalinflow.length;i++) {
			String name=canalinflow[i][0].toString();
			if(tempmapcanal.containsKey(name)) {
				tempmapcanal.get(name).get("latency_flow").add(NumberUtil.objectToDouble(canalinflow[i][2]));
			}else {
				tempdatacanal=new LinkedHashMap<String, List<Double>>();
				List<Double> inflow=new ArrayList<Double>();
				inflow.add(NumberUtil.objectToDouble(canalinflow[i][2]));
				tempdatacanal.put("latency_flow", inflow);
				tempmapcanal.put(name, tempdatacanal);
			}
		}

		for(String key:tempmap.keySet()) {
			
			ReservoirEntity reservoirEntity=(ReservoirEntity)project.seekEntityById(key);
			String testid=reservoirEntity.getEntityStat().getId();
			double[] predict=new double[reservoirEntity.getInflow().length];
			List<Double> inflowpredict=tempmap.get(key).get("water_predict");
			for(int j=0;j<inflowpredict.size();j++) {
				predict[j]=inflowpredict.get(j);
			}
			List<Double> inlimitdown=tempmap.get(key).get("inflow_low");
			double[] limitdown=new double[reservoirEntity.getInminflow().length];
			List<Double> inlimitup=tempmap.get(key).get("inflow_upper");
			double[] limitup=new double[reservoirEntity.getInmaxflow().length];
			List<Double> outlimitdown=tempmap.get(key).get("outflow_low");
			double[] limitdon=new double[reservoirEntity.getOutminflow().length];
			List<Double> outlimitup=tempmap.get(key).get("outflow_upper");
			double[] limiup=new double[reservoirEntity.getOutmaxflow().length];
			for(int j=0;j<inlimitdown.size();j++) {
				limitdown[j]=inlimitdown.get(j);
				limitup[j]=inlimitup.get(j);
				limitdon[j]=outlimitdown.get(j);
				limiup[j]=outlimitup.get(j);
			}

			List<double[]> list = limitmap.get(testid);
			double[] maxlevel=new double[list.size()];
			double[] minlevel=new double[list.size()];
			for(int i=0;i<maxlevel.length;i++) {
				maxlevel[i]=list.get(i)[1];
				minlevel[i]=list.get(i)[0];
			}
			Map<String, double[]> boundary=new LinkedHashMap<String, double[]>();
			boundary.put(BoundaryTypeEnum.RESERVOIR_MININFLOW.getType(), limitdown);
			boundary.put(BoundaryTypeEnum.RESERVOIR_MAXINFLOW.getType(), limitup);
			boundary.put(BoundaryTypeEnum.RESERVOIR_MINOUTFLOW.getType(), limitdon);
			boundary.put(BoundaryTypeEnum.RESERVOIR_MAXOUTFLOW.getType(), limiup);
			boundary.put(BoundaryTypeEnum.RESERVOIR_INFLOWQ.getType(), predict);
			boundary.put(BoundaryTypeEnum.RESERVOIR_MAXLEVEL.getType(), maxlevel);
			boundary.put(BoundaryTypeEnum.RESERVOIR_MINLEVEL.getType(), minlevel);
			project.getBoundary().getReservoirboundary().put(testid, boundary);
		}

		for(String key:tempmappump.keySet()) {
			PumpEntity pumpEntity=(PumpEntity)project.seekEntityById(key);
			String testid=pumpEntity.getEntityStat().getId();
			List<Double> limitdown=tempmappump.get(key).get("limit_low");
			List<Double> limitup=tempmappump.get(key).get("limit_upper");
			double[] condown=new double[pumpEntity.getMinflow().length];
			double[] conup=new double[pumpEntity.getMaxflow().length];
			for(int j=0;j<limitdown.size();j++) {
				condown[j]=limitdown.get(j);
				conup[j]=limitup.get(j);
			}
			Map<String, double[]> boundary=new LinkedHashMap<String, double[]>();
			boundary.put(BoundaryTypeEnum.PUMP_MINFLOW.getType(), condown);
			boundary.put(BoundaryTypeEnum.PUMP_MAXFLOW.getType(), conup);
			project.getBoundary().getPumpboundary().put(testid, boundary);
		}

		for(String key:tempmapgate.keySet()) {
			GateEntity gateEntity=(GateEntity)project.seekEntityById(key);
			String testid=gateEntity.getEntityStat().getId();
			List<Double> limitdown=tempmapgate.get(key).get("limit_low");
			List<Double> limitup=tempmapgate.get(key).get("limit_upper");
			double[] condown=new double[gateEntity.getMaxflow().length];
			double[] conup=new double[gateEntity.getMinflow().length];
			for(int j=0;j<limitdown.size();j++) {
				condown[j]=limitdown.get(j);
				conup[j]=limitup.get(j);
			}
			Map<String, double[]> boundary=new LinkedHashMap<String, double[]>();
			boundary.put(BoundaryTypeEnum.GATE_MINFLOW.getType(), condown);
			boundary.put(BoundaryTypeEnum.GATE_MAXFLOW.getType(), conup);
			project.getBoundary().getGateboundary().put(testid, boundary);
		}

		for(String key:tempmapintake.keySet()) {
			IntakeEntity intakeEntity=(IntakeEntity)project.seekEntityById(key);
			if(intakeEntity==null) {
				continue;
			}
			String testid=intakeEntity.getEntityStat().getId();
			List<Double> inflow=tempmapintake.get(key).get("intake_flow");
			List<Double> limitdown=tempmapintake.get(key).get("limit_low");
			List<Double> limitup=tempmapintake.get(key).get("limit_upper");
			double[] regionintakeflow=new double[intakeEntity.getIntakeflow().length];
			double[] condown=new double[intakeEntity.getMinflow().length];
			double[] conup=new double[intakeEntity.getMaxflow().length];
			for(int i=0;i<inflow.size();i++) {
				regionintakeflow[i]=inflow.get(i);
				condown[i]=limitdown.get(i);
				conup[i]=limitup.get(i);
			}
			Map<String, double[]> boundary=new LinkedHashMap<String, double[]>();
			boundary.put(BoundaryTypeEnum.INTAKE_INTAKEQ.getType(), regionintakeflow);
			boundary.put(BoundaryTypeEnum.INTAKE_DEMANDQ.getType(), regionintakeflow);
			boundary.put(BoundaryTypeEnum.INTAKE_MINFLOW.getType(), condown);
			boundary.put(BoundaryTypeEnum.INTAKE_MAXFLOW.getType(), conup);
			project.getBoundary().getIntakeboundary().put(testid, boundary);
			
		}
		for(String key:tempmapcanal.keySet()) {
			CanalEntity canalEntity=(CanalEntity)project.seekEntityById(key);
			String testid=canalEntity.getEntityStat().getId();
			List<Double> regioninflow=tempmapcanal.get(key).get("latency_flow");
			double[] inflow=new double[canalEntity.getIntakeflow().length];
			for(int j=0;j<regioninflow.size();j++) {
				inflow[j]=regioninflow.get(j);
			}
			Map<String, double[]> boundary=new LinkedHashMap<String, double[]>();
			boundary.put(BoundaryTypeEnum.CANAL_INFLOW.getType(), inflow);
			project.getBoundary().getCanalboundary().put(testid, boundary);
			
		}
		
		}
	
	

	public static Map<String, List<double[]>> readlellimit(String filepath,Project project) throws IOException{
		
		Map<String, List<double[]>> map=new LinkedHashMap<String, List<double[]>>();

		Object[][] reservoirpredict=ExcelTool.read07Excel(filepath, "reservoir");
		
		for(int i=1;i<reservoirpredict.length;i++) {
			
			if(map.containsKey(reservoirpredict[i][0].toString())) {
				map.get(reservoirpredict[i][0].toString()).add(new double[] {NumberUtil.objectToDouble(reservoirpredict[i][6].toString().split(",")[0])
						,NumberUtil.objectToDouble(reservoirpredict[i][6].toString().split(",")[1])});
			}else {
				map.put(reservoirpredict[i][0].toString(), new ArrayList<double[]>(Arrays.asList(new double[] {NumberUtil.objectToDouble(reservoirpredict[i][6].toString().split(",")[0]),
						NumberUtil.objectToDouble(reservoirpredict[i][6].toString().split(",")[1])})));
			}
			
		}
		return map;
		
	}
}
