package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.model.StudentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<StudentModel, UUID> {

    @Query("""
    select s from StudentModel s
        join fetch UserModel u on s.user.id = u.id
            where s.user.id = :id
""")
    Optional<StudentModel> findByUserId(UUID id);
}
