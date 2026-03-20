package com.rynrama.simakerjabackend.mapper;

import com.rynrama.simakerjabackend.dto.DocumentSubmissionDTO;
import com.rynrama.simakerjabackend.dto.DocumentSubmissionRequest;
import com.rynrama.simakerjabackend.dto.MoAIADocumentDTO;
import com.rynrama.simakerjabackend.model.MoaIADocumentModel;
import com.rynrama.simakerjabackend.model.SubmissionModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentSubmissionMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public DocumentSubmissionMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public SubmissionModel toModel(DocumentSubmissionRequest request) {
        return modelMapper.map(request, SubmissionModel.class);
    }

    public DocumentSubmissionDTO  toDTO(SubmissionModel model) {
        return modelMapper.map(model, DocumentSubmissionDTO.class);
    }

    public DocumentSubmissionDTO toDetailDTO(SubmissionModel submission, MoaIADocumentModel moaIaDocument) {
        DocumentSubmissionDTO dto = new DocumentSubmissionDTO();

        dto.setId(submission.getId());
        dto.setUser(submission.getUser());
        dto.setSubmissionCode(submission.getSubmissionCode());
        dto.setSubmissionType(submission.getSubmissionType());
        dto.setStatus(submission.getStatus());
        dto.setNotes(submission.getNotes());
        dto.setFaculty(submission.getFaculty());
        dto.setSubmissionDate(submission.getSubmissionDate());
        dto.setFacultyLetterNumber(submission.getFacultyLetterNumber());
        dto.setFacultyAddress(submission.getFacultyAddress());
        dto.setCreatedAt(submission.getCreatedAt());
        dto.setUpdatedAt(submission.getUpdatedAt());
        dto.setStaffVerifiedAt(submission.getStaffVerifiedAt());
        dto.setLecturerVerifiedAt(submission.getLecturerVerifiedAt());

        if (moaIaDocument != null) {
            var studentSnapshotsDto = StudentSnapshotMapper.toDtos(
                    moaIaDocument.getStudentSnapshots()
            );

            MoAIADocumentDTO moaIaDTO = new MoAIADocumentDTO(
                    moaIaDocument.getPartnerName(),
                    moaIaDocument.getPartnerNumber(),
                    moaIaDocument.getFacultyRepresentativeName(),
                    moaIaDocument.getPartnerRepresentativeName(),
                    moaIaDocument.getPartnerRepresentativePosition(),
                    moaIaDocument.getActivityType(),
                    moaIaDocument.getDocumentType(),
                    studentSnapshotsDto,
                    moaIaDocument.getPartnerAddress(),
                    moaIaDocument.getPartnerLogoKey(),
                    moaIaDocument.getPartnerCooperationPeriod(),
                    moaIaDocument.getScannedDocumentKey(),
                    moaIaDocument.getScannedDocumentOcrConfidentScore()
            );
            dto.setMoaIa(moaIaDTO);
        }

        return dto;
    }
}
