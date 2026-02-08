package com.rynrama.simakerjabackend.mapper;

import com.rynrama.simakerjabackend.dto.DocumentSubmissionDTO;
import com.rynrama.simakerjabackend.dto.DocumentSubmissionRequest;
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
}
