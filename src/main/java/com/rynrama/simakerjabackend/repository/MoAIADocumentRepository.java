package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.model.MoaIADocumentModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MoAIADocumentRepository extends JpaRepository<MoaIADocumentModel, UUID> {
}
