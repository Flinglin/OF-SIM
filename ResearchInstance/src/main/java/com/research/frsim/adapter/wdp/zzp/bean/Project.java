package com.research.frsim.adapter.wdp.zzp.bean;

import com.research.frsim.adapter.wdp.enumerate.EntityTypeEnum;
import com.research.frsim.adapter.wdp.enumerate.ProjectModeEnum;
import com.research.frsim.adapter.wdp.enumerate.SectTypeEnum;
import com.research.frsim.adapter.wdp.enumerate.TimeScaleEnum;
import com.research.frsim.adapter.wdp.util.NumberUtil;
import com.research.frsim.adapter.wdp.util.curve.TribleCurve;
import com.research.frsim.adapter.wdp.zzp.bean.entity.canal.CanalEntity;
import com.research.frsim.adapter.wdp.zzp.bean.entity.catchment.CatchmentEntity;
import com.research.frsim.adapter.wdp.zzp.bean.entity.entity.Entity;
import com.research.frsim.adapter.wdp.zzp.bean.entity.gate.Gate;
import com.research.frsim.adapter.wdp.zzp.bean.entity.util.LineType;
import com.research.frsim.adapter.wdp.zzp.bean.entity.intake.IntakeEntity;
import com.research.frsim.util.ExcelTool;

import java.io.IOException;
import java.util.*;

public class Project {

	private Date starttime;

	private Date endtime;
	

	private int timeStep;
	

	private ProjectModeEnum projectModeEnum;

	private TimeScaleEnum timeScaleEnum;

	private List<TimeUnit> timeUnits;
	

	private List<Entity> entities;
	

	private Map<String, CanalEntity> canalmap;

	private List<String> calculatesequence;
	

	public Project() {
		timeUnits = new ArrayList<TimeUnit>();
		canalmap = new LinkedHashMap<String, CanalEntity>();
		calculatesequence = new ArrayList<String>();
		entities = new ArrayList<Entity>();
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

	private static Map<String,List<double[]>> readCurveData(Object[][] curvedate){
		Map<String,List<double[]>> canalcurvemap = new LinkedHashMap<String, List<double[]>>();
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

	public Entity seekModelEntityByName(String ennm) {
		Entity entity = null;
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i).getStat().getName().equals(ennm)) {
				entity = entities.get(i);
			}
		}
		return entity;
	}

	public Entity seekModelEntityByNameByType(String ennm, EntityTypeEnum modelEntityTypeEnum) {
		Entity entity = null;
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i).getStat().getName().equals(ennm) && entities.get(i).getStat().getEntityTypeEnum() == modelEntityTypeEnum) {
				entity = entities.get(i);
			}
		}
		return entity;
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

}
