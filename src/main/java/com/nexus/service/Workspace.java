package com.nexus.service;

import com.nexus.model.Project;
import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Comparator;

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

    public List<User> topPerformers(List<User> users) {
        return users.stream()
                .sorted(Comparator.comparingLong(
                        (User u) -> tasks.stream()
                                .filter(t -> u.equals(t.getOwner()))
                                .filter(t -> t.getStatus() == TaskStatus.DONE)
                                .count()
                ).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

     public List<User> overloadedUsers(List<User> users) {
        return users.stream()
                .filter(u -> u.calculateWorkload(tasks) > 10)
                .collect(Collectors.toList());
    }

    public double projectHealth(Project project) {
        List<Task> projectTasks = project.getTasks();
        if (projectTasks.isEmpty()) return 0.0;
 
        long done = projectTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE)
                .count();
 
        return (done * 100.0) / projectTasks.size();
    }

    public TaskStatus globalBottleneck() {
        return tasks.stream()
                .filter(t -> t.getStatus() != TaskStatus.DONE)
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public void printReports(List<User> users) {
        System.out.println("\n======= RELATÓRIO NEXUS =======");
 
        System.out.println("\n--- Top Performers ---");
        topPerformers(users).forEach(u -> {
            long done = tasks.stream()
                    .filter(t -> u.equals(t.getOwner()))
                    .filter(t -> t.getStatus() == TaskStatus.DONE)
                    .count();
            System.out.printf("  %s → %d tarefas DONE%n", u.consultUsername(), done);
        });
 
        System.out.println("\n--- Overloaded Users ---");
        List<User> overloaded = overloadedUsers(users);
        if (overloaded.isEmpty()) {
            System.out.println("  Nenhum usuário sobrecarregado.");
        } else {
            overloaded.forEach(u ->
                    System.out.printf("  %s → %d tarefas IN_PROGRESS%n",
                            u.consultUsername(), u.calculateWorkload(tasks)));
        }
 
        System.out.println("\n--- Project Health ---");
        if (projects.isEmpty()) {
            System.out.println("  Nenhum projeto cadastrado.");
        } else {
            projects.forEach(p ->
                    System.out.printf("  %-20s → %.1f%% concluído%n",
                            p.getNome(), projectHealth(p)));
        }
 
        System.out.println("\n--- Global Bottleneck ---");
        TaskStatus bottleneck = globalBottleneck();
        if (bottleneck == null) {
            System.out.println("  Sem gargalos detectados.");
        } else {
            long count = tasks.stream()
                    .filter(t -> t.getStatus() == bottleneck)
                    .count();
            System.out.printf("  %s → %d tarefas%n", bottleneck, count);
        }
 
       // System.out.println("\n======= FIM DO RELATÓRIO =======\n");
    }
}