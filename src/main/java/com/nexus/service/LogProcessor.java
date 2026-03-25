package com.nexus.service;

import com.nexus.model.*;
import com.nexus.exception.NexusValidationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


public class LogProcessor {

    public void processLog(String fileName, Workspace workspace, List<User> users) {
        try {
            // Busca o arquivo dentro da pasta de recursos do projeto (target/classes)
            var resource = getClass().getClassLoader().getResourceAsStream(fileName);
            
            if (resource == null) {
                throw new IOException("Arquivo não encontrado no classpath: " + fileName);
            }

            try (java.util.Scanner s = new java.util.Scanner(resource).useDelimiter("\\A")) {
                String content = s.hasNext() ? s.next() : "";
                List<String> lines = List.of(content.split("\\R"));
                
                for (String line : lines) {
                    if (line.isBlank() || line.startsWith("#")) continue;

                    String[] p = line.split(";");
                    String action = p[0];

                    try {
                        switch (action) {
                            case "CREATE_USER" -> {
                                users.add(new User(p[1], p[2]));
                                System.out.println("[LOG] Usuário criado: " + p[1]);
                            }
                            case "CREATE_PROJECT" -> {
                                workspace.addProject(new Project(p[1], Integer.parseInt(p[2])));
                                System.out.println("[LOG] Projeto criado: " + p[1]);
                            }
                            case "CREATE_TASK" -> {
                                Task t = new Task(p[1], LocalDate.parse(p[2]), Integer.parseInt(p[3])); // tem que mudar pra ter o parametro novo estimatedEffort
                                workspace.addTask(t);

                                Project proj = workspace.findProjectByName(p[4]);
                                if (proj != null) {
                                    proj.addTask(t);
                                } else {
                                    System.err.println("[WARN] Projeto '" + p[4] + "' não encontrado.");
                                }

                                System.out.println("[LOG] Tarefa criada: " + p[1] + " (ID " + t.getId() + ")");
                            }
                            case "ASSIGN_USER" -> {
                                Task t = workspace.findTaskById(Integer.parseInt(p[1]));
                                User u = findUserByUsername(users, p[2]);
 
                                if (t == null) throw new NexusValidationException("Tarefa ID " + p[1] + " não encontrada.");
                                if (u == null) throw new NexusValidationException("Usuário '" + p[2] + "' não encontrado.");
 
                                t.moveToInProgress(u);
                                System.out.println("[LOG] Tarefa " + p[1] + " atribuída a " + p[2]);
                            }
                            case "CHANGE_STATUS" -> {
                                Task t = workspace.findTaskById(Integer.parseInt(p[1]));
 
                                if (t == null) throw new NexusValidationException("Tarefa ID " + p[1] + " não encontrada.");
 
                                switch (p[2]) {
                                    case "IN_PROGRESS" -> {
                                        if (t.getOwner() == null) throw new NexusValidationException("Tarefa ID" + p[1] + "não possui owner.");
                                        t.moveToInProgress(t.getOwner());
                                    }
                                    case "DONE"        -> t.markAsDone();
                                    case "BLOCKED"     -> t.setBlocked(true);
                                    case "TO_DO"       -> t.setBlocked(false);
                                    default -> throw new NexusValidationException("Status desconhecido: " + p[2]);
                                }
 
                                System.out.println("[LOG] Tarefa " + p[1] + " → " + p[2]);
                            }
                            case "REPORT_STATUS" -> workspace.printReports(users);
                            default -> System.err.println("[WARN] Ação desconhecida: " + action);
                        }
                    } catch (NexusValidationException e) {
                        System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }
    private User findUserByUsername(List<User> users, String username) {
        return users.stream()
                .filter(u -> u.consultUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}