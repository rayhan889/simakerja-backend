package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.model.LecturerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface LecturerRepository extends JpaRepository<LecturerModel, UUID> {
    @Query("""
    SELECT l FROM LecturerModel l
            JOIN FETCH UserModel u ON l.user.id = u.id
                WHERE l.user.id = :id
    """)
    Optional<LecturerModel> findByUserId(UUID id);
}
