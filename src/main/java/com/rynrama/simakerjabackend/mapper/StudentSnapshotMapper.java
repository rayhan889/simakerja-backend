package com.rynrama.simakerjabackend.mapper;

import com.rynrama.simakerjabackend.dto.StudentInfo;
import com.rynrama.simakerjabackend.model.MoaIADocumentModel;
import com.rynrama.simakerjabackend.model.StudentSnapshot;
import com.rynrama.simakerjabackend.model.StudentSnapshotModel;
import com.rynrama.simakerjabackend.model.StudentSnapshotStudentModel;

import java.util.ArrayList;
import java.util.List;

public class StudentSnapshotMapper {

    public StudentSnapshotMapper() {
    }

    public static List<StudentSnapshotModel> toEntities(
            List<StudentSnapshot> dtoSnapshots,
            MoaIADocumentModel document
    ) {
        List<StudentSnapshotModel> result = new ArrayList<>();
        if (dtoSnapshots == null) {
            return result;
        }

        for (StudentSnapshot dto : dtoSnapshots) {
            StudentSnapshotModel snapshotModel = new StudentSnapshotModel();
            snapshotModel.setDocument(document);
            snapshotModel.setStudyProgram(dto.getStudyProgram());
            snapshotModel.setUnit(dto.getUnit());
            snapshotModel.setTotal(dto.getTotal());

            if (dto.getStudents() != null) {
                for (StudentInfo info : dto.getStudents()) {
                    StudentSnapshotStudentModel studentModel =
                            new StudentSnapshotStudentModel(
                                    info.getFullName(),
                                    info.getEmail(),
                                    info.getNim()
                            );
                    snapshotModel.addStudent(studentModel);
                }
            }

            result.add(snapshotModel);
        }

        return result;
    }

    public static List<StudentSnapshot> toDtos(List<StudentSnapshotModel> entities) {
        List<StudentSnapshot> result = new ArrayList<>();
        if (entities == null) {
            return result;
        }

        for (StudentSnapshotModel entity : entities) {
            StudentSnapshot dto = new StudentSnapshot();
            dto.setStudyProgram(entity.getStudyProgram());
            dto.setUnit(entity.getUnit());
            dto.setTotal(entity.getTotal());

            List<StudentInfo> students = new ArrayList<>();
            if (entity.getStudents() != null) {
                for (StudentSnapshotStudentModel s : entity.getStudents()) {
                    students.add(new StudentInfo(
                            s.getFullName(),
                            s.getEmail(),
                            s.getNim()
                    ));
                }
            }
            dto.setStudents(students);

            result.add(dto);
        }

        return result;
    }
}
