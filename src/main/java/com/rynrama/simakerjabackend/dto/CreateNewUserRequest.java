package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.UserRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CreateNewUserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank
    @Size(min = 6, message = "Full name must be at least 6 characters")
    private String fullName;

    @Size(min = 12, message = "Phone number must be at least 12 characters")
    @Pattern(regexp = "^[0-9]+$", message = "cannot use alphabet. numbers only")
    private String phoneNumber;

    @NotNull
    private UserRole role;

    private String nip;

    private String nidn;

    private String studyProgram;

    @AssertTrue(message = "nip are required when user role is staff")
    public boolean isStaffValid() {
        if (role == null) return true;

        if (role == UserRole.staff) {
            return nip != null && !nip.trim().isEmpty();
        }
        return true;
    }

    @AssertTrue(message = "nidn and study program are required when user role is lecturer")
    public boolean isLecturerValid() {
        if (role == null) return true;

        if (role == UserRole.lecturer) {
            return (
                nidn != null && !nidn.trim().isEmpty() &&
                studyProgram != null && !studyProgram.trim().isEmpty()
            );
        }
        return true;
    }

}
