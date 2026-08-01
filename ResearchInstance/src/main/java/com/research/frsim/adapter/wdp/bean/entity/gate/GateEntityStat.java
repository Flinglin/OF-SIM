package com.research.frsim.adapter.wdp.bean.entity.gate;

import com.research.frsim.adapter.wdp.bean.entity.Function;
import com.research.frsim.adapter.wdp.bean.entity.entity.EntityStat;
import com.research.frsim.adapter.wdp.util.NumberUtil;

public class GateEntityStat extends EntityStat{

	private double btmel;

	private double designflow;

	private double enlargeflow;

	private double designlevel;

	private double enlargelevel;

	private double alertlevel;

	private double alarmlevel;

	private double warninglevel;

	private double width;

	private double switchtime;

	private double[] opennessspace;
	

	private Function gateLine;
	

	private double Qaddmax;

	private double Qreducemax;

	
	public double getDesignflow() {
		return designflow;
	}

	public void setDesignflow(double designflow) {
		this.designflow = designflow;
	}

	public double getEnlargeflow() {
		return enlargeflow;
	}

	public void setEnlargeflow(double enlargeflow) {
		this.enlargeflow = enlargeflow;
	}

	public double getDesignlevel() {
		return designlevel;
	}

	public void setDesignlevel(double designlevel) {
		this.designlevel = designlevel;
	}

	public double getEnlargelevel() {
		return enlargelevel;
	}

	public void setEnlargelevel(double enlargelevel) {
		this.enlargelevel = enlargelevel;
	}

	public double getWarninglevel() {
		return warninglevel;
	}

	public void setWarninglevel(double warninglevel) {
		this.warninglevel = warninglevel;
	}

	public Function getGateLine() {
		return gateLine;
	}

	public void setGateLine(Function gateLine) {
		this.gateLine = gateLine;
	}

	public double getAlertlevel() {
		return alertlevel;
	}

	public void setAlertlevel(double alertlevel) {
		this.alertlevel = alertlevel;
	}

	public double getAlarmlevel() {
		return alarmlevel;
	}

	public void setAlarmlevel(double alarmlevel) {
		this.alarmlevel = alarmlevel;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getBtmel() {
		return btmel;
	}

	public void setBtmel(double btmel) {
		this.btmel = btmel;
	}

	public double getSwitchtime() {
		return switchtime;
	}

	public void setSwitchtime(double switchtime) {
		this.switchtime = switchtime;
	}

	public double[] getOpennessspace() {
		return opennessspace;
	}

	public void setOpennessspace(double[] opennessspace) {
		this.opennessspace = opennessspace;
	}

	public double getQaddmax() {
		return Qaddmax;
	}

	public void setQaddmax(double qaddmax) {
		Qaddmax = qaddmax;
	}

	public double getQreducemax() {
		return Qreducemax;
	}

	public void setQreducemax(double qreducemax) {
		Qreducemax = qreducemax;
	}
	
}
