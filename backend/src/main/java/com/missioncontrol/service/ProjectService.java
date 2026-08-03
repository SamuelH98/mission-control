package com.missioncontrol.service;

import com.missioncontrol.exception.NotFoundException;
import com.missioncontrol.model.ProjectRequest;
import com.missioncontrol.model.ProjectResponse;
import com.missioncontrol.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository repository;
    private final ManagerService managerService;

    public ProjectService(ProjectRepository repository, ManagerService managerService) {
        this.repository = repository;
        this.managerService = managerService;
    }

    public List<ProjectResponse> findAll() {
        return repository.findAll();
    }

    public ProjectResponse findById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project " + id + " not found"));
    }

    public ProjectResponse create(ProjectRequest request) {
        managerService.ensureExists(request.managerId());
        return repository.insert(request);
    }

    public ProjectResponse update(int id, ProjectRequest request) {
        findById(id);
        managerService.ensureExists(request.managerId());
        repository.update(id, request);
        return findById(id);
    }

    public void delete(int id) {
        findById(id);
        repository.delete(id);
    }
}
