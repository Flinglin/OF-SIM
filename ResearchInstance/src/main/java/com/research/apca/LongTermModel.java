package com.research.apca;

import com.research.apca.algorithm.optimize.enums.OptimizeType;
import com.research.apca.algorithm.optimize.intelligence.DE;
import com.research.apca.core.enums.TimeScaleEnum;
import com.research.utils.data.ExcelTool;
import com.research.utils.numpy.NumberUtil;

import java.io.IOException;
import java.util.ArrayList;

public class LongTermModel {

    public static void main(String[] args) throws IOException {
        String path = "ResearchInstance/src/main/resources/apca_input_data.xlsx";
        Object[][] param = ExcelTool.readXlsxExcel(path, "project");
        ResearchProject project = ResearchProject.builder()
                .startTime(NumberUtil.objectToLocalDateTime(param[0][0]))
                .endTime(NumberUtil.objectToLocalDateTime(param[0][1]))
                .timeStep(NumberUtil.objectToInt(param[0][2]))
                .timeScaleEnum(TimeScaleEnum.LONGTERM)
                .build();
        project.buildLongTimeSequence();
        project.buildProjectParam(project, path);
        ResearchOptimizationProblem researchProblem = new ResearchOptimizationProblem(
                2,
                4 * 36,
                new ArrayList<>() {{
                    add(OptimizeType.MINIMUM);
                    add(OptimizeType.MINIMUM);
                }},
                new ResearchReverseSimulation(project),
                project);
        DE de = DE.builder()
                .iterations(1000)
                .populationSize(200)
                .problem(researchProblem)
                .build();
        de.execute();
    }
}
