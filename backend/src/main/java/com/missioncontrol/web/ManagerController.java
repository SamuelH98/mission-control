package com.missioncontrol.web;

import com.missioncontrol.model.Manager;
import com.missioncontrol.model.ManagerRequest;
import com.missioncontrol.service.ManagerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/managers")
public class ManagerController {

    private final ManagerService service;

    public ManagerController(ManagerService service) {
        this.service = service;
    }

    @GetMapping
    public List<Manager> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Manager get(@PathVariable int id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Manager> create(@Valid @RequestBody ManagerRequest request) {
        Manager created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public Manager update(@PathVariable int id, @Valid @RequestBody ManagerRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable int id) {
        service.delete(id);
    }
}
