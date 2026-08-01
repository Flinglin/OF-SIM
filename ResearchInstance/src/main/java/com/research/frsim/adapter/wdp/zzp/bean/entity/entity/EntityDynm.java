package com.research.frsim.adapter.wdp.zzp.bean.entity.entity;

import java.util.HashMap;
import java.util.Map;


public class EntityDynm {
	

	protected Map<Integer,double[]> boundary;

	public void prepare() {}
	

	public void clean() {}

	
	public EntityDynm() {
		boundary = new HashMap<Integer,double[]>();
	}

	public Map<Integer,double[]> getBoundary() {
		return boundary;
	}

	public void setBoundary(Map<Integer,double[]> boundary) {
		this.boundary = boundary;
	}

}
