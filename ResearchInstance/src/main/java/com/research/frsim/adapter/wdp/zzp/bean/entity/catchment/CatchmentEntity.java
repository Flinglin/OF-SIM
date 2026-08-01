package com.research.frsim.adapter.wdp.zzp.bean.entity.catchment;

import com.research.frsim.adapter.wdp.zzp.bean.Project;
import com.research.frsim.adapter.wdp.zzp.bean.entity.entity.Entity;

public class CatchmentEntity extends Entity {

	private CatchmentEntityStat entityStat;

	private CatchmentEntityDynm entityDynm;
	
	public CatchmentEntity(Project project) {
		entityStat = new CatchmentEntityStat();
		entityDynm = new CatchmentEntityDynm(project);
	}

	public CatchmentEntityStat getStat() {
		return entityStat;
	}

	public void setEntityStat(CatchmentEntityStat entityStat) {
		this.entityStat = entityStat;
	}

	public CatchmentEntityDynm getDynm() {
		return entityDynm;
	}

	public void setEntityDynm(CatchmentEntityDynm entityDynm) {
		this.entityDynm = entityDynm;
	}

}
