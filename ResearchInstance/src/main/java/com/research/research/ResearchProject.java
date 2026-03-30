package com.research.research;

import com.research.core.entity.canal.CanalEntity;
import com.research.core.entity.gate.GateEntity;
import com.research.core.entity.intake.IntakeEntity;
import com.research.core.entity.pump.PumpEntity;
import com.research.core.entity.reservoir.ReservoirEntity;
import com.research.core.project.Project;
import com.research.research.canal.ResearchCanalEntity;
import com.research.research.gate.ResearchGateEntity;
import com.research.research.intake.ResearchIntakeEntity;
import com.research.research.pump.ResearchPumpEntity;
import com.research.research.reservoir.ResearchReservoirEntity;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class ResearchProject extends Project {

    protected GateEntity buildGateEntityInstance(Project project, Object[] param) {
        return new ResearchGateEntity(project, param);
    }
    protected PumpEntity buildPumpEntityInstance(Project project, Object[] param) {
        return new ResearchPumpEntity(project, param);
    }
    protected IntakeEntity buildIntakeEntityInstance(Project project, Object[] param) {
        return new ResearchIntakeEntity(project, param);
    }
    protected ReservoirEntity buildReservoirEntityInstance(Project project, Object[] param) {
        return new ResearchReservoirEntity(project, param);
    }
    protected CanalEntity buildCanalEntityInstance(Project project, Object[] param) {
        return new ResearchCanalEntity(project, param);
    }
}
