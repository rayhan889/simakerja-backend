package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthDTO {
    private UUID id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private UserRole role;
    private String status;
    private String profilePicture;
    private String studyProgram;
    private String nim;
    private String nip;
    private String nidn;
}
