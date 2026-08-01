package com.research.frsim.adapter.wdp.zzp.bean.entity.reservoir;

import com.research.frsim.adapter.wdp.zzp.bean.Project;
import com.research.frsim.adapter.wdp.zzp.bean.entity.entity.Entity;

public class ReservoirEntity extends Entity {

	private ReservoirEntityStat entityStat;

	private ReservoirEntityDynm entityDynm;
	
	public ReservoirEntity(Project project) {
		entityStat = new ReservoirEntityStat();
		entityDynm = new ReservoirEntityDynm(project);
	}
	
	
}
