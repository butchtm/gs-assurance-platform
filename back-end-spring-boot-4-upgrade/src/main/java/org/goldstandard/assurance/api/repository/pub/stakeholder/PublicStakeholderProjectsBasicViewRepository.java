package org.goldstandard.assurance.api.repository.pub.stakeholder;

import org.goldstandard.assurance.api.entity.view.stakeholder.project.ProjectBasicView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicStakeholderProjectsBasicViewRepository
    extends JpaRepository<ProjectBasicView, String> {}
