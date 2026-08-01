package com.research.frsim.adapter.wdp.bean.entity.boundary;

import java.util.LinkedHashMap;
import java.util.Map;

import com.research.frsim.adapter.wdp.bean.entity.canal.CanalEntity;
import com.research.frsim.adapter.wdp.bean.entity.gate.GateEntity;
import com.research.frsim.adapter.wdp.bean.entity.intake.IntakeEntity;
import com.research.frsim.adapter.wdp.bean.entity.pump.PumpEntity;
import com.research.frsim.adapter.wdp.bean.entity.reservoir.ReservoirEntity;
import com.research.frsim.adapter.wdp.enumerate.BoundaryTypeEnum;

public class Boundary {

	private Map<String, Map<String,double[]>> gateboundary;

	private Map<String, Map<String,double[]>> intakeboundary;

	private Map<String, Map<String,double[]>> canalboundary;

	private Map<String, Map<String, double[]>> pumpboundary;

	private Map<String, Map<String, double[]>> reservoirboundary;

	public Boundary() {
		gateboundary = new LinkedHashMap<String, Map<String,double[]>>();
		intakeboundary = new LinkedHashMap<String, Map<String,double[]>>();
		canalboundary = new LinkedHashMap<String, Map<String,double[]>>();
		pumpboundary=new LinkedHashMap<String, Map<String,double[]>>();
		reservoirboundary=new LinkedHashMap<String, Map<String,double[]>>();
	}

