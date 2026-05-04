package org.goldstandard.assurance.api.repository.pub.stakeholder;

import org.goldstandard.assurance.api.entity.view.stakeholder.project.StakeholderProjectDetailView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicStakeholderProjectsDetailViewRepository
    extends JpaRepository<StakeholderProjectDetailView, String> {}
