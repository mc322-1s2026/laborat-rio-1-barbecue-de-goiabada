package com.nexus.model;

import java.time.LocalDate;

import com.nexus.exception.NexusValidationException;

public class Task {
    // Métricas Globais (Alunos implementam a lógica de incremento/decremento)
    public static int totalTasksCreated = 0;
    public static int totalValidationErrors = 0;
    public static int activeWorkload = 0;

    private static int nextId = 1;

    private final int id;   // o id da tarefa também nunca muda
    private final LocalDate deadline; // Imutável após o nascimento
    private String title;
    private TaskStatus status;
    private User owner;
    private int estimatedEffort;

    public Task(String title, LocalDate deadline, int estimatedEffort) {    // agora o esforco tbm precisa ser parametro
        if(title == null || title.isBlank()){
            throw new IllegalArgumentException("O título da tarefa não pode ser vazio");
        }
        this.id = nextId++;
        this.deadline = deadline;
        this.title = title;
        this.status = TaskStatus.TO_DO;
        this.estimatedEffort = estimatedEffort;
        
        // Ação do Aluno:
        totalTasksCreated++; 
    }

    /**
     * Move a tarefa para IN_PROGRESS.
     * Regra: Só é possível se houver um owner atribuído e não estiver BLOCKED.
     */
    public void moveToInProgress(User user) {
        // TODO: Implementar lógica de proteção e atualizar activeWorkload
        // Se falhar, incrementar totalValidationErrors e lançar NexusValidationException
        this.owner = user;

        if(this.owner == null || this.status == TaskStatus.BLOCKED) {
            totalValidationErrors += 1;
            throw new NexusValidationException("Não é possível mover a tarefa para IN_PROGRESS.");
        }
        this.status = TaskStatus.IN_PROGRESS;
        activeWorkload += 1;

    }

    /**
     * Finaliza a tarefa.
     * Regra: Só pode ser movida para DONE se não estiver BLOCKED.
     */
    public void markAsDone() {
        // TODO: Implementar lógica de proteção e atualizar activeWorkload (decrementar)
        if(this.status == TaskStatus.BLOCKED) {
            totalValidationErrors += 1;
            throw new NexusValidationException("Não é possível mover uma tarefa BLOCKED para DONE.");
        }
        this.status = TaskStatus.DONE;
        activeWorkload -= 1;
        
    }

    public void setBlocked(boolean blocked) {
        if (blocked) {
            if (this.status == TaskStatus.DONE) {
                totalValidationErrors += 1;
                throw new NexusValidationException("Uma tarefa DONE não pode ser bloqueada.");
            }
            this.status = TaskStatus.BLOCKED;
        } else {
            this.status = TaskStatus.TO_DO; // Simplificação para o Lab
        }
    }

    // Getters
    public int getId() { return id; }
    public TaskStatus getStatus() { return status; }
    public String getTitle() { return title; }
    public LocalDate getDeadline() { return deadline; }
    public User getOwner() { return owner; }
    public int getEstimatedEffort() { return estimatedEffort;}
}