package com.research.frsim.adapter.wdp.zzp.bean.entity.canal;

import com.research.frsim.adapter.wdp.zzp.bean.Project;
import com.research.frsim.adapter.wdp.zzp.bean.entity.entity.Entity;


public class CanalEntity extends Entity {

	private CanalEntityStat entityStat;

	private CanalEntityDynm entityDynm;
	
	public CanalEntity(Project project) {
		entityStat = new CanalEntityStat();
		entityDynm = new CanalEntityDynm(project);
	}

	public CanalEntityStat getStat() {
		return entityStat;
	}

	public void setEntityStat(CanalEntityStat entityStat) {
		this.entityStat = entityStat;
	}

	public CanalEntityDynm getDynm() {
		return entityDynm;
	}

	public void setEntityDynm(CanalEntityDynm entityDynm) {
		this.entityDynm = entityDynm;
	}


}