	public static void fillBoundary(GateEntity gateEntity,Map<String,double[]> boundarys) {
		
		for (String boundtype:boundarys.keySet()) {
			double[] value = boundarys.get(boundtype).clone();
			if (boundtype.equals(BoundaryTypeEnum.GATE_Q.getType())) {
				gateEntity.setAvgflow(value);
			} else if (boundtype.equals(BoundaryTypeEnum.GATE_HUP.getType())) {
				gateEntity.setUplevel(value);
			} else if (boundtype.equals(BoundaryTypeEnum.GATE_HDOWN.getType())) {
				gateEntity.setDownlevel(value);
			} else if (boundtype.equals(BoundaryTypeEnum.GATE_OPEN.getType())) {
				gateEntity.setOpenness(value);
			} else if(boundtype.equals(BoundaryTypeEnum.GATE_MAXFLOW.getType())) {
				gateEntity.setMaxflow(value);
			} else if(boundtype.equals(BoundaryTypeEnum.GATE_MINFLOW.getType())) {
				gateEntity.setMinflow(value);
			} else if(boundtype.equals(BoundaryTypeEnum.GATE_UPLEVEL.getType())) {
				gateEntity.setUplevel(value);
			} else if(boundtype.equals(BoundaryTypeEnum.GATE_DOWNLEVEL.getType())) {
				gateEntity.setDownlevel(value);
			}
		}
	}
	public static void fillBoundary(IntakeEntity intakeEntity, Map<String,double[]> boundarys) {
		for (String boundtype:boundarys.keySet()) {
			double[] value = boundarys.get(boundtype).clone();
			if (boundtype.equals(BoundaryTypeEnum.INTAKE_DEMANDQ.getType())) {
				intakeEntity.setDemandflow(value);
			} else if (boundtype.equals(BoundaryTypeEnum.INTAKE_INTAKEQ.getType())) {
				intakeEntity.setIntakeflow(value);
			} else if (boundtype.equals(BoundaryTypeEnum.INTAKE_MAXFLOW.getType())) {
				intakeEntity.setMaxflow(value);
			} else if(boundtype.equals(BoundaryTypeEnum.INTAKE_MINFLOW.getType())) {
				intakeEntity.setMinflow(value);
			} else if (boundtype.equals(BoundaryTypeEnum.INTAKE_MINFLOWV.getType())) {
				intakeEntity.setMinvolume(value);
			} else if(boundtype.equals(BoundaryTypeEnum.INTAKE_MAXFLOWV.getType())) {
				intakeEntity.setMaxvolume(value);
			}
		}
	}
	public static void fillBoundary(CanalEntity canalEntity, Map<String,double[]> boundarys) {
		for (String boundtype:boundarys.keySet()) {
			double[] value = boundarys.get(boundtype).clone();
			if (boundtype.equals(BoundaryTypeEnum.CANAL_STORAGE.getType())) {
				canalEntity.setStorage(value);
			}else if (boundtype.equals(BoundaryTypeEnum.CANAL_INFLOW.getType())) {
				canalEntity.setInflow(value);
			}
		}
	}
	public static void fillBoundary(PumpEntity pumpEntity,Map<String, double[]> boundarys) {
		for (String boundtype:boundarys.keySet()) {
			double[] value = boundarys.get(boundtype).clone();
			if (boundtype.equals(BoundaryTypeEnum.PUMP_FLOWQ.getType())) {
				pumpEntity.setAvgflow(value);
			}else if (boundtype.equals(BoundaryTypeEnum.PUMP_UPLEVEL.getType())) {
				pumpEntity.setWlevelUp(value);
			}else if(boundtype.equals(BoundaryTypeEnum.PUMP_DOWNLEVEL.getType())) {
				pumpEntity.setWleveldown(value);
			}else if(boundtype.equals(BoundaryTypeEnum.PUMP_MAXFLOW.getType())) {
				pumpEntity.setMaxflow(value);
			}else if(boundtype.equals(BoundaryTypeEnum.PUMP_MINFLOW.getType())) {
				pumpEntity.setMinflow(value);
			}else if(boundtype.equals(BoundaryTypeEnum.PUMP_MAXVOLUME.getType())) {
				pumpEntity.setMaxvolume(value);
			}else if(boundtype.equals(BoundaryTypeEnum.PUMP_MINVOLUME.getType())) {
				pumpEntity.setMinvolume(value);
			}
		}
	}
	public static void fillBoundary(ReservoirEntity reservoirEntity,Map<String, double[]> boundarys) {
		
		for(String boundtype:boundarys.keySet()) {
			double[] value=boundarys.get(boundtype).clone();
			if(boundtype.equals(BoundaryTypeEnum.RESERVOIR_INFLOWQ.getType())) {
				reservoirEntity.setSectionInflow(value);
			}else if(boundtype.equals(BoundaryTypeEnum.RESERVOIR_MAXINFLOW.getType())) {
				reservoirEntity.setInmaxflow(value);
			}else if(boundtype.equals(BoundaryTypeEnum.RESERVOIR_MININFLOW.getType())) {
				reservoirEntity.setInminflow(value);
			}else if(boundtype.equals(BoundaryTypeEnum.RESERVOIR_MAXOUTFLOW.getType())) {
				reservoirEntity.setOutmaxflow(value);
			}else if(boundtype.equals(BoundaryTypeEnum.RESERVOIR_MINOUTFLOW.getType())) {
				reservoirEntity.setOutminflow(value);
			}else if(boundtype.equals(BoundaryTypeEnum.RESERVOIR_WATERLEVEL.getType())) {
				reservoirEntity.setWaterlevel(value);
			}else if(boundtype.equals(BoundaryTypeEnum.RESERVOIR_MAXLEVEL.getType())) {
				reservoirEntity.setMaxLevel(value);
			}else if(boundtype.equals(BoundaryTypeEnum.RESERVOIR_MINLEVEL.getType())) {
				reservoirEntity.setMinLevel(value);
			}
		}
		
	}

	public Map<String, Map<String, double[]>> getGateboundary() {
		return gateboundary;
	}

	public void setGateboundary(Map<String, Map<String, double[]>> gateboundary) {
		this.gateboundary = gateboundary;
	}

	public Map<String, Map<String, double[]>> getIntakeboundary() {
		return intakeboundary;
	}

	public void setIntakeboundary(Map<String, Map<String, double[]>> intakeboundary) {
		this.intakeboundary = intakeboundary;
	}

	public Map<String, Map<String, double[]>> getCanalboundary() {
		return canalboundary;
	}

	public void setCanalboundary(Map<String, Map<String, double[]>> canalboundary) {
		this.canalboundary = canalboundary;
	}

	public Map<String, Map<String, double[]>> getPumpboundary() {
		return pumpboundary;
	}

	public void setPumpboundary(Map<String, Map<String, double[]>> pumpboundary) {
		this.pumpboundary = pumpboundary;
	}

	public Map<String, Map<String, double[]>> getReservoirboundary() {
		return reservoirboundary;
	}

	public void setReservoirboundary(Map<String, Map<String, double[]>> reservoirboundary) {
		this.reservoirboundary = reservoirboundary;
	}

}
