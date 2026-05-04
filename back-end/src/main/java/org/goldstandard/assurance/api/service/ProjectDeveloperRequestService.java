package org.goldstandard.assurance.api.service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.ProjectConverter;
import org.goldstandard.assurance.api.converter.ProjectDeveloperRequestConverter;
import org.goldstandard.assurance.api.dto.GetProjectDeveloperRequestsDTO;
import org.goldstandard.assurance.api.dto.ProjectDTO;
import org.goldstandard.assurance.api.dto.ProjectDeveloperRequestDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.projection.RequestProject;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectDeveloperRequestService {

  private final RequestFinder requestFinder;
  private final RequestRepository requestRepository;
  private final ProjectDeveloperRequestSpecFactory specFactory;
  private final ProjectConverter projectConverter;
  private final AuthenticatedUserSupplier userSupplier;
  private final ProjectDeveloperRequestConverter requestMapper;

  public Page<ProjectDeveloperRequestDTO> getProjectDeveloperRequests(
      GetProjectDeveloperRequestsDTO getRequestsDto, Pageable pageable) {
    var account = getAuthenticatedUserAccountIfProjectDeveloper();
    var spec = specFactory.toSpec(account.getId(), getRequestsDto);
    return requestFinder.findAndMapPage(
        spec, pageable, requestMapper::toProjectDeveloperRequestDTO);
  }

  public List<ProjectDTO> getProjectDeveloperRequestProjects() {
    var account = getAuthenticatedUserAccountIfProjectDeveloper();
    return requestRepository.findByProjectAccountId(account.getId(), RequestProject.class).stream()
        .collect(
            Collectors.toMap(RequestProject::getProjectGsid, Function.identity(), (p1, p2) -> p1))
        .values()
        .stream()
        .map(projectConverter::toMinimalProjectDto)
        .toList();
  }

  private Account getAuthenticatedUserAccountIfProjectDeveloper() {
    var account = userSupplier.forceGetAuthenticatedUserAccount();
    validateIsProjectDeveloper(account);
    return account;
  }

  private void validateIsProjectDeveloper(Account account) {
    if (!account.getAccountType().isProjectDeveloper()) {
      throw new PermissionException(
          "user account %s is not a project developer account", account.getId());
    }
  }
}
