package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.model.UserModel;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {


    Optional<UserModel> findByEmail(String email);

    @Override
    Optional<UserModel> findById(UUID id);
}
