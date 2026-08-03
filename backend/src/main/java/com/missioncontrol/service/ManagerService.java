package com.missioncontrol.service;

import com.missioncontrol.exception.NotFoundException;
import com.missioncontrol.model.Manager;
import com.missioncontrol.model.ManagerRequest;
import com.missioncontrol.repository.ManagerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagerService {

    private final ManagerRepository repository;

    public ManagerService(ManagerRepository repository) {
        this.repository = repository;
    }

    public List<Manager> findAll() {
        return repository.findAll();
    }

    public Manager findById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Manager " + id + " not found"));
    }

    public Manager create(ManagerRequest request) {
        ensureEmailIsFree(request.email(), null);
        return repository.insert(request);
    }

    public Manager update(int id, ManagerRequest request) {
        findById(id);
        ensureEmailIsFree(request.email(), id);
        repository.update(id, request);
        return findById(id);
    }

    private void ensureEmailIsFree(String email, Integer excludeId) {
        repository.findByEmail(email.trim())
                .filter(existing -> excludeId == null || existing.id() != excludeId)
                .ifPresent(existing -> {
                    throw new ConflictException("Email " + email + " is already in use");
                });
    }

    public void delete(int id) {
        findById(id);
        if (repository.isReferencedByProject(id)) {
            throw new ConflictException("Manager " + id + " is assigned to a project and cannot be deleted");
        }
        repository.delete(id);
    }

    public void ensureExists(int managerId) {
        if (repository.findById(managerId).isEmpty()) {
            throw new NotFoundException("Manager " + managerId + " not found");
        }
    }
}
