package com.nexus.model;

import java.util.ArrayList;
import java.util.List;

import com.nexus.exception.NexusValidationException;

public class Project {
    private String nome;
    private int totalBudget;
    private final List<Task> tasks; // inicializei com "final" para a referência da lista ser imutável

    public Project(String nome, int totalBudget){
        if(nome == null || nome.isBlank()){
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }

        // inicializa atributos da classe
        this.nome = nome;
        this.totalBudget = totalBudget;
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task t){
        if(t == null){
            throw new IllegalArgumentException("Task não pode ser nula.");
        }
        int totalAtual = tasks.stream()
            .mapToInt(task -> task.getEstimatedEffort()).sum();

        if(totalAtual + t.getEstimatedEffort() > totalBudget){
            throw new NexusValidationException("Total de horas não pode exceder o limite do projeto.");
        }
        tasks.add(t);
    }
    
    public String getNome() {
        return nome; 
    }

    public int getTotalBudget() {
        return totalBudget;
    }

    // retorna cópia da lista
    public List<Task> geTasks() {
        return new ArrayList<>(tasks);
    }

}
