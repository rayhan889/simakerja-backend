package com.rynrama.simakerjabackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class StudentUpdateRequest {

    @NotBlank
    @Size(min = 11)
    @Pattern(regexp = "^[0-9]+$", message = "cannot use alphabet. numbers only")
    private String nim;

    @NotBlank
    private String studyProgram;

    @NotBlank
    @Pattern(
            regexp = "^(?:\\+62|62|0)8[1-9][0-9]{6,10}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;

    public StudentUpdateRequest() {
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getStudyProgram() {
        return studyProgram;
    }

    public void setStudyProgram(String studyProgram) {
        this.studyProgram = studyProgram;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
