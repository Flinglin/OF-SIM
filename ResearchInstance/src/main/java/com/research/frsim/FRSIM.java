package com.research.frsim;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.research.frsim.adapter.wdp.algorithm.DE;
import com.research.frsim.adapter.wdp.bean.Project;
import com.research.frsim.adapter.wdp.bean.entity.intake.IntakeEntity;
import com.research.frsim.adapter.wdp.bean.entity.pump.PumpEntity;
import com.research.frsim.adapter.wdp.bean.entity.reservoir.ReservoirEntity;
import com.research.frsim.adapter.wdp.enumerate.ProjectModeEnum;
import com.research.frsim.adapter.wdp.enumerate.TimeScaleEnum;
import com.research.frsim.adapter.wdp.model.longterm.frsim.opt.FRForwardOptProblem;
import com.research.frsim.adapter.wdp.model.longterm.frsim.opt.FRReverseOptProblem;
import com.research.frsim.adapter.wdp.model.longterm.frsim.sim.FRForwardSimulationModel;
import com.research.frsim.adapter.wdp.model.longterm.frsim.sim.FRReverseSimulationModel;
import com.research.frsim.adapter.wdp.model.longterm.frsim.util.FRProjectUtil;
import com.research.frsim.adapter.wdp.util.NumberUtil;
import com.research.frsim.algorithm.opt.intelligence.IntelligenceAlgorithm;

import com.research.frsim.util.ExcelTool;

public class FRSIM {

	public static void main(String[] args) throws IOException, ParseException {

		String inputfilepath = "ResearchInstance/src/main/resources/frsim_input.xlsx";
		Object[][] projectInfo = ExcelTool.read07Excel(inputfilepath, "project");
		Project project = new Project();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sttm = projectInfo[0][0].toString();
		String edtm = projectInfo[1][0].toString();
		double avgflow=NumberUtil.objectToDouble(projectInfo[3][0]);
		project.setStarttime(dateFormat.parse(sttm));
		project.setEndtime(dateFormat.parse(edtm));
		project.setTimeStep(NumberUtil.objectToInt(projectInfo[2][0]));
		project.constructTimeListLongterm();
		project.setTimeScaleEnum(TimeScaleEnum.LONGTERM);
		project.setProjectModeEnum(ProjectModeEnum.OPTIMIZATION);
		String excelpath = "ResearchInstance/src/main/resources/frsim_parameter.xlsx";
		boolean pararslt = Project.readParafile(project, excelpath);
		if (pararslt == false) {
			return;
		}

		Map<String, List<double[]>> lellimit= FRProjectUtil.readlellimit(inputfilepath, project);
		FRProjectUtil.readinputproject(project, inputfilepath,lellimit);

		ReservoirEntity reservoirEntityk=(ReservoirEntity)project.seekEntityByName("h_reservoir");
		double[] sumavgflow=reservoirEntityk.getOutflow();
		Arrays.fill(sumavgflow, avgflow);
		reservoirEntityk.setOutflow(sumavgflow);

		FRReverseSimulationModel model=new FRReverseSimulationModel(project);
		model.prepare();
		FRReverseOptProblem optProblem=new FRReverseOptProblem(model,lellimit);

		IntelligenceAlgorithm algorithm=new DE(optProblem,0);
		algorithm.setIterations(1000);
		algorithm.execute();
 		optProblem.calculateFitness(algorithm.getSolutionBest().getSolution().get(0));

		PumpEntity entity=(PumpEntity)project.seekEntityByName("z_pump");
		PumpEntity entity2=(PumpEntity)project.seekEntityByName("f_pump");
		ReservoirEntity HHreservoirEntity=(ReservoirEntity)project.seekEntityByName("h_reservoir");
		double[] HHwasterwater=HHreservoirEntity.getWasteWater().clone();
		Map<String, List<Integer>> codename=new LinkedHashMap<String, List<Integer>>();
		for(int i=0;i<entity.getAvgflow().length;i++) {
			double zy=entity.getAvgflow()[i];
			double fhj=entity2.getAvgflow()[i];
			if(zy>100&&fhj>100) {
				if(codename.containsKey("same")) {
					codename.get("same").add(i);
				}else {
					List<Integer> tempdata=new ArrayList<Integer>();
					tempdata.add(i);
					codename.put("same", tempdata);
				}
			}else if(zy>100) {
				if(codename.containsKey("z_pump")) {
					codename.get("z_pump").add(i);
				}else {
					List<Integer> tempdata=new ArrayList<Integer>();
					tempdata.add(i);
					codename.put("z_pump", tempdata);
				}
			}else if(fhj>100) {
				if(codename.containsKey("f_pump")) {
					codename.get("f_pump").add(i);
				}else {
					List<Integer> tempdata=new ArrayList<Integer>();
					tempdata.add(i);
					codename.put("f_pump", tempdata);
				}
			}else {
				continue;
			}
		}

		Map<String, List<double[]>> datacache=new LinkedHashMap<String, List<double[]>>();
		PumpEntity FHJpumpEntity=(PumpEntity)project.seekEntityByName("f_pump");
		PumpEntity zYPumpEntity=(PumpEntity)project.seekEntityByName("z_pump");
		datacache.put(FHJpumpEntity.getEntityStat().getName(), Arrays.asList(FHJpumpEntity.getAvgflow().clone()));
		datacache.put(zYPumpEntity.getEntityStat().getName(), Arrays.asList(zYPumpEntity.getAvgflow().clone()));

		for(int i=0;i<optProblem.getIntakeEntities().size();i++) {
			IntakeEntity intakeEntity=optProblem.getIntakeEntities().get(i);
			datacache.put(intakeEntity.getEntityStat().getName(), Arrays.asList(intakeEntity.getIntakeflow().clone()));
		}

		for(int i=0;i<optProblem.getReservoirEntities().size();i++) {
			ReservoirEntity reservoirEntity=optProblem.getReservoirEntities().get(i);
			datacache.put(reservoirEntity.getEntityStat().getName(), Arrays.asList(reservoirEntity.getInflow().clone(),reservoirEntity.getOutflow().clone(),reservoirEntity.getWaterlevel().clone()));
		}

		if(!codename.isEmpty()) {
			FRForwardSimulationModel model2=new FRForwardSimulationModel(project);
			FRForwardOptProblem optProblem2=new FRForwardOptProblem(model2, codename,HHwasterwater);
			optProblem2.setDatamap(datacache);
			IntelligenceAlgorithm algorithm2=new DE(optProblem2,1);
			algorithm2.setIterations(1000);
			algorithm2.execute();
		}
	}
}
