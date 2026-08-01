package com.research.frsim.adapter.wdp.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.research.frsim.adapter.wdp.bean.entity.Function;
import com.research.frsim.adapter.wdp.bean.entity.boundary.Boundary;
import com.research.frsim.adapter.wdp.bean.entity.canal.CanalEntity;
import com.research.frsim.adapter.wdp.bean.entity.catchment.CatchmentEntity;
import com.research.frsim.adapter.wdp.bean.entity.entity.Entity;
import com.research.frsim.adapter.wdp.bean.entity.gate.GateEntity;
import com.research.frsim.adapter.wdp.bean.entity.gate.GateEntityStat;
import com.research.frsim.adapter.wdp.bean.entity.intake.IntakeEntity;
import com.research.frsim.adapter.wdp.bean.entity.intake.IntakeEntityStat;
import com.research.frsim.adapter.wdp.bean.entity.pump.PumpEntity;
import com.research.frsim.adapter.wdp.bean.entity.pump.PumpEntityStat;
import com.research.frsim.adapter.wdp.bean.entity.reservoir.ReservoirEntity;
import com.research.frsim.adapter.wdp.bean.entity.reservoir.ReservoirEntityStat;
import com.research.frsim.adapter.wdp.enumerate.EntityTypeEnum;
import com.research.frsim.adapter.wdp.enumerate.FloodSimDirectEnum;
import com.research.frsim.adapter.wdp.enumerate.ProjectModeEnum;
import com.research.frsim.adapter.wdp.enumerate.SectTypeEnum;
import com.research.frsim.adapter.wdp.enumerate.TimeScaleEnum;
import com.research.frsim.adapter.wdp.util.NumberUtil;
import com.research.frsim.adapter.wdp.util.curve.TribleCurve;
import com.research.frsim.core.util.DoubleCurve;
import com.research.frsim.util.ExcelTool;


public class Project {

	private Date starttime;

	private Date endtime;

	private int timeStep;

	private ProjectModeEnum projectModeEnum;

	private TimeScaleEnum timeScaleEnum;

	private Boundary boundary;

	private List<TimeUnit> timeUnits;

	private List<Entity> entities;
	

	private Map<String, CanalEntity> canalmap;

	private List<String> calculatesequence;

	public Project() {
		timeUnits = new ArrayList<TimeUnit>();
		canalmap = new LinkedHashMap<String, CanalEntity>();
		calculatesequence = new ArrayList<String>();
		entities = new ArrayList<Entity>();
		boundary = new Boundary();
	}

	public void constructTimeList() {
		
		Calendar sttm = Calendar.getInstance();
		sttm.setTime(starttime);
		Calendar edtm = Calendar.getInstance();
		edtm.setTime(endtime);
		Calendar temptm1 = Calendar.getInstance();
		temptm1.setTime(starttime);
		while (!temptm1.equals(edtm)) {
			TimeUnit timeUnit = new TimeUnit();
			timeUnit.setStartTime(temptm1.getTime());
			temptm1.add(Calendar.MINUTE, timeStep);
			timeUnit.setEndTime(temptm1.getTime());
			timeUnit.calculateTimeLength();
			timeUnits.add(timeUnit);
		}
	}

	public void constructTimeListLongterm() {
		Calendar sttm=Calendar.getInstance();
		sttm.setTime(starttime);
		Calendar edtm=Calendar.getInstance();
		edtm.setTime(endtime);

		Calendar temptm1 = Calendar.getInstance();
		temptm1.setTime(starttime);
		while(!temptm1.equals(edtm)) {
			TimeUnit timeUnit=new TimeUnit();
			TimeUnit timeUnit2=new TimeUnit();
			TimeUnit timeUnit3=new TimeUnit();

			int maxday=temptm1.getActualMaximum(Calendar.DAY_OF_MONTH);
			Date date=temptm1.getTime();
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(date);

			timeUnit.setStartTime(temptm1.getTime());
			calendar.add(Calendar.DATE, 9);
			timeUnit.setEndTime(calendar.getTime());
			timeUnits.add(timeUnit);

			calendar.add(Calendar.DATE, 1);
			timeUnit2.setStartTime(calendar.getTime());
			calendar.add(Calendar.DATE, 9);
			timeUnit2.setEndTime(calendar.getTime());
			timeUnits.add(timeUnit2);

			calendar.add(Calendar.DATE, 1);
			timeUnit3.setStartTime(calendar.getTime());
			calendar.add(Calendar.DATE, maxday-21);
			timeUnit3.setEndTime(calendar.getTime());
			timeUnits.add(timeUnit3);
			temptm1.add(Calendar.MONTH, 1);
		}
	}

