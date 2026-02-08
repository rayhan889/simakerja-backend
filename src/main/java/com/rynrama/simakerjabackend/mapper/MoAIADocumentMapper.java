package com.rynrama.simakerjabackend.mapper;

import com.rynrama.simakerjabackend.dto.MoAIADocumentDTO;
import com.rynrama.simakerjabackend.dto.MoaIADocumentRequest;
import com.rynrama.simakerjabackend.model.MoaIADocumentModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MoAIADocumentMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public MoAIADocumentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public MoaIADocumentModel toModel(MoaIADocumentRequest request) {
        return modelMapper.map(request, MoaIADocumentModel.class);
    }

    public MoAIADocumentDTO toDto(MoaIADocumentModel model) {
        return modelMapper.map(model, MoAIADocumentDTO.class);
    }
}
