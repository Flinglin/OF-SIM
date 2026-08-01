package com.research.frsim.adapter.wdp.model.longterm.frsim.util;

import java.util.ArrayList;
import java.util.List;

import com.research.frsim.adapter.wdp.bean.Project;
import com.research.frsim.adapter.wdp.bean.entity.boundary.Boundary;
import com.research.frsim.adapter.wdp.bean.entity.canal.CanalEntity;
import com.research.frsim.adapter.wdp.bean.entity.entity.Entity;
import com.research.frsim.adapter.wdp.bean.entity.gate.GateEntity;
import com.research.frsim.adapter.wdp.bean.entity.intake.IntakeEntity;
import com.research.frsim.adapter.wdp.bean.entity.pump.PumpEntity;
import com.research.frsim.adapter.wdp.bean.entity.reservoir.ReservoirEntity;
import com.research.frsim.adapter.wdp.enumerate.EntityTypeEnum;

public class FRProjectModel {
	

	protected List<CanalEntity> canalEntities;

	protected Project project;

	protected List<IntakeEntity> intakeEntities;
	

	protected List<ReservoirEntity> reservoirEntities;

	protected List<PumpEntity> pumpEntities;
	

	protected List<GateEntity> gateEntities;
	

	public FRProjectModel(Project project) {
		
		this.project=project;
		canalEntities=new ArrayList<CanalEntity>();
		for(int i=0;i<project.getCalculatesequence().size();i++) {
			CanalEntity canalEntity=project.getCanalmap().get(project.getCalculatesequence().get(i));
			canalEntities.add(canalEntity);
		}
		intakeEntities=new ArrayList<IntakeEntity>();
		for(int i=0;i<project.getEntities().size();i++) {
			Entity entity=project.getEntities().get(i);
			if(entity.getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.INTAKE)) {
				intakeEntities.add((IntakeEntity)entity);
			}
		}
		reservoirEntities=new ArrayList<ReservoirEntity>();
		for(int i=0;i<project.getEntities().size();i++) {
			if(project.getEntities().get(i).getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.RESERVOIR)) {
				ReservoirEntity entity=(ReservoirEntity)project.getEntities().get(i);
				reservoirEntities.add(entity);
			}
		}
		pumpEntities=new ArrayList<PumpEntity>();
		for(int i=0;i<project.getEntities().size();i++) {
			if(project.getEntities().get(i).getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.PUMP)) {
				PumpEntity pumpEntity=(PumpEntity)project.getEntities().get(i);
				pumpEntities.add(pumpEntity);
			}
		}
		gateEntities=new ArrayList<GateEntity>();
		for(int i=0;i<project.getEntities().size();i++) {
			if(project.getEntities().get(i).getEntityStat().getEntityTypeEnum().equals(EntityTypeEnum.GATE)) {
				GateEntity gateEntity=(GateEntity)project.getEntities().get(i);
				gateEntities.add(gateEntity);
			}
		}
		
		
	}

	public void clean() {
		for (int i = 0; i < project.getEntities().size(); i++) {
			Entity entity = project.getEntities().get(i);
			entity.clean();
		}
	}

	public void prepare() {

		for (String canalid:project.getBoundary().getCanalboundary().keySet()) {
			CanalEntity canalEntity = (CanalEntity) project.seekEntityByIdByType(canalid, EntityTypeEnum.CANAL);
			Boundary.fillBoundary(canalEntity, project.getBoundary().getCanalboundary().get(canalid));
		}

		for(String pumpid:project.getBoundary().getPumpboundary().keySet()) {
			PumpEntity pumpEntity=(PumpEntity)project.seekEntityByIdByType(pumpid, EntityTypeEnum.PUMP);
			Boundary.fillBoundary(pumpEntity, project.getBoundary().getPumpboundary().get(pumpid));
		}

		for(String reservoirid:project.getBoundary().getReservoirboundary().keySet()) {
			ReservoirEntity reservoirEntity=(ReservoirEntity)project.seekEntityByIdByType(reservoirid, EntityTypeEnum.RESERVOIR);
			Boundary.fillBoundary(reservoirEntity, project.getBoundary().getReservoirboundary().get(reservoirid));
		}

		for(String gateid:project.getBoundary().getGateboundary().keySet()) {
			GateEntity gateEntity=(GateEntity)project.seekEntityByIdByType(gateid, EntityTypeEnum.GATE);
			Boundary.fillBoundary(gateEntity, project.getBoundary().getGateboundary().get(gateid));
		}

		for(String intakeid:project.getBoundary().getIntakeboundary().keySet()) {
			IntakeEntity intakeEntity=(IntakeEntity)project.seekEntityByIdByType(intakeid, EntityTypeEnum.INTAKE);
			Boundary.fillBoundary(intakeEntity, project.getBoundary().getIntakeboundary().get(intakeid));
		}
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


	public Project getProject() {
		return project;
	}


	public void setProject(Project project) {
		this.project = project;
	}



}
