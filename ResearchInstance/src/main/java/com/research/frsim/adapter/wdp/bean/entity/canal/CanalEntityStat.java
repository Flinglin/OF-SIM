package com.research.frsim.adapter.wdp.bean.entity.canal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.research.frsim.adapter.wdp.bean.entity.Function;
import com.research.frsim.adapter.wdp.bean.entity.catchment.CatchmentEntity;
import com.research.frsim.adapter.wdp.bean.entity.entity.Entity;
import com.research.frsim.adapter.wdp.bean.entity.entity.EntityStat;
import com.research.frsim.adapter.wdp.bean.entity.intake.IntakeEntity;
import com.research.frsim.adapter.wdp.util.curve.TribleCurve;

public class CanalEntityStat extends EntityStat{
	

	private String uuid;
	

	private double losspara;
	

	private List<IntakeEntity> intakeEntitys;
	

	private List<CatchmentEntity> catchmentEntitys;

	private Entity upstreammodel;

	private Entity downstreammodel;

	private TribleCurve qzwcurve;
	

	private TribleCurve zqwcurve;
	

	private TribleCurve qzdeltacurve;

	private TribleCurve qzzcurve;
	

	private Function level_storageline;
	

	private Function stroage_levelLine;
	

	private double[][] Df_Dl_UlLine;

	private double Waterlosscoefficient;

	private int ICriverdispatch;
	
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

	public TribleCurve getQzwcurve() {
		return qzwcurve;
	}

	public void setQzwcurve(TribleCurve zqwcurve) {
		this.qzwcurve = zqwcurve;
	}

	public TribleCurve getQzdeltacurve() {
		return qzdeltacurve;
	}

	public void setQzdeltacurve(TribleCurve qzdeltacurve) {
		this.qzdeltacurve = qzdeltacurve;
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

	public Function getLevel_storageline() {
		return level_storageline;
	}

	public void setLevel_storageline(Function level_storageline) {
		this.level_storageline = level_storageline;
	}

	public Function getStroage_levelLine() {
		return stroage_levelLine;
	}

	public void setStroage_levelLine(Function stroage_levelLine) {
		this.stroage_levelLine = stroage_levelLine;
	}

	public double[][] getDf_Dl_UlLine() {
		return Df_Dl_UlLine;
	}

	public void setDf_Dl_UlLine(double[][] df_Dl_UlLine) {
		Df_Dl_UlLine = df_Dl_UlLine;
	}

	public double getWaterlosscoefficient() {
		return Waterlosscoefficient;
	}

	public void setWaterlosscoefficient(double waterlosscoefficient) {
		Waterlosscoefficient = waterlosscoefficient;
	}

	public int getICriverdispatch() {
		return ICriverdispatch;
	}

	public void setICriverdispatch(int iCriverdispatch) {
		ICriverdispatch = iCriverdispatch;
	}

}
