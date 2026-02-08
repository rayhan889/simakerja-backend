package com.rynrama.simakerjabackend.model;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.*;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StudentSnapshot {
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9 .,-]*$", message = "can't use any special characters")
    private String studyProgram;

    @NotNull
    @Size(min = 1, max = 3, message = "students must contain between 1 and 3 items")
    private List<@NotBlank @Pattern(regexp = "^[a-zA-Z0-9 .,-]*$", message = "can't use special characters")String> students;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9 .,-]*$", message = "can't use any special characters")
    private String unit;

    @NotNull
    @Max(value = 3, message = "assigned students can't be more than 3")
    private Integer total;

    public String getStudyProgram() {
        return studyProgram;
    }

    public void setStudyProgram(String studyProgram) {
        this.studyProgram = studyProgram;
    }

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
