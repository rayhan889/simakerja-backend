package com.rynrama.simakerjabackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class StudentInfo {

    @NotBlank
    private String fullName;

    @NotBlank
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@mhs\\.unesa\\.ac\\.id$",
            message = "Email must be a valid @mhs.unesa.ac.id address"
    )
    private String email;

    @NotBlank
    @Size(min = 11)
    @Pattern(regexp = "^[0-9]+$", message = "cannot use alphabet. numbers only")
    private String nim;

    public StudentInfo() {
    }

    public StudentInfo(String fullName, String email, String nim) {
        this.fullName = fullName;
        this.email = email;
        this.nim = nim;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }
}
