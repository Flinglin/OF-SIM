package com.research.frsim.adapter.wdp.zzp.bean.entity.canal;

import com.research.frsim.adapter.wdp.util.curve.TribleCurve;
import com.research.frsim.adapter.wdp.zzp.bean.entity.catchment.CatchmentEntity;
import com.research.frsim.adapter.wdp.zzp.bean.entity.entity.Entity;
import com.research.frsim.adapter.wdp.zzp.bean.entity.entity.EntityStat;
import com.research.frsim.adapter.wdp.zzp.bean.entity.intake.IntakeEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CanalEntityStat extends EntityStat {

	private String uuid;
	

	private double losspara;

	private List<IntakeEntity> intakeEntitys;

	private List<CatchmentEntity> catchmentEntitys;
	

	private Entity upstreammodel;
	

	private Entity downstreammodel;

	private TribleCurve zqwcurve;

	private TribleCurve qzzcurve;
	
	public CanalEntityStat() {
		uuid = UUID.randomUUID().toString().replaceAll("-", "");
		intakeEntitys = new ArrayList<IntakeEntity>();
		catchmentEntitys = new ArrayList<CatchmentEntity>();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public double getLosspara() {
		return losspara;
	}

	public void setLosspara(double losspara) {
		this.losspara = losspara;
	}

	public List<IntakeEntity> getIntakeEntitys() {
		return intakeEntitys;
	}

	public void setIntakeEntitys(List<IntakeEntity> intakeEntitys) {
		this.intakeEntitys = intakeEntitys;
	}

	public List<CatchmentEntity> getCatchmentEntitys() {
		return catchmentEntitys;
	}

	public void setCatchmentEntitys(List<CatchmentEntity> catchmentEntitys) {
		this.catchmentEntitys = catchmentEntitys;
	}

	public Entity getUpstreammodel() {
		return upstreammodel;
	}

	public void setUpstreammodel(Entity upstreammodel) {
		this.upstreammodel = upstreammodel;
	}

	public Entity getDownstreammodel() {
		return downstreammodel;
	}

	public void setDownstreammodel(Entity downstreammodel) {
		this.downstreammodel = downstreammodel;
	}

	public TribleCurve getZqwcurve() {
		return zqwcurve;
	}

	public void setZqwcurve(TribleCurve zqwcurve) {
		this.zqwcurve = zqwcurve;
	}

	public TribleCurve getQzzcurve() {
		return qzzcurve;
	}

	public void setQzzcurve(TribleCurve qzzcurve) {
		this.qzzcurve = qzzcurve;
	}

}
