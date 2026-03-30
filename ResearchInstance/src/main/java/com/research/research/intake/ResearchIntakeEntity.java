package com.research.research.intake;

import com.research.core.entity.intake.IntakeEntity;
import com.research.core.project.Project;


public class ResearchIntakeEntity extends IntakeEntity {
    public ResearchIntakeEntity(Project project) {
        super(project);
    }
    public ResearchIntakeEntity(Project project, Object[] param) {
        super(project, param);
    }
}
