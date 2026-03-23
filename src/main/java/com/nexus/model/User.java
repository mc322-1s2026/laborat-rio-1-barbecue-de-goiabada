package com.nexus.model;
import java.util.List;

public class User {
    private final String username;
    private final String email;

    public User(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }
        this.username = username;

        if (email == null || !email.contains("@") || !email.contains(".")){
            throw new IllegalArgumentException("O e-mail deve seguir o formato usuario@dominio.com");
        }
        this.email = email;
    }

    public String consultEmail() {
        return email;
    }

    public String consultUsername() {
        return username;
    }

    public long calculateWorkload(List<Task> allTasks) {
        return allTasks.stream()
            .filter(tasks -> this.equals(tasks.getOwner()))
            .filter(tasks -> tasks.getStatus() == TaskStatus.IN_PROGRESS)
            .count();
    }
}