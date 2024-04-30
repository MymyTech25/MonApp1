package com.example.monapp1;

public class UserDTO {
    private String email;

    private String id;
    private String numeroImm;
    private Long numeroPortable;
    private int role; // 1 pour client, 2 pour chauffeur, 3 planif

    public UserDTO() {
    }

    public UserDTO(String email, String numeroImm, Long numeroPortable, int role) {
        this.email = email;
        this.numeroImm = numeroImm;
        this.numeroPortable = numeroPortable;
        this.role = role;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumeroImm() {
        return numeroImm;
    }

    public void setNumeroImm(String numeroImm) {
        this.numeroImm = numeroImm;
    }

    public Long getNumeroPortable() {
        return numeroPortable;
    }

    public void setNumeroPortable(Long numeroPortable) {
        this.numeroPortable = numeroPortable;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return
                "email='" + email + '\''
                ;
    }
}
