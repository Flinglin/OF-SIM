package com.research.frsim.adapter.wdp.bean.entity.pump;

import java.util.List;

import com.research.frsim.adapter.wdp.bean.entity.Function;
import com.research.frsim.adapter.wdp.bean.entity.entity.EntityStat;
import com.research.frsim.adapter.wdp.util.curve.TribleCurve;

public class PumpEntityStat extends EntityStat{

	private double mindownlevel;

	private double maxdownlevel;

	private double minuplevel;

	private double maxuplevel;

	private double watervolume;

	private double[] pumpvariable;

	private int crewnumbers;

	private double[] BLanglerange;

	private int Adstep;

	private double[] flowfeasibleregion;

	private double elecPrice;

	private double copfee;

	private double unitenconsume;

	private Function lift_angle_flowline;

	private Function lift_angle_efficiencyline;

	private Function flow_lift_angleline;

	private List<double[][]> SearchF_L_ALine;

	private TribleCurve consumeCurve;

	private double[] Timeofdaytariff;
	

	public double getMindownlevel() {
		return mindownlevel;
	}

	public void setMindownlevel(double mindownlevel) {
		this.mindownlevel = mindownlevel;
	}

	public double getMaxdownlevel() {
		return maxdownlevel;
	}

	public void setMaxdownlevel(double maxdownlevel) {
		this.maxdownlevel = maxdownlevel;
	}

	public double getMinuplevel() {
		return minuplevel;
	}

	public void setMinuplevel(double minuplevel) {
		this.minuplevel = minuplevel;
	}

	public double getMaxuplevel() {
		return maxuplevel;
	}

	public void setMaxuplevel(double maxuplevel) {
		this.maxuplevel = maxuplevel;
	}

	public double[] getPumpvariable() {
		return pumpvariable;
	}

	public void setPumpvariable(double[] pumpvariable) {
		this.pumpvariable = pumpvariable;
	}

	public int getCrewnumbers() {
		return crewnumbers;
	}

	public void setCrewnumbers(int crewnumbers) {
		this.crewnumbers = crewnumbers;
	}

	public double[] getBLanglerange() {
		return BLanglerange;
	}

	public void setBLanglerange(double[] bLanglerange) {
		BLanglerange = bLanglerange;
	}

	public int getAdstep() {
		return Adstep;
	}

	public void setAdstep(int adstep) {
		Adstep = adstep;
	}

	public double[] getFlowfeasibleregion() {
		return flowfeasibleregion;
	}

	public void setFlowfeasibleregion(double[] flowfeasibleregion) {
		this.flowfeasibleregion = flowfeasibleregion;
	}

	public double getElecPrice() {
		return elecPrice;
	}

	public void setElecPrice(double elecPrice) {
		this.elecPrice = elecPrice;
	}

	public Function getLift_angle_flowline() {
		return lift_angle_flowline;
	}

	public void setLift_angle_flowline(Function lift_angle_flowline) {
		this.lift_angle_flowline = lift_angle_flowline;
	}

	public Function getLift_angle_efficiencyline() {
		return lift_angle_efficiencyline;
	}

	public void setLift_angle_efficiencyline(Function lift_angle_efficiencyline) {
		this.lift_angle_efficiencyline = lift_angle_efficiencyline;
	}


	public double getCopfee() {
		return copfee;
	}

	public void setCopfee(double copfee) {
		this.copfee = copfee;
	}

	public double getWatervolume() {
		return watervolume;
	}

	public void setWatervolume(double watervolume) {
		this.watervolume = watervolume;
	}

	public Function getFlow_lift_angleline() {
		return flow_lift_angleline;
	}

	public void setFlow_lift_angleline(Function flow_lift_angleline) {
		this.flow_lift_angleline = flow_lift_angleline;
	}

	public List<double[][]> getSearchF_L_ALine() {
		return SearchF_L_ALine;
	}

	public void setSearchF_L_ALine(List<double[][]> searchF_L_ALine) {
		SearchF_L_ALine = searchF_L_ALine;
	}

	public double getUnitenconsume() {
		return unitenconsume;
	}

	public void setUnitenconsume(double unitenconsume) {
		this.unitenconsume = unitenconsume;
	}

	public TribleCurve getConsumeCurve() {
		return consumeCurve;
	}

	public void setConsumeCurve(TribleCurve consumeCurve) {
		this.consumeCurve = consumeCurve;
	}

	public double[] getTimeofdaytariff() {
		return Timeofdaytariff;
	}

	public void setTimeofdaytariff(double[] timeofdaytariff) {
		Timeofdaytariff = timeofdaytariff;
	}


	
	

}
