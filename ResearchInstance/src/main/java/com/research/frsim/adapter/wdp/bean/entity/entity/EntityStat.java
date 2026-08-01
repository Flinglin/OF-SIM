package com.research.frsim.adapter.wdp.bean.entity.entity;

import java.util.ArrayList;
import java.util.List;

import com.research.frsim.adapter.wdp.bean.entity.canal.CanalEntity;
import com.research.frsim.adapter.wdp.enumerate.EntityTypeEnum;
import com.research.frsim.adapter.wdp.enumerate.SectTypeEnum;

public class EntityStat {
	

	private String name;

	private String id;
	

	private double distance;
	

	private EntityTypeEnum entityTypeEnum;

	private SectTypeEnum sectTypeEnum;

	private List<CanalEntity> upcanalEntitys;

	private List<CanalEntity> downcanalEntitys;

	public EntityStat() {
		upcanalEntitys = new ArrayList<CanalEntity>();
		downcanalEntitys = new ArrayList<CanalEntity>();	
	}
	
	@Override
	public String toString() {
		return id+"-"+name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public EntityTypeEnum getEntityTypeEnum() {
		return entityTypeEnum;
	}

	public void setEntityTypeEnum(EntityTypeEnum entityTypeEnum) {
		this.entityTypeEnum = entityTypeEnum;
	}

	public List<CanalEntity> getUpcanalEntitys() {
		return upcanalEntitys;
	}

	public void setUpcanalEntitys(List<CanalEntity> upcanalEntitys) {
		this.upcanalEntitys = upcanalEntitys;
	}

	public List<CanalEntity> getDowncanalEntitys() {
		return downcanalEntitys;
	}

	public void setDowncanalEntitys(List<CanalEntity> downcanalEntitys) {
		this.downcanalEntitys = downcanalEntitys;
	}

	public SectTypeEnum getSectTypeEnum() {
		return sectTypeEnum;
	}

	public void setSectTypeEnum(SectTypeEnum sectTypeEnum) {
		this.sectTypeEnum = sectTypeEnum;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

}
