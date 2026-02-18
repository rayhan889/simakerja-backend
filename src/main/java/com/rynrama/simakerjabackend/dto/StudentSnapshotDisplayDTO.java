package com.rynrama.simakerjabackend.dto;

public class StudentSnapshotDisplayDTO {
    private String studyProgram;
    private String unit;
    private String students;
    private Integer total;

    public StudentSnapshotDisplayDTO(String studyProgram, String unit, String students, Integer total) {
        this.studyProgram = studyProgram;
        this.unit = unit;
        this.students = students;
        this.total = total;
    }

    public String getStudyProgram() {
        return studyProgram;
    }

    public void setStudyProgram(String studyProgram) {
        this.studyProgram = studyProgram;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStudents() {
        return students;
    }

    public void setStudents(String students) {
        this.students = students;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
