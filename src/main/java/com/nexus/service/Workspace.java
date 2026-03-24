package com.nexus.service;

import com.nexus.model.Project;
import com.nexus.model.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Workspace {
    private final List<Task> tasks = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }
    
    public void addProject(Project project) {
        projects.add(project);
    }
 
    public List<Project> getProjects() {
        return Collections.unmodifiableList(projects);
    }

    public Task findTaskById(int id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }
 
    public Project findProjectByName(String nome) {
        return projects.stream()
                .filter(p -> p.getNome().equals(nome))
                .findFirst()
                .orElse(null);
    }
}