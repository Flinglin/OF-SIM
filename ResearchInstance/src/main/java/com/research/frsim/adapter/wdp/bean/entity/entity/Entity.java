package com.research.frsim.adapter.wdp.bean.entity.entity;

public class Entity {
	

	protected EntityStat entityStat;

	public void clean() {}
	
	public Entity() {
		entityStat = new EntityStat();
	}
	
	@Override
	public String toString() {
		return entityStat.getId()+"-"+entityStat.getName();
	}

	public EntityStat getEntityStat() {
		return entityStat;
	}

	public void setEntityStat(EntityStat entityStat) {
		this.entityStat = entityStat;
	}

}