	public static boolean readParafile(Project project,String excelpath) {
		boolean result = true;
		String filepath = excelpath;

		Object[][] gatepara = null;
		try {
			gatepara = ExcelTool.read07Excel(filepath, "gate_parameter");
			Object firstdata[] = gatepara[1];
		} catch (Exception e) {

		}
		

		List<String> gatelist = new ArrayList<>();
		Object[][] canaldata = null;
		try {
			canaldata = ExcelTool.read07Excel(filepath, "canal_parameter");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (int i = 1; i < canaldata.length; i++) {
			if (!gatelist.contains(canaldata[i][1].toString())) {
				gatelist.add(canaldata[i][1].toString());
			}
			if (!gatelist.contains(canaldata[i][2].toString())) {
				gatelist.add(canaldata[i][2].toString());
			}
		}
		
		for (int i = 1; i < gatepara.length; i++) {
			if (!gatelist.contains(gatepara[i][1].toString())) {
				continue;
			}
			GateEntityStat entityStat = new GateEntityStat();
			try {entityStat.setId(gatepara[i][0].toString());} catch (Exception e) {}
			entityStat.setName(gatepara[i][1].toString());
			entityStat.setEntityTypeEnum(EntityTypeEnum.GATE);
			try {entityStat.setDesignflow(NumberUtil.objectToDouble(gatepara[i][2]));} catch (Exception e) {}
			try {entityStat.setEnlargeflow(NumberUtil.objectToDouble(gatepara[i][3]));} catch (Exception e) {}
			try {entityStat.setDesignlevel(NumberUtil.objectToDouble(gatepara[i][4]));} catch (Exception e) {}
			try {entityStat.setEnlargelevel(NumberUtil.objectToDouble(gatepara[i][5]));} catch (Exception e) {}
			try {entityStat.setAlertlevel(NumberUtil.objectToDouble(gatepara[i][6]));} catch (Exception e) {}
			try {entityStat.setAlarmlevel(NumberUtil.objectToDouble(gatepara[i][7]));} catch (Exception e) {}
			try {entityStat.setWarninglevel(NumberUtil.objectToDouble(gatepara[i][8]));} catch (Exception e) {}
			try {entityStat.setWidth(NumberUtil.objectToDouble(gatepara[i][9]));} catch (Exception e) {}
			try {entityStat.setBtmel(NumberUtil.objectToDouble(gatepara[i][10]));} catch (Exception e) {}
			try {entityStat.setDistance(NumberUtil.objectToDouble(gatepara[i][11]));} catch (Exception e) {}
			try {entityStat.setSwitchtime(NumberUtil.objectToDouble(gatepara[i][12]));} catch (Exception e) {}
			try {entityStat.setQaddmax(NumberUtil.objectToDouble(gatepara[i][16]));} catch (Exception e) {}
			try {entityStat.setQreducemax(NumberUtil.objectToDouble(gatepara[i][17]));} catch (Exception e) {}
			double[] Emaxmin = new double[2];
			try {
				String[] Emaxmindata = gatepara[i][13].toString().split(",");
				Emaxmin[0] = NumberUtil.objectToDouble(Emaxmindata[0]);
				Emaxmin[1] = NumberUtil.objectToDouble(Emaxmindata[1]);
			} catch (Exception e) { }
			entityStat.setOpennessspace(Emaxmin);
			
			Function gateLineType = new Function();
			try {
				gateLineType.setLinetype(NumberUtil.objectToInt(gatepara[i][14]));
			} catch (Exception e) {}
			try {
				String paraString[] = gatepara[i][15].toString().split(",");
				double para[] = new double[paraString.length];
				for (int j = 0; j < para.length; j++) {
					para[j] = NumberUtil.objectToDouble(paraString[j]);
				}
				gateLineType.setPara(para);
			} catch (Exception e) {}
			entityStat.setGateLine(gateLineType);
			GateEntity gateEntity = new GateEntity(entityStat,project);
			project.getEntities().add(gateEntity);
		}

		Object[][] pumppara = null;
		try {
			pumppara = ExcelTool.read07Excel(filepath, "pump_parameter");
			Object firstdata[] = pumppara[1];
		} catch (Exception e) {

		}
		
		List<String> pumplist=new ArrayList<>();
		Object[][] pumpdata=null;
		try {
			pumpdata=ExcelTool.read07Excel(excelpath, "canal_parameter");
		} catch (Exception e) {

		}
		
		for(int i=1;i<pumpdata.length;i++) {
			if(!pumplist.contains(pumpdata[i][1].toString())) {
				pumplist.add(pumpdata[i][1].toString());
			}
			if(!pumplist.contains(pumpdata[i][2].toString())) {
				pumplist.add(pumpdata[i][2].toString());
			}
		}
		
		for(int i=1;i<pumppara.length;i++) {
			
			if(!pumplist.contains(pumppara[i][1].toString())) {
				continue;
			}
			PumpEntityStat entityStat=new PumpEntityStat();
			try {entityStat.setEntityTypeEnum(EntityTypeEnum.PUMP);;} catch (Exception e) {}
			try {entityStat.setId(pumppara[i][0].toString());} catch (Exception e) {}
			try {entityStat.setName(pumppara[i][1].toString());} catch (Exception e) {}
			try {entityStat.setMindownlevel(NumberUtil.objectToDouble(pumppara[i][2]));} catch (Exception e) {}
			try {entityStat.setMaxdownlevel(NumberUtil.objectToDouble(pumppara[i][3]));} catch (Exception e) {}
			try {entityStat.setMinuplevel(NumberUtil.objectToDouble(pumppara[i][4]));} catch (Exception e) {}
			try {entityStat.setMaxuplevel(NumberUtil.objectToDouble(pumppara[i][5]));} catch (Exception e) {}
			try {
				String[] pumprangement=pumppara[i][6].toString().split(",");
				double[] pumprangedouble=new double[pumprangement.length];
				for(int i1=0;i1<pumprangedouble.length;i1++) {
					pumprangedouble[i1]=NumberUtil.objectToDouble(pumprangement[i1]);
				}
				entityStat.setPumpvariable(pumprangedouble);
			} catch (Exception e) {}
			try {entityStat.setCrewnumbers(NumberUtil.objectToInt(pumppara[i][7]));} catch (Exception e) {}
			try {
				String[] pumprangement=pumppara[i][8].toString().split(",");
				double[] pumprangedouble=new double[pumprangement.length];
				for(int i1=0;i1<pumprangedouble.length;i1++) {
					pumprangedouble[i1]=NumberUtil.objectToDouble(pumprangement[i1]);
				}
				entityStat.setBLanglerange(pumprangedouble);
			} catch (Exception e) {}
			try {entityStat.setAdstep(NumberUtil.objectToInt(pumppara[i][9]));} catch (Exception e) {}
			try {
				String[] pumprangement=pumppara[i][10].toString().split(",");
				double[] pumprangedouble=new double[pumprangement.length];
				for(int i1=0;i1<pumprangedouble.length;i1++) {
					pumprangedouble[i1]=NumberUtil.objectToDouble(pumprangement[i1]);
				}
				entityStat.setFlowfeasibleregion(pumprangedouble);
			} catch (Exception e) {}
			try {entityStat.setElecPrice(NumberUtil.objectToDouble(pumppara[i][11]));} catch (Exception e) {}
			try {
				Function function=new Function();
				function.setLinetype(NumberUtil.objectToInt(pumppara[i][12]));
				String[] temppara=pumppara[i][13].toString().split(",");
				double[] tempparadouble=new double[temppara.length];
				for(int i1=0;i1<tempparadouble.length;i1++) {
					tempparadouble[i1]=NumberUtil.objectToDouble(temppara[i1]);
				}
				function.setPara(tempparadouble);
				entityStat.setLift_angle_flowline(function);
			} catch (Exception e) {}
			try {
				Function function=new Function();
				function.setLinetype(NumberUtil.objectToInt(pumppara[i][14]));
				String[] temppara=pumppara[i][15].toString().split(",");
				double[] tempparadouble=new double[temppara.length];
				for(int i1=0;i1<tempparadouble.length;i1++) {
					tempparadouble[i1]=NumberUtil.objectToDouble(temppara[i1]);
				}
				function.setPara(tempparadouble);
				entityStat.setLift_angle_efficiencyline(function);
			} catch (Exception e) {}
			try {
				Function function=new Function();
				String[] temppara=pumppara[i][16].toString().split(",");
				double[] tempparadouble=new double[temppara.length];
				for(int i1=0;i1<tempparadouble.length;i1++) {
					tempparadouble[i1]=NumberUtil.objectToDouble(temppara[i1]);
				}
				function.setPara(tempparadouble);
				entityStat.setFlow_lift_angleline(function);
			} catch (Exception e) {}
			PumpEntity pumpEntity=new PumpEntity(entityStat, project);
			project.getEntities().add(pumpEntity);

		}
		Object[][] intakedata = null;
		try {
			intakedata = ExcelTool.read07Excel(excelpath, "canal_parameter");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		List<String> intakelist = new ArrayList<>();
		for (int i = 1; i < intakedata.length; i++) {
			if (intakedata[i].length < 5) {
				continue;
			}
			if (intakedata[i][4] == null || intakedata[i][4].equals("")) {
				continue;
			}
			String[] intakes = intakedata[i][4].toString().split(",");
			for (int k = 0; k < intakes.length; k++) {
				intakelist.add(intakes[k]);
			}
		}
		Object[][] intakepara = null;
		try {
			intakepara = ExcelTool.read07Excel(filepath, "intake_parameter");
			Object firstdata[] = intakepara[1];
		} catch (Exception e) {

		}
		for (int i = 1; i < intakepara.length; i++) {
			if (!intakelist.contains(intakepara[i][1].toString())) {
				continue;
			}
			IntakeEntityStat entityStat = new IntakeEntityStat();
			try {entityStat.setId(intakepara[i][0].toString());} catch (Exception e) {}
			entityStat.setName(intakepara[i][1].toString());
			entityStat.setEntityTypeEnum(EntityTypeEnum.INTAKE);
			try {entityStat.setDistance(NumberUtil.objectToDouble(intakepara[i][2]));} catch (Exception e) {}
			try {entityStat.setIntakeability(NumberUtil.objectToDouble(intakepara[i][3]));} catch (Exception e) {}
			try {entityStat.setUse(NumberUtil.objectToInt(intakepara[i][4]));} catch (Exception e) {}
			try {entityStat.setDesertability(NumberUtil.objectToDouble(intakepara[i][5]));} catch (Exception e) {}
			IntakeEntity intakeEntity = new IntakeEntity(entityStat, project);
			project.getEntities().add(intakeEntity);
		}
		Object[][] reservoirpara=null;
		try {
			reservoirpara=ExcelTool.read07Excel(filepath, "reservoir_parameter");
			Object firstdata[]=reservoirpara[1];
		} catch (Exception e) {

		}
		if(reservoirpara!=null) {
			for(int i=1;i<reservoirpara.length;i++) {
				ReservoirEntityStat entityStat=new ReservoirEntityStat();
				try {entityStat.setName(reservoirpara[i][1].toString());} catch (Exception e) {}
				try {entityStat.setId(reservoirpara[i][0].toString());} catch (Exception e) {}
				try {entityStat.setLevelDead(NumberUtil.objectToDouble(reservoirpara[i][3]));} catch (Exception e) {}
				try {entityStat.setLevelNormal(NumberUtil.objectToDouble(reservoirpara[i][2]));} catch (Exception e) {}
				try {entityStat.setEntityTypeEnum(EntityTypeEnum.RESERVOIR);} catch (Exception e) {}
				try {entityStat.setLelvar(new double[] {NumberUtil.objectToDouble(reservoirpara[i][6].toString().split(",")[0])
						,NumberUtil.objectToDouble(reservoirpara[i][6].toString().split(",")[1])});
				} catch (Exception e) {}
				ReservoirEntity reservoirEntity=new ReservoirEntity(entityStat, project);
				project.getEntities().add(reservoirEntity);
			}
			for(int i=1;i<reservoirpara.length;i++) {
				ReservoirEntity reserentity=(ReservoirEntity)project.seekEntityById(reservoirpara[i][0].toString());
				ReservoirEntityStat entityStat=reserentity.getEntityStat();
				try {if(NumberUtil.objectToDouble(reservoirpara[i][4])==1) {
					entityStat.setDirectEnum(FloodSimDirectEnum.FORWARD);
				}else if(NumberUtil.objectToDouble(reservoirpara[i][4])==-1) {
					entityStat.setDirectEnum(FloodSimDirectEnum.REVERSE);
				}else {
					entityStat.setDirectEnum(FloodSimDirectEnum.NORMAL);;
				}
			} catch (Exception e) {}
			try {
				Entity entity=project.seekEntityByName(reservoirpara[i][5].toString());
				entityStat.setWasteentity(entity);
			} catch (Exception e) {}
			}
		}

		Object[][] canalpara = null;
		try {
			canalpara = ExcelTool.read07Excel(filepath, "canal_parameter");
			Object firstdata[] = canalpara[1];
		} catch (Exception e) {
			result = false;
			return result;
		}
		for (int i = 1; i < canalpara.length; i++) {
			CanalEntity canalEntity = new CanalEntity(project);
			try {canalEntity.getEntityStat().setId(canalpara[i][0].toString());} catch (Exception e) {}
			canalEntity.getEntityStat().setName(canalpara[i][1].toString()+"-"+canalpara[i][2].toString());
			Entity upmodel = project.seekEntityByName(canalpara[i][1].toString());
			Entity downmodel = project.seekEntityByName(canalpara[i][2].toString());
			canalEntity.getEntityStat().setEntityTypeEnum(EntityTypeEnum.CANAL);
			canalEntity.getEntityStat().setUpstreammodel(upmodel);
			canalEntity.getEntityStat().setDownstreammodel(downmodel);
			upmodel.getEntityStat().getUpcanalEntitys().add(canalEntity);
			downmodel.getEntityStat().getDowncanalEntitys().add(canalEntity);
			canalEntity.getEntityStat().setLosspara(NumberUtil.objectToDouble(canalpara[i][3]));

			Function level_storageline=new Function();
			try {level_storageline.setLinetype(NumberUtil.objectToInt(canalpara[i][6]));} catch (Exception e) {}
			try {
				String[] parastringcanal=canalpara[i][7].toString().split(",");
				double[] paradoublecanal=new double[parastringcanal.length];
				for(int i1=0;i1<paradoublecanal.length;i1++) {
					paradoublecanal[i1]=NumberUtil.objectToDouble(parastringcanal[i1]);
				}
				level_storageline.setPara(paradoublecanal);
			} catch (Exception e) {}
			canalEntity.getEntityStat().setLevel_storageline(level_storageline);

			Function storage_levelline=new Function();
			try {storage_levelline.setLinetype(NumberUtil.objectToInt(canalpara[i][8]));} catch (Exception e) {}
			try {
				String[] parastringcanal=canalpara[i][9].toString().split(",");
				double[] paradoublecanal=new double[parastringcanal.length];
				for(int i1=0;i1<paradoublecanal.length;i1++) {
					paradoublecanal[i1]=NumberUtil.objectToDouble(parastringcanal[i1]);
				}
				storage_levelline.setPara(paradoublecanal);
			} catch (Exception e) {}
			canalEntity.getEntityStat().setStroage_levelLine(storage_levelline);

			try {
				String intakes[] = canalpara[i][4].toString().split(",");
				for (int j = 0; j < intakes.length; j++) {
					IntakeEntity intakeEntity = (IntakeEntity) project.seekEntityByName(intakes[j]);
					if (intakeEntity != null) {
						canalEntity.getEntityStat().getIntakeEntitys().add(intakeEntity);
					}
				}
			} catch (Exception e) {}

			try {
				String catchments[] = canalpara[i][5].toString().split(",");
				for (int j = 0; j < catchments.length; j++) {
					CatchmentEntity catchmentEntity = (CatchmentEntity) project.seekEntityByName(catchments[j]);
					if (catchmentEntity != null) {
						canalEntity.getEntityStat().getCatchmentEntitys().add(catchmentEntity);
					}
				}
			} catch (Exception e) {}
			project.getEntities().add(canalEntity);
			project.getCalculatesequence().add(canalEntity.getEntityStat().getUuid());
			project.getCanalmap().put(canalEntity.getEntityStat().getUuid(), canalEntity);
		}

		Object[][] canalqzwcurve = null;
		try {
			canalqzwcurve = ExcelTool.read07Excel(filepath, "canal_level");
			Object firstdata[] = canalqzwcurve[1];
		} catch (Exception e) {

		}
		Map<String,List<double[]>> canalqzwcurvemap = readCurveData(canalqzwcurve);
		for (String key:canalqzwcurvemap.keySet()) {
			List<double[]> templist = canalqzwcurvemap.get(key);
			if(templist.size()==0) {
				continue;
			}
			double[][] curvedata = new double[templist.size()][3];
			for (int i = 0; i < templist.size(); i++) {
				curvedata[i] = templist.get(i);
			}
			TribleCurve tribleCurve = new TribleCurve(curvedata);
			CanalEntity canalEntity = (CanalEntity) project.seekEntityById(key);
			if (canalEntity!=null) {
				canalEntity.getEntityStat().setQzwcurve(tribleCurve);
			}
		}

		Object[][] canalqzdeltacurve = null;
		try {
			canalqzdeltacurve = ExcelTool.read07Excel(filepath, "canal_down");
			Object firstdata[] = canalqzdeltacurve[1];
		} catch (Exception e) {

		}
		Map<String,List<double[]>> canalqzdeltacurvemap = readCurveData(canalqzdeltacurve);
		for (String key:canalqzdeltacurvemap.keySet()) {
			List<double[]> templist = canalqzdeltacurvemap.get(key);
			if(templist.size()==0) {
				continue;
			}
			double[][] curvedata = new double[templist.size()][3];
			for (int i = 0; i < templist.size(); i++) {
				curvedata[i] = templist.get(i);
			}
			TribleCurve tribleCurve = new TribleCurve(curvedata);
			CanalEntity canalEntity = (CanalEntity) project.seekEntityById(key);
			if (canalEntity!=null) {
				canalEntity.getEntityStat().setQzdeltacurve(tribleCurve);
			}
		}

		Object[][] canalzqwcurve = null;
		try {
			canalzqwcurve = ExcelTool.read07Excel(filepath, "canal_down");
			Object firstdata[] = canalzqwcurve[1];
		} catch (Exception e) {

		}
		Map<String, List<double[]>> canalzqwcurvemap = readCurveData(canalzqwcurve);
		for (String key : canalzqwcurvemap.keySet()) {
			List<double[]> templist = canalzqwcurvemap.get(key);
			double[][] curvedata = new double[templist.size()][3];
			for (int i = 0; i < templist.size(); i++) {
				curvedata[i] = templist.get(i);
			}
			TribleCurve tribleCurve = new TribleCurve(curvedata);
			CanalEntity canalEntity = (CanalEntity) project.seekEntityById(key);
			if (canalEntity != null) {
				canalEntity.getEntityStat().setZqwcurve(tribleCurve);
			}
		}

		Object[][] canalqzzcurve = null;
		try {
			canalqzzcurve = ExcelTool.read07Excel(filepath, "canal_down_level");
			Object firstdata[] = canalqzzcurve[1];
		} catch (Exception e) {

		}
		Map<String, List<double[]>> canalqzzcurvemap = readCurveData(canalqzzcurve);
		for (String key : canalqzzcurvemap.keySet()) {
			List<double[]> templist = canalqzzcurvemap.get(key);
			double[][] curvedata = new double[templist.size()][3];
			for (int i = 0; i < templist.size(); i++) {
				curvedata[i] = templist.get(i);
			}
			TribleCurve tribleCurve = new TribleCurve(curvedata);
			CanalEntity canalEntity = (CanalEntity) project.seekEntityById(key);
			if (canalEntity != null) {
				canalEntity.getEntityStat().setQzzcurve(tribleCurve);
			}
		}

		Object[][] pumphqacurve=null;
		try {
			pumphqacurve = ExcelTool.read07Excel(filepath, "pump_rotate");
			Object firstdata[] = pumphqacurve[1];
		} catch (Exception e) {

		}
		Map<String, Map<String, List<double[]>>> reMap=readpumpcurvedata(pumphqacurve);
		if(reMap!=null) {
			for(String key:reMap.keySet()) {
				
				PumpEntity pumpEntity=(PumpEntity)project.seekEntityById(key);
				Map<String, List<double[]>> tempMap=reMap.get(key);
				List<double[][]> ds=new ArrayList<double[][]>();
				for(int i=1;i<tempMap.size()+1;i++) {
					
					List<double[]> tempList=tempMap.get(String.valueOf(i));
					double[][] tempresult=new double[tempList.size()][];
					for(int k=0;k<tempresult.length;k++) {
						tempresult[k]=tempList.get(k);
					}
					ds.add(tempresult);
				}

				pumpEntity.getEntityStat().setSearchF_L_ALine(ds);
			}
		}

		Object[][] canalcurvedata=null;
		try {
			canalcurvedata = ExcelTool.read07Excel(filepath, "canal_down_level");
			Object firstdata[] = canalcurvedata[1];
		} catch (Exception e) {

		}
		Map<String, List<double[]>> canalcurveresult=readCurveData(canalcurvedata);
		for(String key:canalcurveresult.keySet()) {
			
			CanalEntity canalEntity=(CanalEntity)project.seekEntityById(key);
			List<double[]> templist=canalcurveresult.get(key);
			double[][] tempresult=new double[templist.size()][];
			for(int i=0;i<templist.size();i++) {
				
				tempresult[i]=templist.get(i);
				
			}
			canalEntity.getEntityStat().setDf_Dl_UlLine(tempresult);
		}

		Object[][] reservoirlinepara=null;
		try {
			reservoirlinepara=ExcelTool.read07Excel(filepath, "level_storage");
			Object firstdata[] = canalcurvedata[1];
		} catch (Exception e) {

		}
		Map<String, List<double[]>> reservoircurveresult=readDoublecurvedata(reservoirlinepara);
		for(String key:reservoircurveresult.keySet()) {
			ReservoirEntity reservoirEntity=(ReservoirEntity)project.seekEntityById(key);
			List<double[]> templist=reservoircurveresult.get(key);
			double[][] tempresult=new double[templist.size()][];
			for(int i=0;i<templist.size();i++) {
				tempresult[i]=templist.get(i);
			}
			DoubleCurve curve=new DoubleCurve(tempresult);
			reservoirEntity.getEntityStat().setWlevel_storageCurve(curve);
		}

		Object[][] reservoirlinecpara=null;
		try {
			reservoirlinecpara=ExcelTool.read07Excel(filepath, "storage_level");
			Object firstdata[] = canalcurvedata[1];
		} catch (Exception e) {

		}
		Map<String, List<double[]>> reservoirlcurveresult=readDoublecurvedata(reservoirlinecpara);
		for(String key:reservoirlcurveresult.keySet()) {
			ReservoirEntity reservoirEntity=(ReservoirEntity)project.seekEntityById(key);
			List<double[]> templist=reservoirlcurveresult.get(key);
			double[][] tempresult=new double[templist.size()][];
			for(int i=0;i<templist.size();i++) {
				tempresult[i]=templist.get(i);
			}
			DoubleCurve curve=new DoubleCurve(tempresult);
			reservoirEntity.getEntityStat().setStorage_WlevelCurve(curve);
		}
		for (int i = 0; i < project.getEntities().size(); i++) {
			Entity entity = project.getEntities().get(i);
			if (entity.getEntityStat().getEntityTypeEnum() == EntityTypeEnum.GATE
					|| entity.getEntityStat().getEntityTypeEnum() == EntityTypeEnum.PUMP
					|| entity.getEntityStat().getEntityTypeEnum() == EntityTypeEnum.RESERVOIR) {

				if(entity.getEntityStat().getUpcanalEntitys().size() > 1 && entity.getEntityStat().getDowncanalEntitys().size() == 0) {

					entity.getEntityStat().setSectTypeEnum(SectTypeEnum.HeadDiversion);
					continue;
				}
				if(entity.getEntityStat().getDowncanalEntitys().size() > 1 && entity.getEntityStat().getUpcanalEntitys().size() == 0) {

					entity.getEntityStat().setSectTypeEnum(SectTypeEnum.EndConverge);
					continue;
				}
				if (entity.getEntityStat().getUpcanalEntitys().size() > 1) {

					entity.getEntityStat().setSectTypeEnum(SectTypeEnum.Diversion);
					continue;
				}
				if (entity.getEntityStat().getDowncanalEntitys().size() > 1) {

					entity.getEntityStat().setSectTypeEnum(SectTypeEnum.Converge);
					continue;
				}
				if (entity.getEntityStat().getUpcanalEntitys().size() == 1 && entity.getEntityStat().getDowncanalEntitys().size() == 0) {

					entity.getEntityStat().setSectTypeEnum(SectTypeEnum.Head);
					continue;
				}
				if (entity.getEntityStat().getUpcanalEntitys().size() == 0 && entity.getEntityStat().getDowncanalEntitys().size() == 1) {

					entity.getEntityStat().setSectTypeEnum(SectTypeEnum.End);
					continue;
				}
				entity.getEntityStat().setSectTypeEnum(SectTypeEnum.Normal);
			}
		}
		
		return result;
	}	

	public Entity seekEntityByName(String ennm) {
		Entity entity = null;
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i).getEntityStat().getName().equals(ennm)) {
				entity = entities.get(i);
			}
		}
		return entity;
	}

	public Entity seekEntityByNameByType(String ennm,EntityTypeEnum modelEntityTypeEnum) {
		Entity entity = null;
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i).getEntityStat().getName().equals(ennm) && entities.get(i).getEntityStat().getEntityTypeEnum() == modelEntityTypeEnum) {
				entity = entities.get(i);
			}
		}
		return entity;
	}

	public Entity seekEntityById(String enid) {
		Entity entity = null;
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i).getEntityStat().getId().equals(enid)) {
				entity = entities.get(i);
			}
		}
		return entity;
	}

	public Entity seekEntityByIdByType(String enid,EntityTypeEnum modelEntityTypeEnum) {
		Entity entity = null;
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i).getEntityStat().getId().equals(enid) && entities.get(i).getEntityStat().getEntityTypeEnum() == modelEntityTypeEnum) {
				entity = entities.get(i);
			}
		}
		return entity;
	}
	private static Map<String,List<double[]>> readCurveData(Object[][] curvedate){
		Map<String,List<double[]>> canalcurvemap = new LinkedHashMap<String, List<double[]>>();
		if(curvedate==null) {
			return canalcurvemap;
		}else {
			for (int i = 1; i < curvedate.length; i++) {
				String canalid = curvedate[i][0].toString();
				double[] tempdata = new double[3];
				tempdata[0] = NumberUtil.objectToDouble(curvedate[i][1]);
				tempdata[1] = NumberUtil.objectToDouble(curvedate[i][2]);
				tempdata[2] = NumberUtil.objectToDouble(curvedate[i][3]);
				if (canalcurvemap.containsKey(canalid)) {
					canalcurvemap.get(canalid).add(tempdata);
				}else {
					List<double[]> templist = new ArrayList<double[]>();
					templist.add(tempdata);
					canalcurvemap.put(canalid, templist);
				}
			}
			return canalcurvemap;
		}
		
	}

	private static Map<String, List<double[]>> readDoublecurvedata(Object[][] curvedata){
		
		Map<String,List<double[]>> canalcurvemap = new LinkedHashMap<String, List<double[]>>();
		if(curvedata==null) {
			return canalcurvemap;
		}else {
			for (int i = 1; i < curvedata.length; i++) {
				String canalid = curvedata[i][0].toString();
				double[] tempdata = new double[2];
				tempdata[0] = NumberUtil.objectToDouble(curvedata[i][1]);
				tempdata[1] = NumberUtil.objectToDouble(curvedata[i][2]);
				if (canalcurvemap.containsKey(canalid)) {
					canalcurvemap.get(canalid).add(tempdata);
				}else {
					List<double[]> templist = new ArrayList<double[]>();
					templist.add(tempdata);
					canalcurvemap.put(canalid, templist);
				}
			}
			return canalcurvemap;
		}
	}

	private static Map<String, Map<String, List<double[]>>> readpumpcurvedata(Object[][] pumpcurvedata){
		
		Map<String, List<double[]>> curvedata=new LinkedHashMap<String, List<double[]>>();
		if(pumpcurvedata==null) {
			return null;
		}
		for(int i=1;i<pumpcurvedata.length;i++) {
			
			String pumpid=pumpcurvedata[i][0].toString();
			String number=pumpcurvedata[i][1].toString();
			double[] tempdata=new double[4];
			tempdata[0] = NumberUtil.objectToDouble(pumpcurvedata[i][1]);
			tempdata[1] = NumberUtil.objectToDouble(pumpcurvedata[i][2]);
			tempdata[2] = NumberUtil.objectToDouble(pumpcurvedata[i][3]);
			tempdata[3] = NumberUtil.objectToDouble(pumpcurvedata[i][4]);
			if(curvedata.containsKey(pumpid)) {
				curvedata.get(pumpid).add(tempdata);
			}else {
				List<double[]> templist=new ArrayList<double[]>();
				templist.add(tempdata);
				curvedata.put(pumpid, templist);
			}
		}

		Map<String, Map<String, List<double[]>>> reMap=deepsort(curvedata);
		return reMap;
		
	}

	public static Map<String, Map<String, List<double[]>>> deepsort(Map<String, List<double[]>> curvedata){
		
		Map<String, Map<String, List<double[]>>> result=new LinkedHashMap<String, Map<String,List<double[]>>>();
		for(Map.Entry<String, List<double[]>> entry:curvedata.entrySet()) {
			
			Map<String, List<double[]>> tempmap=new LinkedHashMap<String, List<double[]>>();
			String key=entry.getKey();
			List<double[]> ds=entry.getValue();
			for(int k=0;k<ds.size();k++) {
				double[] tempdata=ds.get(k);
				double[] redata=new double[tempdata.length-1];
				String id=String.valueOf((int)tempdata[0]);
				for(int j=1;j<tempdata.length;j++) {
					redata[j-1]=tempdata[j];
				}
				if(tempmap.containsKey(id)) {
					tempmap.get(id).add(redata);
				}else {
					List<double[]> templist=new ArrayList<double[]>();
					templist.add(redata);
					tempmap.put(id, templist);
				}
			}
			
			
			result.put(key, tempmap);

		}
		
		return result;
	}

	public Date getStarttime() {
		return starttime;
	}


	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}


	public Date getEndtime() {
		return endtime;
	}


	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}
	

	public Map<String, CanalEntity> getCanalmap() {
		return canalmap;
	}


	public void setCanalmap(Map<String, CanalEntity> canalmap) {
		this.canalmap = canalmap;
	}


	public List<String> getCalculatesequence() {
		return calculatesequence;
	}


	public void setCalculatesequence(List<String> calculatesequence) {
		this.calculatesequence = calculatesequence;
	}


	public List<Entity> getEntities() {
		return entities;
	}


	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}

	public ProjectModeEnum getProjectModeEnum() {
		return projectModeEnum;
	}

	public void setProjectModeEnum(ProjectModeEnum projectModeEnum) {
		this.projectModeEnum = projectModeEnum;
	}

	public int getTimeStep() {
		return timeStep;
	}


	public void setTimeStep(int timeStep) {
		this.timeStep = timeStep;
	}

	public List<TimeUnit> getTimeUnits() {
		return timeUnits;
	}

	public void setTimeUnits(List<TimeUnit> timeUnits) {
		this.timeUnits = timeUnits;
	}

	public TimeScaleEnum getTimeScaleEnum() {
		return timeScaleEnum;
	}

	public void setTimeScaleEnum(TimeScaleEnum timeScaleEnum) {
		this.timeScaleEnum = timeScaleEnum;
	}

	public Boundary getBoundary() {
		return boundary;
	}

	public void setBoundary(Boundary boundary) {
		this.boundary = boundary;
	}
	
	
	

}
