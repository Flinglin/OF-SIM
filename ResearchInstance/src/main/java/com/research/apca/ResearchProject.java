package com.research.apca;

import com.research.apca.core.entity.canal.CanalEntity;
import com.research.apca.core.entity.gate.GateEntity;
import com.research.apca.core.entity.intake.IntakeEntity;
import com.research.apca.core.entity.pump.PumpEntity;
import com.research.apca.core.entity.reservoir.ReservoirEntity;
import com.research.apca.core.project.Project;
import com.research.apca.canal.ResearchCanalEntity;
import com.research.apca.gate.ResearchGateEntity;
import com.research.apca.intake.ResearchIntakeEntity;
import com.research.apca.pump.ResearchPumpEntity;
import com.research.apca.reservoir.ResearchReservoirEntity;
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
