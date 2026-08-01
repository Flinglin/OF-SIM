package com.research.frsim.adapter.wdp.zzp.bean.entity.intake;

import com.research.frsim.adapter.wdp.zzp.bean.Project;
import com.research.frsim.adapter.wdp.zzp.bean.entity.entity.Entity;

public class IntakeEntity extends Entity {
	

	private IntakeEntityStat entityStat;

	private IntakeEntityDynm entityDynm;

	public IntakeEntity(Project project) {
		entityStat = new IntakeEntityStat();
		entityDynm = new IntakeEntityDynm(project);
	}


	public IntakeEntityStat getStat() {
		return entityStat;
	}

	public void setEntityStat(IntakeEntityStat entityStat) {
		this.entityStat = entityStat;
	}

	public IntakeEntityDynm getDynm() {
		return entityDynm;
	}

	public void setEntityDynm(IntakeEntityDynm entityDynm) {
		this.entityDynm = entityDynm;
	}
	
	
	

}
