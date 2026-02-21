package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.model.StaffModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface StaffRepository extends JpaRepository<StaffModel, UUID> {

    @Query("""
        SELECT s FROM StaffModel s
            JOIN FETCH UserModel u ON s.user.id = u.id
                WHERE s.user.id = :id
    """)
    Optional<StaffModel> findByUserId(UUID id);
}
