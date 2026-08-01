package com.research.frsim.adapter.wdp.bean.entity.reservoir;

import com.research.frsim.adapter.wdp.bean.entity.entity.Entity;
import com.research.frsim.adapter.wdp.bean.entity.entity.EntityStat;
import com.research.frsim.adapter.wdp.enumerate.FloodSimDirectEnum;
import com.research.frsim.core.util.DoubleCurve;


public class ReservoirEntityStat extends EntityStat{
	

	private double levelNormal;

	private double levelFloodLimiting;

	private double levelDead;

	private double damElevation;

	private double levelFloodCheck;

	private double levelFloodDesign;

	private double levelFloodControl;

	private double storageTotal;

	private double storageRegulating;

	private double storageControl;

	private double storageProtect;

	private double StorageDead;

	private double powerProductionMeanAnnual;

	private double powerInstalled;

	private DoubleCurve Wlevel_storageCurve;

	private DoubleCurve Storage_WlevelCurve;

	private double iniwaterlevel;

	private Entity wasteentity;

	private FloodSimDirectEnum directEnum;

	private double InLevelenlarge;

	private double INLevelAlert;

	private double[] Lelvar;
	

	public double getLevelNormal() {
		return levelNormal;
	}

	public void setLevelNormal(double levelNormal) {
		this.levelNormal = levelNormal;
	}

	public double getLevelFloodLimiting() {
		return levelFloodLimiting;
	}

	public void setLevelFloodLimiting(double levelFloodLimiting) {
		this.levelFloodLimiting = levelFloodLimiting;
	}

	public double getLevelDead() {
		return levelDead;
	}

	public void setLevelDead(double levelDead) {
		this.levelDead = levelDead;
	}

	public double getDamElevation() {
		return damElevation;
	}

	public void setDamElevation(double damElevation) {
		this.damElevation = damElevation;
	}

	public double getLevelFloodCheck() {
		return levelFloodCheck;
	}

	public void setLevelFloodCheck(double levelFloodCheck) {
		this.levelFloodCheck = levelFloodCheck;
	}

	public double getLevelFloodDesign() {
		return levelFloodDesign;
	}

	public void setLevelFloodDesign(double levelFloodDesign) {
		this.levelFloodDesign = levelFloodDesign;
	}

	public double getLevelFloodControl() {
		return levelFloodControl;
	}

	public void setLevelFloodControl(double levelFloodControl) {
		this.levelFloodControl = levelFloodControl;
	}

	public double getStorageTotal() {
		return storageTotal;
	}

	public void setStorageTotal(double storageTotal) {
		this.storageTotal = storageTotal;
	}

	public double getStorageRegulating() {
		return storageRegulating;
	}

	public void setStorageRegulating(double storageRegulating) {
		this.storageRegulating = storageRegulating;
	}

	public double getStorageControl() {
		return storageControl;
	}

	public void setStorageControl(double storageControl) {
		this.storageControl = storageControl;
	}

	public double getStorageProtect() {
		return storageProtect;
	}

	public void setStorageProtect(double storageProtect) {
		this.storageProtect = storageProtect;
	}

	public double getStorageDead() {
		return StorageDead;
	}

	public void setStorageDead(double storageDead) {
		StorageDead = storageDead;
	}

	public double getPowerProductionMeanAnnual() {
		return powerProductionMeanAnnual;
	}

	public void setPowerProductionMeanAnnual(double powerProductionMeanAnnual) {
		this.powerProductionMeanAnnual = powerProductionMeanAnnual;
	}

	public double getPowerInstalled() {
		return powerInstalled;
	}

	public void setPowerInstalled(double powerInstalled) {
		this.powerInstalled = powerInstalled;
	}

	public DoubleCurve getWlevel_storageCurve() {
		return Wlevel_storageCurve;
	}

	public void setWlevel_storageCurve(DoubleCurve wlevel_storageCurve) {
		Wlevel_storageCurve = wlevel_storageCurve;
	}

	public DoubleCurve getStorage_WlevelCurve() {
		return Storage_WlevelCurve;
	}

	public void setStorage_WlevelCurve(DoubleCurve storage_WlevelCurve) {
		Storage_WlevelCurve = storage_WlevelCurve;
	}

	public double getIniwaterlevel() {
		return iniwaterlevel;
	}

	public void setIniwaterlevel(double iniwaterlevel) {
		this.iniwaterlevel = iniwaterlevel;
	}


	public FloodSimDirectEnum getDirectEnum() {
		return directEnum;
	}

	public void setDirectEnum(FloodSimDirectEnum directEnum) {
		this.directEnum = directEnum;
	}

	public Entity getWasteentity() {
		return wasteentity;
	}

	public void setWasteentity(Entity wasteentity) {
		this.wasteentity = wasteentity;
	}

	public double getInLevelenlarge() {
		return InLevelenlarge;
	}

	public void setInLevelenlarge(double inLevelenlarge) {
		InLevelenlarge = inLevelenlarge;
	}

	public double getINLevelAlert() {
		return INLevelAlert;
	}

	public void setINLevelAlert(double iNLevelAlert) {
		INLevelAlert = iNLevelAlert;
	}

	public double[] getLelvar() {
		return Lelvar;
	}

	public void setLelvar(double[] lelvar) {
		Lelvar = lelvar;
	}

}
