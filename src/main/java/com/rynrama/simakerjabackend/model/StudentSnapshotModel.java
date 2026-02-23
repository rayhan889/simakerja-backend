package com.rynrama.simakerjabackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "student_snapshots")
public class StudentSnapshotModel {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "moa_ia_document_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_student_snapshots_moa_ia_document")
    )
    private MoaIADocumentModel document;

    @Column(name = "study_program", nullable = false)
    private String studyProgram;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "total", nullable = false)
    private Integer total;

    @OneToMany(
            mappedBy = "snapshot",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<StudentSnapshotStudentModel> students = new ArrayList<>();

    public StudentSnapshotModel() {
    }

    public UUID getId() {
        return id;
    }

    public MoaIADocumentModel getDocument() {
        return document;
    }

    public void setDocument(MoaIADocumentModel document) {
        this.document = document;
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

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<StudentSnapshotStudentModel> getStudents() {
        return students;
    }

    public void setStudents(List<StudentSnapshotStudentModel> students) {
        this.students = students;
    }

    public void addStudent(StudentSnapshotStudentModel student) {
        students.add(student);
        student.setSnapshot(this);
    }

    public void clearStudents() {
        for (StudentSnapshotStudentModel s : students) {
            s.setSnapshot(null);
        }
        students.clear();
    }
}
