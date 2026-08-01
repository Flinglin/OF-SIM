package com.research.frsim.adapter.wdp.zzp.bean.entity.pump;

import com.research.frsim.adapter.wdp.zzp.bean.Project;

public class PumpEntity {
	

	private PumpEntityDynm entityDynm;
	

	private PumpEntityStat entityStat;
	
	public PumpEntity(Project project) {
		entityDynm = new PumpEntityDynm(project);
		entityStat = new PumpEntityStat();
	}

	public PumpEntityDynm getEntityDynm() {
		return entityDynm;
	}

	public void setEntityDynm(PumpEntityDynm entityDynm) {
		this.entityDynm = entityDynm;
	}

	public PumpEntityStat getEntityStat() {
		return entityStat;
	}

	public void setEntityStat(PumpEntityStat entityStat) {
		this.entityStat = entityStat;
	}

}
