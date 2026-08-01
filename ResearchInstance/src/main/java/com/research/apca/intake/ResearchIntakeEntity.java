package com.research.apca.intake;

import com.research.apca.core.entity.intake.IntakeEntity;
import com.research.apca.core.project.Project;


public class ResearchIntakeEntity extends IntakeEntity {
    public ResearchIntakeEntity(Project project) {
        super(project);
    }
    public ResearchIntakeEntity(Project project, Object[] param) {
        super(project, param);
    }
}
