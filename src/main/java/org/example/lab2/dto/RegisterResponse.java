package org.example.lab2.dto;

public class RegisterResponse {
    private final String id;
    private final String username;
    private final String email;
    private final String message;

    public RegisterResponse(String id, String username, String email, String message) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.message = message;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getMessage() { return message; }
}
