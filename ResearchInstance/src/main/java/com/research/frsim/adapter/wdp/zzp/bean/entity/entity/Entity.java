package com.research.frsim.adapter.wdp.zzp.bean.entity.entity;

public class Entity {

	private EntityStat entityStat;

	private EntityDynm entityDynm;

	public Entity() {

		entityStat = new EntityStat();
		entityDynm = new EntityDynm();
	}

	@Override
	public String toString() {

		return entityStat.getId()+"-"+entityStat.getName();
	}

	public EntityStat getStat() {
		return entityStat;
	}

	public void setEntityStat(EntityStat entityStat) {
		this.entityStat = entityStat;
	}

	public EntityDynm getDynm() {
		return entityDynm;
	}

	public void setEntityDynm(EntityDynm entityDynm) {
		this.entityDynm = entityDynm;
	}

}
